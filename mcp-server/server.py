import base64
import hashlib
import os
import secrets
import time
from datetime import datetime, timezone
from pathlib import Path
from typing import Any, Optional

import httpx
import uvicorn
from mcp.server.auth.provider import (
	AccessToken,
	AuthorizationCode,
	AuthorizationParams,
	RefreshToken,
	construct_redirect_uri,
)
from mcp.server.auth.settings import AuthSettings, ClientRegistrationOptions, RevocationOptions
from mcp.server.fastmcp import FastMCP
from mcp.shared.auth import InvalidRedirectUriError, OAuthClientInformationFull, OAuthToken
from mcp.types import Icon
from starlette.requests import Request
from starlette.responses import HTMLResponse, RedirectResponse, Response

ICON_SVG = (Path(__file__).parent / "icon.svg").read_bytes()

NANOSERVER_BASE_URL = os.environ["NANOSERVER_BASE_URL"].rstrip("/")
NANOSERVER_SECURITY_KEY = os.environ["NANOSERVER_SECURITY_KEY"]
NANOSERVER_SECURITY_SECRET = os.environ["NANOSERVER_SECURITY_SECRET"]
NANOSERVER_USER_EMAIL = os.environ["NANOSERVER_USER_EMAIL"]
NANOSERVER_TIME_ZONE = os.environ.get("NANOSERVER_TIME_ZONE", "America/Argentina/Buenos_Aires")

MCP_HOST = os.environ.get("MCP_HOST", "0.0.0.0")
MCP_PORT = int(os.environ.get("MCP_PORT", "8765"))
MCP_AUTH_TOKEN = os.environ["MCP_AUTH_TOKEN"]

MCP_PUBLIC_URL = os.environ["MCP_PUBLIC_URL"].rstrip("/")
MCP_OAUTH_CLIENT_ID = os.environ.get("MCP_OAUTH_CLIENT_ID", "grok")
MCP_OAUTH_CLIENT_SECRET = os.environ.get("MCP_OAUTH_CLIENT_SECRET") or None
MCP_OAUTH_CONSENT_PASSWORD = os.environ["MCP_OAUTH_CONSENT_PASSWORD"]
MCP_OAUTH_REDIRECT_URIS = [u.strip() for u in os.environ.get("MCP_OAUTH_REDIRECT_URIS", "").split(",") if u.strip()]
MCP_OAUTH_ACCESS_TOKEN_TTL = int(os.environ.get("MCP_OAUTH_ACCESS_TOKEN_TTL", "3600"))
MCP_OAUTH_REFRESH_TOKEN_TTL = int(os.environ.get("MCP_OAUTH_REFRESH_TOKEN_TTL", "2592000"))
AUTHORIZATION_CODE_TTL_SECONDS = 300
PENDING_CONSENT_TTL_SECONDS = 600


class _OAuthClient(OAuthClientInformationFull):
	def validate_redirect_uri(self, redirect_uri):
		if redirect_uri is None:
			raise InvalidRedirectUriError("redirect_uri is required")
		if str(redirect_uri).split(":", 1)[0] != "https":
			raise InvalidRedirectUriError("redirect_uri must use https")
		if MCP_OAUTH_REDIRECT_URIS and str(redirect_uri) not in MCP_OAUTH_REDIRECT_URIS:
			raise InvalidRedirectUriError(f"redirect_uri '{redirect_uri}' is not allowlisted")
		return redirect_uri

	def validate_scope(self, requested_scope):
		return requested_scope.split(" ") if requested_scope else None


class NanoServerOAuthProvider:
	def __init__(self):
		self._client = _OAuthClient(
			client_id=MCP_OAUTH_CLIENT_ID,
			client_secret=MCP_OAUTH_CLIENT_SECRET,
			redirect_uris=None,
			token_endpoint_auth_method="client_secret_post" if MCP_OAUTH_CLIENT_SECRET else "none",
		)
		self._pending: dict[str, tuple[str, AuthorizationParams, float]] = {}
		self._auth_codes: dict[str, AuthorizationCode] = {}
		self._access_tokens: dict[str, AccessToken] = {}
		self._refresh_tokens: dict[str, RefreshToken] = {}

	async def get_client(self, client_id: str) -> Optional[OAuthClientInformationFull]:
		return self._client if client_id == MCP_OAUTH_CLIENT_ID else None

	async def register_client(self, client_info: OAuthClientInformationFull) -> None:
		raise NotImplementedError("Dynamic client registration is disabled")

	async def authorize(self, client: OAuthClientInformationFull, params: AuthorizationParams) -> str:
		self._purge_pending()
		txn_id = secrets.token_urlsafe(24)
		self._pending[txn_id] = (client.client_id, params, time.time() + PENDING_CONSENT_TTL_SECONDS)
		return f"/consent?txn={txn_id}"

	def peek_pending(self, txn_id: str) -> Optional[tuple[str, AuthorizationParams]]:
		entry = self._pending.get(txn_id)
		if entry is None or entry[2] < time.time():
			return None
		return entry[0], entry[1]

	def pop_pending(self, txn_id: str) -> Optional[tuple[str, AuthorizationParams]]:
		entry = self._pending.pop(txn_id, None)
		if entry is None or entry[2] < time.time():
			return None
		return entry[0], entry[1]

	def store_code(self, code: AuthorizationCode) -> None:
		self._purge_auth_codes()
		self._auth_codes[code.code] = code

	async def load_authorization_code(
		self, client: OAuthClientInformationFull, authorization_code: str
	) -> Optional[AuthorizationCode]:
		code = self._auth_codes.get(authorization_code)
		if code is None or code.client_id != client.client_id:
			return None
		return code

	async def exchange_authorization_code(
		self, client: OAuthClientInformationFull, authorization_code: AuthorizationCode
	) -> OAuthToken:
		self._auth_codes.pop(authorization_code.code, None)
		return self._issue_tokens(client.client_id, authorization_code.scopes, authorization_code.resource)

	async def load_refresh_token(self, client: OAuthClientInformationFull, refresh_token: str) -> Optional[RefreshToken]:
		token = self._refresh_tokens.get(refresh_token)
		if token is None or token.client_id != client.client_id:
			return None
		return token

	async def exchange_refresh_token(
		self, client: OAuthClientInformationFull, refresh_token: RefreshToken, scopes: list[str]
	) -> OAuthToken:
		self._refresh_tokens.pop(refresh_token.token, None)
		return self._issue_tokens(client.client_id, scopes, None)

	async def load_access_token(self, token: str) -> Optional[AccessToken]:
		if secrets.compare_digest(token, MCP_AUTH_TOKEN):
			return AccessToken(token=token, client_id="static", scopes=[], expires_at=None)

		access_token = self._access_tokens.get(token)
		if access_token is None:
			return None
		if access_token.expires_at is not None and access_token.expires_at < time.time():
			self._access_tokens.pop(token, None)
			return None
		return access_token

	async def revoke_token(self, token) -> None:
		self._access_tokens.pop(token.token, None)
		self._refresh_tokens.pop(token.token, None)

	def _issue_tokens(self, client_id: str, scopes: list[str], resource: Optional[str]) -> OAuthToken:
		access_token = secrets.token_urlsafe(32)
		refresh_token = secrets.token_urlsafe(32)
		now = time.time()

		self._access_tokens[access_token] = AccessToken(
			token=access_token,
			client_id=client_id,
			scopes=scopes,
			expires_at=int(now + MCP_OAUTH_ACCESS_TOKEN_TTL),
			resource=resource,
		)
		self._refresh_tokens[refresh_token] = RefreshToken(
			token=refresh_token,
			client_id=client_id,
			scopes=scopes,
			expires_at=int(now + MCP_OAUTH_REFRESH_TOKEN_TTL),
		)

		return OAuthToken(
			access_token=access_token,
			token_type="Bearer",
			expires_in=MCP_OAUTH_ACCESS_TOKEN_TTL,
			scope=" ".join(scopes) if scopes else None,
			refresh_token=refresh_token,
		)

	def _purge_pending(self) -> None:
		now = time.time()
		for txn_id in [k for k, v in self._pending.items() if v[2] < now]:
			self._pending.pop(txn_id, None)

	def _purge_auth_codes(self) -> None:
		now = time.time()
		for code in [k for k, v in self._auth_codes.items() if v.expires_at < now]:
			self._auth_codes.pop(code, None)


oauth_provider = NanoServerOAuthProvider()

mcp = FastMCP(
	"nanoserver",
	website_url=MCP_PUBLIC_URL,
	icons=[Icon(src=f"{MCP_PUBLIC_URL}/icon.svg", mimeType="image/svg+xml", sizes=["832x832"])],
	host=MCP_HOST,
	port=MCP_PORT,
	auth_server_provider=oauth_provider,
	auth=AuthSettings(
		issuer_url=MCP_PUBLIC_URL,
		resource_server_url=f"{MCP_PUBLIC_URL}/mcp",
		client_registration_options=ClientRegistrationOptions(enabled=False),
		revocation_options=RevocationOptions(enabled=True),
	),
)


@mcp.custom_route("/icon.svg", methods=["GET"])
async def icon(request: Request) -> Response:
	return Response(content=ICON_SVG, media_type="image/svg+xml")

CONSENT_PAGE = """<!doctype html>
<html><head><meta charset="utf-8"><title>NanO Server MCP</title></head>
<body style="font-family: sans-serif; max-width: 420px; margin: 80px auto;">
<h2>Autorizar acceso a NanO Server</h2>
<p>Una aplicación externa quiere conectarse a tu servidor MCP.</p>
{error}
<form method="post">
<input type="hidden" name="txn" value="{txn}">
<label>Contraseña<br>
<input type="password" name="password" autofocus style="width:100%;padding:8px;margin:8px 0;box-sizing:border-box;">
</label>
<button type="submit" style="padding:8px 16px;">Autorizar</button>
</form>
</body></html>"""


@mcp.custom_route("/consent", methods=["GET", "POST"])
async def consent(request: Request) -> Response:
	if request.method == "GET":
		txn_id = request.query_params.get("txn", "")
		if oauth_provider.peek_pending(txn_id) is None:
			return HTMLResponse("Solicitud de autorización inválida o expirada.", status_code=400)
		return HTMLResponse(CONSENT_PAGE.format(error="", txn=txn_id))

	form = await request.form()
	txn_id = str(form.get("txn", ""))
	password = str(form.get("password", ""))

	if oauth_provider.peek_pending(txn_id) is None:
		return HTMLResponse("Solicitud de autorización inválida o expirada.", status_code=400)

	if not secrets.compare_digest(password, MCP_OAUTH_CONSENT_PASSWORD):
		error = "<p style='color:#c00;'>Contraseña incorrecta.</p>"
		return HTMLResponse(CONSENT_PAGE.format(error=error, txn=txn_id), status_code=401)

	client_id, params = oauth_provider.pop_pending(txn_id)
	code = secrets.token_urlsafe(32)
	oauth_provider.store_code(AuthorizationCode(
		code=code,
		scopes=params.scopes or [],
		expires_at=time.time() + AUTHORIZATION_CODE_TTL_SECONDS,
		client_id=client_id,
		code_challenge=params.code_challenge,
		redirect_uri=params.redirect_uri,
		redirect_uri_provided_explicitly=params.redirect_uri_provided_explicitly,
		resource=params.resource,
	))

	return RedirectResponse(
		url=construct_redirect_uri(str(params.redirect_uri), code=code, state=params.state),
		status_code=302,
	)


def _build_wsse_headers() -> dict[str, str]:
	nonce_bytes = secrets.token_bytes(16)
	nonce_b64 = base64.b64encode(nonce_bytes).decode("ascii")
	created = datetime.now(timezone.utc).strftime("%Y-%m-%dT%H:%M:%SZ")

	digest_input = nonce_bytes + created.encode("utf-8") + NANOSERVER_SECURITY_SECRET.encode("utf-8")
	password_digest = base64.b64encode(hashlib.sha1(digest_input).digest()).decode("ascii")

	wsse_header = (
		f'UsernameToken Username="{NANOSERVER_SECURITY_KEY}", '
		f'PasswordDigest="{password_digest}", '
		f'Nonce="{nonce_b64}", '
		f'Created="{created}"'
	)

	return {
		"Authorization": 'WSSE profile="UsernameToken"',
		"X-WSSE": wsse_header,
		"Content-Type": "application/json",
	}


async def _post(path: str, payload: dict[str, Any]) -> dict[str, Any]:
	url = f"{NANOSERVER_BASE_URL}{path}"

	async with httpx.AsyncClient(timeout=15.0) as client:
		response = await client.post(url, headers=_build_wsse_headers(), json=payload)

	body = response.json()

	if response.status_code != 200 or not body.get("success"):
		raise RuntimeError(f"NanoServer request to {path} failed: HTTP {response.status_code} - {body.get('message')}")

	return body["data"]


def _to_millis(date_str: str) -> int:
	parsed = datetime.fromisoformat(date_str)
	if parsed.tzinfo is None:
		parsed = parsed.replace(tzinfo=timezone.utc)
	return int(parsed.timestamp() * 1000)


async def _find_device(device_type: str, device_uuid: Optional[str]) -> str:
	if device_uuid:
		return device_uuid

	data = await _post("/api/services/mcp/devices", {
		"userEmail": NANOSERVER_USER_EMAIL,
		"type": device_type,
	})

	devices = data.get("devices") or []
	if not devices:
		raise RuntimeError(f"No devices of type '{device_type}' found for {NANOSERVER_USER_EMAIL}.")

	return devices[0]["uuid"]


@mcp.tool()
async def list_devices(device_type: Optional[str] = None) -> dict[str, Any]:
	"""List the NanO devices registered for this home.

	Args:
		device_type: optional filter, either "environment" (temperature/humidity sensors)
			or "energy" (energy monitors). Omit to list all devices.
	"""
	return await _post("/api/services/mcp/devices", {
		"userEmail": NANOSERVER_USER_EMAIL,
		"type": device_type,
	})


@mcp.tool()
async def get_home_temperature_humidity(device_uuid: Optional[str] = None) -> dict[str, Any]:
	"""Get the most recent temperature and humidity reading from a home sensor.

	Args:
		device_uuid: optional specific device UUID. If omitted, uses the first
			environment sensor found for this home.
	"""
	resolved_uuid = await _find_device("environment", device_uuid)

	data = await _post("/api/services/mcp/environment/latest", {"device": resolved_uuid})
	if data is None:
		raise RuntimeError("No hay lecturas registradas para el dispositivo indicado.")
	return data


@mcp.tool()
async def get_temperature_humidity_history(
	from_date: str,
	until_date: Optional[str] = None,
	view_type: str = "byDay",
	device_uuid: Optional[str] = None,
) -> dict[str, Any]:
	"""Get historical temperature/humidity readings for a home sensor.

	Args:
		from_date: start date, ISO format (e.g. "2026-07-01").
		until_date: end date, ISO format. Defaults to now if omitted.
		view_type: aggregation granularity: byMinute, byHour, byDay, byMonth or byYear.
		device_uuid: optional specific device UUID. If omitted, uses the first
			environment sensor found for this home.
	"""
	resolved_uuid = await _find_device("environment", device_uuid)

	payload: dict[str, Any] = {
		"device": resolved_uuid,
		"viewType": view_type,
		"timeZone": NANOSERVER_TIME_ZONE,
		"from": _to_millis(from_date),
	}
	if until_date:
		payload["until"] = _to_millis(until_date)

	return await _post("/api/services/mcp/environment/history", payload)


@mcp.tool()
async def get_home_energy_consumption(device_uuid: Optional[str] = None) -> dict[str, Any]:
	"""Get the most recent energy consumption reading (volts, amps, power) from a home energy monitor.

	Args:
		device_uuid: optional specific device UUID. If omitted, uses the first
			energy monitor found for this home.
	"""
	resolved_uuid = await _find_device("energy", device_uuid)

	data = await _post("/api/services/mcp/energy/latest", {"device": resolved_uuid})
	if data is None:
		raise RuntimeError("No hay lecturas registradas para el dispositivo indicado.")
	return data


@mcp.tool()
async def get_energy_consumption_history(
	from_date: str,
	until_date: Optional[str] = None,
	view_type: str = "byDay",
	device_uuid: Optional[str] = None,
) -> dict[str, Any]:
	"""Get historical energy consumption readings for a home energy monitor.

	Args:
		from_date: start date, ISO format (e.g. "2026-07-01").
		until_date: end date, ISO format. Defaults to now if omitted.
		view_type: aggregation granularity: byMinute, byHour, byDay, byMonth or byYear.
		device_uuid: optional specific device UUID. If omitted, uses the first
			energy monitor found for this home.
	"""
	resolved_uuid = await _find_device("energy", device_uuid)

	payload: dict[str, Any] = {
		"device": resolved_uuid,
		"viewType": view_type,
		"timeZone": NANOSERVER_TIME_ZONE,
		"from": _to_millis(from_date),
	}
	if until_date:
		payload["until"] = _to_millis(until_date)

	return await _post("/api/services/mcp/energy/history", payload)


if __name__ == "__main__":
	app = mcp.streamable_http_app()
	uvicorn.run(app, host=MCP_HOST, port=MCP_PORT)

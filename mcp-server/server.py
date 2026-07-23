import base64
import hashlib
import os
import secrets
from datetime import datetime, timezone
from typing import Any, Optional

import httpx
import uvicorn
from mcp.server.fastmcp import FastMCP
from starlette.responses import JSONResponse

NANOSERVER_BASE_URL = os.environ["NANOSERVER_BASE_URL"].rstrip("/")
NANOSERVER_SECURITY_KEY = os.environ["NANOSERVER_SECURITY_KEY"]
NANOSERVER_SECURITY_SECRET = os.environ["NANOSERVER_SECURITY_SECRET"]
NANOSERVER_USER_EMAIL = os.environ["NANOSERVER_USER_EMAIL"]
NANOSERVER_TIME_ZONE = os.environ.get("NANOSERVER_TIME_ZONE", "America/Argentina/Buenos_Aires")

MCP_HOST = os.environ.get("MCP_HOST", "0.0.0.0")
MCP_PORT = int(os.environ.get("MCP_PORT", "8765"))
MCP_AUTH_TOKEN = os.environ["MCP_AUTH_TOKEN"]

mcp = FastMCP("nanoserver", host=MCP_HOST, port=MCP_PORT)


class BearerAuthMiddleware:
	"""Pure ASGI middleware so streaming (SSE) responses are never buffered."""

	def __init__(self, app, token: str):
		self.app = app
		self.token = f"Bearer {token}".encode("ascii")

	async def __call__(self, scope, receive, send):
		if scope["type"] != "http":
			await self.app(scope, receive, send)
			return

		headers = dict(scope.get("headers") or [])
		if headers.get(b"authorization") != self.token:
			response = JSONResponse({"error": "Unauthorized"}, status_code=401)
			await response(scope, receive, send)
			return

		await self.app(scope, receive, send)


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

	return await _post("/api/services/mcp/environment/latest", {"device": resolved_uuid})


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

	return await _post("/api/services/mcp/energy/latest", {"device": resolved_uuid})


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
	app.add_middleware(BearerAuthMiddleware, token=MCP_AUTH_TOKEN)
	uvicorn.run(app, host=MCP_HOST, port=MCP_PORT)

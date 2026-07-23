<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="appCtx" value="${pageContext.request.contextPath}" />
<!doctype html>
<html>
	<head>
		<meta charset="utf-8">
		<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
		<link rel="icon" sizes="16x16" href="${appCtx}/resources/icons/favicon-16x16.png">
		<link rel="icon" sizes="32x32" href="${appCtx}/resources/icons/favicon-32x32.png">
		<link rel="icon" sizes="48x48" href="${appCtx}/resources/icons/favicon.png">
		<link rel="apple-touch-icon" href="${appCtx}/resources/icons/apple-touch-icon.png">
		<link rel="manifest" href="${appCtx}/resources/icons/manifest.json">
		<link rel="stylesheet" href="${appCtx}/resources/css/vendor/bootstrap-4.0.0.min.css">
		<link rel="stylesheet" href="${appCtx}/resources/css/main.css?v=20251124">
		<script type="text/javascript" src="${appCtx}/resources/js/vendor/jquery-3.5.1.min.js"></script>
		<script type="text/javascript" src="${appCtx}/resources/js/vendor/popper-1.12.9.min.js"></script>
		<script type="text/javascript" src="${appCtx}/resources/js/vendor/bootstrap-4.0.0.min.js"></script>
		<script type="text/javascript" src="${appCtx}/resources/js/secure.js"></script>
		<script type="text/javascript" src="${appCtx}/resources/js/main.js?v=20251120"></script>
		<script type="text/javascript" src="${appCtx}/resources/js/login.js?v=20251120"></script>
		<title>${page_title}</title>
		<script type="text/javascript">
			var appCtx = '${appCtx}';
		</script>
		<c:if test="${logout != null and logout}">
		<script type="text/javascript">
			var sessionMessage = '${sessionMessage}';
			removeCookie('token');
			$(document).ready(function() {
				if (sessionMessage != '') {
					errorMessage($('#login_form'), sessionMessage);
				}
			});
		</script>
		</c:if>
	</head>
	<body>
		<div class="container h-100 min-100">
			<div class="d-flex justify-content-center vertical-center">
				<div class="card border-0 p-3 bg-light login-width">
					<img src="${appCtx}/resources/img/logo_wide_black.svg" class="card-img-top" alt="${page_title}">
					<div class="card-body">
						<form id="login_form" action="${appCtx}/login" method="post">
							<input type="hidden" id="error_msg_general" value="${error_msg_general}" />
							<div class="input-group form-group">
								<input type="text" class="form-control" name="email" id="login_ph_email" placeholder="${login_ph_email}" required>
							</div>
							<div class="input-group form-group">
								<input type="password" class="form-control" name="pass" id="login_ph_pass" placeholder="${login_ph_pass}" required>
							</div>
							<input type="submit" id="login_btn_enter" value="${login_btn_enter}" class="btn btn-primary w-100" />
						</form>
					</div>
					<div class="card-footer border-0 bg-transparent">
						<div class="d-flex justify-content-center mb-3">
							<a href="${appCtx}/forgot" id="login_btn_forgot">${login_btn_forgot}</a>
						</div>
						<ul class="list-group list-group-horizontal">
							<c:forEach items="${availableLocales}" var="availableLocale">
								<li class="list-group-item list-group-item-action flex-fill rounded-0 text-center bg-transparent btn localizer ${currentLocale eq availableLocale ? 'active' : ''}" id="${availableLocale}">
									<img src="${appCtx}/resources/img/${availableLocale}.svg" width="30px" height="30px" />
								</li>
							</c:forEach>
						</ul>
					</div>
				</div>
			</div>
		</div>
	</body>
</html>

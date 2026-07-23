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
		<title>${page_title}</title>
		<script type="text/javascript">
			var appCtx ='${appCtx}';
		</script>
	</head>
	<body>
		<div class="container h-100 min-100">
			<div class="d-flex justify-content-center vertical-center">
				<div class="card border-0 p-3 bg-light login-width">
					<img src="${appCtx}/resources/img/logo_wide_black.svg" class="card-img-top" alt="${page_title}">
					<div class="card-body">
						<form id="recover_form" action="${appCtx}/validateCode" method="post">
							<input type="hidden" id="error_msg_general" value="${error_msg_general}" />
							<input type="hidden" name="email" value="${email}" />
							<div class="input-group form-group">
								<input type="number" min="${minCodeValue}" max="${maxCodeValue}" class="form-control" name="code" id="login_ph_email" placeholder="${recover_ph_code}" required>
							</div>
							<input type="submit" id="login_btn_enter" value="${recover_btn_enter}" class="btn btn-primary w-100" />
						</form>
					</div>
				</div>
			</div>
		</div>
	</body>
</html>

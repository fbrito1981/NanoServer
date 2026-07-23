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
		<link rel="stylesheet" href="${appCtx}/resources/css/reset.css">
		<script type="text/javascript" src="${appCtx}/resources/js/vendor/jquery-3.5.1.min.js"></script>
		<script type="text/javascript" src="${appCtx}/resources/js/vendor/popper-1.12.9.min.js"></script>
		<script type="text/javascript" src="${appCtx}/resources/js/vendor/bootstrap-4.0.0.min.js"></script>
		<script type="text/javascript" src="${appCtx}/resources/js/secure.js"></script>
		<script type="text/javascript" src="${appCtx}/resources/js/main.js?v=20251120"></script>
		<script type="text/javascript" src="${appCtx}/resources/js/reset.js"></script>
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
						<form id="reset_form" action="${appCtx}/resetPassword" method="post">
							<input type="hidden" id="error_msg_general" value="${error_msg_general}" />
							<input type="hidden" name="email" value="${email}" />
							<div class="input-group form-group">
								<input type="password" class="form-control" name="pass" id="newPass" placeholder="${reset_ph_new_pass}" required>
								<div id="newPassStatus"></div>
							</div>
							<div class="input-group form-group">
								<input type="password" class="form-control" id="confirmPass" placeholder="${reset_ph_confirm_pass}" required>
								<div id="confirmPassStatus"></div>
							</div>
							<input type="submit" id="submitPass" value="${reset_btn_enter}" class="btn btn-primary w-100" />
						</form>
					</div>
					<div class="card-footer border-0 bg-transparent">
						<div class="mb-3 text-muted">
							${reset_text_rules}
						</div>
					</div>
				</div>
			</div>
		</div>
	</body>
</html>

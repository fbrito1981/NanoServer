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
		<link rel="stylesheet" href="${appCtx}/resources/css/main.css?v=20260807h">
		<script type="text/javascript" src="${appCtx}/resources/js/vendor/jquery-3.5.1.min.js"></script>
		<script type="text/javascript" src="${appCtx}/resources/js/vendor/popper-1.12.9.min.js"></script>
		<script type="text/javascript" src="${appCtx}/resources/js/vendor/bootstrap-4.0.0.min.js"></script>
		<script type="text/javascript" src="${appCtx}/resources/js/main.js?v=20260807b"></script>
		<title>${page_title}</title>
	</head>
	<body>
		<div class="container h-100 min-100">
			<div class="d-flex justify-content-center vertical-center">
				<div class="container">
					<div class="row my-5">
						<div class="col-12 text-center">
							<img src="${appCtx}/resources/img/logo_wide_black.svg" alt="${page_title}">
						</div>
					</div>
					<div class="row my-3">
						<div class="col-12 text-center">
							<h1 class="display-1">${errorCode}</h1>
						</div>
					</div>
					<div class="row my-3">
						<div class="col-12 text-center">
							<h1 class="display-4">${errorMessage}</h1>
						</div>
					</div>
					<div class="row">
						<div class="col-12 text-center">
							<image src="${appCtx}/resources/img/warning.svg" width="120" height="120" />
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="d-none">
${error}
		</div>
	</body>
</html>

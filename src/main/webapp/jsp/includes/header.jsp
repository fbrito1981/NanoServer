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
		<link rel="stylesheet" href="${appCtx}/resources/css/vendor/bootstrap-4.5.0.min.css">
		<link rel="stylesheet" href="${appCtx}/resources/vendor/fontawesome/css/all.min.css">
		<link rel="stylesheet" href="${appCtx}/resources/css/sidebar.css?v=20251125">
		<link rel="stylesheet" href="${appCtx}/resources/css/main.css">
		<script type="text/javascript" src="${appCtx}/resources/js/vendor/jquery-3.5.1.min.js"></script>
		<script type="text/javascript" src="${appCtx}/resources/js/vendor/popper-1.16.0.min.js"></script>
		<script type="text/javascript" src="${appCtx}/resources/js/vendor/bootstrap-4.5.0.min.js"></script>
		<script defer type="text/javascript" src="${appCtx}/resources/vendor/fontawesome/fontawesome-solid.js"></script>
		<script defer type="text/javascript" src="${appCtx}/resources/vendor/fontawesome/fontawesome-all.js"></script>
		<script type="text/javascript" src="${appCtx}/resources/js/secure.js"></script>
		<script type="text/javascript" src="${appCtx}/resources/js/main.js?v=20251124"></script>
		<title>${page_title}</title>
		<script type="text/javascript">
			var appCtx ='${appCtx}';
			var errorMsg = '${errorMessage}';
			$(document).ready(function() {
				if (errorMsg.length > 0) {
					errorMessage(errorMsg);
				}
			});
		</script>
	</head>
	<body>
		<nav class="navbar navbar-expand-md navbar-dark bg-dark sticky-top">
			<div class="navbar-brand">
				<button class="navbar-toggler d-md-none" type="button" onclick="toggleSidebar()">
					<span class="navbar-toggler-icon"></span>
				</button>
				<%@include file="../../resources/img/logo_flat_white.svg" %>
			</div>
		</nav>
		<div class="container-fluid mt-2">
			<div class="row">
				<nav id="sidebarMenu" class="col-md-2 d-none d-md-block bg-light sidebar small">
					<div class="sidebar-sticky pt-4">
						<ul class="nav flex-column">
							<c:forEach items="${menuItems}" var="menuItem">
							<li class="nav-item">
								<a class="nav-link${menuItem.active ? ' active' : ''}" href="${menuItem.section}">
									<i class="${menuItem.getIcon()} fa-lg fa-fw"></i>
									<span class="pl-3">${menuItem.title}</span>
								</a>
							</li>
							</c:forEach>
							<li class="nav-item mt-auto">
								<a class="nav-link" href="${appCtx}/logout">
									<i class="fas fa-sign-out-alt fa-lg fa-fw"></i>
									<span class="pl-3">${logout_btn}</span>
								</a>
							</li>
						</ul>
					</div>
				</nav>
				<main role="main" class="col-12 col-md-10 ml-sm-auto px-md-4">

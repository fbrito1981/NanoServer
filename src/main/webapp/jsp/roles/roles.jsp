<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="appCtx" value="${pageContext.request.contextPath}" />

<jsp:include page="../includes/header.jsp" />

<jsp:include page="../includes/dataTableHeader.jsp" />

<script type="text/javascript" src="${appCtx}/resources/js/roles.js"></script>

<div class="container py-3">
	<div class="row mt-3 mb-4">
		<div class="col-12 bg-secondary">
			<h2 class="m-3 text-white">${roles_table_header}</h2>
		</div>
	</div>
	<form id="move_form" action="${appCtx}/roles/move">
		<input type="hidden" id="move_up" name="up">
		<input type="hidden" id="move_id" name="id">
		<div class="container">
			<table id="dataTable" class="table">
				<thead class="thead-dark">
					<tr>
						<th scope="col">${roles_table_th_name}</th>
						<th scope="col">${roles_table_th_level}</th>
						<th scope="col">
							<button type="button" class="btn btn-secondary btn-sm" onclick="create();" data-toggle="tooltip" data-placement="top" title="${data_table_tp_create}">
								<i class="fas fa-plus-circle"></i>
							</button>
						</th>
					</tr>
				</thead>
			</table>
		</div>
	</form>
</div>

<jsp:include page="createModal.jsp" />

<jsp:include page="updateModal.jsp" />

<jsp:include page="confirmModal.jsp" />

<jsp:include page="../includes/footer.jsp" />

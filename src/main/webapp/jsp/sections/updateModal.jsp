<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="appCtx" value="${pageContext.request.contextPath}" />

<div id="update_modal" class="modal fade" tabindex="-2" role="dialog">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<h5 class="modal-title" id="edit_title">${data_table_tl_edit}</h5>
				<button type="button" class="close" data-dismiss="modal" aria-label="Close" onclick="table.draw();">
					<span aria-hidden="true">&times;</span>
				</button>
			</div>
			<form id="update_form" action="${appCtx}/sections/update">
				<input type="hidden" name="menuOrder" id="update_menu_order">
				<div class="modal-body">
					<div class="form-group">
						<label for="update_name">${sections_table_th_name}</label>
						<input type="text" class="form-control" name="name" id="update_name" readonly>
					</div>
					<div class="form-group">
						<label for="update_label">${sections_table_th_label}</label>
						<input type="text" class="form-control" id="update_label" readonly>
					</div>
					<div class="form-group">
						<label for="update_min_role">${sections_table_th_min_role}</label>
						<select class="form-control" name="minRoleName" id="update_min_role" required>
							<c:forEach var="role" items="${roles}">
							<option value="${role.name}">${role.name}</option>
							</c:forEach>
						</select>
					</div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-secondary" data-dismiss="modal" onclick="table.draw();">${data_table_btn_cancel}</button>
					<button type="submit" class="btn btn-primary">
						<span class="spinner-border spinner-border-sm d-none" role="status" aria-hidden="true" id="update_spinner"></span>
						${data_table_btn_save}
					</button>
				</div>
			</form>
		</div>
	</div>
</div>

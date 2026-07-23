<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="appCtx" value="${pageContext.request.contextPath}" />

<div id="confirm_modal" class="modal fade" tabindex="-3" role="dialog">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<h5 class="modal-title">${data_table_msg_confirm}</h5>
				<button type="button" class="close" data-dismiss="modal" aria-label="Close" onclick="table.draw();">
					<span aria-hidden="true">&times;</span>
				</button>
			</div>
			<form id="remove_form" action="${appCtx}/devices/remove">
				<div class="modal-body">
					<h6 class="pb-3">${data_table_msg_delete}</h6>
					<div class="form-group">
						<label for="remove_uuid">${devices_table_th_uuid}</label>
						<input type="text" class="form-control" name="uuid" id="remove_uuid" readonly>
					</div>
					<div class="form-group">
						<label for="remove_name">${devices_table_th_name}</label>
						<input type="text" class="form-control" name="name" id="remove_name" readonly>
					</div>
					<div class="form-group">
						<label for="remove_model">${devices_table_th_model}</label>
						<input type="text" class="form-control" name="model" id="remove_model" readonly>
					</div>
					<div class="form-group">
						<label for="remove_os">${devices_table_th_os}</label>
						<input type="text" class="form-control" name="os" id="remove_os" readonly>
					</div>
					<div class="form-group">
						<label for="remove_version">${devices_table_th_version}</label>
						<input type="text" class="form-control" name="version" id="remove_version" readonly>
					</div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-secondary" data-dismiss="modal" onclick="table.draw();">${data_table_btn_cancel}</button>
					<button type="submit" class="btn btn-danger">
						<span class="spinner-border spinner-border-sm d-none" role="status" aria-hidden="true" id="remove_spinner"></span>
						${data_table_btn_continue}
					</button>
				</div>
			</form>
		</div>
	</div>
</div>

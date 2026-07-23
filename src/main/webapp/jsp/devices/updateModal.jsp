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
			<form id="update_form" action="${appCtx}/devices/update">
				<div class="modal-body">
					<div class="form-group form-check">
						<input type="checkbox" class="form-check-input" name="active" id="update_active">
						<label class="form-check-label" for="update_active">${devices_table_th_active}</label>
					</div>
					<div class="form-group">
						<label for="update_uuid">${devices_table_th_uuid}</label>
						<input type="text" class="form-control" name="uuid" id="update_uuid" readonly>
					</div>
					<div class="form-group">
						<label for="update_name">${devices_table_th_name}</label>
						<input type="text" class="form-control" name="name" id="update_name" aria-describedby="validationNameFeedback" onkeyup="validateName('update');" required>
						<div id="validationNameFeedback" class="invalid-feedback">
							${devices_unique_name_message}
						</div>
					</div>
					<div class="form-group">
						<label for="update_model">${devices_table_th_model}</label>
						<input type="text" class="form-control" name="model" id="update_model" required>
					</div>
					<div class="form-group">
						<label for="update_os">${devices_table_th_os}</label>
						<input type="text" class="form-control" name="os" id="update_os" required>
					</div>
					<div class="form-group">
						<label for="update_version">${devices_table_th_version}</label>
						<input type="text" class="form-control" name="version" id="update_version" required>
					</div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-secondary" data-dismiss="modal" onclick="table.draw();">${data_table_btn_cancel}</button>
					<button type="submit" class="btn btn-primary" id="update_submit">
						<span class="spinner-border spinner-border-sm d-none" role="status" aria-hidden="true" id="update_spinner"></span>
						${data_table_btn_save}
					</button>
				</div>
			</form>
		</div>
	</div>
</div>

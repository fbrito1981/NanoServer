<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="appCtx" value="${pageContext.request.contextPath}" />

<div id="create_modal" class="modal fade" tabindex="-1" role="dialog">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<h5 class="modal-title" id="create_title">${data_table_tl_create}</h5>
				<button type="button" class="close" data-dismiss="modal" aria-label="Close" onclick="table.draw();">
					<span aria-hidden="true">&times;</span>
				</button>
			</div>
			<form id="create_form" action="${appCtx}/devices/create">
				<div class="modal-body">
					<div class="form-group">
						<label for="create_uuid">${devices_table_th_uuid}</label>
						<div class="input-group">
							<div class="input-group-prepend">
								<button type="button" class="input-group-text btn-sm-important" onclick="getRandomId('create_uuid');" data-toggle="tooltip" data-placement="top" title="${devices_generate_randomly_legend}">
									<i class="fas fa-magic"></i>
								</button>
							</div>
							<input type="text" class="form-control" name="uuid" id="create_uuid" required>
						</div>
					</div>
					<div class="form-group">
						<label for="create_name">${devices_table_th_name}</label>
						<input type="text" class="form-control" name="name" id="create_name" aria-describedby="validationNameFeedback" onkeyup="validateName('create');" required>
						<div id="validationNameFeedback" class="invalid-feedback">
							${devices_unique_name_message}
						</div>
					</div>
					<div class="form-group">
						<label for="create_model">${devices_table_th_model}</label>
						<input type="text" class="form-control" name="model" id="create_model" required>
					</div>
					<div class="form-group">
						<label for="create_os">${devices_table_th_os}</label>
						<input type="text" class="form-control" name="os" id="create_os" required>
					</div>
					<div class="form-group">
						<label for="create_version">${devices_table_th_version}</label>
						<input type="text" class="form-control" name="version" id="create_version" required>
					</div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-secondary" data-dismiss="modal" onclick="table.draw();">${data_table_btn_cancel}</button>
					<button type="submit" class="btn btn-primary" id="create_submit">
						<span class="spinner-border spinner-border-sm d-none" role="status" aria-hidden="true" id="create_spinner"></span>
						${data_table_btn_save}
					</button>
				</div>
			</form>
		</div>
	</div>
</div>

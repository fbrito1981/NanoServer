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
			<form id="update_form" action="${appCtx}/users/update">
				<div class="modal-body">
					<div class="form-group form-check">
						<input type="checkbox" class="form-check-input" name="active" id="update_active">
						<label class="form-check-label" for="update_active">${users_table_th_active}</label>
					</div>
					<div class="form-group">
						<label for="update_email">${users_table_th_email}</label>
						<input type="text" class="form-control" name="email" id="update_email" readonly>
					</div>
					<div class="form-group">
						<label for="update_name">${users_table_th_name}</label>
						<input type="text" class="form-control" name="name" id="update_name" required>
					</div>
					<div class="form-group">
						<label for="update_role">${users_table_th_role}</label>
						<select class="form-control" name="roleName" id="update_role" required>
							<c:forEach var="role" items="${updateRoles}">
							<option value="${role.name}">${role.name}</option>
							</c:forEach>
						</select>
					</div>
					<div class="form-group">
						<label for="update_picture">${users_table_th_picture}</label>
						<input type="file" class="d-none" name="picture" id="update_picture" accept="image/png, image/jpeg">
						<input type="checkbox" class="d-none" name="pictureRemoved" id="update_picture_removed">
						<div class="container image-container">
							<div class="row">
								<div class="p-0 rounded-circle border bg-light text-center image-preview profile-picture">
									<i class="fas fa-camera-retro"></i>
									<canvas id="update_picture_img"></canvas>
								</div>
								<div class="ml-2 image-tools slide-tools">
									<button type="button" class="btn btn-secondary m-1" id="update_picture_edit" data-toggle="tooltip" data-placement="top" title="${data_table_tp_edit}" onclick="setPicture('update_picture');"><i class="far fa-edit"></i></button>
									<button type="button" class="btn btn-danger m-1" id="update_picture_remove" data-toggle="tooltip" data-placement="top" title="${data_table_tp_delete}" onclick="removePicture('update_picture');"><i class="far fa-trash-alt"></i></button>
								</div>
							</div>
						</div>
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

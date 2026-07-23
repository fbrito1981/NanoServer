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
			<form id="create_form" action="${appCtx}/users/create">
				<div class="modal-body">
					<div class="form-group">
						<label for="create_email">${users_table_th_email}</label>
						<input type="text" class="form-control" name="email" id="create_email" required>
					</div>
					<div class="form-group">
						<label for="create_name">${users_table_th_name}</label>
						<input type="text" class="form-control" name="name" id="create_name" required>
					</div>
					<div class="form-group">
						<label for="create_role">${users_table_th_role}</label>
						<select class="form-control" name="roleName" id="create_role" required>
							<c:forEach var="role" items="${createRoles}">
							<option value="${role.name}">${role.name}</option>
							</c:forEach>
						</select>
					</div>
					<div class="form-group mb-0">
						<label for="create_picture">${users_table_th_picture}</label>
						<input type="file" class="d-none" name="picture" id="create_picture" accept="image/png, image/jpeg">
						<div class="container image-container">
							<div class="row">
								<div class="p-0 rounded-circle border bg-light text-center image-preview profile-picture">
									<i class="fas fa-camera-retro"></i>
									<canvas id="create_picture_img"></canvas>
								</div>
								<div class="ml-2 image-tools slide-tools">
									<button type="button" class="btn btn-secondary m-1" id="create_picture_edit" data-toggle="tooltip" data-placement="top" title="${data_table_tp_edit}" onclick="setPicture('create_picture');"><i class="far fa-edit"></i></button>
									<button type="button" class="btn btn-danger m-1" id="create_picture_remove" data-toggle="tooltip" data-placement="top" title="${data_table_tp_delete}" onclick="removePicture('create_picture');"><i class="far fa-trash-alt"></i></button>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-secondary" data-dismiss="modal" onclick="table.draw();">${data_table_btn_cancel}</button>
					<button type="submit" class="btn btn-primary">
						<span class="spinner-border spinner-border-sm d-none" role="status" aria-hidden="true" id="create_spinner"></span>
						${data_table_btn_save}
					</button>
				</div>
			</form>
		</div>
	</div>
</div>

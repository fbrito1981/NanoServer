<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="appCtx" value="${pageContext.request.contextPath}" />

<div id="send_email_modal" class="modal fade" tabindex="-4" role="dialog">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<h5 class="modal-title">${data_table_msg_confirm}</h5>
				<button type="button" class="close" data-dismiss="modal" aria-label="Close" onclick="table.draw();">
					<span aria-hidden="true">&times;</span>
				</button>
			</div>
			<form id="send_email_form" action="${appCtx}/users/sendEMail">
				<div class="modal-body">
					<h6 class="pb-3">${data_table_msg_send_mail}</h6>
					<div class="form-group">
						<label for="send_email_email">${users_table_th_email}</label>
						<input type="text" class="form-control" name="email" id="send_email_email" readonly>
					</div>
					<div class="form-group">
						<label for="send_email_name">${users_table_th_name}</label>
						<input type="text" class="form-control" name="name" id="send_email_name" readonly>
					</div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-secondary" data-dismiss="modal" onclick="table.draw();">${data_table_btn_cancel}</button>
					<button type="submit" class="btn btn-danger">
						<span class="spinner-border spinner-border-sm d-none" role="status" aria-hidden="true" id="send_email_spinner"></span>
						${data_table_btn_continue}
					</button>
				</div>
			</form>
		</div>
	</div>
</div>

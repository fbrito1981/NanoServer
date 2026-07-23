<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="appCtx" value="${pageContext.request.contextPath}" />

<jsp:include page="../includes/header.jsp" />

<jsp:include page="../includes/dataTableHeader.jsp" />

<script type="text/javascript" src="${appCtx}/resources/js/users.js?v=20210312"></script>

<script type="text/javascript">
var usersFeaturesPickFeature = '${users_features_pick_feature}';
var usersFeaturesErrorCompletePending = '${users_features_error_complete_pending}'
var usersFeaturesErrorFeatureRequired = '${users_features_error_feature_required}';
var usersFeaturesErrorContentRequired = '${users_features_error_content_required}';
</script>

<div class="container py-3">
	<div class="row mt-3 mb-4">
		<div class="col-12 bg-secondary">
			<h2 class="m-3 text-white">${users_table_header}</h2>
		</div>
	</div>
	<form id="move_form">
		<div class="container">
			<table id="dataTable" class="table">
				<thead class="thead-dark">
					<tr>
						<th scope="col">${users_table_th_active}</th>
						<th scope="col">${users_table_th_name}</th>
						<th scope="col">${users_table_th_email}</th>
						<th scope="col">${users_table_th_role}</th>
						<th scope="col">${users_table_th_created}</th>
						<th scope="col">${users_table_th_updated}</th>
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

<jsp:include page="sendEmailModal.jsp" />

<jsp:include page="../includes/footer.jsp" />

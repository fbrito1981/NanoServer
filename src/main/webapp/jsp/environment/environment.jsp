<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="appCtx" value="${pageContext.request.contextPath}" />

<jsp:include page="../includes/header.jsp" />

<link rel="stylesheet" href="${appCtx}/resources/css/jquery.datetimepicker.min.css">

<script type="text/javascript" src="${appCtx}/resources/js/jquery.datetimepicker.full.min.js"></script>
<script type="text/javascript" src="${appCtx}/resources/js/environment.js?v=1"></script>
<script src="https://canvasjs.com/assets/script/jquery.canvasjs.min.js"></script>

<script type="text/javascript">
var locale = '${environment_locale}';
var dateFormat = '${environment_date_format}';
var generalErrorMessage = '${error_msg_general}';
var generalInfoMessage = '${info_msg_general}';
var fromDate = ${fromDate.getTime()};
var toDate = ${toDate.getTime()};
var tempLabel = '${environment_temp_label}';
var tempUnitLabel = '${environment_temp_unit_label}';
var humLabel = '${environment_hum_label}';
var humUnitLabel = '${environment_hum_unit_label}';
var dataFormAction = '${appCtx}/environment/data';
</script>

<div class="container py-3">
	<div class="row mt-3 mb-4">
		<div class="col-12 bg-secondary">
			<h2 class="m-3 text-white">${environment_header_title}</h2>
		</div>
	</div>
	<form id="instant_form" method="post" action="${appCtx}/environment/instant">
		<input type="hidden" id="instantDevice" name="device" />
	</form>
	<form id="data_form" method="post">
		<input type="hidden" id="timeZone" name="timeZone" />
		<div class="container">
			<div class="form-row">
				<div class="form-group col-md-3">
					<label for="device">${environment_device_label}</label>
					<select id="device" name="device" class="form-control">
						<option selected disabled>${environment_select_option_label}</option>
						<c:forEach var="device" items="${devices}">
						<option value="${device.uuid}">${device.name}</option>
						</c:forEach>
					</select>
				</div>
				<div class="form-group col-md-3">
					<label for="environmentViewType">${environment_view_type_label}</label>
					<select id="viewType" name="viewType" class="form-control">
						<c:forEach var="environmentViewType" items="${environmentViewTypes}">
						<option value="${environmentViewType.getValue()}"
							<c:if test="${environmentViewType.equals(environmentViewTypeDefault)}">selected</c:if>
							>
							${environmentViewType.getLabel()}
						</option>
						</c:forEach>
					</select>
				</div>
				<div class="form-group col-md-3">
					<label for="from">${environment_from_date_label}</label>
					<input type="text" class="d-none" id="from_value" name="from">
					<div class="input-group">
						<div class="input-group-prepend">
							<div class="input-group-text"><i class="far fa-calendar-alt"></i></div>
						</div>
						<input type="text" class="form-control" id="from" autocomplete="off" readonly>
					</div>
				</div>
				<div class="form-group col-md-3">
					<label for="until">${environment_until_date_label}</label>
					<input type="text" class="d-none" id="until_value" name="until">
					<div class="input-group">
						<div class="input-group-prepend">
							<div class="input-group-text"><i class="far fa-calendar-alt"></i></div>
						</div>
						<input type="text" class="form-control" id="until" autocomplete="off" readonly>
						<div class="invalid-feedback">
							${environment_date_error_message}
						</div>
					</div>
				</div>
			</div>
			<div class="row row-cols-1 no-gutters mb-3">
				<div class="card">
					<div class="card-header">
						<h4>${environment_instant_values_label}</h4>
					</div>
					<div class="card-body">
						<div class="row">
							<div class="col-sm-6">
								<div class="card">
									<div class="card-body text-center">
										<h1 class="card-title">${environment_temp_label}</h1>
										<h2 id="tempValue" class="display-4">--</h2>
										<h6 class="no-device-selected">${environment_select_device_label}</h6>
									</div>
								</div>
							</div>
							<div class="col-sm-6">
								<div class="card">
									<div class="card-body text-center">
										<h1 class="card-title">${environment_hum_label}</h1>
										<h2 id="humValue" class="display-4">--</h2>
										<h6 class="no-device-selected">${environment_select_device_label}</h6>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="row row-cols-1 no-gutters mb-3">
				<div class="card">
					<div class="card-header">
						<h4>${environment_temp_label}</h4>
					</div>
					<div class="card-body">
						<div id="tempChart" class="w-100 p-3" style="height: 250px;"></div>
						<h6 class="no-device-selected">${environment_select_device_label}</h6>
					</div>
				</div>
			</div>
			<div class="row row-cols-1 no-gutters mb-3">
				<div class="card">
					<div class="card-header">
						<h4>${environment_hum_label}</h4>
					</div>
					<div class="card-body">
						<div id="humChart" class="w-100 p-3" style="height: 250px;"></div>
						<h6 class="no-device-selected">${environment_select_device_label}</h6>
					</div>
				</div>
			</div>
		</div>
	</form>
</div>

<jsp:include page="../includes/footer.jsp" />

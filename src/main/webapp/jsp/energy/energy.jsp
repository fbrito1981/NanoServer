<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="appCtx" value="${pageContext.request.contextPath}" />

<jsp:include page="../includes/header.jsp" />

<link rel="stylesheet" href="${appCtx}/resources/css/jquery.datetimepicker.min.css">
<link rel="stylesheet" href="${appCtx}/resources/css/energy.css?v=20251124">

<script type="text/javascript" src="${appCtx}/resources/js/jquery.datetimepicker.full.min.js"></script>
<script type="text/javascript" src="${appCtx}/resources/js/energy.js?v=20260521"></script>
<script src="https://canvasjs.com/assets/script/jquery.canvasjs.min.js"></script>

<script type="text/javascript">
var locale = '${energy_locale}';
var dateFormat = '${energy_date_format}';
var generalErrorMessage = '${error_msg_general}';
var generalInfoMessage = '${info_msg_general}';
var fromDate = ${fromDate.getTime()};
var toDate = ${toDate.getTime()};
var powerUnitLabel = '${energy_power_unit_label}';
var energyUnitLabel = '${energy_unit_label}';
var energyLabel = '${energy_energy_label}';
var activePowerLabel = '${energy_active_power_label}';
var reactivePowerLabel = '${energy_reactive_power_label}';
var apparentPowerLabel = '${energy_apparent_power_label}';
var voltsLabel = '${energy_volts_label}';
var voltsUnitLabel = '${energy_volts_unit_label}';
var ampsLabel = '${energy_amps_label}';
var ampsUnitLabel = '${energy_amps_unit_label}';
var cosPhiLabel = '${energy_cos_phi_label}';
var cosPhiUnitLabel = '${energy_cos_phi_unit_label}';
var angleLabel = '${energy_angle_label}';
var frequencyLabel = '${energy_frequency_label}';
var frequencyUnitLabel = '${energy_frequency_unit_label}';
var energyFormAction = '${appCtx}/energy/energy';
var dataFormAction = '${appCtx}/energy/data';
</script>

<div class="container py-3">
	<div class="row mt-3 mb-4">
		<div class="col-12 bg-secondary">
			<h2 class="m-3 text-white">${energy_header_title}</h2>
		</div>
	</div>
	<form id="instant_form" method="post" action="${appCtx}/energy/instant">
		<input type="hidden" id="instantDevice" name="device" />
	</form>
	<form id="data_form" method="post">
		<input type="hidden" id="timeZone" name="timeZone" />
		<div class="container">
			<div class="form-row">
				<div class="form-group col-md-3">
					<label for="device">${energy_device_label}</label>
					<select id="device" name="device" class="form-control">
						<option selected disabled>${energy_select_option_label}</option>
						<c:forEach var="device" items="${devices}">
						<option value="${device.uuid}">${device.name}</option>
						</c:forEach>
					</select>
				</div>
				<div class="form-group col-md-3">
					<label for="energyViewType">${energy_view_type_label}</label>
					<select id="viewType" name="viewType" class="form-control">
						<c:forEach var="energyViewType" items="${energyViewTypes}">
						<option value="${energyViewType.getValue()}"
							<c:if test="${energyViewType.equals(energyViewTypeDefault)}">selected</c:if>
							>
							${energyViewType.getLabel()}
						</option>
						</c:forEach>
					</select>
				</div>
				<div class="form-group col-md-3">
					<label for="from">${energy_from_date_label}</label>
					<input type="text" class="d-none" id="from_value" name="from">
					<div class="input-group">
						<div class="input-group-prepend">
							<div class="input-group-text"><i class="far fa-calendar-alt"></i></div>
						</div>
						<input type="text" class="form-control" id="from" autocomplete="off" readonly>
					</div>
				</div>
				<div class="form-group col-md-3">
					<label for="until">${energy_until_date_label}</label>
					<input type="text" class="d-none" id="until_value" name="until">
					<div class="input-group">
						<div class="input-group-prepend">
							<div class="input-group-text"><i class="far fa-calendar-alt"></i></div>
						</div>
						<input type="text" class="form-control" id="until" autocomplete="off" readonly>
						<div class="invalid-feedback">
							${energy_date_error_message}
						</div>
					</div>
				</div>
			</div>
			<div class="row row-cols-1 no-gutters mb-3">
				<div class="card">
					<div class="card-header">
						<h4>${energy_instant_values_label}</h4>
					</div>
					<div class="card-body">
						<div class="row">
							<div class="col-sm-6">
								<div class="card">
									<div class="card-body text-center">
										<h1 class="card-title">${energy_volts_label}</h1>
										<div id="voltsChart" class="w-100" style="height: 250px;"></div>
										<div id="voltsNeedle" class="needle"></div>
										<span id="voltsValue" class="value"></span>
										<h4 id="frequencyValue" class="card p-3 floating-value">--</h4>
										<h6 class="no-device-selected">${energy_select_device_label}</h6>
									</div>
								</div>
							</div>
							<div class="col-sm-6">
								<div class="card">
									<div class="card-body text-center">
										<h1 class="card-title">${energy_power_label}</h1>
										<div id="wattsChart" class="w-100" style="height: 250px;"></div>
										<div id="wattsNeedle" class="needle"></div>
										<span id="wattsValue" class="value"></span>
										<h4 id="cosPhiValue" class="card p-3 floating-value">--</h4>
										<h6 class="no-device-selected">${energy_select_device_label}</h6>
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
						<h4>${energy_consumption_label}</h4>
					</div>
					<div class="card-body">
						<div id="energyChart" class="w-100 p-3" style="height: 250px;"></div>
						<h6 class="no-device-selected">${energy_select_device_label}</h6>
					</div>
				</div>
			</div>
			<div class="row row-cols-1 no-gutters mb-3">
				<div class="card">
					<div class="card-header">
						<h4>${energy_power_label}</h4>
					</div>
					<div class="card-body">
						<div id="powerChart" class="w-100 p-3" style="height: 250px;"></div>
						<h6 class="no-device-selected">${energy_select_device_label}</h6>
					</div>
				</div>
			</div>
			<div class="row row-cols-1 no-gutters mb-3">
				<div class="card">
					<div class="card-header">
						<h4>${energy_levels_label}</h4>
					</div>
					<div class="card-body">
						<div id="levelsChart" class="w-100 p-3" style="height: 250px;"></div>
						<h6 class="no-device-selected">${energy_select_device_label}</h6>
					</div>
				</div>
			</div>
			<div class="row row-cols-1 no-gutters mb-3">
				<div class="card">
					<div class="card-header">
						<h4>${energy_cos_phi_frequency_label}</h4>
					</div>
					<div class="card-body">
						<div id="cosPhiChart" class="w-100 p-3" style="height: 250px;"></div>
						<h6 class="no-device-selected">${energy_select_device_label}</h6>
					</div>
				</div>
			</div>
		</div>
	</form>
</div>

<jsp:include page="../includes/footer.jsp" />

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="appCtx" value="${pageContext.request.contextPath}" />

<jsp:include page="../includes/header.jsp" />

<link rel="stylesheet" href="${appCtx}/resources/css/jquery.datetimepicker.min.css">
<link rel="stylesheet" href="${appCtx}/resources/css/panel.css?v=1">

<script type="text/javascript" src="${appCtx}/resources/js/jquery.datetimepicker.full.min.js"></script>
<script type="text/javascript" src="${appCtx}/resources/js/energy.js?v=20260824">
</script>
<script src="https://canvasjs.com/assets/script/jquery.canvasjs.min.js"></script>

<script type="text/javascript">
var locale = '${energy_locale}';
var dateFormat = '${energy_date_format}';
var generalErrorMessage = '${error_msg_general}';
var generalInfoMessage = '${info_msg_general}';
var todayDate = ${toDate.getTime()};
var powerUnitLabel = '${energy_power_unit_label}';
var energyUnitLabel = '${energy_unit_label}';
var energyLabel = '${energy_energy_label}';
var voltsLabel = '${energy_volts_label}';
var voltsUnitLabel = '${energy_volts_unit_label}';
var currentLabel = '${energy_current_label}';
var ampsUnitLabel = '${energy_amps_unit_label}';
var powerLabel = '${energy_power_label}';
var frequencyLabel = '${energy_frequency_label}';
var frequencyUnitLabel = '${energy_frequency_unit_label}';
var cosPhiLabel = '${energy_cos_phi_label}';
var periodDayLabel = '${energy_period_day_label}';
var periodMonthLabel = '${energy_period_month_label}';
var periodYearLabel = '${energy_period_year_label}';
var periodAllLabel = '${energy_period_all_label}';
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
		<input type="hidden" id="from" name="from" />
		<input type="hidden" id="until" name="until" />
		<input type="hidden" id="viewType" name="viewType" />
		<div class="container">
			<div class="form-row mb-3">
				<div class="form-group col-md-4">
					<label for="device">${energy_device_label}</label>
					<select id="device" name="device" class="form-control">
						<option selected disabled>${energy_select_option_label}</option>
						<c:forEach var="device" items="${devices}">
						<option value="${device.uuid}">${device.name}</option>
						</c:forEach>
					</select>
				</div>
			</div>
			<div class="panel-card">
				<div class="panel-card-header">
					<h4>${energy_instant_values_label}</h4>
					<button type="button" id="refreshInstant" class="panel-refresh-btn" title="${energy_refresh_label}">
						<i class="fas fa-sync-alt"></i>
					</button>
				</div>
				<div class="last-values d-none" id="lastValues">
					<div class="last-value-item">
						<span class="last-value-label">${energy_volts_label}</span>
						<span id="voltsValue" class="last-value-value">--</span>
					</div>
					<div class="last-value-item">
						<span class="last-value-label">${energy_current_label}</span>
						<span id="ampsValue" class="last-value-value">--</span>
					</div>
					<div class="last-value-item">
						<span class="last-value-label">${energy_power_label}</span>
						<span id="powerValue" class="last-value-value">--</span>
					</div>
				</div>
				<h6 class="no-device-selected" id="lastValuesPlaceholder">${energy_select_device_label}</h6>
			</div>
			<div class="panel-card">
				<div class="panel-card-header">
					<h4>${energy_accumulated_label}</h4>
				</div>
				<div class="last-values d-none" id="accumulatedValues">
					<div class="last-value-item">
						<span id="accumulatedValue" class="last-value-value">--</span>
					</div>
				</div>
				<h6 class="no-device-selected" id="accumulatedPlaceholder">${energy_select_device_label}</h6>
			</div>
			<div class="chart-wrapper mb-3">
				<div id="metricChart" class="panel-chart"></div>
				<h6 class="no-device-selected" id="chartPlaceholder">${energy_select_device_label}</h6>
			</div>
			<div class="metric-select-wrap">
				<select id="metric" class="metric-select">
					<option value="power">${energy_power_label}</option>
					<option value="volts">${energy_volts_label}</option>
					<option value="amps">${energy_current_label}</option>
					<option value="energy">${energy_energy_label}</option>
					<option value="frequency">${energy_frequency_label}</option>
					<option value="cosPhi">${energy_cos_phi_label}</option>
				</select>
				<i class="fas fa-chevron-down"></i>
			</div>
			<div class="period-toggle">
				<button type="button" class="period-toggle-item active" data-period="day">${energy_period_day_label}</button>
				<button type="button" class="period-toggle-item" data-period="month">${energy_period_month_label}</button>
				<button type="button" class="period-toggle-item" data-period="year">${energy_period_year_label}</button>
				<button type="button" class="period-toggle-item" data-period="all">${energy_period_all_label}</button>
			</div>
			<div class="date-picker-group" id="dateFilterWrap">
				<i class="far fa-calendar-alt"></i>
				<input type="text" id="datePicker" autocomplete="off" readonly>
				<input type="text" class="d-none" id="datePicker_value">
			</div>
		</div>
	</form>
</div>

<jsp:include page="../includes/footer.jsp" />

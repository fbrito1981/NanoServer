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
<script type="text/javascript" src="${appCtx}/resources/js/environment.js?v=3"></script>
<script src="https://canvasjs.com/assets/script/jquery.canvasjs.min.js"></script>

<script type="text/javascript">
var locale = '${environment_locale}';
var dateFormat = '${environment_date_format}';
var generalErrorMessage = '${error_msg_general}';
var generalInfoMessage = '${info_msg_general}';
var todayDate = ${toDate.getTime()};
var tempLabel = '${environment_temp_label}';
var tempUnitLabel = '${environment_temp_unit_label}';
var humLabel = '${environment_hum_label}';
var humUnitLabel = '${environment_hum_unit_label}';
var periodDayLabel = '${environment_period_day_label}';
var periodMonthLabel = '${environment_period_month_label}';
var periodYearLabel = '${environment_period_year_label}';
var periodAllLabel = '${environment_period_all_label}';
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
		<input type="hidden" id="from" name="from" />
		<input type="hidden" id="until" name="until" />
		<input type="hidden" id="viewType" name="viewType" />
		<div class="container">
			<div class="form-row mb-3">
				<div class="form-group col-md-4">
					<label for="device">${environment_device_label}</label>
					<select id="device" name="device" class="form-control">
						<option selected disabled>${environment_select_option_label}</option>
						<c:forEach var="device" items="${devices}">
						<option value="${device.uuid}">${device.name}</option>
						</c:forEach>
					</select>
				</div>
			</div>
			<div class="panel-card">
				<div class="panel-card-header">
					<h4>${environment_instant_values_label}</h4>
					<button type="button" id="refreshInstant" class="panel-refresh-btn" title="${environment_refresh_label}">
						<i class="fas fa-sync-alt"></i>
					</button>
				</div>
				<div class="last-values d-none" id="lastValues">
					<div class="last-value-item">
						<span class="last-value-label">${environment_temp_label}</span>
						<span id="tempValue" class="last-value-value">--</span>
					</div>
					<div class="last-value-item">
						<span class="last-value-label">${environment_hum_label}</span>
						<span id="humValue" class="last-value-value">--</span>
					</div>
				</div>
				<h6 class="no-device-selected" id="lastValuesPlaceholder">${environment_select_device_label}</h6>
			</div>
			<div class="chart-wrapper mb-3">
				<div id="metricChart" class="panel-chart"></div>
				<h6 class="no-device-selected" id="chartPlaceholder">${environment_select_device_label}</h6>
			</div>
			<div class="metric-select-wrap">
				<select id="metric" class="metric-select">
					<option value="temp">${environment_temp_label}</option>
					<option value="hum">${environment_hum_label}</option>
				</select>
				<i class="fas fa-chevron-down"></i>
			</div>
			<div class="period-toggle">
				<button type="button" class="period-toggle-item active" data-period="day">${environment_period_day_label}</button>
				<button type="button" class="period-toggle-item" data-period="month">${environment_period_month_label}</button>
				<button type="button" class="period-toggle-item" data-period="year">${environment_period_year_label}</button>
				<button type="button" class="period-toggle-item" data-period="all">${environment_period_all_label}</button>
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

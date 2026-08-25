var instantTimer;
var lastPowerLogs = null;
var lastEnergyLogs = null;
var selectedMetric = 'power';
var selectedPeriod = 'day';

$(document).ready(function() {
	$('#device').change(onDeviceChanged);
	$('#metric').change(onMetricChanged);
	$('#refreshInstant').click(onRefreshClicked);
	$('.period-toggle-item').click(onPeriodClicked);
	$('#datePicker').change(onDateChanged);
	setupDatetimepicker('datePicker', dateFormat, locale, todayDate, false);
	CanvasJS.addCultureInfo('es', {
		decimalSeparator: ',',
		digitGroupSeparator: '.',
		savePNGText: 'Guardar como PNG',
		saveJPGText: 'Guardar como JPG',
		printText: 'Imprimir',
		menuText: 'Más opciones',
		days: ['Domingo', 'Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado'],
		shortDays: ['Dom', 'Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb'],
		months: ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio', 'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'],
		shortMonths: ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun', 'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic']
	});
	const timeZone = Intl.DateTimeFormat().resolvedOptions().timeZone;
	$('#timeZone').val(timeZone);
});

function onDeviceChanged() {
	var value = $('#device').val();
	if (value.length > 0) {
		$('#instantDevice').val(value);
		clearInstantTimer();
		resetPanels();
		applyFilters();
	}
}

function resetPanels() {
	lastPowerLogs = null;
	lastEnergyLogs = null;
	$('#lastValues, #accumulatedValues').addClass('d-none');
	$('#metricChart').empty();
	$('.no-device-selected').show();
}

function onMetricChanged() {
	selectedMetric = $('#metric').val();
	renderChart();
}

function onPeriodClicked() {
	selectedPeriod = $(this).data('period');
	$('.period-toggle-item').removeClass('active');
	$(this).addClass('active');
	$('#dateFilterWrap').toggle(selectedPeriod !== 'all');
	applyFilters();
}

function onDateChanged() {
	applyFilters();
}

function onRefreshClicked() {
	clearInstantTimer();
	getInstantValues();
}

function applyFilters() {
	var device = $('#device').val();
	if (!device) {
		return;
	}
	var baseMillis = parseInt($('#datePicker_value').val());
	var baseDate = isNaN(baseMillis) ? new Date() : new Date(baseMillis);
	var from, until, viewType;
	switch (selectedPeriod) {
	case 'month':
		from = new Date(baseDate.getFullYear(), baseDate.getMonth(), 1);
		until = new Date(from.getFullYear(), from.getMonth() + 1, 1);
		viewType = 'byDay';
		break;
	case 'year':
		from = new Date(baseDate.getFullYear(), 0, 1);
		until = new Date(from.getFullYear() + 1, 0, 1);
		viewType = 'byMonth';
		break;
	case 'all':
		from = new Date(2000, 0, 1);
		until = new Date();
		until.setDate(until.getDate() + 1);
		viewType = 'byYear';
		break;
	default:
		from = new Date(baseDate.getFullYear(), baseDate.getMonth(), baseDate.getDate());
		until = new Date(from);
		until.setDate(until.getDate() + 1);
		viewType = 'byHour';
		break;
	}
	$('#from').val(from.getTime());
	$('#until').val(until.getTime());
	$('#viewType').val(viewType);
	showLoading();
	getEnergyValues();
}

var processData = function(response) {
	hideLoading();
	if (response && response.energyLogs) {
		lastEnergyLogs = response.energyLogs;
		updateAccumulated();
		$('#accumulatedPlaceholder').hide();
		$('#accumulatedValues').removeClass('d-none');
		if (selectedMetric === 'energy') {
			$('#chartPlaceholder').hide();
			renderChart();
		}
		getDataValues();
	} else if (response && response.logs) {
		lastPowerLogs = response.logs;
		if (selectedMetric !== 'energy') {
			$('#chartPlaceholder').hide();
			renderChart();
		}
		getInstantValues();
	} else if (response && response.volts !== undefined) {
		$('#voltsValue').html(response.volts.toFixed(1) + ' ' + voltsUnitLabel);
		$('#ampsValue').html(response.amps.toFixed(1) + ' ' + ampsUnitLabel);
		$('#powerValue').html(response.activePower.toFixed(1) + ' ' + powerUnitLabel);
		$('#lastValuesPlaceholder').hide();
		$('#lastValues').removeClass('d-none');
	} else {
		errorMessage($('#data_form'));
		console.log(response.message);
	}
};

function updateAccumulated() {
	var sum = 0;
	(lastEnergyLogs || []).forEach(function(item) {
		sum += item.energy;
	});
	$('#accumulatedValue').html(sum.toFixed(2) + ' ' + energyUnitLabel);
}

function renderChart() {
	var source, valueFn, unit;
	switch (selectedMetric) {
	case 'volts':
		source = lastPowerLogs;
		valueFn = function(item) { return item.volts; };
		unit = voltsUnitLabel;
		break;
	case 'amps':
		source = lastPowerLogs;
		valueFn = function(item) { return item.amps; };
		unit = ampsUnitLabel;
		break;
	case 'energy':
		source = lastEnergyLogs;
		valueFn = function(item) { return item.energy; };
		unit = energyUnitLabel;
		break;
	case 'frequency':
		source = lastPowerLogs;
		valueFn = function(item) { return item.frequency; };
		unit = frequencyUnitLabel;
		break;
	case 'cosPhi':
		source = lastPowerLogs;
		valueFn = function(item) { return item.cosPhi; };
		unit = '';
		break;
	default:
		source = lastPowerLogs;
		valueFn = function(item) { return item.activePower; };
		unit = powerUnitLabel;
		break;
	}
	if (!source) {
		return;
	}
	var culture = locale.split('-')[0];
	var points = source.map(function(item) {
		return {
			x: new Date(item.created),
			y: valueFn(item)
		};
	});
	var chart = new CanvasJS.Chart('metricChart', {
		culture: culture,
		axisX: {
			gridColor: '#D8D2CC',
			gridThickness: 1,
			gridDashType: 'dash',
			tickColor: '#D8D2CC',
			lineColor: '#D8D2CC',
			labelFontColor: '#6c757d'
		},
		axisY: {
			gridColor: '#D8D2CC',
			gridThickness: 1,
			gridDashType: 'dash',
			lineThickness: 0,
			tickLength: 0,
			labelFontColor: '#6c757d'
		},
		toolTip: {
			contentFormatter: function(e) {
				return parseTooltip(e, unit);
			},
			cornerRadius: 5
		},
		data: [
			{
				type: 'spline',
				color: '#BA4C1B',
				lineThickness: 3,
				markerSize: 0,
				dataPoints: points
			}
		]
	});
	chart.render();
}

function parseTooltip(event, label) {
	var entry = event.entries[0];
	var dataPoint = entry.dataPoint;
	var title = parseChartDate(dataPoint.x);
	var value = dataPoint.y.toFixed(2);
	return `${title}<br>${value} ${label}`;
}

function parseChartDate(value) {
	var config = {};
	var viewType = $('#viewType').val();
	switch (viewType) {
	case 'byHour':
		config.hour = '2-digit';
		config.minute = '2-digit';
		break;
	case 'byDay':
		config.day = '2-digit';
		config.month = 'short';
		break;
	case 'byMonth':
		config.month = 'short';
		config.year = 'numeric';
		break;
	case 'byYear':
		config.year = 'numeric';
		break;
	}

	return new Intl.DateTimeFormat(locale, config).format(value);
}

function clearInstantTimer() {
	if (instantTimer !== undefined) {
		clearTimeout(instantTimer);
	}
}

function getEnergyValues() {
	$('#data_form').attr('action', energyFormAction).submit();
}

function getDataValues() {
	$('#data_form').attr('action', dataFormAction).submit();
}

function getInstantValues() {
	clearInstantTimer();
	instantTimer = setTimeout(function() {
		getInstantValues();
	}, 300000);
	$('#instant_form').submit();
}

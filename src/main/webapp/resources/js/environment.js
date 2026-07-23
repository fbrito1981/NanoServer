var instantTimer;

$(document).ready(function() {
	$('#device').change(onElementChanged);
	$('#viewType').change(onElementChanged);
	$('#from').change(onElementChanged);
	$('#until').change(onElementChanged);
	setDateValidation('from', 'until', 'data_form');
	setupDatetimepicker('from', dateFormat, locale, fromDate, false);
	setupDatetimepicker('until', dateFormat, locale, toDate, false);
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

function onElementChanged() {
	var value = $('#device').val();
	if (value.length > 0) {
		$('#instantDevice').val(value);
		if (instantTimer !== undefined) {
			clearTimeout(instantTimer);
		}
		var viewType = $('#viewType').val();
		if (['byMinute', 'byHour'].includes(viewType)) {
			$('#until').val(null).prop('disabled', true).css('cursor', 'not-allowed');
			$('#until_value').val(null);
		} else {
			$('#until').prop('disabled', false).css('cursor', 'default');
			setupDatetimepicker('until', dateFormat, locale, toDate, false);
		}
		showLoading();
		getDataValues();
	}
}

var processData = function(response) {
	hideLoading();
	if (response && response.logs) {
		getInstantValues();
		var culture = locale.split('-')[0];
		var temps = [];
		var hums = [];
		response.logs.forEach(function(item) {
			temps.push({
				x: new Date(item.created),
				y: item.temp
			});
			hums.push({
				x: new Date(item.created),
				y: item.hum
			});
		});
		$('.no-device-selected').hide();
		var tempChart = new CanvasJS.Chart('tempChart', {
			culture: culture,
			axisY: {
				title: tempUnitLabel
			},
			legend: {
				horizontalAlign: 'right',
				verticalAlign: 'center',
			},
			toolTip: {
				contentFormatter: function(e) {
					return parseTootlip(e, tempUnitLabel);
				},
				cornerRadius: 5
			},
			data: [
				{
					type: 'spline',
					name: tempLabel,
					dataPoints: temps,
					showInLegend: true
				}
			]
		});
		tempChart.render();
		var humChart = new CanvasJS.Chart('humChart', {
			culture: culture,
			axisY: {
				title: humUnitLabel
			},
			legend: {
				horizontalAlign: 'right',
				verticalAlign: 'center',
			},
			toolTip: {
				contentFormatter: function(e) {
					return parseTootlip(e, humUnitLabel);
				},
				cornerRadius: 5
			},
			data: [
				{
					type: 'spline',
					name: humLabel,
					dataPoints: hums,
					showInLegend: true
				}
			]
		});
		humChart.render();
	} else if (response && response.temp !== undefined) {
		$('#tempValue').html(response.temp.toFixed(1) + ' ' + tempUnitLabel);
		$('#humValue').html(response.hum.toFixed(1) + ' ' + humUnitLabel);
		instantTimer = setTimeout(function() {
			getInstantValues();
		}, 300000);
	} else {
		errorMessage($('#data_form'));
		console.log(response.message);
	}
};

function parseTootlip(event, label) {
	var entry = event.entries[0];
	var dataPoint = entry.dataPoint;
	var title = parseChartDate(dataPoint.x);
	var value = dataPoint.y.toFixed(2);
	var unit = Array.isArray(label) ? label[entry.dataSeries.index] : label;
	return `${title}<br>${value} ${unit}`;
}

function parseChartDate(value) {
	var config = {};
	var viewType = $('#viewType').val();
	switch (viewType) {
	case 'byMinute':
		config.minute = '2-digit';
	case 'byHour':
		config.hour = '2-digit';
	case 'byDay':
		config.day = '2-digit';
	case 'byMonth':
		config.month = 'short';
	case 'byYear':
		config.year = 'numeric';
	}

	var result = new Intl.DateTimeFormat(locale, config).format(value);

	return result;
}

function setDateValidation(startId, endId, submitId) {
	$('#' + startId).on('change', function() {
		validateDates(startId, endId, submitId);
	}).removeClass('is-invalid');
	$('#' + endId).on('change', function() {
		validateDates(startId, endId, submitId);
	}).removeClass('is-invalid');
	$('#' + submitId).prop('disabled', false);
}

function validateDates(startId, endId, submitId) {
	var startValue = $('#' + startId + '_value').val();
	var endValue = $('#' + endId + '_value').val();
	var startDate = new Date(parseInt(startValue));
	var endDate = new Date(parseInt(endValue));

	if (startDate != null && startDate.toString() != 'Invalid Date'
		&& endDate != null && endDate.toString() != 'Invalid Date') {
		if (endDate <= startDate) {
			$('#' + startId).addClass('is-invalid');
			$('#' + endId).addClass('is-invalid');
			$('#' + submitId).prop('disabled', true);
		} else {
			$('#' + startId).removeClass('is-invalid');
			$('#' + endId).removeClass('is-invalid');
			$('#' + submitId).prop('disabled', false);
		}
	}
}

function getDataValues() {
	$('#data_form').attr('action', dataFormAction).submit();
}

function getInstantValues() {
	$('#instant_form').submit();
}

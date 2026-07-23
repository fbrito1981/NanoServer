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
		getEnergyValues();
	}
}

var processData = function(response) {
	hideLoading();
	if (response && response.energyLogs) {
		getDataValues();
		var culture = locale.split('-')[0];
		var energy = [];
		response.energyLogs.forEach(function(item) {
			energy.push({
				x: new Date(item.created),
				y: item.energy
			});
		});
		$('.no-device-selected').hide();
		var energyChart = new CanvasJS.Chart('energyChart', {
			culture: culture,
			axisY: {
				title: energyUnitLabel
			},
			legend: {
				horizontalAlign: 'right', 
				verticalAlign: 'center',
			},
			toolTip: {
				contentFormatter: function(e) {
					return parseTootlip(e, energyUnitLabel);
				},
				cornerRadius: 5
			},
			data: [
				{
					type: 'column',
					name: energyLabel,
					dataPoints: energy,
					showInLegend: true
				}
			]
		});
		energyChart.render();
	} else if (response && response.logs) {
		getInstantValues();
		var culture = locale.split('-')[0];
		var activePower = [];
		var reactivePower = [];
		var apparentPower = [];
		var volts = [];
		var amps = [];
		var cosPhis = [];
		var frequencies = [];
		response.logs.forEach(function(item) {
			activePower.push({
				x: new Date(item.created),
				y: item.activePower
			});
			reactivePower.push({
				x: new Date(item.created),
				y: item.reactivePower
			});
			apparentPower.push({
				x: new Date(item.created),
				y: item.apparentPower
			});
			volts.push({
				x: new Date(item.created),
				y: item.volts
			});
			amps.push({
				x: new Date(item.created),
				y: item.amps
			});
			cosPhis.push({
				x: new Date(item.created),
				y: item.cosPhi
			});
			frequencies.push({
				x: new Date(item.created),
				y: item.frequency
			});
		});
		$('.no-device-selected').hide();
		var powerChart = new CanvasJS.Chart('powerChart', {
			culture: culture,
			axisY: {
				title: powerUnitLabel
			},
			legend: {
				horizontalAlign: 'right', 
				verticalAlign: 'center',
			},
			toolTip: {
				contentFormatter: function(e) {
					return parseTootlip(e, powerUnitLabel);
				},
				cornerRadius: 5
			},
			data: [
				{
					type: 'spline',
					name: activePowerLabel,
					dataPoints: activePower,
					showInLegend: true
				},
				{
					type: 'spline',
					name: reactivePowerLabel,
					dataPoints: reactivePower,
					showInLegend: true
				},
				{
					type: 'spline',
					name: apparentPowerLabel,
					dataPoints: apparentPower,
					showInLegend: true
				}
			]
		});
		powerChart.render();
		var levelsChart = new CanvasJS.Chart('levelsChart', {
			culture: culture,
			axisY: {
				title: voltsLabel
			},
			axisY2: {
				title: ampsLabel
			},
			legend: {
				horizontalAlign: 'right', 
				verticalAlign: 'center',
			},
			toolTip: {
				contentFormatter: function(e) {
					return parseTootlip(e, [voltsUnitLabel, ampsUnitLabel]);
				},
				cornerRadius: 5
			},
			data: [
				{
					type: 'spline',
					name: voltsLabel,
					dataPoints: volts,
					showInLegend: true
				},
				{
					type: 'spline',
					name: ampsLabel,
					axisYType: 'secondary',
					dataPoints: amps,
					showInLegend: true
				}
			]
		});
		levelsChart.render();
		var cosPhiChart = new CanvasJS.Chart('cosPhiChart', {
			culture: culture,
			axisY: {
				title: angleLabel
			},
			axisY2: {
				title: frequencyLabel
			},
			legend: {
				horizontalAlign: 'right', 
				verticalAlign: 'center',
			},
			toolTip: {
				contentFormatter: function(e) {
					return parseTootlip(e, [cosPhiUnitLabel, frequencyUnitLabel]);
				},
				cornerRadius: 5
			},
			data: [
				{
					type: 'spline',
					name: cosPhiLabel,
					dataPoints: cosPhis,
					showInLegend: true
				},
				{
					type: 'spline',
					name: frequencyLabel,
					axisYType: 'secondary',
					dataPoints: frequencies,
					showInLegend: true
				}
			]
		});
		cosPhiChart.render();
		var voltsChart = new CanvasJS.Chart('voltsChart', {
			culture: culture,
			legend: {
				horizontalAlign: 'right', 
				verticalAlign: 'center',
			},
			data: [
				{
					type: 'doughnut',
					dataPoints: [
						{ y: 100, color: 'transparent', toolTipContent: null },
						{ y: 23, color: '#EF3D20', toolTipContent: null },
						{ y: 2, color: 'transparent', toolTipContent: null },
						{ y: 50, color: '#86CC27', toolTipContent: null },
						{ y: 2, color: 'transparent', toolTipContent: null },
						{ y: 23, color: '#EF3D20', toolTipContent: null }
					]
				}
			]
		});
		voltsChart.render();
		var wattsChart = new CanvasJS.Chart('wattsChart', {
			culture: culture,
			legend: {
				horizontalAlign: 'right', 
				verticalAlign: 'center',
			},
			data: [
				{
					type: 'doughnut',
					dataPoints: [
						{ y: 100, color: 'transparent', toolTipContent: null },
						{ y: 30, color: '#1CBD26', toolTipContent: null },
						{ y: 2, color: 'transparent', toolTipContent: null },
						{ y: 23, color: '#86CC27', toolTipContent: null },
						{ y: 2, color: 'transparent', toolTipContent: null },
						{ y: 18, color: '#FECE35', toolTipContent: null },
						{ y: 2, color: 'transparent', toolTipContent: null },
						{ y: 13, color: '#FB7923', toolTipContent: null },
						{ y: 2, color: 'transparent', toolTipContent: null },
						{ y: 8, color: '#EF3D20', toolTipContent: null }
					]
				}
			]
		});
		wattsChart.render();
		$('.needle').show();
		$('.value').show();
	} else if (response && response.volts) {
		setVoltsAndFrequency(response.volts, response.frequency);
		setWattsAndCosPhi(response.apparentPower, response.cosPhi);
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
	var dataPoint = entry.dataPoint
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

function getEnergyValues() {
	$('#data_form').attr('action', energyFormAction).submit();
}

function getDataValues() {
	$('#data_form').attr('action', dataFormAction).submit();
}

function getInstantValues() {
	$('#instant_form').submit();
}

function setVoltsAndFrequency(volts, frequency) {
	var bottomLimit = 193;
	var topLimit = 267;
	var minVolts = 230 * .9;
	var maxVolts = 230 * 1.1;
	var minAngle = 36;
	var maxAngle = 144;
	var startVolts;
	if (volts < bottomLimit) {
		startVolts = bottomLimit - minVolts;
	} else if (volts > topLimit) {
		startVolts = topLimit - minVolts;
	} else {
		startVolts = volts - minVolts;
	}
	var propVolts = startVolts / (maxVolts - minVolts);
	var propAngle = propVolts * (maxAngle - minAngle);
	var angle = propAngle + minAngle;
	$('#voltsNeedle').css('transform', 'rotate(' + angle + 'deg)');
	$('#voltsValue').html(volts.toFixed(1) + ' ' + voltsUnitLabel);
	$('#frequencyValue').html(frequency.toFixed(1) + ' ' + frequencyUnitLabel);
}

function setWattsAndCosPhi(watts, cosPhi) {
	var minWatts = 0;
	var maxWatts = 3000;
	var minAngle = 0;
	var maxAngle = 180;
	var startWatts = watts;
	if (watts > maxWatts) {
		startWatts = maxWatts;
	}
	var propWatts = startWatts / (maxWatts - minWatts);
	var propAngle = propWatts * (maxAngle - minAngle);
	var angle = propAngle + minAngle;
	$('#wattsNeedle').css('transform', 'rotate(' + angle + 'deg)');
	$('#wattsValue').html(watts.toFixed(1) + ' ' + powerUnitLabel);
	$('#cosPhiValue').html(cosPhi.toFixed(2) + ' ' + cosPhiUnitLabel);
}


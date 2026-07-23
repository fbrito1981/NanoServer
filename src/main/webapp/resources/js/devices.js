var table;

$(document).ready(function() {
	$.fn.dataTable.ext.errMode = 'throw';
	
	table = $('#dataTable').DataTable( {
        ajax: {
            url: appCtx + '/devices/list',
            type: 'POST'
        },
        serverSide: true,
        language: {
        	url: dataTableLanguageUrl
        },
        columns: [
            { data: 'active', searchable: false, render: function(data, type, row) { return row.activeLabel; } },
            { data: 'uuid' },
            { data: 'name' },
            { data: 'model' },
            { data: 'os' },
            { data: 'version' },
            { data: 'created', searchable: false, render: function(data) { return parseDate(data, dataTableLocale); } },
            { data: 'updated', searchable: false, render: function(data) { return parseDate(data, dataTableLocale); } },
            { data: null, searchable: false, orderable: false }
        ],
        columnDefs: [
        	{
        		targets: [-1],
        		data: null,
        		className: 'text-right',
        		render: function(data, type, row, meta) {
        			return '<div class="d-flex justify-content-end">' +
							'<div class="list-group list-group-horizontal w-max-content">' +
			    				'<button type="button" class="list-group-item list-group-item-info list-group-item-action btn-sm-important" onclick="edit(' + meta.row + ');" data-toggle="tooltip" data-placement="top" title="' + dataTableTooltipEdit + '"><i class="far fa-edit"></i></button>' +
			    				'<button type="button" class="list-group-item list-group-item-info list-group-item-action btn-sm-important" onclick="remove(' + meta.row + ');" data-toggle="tooltip" data-placement="top" title="' + dataTableTooltipDelete + '"><i class="far fa-trash-alt"></i></button>' + 
		        			'</div>' +
	        			'</div>';
        		}
        	}
        ],
        order: [ 1, 'asc' ]
    });
    
    table.on('preDraw', function() {
    	showLoading();
    }).on('draw', function() {
    	$('[data-toggle="tooltip"]').tooltip();
    	hideLoading();
        if (generalInfoMessage !== undefined && generalInfoMessage != null && generalInfoMessage.length > 0) {
        	infoMessage($('#move_form'), generalInfoMessage);
        	generalInfoMessage = null;
        }
    }).on('error.dt', function(e, settings, techNote, message) {
    	hideLoading();
    	console.log('An error has been reported by DataTables: ', message);
    	errorMessage($('#move_form'), generalErrorMessage);
    });
});

function create() {
	$('#create_form')[0].reset();
	$('#create_form').find('button').prop('disabled', false);
	$('#create_spinner').addClass('d-none');
	$('#create_form').submit(function(e) {
		$('#create_form').find('button').prop('disabled', true);
		$('#create_spinner').removeClass('d-none');
	});
	
	$('#create_modal').modal('show');
}

function edit(index) {
	var data = table.row(index).data();
	$('#update_active').prop('checked', data.active);
	$('#update_uuid').val(data.uuid);
	$('#update_name').val(data.name);
	$('#update_model').val(data.model);
	$('#update_os').val(data.os);
	$('#update_version').val(data.version);
	$('#update_form').find('button').prop('disabled', false);
	$('#update_spinner').addClass('d-none');
	$('#update_form').submit(function(e) {
		$('#update_form').find('button').prop('disabled', true);
		$('#update_spinner').removeClass('d-none');
	});
	
	$('#update_modal').modal('show');
}

function remove(index) {
	var data = table.row(index).data();
	$('#remove_uuid').val(data.uuid);
	$('#remove_name').val(data.name);
	$('#remove_model').val(data.model);
	$('#remove_os').val(data.os);
	$('#remove_version').val(data.version);
	$('#remove_form').find('button').prop('disabled', false);
	$('#remove_spinner').addClass('d-none');
	$('#remove_form').submit(function(e) {
		$('#remove_form').find('button').prop('disabled', true);
		$('#remove_spinner').removeClass('d-none');
	});
	
	$('#confirm_modal').modal('show');
}

function getRandomId(elementId) {
	showLoading();
	$.ajax({
		url: appCtx + '/devices/randomUuid',
		cache: false,
		contentType: false,
		processData: false,
		method: 'GET',
		success: function(data, textStatus, response) {
			hideLoading();
			if (data.success) {
				$('#' + elementId).val(data.data);
			} else {
				errorMessage($('#move_form'), generalErrorMessage);
			}
		},
		error: function(response, textStatus, errorThrown) {
			hideLoading();
			console.log('Error [' + errorThrown + ']: ' + response);
			errorMessage($('#move_form'), generalErrorMessage);
		}
	});
}

function validateName(prefix) {
	showLoading();
	$.ajax({
		url: appCtx + '/devices/validateName?name=' + $('#' + prefix + '_name').val(),
		cache: false,
		contentType: false,
		processData: false,
		method: 'GET',
		success: function(data, textStatus, response) {
			hideLoading();
			if (data.success) {
				valid = true;
				if (data.data != null && data.data != '') {
					valid = (data.data.uuid == $('#' + prefix + '_uuid').val());
				}
				$('#' + prefix + '_name').removeClass(valid ? 'is-invalid' : 'is-valid');
				$('#' + prefix + '_name').addClass(valid ? 'is-valid' : 'is-invalid');
				$('#' + prefix + '_submit').prop('disabled', !valid);
			} else {
				$('#' + prefix + '_name').removeClass('is-valid');
				$('#' + prefix + '_name').addClass('is-invalid');
				$('#' + prefix + '_submit').prop('disabled', true);
			}
		},
		error: function(response, textStatus, errorThrown) {
			hideLoading();
			console.log('Error [' + errorThrown + ']: ' + response);
			$('#' + prefix + '_name').removeClass('is-valid');
			$('#' + prefix + '_name').addClass('is-invalid');
			$('#' + prefix + '_submit').prop('disabled', true);
		}
	});
}

var processData = function(data) {
	$('#create_modal').scrollTop(0);
	$('#update_modal').scrollTop(0);
	$('#confirm_modal').scrollTop(0);
	setTimeout(function() {
		table.draw();
		$('#create_modal').modal('hide');
		$('#update_modal').modal('hide');
		$('#confirm_modal').modal('hide');
	}, 3000);
};

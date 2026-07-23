var table;
var usedBeacons;

$(document).ready(function() {
	$.fn.dataTable.ext.errMode = 'throw';
	
	table = $('#dataTable').DataTable( {
        ajax: {
            url: appCtx + '/users/list',
            type: 'POST'
        },
        serverSide: true,
        language: {
        	url: dataTableLanguageUrl
        },
        columns: [
            { data: 'active', searchable: false, render: function(data, type, row) { return row.activeLabel; } },
            { data: 'name' },
            { data: 'email' },
            { data: 'roleName' },
            { data: 'created', searchable: false, render: function(data) { return parseDate(data, dataTableLocale); } },
            { data: 'updated', searchable: false, render: function(data) { return parseDate(data, dataTableLocale); } },
            { data: null, searchable: false, orderable: false }
        ],
        columnDefs: [
        	{
        		targets: [-1],
        		data: 'name',
        		className: 'text-right',
        		render: function(data, type, row, meta) {
        			return '<div class="d-flex justify-content-end">' +
							'<div class="list-group list-group-horizontal w-max-content">' +
			    				'<button type="button" class="list-group-item list-group-item-info list-group-item-action btn-sm-important" onclick="edit(' + meta.row + ');" data-toggle="tooltip" data-placement="top" title="' + dataTableTooltipEdit + '"><i class="far fa-edit"></i></button>' +
			    				'<button type="button" class="list-group-item list-group-item-info list-group-item-action btn-sm-important" onclick="remove(' + meta.row + ');" data-toggle="tooltip" data-placement="top" title="' + dataTableTooltipDelete + '"><i class="far fa-trash-alt"></i></button>' +
			    				'<button type="button" class="list-group-item list-group-item-info list-group-item-action btn-sm-important" onclick="sendEMail(' + meta.row + ');" data-toggle="tooltip" data-placement="top" title="' + dataTableTooltipSendMail + '"><i class="far fa-envelope"></i></button>' + 
		        			'</div>' +
	        			'</div>';
        		}
        	}
        ],
        order: [ 2, 'asc' ]
    });
    
    table.on('preDraw', function() {
    	showLoading();
    	usedBeacons = new Array();
    	table.rows().every(function () {
    		var data = this.data();
    		if (data.beaconFeedId != null) {
    			usedBeacons.push({
    				email: data.email,
    				beaconFeedId: data.beaconFeedId
    			});
    		}
    	});
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
    
    setImageLegend('create_picture');
    setImageLegend('update_picture');
});

function create() {
	clearImage('create_picture');
	$('#create_form')[0].reset();
	$('#create_form').find('button').prop('disabled', false);
	$('#create_spinner').addClass('d-none');
	$('#create_form').submit(function(e) {
		$('#create_form').find('button').prop('disabled', true);
		$('#create_spinner').removeClass('d-none');
	});
	
	$('#create_modal').modal('show');

	$('#create_picture_remove').prop('disabled', true);
}

function edit(index) {
	var data = table.row(index).data();
	clearImage('update_picture');
	$('#update_active').prop('checked', data.active);
	$('#update_email').val(data.email);
	$('#update_name').val(data.name);
	$('#update_role option').filter(function(i, e) { return $(e).val() == data.roleName; }).prop('selected', true);
	$('#update_form').find('button').prop('disabled', false);
	$('#update_spinner').addClass('d-none');
	$('#update_form').submit(function(e) {
		$('#update_form').find('button').prop('disabled', true);
		$('#update_spinner').removeClass('d-none');
	});
	
	$('#update_modal').modal('show');

	if (data.picture != null) {
		loadUrlImage(data.picture, 'update_picture');
		$('#update_picture_remove').prop('disabled', false);
	} else {
		$('#update_picture_remove').prop('disabled', true);
	}
}

function remove(index) {
	var data = table.row(index).data();
	$('#remove_email').val(data.email);
	$('#remove_name').val(data.name);
	$('#remove_form').find('button').prop('disabled', false);
	$('#remove_spinner').addClass('d-none');
	$('#remove_form').submit(function(e) {
		$('#remove_form').find('button').prop('disabled', true);
		$('#remove_spinner').removeClass('d-none');
	});
	
	$('#confirm_modal').modal('show');
}

function setPicture(elementId) {
	$('#' + elementId).change(function() {
		loadImage(this, elementId);
		$('#' + elementId + '_edit').tooltip('hide');
	});
	$('#' + elementId).click();
	$('#' + elementId + '_removed').prop('checked', false);
	$('#' + elementId + '_remove').prop('disabled', false);
}

function removePicture(elementId) {
	clearImage(elementId);
	$('#' + elementId + '_removed').prop('checked', true);
	$('#' + elementId + '_remove').prop('disabled', true).tooltip('hide');
}

function sendEMail(index) {
	var data = table.row(index).data();
	$('#send_email_email').val(data.email);
	$('#send_email_name').val(data.name);
	$('#send_email_form').find('button').prop('disabled', false);
	$('#send_email_spinner').addClass('d-none');
	$('#send_email_form').submit(function(e) {
		$('#send_email_form').find('button').prop('disabled', true);
		$('#send_email_spinner').removeClass('d-none');
	});
	
	$('#send_email_modal').modal('show');
}

var processData = function(data) {
	$('#create_modal').scrollTop(0);
	$('#update_modal').scrollTop(0);
	$('#confirm_modal').scrollTop(0);
	$('#send_email_modal').scrollTop(0);
	setTimeout(function() {
		table.draw();
		$('#create_modal').modal('hide');
		$('#update_modal').modal('hide');
		$('#confirm_modal').modal('hide');
		$('#send_email_modal').modal('hide');
	}, 3000);
};

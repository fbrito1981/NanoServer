var table;
var minRoleName = '';
var maxRoleName = '';

$(document).ready(function() {
	$.fn.dataTable.ext.errMode = 'throw';
	
	table = $('#dataTable').DataTable( {
        ajax: {
            url: appCtx + '/roles/list',
            type: 'POST'
        },
        serverSide: true,
        language: {
        	url: dataTableLanguageUrl
        },
        columns: [
            { data: 'name' },
            { data: 'level', searchable: false },
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
			    				'<button type="button" class="list-group-item list-group-item-info list-group-item-action btn-sm-important" onclick="moveUp(' + meta.row + ');" data-toggle="tooltip" data-placement="top" title="' + dataTableTooltipMoveUp + '" id="move_up_' + data.name + '"><i class="fas fa-arrow-up"></i></button>' +
			    				'<button type="button" class="list-group-item list-group-item-info list-group-item-action btn-sm-important" onclick="moveDown(' + meta.row + ');" data-toggle="tooltip" data-placement="top" title="' + dataTableTooltipMoveDown + '" id="move_down_' + data.name + '"><i class="fas fa-arrow-down"></i></button>' +
		        			'</div>' +
	        			'</div>';
        		}
        	}
        ],
        order: [ 1, 'asc' ]
    });
    
    table.on('preDraw', function() {
    	showLoading();
    	var minLevel = 9999;
    	var maxLevel = 0;
    	table.rows().every(function () {
    		var data = this.data();
    		if (data.level < minLevel) {
    			minLevel = data.level;
    			minRoleName = data.name;
    		}
    		if (data.level > maxLevel) {
    			maxLevel = data.level;
    			maxRoleName = data.name;
    		}
    	});
    }).on('draw', function() {
    	$('[data-toggle="tooltip"]').tooltip();
    	$('#move_up_' + minRoleName).prop('disabled', true);
    	$('#move_down_' + maxRoleName).prop('disabled', true);
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
	$('#update_level').val(data.level);
	$('#update_name').val(data.name);
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
	$('#remove_name').val(data.name);
	$('#remove_form').find('button').prop('disabled', false);
	$('#remove_spinner').addClass('d-none');
	$('#remove_form').submit(function(e) {
		$('#remove_form').find('button').prop('disabled', true);
		$('#remove_spinner').removeClass('d-none');
	});
	
	$('#confirm_modal').modal('show');
}

function moveUp(index) {
	var data = table.row(index).data();
	$('#move_up').val(true);
	$('#move_id').val(data.name);
	
	$('#move_form').submit();
}

function moveDown(index) {
	var data = table.row(index).data();
	$('#move_up').val(false);
	$('#move_id').val(data.name);
	
	$('#move_form').submit();
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

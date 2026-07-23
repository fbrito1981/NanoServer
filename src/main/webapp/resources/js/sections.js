var table;
var minSectionName = '';
var maxSectionName = '';

$(document).ready(function() {
	$.fn.dataTable.ext.errMode = 'throw';
	
	table = $('#dataTable').DataTable( {
        ajax: {
            url: appCtx + '/sections/list',
            type: 'POST'
        },
        serverSide: true,
        language: {
        	url: dataTableLanguageUrl
        },
        columns: [
            { data: 'name' },
            { data: 'label', searchable: false, orderable: false },
            { data: 'minRoleName' },
            { data: 'menuOrder', searchable: false },
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
			    				'<button type="button" class="list-group-item list-group-item-info list-group-item-action btn-sm-important" onclick="moveUp(' + meta.row + ');" data-toggle="tooltip" data-placement="top" title="' + dataTableTooltipMoveUp + '" id="move_up_' + data.name + '"><i class="fas fa-arrow-up"></i></button>' +
			    				'<button type="button" class="list-group-item list-group-item-info list-group-item-action btn-sm-important" onclick="moveDown(' + meta.row + ');" data-toggle="tooltip" data-placement="top" title="' + dataTableTooltipMoveDown + '" id="move_down_' + data.name + '"><i class="fas fa-arrow-down"></i></button>' +
		        			'</div>' +
	        			'</div>';
        		}
        	}
        ],
        order: [ 3, 'asc' ]
    });
    
    table.on('preDraw', function() {
    	showLoading();
    	var minMenuOrder = 9999;
    	var maxMenuOrder = 0;
    	table.rows().every(function () {
    		var data = this.data();
    		if (data.menuOrder < minMenuOrder) {
    			minMenuOrder = data.menuOrder;
    			minSectionName = data.name;
    		}
    		if (data.menuOrder > maxMenuOrder) {
    			maxMenuOrder = data.menuOrder;
    			maxSectionName = data.name;
    		}
    	});
    }).on('draw', function() {
    	$('[data-toggle="tooltip"]').tooltip();
    	$('#move_up_' + minSectionName).prop('disabled', true);
    	$('#move_down_' + maxSectionName).prop('disabled', true);
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

function edit(index) {
	var data = table.row(index).data();
	$('#update_menu_order').val(data.menuOrder);
	$('#update_name').val(data.name);
	$('#update_label').val(data.label);
	$('#update_min_role option').filter(function(i, e) { return $(e).val() == data.minRoleName; }).prop('selected', true);
	$('#update_form').find('button').prop('disabled', false);
	$('#update_spinner').addClass('d-none');
	$('#update_form').submit(function(e) {
		$('#update_form').find('button').prop('disabled', true);
		$('#update_spinner').removeClass('d-none');
	});
	
	$('#update_modal').modal('show');
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
	$('#update_modal').scrollTop(0);
	setTimeout(function() {
		table.draw();
		$('#update_modal').modal('hide');
	}, 3000);
};

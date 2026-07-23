<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<link rel="stylesheet" href="https://cdn.datatables.net/1.10.21/css/dataTables.bootstrap4.min.css">

<script type="text/javascript" src="https://cdn.datatables.net/1.10.21/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="https://cdn.datatables.net/1.10.21/js/dataTables.bootstrap4.min.js"></script>

<script type="text/javascript">
var dataTableLanguageUrl = '${data_table_language}';
var dataTableLocale = '${data_table_locale}';
var dateTableDateFormat = '${data_table_date_format}';
var dataTableUnallocated = '${data_table_unallocated}';
var dataTableMaximumSize = '${data_table_maximum_size}';
var dataTableTooltipEdit = '${data_table_tp_edit}';
var dataTableTooltipDelete = '${data_table_tp_delete}';
var dataTableTooltipRemove = '${data_table_tp_remove}';
var dataTableTooltipMoveUp = '${data_table_tp_move_up}';
var dataTableTooltipMoveDown = '${data_table_tp_move_down}';
var dataTableTooltipSendMail = '${data_table_tp_send_mail}';
var dataTableTooltipFeatures = '${data_table_tp_features}';
var dataTableTooltipViewDescription = '${data_table_tp_view_description}';
var dataTableMsgImageFormat = '${data_table_msg_image_format}';
var generalErrorMessage = '${error_msg_general}';
var generalInfoMessage = '${info_msg_general}';
</script>

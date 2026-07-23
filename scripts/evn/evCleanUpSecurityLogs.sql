drop event if exists evCleanUpSecurityLogs;

delimiter ;;

create event evCleanUpSecurityLogs
on schedule every 1 day_hour
comment 'Removes security logs older than a month'
do begin
delete from security_log
where created < timestampadd(month, -1, current_timestamp());
end;;

delimiter ;

set global event_scheduler = 1;

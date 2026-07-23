drop procedure if exists spGetEnvironmentLog;

delimiter ;;

create procedure spGetEnvironmentLog(mDeviceUuid varchar(255))
begin
select deviceUuid,
	created,
	temp,
    hum
from environment_log
where deviceUuid = mDeviceUuid
order by created desc
limit 1;
end;;

delimiter ;

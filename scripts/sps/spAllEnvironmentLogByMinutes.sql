drop procedure if exists spAllEnvironmentLogByMinutes;

delimiter ;;

create procedure spAllEnvironmentLogByMinutes(mDeviceUuid varchar(255), mFrom int, mUntil int)
begin
select *
from environment_log
where deviceUuid = mDeviceUuid
and created between from_unixtime(mFrom) and from_unixtime(mUntil)
order by created desc;
end;;

delimiter ;

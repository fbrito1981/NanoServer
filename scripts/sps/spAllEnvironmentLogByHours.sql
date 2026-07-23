drop procedure if exists spAllEnvironmentLogByHours;

delimiter ;;

create procedure spAllEnvironmentLogByHours(mDeviceUuid varchar(255), mFrom int, mUntil int)
begin
select a.deviceUuid,
	a.hours as created,
	avg(a.temp) as temp,
    avg(a.hum) as hum
from (
	select *, (created - interval minute(created) minute - interval second(created) second) as hours
	from environment_log
	where deviceUuid = mDeviceUuid
	and created between from_unixtime(mFrom) and from_unixtime(mUntil)
) a
group by a.deviceUuid, a.hours
order by a.hours desc;
end;;

delimiter ;

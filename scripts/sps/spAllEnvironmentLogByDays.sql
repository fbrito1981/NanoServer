drop procedure if exists spAllEnvironmentLogByDays;

delimiter ;;

create procedure spAllEnvironmentLogByDays(mDeviceUuid varchar(255), mFrom int, mUntil int)
begin
select a.deviceUuid,
	a.days as created,
	avg(a.temp) as temp,
    avg(a.hum) as hum
from (
	select *, date(created) as days
	from environment_log
	where deviceUuid = mDeviceUuid
	and created between from_unixtime(mFrom) and from_unixtime(mUntil)
) a
group by a.deviceUuid, a.days
order by a.days desc;
end;;

delimiter ;

drop procedure if exists spAllEnvironmentLogByYears;

delimiter ;;

create procedure spAllEnvironmentLogByYears(mDeviceUuid varchar(255), mFrom int, mUntil int)
begin
select a.deviceUuid,
	a.years as created,
	avg(a.temp) as temp,
    avg(a.hum) as hum
from (
	select *, (date(created) - interval (month(created) - 1) month - interval (day(created) - 1) day) as years
	from environment_log
	where deviceUuid = mDeviceUuid
	and created between from_unixtime(mFrom) and from_unixtime(mUntil)
) a
group by a.deviceUuid, a.years
order by a.years desc;
end;;

delimiter ;

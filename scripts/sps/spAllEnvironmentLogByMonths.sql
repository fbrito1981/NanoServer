drop procedure if exists spAllEnvironmentLogByMonths;

delimiter ;;

create procedure spAllEnvironmentLogByMonths(mDeviceUuid varchar(255), mFrom int, mUntil int)
begin
select a.deviceUuid,
	a.months as created,
	avg(a.temp) as temp,
    avg(a.hum) as hum
from (
	select *, (date(created) - interval (day(created) - 1) day) as months
	from environment_log
	where deviceUuid = mDeviceUuid
	and created between from_unixtime(mFrom) and from_unixtime(mUntil)
) a
group by a.deviceUuid, a.months
order by a.months desc;
end;;

delimiter ;

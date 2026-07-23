drop procedure if exists spAllEnergyLogByMonths;

delimiter ;;

create procedure spAllEnergyLogByMonths(mDeviceUuid varchar(255), mFrom int, mUntil int)
begin
select a.deviceUuid,
	a.months as created,
	avg(a.volts) as volts,
    avg(a.amps) as amps,
    avg(a.frequency) as frequency,
    avg(a.difference) as difference
from (
	select *, (date(created) - interval (day(created) - 1) day) as months
	from energy_log
	where deviceUuid = mDeviceUuid
	and created between from_unixtime(mFrom) and from_unixtime(mUntil)
) a
group by a.deviceUuid, a.months
order by a.months desc;
end;;

delimiter ;


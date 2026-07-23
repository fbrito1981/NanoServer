drop procedure if exists spAllEnergyLogByYears;

delimiter ;;

create procedure spAllEnergyLogByYears(mDeviceUuid varchar(255), mFrom int, mUntil int)
begin
select a.deviceUuid,
	a.years as created,
	avg(a.volts) as volts,
    avg(a.amps) as amps,
    avg(a.frequency) as frequency,
    avg(a.difference) as difference
from (
	select *, (date(created) - interval (month(created) - 1) month - interval (day(created) - 1) day) as years
	from energy_log
	where deviceUuid = mDeviceUuid
	and created between from_unixtime(mFrom) and from_unixtime(mUntil)
) a
group by a.deviceUuid, a.years
order by a.years desc;
end;;

delimiter ;


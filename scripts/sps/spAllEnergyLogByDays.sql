drop procedure if exists spAllEnergyLogByDays;

delimiter ;;

create procedure spAllEnergyLogByDays(mDeviceUuid varchar(255), mFrom int, mUntil int)
begin
select a.deviceUuid,
	a.days as created,
	avg(a.volts) as volts,
    avg(a.amps) as amps,
    avg(a.frequency) as frequency,
    avg(a.difference) as difference
from (
	select *, date(created) as days
	from energy_log
	where deviceUuid = mDeviceUuid
	and created between from_unixtime(mFrom) and from_unixtime(mUntil)
) a
group by a.deviceUuid, a.days
order by a.days desc;
end;;

delimiter ;


drop procedure if exists spAllEnergyLogByHours;

delimiter ;;

create procedure spAllEnergyLogByHours(mDeviceUuid varchar(255), mFrom int, mUntil int)
begin
select a.deviceUuid,
	a.hours as created,
	avg(a.volts) as volts,
    avg(a.amps) as amps,
    avg(a.frequency) as frequency,
    avg(a.difference) as difference
from (
	select *, (created - interval minute(created) minute - interval second(created) second) as hours
	from energy_log
	where deviceUuid = mDeviceUuid
	and created between from_unixtime(mFrom) and from_unixtime(mUntil)
) a
group by a.deviceUuid, a.hours
order by a.hours desc;
end;;

delimiter ;


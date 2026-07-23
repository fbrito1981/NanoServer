drop procedure if exists spAllEnergyConsumedByYears;

delimiter ;;

create procedure spAllEnergyConsumedByYears(mDeviceUuid varchar(255), mFrom int, mUntil int, mMinBetReed int)
begin
select a.deviceUuid,
	a.years as created,
	sum(a.volts * a.amps * mMinBetReed / 60000.0) as energy
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


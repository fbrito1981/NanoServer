drop procedure if exists spAllEnergyConsumedByHours;

delimiter ;;

create procedure spAllEnergyConsumedByHours(mDeviceUuid varchar(255), mFrom int, mUntil int, mMinBetReed int)
begin
select a.deviceUuid,
	a.hours as created,
	sum(a.volts * a.amps * mMinBetreed / 60000.0) as energy
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


drop procedure if exists spAllEnergyConsumedByMinutes;

delimiter ;;

create procedure spAllEnergyConsumedByMinutes(mDeviceUuid varchar(255), mFrom int, mUntil int, mMinBetReed int)
begin
select
	deviceUuid,
	created,
	(volts * amps * mMinBetReed / 60000.0) as energy
from energy_log
where deviceUuid = mDeviceUuid
and created between from_unixtime(mFrom) and from_unixtime(mUntil)
order by created desc;
end;;

delimiter ;



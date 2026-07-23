drop procedure if exists spAllEnergyConsumedByDays;

delimiter ;;

create procedure spAllEnergyConsumedByDays(mDeviceUuid varchar(255), mFrom int, mUntil int, mMinBetReed int)
begin
select a.deviceUuid,
	a.days as created,
	sum(a.volts * a.amps * mMinBetReed / 60000.0) as energy
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


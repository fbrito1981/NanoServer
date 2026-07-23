drop procedure if exists spAllEnergyConsumedByMonths;

delimiter ;;

create procedure spAllEnergyConsumedByMonths(mDeviceUuid varchar(255), mFrom int, mUntil int, mMinBetReed int)
begin
select a.deviceUuid,
	a.months as created,
	sum(a.volts * a.amps * mMinBetReed / 60000.0) as energy
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


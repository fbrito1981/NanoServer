drop procedure if exists spAllEnergyLogByMinutes;

delimiter ;;

create procedure spAllEnergyLogByMinutes(mDeviceUuid varchar(255), mFrom int, mUntil int)
begin
select *
from energy_log
where deviceUuid = mDeviceUuid
and created between from_unixtime(mFrom) and from_unixtime(mUntil)
order by created desc;
end;;

delimiter ;



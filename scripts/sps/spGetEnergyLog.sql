drop procedure if exists spGetEnergyLog;

delimiter ;;

create procedure spGetEnergyLog(mDeviceUuid varchar(255))
begin
select deviceUuid,
	created,
	volts,
    amps,
    frequency,
    difference
from energy_log
where deviceUuid = mDeviceUuid
order by created desc
limit 1;
end;;

delimiter ;


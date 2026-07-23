drop procedure if exists spSetEnergyLog;

delimiter ;;

create procedure spSetEnergyLog(mDeviceUuid varchar(255), mCreated int,
	mVolts float, mAmps float, mFrequency float, mDifference int)
begin
insert into energy_log (deviceUuid, created, volts, amps, frequency, difference)
values (mDeviceUuid, from_unixtime(mCreated), mVolts, mAmps, mFrequency, mDifference)
on duplicate key update
	volts = mVolts,
    amps = mAmps,
    frequency = mFrequency,
    difference = mDifference;
end;;

delimiter ;


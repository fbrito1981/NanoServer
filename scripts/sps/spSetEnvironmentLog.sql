drop procedure if exists spSetEnvironmentLog;

delimiter ;;

create procedure spSetEnvironmentLog(mDeviceUuid varchar(255), mCreated int,
	mTemp float, mHum float)
begin
insert into environment_log (deviceUuid, created, temp, hum)
values (mDeviceUuid, from_unixtime(mCreated), mTemp, mHum)
on duplicate key update
	temp = mTemp,
    hum = mHum;
end;;

delimiter ;

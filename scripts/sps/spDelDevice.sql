drop procedure if exists spDelDevice;

delimiter ;;

create procedure spDelDevice(mUuid varchar(255))
begin
delete from devices
where uuid = mUuid;
end;;

delimiter ;


drop procedure if exists spGetDevice;

delimiter ;;

create procedure spGetDevice(mUuid varchar(255))
begin
select *
from devices
where uuid = mUuid;
end;;

delimiter ;


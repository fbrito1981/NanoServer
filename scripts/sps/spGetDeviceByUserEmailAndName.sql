drop procedure if exists spGetDeviceByUserEmailAndName;

delimiter ;;

create procedure spGetDeviceByUserEmailAndName(mUserEmail varchar(255), mName varchar(255))
begin
select *
from devices
where userEmail = mUserEmail
and name = mName
limit 1;
end;;

delimiter ;


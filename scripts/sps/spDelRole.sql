drop procedure if exists spDelRole;

delimiter ;;

create procedure spDelRole(mName varchar(255))
begin
delete from roles
where name = mName;
end;;

delimiter ;


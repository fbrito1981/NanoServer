drop procedure if exists spGetRole;

delimiter ;;

create procedure spGetRole(mName varchar(255))
begin
select *
from roles
where name = mName;
end;;

delimiter ;


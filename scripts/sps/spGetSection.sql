drop procedure if exists spGetSection;

delimiter ;;

create procedure spGetSection(mName varchar(255))
begin
select *
from sections
where name = mName;
end;;

delimiter ;


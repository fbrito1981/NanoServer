drop procedure if exists spDelSection;

delimiter ;;

create procedure spDelSection(mName varchar(255))
begin
delete from sections
where name = mName;
end;;

delimiter ;


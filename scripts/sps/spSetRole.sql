drop procedure if exists spSetRole;

delimiter ;;

create procedure spSetRole(mName varchar(255), mLevel int)
begin
insert into roles (name, level)
values (mName, mLevel)
on duplicate key update
	level = mLevel;
end;;

delimiter ;


drop procedure if exists spSetSection;

delimiter ;;

create procedure spSetSection(mName varchar(255), mMinRoleName varchar(255), mMenuOrder int)
begin
insert into sections (name, minRoleName, menuOrder)
values (mName, mMinRoleName, mMenuOrder)
on duplicate key update
	minRoleName = mMinRoleName,
	menuOrder = mMenuOrder;
end;;

delimiter ;


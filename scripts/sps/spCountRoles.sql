drop procedure if exists spCountRoles;

delimiter ;;

create procedure spCountRoles(mSearch varchar(255))
begin
set @sql = concat("
select count(name) as count
from roles");

if mSearch is not null then
set @sql = concat(@sql, "
where (
	name like '%", mSearch, "%'
)");
end if;

prepare stmt from @sql;

execute stmt;
end;;

delimiter ;


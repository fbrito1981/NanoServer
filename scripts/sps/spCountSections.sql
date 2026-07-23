drop procedure if exists spCountSections;

delimiter ;;

create procedure spCountSections(mSearch varchar(255))
begin
set @sql = concat("
select count(name) as count
from sections");

if mSearch is not null then
set @sql = concat(@sql, "
where (
	name like '%", mSearch, "%'
    or minRoleName like '%", mSearch, "%'
)");
end if;

prepare stmt from @sql;

execute stmt;
end;;

delimiter ;


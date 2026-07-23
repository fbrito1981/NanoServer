drop procedure if exists spAllSections;

delimiter ;;

create procedure spAllSections(mSearch varchar(255), mOrder varchar(255), mLimit int, mOffset int)
begin
set @sql = concat("
select *
from sections");

if mSearch is not null then
set @sql = concat(@sql, "
where (
	name like '%", mSearch, "%'
    or minRoleName like '%", mSearch, "%'
)");
end if;

if mOrder is not null then
set @sql = concat(@sql, "
order by ", mOrder);
else
set @sql = concat(@sql, "
order by menuOrder asc");
end if;

set @sql = concat(@sql, "
limit ", mLimit, " offset ", mOffset, ";");

prepare stmt from @sql;

execute stmt;
end;;

delimiter ;


drop procedure if exists spAllUsers;

delimiter ;;

create procedure spAllUsers(mSearch varchar(255), mOrder varchar(255), mLimit int, mOffset int)
begin
set @sql = concat("
select *
from users");

if mSearch is not null then
set @sql = concat(@sql, "
where (
	email like '%", mSearch, "%'
	or name like '%", mSearch, "%'
    or roleName like '%", mSearch, "%'
)");
end if;

if mOrder is not null then
set @sql = concat(@sql, "
order by ", mOrder);
else
set @sql = concat(@sql, "
order by name asc");
end if;

set @sql = concat(@sql, "
limit ", mLimit, " offset ", mOffset, ";");

prepare stmt from @sql;

execute stmt;
end;;

delimiter ;


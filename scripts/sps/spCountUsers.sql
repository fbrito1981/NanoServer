drop procedure if exists spCountUsers;

delimiter ;;

create procedure spCountUsers(mSearch varchar(255))
begin
set @sql = concat("
select count(email) as count
from users");

if mSearch is not null then
set @sql = concat(@sql, "
where (
	email like '%", mSearch, "%'
	or name like '%", mSearch, "%'
    or roleName like '%", mSearch, "%'
)");
end if;

prepare stmt from @sql;

execute stmt;
end;;

delimiter ;


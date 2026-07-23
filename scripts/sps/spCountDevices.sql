drop procedure if exists spCountDevices;

delimiter ;;

create procedure spCountDevices(mSearch varchar(255))
begin
set @sql = concat("
select count(uuid) as count
from devices");

if mSearch is not null then
	if instr(mSearch, "//") > 0 then
		set @userEmail = substring_index(mSearch, '//', 1);
		set @search = substring_index(mSearch, '//', -1);
		set @sql = concat(@sql, "
where userEmail = '", @userEmail, "'
and (
	uuid like '%", @search, "%'
	or name like '%", @search, "%'
	or model like '%", @search, "%'
    or os like '%", @search, "%'
    or version like '%", @search, "%'
)");
	else
		set @sql = concat(@sql, "
where (
	userEmail like '%", mSearch, "%'
    or uuid like '%", mSearch, "%'
	or name like '%", @search, "%'
	or model like '%", mSearch, "%'
    or os like '%", mSearch, "%'
    or version like '%", mSearch, "%'
)");
	end if;
end if;

prepare stmt from @sql;

execute stmt;
end;;

delimiter ;


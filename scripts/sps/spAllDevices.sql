drop procedure if exists spAllDevices;

delimiter ;;

create procedure spAllDevices(mSearch varchar(255), mOrder varchar(255), mLimit int, mOffset int)
begin
set @sql = concat("
select *
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

if mOrder is not null then
set @sql = concat(@sql, "
order by ", mOrder);
else
set @sql = concat(@sql, "
order by created asc");
end if;

set @sql = concat(@sql, "
limit ", mLimit, " offset ", mOffset, ";");

prepare stmt from @sql;

execute stmt;
end;;

delimiter ;


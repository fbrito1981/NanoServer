drop procedure if exists spAllEnergyLogs;

delimiter ;;

create procedure spAllEnergyLogs(mSearch varchar(255), mOrder varchar(255), mLimit int, mOffset int)
begin
set @sql = concat("
select *
from energy_log");

if mSearch is not null then
	if instr(mSearch, "//") > 0 then
		set @deviceUuid = substring_index(mSearch, '//', 1);
		set @search = substring_index(mSearch, '//', -1);
		set @sql = concat(@sql, "
where deviceUuid = '", @deviceUuid, "'
and (
	volts like '%", @search, "%'
    or amps like '%", @search, "%'
    or frequency like '%", @search, "%'
    or difference like '%", @search, "%'
)");
	else
		set @sql = concat(@sql, "
where deviceUuid like '%", mSearch, "%'
or volts like '%", @search, "%'
or amps like '%", @search, "%'
or frequency like '%", @search, "%'
or difference like '%", @search, "%''");
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


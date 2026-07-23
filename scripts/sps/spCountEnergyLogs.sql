drop procedure if exists spCountEnergyLogs;

delimiter ;;

create procedure spCountEnergyLogs(mSearch varchar(255))
begin
set @sql = concat("
select count(created)
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
or difference like '%", @search, "%'");
	end if;
end if;

set @sql = concat(@sql, ";");

prepare stmt from @sql;

execute stmt;
end;;

delimiter ;


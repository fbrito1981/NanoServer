drop procedure if exists spGetSecurityLog;

delimiter ;;

create procedure spGetSecurityLog(mNonce varchar(255))
begin
select *
from security_log
where nonce = mNonce;
end;;

delimiter ;


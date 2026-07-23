drop procedure if exists spSetSecurityLog;

delimiter ;;

create procedure spSetSecurityLog(mNonce varchar(255))
begin
insert into security_log (nonce)
values (mNonce);
end;;

delimiter ;


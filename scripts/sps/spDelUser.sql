drop procedure if exists spDelUser;

delimiter ;;

create procedure spDelUser(mEmail varchar(255))
begin
delete from users
where email = mEmail;
end;;

delimiter ;


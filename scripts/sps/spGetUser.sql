drop procedure if exists spGetUser;

delimiter ;;

create procedure spGetUser(mEmail varchar(255), mKey varchar(255))
begin
select email,
	aes_decrypt(pass, mKey) as pass,
    name,
    active,
    resetCode,
    roleName,
    token,
    picture,
    created,
    updated
from users
where email = mEmail;
end;;

delimiter ;


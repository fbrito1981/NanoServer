drop procedure if exists spSetUser;

delimiter ;;

create procedure spSetUser(mEmail varchar(255), mPass varchar(255), mKey varchar(255), mName varchar(255),
	mActive BOOL, mResetCode int, mRoleName varchar(255), mToken varchar(255), mPicture varchar(255))
begin
insert into users (email, pass, name, roleName, picture)
values (mEmail, aes_encrypt(mPass, mKey), mName, mRoleName, mPicture)
on duplicate key update
	pass = aes_encrypt(mPass, mKey),
	name = mName,
	active = mActive,
    resetCode = mResetCode,
    roleName = mRoleName,
    token = mToken,
    picture = mPicture;
end;;

delimiter ;


drop procedure if exists spSetDevice;

delimiter ;;

create procedure spSetDevice(mUuid varchar(255), mUserEmail varchar(255), mName varchar(255),
	mModel varchar(255), mOs varchar(255), mVersion varchar(255), mActive BOOL)
begin
insert into devices (uuid, userEmail, name, model, os, version)
values (mUuid, mUserEmail, mName, mModel, mOs, mVersion)
on duplicate key update
	userEmail = mUserEmail,
    name = mName,
	model = mModel,
	os = mOs,
	version = mVersion,
	active = mActive;
end;;

delimiter ;


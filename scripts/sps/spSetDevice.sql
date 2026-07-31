drop procedure if exists spSetDevice;

delimiter ;;

create procedure spSetDevice(mUuid varchar(255), mUserEmail varchar(255), mName varchar(255),
	mModel varchar(255), mOs varchar(255), mVersion varchar(255), mSettings text, mActive BOOL)
begin
insert into devices (uuid, userEmail, name, model, os, version, settings)
values (mUuid, mUserEmail, mName, mModel, mOs, mVersion, mSettings)
on duplicate key update
	userEmail = mUserEmail,
    name = mName,
	model = mModel,
	os = mOs,
	version = mVersion,
	settings = mSettings,
	active = mActive;
end;;

delimiter ;


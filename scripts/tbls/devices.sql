drop table if exists devices;

create table devices (
	uuid varchar(255) not null primary key,
    userEmail varchar(255) not null,
	name varchar(255) not null,
	model varchar(255) not null,
	os varchar(255) not null,
	version varchar(255) not null,
	settings text null,
	active bool not null default true,
	created timestamp not null default current_timestamp(),
	updated timestamp null on update current_timestamp(),
    foreign key (userEmail) references users (email),
    unique key (userEmail, name)
);


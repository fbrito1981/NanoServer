drop table if exists roles;

create table roles (
	name varchar(255) not null primary key,
	level int not null unique
);

insert into roles (name, level)
values ('Admin', 0),
	('User', 1);


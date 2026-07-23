drop table if exists users;

create table users (
	email varchar(255) not null primary key,
    pass blob not null,
	name varchar(255) not null,
	active bool not null default true,
    resetCode int null,
    roleName varchar(255) not null,
	token varchar(255) null,
	picture varchar(255) null,
	created timestamp not null default current_timestamp(),
	updated timestamp null on update current_timestamp(),
    constraint fk_users_roles foreign key (roleName) references roles (name)
);

insert into users (email, pass, name, roleName)
values ('fbrito@gmail.com', aes_encrypt('f16e7r81', 'mz5FUuA8%hqYPj6*'), 'Fernando Brito', 'Admin');


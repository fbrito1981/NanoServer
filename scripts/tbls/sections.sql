drop table if exists sections;

create table sections (
	name varchar(255) not null primary key,
    minRoleName varchar(255) not null,
    menuOrder int not null unique,
    constraint fk_sections_roles foreign key (minRoleName) references roles (name)
);

insert into sections (name, minRoleName, menuOrder)
values ('roles', 'Admin', 1),
    ('sections', 'Admin', 2),
    ('users', 'Admin', 3),
    ('devices', 'User', 4),
    ('energy', 'User', 5),
    ('environment', 'User', 6);


drop table if exists energy_log;

create table energy_log (
	deviceUuid varchar(255) not null,
    created timestamp not null default current_timestamp(),
    volts float not null,
    amps float not null,
    frequency float not null,
    difference int not null,
    primary key (deviceUuid, created),
    foreign key (deviceUuid) references devices (uuid)
);


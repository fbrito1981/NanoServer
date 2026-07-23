drop table if exists environment_log;

create table environment_log (
	deviceUuid varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci not null,
    created timestamp not null default current_timestamp(),
    temp float not null,
    hum float not null,
    primary key (deviceUuid, created),
    foreign key (deviceUuid) references devices (uuid)
);

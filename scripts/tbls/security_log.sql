drop table if exists security_log;

create table security_log(
	nonce varchar(255) not null primary key,
    created timestamp not null default current_timestamp()
);

create index ix_security_log_created
on security_log (created);


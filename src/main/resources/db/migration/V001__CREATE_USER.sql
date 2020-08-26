create table "users"
(
	userid serial
		constraint user_pk
			primary key,
	name varchar not null,
	password varchar not null
);


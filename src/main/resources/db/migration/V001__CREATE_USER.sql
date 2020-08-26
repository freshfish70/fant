create table "users"
(
	id serial
		constraint user_pk
			primary key,
	firstName varchar not null,
	lastName varchar not null,
	email varchar not null,
	password varchar not null
);


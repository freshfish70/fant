create table users
(
	id serial not null
		constraint user_pk
			primary key,
	firstname varchar not null,
	lastname varchar not null,
	email varchar not null
		constraint users_pk
			unique,
	password varchar not null
);

alter table users owner to postgres;

create table groups
(
	name varchar(25) not null
		constraint user_groups_pk
			primary key
);

alter table groups owner to postgres;



create table user_groups
(
	name varchar(25)
		constraint user_groups_groups_name_fk
			references groups,
	email varchar
		constraint table_name_users_email_fk
			references users (email)
);

alter table user_groups owner to postgres;


-- INSERT INTO users (firstname, lastname, email, password) VALUES ('Admin', 'Senior', 'admin@fant.no', 'PBKDF2WithHmacSHA256:2048:3V5UiWVGCirU44s67j04iHzyYKglSj5lwvjtheZP3E0=:A3Pj6HHaWu4vVRwqkD4zAGPY0HQz4h3lwtWVCrkgtLI=')
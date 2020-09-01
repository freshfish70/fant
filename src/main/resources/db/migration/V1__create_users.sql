create table "users"
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

-- INSERT INTO users (firstname, lastname, email, password) VALUES ('Admin', 'Senior', 'admin@fant.no', 'PBKDF2WithHmacSHA256:2048:3V5UiWVGCirU44s67j04iHzyYKglSj5lwvjtheZP3E0=:A3Pj6HHaWu4vVRwqkD4zAGPY0HQz4h3lwtWVCrkgtLI=')


create table "groups"
(
	name varchar(25) not null
		constraint user_groups_pk
			primary key
);

create table "user_groups"
(
	name varchar(25)
		constraint table_name_groups_name_fk
			references groups
		constraint table_name_users_email_fk
			references users (email),
	email varchar
);


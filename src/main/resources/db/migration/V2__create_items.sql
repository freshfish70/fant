create table items
(
	id serial not null
		constraint items_pk
			primary key,
	name varchar,
	description varchar,
	price real default 0,
	image integer,
	sold boolean default false,
	created date,
	updated date,
	seller integer
		constraint items_users_id_fk
			references users,
	buyer integer
		constraint items_users_id_fk_2
			references users
);

alter table items owner to postgres;

create unique index items_id_uindex
	on items (id);


create table images
(
	id serial not null
		constraint images_pk
			primary key,
	name varchar,
	mime_type varchar,
	size real,
	owner integer
		constraint images_items_id_fk
			references items
				on update cascade on delete cascade
);

alter table images owner to postgres;

create unique index images_id_uindex
	on images (id);
create sequence IF NOT EXISTS seq_id_gen START with 10;
--
-- db_role
--
create TABLE IF NOT EXISTS db_role
(
    name varchar(16) NOT NULL,
    PRIMARY KEY (name)
);
--
-- db_user
--
create TABLE IF NOT EXISTS db_user
(
    id       integer     NOT NULL DEFAULT nextval('seq_id_gen'),
    name     varchar(30) NOT NULL,
    email    varchar(50) NOT NULL,
    password varchar(16) NOT NULL,
    role_id  varchar(16) NOT NULL,
    active   smallint    NOT NULL default 1,
    PRIMARY KEY (id),
    CONSTRAINT db_user_role_fk FOREIGN KEY (role_id) REFERENCES db_role (name),
    constraint active_flag check (active in (0, 1))
);
create
    INDEX IF NOT EXISTS db_user_role_fki on db_user (role_id);
--
-- db_customer
--
create TABLE IF NOT EXISTS db_customer
(
    id         integer     NOT NULL default 0,
    category   char(1)     NOT NULL,
    salutation varchar(10),
    first_name varchar(30) NOT NULL,
    last_name  varchar(30) NOT NULL,
    birth_date date,
    PRIMARY KEY (id),
    CONSTRAINT db_customer_user_fk FOREIGN KEY (id) REFERENCES db_user (id)
);
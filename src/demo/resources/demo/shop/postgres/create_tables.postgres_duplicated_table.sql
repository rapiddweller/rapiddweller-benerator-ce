CREATE SEQUENCE schema2.seq_id_gen START WITH 10;
--
-- db_category
--
CREATE TABLE schema2.db_category
(
    id        varchar(9)  NOT NULL,
    name      varchar(30) NOT NULL,
    parent_id varchar(9) default NULL,
    PRIMARY KEY (id),
    CONSTRAINT db_category_parent_fk FOREIGN KEY (parent_id) REFERENCES schema2.db_category (id)
);
CREATE
INDEX db_cat_parent_fki ON schema2.db_category (parent_id);
--
-- db_product
--
CREATE TABLE schema2.db_product
(
    ean_code     varchar(13)   NOT NULL,
    name         varchar(30)   NOT NULL,
    category_id  varchar(9)    NOT NULL,
    price        decimal(8, 2) NOT NULL,
    manufacturer varchar(30)   NOT NULL,
    notes        varchar(256) NULL,
    description  text NULL,
    PRIMARY KEY (ean_code),
    CONSTRAINT db_product_category_fk FOREIGN KEY (category_id) REFERENCES schema2.db_category (id)
);
CREATE
INDEX db_product_category_fki ON schema2.db_product (category_id);
----
---- db_role
----
CREATE TABLE schema2.db_role
(
    name_sl varchar(16) NOT NULL,
    PRIMARY KEY (name_sl)
);
----
---- db_user
----
CREATE TABLE schema2.db_user
(
    id       integer     NOT NULL DEFAULT nextval('schema2.seq_id_gen'),
    name_sl     varchar(30) NOT NULL,
    email_sl    varchar(50) NOT NULL,
    password_sl varchar(16) NOT NULL,
    role_id_sl  varchar(16) NOT NULL,
    active_sl   smallint    NOT NULL default 1,
    CONSTRAINT db_user_pkey_sl PRIMARY KEY (id),
    constraint active_flag_sl check (active_sl in (0, 1))
);
--CREATE
--INDEX db_user_role_sl_fki on schema2.db_user (role_id_sl);
CREATE SEQUENCE schema1.seq_id_gen START WITH 10;
--
-- db_role
--
CREATE TABLE schema1.db_role
(
    name varchar(16) NOT NULL,
    PRIMARY KEY (name)
);
--
-- db_user
--
CREATE TABLE schema1.db_user
(
    id       integer     NOT NULL DEFAULT nextval('schema1.seq_id_gen'),
    name     varchar(30) NOT NULL,
    email    varchar(50) NOT NULL,
    password varchar(16) NOT NULL,
    role_id  varchar(16) NOT NULL,
    active   smallint    NOT NULL default 1,
    CONSTRAINT db_user_pkey PRIMARY KEY (id),
    CONSTRAINT db_user_role_fk FOREIGN KEY (role_id) REFERENCES schema1.db_role (name),
    constraint active_flag check (active in (0, 1))
);
CREATE
INDEX db_user_role_fki on schema1.db_user (role_id);
--
-- db_customer
--
CREATE TABLE schema1.db_customer
(
    id         integer     PRIMARY KEY,
    category   char(1)     NOT NULL,
    salutation varchar(10),
    first_name varchar(30) NOT NULL,
    last_name  varchar(30) NOT NULL,
    birth_date date,
    CONSTRAINT db_customer_user_fk FOREIGN KEY (id) REFERENCES schema1.db_user (id)
);
--
-- db_order
--
CREATE TABLE schema1.db_order
(
    id          integer       PRIMARY KEY DEFAULT nextval('seq_id_gen'),
    customer_id integer       NOT NULL,
    total_price decimal(8, 2) NOT NULL,
    created_at  timestamp     NOT NULL,
    CONSTRAINT db_order_customer_fk FOREIGN KEY (customer_id) REFERENCES schema1.db_customer (id)
);
CREATE
INDEX db_order_customer_fki on schema1.db_order(customer_id);

--
-- db_order_item
--
CREATE TABLE schema1.db_order_item
(
    id               integer       NOT NULL DEFAULT nextval('seq_id_gen'),
    order_id         integer       NOT NULL,
    number_of_items  integer       NOT NULL default 1,
    product_ean_code varchar(13)   NOT NULL,
    total_price      decimal(8, 2) NOT NULL,
    CONSTRAINT db_order_item_pkey PRIMARY KEY (id),
    CONSTRAINT db_order_item_order_fk FOREIGN KEY (order_id) REFERENCES schema1.db_order (id),
    CONSTRAINT db_order_item_product_fk FOREIGN KEY (product_ean_code) REFERENCES schema2.db_product (ean_code)
);
CREATE
INDEX db_order_item_order_fki ON schema1.db_order_item (order_id);
CREATE
INDEX db_order_item_product_fki ON schema1.db_order_item (product_ean_code);
COMMIT;
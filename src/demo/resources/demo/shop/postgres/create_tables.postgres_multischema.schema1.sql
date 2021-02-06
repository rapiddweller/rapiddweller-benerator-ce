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
    id       integer     NOT NULL DEFAULT nextval('seq_id_gen'),
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
    CONSTRAINT db_order_item_order_fk FOREIGN KEY (order_id) REFERENCES db_order (id),
    CONSTRAINT db_order_item_product_fk FOREIGN KEY (product_ean_code) REFERENCES schema2.db_product (ean_code)
);
CREATE
INDEX db_order_item_order_fki ON schema1.db_order_item (order_id);
CREATE
INDEX db_order_item_product_fki ON schema1.db_order_item (product_ean_code);
COMMIT;
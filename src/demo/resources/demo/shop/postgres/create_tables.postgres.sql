create sequence IF NOT EXISTS seq_id_gen START with 10;
--
-- db_category
--
create TABLE IF NOT EXISTS db_category
(
    id        varchar(9)  NOT NULL,
    name      varchar(30) NOT NULL,
    parent_id varchar(9) default NULL,
    PRIMARY KEY (id),
    CONSTRAINT db_category_parent_fk FOREIGN KEY (parent_id) REFERENCES db_category (id)
);
create
    INDEX IF NOT EXISTS db_cat_parent_fki ON db_category (parent_id);
--
-- db_product
--
create TABLE IF NOT EXISTS db_product
(
    ean_code     varchar(13)   NOT NULL,
    name         varchar(30)   NOT NULL,
    category_id  varchar(9)    NOT NULL,
    price        decimal(8, 2) NOT NULL,
    manufacturer varchar(30)   NOT NULL,
    notes        varchar(256)  NULL,
    description  text          NULL,
    PRIMARY KEY (ean_code),
    CONSTRAINT db_product_category_fk FOREIGN KEY (category_id) REFERENCES db_category (id)
);
create
    INDEX IF NOT EXISTS db_product_category_fki ON db_product (category_id);
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
--
-- db_order
--
create TABLE IF NOT EXISTS db_order
(
    id          integer       NOT NULL DEFAULT nextval('seq_id_gen'),
    customer_id integer       NOT NULL,
    total_price decimal(8, 2) NOT NULL,
    order_date   date          NOT NULL default now(),
    created_at  timestamp     NOT NULL default now(),
    PRIMARY KEY (id),
    CONSTRAINT db_order_customer_fk FOREIGN KEY (customer_id) REFERENCES db_customer (id)
);
create
    INDEX IF NOT EXISTS db_order_customer_fki on db_order (customer_id);

--
-- db_order_item
--
create TABLE IF NOT EXISTS db_order_item
(
    id               integer       NOT NULL DEFAULT nextval('seq_id_gen'),
    order_id         integer       NOT NULL,
    number_of_items  integer       NOT NULL default 1,
    product_ean_code varchar(13)   NOT NULL,
    total_price      decimal(8, 2) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT db_order_item_order_fk FOREIGN KEY (order_id) REFERENCES db_order (id),
    CONSTRAINT db_order_item_product_fk FOREIGN KEY (product_ean_code) REFERENCES db_product (ean_code)
);
create
    INDEX IF NOT EXISTS db_order_item_order_fki ON db_order_item (order_id);
create
    INDEX IF NOT EXISTS db_order_item_product_fki ON db_order_item (product_ean_code);
commit;
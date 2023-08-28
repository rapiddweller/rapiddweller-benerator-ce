-- CREATE TABLE statements with Vertica syntax

CREATE TABLE db_category
(
    id        VARCHAR(9)  NOT NULL,
    name      VARCHAR(30) NOT NULL,
    parent_id VARCHAR(9) DEFAULT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (parent_id) REFERENCES db_category (id)
);

CREATE TABLE db_product
(
    ean_code    VARCHAR(13) NOT NULL,
    name        VARCHAR(30) NOT NULL,
    category_id VARCHAR(9)  NOT NULL,
    price       FLOAT(8) ,
    manufacturer VARCHAR(30)  NOT NULL,
    notes        VARCHAR(256)     NULL,
    description  LONG VARCHAR     NULL,
    image        VARBINARY     NULL,
    PRIMARY KEY  (ean_code),
    FOREIGN KEY (category_id) REFERENCES db_category (id)
);

CREATE TABLE db_role
(
    name VARCHAR(16) NOT NULL,
    PRIMARY KEY (name)
);

CREATE TABLE db_user
(
    id       INT NOT NULL,
    name     VARCHAR(30) NOT NULL,
    email    VARCHAR(50) NOT NULL,
    password VARCHAR(16) NOT NULL,
    role_id  VARCHAR(16) NOT NULL,
    active   BOOLEAN NOT NULL DEFAULT true,
    PRIMARY KEY (id),
    FOREIGN KEY (role_id) REFERENCES db_role (name),
    CONSTRAINT active_flag CHECK (active IN (false, true))
);

CREATE TABLE db_customer
(
    id         INT NOT NULL,
    category   CHAR(1) NOT NULL,
    salutation VARCHAR(10) NULL,
    first_name VARCHAR(30) NOT NULL,
    last_name  VARCHAR(30) NOT NULL,
    birth_date DATE,
    PRIMARY KEY (id),
    FOREIGN KEY (id) REFERENCES db_user (id)
);

CREATE TABLE db_order
(
    id          INT NOT NULL,
    customer_id INT NOT NULL,
    total_price FLOAT(8) ,
    created_at  TIMESTAMP NOT NULL,
    order_date  DATE NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (customer_id) REFERENCES db_customer (id)
);

CREATE TABLE db_order_item
(
    id               INT NOT NULL,
    order_id         INT NOT NULL,
    number_of_items  INT NOT NULL DEFAULT 1,
    product_ean_code VARCHAR(13) NOT NULL,
    total_price      FLOAT(8) ,
    PRIMARY KEY  (id),
    FOREIGN KEY (order_id) REFERENCES db_order (id),
    FOREIGN KEY (product_ean_code) REFERENCES db_product (ean_code)
);

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
--  image        bytea            NULL, TODO support bytea type
    PRIMARY KEY (ean_code),
    CONSTRAINT db_product_category_fk FOREIGN KEY (category_id) REFERENCES schema2.db_category (id)
);
CREATE
INDEX db_product_category_fki ON schema2.db_product (category_id);
COMMIT;

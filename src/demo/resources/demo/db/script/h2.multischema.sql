CREATE SCHEMA "schema1";
CREATE SCHEMA "schema2";
CREATE SCHEMA "schema3";

CREATE TABLE "schema3"."db_manufacturer"
(
    id          INT AUTO_INCREMENT PRIMARY KEY,
    name        varchar(30) NOT NULL,
    description VARCHAR(250) DEFAULT NULL
);
CREATE TABLE "schema1"."db_Category"
(
    id   INT AUTO_INCREMENT PRIMARY KEY,
    name varchar(30) NOT NULL
);
CREATE TABLE "schema1"."db_product"
(
    ean_code        varchar(13)   NOT NULL,
    name            varchar(30)   NOT NULL,
    category_id     int,
    manufacturer_id int,
    price           decimal(8, 2) NOT NULL,
    notes           varchar(256)  NULL,
    description     VARCHAR(250) DEFAULT NULL,
    FOREIGN KEY (category_id) REFERENCES "schema1"."db_Category",
    FOREIGN KEY (manufacturer_id) REFERENCES "schema3"."db_manufacturer"
);
COMMIT;
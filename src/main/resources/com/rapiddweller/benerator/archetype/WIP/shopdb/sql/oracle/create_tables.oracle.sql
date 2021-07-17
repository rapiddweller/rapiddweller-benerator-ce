PURGE
RECYCLEBIN;
--
-- db_category
--
CREATE TABLE db_category
(
    id        varchar2(9) NOT NULL,
    name      varchar2(30) NOT NULL,
    parent_id varchar2(9) default NULL,
    PRIMARY KEY (id),
    CONSTRAINT db_category_parent_fk FOREIGN KEY (parent_id) REFERENCES db_category (id)
);
COMMENT
ON TABLE db_category IS 'Hierarchy of product categories';
CREATE
INDEX db_cat_parent_fki ON db_category (parent_id);
--
-- db_product
--
CREATE TABLE db_product
(
    ean_code     varchar2(13) NOT NULL,
    name         varchar2(30) NOT NULL,
    category_id  varchar2(9) NOT NULL,
    price        number(8,2) NOT NULL,
    manufacturer varchar2(30) NOT NULL,
    notes        varchar2(256) NULL,
    description  clob NULL,
    image        blob NULL,
    PRIMARY KEY (ean_code),
    CONSTRAINT db_product_category_fk FOREIGN KEY (category_id) REFERENCES db_category (id)
);
COMMENT
ON TABLE db_product IS 'products';
CREATE
INDEX db_product_category_fki ON db_product (category_id);
--
-- db_role
--
CREATE TABLE db_role
(
    name varchar2(16) NOT NULL,
    PRIMARY KEY (name)
);
COMMENT
ON TABLE db_role IS 'roles of the shop users';
--
-- db_user
--
CREATE TABLE db_user
(
    id       number(10) NOT NULL,
    name     varchar2(30) NOT NULL,
    email    varchar2(50) NOT NULL,
    password varchar2(16) NOT NULL,
    role_id  varchar2(16) NOT NULL,
    active   number(1) default 1 NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT db_user_role_fk FOREIGN KEY (role_id) REFERENCES db_role (name),
    constraint active_flag check (active in (0, 1))
);
COMMENT
ON TABLE db_user IS 'shop users';
CREATE
INDEX db_user_role_fki on db_user (role_id);
--
-- db_customer
--
CREATE TABLE db_customer
(
    id         number(10) default 0 NOT NULL,
    category   char(1) NOT NULL,
    salutation varchar2(10) NULL,
    first_name varchar2(30) NOT NULL,
    last_name  varchar2(30) NOT NULL,
    birth_date date,
    PRIMARY KEY (id),
    CONSTRAINT db_customer_user_fk FOREIGN KEY (id) REFERENCES db_user (id)
);
COMMENT
ON TABLE db_customer IS 'users of type ''customer''';
CREATE
INDEX db_customer_name on db_customer (first_name, last_name);
--
-- db_order
--
CREATE TABLE db_order
(
    id          number(10) NOT NULL,
    customer_id number(10) NOT NULL,
    total_price number(8,2) NOT NULL,
    created_at  timestamp with time zone NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT db_order_customer_fk FOREIGN KEY (customer_id) REFERENCES db_customer (id)
);
COMMENT
ON TABLE db_order IS 'product orders';
CREATE
INDEX db_order_customer_fki on db_order (customer_id);
--
-- db_order_item
--
CREATE TABLE db_order_item
(
    id               number(10) NOT NULL,
    order_id         number(10) NOT NULL,
    number_of_items  number(10) default 1 NOT NULL,
    product_ean_code varchar2(13) NOT NULL,
    total_price      number(8,2) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT db_order_item_order_fk FOREIGN KEY (order_id) REFERENCES db_order (id),
    CONSTRAINT db_order_item_product_fk FOREIGN KEY (product_ean_code) REFERENCES db_product (ean_code)
);
COMMENT
ON TABLE db_order_item IS 'order items';
CREATE
INDEX db_order_item_order_fki ON db_order_item (order_id);
CREATE
INDEX db_order_item_product_fki ON db_order_item (product_ean_code);
/*
 * (c) Copyright 2006-2020 by rapiddweller GmbH & Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General Public License.
 *
 * For redistributing this software or a derivative work under a license other
 * than the GPL-compatible Free Software License as defined by the Free
 * Software Foundation or approved by OSI, you must first obtain a commercial
 * license to this software product from rapiddweller GmbH & Volker Bergmann.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED CONDITIONS,
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE
 * HEREBY EXCLUDED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

--
-- sequence
--
CREATE SEQUENCE seq_id_gen START WITH 10;
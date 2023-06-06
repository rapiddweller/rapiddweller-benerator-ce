CREATE SCHEMA schema1;

CREATE TABLE schema1.table1
(
    id          INT AUTO_INCREMENT PRIMARY KEY,
    string_value        varchar(30),
    long_value          BIGINT,
    bool_value          BOOLEAN,
    double_value        DOUBLE PRECISION,
    decimal_value       NUMERIC,
    date_value          DATE
);

COMMIT;
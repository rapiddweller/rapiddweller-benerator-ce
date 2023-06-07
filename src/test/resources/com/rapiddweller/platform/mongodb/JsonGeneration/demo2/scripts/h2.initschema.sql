CREATE SCHEMA schemaH2;

CREATE TABLE schemaH2.tableH2
(
    id          INT PRIMARY KEY,
    string_value        varchar(30),
    long_value          BIGINT,
    bool_value          BOOLEAN,
    double_value        DOUBLE PRECISION,
    decimal_value       NUMERIC,
    date_value          DATE
);

COMMIT;
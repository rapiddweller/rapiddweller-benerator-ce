DROP TABLE IF EXISTS "db_postgres_types";
        CREATE TABLE IF NOT EXISTS "db_postgres_types"
        (
        id          INT PRIMARY KEY,
        "boolean"   BOOLEAN NOT NULL,
        "char"      CHAR(1) NOT NULL,
        "date"      DATE NOT NULL DEFAULT CURRENT_DATE,
        "double"    DOUBLE PRECISION NOT NULL,
        "float"     REAL NOT NULL,
        "int"       INT NOT NULL,
        "long"      BIGINT NOT NULL,
        "short"     SMALLINT NOT NULL,
        "string"    VARCHAR(30) NOT NULL,
        "timestamp" TIMESTAMP NOT NULL DEFAULT now(),
        "json"      JSON NOT NULL,
        "genom"     GEOMETRY NOT NULL
        );
COMMIT;
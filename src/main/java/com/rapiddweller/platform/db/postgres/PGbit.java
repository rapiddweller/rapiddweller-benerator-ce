package com.rapiddweller.platform.db.postgres;

import org.postgresql.geometric.PGcircle;
import org.postgresql.jdbc.PgArray;
import org.postgresql.util.PGBinaryObject;
import org.postgresql.util.PGobject;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Base64;

/**
 * Helper class for writing BIT to Postgres.<br/><br/>
 * Created: 03.30.2023
 * @author rapiddweller
 * @since 3.1.0
 */
public class PGbit extends PGobject {
    public PGbit(byte[] bytes) throws SQLException {
        String bit = Base64.getEncoder().encodeToString(bytes);
        setValue(bit);
        setType("BIT");
    }
}
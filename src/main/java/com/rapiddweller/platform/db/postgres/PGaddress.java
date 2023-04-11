package com.rapiddweller.platform.db.postgres;

import org.postgresql.util.PGTime;
import org.postgresql.util.PGobject;

import java.sql.SQLException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class is made for test postgres composite type
 *
 */
public class PGaddress extends PGobject {
    public PGaddress(String str) throws SQLException, ParseException {
        setValue(str);
        setType("ADDRESS");
    }
}
package com.rapiddweller.platform.db.postgres;

import org.postgresql.util.PGTime;
import org.postgresql.util.PGobject;

import java.sql.SQLException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Helper class for writing CIDR to Postgres.<br/><br/>
 * Created: 03.30.2023 18:02:42
 * @author rapiddweller
 * @since 3.1.0
 */
public class PGtime extends PGobject {

    public PGtime(Date date) throws SQLException {
        PGTime pgTime = new PGTime(date.getTime());
        setValue(pgTime.toString());
        setType("TIME");
    }

    public PGtime(Time time) throws SQLException {
        PGTime pgTime = new PGTime(time.getTime());
        setValue(pgTime.toString());
        setType("TIME");
    }

    public PGtime(String datetime) throws SQLException, ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date date = sdf.parse(datetime);
        PGTime pgTime = new PGTime(date.getTime());
        setValue(pgTime.toString());
        setType("TIME");
    }
}
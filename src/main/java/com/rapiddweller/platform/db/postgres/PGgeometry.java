package com.rapiddweller.platform.db.postgres;

import org.postgresql.util.PGobject;

import java.sql.SQLException;

/**
 * Helper class for writing GEOMETRY to Postgres.<br/><br/>
 * Created: 09.11.2021 19:33:42
 * @author rapiddweller
 * @since 3.1.0
 */
public class PGgeometry extends PGobject {
  public PGgeometry(String geometry) throws SQLException {
    setValue(geometry);
    setType("GEOMETRY");
  }
}

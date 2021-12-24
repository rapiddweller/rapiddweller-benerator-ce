/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.platform.db.postgres;

import com.rapiddweller.benerator.util.DeprecationLogger;
import org.postgresql.util.PGobject;

import java.sql.SQLException;

/**
 * Helper class for writing JSON strings to Postgres.<br/><br/>
 * Created: 09.11.2021 19:33:42
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class JSONPGObject extends PGobject {

  public JSONPGObject(String json) throws SQLException {
    if (json != null && json.startsWith("#{") && json.endsWith("}#")) {
      json = json.substring(1, json.length() - 1);
      DeprecationLogger.warn("Obsolete syntax: Escaping JSON strings in #{}#. " +
          "This is still supported for backwards compatibility but will be abandoned in a future release.");
    }
    setValue(json);
    setType("JSON");
  }

}

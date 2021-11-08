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

package com.rapiddweller.platform.db;

import com.rapiddweller.common.CollectionUtil;
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.jdbacl.model.DBDataType;
import com.rapiddweller.script.PrimitiveType;

import java.sql.Types;
import java.util.Map;


/**
 * Maps JDBC types to benerator types.
 * @author Volker Bergmann
 * @since 0.3.04
 */
@SuppressWarnings("unchecked")
public class JdbcMetaTypeMapper {

  private static final Map<Integer, PrimitiveType> TYPE_MAP;

  static {

    TYPE_MAP = CollectionUtil.buildMap(
        // Types.ARRAY is not supported
        Types.BIGINT, PrimitiveType.LONG,
        Types.BINARY, PrimitiveType.BINARY,
        Types.BIT, PrimitiveType.BYTE,
        Types.BLOB, PrimitiveType.BINARY,
        Types.BOOLEAN, PrimitiveType.BOOLEAN,
        Types.CHAR, PrimitiveType.STRING,
        Types.CLOB, PrimitiveType.STRING,
        Types.DATALINK, PrimitiveType.STRING, // TODO test
        Types.DATE, PrimitiveType.DATE,
        Types.DECIMAL, PrimitiveType.BIG_DECIMAL,
        // Types.DISTINCT is not supported
        Types.DOUBLE, PrimitiveType.DOUBLE,
        Types.FLOAT, PrimitiveType.FLOAT,
        Types.INTEGER, PrimitiveType.INT,
        Types.JAVA_OBJECT, PrimitiveType.OBJECT,
        Types.LONGVARBINARY, PrimitiveType.BINARY,
        Types.LONGVARCHAR, PrimitiveType.STRING,
        // Types.NULL is not supported
        Types.NUMERIC, PrimitiveType.DOUBLE,
        Types.REAL, PrimitiveType.DOUBLE,
        Types.REF, PrimitiveType.STRING, // TODO test
        Types.SMALLINT, PrimitiveType.SHORT,
        // Types.STRUCT is not supported
        Types.TIME, PrimitiveType.DATE,
        Types.TIMESTAMP, PrimitiveType.TIMESTAMP,
        Types.TINYINT, PrimitiveType.BYTE,
        Types.VARBINARY, PrimitiveType.BINARY,
        Types.VARCHAR, PrimitiveType.STRING,
        Types.OTHER, PrimitiveType.STRING);
  }

  private JdbcMetaTypeMapper() {
    // private constructor to prevent instantiation
  }

  public static String abstractType(DBDataType columnType,
                                    boolean acceptUnknown) {
    int jdbcType = columnType.getJdbcType();
    PrimitiveType primitiveType = TYPE_MAP.get(jdbcType);
    if (primitiveType != null) {
      return primitiveType.getName();
    } else {
      String lcName = columnType.getName().toLowerCase();
      if (lcName.startsWith("timestamp")) {
        return PrimitiveType.TIMESTAMP.getName();
      } else if (lcName.endsWith("char") || lcName.startsWith("xml")
          || lcName.endsWith("varchar2") || lcName.endsWith("clob")) {
        return PrimitiveType.STRING.getName();
      } else if (!acceptUnknown) {
        throw new ConfigurationError(
            "Platform specific SQL type (" + jdbcType +
                ") not mapped: " + columnType.getName());
      } else {
        return null;
      }
    }
  }

}

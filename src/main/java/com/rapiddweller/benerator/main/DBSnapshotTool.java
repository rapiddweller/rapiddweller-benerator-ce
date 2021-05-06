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

package com.rapiddweller.benerator.main;

import com.rapiddweller.benerator.Consumer;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.common.NumberUtil;
import com.rapiddweller.common.RoundedNumberFormat;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.common.SystemInfo;
import com.rapiddweller.common.ui.ProgressMonitor;
import com.rapiddweller.format.DataContainer;
import com.rapiddweller.format.DataIterator;
import com.rapiddweller.model.data.DataModel;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.model.data.TypeDescriptor;
import com.rapiddweller.platform.db.DefaultDBSystem;
import com.rapiddweller.platform.db.SQLEntityExporter;
import com.rapiddweller.platform.dbunit.DbUnitEntityExporter;
import com.rapiddweller.platform.xls.XLSEntityExporter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Creates a snapshot of a database schema and exports it in DbUnit XML file format.
 *
 * @author Volker Bergmann
 * @since 0.3.04
 */
public class DBSnapshotTool {

  /**
   * The constant DBUNIT_FORMAT.
   */
  public static final String DBUNIT_FORMAT = "dbunit";
  /**
   * The constant XLS_FORMAT.
   */
  public static final String XLS_FORMAT = "xls";
  /**
   * The constant SQL_FORMAT.
   */
  public static final String SQL_FORMAT = "sql";

  /**
   * The constant DEFAULT_FORMAT.
   */
  public static final String DEFAULT_FORMAT = DBUNIT_FORMAT;

  /**
   * The constant DB_PASSWORD.
   */
  public static final String DB_PASSWORD = "dbPassword";
  /**
   * The constant DB_URL.
   */
  public static final String DB_URL = "dbUrl";
  /**
   * The constant DB_DRIVER.
   */
  public static final String DB_DRIVER = "dbDriver";
  /**
   * The constant DB_SCHEMA.
   */
  public static final String DB_SCHEMA = "dbSchema";
  /**
   * The constant DB_CATALOG.
   */
  public static final String DB_CATALOG = "dbCatalog";
  /**
   * The constant DB_USER.
   */
  public static final String DB_USER = "dbUser";
  /**
   * The constant FORMAT.
   */
  public static final String FORMAT = "format";
  /**
   * The constant DIALECT.
   */
  public static final String DIALECT = "dialect";

  // TODO v0.8 test with each database
  private static final Logger logger = LogManager.getLogger(DBSnapshotTool.class);

  /**
   * Supported formats string [ ].
   *
   * @return the string [ ]
   */
  public static String[] supportedFormats() {
    return new String[] {
        DBUNIT_FORMAT, XLS_FORMAT, SQL_FORMAT
    };
  }

  /**
   * The entry point of application.
   *
   * @param args the input arguments
   */
  public static void main(String[] args) {
    logger.info("Starting " + DBSnapshotTool.class.getSimpleName());
    String format = System.getProperty(FORMAT);
    if (format == null) {
      format = DEFAULT_FORMAT;
    }
    String filename = (args.length > 0 ? args[0] : defaultFilename(format));

    String dbUrl = System.getProperty(DB_URL);
    if (StringUtil.isEmpty(dbUrl)) {
      throw new IllegalArgumentException("No database URL specified. " +
          "Please provide the JDBC URL as an environment property like '-DdbUrl=jdbc:...'");
    }
    String dbDriver = System.getProperty(DB_DRIVER);
    if (StringUtil.isEmpty(dbDriver)) {
      throw new IllegalArgumentException("No database driver specified. " +
          "Please provide the JDBC driver class name as an environment property like '-DdbDriver=...'");
    }
    String dbUser = System.getProperty(DB_USER);
    String dbPassword = System.getProperty(DB_PASSWORD);
    String dbSchema = System.getProperty(DB_SCHEMA);
	String dbCatalog = System.getProperty(DB_CATALOG);
    String dialect = System.getProperty(DIALECT);

    logger.info("Exporting data of database '" + dbUrl + "' with driver '" + dbDriver + "' as user '" + dbUser
		+ "'" + (dbSchema != null ? " using schema '" + dbSchema + "'" : "") + "'"
		+ (dbCatalog != null ? " using catalog '" + dbCatalog + "'" : "")
        + " in " + format + " format to file " + filename);

	export(dbUrl, dbDriver, dbSchema, dbCatalog, dbUser, dbPassword, filename, format, dialect);
  }

  private static String defaultFilename(String format) {
    if (XLS_FORMAT.equals(format)) {
      return "snapshot.xls";
    } else if (SQL_FORMAT.equals(format)) {
      return "snapshot.sql";
    } else {
      return "snapshot.dbunit.xml";
    }
  }

  /**
   * Export.
   *
   * @param dbUrl      the db url
   * @param dbDriver   the db driver
   * @param dbSchema   the db schema
   * @param dbUser     the db user
   * @param dbPassword the db password
   * @param filename   the filename
   * @param format     the format
   * @param dialect    the dialect
   */
  public static void export(String dbUrl, String dbDriver, String dbSchema, String dbCatalog,
                            String dbUser, String dbPassword, String filename, String format, String dialect) {
	export(dbUrl, dbDriver, dbSchema, dbCatalog, dbUser, dbPassword, filename, SystemInfo.getFileEncoding(),
        format, dialect, null);
  }

  /**
   * Export.
   *
   * @param dbUrl      the db url
   * @param dbDriver   the db driver
   * @param dbSchema   the db schema
   * @param dbUser     the db user
   * @param dbPassword the db password
   * @param filename   the filename
   * @param encoding   the encoding
   * @param format     the format
   * @param dialect    the dialect
   * @param monitor    the monitor
   */
  public static void export(String dbUrl, String dbDriver, String dbSchema, String dbCatalog,
                            String dbUser, String dbPassword, String filename, String encoding, String format, String dialect,
                            ProgressMonitor monitor) {
    if (dbUser == null) {
      logger.warn("No JDBC user specified");
    }
    String lineSeparator = SystemInfo.getLineSeparator();
    long startTime = System.currentTimeMillis();

    Consumer exporter = null;
    int count = 0;
    try (DefaultDBSystem db = new DefaultDBSystem("db", dbUrl, dbDriver, dbUser, dbPassword, new DataModel())) {
      // connect DB
      if (dbSchema != null) {
        db.setSchema(dbSchema);
      }
	  if (dbCatalog != null) {
		db.setCatalog(dbCatalog);
	  }
      db.setDynamicQuerySupported(false);

      // create exporter
      if (DBUNIT_FORMAT.equalsIgnoreCase(format)) {
        exporter = new DbUnitEntityExporter(filename, encoding);
      } else if (XLS_FORMAT.equals(format)) {
        exporter = new XLSEntityExporter(filename);
      } else if (SQL_FORMAT.equals(format)) {
        if (dialect == null) {
          dialect = db.getDialect().getSystem();
        }
        exporter = new SQLEntityExporter(filename, encoding, lineSeparator, dialect);
      } else {
        throw new IllegalArgumentException("Unknown format: " + format);
      }

      // export data
      TypeDescriptor[] descriptors = db.getTypeDescriptors();
      logger.info("Starting export");
      for (TypeDescriptor descriptor : descriptors) {
        String note = "Exporting table " + descriptor.getName();
        if (monitor != null) {
          monitor.setNote(note);
          if (monitor.isCanceled()) {
            throw new RuntimeException("Export cancelled");
          }
        }
        logger.info(note);
        Thread.yield();
        DataIterator<Entity> source = db.queryEntities(descriptor.getName(), null, null).iterator();
        DataContainer<Entity> container = new DataContainer<>();
        ProductWrapper<Entity> wrapper = new ProductWrapper<>();
        while ((container = source.next(container)) != null) {
          Entity entity = container.getData();
          wrapper.wrap(entity);
          exporter.startConsuming(wrapper);
          wrapper.wrap(entity);
          exporter.finishConsuming(wrapper);
          count++;
        }
        if (monitor != null) {
          monitor.advance();
        }
      }
      long duration = System.currentTimeMillis() - startTime;
      if (count == 0) {
        logger.warn("No entities found for snapshot.");
      } else {
        logger.info("Exported " + NumberUtil.format(count, 0) + " entities in " +
            RoundedNumberFormat.format(duration, 0) + " ms " +
            "(" + RoundedNumberFormat.format(count * 3600000L / duration, 0) + " p.h.)");
      }
    } finally {
      IOUtil.close(exporter);
    }
  }

}

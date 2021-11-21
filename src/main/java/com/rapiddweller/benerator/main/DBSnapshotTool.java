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
import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
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

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * Creates a snapshot of a database schema and exports it in DbUnit XML file format.
 * @author Volker Bergmann
 * @since 0.3.04
 */
public class DBSnapshotTool {

  public static final String DBUNIT_FORMAT = "dbunit";
  public static final String XLS_FORMAT = "xls";
  public static final String SQL_FORMAT = "sql";

  public static final String DEFAULT_FORMAT = DBUNIT_FORMAT;

  public static final String DB_PASSWORD = "dbPassword";
  public static final String DB_URL = "dbUrl";
  public static final String DB_DRIVER = "dbDriver";
  public static final String DB_SCHEMA = "dbSchema";
  public static final String DB_CATALOG = "dbCatalog";
  public static final String DB_USER = "dbUser";
  public static final String FORMAT = "format";
  public static final String DIALECT = "dialect";

  // TODO test with each database
  private static final Logger logger = LoggerFactory.getLogger(DBSnapshotTool.class);

  public static String[] supportedFormats() {
    return new String[] {
        DBUNIT_FORMAT, XLS_FORMAT, SQL_FORMAT
    };
  }

  public static void main(String[] args) {
    logger.info("Starting {}", DBSnapshotTool.class.getSimpleName());
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
    String dbCatalog = System.getProperty(DB_CATALOG);
    String dbSchema = System.getProperty(DB_SCHEMA);
    String dialect = System.getProperty(DIALECT);

    logger.info("Exporting data of database '{}}' with driver '{}' as user '{}'{}'{} in {} format to file {}",
        dbUrl, dbDriver, dbUser, (dbSchema != null ? " using schema '" + dbSchema + "'" : ""),
        (dbCatalog != null ? " using catalog '" + dbCatalog + "'" : ""), format, filename);

	export(dbUrl, dbDriver, dbCatalog, dbSchema, dbUser, dbPassword, filename, format, dialect);
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

  public static void export(String dbUrl, String dbDriver, String dbCatalog, String dbSchema,
                            String dbUser, String dbPassword, String filename, String format, String dialect) {
	export(dbUrl, dbDriver, dbCatalog, dbSchema, dbUser, dbPassword, filename, SystemInfo.getFileEncoding(),
        format, dialect, null);
  }

  public static void export(String dbUrl, String dbDriver, String dbCatalog, String dbSchema,
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
          dialect = db.getDialect().getDbType();
        }
        exporter = new SQLEntityExporter(filename, dialect, lineSeparator, encoding);
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
            throw BeneratorExceptionFactory.getInstance().operationCancelled("Export cancelled");
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
      } else if (logger.isInfoEnabled()) {
        logger.info("Exported {} entities in {} ms ({} p.h.)", NumberUtil.format(count, 0),
            RoundedNumberFormat.format(duration, 0), RoundedNumberFormat.format(count * 3600000L / duration, 0));
      }
    } finally {
      IOUtil.close(exporter);
    }
  }

}

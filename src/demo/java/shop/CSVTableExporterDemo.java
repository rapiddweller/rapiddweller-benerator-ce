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

package shop;

import java.io.IOException;

import com.rapiddweller.benerator.StorageSystem;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.common.ReaderLineIterator;
import com.rapiddweller.model.data.DataModel;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.platform.csv.CSVEntityExporter;
import com.rapiddweller.platform.db.DBSystem;
import org.databene.webdecs.DataContainer;
import org.databene.webdecs.DataIterator;
import org.databene.webdecs.DataSource;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class CSVTableExporterDemo {

    private static final String JDBC_DRIVER = "org.hsqldb.jdbcDriver";
    private static final String JDBC_URL = "jdbc:hsqldb:mem:benerator";
    private static final String USER = "sa";
    private static final String PASSWORD = null;

    private static Logger logger =
            LogManager.getLogger(CSVTableExporterDemo.class);

    public static void main(String[] args) throws IOException {
        // first we create a table with some data to export
        DBSystem db = new DBSystem(null, JDBC_URL, JDBC_DRIVER, USER, PASSWORD,
                new DataModel());
        try {
            db.execute("create table db_data (" +
                    "    id   int," +
                    "    name varchar(30) NOT NULL," +
                    "    PRIMARY KEY  (id)" +
                    ")");
            db.execute("insert into db_data values (1, 'alpha')");
            db.execute("insert into db_data values (2, 'beta')");
            db.execute("insert into db_data values (3, 'gamma')");
            db.setFetchSize(100);
            // ...and then we export it
            exportTableAsCSV(db, "db_data.csv");
            logger.info("...done!");
            printFile("db_data.csv");
        } finally {
            db.execute("drop table db_data");
        }
    }

    private static void exportTableAsCSV(StorageSystem db, String filename) {
        DataSource<Entity> entities =
                (DataSource<Entity>) db.queryEntities("db_data", null, null);
        DataIterator<Entity> iterator = null;
        try {
            iterator = entities.iterator();
            DataContainer<Entity> container =
                    iterator.next(new DataContainer<Entity>());
            Entity cursor = container.getData();
            CSVEntityExporter exporter =
                    new CSVEntityExporter(filename, cursor.descriptor());
            try {
                logger.info("exporting data, please wait...");
                exporter.startProductConsumption(cursor);
                while ((container = iterator.next(container)) != null) {
                    exporter.startProductConsumption(container.getData());
                }
            } finally {
                exporter.close();
            }
        } finally {
            IOUtil.close(iterator);
        }
    }

    private static void printFile(String filename) throws IOException {
        System.out.println("Content of file " + filename + ":");
        ReaderLineIterator iterator = null;
        try {
            iterator = new ReaderLineIterator(IOUtil.getReaderForURI(filename));
            while (iterator.hasNext()) {
                System.out.println(iterator.next());
            }
        } finally {
            IOUtil.close(iterator);
        }
    }

}

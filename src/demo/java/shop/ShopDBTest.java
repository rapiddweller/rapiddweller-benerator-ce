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

import static org.junit.Assert.*;

import com.rapiddweller.benerator.engine.DefaultBeneratorContext;
import com.rapiddweller.benerator.main.Benerator;
import com.rapiddweller.benerator.parser.DefaultEntryConverter;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.common.Validator;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.platform.db.DBSystem;
import org.databene.webdecs.DataContainer;
import org.databene.webdecs.DataIterator;
import org.junit.Test;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.util.Map;

/**
 * Tests the shop demo on all supported database systems.<br/>
 * <br/>
 * Created: 20.11.2007 13:24:13
 */
public class ShopDBTest {

  private static final String BENERATOR_FILE = "demo/shop/shop.ben.xml";

  private static final Logger logger = LogManager.getLogger(ShopDBTest.class);

  /**
   * Test hsql in mem.
   *
   * @throws IOException          the io exception
   * @throws InterruptedException the interrupted exception
   */
/*
      public void testDB2() throws IOException, InterruptedException {
          checkGeneration("db2");
      }

      public void testDerby() throws IOException, InterruptedException {
          checkGeneration("derby");
      }
  */
  @Test
  public void testHSQLInMem() throws IOException, InterruptedException {
    checkGeneration("hsqlmem");
  }

  /**
   * Test firebird.
   *
   * @throws IOException          the io exception
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testFirebird() throws IOException, InterruptedException {
    checkGeneration("firebird");
  }
    /*
    public void testHSQL() throws IOException, InterruptedException {
        checkGeneration("hsql");
    }
    public void testSQLServer() throws IOException, InterruptedException {
        checkGeneration("ms_sql_server");
    }

    public void testMySQL() throws IOException, InterruptedException {
        checkGeneration("mysql");
    }

    public void testOracle() throws IOException, InterruptedException {
        checkGeneration("oracle");
    }

    public void testPostgres() throws IOException, InterruptedException {
        checkGeneration("postgres");
    }
*/
  // private helpers -------------------------------------------------------------------------------------------------

  private void checkGeneration(String database)
      throws IOException, InterruptedException {
    //checkGeneration(database, true);
    checkGeneration(database, "test", false);
  }

  private void checkGeneration(String database, String stage, boolean shell)
      throws IOException, InterruptedException {
    if (shell) {
      runFromCommandLine(BENERATOR_FILE, database, "test");
    } else {
      runAsClass(BENERATOR_FILE, database, "test");
    }
    // connect to database
    Map<String, String> dbCfg = IOUtil.readProperties(
        "demo/shop/" + database + "/shop." + database + ".properties");
    DefaultBeneratorContext context = new DefaultBeneratorContext();
    DBSystem db =
        new DBSystem("db", dbCfg.get("dbUri"), dbCfg.get("dbDriver"),
            dbCfg.get("dbUser"), dbCfg.get("dbPassword"),
            context.getDataModel());
    // check generation results
    Map<String, Object> genCfg =
        IOUtil.readProperties("demo/shop/shop." + stage + ".properties",
            new DefaultEntryConverter(context));
    int expectedProductCount = 6 + (Integer) genCfg.get("product_count");
    int expectedCustomerCount = 1 + (Integer) genCfg.get("customer_count");
    int expectedUserCount = 3 + expectedCustomerCount;
    int ordersPerCustomer = (Integer) genCfg.get("orders_per_customer");
    int expectedOrderCount =
        (expectedCustomerCount - 1) * ordersPerCustomer + 1;
    int itemsPerOrder = (Integer) genCfg.get("items_per_order");
    int expectedOrderItemCount =
        (expectedOrderCount - 1) * itemsPerOrder + 1;
    checkEntities("db_category", new CategoryValidator("db_category"), 28,
        db);
    checkEntities("db_product", new ProductValidator("db_product"),
        expectedProductCount, db);
    checkEntities("db_user", new UserValidator("db_user"),
        expectedUserCount, db);
    checkEntities("db_customer", new CustomerValidator("db_customer"),
        expectedCustomerCount, db);
    checkEntities("db_order", new OrderValidator("db_order"),
        expectedOrderCount, db);
    checkEntities("db_order_item", new OrderItemValidator("db_order_item"),
        expectedOrderItemCount, db);
  }

  private void checkEntities(String entityName, Validator<Entity> validator,
                             int expectedCount, DBSystem db) {
    assertEquals("Wrong number of '" + entityName + "' instances.",
        expectedCount, db.countEntities(entityName));
    DataIterator<Entity> iterator =
        db.queryEntities(entityName, null, null).iterator();
    DataContainer<Entity> container = new DataContainer<Entity>();
    while ((container = iterator.next(container)) != null) {
      Entity entity = container.getData();
      assertTrue("Invalid entity: " + entity, validator.valid(entity));
    }
  }

  private void runAsClass(String file, String database, String stage)
      throws IOException {
    System.setProperty("stage", stage);
    System.setProperty("database", database);
    Benerator.main(new String[] {file});
  }

  private void runFromCommandLine(String file, String database, String stage)
      throws IOException, InterruptedException {
    String command =
        "benerator -Ddatabase=" + database + " -Dstage=" + stage + " " +
            file;
    logger.debug(command);
    Process process = Runtime.getRuntime().exec(command);
    IOUtil.transfer(process.getInputStream(), System.out);
    process.waitFor();
    logger.debug(String.valueOf(process.exitValue()));
  }
}

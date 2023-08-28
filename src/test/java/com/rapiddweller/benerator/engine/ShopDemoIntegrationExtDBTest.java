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

package com.rapiddweller.benerator.engine;

import com.rapiddweller.benerator.test.AbstractBeneratorIntegrationTest;
import com.rapiddweller.common.ConfigUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static com.rapiddweller.common.SystemInfo.isLinux;
import static org.junit.Assume.assumeTrue;

/**
 * Integration test for Benerator's Demo Files.<br/><br/>
 * Created at 30.12.2020
 * @author Alexander Kell
 * @since 1.1.0
 */
public class ShopDemoIntegrationExtDBTest extends AbstractBeneratorIntegrationTest {

  @Before
  public void PrepareShopCtx() {
    context.setContextUri("/demo/shop");
  }

  /**
   * Demo postgres multi schema.
   */
  @Test
  public void DemoPostgresMultiSchema() {
    assumeTestActive("postgres");
    BeneratorContext benCtx = parseAndExecuteFile("/demo/shop/postgres.multischema.ben.xml");
    Assert.assertEquals("/demo/shop", benCtx.getContextUri());
  }

  /**
   * Demo postgres multi schema with table with same name and different columns and foreign key in different schema
   */
  @Test()
  public void DemoPostgresMultiSchemaDuplicatedTableInBenCtx() {
    assumeTestActive("postgres");
    BeneratorContext benCtx = parseAndExecuteFile("/demo/shop/postgres.multischema_duplicated_table.ben.xml");
    Assert.assertEquals("/demo/shop", benCtx.getContextUri());
  }

  /**
   * Demo Mssql Shop
   */
  @Test
  public void DemoMssqlShop() {
    assumeTestActive("mssql");
    BeneratorContext benCtx = parseAndExecuteFile("/demo/shop/shop-mssql.ben.xml");
    Assert.assertEquals("/demo/shop", benCtx.getContextUri());
  }

  /**
   * Demo Mysql Shop
   */
  @Test
  public void DemoMysqlShop() {
    assumeTestActive("mysql");
    BeneratorContext benCtx = parseAndExecuteFile("/demo/shop/shop-mysql.ben.xml");
    Assert.assertEquals("/demo/shop", benCtx.getContextUri());

  }

  /**
   * Demo postgres shop.
   */
  @Test
  public void DemoPostgresShop() {
    assumeTestActive("postgres");
    BeneratorContext benCtx = parseAndExecuteFile("/demo/shop/shop-postgres.ben.xml");
    Assert.assertEquals("/demo/shop", benCtx.getContextUri());
  }

  /**
   * Demo vertica shop.
   */
  @Test
  public void DemoVerticaShop() {
    assumeTestActive("vertica");
    BeneratorContext benCtx = parseAndExecuteFile("/demo/shop/shop-vertica.ben.xml");
    Assert.assertEquals("/demo/shop", benCtx.getContextUri());
  }

  /**
   * Demo oracle shop.
   */
  @Test
  public void DemoOracleShop() {
    assumeTestActive("oracle");
    BeneratorContext benCtx = parseAndExecuteFile("/demo/shop/shop-oracle.ben.xml");
    Assert.assertEquals("/demo/shop", benCtx.getContextUri());
  }

  /**
   * Demo mongodb shop.
   */
  @Test
  public void DemoMongoDBShop() {
    assumeTestActive("mongodb");
    BeneratorContext benCtx = parseAndExecuteFile("/demo/shop/shop-mongodb.ben.xml");
    Assert.assertEquals("/demo/shop", benCtx.getContextUri());
  }

  /**
   * Demo postgres and mongodb shop.
   */
  @Test
  public void DemoPostgresMongoDBShop() {
    assumeTestActive("postgres");
    assumeTestActive("mongodb");
    BeneratorContext benCtx = parseAndExecuteFile("/demo/shop/shop-postgres-mongodb.ben.xml");
    Assert.assertEquals("/demo/shop", benCtx.getContextUri());
  }


  /**
   * Debugging
   */
  @Ignore("for manual internal testing")
  @Test
  public void PostgresDebugging() {
    assumeTrue(isLinux());
    context.setContextUri("/demo/WIP");
    BeneratorContext benCtx = parseAndExecuteFile("/demo/WIP/benerator.xml");
    Assert.assertEquals("/demo/WIP", benCtx.getContextUri());
  }

}

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

package com.rapiddweller.platform.xls;

import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.DefaultBeneratorContext;
import com.rapiddweller.format.DataContainer;
import com.rapiddweller.format.util.DataUtil;
import com.rapiddweller.model.data.Entity;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Tests the {@link AllSheetsXLSEntityIterator} class.<br/>
 * <br/>
 * Created at 29.01.2009 11:06:33
 *
 * @author Volker Bergmann
 * @since 0.5.8
 */
public class SingleSheetXLSEntityIteratorTest extends XLSTest {

  private static final String IMPORT_XLS = "com/rapiddweller/platform/xls/import-multisheet.ent.xls";
  private static final String COUNTRY_XLS = "com/rapiddweller/platform/xls/country.ent.xls";

  private BeneratorContext context;

  /**
   * Sets up.
   */
  @Before
  public void setUp() {
    context = new DefaultBeneratorContext();
  }

  /**
   * Test import product sheet.
   *
   * @throws Exception the exception
   */
  @Test
  public void testImportProductSheet() throws Exception {
    try (SingleSheetXLSEntityIterator iterator = new SingleSheetXLSEntityIterator(IMPORT_XLS, "Product", null, null, context, true, false, null)) {
      assertProduct(PROD1, DataUtil.nextNotNullData(iterator));
      Entity next = DataUtil.nextNotNullData(iterator);
      assertProduct(PROD2, next);
      assertNull(iterator.next(new DataContainer<>()));
    }
  }

  /**
   * Test import person sheet.
   *
   * @throws Exception the exception
   */
  @Test
  public void testImportPersonSheet() throws Exception {
    try (SingleSheetXLSEntityIterator iterator = new SingleSheetXLSEntityIterator(IMPORT_XLS, "Person", null, null, context, true, false, null)) {
      assertPerson(PERSON1, DataUtil.nextNotNullData(iterator));
      assertNull(iterator.next(new DataContainer<>()));
    }
  }

  /**
   * Test parse all.
   *
   * @throws Exception the exception
   */
  @Test
  public void testParseAll() throws Exception {
    List<Entity> entities = SingleSheetXLSEntityIterator.parseAll(IMPORT_XLS, "Product", null, null, context, true, false, null);
    assertEquals(2, entities.size());
    assertProduct(PROD1, entities.get(0));
    assertProduct(PROD2, entities.get(1));
  }

  /**
   * Test collection mapping.
   *
   * @throws Exception the exception
   */
  @Test
  public void testCollectionMapping() throws Exception {
    try (SingleSheetXLSEntityIterator iterator = new SingleSheetXLSEntityIterator(COUNTRY_XLS, "country", null, null, context, true, false, null)) {
      // check germany
      Entity germany = DataUtil.nextNotNullData(iterator);
      assertEquals("DE", germany.get("id"));
      assertEquals("Germany", germany.get("name"));
      Entity[] states_de = (Entity[]) germany.get("states");
      assertEquals(2, states_de.length);
      Entity bayern = states_de[0];
      assertEquals("Bayern", bayern.get("name"));
      Entity[] cities_by = (Entity[]) bayern.get("cities");
      assertEquals(2, cities_by.length);
      assertEquals("Ingolstadt", cities_by[0].get("name"));
      assertEquals("Regensburg", cities_by[1].get("name"));

      Entity hessen = states_de[1];
      assertEquals("Hessen", hessen.get("name"));
      Entity[] cities_he = (Entity[]) hessen.get("cities");
      assertEquals(1, cities_he.length);
      assertEquals("Frankfurt", cities_he[0].get("name"));

      // check italy
      Entity it = DataUtil.nextNotNullData(iterator);
      Entity[] states_it = (Entity[]) it.get("states");
      assertEquals(2, states_it.length);
      Entity veneto = states_it[0];
      assertEquals("Veneto", veneto.get("name"));
      Entity[] cities_vr = (Entity[]) veneto.get("cities");
      assertEquals(2, cities_vr.length);
      assertEquals("Venezia", cities_vr[0].get("name"));
      assertEquals("Verona", cities_vr[1].get("name"));

      Entity lombardia = states_it[1];
      assertEquals("Lombardia", lombardia.get("name"));
      Entity[] cities_lo = (Entity[]) lombardia.get("cities");
      assertEquals(0, cities_lo.length);

      // assert endof data
      assertNull(iterator.next(new DataContainer<>()));
    }
  }

}

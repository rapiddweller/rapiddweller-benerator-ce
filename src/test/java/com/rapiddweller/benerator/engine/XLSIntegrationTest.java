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
import com.rapiddweller.benerator.test.ConsumerMock;
import com.rapiddweller.common.LocaleUtil;
import com.rapiddweller.common.TimeUtil;
import com.rapiddweller.model.data.Entity;
import org.junit.Test;

import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

/**
 * Testing XLS imports.<br/><br/>
 * Created: 24.01.2013 15:46:57
 * @author Volker Bergmann
 * @since 0.8.0
 */
public class XLSIntegrationTest extends AbstractBeneratorIntegrationTest {

  @SuppressWarnings("unchecked")
  @Test
  public void testDefault() {
    ConsumerMock con = new ConsumerMock(true);
    context.setGlobal("con", con);
    parseAndExecute("<iterate type='dummy' source='com/rapiddweller/benerator/engine/xls/types.xls' consumer='con'/>");
    List<Entity> products = (List<Entity>) con.getProducts();
    assertEquals(1, products.size());
    assertPersonValues("Alice", 123L, TimeUtil.date(2008, 11, 31), TimeUtil.date(2008, 11, 31, 13, 45, 0, 0), products.get(0));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testFormatted() {
    ConsumerMock con = new ConsumerMock(true);
    context.setGlobal("con", con);
    LocaleUtil.runInLocale(Locale.US,
        () -> parseAndExecute("<iterate type='dummy' source='com/rapiddweller/benerator/engine/xls/types.xls' format='formatted' consumer='con'/>"));
    List<Entity> products = (List<Entity>) con.getProducts();
    assertEquals(1, products.size());
    assertPersonValues("Alice", "123", "2008-Dec-31", "13:45", products.get(0));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testSheet() {
    ConsumerMock con = new ConsumerMock(true);
    context.setGlobal("con", con);
    parseAndExecute("<iterate type='dummy' segment='address' source='com/rapiddweller/benerator/engine/xls/sheets.xls' consumer='con'/>");
    List<Entity> products = (List<Entity>) con.getProducts();
    assertEquals(1, products.size());
    Entity address = products.get(0);
    assertEquals("Main Street", address.get("street"));
    assertEquals("New York", address.get("city"));
  }


  // private helper methods ------------------------------------------------------------------------------------------

  private static void assertPersonValues(Object name, Object number, Object date, Object time, Entity entity) {
    assertEquals(name, entity.get("name"));
    assertEquals(number, entity.get("number"));
    assertEquals(date, entity.get("a_date"));
    assertEquals(time, entity.get("a_time"));
  }

}

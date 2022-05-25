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

package com.rapiddweller.benerator.primitive.datetime;

import com.rapiddweller.benerator.test.AbstractBeneratorIntegrationTest;
import com.rapiddweller.benerator.test.ConsumerMock;
import com.rapiddweller.common.TimeUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests the correct interaction of XML parser,
 * Benerator engine and {@link DateTimeGenerator}.<br/><br/>
 * Created: 04.05.2010 06:13:08
 *
 * @author Volker Bergmann
 * @since 0.6.1
 */
public class DateTimeIntegrationTest extends AbstractBeneratorIntegrationTest {

  private static final Date MIN_DATE = TimeUtil.date(2008, 8, 29);
  private static final Date MAX_DATE = TimeUtil.date(2008, 9, 3);
  private static final int INDIVIDUAL_DATE_COUNT = 4;

  private ConsumerMock consumer;

  /**
   * Sets up context.
   */
  @Before
  public void setUpContext() {
    consumer = new ConsumerMock(true);
    context.setGlobal("cons", consumer);
  }


  // test methods ----------------------------------------------------------------------------------------------------

  /**
   * Test date with min max and granularity.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testDateWithMinMaxAndGranularity() {
    // create DateTimeGenerator from XML descriptor
    parseAndExecuteXmlString(
        "<generate type='entity' count='500' consumer='cons'>" +
            "  <value type='date' min='2008-09-29' max='2008-10-02' granularity='0000-00-01'/>" +
            "</generate>");
    List<Object[]> products = (List<Object[]>) consumer.getProducts();
    HashSet<Date> usedDates = new HashSet<>();
    for (Object[] product : products) {
      Date date = (Date) product[0];
      assertFalse(date.before(MIN_DATE));
      assertFalse(date.after(MAX_DATE));
      usedDates.add(date);
    }
    assertEquals(INDIVIDUAL_DATE_COUNT, usedDates.size());
  }

  /**
   * Test date with min and max.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testDateWithMinAndMax() {
    // create DateTimeGenerator from XML descriptor
    parseAndExecuteXmlString(
        "<generate type='entity' count='500' consumer='cons'>" +
            "  <value type='date' min='2008-09-29' max='2008-10-02' />" +
            "</generate>");
    List<Object[]> products = (List<Object[]>) consumer.getProducts();
    HashSet<Date> usedDates = new HashSet<>();
    for (Object[] product : products) {
      Date date = (Date) product[0];
      assertFalse(date.before(MIN_DATE));
      assertFalse(date.after(MAX_DATE));
      usedDates.add(date);
    }
    assertEquals(INDIVIDUAL_DATE_COUNT, usedDates.size());
  }

  /**
   * Test date with min.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testDateWithMin() {
    // create DateTimeGenerator from XML descriptor
    parseAndExecuteXmlString(
        "<generate type='entity' count='500' consumer='cons'>" +
            "  <value type='date' min='2008-09-29' />" +
            "</generate>");
    List<Object[]> products = (List<Object[]>) consumer.getProducts();
    HashSet<Date> usedDates = new HashSet<>();
    for (Object[] product : products) {
      Date date = (Date) product[0];
      assertFalse(date.before(MIN_DATE));
      usedDates.add(date);
    }
    assertTrue(usedDates.size() > 10);
  }

  /**
   * Test date with max.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testDateWithMax() {
    // create DateTimeGenerator from XML descriptor
    parseAndExecuteXmlString(
        "<generate type='entity' count='500' consumer='cons'>" +
            "  <value type='date' max='2008-10-02' />" +
            "</generate>");
    List<Object[]> products = (List<Object[]>) consumer.getProducts();
    HashSet<Date> usedDates = new HashSet<>();
    for (Object[] product : products) {
      Date date = (Date) product[0];
      assertFalse(date.after(MAX_DATE));
      usedDates.add(date);
    }
    assertTrue(usedDates.size() > 10);
  }

}

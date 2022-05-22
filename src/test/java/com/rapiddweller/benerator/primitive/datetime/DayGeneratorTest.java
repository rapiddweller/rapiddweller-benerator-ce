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

import com.rapiddweller.benerator.distribution.SequenceManager;
import com.rapiddweller.benerator.engine.DefaultBeneratorContext;
import com.rapiddweller.benerator.test.GeneratorClassTest;
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.common.TimeUtil;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * Tests the {@link DayGenerator}.<br/><br/>
 * Created: 12.10.2010 21:31:12
 * @author Volker Bergmann
 * @since 0.6.4
 */
public class DayGeneratorTest extends GeneratorClassTest {

  public DayGeneratorTest() {
    super(DayGenerator.class);
  }

  @Test
  public void testSetup() {
    Date minDate = TimeUtil.date(2010, 7, 6);
    Date maxDate = TimeUtil.date(2010, 8, 8);
    DayGenerator generator = new DayGenerator(
        minDate, maxDate, SequenceManager.STEP_SEQUENCE, false);
    generator.setGranularity("01-02-03");
    generator.init(context);
    assertEquals(minDate, generator.min);
    assertEquals(maxDate, generator.max);
    assertEquals(1, generator.yearGranularity);
    assertEquals(2, generator.monthGranularity);
    assertEquals(3, generator.dayGranularity);
  }

  @Test(expected = ConfigurationError.class)
  public void testIllegalSetup() {
    Date minDate = TimeUtil.date(2022, 3, 2);
    Date maxDate = TimeUtil.date(2022, 3, 1);
    DayGenerator g = new DayGenerator(minDate, maxDate, SequenceManager.STEP_SEQUENCE, false);
    g.init(new DefaultBeneratorContext());
  }

  @Test
  public void testNormalRange() {
    Date min = TimeUtil.date(2009, 2, 5);
    Date max = TimeUtil.date(2009, 4, 8);
    DayGenerator generator = new DayGenerator(min, max, SequenceManager.RANDOM_SEQUENCE, false);
    generator.init(context);
    for (int i = 0; i < 1000; i++) {
      Date day = generator.generate();
      assertNotNull(day);
      assertFalse(day.before(min));
      assertFalse(day.after(max));
      GregorianCalendar calendar = new GregorianCalendar();
      calendar.setTime(day);
      assertEquals(0, calendar.get(Calendar.MILLISECOND));
      assertEquals(0, calendar.get(Calendar.SECOND));
      assertEquals(0, calendar.get(Calendar.MINUTE));
      assertEquals(0, calendar.get(Calendar.HOUR));
    }
  }

  @Test
  public void testEmptyRange() {
    Date min = TimeUtil.date(2009, 2, 5);
    Date max = TimeUtil.date(2009, 2, 5);
    DayGenerator generator = new DayGenerator(min, max, SequenceManager.RANDOM_SEQUENCE, false);
    generator.init(context);
    for (int i = 0; i < 1000; i++) {
      Date day = generator.generate();
      assertNotNull(day);
      assertEquals(day, min);
    }
  }

  @Test
  public void testDateDistribution() {
    Date minDate = TimeUtil.date(2010, 7, 6);
    Date maxDate = TimeUtil.date(2010, 8, 8);
    DayGenerator generator = new DayGenerator(
        minDate, maxDate, SequenceManager.STEP_SEQUENCE, false);
    generator.init(context);
    for (int i = 0; i < 34; i++) {
      Date date = generator.generate();
      assertNotNull("Generator unavailable after " + i + " generations", date);
      assertFalse("Generated date " + date + " is before min date: " + minDate, date.before(minDate));
      assertFalse(date.after(maxDate));
    }
    assertUnavailable(generator);
  }

  @Test
  public void testIncrement() {
    Date minDate = TimeUtil.date(2022, 3, 1);
    Date maxDate = TimeUtil.date(2022, 3, 5);
    DayGenerator generator = new DayGenerator(minDate, maxDate, SequenceManager.INCREMENT_SEQUENCE, false);
    generator.init(context);
    for (int i = 0; i < 5; i++) {
      Date generatedDate = generator.generate();
      assertNotNull("Generator unavailable after " + i + " generations", generatedDate);
      Date expectedDate = TimeUtil.addDays(minDate, i);
      assertEquals(expectedDate, generatedDate);
    }
    assertUnavailable(generator);
  }

}

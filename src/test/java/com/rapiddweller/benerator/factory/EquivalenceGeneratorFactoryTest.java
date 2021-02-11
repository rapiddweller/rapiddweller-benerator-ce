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

package com.rapiddweller.benerator.factory;

import org.junit.Test;

import java.util.Date;

import static com.rapiddweller.common.Period.DAY;
import static com.rapiddweller.common.Period.HOUR;
import static com.rapiddweller.common.Period.MINUTE;
import static com.rapiddweller.common.TimeUtil.date;
import static org.junit.Assert.assertEquals;

/**
 * Tests the {@link EquivalenceGeneratorFactory}.<br/><br/>
 * Created: 29.08.2011 18:09:46
 *
 * @author Volker Bergmann
 * @since 0.7.0
 */
public class EquivalenceGeneratorFactoryTest {

  /**
   * Test mid date midnights.
   */
  @Test
  public void testMidDate_midnights() {
    checkMidDate(date(2011, 7, 29), date(2011, 8, 1), DAY.getMillis(), date(2011, 7, 30));
    checkMidDate(date(2011, 7, 20), date(2011, 7, 25), 2 * DAY.getMillis(), date(2011, 7, 22));
  }

  /**
   * Test mid date hours.
   */
  @Test
  public void testMidDate_hours() {
    checkMidDate(date(2011, 7, 29, 16, 0, 0, 0), date(2011, 7, 29, 17, 0, 0, 0), HOUR.getMillis() / 2, date(2011, 7, 29, 16, 30, 0, 0));
    checkMidDate(date(2011, 7, 29, 16, 0, 0, 0), date(2011, 7, 29, 17, 0, 0, 0), MINUTE.getMillis(), date(2011, 7, 29, 16, 30, 0, 0));
    checkMidDate(date(2011, 7, 29, 16, 0, 0, 0), date(2011, 7, 29, 17, 0, 0, 0), 20 * MINUTE.getMillis(), date(2011, 7, 29, 16, 20, 0, 0));
  }

  /**
   * Test mid date fractional days.
   */
  @Test
  public void testMidDate_fractionalDays() {
    checkMidDate(date(2011, 7, 29), date(2011, 7, 30), DAY.getMillis() / 2, date(2011, 7, 29, 12, 0, 0, 0));
    checkMidDate(date(2011, 7, 20), date(2011, 7, 30), DAY.getMillis() / 2, date(2011, 7, 25, 0, 0, 0, 0));
    checkMidDate(date(2011, 7, 20), date(2011, 7, 30), DAY.getMillis() * 3 / 2, date(2011, 7, 24, 12, 0, 0, 0));
  }

  private static void checkMidDate(Date min, Date max, long granularity, Date expected) {
    EquivalenceGeneratorFactory factory = new EquivalenceGeneratorFactory();
    Date result = factory.midDate(min, max, granularity);
    assertEquals(expected, result);
  }

}

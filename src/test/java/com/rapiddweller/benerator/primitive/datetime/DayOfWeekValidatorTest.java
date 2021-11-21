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

import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.common.BeanUtil;
import com.rapiddweller.common.Validator;
import org.junit.Test;

import javax.validation.Constraint;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertEquals;

/**
 * Tests the {@link DayOfWeekValidator}.<br/><br/>
 * Created at 26.09.2009 08:48:03
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class DayOfWeekValidatorTest {

  @Test
  public void testDefault() {
    check(new DayOfWeekValidator(), true, true, true, true, true, true, true);
  }

  @Test
  public void testWeekend() {
    DayOfWeekValidator validator = new DayOfWeekValidator();
    validator.setWeekdaysAccepted(false);
    check(validator, false, false, false, false, false, true, true);
  }

  @Test
  public void testWeekdays() {
    DayOfWeekValidator validator = new DayOfWeekValidator();
    validator.setWeekendsAccepted(false);
    check(validator, true, true, true, true, true, false, false);
  }

  @Test
  public void testExplicitly() {
    DayOfWeekValidator validator = new DayOfWeekValidator();
    validator.setDaysOfWeekAccepted(true, false, true, false, true, false, true);
    check(validator, true, false, true, false, true, false, true);
  }

  @Test
  public void testAnnotation() throws Exception {
    DayOfWeek validationAnnotation = Dummy.class.getField("date").getAnnotation(DayOfWeek.class);
    // instantiate validator from annotation info
    Constraint validatorAnnotation = DayOfWeek.class.getAnnotation(Constraint.class);
    Class<?>[] validatorClass = validatorAnnotation.validatedBy();
    DayOfWeekValidator validator = (DayOfWeekValidator) BeanUtil.newInstance(validatorClass[0]);
    validator.initialize(validationAnnotation);
    // test
    check(validator, true, false, true, false, false, false, false);
  }

  // private helper method -------------------------------------------------------------------------------------------

  private static void check(Validator<Date> validator,
                            boolean monday, boolean tuesday, boolean wednesday, boolean thursday, boolean friday,
                            boolean saturday, boolean sunday) {
    Calendar cal = new GregorianCalendar();
    for (int i = 0; i < 7; i++) {
      int javaDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
      boolean expectedResult;
      switch (javaDayOfWeek) {
        case Calendar.MONDAY:
          expectedResult = monday;
          break;
        case Calendar.TUESDAY:
          expectedResult = tuesday;
          break;
        case Calendar.WEDNESDAY:
          expectedResult = wednesday;
          break;
        case Calendar.THURSDAY:
          expectedResult = thursday;
          break;
        case Calendar.FRIDAY:
          expectedResult = friday;
          break;
        case Calendar.SATURDAY:
          expectedResult = saturday;
          break;
        case Calendar.SUNDAY:
          expectedResult = sunday;
          break;
        default:
          throw BeneratorExceptionFactory.getInstance().internalError("Not a supported day of week: " + javaDayOfWeek, null);
      }
      assertEquals("Check failed for " + cal, expectedResult, validator.valid(cal.getTime()));
      cal.add(Calendar.DATE, 1);
    }
  }

  static class Dummy {
    @DayOfWeek(daysOfWeekAccepted = {DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY})
    public Date date;
  }

}

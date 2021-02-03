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

import javax.validation.Constraint;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Calendar;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Bean validation annotation (JSR 303) based on the day of week.<br/><br/>
 * Created: 16.02.2010 22:50:57
 *
 * @author Volker Bergmann
 * @since 0.6.0
 */
@Documented
@Constraint(validatedBy = DayOfWeekValidator.class)
@Target({METHOD, FIELD, TYPE})
@Retention(RUNTIME)
public @interface DayOfWeek {

  /**
   * The constant MONDAY.
   */
  int MONDAY = Calendar.MONDAY;
  /**
   * The constant TUESDAY.
   */
  int TUESDAY = Calendar.TUESDAY;
  /**
   * The constant WEDNESDAY.
   */
  int WEDNESDAY = Calendar.WEDNESDAY;
  /**
   * The constant THURSDAY.
   */
  int THURSDAY = Calendar.THURSDAY;
  /**
   * The constant FRIDAY.
   */
  int FRIDAY = Calendar.FRIDAY;
  /**
   * The constant SATURDAY.
   */
  int SATURDAY = Calendar.SATURDAY;
  /**
   * The constant SUNDAY.
   */
  int SUNDAY = Calendar.SUNDAY;

  /**
   * Days of week accepted int [ ].
   *
   * @return the int [ ]
   */
  int[] daysOfWeekAccepted() default {MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY};
}

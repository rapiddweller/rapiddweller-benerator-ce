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

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.rapiddweller.benerator.distribution.SequenceManager;
import com.rapiddweller.benerator.test.GeneratorClassTest;
import com.rapiddweller.common.TimeUtil;
import com.rapiddweller.script.math.DateArithmetic;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests the {@link DateTimeGenerator}.
 * @since 0.1
 * @author Volker Bergmann
 */
public class DateTimeGeneratorTest extends GeneratorClassTest {
	
	final DateArithmetic arithmetic = new DateArithmetic();
	
    public DateTimeGeneratorTest() {
	    super(DateTimeGenerator.class);
    }

	static final int N = 100;

	@Test
    public void testInvalidSettings() {
        new DateTimeGenerator();
    }

	@Test
    public void testMinMax() {
        check(TimeUtil.date(1970, 0, 1), TimeUtil.date(1970, 0,  1), TimeUtil.time(12, 0), TimeUtil.time(12, 00));
        check(TimeUtil.date(1970, 0, 1), TimeUtil.date(1970, 0,  1), TimeUtil.time( 0, 0), TimeUtil.time(23, 59));
        check(TimeUtil.date(2008, 6, 5), TimeUtil.date(2008, 6, 25), TimeUtil.time( 9, 0), TimeUtil.time(17,  0));
    }

	@Test
    public void testDateDistribution() {
    	int minYear = 2008;
    	int maxYear = 2008;
    	int hour = 1;
    	int minute = 2;
    	int second = 3;
    	int millisecond = 4;
    	
        DateTimeGenerator generator = createGenerator(
        		TimeUtil.date(minYear, 7, 6), 
        		TimeUtil.date(maxYear, 8, 8),
        		TimeUtil.time(hour, minute, second, millisecond), 
        		TimeUtil.time(hour, minute, second, millisecond));
        Date minDate = TimeUtil.date(minYear, 7, 6, hour, minute, second, millisecond);
        Date maxDate = TimeUtil.date(maxYear, 8, 8, hour, minute, second, millisecond);
        generator.setDateDistribution(SequenceManager.STEP_SEQUENCE);
        generator.init(context);
        for (int i = 0; i < 34; i++) {
            Date date = generator.generate();
            assertNotNull("Generator unavailable after " + i + " generations", date);
            assertFalse("Generated date " + date + " is before min date: " + minDate, date.before(minDate));
            assertFalse(date.after(maxDate));
            Calendar cal = new GregorianCalendar();
            cal.setTime(date);
            assertEquals(hour, cal.get(Calendar.HOUR));
            assertEquals(minute, cal.get(Calendar.MINUTE));
            assertEquals(second, cal.get(Calendar.SECOND));
            assertEquals(millisecond, cal.get(Calendar.MILLISECOND));
        }
        assertUnavailable(generator);
    }

    // private helpers ---------------------------------------------------------
    
    private void check(Date minDate, Date maxDate, Time minTime, Time maxTime) {
        DateTimeGenerator generator = createGenerator(minDate, maxDate, minTime, maxTime);
        generator.init(context);
        Date maxResult = arithmetic.add(maxDate, maxTime);
        for (int i = 0; i < N; i++) {
            Date date = generator.generate();
            assertFalse("Generated date (" + date + ") is before minDate (" + minDate + ")", date.before(minDate));
            assertFalse(date.after(maxResult));
            Calendar cal = new GregorianCalendar();
            cal.setTime(date);
        }
    }

	private static DateTimeGenerator createGenerator(Date minDate, Date maxDate, Time minTime, Time maxTime) {
	    DateTimeGenerator generator = new DateTimeGenerator();
        generator.setMinDate(minDate);
        generator.setMaxDate(maxDate);
        generator.setMinTime(minTime);
        generator.setMaxTime(maxTime);
	    return generator;
    }
	
}

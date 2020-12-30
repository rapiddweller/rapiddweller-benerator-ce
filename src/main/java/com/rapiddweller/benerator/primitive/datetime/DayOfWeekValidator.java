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

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import javax.validation.ConstraintValidatorContext;

import com.rapiddweller.common.Assert;
import com.rapiddweller.common.TimeUtil;
import com.rapiddweller.common.validator.bean.AbstractConstraintValidator;

/**
 * Filters {@link Date}s by their day of week.
 * All days of the week are accepted by default. 
 * Attention: The weekday array begins with Monday (as defined in ISO_8601), 
 * not with Sunday (as used in {@link java.util.Calendar}).<br/>
 * <br/>
 * Created at 23.09.2009 17:51:52
 * @since 0.6.0
 * @author Volker Bergmann
 * @see <a href="http://en.wikipedia.org/wiki/ISO_8601#Week_dates">ISO 8601</a>
 */

public class DayOfWeekValidator extends AbstractConstraintValidator<DayOfWeek, Date> {
	
	/** 
	 * holds a flag for each weekday that tells if it is accepted. 
	 */
	private final boolean[] daysOfWeekAccepted;
	
    public DayOfWeekValidator() {
    	this.daysOfWeekAccepted = new boolean[7];
    	Arrays.fill(daysOfWeekAccepted, true);
    }
    
    // properties ------------------------------------------------------------------------------------------------------

    public void setDaysOfWeekAccepted(boolean... daysOfWeekAccepted) {
    	Assert.equals(7, daysOfWeekAccepted.length, getClass().getName() + ".day");
    	System.arraycopy(daysOfWeekAccepted, 0, this.daysOfWeekAccepted, 0, 7);
    }
    
    public void setWeekdaysAccepted(boolean weekdayAccepted) {
    	Arrays.fill(daysOfWeekAccepted, 0, 5, weekdayAccepted);
    }
    
    public void setWeekendsAccepted(boolean weekendAccepted) {
    	daysOfWeekAccepted[6] = weekendAccepted;
    	daysOfWeekAccepted[5] = weekendAccepted;
    }
    
    @Override
    public void initialize(DayOfWeek params) {
        Arrays.fill(daysOfWeekAccepted, false);
        for (int dayOfWeek : params.daysOfWeekAccepted())
        	daysOfWeekAccepted[isoDayOfWeek(dayOfWeek) - 1] = true;
    }
    
	@Override
	public boolean isValid(Date candidate, ConstraintValidatorContext ctx) {
	    int isoDayOfWeek = isoDayOfWeek(candidate);
		return daysOfWeekAccepted[isoDayOfWeek - 1];
    }

    static int isoDayOfWeek(Date candidate) {
	    Calendar calendar = TimeUtil.calendar(candidate);
		int javaDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		return isoDayOfWeek(javaDayOfWeek);
    }

    /** 
     * Calculates the day of the week (1=monday - 7=sunday) according to ISO 8601 
     * for the day of week returned by Calendar.get(DAY_OF_WEEK).
     */
	private static int isoDayOfWeek(int calendarDayOfWeek) {
	    return (calendarDayOfWeek == Calendar.SUNDAY ? 7 : calendarDayOfWeek - 1);
    }

}

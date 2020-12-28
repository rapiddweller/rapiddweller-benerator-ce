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

import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.distribution.Distribution;
import com.rapiddweller.benerator.distribution.SequenceManager;
import com.rapiddweller.benerator.wrapper.NonNullGeneratorWrapper;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.commons.Period;
import com.rapiddweller.commons.TimeUtil;
import com.rapiddweller.commons.converter.DateString2DurationConverter;

import java.util.Date;
import java.util.GregorianCalendar;

/**
 * creates date values by a LongGenerator.
 * <br/>
 * Created: 07.06.2006 22:54:28
 * @since 0.1
 * @author Volker Bergmann
 */
public class DateGenerator extends NonNullGeneratorWrapper<Long, Date> {
    
    private final DateString2DurationConverter dateConverter = new DateString2DurationConverter();

    private long min;
    private long max;
    private long granularity;
    private Distribution distribution;
    private final boolean unique;
    
    // constructors ----------------------------------------------------------------------------------------------------

    /** Initializes the generator to create days within about the last 80 years with a one-day resolution */
    public DateGenerator() {
        this(defaultStartDate(), currentDay(), Period.DAY.getMillis());
    }

    /** Initializes the generator to create dates with a uniform distribution */
    public DateGenerator(Date min, Date max, long granularity) {
        this(min, max, granularity, SequenceManager.RANDOM_SEQUENCE);
    }

    /** Initializes the generator to create dates of a Sequence or WeightFunction */
    public DateGenerator(Date min, Date max, long granularity, Distribution distribution) {
        this(min, max, granularity, distribution, false);
    }

    /** Initializes the generator to create dates of a Sequence or WeightFunction */
    public DateGenerator(Date min, Date max, long granularity, Distribution distribution, boolean unique) {
    	super(null);
        this.distribution = distribution;
		this.min = (min != null ? min.getTime() : Long.MIN_VALUE);
		this.max = (max != null ? max.getTime() : TimeUtil.date(TimeUtil.currentYear() + 10, 11, 31).getTime());
		this.granularity = granularity;
		this.unique = unique;
        setSource(distribution.createNumberGenerator(Long.class, this.min, this.max, this.granularity, this.unique));
    }

    // config properties -----------------------------------------------------------------------------------------------

    /** Sets the earliest date to generate */
    public void setMin(Date min) {
        this.min = min.getTime();
    }

    /** Sets the latest date to generate */
    public void setMax(Date max) {
        this.max = max.getTime();
    }

    /** Sets the date granularity in milliseconds */
    public void setGranularity(String granularity) {
        this.granularity = dateConverter.convert(granularity);
    }

    /** Sets the distribution to use */
    public void setDistribution(Distribution distribution) {
        this.distribution = distribution;
    }

    // source interface ---------------------------------------------------------------------------------------------

    @Override
	public Class<Date> getGeneratedType() {
        return Date.class;
    }

    @Override
    public void init(GeneratorContext context) {
    	assertNotInitialized();
    	setSource(distribution.createNumberGenerator(Long.class, min, max, granularity, unique));
		super.init(context);
    }

    /** Generates a Date by creating a millisecond value from the source generator and wrapping it into a Date */
	@Override
	public Date generate() {
    	assertInitialized();
        ProductWrapper<Long> tmp = generateFromSource();
        if (tmp == null)
        	return null;
		Long millis = tmp.unwrap();
        return new Date(millis);
    }
    
    @Override
    public boolean isThreadSafe() {
        return super.isThreadSafe() && dateConverter.isThreadSafe();
    }
    
    @Override
    public boolean isParallelizable() {
        return super.isParallelizable() && dateConverter.isParallelizable();
    }

    // implementation --------------------------------------------------------------------------------------------------

    /** Returns the default start date as 80 years ago */
    private static Date defaultStartDate() {
        return new Date(currentDay().getTime() - 80L * 365 * Period.DAY.getMillis());
    }

    /** Returns the current day as Date value rounded to midnight */
    private static Date currentDay() {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(
                calendar.get(GregorianCalendar.YEAR),
                calendar.get(GregorianCalendar.MONTH),
                calendar.get(GregorianCalendar.DAY_OF_MONTH),
                0,
                0,
                0);
        calendar.set(GregorianCalendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    @Override
    public String toString() {
    	return getClass().getSimpleName() + '[' + getSource() + ']';
    }

}

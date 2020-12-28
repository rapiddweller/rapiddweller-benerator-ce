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

import java.util.Calendar;
import java.util.Date;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.distribution.Distribution;
import com.rapiddweller.benerator.distribution.SequenceManager;
import com.rapiddweller.benerator.util.ThreadSafeNonNullGenerator;
import com.rapiddweller.benerator.util.WrapperProvider;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.commons.BeanUtil;
import com.rapiddweller.commons.ConfigurationError;
import com.rapiddweller.commons.TimeUtil;
import com.rapiddweller.model.data.Uniqueness;

/**
 * Generates dates with a granularity of days, months or years.<br/><br/>
 * Created: 12.10.2010 20:57:18
 * @since 0.6.4
 * @author Volker Bergmann
 */
public class DayGenerator extends ThreadSafeNonNullGenerator<Date> {

	protected Date min;
	protected Date max;
	protected Distribution distribution;
	protected boolean unique;
	
	protected int yearGranularity;
	protected int monthGranularity;
	protected int dayGranularity;
	
	private Calendar minCalendar;
	private Generator<Integer> multiplierGenerator;
	private final WrapperProvider<Integer> intWrapper = new WrapperProvider<>();

	public DayGenerator() {
		this(TimeUtil.date(TimeUtil.currentYear() - 5, 0, 1),
			TimeUtil.today(),
			SequenceManager.RANDOM_SEQUENCE,
			false);
	}

	public DayGenerator(Date min, Date max, Distribution distribution, boolean unique) {
	    this.min = min;
	    this.max = max;
	    this.distribution = distribution;
	    this.unique = unique;
	    this.yearGranularity = 0;
	    this.monthGranularity = 0;
	    this.dayGranularity = 1;
    }

	public void setMin(Date min) {
    	this.min = min;
    }

	public void setMax(Date max) {
    	this.max = max;
    }

	public void setGranularity(String granularitySpec) {
		String[] tokens = granularitySpec.split("-");
		if (tokens.length != 3)
			throw new ConfigurationError("Illegal date granularity spec: " + granularitySpec);
		this.yearGranularity = Integer.parseInt(tokens[0]);
		this.monthGranularity = Integer.parseInt(tokens[1]);
		this.dayGranularity = Integer.parseInt(tokens[2]);
	}
	
	public void setDistribution(Distribution distribution) {
    	this.distribution = distribution;
    }

	public void setUnique(boolean unique) {
		this.unique = unique;
	}
	
	@Override
	public Class<Date> getGeneratedType() {
	    return Date.class;
    }

	@Override
	public synchronized void init(GeneratorContext context) {
		this.minCalendar = TimeUtil.calendar(min);
		int count = 0;
		Calendar calendar = (Calendar) minCalendar.clone();
		do {
			calendar.add(Calendar.YEAR, yearGranularity);
			calendar.add(Calendar.MONTH, monthGranularity);
			calendar.add(Calendar.DAY_OF_MONTH, dayGranularity);
			count++;
		} while (!max.before(calendar.getTime()));
		multiplierGenerator = context.getGeneratorFactory().createNumberGenerator(
				Integer.class, 0, true, count - 1, true, 1, distribution, (unique ? Uniqueness.SIMPLE : Uniqueness.NONE));
		multiplierGenerator.init(context);
	    super.init(context);
	}
	
	@Override
	public Date generate() {
		assertInitialized();
		ProductWrapper<Integer> multiplierWrapper = multiplierGenerator.generate(intWrapper.get());
		if (multiplierWrapper == null)
			return null;
		int multiplier = multiplierWrapper.unwrap();
		Calendar calendar = (Calendar) minCalendar.clone();
		calendar.add(Calendar.YEAR,         multiplier * yearGranularity );
		calendar.add(Calendar.MONTH,        multiplier * monthGranularity);
		calendar.add(Calendar.DAY_OF_MONTH, multiplier * dayGranularity  );
		return calendar.getTime();
    }

	@Override
	public void reset() {
		multiplierGenerator.reset();
		super.reset();
	}
	
	@Override
	public void close() {
		multiplierGenerator.close();
		super.close();
	}
	
	@Override
	public String toString() {
		return BeanUtil.toString(this);
	}

}

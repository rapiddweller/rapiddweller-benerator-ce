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

package com.rapiddweller.benerator.distribution.sequence;

import java.math.BigDecimal;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.InvalidGeneratorSetupException;
import com.rapiddweller.benerator.NonNullGenerator;
import com.rapiddweller.benerator.distribution.Sequence;
import com.rapiddweller.benerator.wrapper.SkipGeneratorProxy;
import com.rapiddweller.benerator.wrapper.WrapperFactory;
import com.rapiddweller.commons.BeanUtil;
import com.rapiddweller.commons.ConfigurationError;
import com.rapiddweller.commons.MathUtil;
import com.rapiddweller.commons.NumberUtil;
import com.rapiddweller.commons.converter.NumberToNumberConverter;

import static com.rapiddweller.commons.NumberUtil.*;

/**
 * Random Walk {@link Sequence} implementation that supports a variable step width.<br/>
 * <br/>
 * Created at 30.06.2009 07:48:40
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class RandomWalkSequence extends Sequence {
	
	private static final BigDecimal ONE = BigDecimal.ONE;
	private static final BigDecimal MINUS_ONE = BigDecimal.ZERO.subtract(ONE);

	private static final boolean DEFAULT_BUFFERED = false;
	
	private BigDecimal initial;
	private BigDecimal minStep;
	private BigDecimal maxStep;
	private boolean buffered;
	
	// constructors ----------------------------------------------------------------------------------------------------

    public RandomWalkSequence() {
	    this(MINUS_ONE, ONE);
    }

    public RandomWalkSequence(BigDecimal minStep, BigDecimal maxStep) {
	    this(minStep, maxStep, null);
    }
    
    public RandomWalkSequence(BigDecimal minStep, BigDecimal maxStep, BigDecimal initial) {
	    this(minStep, maxStep, initial, DEFAULT_BUFFERED);
    }
    
    public RandomWalkSequence(BigDecimal minStep, BigDecimal maxStep, BigDecimal initial, boolean buffered) {
	    this.minStep = minStep;
	    this.maxStep = maxStep;
	    this.initial = initial;
	    this.buffered = buffered;
    }
    
	public void setMinStep(BigDecimal minStep) {
		this.minStep = minStep;
	}
	
	public void setMaxStep(BigDecimal maxStep) {
		this.maxStep = maxStep;
	}

	public void setInitial(BigDecimal initial) {
		this.initial = initial;
	}
	
	
	
    // Distribution interface implementation ---------------------------------------------------------------------------

    @Override
	public <T extends Number> NonNullGenerator<T> createNumberGenerator(Class<T> numberType, T min, T max, T granularity, boolean unique) {
    	if (max == null)
    		max = NumberUtil.maxValue(numberType);
    	NonNullGenerator<? extends Number> base;
		if (BeanUtil.isIntegralNumberType(numberType))
			base = createLongGenerator(toLong(min), toLong(max), toLong(granularity), unique);
		else
			base = createDoubleGenerator(toDouble(min), toDouble(max), toDouble(granularity), unique);
		return WrapperFactory.asNonNullNumberGeneratorOfType(numberType, base, min, granularity);
    }
    
    @Override
    public <T> Generator<T> applyTo(Generator<T> source, boolean unique) {
        if (buffered || MathUtil.between(0L, toLong(minStep), toLong(maxStep)))
        	return super.applyTo(source, unique);
        else
	        return applySkipGenerator(source, unique);
    }

	private <T> Generator<T> applySkipGenerator(Generator<T> source, boolean unique) {
		int minStepI = toInteger(minStep);
		if (unique && minStepI <= 0)
			throw new ConfigurationError("Cannot generate unique values when minStep=" + minStep);
	    return new SkipGeneratorProxy<T>(source, minStepI, toInteger(maxStep));
    }
    
    // helper methods --------------------------------------------------------------------------------------------------

	private <T> NonNullGenerator<? extends Number> createDoubleGenerator(double min, double max, double granularity, boolean unique) {
	    if (unique && MathUtil.rangeIncludes(0., min, max)) // check if uniqueness requirements can be met
	    	throw new InvalidGeneratorSetupException("Cannot guarantee uniqueness for [min=" + min + ",max=" + max + "]");
	    return new RandomWalkDoubleGenerator(
	    		toDouble(min), toDouble(max), toDouble(granularity), toDouble(minStep), toDouble(maxStep));
    }

	private <T> NonNullGenerator<? extends Number> createLongGenerator(long min, long max, long granularity, boolean unique) {
	    if (unique && MathUtil.rangeIncludes(0, min, max)) // check if uniqueness requirements can be met
	    	throw new InvalidGeneratorSetupException("Cannot guarantee uniqueness for [min=" + min + ",max=" + max + "]");
	    return new RandomWalkLongGenerator(
	    		min, max, toLong(granularity), toLong(initial(min, max, Long.class)), toLong(minStep), toLong(maxStep));
    }

    private <T extends Number> T initial(T min, T max, Class<T> numberType) {
    	if (initial != null)
    		return NumberToNumberConverter.convert(initial, numberType);
    	if (minStep.doubleValue() > 0)
    		return min;
		if (maxStep.doubleValue() > 0)
			return NumberToNumberConverter.convert((min.doubleValue() + max.doubleValue()) / 2, numberType);
		else
			return max;
    }

}

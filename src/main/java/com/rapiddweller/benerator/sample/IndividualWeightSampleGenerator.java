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

package com.rapiddweller.benerator.sample;

import java.util.ArrayList;
import java.util.List;

import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.WeightedGenerator;
import com.rapiddweller.benerator.distribution.AbstractWeightFunction;
import com.rapiddweller.benerator.distribution.IndividualWeight;
import com.rapiddweller.benerator.distribution.WeightFunction;
import com.rapiddweller.benerator.distribution.WeightedLongGenerator;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.common.Assert;

/**
 * Maps an {@link IndividualWeight} distribution to an {@link AbstractWeightFunction} and uses its capabilities
 * for providing distribution features based on the {@link IndividualWeight}'s characteristics.<br/>
 * <br/>
 * Created at 01.07.2009 11:48:23
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class IndividualWeightSampleGenerator<E> extends AbstractSampleGenerator<E> implements WeightedGenerator<E> {
	
    /** Keeps the Sample information */
    final List<E> samples = new ArrayList<>();
    
    final IndividualWeight<E> individualWeight;
    
	private double totalWeight;

    /** Generator for choosing a List index of the sample list */
    private WeightedLongGenerator indexGenerator;

    // constructors ----------------------------------------------------------------------------------------------------

    /** Initializes the generator to an unweighted sample list */
    @SafeVarargs
    public IndividualWeightSampleGenerator(Class<E> generatedType, IndividualWeight<E> individualWeight, E ... values) {
    	super(generatedType);
    	Assert.notNull(individualWeight, "individualWeight");
        this.individualWeight = individualWeight;
        setValues(values);
    }

    /** Initializes the generator to an unweighted sample list */
    public IndividualWeightSampleGenerator(Class<E> generatedType, IndividualWeight<E> individualWeight, Iterable<E> values) {
    	super(generatedType);
    	Assert.notNull(individualWeight, "individualWeight");
        this.individualWeight = individualWeight;
        setValues(values);
    }

    // samples property ------------------------------------------------------------------------------------------------

    /** Sets the sample list to the specified weighted values */
    @SafeVarargs
    public final void setSamples(E... samples) {
        this.samples.clear();
        for (E sample : samples)
            addValue(sample);
    }

    // values property -------------------------------------------------------------------------------------------------

    /** Adds an unweighted value to the sample list */
    @Override
    public <T extends E> void addValue(T value) {
        samples.add(value);
        this.totalWeight += individualWeight.weight(value);
    }

	@Override
	public long getVariety() {
		return samples.size();
	}
	
	@Override
	public double getWeight() {
		return totalWeight;
	}

    @Override
    public void clear() {
    	this.samples.clear();
    }
    
    // Generator implementation ----------------------------------------------------------------------------------------

    /** Initializes all attributes */
    @Override
    public void init(GeneratorContext context) {
    	assertNotInitialized();
        indexGenerator = new WeightedLongGenerator(0, samples.size() - 1, 1, new SampleWeightFunction());
        indexGenerator.init(context);
        super.init(context);
    }

	@Override
	public ProductWrapper<E> generate(ProductWrapper<E> wrapper) {
        assertInitialized();
        if (samples.size() == 0)
            return null;
        int index = indexGenerator.generate().intValue();
        return wrapper.wrap(samples.get(index));
    }

    // implementation --------------------------------------------------------------------------------------------------

    /** Weight function that evaluates the weights that are stored in the sample list. */
    class SampleWeightFunction extends AbstractWeightFunction {
    	
        /** @see WeightFunction#value(double) */
        @Override
		public double value(double param) {
            return individualWeight.weight(samples.get((int) param));
        }
        
        /** creates a String representation */
        @Override
        public String toString() {
            return getClass().getSimpleName();
        }
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}
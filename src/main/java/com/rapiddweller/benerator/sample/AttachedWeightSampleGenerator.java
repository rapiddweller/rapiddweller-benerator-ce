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

import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.WeightedGenerator;
import com.rapiddweller.benerator.distribution.AbstractWeightFunction;
import com.rapiddweller.benerator.distribution.WeightFunction;
import com.rapiddweller.benerator.distribution.WeightedLongGenerator;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.commons.NullSafeComparator;
import com.rapiddweller.script.WeightedSample;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

/**
 * Generates values from a weighted or non-weighted set of samples.<br/>
 * <br/>
 * Created: 07.06.2006 19:04:08
 * @since 0.1
 * @author Volker Bergmann
 */
public class AttachedWeightSampleGenerator<E> extends AbstractSampleGenerator<E> implements WeightedGenerator<E> { 
	
    /** Keeps the Sample information */
    List<WeightedSample<? extends E>> samples = new ArrayList<>();
    
    /** Generator for choosing a List index of the sample list */
    private final WeightedLongGenerator indexGenerator = new WeightedLongGenerator(0, 0, 1, new SampleWeightFunction());
    
    private double totalWeight;

    // constructors ----------------------------------------------------------------------------------------------------

    /** Initializes the generator to an empty sample list */
    public AttachedWeightSampleGenerator(Class<E> generatedType) {
        this(generatedType, (E[]) null);
    }

    /** Initializes the generator to an unweighted sample list */
    @SafeVarargs
    public AttachedWeightSampleGenerator(Class<E> generatedType, E ... values) {
    	super(generatedType);
        setValues(values);
    }

    /** Initializes the generator to an unweighted sample list */
    public AttachedWeightSampleGenerator(Class<E> generatedType, Iterable<E> values) {
    	super(generatedType);
        setValues(values);
    }

    // samples property ------------------------------------------------------------------------------------------------

    /** returns the sample list */
    public List<WeightedSample<? extends E>> getSamples() {
        return samples;
    }

    /** Sets the sample list to the specified weighted values */
    @SafeVarargs
    public final void setSamples(WeightedSample<? extends E>... samples) {
        this.samples.clear();
        for (WeightedSample<? extends E> sample : samples)
            addSample(sample);
    }

    /** Adds weighted values to the sample list */
    public void setSamples(Collection<WeightedSample<E>> samples) {
        this.samples.clear();
        if (samples != null)
            for (WeightedSample<E> sample : samples)
            	addSample(sample);
    }

    /** Adds weighted values to the sample list */
    public <T extends E> void addSample(T value, double weight) {
        addSample(new WeightedSample<E>(value, weight));
        totalWeight += weight;
    }

    /** Adds a weighted value to the sample list */
    public void addSample(WeightedSample<? extends E> sample) {
        samples.add(sample);
        totalWeight += sample.getWeight();
    }

    // values property -------------------------------------------------------------------------------------------------

    /** Adds an unweighted value to the sample list */
    @Override
    public <T extends E> void addValue(T value) {
        samples.add(new WeightedSample<E>(value, 1));
        totalWeight += 1;
    }

    @Override
    public void clear() {
    	this.samples.clear();
    }
    
    // Generator implementation ----------------------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    @Override
    public Class<E> getGeneratedType() {
    	if (samples.size() == 0)
    		return (Class<E>) String.class;
        return (Class<E>) samples.get(0).getClass();
    }

    /** Initializes all attributes */
    @Override
    public void init(GeneratorContext context) {
        normalize();
        if (samples.size() > 0) {
	        indexGenerator.setMax((long) (samples.size() - 1));
	        indexGenerator.init(context);
        }
        super.init(context);
    }

    @Override
	public ProductWrapper<E> generate(ProductWrapper<E> wrapper) {
    	assertInitialized();
        if (samples.size() == 0)
            return null;
        int index = indexGenerator.generate().intValue();
        WeightedSample<? extends E> sample = samples.get(index);
        return wrapper.wrap(sample.getValue());
    }

    // implementation --------------------------------------------------------------------------------------------------

    /** normalizes the sample weights to a sum of 1 */
    private void normalize() {
    	if (totalWeight == 0) {
	        for (WeightedSample<? extends E> sample : samples)
	        	sample.setWeight(1);
	        totalWeight = samples.size();
    	}
    }

	@Override
	public double getWeight() {
		return totalWeight;
	}

	@Override
	public long getVariety() {
		return samples.size();
	}
    
	public boolean containsSample(E searchedValue) {
		for (WeightedSample<? extends E> sample : samples)
			if (NullSafeComparator.equals(searchedValue, sample.getValue()))
				return true;
		return false;
	}

    /** Weight function that evaluates the weights that are stored in the sample list. */

    class SampleWeightFunction extends AbstractWeightFunction {

        /** @see WeightFunction#value(double) */
        @Override
		public double value(double param) {
            return samples.get((int) param).getWeight();
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

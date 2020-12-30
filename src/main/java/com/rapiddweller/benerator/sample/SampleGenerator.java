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
import com.rapiddweller.benerator.InvalidGeneratorSetupException;
import com.rapiddweller.benerator.NonNullGenerator;
import com.rapiddweller.benerator.distribution.Distribution;
import com.rapiddweller.benerator.distribution.SequenceManager;
import com.rapiddweller.benerator.util.RandomUtil;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.common.ConfigurationError;

import java.util.List;
import java.util.ArrayList;

/**
 * Generates values from a non-weighted list of samples, applying an explicitly defined distribution.<br/>
 * <br/>
 * Created: 07.06.2006 19:04:08
 * @since 0.1
 * @author Volker Bergmann
 */
public class SampleGenerator<E> extends AbstractSampleGenerator<E> {

    /** Keeps the Sample information */
    private final List<E> samples = new ArrayList<>();

    /** Sequence for choosing a List index of the sample list */
    private final Distribution distribution;

    /** Sequence for choosing a List index of the sample list */
    private NonNullGenerator<Integer> indexGenerator = null;
    
    private boolean unique;

    // constructors ----------------------------------------------------------------------------------------------------

    public SampleGenerator() {
        this(null);
    }

    /** Initializes the generator to an empty sample list */
    public SampleGenerator(Class<E> generatedType) {
        this(generatedType, new ArrayList<>());
    }

    /** Initializes the generator to a sample list */
    @SafeVarargs
    public SampleGenerator(Class<E> generatedType, E ... values) {
    	super(generatedType);
        setValues(values);
        this.distribution = SequenceManager.RANDOM_SEQUENCE;
    }

    /** Initializes the generator to a sample list */
    @SafeVarargs
    public SampleGenerator(Class<E> generatedType, Distribution distribution, E ... values) {
    	super(generatedType);
        this.distribution = distribution;
        setValues(values);
    }

    /** Initializes the generator to a sample list */
    public SampleGenerator(Class<E> generatedType, Iterable<E> values) {
    	super(generatedType);
        setValues(values);
        this.distribution = SequenceManager.RANDOM_SEQUENCE;
    }

    /** Initializes the generator to a sample list */
    public SampleGenerator(Class<E> generatedType, Distribution distribution, boolean unique, Iterable<E> values) {
    	super(generatedType);
        this.distribution = distribution;
        this.unique = unique;
        setValues(values);
    }

    // properties ------------------------------------------------------------------------------------------------------

    /** Adds a value to the sample list */
    @Override
    public <T extends E> void addValue(T value) {
    	if (unique && this.contains(value))
    		throw new ConfigurationError("Trying to add a duplicate value (" + value + ") " +
    				"to unique generator: " + this);
    	samples.add(value);
    }

    public boolean isUnique() {
    	return unique;
    }

	public void setUnique(boolean unique) {
    	this.unique = unique;
    }
	
	public <T extends E> boolean contains(E value) {
		return samples.contains(value);
	}

	@Override
    public void clear() {
    	this.samples.clear();
    }
    
    @Override
	public long getVariety() {
    	return samples.size();
    }
    
    // Generator implementation ----------------------------------------------------------------------------------------

	/** Initializes all attributes */
    @Override
    public void init(GeneratorContext context) {
    	assertNotInitialized();
        if (samples.size() == 0) 
        	throw new InvalidGeneratorSetupException("No samples defined in " + this);
        else {
        	indexGenerator = distribution.createNumberGenerator(Integer.class, 0, samples.size() - 1, 1, unique);
        	indexGenerator.init(context);
        }
        super.init(context);
    }

	@Override
	public ProductWrapper<E> generate(ProductWrapper<E> wrapper) {
        assertInitialized();
        Integer index;
        if (samples.size() > 0 && (index = indexGenerator.generate()) != null)
        	return wrapper.wrap(samples.get(index));
        else
            return null;
    }
    
    @Override
    public void reset() {
    	indexGenerator.reset();
    	super.reset();
    }
    
    @Override
    public void close() {
    	indexGenerator.close();
    	super.close();
    }

    // static interface ------------------------------------------------------------------------------------------------

    /** Convenience utility method that chooses one sample out of a list with uniform random distribution */
    @SafeVarargs
    public static <T> T generate(T ... samples) {
        return samples[RandomUtil.randomInt(0, samples.length - 1)];
    }

    /** Convenience utility method that chooses one sample out of a list with uniform random distribution */
    public static <T> T generate(List<T> samples) {
        return samples.get(RandomUtil.randomInt(0, samples.size() - 1));
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}

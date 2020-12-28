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

package com.rapiddweller.benerator.wrapper;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.NonNullGenerator;
import com.rapiddweller.benerator.distribution.Distribution;
import com.rapiddweller.benerator.distribution.SequenceManager;
import com.rapiddweller.benerator.util.WrapperProvider;

/**
 * Combines a a random number a source generator's products into a collection.<br/>
 * <br/>
 * Created: 06.03.2008 16:08:22
 * @author Volker Bergmann
 */
public abstract class CardinalGenerator<S, P> extends GeneratorWrapper<S, P> {

    /** Generator that determines the cardinality of generation */
    protected NonNullGenerator<Integer> cardinalGenerator;
    final boolean resettingCardinal;
    
    int minCardinal;
    int maxCardinal;
    int cardinalGranularity;
    Distribution cardinalDistribution;
    final WrapperProvider<Integer> cardinalWrapperProvider = new WrapperProvider<>();

    // constructors ----------------------------------------------------------------------------------------------------

    public CardinalGenerator(Generator<S> source, boolean resettingCardinal, NonNullGenerator<Integer> cardinalGenerator) {
        super(source);
        this.cardinalGenerator = cardinalGenerator;
        this.resettingCardinal = resettingCardinal;
    }
    
    public CardinalGenerator(Generator<S> source, boolean resettingCardinalGenerator) {
        this(source, resettingCardinalGenerator, 0, 30, 1, SequenceManager.RANDOM_SEQUENCE);
    }

    public CardinalGenerator(Generator<S> source, boolean resettingCardinalGenerator, 
    		int minCardinal, int maxCardinal, int cardinalGranularity, Distribution cardinalDistribution) {
        super(source);
        this.minCardinal = minCardinal;
        this.maxCardinal = maxCardinal;
        this.cardinalGranularity = cardinalGranularity;
        this.cardinalDistribution = (cardinalDistribution != null ? cardinalDistribution : SequenceManager.RANDOM_SEQUENCE);
        this.resettingCardinal = resettingCardinalGenerator;
    }
    
    // Generator interface ---------------------------------------------------------------------------------------------

	/** ensures consistency of the state */
    @Override
    public void init(GeneratorContext context) {
    	if (cardinalGenerator == null)
    		cardinalGenerator = cardinalDistribution.createNumberGenerator(Integer.class, minCardinal, maxCardinal, cardinalGranularity, false);
        cardinalGenerator.init(context);
        super.init(context);
    }

    @Override
    public void reset() {
    	assertInitialized();
    	if (resettingCardinal)
    		cardinalGenerator.reset();
        super.reset();
    }
    
    // helpers ---------------------------------------------------------------------------------------------------------
    
    protected Integer generateCardinal() {
    	ProductWrapper<Integer> wrapper = generateCardinalWrapper();
    	if (wrapper == null)
    		return null;
    	return wrapper.unwrap();
    }

	protected ProductWrapper<Integer> generateCardinalWrapper() {
		return cardinalGenerator.generate(cardinalWrapperProvider.get());
	}
    
}

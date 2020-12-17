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

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.NonNullGenerator;
import com.rapiddweller.benerator.distribution.Distribution;
import com.rapiddweller.benerator.distribution.Sequence;
import com.rapiddweller.benerator.distribution.SequenceManager;
import com.rapiddweller.benerator.wrapper.RepeatGeneratorProxy;
import com.rapiddweller.benerator.wrapper.WrapperFactory;
import com.rapiddweller.commons.ConfigurationError;

/**
 * Distribution that repeats consecutive elements or numbers.<br/>
 * <br/>
 * Created at 01.07.2009 15:40:02
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class RepeatSequence extends Sequence {

    private int minRepetitions;
    private int maxRepetitions;
    private int repetitionGranularity;
    private Distribution repetitionDistribution;

	private final static Distribution stepSequence = new StepSequence();

	public RepeatSequence() {
		this(0, 3);
	}
    
	public RepeatSequence(int minRepetitions, int maxRepetitions) {
		this(minRepetitions, maxRepetitions, 1, SequenceManager.RANDOM_SEQUENCE);
	}
	
	public RepeatSequence(int minRepetitions, int maxRepetitions, int repetitionGranularity,
			Distribution repetitionDistribution) {
	    this.minRepetitions = minRepetitions;
	    this.maxRepetitions = maxRepetitions;
	    this.repetitionGranularity = repetitionGranularity;
	    this.repetitionDistribution = repetitionDistribution;
    }

    @Override
	public <T extends Number> NonNullGenerator<T> createNumberGenerator(
    		Class<T> numberType, T min, T max, T granularity, boolean unique) {
    	if (minRepetitions > maxRepetitions)
    		throw new ConfigurationError("minRepetitions (" + minRepetitions + ") > " +
    				"maxRepetitions (" + maxRepetitions + ")");
    	if (unique && (minRepetitions > 0 || maxRepetitions > 0))
    		throw new ConfigurationError("Uniqueness can't be assured for minRepetitions=" + minRepetitions
    				+ " and maxRepetitions=" + maxRepetitions);
		Generator<T> source = stepSequence.createNumberGenerator(numberType, min, max, granularity, unique);
		return WrapperFactory.asNonNullGenerator(applyTo(source, unique));
	}
	
    @Override
	public <T> Generator<T> applyTo(Generator<T> source, boolean unique) {
	    return new RepeatGeneratorProxy<T>(source, minRepetitions, maxRepetitions, 
	    		repetitionGranularity, repetitionDistribution);
	}
	
}

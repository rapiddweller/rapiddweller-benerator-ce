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
import com.rapiddweller.benerator.NonNullGenerator;
import com.rapiddweller.benerator.distribution.Distribution;

/**
 * Abstract parent class for {@link Generator}s that generate objects of a variable length.<br/><br/>
 * Created: 01.08.2011 11:34:58
 * @since 0.7.0
 * @author Volker Bergmann
 */
public abstract class LengthGenerator<S, P> extends CardinalGenerator<S, P> {

	public LengthGenerator(Generator<S> source, boolean resettingLengthGenerator) {
		super(source, resettingLengthGenerator);
	}

	public LengthGenerator(Generator<S> source, boolean resettingLength, NonNullGenerator<Integer> lengthGenerator) {
		super(source, resettingLength, lengthGenerator);
	}

	public LengthGenerator(Generator<S> source,
			boolean resettingLengthGenerator, int minLength, int maxLength,
			int lengthGranularity, Distribution lengthDistribution) {
		super(source, resettingLengthGenerator, minLength, maxLength, lengthGranularity, lengthDistribution);
	}

	public int getMinLength() {
		return minCardinal;
	}
	
	public void setMinLength(int minLength) {
		this.minCardinal = minLength;
	}
	
	public int getMaxLength() {
		return maxCardinal;
	}
	
	public void setMaxLength(int maxLength) {
		this.maxCardinal = maxLength;
	}
	
	public int getLengthGranularity() {
		return cardinalGranularity;
	}
	
	public void setLengthGranularity(int lengthGranularity) {
		this.cardinalGranularity = lengthGranularity;
	}
	
	public Distribution getLengthDistribution() {
		return cardinalDistribution;
	}
	
	public void setLengthDistribution(Distribution lengthDistribution) {
		this.cardinalDistribution = lengthDistribution;
	}
	
}

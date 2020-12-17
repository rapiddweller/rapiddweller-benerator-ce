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

import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.primitive.number.AbstractNonNullNumberGenerator;

/**
 * Double Generator that implements a 'step' Double Sequence.<br/>
 * <br/>
 * Created: 26.07.2007 18:36:45
 * @author Volker Bergmann
 */
public class StepDoubleGenerator extends AbstractNonNullNumberGenerator<Double> {

	private double increment;
	private double initial;
	
    private double next;

    // constructors ----------------------------------------------------------------------------------------------------

    public StepDoubleGenerator() {
        this(Double.MIN_VALUE, Double.MAX_VALUE);
    }

    public StepDoubleGenerator(double min, double max) {
        this(min, max, 1., null);
    }

    public StepDoubleGenerator(double min, double max, double increment) {
        this(min, max, increment, null);
    }

    public StepDoubleGenerator(double min, Double max, double increment, Double initial) {
        super(Double.class, min, max, Math.abs(increment));
        this.increment = increment;
        this.initial = (initial != null ? initial : (increment >= 0 ? min : max));
        reset();
    }

    // properties ------------------------------------------------------------------------------------------------------

    @Override
	public void init(GeneratorContext context) {
    	assertNotInitialized();
        resetMembers();
        super.init(context);
    }

	@Override
	public Double generate() {
    	assertInitialized();
    	if (increment == 0 || (increment > 0 && (max == null || next <= max)) || (increment < 0 && next >= min)) {
	        double value = next;
	        next += increment;
	        return value;
    	} else
    		return null;
    }

	@Override
	public synchronized void reset() {
		resetMembers();
	}

	private void resetMembers() {
	    next = initial;
    }

}

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
 * Long Generator that implements a 'step' Long Sequence.<br/>
 * <br/>
 * Created: 26.07.2007 18:36:45
 * @author Volker Bergmann
 */
public class StepLongGenerator extends AbstractNonNullNumberGenerator<Long> {

	private long increment;
	private long initial;
	
    private long next;

    // constructors ----------------------------------------------------------------------------------------------------

    public StepLongGenerator() {
        this(Long.MIN_VALUE, Long.MAX_VALUE);
    }

    public StepLongGenerator(long min, long max) {
        this(min, max, 1);
    }

    public StepLongGenerator(long min, long max, long increment) {
        this(min, max, increment, null);
    }

    public StepLongGenerator(long min, Long max, long increment, Long initial) {
        super(Long.class, min, max, Math.abs(increment));
        this.increment = increment;
        this.initial = (initial != null ? initial : (increment >= 0 ? min : max));
        reset();
    }
    
    @Override
    public void setGranularity(Long granularity) {
        super.setGranularity(granularity);
        this.increment = granularity;
    }

    // Generator implementation ----------------------------------------------------------------------------------------

    @Override
	public void init(GeneratorContext context) {
        reset();
		super.init(context);
    }

	@Override
	public synchronized Long generate() {
        if ((increment == 0 || (increment > 0 && (max == null || next <= max)) || (increment < 0 && next >= min))) {
        	long value = next;
		    next += increment;
	        return value;
        } else
        	return null;
    }

    @Override
	public synchronized void reset() {
		next = initial;
	}

}

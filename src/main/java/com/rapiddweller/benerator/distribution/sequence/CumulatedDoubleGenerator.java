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
import com.rapiddweller.benerator.InvalidGeneratorSetupException;
import com.rapiddweller.benerator.primitive.number.AbstractNonNullNumberGenerator;

/**
 * Double Generator that implements a 'cumulated' Double Sequence.<br/>
 * Created: 07.06.2006 19:33:37<br/>
 * @since 0.1
 * @author Volker Bergmann
 */
public class CumulatedDoubleGenerator extends AbstractNonNullNumberGenerator<Double> {
	
    RandomDoubleGenerator baseGen;

    public CumulatedDoubleGenerator() {
        this(Long.MIN_VALUE, Long.MAX_VALUE);
    }

    public CumulatedDoubleGenerator(double min, double max) {
        this(min, max, 1);
    }

    public CumulatedDoubleGenerator(double min, double max, double granularity) {
        super(Double.class, min, max, granularity);
    }
    
    @Override
    public void init(GeneratorContext context) {
    	if (granularity == 0.)
    		throw new InvalidGeneratorSetupException(getClass().getSimpleName() + ".granularity may not be 0");
        super.init(context);
        baseGen = new RandomDoubleGenerator(min, max, granularity);
        baseGen.init(context);
    }

	@Override
	public Double generate() {
    	assertInitialized();
        double exactValue = (baseGen.generate() + baseGen.generate() + baseGen.generate() + 
        		baseGen.generate() + baseGen.generate()) / 5.;
        return min + (int)(Math.round((exactValue - min) / granularity)) * granularity;
    }

}

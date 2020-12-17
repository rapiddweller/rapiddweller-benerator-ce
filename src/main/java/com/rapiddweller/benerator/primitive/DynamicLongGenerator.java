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

package com.rapiddweller.benerator.primitive;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.distribution.Distribution;
import com.rapiddweller.benerator.distribution.SequenceManager;
import com.rapiddweller.benerator.wrapper.GeneratorProxy;
import com.rapiddweller.script.Expression;
import com.rapiddweller.script.expression.ExpressionUtil;

/**
 * {@link Generator} implementation that generates {@link Long} numbers, 
 * redefining the underlying distribution on each <code>reset()</code> by
 * evaluating the <code>min</code>, <code>max</code>, <code>granularity</code>,
 * <code>distribution</code> and <code>unique</code> values.<br/><br/>
 * Created: 27.03.2010 19:28:38
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class DynamicLongGenerator extends GeneratorProxy<Long> {

    protected Expression<Long> min;
    protected Expression<Long> max;
    protected Expression<Long> granularity;
    protected Expression<? extends Distribution> distribution;

    // constructors ----------------------------------------------------------------------------------------------------

    public DynamicLongGenerator() {
        this(ExpressionUtil.constant(0L), ExpressionUtil.constant(30L), 
        		ExpressionUtil.constant(1L), ExpressionUtil.constant(SequenceManager.RANDOM_SEQUENCE), 
        		ExpressionUtil.constant(false));
    }

    public DynamicLongGenerator(Expression<Long> min, Expression<Long> max, 
    		Expression<Long> granularity, Expression<? extends Distribution> distribution,
    	    Expression<Boolean> unique) {
        super(Long.class);
        this.min = min;
        this.max = max;
        this.granularity = granularity;
        this.distribution = distribution;
    }
    
    // Generator interface ---------------------------------------------------------------------------------------------

	/** ensures consistency of the state */
    @Override
    public void init(GeneratorContext context) {
    	assertNotInitialized();
    	this.context = context;
    	resetMembers(min.evaluate(context), max.evaluate(context));
        super.init(context);
    }

    @Override
    public void reset() {
    	assertInitialized();
    	resetMembers(min.evaluate(context), max.evaluate(context));
        super.reset();
    }

	protected void resetMembers(Long minValue, Long maxValue) {
		if (minValue == null)
			minValue = 0L;
		Long granularityValue = ExpressionUtil.evaluate(granularity, context);
		if (granularityValue == null)
			granularityValue = 1L;
	    Distribution dist = distribution.evaluate(context);
	    Generator<Long> source = dist.createNumberGenerator(Long.class, minValue, maxValue, granularityValue, false);
        source.init(context);
        setSource(source);
    }
    
}

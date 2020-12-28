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

import java.math.BigInteger;

import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.InvalidGeneratorSetupException;
import com.rapiddweller.benerator.PropertyMessage;
import com.rapiddweller.benerator.util.ThreadSafeNonNullGenerator;

/**
 * Generates random {@link BigInteger} with a uniform distribution.<br/>
 * <br/>
 * Created at 23.06.2009 23:26:06
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class RandomBigIntegerGenerator extends ThreadSafeNonNullGenerator<BigInteger> {

    private static final BigInteger DEFAULT_MIN = BigInteger.valueOf(Long.MIN_VALUE);
	private static final BigInteger DEFAULT_MAX = BigInteger.valueOf(Long.MAX_VALUE);
	private static final BigInteger DEFAULT_GRNULARITY = BigInteger.valueOf(1);

    private final BigInteger min;
    private final BigInteger max;
    private final BigInteger granularity;
    
    // constructors ----------------------------------------------------------------------------------------------------

    public RandomBigIntegerGenerator() {
    	this(DEFAULT_MIN, DEFAULT_MAX);
    }

    public RandomBigIntegerGenerator(BigInteger min, BigInteger max) {
        this(min, max, DEFAULT_GRNULARITY);
    }

    public RandomBigIntegerGenerator(BigInteger min, BigInteger max, BigInteger granularity) {
        this.min = min;
        this.max = max;
        this.granularity = granularity;
    }

    // Generator implementation ----------------------------------------------------------------------------------------

    @Override
	public Class<BigInteger> getGeneratedType() {
	    return BigInteger.class;
    }

    @Override
    public synchronized void init(GeneratorContext context) {
    	if (BigInteger.ZERO.compareTo(granularity) == 0)
    		throw new InvalidGeneratorSetupException(getClass().getSimpleName() + ".granularity may not be 0");
        super.init(context);
    }
    
	@Override
	public BigInteger generate() {
        return generate(min, max, granularity);
    }
    
    // public convenience method ---------------------------------------------------------------------------------------

    public static BigInteger generate(BigInteger min, BigInteger max, BigInteger granularity) {
        if (min.compareTo(max) > 0)
            throw new InvalidGeneratorSetupException(
                    new PropertyMessage("min", "greater than max"),
                    new PropertyMessage("max", "less than min"));
        long range = max.subtract(min).divide(granularity).longValue();
        return min.add(BigInteger.valueOf(RandomLongGenerator.generate(0, range, 1)).multiply(granularity));
    }

}

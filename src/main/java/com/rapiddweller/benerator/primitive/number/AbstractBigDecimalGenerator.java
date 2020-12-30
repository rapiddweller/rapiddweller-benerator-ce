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

package com.rapiddweller.benerator.primitive.number;

import com.rapiddweller.common.MathUtil;

import java.math.BigDecimal;

/**
 * Wrapper for a LongGenerator that maps the generated Longs to BigDecimals.<br/>
 * <br/>
 * Created: 01.07.2006 17:43:29
 * @author Volker Bergmann
 */
public abstract class AbstractBigDecimalGenerator extends AbstractNonNullNumberGenerator<BigDecimal> {

    public static final BigDecimal DEFAULT_GRANULARITY = new BigDecimal("0.01");

    private Integer fractionDigits;

    /** Initializes the generator to create uniformly distributed random BigDecimals with granularity 1 */
    public AbstractBigDecimalGenerator() {
        this(new BigDecimal(Long.MIN_VALUE), new BigDecimal(Long.MAX_VALUE));
    }

    /** Initializes the generator to create uniformly distributed random BigDecimals with granularity 1 */
    public AbstractBigDecimalGenerator(BigDecimal min, BigDecimal max) {
        this(min, max, DEFAULT_GRANULARITY);
    }

    /** Initializes the generator to create uniformly distributed random BigDecimals */
    public AbstractBigDecimalGenerator(BigDecimal min, BigDecimal max, BigDecimal granularity) {
        super(BigDecimal.class, min, max, granularity);
    }

    // config properties -----------------------------------------------------------------------------------------------

    @Override
    public void setGranularity(BigDecimal granularity) {
        super.setGranularity(granularity);
        this.fractionDigits = Math.max(
                MathUtil.fractionDigits(min.doubleValue()),
                MathUtil.fractionDigits(granularity.doubleValue())
            );
    }

    public Integer getFractionDigits() {
        return fractionDigits;
    }

}

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

import com.rapiddweller.common.converter.NumberToNumberConverter;

import java.math.BigInteger;

/**
 * Wrapper for a LongGenerator that maps the generated Longs to BigIntegers.<br/>
 * <br/>
 * Created: 07.06.2006 19:04:08
 * @author Volker Bergmann
 */
public abstract class AbstractBigIntegerGenerator extends AbstractNonNullNumberGenerator<BigInteger> {

    private static final BigInteger DEFAULT_MIN = new BigInteger(new byte[] {
            (byte)0x80, (byte)0x00, (byte)0x00, (byte)0x00,
            (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00}
    );

    private static final BigInteger DEFAULT_MAX = new BigInteger(new byte[] {
            (byte)0x7f, (byte)0xff, (byte)0xff, (byte)0xff,
            (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff}
    );

    /** Initializes the generator to create uniformly distributed random BigIntegers with granularity 1 */
    public AbstractBigIntegerGenerator() {
        this(DEFAULT_MIN, DEFAULT_MAX);
    }

    /** Initializes the generator to create uniformly distributed random BigIntegers with granularity 1 */
    public AbstractBigIntegerGenerator(BigInteger min, BigInteger max) {
        this(min, max, NumberToNumberConverter.convert(1, BigInteger.class));
    }

    /** Initializes the generator to create uniformly distributed random BigIntegers with the specified granularity */
    public AbstractBigIntegerGenerator(BigInteger min, BigInteger max, BigInteger granularity) {
        super(BigInteger.class, min, max, granularity);
    }
}

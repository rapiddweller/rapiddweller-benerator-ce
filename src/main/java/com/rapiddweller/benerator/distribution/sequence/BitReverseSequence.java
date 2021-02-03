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

import com.rapiddweller.benerator.NonNullGenerator;
import com.rapiddweller.benerator.distribution.Sequence;
import com.rapiddweller.benerator.wrapper.WrapperFactory;

import static com.rapiddweller.common.NumberUtil.toLong;

/**
 * {@link Sequence} implementation for a distribution that creates the bit-reverses (r)
 * of a continuous series, e.g. r(1), r(2), r(3), ...; the numbers produced by a related
 * generator are unique as long as the generator is not reset.<br/>
 * <br/>
 * Created at 23.09.2009 18:59:30
 *
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class BitReverseSequence extends Sequence {

  @Override
  public <T extends Number> NonNullGenerator<T> createNumberGenerator(
      Class<T> numberType, T min, T max, T granularity, boolean unique) {
    long lMax = (max != null ? max.longValue() : BitReverseLongGenerator.MAX_INDEX_RANGE + toLong(min));
    NonNullGenerator<? extends Number> base = new BitReverseLongGenerator(toLong(min), lMax, toLong(granularity));
    return WrapperFactory.asNonNullNumberGeneratorOfType(numberType, base, min, granularity);
  }

}

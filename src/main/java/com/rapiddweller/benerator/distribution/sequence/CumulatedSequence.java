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
import com.rapiddweller.common.BeanUtil;
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.common.NumberUtil;

import java.math.BigInteger;

import static com.rapiddweller.common.NumberUtil.toDouble;
import static com.rapiddweller.common.NumberUtil.toLong;

/**
 * {@link Sequence} implementation for an efficient bell-like distribution.<br/>
 * <br/>
 * Created at 23.09.2009 18:59:30
 *
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class CumulatedSequence extends Sequence {

  @Override
  public <T extends Number> NonNullGenerator<T> createNumberGenerator(Class<T> numberType, T min, T max, T granularity, boolean unique) {
    if (unique) {
      throw new ConfigurationError(getClass().getSimpleName() + " does not support uniqueness");
    }
    NonNullGenerator<? extends Number> base;
    if (BeanUtil.isIntegralNumberType(numberType)) {
      long lMax = (max != null ? max.longValue() :
          (numberType != Long.class && numberType != BigInteger.class ? NumberUtil.maxValue(numberType).longValue() :
              CumulatedLongGenerator.DEFAULT_MAX));
      base = new CumulatedLongGenerator(toLong(min), lMax, toLong(granularity));
    } else {
      double dMax = (max != null ? max.doubleValue() : Long.MAX_VALUE);
      base = new CumulatedDoubleGenerator(toDouble(min), dMax, toDouble(granularity));
    }
    return WrapperFactory.asNonNullNumberGeneratorOfType(numberType, base, min, granularity);
  }

}

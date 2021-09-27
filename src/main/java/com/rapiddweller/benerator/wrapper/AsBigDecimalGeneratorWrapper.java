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
import com.rapiddweller.common.MathUtil;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;
import java.util.Map;

/**
 * Converts the {@link Number} products of another {@link Generator} to {@link BigDecimal}.<br/><br/>
 * Created at 23.06.2009 22:58:26
 * @param <E> the type parameter
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class AsBigDecimalGeneratorWrapper<E extends Number> extends GeneratorWrapper<E, BigDecimal> {

  private int fractionDigits;
  private static Map<Integer, MathContext> contexts = new HashMap<>();

  public AsBigDecimalGeneratorWrapper(Generator<E> source) {
    this(source, null, null);
  }

  public AsBigDecimalGeneratorWrapper(Generator<E> source, BigDecimal min, BigDecimal granularity) {
    super(source);
    if (granularity != null) {
      this.fractionDigits = MathUtil.fractionDigits(granularity.doubleValue());
      if (min != null) {
        this.fractionDigits = Math.max(this.fractionDigits, MathUtil.fractionDigits(min.doubleValue()));
      }
    } else if (min != null) {
      this.fractionDigits = MathUtil.fractionDigits(min.doubleValue());
    } else {
      this.fractionDigits = 0;
    }
  }

  @Override
  public Class<BigDecimal> getGeneratedType() {
    return BigDecimal.class;
  }

  @Override
  public ProductWrapper<BigDecimal> generate(ProductWrapper<BigDecimal> wrapper) {
    ProductWrapper<E> tmp = generateFromSource();
    if (tmp == null) {
      return null;
    }
    String s = tmp.unwrap().toString();
    return wrapper.wrap(createBigDecimal(s));
  }

  private BigDecimal createBigDecimal(String s) {
    int prefixDigits = countPrefixDigits(s);
    MathContext mathcontext = getMathContext(prefixDigits + fractionDigits);
    return new BigDecimal(s, mathcontext);
  }

  private MathContext getMathContext(int totalDigits) {
    return contexts.computeIfAbsent(totalDigits, k -> new MathContext(totalDigits));
  }

  private static int countPrefixDigits(String s) {
    int sepIndex = s.indexOf('.');
    if (sepIndex >= 0) {
      if (s.startsWith("-")) {
        sepIndex--;
      }
    } else {
      sepIndex = 0;
    }
    return (sepIndex > 0 && s.charAt(sepIndex - 1) == '0' ? sepIndex - 1 : sepIndex);
  }

}

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
import com.rapiddweller.benerator.SequenceTestGenerator;
import com.rapiddweller.benerator.engine.DefaultBeneratorContext;
import com.rapiddweller.benerator.util.GeneratorUtil;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

/**
 * Tests the {@link AsBigDecimalGeneratorWrapper}.<br/><br/>
 * Created: 12.01.2011 00:02:55
 *
 * @author Volker Bergmann
 * @since 0.6.4
 */
public class AsBigDecimalGeneratorWrapperTest {

  /**
   * Test granularity.
   */
  @Test
  public void testGranularity() {
    Generator<Double> source = new SequenceTestGenerator<>(0.1234, 1.234, 12.34, 123.4, 1234.56);
    AsBigDecimalGeneratorWrapper<Double> wrapper
        = new AsBigDecimalGeneratorWrapper<>(source, BigDecimal.ZERO, new BigDecimal("0.01"));
    wrapper.init(new DefaultBeneratorContext());
    assertEquals(new BigDecimal("0.12"), GeneratorUtil.generateNonNull(wrapper));
    assertEquals(new BigDecimal("1.23"), GeneratorUtil.generateNonNull(wrapper));
    assertEquals(new BigDecimal("12.34"), GeneratorUtil.generateNonNull(wrapper));
    assertEquals(new BigDecimal("123.40"), GeneratorUtil.generateNonNull(wrapper));
    assertEquals(new BigDecimal("1234.56"), GeneratorUtil.generateNonNull(wrapper));
  }

}

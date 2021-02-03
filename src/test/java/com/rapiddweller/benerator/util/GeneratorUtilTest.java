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

package com.rapiddweller.benerator.util;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.SequenceTestGenerator;
import com.rapiddweller.benerator.engine.BeneratorOpts;
import com.rapiddweller.benerator.primitive.IncrementGenerator;
import com.rapiddweller.benerator.test.GeneratorTest;
import com.rapiddweller.common.SysUtil;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Tests the {@link GeneratorUtil} class.<br/><br/>
 * Created: 30.07.2010 18:54:13
 *
 * @author Volker Bergmann
 * @since 0.6.3
 */
public class GeneratorUtilTest extends GeneratorTest {

  /**
   * Test all products default cache size.
   */
  @Test
  public void testAllProducts_defaultCacheSize() {
    Generator<Long> source = new IncrementGenerator(1, 1, 120000);
    source.init(context);
    List<Long> products = GeneratorUtil.allProducts(source);
    assertEquals(100000, products.size());
    assertEquals(1L, products.get(0).longValue());
    assertEquals(100000L, products.get(99999).longValue());
  }

  /**
   * Test all products cache size override.
   */
  @Test
  public void testAllProducts_cacheSizeOverride() {
    SysUtil.runWithSystemProperty(BeneratorOpts.OPTS_CACHE_SIZE, "2", () -> {
      SequenceTestGenerator<Integer> source = new SequenceTestGenerator<>(1, 2, 3, 4);
      source.init(context);
      List<Integer> products = GeneratorUtil.allProducts(source);
      assertEquals(2, products.size());
      assertEquals(1, products.get(0).intValue());
      assertEquals(2, products.get(1).intValue());
    });
  }

}

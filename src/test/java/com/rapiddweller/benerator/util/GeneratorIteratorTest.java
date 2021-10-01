/*
 * (c) Copyright 2021 by rapiddweller GmbH & Volker Bergmann. All rights reserved.
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
import com.rapiddweller.benerator.sample.SequenceGenerator;
import org.junit.Test;

import java.util.NoSuchElementException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests the {@link GeneratorIterator}.<br/><br/>
 * Created: 01.10.2021 22:50:39
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class GeneratorIteratorTest {

  @Test
  public void testCycle() {
    Generator<Integer> gen = new SequenceGenerator<>(Integer.class,1, 2);
    GeneratorIterator<Integer> iter = new GeneratorIterator<>(gen);
    assertTrue(iter.hasNext());
    assertEquals(1, (int) iter.next());
    assertTrue(iter.hasNext());
    assertEquals(2, (int) iter.next());
    assertFalse(iter.hasNext());
    try {
      iter.next();
      fail("Expected NoSuchElementException");
    } catch (NoSuchElementException e) {
      // this is expected
    }
    iter.close();
  }

  @Test
  public void testRemove() {
    Generator<Integer> gen = new SequenceGenerator<>(Integer.class,1, 2);
    GeneratorIterator<Integer> iter = new GeneratorIterator<>(gen);
    assertTrue(iter.hasNext());
    assertEquals(1, (int) iter.next());
    try {
      iter.remove();
      fail("Expected UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      // this is expected
    }
    iter.close();
  }

}

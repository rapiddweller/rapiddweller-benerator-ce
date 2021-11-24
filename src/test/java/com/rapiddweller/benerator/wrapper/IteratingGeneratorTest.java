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
import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.benerator.test.GeneratorTest;
import com.rapiddweller.common.HeavyweightIterator;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.common.TypedIterable;
import com.rapiddweller.common.iterator.HeavyweightIterableAdapter;
import com.rapiddweller.common.iterator.TypedIterableProxy;
import org.junit.Test;

import java.util.Arrays;
import java.util.NoSuchElementException;

import static org.junit.Assert.assertTrue;

/**
 * Tests the {@link IteratingGenerator}.<br/><br/>
 * Created: 01.09.2007 17:22:03
 * @author Volker Bergmann
 */
public class IteratingGeneratorTest extends GeneratorTest {

  @Test
  public void testDefaultBehaviour() {
    HeavyweightIterableAdapter<Integer> iterable = new HeavyweightIterableAdapter<>(Arrays.asList(1, 2));
    TypedIterableProxy<Integer> hwIterable = new TypedIterableProxy<>(Integer.class, iterable);
    Generator<Integer> gen = new IteratingGenerator<>(hwIterable);
    gen.init(context);
    expectGeneratedSequence(gen, 1, 2).withCeasedAvailability();
  }

  @Test
  public void testEmptyIterator() {
    EmptyIterable emptySource = new EmptyIterable();
    Generator<Integer> generator = new IteratingGenerator<>(emptySource);
    generator.init(context);
    assertUnavailable(generator);
    assertTrue(emptySource.latestInstance.closed);
    IOUtil.close(generator);
  }

  public static class EmptyIterable implements TypedIterable<Integer> {

    public EmptyIterator latestInstance;

    @Override
    public Class<Integer> getType() {
      return Integer.class;
    }

    @Override
    public HeavyweightIterator<Integer> iterator() {
      latestInstance = new EmptyIterator();
      return latestInstance;
    }

  }

  public static class EmptyIterator implements HeavyweightIterator<Integer> {

    public boolean closed = false;

    @Override
    public boolean hasNext() {
      return false;
    }

    @Override
    public Integer next() {
      throw new NoSuchElementException();
    }

    @Override
    public void remove() {
      throw BeneratorExceptionFactory.getInstance().programmerUnsupported("EmptyIterator.remove() is not supported");
    }

    @Override
    public void close() {
      closed = true;
    }

  }

}

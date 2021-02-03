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

import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.primitive.number.AbstractNonNullNumberGenerator;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Long Generator that implements a 'wedge' Long Sequence.<br/>
 * <br/>
 * Created: 13.11.2007 12:54:29
 *
 * @author Volker Bergmann
 */
public class WedgeLongGenerator extends AbstractNonNullNumberGenerator<Long> {

  private AtomicLong next;
  private long end;

  /**
   * Instantiates a new Wedge long generator.
   */
  public WedgeLongGenerator() {
    this(Long.MIN_VALUE, Long.MAX_VALUE);
  }

  /**
   * Instantiates a new Wedge long generator.
   *
   * @param min the min
   * @param max the max
   */
  public WedgeLongGenerator(long min, long max) {
    this(min, max, 1);
  }

  /**
   * Instantiates a new Wedge long generator.
   *
   * @param min         the min
   * @param max         the max
   * @param granularity the granularity
   */
  public WedgeLongGenerator(long min, long max, long granularity) {
    super(Long.class, min, max, granularity);
    this.next = new AtomicLong(min);
  }

  // generator interface ---------------------------------------------------------------------------------------------

  @Override
  public void init(GeneratorContext context) {
    assertNotInitialized();
    max = min + (max - min) / granularity * granularity;
    long steps = (max - min) / granularity + 1;
    end = min + steps / 2 * granularity;
    super.init(context);
  }

  @Override
  public synchronized Long generate() {
    assertInitialized();
    if (next == null) {
      return null;
    }
    long result = next.get();
    if (result == end) {
      next = null;
    } else {
      long nextValue = max - result + min;
      if (nextValue < end) {
        nextValue += granularity;
      }
      next.set(nextValue);
    }
    return result;
  }

  @Override
  public synchronized void reset() {
    super.reset();
    this.next = new AtomicLong(min);
  }

  @Override
  public synchronized void close() {
    super.close();
    this.next = null;
  }

}

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

package com.rapiddweller.benerator.primitive;

import com.rapiddweller.benerator.util.ThreadSafeNonNullGenerator;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Generates unique long values incrementally.<br/><br/>
 * Created: 14.11.2009 06:49:49
 *
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class IncrementalIdGenerator extends ThreadSafeNonNullGenerator<Long> {

  private long increment;
  private final AtomicLong cursor = new AtomicLong();

  // constructors ----------------------------------------------------------------------------------------------------

  /**
   * Instantiates a new Incremental id generator.
   */
  public IncrementalIdGenerator() {
    this(1, 1);
  }

  /**
   * Instantiates a new Incremental id generator.
   *
   * @param initial the initial
   */
  public IncrementalIdGenerator(long initial) {
    this(initial, 1);
  }

  /**
   * Instantiates a new Incremental id generator.
   *
   * @param initial   the initial
   * @param increment the increment
   */
  public IncrementalIdGenerator(long initial, long increment) {
    setInitial(initial);
    this.increment = increment;
  }

  // properties ------------------------------------------------------------------------------------------------------

  /**
   * Gets cursor.
   *
   * @return the cursor
   */
  public long getCursor() {
    return cursor.get();
  }

  /**
   * Sets initial.
   *
   * @param initial the initial
   */
  public void setInitial(long initial) {
    this.cursor.set(initial);
  }

  /**
   * Sets increment.
   *
   * @param increment the increment
   */
  public void setIncrement(long increment) {
    this.increment = increment;
  }

  // Generator interface implementation ------------------------------------------------------------------------------

  @Override
  public Class<Long> getGeneratedType() {
    return Long.class;
  }

  @Override
  public Long generate() {
    return cursor.getAndAdd(increment);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "[current=" + cursor.get() + ", increment=" + increment + "]";
  }

}

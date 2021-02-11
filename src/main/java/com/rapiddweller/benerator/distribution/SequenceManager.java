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

package com.rapiddweller.benerator.distribution;

import com.rapiddweller.benerator.distribution.sequence.BitReverseSequence;
import com.rapiddweller.benerator.distribution.sequence.CumulatedSequence;
import com.rapiddweller.benerator.distribution.sequence.ExpandSequence;
import com.rapiddweller.benerator.distribution.sequence.HeadSequence;
import com.rapiddweller.benerator.distribution.sequence.RandomSequence;
import com.rapiddweller.benerator.distribution.sequence.RandomWalkSequence;
import com.rapiddweller.benerator.distribution.sequence.ShuffleSequence;
import com.rapiddweller.benerator.distribution.sequence.StepSequence;
import com.rapiddweller.benerator.distribution.sequence.WedgeSequence;
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.domain.math.FibonacciSequence;
import com.rapiddweller.domain.math.PadovanSequence;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages {@link Sequence}s.<br/><br/>
 * Created: 17.02.2010 13:36:17
 *
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class SequenceManager {

  private static final Map<String, Sequence> instances = new HashMap<>();

  /**
   * The constant RANDOM_SEQUENCE.
   */
  public static final Sequence RANDOM_SEQUENCE = register("random", new RandomSequence());
  /**
   * The constant SHUFFLE_SEQUENCE.
   */
  public static final Sequence SHUFFLE_SEQUENCE = register("shuffle", new ShuffleSequence());
  /**
   * The constant CUMULATED_SEQUENCE.
   */
  public static final Sequence CUMULATED_SEQUENCE = register("cumulated", new CumulatedSequence());
  /**
   * The constant RANDOM_WALK_SEQUENCE.
   */
  public static final Sequence RANDOM_WALK_SEQUENCE = register("randomWalk", new RandomWalkSequence());
  /**
   * The constant STEP_SEQUENCE.
   */
  public static final Sequence STEP_SEQUENCE = register("step", new StepSequence());
  /**
   * The constant INCREMENT_SEQUENCE.
   */
  public static final Sequence INCREMENT_SEQUENCE = register("increment", new StepSequence(BigDecimal.ONE));
  /**
   * The constant WEDGE_SEQUENCE.
   */
  public static final Sequence WEDGE_SEQUENCE = register("wedge", new WedgeSequence());
  /**
   * The constant BIT_REVERSE_SEQUENCE.
   */
  public static final Sequence BIT_REVERSE_SEQUENCE = register("bitreverse", new BitReverseSequence());
  /**
   * The constant EXPAND_SEQUENCE.
   */
  public static final Sequence EXPAND_SEQUENCE = register("expand", new ExpandSequence());
  /**
   * The constant FIBONACCI_SEQUENCE.
   */
  public static final Sequence FIBONACCI_SEQUENCE = register("fibonacci", new FibonacciSequence());
  /**
   * The constant PADOVAN_SEQUENCE.
   */
  public static final Sequence PADOVAN_SEQUENCE = register("padovan", new PadovanSequence());
  /**
   * The constant SINGLE_SEQUENCE.
   */
  public static final Sequence SINGLE_SEQUENCE = register("head", new HeadSequence());

  // Construction & lookup -------------------------------------------------------------------------------------------

  /**
   * Gets registered sequence.
   *
   * @param name     the name
   * @param required the required
   * @return the registered sequence
   */
  public synchronized static Sequence getRegisteredSequence(String name, boolean required) {
    Sequence sequence = instances.get(name);
    if (sequence == null && required) {
      throw new ConfigurationError("Sequence not registered: " + name);
    }
    return sequence;
  }

  /**
   * Register sequence.
   *
   * @param name     the name
   * @param sequence the sequence
   * @return the sequence
   */
  public synchronized static Sequence register(String name, Sequence sequence) {
    instances.put(name, sequence);
    return sequence;
  }

  /**
   * Registered sequences collection.
   *
   * @return the collection
   */
  public synchronized static Collection<Sequence> registeredSequences() {
    return instances.values();
  }

}

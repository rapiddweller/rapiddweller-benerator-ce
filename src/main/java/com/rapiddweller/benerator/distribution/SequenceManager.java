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
import com.rapiddweller.common.exception.ExceptionFactory;
import com.rapiddweller.domain.math.FibonacciSequence;
import com.rapiddweller.domain.math.PadovanSequence;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages {@link Sequence}s.<br/><br/>
 * Created: 17.02.2010 13:36:17
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class SequenceManager {

  private static final Map<String, Sequence> instances = new HashMap<>();

  public static final Sequence RANDOM_SEQUENCE = register("random", new RandomSequence());
  public static final Sequence SHUFFLE_SEQUENCE = register("shuffle", new ShuffleSequence());
  public static final Sequence CUMULATED_SEQUENCE = register("cumulated", new CumulatedSequence());
  public static final Sequence RANDOM_WALK_SEQUENCE = register("randomWalk", new RandomWalkSequence());
  public static final Sequence STEP_SEQUENCE = register("step", new StepSequence());
  public static final Sequence INCREMENT_SEQUENCE = register("increment", new StepSequence(BigDecimal.ONE));
  public static final Sequence WEDGE_SEQUENCE = register("wedge", new WedgeSequence());
  public static final Sequence BIT_REVERSE_SEQUENCE = register("bitreverse", new BitReverseSequence());
  public static final Sequence EXPAND_SEQUENCE = register("expand", new ExpandSequence());
  public static final Sequence FIBONACCI_SEQUENCE = register("fibonacci", new FibonacciSequence());
  public static final Sequence PADOVAN_SEQUENCE = register("padovan", new PadovanSequence());
  public static final Sequence SINGLE_SEQUENCE = register("head", new HeadSequence());

  // Construction & lookup -------------------------------------------------------------------------------------------

  private SequenceManager() {
    // private constructor to prevent instantiation
  }

  public static synchronized Sequence getRegisteredSequence(String name, boolean required) {
    Sequence sequence = instances.get(name);
    if (sequence == null && required) {
      throw ExceptionFactory.getInstance().configurationError("Sequence not registered: " + name);
    }
    return sequence;
  }

  public static synchronized Sequence register(String name, Sequence sequence) {
    instances.put(name, sequence);
    return sequence;
  }

  public static synchronized Collection<Sequence> registeredSequences() {
    return instances.values();
  }

}

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

package com.rapiddweller.benerator.sample;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.IllegalGeneratorStateException;
import com.rapiddweller.benerator.InvalidGeneratorSetupException;
import com.rapiddweller.benerator.util.UnsafeNonNullGenerator;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.common.ParseException;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.script.DatabeneScriptParser;
import com.rapiddweller.script.WeightedTransition;

import java.util.HashMap;
import java.util.Map;

/**
 * Generates states as configured by a state machine.<br/>
 * <br/>
 * Created at 17.07.2009 05:41:47
 *
 * @param <E> the type parameter
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class StateGenerator<E> extends UnsafeNonNullGenerator<E> {

  private final Class<E> generatedType;
  private final Map<E, AttachedWeightSampleGenerator<E>> transitionsGenerators;
  private E nextState;

  // initialization --------------------------------------------------------------------------------------------------

  /**
   * Instantiates a new State generator.
   */
  public StateGenerator() {
    this((String) null);
  }

  /**
   * Instantiates a new State generator.
   *
   * @param transitionSpec the transition spec
   */
  @SuppressWarnings("unchecked")
  public StateGenerator(String transitionSpec) {
    this((Class<E>) Object.class);
    setTransitions(transitionSpec);
  }

  /**
   * Instantiates a new State generator.
   *
   * @param generatedType the generated type
   */
  public StateGenerator(Class<E> generatedType) {
    this.generatedType = generatedType;
    this.transitionsGenerators = new HashMap<>();
    this.nextState = null;
  }

  /**
   * Sets transitions.
   *
   * @param transitionSpec the transition spec
   */
  @SuppressWarnings("unchecked")
  public void setTransitions(String transitionSpec) {
    if (StringUtil.isEmpty(transitionSpec)) {
      transitionsGenerators.clear();
      return;
    }
    try {
      WeightedTransition[] ts = DatabeneScriptParser.parseTransitionList(transitionSpec);
      for (WeightedTransition t : ts) {
        addTransition((E) t.getFrom(), (E) t.getTo(), t.getWeight());
      }
    } catch (ParseException e) {
      throw new ConfigurationError("Error parsing state machine specification: " + transitionSpec, e);
    }
  }

  /**
   * Add transition.
   *
   * @param from   the from
   * @param to     the to
   * @param weight the weight
   */
  public void addTransition(E from, E to, double weight) {
    AttachedWeightSampleGenerator<E> subGenerator = transitionsGenerators.get(from);
    if (subGenerator == null) {
      subGenerator = new AttachedWeightSampleGenerator<>(generatedType);
      transitionsGenerators.put(from, subGenerator);
    }
    subGenerator.addSample(to, weight);
  }

  // Generator interface implementation ------------------------------------------------------------------------------

  @Override
  public Class<E> getGeneratedType() {
    return generatedType;
  }

  @Override
  public void init(GeneratorContext context) throws InvalidGeneratorSetupException {
    assertNotInitialized();
    boolean hasEndTransition = false;
    for (AttachedWeightSampleGenerator<E> tmp : transitionsGenerators.values()) {
      if (tmp.containsSample(null)) {
        hasEndTransition = true;
        break;
      }
    }
    if (!hasEndTransition) {
      throw new InvalidGeneratorSetupException("No final state defined for " + this);
    }
    for (Generator<E> tmp : transitionsGenerators.values()) {
      tmp.init(context);
    }
    AttachedWeightSampleGenerator<E> gen = this.transitionsGenerators.get(null);
    nextState = gen.generate(getResultWrapper()).unwrap();
    super.init(context);
  }

  @Override
  public E generate() {
    if (nextState == null) {
      return null;
    }
    E result = nextState;
    AttachedWeightSampleGenerator<E> transitionGenerator = transitionsGenerators.get(nextState);
    ProductWrapper<E> wrapper = transitionGenerator.generate(getResultWrapper());
    nextState = (wrapper != null ? wrapper.unwrap() : null);
    return result;
  }

  @Override
  public void reset() throws IllegalGeneratorStateException {
    AttachedWeightSampleGenerator<E> transitionGenerator = this.transitionsGenerators.get(null);
    ProductWrapper<E> wrapper = transitionGenerator.generate(getResultWrapper());
    nextState = (wrapper != null ? wrapper.unwrap() : null);
    super.reset();
  }

  @Override
  public void close() {
    super.close();
  }

  // java.lang.Object overrides --------------------------------------------------------------------------------------

  @Override
  public String toString() {
    return getClass().getSimpleName() + transitionsGenerators;
  }

}

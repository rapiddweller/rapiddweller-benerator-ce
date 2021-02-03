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

import com.rapiddweller.benerator.wrapper.GeneratorWrapper;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.common.converter.AnyConverter;
import com.rapiddweller.script.DatabeneScriptParser;
import com.rapiddweller.script.Transition;
import com.rapiddweller.script.WeightedTransition;

/**
 * Generates state transitions of a state machine.<br/>
 * <br/>
 * Created at 17.07.2009 08:04:12
 *
 * @param <E> the type parameter
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class StateTransitionGenerator<E> extends GeneratorWrapper<E, Transition> {

  private final Class<E> stateType;
  private E currentState;
  private boolean done;

  /**
   * Instantiates a new State transition generator.
   *
   * @param stateType the state type
   */
  public StateTransitionGenerator(Class<E> stateType) {
    this(stateType, null);
  }

  /**
   * Instantiates a new State transition generator.
   *
   * @param stateType      the state type
   * @param transitionSpec the transition spec
   */
  public StateTransitionGenerator(Class<E> stateType, String transitionSpec) {
    super(new StateGenerator<>(stateType));
    this.stateType = stateType;
    if (transitionSpec != null) {
      WeightedTransition[] transitions = DatabeneScriptParser.parseTransitionList(transitionSpec);
      for (WeightedTransition t : transitions) {
        addTransition(convert(t.getFrom()), convert(t.getTo()), t.getWeight());
      }
    }
    this.currentState = null;
    this.done = false;
  }

  /**
   * Add transition.
   *
   * @param from   the from
   * @param to     the to
   * @param weight the weight
   */
  public void addTransition(E from, E to, double weight) {
    ((StateGenerator<E>) getSource()).addTransition(from, to, weight);
  }

  // Generator interface implementation ------------------------------------------------------------------------------

  @Override
  public Class<Transition> getGeneratedType() {
    return Transition.class;
  }

  @Override
  public ProductWrapper<Transition> generate(ProductWrapper<Transition> wrapper) {
    if (done) {
      return null;
    }
    E previousState = currentState;
    ProductWrapper<E> sourceWrapper = generateFromSource();
    if (sourceWrapper == null) {
      done = true;
      return wrapper.wrap(new Transition(previousState, null)); // final transition
    }
    currentState = sourceWrapper.unwrap();
    return wrapper.wrap(new Transition(previousState, currentState));
  }

  @Override
  public void reset() {
    currentState = null;
    this.done = false;
    super.reset();
  }

  @Override
  public void close() {
    currentState = null;
    this.done = true;
    super.close();
  }

  // private helpers -------------------------------------------------------------------------------------------------

  private E convert(Object object) {
    return AnyConverter.convert(object, stateType);
  }

}

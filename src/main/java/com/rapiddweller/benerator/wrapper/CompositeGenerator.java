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
import com.rapiddweller.benerator.util.AbstractGenerator;
import com.rapiddweller.common.Converter;
import com.rapiddweller.common.ThreadAware;
import com.rapiddweller.common.ThreadUtil;
import com.rapiddweller.common.context.ContextAware;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * {@link Generator} implementation that makes use of other {@link ContextAware}
 * objects by which its threading support is influenced.<br/><br/>
 * Created: 20.03.2010 11:19:11
 *
 * @param <E> the type parameter
 * @author Volker Bergmann
 * @since 0.6.0
 */
public abstract class CompositeGenerator<E> extends AbstractGenerator<E> {

  /**
   * The Generated type.
   */
  protected final Class<E> generatedType;
  /**
   * The Components.
   */
  protected final List<ThreadAware> components;

  /**
   * Instantiates a new Composite generator.
   *
   * @param generatedType the generated type
   */
  protected CompositeGenerator(Class<E> generatedType) {
    this.generatedType = generatedType;
    this.components = new ArrayList<>();
  }


  // component registration ------------------------------------------------------------------------------------------

  /**
   * Register component t.
   *
   * @param <T>       the type parameter
   * @param <U>       the type parameter
   * @param component the component
   * @return the t
   */
  protected <T extends Generator<U>, U> T registerComponent(T component) {
    components.add(component);
    return component;
  }

  /**
   * Register component t.
   *
   * @param <T>       the type parameter
   * @param <U>       the type parameter
   * @param <V>       the type parameter
   * @param component the component
   * @return the t
   */
  protected <T extends Converter<U, V>, U, V> T registerComponent(T component) {
    components.add(component);
    return component;
  }

  /**
   * Register components.
   *
   * @param components the components
   */
  protected void registerComponents(ThreadAware[] components) {
    this.components.addAll(Arrays.asList(components));
  }


  // partial Generator interface implementation ----------------------------------------------------------------------

  @Override
  public Class<E> getGeneratedType() {
    return generatedType;
  }

  @Override
  public boolean isThreadSafe() {
    return ThreadUtil.allThreadSafe(components);
  }

  @Override
  public boolean isParallelizable() {
    return ThreadUtil.allParallelizable(components);
  }

}

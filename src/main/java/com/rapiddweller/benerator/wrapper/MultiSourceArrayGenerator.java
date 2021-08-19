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
import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.common.ArrayUtil;
import com.rapiddweller.common.ThreadUtil;

/**
 * Keeps an array of generators, of which it combines the products to an array.<br/><br/>
 * Created: 28.07.2010 19:10:53
 *
 * @param <S> the type parameter
 * @author Volker Bergmann
 * @since 0.1
 */
public class MultiSourceArrayGenerator<S> extends GeneratorProxy<S[]> {

  private final Class<S> componentType;
  private boolean unique;
  private Generator<? extends S>[] sources;

  /**
   * Instantiates a new Multi source array generator.
   *
   * @param componentType the component type
   * @param unique        the unique
   * @param sources       the sources
   */
  @SuppressWarnings("unchecked")
  public MultiSourceArrayGenerator(Class<S> componentType, boolean unique, Generator<? extends S>... sources) {
    super(ArrayUtil.arrayType(componentType));
    this.componentType = componentType;
    this.unique = unique;
    this.sources = sources;
  }

  @Override
  public boolean isThreadSafe() {
    return ThreadUtil.allThreadSafe(this.sources);
  }

  @Override
  public boolean isParallelizable() {
    return ThreadUtil.allParallelizable(this.sources);
  }

  /**
   * Sets unique.
   *
   * @param unique the unique
   */
  public void setUnique(boolean unique) {
    this.unique = unique;
  }

  /**
   * Get sources generator [ ].
   *
   * @return the generator [ ]
   */
  public Generator<? extends S>[] getSources() {
    return sources;
  }

  /**
   * Sets sources.
   *
   * @param sources the sources
   */
  public void setSources(Generator<? extends S>[] sources) {
    this.sources = sources;
  }

  @Override
  public synchronized void init(GeneratorContext context) {
    if (unique) {
      super.setSource(new UniqueMultiSourceArrayGenerator<>(componentType, sources));
    } else {
      super.setSource(new SimpleMultiSourceArrayGenerator<>(componentType, sources));
    }
    super.init(context);
  }

}

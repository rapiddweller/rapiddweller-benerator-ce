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
import com.rapiddweller.common.ArrayUtil;

import java.lang.reflect.Array;

/**
 * Keeps an array of generators, of which it combines the products to an array.<br/>
 * <br/>
 * Created: 26.08.2006 09:37:55
 *
 * @param <S> the type parameter
 * @author Volker Bergmann
 * @since 0.1
 */
public class SimpleMultiSourceArrayGenerator<S> extends MultiGeneratorWrapper<S, S[]> {

  private final Class<S> componentType;
  private boolean available;

  // constructors ----------------------------------------------------------------------------------------------------

  /**
   * Initializes the generator to an array of source generators
   *
   * @param componentType the component type
   * @param sources       the sources
   */
  @SuppressWarnings("unchecked")
  public SimpleMultiSourceArrayGenerator(Class<S> componentType, Generator<? extends S>... sources) {
    super(ArrayUtil.arrayType(componentType), sources);
    this.componentType = componentType;
    this.available = true;
  }

  // Generator implementation ----------------------------------------------------------------------------------------

  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public ProductWrapper<S[]> generate(ProductWrapper<S[]> wrapper) {
    assertInitialized();
    if (!available) {
      return null;
    }
    S[] array = (S[]) Array.newInstance(componentType, availableSourceCount());
    for (int i = 0; i < array.length; i++) {
      try {
        ProductWrapper<S> productWrapper = sources.get(i).generate((ProductWrapper) getSourceWrapper());
        if (productWrapper == null) {
          available = false;
          return null;
        }
        array[i] = productWrapper.unwrap();
      } catch (Exception e) {
        throw new RuntimeException("Generation failed for generator #" + i + " of " + this, e);
      }
    }
    return wrapper.wrap(array);
  }

  @Override
  public void reset() {
    super.reset();
    this.available = true;
  }

  @Override
  public void close() {
    super.close();
    this.available = false;
  }

}

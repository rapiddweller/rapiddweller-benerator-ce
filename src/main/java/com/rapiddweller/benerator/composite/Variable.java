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

package com.rapiddweller.benerator.composite;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.wrapper.ProductWrapper;

/**
 * Wraps variable name and generator functionality.<br/><br/>
 * Created: 07.08.2011 16:24:10
 *
 * @param <E> the type parameter
 * @author Volker Bergmann
 * @since 0.7.0
 */
public class Variable<E> extends AbstractGeneratorComponent<E> {

  private final String name;

  /**
   * Instantiates a new Variable.
   *
   * @param name   the name
   * @param source the source
   * @param scope  the scope
   */
  public Variable(String name, Generator<?> source, String scope) {
    super(source, scope);
    this.name = name;
  }

  @Override
  @SuppressWarnings({"rawtypes", "unchecked"})
  public boolean execute(BeneratorContext context) {
    assertInitialized();
    ProductWrapper<?> productWrapper = source.generate(new ProductWrapper());
    if (productWrapper == null) {
      context.remove(name);
      return false;
    }
    context.set(name, productWrapper.unwrap());
    return true;
  }

  // Closeable interface implementation ------------------------------------------------------------------------------

  @Override
  public void close() {
    if (context != null) // if the variable has not been used (count="0"), it has not been initialized
    {
      context.remove(name);
    }
    super.close();
  }

  // java.lang.Object overrides --------------------------------------------------------------------------------------

  @Override
  public String toString() {
    return getClass().getSimpleName() + "[" + name + ":" + source + "]";
  }

}

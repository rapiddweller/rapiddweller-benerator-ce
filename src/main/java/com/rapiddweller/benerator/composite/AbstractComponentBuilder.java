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
import com.rapiddweller.benerator.util.WrapperProvider;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.common.Mutator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Helper class for simple definition of custom {@link ComponentBuilder}s which uses a {@link Mutator}
 * Created: 30.04.2010 09:34:42
 *
 * @param <E> the type parameter
 * @author Volker Bergmann
 * @since 0.6.1
 */
public abstract class AbstractComponentBuilder<E> extends AbstractGeneratorComponent<E> implements ComponentBuilder<E> {

  /**
   * The Mutator.
   */
  protected final Mutator mutator;
  private final Logger logger = LogManager.getLogger(getClass());
  private final WrapperProvider<Object> wrapperProvider = new WrapperProvider<>();

  /**
   * Instantiates a new Abstract component builder.
   *
   * @param source  the source
   * @param mutator the mutator
   * @param scope   the scope
   */
  public AbstractComponentBuilder(Generator<?> source, Mutator mutator, String scope) {
    super(source, scope);
    this.mutator = mutator;
  }

  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public boolean execute(BeneratorContext context) {
    message = null;
    Object target = context.getCurrentProduct().unwrap();
    ProductWrapper<?> wrapper = source.generate((ProductWrapper) wrapperProvider.get());
    logger.debug("execute(): {} := {}", mutator, wrapper);
    if (wrapper == null) {
      message = "Generator unavailable: " + source;
      return false;
    }
    mutator.setValue(target, wrapper.unwrap());
    return true;
  }

}

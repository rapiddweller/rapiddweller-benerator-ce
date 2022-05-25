/*
 * (c) Copyright 2006-2022 by rapiddweller GmbH & Volker Bergmann. All rights reserved.
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

import com.rapiddweller.benerator.BeneratorErrorIds;
import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.benerator.util.WrapperProvider;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.common.Mutator;
import com.rapiddweller.common.TextFileLocation;
import com.rapiddweller.common.exception.ApplicationException;
import com.rapiddweller.common.exception.ExitCodes;
import com.rapiddweller.common.exception.ScriptException;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * Helper class for simple definition of custom {@link ComponentBuilder}s which uses a {@link Mutator}
 * Created: 30.04.2010 09:34:42
 * @param <E> the type parameter
 * @author Volker Bergmann
 * @since 0.6.1
 */
public abstract class AbstractComponentBuilder<E> extends SourcedGenerationStep<E> implements ComponentBuilder<E> {

  protected final Mutator mutator;
  private final Logger logger = LoggerFactory.getLogger(getClass());
  private final WrapperProvider<Object> wrapperProvider = new WrapperProvider<>();
  private final TextFileLocation fileLocation;

  protected AbstractComponentBuilder(
      Generator<?> source, Mutator mutator, String scope, TextFileLocation fileLocation) {
    super(source, scope);
    this.mutator = mutator;
    this.fileLocation = fileLocation;
  }

  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public boolean execute(BeneratorContext context) {
    message = null;
    Object target = context.getCurrentProduct().unwrap();
    try {
      ProductWrapper<?> wrapper = source.generate((ProductWrapper) wrapperProvider.get());
      logger.debug("execute(): {} := {}", mutator, wrapper);
      if (wrapper == null) {
        message = "Generator unavailable: " + source;
        return false;
      }
      mutator.setValue(target, wrapper.unwrap());
      return true;
    } catch (ScriptException e) {
      e.setLocation(fileLocation);
      e.setErrorId(BeneratorErrorIds.SCRIPT_FAILED);
      throw e;
    } catch (ApplicationException e) {
      e.setLocation(fileLocation);
      throw e;
    } catch (Exception e) {
      throw BeneratorExceptionFactory.getInstance().operationFailed(
          e.getMessage(), e, BeneratorErrorIds.UNSPECIFIC, ExitCodes.MISCELLANEOUS_ERROR);
    }
  }

}

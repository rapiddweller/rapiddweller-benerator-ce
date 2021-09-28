/*
 * (c) Copyright 2006-2021 by rapiddweller GmbH & Volker Bergmann. All rights reserved.
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

import com.rapiddweller.benerator.BeneratorConstants;
import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.common.MessageHolder;
import com.rapiddweller.common.Resettable;
import com.rapiddweller.common.ThreadAware;
import com.rapiddweller.common.ThreadUtil;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

/**
 * Offers support for entity or array component generation with or without variable generation.<br/><br/>
 * Created: 13.01.2011 10:52:43
 * @param <E> the type parameter
 * @author Volker Bergmann
 * @since 0.6.4
 */
public class GenerationStepSupport<E> implements ThreadAware, MessageHolder, Resettable, Closeable {

  private static final Logger LOGGER = LoggerFactory.getLogger(GenerationStepSupport.class);
  private static final Logger STATE_LOGGER = LoggerFactory.getLogger(BeneratorConstants.STATE_LOGGER);

  private final String instanceName;
  private final List<GenerationStep<E>> steps;
  private String message;

  public GenerationStepSupport(String instanceName, List<GenerationStep<E>> steps) {
    this.instanceName = instanceName;
    this.steps = (steps != null ? steps : new ArrayList<>());
  }

  public void init(BeneratorContext context) {
    for (GenerationStep<?> step : steps) {
      step.init(context);
    }
  }

  public boolean apply(E target, BeneratorContext context) {
    BeneratorContext subContext = context.createSubContext(instanceName);
    subContext.setCurrentProduct(new ProductWrapper<>(target));
    for (GenerationStep<E> step : steps) {
      try {
        if (!step.execute(subContext)) {
          message = "generation step for '" + instanceName + "' is not available any longer: " + step;
          STATE_LOGGER.debug(message);
          return false;
        }
      } catch (Exception e) {
        throw new RuntimeException("Failure in generation of '" + instanceName + "', " +
            "Failed step: " + step, e);
      }
    }
    LOGGER.debug("Generated {}", target);
    subContext.close();
    return true;
  }

  @Override
  public void reset() {
    for (GenerationStep<E> step : steps) {
      step.reset();
    }
  }

  @Override
  public void close() {
    for (GenerationStep<E> step : steps) {
      step.close();
    }
  }

  @Override
  public String getMessage() {
    return message;
  }


  // ThreadAware interface implementation ----------------------------------------------------------------------------

  @Override
  public boolean isParallelizable() {
    return ThreadUtil.allParallelizable(steps);
  }

  @Override
  public boolean isThreadSafe() {
    return ThreadUtil.allThreadSafe(steps);
  }


  // java.lang.Object overrides --------------------------------------------------------------------------------------

  @Override
  public String toString() {
    return getClass().getSimpleName() + steps;
  }

}

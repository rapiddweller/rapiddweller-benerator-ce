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

import com.rapiddweller.benerator.BeneratorConstants;
import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.wrapper.GeneratorProxy;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.common.MessageHolder;
import com.rapiddweller.model.data.Entity;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.List;

/**
 * {@link Generator} that takes a (genrated or imported) 'source' {@link Entity} from a source generator
 * and applies a list of {@link GenerationStep}s to evaluate variables and generate or overwrite
 * the source entity's properties.
 * Created: 29.08.2010 09:59:03
 * @author Volker Bergmann
 * @since 0.6.4
 */
public class CompositeEntityGenerator extends GeneratorProxy<Entity> implements MessageHolder {

  private static final Logger LOGGER = LoggerFactory.getLogger(CompositeEntityGenerator.class);
  private static final Logger STATE_LOGGER = LoggerFactory.getLogger(BeneratorConstants.STATE_LOGGER);

  private final String instanceName;
  //private String message;
  private final GenerationStepSupport<Entity> support;

  public CompositeEntityGenerator(String instanceName, Generator<Entity> source,
                                  List<GenerationStep<Entity>> components, BeneratorContext context) {
    super(source);
    this.instanceName = instanceName;
    this.support = new GenerationStepSupport<>(instanceName, components, context);
    this.context = context;
  }

  // Generator implementation ----------------------------------------------------------------------------------------

  @Override
  public void init(GeneratorContext context) {
    support.init((BeneratorContext) context);
    super.init(context);
  }

  @SuppressWarnings("null")
  @Override
  public ProductWrapper<Entity> generate(ProductWrapper<Entity> wrapper) {
    wrapper = getSource().generate(wrapper);
    boolean available = (wrapper != null);
    if (!available) {
      STATE_LOGGER.debug("Source for entity '{}' is not available: {}", instanceName, getSource());
    }
    Entity currentInstance = null;
    if (available) {
      currentInstance = wrapper.unwrap();
      if (instanceName != null) {
        context.set(instanceName, currentInstance);
      }
      available = support.apply(currentInstance, (BeneratorContext) context);
    }
    if (available) {
      LOGGER.debug("Generated {}", currentInstance);
      return wrapper.wrap(currentInstance);
    } else {
      currentInstance = null;
      if (instanceName != null) {
        context.remove(instanceName);
      }
      return null;
    }
  }

  @Override
  public void reset() {
    support.reset();
    super.reset();
  }

  @Override
  public void close() {
    support.close();
    super.close();
  }

  @Override
  public String getMessage() {
    //if (message != null) {
    //  return message;
    //}
    Generator<Entity> source = getSource();
    if (source instanceof MessageHolder && ((MessageHolder) source).getMessage() != null) {
      return ((MessageHolder) source).getMessage();
    }
    return support.getMessage();
  }


  // java.lang.Object overrides --------------------------------------------------------------------------------------

  @Override
  public String toString() {
    return getClass().getSimpleName() + "[" + getSource() + "]";
  }

  @Override
  public boolean isParallelizable() {
    return getSource().isParallelizable() && support.isParallelizable();
  }

  @Override
  public boolean isThreadSafe() {
    return getSource().isThreadSafe() && support.isThreadSafe();
  }

}

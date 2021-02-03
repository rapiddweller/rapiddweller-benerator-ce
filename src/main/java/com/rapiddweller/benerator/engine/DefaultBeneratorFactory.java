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

package com.rapiddweller.benerator.engine;

import com.rapiddweller.benerator.BeneratorFactory;
import com.rapiddweller.benerator.Consumer;
import com.rapiddweller.benerator.engine.parser.xml.BeneratorParseContext;
import com.rapiddweller.benerator.factory.ComplexTypeGeneratorFactory;
import com.rapiddweller.benerator.factory.SimpleTypeGeneratorFactory;
import com.rapiddweller.common.Context;
import com.rapiddweller.common.Converter;
import com.rapiddweller.common.Validator;
import com.rapiddweller.common.context.CaseInsensitiveContext;
import com.rapiddweller.common.context.ContextAware;

/**
 * Default implementation of the abstract {@link BeneratorFactory} class.<br/><br/>
 * Created: 08.09.2010 15:45:25
 *
 * @author Volker Bergmann
 * @since 0.6.4
 */
public class DefaultBeneratorFactory extends BeneratorFactory {

  @Override
  public BeneratorContext createContext(String contextUri) {
    return new DefaultBeneratorContext();
  }

  @Override
  public BeneratorParseContext createParseContext(ResourceManager resourceManager) {
    return new BeneratorParseContext(resourceManager);
  }

  @Override
  public ComplexTypeGeneratorFactory getComplexTypeGeneratorFactory() {
    return ComplexTypeGeneratorFactory.getInstance();
  }

  @Override
  public SimpleTypeGeneratorFactory getSimpleTypeGeneratorFactory() {
    return SimpleTypeGeneratorFactory.getInstance();
  }

  @Override
  public <S, T> Converter<S, T> configureConverter(Converter<S, T> converter, BeneratorContext context) {
    if (converter instanceof ContextAware) {
      ((ContextAware) converter).setContext(context);
    }
    return converter;
  }

  @Override
  public <T> Validator<T> configureValidator(Validator<T> validator, BeneratorContext context) {
    if (validator instanceof ContextAware) {
      ((ContextAware) validator).setContext(context);
    }
    return validator;
  }

  @Override
  public Consumer configureConsumer(Consumer consumer, BeneratorContext context) {
    if (consumer instanceof ContextAware) {
      ((ContextAware) consumer).setContext(context);
    }
    return consumer;
  }

  @Override
  public Context createGenerationContext() {
    return new CaseInsensitiveContext(true);
  }

}

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

import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.factory.DefaultsProvider;
import com.rapiddweller.benerator.factory.GeneratorFactory;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.model.data.ComponentDescriptor;
import com.rapiddweller.model.data.DataModel;
import com.rapiddweller.model.data.DescriptorProvider;
import com.rapiddweller.model.data.TypeDescriptor;
import com.rapiddweller.script.ScriptContext;

import java.util.Locale;

/**
 * A BeneratorContext.<br/><br/>
 * Created at 20.04.2008 06:41:04
 *
 * @author Volker Bergmann
 * @since 0.5.2
 */
public interface BeneratorContext extends GeneratorContext, ScriptContext {

  // simple configuration properties ---------------------------------------------------------------------------------

  /**
   * Sets default encoding.
   *
   * @param defaultEncoding the default encoding
   */
  void setDefaultEncoding(String defaultEncoding);

  /**
   * Sets default line separator.
   *
   * @param defaultLineSeparator the default line separator
   */
  void setDefaultLineSeparator(String defaultLineSeparator);

  /**
   * Sets default locale.
   *
   * @param defaultLocale the default locale
   */
  void setDefaultLocale(Locale defaultLocale);

  /**
   * Sets default dataset.
   *
   * @param defaultDataset the default dataset
   */
  void setDefaultDataset(String defaultDataset);

  /**
   * Sets default page size.
   *
   * @param defaultPageSize the default page size
   */
  void setDefaultPageSize(long defaultPageSize);

  /**
   * Sets default script.
   *
   * @param defaultScript the default script
   */
  void setDefaultScript(String defaultScript);

  /**
   * Sets default null.
   *
   * @param defaultNull the default null
   */
  void setDefaultNull(boolean defaultNull);

  /**
   * Sets default separator.
   *
   * @param defaultSeparator the default separator
   */
  void setDefaultSeparator(char defaultSeparator);

  /**
   * Sets default error handler.
   *
   * @param defaultErrorHandler the default error handler
   */
  void setDefaultErrorHandler(String defaultErrorHandler);

  /**
   * Sets context uri.
   *
   * @param contextUri the context uri
   */
  void setContextUri(String contextUri);

  /**
   * Sets validate.
   *
   * @param validate the validate
   */
  void setValidate(boolean validate);

  /**
   * Sets max count.
   *
   * @param maxCount the max count
   */
  void setMaxCount(Long maxCount);

  /**
   * Is default imports boolean.
   *
   * @return the boolean
   */
  boolean isDefaultImports();

  /**
   * Sets default imports.
   *
   * @param defaultImports the default imports
   */
  void setDefaultImports(boolean defaultImports);

  /**
   * Is default one to one boolean.
   *
   * @return the boolean
   */
  boolean isDefaultOneToOne();

  /**
   * Sets default one to one.
   *
   * @param defaultOneToOne the default one to one
   */
  void setDefaultOneToOne(boolean defaultOneToOne);

  /**
   * Is accept unknown simple types boolean.
   *
   * @return the boolean
   */
  boolean isAcceptUnknownSimpleTypes();

  /**
   * Sets accept unknown simple types.
   *
   * @param acceptUnknownSimpleTypes the accept unknown simple types
   */
  void setAcceptUnknownSimpleTypes(boolean acceptUnknownSimpleTypes);

  // import handling -------------------------------------------------------------------------------------------------

  @Override
  void importClass(String className);

  /**
   * Import package.
   *
   * @param packageName the package name
   */
  void importPackage(String packageName);

  /**
   * Import defaults.
   */
  void importDefaults();

  // service provider sharing ----------------------------------------------------------------------------------------

  @Override
  GeneratorFactory getGeneratorFactory();

  /**
   * Sets generator factory.
   *
   * @param generatorFactory the generator factory
   */
  void setGeneratorFactory(GeneratorFactory generatorFactory);

  /**
   * Gets data model.
   *
   * @return the data model
   */
  DataModel getDataModel();

  /**
   * Sets data model.
   *
   * @param dataModel the data model
   */
  void setDataModel(DataModel dataModel);

  /**
   * Gets defaults provider.
   *
   * @return the defaults provider
   */
  DefaultsProvider getDefaultsProvider();

  /**
   * Sets defaults provider.
   *
   * @param defaultsProvider the defaults provider
   */
  void setDefaultsProvider(DefaultsProvider defaultsProvider);

  /**
   * Gets local descriptor provider.
   *
   * @return the local descriptor provider
   */
  DescriptorProvider getLocalDescriptorProvider();

  /**
   * Add local type.
   *
   * @param type the type
   */
  void addLocalType(TypeDescriptor type);

  /**
   * Gets default component config.
   *
   * @param name the name
   * @return the default component config
   */
  ComponentDescriptor getDefaultComponentConfig(String name);

  /**
   * Sets default component config.
   *
   * @param component the component
   */
  void setDefaultComponentConfig(ComponentDescriptor component);

  // data management -------------------------------------------------------------------------------------------------

  /**
   * Sets global.
   *
   * @param name  the name
   * @param value the value
   */
  void setGlobal(String name, Object value);

  @Override
  ProductWrapper<?> getCurrentProduct();

  @Override
  void setCurrentProduct(ProductWrapper<?> currentProduct);

  /**
   * Has product name in scope boolean.
   *
   * @param productName the product name
   * @return the boolean
   */
  boolean hasProductNameInScope(String productName);

  /**
   * Create sub context benerator context.
   *
   * @param productName the product name
   * @return the benerator context
   */
  BeneratorContext createSubContext(String productName);

}

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

package com.rapiddweller.benerator;

import com.rapiddweller.benerator.factory.GeneratorFactory;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.common.Context;

import java.util.Locale;
import java.util.concurrent.ExecutorService;

/**
 * Provides configuration and variable space for {@link Generator}s.<br/><br/>
 * Created: 14.03.2010 13:14:00
 *
 * @author Volker Bergmann
 * @since 0.6.0
 */
public interface GeneratorContext extends Context {

  // global properties -----------------------------------------------------------------------------------------------

  /**
   * Gets default encoding.
   *
   * @return the default encoding
   */
  String getDefaultEncoding();

  /**
   * Gets default line separator.
   *
   * @return the default line separator
   */
  String getDefaultLineSeparator();

  /**
   * Gets default locale.
   *
   * @return the default locale
   */
  Locale getDefaultLocale();

  /**
   * Gets default dataset.
   *
   * @return the default dataset
   */
  String getDefaultDataset();

  /**
   * Gets default page size.
   *
   * @return the default page size
   */
  long getDefaultPageSize();

  /**
   * Gets default script.
   *
   * @return the default script
   */
  String getDefaultScript();

  /**
   * Is default null boolean.
   *
   * @return the boolean
   */
  boolean isDefaultNull();

  /**
   * Gets default separator.
   *
   * @return the default separator
   */
  char getDefaultSeparator();

  /**
   * Gets default error handler.
   *
   * @return the default error handler
   */
  String getDefaultErrorHandler();

  /**
   * Gets context uri.
   *
   * @return the context uri
   */
  String getContextUri();

  /**
   * Is validate boolean.
   *
   * @return the boolean
   */
  boolean isValidate();

  /**
   * Gets max count.
   *
   * @return the max count
   */
  Long getMaxCount();

  // other features --------------------------------------------------------------------------------------------------

  /**
   * Gets generator factory.
   *
   * @return the generator factory
   */
  GeneratorFactory getGeneratorFactory();

  /**
   * Gets global.
   *
   * @param name the name
   * @return the global
   */
  Object getGlobal(String name);

  /**
   * For name class.
   *
   * @param className the class name
   * @return the class
   */
  Class<?> forName(String className);

  /**
   * Gets executor service.
   *
   * @return the executor service
   */
  ExecutorService getExecutorService();

  /**
   * Resolve relative uri string.
   *
   * @param relativeUri the relative uri
   * @return the string
   */
  String resolveRelativeUri(String relativeUri);

  /**
   * Gets current product.
   *
   * @return the current product
   */
  ProductWrapper<?> getCurrentProduct();

  /**
   * Sets current product.
   *
   * @param currentProduct the current product
   */
  void setCurrentProduct(ProductWrapper<?> currentProduct);

}

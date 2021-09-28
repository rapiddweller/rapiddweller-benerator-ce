/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine;

import java.util.Locale;

/**
 * Root context for a Benerator run.<br/><br/>
 * Created: 28.09.2021 10:01:34
 * @author Volker Bergmann
 * @since 2.1.0
 */
public interface BeneratorRootContext extends BeneratorContext {
  void setDefaultEncoding(String defaultEncoding);
  void setDefaultLineSeparator(String defaultLineSeparator);
  void setDefaultLocale(Locale defaultLocale);
  void setDefaultDataset(String defaultDataset);
  void setDefaultPageSize(long defaultPageSize);
  void setDefaultScript(String defaultScript);
  void setDefaultNull(boolean defaultNull);
  void setDefaultSeparator(char defaultSeparator);
  void setDefaultErrorHandler(String defaultErrorHandler);
  void setValidate(boolean validate);
  void setMaxCount(Long maxCount);
  void setDefaultImports(boolean defaultImports);
  void setDefaultOneToOne(boolean defaultOneToOne);
  void setAcceptUnknownSimpleTypes(boolean acceptUnknownSimpleTypes);
}

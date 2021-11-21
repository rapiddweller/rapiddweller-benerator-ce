/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.factory;

import com.rapiddweller.benerator.BeneratorFactory;
import com.rapiddweller.benerator.IllegalGeneratorStateException;
import com.rapiddweller.common.exception.ExceptionFactory;
import com.rapiddweller.common.file.FileResourceNotFoundException;

/**
 * Extends {@link ExceptionFactory} with Benerator-specific factory methods.<br/><br/>
 * Created: 18.11.2021 06:25:06
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class BeneratorExceptionFactory extends ExceptionFactory {

  public static BeneratorExceptionFactory getInstance() {
    ExceptionFactory result = ExceptionFactory.getInstance();
    if (!(result instanceof BeneratorExceptionFactory)) {
      result = BeneratorFactory.getInstance().createExceptionFactory();
    }
    return (BeneratorExceptionFactory) result;
  }

  public FileResourceNotFoundException beneratorFileNotFound(String uri) {
    return fileNotFound(uri, null);
  }

  public IllegalGeneratorStateException illegalGeneratorState(String message, Exception cause) {
    return new IllegalGeneratorStateException(message, cause);
  }

}

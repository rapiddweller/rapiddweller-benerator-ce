/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.platform.db.postgres;

import com.rapiddweller.benerator.util.ThreadSafeGenerator;
import com.rapiddweller.benerator.wrapper.ProductWrapper;

/**
 * Helper class for test.<br/><br/>
 * Created: 09.11.2021 20:15:26
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class JSONGenerator extends ThreadSafeGenerator<String> {

  public final static String SAMPLE = "{\"sam\": \"ple\"}";

  @Override
  public Class<String> getGeneratedType() {
    return String.class;
  }

  @Override
  public ProductWrapper<String> generate(ProductWrapper<String> wrapper) {
    return wrapper.wrap(SAMPLE);
  }

}

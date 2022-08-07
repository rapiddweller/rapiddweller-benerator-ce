/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.platform.db.postgres;

import com.rapiddweller.benerator.util.ThreadSafeGenerator;
import com.rapiddweller.benerator.wrapper.ProductWrapper;

/**
 * Generates byte arrays for testing.<br/><br/>
 * Created: 09.11.2021 09:39:15
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class ByteaGenerator extends ThreadSafeGenerator<byte[]> {

  @Override
  public Class<byte[]> getGeneratedType() {
    return byte[].class;
  }

  @Override
  public ProductWrapper<byte[]> generate(ProductWrapper<byte[]> wrapper) {
    return wrapper.wrap(new byte[] { 9, 8, 7, 6, 5, 4, 3, 2, 1, 0 });
  }

}

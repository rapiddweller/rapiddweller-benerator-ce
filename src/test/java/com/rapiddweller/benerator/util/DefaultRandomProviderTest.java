/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.util;

import com.rapiddweller.benerator.RandomProvider;
import com.rapiddweller.benerator.test.AbstractRandomProviderTest;

/**
 * Tests the {@link DefaultRandomProvider}.<br/><br/>
 * Created: 13.09.2021 03:17:38
 * @author Volker Bergmann
 * @since 2.0.0
 */
public class DefaultRandomProviderTest extends AbstractRandomProviderTest {

  @Override
  protected RandomProvider getRandom() {
    return new DefaultRandomProvider();
  }

}

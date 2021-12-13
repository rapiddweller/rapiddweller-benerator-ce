/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine;

import org.junit.Test;

/**
 * Tests the &lt;defaultComopnents&gt; element.<br/><br/>
 * Created: 13.12.2021 10:55:31
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class StandardDefaultComponentIntegrationTest extends AbstractDefaultComponentIntegrationTest {

  @Test
  public void testStandardIntegration() {
    checkFile("com/rapiddweller/benerator/engine/defaultComponent-std.ben.xml");
  }

}

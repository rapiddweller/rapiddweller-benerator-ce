/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.platform.db;

import com.rapiddweller.benerator.engine.AbstractDefaultComponentIntegrationTest;
import org.junit.Test;

/**
 * Tests the &lt;defaultComponents&gt; configuration wth a &lt;database&gt;.<br/><br/>
 * Created: 13.12.2021 10:56:35
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class DBDefaultComponentIntegrationTest extends AbstractDefaultComponentIntegrationTest {

  @Test
  public void testDbIntegration() {
    checkFile("com/rapiddweller/platform/db/defaultComponent-db.ben.xml");
  }

}

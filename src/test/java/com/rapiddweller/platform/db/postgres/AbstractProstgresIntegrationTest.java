/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.platform.db.postgres;

import com.rapiddweller.benerator.test.AbstractBeneratorIntegrationTest;
import org.junit.Before;

/**
 * Parent class for Postgres integration tests.<br/><br/>
 * Created: 09.11.2021 10:10:17
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class AbstractProstgresIntegrationTest extends AbstractBeneratorIntegrationTest {

  protected final String folder = getClass().getPackageName().replace('.', '/');

  @Before
  public void setUp() {
    assumePostgresEnabled();
  }

}

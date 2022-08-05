/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.platform.db.postgres;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests Postgres access.<br/><br/>
 * Created: 07.11.2021 17:12:55
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class PostgresUUIDTest extends AbstractProstgresIntegrationTest {

  @Test @Ignore("So far there is no agreed way to set this up uniformly on CI and a local system")
  public void testUUID() {
    assertMinGenerations(100, () -> parseAndExecuteFile(folder + "/postgres-uuid.ben.xml"));
  }

}

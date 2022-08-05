/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.platform.db.postgres;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests sequence support for Postgres.<br/><br/>
 * Created: 09.11.2021 10:15:05
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class PostgresSequenceTest extends AbstractProstgresIntegrationTest {

  @Test @Ignore("So far there is no agreed way to set this up uniformly on CI and a local system")
  public void testSequence() {
    assertMinGenerations(100, () -> parseAndExecuteFile(folder + "/postgres-seq.ben.xml"));
  }

}

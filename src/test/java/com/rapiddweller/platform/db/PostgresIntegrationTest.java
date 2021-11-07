/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.platform.db;

import com.rapiddweller.benerator.test.AbstractBeneratorIntegrationTest;
import com.rapiddweller.common.ConfigUtil;
import org.junit.Assume;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests Postgres access.<br/><br/>
 * Created: 07.11.2021 17:12:55
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class PostgresIntegrationTest extends AbstractBeneratorIntegrationTest {

  final String folder = getClass().getPackageName().replace('.', '/');

  @Test @Ignore("So far there is no agreed way to set this up uniformly on CI and a local system")
  public void testSequence() {
    Assume.assumeTrue("Postgres testing is deactivated", ConfigUtil.isTestActive("postgres"));
    assertMinGenerations(100, () -> parseAndExecuteFile(folder + "/postgres-seq.ben.xml"));
  }

}

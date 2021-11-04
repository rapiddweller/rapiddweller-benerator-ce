/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.environment;

import org.junit.Ignore;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertNotNull;

/**
 * Tests the {@link EnvironmentUtil}.<br/><br/>
 * Created: 04.11.2021 09:46:29
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class EnvironmentUtilTest {

  @Test @Ignore("Only for individual testing, since it requires a 'local.env.properties' in the user's home folder")
  public void testFindEnvironments_global() {
    Map<String, Environment> environments = EnvironmentUtil.findEnvironments();
    System.out.println(environments);
    Environment localEnv = environments.get("local");
    assertNotNull(localEnv);
    assertNotNull(localEnv.getSystem("h2"));
  }

}

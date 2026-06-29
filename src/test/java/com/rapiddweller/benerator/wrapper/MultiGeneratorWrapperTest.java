/* (c) Copyright 2024 by rapiddweller GmbH & Volker Bergmann. All rights reserved. */
package com.rapiddweller.benerator.wrapper;

import com.rapiddweller.benerator.InvalidGeneratorSetupException;
import com.rapiddweller.benerator.SequenceTestGenerator;
import com.rapiddweller.benerator.test.GeneratorTest;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests the source-management and lifecycle behaviour of {@link MultiGeneratorWrapper}
 * through its concrete subclass {@link AlternativeGenerator}.<br/><br/>
 * @author Alexander Kell
 */
public class MultiGeneratorWrapperTest extends GeneratorTest {

  @Test
  public void testSourceManagementAndLifecycle() {
    AlternativeGenerator<Integer> gen = new AlternativeGenerator<>(
        Integer.class, new SequenceTestGenerator<>(1), new SequenceTestGenerator<>(2));
    assertEquals(Integer.class, gen.getGeneratedType());
    assertEquals(2, gen.getSources().size());
    assertNotNull(gen.getSource(0));
    gen.addSource(new SequenceTestGenerator<>(3));
    assertEquals(3, gen.getSources().size());

    gen.init(context);
    gen.isThreadSafe();
    gen.isParallelizable();
    gen.reset();
    gen.close();
  }

  @Test
  public void testSetSourcesReplacesSources() {
    AlternativeGenerator<Integer> gen = new AlternativeGenerator<>(Integer.class, new SequenceTestGenerator<>(1));
    gen.setSources(Arrays.asList(new SequenceTestGenerator<>(5), new SequenceTestGenerator<>(6)));
    assertEquals(2, gen.getSources().size());
  }

  @Test(expected = InvalidGeneratorSetupException.class)
  public void testInitWithoutSourcesFails() {
    new AlternativeGenerator<>(Integer.class).init(context);
  }

}

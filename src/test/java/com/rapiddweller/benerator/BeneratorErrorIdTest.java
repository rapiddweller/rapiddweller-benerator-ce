/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator;

import com.rapiddweller.common.RegexUtil;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests the {@link BeneratorErrorIds} class.<br/><br/>
 * Created: 09.12.2021 06:40:32
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class BeneratorErrorIdTest {

  @Test
  public void testIdUniqueness() throws IllegalAccessException {
    Set<String> usedIds = new HashSet<>();
    String regex = "BEN-[0-9]{4}";
    for (Field field : BeneratorErrorIds.class.getFields()) {
      assertModifier(Modifier.PUBLIC, field);
      assertModifier(Modifier.STATIC, field);
      assertModifier(Modifier.FINAL, field);
      Object value = field.get(null);
      assertTrue(value instanceof String);
      String id = (String) value;
      assertTrue("Id does not match regex " + regex + ": " + id, RegexUtil.matches(regex, id));
      assertFalse("Duplicate ErrorId: " + id, usedIds.contains(id));
      usedIds.add(id);
    }
  }

  private void assertModifier(int modifier, Field field) {
    assertEquals(modifier, field.getModifiers() & modifier);
  }

}

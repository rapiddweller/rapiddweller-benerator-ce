package com.rapiddweller.domain.address;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

/**
 * The type City generator test.
 */
public class CityGeneratorTest {
  /**
   * Test constructor.
   */
  @Test
  public void testConstructor() {
    CityGenerator actualCityGenerator = new CityGenerator("Dataset");
    assertEquals("Dataset", actualCityGenerator.getDataset());
    Class<?> expectedGeneratedType = City.class;
    assertSame(expectedGeneratedType, actualCityGenerator.getGeneratedType());
    assertNull(actualCityGenerator.getSource());
    assertEquals("/com/rapiddweller/dataset/region", actualCityGenerator.getNesting());
    assertEquals(0.0, actualCityGenerator.getWeight(), 0.0);
  }
}


package com.rapiddweller.domain.address;

import com.rapiddweller.benerator.engine.DefaultBeneratorContext;
import com.rapiddweller.common.ui.ConsolePrinter;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

/**
 * Tests the {@link CityGenerator}.
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class CityGeneratorTest {

  @Test
  public void testConstructor() {
    CityGenerator gen = new CityGenerator("US");
    assertEquals("US", gen.getDataset());
    assertSame(City.class, gen.getGeneratedType());
    assertNull(gen.getSource());
    assertEquals("/com/rapiddweller/dataset/region", gen.getNesting());
    assertEquals(0.0, gen.getWeight(), 0.0);
  }

  @Test
  public void testGeneratedObjectGraph_US() {
    checkCountry(Country.US);
  }

  @Test
  public void testGeneratedObjectGraph_DE() {
    checkCountry(Country.GERMANY);
  }

  @Test
  public void testGeneratedObjectGraph_BR() {
    checkCountry(Country.BRAZIL);
  }

  @Test
  public void testGeneratedObjectGraph_CH() {
    checkCountry(Country.SWITZERLAND);
  }

  @Test
  public void testGeneratedObjectGraph_GB() {
    checkCountry(Country.GREAT_BRITAIN);
  }

  private void checkCountry(Country country) {
    CityGenerator g = new CityGenerator(country.getIsoCode());
    DefaultBeneratorContext context = new DefaultBeneratorContext();
    g.init(context);
    for (int i = 0; i < 20; i++) {
      City city = g.generate();
      ConsolePrinter.printStandard(
          city.toString(),
          "  " + city.getState().toString(),
          "  " + city.getCountry().toString());
      assertNotNull(city);
      assertNotNull(city.getName());
      State state = city.getState();
      assertNotNull(state);
      assertNotNull(state.getId());
      assertNotNull(state.getName());
      assertEquals(country, city.getCountry());
      assertEquals(country, state.getCountry());
    }
  }

}


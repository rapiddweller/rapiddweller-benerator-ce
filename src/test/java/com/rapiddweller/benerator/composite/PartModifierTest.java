/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.composite;

import com.rapiddweller.benerator.engine.DefaultBeneratorContext;
import com.rapiddweller.benerator.sample.SequenceGenerator;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.common.CollectionUtil;
import com.rapiddweller.model.data.DataModel;
import com.rapiddweller.model.data.DefaultDescriptorProvider;
import com.rapiddweller.model.data.DescriptorProvider;
import com.rapiddweller.model.data.Entity;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests the {@link PartModifier}.<br/><br/>
 * Created: 29.09.2021 19:11:56
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class PartModifierTest {

  PartModifier modifier;
  DescriptorProvider dp;
  DefaultBeneratorContext context;

  @Before
  public void setUp() {
    DataModel dataModel = new DataModel();
    dp = new DefaultDescriptorProvider("default", dataModel);
    List<GenerationStep<Entity>> steps = new ArrayList<>();
    SequenceGenerator<String> makerGen = new SequenceGenerator<>(String.class, "BMW", "BMW");
    steps.add(new PlainEntityComponentBuilder("maker", makerGen, null));
    context = new DefaultBeneratorContext();
    modifier = new PartModifier("cars", steps, null);
    modifier.init(context);
  }

  @After
  public void tearDown() {
    modifier.close();
    context.close();
  }

  @Test
  public void testThreadAware() {
    assertTrue(modifier.isThreadSafe());
    assertTrue(modifier.isParallelizable());
  }

  @Test
  public void testPart_entity() {
    Entity alice = new Entity("person", dp);
    alice.set("cars", createCar("Audi"));
    context.setCurrentProduct(new ProductWrapper<>(alice));
    modifier.execute(context);
    Entity car = (Entity) alice.get("cars");
    assertNotNull(car);
    assertEquals("BMW", car.get("maker"));
  }

  @Test
  public void testPart_array() {
    Entity alice = new Entity("person", dp);
    Entity a3 = createCar("Audi");
    Entity v5 = createCar("Volvo");
    alice.set("cars", new Entity[] { a3, v5 });
    context.setCurrentProduct(new ProductWrapper<>(alice));
    modifier.execute(context);
    Entity[] cars = (Entity[]) alice.get("cars");
    assertNotNull(cars);
    assertEquals(2, cars.length);
    for (Entity car : cars) {
      assertEquals("BMW", car.get("maker"));
    }
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testPart_list() {
    Entity alice = new Entity("person", dp);
    Entity a3 = createCar("Audi");
    Entity v5 = createCar("Volvo");
    alice.set("cars", CollectionUtil.toList(a3, v5));
    context.setCurrentProduct(new ProductWrapper<>(alice));
    modifier.execute(context);
    List<Entity> cars = (List<Entity>) alice.get("cars");
    assertNotNull(cars);
    for (Entity car : cars) {
      assertEquals("BMW", car.get("maker"));
    }
  }

  private Entity createCar(String maker) {
    Entity result = new Entity("car", dp);
    result.set("maker", maker);
    return result;
  }

}

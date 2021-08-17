package com.rapiddweller.platform;

import com.rapiddweller.benerator.test.ModelTest;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.model.data.SimpleTypeDescriptor;
import org.junit.Before;

public abstract class ABCTest extends ModelTest {

  protected ComplexTypeDescriptor personType;
  protected Entity aliceEntity, bobEntity, carEntity, charlyEntity;

  @Before
  public void setUpABC() {
    SimpleTypeDescriptor stringType = dataModel.getPrimitiveTypeDescriptor(String.class);
    SimpleTypeDescriptor intType = dataModel.getPrimitiveTypeDescriptor(int.class);
    ComplexTypeDescriptor carType = createComplexType("Car");
    carType.addComponent(createPart("name", stringType));
    personType = createComplexType("Person");
    personType.addComponent(createPart("name", stringType));
    personType.addComponent(createPart("age", intType));
    personType.addComponent(createPart("notes", stringType));
    personType.addComponent(createPart("car", carType));
    aliceEntity = createEntity("Person", "name", "Alice", "age", 23, "notes", "");
    bobEntity = createEntity("Person", "name", "Bob", "age", 34, "notes", null);
    carEntity = createEntity("Car", "name", "Audi A3");
    charlyEntity = createEntity("Person", "name", "Charly", "age", 45, "notes", null, "car", carEntity);
  }

}

package com.rapiddweller.benerator.script;

import com.rapiddweller.benerator.test.Person;
import org.graalvm.polyglot.Value;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class GraalValueConverterTest {

  Person person = new Person();
  byte[] bytes = "djfljsdlkfjsd".getBytes();
  String[] cars = {"Volvo", "BMW", "Ford", "Mazda"};

  Value i = Value.asValue(18218312);
  Value f = Value.asValue(1.8218312);
  Value s = Value.asValue("djfljsdlkfjsd");
  Value p = Value.asValue(person);
  Value b = Value.asValue(true);
  Value by = Value.asValue(bytes);
  Value array = Value.asValue(cars);


  @Test
  public void testConstructor() {
    GraalValueConverter actualGraalValueConverter = new GraalValueConverter();
    Class<?> expectedSourceType = Value.class;
    assertSame(expectedSourceType, actualGraalValueConverter.getSourceType());
    assertTrue(actualGraalValueConverter.isThreadSafe());
    assertTrue(actualGraalValueConverter.isParallelizable());
    Class<?> expectedTargetType = Object.class;
    assertSame(expectedTargetType, actualGraalValueConverter.getTargetType());
  }

  @Test
  public void testConverter() {
    GraalValueConverter actualGraalValueConverter = new GraalValueConverter();
    Assert.assertEquals("djfljsdlkfjsd", actualGraalValueConverter.convert(s));
    Assert.assertEquals(person, actualGraalValueConverter.convert(p));
    Assert.assertEquals(18218312, actualGraalValueConverter.convert(i));
    Assert.assertEquals(1.8218312, actualGraalValueConverter.convert(f));
    Assert.assertEquals(true, actualGraalValueConverter.convert(b));
    Assert.assertEquals(bytes, actualGraalValueConverter.convert(by));
    Assert.assertEquals(cars, actualGraalValueConverter.convert(array));
  }
}


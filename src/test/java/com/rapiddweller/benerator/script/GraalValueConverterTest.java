package com.rapiddweller.benerator.script;

import com.rapiddweller.benerator.test.Person;
import org.graalvm.polyglot.Value;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class GraalValueConverterTest {

  Person person = new Person();
  byte[] bytes = "djfljsdlkfjsd".getBytes();
  String[] cars = {"Volvo", "BMW", "Ford", "Mazda"};
  Date myDate = new Date();
  HashMap<String, Object> capitalCities = new HashMap<>();
  HashSet<String> carsHash = new HashSet<String>();


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
    capitalCities.put("Germany", "Berlin");
    capitalCities.put("Norway", "Oslo");
    capitalCities.put("USA", "Washington DC");

    carsHash.add("Volvo");
    carsHash.add("BMW");
    carsHash.add("Ford");
    carsHash.add("Mazda");

    capitalCities.put("Cars", carsHash);

    Value i = Value.asValue(18218312);
    Value f = Value.asValue(1.8218312);
    Value s = Value.asValue("djfljsdlkfjsd");
    Value p = Value.asValue(person);
    Value b = Value.asValue(true);
    Value by = Value.asValue(bytes);
    Value array = Value.asValue(cars);
    Value date = Value.asValue(myDate);
    Value hash = Value.asValue(capitalCities);
    Value hashSet = Value.asValue(carsHash);

    GraalValueConverter actualGraalValueConverter = new GraalValueConverter();
    Assert.assertEquals("djfljsdlkfjsd", actualGraalValueConverter.convert(s));
    Assert.assertEquals(person, actualGraalValueConverter.convert(p));
    Assert.assertEquals(18218312, actualGraalValueConverter.convert(i));
    Assert.assertEquals(1.8218312, actualGraalValueConverter.convert(f));
    Assert.assertEquals(true, actualGraalValueConverter.convert(b));
    Assert.assertEquals(bytes, actualGraalValueConverter.convert(by));
    Assert.assertEquals(cars, actualGraalValueConverter.convert(array));
    Assert.assertEquals(myDate, actualGraalValueConverter.convert(date));
    Assert.assertEquals(capitalCities, actualGraalValueConverter.convert(hash));
    Assert.assertEquals(carsHash, actualGraalValueConverter.convert(hashSet));
  }
}


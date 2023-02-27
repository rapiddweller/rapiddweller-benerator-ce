package com.rapiddweller.domain.faker;

import com.github.javafaker.Faker;
import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

/*
 * Generate data, base on Java Faker dependency
 * must input 2 parameter: topic and property
 *
 */

public class FakerGenerator extends JavaFaker {
  private Faker faker;

  public FakerGenerator() {
    this(Locale.getDefault());
  }

  public FakerGenerator(Locale locale) {
    this(locale, "name", "fullName");
  }

  public FakerGenerator(String topic, String property) {
    this(Locale.getDefault(), topic, property);
  }

  protected FakerGenerator(Locale locale, String topic, String property) {
    super(locale, topic, property);
  }

  @Override
  public void init(GeneratorContext context) {
    faker = new Faker(getLocale());
    super.init(context);
  }

  @Override
  protected Object fakerHandler(String topic, String property) {
    Object result;
    try {
      Method method = getMethodIgnoreCase(topic, "topic", faker.getClass());
      Object obj = method.invoke(faker);

      Method md = getMethodIgnoreCase(property, "property", obj.getClass());
      result = md.invoke(obj);

    } catch (InvocationTargetException | IllegalAccessException e) {
      throw BeneratorExceptionFactory.getInstance().illegalGeneratorState(
          "Something went wrong by using the faker", e);
    }
    return result;
  }
}

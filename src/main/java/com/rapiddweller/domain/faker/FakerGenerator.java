package com.rapiddweller.domain.faker;

import com.github.javafaker.Faker;
import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.NonNullGenerator;
import com.rapiddweller.benerator.dataset.DatasetBasedGenerator;
import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.benerator.wrapper.CompositeGenerator;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.common.exception.IllegalArgumentError;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/*
 * Generate data, base on Java Faker dependency
 * must input 2 parameter: topic and property
 *
 */

public class FakerGenerator extends CompositeGenerator<String>
    implements DatasetBasedGenerator<String>, NonNullGenerator<String> {

  private static final String REGION_NESTING = "com/rapiddweller/dataset/region";
  private String datasetName;
  private Locale locale;
  private final String topic;
  private final String property;
  private Faker faker;

  // constructors ----------------------------------------------------------------------------------------------------

  public FakerGenerator() {
    this(Locale.getDefault());
  }

  public FakerGenerator(Locale locale) {
    this(locale, "name", "fullName");
  }

  public FakerGenerator(String topic, String property) {
    this(Locale.getDefault(), topic, property);
  }

  public FakerGenerator(Locale locale, String topic, String property) {
    super(String.class);
    this.locale = locale;
    this.topic = topic;
    this.property = property;
  }

  // properties ------------------------------------------------------------------------------------------------------

  public Locale getLocale() {
    return locale;
  }

  public void setLocale(Locale locale) {
    this.locale = locale;
  }

  @Override
  public String getDataset() {
    return datasetName;
  }

  // DatasetBasedGenerator interface implementation ------------------------------------------------------------------

  public void setDataset(String datasetName) {
    this.datasetName = datasetName;
  }

  @Override
  public String getNesting() {
    return REGION_NESTING;
  }

  @Override
  public String generateForDataset(String dataset) {
    return null;
  }

  // Generator interface ---------------------------------------------------------------------------------------------
  @Override
  public boolean isThreadSafe() {
    return true;
  }

  @Override
  public boolean isParallelizable() {
    return false;
  }

  @Override
  public void init(GeneratorContext context) {
    faker = new Faker(locale);
    super.init(context);
  }

  @Override
  public ProductWrapper<String> generate(ProductWrapper<String> wrapper) {
    return wrapper.wrap(generate());
  }

  @Override
  public String generate() {
    return fakerHandler(topic, property);
  }

  public List<Method> getMethodsIgnoreCase
      (Class<?> clazz, String methodName) {

    return Arrays.stream(clazz.getMethods())
        .filter(m -> m.getName().equalsIgnoreCase(methodName))
        .collect(Collectors.toList());
  }

  // get method from faker case insensitive
  public Method getMethodIgnoreCase(String methodName, String fakerPart, Class<?> clazz) {

    List<Method> methods = getMethodsIgnoreCase(clazz, methodName);
    if (methods.isEmpty()) {
      throw new IllegalArgumentError("Can't find " + fakerPart + " " + methodName + " in faker library");
    }
    return methods.get(0);
  }

  private String fakerHandler(String topic, String property) {
    String result;
    try {
      Method method = getMethodIgnoreCase(topic, "topic", faker.getClass());
      Object obj = method.invoke(faker);

      Method md = getMethodIgnoreCase(property, "property", obj.getClass());
      result = (String) md.invoke(obj);

    } catch (InvocationTargetException | IllegalAccessException e) {
      throw BeneratorExceptionFactory.getInstance().illegalGeneratorState(
          "Something went wrong by using the faker", e);
    }
    return result;
  }
}

package com.rapiddweller.domain.faker;

import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.NonNullGenerator;
import com.rapiddweller.benerator.dataset.DatasetBasedGenerator;
import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.benerator.wrapper.CompositeGenerator;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.common.exception.IllegalArgumentError;
import net.datafaker.Faker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/*
 * Generate data, base on DataFaker dependency
 * must input 2 parameter: topic and property
 *
 */

public class DataFakerGenerator extends CompositeGenerator
    implements DatasetBasedGenerator, NonNullGenerator {

  private static final String REGION_NESTING = "com/rapiddweller/dataset/region";
  private String datasetName;
  private Locale locale;
  private final String topic;
  private final String property;
  private Faker faker;

  private final List<String> ignoreTopicsList = Arrays.asList(
          "options", "stream", "getFaker", "fakeValuesService", "getClass", "hashCode", "instance", "notify", "notifyAll", "random", "toString", "wait");

  private final List<String> ignorePropertiesList = Arrays.asList(
          "toString", "hashCode", "getClass", "getFaker"
  );

  private HashMap<String, List<String>> errorProperties;

  // constructors ----------------------------------------------------------------------------------------------------

  public DataFakerGenerator() {
    this(Locale.getDefault());
  }

  public DataFakerGenerator(Class generatedType) {
    this(generatedType, Locale.getDefault(), "name", "fullName");
  }

  public DataFakerGenerator(Locale locale) {
    this(Object.class, locale, "name", "fullName");
  }

  public DataFakerGenerator(String topic, String property) {
    this(Object.class, Locale.getDefault(), topic, property);
  }

  public DataFakerGenerator(Class generatedType, Locale locale, String topic, String property) {
    super(generatedType);
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
  public Object generateForDataset(String dataset) {
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

    errorProperties = getErrorMethod();

    super.init(context);
  }

  @Override
  public ProductWrapper generate(ProductWrapper wrapper) {
    return wrapper.wrap(generate());
  }

  @Override
  public Object generate() {
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
      throw new IllegalArgumentError("Can't find " + fakerPart + " " + methodName + " in data faker library");
    }

    //only use method with no parameter
    for (Method m : methods){
      if (m.getParameterTypes().length==0){
        return m;
      }
    }
    throw new IllegalArgumentError(fakerPart + " " + methodName + " is not supported");
  }

  private Object fakerHandler(String topic, String property) {

    //prevent to access ignoreTopicsList
    if (ignoreTopicsList.contains(topic)){
      throw new IllegalArgumentError("Can't find topic " + topic + " in data faker library");
    }

    //prevent to access ignorePropertiesList
    if (ignorePropertiesList.contains(property)){
      throw new IllegalArgumentError("Can't find property " + property + " in data faker library");
    }

    //prevent to access errorProperties
    List<String> errorPropertiesList = errorProperties.get(topic);
    if ((errorPropertiesList!=null) && errorPropertiesList.contains(property)){
      throw new IllegalArgumentError("Can't find property " + property + " in data faker library");
    }

    Object result;

    try {
      Method method = getMethodIgnoreCase(topic, "topic", faker.getClass());
      Object obj = method.invoke(faker);

      Method md = getMethodIgnoreCase(property, "property", obj.getClass());
      result = md.invoke(obj);

    } catch (InvocationTargetException | IllegalAccessException e) {
      throw BeneratorExceptionFactory.getInstance().illegalGeneratorState(
          "Something went wrong by using the data faker", e);
    }
    return result;
  }

  //get Map of Data Faker error methods, Key is topic names
  private static HashMap<String, List<String>> getErrorMethod(){

    HashMap<String, List<String>> errorTopics = new HashMap<>();

    errorTopics.put("address", Arrays.asList("mailBox","zipCodePlus4"));
    errorTopics.put("animal", Arrays.asList("species","genus","scientificName"));
    errorTopics.put("aviation", Arrays.asList("flight","airline"));
    errorTopics.put("breakingBad", Arrays.asList("character","episode"));
    errorTopics.put("business", Arrays.asList("creditCardNumber","securityCode"));
    errorTopics.put("collection", Arrays.asList("generate"));
    errorTopics.put("commerce", Arrays.asList("brand","vendor"));
    errorTopics.put("compass", Arrays.asList("word","azimuth","abbreviation"));
    errorTopics.put("doctorWho", Arrays.asList("villain"));
    errorTopics.put("fallout", Arrays.asList("location","character","quote","faction"));
    errorTopics.put("finance", Arrays.asList("nyseTicker","nasdaqTicker","stockMarket"));
    errorTopics.put("freshPrinceOfBelAir", Arrays.asList("characters","quotes","celebrities"));
    errorTopics.put("gender", Arrays.asList("shortBinaryTypes"));
    errorTopics.put("idNumber", Arrays.asList("validKoKrRrn"));
    errorTopics.put("internet", Arrays.asList("httpMethod","botUserAgentAny"));
    errorTopics.put("medical", Arrays.asList("diagnosisCode","procedureCode"));
    errorTopics.put("phoneNumber", Arrays.asList("phoneNumberInternational"));
    errorTopics.put("pokemon", Arrays.asList("type"));
    errorTopics.put("science", Arrays.asList("unit","tool","quark","leptons","bosons", "elementSymbol"));
    errorTopics.put("starTrek", Arrays.asList("species","klingon"));
    errorTopics.put("starWars", Arrays.asList("callSign"));
    errorTopics.put("vehicle", Arrays.asList("upholstery","engine","doors","upholsteryColor","upholsteryFabric"));
    errorTopics.put("witcher", Arrays.asList("sign","potion","book"));

    return errorTopics;
  }

}

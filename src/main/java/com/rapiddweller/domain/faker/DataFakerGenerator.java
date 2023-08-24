package com.rapiddweller.domain.faker;

import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.common.exception.IllegalArgumentError;
import net.datafaker.Faker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/*
 * Generate data, base on DataFaker dependency
 * must input 2 parameter: topic and property
 *
 */

public class DataFakerGenerator extends JavaFaker {

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

  public DataFakerGenerator(Locale locale) {
    this(locale, "name", "fullName");
  }

  public DataFakerGenerator(String topic, String property) {
    this(Locale.getDefault(), topic, property);
  }

  public DataFakerGenerator(Locale locale, String topic, String property) {
    super(locale, topic, property);
  }

  @Override
  public void init(GeneratorContext context) {
    faker = new Faker(getLocale());
    errorProperties = getErrorMethod();

    super.init(context);
  }

  @Override
  protected Object fakerHandler(String topic, String property) {

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

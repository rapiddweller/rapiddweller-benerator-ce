package com.rapiddweller.domain.faker;

import com.github.javafaker.Faker;
import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.NonNullGenerator;
import com.rapiddweller.benerator.dataset.DatasetBasedGenerator;
import com.rapiddweller.benerator.wrapper.CompositeGenerator;
import com.rapiddweller.benerator.wrapper.ProductWrapper;

import java.lang.reflect.Method;
import java.util.Locale;

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
    private String topic;
    private String property;
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

    private String fakerHandler(String topic, String property){
        String result;
        try {

            Method method = faker.getClass().getMethod(topic);
            Object obj = method.invoke(faker);

            Method md = obj.getClass().getMethod(property);
            result = (String) md.invoke(obj);

        }catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}

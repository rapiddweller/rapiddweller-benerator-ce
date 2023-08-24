package com.rapiddweller.domain.faker;

import com.rapiddweller.benerator.NonNullGenerator;
import com.rapiddweller.benerator.dataset.DatasetBasedGenerator;
import com.rapiddweller.benerator.wrapper.CompositeGenerator;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.common.exception.IllegalArgumentError;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

abstract class JavaFaker extends CompositeGenerator<Object>
        implements DatasetBasedGenerator<Object>, NonNullGenerator<Object> {

    private static final String REGION_NESTING = "com/rapiddweller/dataset/region";
    private String datasetName;
    private Locale locale;
    private final String topic;
    private final String property;

    // constructors ----------------------------------------------------------------------------------------------------

    protected JavaFaker(Locale locale, String topic, String property) {
        super(Object.class);
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

    // DatasetBasedGenerator interface implementation ------------------------------------------------------------------

    @Override
    public String getNesting() {
        return REGION_NESTING;
    }

    public void setDataset(String datasetName) {
        this.datasetName = datasetName;
    }

    @Override
    public String getDataset() {
        return datasetName;
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
    public ProductWrapper<Object> generate(ProductWrapper<Object> wrapper) {
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

        //prevent to access toString and getClass method
        if (methodName.equalsIgnoreCase("toString")
                && methodName.equalsIgnoreCase("getClass")){
            throw new IllegalArgumentError("Can't find " + fakerPart + " " + methodName + " in faker library");
        }

        List<Method> methods = getMethodsIgnoreCase(clazz, methodName);
        if (methods.isEmpty()) {
            throw new IllegalArgumentError("Can't find " + fakerPart + " " + methodName + " in faker library");
        }

        //only use method with no parameter
        for (Method m : methods){
            if (m.getParameterTypes().length==0){
                return m;
            }
        }
        throw new IllegalArgumentError(fakerPart + " " + methodName + " is not supported");
    }

    protected abstract Object fakerHandler(String topic, String property);
}
package com.rapiddweller.benerator.engine.statement;

import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.ResourceManager;
import com.rapiddweller.benerator.engine.Statement;
import com.rapiddweller.benerator.storage.AbstractStorageSystem;
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.common.Expression;
import com.rapiddweller.common.exception.ExceptionFactory;
import com.rapiddweller.model.data.DataModel;
import com.rapiddweller.platform.nosql.CustomStorageSystem;
import com.rapiddweller.script.expression.ExpressionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class DefineStorageSystemStatement implements Statement {

    private static final ExceptionFactory EXCEPTION_FACTORY = ExceptionFactory.getInstance();

    private static final Logger logger = LoggerFactory.getLogger(DefineStorageSystemStatement.class);

    private final Expression<String> id;
    Expression<Class<? extends CustomStorageSystem>> clazz;
    private final Expression<Map<String, Expression<String>>> classParams;
    private final ResourceManager resourceManager;

    public DefineStorageSystemStatement(Expression<String> id,
                                        Expression<Class<? extends CustomStorageSystem>> clazz,
                                        Expression<Map<String, Expression<String>>> classParams,
                                        ResourceManager resourceManager) {
        if (id == null) {
            throw new ConfigurationError("No database id defined");
        }
        this.id = id;
        this.clazz = clazz;
        this.classParams = classParams;
        this.resourceManager = resourceManager;
    }

    @Override
    public boolean execute(BeneratorContext context) {
        logger.debug("Instantiating storage system with id '{}'", id);
        String idValue = id.evaluate(context);
        Class<? extends CustomStorageSystem> clazzValue = clazz.evaluate(context);
        AbstractStorageSystem storageSystem = createStorageSystem(context, idValue, clazzValue, getEvaluatedClassParams(context));

        context.setGlobal(idValue, storageSystem);
        context.getDataModel().addDescriptorProvider(storageSystem, context.isValidate());
        resourceManager.addResource(storageSystem);

        return true;
    }

    private AbstractStorageSystem createStorageSystem(BeneratorContext context,
                                                      String id,
                                                      Class<? extends CustomStorageSystem> clazz,
                                                      Map<String, String> classParams) {
        return createNewInstance(context, clazz, id, classParams);
    }

    private AbstractStorageSystem createNewInstance(BeneratorContext context, Class<?> storageSystemClass,
                                                    String id,
                                                    Map<String, String> classParams) {
        try {
            return (AbstractStorageSystem) storageSystemClass
                    .getDeclaredConstructor(String.class, DataModel.class, Map.class)
                    .newInstance(id, context.getDataModel(), classParams);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw EXCEPTION_FACTORY.internalError(format("Cannot initialize class %s for storage system %s.", classParams, id), e);
        }
    }

    private Map<String, String> getEvaluatedClassParams(BeneratorContext context) {
        return classParams.evaluate(context).entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> ExpressionUtil.evaluate(e.getValue(), context)));
    }
 }




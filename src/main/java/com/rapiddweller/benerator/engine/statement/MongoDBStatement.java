package com.rapiddweller.benerator.engine.statement;

import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.ResourceManager;
import com.rapiddweller.benerator.engine.Statement;
import com.rapiddweller.common.Expression;
import com.rapiddweller.platform.mongodb.MongoDBSystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoDBStatement implements Statement {

    private static final Logger logger = LoggerFactory.getLogger(MongoDBStatement.class);

    private final Expression<String> id;
    private final Expression<String> environment;
    private final Expression<String> system;
    private final Expression<String> host;
    private final Expression<Integer> port;
    private final Expression<String> database;
    private final Expression<String> user;
    private final Expression<String> password;
    private final Expression<Boolean> clean;
    private final ResourceManager resourceManager;

    public MongoDBStatement(Expression<String> id, Expression<String> environment, Expression<String> system, Expression<String> host, Expression<Integer> port,
                            Expression<String> database, Expression<String> user, Expression<String> password,
                            Expression<Boolean> clean, ResourceManager resourceManager) {
        this.id = id;
        this.environment = environment;
        this.system = system;
        this.host = host;
        this.port = port;
        this.database = database;
        this.user = user;
        this.password = password;
        this.clean = clean;
        this.resourceManager = resourceManager;
    }

    @Override
    public boolean execute(BeneratorContext context) {
        logger.debug("Instantiating storage system with id '{}'", id);
        MongoDBSystem mongoDBSystem = createMongoDBSystem(context);
        context.setGlobal(id.evaluate(context), mongoDBSystem);
        context.getDataModel().addDescriptorProvider(mongoDBSystem, context.isValidate());
        resourceManager.addResource(mongoDBSystem);
        return true;
    }

    private MongoDBSystem createMongoDBSystem(BeneratorContext context) {
        if (environment != null) {
            return new MongoDBSystem(
                    id.evaluate(context),
                    environment.evaluate(context),
                    system.evaluate(context),
                    context);
        }
        return new MongoDBSystem(
                context.getDataModel(),
                id.evaluate(context),
                host.evaluate(context),
                port.evaluate(context),
                database.evaluate(context),
                user.evaluate(context),
                password.evaluate(context),
                clean.evaluate(context));
    }
 }




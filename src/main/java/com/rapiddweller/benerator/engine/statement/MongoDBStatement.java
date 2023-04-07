package com.rapiddweller.benerator.engine.statement;

import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.ResourceManager;
import com.rapiddweller.benerator.engine.Statement;
import com.rapiddweller.common.Expression;
import com.rapiddweller.platform.mongodb.MongoDBSystem;

import com.rapiddweller.script.expression.ExpressionUtil;
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
  private final Expression<String> authenticationDatabase;
  private final Expression<String> authMechanism;
  private final Expression<String> password;
  private final Expression<Boolean> clean;
  private final ResourceManager resourceManager;

  public MongoDBStatement(Expression<String> id, Expression<String> environment, Expression<String> system, Expression<String> host,
                          Expression<Integer> port,
                          Expression<String> database, Expression<String> user, Expression<String> password,
                          Expression<String> authenticationDatabase, Expression<String> authMechanism, Expression<Boolean> clean,
                          ResourceManager resourceManager) {
    this.id = id;
    this.environment = environment;
    this.system = system;
    this.host = host;
    this.port = port;
    this.database = database;
    this.user = user;
    this.password = password;
    this.authenticationDatabase = authenticationDatabase;
    this.authMechanism = authMechanism;
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
    String envName = null;
    String systemName = null;

    if (host==null && environment==null) {
      // check if environement is null or empty set to default "environment"
      envName = "environment";
    }

    if (envName != null || environment != null) {
      logger.debug("Instantiating mongodb with id '{}'", id);
      String idValue = id.evaluate(context);

      if (envName == null) {
        envName = ExpressionUtil.evaluate(environment, context);
      }

      if (system == null || ExpressionUtil.evaluate(system, context).isEmpty()) {
        systemName = idValue;
      }
      else {
        systemName = ExpressionUtil.evaluate(system, context);
      }
      return new MongoDBSystem(
          id.evaluate(context),
          envName,
          systemName,
          context);
    } else {
      // check each attribute separately if null before evaluate
      String hostTemp = this.host.evaluate(context);
      Integer portTemp = (this.port != null) ? this.port.evaluate(context) : null;
      String databaseTemp = (this.database != null) ? this.database.evaluate(context) : null;
      String userTemp = (this.user != null) ? this.user.evaluate(context) : null;
      String passwordTemp = (this.password != null) ? this.password.evaluate(context) : null;
      String authenticationDatabaseTemp = (this.authenticationDatabase != null) ? this.authenticationDatabase.evaluate(context) : null;
      String authMechanismTemp = (this.authMechanism != null) ? this.authMechanism.evaluate(context) : null;
      Boolean cleanTemp = (this.clean != null) ? this.clean.evaluate(context) : null;
      return new MongoDBSystem(
          context.getDataModel(),
          id.evaluate(context),
          hostTemp,
          portTemp,
          databaseTemp,
          userTemp,
          passwordTemp,
          authenticationDatabaseTemp,
          authMechanismTemp,
          cleanTemp);
    }
  }
}




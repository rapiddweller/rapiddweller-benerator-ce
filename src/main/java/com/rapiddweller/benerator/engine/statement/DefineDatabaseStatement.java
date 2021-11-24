/*
 * (c) Copyright 2006-2020 by rapiddweller GmbH & Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General Public License.
 *
 * For redistributing this software or a derivative work under a license other
 * than the GPL-compatible Free Software License as defined by the Free
 * Software Foundation or approved by OSI, you must first obtain a commercial
 * license to this software product from rapiddweller GmbH & Volker Bergmann.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED CONDITIONS,
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE
 * HEREBY EXCLUDED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.rapiddweller.benerator.engine.statement;

import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.ResourceManager;
import com.rapiddweller.benerator.engine.Statement;
import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.platform.db.AbstractDBSystem;
import com.rapiddweller.platform.db.DefaultDBSystem;
import com.rapiddweller.script.Expression;
import com.rapiddweller.script.expression.ExpressionUtil;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * Executes a &lt;database/&gt; from an XML descriptor.<br/><br/>
 * Created at 23.07.2009 07:13:02
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class DefineDatabaseStatement implements Statement {

  private static final Logger logger = LoggerFactory.getLogger(DefineDatabaseStatement.class);

  private final Expression<String> id;
  private final Expression<String> environment;
  private final Expression<String> system;
  private final Expression<String> url;
  private final Expression<String> driver;
  private final Expression<String> user;
  private final Expression<String> password;
  private final Expression<String> catalog;
  private final Expression<String> schema;
  private final Expression<Boolean> metaCache;
  private final Expression<String> tableFilter;
  private final Expression<String> includeTables;
  private final Expression<String> excludeTables;
  private final Expression<Boolean> batch;
  private final Expression<Integer> fetchSize;
  private final Expression<Boolean> readOnly;
  private final Expression<Boolean> lazy;
  private final Expression<Boolean> acceptUnknownColumnTypes;
  private final ResourceManager resourceManager;

  public DefineDatabaseStatement(Expression<String> id, Expression<String> environment, Expression<String> system,
                                 Expression<String> url, Expression<String> driver, Expression<String> user, Expression<String> password,
                                 Expression<String> catalog, Expression<String> schema, Expression<Boolean> metaCache,
                                 Expression<String> tableFilter, Expression<String> includeTables, Expression<String> excludeTables,
                                 Expression<Boolean> batch, Expression<Integer> fetchSize, Expression<Boolean> readOnly, Expression<Boolean> lazy,
                                 Expression<Boolean> acceptUnknownColumnTypes, ResourceManager resourceManager) {
    if (id == null) {
      throw BeneratorExceptionFactory.getInstance().configurationError("No database id defined");
    }
    this.id = id;
    this.environment = environment;
    this.system = system;
    this.url = url;
    this.driver = driver;
    this.user = user;
    this.password = password;
    this.catalog = catalog;
    this.schema = schema;
    this.metaCache = metaCache;
    this.tableFilter = tableFilter;
    this.includeTables = includeTables;
    this.excludeTables = excludeTables;
    this.batch = batch;
    this.fetchSize = fetchSize;
    this.readOnly = readOnly;
    this.lazy = lazy;
    this.acceptUnknownColumnTypes = acceptUnknownColumnTypes;
    this.resourceManager = resourceManager;
  }

  @Override
  @SuppressWarnings("deprecation")
  public boolean execute(BeneratorContext context) {
    logger.debug("Instantiating database with id '{}'", id);
    String idValue = id.evaluate(context);

    // DB config is based on the (optional) environment setting
    AbstractDBSystem db = accessDatabase(idValue, ExpressionUtil.evaluate(environment, context), ExpressionUtil.evaluate(system, context), context);

    // The user may override single or all settings from the environment configuration
    String urlValue = ExpressionUtil.evaluate(url, context);
    if (urlValue != null) {
      db.setUrl(urlValue);
    }
    String driverValue = ExpressionUtil.evaluate(driver, context);
    if (driverValue != null) {
      db.setDriver(driverValue);
    }
    String userValue = ExpressionUtil.evaluate(user, context);
    if (userValue != null) {
      db.setUser(userValue);
    }
    String passwordValue = ExpressionUtil.evaluate(password, context);
    if (passwordValue != null) {
      db.setPassword(passwordValue);
    }
    String catalogValue = ExpressionUtil.evaluate(catalog, context);
    if (catalogValue != null) {
      db.setCatalog(catalogValue);
    }
    String schemaValue = ExpressionUtil.evaluate(schema, context);
    if (schemaValue != null) {
      db.setSchema(schemaValue);
    }

    // apply all other settings without further validation
    db.setMetaCache(ExpressionUtil.evaluate(metaCache, context));
    db.setTableFilter(ExpressionUtil.evaluate(tableFilter, context));
    db.setIncludeTables(ExpressionUtil.evaluate(includeTables, context));
    db.setExcludeTables(ExpressionUtil.evaluate(excludeTables, context));
    db.setBatch(ExpressionUtil.evaluate(batch, context));
    db.setFetchSize(ExpressionUtil.evaluate(fetchSize, context));
    db.setReadOnly(ExpressionUtil.evaluate(readOnly, context));
    Boolean isLazy = ExpressionUtil.evaluate(lazy, context);
    db.setLazy(isLazy);
    db.setAcceptUnknownColumnTypes(ExpressionUtil.evaluate(acceptUnknownColumnTypes, context));

    // register this object on all relevant managers and in the context
    context.setGlobal(idValue, db);
    context.getDataModel().addDescriptorProvider(db, context.isValidate() && !isLazy);
    resourceManager.addResource(db);
    return true;
  }

  protected AbstractDBSystem accessDatabase(String id, String environment, String system, BeneratorContext context) {
    return new DefaultDBSystem(id, environment, system, context);
  }

}

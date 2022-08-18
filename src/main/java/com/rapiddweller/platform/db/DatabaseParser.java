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

package com.rapiddweller.platform.db;

import com.rapiddweller.benerator.BeneratorErrorIds;
import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.BeneratorRootStatement;
import com.rapiddweller.benerator.engine.Statement;
import com.rapiddweller.benerator.engine.parser.attr.CommonAttrs;
import com.rapiddweller.benerator.engine.parser.attr.IdAttribute;
import com.rapiddweller.benerator.engine.parser.string.IdParser;
import com.rapiddweller.benerator.engine.parser.string.ScriptableParser;
import com.rapiddweller.benerator.engine.parser.xml.AbstractBeneratorDescriptorParser;
import com.rapiddweller.benerator.engine.parser.xml.BeneratorParseContext;
import com.rapiddweller.benerator.engine.parser.xml.DescriptorParserUtil;
import com.rapiddweller.benerator.engine.statement.IfStatement;
import com.rapiddweller.common.Assert;
import com.rapiddweller.common.Context;
import com.rapiddweller.common.ConversionException;
import com.rapiddweller.common.Validator;
import com.rapiddweller.common.exception.ExceptionFactory;
import com.rapiddweller.common.parser.BooleanParser;
import com.rapiddweller.common.parser.FullyQualifiedClassNameParser;
import com.rapiddweller.common.parser.NonNegativeIntegerParser;
import com.rapiddweller.common.parser.StringParser;
import com.rapiddweller.common.parser.AbstractTypedParser;
import com.rapiddweller.format.xml.AttrInfo;
import com.rapiddweller.format.xml.AttrInfoSupport;
import com.rapiddweller.common.Expression;
import com.rapiddweller.script.expression.DynamicExpression;
import com.rapiddweller.script.expression.FallbackExpression;
import org.w3c.dom.Element;

import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_ACC_UNK_COL_TYPES;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_BATCH;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_CATALOG;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_DRIVER;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_ENVIRONMENT;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_EXCL_TABLES;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_FETCH_SIZE;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_ID;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_INCL_TABLES;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_LAZY;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_META_CACHE;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_PASSWORD;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_READ_ONLY;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_SCHEMA;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_SYSTEM;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_TABLE_FILTER;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_URL;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_USER;
import static com.rapiddweller.benerator.engine.DescriptorConstants.EL_DATABASE;
import static com.rapiddweller.common.xml.XMLAssert.assertAtLeastOneAttributeIsSet;
import static com.rapiddweller.common.xml.XMLAssert.assertGroupComplete;
import static com.rapiddweller.common.xml.XMLAssert.assertNoTextContent;
import static com.rapiddweller.common.xml.XMLAssert.mutuallyExcludeAttrGroups;

/**
 * Parses a &lt;database&gt; element in a Benerator descriptor file.<br/><br/>
 * Created: 25.10.2009 00:40:56
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class DatabaseParser extends AbstractBeneratorDescriptorParser {

  private static final String FALSE = "false";

  public static final AttrInfo<String> ID = new IdAttribute(BeneratorErrorIds.SYN_DB_ID, true);
  public static final AttrInfo<Expression<String>> ENVIRONMENT = CommonAttrs.environment(BeneratorErrorIds.SYN_DB_ENVIRONMENT, false);
  public static final AttrInfo<Expression<String>> SYSTEM = CommonAttrs.system(BeneratorErrorIds.SYN_DB_SYSTEM, false);

  public static final AttrInfo<Expression<String>> URL = new AttrInfo<>(
    ATT_URL, false, BeneratorErrorIds.SYN_DB_URL, new ScriptableParser<>(new JdbcUrlParser()));

  public static final AttrInfo<Expression<String>> DRIVER = new AttrInfo<>(
    ATT_DRIVER, false, BeneratorErrorIds.SYN_DB_DRIVER,
      new ScriptableParser<>(new FullyQualifiedClassNameParser(false)));

  public static final AttrInfo<Expression<String>> USER = new AttrInfo<>(
    ATT_USER, false, BeneratorErrorIds.SYN_DB_USER, new ScriptableParser<>(new IdParser()));

  public static final AttrInfo<Expression<String>> PASSWORD = new AttrInfo<>(
    ATT_PASSWORD, false, BeneratorErrorIds.SYN_DB_PWD, new ScriptableParser<>(new StringParser("db password")));

  public static final AttrInfo<Expression<String>> CATALOG = new AttrInfo<>(
    ATT_CATALOG, false, BeneratorErrorIds.SYN_DB_CATALOG, new ScriptableParser<>(new IdParser()));

  public static final AttrInfo<Expression<String>> SCHEMA = new AttrInfo<>(
    ATT_SCHEMA, false, BeneratorErrorIds.SYN_DB_SCHEMA, new ScriptableParser<>(new IdParser()));

  public static final AttrInfo<Expression<String>> TABLE_FILTER = new AttrInfo<>(
    ATT_TABLE_FILTER, false, BeneratorErrorIds.SYN_DB_TABLE_FILTER,
      new ScriptableParser<>(new StringParser("tableFilter expression")));

  public static final AttrInfo<Expression<String>> INCL_TABLES = new AttrInfo<>(
    ATT_INCL_TABLES, false, BeneratorErrorIds.SYN_DB_INCLUDE_TABLES,
      new ScriptableParser<>(new StringParser("includeTables expression")));

  public static final AttrInfo<Expression<String>> EXCL_TABLES = new AttrInfo<>(
    ATT_EXCL_TABLES, false, BeneratorErrorIds.SYN_DB_EXCLUDE_TABLES,
      new ScriptableParser<>(new StringParser("excludeTables expression")));

  public static final AttrInfo<Expression<Boolean>> META_CACHE = new AttrInfo<>(
    ATT_META_CACHE, false, BeneratorErrorIds.SYN_DB_META_CACHE,
      new ScriptableParser<>(new BooleanParser()), FALSE);

  public static final AttrInfo<Expression<Boolean>> BATCH = new AttrInfo<>(
  ATT_BATCH, false, BeneratorErrorIds.SYN_DB_BATCH,
      new ScriptableParser<>(new BooleanParser()), FALSE);

  public static final AttrInfo<Expression<Integer>> FETCH_SIZE = new AttrInfo<>(
  ATT_FETCH_SIZE, false, BeneratorErrorIds.SYN_DB_FETCH_SIZE,
      new ScriptableParser<>(new NonNegativeIntegerParser()), "100");

  public static final AttrInfo<Expression<Boolean>> READ_ONLY = new AttrInfo<>(
  ATT_READ_ONLY, false, BeneratorErrorIds.SYN_DB_READ_ONLY,
      new ScriptableParser<>(new BooleanParser()), FALSE);

  public static final AttrInfo<Expression<Boolean>> LAZY = new AttrInfo<>(
  ATT_LAZY, false, BeneratorErrorIds.SYN_DB_LAZY,
      new ScriptableParser<>(new BooleanParser()), "true");

  public static final AttrInfo<Expression<Boolean>> ACC_UNK_COL_TYPES = new AttrInfo<>(
  ATT_ACC_UNK_COL_TYPES, false, BeneratorErrorIds.SYN_DB_ACCEPT_UNK_COL_TYPES,
      new ScriptableParser<>(new BooleanParser()));

  private static final AttrInfoSupport ATTR_INFO = new AttrInfoSupport(BeneratorErrorIds.SYN_DB_ILLEGAL_ATTR,
      new DatabaseValidator(), ID, ENVIRONMENT, SYSTEM, URL, DRIVER, USER, PASSWORD, CATALOG, SCHEMA,
      TABLE_FILTER, INCL_TABLES, EXCL_TABLES, META_CACHE, BATCH, FETCH_SIZE, READ_ONLY, LAZY, ACC_UNK_COL_TYPES);

  public DatabaseParser() {
    super(EL_DATABASE, ATTR_INFO, BeneratorRootStatement.class, IfStatement.class);
  }

  @Override
  public DefineDatabaseStatement doParse(
      Element element, Element[] parentXmlPath, Statement[] parentComponentPath, BeneratorParseContext context) {
    // parse
    try {
      Expression<String> id = DescriptorParserUtil.getConstantStringAttributeAsExpression(ATT_ID, element);
      Expression<String> environment = ENVIRONMENT.parse(element);
      Expression<String> system = SYSTEM.parse(element);
      Expression<String> url = URL.parse(element);
      Expression<String> driver = DRIVER.parse(element);
      Expression<String> user = USER.parse(element);
      Expression<String> password = PASSWORD.parse(element);
      Expression<String> catalog = CATALOG.parse(element);
      Expression<String> schema = SCHEMA.parse(element);
      Expression<String> tableFilter = TABLE_FILTER.parse(element);
      Expression<String> includeTables = INCL_TABLES.parse(element);
      Expression<String> excludeTables = EXCL_TABLES.parse(element);
      Expression<Boolean> metaCache = META_CACHE.parse(element);
      Expression<Boolean> batch = BATCH.parse(element);
      Expression<Integer> fetchSize = FETCH_SIZE.parse(element);
      Expression<Boolean> readOnly = READ_ONLY.parse(element);
      Expression<Boolean> lazy = LAZY.parse(element);
      Expression<Boolean> acceptUnknownColumnTypes = new FallbackExpression<>(
          ACC_UNK_COL_TYPES.parse(element), new GlobalAcceptUnknownSimpleTypeExpression());
      return createDatabaseStatement(id, environment, system, url, driver, user,
          password, catalog, schema, tableFilter, includeTables,
          excludeTables, metaCache, batch, fetchSize, readOnly, lazy,
          acceptUnknownColumnTypes, context);
    } catch (ConversionException e) {
      throw ExceptionFactory.getInstance().configurationError("Error parsing <database>", e);
    }
  }

  protected DefineDatabaseStatement createDatabaseStatement(
      Expression<String> id, Expression<String> environment, Expression<String> system,
      Expression<String> url, Expression<String> driver,
      Expression<String> user, Expression<String> password,
      Expression<String> catalog, Expression<String> schema,
      Expression<String> tableFilter, Expression<String> includeTables,
      Expression<String> excludeTables, Expression<Boolean> metaCache,
      Expression<Boolean> batch, Expression<Integer> fetchSize,
      Expression<Boolean> readOnly, Expression<Boolean> lazy,
      Expression<Boolean> acceptUnknownColumnTypes,
      BeneratorParseContext context) {
    return new DefineDatabaseStatement(id, environment, system, url, driver, user, password, catalog, schema,
        metaCache, tableFilter, includeTables, excludeTables,
        batch, fetchSize, readOnly, lazy, acceptUnknownColumnTypes, context.getResourceManager());
  }

  static class GlobalAcceptUnknownSimpleTypeExpression extends DynamicExpression<Boolean> {
    @Override
    public Boolean evaluate(Context context) {
      return ((BeneratorContext) context).isAcceptUnknownSimpleTypes();
    }
  }

  static class DatabaseValidator implements Validator<Element> {

    @Override
    public boolean valid(Element element) {
      assertNoTextContent(element, BeneratorErrorIds.SYN_DB);
      assertAtLeastOneAttributeIsSet(element, BeneratorErrorIds.SYN_DB_NO_URL_AND_ENV_GROUP,
          ATT_ENVIRONMENT, ATT_URL);
      mutuallyExcludeAttrGroups(element, BeneratorErrorIds.SYN_DB_URL_AND_ENV_GROUP,
          new String[] { ATT_ENVIRONMENT, ATT_SYSTEM },
          new String[] { ATT_URL, ATT_DRIVER });
      assertGroupComplete(BeneratorErrorIds.SYN_DB_URL_GROUP_INCOMPLETE, element, ATT_URL, ATT_DRIVER);
      // For backwards compatibility, it is allowed to leave out ATT_SYSTEM.
      // When dropping that, then enable the following check
      // assertGroupComplete(element, BeneratorErrorIds.SYN_DB_ENV_GROUP_INCOMPLETE, ATT_ENVIRONMENT, ATT_SYSTEM)
      if (SYSTEM.parse(element) != null && ENVIRONMENT.parse(element) == null) {
        throw ExceptionFactory.getInstance().syntaxErrorForXmlElement(
            "if <database> has the attribute 'system' then it must have 'environment' too", null,
            BeneratorErrorIds.SYN_DB_ENV_GROUP_INCOMPLETE, element);
      }
      return true;
    }
  }

  static class JdbcUrlParser extends AbstractTypedParser<String> {

    protected JdbcUrlParser() {
      super("JDBC URL", String.class);
    }

    @Override
    protected String parseImpl(String spec) {
      Assert.isTrue(spec.startsWith("jdbc:"), "JDBC URLs are expected to start with 'jdbc:'");
      return spec;
    }
  }
}

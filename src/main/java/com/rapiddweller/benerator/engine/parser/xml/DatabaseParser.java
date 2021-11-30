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

package com.rapiddweller.benerator.engine.parser.xml;

import com.rapiddweller.benerator.BeneratorErrorIds;
import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.BeneratorRootStatement;
import com.rapiddweller.benerator.engine.Statement;
import com.rapiddweller.benerator.engine.statement.DefineDatabaseStatement;
import com.rapiddweller.benerator.engine.statement.IfStatement;
import com.rapiddweller.common.Context;
import com.rapiddweller.common.ConversionException;
import com.rapiddweller.common.exception.ExceptionFactory;
import com.rapiddweller.format.xml.AttrInfoSupport;
import com.rapiddweller.script.Expression;
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
import static com.rapiddweller.benerator.engine.parser.xml.DescriptorParserUtil.getConstantStringAttributeAsExpression;
import static com.rapiddweller.benerator.engine.parser.xml.DescriptorParserUtil.parseBooleanExpressionAttribute;
import static com.rapiddweller.benerator.engine.parser.xml.DescriptorParserUtil.parseIntAttribute;
import static com.rapiddweller.benerator.engine.parser.xml.DescriptorParserUtil.parseScriptableStringAttribute;

/**
 * Parses a &lt;database&gt; element in a Benerator descriptor file.<br/><br/>
 * Created: 25.10.2009 00:40:56
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class DatabaseParser extends AbstractBeneratorDescriptorParser {

  private static final AttrInfoSupport ATTR_INFO;
  static {
    ATTR_INFO = new AttrInfoSupport(BeneratorErrorIds.SYN_DATABASE_ILLEGAL_ATTR);
    ATTR_INFO.add(ATT_ID, true, BeneratorErrorIds.SYN_DATABASE_ID);
    ATTR_INFO.add(ATT_ENVIRONMENT, false, BeneratorErrorIds.SYN_DATABASE_ENVIRONMENT);
    ATTR_INFO.add(ATT_SYSTEM, false, BeneratorErrorIds.SYN_DATABASE_SYSTEM);
    ATTR_INFO.add(ATT_URL, false, BeneratorErrorIds.SYN_DATABASE_URL);
    ATTR_INFO.add(ATT_DRIVER, false, BeneratorErrorIds.SYN_DATABASE_DRIVER);
    ATTR_INFO.add(ATT_USER, false, BeneratorErrorIds.SYN_DATABASE_USER);
    ATTR_INFO.add(ATT_PASSWORD, false, BeneratorErrorIds.SYN_DATABASE_PASSWORD);
    ATTR_INFO.add(ATT_CATALOG, false, BeneratorErrorIds.SYN_DATABASE_CATALOG);
    ATTR_INFO.add(ATT_SCHEMA, false, BeneratorErrorIds.SYN_DATABASE_SCHEMA);
    ATTR_INFO.add(ATT_TABLE_FILTER, false, BeneratorErrorIds.SYN_DATABASE_TABLE_FILTER);
    ATTR_INFO.add(ATT_INCL_TABLES, false, BeneratorErrorIds.SYN_DATABASE_INCLUDE_TABLES);
    ATTR_INFO.add(ATT_EXCL_TABLES, false, BeneratorErrorIds.SYN_DATABASE_EXCLUDE_TABLES);
    ATTR_INFO.add(ATT_META_CACHE, false, BeneratorErrorIds.SYN_DATABASE_META_CACHE);
    ATTR_INFO.add(ATT_BATCH, false, BeneratorErrorIds.SYN_DATABASE_BATCH);
    ATTR_INFO.add(ATT_FETCH_SIZE, false, BeneratorErrorIds.SYN_DATABASE_FETCH_SIZE);
    ATTR_INFO.add(ATT_READ_ONLY, false, BeneratorErrorIds.SYN_DATABASE_READ_ONLY);
    ATTR_INFO.add(ATT_LAZY, false, BeneratorErrorIds.SYN_DATABASE_LAZY);
    ATTR_INFO.add(ATT_ACC_UNK_COL_TYPES, false, BeneratorErrorIds.SYN_DATABASE_ACCEPT_UNK_COL_TYPES);
  }

  // TODO define parser extension mechanism and move DatabaseParser and DefineDatabaseStatement to DB package?

  public DatabaseParser() {
    super(EL_DATABASE, ATTR_INFO, BeneratorRootStatement.class, IfStatement.class);
  }

  @Override
  public DefineDatabaseStatement doParse(
      Element element, Element[] parentXmlPath, Statement[] parentComponentPath, BeneratorParseContext context) {
    // check preconditions
    assertAtLeastOneAttributeIsSet(element, ATT_ENVIRONMENT, ATT_DRIVER);
    assertAtLeastOneAttributeIsSet(element, ATT_ENVIRONMENT, ATT_URL);

    // parse
    try {
      Expression<String> id = DescriptorParserUtil.getConstantStringAttributeAsExpression(ATT_ID, element);
      Expression<String> environment = parseScriptableStringAttribute(ATT_ENVIRONMENT, element);
      Expression<String> system = parseScriptableStringAttribute(ATT_SYSTEM, element);
      Expression<String> url = parseScriptableStringAttribute(ATT_URL, element);
      Expression<String> driver = parseScriptableStringAttribute(ATT_DRIVER, element);
      Expression<String> user = parseScriptableStringAttribute(ATT_USER, element);
      Expression<String> password = parseScriptableStringAttribute(ATT_PASSWORD, element);
      Expression<String> catalog = parseScriptableStringAttribute(ATT_CATALOG, element);
      Expression<String> schema = parseScriptableStringAttribute(ATT_SCHEMA, element);
      Expression<String> tableFilter = parseScriptableStringAttribute(ATT_TABLE_FILTER, element);
      Expression<String> includeTables = parseScriptableStringAttribute(ATT_INCL_TABLES, element);
      Expression<String> excludeTables = parseScriptableStringAttribute(ATT_EXCL_TABLES, element);
      Expression<Boolean> metaCache = parseBooleanExpressionAttribute(ATT_META_CACHE, element, false);
      Expression<Boolean> batch = parseBooleanExpressionAttribute(ATT_BATCH, element, false);
      Expression<Integer> fetchSize = parseIntAttribute(ATT_FETCH_SIZE, element, 100);
      Expression<Boolean> readOnly = parseBooleanExpressionAttribute(ATT_READ_ONLY, element, false);
      Expression<Boolean> lazy = parseBooleanExpressionAttribute(ATT_LAZY, element, true);
      Expression<Boolean> acceptUnknownColumnTypes = new FallbackExpression<>(
          parseBooleanExpressionAttribute(ATT_ACC_UNK_COL_TYPES, element),
          new GlobalAcceptUnknownSimpleTypeExpression());
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

}

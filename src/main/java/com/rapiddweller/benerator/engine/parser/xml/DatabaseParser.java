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

import static com.rapiddweller.benerator.engine.DescriptorConstants.*;
import static com.rapiddweller.benerator.engine.parser.xml.DescriptorParserUtil.*;

import java.util.Set;

import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.BeneratorRootStatement;
import com.rapiddweller.benerator.engine.Statement;
import com.rapiddweller.benerator.engine.statement.DefineDatabaseStatement;
import com.rapiddweller.benerator.engine.statement.IfStatement;
import com.rapiddweller.common.CollectionUtil;
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.common.Context;
import com.rapiddweller.common.ConversionException;
import com.rapiddweller.script.Expression;
import com.rapiddweller.script.expression.DynamicExpression;
import com.rapiddweller.script.expression.FallbackExpression;
import org.w3c.dom.Element;

/**
 * Parses a &lt;database&gt; element in a Benerator descriptor file.<br/><br/>
 * Created: 25.10.2009 00:40:56
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class DatabaseParser extends AbstractBeneratorDescriptorParser {
	
	private static final Set<String> REQUIRED_ATTRIBUTES = CollectionUtil.toSet(ATT_ID);

	private static final Set<String> OPTIONAL_ATTRIBUTES = CollectionUtil.toSet(
			ATT_ENVIRONMENT, ATT_URL, ATT_DRIVER, ATT_USER, ATT_PASSWORD, ATT_CATALOG, ATT_SCHEMA, 
			ATT_TABLE_FILTER, ATT_INCL_TABLES, ATT_EXCL_TABLES, ATT_META_CACHE, ATT_BATCH, ATT_FETCH_SIZE, 
			ATT_READ_ONLY, ATT_LAZY, ATT_ACC_UNK_COL_TYPES);


	// TODO v1.0 define parser extension mechanism and move DatabaseParser and DefineDatabaseStatement to DB package?
	
	public DatabaseParser() {
	    super(EL_DATABASE, REQUIRED_ATTRIBUTES, OPTIONAL_ATTRIBUTES, 
	    		BeneratorRootStatement.class, IfStatement.class);
    }

	@Override
    public DefineDatabaseStatement doParse(Element element, Statement[] parentPath, BeneratorParseContext context) {
		// check preconditions
		assertAtLeastOneAttributeIsSet(element, ATT_ENVIRONMENT, ATT_DRIVER);
		assertAtLeastOneAttributeIsSet(element, ATT_ENVIRONMENT, ATT_URL);
		
		// parse
		try {
			Expression<String>  id            = parseAttribute(ATT_ID, element);
			Expression<String>  environment   = parseScriptableStringAttribute(ATT_ENVIRONMENT,  element);
			Expression<String>  url           = parseScriptableStringAttribute(ATT_URL,          element);
			Expression<String>  driver        = parseScriptableStringAttribute(ATT_DRIVER,       element);
			Expression<String>  user          = parseScriptableStringAttribute(ATT_USER,         element);
			Expression<String>  password      = parseScriptableStringAttribute(ATT_PASSWORD,     element);
			Expression<String>  catalog       = parseScriptableStringAttribute(ATT_CATALOG,      element);
			Expression<String>  schema        = parseScriptableStringAttribute(ATT_SCHEMA,       element);
			Expression<String>  tableFilter   = parseScriptableStringAttribute(ATT_TABLE_FILTER, element);
			Expression<String>  includeTables = parseScriptableStringAttribute(ATT_INCL_TABLES,  element);
			Expression<String>  excludeTables = parseScriptableStringAttribute(ATT_EXCL_TABLES,  element);
			Expression<Boolean> metaCache     = parseBooleanExpressionAttribute(ATT_META_CACHE,  element, false);
			Expression<Boolean> batch         = parseBooleanExpressionAttribute(ATT_BATCH,       element, false);
			Expression<Integer> fetchSize     = parseIntAttribute(ATT_FETCH_SIZE,                element, 100);
			Expression<Boolean> readOnly      = parseBooleanExpressionAttribute(ATT_READ_ONLY,   element, false);
			Expression<Boolean> lazy          = parseBooleanExpressionAttribute(ATT_LAZY,        element, true);
			Expression<Boolean> acceptUnknownColumnTypes = new FallbackExpression<>(
                    parseBooleanExpressionAttribute(ATT_ACC_UNK_COL_TYPES, element),
                    new GlobalAcceptUnknownSimpleTypeExpression());
			return createDatabaseStatement(id, environment, url, driver, user,
					password, catalog, schema, tableFilter, includeTables,
					excludeTables, metaCache, batch, fetchSize, readOnly, lazy,
					acceptUnknownColumnTypes, context);
		} catch (ConversionException e) {
			throw new ConfigurationError(e);
		}
    }

	protected DefineDatabaseStatement createDatabaseStatement(
			Expression<String> id, Expression<String> environment,
			Expression<String> url, Expression<String> driver,
			Expression<String> user, Expression<String> password,
			Expression<String> catalog, Expression<String> schema,
			Expression<String> tableFilter, Expression<String> includeTables,
			Expression<String> excludeTables, Expression<Boolean> metaCache,
			Expression<Boolean> batch, Expression<Integer> fetchSize,
			Expression<Boolean> readOnly, Expression<Boolean> lazy,
			Expression<Boolean> acceptUnknownColumnTypes,
			BeneratorParseContext context) {
		return new DefineDatabaseStatement(id, environment, url, driver, user, password, catalog, schema, 
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

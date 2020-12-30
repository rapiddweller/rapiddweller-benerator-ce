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

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.Statement;
import com.rapiddweller.common.CollectionUtil;
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.common.ErrorHandler;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.common.collection.OrderedNameMap;
import com.rapiddweller.common.xml.XMLUtil;
import com.rapiddweller.jdbacl.identity.IdentityModel;
import com.rapiddweller.jdbacl.identity.IdentityProvider;
import com.rapiddweller.jdbacl.identity.KeyMapper;
import com.rapiddweller.jdbacl.identity.NoIdentity;
import com.rapiddweller.jdbacl.identity.mem.MemKeyMapper;
import com.rapiddweller.jdbacl.identity.xml.IdentityParseContext;
import com.rapiddweller.jdbacl.identity.xml.IdentityParser;
import com.rapiddweller.jdbacl.model.DBTable;
import com.rapiddweller.jdbacl.model.Database;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.ReferenceDescriptor;
import com.rapiddweller.platform.db.DBSystem;
import com.rapiddweller.script.Expression;
import com.rapiddweller.script.expression.ExpressionUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Groups {@link TranscodeStatement}s and provides common features like 
 * {@link IdentityProvider} and {@link KeyMapper} objects.<br/><br/>
 * Created: 10.09.2010 18:25:18
 * @since 0.6.4
 * @author Volker Bergmann
 */
public class TranscodingTaskStatement extends SequentialStatement {
	
    final Expression<DBSystem> sourceEx;
    final Expression<DBSystem> targetEx;
    final Expression<String> identityEx;
	final Expression<Long> pageSizeEx;
    final Expression<ErrorHandler> errorHandlerExpression;
    final IdentityProvider identityProvider;
    KeyMapper mapper;
	final Map<String, Boolean> tableNkRequirements = OrderedNameMap.createCaseIgnorantMap();
    
	public TranscodingTaskStatement(Expression<DBSystem> sourceEx, Expression<DBSystem> targetEx, Expression<String> identityEx, 
    		Expression<Long> pageSizeEx, Expression<ErrorHandler> errorHandlerExpression) {
	    this.sourceEx = cache(sourceEx);
	    this.targetEx = cache(targetEx);
	    this.identityEx = cache(identityEx);
	    this.pageSizeEx = cache(pageSizeEx);
	    this.errorHandlerExpression = cache(errorHandlerExpression);
		this.identityProvider = new IdentityProvider();
    }

	public Expression<DBSystem> getSourceEx() {
	    return sourceEx;
    }

	public Expression<DBSystem> getTargetEx() {
	    return targetEx;
    }

    public Expression<Long> getPageSizeEx() {
	    return pageSizeEx;
    }

	public Expression<ErrorHandler> getErrorHandlerEx() {
    	return errorHandlerExpression;
    }

	public IdentityProvider getIdentityProvider() {
		return identityProvider;
	}
	
	KeyMapper getKeyMapper() {
		return mapper;
	}
	
	@Override
	public boolean execute(BeneratorContext context) {
		DBSystem target = getTarget(context);
		Database database = target.getDbMetaData();
		mapper = new MemKeyMapper(null, null, target.getConnection(), target.getId(), identityProvider, database);
		checkPrecoditions(context);
		super.execute(context);
    	return true;
	}
	
	private void checkPrecoditions(BeneratorContext context) {
		DBSystem target = targetEx.evaluate(context);
		boolean identitiesRequired = collectPreconditions(subStatements, context);
		// check that each table for which an identity definition is required has one
		if (identitiesRequired)
			readIdentityDefinition(context);
		for (Entry<String, Boolean> req : tableNkRequirements.entrySet()) {
			String tableName = req.getKey();
			Boolean required = req.getValue();
			IdentityModel identity = identityProvider.getIdentity(tableName, false);
			if (identity == null) {
				if (required) {
					throw new ConfigurationError("For transcoding, an identity definition of table '" + tableName + "' is required");
				} else {
					DBTable table = target.getDbMetaData().getTable(tableName);
					identity = new NoIdentity(table.getName());
					identityProvider.registerIdentity(identity, tableName);
				}
			}
		}
	}
	
	
	
	// helpers ---------------------------------------------------------------------------------------------------------

	private boolean collectPreconditions(List<Statement> subStatements, BeneratorContext context) {
		boolean identitiesRequired = false;
		List<CascadeParent> children = CollectionUtil.extractItemsOfCompatibleType(CascadeParent.class, subStatements);
		for (CascadeParent statement : children) {
			ComplexTypeDescriptor type = statement.getType(getSourceEx().evaluate(context), context);
			String tableName = type.getName();
			// items to be transcoded do not need NK definition
			tableNkRequirements.put(tableName, false);
			for (ReferenceDescriptor ref : type.getReferenceComponents()) {
				String targetTable = ref.getTargetType();
				if (!tableNkRequirements.containsKey(targetTable) && statement.getSource(context).countEntities(targetTable) > 0) {
					tableNkRequirements.put(targetTable, true);
					identitiesRequired = true;
				}
			}
			identitiesRequired |= collectPreconditions(statement.getSubStatements(), context);
		}
		return identitiesRequired;
	}

	private void readIdentityDefinition(BeneratorContext context) {
		try {
			// check identity definition
			String identityUri = ExpressionUtil.evaluate(identityEx, context);
			if (identityUri == null)
				throw new ConfigurationError("No 'identity' definition file defined");
			String idFile = context.resolveRelativeUri(identityUri);
			IdentityParser parser = new IdentityParser();
			IdentityParseContext parseContext = new IdentityParseContext(identityProvider);
			Document idXml = XMLUtil.parse(IOUtil.getInputStreamForURI(idFile));
			Object[] parentPath = new Object[0];
			for (Element child : XMLUtil.getChildElements(idXml.getDocumentElement()))
				parser.parse(child, parentPath, parseContext);
		} catch (Exception e) {
			throw new ConfigurationError("Error setting up transcoding task", e);
		}
	}

	private DBSystem getTarget(BeneratorContext context) {
		DBSystem target = ExpressionUtil.evaluate(targetEx, context);
		if (target == null)
			throw new ConfigurationError("No 'target' database defined in <transcodingTask>");
		return target;
	}

	public boolean needsNkMapping(String tableName) {
		Boolean required = tableNkRequirements.get(tableName);
		if (required == null)
			throw new RuntimeException("Assertion failed: Not clear if an identity definition is necessary for table " + tableName);
		return required;
	}
	
}

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
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.platform.xml.DOMTree;
import com.rapiddweller.script.Expression;
import com.rapiddweller.script.expression.ExpressionUtil;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * {@link Statement} for creating a {@link DOMTree} element 
 * and assigning it with context and resource manager.<br/><br/>
 * Created: 16.01.2014 16:07:06
 * @since 0.9.0
 * @author Volker Bergmann
 */

public class DefineDOMTreeStatement implements Statement {
	
	private static final Logger logger = LogManager.getLogger(DefineDOMTreeStatement.class);
	
	private final ResourceManager resourceManager;
	
	private final Expression<String>  id;
	private final Expression<String>  inputUri;
	private final Expression<String>  outputUri;
	private final Expression<Boolean> namespaceAware;
	
	public DefineDOMTreeStatement(Expression<String> id, Expression<String> inputUri, 
			Expression<String> outputUri, Expression<Boolean> namespaceAware, ResourceManager resourceManager) {
		if (id == null)
			throw new ConfigurationError("No DOMTree id defined");
		this.id = id;
		this.inputUri = inputUri;
	    this.outputUri = outputUri;
	    this.namespaceAware = namespaceAware;
	    this.resourceManager = resourceManager;
    }

	@Override
    public boolean execute(BeneratorContext context) {
	    logger.debug("Instantiating database with id '" + id + "'");
	    String idValue = id.evaluate(context);
		String inputUriValue = ExpressionUtil.evaluate(inputUri, context);
	    DOMTree domTree = new DOMTree(inputUriValue, context);
		
		String outputUriValue = ExpressionUtil.evaluate(outputUri, context);
		if (outputUriValue != null)
			domTree.setOutputUri(outputUriValue);
		Boolean namespaceAwareValue = ExpressionUtil.evaluate(namespaceAware, context);
		if (namespaceAware != null)
			domTree.setNamespaceAware(namespaceAwareValue);

	    // register this object on all relevant managers and in the context
	    context.setGlobal(idValue, domTree);
	    context.getDataModel().addDescriptorProvider(domTree, context.isValidate());
	    resourceManager.addResource(domTree);
    	return true;
    }

}

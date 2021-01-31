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

import com.rapiddweller.benerator.engine.BeneratorRootStatement;
import com.rapiddweller.benerator.engine.Statement;
import com.rapiddweller.benerator.engine.statement.DefineDOMTreeStatement;
import com.rapiddweller.benerator.engine.statement.IfStatement;
import com.rapiddweller.common.CollectionUtil;
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.common.ConversionException;
import com.rapiddweller.script.Expression;
import org.w3c.dom.Element;

import java.util.Set;

import static com.rapiddweller.benerator.engine.DescriptorConstants.*;
import static com.rapiddweller.benerator.engine.parser.xml.DescriptorParserUtil.*;

/**
 * Parses &lt;domtree&gt; elements in a Benerator descriptor file.<br/><br/>
 * Created: 16.01.2014 15:59:48
 * @since 0.9.0
 * @author Volker Bergmann
 */

public class DOMTreeParser extends AbstractBeneratorDescriptorParser {
	
	private static final Set<String> REQUIRED_ATTRIBUTES = CollectionUtil.toSet(ATT_ID, ATT_INPUT_URI);

	private static final Set<String> OPTIONAL_ATTRIBUTES = CollectionUtil.toSet(ATT_OUTPUT_URI, ATT_NAMESPACE_AWARE);


	public DOMTreeParser() {
	    super(EL_DOMTREE, REQUIRED_ATTRIBUTES, OPTIONAL_ATTRIBUTES, BeneratorRootStatement.class, IfStatement.class);
    }

	@Override
    public DefineDOMTreeStatement doParse(Element element, Statement[] parentPath, BeneratorParseContext context) {
		try {
			Expression<String>  id        = parseAttribute(ATT_ID, element);
			Expression<String>  inputUri  = parseScriptableStringAttribute(ATT_INPUT_URI,  element);
			Expression<String>  outputUri = parseScriptableStringAttribute(ATT_OUTPUT_URI, element);
			Expression<Boolean> namespaceAware = parseBooleanExpressionAttribute(ATT_NAMESPACE_AWARE, element);
			return new DefineDOMTreeStatement(id, inputUri, outputUri, namespaceAware, context.getResourceManager());
		} catch (ConversionException e) {
			throw new ConfigurationError(e);
		}
    }

}

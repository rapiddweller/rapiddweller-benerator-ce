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

import java.util.List;
import java.util.Set;

import com.rapiddweller.benerator.engine.Statement;
import com.rapiddweller.benerator.engine.statement.IfStatement;
import com.rapiddweller.benerator.engine.statement.SequentialStatement;
import com.rapiddweller.commons.CollectionUtil;
import com.rapiddweller.commons.xml.XMLUtil;
import com.rapiddweller.script.Expression;

import static com.rapiddweller.benerator.engine.DescriptorConstants.*;
import static com.rapiddweller.benerator.engine.parser.xml.DescriptorParserUtil.*;

import org.w3c.dom.Element;

/**
 * Parses an &lt;if&gt; element.<br/><br/>
 * Created: 19.02.2010 09:07:51
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class IfParser extends AbstractBeneratorDescriptorParser {
	
	private static final Set<String> STRICT_CHILDREN = CollectionUtil.toSet(
			EL_THEN, EL_ELSE, EL_COMMENT);

	public IfParser() {
		super(EL_IF, CollectionUtil.toSet(ATT_TEST), null);
	}

	@Override
	public Statement doParse(Element ifElement, Statement[] parentPath, BeneratorParseContext context) {
		Expression<Boolean> condition = parseBooleanExpressionAttribute(ATT_TEST, ifElement);
		Element[] thenElements = XMLUtil.getChildElements(ifElement, false, "then");
		if (thenElements.length > 1)
			syntaxError("Multiple <then> elements", ifElement);
		Element thenElement = (thenElements.length == 1 ? thenElements[0] : null);
		Element[] elseElements = XMLUtil.getChildElements(ifElement, false, "else");
		if (elseElements.length > 1)
			syntaxError("Multiple <else> elements", ifElement);
		Element elseElement = (elseElements.length == 1 ? elseElements[0] : null);
		List<Statement> thenStatements = null;
		List<Statement> elseStatements = null;
		IfStatement ifStatement = new IfStatement(condition);
		Statement[] ifPath = context.createSubPath(parentPath, ifStatement);
		if (elseElement != null) {
			// if there is an 'else' element, there must be an 'if' element too
			if (thenElement == null)
				syntaxError("'else' without 'then'", elseElement);
			thenStatements = context.parseChildElementsOf(thenElement, ifPath);
			elseStatements = context.parseChildElementsOf(elseElement, ifPath);
			// check that no elements conflict with 'then' and 'else'
			assertThenElseChildren(ifElement);
		} else if (thenElement != null) {
			thenStatements = context.parseChildElementsOf(thenElement, ifPath);
		} else
			thenStatements = context.parseChildElementsOf(ifElement, ifPath);
		ifStatement.setThenStatement(new SequentialStatement(thenStatements));
		ifStatement.setElseStatement(new SequentialStatement(elseStatements));
		return ifStatement;
    }

	private static void assertThenElseChildren(Element ifElement) {
	    for (Element child : XMLUtil.getChildElements(ifElement)) {
	    	if (!STRICT_CHILDREN.contains(child.getNodeName()))
	    		syntaxError("Illegal child element: ", child);
	    }
    }

}

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

import com.rapiddweller.benerator.engine.Statement;
import com.rapiddweller.benerator.engine.statement.WhileStatement;
import com.rapiddweller.common.CollectionUtil;
import com.rapiddweller.script.Expression;
import org.w3c.dom.Element;

import java.util.List;
import java.util.Set;

import static com.rapiddweller.benerator.engine.DescriptorConstants.*;
import static com.rapiddweller.benerator.engine.parser.xml.DescriptorParserUtil.parseBooleanExpressionAttribute;

/**
 * Parses a 'while' element.<br/><br/>
 * Created: 19.02.2010 09:18:47
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class WhileParser extends AbstractBeneratorDescriptorParser {

	private static final Set<String> LEGAL_PARENTS = CollectionUtil.toSet(
			EL_SETUP, EL_IF, EL_WHILE);

	public WhileParser() {
		super(EL_WHILE, CollectionUtil.toSet(ATT_TEST), null);
	}

    public boolean supports(String elementName, String parentName) {
	    return (EL_WHILE.equals(elementName) && LEGAL_PARENTS.contains(parentName));
    }

	@Override
	public Statement doParse(Element element, Statement[] parentPath, BeneratorParseContext context) {
		Expression<Boolean> condition = parseBooleanExpressionAttribute(ATT_TEST, element);
		WhileStatement whileStatement = new WhileStatement(condition);
		List<Statement> subStatements = context.parseChildElementsOf(element, context.createSubPath(parentPath, whileStatement));
		whileStatement.setSubStatements(subStatements);
	    return whileStatement;
    }

}

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

import static com.rapiddweller.benerator.engine.parser.xml.DescriptorParserUtil.*;

import static com.rapiddweller.benerator.engine.DescriptorConstants.*;

import com.rapiddweller.benerator.engine.BeneratorRootStatement;
import com.rapiddweller.benerator.engine.Statement;
import com.rapiddweller.benerator.engine.statement.IfStatement;
import com.rapiddweller.benerator.engine.statement.TranscodingTaskStatement;
import com.rapiddweller.benerator.engine.statement.WhileStatement;
import com.rapiddweller.commons.CollectionUtil;
import com.rapiddweller.commons.ErrorHandler;
import com.rapiddweller.script.Expression;
import com.rapiddweller.platform.db.DBSystem;
import org.w3c.dom.Element;

/**
 * Parses Benerator's &lt;transcode&gt; XML descriptor element.<br/><br/>
 * Created: 10.09.2010 18:14:53
 * @since 0.6.4
 * @author Volker Bergmann
 */
public class TranscodingTaskParser extends AbstractTranscodeParser {

	public TranscodingTaskParser() {
	    super(EL_TRANSCODING_TASK, 
	    		CollectionUtil.toSet(ATT_TARGET), 
	    		CollectionUtil.toSet(ATT_IDENTITY, ATT_DEFAULT_SOURCE, ATT_PAGESIZE, ATT_ON_ERROR), 
	    		BeneratorRootStatement.class, IfStatement.class, WhileStatement.class);
    }

    @Override
    public Statement doParse(Element element, Statement[] parentPath, BeneratorParseContext parsingContext) {
    	Expression<ErrorHandler> errorHandlerExpression = parseOnErrorAttribute(element, "transcodingTask");
		TranscodingTaskStatement statement = new TranscodingTaskStatement(
				parseDefaultSource(element), 
				parseTarget(element), 
				parseIdentity(element), 
				parsePageSize(element), 
	    		errorHandlerExpression);
		Statement[] subPath = parsingContext.createSubPath(parentPath, statement);
		statement.setSubStatements(parsingContext.parseChildElementsOf(element, subPath));
		return statement;
    }

	private static Expression<String> parseIdentity(Element element) {
		return parseScriptableStringAttribute("identity", element);
	}

	@SuppressWarnings("unchecked")
    protected Expression<DBSystem> parseDefaultSource(Element element) {
	    Expression<DBSystem> sourceEx = (Expression<DBSystem>) parseScriptAttribute("defaultSource", element);
	    return sourceEx;
    }

}

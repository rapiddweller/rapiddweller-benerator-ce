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

import java.util.Set;

import static com.rapiddweller.benerator.engine.DescriptorConstants.*;
import com.rapiddweller.benerator.engine.Statement;
import com.rapiddweller.benerator.engine.statement.MutatingTypeExpression;
import com.rapiddweller.benerator.engine.statement.TranscodeStatement;
import com.rapiddweller.benerator.engine.statement.TranscodingTaskStatement;

import static com.rapiddweller.benerator.engine.parser.xml.DescriptorParserUtil.*;

import com.rapiddweller.commons.ArrayUtil;
import com.rapiddweller.commons.CollectionUtil;
import com.rapiddweller.commons.ErrorHandler;
import com.rapiddweller.commons.xml.XMLUtil;
import com.rapiddweller.platform.db.DBSystem;
import com.rapiddweller.script.Expression;
import com.rapiddweller.script.expression.FallbackExpression;
import org.w3c.dom.Element;

/**
 * Parses a &lt;transcode&gt; element.<br/><br/>
 * Created: 08.09.2010 16:13:13
 * @since 0.6.4
 * @author Volker Bergmann
 */
public class TranscodeParser extends AbstractTranscodeParser {
	
	private static final Set<String> MEMBER_ELEMENTS = CollectionUtil.toSet(
			EL_ID, EL_ATTRIBUTE, EL_REFERENCE);
	
	public TranscodeParser() {
	    super(EL_TRANSCODE, 
	    		CollectionUtil.toSet(ATT_TABLE), 
	    		CollectionUtil.toSet(ATT_SOURCE, ATT_SELECTOR, ATT_TARGET, ATT_PAGESIZE, ATT_ON_ERROR), 
	    		TranscodingTaskStatement.class);
    }

    @Override
    public Statement doParse(Element element, Statement[] parentPath, BeneratorParseContext context) {
		String table = getAttribute(ATT_TABLE, element);
		TranscodingTaskStatement parent = (TranscodingTaskStatement) ArrayUtil.lastElementOf(parentPath);
		Expression<DBSystem> sourceEx   = parseSource(element, parent);
		Expression<String>   selectorEx = parseSelector(element, parent);
		Expression<DBSystem> targetEx   = parseTarget(element, parent);
		Expression<Long>     pageSizeEx = parsePageSize(element, parent);
	    Expression<ErrorHandler> errorHandlerEx = parseOnErrorAttribute(element, table);
	    TranscodeStatement result = new TranscodeStatement(new MutatingTypeExpression(element, getRequiredAttribute("table", element)), 
	    		parent, sourceEx, selectorEx, targetEx, pageSizeEx, errorHandlerEx);
	    Statement[] currentPath = context.createSubPath(parentPath, result);
	    for (Element child : XMLUtil.getChildElements(element)) {
	    	String childName = child.getNodeName();
	    	if (!MEMBER_ELEMENTS.contains(childName))
	    		result.addSubStatement(context.parseChildElement(child, currentPath));
	    	// The 'component' child elements (id, attribute, reference) are handled by the MutatingTypeExpression 
	    }
		return result;
    }

	private static Expression<String> parseSelector(Element element, TranscodingTaskStatement parent) {
		return parseScriptableStringAttribute("selector", element);
	}

	private Expression<Long> parsePageSize(Element element, Statement parent) {
	    Expression<Long> result = super.parsePageSize(element);
	    if (parent instanceof TranscodingTaskStatement)
			result = new FallbackExpression<>(result, ((TranscodingTaskStatement) parent).getPageSizeEx());
	    return result;
    }

	private Expression<DBSystem> parseSource(Element element, Statement parent) {
	    Expression<DBSystem> result = super.parseSource(element);
	    if (parent instanceof TranscodingTaskStatement)
			result = new FallbackExpression<>(result, ((TranscodingTaskStatement) parent).getSourceEx());
	    return result;
    }

	private Expression<DBSystem> parseTarget(Element element, Statement parent) {
	    Expression<DBSystem> result = super.parseTarget(element);
	    if (parent instanceof TranscodingTaskStatement)
			result = new FallbackExpression<>(result, ((TranscodingTaskStatement) parent).getTargetEx());
	    return result;
    }

}

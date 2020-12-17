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

import com.rapiddweller.benerator.engine.BeneratorRootStatement;
import com.rapiddweller.benerator.engine.DescriptorConstants;
import com.rapiddweller.benerator.engine.Statement;
import com.rapiddweller.benerator.engine.expression.ScriptableExpression;
import com.rapiddweller.benerator.engine.statement.IfStatement;
import com.rapiddweller.benerator.engine.statement.IncludeStatement;
import com.rapiddweller.commons.CollectionUtil;
import com.rapiddweller.script.Expression;
import com.rapiddweller.script.expression.StringExpression;
import org.w3c.dom.Element;
import static com.rapiddweller.benerator.engine.DescriptorConstants.*;

/**
 * Parses an {@literal <}include{@literal >} element in a Benerator descriptor file.<br/><br/>
 * Created: 25.10.2009 00:32:02
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class IncludeParser extends AbstractBeneratorDescriptorParser {
	
	static final Set<String> REQUIRED_ATTRIBUTES = CollectionUtil.toSet(ATT_URI);

	public IncludeParser() {
	    super(EL_INCLUDE, REQUIRED_ATTRIBUTES, null, 
	    		BeneratorRootStatement.class, IfStatement.class);
    }

	@Override
	public IncludeStatement doParse(Element element, Statement[] parentPath, BeneratorParseContext context) {
        String uriAttr = element.getAttribute(DescriptorConstants.ATT_URI);
		Expression<String> uriEx = new StringExpression(new ScriptableExpression(uriAttr, null));
        return new IncludeStatement(uriEx);
    }

}

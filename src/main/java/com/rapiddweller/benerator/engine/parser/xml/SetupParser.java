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
import com.rapiddweller.common.CollectionUtil;
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.common.xml.XMLUtil;
import com.rapiddweller.format.xml.XMLElementParser;
import org.w3c.dom.Element;

import java.util.*;
import java.util.Map.Entry;

import static com.rapiddweller.benerator.engine.DescriptorConstants.*;

/**
 * {@link XMLElementParser} implementation for parsing a Benerator descriptor file's root XML element.<br/><br/>
 * Created: 14.12.2010 19:48:00
 * @since 0.6.4
 * @author Volker Bergmann
 */
public class SetupParser extends AbstractBeneratorDescriptorParser {
	
	private static final Set<String> BENERATOR_PROPERTIES = CollectionUtil.toSet(
			ATT_DEFAULT_SCRIPT,
			ATT_DEFAULT_NULL,
			ATT_DEFAULT_ENCODING,
			ATT_DEFAULT_LINE_SEPARATOR,
			ATT_DEFAULT_TIME_ZONE,
			ATT_DEFAULT_LOCALE,
			ATT_DEFAULT_DATASET,
			ATT_DEFAULT_PAGE_SIZE,
			ATT_DEFAULT_SEPARATOR,
			ATT_DEFAULT_ONE_TO_ONE,
			ATT_DEFAULT_ERR_HANDLER,
			ATT_MAX_COUNT,
			ATT_ACCEPT_UNKNOWN_SIMPLE_TYPES,
			ATT_GENERATOR_FACTORY,
			ATT_DEFAULT_IMPORTS
		);

	private static final Set<String> XML_ATTRIBUTES = CollectionUtil.toSet(
		"xmlns", "xmlns:xsi", "xsi:schemaLocation"
	);

	private static final Set<String> OPTIONAL_ATTRIBUTES;
	
	static {
		OPTIONAL_ATTRIBUTES = new HashSet<>(BENERATOR_PROPERTIES);
		OPTIONAL_ATTRIBUTES.addAll(XML_ATTRIBUTES);
	}
	
	public SetupParser() {
		super(EL_SETUP, null, OPTIONAL_ATTRIBUTES);
	}

	@Override
	public Statement doParse(Element element, Statement[] parentPath, BeneratorParseContext context) {
		Map<String, String> attributes = XMLUtil.getAttributes(element);
		// remove standard XML root attributes and verify that the remaining ones are legal
		Iterator<Entry<String, String>> iterator = attributes.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, String> attribute = iterator.next();
			if (BENERATOR_PROPERTIES.contains(attribute.getKey()))
				attribute.setValue(StringUtil.unescape(attribute.getValue()));
			else if (isStandardXmlRootAttribute(attribute.getKey()))
				iterator.remove();
			else
				throw new ConfigurationError("Not a supported attribute in <" + EL_SETUP + ">: " + attribute.getKey());
		}
		// create root statement and configure its children
	    BeneratorRootStatement rootStatement = new BeneratorRootStatement(attributes);
	    Statement[] currentPath = context.createSubPath(parentPath, rootStatement);
		List<Statement> subStatements = context.parseChildElementsOf(element, currentPath);
	    rootStatement.setSubStatements(subStatements);
	    return rootStatement;
	}

	private static boolean isStandardXmlRootAttribute(String key) {
		return XML_ATTRIBUTES.contains(key) || key.contains(":");
	}

}

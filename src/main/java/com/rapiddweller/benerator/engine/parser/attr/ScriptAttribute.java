/* (c) Copyright 2022 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.attr;

import com.rapiddweller.format.xml.AttrInfo;

import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_SCRIPT;

/**
 * {@link AttrInfo} implementation parses a 'script' attribute in an XML element.<br/><br/>
 * Created: 18.02.2022 11:38:18
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class ScriptAttribute extends AttrInfo<String> {
	public ScriptAttribute(String errorId) {
		super(ATT_SCRIPT, false, errorId, null);
	}
}

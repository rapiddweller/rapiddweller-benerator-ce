/* (c) Copyright 2022 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.attr;

import com.rapiddweller.benerator.engine.parser.string.BeanSpecParser;
import com.rapiddweller.common.Expression;
import com.rapiddweller.format.xml.AttrInfo;

import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_GENERATOR;

/**
 * {@link AttrInfo} implementation for a 'generator' XML attribute.<br/><br/>
 * Created: 18.02.2022 11:26:32
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class GeneratorAttribute extends AttrInfo<Expression<?>> {
	public GeneratorAttribute(String errorId) {
		super(ATT_GENERATOR, false, errorId, new BeanSpecParser());
	}
}

/* (c) Copyright 2022 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.platform.mongodb;

import com.rapiddweller.benerator.DefaultPlatformDescriptor;
import com.rapiddweller.benerator.engine.parser.xml.MongoDBParser;
import com.rapiddweller.benerator.engine.parser.xml.XMLStatementParser;

/**
 * {@link com.rapiddweller.benerator.PlatformDescriptor} implementation for the 'mongodb' platform.<br/><br/>
 * Created: 02.06.2022 10:51:13
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class PlatformDescriptor extends DefaultPlatformDescriptor {

	public PlatformDescriptor() {
		super("mongodb", PlatformDescriptor.class.getPackageName());
	}

	@Override
	public XMLStatementParser[] getParsers() {
		return new XMLStatementParser[] { new MongoDBParser()};
	}

}

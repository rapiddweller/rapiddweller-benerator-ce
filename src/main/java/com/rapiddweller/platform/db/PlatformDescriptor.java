/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.platform.db;

import com.rapiddweller.benerator.DefaultPlatformDescriptor;
import com.rapiddweller.benerator.engine.parser.xml.XMLStatementParser;

/**
 * Descriptor for the 'db' platform.<br/><br/>
 * Created: 01.12.2021 17:41:37
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class PlatformDescriptor extends DefaultPlatformDescriptor {

  public PlatformDescriptor() {
    super("db", PlatformDescriptor.class.getPackageName());
  }

  @Override
  public XMLStatementParser[] getParsers() {
    return new XMLStatementParser[] { new DatabaseParser() };
  }

}

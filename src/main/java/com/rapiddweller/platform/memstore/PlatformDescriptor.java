/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.platform.memstore;

import com.rapiddweller.benerator.DefaultPlatformDescriptor;
import com.rapiddweller.benerator.engine.parser.xml.XMLStatementParser;

/**
 * Platform descriptor for the 'memstore' platform.<br/><br/>
 * Created: 01.12.2021 17:59:37
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class PlatformDescriptor extends DefaultPlatformDescriptor {

  public PlatformDescriptor() {
    super("memstore", null);
  }

  @Override
  public XMLStatementParser[] getParsers() {
    return new XMLStatementParser[] { new MemStoreParser()};
  }

}

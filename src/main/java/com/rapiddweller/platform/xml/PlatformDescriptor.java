/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.platform.xml;

import com.rapiddweller.benerator.DefaultPlatformDescriptor;

/**
 * Platfrom descriptor 'xml'.<br/><br/>
 * Created: 06.12.2021 23:08:35
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class PlatformDescriptor extends DefaultPlatformDescriptor {

  public PlatformDescriptor() {
    super("xml", PlatformDescriptor.class.getPackageName());
  }

}

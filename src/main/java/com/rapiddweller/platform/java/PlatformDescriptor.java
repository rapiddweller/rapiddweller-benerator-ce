/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.platform.java;

import com.rapiddweller.benerator.DefaultPlatformDescriptor;

/**
 * PlatformDescriptor for the 'java' package.<br/><br/>
 * Created: 13.12.2021 15:42:00
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class PlatformDescriptor extends DefaultPlatformDescriptor {
  public PlatformDescriptor() {
    super("java", PlatformDescriptor.class.getPackageName());
  }
}

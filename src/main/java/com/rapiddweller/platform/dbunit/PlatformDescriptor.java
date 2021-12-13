/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.platform.dbunit;

import com.rapiddweller.benerator.DefaultPlatformDescriptor;

/**
 * Platform descriptor for the 'dbunit' platform.<br/><br/>
 * Created: 13.12.2021 15:39:39
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class PlatformDescriptor extends DefaultPlatformDescriptor {
  public PlatformDescriptor() {
    super("dbunit", PlatformDescriptor.class.getPackageName());
  }
}

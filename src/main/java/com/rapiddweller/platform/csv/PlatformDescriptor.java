/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.platform.csv;

import com.rapiddweller.benerator.DefaultPlatformDescriptor;

/**
 * Platform descriptor for the 'csv' platform.<br/><br/>
 * Created: 13.12.2021 15:37:47
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class PlatformDescriptor extends DefaultPlatformDescriptor {
  public PlatformDescriptor() {
    super("csv", PlatformDescriptor.class.getPackageName());
  }
}

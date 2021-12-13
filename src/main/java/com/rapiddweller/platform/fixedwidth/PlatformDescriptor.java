/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.platform.fixedwidth;

import com.rapiddweller.benerator.DefaultPlatformDescriptor;

/**
 * PlatformDescriptor for the 'fixedwidth' platform.<br/><br/>
 * Created: 13.12.2021 15:40:45
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class PlatformDescriptor extends DefaultPlatformDescriptor {
  public PlatformDescriptor() {
    super("fixedwidth", PlatformDescriptor.class.getPackageName());
  }
}

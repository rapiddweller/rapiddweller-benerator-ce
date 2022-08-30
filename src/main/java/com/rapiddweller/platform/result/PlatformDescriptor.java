/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.platform.result;

import com.rapiddweller.benerator.DefaultPlatformDescriptor;

/**
 * Platform descriptor for the 'result' platform.<br/><br/>
 * Created: 13.12.2021 15:43:07
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class PlatformDescriptor extends DefaultPlatformDescriptor {
  public PlatformDescriptor() {
    super("result", PlatformDescriptor.class.getPackageName());
  }
}

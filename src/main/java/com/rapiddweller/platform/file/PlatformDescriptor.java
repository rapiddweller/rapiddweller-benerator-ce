/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.platform.file;

import com.rapiddweller.benerator.DefaultPlatformDescriptor;

/**
 * Platform descriptor for the 'file' platform.<br/><br/>
 * Created: 06.12.2021 23:37:23
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class PlatformDescriptor extends DefaultPlatformDescriptor {

  public PlatformDescriptor() {
    super("file", PlatformDescriptor.class.getPackageName());
  }

}

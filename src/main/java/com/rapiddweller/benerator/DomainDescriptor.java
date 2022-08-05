/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator;

/**
 * Describes a domain.<br/><br/>
 * Created: 04.12.2021 12:13:00
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class DomainDescriptor {

  private final String pkgName;

  public DomainDescriptor(String pkgName) {
    this.pkgName = pkgName;
  }

  public String getPackage() {
    return pkgName;
  }

}

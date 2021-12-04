/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.platform.test_no_desc;

/**
 * Simple bean class for testing.<br/><br/>
 * Created: 01.12.2021 16:00:41
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class TNDSimpleBean {

  public String hello(String name) {
    return "Hello " + name + ", " + getClass().getPackageName();
  }

}

/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.util;

import com.rapiddweller.common.ArrayUtil;

/**
 * Provides utilities for parsing command line parameters.<br/><br/>
 * Created: 19.10.2021 22:34:13
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class CliUtil {

  private CliUtil() {
    // private constructor to prevent instantiation of this utility class
  }

  public static boolean containsVersionFlag(String[] args) {
    return ArrayUtil.contains("--version", args)
        || ArrayUtil.contains("-v", args)
        || ArrayUtil.contains("-version", args);
  }

  public static boolean containsHelpFlag(String[] args) {
    return ArrayUtil.contains("--help", args)
        || ArrayUtil.contains("-h", args)
        || ArrayUtil.contains("-help", args);
  }

  public static String getParameter(String name, String[] args) {
    int index = ArrayUtil.indexOf(name, args);
    if (index >= 0 && index < args.length - 1) {
      return args[index + 1];
    } else {
      return null;
    }
  }

}

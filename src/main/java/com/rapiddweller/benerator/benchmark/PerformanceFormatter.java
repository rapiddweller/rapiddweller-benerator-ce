/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.benchmark;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Formats performance numbers < 0 with one fraction digit,
 * numbers greater or equal to 10 without fraction digits.<br/><br/>
 * Created: 04.11.2021 06:29:18
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class PerformanceFormatter {

  public static final DecimalFormat FORMAT_1 = new DecimalFormat("0.0", DecimalFormatSymbols.getInstance(Locale.US));
  public static final DecimalFormat FORMAT_0 = new DecimalFormat("#,##0", DecimalFormatSymbols.getInstance(Locale.US));

  private PerformanceFormatter() {
    // private constructor to prevent instantiation of this utility class
  }

  public static String format(double number) {
    return (number < 10 ? FORMAT_1 : FORMAT_0).format(number);
  }

}

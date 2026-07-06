/* (c) Copyright 2025 by rapiddweller GmbH. All rights reserved. */

package com.rapiddweller.benerator.main;

import com.rapiddweller.benerator.main.datamimic.DescriptorConverter;
import com.rapiddweller.benerator.main.datamimic.MigrationReport;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Command-line tool that translates a Benerator XML descriptor into a native DATAMIMIC DSL
 * descriptor. Standalone {@code main()} in the style of {@code BenchmarkTool} / {@code DBSnapshotTool}.
 *
 * <pre>Usage: DatamimicConverter &lt;input.ben.xml&gt; &lt;output.datamimic.xml&gt; [report.txt]</pre>
 */
public class DatamimicConverter {

  private DatamimicConverter() {
    // utility class
  }

  public static void main(String[] args) throws Exception {
    if (args.length < 2) {
      System.out.println("Usage: DatamimicConverter <input.ben.xml> <output.datamimic.xml> [report.txt]");
      System.exit(1);
      return;
    }
    File input = new File(args[0]);
    File output = new File(args[1]);
    MigrationReport report = new MigrationReport();
    new DescriptorConverter(report).convert(input, output);

    String formatted = report.format();
    System.out.println("Converted " + input.getName() + " -> " + output.getPath());
    System.out.println(formatted);
    if (args.length >= 3) {
      Files.writeString(Path.of(args[2]), formatted);
    }
  }
}

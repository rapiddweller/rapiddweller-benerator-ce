/* (c) Copyright 2025 by rapiddweller GmbH. All rights reserved. */

package com.rapiddweller.benerator.main.datamimic;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * CLI entry point for the Benerator&#8594;DATAMIMIC converter, used by the migration CI stage.
 *
 * <pre>
 *   java -cp &lt;classpath&gt; com.rapiddweller.benerator.main.datamimic.DatamimicConverter \
 *        &lt;input-file-or-dir&gt; &lt;output-dir&gt; [report.txt]
 * </pre>
 *
 * Converts a single {@code *.ben.xml} or every {@code *.ben.xml} under a directory to
 * {@code &lt;output-dir&gt;/&lt;name&gt;.datamimic.xml}, preserving the relative layout so bundled sources
 * (CSV, .sql, includes) resolve when the output is run through DATAMIMIC CE. Prints a one-line summary;
 * exits non-zero only on a hard I/O failure (a per-file conversion error is reported, not fatal, so one
 * bad descriptor does not fail the whole batch).
 */
public final class DatamimicConverter {

  private DatamimicConverter() {
  }

  public static void main(String[] args) throws Exception {
    if (args.length < 2) {
      System.err.println("usage: DatamimicConverter <input-file-or-dir> <output-dir> [report.txt]");
      System.exit(2);
    }
    Path input = Path.of(args[0]);
    Path outDir = Path.of(args[1]);
    Files.createDirectories(outDir);

    List<Path> inputs = new ArrayList<>();
    if (Files.isDirectory(input)) {
      try (Stream<Path> s = Files.walk(input)) {
        s.filter(p -> p.toString().endsWith(".ben.xml")).forEach(inputs::add);
      }
    } else {
      inputs.add(input);
    }

    MigrationReport report = new MigrationReport();
    int ok = 0;
    int failed = 0;
    for (Path in : inputs) {
      Path rel = Files.isDirectory(input) ? input.relativize(in) : in.getFileName();
      String outName = rel.toString().replaceFirst("\\.ben\\.xml$", ".datamimic.xml");
      Path out = outDir.resolve(outName);
      Files.createDirectories(out.getParent());
      try {
        new DescriptorConverter(report).convert(in.toFile(), out.toFile());
        ok++;
      } catch (Exception e) {
        failed++;
        System.err.println("convert FAILED: " + in + " -> " + e.getMessage());
      }
    }

    if (args.length >= 3) {
      Files.writeString(Path.of(args[2]), report.format());
    }
    System.out.printf("Converted %d/%d descriptor(s); %d flagged item(s) for manual review.%n",
        ok, inputs.size(), report.items().size());
    if (failed > 0) {
      System.out.printf("%d descriptor(s) could not be converted (see stderr).%n", failed);
    }
  }
}

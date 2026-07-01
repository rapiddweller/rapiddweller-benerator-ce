/* (c) Copyright 2025 by rapiddweller GmbH. All rights reserved. */

package com.rapiddweller.benerator.main.datamimic;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

    inputs.sort(Path::compareTo);
    System.out.println("Converting " + inputs.size() + " Benerator descriptor(s) from " + input + ":");
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
        System.out.println("  [OK]   " + rel + "  ->  " + outName);
      } catch (Exception e) {
        failed++;
        System.out.println("  [FAIL] " + rel + "  ->  " + e.getMessage());
      }
    }

    // Migrate DB environment files (JDBC URL -> DATAMIMIC host/port/database/dbms) so a DB-backed
    // converted descriptor has coordinates to connect with.
    int envMigrated = 0;
    if (Files.isDirectory(input)) {
      List<Path> envFiles;
      try (Stream<Path> s = Files.walk(input)) {
        envFiles = s.filter(p -> p.getFileName().toString().endsWith(".env.properties")).sorted()
            .collect(java.util.stream.Collectors.toList());
      }
      for (Path envIn : envFiles) {
        Path rel = input.relativize(envIn);
        Map<String, String> migrated = EnvironmentMigrator.migrate(readProps(envIn), report, rel.toString());
        Path envOut = outDir.resolve(rel);
        Files.createDirectories(envOut.getParent());
        writeProps(envOut, migrated);
        envMigrated++;
        System.out.println("  [ENV]  " + rel);
      }
    }

    if (args.length >= 3) {
      Files.writeString(Path.of(args[2]), report.format());
    }
    System.out.printf("Converted %d/%d descriptor(s), migrated %d env file(s); %d item(s) need manual attention.%n",
        ok, inputs.size(), envMigrated, report.attention().size());
    if (failed > 0) {
      System.out.printf("%d descriptor(s) could not be converted (see stderr).%n", failed);
    }
  }

  /** Read a .properties file into an insertion-ordered map (key = first '='; '#'/'!' and blanks skipped). */
  private static Map<String, String> readProps(Path file) throws Exception {
    Map<String, String> props = new LinkedHashMap<>();
    for (String line : Files.readAllLines(file)) {
      String trimmed = line.trim();
      if (trimmed.isEmpty() || trimmed.startsWith("#") || trimmed.startsWith("!")) {
        continue;
      }
      int eq = trimmed.indexOf('=');
      if (eq > 0) {
        props.put(trimmed.substring(0, eq).trim(), trimmed.substring(eq + 1).trim());
      }
    }
    return props;
  }

  private static void writeProps(Path file, Map<String, String> props) throws Exception {
    StringBuilder sb = new StringBuilder("# Migrated from Benerator by DatamimicConverter\n");
    props.forEach((k, v) -> sb.append(k).append('=').append(v).append('\n'));
    Files.writeString(file, sb.toString());
  }
}

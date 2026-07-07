/* (c) Copyright 2025 by rapiddweller GmbH. All rights reserved. */

package com.rapiddweller.benerator.main.datamimic;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    // File -> its report items (a slice of the shared report, taken around each conversion), so the
    // summary can attribute every finding to the descriptor it came from. Insertion order = batch order.
    Map<String, List<MigrationReport.Item>> perFile = new LinkedHashMap<>();
    Map<String, String> failures = new LinkedHashMap<>();
    int ok = 0;
    int failed = 0;
    // environment name -> (system prefix -> "db"|"mongo"), merged across all descriptors so the
    // env-properties migration knows each system's type and the flat-format fallback prefix.
    Map<String, Map<String, String>> envSystems = new LinkedHashMap<>();
    // Resource files the conversion itself produced (e.g. a .fcw with an added spec header) -
    // the verbatim resource copy below must skip these, not clobber them with the raw input.
    java.util.Set<String> conversionWritten = new java.util.LinkedHashSet<>();
    for (Path in : inputs) {
      Path rel = Files.isDirectory(input) ? input.relativize(in) : in.getFileName();
      String outName = rel.toString().replaceFirst("\\.ben\\.xml$", ".datamimic.xml");
      Path out = outDir.resolve(outName);
      Files.createDirectories(out.getParent());
      int before = report.items().size();
      try {
        DescriptorConverter converter = new DescriptorConverter(report);
        converter.convert(in.toFile(), out.toFile());
        for (Map.Entry<String, Map<String, String>> e : converter.envSystems().entrySet()) {
          envSystems.computeIfAbsent(e.getKey(), k -> new LinkedHashMap<>()).putAll(e.getValue());
        }
        conversionWritten.addAll(converter.writtenResources());
        ok++;
        System.out.println("  [OK]   " + rel + "  ->  " + outName);
      } catch (Exception e) {
        failed++;
        failures.put(rel.toString(), e.getMessage());
        System.out.println("  [FAIL] " + rel + "  ->  " + e.getMessage());
      }
      perFile.put(rel.toString(), new ArrayList<>(report.items().subList(before, report.items().size())));
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
        int before = report.items().size();
        // <env>.env.properties -> the systems the descriptors bind to environment <env>
        String envName = envIn.getFileName().toString().replaceFirst("\\.env\\.properties$", "");
        Map<String, String> systems = envSystems.getOrDefault(envName, java.util.Collections.emptyMap());
        Map<String, String> migrated = EnvironmentMigrator.migrate(readProps(envIn), report, rel.toString(), systems);
        Path envOut = outDir.resolve(rel);
        Files.createDirectories(envOut.getParent());
        writeProps(envOut, migrated);
        envMigrated++;
        System.out.println("  [ENV]  " + rel);
        perFile.put(rel.toString(), new ArrayList<>(report.items().subList(before, report.items().size())));
      }
    }

    // Copy every non-descriptor resource (CSV/XLS sources, scripts, properties, schemas) alongside the
    // converted XML - the descriptors reference them relatively, so without this no file-based
    // descriptor can run from the output tree.
    int resourcesCopied = 0;
    if (Files.isDirectory(input)) {
      List<Path> resources;
      try (Stream<Path> s = Files.walk(input)) {
        resources = s.filter(Files::isRegularFile)
            .filter(p -> !p.toString().endsWith(".ben.xml"))
            .filter(p -> !p.getFileName().toString().endsWith(".env.properties")) // migrated above
            .sorted()
            .collect(java.util.stream.Collectors.toList());
      }
      for (Path res : resources) {
        Path dest = outDir.resolve(input.relativize(res));
        if (conversionWritten.contains(dest.toAbsolutePath().toString())) {
          continue; // the conversion wrote a modified version (e.g. a .fcw with its spec header)
        }
        Files.createDirectories(dest.getParent());
        Files.copy(res, dest, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        resourcesCopied++;
      }
      System.out.println("  [RES]  copied " + resourcesCopied + " data/script resource(s)");
    }

    if (args.length >= 3) {
      Files.writeString(Path.of(args[2]), report.format());
    }
    String summary = buildSummary(perFile, failures, inputs.size(), report);
    System.out.println();
    System.out.println(summary);
    Files.writeString(outDir.resolve("migration-summary.md"), summary);
    System.out.printf("Converted %d/%d descriptor(s), migrated %d env file(s); %d item(s) need manual attention"
        + " (see migration-summary.md).%n", ok, inputs.size(), envMigrated, report.attention().size());
    if (failed > 0) {
      System.out.printf("%d descriptor(s) could not be converted (see stderr).%n", failed);
    }
  }

  /**
   * Action-oriented batch summary (stdout + {@code migration-summary.md}): how many descriptors convert
   * clean, then one row per file with open points - its manual-work kinds (deduplicated, counted) and the
   * matching MIGRATION_PLAYBOOK.md recipe links. info() findings appear only as a total.
   */
  private static String buildSummary(Map<String, List<MigrationReport.Item>> perFile,
                                     Map<String, String> failures, int descriptorTotal, MigrationReport report) {
    StringBuilder sb = new StringBuilder("# Migration summary\n\n");
    long clean = perFile.entrySet().stream()
        .filter(e -> e.getKey().endsWith(".ben.xml") && !failures.containsKey(e.getKey())
            && e.getValue().stream().noneMatch(it -> !it.info))
        .count();
    sb.append("**").append(clean).append(" of ").append(descriptorTotal)
        .append(" descriptors convert with no manual work.**\n\n");

    boolean anyRow = failures.size() > 0
        || perFile.values().stream().flatMap(List::stream).anyMatch(it -> !it.info);
    if (anyRow) {
      sb.append("| File | Manual work | Playbook |\n|---|---|---|\n");
      for (Map.Entry<String, List<MigrationReport.Item>> e : perFile.entrySet()) {
        List<MigrationReport.Item> attention = e.getValue().stream().filter(it -> !it.info).collect(java.util.stream.Collectors.toList());
        if (attention.isEmpty() && !failures.containsKey(e.getKey())) {
          continue;
        }
        // kind -> count, deduplicated in first-seen order
        Map<String, Integer> kinds = new LinkedHashMap<>();
        Set<String> anchors = new LinkedHashSet<>();
        for (MigrationReport.Item it : attention) {
          kinds.merge(it.kind, 1, Integer::sum);
          String anchor = playbookAnchor(it);
          if (anchor != null) {
            anchors.add(anchor);
          }
        }
        StringBuilder work = new StringBuilder();
        if (failures.containsKey(e.getKey())) {
          work.append("FAILED: ").append(failures.get(e.getKey()));
        }
        kinds.forEach((kind, count) ->
            work.append(work.length() > 0 ? ", " : "").append(kind).append(" x").append(count));
        String links = anchors.stream()
            .map(a -> "[" + a + "](MIGRATION_PLAYBOOK.md#" + a + ")")
            .collect(java.util.stream.Collectors.joining(", "));
        sb.append("| ").append(e.getKey()).append(" | ").append(work).append(" | ").append(links).append(" |\n");
      }
      sb.append('\n');
    }
    long infos = report.items().size() - report.attention().size();
    if (infos > 0) {
      sb.append(infos).append(" finding(s) were converted automatically (informational, no action).\n");
    }
    return sb.toString();
  }

  /** MIGRATION_PLAYBOOK.md anchor for a manual-work item's report kind; null when no recipe exists. */
  private static String playbookAnchor(MigrationReport.Item item) {
    switch (item.kind) {
      case "execute":
        return "execute-js";
      case "condition":
      case "if":
        return "setup-if";
      case "evaluate":
        return "evaluate-without-assert";
      case "consumer":
        return "no-equivalent-consumer";
      case "generator":
        return "unknown-generators";
      case "reference":
        return "reference-selector-type";
      case "element": // the flagged tag is the first <...> in the detail text
        int lt = item.detail.indexOf('<');
        int gt = item.detail.indexOf('>', lt);
        String tag = lt >= 0 && gt > lt ? item.detail.substring(lt + 1, gt) : "";
        switch (tag) {
          case "bean":
            return "bean";
          case "value":
            return "value";
          case "pre-parse-generate":
            return "pre-parse-generate";
          case "transcodingTask":
          case "transcode":
          case "meta-model":
            return "transcoding-meta-model";
          default:
            return null;
        }
      default: // attribute / database / type / converter / while - no dedicated playbook recipe
        return null;
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

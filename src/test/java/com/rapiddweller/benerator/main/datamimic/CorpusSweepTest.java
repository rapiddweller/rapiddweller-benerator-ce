/* (c) Copyright 2025 by rapiddweller GmbH. All rights reserved. */

package com.rapiddweller.benerator.main.datamimic;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

/**
 * Sweeps the whole Benerator demo + test corpus through the converter and reports coverage: how many
 * descriptors convert, how many throw, and a frequency table of every construct flagged for manual
 * migration. Not an assertion test — a gap-analysis harness (run with -Dtest=CorpusSweepTest).
 */
public class CorpusSweepTest {

  private static final String[] ROOTS = {
      "src/demo/resources/demo",
      "src/test/resources/com/rapiddweller",
  };

  @Test
  public void sweep() throws Exception {
    List<Path> files = new java.util.ArrayList<>();
    for (String root : ROOTS) {
      Path p = Path.of(root);
      if (!Files.isDirectory(p)) {
        continue;
      }
      try (Stream<Path> s = Files.walk(p)) {
        files.addAll(s.filter(f -> f.toString().endsWith(".ben.xml"))
            .filter(f -> !f.toString().contains("/target/"))
            .collect(Collectors.toList()));
      }
    }

    int ok = 0;
    int threw = 0;
    Map<String, AtomicInteger> byKind = new TreeMap<>();
    Map<String, AtomicInteger> byDetail = new TreeMap<>();
    Map<String, AtomicInteger> byException = new TreeMap<>();

    for (Path f : files) {
      MigrationReport report = new MigrationReport();
      File out = File.createTempFile("conv", ".xml");
      out.deleteOnExit();
      try {
        new DescriptorConverter(report).convert(f.toFile(), out);
        ok++;
        for (MigrationReport.Item it : report.items()) {
          byKind.computeIfAbsent(it.kind, k -> new AtomicInteger()).incrementAndGet();
          byDetail.computeIfAbsent(it.kind + "\t" + token(it.detail), k -> new AtomicInteger())
              .incrementAndGet();
        }
      } catch (Exception | StackOverflowError e) {
        threw++;
        String key = e.getClass().getSimpleName()
            + (e.getMessage() == null ? "" : ": " + e.getMessage().split("\n")[0]);
        byException.computeIfAbsent(key.length() > 90 ? key.substring(0, 90) : key,
            k -> new AtomicInteger()).incrementAndGet();
      }
    }

    System.out.println("\n===== CONVERTER CORPUS SWEEP =====");
    System.out.println("files: " + files.size() + " | converted: " + ok + " | threw: " + threw);
    System.out.println("\n--- flagged for manual migration, by kind (count) ---");
    byKind.entrySet().stream()
        .sorted((a, b) -> b.getValue().get() - a.getValue().get())
        .forEach(e -> System.out.println("  " + e.getValue().get() + "\t" + e.getKey()));
    System.out.println("\n--- top flagged constructs, by kind + name (count) ---");
    byDetail.entrySet().stream()
        .sorted((a, b) -> b.getValue().get() - a.getValue().get())
        .limit(40)
        .forEach(e -> System.out.println("  " + e.getValue().get() + "\t" + e.getKey()));
    System.out.println("\n--- conversions that THREW (count) ---");
    byException.entrySet().stream()
        .sorted((a, b) -> b.getValue().get() - a.getValue().get())
        .forEach(e -> System.out.println("  " + e.getValue().get() + "\t" + e.getKey()));
    System.out.println("===== END SWEEP =====\n");
  }

  /** Pull the first &lt;tag&gt; or 'name' token out of a report detail so gaps aggregate by construct. */
  private static String token(String detail) {
    if (detail == null) {
      return "?";
    }
    int lt = detail.indexOf('<');
    if (lt >= 0 && detail.indexOf('>', lt) > lt) {
      return detail.substring(lt, detail.indexOf('>', lt) + 1);
    }
    int q = detail.indexOf('\'');
    if (q >= 0 && detail.indexOf('\'', q + 1) > q) {
      return "'" + detail.substring(q + 1, detail.indexOf('\'', q + 1)) + "'";
    }
    return detail.length() > 40 ? detail.substring(0, 40) : detail;
  }
}

/* (c) Copyright 2025 by rapiddweller GmbH. All rights reserved. */

package com.rapiddweller.benerator.main.datamimic;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

/**
 * Sweeps the whole Benerator demo + test corpus through the converter and gates coverage against the
 * checked-in baseline (gap-baseline.properties): the number of descriptors that throw and the number of
 * manual-attention findings must never rise. Lowering a number is a deliberate commit (test prints the
 * hint). The full frequency tables land in target/gap-report.txt — MIGRATION_GAPS.md is updated from
 * that file, never by hand.
 */
public class CorpusSweepTest {

  private static final String BASELINE_RESOURCE = "gap-baseline.properties";

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
    int manual = 0;
    int infos = 0;
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
          if (it.info) {
            infos++;
          } else {
            manual++;
          }
          String kind = (it.info ? "info:" : "") + it.kind;
          byKind.computeIfAbsent(kind, k -> new AtomicInteger()).incrementAndGet();
          byDetail.computeIfAbsent(kind + "\t" + token(it.detail), k -> new AtomicInteger())
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

    StringBuilder rep = new StringBuilder();
    rep.append("===== CONVERTER CORPUS SWEEP =====\n");
    rep.append("files: ").append(files.size()).append(" | converted: ").append(ok)
        .append(" | threw: ").append(threw)
        .append(" | manual findings: ").append(manual)
        .append(" | info findings: ").append(infos).append('\n');
    rep.append("\n--- findings by kind (info:* = converted automatically, FYI) ---\n");
    byKind.entrySet().stream()
        .sorted((a, b) -> b.getValue().get() - a.getValue().get())
        .forEach(e -> rep.append("  ").append(e.getValue().get()).append('\t').append(e.getKey()).append('\n'));
    rep.append("\n--- top findings, by kind + name ---\n");
    byDetail.entrySet().stream()
        .sorted((a, b) -> b.getValue().get() - a.getValue().get())
        .limit(60)
        .forEach(e -> rep.append("  ").append(e.getValue().get()).append('\t').append(e.getKey()).append('\n'));
    rep.append("\n--- conversions that THREW ---\n");
    byException.entrySet().stream()
        .sorted((a, b) -> b.getValue().get() - a.getValue().get())
        .forEach(e -> rep.append("  ").append(e.getValue().get()).append('\t').append(e.getKey()).append('\n'));
    rep.append("===== END SWEEP =====\n");
    System.out.println("\n" + rep);
    Files.createDirectories(Path.of("target"));
    Files.writeString(Path.of("target/gap-report.txt"), rep);

    Properties baseline = new Properties();
    try (InputStream in = CorpusSweepTest.class.getResourceAsStream(BASELINE_RESOURCE)) {
      assertTrue("Missing baseline resource " + BASELINE_RESOURCE
          + " next to CorpusSweepTest — create it with:\nthrew=" + threw + "\nmanualFindings=" + manual,
          in != null);
      baseline.load(in);
    }
    int baseThrew = Integer.parseInt(baseline.getProperty("threw"));
    int baseManual = Integer.parseInt(baseline.getProperty("manualFindings"));
    if (threw < baseThrew || manual < baseManual) {
      System.out.println("Baseline can be lowered to: threw=" + threw + ", manualFindings=" + manual
          + " (edit " + BASELINE_RESOURCE + " in this commit).");
    }
    assertTrue("Converter regression: " + threw + " descriptors threw (baseline " + baseThrew
        + "). See target/gap-report.txt.", threw <= baseThrew);
    assertTrue("Converter regression: " + manual + " manual-attention findings (baseline " + baseManual
        + "). Fix the mapping or consciously raise the baseline. See target/gap-report.txt.",
        manual <= baseManual);
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

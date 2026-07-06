/*
 * Copyright (C) 2025 rapiddweller GmbH.
 * Licensed under the GPL (see the LICENSE file of the Benerator CE project).
 */

package com.rapiddweller.benerator.main.datamimic;

import java.util.ArrayList;
import java.util.List;

/**
 * Collects everything the Benerator-&gt;DATAMIMIC converter could not translate automatically, so a
 * partial migration is honest rather than silently wrong. Each item names where it occurred, what it
 * was, and why it needs manual attention.
 */
public class MigrationReport {

  /** One reported item. {@code info} items were converted automatically (FYI only); the rest need work. */
  public static final class Item {
    public final String location;
    public final String kind;
    public final String detail;
    public final boolean info;

    Item(String location, String kind, String detail, boolean info) {
      this.location = location;
      this.kind = kind;
      this.detail = detail;
      this.info = info;
    }

    @Override
    public String toString() {
      return "  - [" + kind + "] " + location + " : " + detail;
    }
  }

  private final List<Item> items = new ArrayList<>();

  /** Record something that needs MANUAL migration (no automatic equivalent). */
  public void add(String location, String kind, String detail) {
    items.add(new Item(location, kind, detail, false));
  }

  /** Record an automatic/informational transformation (converted OK; shown for transparency, no action). */
  public void info(String location, String kind, String detail) {
    items.add(new Item(location, kind, detail, true));
  }

  public List<Item> items() {
    return items;
  }

  /** Only the items that genuinely need manual attention (excludes {@link #info} notes). */
  public List<Item> attention() {
    return items.stream().filter(it -> !it.info).collect(java.util.stream.Collectors.toList());
  }

  public boolean isEmpty() {
    return items.isEmpty();
  }

  public String format() {
    List<Item> attention = attention();
    if (attention.isEmpty()) {
      return "Migration complete - no manual steps needed.";
    }
    StringBuilder sb = new StringBuilder("Migration report - ")
        .append(attention.size()).append(" item(s) need manual attention:\n");
    for (Item it : attention) {
      sb.append(it).append('\n');
    }
    long infoCount = items.size() - attention.size();
    if (infoCount > 0) {
      sb.append("\n(").append(infoCount).append(" item(s) converted automatically - informational, no action)\n");
    }
    return sb.toString();
  }
}

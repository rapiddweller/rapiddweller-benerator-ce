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

  /** One thing that needs manual migration. */
  public static final class Item {
    public final String location;
    public final String kind;
    public final String detail;

    Item(String location, String kind, String detail) {
      this.location = location;
      this.kind = kind;
      this.detail = detail;
    }

    @Override
    public String toString() {
      return "  - [" + kind + "] " + location + " : " + detail;
    }
  }

  private final List<Item> items = new ArrayList<>();

  public void add(String location, String kind, String detail) {
    items.add(new Item(location, kind, detail));
  }

  public List<Item> items() {
    return items;
  }

  public boolean isEmpty() {
    return items.isEmpty();
  }

  public String format() {
    if (items.isEmpty()) {
      return "Migration complete - no manual steps needed.";
    }
    StringBuilder sb = new StringBuilder("Migration report - ")
        .append(items.size()).append(" item(s) need manual attention:\n");
    for (Item it : items) {
      sb.append(it).append('\n');
    }
    return sb.toString();
  }
}

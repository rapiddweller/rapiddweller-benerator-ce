/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.benchmark;

import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.common.comparator.IntComparator;

import java.util.Objects;

/**
 * Specifies a Benerator edition and execution thread count.<br/><br/>
 * Created: 02.11.2021 07:33:09
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class ExecutionMode implements Comparable<ExecutionMode> {

  private final boolean ee;
  private final int threadCount;

  public ExecutionMode(boolean ee, int threadCount) {
    this.ee = ee;
    if (!ee && threadCount > 1) {
      throw BeneratorExceptionFactory.getInstance().illegalArgument("Cannot call CE concurrently");
    }
    this.threadCount = threadCount;
  }

  public boolean isEe() {
    return this.ee;
  }

  public int getThreadCount() {
    return threadCount;
  }

  @Override
  public int compareTo(ExecutionMode that) {
    // ce < ee
    if (this.ee && !that.ee) {
      return 1;
    } else if (!this.ee && that.ee) {
      return -1;
    }
    // if the editions are equal, then compare the thread count
    return IntComparator.compare(this.threadCount, that.threadCount);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExecutionMode that = (ExecutionMode) o;
    return ee == that.ee && threadCount == that.threadCount;
  }

  @Override
  public int hashCode() {
    return Objects.hash(ee, threadCount);
  }

}

/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.benchmark;

import java.io.IOException;

/**
 * Common interface for Benchmark result exporters.<br/><br/>
 * Created: 16.11.2021 10:19:31
 * @author Volker Bergmann
 * @since 3.0.0
 */
public interface BenchmarkResultExporter {
  void export(BenchmarkToolReport result) throws IOException;
}

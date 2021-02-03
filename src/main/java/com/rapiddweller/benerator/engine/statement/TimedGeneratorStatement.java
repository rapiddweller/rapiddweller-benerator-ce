/*
 * (c) Copyright 2006-2020 by rapiddweller GmbH & Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General Public License.
 *
 * For redistributing this software or a derivative work under a license other
 * than the GPL-compatible Free Software License as defined by the Free
 * Software Foundation or approved by OSI, you must first obtain a commercial
 * license to this software product from rapiddweller GmbH & Volker Bergmann.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED CONDITIONS,
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE
 * HEREBY EXCLUDED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.rapiddweller.benerator.engine.statement;

import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.BeneratorMonitor;
import com.rapiddweller.benerator.engine.Statement;
import com.rapiddweller.common.time.ElapsedTimeFormatter;
import com.rapiddweller.profile.Profiler;
import com.rapiddweller.profile.Profiling;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Locale;

/**
 * {Task} implementation that acts as a proxy to another tasks, forwards calls to it,
 * measures execution times and logs them.<br/><br/>
 * Created at 23.07.2009 06:55:46
 *
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class TimedGeneratorStatement extends StatementProxy {

  private static final Logger logger = LogManager.getLogger(TimedGeneratorStatement.class);

  private final String name;
  /**
   * The Profiler path.
   */
  final List<String> profilerPath;
  private final boolean logging;
  private final ElapsedTimeFormatter elapsedTimeFormatter;

  /**
   * Instantiates a new Timed generator statement.
   *
   * @param name          the name
   * @param realStatement the real statement
   * @param profilerPath  the profiler path
   * @param logging       the logging
   */
  public TimedGeneratorStatement(String name, Statement realStatement, List<String> profilerPath, boolean logging) {
    super(realStatement);
    this.name = name;
    this.profilerPath = profilerPath;
    this.logging = logging;
    this.elapsedTimeFormatter = new ElapsedTimeFormatter(Locale.US, " ", false);
  }

  @Override
  public boolean execute(BeneratorContext context) {
    long c0 = BeneratorMonitor.INSTANCE.getTotalGenerationCount();
    long t0 = System.currentTimeMillis();
    boolean result = super.execute(context);
    long dc = BeneratorMonitor.INSTANCE.getTotalGenerationCount() - c0;
    long dt = System.currentTimeMillis() - t0;
    if (logging) {
      if (dc == 0) {
        logger.info("No data created for '" + name + "' setup");
      } else if (dt > 0) {
        logger.info("Created " + dc + " data sets from '"
            + name + "' setup in " + elapsedTimeFormatter.convert(dt)
            + " (" + (dc * 1000 / dt) + "/s)");
      } else {
        logger.info("Created " + dc + " '" + name + "' data set(s)");
      }
    }
    if (Profiling.isEnabled()) {
      Profiler.defaultInstance().addSample(profilerPath, dt);
    }
    return result;
  }

}

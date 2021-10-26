/*
 * (c) Copyright 2006-2021 by rapiddweller GmbH & Volker Bergmann. All rights reserved.
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

package com.rapiddweller.benerator.main;

import com.rapiddweller.benerator.BeneratorConstants;
import com.rapiddweller.benerator.BeneratorError;
import com.rapiddweller.benerator.BeneratorFactory;
import com.rapiddweller.benerator.BeneratorMode;
import com.rapiddweller.benerator.BeneratorUtil;
import com.rapiddweller.benerator.engine.BeneratorMonitor;
import com.rapiddweller.benerator.engine.BeneratorRootContext;
import com.rapiddweller.benerator.engine.DefaultBeneratorFactory;
import com.rapiddweller.benerator.engine.DescriptorRunner;
import com.rapiddweller.common.Assert;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.common.LogCategoriesConstants;
import com.rapiddweller.common.cli.CommandLineParser;
import com.rapiddweller.common.log.LoggingInfoPrinter;
import com.rapiddweller.common.ui.ConsoleInfoPrinter;
import com.rapiddweller.common.ui.InfoPrinter;
import com.rapiddweller.common.version.VersionInfo;
import com.rapiddweller.common.version.VersionNumber;
import com.rapiddweller.common.version.VersionNumberParser;
import com.rapiddweller.contiperf.sensor.MemorySensor;
import com.rapiddweller.format.text.KiloFormatter;
import com.rapiddweller.jdbacl.DBUtil;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.IOException;

/**
 * Parses and executes a benerator setup file.<br/><br/>
 * Created: 14.08.2007 19:14:28
 * @author Volker Bergmann
 */
public class Benerator {

  private static final Logger logger = LoggerFactory.getLogger(Benerator.class);

  public static final String BENERATOR_KEY = "benerator";

  public static final String MAINTAINER = "rapiddweller";

  protected static final String[] CE_CLI_HELP = {
      "Usage benerator [options] [filename]",
      "",
      "  if [filename] is left out, it defaults to benerator.xml",
      "",
      "Options:",
      "  -v,--version           Display system and version information",
      "  -h,--help              Display help information",
      "  --mode <spec>          activates Benerator mode strict, lenient or ",
      "                         turbo (default: lenient)",
  };

  private static BeneratorMode mode = BeneratorMode.LENIENT;


  // main ------------------------------------------------------------------------------------------------------------

  public static void main(String[] args) throws IOException {
    VersionInfo.getInfo(BENERATOR_KEY).verifyDependencies();
    BeneratorConfig config = parseCommandLine(args);
    run(config);
  }

  // info properties -------------------------------------------------------------------------------------------------

  public boolean isCommunityEdition() {
    return (DefaultBeneratorFactory.COMMUNITY_EDITION.equals(getEdition()));
  }

  public static String getEdition() {
    return BeneratorFactory.getInstance().getEdition();
  }

  public String getVersion() {
    return "Benerator " + getEdition() + " " + VersionInfo.getInfo(BENERATOR_KEY).getVersion();
  }

  public VersionNumber getVersionNumber() {
    return new VersionNumberParser().parse(VersionInfo.getInfo(BENERATOR_KEY).getVersion());
  }

  public static boolean isStrict() {
    return (mode == BeneratorMode.STRICT);
  }

  public static boolean isTurbo() {
    return (mode == BeneratorMode.TURBO);
  }

  public static BeneratorMode getMode() {
    return mode;
  }

  public static void setMode(BeneratorMode mode) {
    Benerator.mode = Assert.notNull(mode, "mode");
  }

  //  operational interface ------------------------------------------------------------------------------------------

  public static void run(BeneratorConfig config) throws IOException {
    if (config.isHelp()) {
      ConsoleInfoPrinter.printHelp(CE_CLI_HELP);
      System.exit(BeneratorConstants.EXIT_CODE_NORMAL);
    }
    if (config.isVersion()) {
      BeneratorUtil.printVersionInfo(false, new ConsoleInfoPrinter());
      System.exit(BeneratorConstants.EXIT_CODE_NORMAL);
    }
    Benerator.setMode(config.getMode());
    new Benerator().runFile(config.getFile());
  }

  public void runFile(String filename) throws IOException {
    // Run descriptor file
    DescriptorRunner runner = null;
    try {
      // log separator in order to distinguish benerator runs in the log file
      logger.info("-------------------------------------------------------------" +
          "-----------------------------------------------------------");
      InfoPrinter printer = new LoggingInfoPrinter(LogCategoriesConstants.CONFIG);
      BeneratorMonitor.INSTANCE.reset();
      MemorySensor memProfiler = MemorySensor.getInstance();
      memProfiler.reset();
      printer.printLines("Running file " + filename);
      BeneratorUtil.checkSystem(printer);
      BeneratorRootContext context = BeneratorFactory.getInstance().createRootContext(IOUtil.getParentUri(filename));
      runner = new DescriptorRunner(filename, context);
      runner.run();
      BeneratorUtil.logConfig("Max. committed heap size: " + new KiloFormatter(1024).format(memProfiler.getMaxCommittedHeapSize()) + "B");
      DBUtil.assertAllDbResourcesClosed(false);
    } catch (BeneratorError e) {
      logger.error(e.getMessage(), e);
      System.exit(e.getCode());
    } finally {
      IOUtil.close(runner);
    }
  }


  // helper methods --------------------------------------------------------------------------------------------------

  static BeneratorConfig parseCommandLine(String... args) {
    BeneratorConfig result = new BeneratorConfig();
    CommandLineParser p = new CommandLineParser();
    p.addOption("mode", "--mode", "-m");
    p.addArgument("file", false);
    return p.parse(result, args);
  }

}

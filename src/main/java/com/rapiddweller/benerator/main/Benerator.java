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

import com.rapiddweller.benerator.BeneratorErrorIds;
import com.rapiddweller.benerator.BeneratorFactory;
import com.rapiddweller.benerator.BeneratorMode;
import com.rapiddweller.benerator.BeneratorUtil;
import com.rapiddweller.benerator.engine.BeneratorMonitor;
import com.rapiddweller.benerator.engine.BeneratorResult;
import com.rapiddweller.benerator.engine.BeneratorRootContext;
import com.rapiddweller.benerator.engine.DefaultBeneratorFactory;
import com.rapiddweller.benerator.engine.DescriptorRunner;
import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.benerator.sensor.Profiling;
import com.rapiddweller.common.Assert;
import com.rapiddweller.common.ExceptionUtil;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.common.LogCategoriesConstants;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.common.cli.CommandLineParser;
import com.rapiddweller.common.converter.ConverterManager;
import com.rapiddweller.common.exception.ApplicationException;
import com.rapiddweller.common.exception.ExitCodes;
import com.rapiddweller.common.log.LoggingPrinter;
import com.rapiddweller.common.ui.ConsolePrinter;
import com.rapiddweller.common.ui.TextPrinter;
import com.rapiddweller.common.version.VersionInfo;
import com.rapiddweller.common.version.VersionNumber;
import com.rapiddweller.common.version.VersionNumberParser;
import com.rapiddweller.contiperf.sensor.MemorySensor;
import com.rapiddweller.format.script.ScriptUtil;
import com.rapiddweller.format.text.KiloFormatter;
import com.rapiddweller.jdbacl.DBUtil;
import com.rapiddweller.jdbacl.DatabaseDialectManager;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;

/**
 * Parses and executes a benerator setup file.<br/><br/>
 * Created: 14.08.2007 19:14:28
 * @author Volker Bergmann
 */
public class Benerator {

  protected static final Logger logger = LoggerFactory.getLogger(Benerator.class);

  static {
    BeneratorExceptionFactory.getInstance();
  }

  public static final String BENERATOR_KEY = "benerator";

  public static final String MAINTAINER = "rapiddweller";

  protected static final String[] CE_CLI_HELP = {
      "Usage: benerator [options] [filename]",
      "",
      "  if [filename] is left out, it defaults to benerator.xml",
      "",
      "Options:",
      "  --version,-v           Display system and version information",
      "  --help,-h              Display help information",
      "  --list <type>          List the available environments or systems. ",
      "                         <type> may be env, db or kafka.",
      "  --clearCaches          Clear all caches",
      "  --mode <spec>          Activate Benerator mode strict, lenient or turbo ",
      "                         (default: lenient)"
  };

  private static BeneratorMode mode = BeneratorMode.LENIENT;


  // main ------------------------------------------------------------------------------------------------------------

  public static void main(String[] args) {
    VersionInfo.getInfo(BENERATOR_KEY).verifyDependencies();
    BeneratorResult result = runWithArgs(args);
    if (!StringUtil.isEmpty(result.getErrOut())) {
      ConsolePrinter.printError(result.getErrOut());
      logger.error(result.getErrOut());
    }
    System.exit(result.getExitCode());
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

  public static boolean isLenient() {
    return (mode == BeneratorMode.LENIENT);
  }

  public static BeneratorMode getMode() {
    return mode;
  }

  public static void setMode(BeneratorMode mode) {
    Benerator.mode = Assert.notNull(mode, "mode");
  }

  //  operational interface ------------------------------------------------------------------------------------------

  public static BeneratorResult runWithArgs(String... args) {
    String renderedArgs = CommandLineParser.formatArgs(args);
    logger.info("benerator {}", renderedArgs);
    BeneratorConfig config = null;
    try {
      config = parseCommandLine(args);
      run(config);
      return new BeneratorResult(ExitCodes.OK, null);
    } catch (Exception e) {
      return handleException(e, config);
    }
  }

  public static void run(BeneratorConfig config) {
    boolean run = true;
    if (config.isHelp()) {
      ConsolePrinter.printStandard(CE_CLI_HELP);
      run = false;
    }
    if (config.isVersion()) {
      ConsolePrinter.printStandard(BeneratorFactory.getInstance().getVersionInfo(false));
      run = false;
    }
    if (config.getList() != null) {
      String arg = config.getList();
      if ("env".equals(arg)) {
        ConsolePrinter.printStandard(BeneratorUtil.formatEnvironmentList());
      } else if ("db".equals(arg)) {
        BeneratorUtil.printEnvDbs(new ConsolePrinter());
      } else if ("kafka".equals(arg)) {
        BeneratorUtil.printEnvKafkas(new ConsolePrinter());
      } else {
        throw BeneratorExceptionFactory.getInstance().illegalCommandLineOption(arg);
      }
      run = false;
    }
    if (config.isClearCaches()) {
      BeneratorUtil.clearCaches();
      run = false;
    }
    if (run) {
      checkComponents();
      Benerator.setMode(config.getMode());
      new Benerator().runFile(config.getFile());
    }
  }

  private static void checkComponents() {
    // check setup of ConverterManager
    try {
      ConverterManager.getInstance();
    } catch (Exception e) {
      throw BeneratorExceptionFactory.getInstance().componentInitializationFailed(
          "ConverterManager", e, BeneratorErrorIds.COMP_INIT_FAILED_CONVERTER);
    }
    // check setup of DelocalizingConverter
    try {
      BeneratorFactory.getInstance().createDelocalizingConverter();
    } catch (Exception e) {
      throw BeneratorExceptionFactory.getInstance().componentInitializationFailed(
          "DelocalizingConverter", e, BeneratorErrorIds.COMP_INIT_FAILED_CONVERTER);
    }
    // check setup of rd-lib-script
    try {
      Assert.notNull(ScriptUtil.getDefaultScriptEngine(), "Default script engine");
    } catch (Exception e) {
      throw BeneratorExceptionFactory.getInstance().componentInitializationFailed(
          "rd-lib-script", e, BeneratorErrorIds.COMP_INIT_FAILED_SCRIPT);
    }
    // check setup of rd-lib-jdbacl
    try {
      DatabaseDialectManager.init();
    } catch (Exception e) {
      throw BeneratorExceptionFactory.getInstance().componentInitializationFailed(
          "rd-lib-jdbacl", e, BeneratorErrorIds.COMP_INIT_FAILED_JDBACL);
    }
    // check setup of BeneratorMonitor
    try {
      MBeanServer server = ManagementFactory.getPlatformMBeanServer();
      MBeanInfo info = server.getMBeanInfo(BeneratorMonitor.OBJECT_NAME);
      Assert.notNull(info, "MBean info");
    } catch (Exception e) {
      throw BeneratorExceptionFactory.getInstance().componentInitializationFailed(
          "BeneratorMonitor", e, BeneratorErrorIds.COMP_INIT_FAILED_BEN_MONITOR);
    }
  }

  public void runFile(String filename) {
    // log separator in order to distinguish benerator runs in the log file
    logger.info("-------------------------------------------------------------" +
        "-----------------------------------------------------------");
    TextPrinter printer = new LoggingPrinter(LogCategoriesConstants.CONFIG);
    BeneratorMonitor.INSTANCE.reset();
    MemorySensor memProfiler = MemorySensor.getInstance();
    memProfiler.reset();
    printer.printStd("Running file " + filename);
    BeneratorUtil.checkSystem(printer);
    BeneratorRootContext context = BeneratorFactory.getInstance().createRootContext(IOUtil.getParentUri(filename));
    try (DescriptorRunner runner = new DescriptorRunner(filename, context)) {
      runner.run();
      BeneratorUtil.logConfig("Max. committed heap size: " + new KiloFormatter(1024).format(memProfiler.getMaxCommittedHeapSize()) + "B");
    }
    DBUtil.assertAllDbResourcesClosed(false);
  }


  // helper methods --------------------------------------------------------------------------------------------------

  static BeneratorConfig parseCommandLine(String... args) {
    BeneratorConfig result = new BeneratorConfig();
    CommandLineParser p = createCommandLineParser();
    return p.parse(result, args);
  }

  protected static CommandLineParser createCommandLineParser() {
    CommandLineParser p = new CommandLineParser();
    p.addOption("list", "--list", null);
    p.addFlag("clearCaches", "--clearCaches", null);
    p.addOption("mode", "--mode", "-m");
    p.addFlag("exception", "--exception", null);
    p.addArgument("file", false);
    return p;
  }

  private static BeneratorResult handleException(Throwable e, BeneratorConfig config) {
    logger.error("Error in Benerator execution", e);
    // print exception stack trace if this is configured
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(buffer);
    if (config != null && config.isException()) {
      e.printStackTrace(out);
    }
    // process exception
    int exitCode;
    if (ExceptionUtil.containsException(OutOfMemoryError.class, e) && Profiling.isEnabled()) {
      exitCode = ExitCodes.MISCELLANEOUS_ERROR;
      ApplicationException appEx = BeneratorExceptionFactory.getInstance().outOfMemory(e);
      out.println(appEx);
    } else if (e instanceof ApplicationException) {
      ApplicationException appEx = (ApplicationException) e;
      out.println(appEx);
      exitCode = appEx.getExitCode();
    } else {
      out.println("Error: " + e.getMessage());
      exitCode = ExitCodes.MISCELLANEOUS_ERROR;
    }
    try {
      return new BeneratorResult(exitCode, buffer.toString(StandardCharsets.UTF_8.name()));
    } catch (UnsupportedEncodingException ex) {
      throw BeneratorExceptionFactory.getInstance().programmerConfig("Error reading ByteArrayOutputStream", e);
    }
  }

}

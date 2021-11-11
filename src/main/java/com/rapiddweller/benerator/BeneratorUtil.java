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

package com.rapiddweller.benerator;

import com.rapiddweller.benerator.environment.Environment;
import com.rapiddweller.benerator.environment.EnvironmentUtil;
import com.rapiddweller.benerator.environment.SystemRef;
import com.rapiddweller.benerator.main.Benerator;
import com.rapiddweller.common.ConfigUtil;
import com.rapiddweller.common.FileUtil;
import com.rapiddweller.common.LogCategoriesConstants;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.common.SystemInfo;
import com.rapiddweller.common.VMInfo;
import com.rapiddweller.common.ui.InfoPrinter;
import com.rapiddweller.common.version.VersionInfo;
import com.rapiddweller.common.version.VersionNumber;
import com.rapiddweller.profile.Profiling;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.impl.StaticLoggerBinder;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.Collection;

/**
 * Provides general utility methods for Benerator.<br/><br/>
 * Created: 01.02.2013 16:20:10
 * @author Volker Bergmann
 * @since 0.8.0
 */
public class BeneratorUtil {

  private static final Logger CONFIG_LOGGER = LoggerFactory.getLogger(LogCategoriesConstants.CONFIG);

  public static final String EE_BENERATOR = "com.rapiddweller.benerator_ee.main.EEBenerator";
  public static final String EE_BENERATOR_FACTORY = "com.rapiddweller.benerator_ee.EEBeneratorFactory";

  private BeneratorUtil() {
    // private constructor to prevent instantiation
  }

  public static boolean isEEAvailable() {
    try {
      Class.forName(EE_BENERATOR);
      return true;
    } catch (ClassNotFoundException e) {
      return false;
    }
  }

  public static boolean isDescriptorFilePath(String filePath) {
    if (StringUtil.isEmpty(filePath)) {
      return false;
    }
    String lcName = filePath.toLowerCase();
    return ("benerator.xml".equals(filePath) || lcName.replace(File.separatorChar, '/').endsWith("/benerator.xml") || lcName.endsWith(".ben.xml"));
  }

  public static void checkSystem(InfoPrinter printer) {
    // print general Benerator version and system information
    printVersionInfo(true, printer);
    // Check logging setup
    checkSlf4jSetup();
    checkLog4j2Setup();
    // Print heap info
    printer.printLines("Configured heap size limit: " + Runtime.getRuntime().maxMemory() / 1024 / 1024 + " MB");
    // Check script engine
    try {
      Class.forName("javax.script.ScriptEngine");
    } catch (ClassNotFoundException e) {
      CONFIG_LOGGER.error("You need to run benerator with Java 6 or greater!");
      if (SystemInfo.isMacOsx()) {
        CONFIG_LOGGER.error("Please check the manual for Java setup on Mac OS X.");
      }
      System.exit(BeneratorConstants.EXIT_CODE_ERROR);
    }
    // Check Java version
    VersionNumber javaVersion = VersionNumber.valueOf(VMInfo.getJavaVersion());
    if (javaVersion.compareTo(VersionNumber.valueOf("1.6")) < 0) {
      CONFIG_LOGGER.warn("benerator is written for and tested under Java 6 - " +
          "you managed to set up JSR 223, but may face other problems.");
    }
    // Check profiling setting
    if (Profiling.isEnabled()) {
      CONFIG_LOGGER.warn("Profiling is active. This may lead to memory issues");
    }
  }

  private static void checkSlf4jSetup() {
    // first check slf4j setup
    StaticLoggerBinder binder = StaticLoggerBinder.getSingleton();
    if ("org.apache.logging.slf4j.Log4jLoggerFactory".equals(binder.getLoggerFactoryClassStr())) {
      logConfig("Slf4j is configured to use Log4j");
    } else {
      System.err.println("The classpath is not configured properly for Slf4j. See http://www.slf4j.org/codes.html");
    }
  }

  private static void checkLog4j2Setup() {
    LoggerContext context = (LoggerContext) LogManager.getContext();
    if (context != null) {
      Configuration configuration = context.getConfiguration();
      if (configuration != null) {
        ConfigurationSource configSource = configuration.getConfigurationSource();
        if (configSource != null) {
          String configSourceLocation = configSource.getLocation();
          if (configSourceLocation != null && configSourceLocation.endsWith("log4j2.xml")) {
            logConfig("Log4j is configured properly");
            //logConfig("Log4j is configured to use " + configSourceLocation)
            return;
          }
        }
      }
      System.err.println("Log4j2 is not configured properly, " +
          "probably because no log4j2.xml file has been found or it is invalid.");
    }
  }

  public static void printVersionInfo(boolean withMode, InfoPrinter printer) {
    VersionInfo version = VersionInfo.getInfo("benerator");
    String edition = BeneratorFactory.getInstance().getEdition();
    printer.printLines(
        "Benerator " + edition + " " + version.getVersion() + " build " + version.getBuildNumber()
    );
    if (withMode) {
      printer.printLines(
          "Mode:          " + Benerator.getMode().getCode()
      );
    }
    printer.printLines(
        "Java version:  " + VMInfo.getJavaVersion(),
        "JVM product:   " + getJVMInfo(),
        "System:        " + getOsInfo(),
        "CPU & RAM:     " + getCpuAndMemInfo()
    );
  }

  public static String getJVMInfo() {
    return VMInfo.getJavaVmName() + " " + VMInfo.getJavaVmVersion() + " (" + VMInfo.getJavaVmVendor() + ")";
  }

  public static void logConfig(String config) {
    CONFIG_LOGGER.info(config);
  }

  public static String getCpuAndMemInfo() {
    int availableProcessors = Runtime.getRuntime().availableProcessors();
    String result = availableProcessors + " cores";
    long memGb = getMemGB();
    if (memGb > 0) {
      result += " and " + memGb + " GB RAM";
    }
    long maxMemMB = Runtime.getRuntime().maxMemory() / 1024 / 1024;
    if (maxMemMB >= 1024)
      result += ", max " + (maxMemMB / 1024) + " GB of RAM for this process";
    else
      result += ", max " + maxMemMB + " MB of RAM for this process";
    return result;
  }

  public static int getMemGB() {
    try {
      MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
      ObjectName osName = new ObjectName("java.lang", "type", "OperatingSystem");
      return ((int) ((Long) mBeanServer.getAttribute(osName, "TotalPhysicalMemorySize") / 1024 / 1024 / 1024));
    } catch (Exception e) {
      e.printStackTrace();
      return -1;
    }
  }

  public static String getOsInfo() {
    return SystemInfo.getOsName() + " " + SystemInfo.getOsVersion() + " " + SystemInfo.getOsArchitecture();
  }

  public static void printEnvironments(InfoPrinter printer) {
    Collection<Environment> environments = EnvironmentUtil.findEnvironments().values();
    printer.printLines("Environments:");
    for (Environment environment : environments) {
      printer.printLines("- " + environment.getName());
      for (SystemRef system : environment.getSystems()) {
        printer.printLines("  # " + system.getName() + ": " + system.getType());
      }
    }
  }

  public static void printEnvDbs(InfoPrinter printer) {
    printEnvSystems("Databases", "db", printer);
  }

  public static void printEnvKafkas(InfoPrinter printer) {
    printEnvSystems("Kafkas", "kafka", printer);
  }

  private static void printEnvSystems(String label, String type, InfoPrinter printer) {
    SystemRef[] systems = EnvironmentUtil.findSystems(type);
    printer.printLines(label + ":");
    for (SystemRef system : systems) {
      printer.printLines("- " + system.getEnvironment().getName() + "#" + system.getName());
    }
  }

  public static void clearCaches() {
    CONFIG_LOGGER.info("Deleting caches");
    FileUtil.deleteDirectory(ConfigUtil.commonCacheFolder());
  }

}

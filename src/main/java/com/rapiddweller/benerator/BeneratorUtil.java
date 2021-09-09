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

package com.rapiddweller.benerator;

import com.rapiddweller.benerator.main.Benerator;
import com.rapiddweller.common.LogCategoriesConstants;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.common.SystemInfo;
import com.rapiddweller.common.VMInfo;
import com.rapiddweller.common.ui.InfoPrinter;
import com.rapiddweller.common.version.VersionInfo;
import com.rapiddweller.common.version.VersionNumber;
import com.rapiddweller.profile.Profiling;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.File;

/**
 * Provides general utility methods for Benerator.<br/><br/>
 * Created: 01.02.2013 16:20:10
 *
 * @author Volker Bergmann
 * @since 0.8.0
 */
public class BeneratorUtil {

  private static final Logger CONFIG_LOGGER = LoggerFactory.getLogger(LogCategoriesConstants.CONFIG);

  /**
   * Is descriptor file path boolean.
   *
   * @param filePath the file path
   * @return the boolean
   */
  public static boolean isDescriptorFilePath(String filePath) {
    if (StringUtil.isEmpty(filePath)) {
      return false;
    }
    String lcName = filePath.toLowerCase();
    return ("benerator.xml".equals(filePath) || lcName.replace(File.separatorChar, '/').endsWith("/benerator.xml") || lcName.endsWith(".ben.xml"));
  }

  /**
   * Check system.
   *
   * @param printer the printer
   */
  public static void checkSystem(InfoPrinter printer) {
    printVersionInfo(printer);
    printer.printLines("Configured heap size limit: " + Runtime.getRuntime().maxMemory() / 1024 / 1024 + " MB");
    try {
      Class.forName("javax.script.ScriptEngine");
    } catch (ClassNotFoundException e) {
      CONFIG_LOGGER.error("You need to run benerator with Java 6 or greater!");
      if (SystemInfo.isMacOsx()) {
        CONFIG_LOGGER.error("Please check the manual for Java setup on Mac OS X.");
      }
      System.exit(BeneratorConstants.EXIT_CODE_ERROR);
    }
    VersionNumber javaVersion = VersionNumber.valueOf(VMInfo.getJavaVersion());
    if (javaVersion.compareTo(VersionNumber.valueOf("1.6")) < 0) {
      CONFIG_LOGGER.warn("benerator is written for and tested under Java 6 - " +
          "you managed to set up JSR 223, but may face other problems.");
    }
    if (Profiling.isEnabled()) {
      CONFIG_LOGGER.warn("Profiling is active. This may lead to memory issues");
    }
  }

  /**
   * Print version info.
   *
   * @param printer the printer
   */
  public static void printVersionInfo(InfoPrinter printer) {
    VersionInfo version = Benerator.getVersion();
    printer.printLines(
        "Benerator " + version.getVersion() + " build " + version.getBuildNumber(),
        "Java version " + VMInfo.getJavaVersion(),
        "JVM " + VMInfo.getJavaVmName() + " " + VMInfo.getJavaVmVersion() + " (" + VMInfo.getJavaVmVendor() + ")",
        "OS " + SystemInfo.getOsName() + " " + SystemInfo.getOsVersion() + " (" + SystemInfo.getOsArchitecture() + ")"
    );
  }

  /**
   * Log config.
   *
   * @param config the config
   */
  public static void logConfig(String config) {
    CONFIG_LOGGER.info(config);
  }

}

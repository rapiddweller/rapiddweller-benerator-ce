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

package com.rapiddweller.benerator.main;

import com.rapiddweller.benerator.BeneratorConstants;
import com.rapiddweller.benerator.BeneratorError;
import com.rapiddweller.benerator.BeneratorFactory;
import com.rapiddweller.benerator.BeneratorUtil;
import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.BeneratorMonitor;
import com.rapiddweller.benerator.engine.DescriptorRunner;
import com.rapiddweller.common.ArrayUtil;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.common.LogCategories;
import com.rapiddweller.common.log.LoggingInfoPrinter;
import com.rapiddweller.common.ui.ConsoleInfoPrinter;
import com.rapiddweller.common.ui.InfoPrinter;
import com.rapiddweller.common.version.VersionInfo;
import com.rapiddweller.contiperf.sensor.MemorySensor;
import com.rapiddweller.format.text.KiloFormatter;
import com.rapiddweller.jdbacl.DBUtil;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import java.io.IOException;

/**
 * Parses and executes a benerator setup file.<br/>
 * <br/>
 * Created: 14.08.2007 19:14:28
 * @author Volker Bergmann
 */
public class Benerator {
	
	private static final Logger LOGGER = LogManager.getLogger(Benerator.class);

	// methods ---------------------------------------------------------------------------------------------------------

	public static void main(String[] args) throws IOException {
		VersionInfo.getInfo("benerator").verifyDependencies();
		if (ArrayUtil.contains("--version", args) || ArrayUtil.contains("-v", args))
			printVersionInfoAndExit();
		else
			runFromCommandLine(args);
	}

	private static void runFromCommandLine(String[] args) throws IOException {
		try {
			InfoPrinter printer = new LoggingInfoPrinter(LogCategories.CONFIG);
			String filename = (args.length > 0 ? args[0] : "benerator.xml");
			runFile(filename, printer);
	    	DBUtil.assertAllDbResourcesClosed(false);
		} catch (BeneratorError e) {
			LOGGER.error(e.getMessage(), e);
			System.exit(e.getCode());
		}
	}

	public static void runFile(String filename, InfoPrinter printer) throws IOException {
		BeneratorMonitor.INSTANCE.reset();
		MemorySensor memProfiler = MemorySensor.getInstance();
		memProfiler.reset();
		if (printer != null) {
			printer.printLines("Running file " + filename);
			BeneratorUtil.checkSystem(printer);
		}
		BeneratorContext context = BeneratorFactory.getInstance().createContext(IOUtil.getParentUri(filename));
		DescriptorRunner runner = new DescriptorRunner(filename, context);
		try {
			runner.run();
		} finally {
			IOUtil.close(runner);
		}
		BeneratorUtil.logConfig("Max. committed heap size: " + new KiloFormatter(1024).format(memProfiler.getMaxCommittedHeapSize()) + "B");
	}

	private static void printVersionInfoAndExit() {
		InfoPrinter console = new ConsoleInfoPrinter();
		BeneratorUtil.printVersionInfo(console);
		System.exit(BeneratorConstants.EXIT_CODE_NORMAL);
	}

}

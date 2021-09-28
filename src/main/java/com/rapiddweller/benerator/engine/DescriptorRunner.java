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

package com.rapiddweller.benerator.engine;

import com.rapiddweller.benerator.BeneratorFactory;
import com.rapiddweller.benerator.consumer.FileExporter;
import com.rapiddweller.benerator.engine.parser.xml.BeneratorParseContext;
import com.rapiddweller.common.ExceptionUtil;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.common.RoundedNumberFormat;
import com.rapiddweller.common.converter.ConverterManager;
import com.rapiddweller.common.time.ElapsedTimeFormatter;
import com.rapiddweller.common.xml.XMLUtil;
import com.rapiddweller.profile.Profiler;
import com.rapiddweller.profile.Profiling;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses and executes a benerator descriptor file.<br/><br/>
 * Created at 26.02.2009 15:51:59
 * @author Volker Bergmann
 * @since 0.5.8
 */
public class DescriptorRunner implements ResourceManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(DescriptorRunner.class);

  // attributes ------------------------------------------------------------------------------------------------------

  private final String uri;

  private final BeneratorContext context;

  final BeneratorFactory factory;
  private List<String> generatedFiles;

  private final ResourceManagerSupport resourceManager = new ResourceManagerSupport();
  long startTime = 0;


  // constructor -----------------------------------------------------------------------------------------------------

  public DescriptorRunner(String uri, BeneratorContext context) {
    this.uri = uri;
    this.context = context;
    this.factory = BeneratorFactory.getInstance();
    this.generatedFiles = new ArrayList<>();
    ConverterManager.getInstance().setContext(context);
  }

  public static void resetMonitor() {
    BeneratorMonitor.INSTANCE.reset();
  }

  // interface -------------------------------------------------------------------------------------------------------

  public BeneratorContext getContext() {
    return context;
  }

  public void run() throws IOException {
    Runtime runtime = Runtime.getRuntime();
    BeneratorShutdownHook hook = new BeneratorShutdownHook(this);
    runtime.addShutdownHook(hook);
    try {
      runWithoutShutdownHook();
    } finally {
      runtime.removeShutdownHook(hook);
    }
  }

  public void runWithoutShutdownHook() throws IOException {
    execute(parseDescriptorFile());
  }

  public BeneratorRootStatement parseDescriptorFile() throws IOException {
    Document document = XMLUtil.parse(uri);
    Element root = document.getDocumentElement();
    BeneratorParseContext parsingContext = factory.createParseContext(resourceManager);
    BeneratorRootStatement statement = (BeneratorRootStatement) parsingContext.parseElement(root, null);
    // prepare system
    generatedFiles = new ArrayList<>();
    context.setContextUri(IOUtil.getParentUri(uri));
    return statement;
  }

  public void execute(BeneratorRootStatement rootStatement) {
    try {
      startTime = System.currentTimeMillis();
      // run AST
      rootStatement.execute(context);
      // calculate and print statistics
      long elapsedTime = java.lang.System.currentTimeMillis() - startTime;
      printStats(elapsedTime);
      if (Profiling.isEnabled()) {
        Profiler.defaultInstance().printSummary();
      }
      List<String> generations = getGeneratedFiles();
      if (!generations.isEmpty()) {
        LOGGER.info("Generated file(s): {}", generations);
      }
    } catch (Throwable t) {
      if (ExceptionUtil.containsException(OutOfMemoryError.class, t) && Profiling.isEnabled()) {
        LOGGER.error("OutOfMemoryError! This probably happened because you activated profiling", t);
      } else {
        LOGGER.error("Error in Benerator execution", t);
      }
    } finally {
      context.close();
    }
  }

  public List<String> getGeneratedFiles() {
    return generatedFiles;
  }


  // ResourceManager interface implementation ------------------------------------------------------------------------

  @Override
  public boolean addResource(Closeable resource) {
    if (!resourceManager.addResource(resource)) {
      return false;
    } else if (resource instanceof FileExporter) {
      generatedFiles.add(((FileExporter) resource).getUri());
    }
    return true;
  }

  @Override
  public void close() {
    resourceManager.close();
  }


  // private helpers -------------------------------------------------------------------------------------------------

  private static void printStats(long elapsedTime) {
    String message = "Created a total of " + BeneratorMonitor.INSTANCE.getTotalGenerationCount() + " entities";
    if (elapsedTime != 0) {
      long throughput = BeneratorMonitor.INSTANCE.getTotalGenerationCount() * 3600000L / elapsedTime;
      message += " in " + ElapsedTimeFormatter.format(elapsedTime) + " (~" + RoundedNumberFormat.format(throughput, 0) + " p.h.)";
    }
    LOGGER.info(message);
  }


  // java.lang.Object overrides --------------------------------------------------------------------------------------

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }

}

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

import com.rapiddweller.benerator.template.TemplateInputReader;
import com.rapiddweller.common.BeanUtil;
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.common.Context;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.common.context.DefaultContext;
import com.rapiddweller.common.ui.ConsoleInfoPrinter;
import com.rapiddweller.format.script.Script;
import com.rapiddweller.format.script.freemarker.FreeMarkerScriptFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Anonymizes XML files homogeneously based on generator and XPath definitions in an Excel sheet.<br/><br/>
 * Created: 27.02.2014 10:04:58
 *
 * @author Volker Bergmann
 * @since 0.9.0
 */
public class TemplateRunner {

  /**
   * The entry point of application.
   *
   * @param args the input arguments
   * @throws Exception the exception
   */
  public static void main(String[] args) throws Exception {
    // extract possible VM params from argument list
    List<String> params = processCmdLineArgs(args);


    // check preconditions
    if (params.size() < 3 || params.size() > 4) {
      printHelpAndExit();
    }

    // parse parameters
    String configFile = params.get(0);
    String configParserClass = params.get(1);
    String templateFile = params.get(2);
    String generatedFile = (params.size() == 4 ? params.get(3) : "benerator.xml");

    // generate descriptor file
    TemplateInputReader reader = (TemplateInputReader) BeanUtil.newInstance(configParserClass);
    Context context = new DefaultContext();
    reader.parse(configFile, context);
    createDescriptor(context, templateFile, generatedFile);

    // run descriptor file
    runDescriptor(generatedFile);
  }

  private static List<String> processCmdLineArgs(String[] args) {
    List<String> params = new ArrayList<>();
    for (String arg : args) {
      if (arg.startsWith("-D")) {
        String[] tokens = StringUtil.splitOnFirstSeparator(arg.substring(2), '=');
        if (tokens.length == 2) {
          System.setProperty(tokens[0], tokens[1]);
        }
      } else {
        params.add(arg);
      }
    }
    return params;
  }

  private static void printHelpAndExit() {
    ConsoleInfoPrinter.printHelp(
        "The class " + TemplateRunner.class.getName(),
        "creates and runs Benerator descriptor files from custom templates. It has the following parameters:",
        "<config_file> <config_parser_class> <template_file> [<generated_file>]",
        "	config_file:         Path of an individual data file to provide configuration",
        "	config_parser_class: Fully qualified name of a Java class which is able to parse the config_file",
        "	template file:       File path of a FreeMarker template to generate a Benerator descriptor file",
        "	generated_file:      File path of the generated Benerator descriptor file"
    );
    System.exit(-1);
  }

  private static void createDescriptor(Context context, String templateUri, String targetUri) {
    try {
      Script script = new FreeMarkerScriptFactory().readFile(templateUri);
      Writer out = new FileWriter(targetUri);
      script.execute(context, out);
    } catch (Exception e) {
      throw new ConfigurationError("Error generating descriptor file " + targetUri, e);
    }
  }

  private static void runDescriptor(String descriptorUri) throws IOException {
    Benerator.main(new String[] {descriptorUri});
  }

}

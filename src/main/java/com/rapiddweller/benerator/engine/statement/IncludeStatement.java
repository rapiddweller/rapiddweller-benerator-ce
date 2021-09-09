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

import com.rapiddweller.benerator.BeneratorUtil;
import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.DescriptorRunner;
import com.rapiddweller.benerator.engine.Statement;
import com.rapiddweller.benerator.parser.DefaultEntryConverter;
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.format.script.ScriptConverterForStrings;
import com.rapiddweller.platform.xml.XMLSchemaDescriptorProvider;
import com.rapiddweller.script.Expression;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.IOException;

/**
 * Executes an &lt;include/&gt; from an XML descriptor file.<br/>
 * <br/>
 * Created at 23.07.2009 07:18:54
 *
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class IncludeStatement implements Statement {

  private static final Logger logger = LoggerFactory.getLogger(IncludeStatement.class);

  private Expression<String> uriEx;

  /**
   * Instantiates a new Include statement.
   *
   * @param uri the uri
   */
  public IncludeStatement(Expression<String> uri) {
    this.uriEx = uri;
  }

  /**
   * Gets uri.
   *
   * @return the uri
   */
  public Expression<String> getUri() {
    return uriEx;
  }

  /**
   * Sets uri.
   *
   * @param uri the uri
   */
  public void setUri(Expression<String> uri) {
    this.uriEx = uri;
  }

  @Override
  public boolean execute(BeneratorContext context) {
    String uri = context.resolveRelativeUri(uriEx.evaluate(context));
    String lcUri = uri.toLowerCase();
    try {
      if (lcUri.endsWith(".properties")) {
        includeProperties(uri, context);
      } else if (BeneratorUtil.isDescriptorFilePath(uri)) {
        includeDescriptor(uri, context);
      } else if (lcUri.endsWith(".xsd")) {
        includeXmlSchema(uri, context);
      } else {
        throw new ConfigurationError("Not a supported import file type: " + uri);
      }
      return true;
    } catch (IOException e) {
      throw new ConfigurationError("Error processing " + uri, e);
    }
  }

  /**
   * Include properties.
   *
   * @param uri     the uri
   * @param context the context
   * @throws IOException the io exception
   */
  public static void includeProperties(String uri, BeneratorContext context) throws IOException {
    logger.debug("Including properties file: " + uri);
    ScriptConverterForStrings preprocessor = new ScriptConverterForStrings(context);
    DefaultEntryConverter converter = new DefaultEntryConverter(preprocessor, context, true);
    IOUtil.readProperties(uri, converter);
  }

  /**
   * Include xml schema.
   *
   * @param uri     the uri
   * @param context the context
   */
  public static void includeXmlSchema(String uri, BeneratorContext context) {
    logger.debug("Including XML Schema: " + uri);
    new XMLSchemaDescriptorProvider(uri, context).close();
  }

  private static void includeDescriptor(String uri, BeneratorContext context) throws IOException {
    logger.debug("Including Benerator descriptor file: " + uri);
    DescriptorRunner runner = new DescriptorRunner(uri, context);
    runner.runWithoutShutdownHook();
    runner.close();
  }

}

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

package com.rapiddweller.platform.csv;

import com.rapiddweller.benerator.consumer.TextFileExporter;
import com.rapiddweller.benerator.engine.DefaultBeneratorContext;
import com.rapiddweller.common.ArrayFormat;
import com.rapiddweller.common.ArrayUtil;
import com.rapiddweller.common.BeanUtil;
import com.rapiddweller.common.CollectionUtil;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.ComponentDescriptor;
import com.rapiddweller.model.data.Entity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.List;

/**
 * Exports Entities to a CSV file.
 * The default line separator is CR LF according to RFC 4180.
 * It can be set explicitly by <code>setLineSeparator()</code>.<br/>
 * <br/>
 * Created: 21.08.2007 21:16:59
 *
 * @author Volker Bergmann
 */
public class CSVEntityExporter extends TextFileExporter {

  private static final Logger logger =
      LogManager.getLogger(CSVEntityExporter.class);

  // defaults --------------------------------------------------------------------------------------------------------

  private static final String DEFAULT_LINE_SEPARATOR = "\r\n";
  // as defined by RFC 4180
  private static final String DEFAULT_URI = "export.csv";

  // configuration attributes ----------------------------------------------------------------------------------------

  private String[] columns;
  private boolean headless;
  private boolean endWithNewLine;
  private char separator;
  private boolean quoteEmpty;

  // state attributes ------------------------------------------------------------------------------------------------

  private boolean lfRequired;

  // constructors ----------------------------------------------------------------------------------------------------

  /**
   * Instantiates a new Csv entity exporter.
   */
  public CSVEntityExporter() {
    this(DEFAULT_URI);
  }

  /**
   * Instantiates a new Csv entity exporter.
   *
   * @param uri the uri
   */
  public CSVEntityExporter(String uri) {
    this(uri, (String) null);
  }

  /**
   * Instantiates a new Csv entity exporter.
   *
   * @param uri         the uri
   * @param columnsSpec the columns spec
   */
  public CSVEntityExporter(String uri, String columnsSpec) {
    this(uri, columnsSpec,
        DefaultBeneratorContext.getDefaultCellSeparator(), null,
        DEFAULT_LINE_SEPARATOR);
  }

  /**
   * Instantiates a new Csv entity exporter.
   *
   * @param uri           the uri
   * @param columnsSpec   the columns spec
   * @param separator     the separator
   * @param encoding      the encoding
   * @param lineSeparator the line separator
   */
  public CSVEntityExporter(String uri, String columnsSpec, char separator,
                           String encoding, String lineSeparator) {
    super(uri, encoding, lineSeparator);
    if (columnsSpec != null) {
      setColumns(ArrayFormat.parse(columnsSpec, ",", String.class));
    }
    this.separator = separator;
    this.quoteEmpty = true;
  }

  /**
   * Instantiates a new Csv entity exporter.
   *
   * @param descriptor the descriptor
   */
  public CSVEntityExporter(ComplexTypeDescriptor descriptor) {
    this(descriptor.getName() + ".csv", descriptor);
  }

  /**
   * Instantiates a new Csv entity exporter.
   *
   * @param uri        the uri
   * @param descriptor the descriptor
   */
  public CSVEntityExporter(String uri, ComplexTypeDescriptor descriptor) {
    this(uri, descriptor, DefaultBeneratorContext.getDefaultCellSeparator(),
        null, DEFAULT_LINE_SEPARATOR);
  }

  /**
   * Instantiates a new Csv entity exporter.
   *
   * @param uri           the uri
   * @param descriptor    the descriptor
   * @param separator     the separator
   * @param encoding      the encoding
   * @param lineSeparator the line separator
   */
  public CSVEntityExporter(String uri, ComplexTypeDescriptor descriptor,
                           char separator, String encoding,
                           String lineSeparator) {
    super(uri, encoding, lineSeparator);
    Collection<ComponentDescriptor> componentDescriptors =
        descriptor.getComponents();
    List<String> componentNames =
        BeanUtil.extractProperties(componentDescriptors, "name");
    this.columns = CollectionUtil.toArray(componentNames, String.class);
    this.endWithNewLine = false;
    this.separator = separator;
  }


  // properties ------------------------------------------------------------------------------------------------------

  /**
   * Sets columns.
   *
   * @param columns the columns
   */
  public void setColumns(String[] columns) {
    if (ArrayUtil.isEmpty(columns)) {
      this.columns = null;
    } else {
      this.columns = columns.clone();
      StringUtil.trimAll(this.columns);
    }
  }

  /**
   * Sets separator.
   *
   * @param separator the separator
   */
  public void setSeparator(char separator) {
    this.separator = separator;
  }

  /**
   * Is headless boolean.
   *
   * @return the boolean
   */
  public boolean isHeadless() {
    return headless;
  }

  /**
   * Sets headless.
   *
   * @param headless the headless
   */
  public void setHeadless(boolean headless) {
    this.headless = headless;
  }

  /**
   * Is end with new line boolean.
   *
   * @return the boolean
   */
  public boolean isEndWithNewLine() {
    return endWithNewLine;
  }

  /**
   * Sets end with new line.
   *
   * @param endWithNewLine the end with new line
   */
  public void setEndWithNewLine(boolean endWithNewLine) {
    this.endWithNewLine = endWithNewLine;
  }

  /**
   * Is quote empty boolean.
   *
   * @return the boolean
   */
  public boolean isQuoteEmpty() {
    return quoteEmpty;
  }

  /**
   * Sets quote empty.
   *
   * @param quoteEmpty the quote empty
   */
  public void setQuoteEmpty(boolean quoteEmpty) {
    this.quoteEmpty = quoteEmpty;
  }

  // Callback methods for parent class functionality -----------------------------------------------------------------

  @Override
  protected void startConsumingImpl(Object object) {
    logger.debug("exporting {}", object);
    if (!(object instanceof Entity)) {
      throw new IllegalArgumentException("Expecting entity");
    }
    Entity entity = (Entity) object;
    if (lfRequired) {
      println();
    } else {
      lfRequired = true;
    }
    for (int i = 0; i < columns.length; i++) {
      if (i > 0) {
        printer.print(separator);
      }
      Object value = entity.getComponent(columns[i]);
      String out;
      if (value == null) {
        out = getNullString();
      } else {
        out = plainConverter.convert(value);
        if (out.length() == 0 && quoteEmpty) {
          out = "\"\"";
        } else if (out.indexOf(separator) >= 0) {
          out = '"' + out + '"';
        }
      }
      printer.print(out);
    }
  }

  @Override
  protected void postInitPrinter(Object object) {
    Entity entity = (Entity) object;
    // determine columns from entity, if they have not been predefined
    if (columns == null && entity != null) {
      columns = CollectionUtil.toArray(entity.getComponents().keySet());
    }
    printHeaderRow();
  }

  @Override
  protected void preClosePrinter() {
    if (endWithNewLine) {
      println();
    }
  }


  // private helpers -------------------------------------------------------------------------------------------------

  private void printHeaderRow() {
    if (!wasAppended && !headless && columns != null) {
      if (wasAppended && !endWithNewLine) {
        println();
      }
      for (int i = 0; i < columns.length; i++) {
        if (i > 0) {
          printer.print(separator);
        }
        printer.print(columns[i]);
      }
      lfRequired = true;
    } else {
      lfRequired = (wasAppended && !endWithNewLine);
    }
  }


}

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

package com.rapiddweller.platform.fixedwidth;

import com.rapiddweller.benerator.consumer.TextFileExporter;
import com.rapiddweller.common.ArrayFormat;
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.common.collection.OrderedNameMap;
import com.rapiddweller.model.data.Entity;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.Locale;
import java.util.Map;

/**
 * Exports Entities to fixed-width files.<br/><br/>
 * Created: 26.08.2007 06:17:41
 * @author Volker Bergmann
 */
public class FixedWidthEntityExporter extends TextFileExporter {

  private static final Logger logger = LoggerFactory.getLogger(FixedWidthEntityExporter.class);

  private final Map<String, String> formats;
  private Map<String, FWRecordFormatter> formatters;

  private Locale locale;

  public FixedWidthEntityExporter() {
    this("export.fcw", null);
  }

  public FixedWidthEntityExporter(String uri, String columnFormatList) {
    this(uri, null, columnFormatList);
  }

  public FixedWidthEntityExporter(String uri, String encoding, String columnFormatList) {
    super(uri, encoding, null);
    this.uri = uri;
    this.formats = OrderedNameMap.createCaseInsensitiveMap();
    this.formatters = null;
    this.locale = Locale.US;
    setColumns(columnFormatList);
    setDecimalPattern("0.##");
  }

  // properties ------------------------------------------------------------------------------------------------------

  public void setLocale(Locale locale) {
    this.locale = locale;
  }

  public void setColumns(String columnFormatList) {
    if (columnFormatList != null) {
      this.formats.put("*", columnFormatList);
    } else {
      this.formats.clear();
    }
  }

  public Map<String, String> getFormats() {
    return formats;
  }

  // Consumer interface ----------------------------------------------------------------------------------------------

  @Override
  public void flush() {
    if (printer != null) {
      printer.flush();
    }
  }

  @Override
  public void close() {
    IOUtil.close(printer);
  }

  // Callback methods for TextFileExporter ---------------------------------------------------------------------------

  @Override
  protected void postInitPrinter(Object object) {
    if (this.formats.isEmpty()) {
      throw new ConfigurationError("No format(s) set on " + getClass().getName());
    }
  }

  @Override
  protected void startConsumingImpl(Object object) {
    logger.debug("exporting {}", object);
    if (!(object instanceof Entity)) {
      throw new IllegalArgumentException("Expected Entity");
    }
    Entity entity = (Entity) object;
    getFormatter(entity.type()).format(entity, printer);
    printer.print(lineSeparator);
  }

  // private helpers -------------------------------------------------------------------------------------------------

  private FWRecordFormatter getFormatter(String type) {
    if (this.formatters == null) {
      initFormatters();
    }
    FWRecordFormatter formatter = formatters.get(type);
    if (formatter == null) {
      formatter = formatters.get("*");
    }
    if (formatter == null) {
      throw new ConfigurationError("No format defined for type " + type);
    }
    return formatter;
  }

  private void initFormatters() {
    this.formatters = OrderedNameMap.createCaseInsensitiveMap();
    for (Map.Entry<String, String> entry : this.formats.entrySet()) {
      this.formatters.put(entry.getKey(), new FWRecordFormatter(entry.getValue(), getNullString(), locale));
    }
  }

  // java.lang.Object overrides --------------------------------------------------------------------------------------

  @Override
  public String toString() {
    return getClass().getSimpleName() + '[' + ArrayFormat.format() + ']';
  }

}

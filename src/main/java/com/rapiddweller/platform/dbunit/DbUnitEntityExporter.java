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

package com.rapiddweller.platform.dbunit;

import com.rapiddweller.benerator.consumer.AbstractConsumer;
import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.common.Encodings;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.common.SystemInfo;
import com.rapiddweller.common.converter.ToStringConverter;
import com.rapiddweller.model.data.Entity;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * Exports entities in DbUnit XML file format.<br/><br/>
 * @author Volker Bergmann
 * @since 0.3.04
 */
public class DbUnitEntityExporter extends AbstractConsumer {

  // attributes ------------------------------------------------------------------------------------------------------

  private static final Logger logger = LoggerFactory.getLogger(DbUnitEntityExporter.class);

  private static final String DEFAULT_FILE_ENCODING = Encodings.UTF_8;
  private static final String DEFAULT_URI = "data.dbunit.xml";

  private static final String DATE_PATTERN = "yyyy-MM-dd";
  private static final String TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss.SSSSSS";

  private final ToStringConverter toStringConverter;

  private String uri;
  private String encoding;

  private State state;
  private OutputStream out;
  private TransformerHandler handler;


  // constructors ----------------------------------------------------------------------------------------------------

  public DbUnitEntityExporter() {
    this(DEFAULT_URI);
  }

  public DbUnitEntityExporter(String uri) {
    this(uri, DEFAULT_FILE_ENCODING);
  }

  public DbUnitEntityExporter(String uri, String encoding) {
    setUri(uri);
    setEncoding(encoding);
    this.toStringConverter = new ToStringConverter(null, DATE_PATTERN, TIMESTAMP_PATTERN);
    this.state = State.CREATED;
  }

  // properties ------------------------------------------------------------------------------------------------------

  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public String getEncoding() {
    return encoding;
  }

  public void setEncoding(String encoding) {
    this.encoding = (encoding != null ? encoding : SystemInfo.getFileEncoding());
    if (this.encoding == null) {
      this.encoding = DEFAULT_FILE_ENCODING;
    }
  }

  // Consumer interface ----------------------------------------------------------------------------------------------

  @Override
  public void startProductConsumption(Object object) {
    if (!(object instanceof Entity)) {
      throw BeneratorExceptionFactory.getInstance().illegalArgument("Expected entity");
    }
    Entity entity = (Entity) object;
    try {
      logger.debug("exporting {}", entity);
      initializeIfCreated();
      AttributesImpl atts = new AttributesImpl();
      for (Map.Entry<String, Object> entry : entity.getComponents().entrySet()) {
        Object value = entry.getValue();
        if (value == null) {
          continue;
        }
        String s = toStringConverter.convert(value);
        if (s != null) {
          atts.addAttribute("", "", entry.getKey(), "CDATA", s);
        }
      }
      handler.startElement("", "", entity.type(), atts);
      handler.endElement("", "", entity.type());
    } catch (SAXException e) {
      throw BeneratorExceptionFactory.getInstance().configurationError("Error in processing element: " + entity, e);
    }
  }

  @Override
  public void flush() {
    if (out != null) {
      IOUtil.flush(out);
    }
  }

  @Override
  public void close() {
    initializeIfCreated();
    if (state == State.CLOSED) {
      return;
    }
    if (handler != null) {
      try {
        handler.endElement("", "", "dataset");
        handler.endDocument();
        handler = null;
      } catch (SAXException e) {
        throw BeneratorExceptionFactory.getInstance().configurationError("Error closing XML file.", e);
      } finally {
        IOUtil.close(out);
        out = null;
      }
    }
  }

  // private helpers -------------------------------------------------------------------------------------------------

  private void initializeIfCreated() {
    if (state == State.CREATED) {
      initialize();
    }
  }

  private void initialize() {
    try {
      // create file and write header
      SAXTransformerFactory tf = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
      handler = tf.newTransformerHandler();

      Transformer serializer = handler.getTransformer();
      serializer.setOutputProperty(OutputKeys.ENCODING, encoding);
      serializer.setOutputProperty(OutputKeys.INDENT, "yes");

      out = new FileOutputStream(uri);
      handler.setResult(new StreamResult(out));

      // bug fix: the following extra call to start/endDocument fixes the bug,
      // that in normal invocation no line separator is inserted after the <?xml?> header
      handler.startDocument();
      handler.endDocument();
      // end of bug fix

      handler.startDocument();
      handler.startElement("", "", "dataset", null);

      this.state = State.INITIALIZED;
    } catch (TransformerConfigurationException e) {
      throw BeneratorExceptionFactory.getInstance().configurationError("Error in transformer configuration", e);
    } catch (Exception e) {
      throw BeneratorExceptionFactory.getInstance().configurationError("Error in initializing XML file", e);
    }
  }

  private enum State {
    CREATED, INITIALIZED, CLOSED
  }

  //  java.lang.String overrides --------------------------------------------------------------------------------------

  @Override
  public String toString() {
    return getClass().getSimpleName() + '[' + uri + ", " + encoding + "]";
  }

}

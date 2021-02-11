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

package com.rapiddweller.platform.xml;

import com.rapiddweller.benerator.consumer.AbstractConsumer;
import com.rapiddweller.benerator.consumer.FileExporter;
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.common.SystemInfo;
import com.rapiddweller.common.converter.ToStringConverter;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.Entity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * Writes Entities to an XML file.<br/><br/>
 * Created: 20.02.2008 15:39:23
 *
 * @author Volker Bergmann
 * @since 0.5.0
 */
public class XMLEntityExporter extends AbstractConsumer implements FileExporter {

  private static final Logger LOGGER = LogManager.getLogger(XMLEntityExporter.class);

  // defaults --------------------------------------------------------------------------------------------------------

  private static final String DEFAULT_ENCODING = SystemInfo.getFileEncoding();
  private static final String DEFAULT_URI = "export.xml";

  private static final ToStringConverter converter = new ToStringConverter("", "yyyy-MM-dd", "yyyy-MM-dd'T'hh:mm:ss.SSS");

  // attributes ------------------------------------------------------------------------------------------------------

  private String uri;
  private String encoding;

  private OutputStream out;
  private TransformerHandler handler;

  // constructors ----------------------------------------------------------------------------------------------------

  /**
   * Instantiates a new Xml entity exporter.
   */
  public XMLEntityExporter() {
    this(DEFAULT_URI);
  }

  /**
   * Instantiates a new Xml entity exporter.
   *
   * @param uri the uri
   */
  public XMLEntityExporter(String uri) {
    this(uri, DEFAULT_ENCODING);
  }

  /**
   * Instantiates a new Xml entity exporter.
   *
   * @param uri      the uri
   * @param encoding the encoding
   */
  public XMLEntityExporter(String uri, String encoding) {
    this.uri = uri;
    this.encoding = encoding;
  }

  // properties ------------------------------------------------------------------------------------------------------

  private static boolean hasSimpleType(Object value) {
    return (!value.getClass().isArray() && !(value instanceof Entity));
  }

  @Override
  public String getUri() {
    return uri;
  }

  /**
   * Sets uri.
   *
   * @param uri the uri
   */
  public void setUri(String uri) {
    this.uri = uri;
  }

  // Consumer interface ----------------------------------------------------------------------------------------------

  /**
   * Sets encoding.
   *
   * @param encoding the encoding
   */
  public void setEncoding(String encoding) {
    this.encoding = encoding;
  }

  @Override
  public void startProductConsumption(Object object) {
    LOGGER.debug("startConsuming({})", object);
    if (out == null) {
      initHandler();
    }
    Entity entity = (Entity) object;
    renderElementStart(entity);
  }

  @Override
  public void finishProductConsumption(Object object) {
    LOGGER.debug("finishConsuming({})", object);
    Entity entity = (Entity) object;
    try {
      handler.endElement("", "", entity.type());
    } catch (SAXException e) {
      throw new ConfigurationError("Error in processing element: " + entity, e);
    }
  }

  @Override
  public void flush() {
    IOUtil.flush(out);
  }

  // private helpers -------------------------------------------------------------------------------------------------

  @Override
  public void close() {
    if (out != null) {
      try {
        if (handler != null) {
          handler.endDocument();
          handler = null;
        }
      } catch (SAXException e) {
        throw new ConfigurationError("Error closing XML file.", e);
      } finally {
        IOUtil.close(out);
      }
    }
  }

  private void renderSimpleType(Object value) throws SAXException {
    String s = converter.convert(value);
    char[] cc = StringUtil.getChars(s);
    handler.characters(cc, 0, cc.length);
  }

  private void renderElementStart(Entity entity) {
    try {
      AttributesImpl atts = new AttributesImpl();
      for (Map.Entry<String, Object> entry : entity.getComponents().entrySet()) {
        String key = entry.getKey();
        Object value = entry.getValue();
        if (value == null) {
          continue;
        }
        if (key != null && !ComplexTypeDescriptor.__SIMPLE_CONTENT.equals(key) && hasSimpleType(value)) {
          atts.addAttribute("", "", entry.getKey(), "CDATA", converter.convert(value));
        }
      }
      handler.startElement("", "", entity.type(), atts);
      Object content = entity.getComponent(ComplexTypeDescriptor.__SIMPLE_CONTENT);
      if (content != null) {
        renderSimpleType(content);
      }
    } catch (SAXException e) {
      throw new ConfigurationError("Error in processing element: " + entity, e);
    }
  }

  private void initHandler() {
    LOGGER.debug("Initializing {}", uri);
    // create file
    try {
      // create file and write header
      SAXTransformerFactory tf = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
      handler = tf.newTransformerHandler();

      Transformer transformer = handler.getTransformer();
      transformer.setOutputProperty(OutputKeys.ENCODING, encoding);
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty("{http://xml.apache.org/xslt}" + "indent-amount", "2");

      out = new FileOutputStream(uri);
      handler.setResult(new StreamResult(out));
      handler.startDocument();
    } catch (TransformerConfigurationException e) {
      throw new ConfigurationError(e);
    } catch (SAXException e) {
      throw new ConfigurationError("Error in initializing XML file", e);
    } catch (FileNotFoundException e) {
      throw new RuntimeException("Error writing file " + uri, e);
    }
  }

  // java.lang.Object overrides --------------------------------------------------------------------------------------

  @Override
  public String toString() {
    return getClass().getSimpleName() + '[' + uri + ']';
  }

}

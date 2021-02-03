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

package com.rapiddweller.benerator.file;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.InvalidGeneratorSetupException;
import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.statement.IncludeStatement;
import com.rapiddweller.benerator.factory.MetaGeneratorFactory;
import com.rapiddweller.benerator.primitive.IncrementGenerator;
import com.rapiddweller.benerator.util.UnsafeGenerator;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.benerator.wrapper.WrapperFactory;
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.common.SystemInfo;
import com.rapiddweller.common.converter.MessageConverter;
import com.rapiddweller.common.xml.XMLUtil;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.model.data.TypeDescriptor;
import com.rapiddweller.model.data.Uniqueness;
import com.rapiddweller.platform.xml.XMLEntityExporter;
import com.rapiddweller.platform.xml.XMLSchemaDescriptorProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

/**
 * Generates XML files.<br/>
 * <br/>
 *
 * @author Volker Bergmann
 */
public class XMLFileGenerator extends UnsafeGenerator<File> {

  private final String schemaUri;
  private final String encoding;
  private final String root;
  private final String[] propertiesFiles;
  private final String filenamePattern;
  private Generator<String> fileNameGenerator;
  private Generator<?> contentGenerator;

  /**
   * Instantiates a new Xml file generator.
   *
   * @param schemaUri       the schema uri
   * @param root            the root
   * @param filenamePattern the filename pattern
   * @param propertiesFiles the properties files
   */
  public XMLFileGenerator(String schemaUri, String root, String filenamePattern, String... propertiesFiles) {
    this.schemaUri = schemaUri;
    this.encoding = SystemInfo.getFileEncoding();
    this.filenamePattern = filenamePattern;
    this.propertiesFiles = propertiesFiles;
    this.root = root;
  }

  @Override
  public Class<File> getGeneratedType() {
    return File.class;
  }

  @Override
  public void init(GeneratorContext context) {
    BeneratorContext beneratorContext = (BeneratorContext) context;
    // parse schema
    XMLSchemaDescriptorProvider xsdProvider = new XMLSchemaDescriptorProvider(schemaUri, beneratorContext);
    beneratorContext.getDataModel().addDescriptorProvider(xsdProvider);
    // set up file name generator
    this.fileNameGenerator = WrapperFactory.applyConverter(
        new IncrementGenerator(),
        new MessageConverter(filenamePattern, Locale.US));
    // parse properties files
    try {
      for (String propertiesFile : propertiesFiles) {
        IncludeStatement.includeProperties(propertiesFile, beneratorContext);
      }
    } catch (IOException e) {
      throw new InvalidGeneratorSetupException(e);
    }
    // set up content generator
    TypeDescriptor rootDescriptor = beneratorContext.getDataModel().getTypeDescriptor(root);
    if (rootDescriptor == null) {
      throw new ConfigurationError("Type '" + root + "' not found in schema: " + schemaUri);
    }
    contentGenerator = MetaGeneratorFactory.createTypeGenerator(
        rootDescriptor, root, false, Uniqueness.NONE, beneratorContext);
    contentGenerator.init(context);
    super.init(context);
  }

  @Override
  @SuppressWarnings({"rawtypes", "unchecked"})
  public ProductWrapper<File> generate(ProductWrapper<File> wrapper) {
    ProductWrapper tmp = contentGenerator.generate(new ProductWrapper());
    if (tmp == null) {
      return null;
    }
    return wrapper.wrap(persistContent(tmp.unwrap()));
  }

  private File persistContent(Object content) {
    File file = new File(fileNameGenerator.generate(new ProductWrapper<>()).unwrap());
    if (content instanceof Entity) {
      persistRootEntity((Entity) content, file);
    } else {
      persistRootObject(content, file);
    }
    return file;
  }

  private void persistRootEntity(Entity entity, File file) {
    //entity.setComponentValue("xmlns", "http://databene.org/shop-0.5.1.xsd");
    entity.setComponent("elementFormDefault", "unqualified");
    try (XMLEntityExporter exporter = new XMLEntityExporter(file.getAbsolutePath(), encoding)) {
      process(entity, exporter);
    }
  }

  private void process(Entity entity, XMLEntityExporter exporter) {
    exporter.startProductConsumption(entity);
    for (Object component : entity.getComponents().values()) {
      if (component == null) {
        continue;
      }
      if (component instanceof Entity) {
        process((Entity) component, exporter);
      } else if (component.getClass().isArray()) {
        Object[] array = (Object[]) component;
        for (Object element : array) {
          if (element instanceof Entity) {
            process((Entity) element, exporter);
          }
        }
      }
    }
    exporter.finishProductConsumption(entity);
  }

  private void persistRootObject(Object content, File file) {
    PrintWriter printer = null;
    try {
      printer = XMLUtil.createXMLFile(file.getAbsolutePath(), encoding);
      printer.println("<" + root + ">" + content + "</" + root + ">");
    } catch (FileNotFoundException | UnsupportedEncodingException e) {
      throw new ConfigurationError(e);
    } finally {
      IOUtil.close(printer);
    }
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + '[' + filenamePattern + ']';
  }

}

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

package com.rapiddweller.benerator.engine;

import com.rapiddweller.benerator.BeneratorFactory;
import com.rapiddweller.benerator.BeneratorUtil;
import com.rapiddweller.benerator.Consumer;
import com.rapiddweller.benerator.RandomProvider;
import com.rapiddweller.benerator.distribution.Distribution;
import com.rapiddweller.benerator.engine.parser.DefaultGenerationInterceptor;
import com.rapiddweller.benerator.engine.parser.GenerationInterceptor;
import com.rapiddweller.benerator.engine.parser.String2DistributionConverter;
import com.rapiddweller.benerator.engine.parser.xml.BeneratorParseContext;
import com.rapiddweller.benerator.engine.parser.xml.XMLStatementParser;
import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.benerator.factory.ComplexTypeGeneratorFactory;
import com.rapiddweller.benerator.factory.SimpleTypeGeneratorFactory;
import com.rapiddweller.benerator.main.Benerator;
import com.rapiddweller.benerator.primitive.DefaultVarLengthStringGenerator;
import com.rapiddweller.benerator.primitive.VarLengthStringGenerator;
import com.rapiddweller.benerator.util.DefaultRandomProvider;
import com.rapiddweller.common.ArrayBuilder;
import com.rapiddweller.common.Context;
import com.rapiddweller.common.Converter;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.common.Validator;
import com.rapiddweller.common.collection.NamedValueList;
import com.rapiddweller.common.collection.OrderedNameMap;
import com.rapiddweller.common.context.CaseInsensitiveContext;
import com.rapiddweller.common.context.ContextAware;
import com.rapiddweller.common.converter.ConverterManager;
import com.rapiddweller.common.exception.ExceptionFactory;
import com.rapiddweller.common.version.VersionInfo;
import com.rapiddweller.format.text.DelocalizingConverter;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.ComponentDescriptor;
import com.rapiddweller.model.data.InstanceDescriptor;
import com.rapiddweller.platform.xml.DefaultXMLModule;
import com.rapiddweller.platform.xml.XMLModule;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Default implementation of the abstract {@link BeneratorFactory} class.<br/><br/>
 * Created: 08.09.2010 15:45:25
 * @author Volker Bergmann
 * @since 0.6.4
 */
public class DefaultBeneratorFactory extends BeneratorFactory {

  public static final String COMMUNITY_EDITION = "Community Edition";

  private final RandomProvider randomProvider;
  private final XMLModule xmlModule;
  private final List<XMLStatementParser> customParsers;

  public DefaultBeneratorFactory() {
    this(new DefaultRandomProvider(), new DefaultXMLModule());
  }

  public DefaultBeneratorFactory(RandomProvider randomProvider, XMLModule xmlModule) {
    this.randomProvider = randomProvider;
    this.xmlModule = xmlModule;
    this.customParsers = new ArrayList<>();
    try {
      ConverterManager.getInstance().registerConverterClass(String2DistributionConverter.class);
    } catch (Exception e) {
      throw BeneratorExceptionFactory.getInstance().componentInitializationFailed("rd-lib-common", e);
    }
  }

  @Override
  public String getEdition() {
    return COMMUNITY_EDITION;
  }

  public String[] getVersionInfo(boolean withMode) {
    ArrayBuilder<String> builder = new ArrayBuilder<>(String.class);
    getEditionInfo(builder);
    if (withMode) {
      builder.add("Mode:          " + Benerator.getMode().getCode());
    }
    builder.addAll(BeneratorUtil.getSystemInfo());
    return builder.toArray();
  }

  protected void getEditionInfo(ArrayBuilder<String> builder) {
    VersionInfo version = VersionInfo.getInfo("benerator");
    builder.add("Benerator " + getEdition() + " " + version.getVersion() +
        " build " + version.getBuildNumber());
  }

  @Override
  public BeneratorRootContext createRootContext(String contextUri) {
    return new DefaultBeneratorContext();
  }

  @Override
  public GenerationInterceptor getGenerationInterceptor() {
    return new DefaultGenerationInterceptor();
  }

  public void addCustomParser(XMLStatementParser parser) {
    this.customParsers.add(parser);
  }

  @Override
  public BeneratorParseContext createParseContext(ResourceManager resourceManager) {
    BeneratorParseContext result = new BeneratorParseContext(resourceManager);
    for (XMLStatementParser parser : this.customParsers) {
      result.addParser(parser);
    }
    return result;
  }

  @Override
  public ComplexTypeGeneratorFactory getComplexTypeGeneratorFactory() {
    return ComplexTypeGeneratorFactory.getInstance();
  }

  @Override
  public SimpleTypeGeneratorFactory getSimpleTypeGeneratorFactory() {
    return SimpleTypeGeneratorFactory.getInstance();
  }

  @Override
  public <S, T> Converter<S, T> configureConverter(Converter<S, T> converter, BeneratorContext context) {
    if (converter instanceof ContextAware) {
      ((ContextAware) converter).setContext(context);
    }
    return converter;
  }

  @Override
  public <T> Validator<T> configureValidator(Validator<T> validator, BeneratorContext context) {
    if (validator instanceof ContextAware) {
      ((ContextAware) validator).setContext(context);
    }
    return validator;
  }

  @Override
  public Consumer configureConsumer(Consumer consumer, BeneratorContext context) {
    if (consumer instanceof ContextAware) {
      ((ContextAware) consumer).setContext(context);
    }
    return consumer;
  }

  @Override
  public Converter<String, String> createDelocalizingConverter() {
    return new DelocalizingConverter();
  }

  @Override
  public VarLengthStringGenerator createVarLengthStringGenerator(
      String charSetPattern, int minLength, int maxLength, int lengthGranularity, Distribution lengthDistribution) {
    return new DefaultVarLengthStringGenerator(charSetPattern, minLength, maxLength, lengthGranularity, lengthDistribution);
  }

  @Override
  public VarLengthStringGenerator createVarLengthStringGenerator(
      Set<Character> charSet, int minLength, int maxLength, int lengthGranularity, Distribution lengthDistribution) {
    return new DefaultVarLengthStringGenerator(charSet, minLength, maxLength, lengthGranularity, lengthDistribution);
  }

  @Override
  public RandomProvider getRandomProvider() {
    return randomProvider;
  }

  @Override
  public Context createGenerationContext() {
    return new CaseInsensitiveContext(true);
  }

  @Override
  public XMLModule getXMLModule() {
    return xmlModule;
  }

  @Override
  public ComponentDescriptor getComponent(
      String name, NamedValueList<InstanceDescriptor> parts, ComplexTypeDescriptor parent) {
    // search through the components
    for (InstanceDescriptor part : parts.values()) {
      if (StringUtil.equalsIgnoreCase(part.getName(), name) &&
          part instanceof ComponentDescriptor) {
        return (ComponentDescriptor) part;
      }
    }
    // if nothing was found, then query the parent descriptor
    if (parent != null) {
      return (parent.getComponent(name));
    }
    return null;
  }

  @Override
  public OrderedNameMap<Object> createComponentMap() {
    return OrderedNameMap.createCaseInsensitiveMap();
  }

  @Override
  public ExceptionFactory createExceptionFactory() {
    return new BeneratorExceptionFactory();
  }

  @Override
  public void importDefaultParsers(BeneratorParseContext parseContext) {
    Importer.importPlatformParsers(defaultCEPlatformNames(), true, parseContext);
  }

  @Override
  public void importDefaultClasses(BeneratorContext context) {
    // import frequently used Benerator packages
    context.importPackage("com.rapiddweller.benerator.consumer");
    context.importPackage("com.rapiddweller.benerator.converter");
    context.importPackage("com.rapiddweller.benerator.primitive");
    context.importPackage("com.rapiddweller.benerator.primitive.datetime");
    context.importPackage("com.rapiddweller.benerator.distribution.sequence");
    context.importPackage("com.rapiddweller.benerator.distribution.function");
    context.importPackage("com.rapiddweller.benerator.distribution.cumulative");
    context.importPackage("com.rapiddweller.benerator.sample");
    // import ConsoleExporter and LoggingConsumer
    context.importPackage("com.rapiddweller.model.consumer");
    // import format, converters and validators from common
    context.importPackage("com.rapiddweller.common.converter");
    context.importPackage("com.rapiddweller.common.format");
    context.importPackage("com.rapiddweller.common.validator");
    // Import platform classes
    Importer.importPlatformClasses(defaultCEPlatformNames(), true, context);
  }

  private String[] defaultCEPlatformNames() {
    return new String[] { "csv", "db", "dbunit", "fixedwidth",
        "memstore", "result", "template", "xls", "xml", "mongodb" };
  }

  @Override
  public String[] platformPkgCandidates(String platformName) {
    if (platformName.indexOf('.') < 0) {
      return new String[] { "com.rapiddweller.platform." + platformName };
    } else {
      return new String[] { platformName };
    }
  }

  @Override
  public String[] domainPkgCandidates(String platformName) {
    if (platformName.indexOf('.') < 0) {
      return new String[] { "com.rapiddweller.domain." + platformName };
    } else {
      return new String[] { platformName };
    }
  }

}

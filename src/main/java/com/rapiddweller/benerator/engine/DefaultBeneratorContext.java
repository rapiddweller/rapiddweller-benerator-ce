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
import com.rapiddweller.benerator.environment.Environment;
import com.rapiddweller.benerator.environment.EnvironmentUtil;
import com.rapiddweller.benerator.environment.SystemRef;
import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.benerator.factory.DefaultsProvider;
import com.rapiddweller.benerator.factory.GeneratorFactory;
import com.rapiddweller.benerator.factory.StochasticGeneratorFactory;
import com.rapiddweller.benerator.script.BeneratorScriptFactory;
import com.rapiddweller.benerator.script.graaljs.GraalJsScriptFactory;
import com.rapiddweller.benerator.script.graalpy.GraalPyScriptFactory;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.common.Assert;
import com.rapiddweller.common.BeanUtil;
import com.rapiddweller.common.Context;
import com.rapiddweller.common.ErrorHandler;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.common.Level;
import com.rapiddweller.common.LocaleUtil;
import com.rapiddweller.common.NullSafeComparator;
import com.rapiddweller.common.SystemInfo;
import com.rapiddweller.common.bean.ClassCache;
import com.rapiddweller.common.context.ContextStack;
import com.rapiddweller.common.context.DefaultContext;
import com.rapiddweller.common.context.SimpleContextStack;
import com.rapiddweller.common.file.FileSuffixFilter;
import com.rapiddweller.domain.address.Country;
import com.rapiddweller.format.script.ScriptUtil;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.ComponentDescriptor;
import com.rapiddweller.model.data.DataModel;
import com.rapiddweller.model.data.DefaultDescriptorProvider;
import com.rapiddweller.model.data.DescriptorProvider;
import com.rapiddweller.model.data.TypeDescriptor;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Default implementation of {@link BeneratorContext}.<br/><br/>
 * Created: 02.09.2011 14:36:58
 * @author Volker Bergmann
 * @since 0.7.0
 */
public class DefaultBeneratorContext implements BeneratorRootContext {

  // constants -------------------------------------------------------------------------------------------------------

  public static final String DEFAULT_CONTEXT_URI = ".";


  // attributes -------------------------------------------------------------------------------------------------------

  private GeneratorFactory generatorFactory;
  private final DefaultContext settings;
  private final ClassCache classCache;
  private final ContextStack contextStack;

  protected boolean defaultSourceScripted;
  protected String defaultEncoding;
  protected String defaultDataset;
  protected long defaultPageSize;
  protected boolean defaultNull;
  protected String contextUri;
  protected Long maxCount;
  protected boolean defaultOneToOne;
  protected boolean defaultImports;
  protected boolean acceptUnknownSimpleTypes;

  protected final Map<String, Environment> environments;
  protected ComplexTypeDescriptor defaultComponent;
  protected ExecutorService executorService;

  protected String currentProductName;
  private ProductWrapper<?> currentProduct;

  private DataModel dataModel;
  private final DefaultDescriptorProvider localDescriptorProvider;


  // construction ----------------------------------------------------------------------------------------------------

  static {
    ScriptUtil.addFactory("js", new GraalJsScriptFactory());
    ScriptUtil.addFactory("py", new GraalPyScriptFactory());
    ScriptUtil.addFactory("ben", new BeneratorScriptFactory());
    ScriptUtil.setDefaultScriptEngine("ben");
  }

  public DefaultBeneratorContext() {
    this(DEFAULT_CONTEXT_URI);
  }

  public DefaultBeneratorContext(String contextUri) {
    if (contextUri == null) {
      throw BeneratorExceptionFactory.getInstance().programmerConfig("No context URI specified", null);
    }
    this.contextUri = contextUri;
    this.defaultSourceScripted = false;
    this.defaultEncoding = SystemInfo.getFileEncoding();
    this.defaultDataset = Country.getDefault().getIsoCode();
    this.defaultPageSize = 1;
    this.defaultNull = true;
    this.maxCount = null;
    this.defaultOneToOne = false;
    this.defaultImports = true;
    this.acceptUnknownSimpleTypes = false;

    this.executorService = createExecutorService();
    this.dataModel = new DataModel();
    this.localDescriptorProvider = new DefaultDescriptorProvider("ctx", dataModel);
    this.environments = new HashMap<>();
    this.defaultComponent = new ComplexTypeDescriptor("benerator:defaultComponent", localDescriptorProvider);
    this.generatorFactory = createGeneratorFactory();
    this.settings = new DefaultContext();
    this.contextStack = createContextStack(
        new DefaultContext(java.lang.System.getenv()),
        new DefaultContext(java.lang.System.getProperties()),
        settings,
        BeneratorFactory.getInstance().createGenerationContext()
    );
    set("context", this);
    if (IOUtil.isFileUri(contextUri)) {
      addLibFolderToClassLoader();
    }
    classCache = new ClassCache();
  }

  private StochasticGeneratorFactory createGeneratorFactory() {
    return new StochasticGeneratorFactory();
  }


  // properties ------------------------------------------------------------------------------------------------------

  @Override
  public GeneratorFactory getGeneratorFactory() {
    return generatorFactory;
  }

  @Override
  public void setGeneratorFactory(GeneratorFactory generatorFactory) {
    this.generatorFactory = generatorFactory;
  }

  @Override
  public DescriptorProvider getLocalDescriptorProvider() {
    return localDescriptorProvider;
  }

  @Override
  public void setDefaultsProvider(DefaultsProvider defaultsProvider) {
    this.generatorFactory.setDefaultsProvider(defaultsProvider);
  }

  @Override
  public String getDefaultEncoding() {
    return defaultEncoding;
  }

  public boolean isDefaultSourceScripted() {
    return defaultSourceScripted;
  }

  public void setDefaultSourceScripted(boolean defaultSourceScripted) {
    this.defaultSourceScripted = defaultSourceScripted;
  }

  @Override
  public void setDefaultEncoding(String defaultEncoding) {
    SystemInfo.setFileEncoding(defaultEncoding);
    this.defaultEncoding = defaultEncoding;
  }

  @Override
  public String getDefaultLineSeparator() {
    return SystemInfo.getLineSeparator();
  }

  @Override
  public void setDefaultLineSeparator(String defaultLineSeparator) {
    SystemInfo.setLineSeparator(defaultLineSeparator);
  }

  @Override
  public Locale getDefaultLocale() {
    return Locale.getDefault();
  }

  @Override
  public void setDefaultLocale(Locale defaultLocale) {
    Locale.setDefault(defaultLocale);
  }

  @Override
  public String getDefaultDataset() {
    return defaultDataset;
  }

  @Override
  public void setDefaultDataset(String defaultDataset) {
    this.defaultDataset = defaultDataset;
    Country country = Country.getInstance(defaultDataset, false);
    if (country != null) {
      Country.setDefault(country);
    }
  }

  @Override
  public long getDefaultPageSize() {
    return defaultPageSize;
  }

  @Override
  public void setDefaultPageSize(long defaultPageSize) {
    this.defaultPageSize = defaultPageSize;
  }

  @Override
  public String getDefaultScript() {
    return ScriptUtil.getDefaultScriptEngine();
  }

  @Override
  public void setDefaultScript(String defaultScript) {
    ScriptUtil.setDefaultScriptEngine(defaultScript);
  }

  @Override
  public boolean isDefaultNull() {
    return defaultNull;
  }

  @Override
  public void setDefaultNull(boolean defaultNull) {
    this.defaultNull = defaultNull;
  }

  @Override
  public char getDefaultSeparator() {
    return getDefaultCellSeparator();
  }

  @Override
  public void setDefaultSeparator(char defaultSeparator) {
    System.setProperty(CELL_SEPARATOR_SYSPROP, String.valueOf(defaultSeparator));
  }

  @Override
  public ComponentDescriptor getDefaultComponentConfig(String name) {
    return defaultComponent.getComponent(name);
  }

  @Override
  public void setDefaultComponentConfig(ComponentDescriptor component) {
    defaultComponent.setComponent(component);
  }

  @Override
  public SystemRef getEnvironmentSystem(String envName, String system) {
    synchronized (environments) {
      Environment env = environments.computeIfAbsent(envName, k -> EnvironmentUtil.parse(envName, contextUri));
      return env.getSystem(system);
    }
  }

  @Override
  public String getDefaultErrorHandler() {
    return ErrorHandler.getDefaultLevel().name();
  }

  @Override
  public void setDefaultErrorHandler(String defaultErrorHandler) {
    ErrorHandler.setDefaultLevel(Level.valueOf(defaultErrorHandler));
  }

  @Override
  public String getContextUri() {
    return contextUri;
  }

  @Override
  public void setContextUri(String contextUri) {
    this.contextUri = contextUri;
  }

  @Override
  public boolean isValidate() {
    return BeneratorOpts.isValidating();
  }

  @Override
  public void setValidate(boolean validate) {
    BeneratorOpts.setValidating(validate);
  }

  @Override
  public Long getMaxCount() {
    return maxCount;
  }

  @Override
  public void setMaxCount(Long maxCount) {
    if (maxCount != null) {
      Assert.notNegative(maxCount, "maxCount");
    }
    this.maxCount = maxCount;
  }

  @Override
  public ExecutorService getExecutorService() {
    return executorService;
  }

  @Override
  public boolean isDefaultOneToOne() {
    return defaultOneToOne;
  }

  @Override
  public void setDefaultOneToOne(boolean defaultOneToOne) {
    this.defaultOneToOne = defaultOneToOne;
  }

  @Override
  public boolean isAcceptUnknownSimpleTypes() {
    return acceptUnknownSimpleTypes;
  }

  @Override
  public void setAcceptUnknownSimpleTypes(boolean acceptUnknownSimpleTypes) {
    this.acceptUnknownSimpleTypes = acceptUnknownSimpleTypes;
    dataModel.setAcceptUnknownPrimitives(acceptUnknownSimpleTypes);
  }

  public static char getDefaultCellSeparator() {
    String tmp = System.getProperty(CELL_SEPARATOR_SYSPROP);
    if (tmp == null) {
      return DEFAULT_CELL_SEPARATOR;
    }
    if (tmp.length() != 1) {
      throw BeneratorExceptionFactory.getInstance().configurationError("Cell separator has illegal length: '" + tmp + "'");
    }
    return tmp.charAt(0);
  }

  @Override
  public DefaultsProvider getDefaultsProvider() {
    return getGeneratorFactory().getDefaultsProvider();
  }

  @Override
  public void setDefaultImports(boolean defaultImports) {
    this.defaultImports = defaultImports;
  }

  @Override
  public boolean isDefaultImports() {
    return defaultImports;
  }

  @Override
  public ProductWrapper<?> getCurrentProduct() {
    return currentProduct;
  }

  @Override
  public void setCurrentProduct(ProductWrapper<?> currentProduct) {
    this.currentProduct = currentProduct;
  }

  @Override
  public DataModel getDataModel() {
    return dataModel;
  }

  @Override
  public void setDataModel(DataModel dataModel) {
    this.dataModel = dataModel;
  }


  // Context interface -----------------------------------------------------------------------------------------------

  @Override
  public Object get(String key) {
    if (contextStack.contains(key)) {
      return contextStack.get(key);
    } else if (key.equalsIgnoreCase(currentProductName) || "this".equalsIgnoreCase(key)) {
      return currentProduct.unwrap();
    } else {
      return null;
    }
  }

  @Override
  public void set(String key, Object value) {
    contextStack.set(key, value);
  }

  @Override
  public void remove(String key) {
    contextStack.remove(key);
  }

  @Override
  public Set<String> keySet() {
    return contextStack.keySet();
  }

  @Override
  public Set<Entry<String, Object>> entrySet() {
    return contextStack.entrySet();
  }

  @Override
  public boolean contains(String key) {
    return (key != null && (key.equalsIgnoreCase(currentProductName) || "this".equalsIgnoreCase(key) || contextStack.contains(key)));
  }


  // class-loading interface -----------------------------------------------------------------------------------------

  @Override
  public Class<?> forName(String className) {
    return classCache.forName(className);
  }

  @Override
  public Class<?> forName(String className, boolean required) {
    return classCache.forName(className, required);
  }

  @Override
  public void importClass(String className) {
    classCache.importClass(className);
  }

  @Override
  public void importPackage(String packageName) {
    classCache.importPackage(packageName);
  }

  @Override
  public void importDefaults() {
    BeneratorFactory.getInstance().importDefaultClasses(this);
  }


  // other interface methods -----------------------------------------------------------------------------------------

  @Override
  public void setGlobal(String name, Object value) {
    settings.set(name, value);
  }

  @Override
  public Object getGlobal(String name) {
    return settings.get(name);
  }

  @Override
  public void close() {
    executorService.shutdownNow();
  }

  @Override
  public void addLocalType(TypeDescriptor type) {
    localDescriptorProvider.addTypeDescriptor(type);
  }

  @Override
  public BeneratorContext createSubContext(String productName) {
    return new DefaultBeneratorSubContext(productName, this);
  }

  public void setCurrentProduct(ProductWrapper<?> currentProduct, String currentProductName) {
    this.currentProductName = currentProductName;
    setCurrentProduct(currentProduct);
  }

  @Override
  public boolean hasProductNameInScope(String productName) {
    return (NullSafeComparator.equals(this.currentProductName, productName));
  }

  @Override
  public String resolveRelativeUri(String relativeUri) {
    return IOUtil.resolveRelativeUri(relativeUri, contextUri);
  }


  // non-public helper methods ---------------------------------------------------------------------------------------

  private void addLibFolderToClassLoader() {
    File libFolder = new File(contextUri, "lib");
    if (libFolder.exists()) {
      Thread.currentThread().setContextClassLoader(BeanUtil.createDirectoryClassLoader(libFolder));
      for (File jarFile : Objects.requireNonNull(libFolder.listFiles(new FileSuffixFilter("jar", false)))) {
        ClassLoader classLoader = BeanUtil.createJarClassLoader(jarFile);
        Thread.currentThread().setContextClassLoader(classLoader);
      }
    }
  }

  protected ExecutorService createExecutorService() {
    return Executors.newSingleThreadExecutor();
  }

  protected ContextStack createContextStack(Context... contexts) {
    return new SimpleContextStack(contexts);
  }


  // java.lang.Object overrides --------------------------------------------------------------------------------------

  @Override
  public String toString() {
    return getClass() + "[" + currentProductName + "]";
  }

}

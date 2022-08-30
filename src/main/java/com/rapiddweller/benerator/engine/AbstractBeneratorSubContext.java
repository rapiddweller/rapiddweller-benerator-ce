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
import com.rapiddweller.benerator.environment.SystemRef;
import com.rapiddweller.benerator.factory.DefaultsProvider;
import com.rapiddweller.benerator.factory.GeneratorFactory;
import com.rapiddweller.common.Context;
import com.rapiddweller.model.data.ComponentDescriptor;
import com.rapiddweller.model.data.DataModel;
import com.rapiddweller.model.data.DescriptorProvider;
import com.rapiddweller.model.data.TypeDescriptor;

import java.util.HashSet;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutorService;

/**
 * Abstract implementation of the {@link BeneratorSubContext} interface.<br/><br/>
 * Created: 26.01.2013 13:14:37
 * @author Volker Bergmann
 * @since 0.8.0
 */
public abstract class AbstractBeneratorSubContext extends AbstractBeneratorContext implements BeneratorSubContext {

  protected final BeneratorContext parent;
  protected final String currentProductName;
  private final Context localContext;

  protected AbstractBeneratorSubContext(String productName, BeneratorContext parent) {
    this.currentProductName = productName;
    this.parent = parent;
    this.localContext = BeneratorFactory.getInstance().createGenerationContext();
  }

  @Override
  public BeneratorContext getParent() {
    return parent;
  }

  // simple delegates ------------------------------------------------------------------------------------------------


  @Override
  public void setContextUri(String contextUri) {
    parent.setContextUri(contextUri);
  }

  @Override
  public boolean isDefaultSourceScripted() {
    return parent.isDefaultSourceScripted();
  }

  @Override
  public String getDefaultEncoding() {
    return parent.getDefaultEncoding();
  }

  @Override
  public String getDefaultLineSeparator() {
    return parent.getDefaultLineSeparator();
  }

  @Override
  public Locale getDefaultLocale() {
    return parent.getDefaultLocale();
  }

  @Override
  public String getDefaultTimeZone() {
    return parent.getDefaultTimeZone();
  }

  @Override
  public void remove(String key) {
    parent.remove(key);
  }

  @Override
  public String getDefaultDataset() {
    return parent.getDefaultDataset();
  }

  @Override
  public long getDefaultPageSize() {
    return parent.getDefaultPageSize();
  }

  @Override
  public String getDefaultScript() {
    return parent.getDefaultScript();
  }

  @Override
  public boolean isDefaultNull() {
    return parent.isDefaultNull();
  }

  @Override
  public char getDefaultSeparator() {
    return parent.getDefaultSeparator();
  }

  @Override
  public String getDefaultErrorHandler() {
    return parent.getDefaultErrorHandler();
  }

  @Override
  public String getContextUri() {
    return parent.getContextUri();
  }

  @Override
  public boolean isValidate() {
    return parent.isValidate();
  }

  @Override
  public Long getMaxCount() {
    return parent.getMaxCount();
  }

  @Override
  public GeneratorFactory getGeneratorFactory() {
    return parent.getGeneratorFactory();
  }

  @Override
  public void setGeneratorFactory(GeneratorFactory generatorFactory) {
    parent.setGeneratorFactory(generatorFactory);
  }

  @Override
  public Object getGlobal(String name) {
    return parent.getGlobal(name);
  }

  @Override
  public DefaultsProvider getDefaultsProvider() {
    return parent.getDefaultsProvider();
  }

  @Override
  public void setDefaultsProvider(DefaultsProvider defaultsProvider) {
    parent.setDefaultsProvider(defaultsProvider);
  }

  @Override
  public Class<?> forName(String className) {
    return parent.forName(className);
  }

  @Override
  public Class<?> forName(String className, boolean required) {
    return parent.forName(className, required);
  }

  @Override
  public ExecutorService getExecutorService() {
    return parent.getExecutorService();
  }

  @Override
  public void setGlobal(String name, Object value) {
    parent.setGlobal(name, value);
  }

  @Override
  public String resolveRelativeUri(String relativeUri) {
    return parent.resolveRelativeUri(relativeUri);
  }

  @Override
  public void close() {
    parent.close();
  }

  @Override
  public void importClass(String className) {
    parent.importClass(className);
  }

  @Override
  public void importPackage(String packageName) {
    parent.importPackage(packageName);
  }

  @Override
  public void importDefaults() {
    parent.importDefaults();
  }

  @Override
  public SystemRef getEnvironmentSystem(String environment, String system) {
    return parent.getEnvironmentSystem(environment, system);
  }

  @Override
  public ComponentDescriptor getDefaultComponentConfig(String name) {
    return parent.getDefaultComponentConfig(name);
  }

  @Override
  public void setDefaultComponentConfig(ComponentDescriptor component) {
    parent.setDefaultComponentConfig(component);
  }

  @Override
  public boolean isDefaultOneToOne() {
    return parent.isDefaultOneToOne();
  }

  @Override
  public boolean isAcceptUnknownSimpleTypes() {
    return parent.isAcceptUnknownSimpleTypes();
  }

  @Override
  public boolean isDefaultImports() {
    return parent.isDefaultImports();
  }

  @Override
  public DataModel getDataModel() {
    return parent.getDataModel();
  }

  @Override
  public void setDataModel(DataModel dataModel) {
    parent.setDataModel(dataModel);
  }

  @Override
  public DescriptorProvider getLocalDescriptorProvider() {
    return parent.getLocalDescriptorProvider();
  }

  @Override
  public void addLocalType(TypeDescriptor type) {
    parent.addLocalType(type);
  }


  // Functional interface --------------------------------------------------------------------------------------------

  @Override
  public boolean hasProductNameInScope(String currentProductName) {
    return (this.currentProductName != null && this.currentProductName.equals(currentProductName))
        || (parent != null && parent.hasProductNameInScope(currentProductName));
  }

  @Override
  public boolean contains(String key) {
    return key != null && (key.equalsIgnoreCase(currentProductName) || "this".equalsIgnoreCase(key) ||
        localContext.contains(key) || parent.contains(key));
  }

  @Override
  public Object get(String key) {
    if (key == null) {
      return null;
    } else if (localContext.contains(key)) {
      return localContext.get(key);
    } else if (key.equalsIgnoreCase(currentProductName) || "this".equalsIgnoreCase(key)) {
      return getCurrentProduct().unwrap();
    } else {
      return parent.get(key);
    }
  }

  @Override
  public void set(String key, Object value) {
    localContext.set(key, value);
  }

  @Override
  public Set<String> keySet() {
    Set<String> keySet = new HashSet<>(parent.keySet());
    keySet.addAll(localContext.keySet());
    return keySet;
  }

  @Override
  public Set<Entry<String, Object>> entrySet() {
    try {
      Set<Entry<String, Object>> entrySet = new HashSet<>(parent.entrySet());
      entrySet.addAll(localContext.entrySet());
      return entrySet;
    } catch (NullPointerException e) {
      return localContext.entrySet();
    }


  }


  // java.lang.Object overrides --------------------------------------------------------------------------------------

  @Override
  public String toString() {
    return getClass().getSimpleName() + "(" + currentProductName + ")";
  }

}

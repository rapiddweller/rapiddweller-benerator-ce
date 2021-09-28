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

package com.rapiddweller.benerator.test;

import com.rapiddweller.benerator.BeneratorFactory;
import com.rapiddweller.benerator.engine.BeneratorRootContext;
import com.rapiddweller.model.data.ArrayElementDescriptor;
import com.rapiddweller.model.data.ArrayTypeDescriptor;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.DataModel;
import com.rapiddweller.model.data.DefaultDescriptorProvider;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.model.data.IdDescriptor;
import com.rapiddweller.model.data.InstanceDescriptor;
import com.rapiddweller.model.data.PartDescriptor;
import com.rapiddweller.model.data.ReferenceDescriptor;
import com.rapiddweller.model.data.SimpleTypeDescriptor;
import com.rapiddweller.model.data.TypeDescriptor;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.junit.Before;

/**
 * Abstract parent class for all tests which rely on a {@link DataModel}.<br/><br/>
 * Created: 09.12.2011 22:21:24
 * @author Volker Bergmann
 * @since 0.7.4
 */
public abstract class ModelTest {

  protected final Logger logger = LoggerFactory.getLogger(getClass());

  protected BeneratorRootContext context;
  protected DataModel dataModel;
  protected DefaultDescriptorProvider testDescriptorProvider;

  @Before
  public void setUpContextAndDescriptorProvider() {
    this.context = (BeneratorRootContext) BeneratorFactory.getInstance().createRootContext(".");
    this.context.importDefaults();
    this.dataModel = context.getDataModel();
    this.testDescriptorProvider = new DefaultDescriptorProvider("test", context.getDataModel());
  }

  protected ComplexTypeDescriptor createComplexType(String name) {
    return new ComplexTypeDescriptor(name, testDescriptorProvider);
  }

  protected ComplexTypeDescriptor createComplexType(String name, ComplexTypeDescriptor parentType) {
    return new ComplexTypeDescriptor(name, testDescriptorProvider, parentType);
  }

  protected PartDescriptor createPartDescriptor(String componentName) {
    return new PartDescriptor(componentName, testDescriptorProvider);
  }

  protected Entity createEntity(String entityType, Object... componentNameAndValuePairs) {
    return new Entity(entityType, testDescriptorProvider, componentNameAndValuePairs);
  }

  protected PartDescriptor createPart(String partName) {
    return new PartDescriptor(partName, testDescriptorProvider);
  }

  protected PartDescriptor createPart(String partName, String typeName) {
    return new PartDescriptor(partName, testDescriptorProvider, typeName);
  }

  protected PartDescriptor createPart(String partName, TypeDescriptor type) {
    return new PartDescriptor(partName, testDescriptorProvider, type);
  }

  protected SimpleTypeDescriptor createSimpleType(String name) {
    return new SimpleTypeDescriptor(name, testDescriptorProvider);
  }

  protected SimpleTypeDescriptor createSimpleType(String name, String parentName) {
    return new SimpleTypeDescriptor(name, testDescriptorProvider, parentName);
  }

  protected ReferenceDescriptor createReference(String name, String typeName) {
    return new ReferenceDescriptor(name, testDescriptorProvider, typeName);
  }

  protected InstanceDescriptor createInstance(String name) {
    return new InstanceDescriptor(name, testDescriptorProvider);
  }

  protected InstanceDescriptor createInstance(String name, TypeDescriptor type) {
    return new InstanceDescriptor(name, testDescriptorProvider, type);
  }

  protected IdDescriptor createId(String name) {
    return new IdDescriptor(name, testDescriptorProvider);
  }

  protected IdDescriptor createId(String name, String type) {
    return new IdDescriptor(name, testDescriptorProvider, type);
  }

  protected IdDescriptor createId(String name, TypeDescriptor type) {
    return new IdDescriptor(name, testDescriptorProvider, type);
  }

  protected ArrayTypeDescriptor createArrayType(String name) {
    return new ArrayTypeDescriptor(name, testDescriptorProvider);
  }

  protected ArrayTypeDescriptor createArrayType(String name, ArrayTypeDescriptor parent) {
    return new ArrayTypeDescriptor(name, testDescriptorProvider, parent);
  }

  protected ArrayElementDescriptor createArrayElement(int index, String typeName) {
    return new ArrayElementDescriptor(index, testDescriptorProvider, typeName);
  }

  protected String testResourcePath(String fileName) {
    return "src/test/resources/"
        + this.getClass().getPackage().getName().replace('.', '/') + "/"
        + fileName;
  }

}

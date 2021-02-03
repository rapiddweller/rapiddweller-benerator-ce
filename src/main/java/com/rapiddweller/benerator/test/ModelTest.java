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
import com.rapiddweller.benerator.engine.BeneratorContext;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;

/**
 * Abstract parent class for all tests which rely on a {@link DataModel}.<br/><br/>
 * Created: 09.12.2011 22:21:24
 *
 * @author Volker Bergmann
 * @since 0.7.4
 */
public abstract class ModelTest {

  /**
   * The Logger.
   */
  protected final Logger logger = LogManager.getLogger(getClass());

  /**
   * The Context.
   */
  public BeneratorContext context;
  /**
   * The Data model.
   */
  protected DataModel dataModel;
  /**
   * The Test descriptor provider.
   */
  protected DefaultDescriptorProvider testDescriptorProvider;

  /**
   * Sets up context and descriptor provider.
   */
  @Before
  public void setUpContextAndDescriptorProvider() {
    this.context = BeneratorFactory.getInstance().createContext(".");
    this.context.importDefaults();
    this.dataModel = context.getDataModel();
    this.testDescriptorProvider = new DefaultDescriptorProvider("test", context.getDataModel());
  }

  /**
   * Create complex type complex type descriptor.
   *
   * @param name the name
   * @return the complex type descriptor
   */
  protected ComplexTypeDescriptor createComplexType(String name) {
    return new ComplexTypeDescriptor(name, testDescriptorProvider);
  }

  /**
   * Create complex type complex type descriptor.
   *
   * @param name       the name
   * @param parentType the parent type
   * @return the complex type descriptor
   */
  protected ComplexTypeDescriptor createComplexType(String name, ComplexTypeDescriptor parentType) {
    return new ComplexTypeDescriptor(name, testDescriptorProvider, parentType);
  }

  /**
   * Create part descriptor part descriptor.
   *
   * @param componentName the component name
   * @return the part descriptor
   */
  protected PartDescriptor createPartDescriptor(String componentName) {
    return new PartDescriptor(componentName, testDescriptorProvider);
  }

  /**
   * Create entity entity.
   *
   * @param entityType                 the entity type
   * @param componentNameAndValuePairs the component name and value pairs
   * @return the entity
   */
  protected Entity createEntity(String entityType, Object... componentNameAndValuePairs) {
    return new Entity(entityType, testDescriptorProvider, componentNameAndValuePairs);
  }

  /**
   * Create part part descriptor.
   *
   * @param partName the part name
   * @return the part descriptor
   */
  protected PartDescriptor createPart(String partName) {
    return new PartDescriptor(partName, testDescriptorProvider);
  }

  /**
   * Create part part descriptor.
   *
   * @param partName the part name
   * @param typeName the type name
   * @return the part descriptor
   */
  protected PartDescriptor createPart(String partName, String typeName) {
    return new PartDescriptor(partName, testDescriptorProvider, typeName);
  }

  /**
   * Create part part descriptor.
   *
   * @param partName the part name
   * @param type     the type
   * @return the part descriptor
   */
  protected PartDescriptor createPart(String partName, TypeDescriptor type) {
    return new PartDescriptor(partName, testDescriptorProvider, type);
  }

  /**
   * Create simple type simple type descriptor.
   *
   * @param name the name
   * @return the simple type descriptor
   */
  protected SimpleTypeDescriptor createSimpleType(String name) {
    return new SimpleTypeDescriptor(name, testDescriptorProvider);
  }

  /**
   * Create simple type simple type descriptor.
   *
   * @param name       the name
   * @param parentName the parent name
   * @return the simple type descriptor
   */
  protected SimpleTypeDescriptor createSimpleType(String name, String parentName) {
    return new SimpleTypeDescriptor(name, testDescriptorProvider, parentName);
  }

  /**
   * Create reference reference descriptor.
   *
   * @param name     the name
   * @param typeName the type name
   * @return the reference descriptor
   */
  protected ReferenceDescriptor createReference(String name, String typeName) {
    return new ReferenceDescriptor(name, testDescriptorProvider, typeName);
  }

  /**
   * Create instance instance descriptor.
   *
   * @param name the name
   * @return the instance descriptor
   */
  protected InstanceDescriptor createInstance(String name) {
    return new InstanceDescriptor(name, testDescriptorProvider);
  }

  /**
   * Create instance instance descriptor.
   *
   * @param name the name
   * @param type the type
   * @return the instance descriptor
   */
  protected InstanceDescriptor createInstance(String name, TypeDescriptor type) {
    return new InstanceDescriptor(name, testDescriptorProvider, type);
  }

  /**
   * Create id id descriptor.
   *
   * @param name the name
   * @return the id descriptor
   */
  protected IdDescriptor createId(String name) {
    return new IdDescriptor(name, testDescriptorProvider);
  }

  /**
   * Create id id descriptor.
   *
   * @param name the name
   * @param type the type
   * @return the id descriptor
   */
  protected IdDescriptor createId(String name, String type) {
    return new IdDescriptor(name, testDescriptorProvider, type);
  }

  /**
   * Create id id descriptor.
   *
   * @param name the name
   * @param type the type
   * @return the id descriptor
   */
  protected IdDescriptor createId(String name, TypeDescriptor type) {
    return new IdDescriptor(name, testDescriptorProvider, type);
  }

  /**
   * Create array type array type descriptor.
   *
   * @param name the name
   * @return the array type descriptor
   */
  protected ArrayTypeDescriptor createArrayType(String name) {
    return new ArrayTypeDescriptor(name, testDescriptorProvider);
  }

  /**
   * Create array type array type descriptor.
   *
   * @param name   the name
   * @param parent the parent
   * @return the array type descriptor
   */
  protected ArrayTypeDescriptor createArrayType(String name, ArrayTypeDescriptor parent) {
    return new ArrayTypeDescriptor(name, testDescriptorProvider, parent);
  }

  /**
   * Create array element array element descriptor.
   *
   * @param index    the index
   * @param typeName the type name
   * @return the array element descriptor
   */
  protected ArrayElementDescriptor createArrayElement(int index, String typeName) {
    return new ArrayElementDescriptor(index, testDescriptorProvider, typeName);
  }

}

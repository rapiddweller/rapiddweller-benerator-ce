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

package com.rapiddweller.benerator.factory;

import com.rapiddweller.benerator.StorageSystem;
import com.rapiddweller.benerator.composite.ComponentBuilder;
import com.rapiddweller.benerator.sample.ConstantGenerator;
import com.rapiddweller.benerator.test.GeneratorTest;
import com.rapiddweller.common.CollectionUtil;
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.common.Context;
import com.rapiddweller.format.DataSource;
import com.rapiddweller.format.util.DataSourceFromIterable;
import com.rapiddweller.format.util.DataSourceProxy;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.DataModel;
import com.rapiddweller.model.data.DefaultDescriptorProvider;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.model.data.ReferenceDescriptor;
import com.rapiddweller.model.data.SimpleTypeDescriptor;
import com.rapiddweller.model.data.Uniqueness;
import com.rapiddweller.script.expression.ConstantExpression;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests the {@link ComponentBuilderFactory}'s reference-related methods.<br/>
 * <br/>
 * Created at 05.05.2008 17:08:45
 *
 * @author Volker Bergmann
 * @since 0.5.3
 */
public class ReferenceComponentBuilderFactoryTest extends GeneratorTest {

  @SuppressWarnings("rawtypes")
  @Test
  public void testScript() {
    ReferenceDescriptor ref = (ReferenceDescriptor) createTargetTypeDescriptor("ref", "Person", "Storage")
        .withCount(1);
    ref.getTypeDescriptor().setScript("8");
    ComponentBuilder generator = createAndInitBuilder(ref);
    Entity entity = createPersonEntity();
    setCurrentProduct(entity, "e");
    generator.execute(context);
    assertEquals(8, entity.get("ref"));
  }

  @SuppressWarnings("rawtypes")
  @Test
  public void testNullQuotaOne() {
    ReferenceDescriptor ref = (ReferenceDescriptor) createTargetTypeDescriptor("ref", "Person", "Storage")
        .withNullQuota(1).withCount(1);
    ComponentBuilder generator = createAndInitBuilder(ref);
    Entity entity = createPersonEntity();
    setCurrentProduct(entity, "e");
    generator.execute(context);
    assertNull(entity.get("ref"));
  }

  @SuppressWarnings("rawtypes")
  @Test
  public void testNullable() {
    ReferenceDescriptor ref = (ReferenceDescriptor) createTargetTypeDescriptor("ref", "Person", "Storage")
        .withCount(1);
    ref.setNullable(true);
    ComponentBuilder generator = createAndInitBuilder(ref);
    Entity entity = createPersonEntity();
    setCurrentProduct(entity, "e");
    generator.execute(context);
    assertNull(entity.get("ref"));
  }

  @SuppressWarnings("rawtypes")
  @Test
  public void testGenerator() {
    ReferenceDescriptor ref = (ReferenceDescriptor) createTargetTypeDescriptor("ref", "Person", "Storage")
        .withCount(1);
    ref.getTypeDescriptor().setGenerator("new " + ConstantGenerator.class.getName() + "(42)");
    ComponentBuilder generator = createAndInitBuilder(ref);
    Entity entity = createPersonEntity();
    setCurrentProduct(entity, "e");
    generator.execute(context);
    assertEquals(42, entity.get("ref"));
  }

  @SuppressWarnings("rawtypes")
  @Test
  public void testConstant() {
    ReferenceDescriptor ref = (ReferenceDescriptor) createTargetTypeDescriptor("ref", "Person", "Storage")
        .withCount(1);
    ((SimpleTypeDescriptor) ref.getTypeDescriptor()).setConstant("3");
    ComponentBuilder generator = createAndInitBuilder(ref);
    Entity entity = createPersonEntity();
    setCurrentProduct(entity, "e");
    generator.execute(context);
    assertEquals(3, entity.get("ref"));
  }

  @SuppressWarnings("rawtypes")
  @Test
  public void testSample() {
    ReferenceDescriptor ref = (ReferenceDescriptor) createTargetTypeDescriptor("ref", "Person", "Storage")
        .withCount(1);
    ((SimpleTypeDescriptor) ref.getTypeDescriptor()).setValues("6");
    ComponentBuilder generator = createAndInitBuilder(ref);
    Entity entity = createPersonEntity();
    setCurrentProduct(entity, "e");
    generator.execute(context);
    assertEquals("6", entity.get("ref"));
  }

  // tests that resolve the target type ------------------------------------------------------------------------------

  @Test(expected = ConfigurationError.class)
  public void testMissingType() {
    ReferenceDescriptor ref = createTargetTypeDescriptor("ref", null, "Storage");
    createAndInitBuilder(ref);
  }

  @Test(expected = ConfigurationError.class)
  public void testMissingSource() {
    ReferenceDescriptor ref = createTargetTypeDescriptor("ref", "Referee", null);
    createAndInitBuilder(ref);
  }

  @Test
  @SuppressWarnings({"null", "rawtypes"})
  public void testSingleRef() {
    ReferenceDescriptor ref = createTargetTypeDescriptor("ref", "Person", "Storage");
    ref.setCount(new ConstantExpression<>(1L));
    ComponentBuilder generator = createAndInitBuilder(ref);
    assertNotNull(generator);
    Entity entity = createPersonEntity();
    setCurrentProduct(entity, "e");
    generator.execute(context);
    assertTrue("Alice".equals(entity.get("ref")) || "Bob".equals(entity.get("ref")));
  }

  @Test
  @SuppressWarnings({"null", "rawtypes"})
  public void testMultiRef() {
    ReferenceDescriptor ref = createTargetTypeDescriptor("ref", "Person", "Storage");
    ref.setCount(new ConstantExpression<>(2L));
    ComponentBuilder builder = createAndInitBuilder(ref);
    assertNotNull(builder);
    Entity entity = createPersonEntity();
    setCurrentProduct(entity, "e");
    builder.execute(context);
    String[] product = (String[]) entity.get("ref");
    assertEquals(2, product.length);
    for (String element : product) {
      assertTrue("Alice".equals(element) || "Bob".equals(element));
    }
  }

  // private helpers -------------------------------------------------------------------------------------------------

  private ReferenceDescriptor createTargetTypeDescriptor(String refName, String targetType, String source) {
    ReferenceDescriptor descriptor = new ReferenceDescriptor(refName, testDescriptorProvider, "string");
    descriptor.getLocalType(false).setSource(source);
    descriptor.setTargetType(targetType);
    return descriptor;
  }

  private ComponentBuilder<?> createAndInitBuilder(ReferenceDescriptor ref) {
    StorageSystemMock storageSystem = new StorageSystemMock(ref.getDataModel());
    context.getDataModel().addDescriptorProvider(storageSystem);
    context.setGlobal(storageSystem.getId(), storageSystem);
    ComponentBuilder<?> builder = ComponentBuilderFactory.createComponentBuilder(ref, Uniqueness.NONE, false, context);
    builder.init(context);
    return builder;
  }

  public Entity createPersonEntity() {
    return new Entity("Person", testDescriptorProvider);
  }

  public static class StorageSystemMock extends DefaultDescriptorProvider implements StorageSystem {

    public StorageSystemMock(DataModel dataModel) {
      super("Storage", dataModel);
      super.addTypeDescriptor(new ComplexTypeDescriptor("Person", this));
    }

    @Override
    public void close() {
    }

    @Override
    public void flush() {
    }

    @Override
    public Object execute(String command) {
      throw new UnsupportedOperationException("query() not implemented");
    }

    @Override
    public DataSource<?> query(String selector, boolean simplify, Context context) {
      throw new UnsupportedOperationException("query() not implemented");
    }

    @Override
    public DataSource<Entity> queryEntities(String type, String selector, Context context) {
      throw new UnsupportedOperationException("queryEntities() not implemented");
    }

    @Override
    public DataSource<?> queryEntityIds(String entityName, String selector, Context context) {
      DataSource<String> source =
          new DataSourceFromIterable<>(CollectionUtil.toList("Alice", "Bob"), String.class);
      return new DataSourceProxy<>(source);
    }

    @Override
    public void store(Entity entity) {
      throw new UnsupportedOperationException("store() not implemented");
    }

    @Override
    public void update(Entity entity) {
      throw new UnsupportedOperationException("StorageSystem.update() is not implemented");
    }
  }

}

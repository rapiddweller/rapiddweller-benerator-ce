package com.rapiddweller.model.data;

import com.rapiddweller.platform.java.BeanDescriptorProvider;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * The type Data model test.
 */
public class DataModelTest {
  /**
   * Test constructor.
   */
  @Test
  public void testConstructor() {
    DataModel actualDataModel = new DataModel();
    BeanDescriptorProvider beanDescriptorProvider = actualDataModel.getBeanDescriptorProvider();
    assertEquals("BeanDescriptorProvider(bean)", beanDescriptorProvider.toString());
    assertSame(actualDataModel, beanDescriptorProvider.getDataModel());
    assertEquals(0, beanDescriptorProvider.getTypeDescriptors().length);
  }

  /**
   * Test get type descriptor.
   */
  @Test
  public void testGetTypeDescriptor() {
    assertNull((new DataModel()).getTypeDescriptor("Namespace", "Name"));
    assertNull((new DataModel()).getTypeDescriptor(null, "Name"));
    assertTrue((new DataModel()).getTypeDescriptor("Namespace", "date") instanceof SimpleTypeDescriptor);
    assertNull((new DataModel()).getTypeDescriptor("Namespace", null));
    assertNull((new DataModel()).getTypeDescriptor("ben", "com.rapiddweller.model.data.ArrayTypeDescriptor"));
    assertTrue((new DataModel()).getTypeDescriptor("ben", "date") instanceof SimpleTypeDescriptor);
  }

  protected String testResourcePath(String fileName) {
    return "src/test/resources/"
        + this.getClass().getName().replace('.', '/')
        + fileName;
  }

}


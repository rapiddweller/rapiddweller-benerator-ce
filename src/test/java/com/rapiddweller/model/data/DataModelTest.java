package com.rapiddweller.model.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import com.rapiddweller.platform.java.BeanDescriptorProvider;
import org.junit.Test;

public class DataModelTest {
    @Test
    public void testConstructor() {
        DataModel actualDataModel = new DataModel();
        BeanDescriptorProvider beanDescriptorProvider = actualDataModel.getBeanDescriptorProvider();
        assertEquals("BeanDescriptorProvider(bean)", beanDescriptorProvider.toString());
        assertSame(actualDataModel, beanDescriptorProvider.getDataModel());
        assertEquals(0, beanDescriptorProvider.getTypeDescriptors().length);
    }

    @Test
    public void testGetTypeDescriptor() {
        assertNull((new DataModel()).getTypeDescriptor("Namespace", "Name"));
        assertNull((new DataModel()).getTypeDescriptor(null, "Name"));
        assertTrue((new DataModel()).getTypeDescriptor("Namespace", "date") instanceof SimpleTypeDescriptor);
        assertNull((new DataModel()).getTypeDescriptor("Namespace", null));
        assertNull((new DataModel()).getTypeDescriptor("ben", "com.rapiddweller.model.data.ArrayTypeDescriptor"));
        assertTrue((new DataModel()).getTypeDescriptor("ben", "date") instanceof SimpleTypeDescriptor);
    }
}


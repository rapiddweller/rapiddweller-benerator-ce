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

package com.rapiddweller.benerator.composite;

import com.rapiddweller.common.ArrayUtil;
import com.rapiddweller.common.CollectionUtil;
import com.rapiddweller.model.data.ArrayTypeDescriptor;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.DataModel;
import com.rapiddweller.model.data.DefaultDescriptorProvider;
import com.rapiddweller.model.data.DescriptorProvider;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.model.data.PartDescriptor;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Tests the {@link ComponentTypeConverter}.<br/><br/>
 * Created: 28.08.2013 17:19:08
 * @author Volker Bergmann
 * @since 0.8.3
 */
public class ComponentTypeConverterTest {

  DescriptorProvider provider;
  private ComplexTypeDescriptor parentType;
  private ComplexTypeDescriptor childType;
  private ComponentTypeConverter converter;

  @Before
  public void setUp() {
    provider = new DefaultDescriptorProvider("p", new DataModel());
    childType = new ComplexTypeDescriptor("childType", provider);
    childType.setComponent(new PartDescriptor("child", provider, "string"));
    parentType = new ComplexTypeDescriptor("parentType", provider);
    parentType.setComponent(new PartDescriptor("child", provider, childType));
    parentType.setComponent(new PartDescriptor("list", provider, new ComplexTypeDescriptor("childType", provider)));
    parentType.setComponent(new PartDescriptor("array", provider, new ComplexTypeDescriptor("childType", provider)));
    converter = new ComponentTypeConverter(parentType);
  }

  @Test
  public void testRecursively() {
    Entity child = new Entity(childType, "child", "childChildValue");
    Entity parent = new Entity(parentType, "child", child);
    Entity result = converter.convert(parent);
    assertNotNull(result);
    Entity childEntity = (Entity) result.get("child");
    assertNotNull(childEntity);
    assertEquals("childType", childEntity.type());
  }

  @Test
  public void testComponentTypes() {
    List<Entity> list = CollectionUtil.toList(new Entity("childType", provider));
    Entity[] array = new Entity[] { new Entity("childType", provider) };
    Entity parent = new Entity(parentType, "list", list, "array", array);
    Entity result = converter.convert(parent);
    assertNotNull(result);
    // check list conversion
    Entity[] convertedList = (Entity[]) result.get("list");
    Entity[] expectedList = ArrayUtil.toArray(new Entity("childType", provider));
    assertArrayEquals(expectedList, convertedList);
    // check array conversion
    Entity[] convertedArray = (Entity[]) result.get("list");
    Entity[] expectedArray = new Entity[] { new Entity("childType", provider) };
    assertArrayEquals(expectedArray, convertedArray);
  }

  @Test
  public void testConvertNull() {
    assertNull(converter.convert(null));
  }

  @Test
  public void testMTSettings() {
    assertFalse(converter.isParallelizable());
    assertTrue(converter.isThreadSafe());
  }

  @Test
  public void testToString() {
    assertEquals("ComponentTypeConverter[parentType[child[details=[], type=childType[" +
        "child[details=[type=string], type=string[]]]], list[details=[], type=childType[]], " +
        "array[details=[], type=childType[]]]]", converter.toString());
  }

}

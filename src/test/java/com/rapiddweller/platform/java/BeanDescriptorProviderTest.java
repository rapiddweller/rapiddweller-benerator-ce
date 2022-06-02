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

package com.rapiddweller.platform.java;

import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.common.exception.IllegalArgumentError;
import com.rapiddweller.domain.person.Gender;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.SimpleTypeDescriptor;
import com.rapiddweller.model.data.TypeDescriptor;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Tests the {@link BeanDescriptorProvider}.<br/><br/>
 * Created: 19.04.2010 10:38:21
 * @author Volker Bergmann
 * @since 0.6.1
 */
public class BeanDescriptorProviderTest {

  final BeanDescriptorProvider provider = new BeanDescriptorProvider();

  @Test(expected = IllegalArgumentError.class)
  public void testConstructorWithNull() {
    new BeanDescriptorProvider(null);
  }

  @Test
  public void testAbstractType_primitive() {
    assertEquals("string", provider.abstractType(String.class));
  }

  @Test
  public void testAbstractType_bean() {
    assertEquals(ChildBean.class.getName(), provider.abstractType(ChildBean.class));
  }

  @Test
  public void testAbstractType_enum() {
    assertEquals("com.rapiddweller.domain.person.Gender", provider.abstractType(Gender.class));
  }

  @Test
  public void testConcreteType_primitive() {
    assertEquals(String.class, provider.concreteType("string"));
  }

  @Test
  public void testConcreteType_bean() {
    assertEquals(ChildBean.class, provider.concreteType(ChildBean.class.getName()));
  }

  @Test
  public void testConcreteType_enum() {
    assertEquals(Gender.class, provider.concreteType("com.rapiddweller.domain.person.Gender"));
  }

  @Test(expected = ConfigurationError.class)
  public void testConcreteType_illegal() {
    provider.concreteType("my.non.existing.Class");
  }

  @Test
  public void testGetTypeDescriptor_primitive() {
    TypeDescriptor type = provider.getTypeDescriptor("int");
    assertNull(type);
  }

  @Test
  public void testGetTypeDescriptor_bean() {
    ComplexTypeDescriptor type = (ComplexTypeDescriptor) provider.getTypeDescriptor(ChildBean.class.getName());
    assertNotNull(type);
    assertEquals(ChildBean.class.getName(), type.getName());
    assertNotNull(type.getComponent("name"));
    assertNotNull(type.getComponent("age"));
  }

  @Test
  public void testGetTypeDescriptor_enum() {
    SimpleTypeDescriptor type = (SimpleTypeDescriptor) provider.getTypeDescriptor(Gender.class.getName());
    assertNotNull(type);
    assertEquals(Gender.class.getName(), type.getName());
    assertEquals("string", type.getParentName());
  }

}

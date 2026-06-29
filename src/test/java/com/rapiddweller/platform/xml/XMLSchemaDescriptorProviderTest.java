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

package com.rapiddweller.platform.xml;

import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.DefaultBeneratorContext;
import com.rapiddweller.benerator.file.XMLFileGenerator;
import com.rapiddweller.benerator.util.GeneratorUtil;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.model.data.AlternativeGroupDescriptor;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.ComponentDescriptor;
import com.rapiddweller.model.data.PartDescriptor;
import com.rapiddweller.model.data.SimpleTypeDescriptor;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests the {@link XMLSchemaDescriptorProvider}.<br/><br/>
 * Created: 26.02.2008 21:05:23
 * @author Volker Bergmann
 * @since 0.5.0
 */
public class XMLSchemaDescriptorProviderTest {

  private static final String BASE = "com/rapiddweller/platform/xml/";

  private static final String SIMPLE_ELEMENT_TEST_FILE = BASE + "simple-element-test.xsd";
  private static final String NESTING_TEST_FILE = BASE + "nesting-test.xsd";
  private static final String ANNOTATION_TEST_FILE = BASE + "annotation-test.xsd";
  private static final String CHOICE_TEST_FILE = BASE + "choice-test.xsd";
  private static final String CARDINALITY_TEST_FILE = BASE + "cardinality-test.xsd";
  private static final String ENUM_TEST_FILE = BASE + "enum-test.xsd";
  private static final String FACETS_TEST_FILE = BASE + "facets-test.xsd";
  private static final String REF_TEST_FILE = BASE + "ref-test.xsd";

  @Test
  public void testSimpleTypeElement() {
    BeneratorContext context = new DefaultBeneratorContext(IOUtil.getParentUri(SIMPLE_ELEMENT_TEST_FILE));
    XMLSchemaDescriptorProvider provider = new XMLSchemaDescriptorProvider(SIMPLE_ELEMENT_TEST_FILE, context);
    try {
      ComplexTypeDescriptor rootDescriptor = (ComplexTypeDescriptor) provider.getTypeDescriptor("root");
      // check root
      assertNotNull(rootDescriptor);
      assertEquals(2, rootDescriptor.getComponents().size());
      // check inline
      assertComplexComponentWithSimpleContent("inline", rootDescriptor);
      // check external
      assertComplexComponentWithSimpleContent("external", rootDescriptor);
    } finally {
      IOUtil.close(provider);
    }
  }

  @Test
  public void testNesting() {
    BeneratorContext context = new DefaultBeneratorContext(IOUtil.getParentUri(NESTING_TEST_FILE));
    XMLSchemaDescriptorProvider provider = new XMLSchemaDescriptorProvider(NESTING_TEST_FILE, context);
    try {
      ComplexTypeDescriptor rootDescriptor = (ComplexTypeDescriptor) provider.getTypeDescriptor("root");
      // check root
      assertNotNull(rootDescriptor);
      assertEquals(4, rootDescriptor.getComponents().size());
      ComponentDescriptor rootAtt1 = rootDescriptor.getComponent("rootAtt1");
      assertNotNull(rootAtt1);
      // check c1
      ComponentDescriptor c1 = rootDescriptor.getComponent("c1");
      assertNotNull(c1);
      // check number
      ComponentDescriptor number = rootDescriptor.getComponent("number");
      assertNotNull(number);
      assertEquals(Long.valueOf(1), number.getMinCount().evaluate(null));
      assertEquals(Long.valueOf(1), number.getMaxCount().evaluate(null));
      // check c2
      ComponentDescriptor c2 = rootDescriptor.getComponent("c2");
      assertNotNull(c2);
    } finally {
      IOUtil.close(provider);
    }
  }

  @Test
  public void testAnnotations() {
    BeneratorContext context = new DefaultBeneratorContext(IOUtil.getParentUri(ANNOTATION_TEST_FILE));
    XMLSchemaDescriptorProvider provider = new XMLSchemaDescriptorProvider(ANNOTATION_TEST_FILE, context);
    try {
      ComplexTypeDescriptor rootDescriptor = (ComplexTypeDescriptor) provider.getTypeDescriptor("root");
      // check root
      assertNotNull(rootDescriptor);
      assertEquals(2, rootDescriptor.getComponents().size());

      // check component root.simple-type
      ComponentDescriptor simpleTypeComponent = rootDescriptor.getComponent("simple-type");
      assertNotNull(simpleTypeComponent);

      // check simple-type
      SimpleTypeDescriptor simpleType = (SimpleTypeDescriptor) provider.getTypeDescriptor("simple-type");
      assertNotNull(simpleType);
      assertEquals("'Alice','Bob'", simpleType.getValues());

      // check component root.complex-type
      ComponentDescriptor complexTypeComponent = rootDescriptor.getComponent("complex-type");
      assertNotNull(complexTypeComponent);

      // check complex-type
      ComplexTypeDescriptor complexType = (ComplexTypeDescriptor) provider.getTypeDescriptor("complex-type");
      assertNotNull(complexType);
      assertEquals("person.csv", complexType.getSource());

      XMLFileGenerator g = new XMLFileGenerator(ANNOTATION_TEST_FILE, "root", "target/test{0}.xml");
      g.init(context);
      GeneratorUtil.generateNonNull(g);
      GeneratorUtil.generateNonNull(g);
    } finally {
      IOUtil.close(provider);
    }
  }

  @Test
  public void testChoice() {
    BeneratorContext context = new DefaultBeneratorContext(IOUtil.getParentUri(CHOICE_TEST_FILE));
    XMLSchemaDescriptorProvider provider = new XMLSchemaDescriptorProvider(CHOICE_TEST_FILE, context);
    try {
      ComplexTypeDescriptor rootDescriptor = (ComplexTypeDescriptor) provider.getTypeDescriptor("root");
      // check root
      assertNotNull(rootDescriptor);
      List<ComponentDescriptor> components = rootDescriptor.getComponents();
      assertEquals(2, components.size());

      // check choice a/b
      ComponentDescriptor choiceAB = components.get(0);
      assertNotNull(choiceAB);
      assertEquals(1, ((Number) choiceAB.getMinCount().evaluate(null)).intValue());
      assertEquals(1, ((Number) choiceAB.getMaxCount().evaluate(null)).intValue());
      AlternativeGroupDescriptor choiceABType = (AlternativeGroupDescriptor) choiceAB.getTypeDescriptor();
      assertEquals(2, choiceABType.getComponents().size());

      // check choice x/y/z
      ComponentDescriptor choiceXYZ = components.get(1);
      assertNotNull(choiceXYZ);
      assertEquals(0, ((Number) choiceXYZ.getMinCount().evaluate(null)).intValue());
      assertEquals(2, ((Number) choiceXYZ.getMaxCount().evaluate(null)).intValue());
      AlternativeGroupDescriptor choiceXYZType = (AlternativeGroupDescriptor) choiceXYZ.getTypeDescriptor();
      assertEquals(3, choiceXYZType.getComponents().size());
    } finally {
      IOUtil.close(provider);
    }
  }

  /** XSD cardinality annotation (ben:part minCount/maxCount) must map to the component's counts. */
  @Test
  public void testCardinality() {
    BeneratorContext context = new DefaultBeneratorContext(IOUtil.getParentUri(CARDINALITY_TEST_FILE));
    XMLSchemaDescriptorProvider provider = new XMLSchemaDescriptorProvider(CARDINALITY_TEST_FILE, context);
    try {
      ComplexTypeDescriptor outer = (ComplexTypeDescriptor) provider.getTypeDescriptor("outer");
      assertNotNull(outer);
      ComponentDescriptor inner = outer.getComponent("inner");
      assertNotNull(inner);
      assertEquals(3, ((Number) inner.getMinCount().evaluate(null)).intValue());
      assertEquals(5, ((Number) inner.getMaxCount().evaluate(null)).intValue());
    } finally {
      IOUtil.close(provider);
    }
  }

  /** XSD enumeration restriction must become the attribute's value set. */
  @Test
  public void testEnumeration() {
    BeneratorContext context = new DefaultBeneratorContext(IOUtil.getParentUri(ENUM_TEST_FILE));
    XMLSchemaDescriptorProvider provider = new XMLSchemaDescriptorProvider(ENUM_TEST_FILE, context);
    try {
      ComplexTypeDescriptor address = (ComplexTypeDescriptor) provider.getTypeDescriptor("address");
      assertNotNull(address);
      ComponentDescriptor box = address.getComponent("box");
      assertNotNull(box);
      SimpleTypeDescriptor boxType = (SimpleTypeDescriptor) box.getTypeDescriptor();
      assertNotNull(boxType);
      assertNotNull(boxType.getValues());
      assertTrue(boxType.getValues().contains("0203"));
    } finally {
      IOUtil.close(provider);
    }
  }

  /** XSD restriction facets (min/maxInclusive, min/maxExclusive, length, pattern) and
   *  attribute settings (required, default, fixed, prohibited) must map onto the descriptors. */
  @Test
  public void testFacetsAndAttributes() {
    BeneratorContext context = new DefaultBeneratorContext(IOUtil.getParentUri(FACETS_TEST_FILE));
    XMLSchemaDescriptorProvider provider = new XMLSchemaDescriptorProvider(FACETS_TEST_FILE, context);
    try {
      ComplexTypeDescriptor root = (ComplexTypeDescriptor) provider.getTypeDescriptor("root");
      assertNotNull(root);
      // 3 sequence elements + 4 attributes
      assertEquals(7, root.getComponents().size());

      // inclusive range facets
      SimpleTypeDescriptor score = simpleContentOf(root, "score");
      assertEquals("1", score.getMin());
      assertEquals("100", score.getMax());
      // exclusive range facets
      SimpleTypeDescriptor ratio = simpleContentOf(root, "ratio");
      assertEquals("0", ratio.getMin());
      assertEquals("10", ratio.getMax());
      // length + pattern facets
      SimpleTypeDescriptor code = simpleContentOf(root, "code");
      assertEquals(Integer.valueOf(5), code.getMaxLength());
      assertEquals("[A-Z]{5}", code.getPattern());

      // required attribute -> not nullable
      assertEquals(Boolean.FALSE, root.getComponent("id").isNullable());
      // default and fixed values map onto the attribute's value set
      assertEquals("true", ((SimpleTypeDescriptor) root.getComponent("active").getLocalType(false)).getValues());
      assertEquals("v1", ((SimpleTypeDescriptor) root.getComponent("version").getLocalType(false)).getValues());
    } finally {
      IOUtil.close(provider);
    }
  }

  /** Element references, unbounded cardinality and key/keyref declarations must be parsed. */
  @Test
  public void testElementRefAndKeys() {
    BeneratorContext context = new DefaultBeneratorContext(IOUtil.getParentUri(REF_TEST_FILE));
    XMLSchemaDescriptorProvider provider = new XMLSchemaDescriptorProvider(REF_TEST_FILE, context);
    try {
      ComplexTypeDescriptor catalog = (ComplexTypeDescriptor) provider.getTypeDescriptor("catalog");
      assertNotNull(catalog);
      // the referenced element appears as a component
      assertNotNull(catalog.getComponent("color"));
      // maxOccurs="unbounded" -> unbounded max count (evaluates to null)
      ComponentDescriptor item = catalog.getComponent("item");
      assertNotNull(item);
      assertNull(item.getMaxCount().evaluate(null));
      // nillable="false" -> not nullable
      assertEquals(Boolean.FALSE, item.isNullable());
    } finally {
      IOUtil.close(provider);
    }
  }

  // helpers ---------------------------------------------------------------------------------------------------------

  private static SimpleTypeDescriptor simpleContentOf(ComplexTypeDescriptor owner, String name) {
    ComplexTypeDescriptor componentType = (ComplexTypeDescriptor) owner.getComponent(name).getTypeDescriptor();
    return (SimpleTypeDescriptor) componentType.getComponent(ComplexTypeDescriptor.__SIMPLE_CONTENT).getTypeDescriptor();
  }

  private static void assertComplexComponentWithSimpleContent(String name, ComplexTypeDescriptor rootDescriptor) {
    ComponentDescriptor stComponent = rootDescriptor.getComponent(name);
    assertNotNull(stComponent);
    assertTrue(stComponent instanceof PartDescriptor);
    ComplexTypeDescriptor stType = (ComplexTypeDescriptor) stComponent.getTypeDescriptor();
    ComponentDescriptor content = stType.getComponent(ComplexTypeDescriptor.__SIMPLE_CONTENT);
    assertNotNull(content);
    SimpleTypeDescriptor contentType = (SimpleTypeDescriptor) content.getTypeDescriptor();
    assertEquals("string", contentType.getPrimitiveType().getName());
  }

}

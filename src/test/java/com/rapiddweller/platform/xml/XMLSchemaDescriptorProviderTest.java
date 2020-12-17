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

import java.util.List;

import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.DefaultBeneratorContext;
import com.rapiddweller.benerator.file.XMLFileGenerator;
import com.rapiddweller.benerator.util.GeneratorUtil;
import com.rapiddweller.commons.IOUtil;
import com.rapiddweller.model.data.AlternativeGroupDescriptor;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.ComponentDescriptor;
import com.rapiddweller.model.data.PartDescriptor;
import com.rapiddweller.model.data.SimpleTypeDescriptor;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests the {@link XMLSchemaDescriptorProvider}.<br/><br/>
 * Created: 26.02.2008 21:05:23
 * @since 0.5.0
 * @author Volker Bergmann
 */
public class XMLSchemaDescriptorProviderTest {
    
	private static final String BASE = "com/rapiddweller/platform/xml/";
	
    private static final String SIMPLE_ELEMENT_TEST_FILE = BASE + "simple-element-test.xsd";
    private static final String NESTING_TEST_FILE = BASE + "nesting-test.xsd";
    private static final String ANNOTATION_TEST_FILE = BASE + "annotation-test.xsd";
    private static final String CHOICE_TEST_FILE = BASE + "choice-test.xsd";

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
    
    // helpers ---------------------------------------------------------------------------------------------------------

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

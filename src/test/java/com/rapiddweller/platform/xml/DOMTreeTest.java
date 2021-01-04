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

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.DefaultBeneratorContext;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.common.xml.XMLUtil;
import com.rapiddweller.common.xml.XPathUtil;
import com.rapiddweller.format.DataContainer;
import com.rapiddweller.format.DataIterator;
import com.rapiddweller.format.DataSource;
import com.rapiddweller.format.util.DataIteratorTestCase.NextHelper;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.Entity;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * Tests the DOMTree.<br/><br/>
 * Created: 14.01.2014 17:58:07
 *
 * @author Volker Bergmann
 * @since 0.9.0
 */

public class DOMTreeTest {

    @Test
    public void testConstructor() {
        DOMTree actualDomTree = new DOMTree();
        assertNull(actualDomTree.getDataModel());
        assertNull(actualDomTree.getInputUri());
        assertTrue(actualDomTree.isNamespaceAware());
        assertNull(actualDomTree.getId());
        assertEquals(0, actualDomTree.getTypeDescriptors().length);
        assertNull(actualDomTree.getOutputUri());
    }

    @Test
    public void testQueryEntities() throws Exception {
        BeneratorContext context = new DefaultBeneratorContext();
        DOMTree tree = new DOMTree("com/rapiddweller/platform/xml/teamplayers.xml", context);
        String outputUri = "target/test-classes/teamplayers2.xml";
        tree.setOutputUri(outputUri);
        // query all persons
        DataSource<Entity> source = tree.queryEntities("person", "//person", context);
        // verify all persons
        DataIterator<Entity> iterator = source.iterator();
        ComplexTypeDescriptor type = (ComplexTypeDescriptor) tree.getTypeDescriptor("person");
        expectNextElements(iterator,
                new Entity(type, "pnum", "1a", "name", "Alice", "city", "Atlanta", "age", "23"),
                new Entity(type, "pnum", "1b", "name", "Bob", "city", "Boston", "age", "34"),
                new Entity(type, "pnum", "2a", "name", "Charly", "city", "Cleveland", "age", "45"),
                new Entity(type, "pnum", "2b", "name", "Otto", "city", "Oslo", "age", "89")
        );
        assertNull(iterator.next(new DataContainer<>()));
        iterator.close();

        // set all cities to Berlin and update the DOMTree
        iterator = source.iterator();
        DataContainer<Entity> container = new DataContainer<>();
        while ((container = iterator.next(container)) != null) {
            Entity person = container.getData();
            person.set("city", "Berlin");
            tree.update(person);
        }
        iterator.close();

        // close the tree (saving the modified XML)
        IOUtil.close(tree);

        // verify that the tree has been modified and saved correctly
        Document doc2 = XMLUtil.parse(outputUri);
        NodeList cityNodes = XPathUtil.queryNodes(doc2, "//city/text()");
        assertEquals(4, cityNodes.getLength());
        for (int i = 0; i < 4; i++)
            assertEquals("Berlin", cityNodes.item(i).getTextContent());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    public void testQueryAttributes() {
        BeneratorContext context = new DefaultBeneratorContext();
        DOMTree tree = new DOMTree("com/rapiddweller/platform/xml/teamplayers.xml", context);
        tree.setOutputUri("target/test-classes/teamplayers3.xml");
        DataSource<?> source = tree.query("//city", true, context);
        DataIterator iterator = source.iterator();
        expectNextElements(iterator, "Atlanta", "Boston", "Cleveland", "Oslo");
        assertNull(iterator.next(new DataContainer<Entity>()));
        IOUtil.close(tree);
    }


    // private helpers -------------------------------------------------------------------------------------------------

    @SafeVarargs
    private static <T> NextHelper expectNextElements(DataIterator<T> iterator, T... expectedValues) {
        for (T expectedValue : expectedValues) {
            Object actualValue = iterator.next(new DataContainer<>()).getData();
            assertEquals(expectedValue, actualValue);
        }
        return new NextHelper(iterator);
    }

}

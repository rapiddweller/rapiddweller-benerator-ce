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

package com.rapiddweller.benerator.main;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import com.rapiddweller.common.xml.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Tests the {@link XmlCreator}.<br/><br/>
 * Created at 05.05.2008 16:53:29
 * @since 0.5.3
 * @author Volker Bergmann
 */
public class XmlCreatorTest {

	private static final String SIMPLE_ELEMENT_FILE = "com/rapiddweller/platform/xml/simple-element-test.xsd";
	private static final String ENUM_FILE = "com/rapiddweller/platform/xml/enum-test.xsd";
	private static final String CARDINALITY_FILE = "com/rapiddweller/platform/xml/cardinality-test.xsd";

	// tests -----------------------------------------------------------------------------------------------------------

	@Test
	public void testSimpleTypeElement() throws IOException {
        createXMLFile(SIMPLE_ELEMENT_FILE, "root", "target/simpleType.xml");
    }
	
	@Test
	public void testEnum() throws IOException {
		for (int i = 0; i < 10; i++) {
	        Document doc = createXMLFile(ENUM_FILE, "address", "target/enum.xml");
	        Element address = doc.getDocumentElement();
	        String box = address.getAttribute("box");
	        assertTrue("".equals(box) || "0203".equals(box));
		}
    }

	@Test
	public void testCardinalities() throws IOException {
		for (int i = 0; i < 10; i++) {
	        Document doc = createXMLFile(CARDINALITY_FILE, "outer", "target/cardinalities.xml");
	        Element outer = doc.getDocumentElement();
	        Element[] inners = XMLUtil.getChildElements(outer);
	        assertTrue("Expected 3-5 inners, found: " + inners.length, inners.length >= 3 && inners.length <= 5);
		}
	}
	
	// helpers ---------------------------------------------------------------------------------------------------------
	
    private static Document createXMLFile(String schemaUri, String root, String filename) throws IOException {
    	String[] args = new String[] { schemaUri, root, filename, "1" };
        XmlCreator.main(args);
        return XMLUtil.parse(filename);
    }
    
}

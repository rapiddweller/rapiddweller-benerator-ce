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

package com.rapiddweller.benerator.parser.xml;

import com.rapiddweller.benerator.parser.ModelParser;
import com.rapiddweller.benerator.test.GeneratorTest;
import com.rapiddweller.common.xml.XMLUtil;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.ItemListDescriptor;
import org.junit.Test;
import org.w3c.dom.Element;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Tests the {@link ItemListParser}.<br/><br/>
 * @author Alexander Kell
 */
public class ItemListParserTest extends GeneratorTest {

  private ItemListParser parser() {
    return new ItemListParser(new ModelParser(context, false));
  }

  @Test
  public void testParseListWithItems() {
    ComplexTypeDescriptor owner = new ComplexTypeDescriptor("owner", testDescriptorProvider);
    Element list = XMLUtil.parseStringAsElement(
        "<list name='items'>"
        + "<item><attribute name='a' type='string'/></item>"
        + "<item><attribute name='b' type='int'/></item>"
        + "</list>");
    ItemListDescriptor result = parser().parse(list, owner);
    assertNotNull(result);
    assertEquals("items", result.getName());
    // the list has been registered as a component of its owner
    assertNotNull(owner.getComponent("items"));
    // one item element descriptor per <item>
    ComplexTypeDescriptor localType = (ComplexTypeDescriptor) result.getLocalType();
    assertEquals(2, localType.getComponents().size());
  }

  @Test
  public void testNonItemChildRejected() {
    ComplexTypeDescriptor owner = new ComplexTypeDescriptor("owner", testDescriptorProvider);
    Element list = XMLUtil.parseStringAsElement("<list name='items'><foo/></list>");
    try {
      parser().parse(list, owner);
      fail("expected configuration error for non-item child");
    } catch (RuntimeException e) {
      // expected
    }
  }

  @Test
  public void testUnknownItemContentRejected() {
    ComplexTypeDescriptor owner = new ComplexTypeDescriptor("owner", testDescriptorProvider);
    Element list = XMLUtil.parseStringAsElement("<list name='items'><item><bogus/></item></list>");
    try {
      parser().parse(list, owner);
      fail("expected configuration error for unknown item content");
    } catch (RuntimeException e) {
      // expected
    }
  }

}

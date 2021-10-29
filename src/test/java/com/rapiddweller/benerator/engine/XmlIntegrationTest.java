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

package com.rapiddweller.benerator.engine;

import com.rapiddweller.benerator.test.AbstractBeneratorIntegrationTest;
import com.rapiddweller.common.CollectionUtil;
import com.rapiddweller.common.xml.XMLUtil;
import com.rapiddweller.common.xml.XPathUtil;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.util.Set;

import static org.junit.Assert.assertTrue;

/**
 * Integration-tests Benerator's XML features.<br/><br/>
 * Created: 14.01.2014 10:09:40
 * @author Volker Bergmann
 * @since 0.9.0
 */
public class XmlIntegrationTest extends AbstractBeneratorIntegrationTest {

  // TODO this fails because no file is generated - but only in CI build. Find out why and fix it
  @Test @Ignore
  public void testAnonymization() throws Exception {
    parseAndExecuteFile("com/rapiddweller/benerator/engine/xml/anonymize-xml.ben.xml");
    resourceManager.close();

    Set<String> anonNames = CollectionUtil.toSet("Michael", "Maria", "Miles", "Manfred");
    Set<String> anonCities = CollectionUtil.toSet("Munich", "Michigan", "Madrid", "Milano");
    Document document = XMLUtil.parse("target/test-classes/teamplayers-anon.xml");
    NodeList names = XPathUtil.queryNodes(document, "//name/text()");
    for (int i = 0; i < names.getLength(); i++) {
      String name = names.item(i).getTextContent();
      assertTrue("Not an anonymized name: " + name, anonNames.contains(name));
    }
    NodeList cities = XPathUtil.queryNodes(document, "//city/text()");
    for (int i = 0; i < cities.getLength(); i++) {
      String city = cities.item(i).getTextContent();
      assertTrue("not an anonymized city: " + city, anonCities.contains(city));
    }
  }

}

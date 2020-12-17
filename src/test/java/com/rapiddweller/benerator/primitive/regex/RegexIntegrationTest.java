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

package com.rapiddweller.benerator.primitive.regex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

import com.rapiddweller.benerator.test.BeneratorIntegrationTest;
import com.rapiddweller.benerator.test.ConsumerMock;
import com.rapiddweller.model.data.Entity;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the integration of regex-based string generation.<br/><br/>
 * Created: 04.04.2014 13:36:25
 * @since 0.9.3
 * @author Volker Bergmann
 */

public class RegexIntegrationTest extends BeneratorIntegrationTest {

	private ConsumerMock consumer;
	
	@Before
	public void setUpContext() {
		consumer = new ConsumerMock(true);
		context.setGlobal("cons", consumer);
	}

	
	
	// test methods ----------------------------------------------------------------------------------------------------
	
	@Test
	public void testShortRegexGeneration() {
		String regex = "[A-Z][A-Z ]{5,23}[A-Z]";
		// create by regex from XML descriptor
		parseAndExecute(
			"<generate type='entity' count='100' consumer='cons'>" +
        	"  <attribute name='text' pattern='" + regex + "'/>" +
        	"</generate>");
		@SuppressWarnings("unchecked")
		List<Entity> products = (List<Entity>) consumer.getProducts();
		for (Entity product : products) {
			assertTrue(Pattern.matches(regex, (String) product.get("text")));
		}
		assertEquals(100, products.size());
	}
	
	@Test
	public void testLongRegexGeneration() {
		String regex = "[A-Z][A-Z ]{100,2000}[A-Z]";
		// create by regex from XML descriptor
		parseAndExecute(
			"<generate type='entity' count='100' consumer='cons'>" +
        	"  <attribute name='text' pattern='" + regex + "'/>" +
        	"</generate>");
		@SuppressWarnings("unchecked")
		List<Entity> products = (List<Entity>) consumer.getProducts();
		for (Entity product : products) {
			String text = (String) product.get("text");
			System.out.println(text);
			assertTrue(Pattern.matches(regex, text));
		}
		assertEquals(100, products.size());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testUniqueRegexGeneration() {
		String regex = "[A-Z][A-Z ]{10,12}[A-Z]";
		// create by regex from XML descriptor
		parseAndExecute(
			"<generate type='entity' count='100' consumer='cons'>" +
        	"  <attribute name='text' pattern='" + regex + "' unique='true' />" +
        	"</generate>");
		List<Entity> products = (List<Entity>) consumer.getProducts();
		assertEquals(100, products.size());
		HashSet<String> uniqueTexts = new HashSet<String>();
		for (Entity product : products) {
			String text = (String) product.get("text");
			uniqueTexts.add(text);
		}
		assertEquals("Generation was not unique", 100, uniqueTexts.size());
	}
	
}
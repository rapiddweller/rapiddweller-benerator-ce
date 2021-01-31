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

package com.rapiddweller.benerator.engine.parser.xml;

import static org.junit.Assert.*;

import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.test.BeneratorIntegrationTest;
import org.junit.Test;

/**
 * Tests the {@link BeanParser}.<br/><br/>
 * Created: 30.10.2009 19:02:25
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class BeanParserAndStatementTest extends BeneratorIntegrationTest {

	@Test
    public void testParseBeanClass() {
		String xml = "<bean id='id' class='" + BeanMock.class.getName() + "' />";
		BeneratorContext context = parseAndExecute(xml);
		Object bean = context.get("id");
		assertNotNull(bean);
		assertEquals(BeanMock.class, bean.getClass());
		assertEquals(0, ((BeanMock) bean).lastValue);
		assertNotNull(((BeanMock) bean).getContext());
	}

	@Test
	public void testParseBeanSpec() {
		String xml = "<bean id='id' spec='new " + BeanMock.class.getName() + "(2)' />";
		BeneratorContext context = parseAndExecute(xml);
		Object bean = context.get("id");
		assertNotNull(bean);
		assertEquals(BeanMock.class, bean.getClass());
		assertEquals(2, ((BeanMock) bean).lastValue);
		assertNotNull(((BeanMock) bean).getContext());
	}
	
	@Test
	public void testStringProperty() {
		String xml = 
				"<bean id='id' class='" + BeanMock.class.getName() + "'>" +
				"	<property name='text' value='xxx' />" + 
				"</bean>";
		BeneratorContext context = parseAndExecute(xml);
		BeanMock bean = (BeanMock) context.get("id");
		assertEquals("xxx", bean.getText());
	}
	
	@Test
	public void testEmptyStringProperty() {
		String xml = 
				"<bean id='id' class='" + BeanMock.class.getName() + "'>" +
				"	<property name='text' value='' />" + 
				"</bean>";
		BeneratorContext context = parseAndExecute(xml);
		BeanMock bean = (BeanMock) context.get("id");
		assertEquals("", bean.getText());
	}
	
}

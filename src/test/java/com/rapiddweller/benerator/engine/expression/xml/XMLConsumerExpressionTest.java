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

package com.rapiddweller.benerator.engine.expression.xml;

import static org.junit.Assert.*;

import com.rapiddweller.benerator.factory.ConsumerMock;
import com.rapiddweller.benerator.consumer.ConsumerChain;
import com.rapiddweller.benerator.consumer.ConsumerProxy;
import com.rapiddweller.benerator.consumer.NonClosingConsumerProxy;
import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.DefaultBeneratorContext;
import com.rapiddweller.benerator.engine.ResourceManagerSupport;
import com.rapiddweller.commons.xml.XMLUtil;
import org.junit.Test;
import org.w3c.dom.Document;

/**
 * Tests the {@link XMLConsumerExpression}.<br/><br/>
 * Created: 16.02.2010 11:41:13
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class XMLConsumerExpressionTest {
	
	@Test
	public void testInlineConsumerClass() throws Exception {
		Document doc = XMLUtil.parseString("<generate " +
				"consumer='com.rapiddweller.benerator.factory.ConsumerMock'/>");
		XMLConsumerExpression expression = new XMLConsumerExpression(doc.getDocumentElement(), true, 
				new ResourceManagerSupport());
		ConsumerMock consumerMock = (ConsumerMock) expression.evaluate(new DefaultBeneratorContext());
		assertNotNull("Context not set", consumerMock.context);
	}
	
	@Test
	public void testInlineConsumerSpec() throws Exception {
		Document doc = XMLUtil.parseString("<generate " +
				"consumer='new com.rapiddweller.benerator.factory.ConsumerMock(2)'/>");
		XMLConsumerExpression expression = new XMLConsumerExpression(doc.getDocumentElement(), true, 
				new ResourceManagerSupport());
		ConsumerMock consumerMock = (com.rapiddweller.benerator.factory.ConsumerMock) expression.evaluate(new DefaultBeneratorContext());
		assertNotNull("Context not set", consumerMock.context);
		assertEquals(2, consumerMock.id);
	}
	
	@Test
	public void testConsumerBean() throws Exception {
		Document doc = XMLUtil.parseString("<generate>" +
				"    <consumer spec='new com.rapiddweller.benerator.factory.ConsumerMock()'/>" +
				"</generate>");
		XMLConsumerExpression expression = new XMLConsumerExpression(doc.getDocumentElement(), true, 
				new ResourceManagerSupport());
		ConsumerMock consumerMock = (ConsumerMock) expression.evaluate(new DefaultBeneratorContext());
		assertNotNull("Context not set", consumerMock.context);
	}
	
    @Test
	public void testConsumerBeanRef() throws Exception {
		Document doc = XMLUtil.parseString("<generate>" +
				"    <consumer ref='myc'/>" +
				"</generate>");
		XMLConsumerExpression expression = new XMLConsumerExpression(doc.getDocumentElement(), true, 
				new ResourceManagerSupport());
		BeneratorContext context = new DefaultBeneratorContext();
		context.setGlobal("myc", new ConsumerMock(3));
		NonClosingConsumerProxy consumer = (NonClosingConsumerProxy) expression.evaluate(context);
		ConsumerMock consumerMock = (ConsumerMock) consumer.getTarget();
		assertEquals(3, consumerMock.id);
	}
	
	@Test
	public void testInlineConsumerList() throws Exception {
		Document doc = XMLUtil.parseString("<generate " +
			"consumer='myc,new com.rapiddweller.benerator.factory.ConsumerMock(5)'/>");
		XMLConsumerExpression expression = new XMLConsumerExpression(doc.getDocumentElement(), true, 
				new ResourceManagerSupport());
		BeneratorContext context = new DefaultBeneratorContext();
		context.setGlobal("myc", new ConsumerMock());
		ConsumerChain consumerChain = (ConsumerChain) expression.evaluate(context);
		ConsumerMock consumerMock = (ConsumerMock) ((ConsumerProxy) consumerChain.getComponent(0)).getTarget();
		assertEquals(1, consumerMock.id);
		assertEquals(2, consumerChain.componentCount());
		ConsumerMock component2 = (ConsumerMock) consumerChain.getComponent(1);
		assertEquals(5, component2.id);
	}
	
}

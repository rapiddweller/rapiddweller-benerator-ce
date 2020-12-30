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

package com.rapiddweller.benerator.engine.expression;

import static org.junit.Assert.*;

import com.rapiddweller.benerator.engine.DefaultBeneratorContext;
import com.rapiddweller.common.Context;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the {@link ScriptableExpression}.<br/><br/>
 * Created: 06.08.2011 20:28:51
 * @since 0.7.0
 * @author Volker Bergmann
 */
public class ScriptableExpressionTest {

	private Context context;

	@Before
	public void setUpContext() {
		this.context = new DefaultBeneratorContext();
		this.context.set("user", "myself");
	}
	
	@Test
	public void testEmpty() {
		ScriptableExpression expression = new ScriptableExpression(null, null);
		assertEquals(null, expression.evaluate(context));
	}
	
	@Test
	public void testDefault() {
		ScriptableExpression expression = new ScriptableExpression(null, "Hi there");
		assertEquals("Hi there", expression.evaluate(context));
	}
	
	@Test
	public void testText() {
		ScriptableExpression expression = new ScriptableExpression("Hello World", null);
		assertEquals("Hello World", expression.evaluate(context));
	}
	
	@Test
	public void testScript() {
		ScriptableExpression expression = new ScriptableExpression("{'Hi ' + user}", "???");
		assertEquals("Hi myself", expression.evaluate(context));
	}
	
}

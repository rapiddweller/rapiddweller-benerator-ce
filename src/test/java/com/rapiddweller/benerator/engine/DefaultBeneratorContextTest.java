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

import static org.junit.Assert.*;

import java.util.Locale;

import com.rapiddweller.common.BeanUtil;
import com.rapiddweller.common.SystemInfo;
import com.rapiddweller.domain.address.Country;
import com.rapiddweller.format.DataContainer;
import com.rapiddweller.format.script.ScriptUtil;
import com.rapiddweller.format.xls.XLSLineIterator;
import org.junit.Test;

/**
 * Tests the {@link BeneratorContext}.<br/><br/>
 * Created: 31.03.2010 15:15:07
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class DefaultBeneratorContextTest {

	private static final String OFF_CLASSPATH_RESOURCES_FOLDER = "src/test/offCpResources";

	@Test
	public void testDefaults() {
		BeneratorContext context = new DefaultBeneratorContext();
		assertEquals(".", context.getContextUri());
		assertEquals(Country.getDefault().getIsoCode(), context.getDefaultDataset());
		assertEquals("fatal", context.getDefaultErrorHandler());
		assertEquals(SystemInfo.getLineSeparator(), context.getDefaultLineSeparator());
		assertEquals(Locale.getDefault(), context.getDefaultLocale());
		assertEquals(1, context.getDefaultPageSize());
		assertEquals("ben", context.getDefaultScript());
		assertEquals("ben", ScriptUtil.getDefaultScriptEngine());
		assertEquals(',', context.getDefaultSeparator());
		assertEquals(null, context.getMaxCount());
		context.close();
	}
	
	@Test
	public void testSysPropAccess() {
		BeneratorContext context = new DefaultBeneratorContext();
		assertEquals(System.getProperty("user.name"), context.get("user.name"));
		context.close();
	}
	
	@Test
	public void testClassInJarInLibFolder() {
		BeneratorContext context = new DefaultBeneratorContext(OFF_CLASSPATH_RESOURCES_FOLDER);
		Class<?> testClassInJar = context.forName("com.my.TestClassInJar");
		Object o = BeanUtil.newInstance(testClassInJar);
		assertEquals("staticMethodInJar called", BeanUtil.invoke(testClassInJar, "staticMethodInJar"));
		assertEquals("instanceMethodInJar called", BeanUtil.invoke(o, "instanceMethodInJar"));
		context.close();
	}
	
	@Test
	public void testResourceInJarInLibFolder() throws Exception {
		String XLS_RESOURCE_NAME = "xls/xls_in_jar.xls";
		BeneratorContext context = new DefaultBeneratorContext(OFF_CLASSPATH_RESOURCES_FOLDER);
		String resourceUri = context.resolveRelativeUri(XLS_RESOURCE_NAME);
		XLSLineIterator iterator = new XLSLineIterator(resourceUri);
		assertArrayEquals(new Object[] { "name", "age" }, iterator.next(new DataContainer<Object[]>()).getData());
		assertArrayEquals(new Object[] { "Alice", 23L }, iterator.next(new DataContainer<Object[]>()).getData());
		assertArrayEquals(new Object[] { "Bob", 34L }, iterator.next(new DataContainer<Object[]>()).getData());
		assertNull(iterator.next(new DataContainer<Object[]>()));
		context.close();
	}
	
	@Test
	public void testClassFileInLibFolder() {
		BeneratorContext context = new DefaultBeneratorContext(OFF_CLASSPATH_RESOURCES_FOLDER);
		Class<?> testClassInJar = context.forName("com.my.TestClassInPath");
		Object o = BeanUtil.newInstance(testClassInJar);
		assertEquals("staticMethodInPath called", BeanUtil.invoke(testClassInJar, "staticMethodInPath"));
		assertEquals("instanceMethodInPath called", BeanUtil.invoke(o, "instanceMethodInPath"));
		context.close();
	}
	
}

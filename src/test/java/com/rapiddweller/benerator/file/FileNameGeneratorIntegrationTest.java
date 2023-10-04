/* (c) Copyright 2022 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.file;

import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.test.AbstractBeneratorIntegrationTest;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.platform.memstore.MemStore;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Integration test of the {@link FileNameGenerator}.<br/><br/>
 * Created: 07.08.2022 17:13:13
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class FileNameGeneratorIntegrationTest extends AbstractBeneratorIntegrationTest {

	private static final String XML = "<setup xmlns='https://www.benerator.de/schema/3.0.0'>\n" +
		"    <memstore id='mem'/>\n" +
		"    <generate type='file' count='100' consumer='mem'>\n" +
		"        <attribute name='path' generator=\"new FileNameGenerator('src/main/java', null, '{pathType}', true, true, false)\"/>\n" +
		"    </generate>\n" +
		"</setup>";

	@Test
	public void testLocalPath() {
		BeneratorContext context = parseAndExecuteXmlString(XML.replace("{pathType}", "local"));
		MemStore mem = (MemStore) context.get("mem");
		List<Entity> files = mem.getEntities("file");
		assertEquals(100, files.size());
		for (Entity file : files) {
			System.out.println(file);
			String path = (String) file.get("path");
			assertNotNull(path);
			assertFalse(path.contains("/"));
			assertFalse(path.contains("\\"));
			//assertTrue(path.endsWith(".java") || path.endsWith(".html"));
		}
	}

	@Test
	public void testCanonicalPath() {
		BeneratorContext context = parseAndExecuteXmlString(XML.replace("{pathType}", "canonical"));
		MemStore mem = (MemStore) context.get("mem");
		List<Entity> files = mem.getEntities("file");
		assertEquals(100, files.size());
		for (Entity file : files) {
			System.out.println(file);
			String path = (String) file.get("path");
			assertNotNull(path);
			assertTrue(path.contains("src/main/java") || path.contains("src\\main\\java"));
			assertTrue(path.contains("/") || path.contains("\\"));
			//assertTrue(path.endsWith(".java") || path.endsWith(".html"));
		}
	}

	@Test
	public void testAbsolutePath() {
		BeneratorContext context = parseAndExecuteXmlString(XML.replace("{pathType}", "absolute"));
		MemStore mem = (MemStore) context.get("mem");
		List<Entity> files = mem.getEntities("file");
		assertEquals(100, files.size());
		for (Entity file : files) {
			System.out.println(file);
			String path = (String) file.get("path");
			assertNotNull(path);
			assertTrue(path.contains("src/main/java") || path.contains("src\\main\\java"));
			assertTrue(path.contains("/") || path.contains("\\"));
			//assertTrue(path.endsWith(".java") || path.endsWith(".html"));
		}
	}

}

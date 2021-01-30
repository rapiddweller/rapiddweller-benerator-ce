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

package com.rapiddweller.platform.csv;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.rapiddweller.benerator.test.GeneratorTest;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.common.ReaderLineIterator;
import com.rapiddweller.common.SystemInfo;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.model.data.SimpleTypeDescriptor;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests the {@link CSVEntityExporter}.<br/>
 * <br/>
 * Created at 14.03.2009 06:10:37
 * @since 0.5.8
 * @author Volker Bergmann
 */

public class CSVEntityExporterTest extends GeneratorTest {
	
	private static final File DEFAULT_FILE = new File("export.csv");
	
	private final File customFile = new File("target" + File.separator + getClass().getSimpleName() + ".csv");
	
	private ComplexTypeDescriptor descriptor;
	private Entity alice;
	private Entity bob;
	
	@Before
	public void setUp() {
		// create descriptor for 'Person' entities
		descriptor = createComplexType("Person", (ComplexTypeDescriptor) dataModel.getTypeDescriptor("entity"));
		descriptor.addComponent(createPart("name", dataModel.getTypeDescriptor("string")));
		descriptor.addComponent(createPart("age", dataModel.getTypeDescriptor("int")));
		descriptor.addComponent(createPart("notes", dataModel.getTypeDescriptor("string")));
		// create Person instances for testing
		alice = createEntity("Person", "name", "Alice", "age", 23, "notes", "");
		bob = createEntity("Person", "name", "Bob", "age", 34, "notes", null);
	}
	
	// tests -----------------------------------------------------------------------------------------------------------

	@Test
	public void testEmptyFile() {
		if (DEFAULT_FILE.exists())
			DEFAULT_FILE.delete();
		try {
			CSVEntityExporter exporter = new CSVEntityExporter();
			exporter.close();
			assertTrue(DEFAULT_FILE.exists());
			assertEquals(0, DEFAULT_FILE.length());
		} finally {
			DEFAULT_FILE.delete();
		}
	}
	
	/**
	 * Tests a bug
	 */
	@Test
	public void testEmptyFileWithEndWithNewLine() {
		if (DEFAULT_FILE.exists())
			DEFAULT_FILE.delete();
		try {
			CSVEntityExporter exporter = new CSVEntityExporter();
			exporter.setEndWithNewLine(true);
			exporter.close();
			assertTrue(DEFAULT_FILE.exists());
			assertEquals("\r\n".length(), DEFAULT_FILE.length());
		} finally {
			DEFAULT_FILE.delete();
		}
	}
	
	@Test
	public void testExplicitColumns() throws Exception {
		try {
			CSVEntityExporter exporter = new CSVEntityExporter(customFile.getAbsolutePath(), "name");
			cosumeAndClose(exporter);
			assertEquals("name\r\nAlice\r\nBob", getContent(customFile));
		} finally {
			customFile.delete();
		}
	}

	@Test
	public void testEndWithNewLine() throws Exception {
		try {
			CSVEntityExporter exporter = new CSVEntityExporter(customFile.getAbsolutePath(), "name");
			exporter.setEndWithNewLine(true);
			cosumeAndClose(exporter);
			assertEquals("name\r\nAlice\r\nBob\r\n", getContent(customFile));
		} finally {
			customFile.delete();
		}
	}

	@Test
	public void testHeadless() throws Exception {
		try {
			CSVEntityExporter exporter = new CSVEntityExporter(customFile.getAbsolutePath(), "name");
			exporter.setHeadless(true);
			cosumeAndClose(exporter);
			assertEquals("Alice\r\nBob", getContent(customFile));
		} finally {
			customFile.delete();
		}
	}
	
	@Test
	public void testAppend() throws Exception {
		if (SystemInfo.isWindows()) { // TODO make this test work on Windows
			System.out.println("Skipping testAppend()");
			return;
		}
		try {
			CSVEntityExporter exporter = new CSVEntityExporter(customFile.getAbsolutePath(), "name");
			exporter.setAppend(true);
			cosumeAndClose(exporter);
			CSVEntityExporter exporter2 = new CSVEntityExporter(customFile.getAbsolutePath(), "name");
			exporter2.setAppend(true);
			cosumeAndClose(exporter2);
			assertEquals("name\r\nAlice\r\nBob\r\nAlice\r\nBob", getContent(customFile));
		} finally {
			customFile.delete();
		}
	}

	@Test
	public void testColumnsByDescriptor() throws Exception {
		try {
			CSVEntityExporter exporter = new CSVEntityExporter(customFile.getAbsolutePath(), descriptor);
			cosumeAndClose(exporter);
			assertEquals("name,age,notes\r\nAlice,23,\r\nBob,34,", getContent(customFile));
		} finally {
			customFile.delete();
		}
	}

	@Test
	public void testColumnsByInstance() throws Exception {
		try {
			CSVEntityExporter exporter = new CSVEntityExporter();
			cosumeAndClose(exporter);
			assertEquals("name,age,notes\r\nAlice,23,\"\"\r\nBob,34,", getContent(DEFAULT_FILE));
		} finally {
			DEFAULT_FILE.delete();
		}
	}
	
	@Test
	public void testEmptyAndNull_default() throws Exception {
		try {
			CSVEntityExporter exporter = new CSVEntityExporter();
			cosumeAndClose(exporter);
			assertEquals("name,age,notes\r\nAlice,23,\"\"\r\nBob,34,", getContent(DEFAULT_FILE));
		} finally {
			DEFAULT_FILE.delete();
		}
	}
	
	@Test
	public void testEmptyAndNull_quoteEmpty() throws Exception {
		try {
			CSVEntityExporter exporter = new CSVEntityExporter();
			exporter.setQuoteEmpty(true);
			cosumeAndClose(exporter);
			assertEquals("name,age,notes\r\nAlice,23,\"\"\r\nBob,34,", getContent(DEFAULT_FILE));
		} finally {
			DEFAULT_FILE.delete();
		}
	}
	
	@Test
	public void testEmptyAndNull_dontQuoteEmpty() throws Exception {
		try {
			CSVEntityExporter exporter = new CSVEntityExporter();
			exporter.setQuoteEmpty(false);
			cosumeAndClose(exporter);
			assertEquals("name,age,notes\r\nAlice,23,\r\nBob,34,", getContent(DEFAULT_FILE));
		} finally {
			DEFAULT_FILE.delete();
		}
	}
	
	@Test
	public void testDecimalFormat() throws Exception {
		try {
			CSVEntityExporter exporter = new CSVEntityExporter();
			exporter.setDecimalPattern("0.00");
			exporter.setDecimalSeparator('-');
			Entity entity = createEntity("test", "value", 1.);
			exporter.startProductConsumption(entity);
			exporter.finishProductConsumption(entity);
			exporter.close();
			assertEquals("value\r\n1-00", getContent(DEFAULT_FILE));
		} finally {
			DEFAULT_FILE.delete();
		}
	}
	
	@Test
	public void testMultiThreaded() throws Exception {
		try {
			ComplexTypeDescriptor type = createComplexType("testtype");
			SimpleTypeDescriptor stringType = dataModel.getPrimitiveTypeDescriptor(String.class);
			type.addComponent(createPart("a", stringType));
			type.addComponent(createPart("b", stringType));
			type.addComponent(createPart("c", stringType));
			final CSVEntityExporter exporter = new CSVEntityExporter(
					DEFAULT_FILE.getAbsolutePath(), type);
			final Entity entity = new Entity(type, "a", "0123456789", "b", "5555555555", "c", "9876543210");
			ExecutorService service = Executors.newCachedThreadPool();
			Runnable runner = () -> {
				for (int i = 0; i < 500; i++)
					exporter.startProductConsumption(entity);
				exporter.finishProductConsumption(entity);
			};
			for (int i = 0; i < 20; i++)
				service.execute(runner);
			service.shutdown();
			service.awaitTermination(2, TimeUnit.SECONDS);
			exporter.close();
			ReaderLineIterator iterator = new ReaderLineIterator(new FileReader(DEFAULT_FILE));
			assertEquals("a,b,c", iterator.next());
			String expectedContent = "0123456789,5555555555,9876543210";
			while (iterator.hasNext()) {
				String line = iterator.next();
				assertEquals(expectedContent, line);
			}
			iterator.close();
		} finally {
			DEFAULT_FILE.delete();
		}
	}
	
	// helper methods --------------------------------------------------------------------------------------------------

	private void cosumeAndClose(CSVEntityExporter exporter) {
	    exporter.startProductConsumption(alice);
        exporter.finishProductConsumption(alice);
	    exporter.startProductConsumption(bob);
        exporter.finishProductConsumption(alice);
	    exporter.close();
    }

	private static String getContent(File file) throws IOException {
	    return IOUtil.getContentOfURI(file.getAbsolutePath());
    }

}

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

package com.rapiddweller.benerator.archetype;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URL;

import com.rapiddweller.benerator.BeneratorFactory;
import com.rapiddweller.commons.ArrayUtil;
import com.rapiddweller.commons.FileUtil;
import com.rapiddweller.commons.StringUtil;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests the {@link Archetype} class.<br/><br/>
 * Created: 02.04.2010 12:35:46
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class ArchetypeTest {

	@Ignore
	@Test
	public void testSimpleProjectArchetype() throws Exception {
		// prepare
		Archetype simple = new Archetype(new URL(ArchetypeManager.ARCHETYPE_FOLDER_URL.toString() + "/simple"));
		File targetFolder = new File("target/simple");
		FileUtil.deleteIfExists(targetFolder);
		
		// run
		simple.copyFilesTo(targetFolder, new EclipseFolderLayout());
		
		// verify
		String[] createdFiles = targetFolder.list();
		// check that benerator.xml was copied from the archetype
		assertTrue(ArrayUtil.contains("benerator.xml", createdFiles));
		// check that benerator xsd was copied from the classpath
		String schemaPath = StringUtil.splitOnLastSeparator(BeneratorFactory.getSchemaPathForCurrentVersion(), '/')[1];
		assertTrue("File not found: " + schemaPath, ArrayUtil.contains(schemaPath, createdFiles));
		// check that src/main/resources is mapped to src in Eclipse projects
		File src = new File(targetFolder, "src");
		assertTrue(src.exists());
		assertTrue(src.isDirectory());
		assertTrue(ArrayUtil.contains("log4j.xml", src.list())); 
		// check that ARCHETYPE-INF was not copied
		assertFalse(ArrayUtil.contains("ARCHETYPE-INF", createdFiles));
	}
	
}

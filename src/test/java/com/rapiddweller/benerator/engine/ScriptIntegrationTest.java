/*
 * (c) Copyright 2006-2022 by rapiddweller GmbH & Volker Bergmann. All rights reserved.
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
import com.rapiddweller.common.exception.ApplicationException;
import com.rapiddweller.platform.memstore.MemStore;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Integration test for Benerator's Script functionality.<br/><br/>
 * Created at 30.12.2020
 * @author Alexander Kell
 * @author Volker Bergmann
 * @since 1.1.0
 */
public class ScriptIntegrationTest extends AbstractBeneratorIntegrationTest {

  @Test
  public void scriptInFile() throws IOException {
    context.setContextUri("/com/rapiddweller/benerator/engine/script");
    parseAndExecuteFile("/com/rapiddweller/benerator/engine/script/scriptfile.ben.xml");
    MemStore mem = (MemStore) context.get("mem");
    assertEquals(2, mem.entityCount("person"));
    assertEquals(5, mem.entityCount("script"));
  }

  @Test
  public void scriptInCode() throws IOException {
    context.setContextUri("/com/rapiddweller/benerator/engine/script");
    parseAndExecuteFile("/com/rapiddweller/benerator/engine/script/scriptcode.ben.xml");
    MemStore mem = (MemStore) context.get("mem");
    assertEquals(2, mem.entityCount("person"));
    assertEquals(5, mem.entityCount("script"));
  }

  @Test
  public void testAccessSimpleTypeAsArray() {
    String fileName = "simpletype_as_array_error.ben.xml";
    try {
      parseAndExecuteFile(getClass().getPackageName().replace('.', '/') + "/" + fileName);
    } catch (ApplicationException e) {
      assertEquals("Cannot do index-based access on Integer. Script text: 'myvar[0]' in  file file:///Users/volker/IdeaProjects/rapiddweller-benerator-ce/com/rapiddweller/benerator/engine/simpletype_as_array_error.ben.xml line 4 column 51", e.getMessage());
      assertEquals("BEN-0120", e.getErrorId());
      return;
    }
    fail("Expected exception is missing");
  }

}

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

import com.rapiddweller.benerator.test.BeneratorIntegrationTest;
import com.rapiddweller.benerator.test.ConsumerMock;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.model.data.Entity;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests the &lt;defaultComponents&gt; element.<br/><br/>
 * Created: 23.05.2011 09:37:37
 *
 * @author Volker Bergmann
 * @since 0.6.6
 */
public class DefaultComponentIntegrationTest extends BeneratorIntegrationTest {

  /**
   * Test standard integration.
   *
   * @throws Exception the exception
   */
  @Test
  public void testStandardIntegration() throws Exception {
    checkFile("com/rapiddweller/benerator/engine/defaultComponent-std.ben.xml");
  }

  /**
   * Test db integration.
   *
   * @throws Exception the exception
   */
  @Test
  public void testDbIntegration() throws Exception {
    checkFile("com/rapiddweller/benerator/engine/defaultComponent-db.ben.xml");
  }

  /**
   * Check file.
   *
   * @param uri the uri
   * @throws IOException the io exception
   */
  @SuppressWarnings("unchecked")
  public void checkFile(String uri) throws IOException {
    ConsumerMock consumer = new ConsumerMock(true);
    context.setGlobal("cons", consumer);
    DescriptorRunner runner = new DescriptorRunner(uri, context);
    try {
      runner.run();
      List<Entity> products = (List<Entity>) consumer.getProducts();
      long currentMillies = System.currentTimeMillis();
      for (Entity product : products) {
        // check created_by
        String createdBy = (String) product.get("created_by");
        assertEquals("Bob", createdBy);
        // check created_at
        Date creationDate = (Date) product.get("created_at");
        assertNotNull(creationDate);
        long productMillies = creationDate.getTime();
        assertTrue(Math.abs(productMillies - currentMillies) < 3000);
      }
    } finally {
      IOUtil.close(runner);
    }
  }

}

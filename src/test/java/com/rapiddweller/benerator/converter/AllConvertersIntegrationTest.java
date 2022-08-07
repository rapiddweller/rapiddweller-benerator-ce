/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.converter;

import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.test.AbstractBeneratorIntegrationTest;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.platform.memstore.MemStore;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Tests all converters in package com.rapiddweller.benerator.converter.<br/><br/>
 * Created: 27.10.2021 12:13:20
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class AllConvertersIntegrationTest extends AbstractBeneratorIntegrationTest {

  final String folder = getClass().getPackageName().replace('.', '/') + '/';

  @Test
  public void test() throws IOException {
    BeneratorContext context = parseAndExecuteFile(folder + "all_converters.ben.xml");
    MemStore mem = (MemStore) context.get("dst");
    List<Entity> entities = mem.getEntities("data");
    assertEquals(10, entities.size());
    for (Entity e : entities) {
      assertEquals("append_suffix", e.get("append"));
      assertEquals("cut", e.get("cutLength"));
      assertEquals("****", e.get("mask"));
      assertEquals("dd4dfe50", e.get("javaHash"));
      assertEquals("38BB196673F7136F814A22E7966E0992", e.get("md5hex"));
      assertEquals("4FGzi7jkY8wafXDNNb17AA==", e.get("md5base64"));
      assertEquals("70BC35FBAAD20FA7AD2676671A1FD240C7A02D09", e.get("sha1hex"));
      assertEquals("CVW2V525jRq0944XhmyDconFGq8=", e.get("sha1base64"));
      assertEquals("2F06688CE790BB2D831B33A21336C8214BBC81FC3902594D7E5692ED3DF5703D", e.get("sha256hex"));
      assertEquals("SoKU3QAKuzZw7nFbhPs7tNKRoKc=", e.get("sha256base64"));
      assertEquals("mi*****ask", e.get("middleMask"));
    }
  }
}

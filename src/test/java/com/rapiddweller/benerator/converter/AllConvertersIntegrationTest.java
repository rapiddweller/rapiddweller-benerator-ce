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
      assertEquals("6D932AE1A3EB66C4C16B2E84CBC63532B7A3EE551B2B6A86A121639A40B1A1C05BC238595B7B7BE7FD2CA92B54F0BC64C2081361DCE4F29C1E42342EA75457EA", e.get("sha512hex"));
      assertEquals("D21895BF51EB75DE835398DFF87040CB841A3E4E5567EBACBD28D93B0D5C58AF806F318DFA7916FF5BE25B4891D2F3506616E41E6E9E50F76836BBE4518F6AD4", e.get("sha512hex_salt"));
      assertEquals("3gQtIYsGs7ZHb+kmlrfPHsP2L6Tr+QgdECZujM/6ix60m1U8MnnLpf9ZeAF8ph1PQ46g2adn6cHSZ6lzeO4leQ==", e.get("sha512base64"));
      assertEquals("Tpw03jcL4t8bKk4ki3j5WO/w+1qbuOVEZqKyXvlKL5K2WCwQn0BYbaNa9DUFF0SpciTxDzQbBWDdtInLjT1p4g==", e.get("sha512base64_salt"));
      assertEquals("F7DCEE649C5FCF07B25F8E83D898C37DD6CA2630ED1F09009C9DD534483A13055CE829AD1BD9F39E5299E390B06A7BF34443ABA13BCD88820F1EC585DB1DAEE6", e.get("sha3_512hex"));
      assertEquals("8F0635D4234AA274686070198E36FFA6BD0F1D258E4968E6EF5CEE7690681449963CAE141E1750EDEE2C92EED50C50E5DEF0A5BB29FB970E0D44DA9AEB21381F", e.get("sha3_512hex_salt"));
      assertEquals("1VDeRb29k3petbzcqerOLs2Rw8UzxNBOgBfLAX7EdSuyfEae5ShLIOGD+fdFQ079zTgBMiKa2FR1J1bC4snMEw==", e.get("sha3_512base64"));
      assertEquals("YDTvOzrwLlV0ffkGtUrHAfx2NxbEm6u7jx//753CuyVksvf78VpCregyywirQVOOYUfO7OulSAYXdrFdUEblgA==", e.get("sha3_512base64_salt"));
    }
  }
}

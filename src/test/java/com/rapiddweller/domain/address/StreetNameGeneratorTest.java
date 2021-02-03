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

package com.rapiddweller.domain.address;

import com.rapiddweller.benerator.dataset.DatasetUtil;
import com.rapiddweller.benerator.test.GeneratorClassTest;
import com.rapiddweller.domain.person.FamilyNameGenerator;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

/**
 * Tests the {@link StreetNameGenerator}.<br/><br/>
 * Created: 12.06.2007 06:46:33
 *
 * @author Volker Bergmann
 */
public class StreetNameGeneratorTest extends GeneratorClassTest {

  /**
   * Test constructor.
   */
  @Test
  public void testConstructor() {
    StreetNameGenerator actualStreetNameGenerator = new StreetNameGenerator();
    assertNull(actualStreetNameGenerator.getDataset());
    Class<?> expectedGeneratedType = String.class;
    assertSame(expectedGeneratedType, actualStreetNameGenerator.getGeneratedType());
    assertNull(actualStreetNameGenerator.getSource());
  }

  /**
   * Test constructor 2.
   */
  @Test
  public void testConstructor2() {
    StreetNameGenerator actualStreetNameGenerator = new StreetNameGenerator("Dataset Name");
    assertEquals("Dataset Name", actualStreetNameGenerator.getDataset());
    Class<?> expectedGeneratedType = String.class;
    assertSame(expectedGeneratedType, actualStreetNameGenerator.getGeneratedType());
    assertNull(actualStreetNameGenerator.getSource());
  }

  /**
   * Test set dataset.
   */
  @Test
  public void testSetDataset() {
    StreetNameGenerator streetNameGenerator = new StreetNameGenerator();
    streetNameGenerator.setDataset("Dataset Name");
    assertEquals("Dataset Name", streetNameGenerator.getDataset());
  }

  /**
   * Test get source.
   */
  @Test
  public void testGetSource() {
    assertNull((new StreetNameGenerator()).getSource());
  }

  /**
   * Test get source 2.
   */
  @Test
  public void testGetSource2() {
    StreetNameGenerator streetNameGenerator = new StreetNameGenerator();
    FamilyNameGenerator familyNameGenerator = new FamilyNameGenerator();
    streetNameGenerator.setSource(familyNameGenerator);
    assertSame(familyNameGenerator, streetNameGenerator.getSource());
  }

  /**
   * Instantiates a new Street name generator test.
   */
  public StreetNameGeneratorTest() {
    super(StreetNameGenerator.class);
  }

  /**
   * Test de.
   */
  @Test
  public void test_DE() {
    StreetNameGenerator generator = new StreetNameGenerator("DE");
    generator.init(context);
    for (int i = 0; i < 10; i++) {
      String product = generator.generate();
      assertNotNull(product);
    }
    generator.close();
  }

  /**
   * Test au.
   */
  @Test
  public void test_AU() {
    DatasetUtil.runInRegion(Country.AUSTRALIA.getIsoCode(), () -> {
      StreetNameGenerator generator = new StreetNameGenerator();
      generator.init(context);
      for (int i = 0; i < 10; i++) {
        String product = generator.generate();
        assertNotNull(product);
      }
      generator.close();
    });
  }

}
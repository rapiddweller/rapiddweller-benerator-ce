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

import com.rapiddweller.benerator.dataset.CompositeDatasetGenerator;
import com.rapiddweller.benerator.test.GeneratorClassTest;
import com.rapiddweller.benerator.wrapper.WeightedGeneratorGenerator;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

/**
 * Tests the {@link CountryGenerator}.<br/><br/>
 * Created: 12.06.2007 06:46:13
 *
 * @author Volker Bergmann
 * @since 0.2
 */
public class CountryGeneratorTest extends GeneratorClassTest {

  /**
   * Test constructor.
   */
  @Test
  public void testConstructor() {
    CountryGenerator actualCountryGenerator = new CountryGenerator();
    assertEquals("world", actualCountryGenerator.getDataset());
    Class<?> expectedGeneratedType = Country.class;
    assertSame(expectedGeneratedType, actualCountryGenerator.getGeneratedType());
    assertNull(actualCountryGenerator.getSource());
    assertEquals("/com/rapiddweller/dataset/region", actualCountryGenerator.getNesting());
    assertEquals(0.0, actualCountryGenerator.getWeight(), 0.0);
  }

  /**
   * Test constructor 2.
   */
  @Test
  public void testConstructor2() {
    CountryGenerator actualCountryGenerator = new CountryGenerator("Dataset Name");
    assertEquals("Dataset Name", actualCountryGenerator.getDataset());
    Class<?> expectedGeneratedType = Country.class;
    assertSame(expectedGeneratedType, actualCountryGenerator.getGeneratedType());
    assertNull(actualCountryGenerator.getSource());
    assertEquals("/com/rapiddweller/dataset/region", actualCountryGenerator.getNesting());
    assertEquals(0.0, actualCountryGenerator.getWeight(), 0.0);
  }

  /**
   * Instantiates a new Country generator test.
   */
  public CountryGeneratorTest() {
    super(CountryGenerator.class);
  }

  /**
   * Test default generation.
   */
  @Test
  public void testDefaultGeneration() {
    CountryGenerator generator = new CountryGenerator();
    generator.init(context);
    for (int i = 0; i < 100; i++) {
      Country country = generator.generate();
      assertNotNull(country);
      assertNotNull(Country.getInstance(country.getIsoCode()));
    }
  }

  /**
   * Test weights for dach.
   */
  @Test
  public void testWeightsForDACH() {
    CountryGenerator generator = new CountryGenerator("dach");
    generator.init(context);
    CompositeDatasetGenerator<Country> compGen = (CompositeDatasetGenerator<Country>) generator.getSource();
    WeightedGeneratorGenerator<Country> genGen = compGen.getSource();
    List<Double> sourceWeights = genGen.getSourceWeights();
    assertEquals(81000000., sourceWeights.get(0), 0); // DE
    assertEquals(8000000., sourceWeights.get(2), 0); // CH
    assertEquals(7000000., sourceWeights.get(1), 0); // AT
  }

}
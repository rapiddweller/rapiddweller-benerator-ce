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

package com.rapiddweller.domain.product;

import com.rapiddweller.benerator.test.GeneratorClassTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Tests the EANGenerator.<br/>
 * <br/>
 * Created: 30.07.2007 21:35:18
 *
 * @author Volker Bergmann
 */
public class EANGeneratorTest extends GeneratorClassTest {

  /**
   * Instantiates a new Ean generator test.
   */
  public EANGeneratorTest() {
    super(EANGenerator.class);
  }

  /**
   * Test non unique.
   */
  @Test
  public void testNonUnique() {
    expectGenerations(createGenerator(false), 100, new EANValidator());
  }

  /**
   * Test unique.
   */
  @Test
  public void testUnique() {
    expectUniqueGenerations(createGenerator(true), 10000);
  }

  private EANGenerator createGenerator(boolean unique) {
    EANGenerator generator = new EANGenerator(unique);
    generator.init(context);
    return generator;
  }

  /**
   * Test constructor.
   */
  @Test
  public void testConstructor() {
    EANGenerator actualEanGenerator = new EANGenerator();
    assertNull(actualEanGenerator.getSource());
    assertEquals("EANGenerator", actualEanGenerator.toString());
  }

  /**
   * Test constructor 2.
   */
  @Test
  public void testConstructor2() {
    EANGenerator actualEanGenerator = new EANGenerator(true);
    assertNull(actualEanGenerator.getSource());
    assertEquals("EANGenerator[unique]", actualEanGenerator.toString());
  }

  /**
   * Test set unique.
   */
  @Test
  public void testSetUnique() {
    EANGenerator eanGenerator = new EANGenerator();
    eanGenerator.setUnique(true);
    assertEquals("EANGenerator[unique]", eanGenerator.toString());
  }

  @Test
  public void testToString() {
    assertEquals("EANGenerator", (new EANGenerator()).toString());
    assertEquals("EANGenerator[unique]", (new EANGenerator(true)).toString());
  }

}

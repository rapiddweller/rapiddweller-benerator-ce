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
import com.rapiddweller.common.validator.UniqueValidator;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests the {@link EAN8Generator}.<br/>
 * <br/>
 * Created: 30.07.2007 21:47:30
 *
 * @author Volker Bergmann
 */
public class EAN8GeneratorTest extends GeneratorClassTest {

  /**
   * Instantiates a new Ean 8 generator test.
   */
  public EAN8GeneratorTest() {
    super(EAN8Generator.class);
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
    expectGenerations(createGenerator(true), 10000, new EANValidator(), new UniqueValidator<>());
  }

  private EAN8Generator createGenerator(boolean unique) {
    EAN8Generator generator = new EAN8Generator(unique);
    generator.init(context);
    return generator;
  }

  /**
   * Test constructor.
   */
  @Test
  public void testConstructor() {
    EAN8Generator actualEan8Generator = new EAN8Generator();
    assertNull(actualEan8Generator.getSource());
    assertEquals("EAN8Generator", actualEan8Generator.toString());
  }

  /**
   * Test constructor 2.
   */
  @Test
  public void testConstructor2() {
    EAN8Generator actualEan8Generator = new EAN8Generator(true);
    assertNull(actualEan8Generator.getSource());
    assertEquals("EAN8Generator[unique]", actualEan8Generator.toString());
  }

  /**
   * Test constructor 3.
   */
  @Test
  public void testConstructor3() {
    EAN8Generator actualEan8Generator = new EAN8Generator(true, true);
    assertNull(actualEan8Generator.getSource());
    assertTrue(actualEan8Generator.isOrdered());
    assertEquals("EAN8Generator[unique]", actualEan8Generator.toString());
  }

  /**
   * Test set ordered.
   */
  @Test
  public void testSetOrdered() {
    EAN8Generator ean8Generator = new EAN8Generator();
    ean8Generator.setOrdered(true);
    assertTrue(ean8Generator.isOrdered());
  }

  @Test
  public void testToString() {
    assertEquals("EAN8Generator", (new EAN8Generator()).toString());
    assertEquals("EAN8Generator[unique]", (new EAN8Generator(true)).toString());
  }

}

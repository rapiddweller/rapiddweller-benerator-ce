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

import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.common.BeanUtil;
import com.rapiddweller.domain.person.GivenNameGenerator;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Tests the {@link DefaultBeneratorSubContext}.<br/><br/>
 * Created: 15.02.2012 05:35:10
 * @author Volker Bergmann
 * @since 0.8.0
 */
@SuppressWarnings("resource")
public class DefaultBeneratorSubContextTest {

  private static final String CONTEXT_URI = "~/benerator";

  private BeneratorContext parent;
  private BeneratorSubContext child;

  @Before
  public void setUp() {
    this.parent = new DefaultBeneratorContext(CONTEXT_URI);
    this.parent.setGlobal("globalVar", "globalValue");
    this.child = new DefaultBeneratorSubContext("person", parent);
  }

  @Test
  public void testGetParent() {
    assertSame(parent, child.getParent());
  }

  @Test
  public void testScopedGetAndSet() {
    // verify that child settings are not available in parent
    child.set("c", 2);
    assertNull(parent.get("c"));
    assertEquals(2, child.get("c"));
    // verify that parent settings are available in child
    parent.set("x", 3);
    assertEquals(3, parent.get("x"));
    assertEquals(3, child.get("x"));
    // verify override of parent setting in child
    parent.set("x", 3);
    child.set("x", 4);
    assertEquals(3, parent.get("x"));
    assertEquals(4, child.get("x"));
  }

  @Test
  public void testCurrentProduct() {
    BeneratorContext root = new DefaultBeneratorContext();
    BeneratorContext parent = root.createSubContext("top");
    DefaultBeneratorSubContext child = (DefaultBeneratorSubContext) parent.createSubContext("sub");

    // verify access to parent's currentProduct
    ProductWrapper<Integer> pp = new ProductWrapper<>(11);
    parent.setCurrentProduct(pp);
    assertEquals(pp, parent.getCurrentProduct());
    assertEquals(pp.unwrap(), parent.get("this"));
    assertEquals(pp.unwrap(), child.get("top"));

    // verify access to child's currentProduct
    ProductWrapper<Integer> cp = new ProductWrapper<>(12);
    child.setCurrentProduct(cp);
    assertEquals(pp, parent.getCurrentProduct());
    assertEquals(pp.unwrap(), parent.get("this"));
    assertEquals(cp.unwrap(), child.get("sub"));
  }

  @Test
  public void testGlobal() {
    assertEquals("globalValue", child.getGlobal("globalVar"));
    assertTrue(child.contains("globalVar"));
  }

  @Test
  public void testContextUri() {
    assertEquals(CONTEXT_URI, parent.getContextUri());
    assertEquals(CONTEXT_URI, child.getContextUri());
  }

  @Test
  public void testForName() {
    assertEquals(java.lang.Object.class, child.forName("java.lang.Object"));
  }

  @Test
  public void testParentDelegation() {
    checkParentDelegation("validate", true, false);
    assertEquals(parent.getExecutorService(), child.getExecutorService());
    checkParentDelegation("defaultImports", true, false);
    checkParentDelegation("acceptUnknownSimpleTypes", true, false);
    checkParentDelegation("defaultErrorHandler", "error", "warn", "fatal");
    checkParentDelegation("defaultDataset", "BR", "US");
    checkParentDelegation("defaultLocale", Locale.GERMAN, Locale.ENGLISH);
    checkParentDelegation("defaultLineSeparator", "\r\n", "\n");
  }

  public void checkParentDelegation(String propertyName, Object... values) {
    assertEquals(BeanUtil.getPropertyValue(parent, propertyName), BeanUtil.getPropertyValue(child, propertyName));
    for (Object value : values) {
      BeanUtil.setPropertyValue(parent, propertyName, value);
      assertEquals(value, BeanUtil.getPropertyValue(parent, propertyName));
      assertEquals(value, BeanUtil.getPropertyValue(child, propertyName));
    }
  }

  @Test
  public void testImportPackage() {
    parent.importPackage("com.rapiddweller.domain.person");
    assertEquals(GivenNameGenerator.class, child.forName("GivenNameGenerator"));
  }

  @Test
  public void testImportClass() {
    parent.importClass("com.rapiddweller.domain.address.Address");
    assertEquals(com.rapiddweller.domain.address.Address.class, child.forName("Address"));
  }

  @Test
  public void testImportDefaults() {
    parent.importDefaults();
    assertTrue(child.isDefaultImports());
    assertEquals(com.rapiddweller.benerator.consumer.ConsoleExporter.class,
        child.forName("ConsoleExporter"));
  }

  @Test
  public void testToString() {
    assertEquals("DefaultBeneratorSubContext(person)", child.toString());
  }

  @Test
  public void testContains() {
    assertTrue(child.contains("globalVar"));
    assertTrue(child.contains("line.separator"));
  }

  @Test
  public void testKeySet() {
    Set<String> keySet = child.keySet();
    assertTrue(keySet.contains("globalVar"));
    assertTrue(keySet.contains("line.separator"));
  }

  @Test
  public void testGetNull() {
    assertNull(child.get(null));
  }

}

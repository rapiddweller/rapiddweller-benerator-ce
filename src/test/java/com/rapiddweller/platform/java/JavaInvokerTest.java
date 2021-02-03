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

package com.rapiddweller.platform.java;

import com.rapiddweller.benerator.test.GeneratorTest;
import com.rapiddweller.common.IOUtil;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests the {@link JavaInvoker}.<br/><br/>
 * Created: 21.10.2009 18:28:17
 *
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class JavaInvokerTest extends GeneratorTest {

  /**
   * Test instance method entity.
   */
  @Test
  public void testInstanceMethodEntity() {
    POJO target = new POJO();
    JavaInvoker invoker = new JavaInvoker(target, "dynP2");
    try {
      invoker.startProductConsumption(createEntity("params", "name", "Alice", "age", 23));
      invoker.startProductConsumption(createEntity("params", "name", "Bob", "age", 34));
      assertEquals(2, target.dynCountP2);
    } finally {
      IOUtil.close(invoker);
    }
  }

  /**
   * Test static method entity.
   */
  @Test
  public void testStaticMethodEntity() {
    POJO.statCountP2 = 0;
    Class<POJO> target = POJO.class;
    JavaInvoker invoker = new JavaInvoker(target, "statP2");
    try {
      invoker.startProductConsumption(createEntity("params", "name", "Alice", "age", 23));
      invoker.startProductConsumption(createEntity("params", "name", "Bob", "age", 34));
      assertEquals(2, POJO.statCountP2);
    } finally {
      IOUtil.close(invoker);
    }
  }

  /**
   * Test instance method object.
   */
  @Test
  public void testInstanceMethodObject() {
    POJO target = new POJO();
    JavaInvoker invoker = new JavaInvoker(target, "dynP1");
    try {
      invoker.startProductConsumption("Alice");
      invoker.startProductConsumption("Bob");
      assertEquals(2, target.dynCountP1);
    } finally {
      IOUtil.close(invoker);
    }
  }

  /**
   * Test static method object.
   */
  @Test
  public void testStaticMethodObject() {
    POJO.statCountP1 = 0;
    Class<POJO> target = POJO.class;
    JavaInvoker invoker = new JavaInvoker(target, "statP1");
    try {
      invoker.startProductConsumption(23);
      invoker.startProductConsumption(34);
      assertEquals(2, POJO.statCountP1);
    } finally {
      IOUtil.close(invoker);
    }
  }

  /**
   * Test instance method array.
   */
  @Test
  public void testInstanceMethodArray() {
    POJO target = new POJO();
    JavaInvoker invoker = new JavaInvoker(target, "dynP2");
    try {
      invoker.startProductConsumption(new Object[] {"Alice", 23});
      invoker.startProductConsumption(new Object[] {"Bob", 34});
      assertEquals(2, target.dynCountP2);
    } finally {
      IOUtil.close(invoker);
    }
  }

  /**
   * Test static method array.
   */
  @Test
  public void testStaticMethodArray() {
    POJO.statCountP2 = 0;
    Class<POJO> target = POJO.class;
    JavaInvoker invoker = new JavaInvoker(target, "statP2");
    try {
      invoker.startProductConsumption(new Object[] {"Alice", 23});
      invoker.startProductConsumption(new Object[] {"Bob", 34});
      assertEquals(2, POJO.statCountP2);
    } finally {
      IOUtil.close(invoker);
    }
  }

  /**
   * The type Pojo.
   */
  public static class POJO {

    /**
     * The Dyn count p 1.
     */
    public int dynCountP1 = 0;
    /**
     * The constant statCountP1.
     */
    public static int statCountP1 = 0;
    /**
     * The Dyn count p 2.
     */
    public int dynCountP2 = 0;
    /**
     * The constant statCountP2.
     */
    public static int statCountP2 = 0;

    /**
     * Dyn p 1.
     *
     * @param name the name
     */
    public void dynP1(String name) {
      dynCountP1++;
    }

    /**
     * Stat p 1.
     *
     * @param age the age
     */
    public static void statP1(int age) {
      statCountP1++;
    }

    /**
     * Dyn p 2.
     *
     * @param name the name
     * @param age  the age
     */
    public void dynP2(String name, int age) {
      dynCountP2++;
    }

    /**
     * Stat p 2.
     *
     * @param name the name
     * @param age  the age
     */
    public static void statP2(String name, int age) {
      statCountP2++;
    }
  }

}

/*
 * (c) Copyright 2006-2021 by rapiddweller GmbH & Volker Bergmann. All rights reserved.
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

package com.rapiddweller.benerator.primitive;

import com.rapiddweller.benerator.engine.DescriptorRunner;
import com.rapiddweller.benerator.test.GeneratorTest;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.model.data.Entity;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Tests the {@link AddingConsumer}.<br/><br/>
 * Created: 04.04.2010 08:03:25
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class AddingConsumerTest extends GeneratorTest {

  private Entity ALICE;
  private Entity METHUSALEM;

  @Before
  public void setUpPersons() {
    ALICE = createEntity("Person", "age", 23L); // long age
    METHUSALEM = createEntity("Person", "age", 1024.); // double age
  }

  @Test
  public void testJavaInvocation() {
    AddingConsumer consumer = new AddingConsumer();
    try {
      consumer.setFeature("age");
      consumer.setType("int");
      consumer.startProductConsumption(ALICE);
      consumer.finishProductConsumption(ALICE);
      consumer.startProductConsumption(METHUSALEM);
      consumer.finishProductConsumption(METHUSALEM);
      assertEquals(1047, consumer.getSum());
    } finally {
      IOUtil.close(consumer);
    }
  }

  @Test
  public void testBeneratorInvocation() throws IOException {
    DescriptorRunner runner = new DescriptorRunner("com/rapiddweller/benerator/primitive/AddingConsumerTest.ben.xml", context);
    try {
      runner.run();
    } finally {
      IOUtil.close(runner);
    }
  }

}

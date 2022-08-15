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

package com.rapiddweller.benerator.engine;

import com.rapiddweller.benerator.consumer.AbstractConsumer;
import com.rapiddweller.benerator.consumer.FileExporter;
import com.rapiddweller.benerator.test.ModelTest;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.common.FileUtil;
import com.rapiddweller.common.IOUtil;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Tests the {@link DescriptorRunner}.<br/><br/>
 * Created at 13.03.2009 07:16:55
 * @author Volker Bergmann
 * @since 0.5.8
 */
@SuppressWarnings("CheckStyle")
public class DescriptorRunnerTest extends ModelTest {

  private static final String EXPORT_FILE_URI = "test-uri.txt";

  @Test
  public void testProgrammaticInvocation() throws IOException {
    DefaultBeneratorContext context = new DefaultBeneratorContext();
    DescriptorRunner runner = new DescriptorRunner(
        "string://<setup>" +
            "	<generate type='Person' count='1' consumer='myConsumer'>" +
            "		<attribute name='name' constant='Alice'/>" +
            "	</generate>" +
            "</setup>", context);
    try {
      context.importDefaults();
      context.setValidate(false);
      MyConsumer myConsumer = new MyConsumer();
      context.setGlobal("myConsumer", myConsumer);
      runner.run();
      assertEquals(1, myConsumer.products.size());
      assertEquals(createEntity("Person", "name", "Alice"), myConsumer.products.get(0));
    } finally {
      IOUtil.close(runner);
    }
  }

  @Test @Ignore // TODO vbergmann fix or remove generatedFiles()
  public void testGetGeneratedFiles_csv() throws IOException {
    DescriptorRunner runner = new DescriptorRunner("string://<setup>" +
        "  <import platforms='csv'/>" +
        "  <generate type='data' count='10' consumer='CSVEntityExporter'>" +
        "    <attribute name='x' constant='123'/>" +
        "  </generate>" +
        "</setup>", new DefaultBeneratorContext());
    try {
      runner.run();
      List<String> generatedFiles = runner.getGeneratedFiles();
      assertEquals(1, generatedFiles.size());
      assertEquals(EXPORT_FILE_URI, generatedFiles.get(0));
    } finally {
      IOUtil.close(runner);
      FileUtil.deleteIfExists(new File(EXPORT_FILE_URI));
    }
  }

  static class TestExporter implements FileExporter {

    @Override
    public String getUri() {
      return EXPORT_FILE_URI;
    }

    @Override
    public void startConsuming(ProductWrapper<?> object) {
    }

    @Override
    public void finishConsuming(ProductWrapper<?> object) {
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() {
    }
  }

  static class MyConsumer extends AbstractConsumer {

    final List<Object> products = new ArrayList<>();

    @Override
    public void startProductConsumption(Object object) {
      products.add(object);
    }
  }

}

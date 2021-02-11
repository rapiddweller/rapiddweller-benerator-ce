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

package com.rapiddweller.benerator.primitive;

import com.rapiddweller.benerator.test.GeneratorClassTest;
import com.rapiddweller.common.FileUtil;
import org.junit.Test;

import java.io.File;

/**
 * Tests the {@link LocalSequenceGenerator}.<br/>
 * <br/>
 * Created at 01.07.2009 17:30:46
 *
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class LocalSequenceGeneratorTest extends GeneratorClassTest {

  /**
   * Instantiates a new Local sequence generator test.
   */
  public LocalSequenceGeneratorTest() {
    super(LocalSequenceGenerator.class);
  }

  /**
   * Test persistence.
   */
  @Test
  public void testPersistence() {
    File propertiesFile = new File(LocalSequenceGenerator.FILENAME);
    FileUtil.deleteIfExists(propertiesFile);
    String sequenceName = getClass().getSimpleName();
    LocalSequenceGenerator.invalidateInstances();
    try {
      LocalSequenceGenerator generator = new LocalSequenceGenerator(sequenceName);
      generator.init(context);
      expectGeneratedSequenceOnce(generator, 1L, 2L, 3L);
      generator.close();

      LocalSequenceGenerator generator2 = new LocalSequenceGenerator(sequenceName);
      generator2.init(context);
      expectGeneratedSequenceOnce(generator2, 4L, 5L, 6L);
      generator.close();
    } finally {
      FileUtil.deleteIfExists(propertiesFile);
    }
  }

}

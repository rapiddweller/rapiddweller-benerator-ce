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

package com.rapiddweller.domain.person;

import com.rapiddweller.benerator.IllegalGeneratorStateException;
import com.rapiddweller.benerator.InvalidGeneratorSetupException;
import com.rapiddweller.benerator.test.GeneratorClassTest;
import com.rapiddweller.common.LocaleUtil;
import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.assertTrue;

/**
 * Tests the {@link GivenNameGenerator}.
 * Created: 09.06.2006 21:37:05
 * @author Volker Bergmann
 * @since 0.1
 */
public class GivenNameGeneratorTest extends GeneratorClassTest {

  public GivenNameGeneratorTest() {
    super(GivenNameGenerator.class);
  }

  @Test
  public void test_default_us() throws IllegalGeneratorStateException {
    LocaleUtil.runInLocale(Locale.US,
        () -> checkDiversity(new GivenNameGenerator(), 100, 20));
  }

  @Test
  public void test_us() throws IllegalGeneratorStateException {
    checkDiversity(new GivenNameGenerator("US", Gender.MALE), 100, 20);
    checkDiversity(new GivenNameGenerator("US", Gender.FEMALE), 100, 20);
  }

  @Test
  public void test_de() throws IllegalGeneratorStateException {
    checkDiversity(new GivenNameGenerator("DE", Gender.MALE), 100, 20);
    checkDiversity(new GivenNameGenerator("DE", Gender.FEMALE), 100, 20);
  }

  @Test(expected = InvalidGeneratorSetupException.class)
  public void test_xy() throws IllegalGeneratorStateException {
    checkDiversity(new GivenNameGenerator("XY", Gender.MALE), 100, 20);
    checkDiversity(new GivenNameGenerator("XY", Gender.FEMALE), 100, 20);
  }

}

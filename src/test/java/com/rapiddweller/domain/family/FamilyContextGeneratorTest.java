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

package com.rapiddweller.domain.family;

import com.rapiddweller.benerator.test.GeneratorTest;
import com.rapiddweller.domain.finance.BankAccountGenerator;
import com.rapiddweller.domain.finance.BankAccountValidator;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Tests the {@link FamilyContextGenerator}.<br/>
 * <br/>
 *
 * @since 0.5.4
 */
public class FamilyContextGeneratorTest extends GeneratorTest {

  /**
   * Test.
   */
  @Test
  public void generateTest() {
    FamilyContextGenerator generator = new FamilyContextGenerator();
    generator.init(context);
    FamilyContext fc = generator.generate();
    List<PersonNode> personNodeList = fc.getFamilyPersonList();
    System.out.println(personNodeList.toString());
    Assert.assertNotNull(fc);
    Assert.assertTrue(personNodeList.size()>=2 && personNodeList.size()<=12);
  }
}

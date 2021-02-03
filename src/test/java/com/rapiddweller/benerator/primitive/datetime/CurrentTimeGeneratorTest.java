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

package com.rapiddweller.benerator.primitive.datetime;

import com.rapiddweller.benerator.test.GeneratorClassTest;
import com.rapiddweller.common.TimeUtil;
import com.rapiddweller.common.validator.bean.AbstractConstraintValidator;
import org.junit.Test;

import javax.validation.ConstraintValidatorContext;
import java.lang.annotation.Annotation;
import java.util.Date;

/**
 * Tests the CurrentTimeGenerator.<br/>
 * <br/>
 * Created: 19.11.2007 20:43:45
 *
 * @author Volker Bergmann
 */
public class CurrentTimeGeneratorTest extends GeneratorClassTest {

  /**
   * Instantiates a new Current time generator test.
   */
  public CurrentTimeGeneratorTest() {
    super(CurrentTimeGenerator.class);
  }

  /**
   * Test products.
   */
  @Test
  public void testProducts() {
    CurrentTimeGenerator generator = new CurrentTimeGenerator();
    generator.init(context);
    expectGenerations(generator, 10, new CurrentTimeValidator());
  }

  /**
   * The type Current time validator.
   */
  static class CurrentTimeValidator extends AbstractConstraintValidator<Annotation, Date> {
    @Override
    public boolean isValid(Date date, ConstraintValidatorContext context) {
      long currentTimeMillis = TimeUtil.currentTime().getTime();
      long generatedTimeMillis = date.getTime();
      return (Math.abs(currentTimeMillis - generatedTimeMillis) < 1000);
    }
  }

}

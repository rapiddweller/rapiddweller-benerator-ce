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

package com.rapiddweller.benerator.util;

import com.rapiddweller.benerator.BeneratorFactory;
import com.rapiddweller.benerator.RandomProvider;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Provides utility functions for generating numbers in an interval.<br/><br/>
 * Created: 03.09.2006 13:23:02
 * @author Volker Bergmann
 * @since 0.1
 */
public class RandomUtil {

  private static final RandomProvider random = BeneratorFactory.getInstance().getRandomProvider();

  private RandomUtil() {
    // private constructor to prevent instantiation
  }

  public static long randomLong(long min, long max) {
    return random.randomLong(min, max);
  }

  public static int randomInt(int min, int max) {
    return random.randomInt(min, max);
  }

  @SafeVarargs
  public static <T> T randomElement(T... values) {
    return random.randomElement(values);
  }

  public static <T> T randomElement(List<T> values) {
    return random.randomElement(values);
  }

  public static int randomIndex(Collection<?> values) {
    return random.randomIndex(values);
  }

  public static float randomProbability() {
    return random.randomProbability();
  }

  public static char randomDigit(int min) {
    return random.randomDigit(min);
  }

  public static Date randomDate(Date min, Date max) {
    return random.randomDate(min, max);
  }

  public static Object randomFromWeightLiteral(String literal) {
    return random.randomFromWeightLiteral(literal);
  }

}

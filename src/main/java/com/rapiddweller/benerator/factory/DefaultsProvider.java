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

package com.rapiddweller.benerator.factory;

import java.util.Date;

/**
 * Interface for all classes that provide default values for testing.<br/><br/>
 * Created: 15.07.2011 21:13:20
 *
 * @author Volker Bergmann
 * @since 0.7.0
 */
public interface DefaultsProvider {
  /**
   * Default granularity t.
   *
   * @param <T>        the type parameter
   * @param numberType the number type
   * @return the t
   */
  <T extends Number> T defaultGranularity(Class<T> numberType);

  /**
   * Default min t.
   *
   * @param <T>        the type parameter
   * @param numberType the number type
   * @return the t
   */
  <T extends Number> T defaultMin(Class<T> numberType);

  /**
   * Default max t.
   *
   * @param <T>        the type parameter
   * @param numberType the number type
   * @return the t
   */
  <T extends Number> T defaultMax(Class<T> numberType);

  /**
   * Default min length int.
   *
   * @return the int
   */
  int defaultMinLength();

  /**
   * Default max length integer.
   *
   * @return the integer
   */
  Integer defaultMaxLength();

  /**
   * Default nullable boolean.
   *
   * @return the boolean
   */
  boolean defaultNullable();

  /**
   * Default null quota double.
   *
   * @return the double
   */
  double defaultNullQuota();

  /**
   * Default min date date.
   *
   * @return the date
   */
  Date defaultMinDate();

  /**
   * Default max date date.
   *
   * @return the date
   */
  Date defaultMaxDate();
}

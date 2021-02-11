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

import com.rapiddweller.common.StringUtil;

/**
 * Provides support for Benerator's system property settings.<br/><br/>
 * Created: 30.07.2010 18:25:01
 *
 * @author Volker Bergmann
 * @since 0.6.3
 */
public class BeneratorOpts {

  /**
   * The constant OPTS_VALIDATE.
   */
  public static final String OPTS_VALIDATE = "benerator.validate";
  /**
   * The constant OPTS_CACHE_SIZE.
   */
  public static final String OPTS_CACHE_SIZE = "benerator.cacheSize";

  private static final int DEFAULT_CACHE_SIZE = 100000;

  /**
   * Sets validating.
   *
   * @param validating the validating
   */
  public static void setValidating(boolean validating) {
    System.setProperty(OPTS_VALIDATE, String.valueOf(validating));
  }

  /**
   * Is validating boolean.
   *
   * @return the boolean
   */
  public static boolean isValidating() {
    return !("false".equals(System.getProperty(OPTS_VALIDATE)));
  }

  /**
   * Gets cache size.
   *
   * @return the cache size
   */
  public static int getCacheSize() {
    return parseIntProperty(OPTS_CACHE_SIZE, DEFAULT_CACHE_SIZE);
  }

  private static int parseIntProperty(String propertyKey, int defaultValue) {
    String propertyValue = System.getProperty(propertyKey);
    return (StringUtil.isEmpty(propertyValue) ? defaultValue : Integer.parseInt(propertyValue));
  }

}

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

package com.rapiddweller.benerator.wrapper;

import com.rapiddweller.benerator.Generator;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper class for the {@link Generator} class.<br/><br/>
 * Created: 26.01.2010 10:53:53
 *
 * @param <E> the type parameter
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class ProductWrapper<E> {

  private E product;
  private Map<String, String> tags;

  /**
   * Instantiates a new Product wrapper.
   *
   * @param product the product
   */
  public ProductWrapper(E product) {
    this();
    wrap(product);
  }

  /**
   * Instantiates a new Product wrapper.
   */
  public ProductWrapper() {
    this.tags = null;
  }

  /**
   * Wrap product wrapper.
   *
   * @param product the product
   * @return the product wrapper
   */
  public ProductWrapper<E> wrap(E product) {
    return wrap(product, true);
  }

  /**
   * Wrap product wrapper.
   *
   * @param product   the product
   * @param clearTags the clear tags
   * @return the product wrapper
   */
  public ProductWrapper<E> wrap(E product, boolean clearTags) {
    this.product = product;
    if (tags != null && clearTags) {
      tags.clear();
    }
    return this;
  }

  /**
   * Unwrap e.
   *
   * @return the e
   */
  public E unwrap() {
    return this.product;
  }

  /**
   * Gets tag.
   *
   * @param key the key
   * @return the tag
   */
  public String getTag(String key) {
    return (tags != null ? tags.get(key) : null);
  }

  /**
   * Sets tag.
   *
   * @param key   the key
   * @param value the value
   * @return the tag
   */
  public ProductWrapper<E> setTag(String key, String value) {
    if (tags == null) {
      tags = new HashMap<>();
    }
    tags.put(key, value);
    return this;
  }

  @Override
  public String toString() {
    return String.valueOf(product);
  }

  /**
   * Unwrap object.
   *
   * @param wrapper the wrapper
   * @return the object
   */
  public static Object unwrap(ProductWrapper<?> wrapper) {
    return (wrapper != null ? wrapper.product : null);
  }

}

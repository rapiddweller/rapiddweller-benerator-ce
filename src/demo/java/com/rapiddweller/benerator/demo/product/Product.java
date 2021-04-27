/*
 * (c) Copyright 2007 by Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General Public License.
 *
 * For redistributing this software or a derivative work under a license other
 * than the GPL-compatible Free Software License as defined by the Free
 * Software Foundation or approved by OSI, you must first obtain a commercial
 * license to this software product from Volker Bergmann.
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

package com.rapiddweller.benerator.demo.product;

/**
 * Represents a product in a category hierarchy.<br/>
 * <br/>
 * Created: 19.07.2007 07:09:30
 */
public class Product {

  private String name;
  private String eanCode;
  private ProductCategory category;

  /**
   * Instantiates a new Product.
   */
  public Product() {
    this(null, null, null);
  }

  /**
   * Instantiates a new Product.
   *
   * @param name     the name
   * @param eanCode  the ean code
   * @param category the category
   */
  public Product(String name, String eanCode, ProductCategory category) {
    this.name = name;
    this.eanCode = eanCode;
    this.category = category;
  }

  /**
   * Gets name.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets name.
   *
   * @param name the name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets ean code.
   *
   * @return the ean code
   */
  public String getEanCode() {
    return eanCode;
  }

  /**
   * Sets ean code.
   *
   * @param eanCode the ean code
   */
  public void setEanCode(String eanCode) {
    this.eanCode = eanCode;
  }

  /**
   * Gets category.
   *
   * @return the category
   */
  public ProductCategory getCategory() {
    return category;
  }

  /**
   * Sets category.
   *
   * @param category the category
   */
  public void setCategory(ProductCategory category) {
    this.category = category;
  }

  /**
   * To string string.
   *
   * @return the string
   */
  @Override
  public String toString() {
    StringBuilder buffer = new StringBuilder();
    if (category != null) {
      if (category.getGroup() != null) {
        buffer.append(category.getGroup().getName()).append('/');
      }
      buffer.append(category.getName()).append('/');
    }
    buffer.append(name).append('[').append(eanCode).append(']');
    return buffer.toString();
  }
}

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

package com.rapiddweller.domain.address;

import com.rapiddweller.common.NullSafeComparator;
import com.rapiddweller.common.StringUtil;

/**
 * Wrapper for simple or composite city names like 'Paris' or 'Paris, Texas'.
 * A name extension is used to make the name unique.<br/>
 * <br/>
 * Created: 27.07.2007 19:06:56
 */
public class CityId {

  private String name;
  private String nameExtension;

  /**
   * Instantiates a new City id.
   *
   * @param name          the name
   * @param nameExtension the name extension
   */
  public CityId(String name, String nameExtension) {
    this.name = name;
    this.nameExtension = nameExtension;
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
   * Gets name extension.
   *
   * @return the name extension
   */
  public String getNameExtension() {
    return nameExtension;
  }

  /**
   * Sets name extension.
   *
   * @param nameExtension the name extension
   */
  public void setNameExtension(String nameExtension) {
    this.nameExtension = nameExtension;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final CityId that = (CityId) o;
    if (!name.equals(that.name)) {
      return false;
    }
    return NullSafeComparator
        .equals(this.nameExtension, that.nameExtension);
  }

  @Override
  public int hashCode() {
    return name.hashCode() * 29 +
        (nameExtension != null ? nameExtension.hashCode() : 0);
  }

  @Override
  public String toString() {
    if (StringUtil.isEmpty(nameExtension)) {
      return name;
    } else if (nameExtension.charAt(0) == ',') {
      return name + nameExtension;
    } else {
      return name + ' ' + nameExtension;
    }
  }
}

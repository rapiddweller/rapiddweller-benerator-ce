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

import com.rapiddweller.benerator.util.UnsafeGenerator;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.model.data.DataModel;
import com.rapiddweller.model.data.DefaultDescriptorProvider;
import com.rapiddweller.model.data.DescriptorProvider;
import com.rapiddweller.model.data.Entity;

import java.util.Locale;

/**
 * The type My generator.
 */
public class MyGenerator extends UnsafeGenerator<Entity> {

  private final DescriptorProvider descriptorProvider = new DefaultDescriptorProvider(getClass().getName(), new DataModel());
  private Locale locale;

  /**
   * Gets locale.
   *
   * @return the locale
   */
  public Locale getLocale() {
    return locale;
  }

  /**
   * Sets locale.
   *
   * @param locale the locale
   */
  public void setLocale(Locale locale) {
    this.locale = locale;
  }

  @Override
  public ProductWrapper<Entity> generate(ProductWrapper<Entity> wrapper) {
    return wrapper.wrap(new Entity("MyEntity", descriptorProvider, "locale", locale));
  }

  @Override
  public Class<Entity> getGeneratedType() {
    return Entity.class;
  }

}
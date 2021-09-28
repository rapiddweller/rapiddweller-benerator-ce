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
import com.rapiddweller.benerator.IllegalGeneratorStateException;
import com.rapiddweller.benerator.InvalidGeneratorSetupException;
import com.rapiddweller.common.Assert;

/**
 * Wraps another Generator of same product type.<br/><br/>
 * Created: 17.08.2007 19:05:42
 * @param <E> the type parameter
 * @author Volker Bergmann
 */
public abstract class GeneratorProxy<E> extends GeneratorWrapper<E, E> {

  protected Class<E> generatedType;

  // constructors ----------------------------------------------------------------------------------------------------

  protected GeneratorProxy(Class<E> generatedType) {
    super(null);
    this.generatedType = generatedType;
  }

  protected GeneratorProxy(Generator<E> source) {
    super(source);
    if (source == null) {
      throw new InvalidGeneratorSetupException("source is null");
    }
  }

  // Generator interface implementation --------------------------------------------------------------------------------

  @Override
  public Class<E> getGeneratedType() {
    if (getSource() != null) {
      return getSource().getGeneratedType();
    } else if (generatedType != null) {
      return generatedType;
    } else {
      throw new IllegalGeneratorStateException("Generator not initialized correctly: " + this);
    }
  }

  @Override
  public ProductWrapper<E> generate(ProductWrapper<E> wrapper) {
    assertInitialized();
    return getSource().generate(wrapper);
  }

  // java.lang.Object overrides ----------------------------------------------------------------------------------------

  @Override
  public String toString() {
    return getClass().getSimpleName() + '[' + getSource() + ']';
  }

}

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

package com.rapiddweller.benerator.sample;

import com.rapiddweller.benerator.util.ThreadSafeGenerator;
import com.rapiddweller.benerator.wrapper.ProductWrapper;

/**
 * Generator implementation that always returns the same value.<br/>
 * <br/>
 * Created: 08.06.2006 20:26:22
 *
 * @param <E> the type parameter
 * @author Volker Bergmann
 * @since 0.1
 */
public class ConstantGenerator<E> extends ThreadSafeGenerator<E> {

  /**
   * The value to return
   */
  private E value;

  private final Class<E> generatedType;

  // constructors ----------------------------------------------------------------------------------------------------

  /**
   * Instantiates a new Constant generator.
   */
  public ConstantGenerator() {
    this(null);
  }

  /**
   * Initializes the generator to generate the specified value
   *
   * @param value the value
   */
  @SuppressWarnings("unchecked")
  public ConstantGenerator(E value) {
    this(value, (Class<E>) (value != null ? value.getClass() : Object.class));
  }

  /**
   * Instantiates a new Constant generator.
   *
   * @param value         the value
   * @param generatedType the generated type
   */
  public ConstantGenerator(E value, Class<E> generatedType) {
    this.value = value;
    this.generatedType = generatedType;
  }

  // config properties -----------------------------------------------------------------------------------------------

  /**
   * Returns the property 'value'
   *
   * @return the value
   */
  public E getValue() {
    return value;
  }

  /**
   * Sets the property 'value'
   *
   * @param value the value
   */
  public void setValue(E value) {
    this.value = value;
  }

  // Generator implementation ----------------------------------------------------------------------------------------

  @Override
  @SuppressWarnings("unchecked")
  public Class<E> getGeneratedType() {
    return (generatedType != null ?
        generatedType :
        (value != null ? (Class<E>) value.getClass() : (Class<E>) Object.class)
    );
  }

  /**
   * Returns the value of property 'value'
   */
  @Override
  public ProductWrapper<E> generate(ProductWrapper<E> wrapper) {
    return wrapper.wrap(value);
  }

  // java.lang.Object overrides --------------------------------------------------------------------------------------

  @Override
  public String toString() {
    return getClass().getSimpleName() + '[' + value + ']';
  }

}

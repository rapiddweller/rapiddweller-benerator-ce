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

/**
 * On each call to generate(), it chooses a generator from a collection,
 * calls its generate() method and returns the product.<br/>
 * <br/>
 * Created: 30.08.2006 21:56:59
 *
 * @param <E> the type parameter
 * @author Volker Bergmann
 * @since 0.1
 */
public class AlternativeGenerator<E> extends MultiGeneratorWrapper<E, E> {

  // constructors ----------------------------------------------------------------------------------------------------

  /**
   * Initializes the generator to a collection of source generators
   *
   * @param targetType the target type
   * @param sources    the sources
   */
  @SafeVarargs
  public AlternativeGenerator(Class<E> targetType, Generator<E>... sources) {
    super(targetType, sources);
  }

  // Generator implementation ----------------------------------------------------------------------------------------

  @Override
  public ProductWrapper<E> generate(ProductWrapper<E> wrapper) {
    assertInitialized();
    return generateFromRandomSource(wrapper);
  }

}

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
import com.rapiddweller.benerator.GeneratorState;

/**
 * Generator proxy that 'loops' through a source Generator,
 * calling reset() each time the source becomes unavailable.
 * It becomes unavailable only if the source generator is
 * unavailable after a reset.<br/>
 * <br/>
 * Created: 18.08.2007 16:55:21
 *
 * @param <E> the type parameter
 * @author Volker Bergmann
 */
public class CyclicGeneratorProxy<E> extends GeneratorProxy<E> {

  /**
   * Instantiates a new Cyclic generator proxy.
   *
   * @param source the source
   */
  public CyclicGeneratorProxy(Generator<E> source) {
    super(source);
  }

  @Override
  public ProductWrapper<E> generate(ProductWrapper<E> wrapper) {
    if (getSource() == null || state == GeneratorState.CLOSED) {
      return null;
    }
    ProductWrapper<E> test = super.generate(wrapper);
    if (test == null) {
      reset();
      test = super.generate(wrapper);
    }
    return test;
  }

}

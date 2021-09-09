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

package com.rapiddweller.benerator.distribution;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.NonNullGenerator;
import com.rapiddweller.benerator.wrapper.ProductWrapper;

/**
 * Parent interface for all distribution types.
 * Implementors of the Distribution interface are recommended to extend
 * {@link AbstractDistribution} for forward compatibility.
 * In order to migrate implementors of the {@link Distribution} interface before version 1.2.0,
 * their <code>implements Distribution</code> directive should be changed to
 * <code>extends AbstractDistribution</code>.<br/><br/>
 * Created: 11.09.2006 21:31:54
 * @author Volker Bergmann
 * @version 0.1
 */
public interface Distribution {

  /** Creates a {@link Generator} which generates numbers according to this type of sequence. */
  <T extends Number> NonNullGenerator<T> createNumberGenerator(
      Class<T> numberType, T min, T max, T granularity, boolean unique);

  /** Tells if the {@link Generator} created by {@link Distribution#applyTo(Generator, boolean)}
   *  is detached from its source generator (which means that calls to its
   *  {@link Generator#generate(ProductWrapper)}) method never forward calls to its source generator. */
  boolean isApplicationDetached();

  /** Creates a {@link Generator} which takes the elements created by the specified
   *  <code>source</code> generator and provides them in an order specified by
   *  this {@link Distribution} object.
   *  Depending on the sequence implementation, that generator usually
   *  <ol>
   *    <li>either fetches all products by the source at initialization, buffer and iterate through them</li>
   *    <li>or calls the source generator on demand</li>
   *  </ol>
   *  @param source the generator which provides the source elements to be distributed
   *  @param unique specifies if the created generator must guarantee that no source element is repeated */
  <T> Generator<T> applyTo(Generator<T> source, boolean unique);

}

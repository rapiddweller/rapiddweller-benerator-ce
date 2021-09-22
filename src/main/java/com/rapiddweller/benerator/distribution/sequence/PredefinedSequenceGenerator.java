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

package com.rapiddweller.benerator.distribution.sequence;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.util.ThreadSafeGenerator;
import com.rapiddweller.benerator.wrapper.ProductWrapper;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * {@link Generator} class for use by the {@link LiteralSequence}.<br/><br/>
 * Created: 03.06.2010 08:48:44
 * @param <E> the type parameter
 * @author Volker Bergmann
 * @since 0.6.3
 */
public class PredefinedSequenceGenerator<E extends Number> extends ThreadSafeGenerator<E> { // compare with SequenceGenerator

  private final Class<E> numberType;
  private final E[] numbers;
  private final AtomicInteger cursor;

  @SuppressWarnings("unchecked")
  public PredefinedSequenceGenerator(E... numbers) {
    this.numbers = numbers;
    this.numberType = (numbers.length > 0 ? (Class<E>) numbers[0].getClass() : (Class<E>) Number.class);
    this.cursor = new AtomicInteger(0);
  }

  @Override
  public Class<E> getGeneratedType() {
    return numberType;
  }

  @Override
  public ProductWrapper<E> generate(ProductWrapper<E> wrapper) {
    int i = cursor.getAndIncrement();
    if (i >= numbers.length) {
      return null;
    } else {
      return wrapper.wrap(numbers[i]);
    }
  }

  @Override
  public void reset() {
    this.cursor.set(0);
    super.reset();
  }

}

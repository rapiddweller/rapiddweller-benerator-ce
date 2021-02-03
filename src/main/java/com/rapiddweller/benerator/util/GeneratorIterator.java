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

package com.rapiddweller.benerator.util;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.wrapper.ProductWrapper;

import java.io.Closeable;
import java.util.Iterator;

/**
 * Wraps a {@link Generator} with an {@link Iterator} interface.<br/>
 * <br/>
 * Created at 21.07.2009 10:09:55
 *
 * @param <E> the type parameter
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class GeneratorIterator<E> implements Iterator<E>, Closeable {

  private final Generator<E> source;
  private E next;
  private final WrapperProvider<E> wrapperProvider = new WrapperProvider<>();

  /**
   * Instantiates a new Generator iterator.
   *
   * @param source the source
   */
  public GeneratorIterator(Generator<E> source) {
    this.source = source;
    this.next = fetchNext(source);
  }

  @Override
  public boolean hasNext() {
    return next != null;
  }

  @Override
  public E next() {
    E result = next;
    next = source.generate(wrapperProvider.get()).unwrap();
    return result;
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException("removal is not supported by " + getClass());
  }

  @Override
  public void close() {
    source.close();
  }

  /**
   * Fetch next e.
   *
   * @param source the source
   * @return the e
   */
  protected E fetchNext(Generator<E> source) {
    ProductWrapper<E> wrapper = source.generate(wrapperProvider.get());
    return (wrapper != null ? wrapper.unwrap() : null);
  }

}

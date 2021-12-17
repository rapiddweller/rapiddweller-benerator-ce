/*
 * (c) Copyright 2006-2021 by rapiddweller GmbH & Volker Bergmann. All rights reserved.
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
import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.InvalidGeneratorSetupException;
import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.benerator.util.AbstractGenerator;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.common.ThreadAware;
import com.rapiddweller.format.DataContainer;
import com.rapiddweller.format.DataIterator;
import com.rapiddweller.format.DataSource;
import com.rapiddweller.format.util.ThreadLocalDataContainer;

/**
 * {@link Generator} implementation which reads and forwards data from a {@link DataSource}.<br/><br/>
 * Created: 24.07.2011 08:58:09
 * @param <E> the type parameter
 * @author Volker Bergmann
 * @since 0.7.0
 */
public class DataSourceGenerator<E> extends AbstractGenerator<E> {

  private DataSource<E> source;
  private DataIterator<E> iterator;
  private final ThreadLocalDataContainer<E> container = new ThreadLocalDataContainer<>();

  // constructors ----------------------------------------------------------------------------------------------------

  public DataSourceGenerator() {
    this(null);
  }

  public DataSourceGenerator(DataSource<E> source) {
    this.source = source;
    this.iterator = null;
  }

  // properties ------------------------------------------------------------------------------------------------------

  public DataSource<E> getSource() {
    return source;
  }

  public void setSource(DataSource<E> source) {
    if (this.source != null) {
      throw BeneratorExceptionFactory.getInstance().illegalGeneratorState("Mutating an initialized generator");
    }
    this.source = source;
  }

  // Generator interface ---------------------------------------------------------------------------------------------

  @Override
  public boolean isParallelizable() {
    return false;
  }

  @Override
  public boolean isThreadSafe() {
    return (source instanceof ThreadAware && ((ThreadAware) source).isThreadSafe());
  }

  @Override
  public Class<E> getGeneratedType() {
    return source.getType();
  }

  @Override
  public void init(GeneratorContext context) {
    if (source == null) {
      throw new InvalidGeneratorSetupException("source", "is null");
    }
    super.init(context);
  }

  @Override
  public ProductWrapper<E> generate(ProductWrapper<E> wrapper) {
    assertInitialized();
    if (iterator == null) {
      iterator = source.iterator(); // iterator initialized lazily to reflect context state at invocation
    }
    DataContainer<E> tmp = iterator.next(container.get());
    if (tmp == null) {
      IOUtil.close(iterator);
      return null;
    }
    return wrapper.wrap(tmp.getData());
  }

  @Override
  public void reset() {
    IOUtil.close(iterator);
    iterator = null;
    super.reset();
  }

  @Override
  public void close() {
    IOUtil.close(iterator);
    super.close();
    IOUtil.close(source);
  }

  // java.lang.Object overrides --------------------------------------------------------------------------------------

  @Override
  public String toString() {
    return getClass().getSimpleName() + "[" + source + ']';
  }

}

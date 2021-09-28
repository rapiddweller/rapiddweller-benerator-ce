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

package com.rapiddweller.benerator.composite;

import com.rapiddweller.benerator.BeneratorFactory;
import com.rapiddweller.benerator.RandomProvider;
import com.rapiddweller.benerator.engine.AbstractScopedLifeCycleHolder;
import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.common.ArrayFormat;
import com.rapiddweller.common.CollectionUtil;

import java.util.List;

/**
 * Abstract parent class for all builders that relate to a group of components.<br/><br/>
 * Created at 09.05.2008 13:38:33
 * @param <E> the type parameter
 * @author Volker Bergmann
 * @since 0.5.4
 */
public abstract class MultiComponentBuilder<E> extends AbstractScopedLifeCycleHolder implements ComponentBuilder<E> {

  protected RandomProvider random;
  protected final ComponentBuilder<E>[] builders;
  private List<ComponentBuilder<E>> availableBuilders;
  protected String message;

  protected MultiComponentBuilder(ComponentBuilder<E>[] builders, String scope) {
    super(scope);
    this.builders = builders;
    this.availableBuilders = CollectionUtil.toList(builders);
  }

  @Override
  public String getMessage() {
    return message;
  }

  // Generator interface ---------------------------------------------------------------------------------------------

  @Override
  public void init(BeneratorContext context) {
    random = BeneratorFactory.getInstance().getRandomProvider();
    for (ComponentBuilder<E> builder : builders) {
      builder.init(context);
    }
  }

  @Override
  public void reset() {
    for (ComponentBuilder<E> builder : builders) {
      builder.reset();
    }
    this.availableBuilders = CollectionUtil.toList(builders);
  }

  @Override
  public void close() {
    for (ComponentBuilder<E> builder : builders) {
      builder.close();
    }
    this.availableBuilders.clear();
  }

  public boolean buildRandomComponent(BeneratorContext context) {
    message = null;
    if (availableBuilders.size() == 0) {
      message = "No component available: " + this;
      return false;
    }
    boolean success;
    do {
      int builderIndex = random.randomIndex(availableBuilders);
      success = availableBuilders.get(builderIndex).execute(context);
      if (!success) {
        availableBuilders.remove(builderIndex);
      }
    } while (!success && availableBuilders.size() > 0);
    if (!success) {
      message = "No component available: " + this;
    }
    return success;
  }

  // java.lang.Object overrides --------------------------------------------------------------------------------------

  @Override
  public String toString() {
    return getClass().getSimpleName() + ArrayFormat.format(builders);
  }

}

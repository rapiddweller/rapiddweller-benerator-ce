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

package com.rapiddweller.benerator.engine;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.util.WrapperProvider;
import com.rapiddweller.benerator.wrapper.ProductWrapper;

/**
 * Uses a {@link Generator} to create the currently processed object.<br/><br/>
 * Created: 01.09.2011 19:03:38
 * @author Volker Bergmann
 * @since 0.7.0
 */
public class CurrentProductGeneration implements Statement, LifeCycleHolder {

  private final String instanceName;
  private final Generator<?> source;
  private final WrapperProvider<?> provider;

  @SuppressWarnings({"unchecked", "rawtypes"})
  public CurrentProductGeneration(String instanceName, Generator<?> source) {
    this.instanceName = instanceName;
    this.source = source;
    this.provider = new WrapperProvider<>();
  }

  @Override
  public void init(BeneratorContext context) {
    source.init(context);
  }

  @Override
  public boolean execute(BeneratorContext context) {
    ProductWrapper<?> wrapper = source.generate((ProductWrapper) provider.get());
    context.setCurrentProduct(wrapper);
    if (wrapper != null && instanceName != null) {
      BeneratorContext parent = ((BeneratorSubContext) context).getParent();
      parent.set(instanceName, wrapper.unwrap());
    }
    return (wrapper != null);
  }

  @Override
  public void reset() {
    source.reset();
  }

  @Override
  public void close() {
    source.close();
  }

  @Override
  public String toString() {
    return instanceName + ':' + source;
  }

}

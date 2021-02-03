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

package com.rapiddweller.benerator.engine.parser.xml;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.util.UnsafeGenerator;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.common.Assert;
import com.rapiddweller.common.context.ContextAware;
import com.rapiddweller.model.data.Entity;

/**
 * Mock implementation of {@link Generator} and {@link ContextAware}.<br/><br/>
 * Created: 16.02.2010 12:16:33
 *
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class GeneratorMock extends UnsafeGenerator<Entity> {

  private final BeneratorContext context;

  /**
   * Instantiates a new Generator mock.
   *
   * @param context the context
   */
  public GeneratorMock(BeneratorContext context) {
    this.context = context;
  }

  @Override
  public Class<Entity> getGeneratedType() {
    return Entity.class;
  }

  @Override
  public ProductWrapper<Entity> generate(ProductWrapper<Entity> wrapper) {
    Assert.notNull(context, "context");
    return wrapper.wrap(new Entity("Dummy", context.getLocalDescriptorProvider()));
  }

}

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

package com.rapiddweller.benerator.composite;

import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.common.Assert;
import com.rapiddweller.script.Expression;

/**
 * {@link ComponentBuilder} which executes only if a condition expression evaluates to 'true'.<br/><br/>
 * Created: 11.10.2010 11:15:14
 * @param <E> the type parameter
 * @author Volker Bergmann
 * @since 0.6.4
 */
public class ConditionalComponentBuilder<E> extends ComponentBuilderProxy<E> {

  private final Expression<?> condition;

  public ConditionalComponentBuilder(ComponentBuilder<E> source, Expression<?> condition) {
    super(source);
    Assert.notNull(condition, "condition");
    this.condition = condition;
  }

  @Override
  public boolean execute(BeneratorContext context) {
    Object conditionResult = condition.evaluate(context);
    if (conditionResult == null) {
      throw new IllegalArgumentException("Condition resolves to null: " + condition);
    }
    if (!(conditionResult instanceof Boolean)) {
      throw new IllegalArgumentException("Condition does not resolve to a boolean value: " + condition);
    }
    if ((boolean) conditionResult) {
      return source.execute(context);
    } else {
      return true;
    }
  }

}

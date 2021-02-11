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

package com.rapiddweller.benerator.engine.statement;

import com.rapiddweller.benerator.Consumer;
import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.Statement;
import com.rapiddweller.benerator.wrapper.ProductWrapper;

/**
 * {@link Statement} that consumes the current entity of a {@link GeneratorContext} using a {@link Consumer}.<br/><br/>
 * Created: 01.09.2011 15:51:27
 *
 * @author Volker Bergmann
 * @since 0.7.0
 */
public class ConsumptionStatement implements Statement {

  private final Consumer consumer;
  private final boolean start;
  private final boolean finish;

  /**
   * Instantiates a new Consumption statement.
   *
   * @param consumer the consumer
   * @param start    the start
   * @param finish   the finish
   */
  public ConsumptionStatement(Consumer consumer, boolean start, boolean finish) {
    this.consumer = consumer;
    this.start = start;
    this.finish = finish;
  }

  @Override
  public boolean execute(BeneratorContext context) {
    if (consumer != null) {
      ProductWrapper<?> product = context.getCurrentProduct();
      if (start) {
        consumer.startConsuming(product);
      }
      if (finish) {
        consumer.finishConsuming(product);
      }
    }
    return true;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "[" + consumer + "]";
  }

}

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

import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.Statement;
import com.rapiddweller.common.Expression;

/**
 * {@link Statement} implementation that evaluates an {@link Expression}
 * which returns a Task and executes the returned Task.<br/><br/>
 * Created: 27.10.2009 16:09:20
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class LazyStatement extends StatementProxy {

  private final Expression<Statement> statementExpression;

  public LazyStatement(Expression<Statement> statementExpression) {
    super(null);
    this.statementExpression = statementExpression;
  }

  public Expression<Statement> getTargetExpression() {
    return statementExpression;
  }

  @Override
  public Statement getRealStatement(BeneratorContext context) {
    if (this.realStatement == null) {
      this.realStatement = statementExpression.evaluate(context);
    }
    return this.realStatement;
  }

  @Override
  public boolean execute(BeneratorContext context) {
    return getRealStatement(context).execute(context);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + '(' + (realStatement != null ? realStatement : statementExpression) + ')';
  }

}

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
import com.rapiddweller.script.Expression;

import java.io.Closeable;
import java.io.IOException;

/**
 * {@link CompositeStatement} that executes it parts
 * only if a condition is matched.<br/><br/>
 * Created: 19.02.2010 09:13:30
 *
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class IfStatement extends ConditionStatement {

  private Statement thenStatement;
  private Statement elseStatement;

  /**
   * Instantiates a new If statement.
   *
   * @param condition the condition
   */
  public IfStatement(Expression<Boolean> condition) {
    super(condition);
  }

  /**
   * Instantiates a new If statement.
   *
   * @param condition     the condition
   * @param thenStatement the then statement
   */
  public IfStatement(Expression<Boolean> condition, Statement thenStatement) {
    this(condition, thenStatement, null);
  }

  /**
   * Instantiates a new If statement.
   *
   * @param condition     the condition
   * @param thenStatement the then statement
   * @param elseStatement the else statement
   */
  public IfStatement(Expression<Boolean> condition, Statement thenStatement, Statement elseStatement) {
    super(condition);
    setThenStatement(thenStatement);
    setElseStatement(elseStatement);
  }

  @Override
  public boolean execute(BeneratorContext context) {
    if (condition.evaluate(context)) {
      return thenStatement.execute(context);
    } else if (elseStatement != null) {
      return elseStatement.execute(context);
    }
    return true;
  }

  /**
   * Sets then statement.
   *
   * @param thenStatement the then statement
   */
  public void setThenStatement(Statement thenStatement) {
    this.thenStatement = thenStatement;
  }

  /**
   * Sets else statement.
   *
   * @param elseStatement the else statement
   */
  public void setElseStatement(Statement elseStatement) {
    this.elseStatement = elseStatement;
  }

  @Override
  public void close() throws IOException {
    if (thenStatement instanceof Closeable) {
      ((Closeable) thenStatement).close();
    }
    if (elseStatement instanceof Closeable) {
      ((Closeable) elseStatement).close();
    }
  }

}

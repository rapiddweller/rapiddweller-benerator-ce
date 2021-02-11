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

import com.rapiddweller.benerator.BeneratorError;
import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.Statement;
import com.rapiddweller.script.Expression;
import com.rapiddweller.script.expression.ExpressionUtil;

/**
 * {@link Statement} implementation that raises a {@link BeneratorError}
 * and provides a result <code>code</code> for the operating system.<br/><br/>
 * Created: 12.01.2011 09:04:26
 *
 * @author Volker Bergmann
 * @since 0.6.4
 */
public class ErrorStatement implements Statement {

  /**
   * The Message ex.
   */
  public final Expression<String> messageEx;
  /**
   * The Code ex.
   */
  public final Expression<Integer> codeEx;

  /**
   * Instantiates a new Error statement.
   *
   * @param messageEx the message ex
   * @param codeEx    the code ex
   */
  public ErrorStatement(Expression<String> messageEx, Expression<Integer> codeEx) {
    this.messageEx = messageEx;
    this.codeEx = codeEx;
  }

  @Override
  public boolean execute(BeneratorContext context) {
    String message = ExpressionUtil.evaluate(messageEx, context);
    Integer code = ExpressionUtil.evaluate(codeEx, context);
    if (code == null) {
      code = 0;
    }
    throw new BeneratorError(message, code);
  }

}

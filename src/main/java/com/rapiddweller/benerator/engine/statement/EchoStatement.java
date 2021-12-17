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
import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.common.SpeechUtil;
import com.rapiddweller.common.Expression;
import com.rapiddweller.script.expression.ExpressionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Prints out a message to the console.<br/><br/>
 * Created at 22.07.2009 07:13:28
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class EchoStatement implements Statement {

  private static final Logger logger = LoggerFactory.getLogger(EchoStatement.class);

  private final Expression<String> messageEx;
  private final Expression<EchoType> typeEx;

  public EchoStatement(Expression<String> messageEx, Expression<EchoType> typeEx) {
    this.messageEx = messageEx;
    this.typeEx = typeEx;
  }

  public Expression<String> getExpression() {
    return messageEx;
  }

  @Override
  public boolean execute(BeneratorContext context) {
    String message = ExpressionUtil.evaluate(messageEx, context);
    EchoType type = ExpressionUtil.evaluate(typeEx, context);
    if (type == EchoType.speech) {
      if (SpeechUtil.speechSupported()) {
        SpeechUtil.say(message);
      } else {
        consoleOut(message);
      }
    } else if (type == EchoType.console) {
      consoleOut(message);
    } else {
      // this is not supposed to happen since syntax has been checked while parsing
      throw BeneratorExceptionFactory.getInstance().internalError("Illegal echo type", null);
    }
    return true;
  }

  private void consoleOut(String message) {
    logger.debug(message);
    System.out.println(message);
  }

}

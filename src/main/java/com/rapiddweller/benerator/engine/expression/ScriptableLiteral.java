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

package com.rapiddweller.benerator.engine.expression;

import com.rapiddweller.common.Context;
import com.rapiddweller.common.converter.LiteralParser;
import com.rapiddweller.script.expression.UnaryExpression;

/**
 * Expression that evaluates a text as a literal; if it encounters a script expression
 * (like {settings.base}) it evaluates the script and parses its result.<br/><br/>
 * Created at 23.07.2009 14:34:42
 * @author Volker Bergmann
 * @see LiteralParser
 * @since 0.6.0
 */
public class ScriptableLiteral extends UnaryExpression<Object> {

  public ScriptableLiteral(String textOrScript) {
    super(ScriptableLiteral.class.getSimpleName(), new ScriptableExpression(textOrScript, null));
  }

  @Override
  public Object evaluate(Context context) {
    Object feed = term.evaluate(context);
    if (feed instanceof String) {
      return LiteralParser.parse((String) feed);
    } else {
      return feed;
    }
  }

}

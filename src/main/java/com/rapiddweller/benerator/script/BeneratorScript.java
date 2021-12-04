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

package com.rapiddweller.benerator.script;

import com.rapiddweller.common.Context;
import com.rapiddweller.format.script.AbstractScript;
import com.rapiddweller.format.script.Script;
import com.rapiddweller.format.script.ScriptException;
import com.rapiddweller.script.Expression;

/**
 * {@link Script} implementation for BeneratorScript.<br/><br/>
 * Created at 09.10.2009 06:48:01
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class BeneratorScript extends AbstractScript {

  private final Expression<?> expression;
  private final String text;

  public BeneratorScript(Expression<?> expression, String text) {
    this.expression = expression;
    this.text = text;
  }

  @Override
  public Object evaluate(Context context) throws ScriptException {
    if (expression == null) {
      return null;
    }
    try {
      return expression.evaluate(context);
    } catch (Exception e) {
      throw new ScriptException("Error in script: " + text, e);
    }
  }

  @Override
  public String toString() {
    return text;
  }

}

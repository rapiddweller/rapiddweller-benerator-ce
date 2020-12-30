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
import com.rapiddweller.format.script.Script;
import com.rapiddweller.format.script.ScriptUtil;
import com.rapiddweller.script.Expression;
import com.rapiddweller.script.expression.ConstantExpression;
import com.rapiddweller.script.expression.DynamicExpression;

/**
 * Expression that evaluates a script.<br/><br/>
 * Created: 27.10.2009 13:48:11
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class ScriptExpression<E> extends DynamicExpression<E> {

	private final Script script;
	private final Expression<E> defaultValueExpression;

    public ScriptExpression(String script) {
    	this(ScriptUtil.parseScriptText(script), (E) null);
    }

    public ScriptExpression(Script script) {
    	this(script, (E) null);
    }

    public ScriptExpression(Script script, E defaultValue) {
    	this(script, (defaultValue != null ? new ConstantExpression<>(defaultValue) : null));
    }

    private ScriptExpression(Script script, Expression<E> defaultValueExpression) {
    	this.script = script;
    	this.defaultValueExpression = defaultValueExpression;
    }
    
    public static <T> Expression<T> createWithDefaultExpression(
    		Script script, Expression<T> defaultValueExpression) {
    	return new ScriptExpression<>(script, defaultValueExpression);
    }

	@Override
	@SuppressWarnings("unchecked")
    public E evaluate(Context context) {
		if (script == null)
			return (defaultValueExpression != null ? defaultValueExpression.evaluate(context) : null);
		else
			return (E) script.evaluate(context);
    }

	@Override
	public String toString() {
		return script.toString();
	}
	
}

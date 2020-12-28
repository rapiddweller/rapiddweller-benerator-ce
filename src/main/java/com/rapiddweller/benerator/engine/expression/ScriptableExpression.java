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

import com.rapiddweller.commons.Context;
import com.rapiddweller.formats.script.ScriptUtil;
import com.rapiddweller.script.Expression;
import com.rapiddweller.script.expression.ConstantExpression;
import com.rapiddweller.script.expression.DynamicExpression;

/**
 * Evaluates a string which may be a script (indicated by {}).<br/><br/>
 * Created: 19.02.2010 10:39:29
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class ScriptableExpression extends DynamicExpression<Object> {

	private final String scriptOrText;
	private final Expression<?> defaultValueExpression;
	private final boolean isScript;

    public ScriptableExpression(String scriptOrText, Object defaultValue) {
    	this(scriptOrText, (defaultValue != null ? new ConstantExpression<>(defaultValue) : null));
    }

    private ScriptableExpression(String scriptOrText, Expression<?> defaultValueExpression) {
    	this.defaultValueExpression = defaultValueExpression;
    	this.isScript = ScriptUtil.isScript(scriptOrText);
		this.scriptOrText = scriptOrText;
    }
    
    public static Expression<?> createWithDefaultExpression(
    		String scriptOrText, Expression<?> defaultValueExpression) {
    	return new ScriptableExpression(scriptOrText, defaultValueExpression);
    }

    @Override
	public Object evaluate(Context context) {
    	Object result;
		if (scriptOrText == null)
			result = (defaultValueExpression != null ? defaultValueExpression.evaluate(context) : null);
		else if (isScript)
			result = ScriptUtil.evaluate(scriptOrText, context);
		else
			result = scriptOrText;
		return result;
    }

	@Override
	public String toString() {
		return (ScriptUtil.isScript(scriptOrText) ? scriptOrText : "'" + scriptOrText + "'");
	}
	
}

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

import com.rapiddweller.format.script.Script;
import com.rapiddweller.format.script.ScriptUtil;
import com.rapiddweller.script.Expression;
import com.rapiddweller.script.expression.TypeConvertingExpression;

/**
 * {@link Expression} implementation that evaluates a script.<br/>
 * <br/>
 * Created at 22.07.2009 07:19:44
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class TypedScriptExpression<E> extends TypeConvertingExpression<E> {
	
    public TypedScriptExpression(String script) {
    	this(script, null);
    }

    @SuppressWarnings("unchecked")
    public TypedScriptExpression(Script script) {
    	this(script, (Class<E>) Object.class);
    }

    public TypedScriptExpression(String script, Class<E> resultType) {
    	this(ScriptUtil.parseScriptText(script), resultType);
    }

    public TypedScriptExpression(Script script, Class<E> resultType) {
    	this(script, resultType, (E) null);
    }

    public TypedScriptExpression(Script script, Class<E> resultType, E defaultValue) {
    	super(new ScriptExpression<Object>(script, defaultValue), resultType);
    }

    public TypedScriptExpression(Script script, Class<E> resultType, Expression<?> defaultValue) {
    	super(ScriptExpression.createWithDefaultExpression(script, defaultValue), resultType);
    }

}

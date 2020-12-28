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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.Statement;
import com.rapiddweller.script.Expression;

/**
 * 'While' loop statement.<br/><br/>
 * Created: 19.02.2010 09:15:11
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class WhileStatement extends ConditionStatement {
	
	private SequentialStatement statements;

	public WhileStatement(Expression<Boolean> condition) {
	    this(condition, new ArrayList<>());
    }

	public WhileStatement(Expression<Boolean> condition, List<Statement> statements) {
	    super(condition);
	    setSubStatements(statements);
    }
	
	public void setSubStatements(List<Statement> statements) {
	    this.statements = new SequentialStatement(statements);
	}

	@Override
	public boolean execute(BeneratorContext context) {
	    while (condition.evaluate(context))
	    	if (!statements.execute(context))
	    		return false;
	    return true;
	}

	@Override
	public void close() throws IOException {
		statements.close();
	}
	
}

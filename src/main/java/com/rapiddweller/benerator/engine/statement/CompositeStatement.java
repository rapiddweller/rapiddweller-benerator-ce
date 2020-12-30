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

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.rapiddweller.benerator.engine.Statement;
import com.rapiddweller.common.Element;
import com.rapiddweller.common.Visitor;

/**
 * Combines other statements to a composite statement.<br/><br/>
 * Created: 27.10.2009 15:59:21
 * @since 0.6.0
 * @author Volker Bergmann
 */
public abstract class CompositeStatement extends AbstractStatement implements Closeable, Element<Statement> {
	
	protected List<Statement> subStatements = new ArrayList<>();

	public CompositeStatement() {
		this(null);
    }

	public CompositeStatement(List<Statement> subStatements) {
		this.subStatements = (subStatements != null ? subStatements : new ArrayList<>());
    }

	public List<Statement> getSubStatements() {
		return subStatements;
	}
	
	public void addSubStatement(Statement subStatement) {
		subStatements.add(subStatement);
	}
	
	public void setSubStatements(List<Statement> subStatements) {
		this.subStatements = subStatements;
	}
	
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
    public void accept(Visitor<Statement> visitor) {
		visitor.visit(this);
	    for (Statement subStatement : subStatements)
	    	if (subStatement instanceof Element)
	    		((Element) subStatement).accept(visitor);
	    	else
	    		visitor.visit(subStatement);
    }

	@Override
	public void close() throws IOException {
		for (Statement subStatement : subStatements)
			if (subStatement instanceof Closeable)
				((Closeable) subStatement).close();
	}
	
}

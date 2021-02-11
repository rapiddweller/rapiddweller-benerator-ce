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

import java.util.List;

/**
 * Executes all sub statements sequentially.<br/><br/>
 * Created: 20.02.2010 08:00:24
 *
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class SequentialStatement extends CompositeStatement {

  /**
   * Instantiates a new Sequential statement.
   */
  public SequentialStatement() {
    this(null);
  }

  /**
   * Instantiates a new Sequential statement.
   *
   * @param subStatements the sub statements
   */
  public SequentialStatement(List<Statement> subStatements) {
    super(subStatements);
  }

  @Override
  public boolean execute(BeneratorContext context) {
    return executeSubStatements(context);
  }

  /**
   * Execute sub statements boolean.
   *
   * @param context the context
   * @return the boolean
   */
  protected boolean executeSubStatements(BeneratorContext context) {
    for (Statement subStatement : subStatements) {
      if (!subStatement.execute(context)) {
        return false;
      }
    }
    return true;
  }

}

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
import com.rapiddweller.common.exception.ExitCodes;

/**
 * {@link Statement} implementation that raises an
 * {@link com.rapiddweller.common.OperationFailed} exception
 * and provides a result <code>code</code> for the operating system.<br/><br/>
 * Created: 12.01.2011 09:04:26
 * @author Volker Bergmann
 * @since 0.6.4
 */
public class ErrorStatement implements Statement {

  public final String id;
  public final String message;
  public final int exitCode;

  public ErrorStatement(String id, Integer exitCode, String message) {
    this.id = id;
    this.message = message;
    this.exitCode = (exitCode != null ? exitCode : ExitCodes.MISCELLANEOUS_ERROR);
  }

  @Override
  public boolean execute(BeneratorContext context) {
    throw BeneratorExceptionFactory.getInstance().operationFailed(message, null, id, exitCode);
  }

}

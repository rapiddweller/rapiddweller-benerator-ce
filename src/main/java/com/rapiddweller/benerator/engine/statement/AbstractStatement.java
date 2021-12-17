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

import com.rapiddweller.benerator.engine.Statement;
import com.rapiddweller.benerator.engine.expression.CachedExpression;
import com.rapiddweller.common.Assert;
import com.rapiddweller.common.BeanUtil;
import com.rapiddweller.common.Context;
import com.rapiddweller.common.ErrorHandler;
import com.rapiddweller.common.Level;
import com.rapiddweller.common.Expression;
import com.rapiddweller.script.expression.ExpressionUtil;

/**
 * Abstract implementation of the Statement interface.<br/><br/>
 * Created: 27.10.2009 20:16:20
 *
 * @author Volker Bergmann
 * @since 0.5.0
 */
public abstract class AbstractStatement implements Statement {

  private final Expression<ErrorHandler> errorHandler;

  // constructors ----------------------------------------------------------------------------------------------------

  protected AbstractStatement() {
    this(null);
  }

  protected AbstractStatement(Expression<ErrorHandler> errorHandler) {
    this.errorHandler = errorHandler;
  }

  // Task interface --------------------------------------------------------------------------------------------------

  public ErrorHandler getErrorHandler(Context context) {
    if (errorHandler == null) {
      return new ErrorHandler(getClass().getName(), Level.fatal);
    }
    return errorHandler.evaluate(context);
  }

  // helpers ---------------------------------------------------------------------------------------------------------

  protected void handleError(String message, Context context) {
    getErrorHandler(context).handleError(message);
  }

  protected void handleError(String message, Context context, Throwable t) {
    getErrorHandler(context).handleError(message, t);
  }

  protected static <T> Expression<T> cache(Expression<T> expression) {
    return (expression != null ? new CachedExpression<>(expression) : null);
  }

  protected static Object setIfNotNull(Expression<?> expression, Context context, Object bean, String propertyName) {
    return set(expression, context, bean, propertyName, SetMode.IF_NOT_NULL);
  }

  protected static Object setAssertNotNull(Expression<?> expression, Context context, Object bean, String propertyName) {
    return set(expression, context, bean, propertyName, SetMode.ASSERT_NOT_NULL);
  }

  protected static Object set(Expression<?> expression, Context context, Object bean, String propertyName) {
    return set(expression, context, bean, propertyName, SetMode.SIMPLE);
  }

  protected static Object set(Expression<?> expression, Context context, Object bean, String propertyName, SetMode mode) {
    Object value = ExpressionUtil.evaluate(expression, context);
    switch (mode) {
      case ASSERT_NOT_NULL:
        Assert.notNull(value, propertyName);
        BeanUtil.setPropertyValue(bean, propertyName, value, false);
        break;
      case IF_NOT_NULL:
        if (value != null)
          BeanUtil.setPropertyValue(bean, propertyName, value, false);
        break;
      default:
        BeanUtil.setPropertyValue(bean, propertyName, value, false);
        break;
    }
    return value;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }

  enum SetMode {
    SIMPLE, IF_NOT_NULL, ASSERT_NOT_NULL
  }
}

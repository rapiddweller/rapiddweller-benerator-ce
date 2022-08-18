/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.expression;

import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.common.Context;
import com.rapiddweller.common.Expression;

/**
 * {@link Expression} which resolves to the 'maxCount' value defined in the root &lt;setup&gt; element.<br/><br/>
 * Created: 20.12.2021 00:33:35
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class GlobalMaxCountExpression implements Expression<Long> {

  @Override
  public boolean isConstant() {
    return true;
  }

  @Override
  public Long evaluate(Context context) {
    return ((BeneratorContext) context).getMaxCount();
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }

}
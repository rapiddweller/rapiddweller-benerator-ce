/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.attr;

import com.rapiddweller.benerator.engine.expression.context.DefaultPageSizeExpression;
import com.rapiddweller.benerator.engine.parser.string.PageSizeParser;
import com.rapiddweller.format.xml.AttrInfo;
import com.rapiddweller.script.Expression;

import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_PAGESIZE;

/**
 * {@link AttrInfo} for the 'pageSize' attribute falling back to the 'defaultPageSize' property
 * of the {@link com.rapiddweller.benerator.engine.BeneratorRootContext}.<br/><br/>
 * Created: 10.12.2021 19:33:54
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class PageSizeAttribute extends AttrInfo<Expression<Long>> {
  public PageSizeAttribute(String errorId) {
    super(ATT_PAGESIZE, false, errorId, new PageSizeParser(), new DefaultPageSizeExpression());
  }
}

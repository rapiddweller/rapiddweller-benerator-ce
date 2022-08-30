/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.string;

import com.rapiddweller.benerator.engine.expression.context.DefaultPageSizeExpression;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.common.parser.AbstractParser;
import com.rapiddweller.common.parser.NonNegativeLongParser;
import com.rapiddweller.common.parser.Parser;
import com.rapiddweller.common.Expression;

/**
 * Parses a page size.<br/><br/>
 * Created: 10.12.2021 18:09:42
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class PageSizeParser extends AbstractParser<Expression<Long>> {

  private final Parser<Expression<Long>> realParser;
  private final DefaultPageSizeExpression defaultExpression;

  public PageSizeParser() {
    super("page size");
    this.realParser = new ScriptableParser<>(new NonNegativeLongParser());
    this.defaultExpression = new DefaultPageSizeExpression();
  }

  @Override
  protected Expression<Long> parseImpl(String spec) {
    if (StringUtil.isEmpty(spec)) {
      return defaultExpression;
    } else {
      return realParser.parse(spec);
    }
  }

  @Override
  public String getDescription() {
    return "page size";
  }

}

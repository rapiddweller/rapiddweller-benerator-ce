/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.xml;

import com.rapiddweller.benerator.BeneratorErrorIds;
import com.rapiddweller.benerator.engine.parser.attr.ConsumerAttribute;
import com.rapiddweller.benerator.engine.parser.attr.CountAttribute;
import com.rapiddweller.benerator.engine.parser.attr.CountDistributionAttribute;
import com.rapiddweller.benerator.engine.parser.attr.CountGranularityAttribute;
import com.rapiddweller.benerator.engine.parser.attr.EncodingAttribute;
import com.rapiddweller.benerator.engine.parser.attr.ErrorHandlerAttribute;
import com.rapiddweller.benerator.engine.parser.attr.MinMaxCountAttribute;
import com.rapiddweller.benerator.engine.parser.attr.NameAttribute;
import com.rapiddweller.benerator.engine.parser.attr.NullQuotaAttribute;
import com.rapiddweller.benerator.engine.parser.attr.PageSizeAttribute;
import com.rapiddweller.benerator.engine.parser.attr.ScriptableBooleanAttribute;
import com.rapiddweller.benerator.engine.parser.attr.SourceFormattedAttribute;
import com.rapiddweller.benerator.engine.parser.attr.SourceScriptedAttribute;
import com.rapiddweller.benerator.engine.parser.attr.ThreadsAttribute;
import com.rapiddweller.benerator.engine.parser.string.IdParser;
import com.rapiddweller.benerator.engine.parser.string.ScriptParser;
import com.rapiddweller.common.Expression;
import com.rapiddweller.common.parser.BooleanParser;
import com.rapiddweller.common.parser.CharacterParser;
import com.rapiddweller.common.parser.NonNegativeLongParser;
import com.rapiddweller.format.xml.AttrInfoSupport;
import com.rapiddweller.format.xml.AttrInfo;
import org.w3c.dom.Element;

import static com.rapiddweller.benerator.BeneratorErrorIds.*;
import static com.rapiddweller.benerator.engine.DescriptorConstants.*;

/**
 * Parses an XML &LT;iterate&GT; element.<br/><br/>
 * Created: 29.11.2021 15:49:15
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class IterateParser extends AbstractGenIterParser {

  protected static final AttrInfo<String> NAME = new NameAttribute(SYN_ITERATE_NAME, false, false);
  protected static final AttrInfo<String> TYPE = new AttrInfo<>(ATT_TYPE, false, BeneratorErrorIds.SYN_ITERATE_TYPE, new IdParser());
  protected static final AttrInfo<String> SOURCE = new AttrInfo<>(ATT_SOURCE, true, BeneratorErrorIds.SYN_ITERATE_SOURCE, null);

  protected static final CountAttribute COUNT = new CountAttribute(BeneratorErrorIds.SYN_ITERATE_TYPE, false);
  private static final MinMaxCountAttribute MIN_COUNT = new MinMaxCountAttribute(ATT_MIN_COUNT, SYN_ITERATE_MIN_COUNT);
  private static final MinMaxCountAttribute MAX_COUNT = new MinMaxCountAttribute(ATT_MAX_COUNT, SYN_ITERATE_MAX_COUNT);
  private static final CountGranularityAttribute COUNT_GRANULARITY = new CountGranularityAttribute(SYN_GENERATE_COUNT_GRANULARITY);
  private static final CountDistributionAttribute COUNT_DISTRIBUTION = new CountDistributionAttribute(SYN_ITERATE_COUNT_DIST);

  private static final ThreadsAttribute THREADS = new ThreadsAttribute(BeneratorErrorIds.SYN_ITERATE_THREADS);
  private static final PageSizeAttribute PAGESIZE = new PageSizeAttribute(SYN_ITERATE_PAGE_SIZE);
  private static final ErrorHandlerAttribute ON_ERROR = new ErrorHandlerAttribute(SYN_ITERATE_ON_ERROR);
  private static final ScriptableBooleanAttribute STATS = new ScriptableBooleanAttribute(ATT_STATS, false, SYN_ITERATE_STATS, false);
  private static final AttrInfo<String> TEMPLATE = new AttrInfo<>(ATT_TEMPLATE, false, SYN_ITERATE_TEMPLATE, null, null);
  private static final ConsumerAttribute CONSUMER = new ConsumerAttribute(SYN_ITERATE_CONSUMER);
  private static final AttrInfo<String> SCOPE = new AttrInfo<>(ATT_SCOPE, false, BeneratorErrorIds.SYN_ITERATE_SCOPE, null);

  private static final AttrInfo<String> VALIDATOR = new AttrInfo<>(ATT_VALIDATOR, false, BeneratorErrorIds.SYN_ITERATE_VALIDATOR, null);
  private static final AttrInfo<String> CONVERTER = new AttrInfo<>(ATT_CONVERTER, false, BeneratorErrorIds.SYN_ITERATE_CONVERTER, null);
  private static final NullQuotaAttribute NULL_QUOTA = new NullQuotaAttribute(SYN_ITERATE_NULL_QUOTA);
  private static final AttrInfo<Boolean> UNIQUE = new AttrInfo<>(ATT_UNIQUE, false, SYN_ITERATE_UNIQUE, new BooleanParser(), "false");
  private static final AttrInfo<String> DISTRIBUTION = new AttrInfo<>(ATT_DISTRIBUTION, false, SYN_ITERATE_DISTRIBUTION, null, null);
  private static final AttrInfo<Boolean> CYCLIC = new AttrInfo<>(ATT_CYCLIC, false, SYN_ITERATE_CYCLIC, new BooleanParser(), "false");
  private static final AttrInfo<Long> OFFSET = new AttrInfo<>(ATT_OFFSET, false, SYN_ITERATE_OFFSET, new NonNegativeLongParser(), "0");
  protected static final AttrInfo<String> SENSOR = new AttrInfo<>(ATT_SENSOR, false, BeneratorErrorIds.SYN_ITERATE_SENSOR, null, null);

  protected static final AttrInfo<String> DATASET = new AttrInfo<>(ATT_DATASET, false, BeneratorErrorIds.SYN_ITERATE_DATASET, null);
  protected static final AttrInfo<String> NESTING = new AttrInfo<>(ATT_NESTING, false, BeneratorErrorIds.SYN_ITERATE_NESTING, null);
  protected static final AttrInfo<String> LOCALE  = new AttrInfo<>(ATT_LOCALE, false, BeneratorErrorIds.SYN_ITERATE_LOCALE, null);
  protected static final EncodingAttribute ENCODING = new EncodingAttribute(SYN_ITERATE_ENCODING);
  protected static final AttrInfo<Character> SEPARATOR = new AttrInfo<>(ATT_SEPARATOR, false, BeneratorErrorIds.SYN_ITERATE_SEPARATOR, new CharacterParser());
  protected static final SourceFormattedAttribute FORMAT = new SourceFormattedAttribute(SYN_ITERATE_FORMAT);
  protected static final SourceScriptedAttribute SOURCE_SCRIPTED = new SourceScriptedAttribute(SYN_ITERATE_SOURCE_SCRIPTED);

  protected static final AttrInfo<String> SEGMENT = new AttrInfo<>(ATT_SEGMENT, false, BeneratorErrorIds.SYN_ITERATE_SEGMENT, null);
  protected static final AttrInfo<Boolean> ROW_BASED = new AttrInfo<>(ATT_ROW_BASED, false, BeneratorErrorIds.SYN_ITERATE_ROW_BASED, new BooleanParser());
  protected static final AttrInfo<String> EMPTY_MARKER = new AttrInfo<>(ATT_EMPTY_MARKER, false, BeneratorErrorIds.SYN_ITERATE_EMPTY_MARKER, null);
  protected static final AttrInfo<String> SELECTOR = new AttrInfo<>(ATT_SELECTOR, false, BeneratorErrorIds.SYN_ITERATE_SELECTOR, null);
  protected static final AttrInfo<String> SUB_SELECTOR = new AttrInfo<>(ATT_SUB_SELECTOR, false, BeneratorErrorIds.SYN_ITERATE_SUB_SELECTOR, null);
  protected static final AttrInfo<Expression<Boolean>> FILTER = new AttrInfo<>(ATT_FILTER, false, SYN_ITERATE_FILTER, new ScriptParser<>(Boolean.class));

  private static final AttrInfoSupport ATTR_INFO = new AttrInfoSupport(BeneratorErrorIds.SYN_ITERATE_ILLEGAL_ATTR,
      NAME, TYPE, SOURCE, COUNT, MIN_COUNT, MAX_COUNT, COUNT_GRANULARITY, COUNT_DISTRIBUTION,
      THREADS, PAGESIZE, ON_ERROR, STATS, TEMPLATE, CONSUMER, SCOPE,
      VALIDATOR, CONVERTER, NULL_QUOTA, UNIQUE, DISTRIBUTION, CYCLIC, OFFSET, SENSOR,
      DATASET, NESTING, LOCALE, ENCODING, SEPARATOR, FORMAT, SOURCE_SCRIPTED,
      SEGMENT, ROW_BASED, EMPTY_MARKER, SELECTOR, SUB_SELECTOR, FILTER);

  public IterateParser() {
    super(EL_ITERATE, ATTR_INFO);
  }

  @Override
  protected String parseName(Element element) {
    return NAME.parse(element);
  }

  @Override
  protected String parseType(Element element) {
    return TYPE.parse(element);
  }

  @Override
  protected Expression<Integer> parseThreadsAttr(Element element) {
    return THREADS.parse(element);
  }

  @Override
  protected Expression<Boolean> parseStats(Element element) {
    return STATS.parse(element);
  }

  @Override
  protected Expression<Long> parseMinCount(Element element) {
    return MIN_COUNT.parse(element);
  }

  @Override
  protected Expression<String> getCountDistribution(Element element) {
    return COUNT_DISTRIBUTION.parse(element);
  }

  @Override
  protected Expression<Long> getCountGranularity(Element element) {
    return COUNT_GRANULARITY.parse(element);
  }

  @Override
  protected Expression<Long> getMaxCount(Element element) {
    return MAX_COUNT.parse(element);
  }

  @Override
  protected Expression<Long> getMinCount(Element element) {
    return MIN_COUNT.parse(element);
  }

  @Override
  protected Expression<Long> getCount(Element element) {
    return COUNT.parse(element);
  }

  @Override
  protected String parseSensor(Element element) {
    return SENSOR.parse(element);
  }

}

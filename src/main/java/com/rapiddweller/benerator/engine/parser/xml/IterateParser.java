/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.xml;

import com.rapiddweller.benerator.BeneratorErrorIds;
import com.rapiddweller.benerator.engine.parser.attr.EncodingAttribute;
import com.rapiddweller.benerator.engine.parser.attr.SourceAttribute;
import com.rapiddweller.benerator.engine.parser.attr.SourceFormattedAttribute;
import com.rapiddweller.benerator.engine.parser.attr.SourceScriptedAttribute;
import com.rapiddweller.benerator.engine.parser.string.ScriptParser;
import com.rapiddweller.common.Expression;
import com.rapiddweller.common.parser.BooleanParser;
import com.rapiddweller.common.parser.CharacterParser;
import com.rapiddweller.format.xml.AttrInfoSupport;
import com.rapiddweller.format.xml.AttrInfo;

import static com.rapiddweller.benerator.BeneratorErrorIds.*;
import static com.rapiddweller.benerator.engine.DescriptorConstants.*;

/**
 * Parses an XML &LT;iterate&GT; element.<br/><br/>
 * Created: 29.11.2021 15:49:15
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class IterateParser extends AbstractGenIterParser {

  protected static final AttrInfo<String> SOURCE = new SourceAttribute(BeneratorErrorIds.SYN_ITERATE_SOURCE, true);

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

  public IterateParser() {
    super(EL_ITERATE, null);
    this.nameAttr.setErrorId(SYN_ITERATE_NAME);
    this.typeAttr.setErrorId(SYN_ITERATE_TYPE);

    this.countAttr.setErrorId(SYN_ITERATE_COUNT);
    this.minCountAttr.setErrorId(SYN_ITERATE_MIN_COUNT);
    this.maxCountAttr.setErrorId(SYN_ITERATE_MAX_COUNT);
    this.countGranularityAttr.setErrorId(SYN_ITERATE_COUNT_GRANULARITY);
    this.countDistributionAttr.setErrorId(SYN_ITERATE_COUNT_DIST);

    this.threadsAttr.setErrorId(SYN_ITERATE_THREADS);
    this.statsAttr.setErrorId(SYN_ITERATE_STATS);
    this.sensorAttr.setErrorId(SYN_ITERATE_SENSOR);

    this.pagesizeAttr.setErrorId(SYN_ITERATE_PAGE_SIZE);
    this.onErrorAttr.setErrorId(SYN_ITERATE_ON_ERROR);
    this.templateAttr.setErrorId(SYN_ITERATE_TEMPLATE);
    this.consumerAttr.setErrorId(SYN_ITERATE_CONSUMER);
    this.scopeAttr.setErrorId(SYN_ITERATE_SCOPE);

    this.validatorAttr.setErrorId(SYN_ITERATE_VALIDATOR);
    this.converterAttr.setErrorId(SYN_ITERATE_CONVERTER);
    this.nullQuotaAttr.setErrorId(SYN_ITERATE_NULL_QUOTA);
    this.uniqueAttr.setErrorId(SYN_ITERATE_UNIQUE);
    this.distributionAttr.setErrorId(SYN_ITERATE_DISTRIBUTION);
    this.cyclicAttr.setErrorId(SYN_ITERATE_CYCLIC);
    this.offsetAttr.setErrorId(SYN_ITERATE_OFFSET);

    this.attrSupport = new AttrInfoSupport(BeneratorErrorIds.SYN_ITERATE_ILLEGAL_ATTR,
        nameAttr, typeAttr, SOURCE,
        countAttr, minCountAttr, maxCountAttr, countGranularityAttr, countDistributionAttr,
        threadsAttr, pagesizeAttr, onErrorAttr, statsAttr, templateAttr, consumerAttr, scopeAttr,
        validatorAttr, converterAttr, nullQuotaAttr, uniqueAttr, distributionAttr, cyclicAttr, offsetAttr, sensorAttr,
        DATASET, NESTING, LOCALE, ENCODING, SEPARATOR, FORMAT, SOURCE_SCRIPTED,
        SEGMENT, ROW_BASED, EMPTY_MARKER, SELECTOR, SUB_SELECTOR, FILTER);
  }

}

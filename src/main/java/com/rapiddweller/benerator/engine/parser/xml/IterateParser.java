/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.xml;

import com.rapiddweller.benerator.BeneratorErrorIds;
import com.rapiddweller.benerator.engine.parser.attr.NameAttribute;
import com.rapiddweller.format.xml.AttrInfoSupport;
import com.rapiddweller.format.xml.AttrInfo;

import static com.rapiddweller.benerator.BeneratorErrorIds.SYN_ITERATE_NAME;
import static com.rapiddweller.benerator.engine.DescriptorConstants.*;

/**
 * Parses an XML &LT;iterate&GT; element.<br/><br/>
 * Created: 29.11.2021 15:49:15
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class IterateParser extends AbstractGenIterParser {

  private static final AttrInfo<String> NAME = new NameAttribute(SYN_ITERATE_NAME, false, false);


  private static final AttrInfoSupport ATTR_INFO;
  static {
    ATTR_INFO = new AttrInfoSupport(BeneratorErrorIds.SYN_ITERATE_ILLEGAL_ATTR, NAME);
    ATTR_INFO.add(ATT_SOURCE, true, BeneratorErrorIds.SYN_ITERATE_SOURCE);
    ATTR_INFO.add(ATT_COUNT, false, BeneratorErrorIds.SYN_ITERATE_COUNT);
    ATTR_INFO.add(ATT_MIN_COUNT, false, BeneratorErrorIds.SYN_ITERATE_MIN_COUNT);
    ATTR_INFO.add(ATT_MAX_COUNT, false, BeneratorErrorIds.SYN_ITERATE_MAX_COUNT);
    ATTR_INFO.add(ATT_COUNT_DISTRIBUTION, false, BeneratorErrorIds.SYN_ITERATE_COUNT_DIST);
    ATTR_INFO.add(ATT_THREADS, false, BeneratorErrorIds.SYN_ITERATE_THREADS);
    ATTR_INFO.add(ATT_PAGESIZE, false, BeneratorErrorIds.SYN_ITERATE_PAGE_SIZE);
    ATTR_INFO.add(ATT_STATS, false, BeneratorErrorIds.SYN_ITERATE_STATS);
    ATTR_INFO.add(ATT_ON_ERROR, false, BeneratorErrorIds.SYN_ITERATE_ON_ERROR);
    ATTR_INFO.add(ATT_TEMPLATE, false, BeneratorErrorIds.SYN_ITERATE_TEMPLATE);
    ATTR_INFO.add(ATT_CONSUMER, false, BeneratorErrorIds.SYN_ITERATE_CONSUMER);
    ATTR_INFO.add(ATT_TYPE, false, BeneratorErrorIds.SYN_ITERATE_TYPE);
    ATTR_INFO.add(ATT_SCOPE, false, BeneratorErrorIds.SYN_ITERATE_SCOPE);
    ATTR_INFO.add(ATT_VALIDATOR, false, BeneratorErrorIds.SYN_ITERATE_VALIDATOR);
    ATTR_INFO.add(ATT_CONVERTER, false, BeneratorErrorIds.SYN_ITERATE_CONVERTER);
    ATTR_INFO.add(ATT_NULL_QUOTA, false, BeneratorErrorIds.SYN_ITERATE_NULL_QUOTA);
    ATTR_INFO.add(ATT_UNIQUE, false, BeneratorErrorIds.SYN_ITERATE_UNIQUE);
    ATTR_INFO.add(ATT_DISTRIBUTION, false, BeneratorErrorIds.SYN_ITERATE_DISTRIBUTION);
    ATTR_INFO.add(ATT_CYCLIC, false, BeneratorErrorIds.SYN_ITERATE_CYCLIC);
    ATTR_INFO.add(ATT_OFFSET, false, BeneratorErrorIds.SYN_ITERATE_OFFSET);
    ATTR_INFO.add(ATT_SENSOR, false, BeneratorErrorIds.SYN_ITERATE_SENSOR);
    ATTR_INFO.add(ATT_SOURCE_SCRIPTED, false, BeneratorErrorIds.SYN_ITERATE_SOURCE_SCRIPTED);
    ATTR_INFO.add(ATT_SEGMENT, false, BeneratorErrorIds.SYN_ITERATE_SEGMENT);
    ATTR_INFO.add(ATT_FORMAT, false, BeneratorErrorIds.SYN_ITERATE_FORMAT);
    ATTR_INFO.add(ATT_ROW_BASED, false, BeneratorErrorIds.SYN_ITERATE_ROW_BASED);
    ATTR_INFO.add(ATT_EMPTY_MARKER, false, BeneratorErrorIds.SYN_ITERATE_EMPTY_MARKER);
    ATTR_INFO.add(ATT_FORMAT, false, BeneratorErrorIds.SYN_ITERATE_FORMAT);
    ATTR_INFO.add(ATT_SEPARATOR, false, BeneratorErrorIds.SYN_ITERATE_SEPARATOR);
    ATTR_INFO.add(ATT_ENCODING, false, BeneratorErrorIds.SYN_ITERATE_ENCODING);
    ATTR_INFO.add(ATT_SELECTOR, false, BeneratorErrorIds.SYN_ITERATE_SELECTOR);
    ATTR_INFO.add(ATT_SUB_SELECTOR, false, BeneratorErrorIds.SYN_ITERATE_SUB_SELECTOR);
    ATTR_INFO.add(ATT_DATASET, false, BeneratorErrorIds.SYN_ITERATE_DATASET);
    ATTR_INFO.add(ATT_NESTING, false, BeneratorErrorIds.SYN_ITERATE_NESTING);
    ATTR_INFO.add(ATT_LOCALE, false, BeneratorErrorIds.SYN_ITERATE_LOCALE);
    ATTR_INFO.add(ATT_FILTER, false, BeneratorErrorIds.SYN_ITERATE_FILTER);
  }

  public IterateParser() {
    super(EL_ITERATE, ATTR_INFO);
  }

}

/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.xml;

import com.rapiddweller.benerator.BeneratorErrorIds;
import com.rapiddweller.format.xml.AttrInfoSupport;

import static com.rapiddweller.benerator.engine.DescriptorConstants.*;

/**
 * Parses a &lt;generate&gt; element in a Benerator XML file.<br/><br/>
 * Created: 29.11.2021 15:41:49
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class GenerateParser extends AbstractGenIterParser {

  private static final AttrInfoSupport ATTR_INFO;
  static {
    ATTR_INFO = new AttrInfoSupport(BeneratorErrorIds.SYN_GENERATE_ILLEGAL_ATTR);
    ATTR_INFO.add(ATT_COUNT, false, BeneratorErrorIds.SYN_GENERATE_COUNT);
    ATTR_INFO.add(ATT_MIN_COUNT, false, BeneratorErrorIds.SYN_GENERATE_MIN_COUNT);
    ATTR_INFO.add(ATT_MAX_COUNT, false, BeneratorErrorIds.SYN_GENERATE_MAX_COUNT);
    ATTR_INFO.add(ATT_COUNT_DISTRIBUTION, false, BeneratorErrorIds.SYN_GENERATE_COUNT_DISTRIBUTION);
    ATTR_INFO.add(ATT_THREADS, false, BeneratorErrorIds.SYN_GENERATE_THREADS);
    ATTR_INFO.add(ATT_PAGESIZE, false, BeneratorErrorIds.SYN_GENERATE_PAGE_SIZE);
    ATTR_INFO.add(ATT_STATS, false, BeneratorErrorIds.SYN_GENERATE_STATS);
    ATTR_INFO.add(ATT_ON_ERROR, false, BeneratorErrorIds.SYN_GENERATE_ON_ERROR);
    ATTR_INFO.add(ATT_TEMPLATE, false, BeneratorErrorIds.SYN_GENERATE_TEMPLATE);
    ATTR_INFO.add(ATT_CONSUMER, false, BeneratorErrorIds.SYN_GENERATE_CONSUMER);
    ATTR_INFO.add(ATT_NAME, false, BeneratorErrorIds.SYN_GENERATE_NAME);
    ATTR_INFO.add(ATT_TYPE, false, BeneratorErrorIds.SYN_GENERATE_TYPE);
    ATTR_INFO.add(ATT_SCOPE, false, BeneratorErrorIds.SYN_GENERATE_SCOPE);
    ATTR_INFO.add(ATT_GENERATOR, false, BeneratorErrorIds.SYN_GENERATE_GENERATOR);
    ATTR_INFO.add(ATT_VALIDATOR, false, BeneratorErrorIds.SYN_GENERATE_VALIDATOR);
    ATTR_INFO.add(ATT_CONVERTER, false, BeneratorErrorIds.SYN_GENERATE_CONVERTER);
    ATTR_INFO.add(ATT_NULL_QUOTA, false, BeneratorErrorIds.SYN_GENERATE_NULL_QUOTA);
    ATTR_INFO.add(ATT_UNIQUE, false, BeneratorErrorIds.SYN_GENERATE_UNIQUE);
    ATTR_INFO.add(ATT_DISTRIBUTION, false, BeneratorErrorIds.SYN_GENERATE_DISTRIBUTION);
    ATTR_INFO.add(ATT_CYCLIC, false, BeneratorErrorIds.SYN_GENERATE_CYCLIC);
    ATTR_INFO.add(ATT_OFFSET, false, BeneratorErrorIds.SYN_GENERATE_OFFSET);
    ATTR_INFO.add(ATT_SENSOR, false, BeneratorErrorIds.SYN_GENERATE_SENSOR);
  }

  public GenerateParser() {
    super(EL_GENERATE, ATTR_INFO);
  }

}

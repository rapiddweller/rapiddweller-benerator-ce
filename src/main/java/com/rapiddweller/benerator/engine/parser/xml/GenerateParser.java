/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.xml;

import com.rapiddweller.benerator.BeneratorErrorIds;
import com.rapiddweller.benerator.engine.parser.attr.ConsumerAttribute;
import com.rapiddweller.benerator.engine.parser.attr.CountAttribute;
import com.rapiddweller.benerator.engine.parser.attr.CountDistributionAttribute;
import com.rapiddweller.benerator.engine.parser.attr.CountGranularityAttribute;
import com.rapiddweller.benerator.engine.parser.attr.ErrorHandlerAttribute;
import com.rapiddweller.benerator.engine.parser.attr.MinMaxCountAttribute;
import com.rapiddweller.benerator.engine.parser.attr.NameAttribute;
import com.rapiddweller.benerator.engine.parser.attr.NullQuotaAttribute;
import com.rapiddweller.benerator.engine.parser.attr.PageSizeAttribute;
import com.rapiddweller.benerator.engine.parser.attr.ScriptableBooleanAttribute;
import com.rapiddweller.benerator.engine.parser.attr.ThreadsAttribute;
import com.rapiddweller.benerator.engine.parser.string.IdParser;
import com.rapiddweller.benerator.engine.parser.string.ScriptableParser;
import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.common.Validator;
import com.rapiddweller.common.parser.BooleanParser;
import com.rapiddweller.common.parser.NonNegativeLongParser;
import com.rapiddweller.format.xml.AttrInfo;
import com.rapiddweller.format.xml.AttrInfoSupport;
import com.rapiddweller.common.Expression;
import org.w3c.dom.Element;

import static com.rapiddweller.benerator.BeneratorErrorIds.*;
import static com.rapiddweller.benerator.engine.DescriptorConstants.*;

/**
 * Parses a &lt;generate&gt; element in a Benerator XML file.<br/><br/>
 * Created: 29.11.2021 15:41:49
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class GenerateParser extends AbstractGenIterParser {

  private static final NameAttribute NAME = new NameAttribute(SYN_GENERATE_NAME, false, false);
  private static final AttrInfo<String> TYPE = new AttrInfo<>(ATT_TYPE, false, SYN_GENERATE_TYPE, new IdParser(), null);
  private static final AttrInfo<String> GENERATOR = new AttrInfo<>(ATT_GENERATOR, false, SYN_GENERATE_GENERATOR, null, null);

  private static final CountAttribute COUNT = new CountAttribute(SYN_GENERATE_COUNT, false);
  private static final MinMaxCountAttribute MIN_COUNT = new MinMaxCountAttribute(ATT_MIN_COUNT, SYN_GENERATE_MIN_COUNT);
  private static final MinMaxCountAttribute MAX_COUNT = new MinMaxCountAttribute(ATT_MAX_COUNT, SYN_GENERATE_MAX_COUNT);
  private static final CountGranularityAttribute COUNT_GRANULARITY = new CountGranularityAttribute(SYN_GENERATE_COUNT_GRANULARITY);
  private static final CountDistributionAttribute COUNT_DISTRIBUTION = new CountDistributionAttribute(SYN_GENERATE_COUNT_DIST);

  private static final ThreadsAttribute THREADS = new ThreadsAttribute(SYN_GENERATE_THREADS);
  private static final PageSizeAttribute PAGESIZE = new PageSizeAttribute(SYN_GENERATE_PAGE_SIZE);
  private static final ErrorHandlerAttribute ON_ERROR = new ErrorHandlerAttribute(SYN_GENERATE_ON_ERROR);
  private static final ScriptableBooleanAttribute STATS = new ScriptableBooleanAttribute(ATT_STATS, false, SYN_GENERATE_STATS, false);
  private static final AttrInfo<String> TEMPLATE = new AttrInfo<>(ATT_TEMPLATE, false, SYN_GENERATE_TEMPLATE, null, null);
  private static final ConsumerAttribute CONSUMER = new ConsumerAttribute(SYN_GENERATE_CONSUMER);
  private static final AttrInfo<String> SCOPE = new AttrInfo<>(ATT_SCOPE, false, SYN_GENERATE_SCOPE, null, null);

  private static final AttrInfo<String> VALIDATOR = new AttrInfo<>(ATT_VALIDATOR, false, SYN_GENERATE_VALIDATOR, null, null);
  private static final AttrInfo<String> CONVERTER = new AttrInfo<>(ATT_CONVERTER, false, SYN_GENERATE_CONVERTER, null, null);
  private static final NullQuotaAttribute NULL_QUOTA = new NullQuotaAttribute(SYN_GENERATE_NULL_QUOTA);
  private static final AttrInfo<Boolean> UNIQUE = new AttrInfo<>(ATT_UNIQUE, false, SYN_GENERATE_UNIQUE, new BooleanParser(), "false");
  private static final AttrInfo<String> DISTRIBUTION = new AttrInfo<>(ATT_DISTRIBUTION, false, SYN_GENERATE_DISTRIBUTION, null, null);
  private static final AttrInfo<Boolean> CYCLIC = new AttrInfo<>(ATT_CYCLIC, false, SYN_GENERATE_CYCLIC, new BooleanParser(), "false");
  private static final AttrInfo<Long> OFFSET = new AttrInfo<>(ATT_OFFSET, false, SYN_GENERATE_OFFSET, new NonNegativeLongParser(), "0");
  private static final AttrInfo<String> SENSOR = new AttrInfo<>(ATT_SENSOR, false, BeneratorErrorIds.SYN_GENERATE_SENSOR, null, null);

  // TODO support dataset, nesting and locale for generator?

  private static final AttrInfoSupport ATTR_INFO = new AttrInfoSupport(SYN_GENERATE_ILLEGAL_ATTR,
      new GenerateValidator(),
      NAME, TYPE, GENERATOR, COUNT, MIN_COUNT, MAX_COUNT, COUNT_GRANULARITY, COUNT_DISTRIBUTION,
      THREADS, PAGESIZE, STATS, ON_ERROR, TEMPLATE, CONSUMER, SCOPE,
      VALIDATOR, CONVERTER, NULL_QUOTA, UNIQUE, DISTRIBUTION,
      CYCLIC, OFFSET, SENSOR);

  public GenerateParser() {
    super(EL_GENERATE, ATTR_INFO);
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

  static class GenerateValidator implements Validator<Element> {
    @Override
    public boolean valid(Element element) {
      if (!COUNT.isDefinedIn(element) && !MAX_COUNT.isDefinedIn(element)) {
        throw BeneratorExceptionFactory.getInstance().syntaxErrorForXmlElement(
            "Neither 'count' nor 'maxCount' is specified. For unbounded data generation, " +
                "set maxCount=\"unbounded\"", null, SYN_GENERATE_COUNT, element);
      }
      return true;
    }
  }

}

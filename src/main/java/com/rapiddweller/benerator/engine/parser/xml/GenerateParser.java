/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.xml;

import com.rapiddweller.benerator.engine.parser.attr.ConsumerAttribute;
import com.rapiddweller.benerator.engine.parser.attr.ErrorHandlerAttribute;
import com.rapiddweller.benerator.engine.parser.attr.NullQuotaAttribute;
import com.rapiddweller.benerator.engine.parser.attr.PageSizeAttribute;
import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.common.Validator;
import com.rapiddweller.common.parser.BooleanParser;
import com.rapiddweller.common.parser.NonNegativeLongParser;
import com.rapiddweller.format.xml.AttrInfo;
import com.rapiddweller.format.xml.AttrInfoSupport;
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

  private static final AttrInfo<String> GENERATOR = new AttrInfo<>(ATT_GENERATOR, false, SYN_GENERATE_GENERATOR, null, null);

  private static final PageSizeAttribute PAGESIZE = new PageSizeAttribute(SYN_GENERATE_PAGE_SIZE);
  private static final ErrorHandlerAttribute ON_ERROR = new ErrorHandlerAttribute(SYN_GENERATE_ON_ERROR);
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

  // TODO support dataset, nesting and locale for generator?

  public GenerateParser() {
    super(EL_GENERATE, null);
    this.nameAttr.setErrorId(SYN_GENERATE_NAME);
    this.typeAttr.setErrorId(SYN_GENERATE_TYPE);

    this.countAttr.setErrorId(SYN_GENERATE_COUNT);
    this.minCountAttr.setErrorId(SYN_GENERATE_MIN_COUNT);
    this.maxCountAttr.setErrorId(SYN_GENERATE_MAX_COUNT);
    this.countGranularityAttr.setErrorId(SYN_GENERATE_COUNT_GRANULARITY);
    this.countDistributionAttr.setErrorId(SYN_GENERATE_COUNT_DIST);

    this.threadsAttr.setErrorId(SYN_GENERATE_THREADS);
    this.statsAttr.setErrorId(SYN_GENERATE_STATS);
    this.sensorAttr.setErrorId(SYN_GENERATE_SENSOR);

    this.attrSupport = new AttrInfoSupport(SYN_GENERATE_ILLEGAL_ATTR, new GenerateValidator(),
        nameAttr, typeAttr, GENERATOR,
        countAttr, minCountAttr, maxCountAttr, countGranularityAttr, countDistributionAttr,
        threadsAttr, PAGESIZE, statsAttr, ON_ERROR, TEMPLATE, CONSUMER, SCOPE,
        VALIDATOR, CONVERTER, NULL_QUOTA, UNIQUE, DISTRIBUTION,
        CYCLIC, OFFSET, sensorAttr);
  }

  class GenerateValidator implements Validator<Element> {
    @Override
    public boolean valid(Element element) {
      if (!countAttr.isDefinedIn(element) && !maxCountAttr.isDefinedIn(element)) {
        throw BeneratorExceptionFactory.getInstance().syntaxErrorForXmlElement(
            "Neither 'count' nor 'maxCount' is specified. For unbounded data generation, " +
                "set maxCount=\"unbounded\"", null, SYN_GENERATE_COUNT, element);
      }
      return true;
    }
  }

}

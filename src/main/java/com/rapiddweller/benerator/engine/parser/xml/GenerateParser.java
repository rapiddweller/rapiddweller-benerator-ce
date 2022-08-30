/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.xml;

import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.common.Validator;
import com.rapiddweller.format.xml.AttrInfo;
import com.rapiddweller.format.xml.AttrInfoSupport;
import org.w3c.dom.Element;

import static com.rapiddweller.benerator.BeneratorErrorIds.*;
import static com.rapiddweller.benerator.engine.DescriptorConstants.*;

/**
 * Parses a &lt;generate&gt; element in a Benerator XML file.<br/><br/>
 * Created: 29.11.2021 15:41:49
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class GenerateParser extends AbstractGenIterParser {

  private static final AttrInfo<String> GENERATOR = new AttrInfo<>(ATT_GENERATOR, false, SYN_GENERATE_GENERATOR, null, null);

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

    this.pagesizeAttr.setErrorId(SYN_GENERATE_PAGE_SIZE);
    this.onErrorAttr.setErrorId(SYN_GENERATE_ON_ERROR);
    this.templateAttr.setErrorId(SYN_GENERATE_TEMPLATE);
    this.consumerAttr.setErrorId(SYN_GENERATE_CONSUMER);
    this.scopeAttr.setErrorId(SYN_GENERATE_SCOPE);

    this.validatorAttr.setErrorId(SYN_GENERATE_VALIDATOR);
    this.converterAttr.setErrorId(SYN_GENERATE_CONVERTER);
    this.nullQuotaAttr.setErrorId(SYN_GENERATE_NULL_QUOTA);
    this.uniqueAttr.setErrorId(SYN_GENERATE_UNIQUE);
    this.distributionAttr.setErrorId(SYN_GENERATE_DISTRIBUTION);
    this.cyclicAttr.setErrorId(SYN_GENERATE_CYCLIC);
    this.offsetAttr.setErrorId(SYN_GENERATE_OFFSET);

    this.attrSupport = new AttrInfoSupport(SYN_GENERATE_ILLEGAL_ATTR, new GenerateValidator(),
        nameAttr, typeAttr, GENERATOR,
        countAttr, minCountAttr, maxCountAttr, countGranularityAttr, countDistributionAttr,
        threadsAttr, pagesizeAttr, statsAttr, onErrorAttr, templateAttr, consumerAttr, scopeAttr,
        validatorAttr, converterAttr, nullQuotaAttr, uniqueAttr, distributionAttr,
        cyclicAttr, offsetAttr, sensorAttr);
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

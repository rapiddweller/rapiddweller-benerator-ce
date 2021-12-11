/*
 * (c) Copyright 2006-2021 by rapiddweller GmbH & Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General Public License.
 *
 * For redistributing this software or a derivative work under a license other
 * than the GPL-compatible Free Software License as defined by the Free
 * Software Foundation or approved by OSI, you must first obtain a commercial
 * license to this software product from rapiddweller GmbH & Volker Bergmann.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED CONDITIONS,
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE
 * HEREBY EXCLUDED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.rapiddweller.benerator.engine.parser.xml;

import com.rapiddweller.benerator.BeneratorErrorIds;
import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.distribution.Distribution;
import com.rapiddweller.benerator.engine.Statement;
import com.rapiddweller.benerator.engine.parser.string.ScriptableParser;
import com.rapiddweller.benerator.engine.statement.WaitStatement;
import com.rapiddweller.benerator.factory.FactoryUtil;
import com.rapiddweller.benerator.primitive.DynamicLongGenerator;
import com.rapiddweller.benerator.util.ExpressionBasedGenerator;
import com.rapiddweller.benerator.wrapper.WrapperFactory;
import com.rapiddweller.common.parser.NonNegativeLongParser;
import com.rapiddweller.format.xml.AttrInfoSupport;
import com.rapiddweller.format.xml.AttributeInfo;
import com.rapiddweller.model.data.Uniqueness;
import com.rapiddweller.script.Expression;
import com.rapiddweller.script.expression.ExpressionUtil;
import org.w3c.dom.Element;

import static com.rapiddweller.benerator.engine.DescriptorConstants.*;
import static com.rapiddweller.benerator.engine.parser.xml.DescriptorParserUtil.parseLongAttribute;

/**
 * Parses a 'wait' element.<br/><br/>
 * Created: 21.02.2010 08:07:59
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class WaitParser extends AbstractBeneratorDescriptorParser {

  // format definitions ----------------------------------------------------------------------------------------------

  private static final AttributeInfo<Expression<Long>> DURATION = new AttributeInfo<>(
    ATT_DURATION, false, BeneratorErrorIds.SYN_WAIT_DURATION, new ScriptableParser<>(new NonNegativeLongParser()), null);

  private static final AttributeInfo<Expression<Long>> MIN = new AttributeInfo<>(
    ATT_MIN, false, BeneratorErrorIds.SYN_WAIT_MIN, new ScriptableParser<>(new NonNegativeLongParser()), null);

  private static final AttributeInfo<Expression<Long>> MAX = new AttributeInfo<>(
    ATT_MAX, false, BeneratorErrorIds.SYN_WAIT_MAX, new ScriptableParser<>(new NonNegativeLongParser()), null);

  private static final AttributeInfo<Expression<Long>> GRANULARITY = new AttributeInfo<>(
    ATT_GRANULARITY, false, BeneratorErrorIds.SYN_WAIT_GRANULARITY, new ScriptableParser<>(new NonNegativeLongParser()), null);

  private static final AttributeInfo<String> DISTRIBUTION = new AttributeInfo<>(
    ATT_DISTRIBUTION, false, BeneratorErrorIds.SYN_WAIT_DISTRIBUTION, null, null);

  private static final AttrInfoSupport ATTR_INFO = new AttrInfoSupport(BeneratorErrorIds.SYN_WAIT_ILLEGAL_ATTRIBUTE,
      DURATION, MIN, MAX, GRANULARITY, DISTRIBUTION);


  // constructor & interface -----------------------------------------------------------------------------------------

  public WaitParser() {
    super(EL_WAIT, ATTR_INFO);
  }

  @Override
  public Statement doParse(
      Element element, Element[] parentXmlPath, Statement[] parentComponentPath, BeneratorParseContext context) {
    attrSupport.validate(element);

    // check attribute combinations
    assertAtLeastOneAttributeIsSet(element, ATT_DURATION, ATT_MIN, ATT_MAX);
    mutuallyExcludeAttrGroups(element, BeneratorErrorIds.SYN_WAIT_MUTUALLY_EXCLUDED, new String[] { ATT_DURATION },
        new String[] { ATT_MIN, ATT_MAX, ATT_GRANULARITY, ATT_DISTRIBUTION }
    );

    // check for fix or random 'duration'
    Expression<Long> duration = DURATION.parse(element);
    if (duration != null) {
      return durationBasedStatement(duration);
    } else {
      return distributionBasedStatement(element);
    }
  }

  // helper methods --------------------------------------------------------------------------------------------------

  private WaitStatement durationBasedStatement(Expression<Long> duration) {
    ExpressionBasedGenerator<Long> base = new ExpressionBasedGenerator<>(duration, Long.class);
    return new WaitStatement(WrapperFactory.asNonNullGenerator(base));
  }

  private WaitStatement distributionBasedStatement(Element element) {
    // check for distribution
    Expression<Long> min = MIN.parse(element);
    Expression<Long> max = MAX.parse(element);
    Expression<Long> granularity = GRANULARITY.parse(element);
    String distSpec = DISTRIBUTION.parse(element);
    Expression<Distribution> distribution = FactoryUtil.getDistributionExpression(distSpec, Uniqueness.NONE, false);
    Generator<Long> durationGenerator = new DynamicLongGenerator(min, max, granularity,
        distribution, ExpressionUtil.constant(false));
    return new WaitStatement(WrapperFactory.asNonNullGenerator(durationGenerator));
  }

}

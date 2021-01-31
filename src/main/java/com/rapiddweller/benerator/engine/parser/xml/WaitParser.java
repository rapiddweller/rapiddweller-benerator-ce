/*
 * (c) Copyright 2006-2020 by rapiddweller GmbH & Volker Bergmann. All rights reserved.
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

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.distribution.Distribution;
import com.rapiddweller.benerator.engine.Statement;
import com.rapiddweller.benerator.engine.statement.WaitStatement;
import com.rapiddweller.benerator.factory.FactoryUtil;
import com.rapiddweller.benerator.primitive.DynamicLongGenerator;
import com.rapiddweller.benerator.util.ExpressionBasedGenerator;
import com.rapiddweller.benerator.wrapper.WrapperFactory;
import com.rapiddweller.common.CollectionUtil;
import com.rapiddweller.model.data.Uniqueness;
import com.rapiddweller.script.Expression;
import com.rapiddweller.script.expression.ExpressionUtil;
import org.w3c.dom.Element;

import static com.rapiddweller.benerator.engine.DescriptorConstants.*;
import static com.rapiddweller.benerator.engine.parser.xml.DescriptorParserUtil.getAttribute;
import static com.rapiddweller.benerator.engine.parser.xml.DescriptorParserUtil.parseLongAttribute;

/**
 * Parses a 'wait' element.<br/><br/>
 * Created: 21.02.2010 08:07:59
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class WaitParser extends AbstractBeneratorDescriptorParser {

	public WaitParser() {
	    super(EL_WAIT, null, CollectionUtil.toSet(ATT_DURATION, ATT_MIN, ATT_MAX, ATT_GRANULARITY, ATT_DISTRIBUTION));
    }

	@Override
	public Statement doParse(Element element, Statement[] parentPath, BeneratorParseContext context) {
		
		// check attribute combinations
		assertAtLeastOneAttributeIsSet(element, ATT_DURATION, ATT_MIN, ATT_MAX);
		excludeAttributes(element, ATT_DURATION, ATT_MIN);
		excludeAttributes(element, ATT_DURATION, ATT_MAX);
		
		// check for constant value
		Expression<Long> duration  = parseLongAttribute(ATT_DURATION, element, null);
		if (duration != null) {
			ExpressionBasedGenerator<Long> base = new ExpressionBasedGenerator<>(duration, Long.class);
			return new WaitStatement(WrapperFactory.asNonNullGenerator(base));
		}
		
		// check for distribution
		Expression<Long> min  = parseLongAttribute(ATT_MIN, element, null);
		Expression<Long> max  = parseLongAttribute(ATT_MAX, element, null);
		Expression<Long> granularity  = parseLongAttribute(ATT_GRANULARITY, element, null);
		String distSpec  = getAttribute(ATT_DISTRIBUTION, element);
		Expression<Distribution> distribution 
			= FactoryUtil.getDistributionExpression(distSpec, Uniqueness.NONE, false);
		Generator<Long> durationGenerator = new DynamicLongGenerator(min, max, granularity, 
				distribution, ExpressionUtil.constant(false));
	    return new WaitStatement(WrapperFactory.asNonNullGenerator(durationGenerator));
    }

}

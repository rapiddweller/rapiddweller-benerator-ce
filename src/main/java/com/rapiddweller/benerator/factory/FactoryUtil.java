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

package com.rapiddweller.benerator.factory;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.StorageSystem;
import com.rapiddweller.benerator.distribution.AttachedWeight;
import com.rapiddweller.benerator.distribution.Distribution;
import com.rapiddweller.benerator.distribution.FeatureWeight;
import com.rapiddweller.benerator.distribution.SequenceManager;
import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.util.DynamicSourceGenerator;
import com.rapiddweller.benerator.primitive.ScriptGenerator;
import com.rapiddweller.common.BeanUtil;
import com.rapiddweller.common.Context;
import com.rapiddweller.common.LocaleUtil;
import com.rapiddweller.common.exception.ParseException;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.format.regex.RegexParser;
import com.rapiddweller.format.script.Script;
import com.rapiddweller.format.script.ScriptUtil;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.FeatureDescriptor;
import com.rapiddweller.model.data.FeatureDetail;
import com.rapiddweller.model.data.Uniqueness;
import com.rapiddweller.script.DatabeneScriptParser;
import com.rapiddweller.common.Expression;
import com.rapiddweller.script.WeightedSample;
import com.rapiddweller.script.expression.DynamicExpression;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_NAME;

/**
 * Provides utility methods for Generator factories.<br/><br/>
 * Created: 08.03.2008 09:39:05
 * @author Volker Bergmann
 */
public class FactoryUtil {

  private FactoryUtil() {
    // private constructor to prevent instantiation
  }

  public static void mapDetailsToBeanProperties(FeatureDescriptor descriptor, Object bean, Context context) {
    for (FeatureDetail<?> detail : descriptor.getDetails()) {
      if (!ATT_NAME.equals(detail.getName())) {
        mapDetailToBeanProperty(descriptor, detail.getName(), bean, context);
      }
    }
  }

  public static void mapDetailToBeanProperty(FeatureDescriptor descriptor, String detailName, Object bean, Context context) {
    Object detailValue = descriptor.getDetailValue(detailName);
    if (detailValue instanceof Expression) {
      detailValue = ((Expression<?>) detailValue).evaluate(context);
    }
    setBeanProperty(bean, detailName, detailValue, context);
  }

  public static void setBeanProperty(Object bean, String detailName, Object detailValue, Context context) {
    if (detailValue != null && BeanUtil.hasProperty(bean.getClass(), detailName)) {
      PropertyDescriptor propertyDescriptor = BeanUtil.getPropertyDescriptor(bean.getClass(), detailName);
      Class<?> propertyType = propertyDescriptor.getPropertyType();
      Object propertyValue = detailValue;
      if (detailValue instanceof String && StorageSystem.class.isAssignableFrom(propertyType)) {
        propertyValue = context.get(propertyValue.toString());
      }
      BeanUtil.setPropertyValue(bean, detailName, propertyValue, false);
    }
  }

  /**
   * Extracts distribution information from the descriptor. The <code>spec</code> is the central configuration:
   * <ol>
   *   <li>if not specified, the setup's default distribution method is used</li>
   *   <li>If a spec is defined, it checks for a definition of format <code>weighted['featureName']</code>
   *       to use an object's feature as definition of its weight</li>
   *   <li>if none was found above it checks for a definition of format <code>weighted</code>
   *       and assumes th related objects have a feature of name 'weight' to define their weight</li>
   *   <li>if none was found above, it looks a variable (or <code>bean</code>) of that name (specified earlier in the setup)</li>
   *   <li>if none was found above, it asks the {@link SequenceManager} for a {@link com.rapiddweller.benerator.distribution.Sequence} of that name</li>
   *   <li>if none was found above, it tries to evaluate the spec as a script expression like <code>'new Random(2,8)'</code></li>
   * </ol>
   *
   * @param spec       the textual representation of the distribution, see method doc
   * @param uniqueness tells if a unique distribution is requested
   * @param required   if set the method will never return null
   * @param context    the {@link BeneratorContext}
   * @return a distribution that reflects the descriptor setup, null if distribution info is not found nor required.
   */
  @SuppressWarnings("rawtypes")
  public static Distribution getDistribution(
      String spec, Uniqueness uniqueness, boolean required, BeneratorContext context) {

    // handle absence of distribution spec
    if (StringUtil.isEmpty(spec)) {
      if (required) {
        return context.getGeneratorFactory().defaultDistribution(uniqueness);
      } else {
        return null;
      }
    }

    // check for 'weighted' distribution
    if (spec.startsWith("weighted[") && spec.endsWith("]")) {
      return new FeatureWeight(spec.substring("weighted[".length(), spec.length() - 1).trim());
    } else if ("weighted".equals(spec)) {
      return new AttachedWeight();
    }

    // check for context reference
    Object contextObject = context.get(spec);
    if (contextObject != null) {
      if (contextObject instanceof Distribution) {
        return (Distribution) contextObject;
      } else {
        throw BeneratorExceptionFactory.getInstance().configurationError("Not a distribution: " + spec + "=" + contextObject);
      }
    }

    // check for default sequence reference
    Distribution result = SequenceManager.getRegisteredSequence(spec, false);
    if (result != null) {
      return result;
    }

    // check for explicit construction
    try {
      Expression beanEx = DatabeneScriptParser.parseBeanSpec(spec);
      return (Distribution) beanEx.evaluate(context);
    } catch (ParseException e) {
      throw BeneratorExceptionFactory.getInstance().configurationError("Error parsing distribution spec: " + spec);
    }
  }


  public static Expression<Distribution> getDistributionExpression(
      final String spec, final Uniqueness uniqueness, final boolean required) {
    return new DynamicExpression<>() {

      @Override
      public Distribution evaluate(Context context) {
        return getDistribution(spec, uniqueness, required, (BeneratorContext) context);
      }

    };
  }

  public static Set<Character> fullLocaleCharSet(String pattern, Locale locale) {
    Set<Character> chars;
    if (pattern != null) {
      try {
        chars = new RegexParser(locale).parseSingleChar(pattern).getCharSet().getSet();
      } catch (ParseException e) {
        throw BeneratorExceptionFactory.getInstance().configurationError("Invalid regular expression.", e);
      }
    } else {
      chars = LocaleUtil.letters(locale);
    }
    return chars;
  }

  public static Locale defaultLocale() {
    return Locale.getDefault();
  }

  public static <T> List<T> extractValues(Collection<WeightedSample<T>> samples) {
    List<T> values = new ArrayList<>(samples.size());
    for (WeightedSample<T> sample : samples) {
      values.add(sample.getValue());
    }
    return values;
  }

  public static Generator<?> createScriptGenerator(String scriptText) {
    Script script = ScriptUtil.parseScriptText(scriptText);
    return new ScriptGenerator(script);
  }

  public static Generator<?> createDynamicSourceGenerator(Uniqueness uniqueness, BeneratorContext context, ComplexTypeDescriptor descriptor, ComplexTypeGeneratorFactory factory) {
    return new DynamicSourceGenerator(uniqueness, context, descriptor, factory);
  }

}

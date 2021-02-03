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

package com.rapiddweller.model.data;

import com.rapiddweller.common.operation.AndOperation;
import com.rapiddweller.common.operation.MaxNumberStringOperation;
import com.rapiddweller.common.operation.MaxOperation;
import com.rapiddweller.common.operation.MinNumberStringOperation;
import com.rapiddweller.common.operation.MinOperation;
import com.rapiddweller.script.PrimitiveType;

/**
 * Describes a simple type.<br/>
 * <br/>
 * Created: 03.03.2008 08:58:58
 *
 * @author Volker Bergmann
 * @since 0.5.0
 */
public class SimpleTypeDescriptor extends TypeDescriptor {

  /**
   * The constant MIN.
   */
  public static final String MIN = "min";
  /**
   * The constant MAX.
   */
  public static final String MAX = "max";
  /**
   * The constant MIN_INCLUSIVE.
   */
  public static final String MIN_INCLUSIVE = "minInclusive";
  /**
   * The constant MAX_INCLUSIVE.
   */
  public static final String MAX_INCLUSIVE = "maxInclusive";

  /**
   * The constant GRANULARITY.
   */
  public static final String GRANULARITY = "granularity";

  /**
   * The constant TRUE_QUOTA.
   */
  public static final String TRUE_QUOTA = "trueQuota";
  /**
   * The constant MIN_LENGTH.
   */
  public static final String MIN_LENGTH = "minLength";
  /**
   * The constant MAX_LENGTH.
   */
  public static final String MAX_LENGTH = "maxLength";
  /**
   * The constant LENGTH_DISTRIBUTION.
   */
  public static final String LENGTH_DISTRIBUTION = "lengthDistribution";

  /**
   * The constant CONSTANT.
   */
  public static final String CONSTANT = "constant";
  /**
   * The constant VALUES.
   */
  public static final String VALUES = "values";
  /**
   * The constant MAP.
   */
  public static final String MAP = "map";

  private PrimitiveType primitiveType = null;

  /**
   * Instantiates a new Simple type descriptor.
   *
   * @param name     the name
   * @param provider the provider
   */
  public SimpleTypeDescriptor(String name, DescriptorProvider provider) {
    this(name, provider, (String) null);
  }

  /**
   * Instantiates a new Simple type descriptor.
   *
   * @param name     the name
   * @param provider the provider
   * @param parent   the parent
   */
  public SimpleTypeDescriptor(String name, DescriptorProvider provider,
                              SimpleTypeDescriptor parent) {
    this(name, provider, parent.getName());
    this.parent = parent;
  }

  /**
   * Instantiates a new Simple type descriptor.
   *
   * @param name       the name
   * @param provider   the provider
   * @param parentName the parent name
   */
  public SimpleTypeDescriptor(String name, DescriptorProvider provider,
                              String parentName) {
    super(name, provider, parentName);
    // number setup
    addConstraint(MIN, String.class, new MaxNumberStringOperation());
    addConstraint(MAX, String.class, new MinNumberStringOperation());
    addConstraint(MIN_INCLUSIVE, Boolean.class, new AndOperation());
    addConstraint(MAX_INCLUSIVE, Boolean.class, new AndOperation());
    addConfig(GRANULARITY, String.class);
    // boolean setup
    addConfig(TRUE_QUOTA, Double.class);
    // string setup
    addConstraint(MIN_LENGTH, Integer.class, new MaxOperation<>());
    addConstraint(MAX_LENGTH, Integer.class, new MinOperation<>());
    addConfig(LENGTH_DISTRIBUTION, String.class);
    // other config
    addConfig(VALUES, String.class);
    addConfig(CONSTANT, String.class);
    addConfig(MAP, String.class);
  }

  // properties ------------------------------------------------------------------------------------------------------

  @Override
  public SimpleTypeDescriptor getParent() {
    return (SimpleTypeDescriptor) super.getParent();
  }

  /**
   * Gets primitive type.
   *
   * @return the primitive type
   */
  public PrimitiveType getPrimitiveType() {
    if (primitiveType != null) {
      return primitiveType;
    }
    primitiveType = PrimitiveType.getInstance(getName());
    if (primitiveType != null) {
      return primitiveType;
    }
    if (getParent() != null) {
      return getParent().getPrimitiveType();
    }
    return null;
  }

  /**
   * Gets min.
   *
   * @return the min
   */
  public String getMin() {
    return (String) getDetailValue(MIN);
  }

  /**
   * Sets min.
   *
   * @param min the min
   */
  public void setMin(String min) {
    setDetailValue(MIN, min);
  }

  /**
   * Is min inclusive boolean.
   *
   * @return the boolean
   */
  public Boolean isMinInclusive() {
    return (Boolean) getDetailValue(MIN_INCLUSIVE);
  }

  /**
   * Sets min inclusive.
   *
   * @param minInclusive the min inclusive
   */
  public void setMinInclusive(Boolean minInclusive) {
    setDetailValue(MIN, minInclusive);
  }

  /**
   * Gets max.
   *
   * @return the max
   */
  public String getMax() {
    return (String) getDetailValue(MAX);
  }

  /**
   * Sets max.
   *
   * @param max the max
   */
  public void setMax(String max) {
    setDetailValue(MAX, max);
  }

  /**
   * Is max inclusive boolean.
   *
   * @return the boolean
   */
  public Boolean isMaxInclusive() {
    return (Boolean) getDetailValue(MAX_INCLUSIVE);
  }

  /**
   * Sets max inclusive.
   *
   * @param maxInclusive the max inclusive
   */
  public void setMaxInclusive(Boolean maxInclusive) {
    setDetailValue(MAX_INCLUSIVE, maxInclusive);
  }

  /**
   * Gets granularity.
   *
   * @return the granularity
   */
  public String getGranularity() {
    return (String) getDetailValue(GRANULARITY);
  }

  /**
   * Sets granularity.
   *
   * @param granularity the granularity
   */
  public void setGranularity(String granularity) {
    setDetailValue(GRANULARITY, granularity);
  }

  /**
   * Gets true quota.
   *
   * @return the true quota
   */
  public Double getTrueQuota() {
    return (Double) getDetailValue(TRUE_QUOTA);
  }

  /**
   * Sets true quota.
   *
   * @param trueQuota the true quota
   */
  public void setTrueQuota(Double trueQuota) {
    setDetailValue(TRUE_QUOTA, trueQuota);
  }

  /**
   * Gets min length.
   *
   * @return the min length
   */
  public Integer getMinLength() {
    return (Integer) getDetailValue(MIN_LENGTH);
  }

  /**
   * Sets min length.
   *
   * @param minLength the min length
   */
  public void setMinLength(Integer minLength) {
    setDetailValue(MIN_LENGTH, minLength);
  }

  /**
   * Gets max length.
   *
   * @return the max length
   */
  public Integer getMaxLength() {
    return (Integer) getDetailValue(MAX_LENGTH);
  }

  /**
   * Sets max length.
   *
   * @param maxLength the max length
   */
  public void setMaxLength(Integer maxLength) {
    setDetailValue(MAX_LENGTH, maxLength);
  }

  /**
   * Gets length distribution.
   *
   * @return the length distribution
   */
  public String getLengthDistribution() {
    return (String) getDetailValue(LENGTH_DISTRIBUTION);
  }

  /**
   * Sets length distribution.
   *
   * @param lengthDistribution the length distribution
   */
  public void setLengthDistribution(String lengthDistribution) {
    setDetailValue(LENGTH_DISTRIBUTION, lengthDistribution);
  }

  /**
   * Gets values.
   *
   * @return the values
   */
  public String getValues() {
    return (String) getDetailValue(VALUES);
  }

  /**
   * Sets values.
   *
   * @param values the values
   */
  public void setValues(String values) {
    setDetailValue(VALUES, values);
  }

  /**
   * Add value.
   *
   * @param value the value
   */
  public void addValue(String value) {
    String valuesBefore = getValues();
    if (valuesBefore == null || valuesBefore.length() == 0) {
      setValues(value);
    } else {
      setValues(valuesBefore + ',' + value);
    }
  }

  /**
   * Gets constant.
   *
   * @return the constant
   */
  public String getConstant() {
    return (String) getDetailValue(CONSTANT);
  }

  /**
   * Sets constant.
   *
   * @param constant the constant
   */
  public void setConstant(String constant) {
    setDetailValue(CONSTANT, constant);
  }

  /**
   * Gets map.
   *
   * @return the map
   */
  public String getMap() {
    return (String) getDetailValue(MAP);
  }

  /**
   * Sets map.
   *
   * @param map the map
   */
  public void setMap(String map) {
    setDetailValue(MAP, map);
  }

  // literate build helpers ------------------------------------------------------------------------------------------

  /**
   * With min simple type descriptor.
   *
   * @param min the min
   * @return the simple type descriptor
   */
  public SimpleTypeDescriptor withMin(String min) {
    setMin(min);
    return this;
  }

  /**
   * With max simple type descriptor.
   *
   * @param max the max
   * @return the simple type descriptor
   */
  public SimpleTypeDescriptor withMax(String max) {
    setMax(max);
    return this;
  }

  /**
   * With granularity simple type descriptor.
   *
   * @param granularity the granularity
   * @return the simple type descriptor
   */
  public SimpleTypeDescriptor withGranularity(String granularity) {
    setGranularity(granularity);
    return this;
  }

  /**
   * With pattern simple type descriptor.
   *
   * @param pattern the pattern
   * @return the simple type descriptor
   */
  public SimpleTypeDescriptor withPattern(String pattern) {
    setPattern(pattern);
    return this;
  }

  /**
   * With distribution simple type descriptor.
   *
   * @param distribution the distribution
   * @return the simple type descriptor
   */
  public SimpleTypeDescriptor withDistribution(String distribution) {
    setDistribution(distribution);
    return this;
  }

  /**
   * With dataset simple type descriptor.
   *
   * @param dataset the dataset
   * @return the simple type descriptor
   */
  public SimpleTypeDescriptor withDataset(String dataset) {
    setDataset(dataset);
    return this;
  }

  /**
   * With locale id simple type descriptor.
   *
   * @param localeId the locale id
   * @return the simple type descriptor
   */
  public SimpleTypeDescriptor withLocaleId(String localeId) {
    setLocaleId(localeId);
    return this;
  }

  /**
   * With true quota simple type descriptor.
   *
   * @param trueQuota the true quota
   * @return the simple type descriptor
   */
  public SimpleTypeDescriptor withTrueQuota(Double trueQuota) {
    setTrueQuota(trueQuota);
    return this;
  }

  /**
   * With uri simple type descriptor.
   *
   * @param source the source
   * @return the simple type descriptor
   */
  public SimpleTypeDescriptor withUri(String source) {
    setSource(source);
    return this;
  }

  /**
   * With values simple type descriptor.
   *
   * @param values the values
   * @return the simple type descriptor
   */
  public SimpleTypeDescriptor withValues(String values) {
    this.setValues(values);
    return this;
  }

  // generic property access -----------------------------------------------------------------------------------------

/*
    public void setDetail(String detailName, Object detailValue) {
        Class<?> targetType = getDetailType(detailName);
        if (targetType == Distribution.class && detailValue.getClass() == String.class)
            detailValue = mapDistribution((String) detailValue);
        else if (targetType == Converter.class && detailValue.getClass() == String.class)
            detailValue = mapConverter((String) detailValue);
        super.setDetailValue(detailName, detailValue);
    }
*/

// private helpers -------------------------------------------------------------------------------------------------
/*
    private Converter<?, ?> mapConverter(String converterString) {
        Object result = BeanUtil.newInstance(converterString);
        if (result instanceof Format)
            result = new ParseFormatConverter(Object.class, (Format) result);
        else if (!(result instanceof Converter))
            throw new ConfigurationError("Class is no Converter: " + result.getClass());
        return (Converter<?, ?>) result;
    }

    private static Distribution mapDistribution(String distributionName) {
        if (distributionName == null)
            return null;
        try {
            return Sequence.getInstance(distributionName);
        } catch (Exception e) {
            return (Distribution) BeanUtil.newInstance(distributionName);
        }
    }
*/

}

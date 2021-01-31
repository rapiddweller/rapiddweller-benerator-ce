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

    public static final String MIN = "min";
    public static final String MAX = "max";
    public static final String MIN_INCLUSIVE = "minInclusive";
    public static final String MAX_INCLUSIVE = "maxInclusive";

    public static final String GRANULARITY = "granularity";

    public static final String TRUE_QUOTA = "trueQuota";
    public static final String MIN_LENGTH = "minLength";
    public static final String MAX_LENGTH = "maxLength";
    public static final String LENGTH_DISTRIBUTION = "lengthDistribution";

    public static final String CONSTANT = "constant";
    public static final String VALUES = "values";
    public static final String MAP = "map";

    private PrimitiveType primitiveType = null;

    public SimpleTypeDescriptor(String name, DescriptorProvider provider) {
        this(name, provider, (String) null);
    }

    public SimpleTypeDescriptor(String name, DescriptorProvider provider,
                                SimpleTypeDescriptor parent) {
        this(name, provider, parent.getName());
        this.parent = parent;
    }

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

    public String getMin() {
        return (String) getDetailValue(MIN);
    }

    public void setMin(String min) {
        setDetailValue(MIN, min);
    }

    public Boolean isMinInclusive() {
        return (Boolean) getDetailValue(MIN_INCLUSIVE);
    }

    public void setMinInclusive(Boolean minInclusive) {
        setDetailValue(MIN, minInclusive);
    }

    public String getMax() {
        return (String) getDetailValue(MAX);
    }

    public void setMax(String max) {
        setDetailValue(MAX, max);
    }

    public Boolean isMaxInclusive() {
        return (Boolean) getDetailValue(MAX_INCLUSIVE);
    }

    public void setMaxInclusive(Boolean maxInclusive) {
        setDetailValue(MAX_INCLUSIVE, maxInclusive);
    }

    public String getGranularity() {
        return (String) getDetailValue(GRANULARITY);
    }

    public void setGranularity(String granularity) {
        setDetailValue(GRANULARITY, granularity);
    }

    public Double getTrueQuota() {
        return (Double) getDetailValue(TRUE_QUOTA);
    }

    public void setTrueQuota(Double trueQuota) {
        setDetailValue(TRUE_QUOTA, trueQuota);
    }

    public Integer getMinLength() {
        return (Integer) getDetailValue(MIN_LENGTH);
    }

    public void setMinLength(Integer minLength) {
        setDetailValue(MIN_LENGTH, minLength);
    }

    public Integer getMaxLength() {
        return (Integer) getDetailValue(MAX_LENGTH);
    }

    public void setMaxLength(Integer maxLength) {
        setDetailValue(MAX_LENGTH, maxLength);
    }

    public String getLengthDistribution() {
        return (String) getDetailValue(LENGTH_DISTRIBUTION);
    }

    public void setLengthDistribution(String lengthDistribution) {
        setDetailValue(LENGTH_DISTRIBUTION, lengthDistribution);
    }

    public String getValues() {
        return (String) getDetailValue(VALUES);
    }

    public void setValues(String values) {
        setDetailValue(VALUES, values);
    }

    public void addValue(String value) {
        String valuesBefore = getValues();
        if (valuesBefore == null || valuesBefore.length() == 0) {
            setValues(value);
        } else {
            setValues(valuesBefore + ',' + value);
        }
    }

    public String getConstant() {
        return (String) getDetailValue(CONSTANT);
    }

    public void setConstant(String constant) {
        setDetailValue(CONSTANT, constant);
    }

    public String getMap() {
        return (String) getDetailValue(MAP);
    }

    public void setMap(String map) {
        setDetailValue(MAP, map);
    }

    // literate build helpers ------------------------------------------------------------------------------------------

    public SimpleTypeDescriptor withMin(String min) {
        setMin(min);
        return this;
    }

    public SimpleTypeDescriptor withMax(String max) {
        setMax(max);
        return this;
    }

    public SimpleTypeDescriptor withGranularity(String granularity) {
        setGranularity(granularity);
        return this;
    }

    public SimpleTypeDescriptor withPattern(String pattern) {
        setPattern(pattern);
        return this;
    }

    public SimpleTypeDescriptor withDistribution(String distribution) {
        setDistribution(distribution);
        return this;
    }

    public SimpleTypeDescriptor withDataset(String dataset) {
        setDataset(dataset);
        return this;
    }

    public SimpleTypeDescriptor withLocaleId(String localeId) {
        setLocaleId(localeId);
        return this;
    }

    public SimpleTypeDescriptor withTrueQuota(Double trueQuota) {
        setTrueQuota(trueQuota);
        return this;
    }

    public SimpleTypeDescriptor withUri(String source) {
        setSource(source);
        return this;
    }

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

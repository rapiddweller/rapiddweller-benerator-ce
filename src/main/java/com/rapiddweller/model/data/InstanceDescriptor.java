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

import com.rapiddweller.benerator.engine.expression.ScriptExpression;
import com.rapiddweller.commons.operation.AndOperation;
import com.rapiddweller.commons.operation.OrOperation;
import com.rapiddweller.script.Expression;
import com.rapiddweller.script.expression.ConstantExpression;
import com.rapiddweller.script.expression.TypeConvertingExpression;

/**
 * Describes generation of (several) entities of a type by uniqueness,
 * nullability and count characteristics.<br/><br/>
 * Created: 03.03.2008 07:55:45
 *
 * @author Volker Bergmann
 * @since 0.5.0
 */
public class InstanceDescriptor extends FeatureDescriptor {

    public static final String TYPE = "type";

    // constraints
    public static final String UNIQUE = "unique";
    public static final String NULLABLE = "nullable";
    public static final String MIN_COUNT = "minCount";
    public static final String MAX_COUNT = "maxCount";
    public static final String CONTAINER = "container";

    // configs
    public static final String COUNT_GRANULARITY = "countGranularity";
    public static final String COUNT_DISTRIBUTION = "countDistribution";
    public static final String COUNT = "count";
    public static final String NULL_QUOTA = "nullQuota";
    public static final String MODE = "mode";


    private InstanceDescriptor parent;
    private TypeDescriptor localType;

    // constructors ----------------------------------------------------------------------------------------------------

    public InstanceDescriptor(String name, DescriptorProvider provider) {
        this(name, provider, null, null);
    }

    public InstanceDescriptor(String name, DescriptorProvider provider, String typeName) {
        this(name, provider, typeName, null);
    }

    public InstanceDescriptor(String name, DescriptorProvider provider, TypeDescriptor localType) {
        this(name, provider, null, localType);
    }

    protected InstanceDescriptor(String name, DescriptorProvider provider, String typeName, TypeDescriptor localType) {
        super(name, provider);
        this.localType = localType;

        addConstraint(TYPE, String.class, null);
        setType(typeName);

        // constraints
        addConstraint(UNIQUE, Boolean.class, new OrOperation());
        addConstraint(NULLABLE, Boolean.class, new AndOperation());
        addConstraint(MIN_COUNT, Expression.class, null);
        addConstraint(MAX_COUNT, Expression.class, null);
        addConstraint(CONTAINER, String.class, null);

        // configs
        addConfig(COUNT, Expression.class);
        addConfig(COUNT_GRANULARITY, Expression.class);
        addConfig(COUNT_DISTRIBUTION, String.class);
        addConfig(NULL_QUOTA, Double.class);
        addConfig(MODE, Mode.class);
    }

    // properties ------------------------------------------------------------------------------------------------------

    public void setParent(InstanceDescriptor parent) {
        this.parent = parent;
    }

    @Override
    public String getName() {
        String result = super.getName();
        return (result != null ? result : getType());
    }

    public String getType() {
        String type = (String) getDetailValue(TYPE);
        return (type == null && parent != null ? parent.getType() : type);
    }

    public void setType(String type) {
        setDetailValue(TYPE, type);
    }

    public TypeDescriptor getTypeDescriptor() {
        if (getLocalType() != null)
            return getLocalType();
        TypeDescriptor type = null;
        if (getType() != null)
            type = getDataModel().getTypeDescriptor(getType());
        return type;
    }

    public TypeDescriptor getLocalType() {
        if (localType == null && parent != null && parent.getLocalType() != null)
            localType = getLocalType(parent.getLocalType() instanceof ComplexTypeDescriptor);
        return localType;
    }

    public void setLocalType(TypeDescriptor localType) {
        this.localType = localType;
        if (localType != null)
            setType(null);
    }

    public TypeDescriptor getLocalType(boolean complexType) {
        if (localType != null)
            return localType;
        if (complexType)
            localType = new ComplexTypeDescriptor(getName(), provider, getType());
        else
            localType = new SimpleTypeDescriptor(getName(), provider, getType());
        setType(null);
        return localType;
    }

    public Boolean isUnique() {
        return (Boolean) getDetailValue(UNIQUE);
    }

    public void setUnique(Boolean unique) {
        setDetailValue(UNIQUE, unique);
    }

    public Uniqueness getUniqueness() {
        Boolean unique = isUnique();
        return (unique != null ? (unique ? Uniqueness.SIMPLE : Uniqueness.NONE) : null);
    }

    public Boolean isNullable() {
        Boolean value = (Boolean) super.getDetailValue(NULLABLE);
        if (value == null && parent != null) {
            FeatureDetail<?> detail = parent.getConfiguredDetail(NULLABLE);
            if (detail.getValue() != null && !(Boolean) detail.getValue())
                value = (Boolean) detail.getValue();
        }
        return value;
    }

    public void setNullable(Boolean nullable) {
        setDetailValue(NULLABLE, nullable);
    }

    @SuppressWarnings("unchecked")
    public Expression<Long> getMinCount() {
        return (Expression<Long>) getDetailValue(MIN_COUNT);
    }

    public void setMinCount(Expression<Long> minCount) {
        setDetailValue(MIN_COUNT, minCount);
    }

    @SuppressWarnings("unchecked")
    public Expression<Long> getMaxCount() {
        return (Expression<Long>) getDetailValue(MAX_COUNT);
    }

    public void setMaxCount(Expression<Long> maxCount) {
        setDetailValue(MAX_COUNT, maxCount);
    }

    public String getContainer() {
        return (String) getDetailValue(CONTAINER);
    }

    public void setContainer(String container) {
        setDetailValue(CONTAINER, container);
    }

    @SuppressWarnings("unchecked")
    public Expression<Long> getCount() {
        return (Expression<Long>) getDetailValue(COUNT);
    }

    public void setCount(Expression<Long> count) {
        setDetailValue(COUNT, count);
    }

    public String getCountDistribution() {
        return (String) getDetailValue(COUNT_DISTRIBUTION);
    }

    public void setCountDistribution(String distribution) {
        setDetailValue(COUNT_DISTRIBUTION, distribution);
    }

    @SuppressWarnings("unchecked")
    public Expression<Long> getCountGranularity() {
        return (Expression<Long>) getDetailValue(COUNT_GRANULARITY);
    }

    public void setCountGranularity(Expression<Long> distribution) {
        setDetailValue(COUNT_GRANULARITY, distribution);
    }

    public Double getNullQuota() {
        return (Double) getDetailValue(NULL_QUOTA);
    }

    public void setNullQuota(Double nullQuota) {
        setDetailValue(NULL_QUOTA, nullQuota);
    }

    public Mode getMode() {
        return (Mode) getDetailValue(MODE);
    }

    public void setMode(Mode mode) {
        setDetailValue(MODE, mode);
    }

    @Override
    public Object getDetailValue(String name) {
        Object value = super.getDetailValue(name);
        if (value == null && parent != null && parent.supportsDetail(name)) {
            FeatureDetail<?> detail = parent.getConfiguredDetail(name);
            if (detail.isConstraint())
                value = detail.getValue();
        }
        return value;
    }

    @Override
    public void setDetailValue(String detailName, Object detailValue) {
        if (COUNT.equals(detailName) || MIN_COUNT.equals(detailName) || MAX_COUNT.equals(detailName) || COUNT_GRANULARITY.equals(detailName)) {
            FeatureDetail<Object> detail = getConfiguredDetail(detailName);
            if (detail == null)
                throw new UnsupportedOperationException(getClass().getSimpleName() + " does not support detail type: " + detailName);
            if (detailValue instanceof Expression)
                detail.setValue(new TypeConvertingExpression<Long>((Expression<?>) detailValue, Long.class));
            else if (detailValue == null)
                detail.setValue(null);
            else if (detailValue instanceof String)
                detail.setValue(new TypeConvertingExpression<Long>(new ScriptExpression<Object>((String) detailValue), Long.class));
            else
                detail.setValue(new TypeConvertingExpression<Long>(new ConstantExpression<Object>(detailValue), Long.class));
        } else
            super.setDetailValue(detailName, detailValue);
    }

    // convenience 'with...' methods -----------------------------------------------------------------------------------

    public InstanceDescriptor withCount(long count) {
        setCount(new ConstantExpression<Long>(count));
        return this;
    }

    public InstanceDescriptor withMinCount(long minCount) {
        setMinCount(new ConstantExpression<Long>(minCount));
        return this;
    }

    public InstanceDescriptor withMaxCount(long maxCount) {
        setMaxCount(new ConstantExpression<Long>(maxCount));
        return this;
    }

    public InstanceDescriptor withNullQuota(double nullQuota) {
        setNullQuota(nullQuota);
        return this;
    }

    public InstanceDescriptor withUnique(boolean unique) {
        setUnique(unique);
        return this;
    }

    public boolean overwritesParent() {
        return parent != null;
    }

    @Override
    public String toString() {
        return getName() + "[details=" + renderDetails() + ", type=" + getTypeDescriptor() + "]";
    }

}

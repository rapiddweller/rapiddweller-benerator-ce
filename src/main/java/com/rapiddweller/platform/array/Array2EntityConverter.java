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

package com.rapiddweller.platform.array;

import com.rapiddweller.model.data.*;
import com.rapiddweller.commons.*;
import com.rapiddweller.commons.converter.AnyConverter;
import com.rapiddweller.commons.converter.ConverterManager;
import com.rapiddweller.commons.converter.NoOpConverter;
import com.rapiddweller.commons.converter.ThreadSafeConverter;
import com.rapiddweller.commons.mutator.ConvertingMutator;

/**
 * Converts an array of feature values to an entity.<br/>
 * <br/>
 * Created: 26.08.2007 12:27:45
 *
 * @author Volker Bergmann
 */
public class Array2EntityConverter extends ThreadSafeConverter<Object[], Entity> {

    protected Escalator escalator = new LoggerEscalator();
    private final ComplexTypeDescriptor descriptor;
    private final String[] featureNames;
    private final Mutator[] entityMutators;

    public Array2EntityConverter(ComplexTypeDescriptor descriptor, String[] featureNames, boolean stringSource) {
        super(Object[].class, Entity.class);
        this.descriptor = descriptor;
        this.featureNames = featureNames;
        this.entityMutators = new Mutator[featureNames.length];
        for (int i = 0; i < featureNames.length; i++)
            this.entityMutators[i] = createFeatureMutator(featureNames[i], descriptor, stringSource);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static Converter<?, ?> createConverter(String featureName, ComplexTypeDescriptor complexType, boolean stringSource) {
        if (complexType != null) {
            ComponentDescriptor component = complexType.getComponent(featureName);
            // if all parts of the feature path have been defined in the associated descriptors,
            // then determine an appropriate converter
            if (component != null && component.getTypeDescriptor() != null) {
                TypeDescriptor componentType = component.getTypeDescriptor();
                if (componentType instanceof SimpleTypeDescriptor) {
                    Class<?> javaType = ((SimpleTypeDescriptor) componentType).getPrimitiveType().getJavaType();
                    if (stringSource)
                        return ConverterManager.getInstance().createConverter(String.class, javaType);
                    else
                        return new AnyConverter(javaType);
                }
            }
        }
        return new NoOpConverter();
    }

    private static ComplexTypeDescriptor getComplexType(String featureName, ComplexTypeDescriptor parentType) {
        ComplexTypeDescriptor complexType = parentType;
        // for sub paths, iterate recursively through the component names separated with '.'
        while (featureName.contains(".") && complexType != null) {
            String[] pathComponents = StringUtil.splitOnFirstSeparator(featureName, '.');
            String partName = pathComponents[0];
            ComponentDescriptor component = complexType.getComponent(partName);
            if (component != null)
                complexType = (ComplexTypeDescriptor) component.getTypeDescriptor();
            else
                complexType = null;
            featureName = pathComponents[1];
        }
        return complexType;
    }

    @SuppressWarnings("rawtypes")
    protected ConvertingMutator createFeatureMutator(
            String featureName, ComplexTypeDescriptor descriptor, boolean stringSource) {
        ComplexTypeDescriptor complexType = getComplexType(featureName, descriptor);
        Converter converter = createConverter(featureName, complexType, stringSource);
        Mutator mutator = new EntityGraphMutator(featureName, descriptor);
        return new ConvertingMutator(mutator, converter);
    }


    // private helper methods ------------------------------------------------------------------------------------------

    @Override
    public Entity convert(Object[] sourceValue) {
        if (sourceValue == null)
            return null;
        Entity entity = new Entity(descriptor);
        int length;
        if (sourceValue.length > featureNames.length) {
            escalator.escalate("Row has more columns than specified in the file header", this, sourceValue);
            length = featureNames.length;
        } else
            length = sourceValue.length;
        for (int i = 0; i < length; i++)
            entityMutators[i].setValue(entity, sourceValue[i]);
        return entity;
    }


    // java.lang.Object overrides --------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + '[' + ArrayFormat.format(", ", featureNames) + ']';
    }

}

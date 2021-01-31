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

package com.rapiddweller.platform.java;

import com.rapiddweller.common.BeanUtil;
import com.rapiddweller.common.converter.ThreadSafeConverter;
import com.rapiddweller.common.mutator.AnyMutator;
import com.rapiddweller.model.data.Entity;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Map;

/**
 * Converts entities and entity arrays to Java beans and bean arrays.<br/>
 * <br/>
 * Created: 29.08.2007 08:50:24
 *
 * @author Volker Bergmann
 */
public class Entity2JavaConverter extends ThreadSafeConverter<Object, Object> {

    public Entity2JavaConverter() {
        super(Object.class, Object.class);
    }

    public static Object convertAny(Object entityOrArray) {
        if (entityOrArray == null)
            return null;
        else if (entityOrArray instanceof Entity)
            return convertEntity((Entity) entityOrArray, BeanUtil.forName(((Entity) entityOrArray).type()));
        else if (entityOrArray.getClass().isArray())
            return convertArray((Object[]) entityOrArray);
        else
            return entityOrArray;
    }

    public static Object convertAny(Object entityOrArray, Class<?> targetType) {
        if (entityOrArray == null)
            return null;
        else if (entityOrArray instanceof Entity)
            return convertEntity((Entity) entityOrArray, targetType);
        else if (entityOrArray.getClass().isArray())
            return convertArray((Object[]) entityOrArray, targetType);
        else
            return entityOrArray;
    }

    private static Object convertArray(Object[] array, Class<?> targetType) {
        Object result = Array.newInstance(targetType, array.length);
        for (int i = 0; i < array.length; i++) {
            Object value = convertAny(array[i], targetType);
            Array.set(result, i, value);
        }
        return result;
    }

    private static Object convertArray(Object[] array) {
        Object[] result = new Object[array.length];
        for (int i = 0; i < array.length; i++)
            result[i] = convertAny(array[i]);
        return result;
    }

    private static Object convertEntity(Entity entity, Class<?> targetBeanType) {
        Object result = BeanUtil.newInstance(targetBeanType);
        for (Map.Entry<String, Object> entry : entity.getComponents().entrySet()) {
            String featureName = entry.getKey();
            Class<?> targetComponentType = typeOrComponentTypeOf(featureName, targetBeanType);
            if (targetComponentType != null) { // if the target object does not contain a feature of the given name, ignore the entry
                Object value = convertAny(entry.getValue(), targetComponentType);
                AnyMutator.setValue(result, featureName, value, false, true);
            }
        }
        return result;
    }

    private static Class<?> typeOrComponentTypeOf(String featureName, Class<?> beanClass) {
        Class<?> propertyType = null;
        PropertyDescriptor propertyDescriptor = BeanUtil.getPropertyDescriptor(beanClass, featureName);
        if (propertyDescriptor != null) {
            propertyType = propertyDescriptor.getPropertyType();
        } else {
            try {
                Field field = BeanUtil.getField(beanClass, featureName);
                propertyType = field.getType();
            } catch (Exception e) {
                return null;
            }
        }
        if (propertyType.isArray())
            return propertyType.getComponentType();
        else if (Collection.class.isAssignableFrom(propertyType))
            return getCollectionType(propertyDescriptor);
        else
            return propertyType;
    }

    private static Class<?> getCollectionType(PropertyDescriptor propertyDescriptor) {
        ParameterizedType genericReturnType = (ParameterizedType) propertyDescriptor.getReadMethod().getGenericReturnType();
        return (Class<?>) genericReturnType.getActualTypeArguments()[0];
    }

    @Override
    public Object convert(Object entityOrArray) {
        return convertAny(entityOrArray);
    }

}

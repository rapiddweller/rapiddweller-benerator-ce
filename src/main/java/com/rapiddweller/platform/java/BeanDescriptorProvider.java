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

import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.common.BeanUtil;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.DataModel;
import com.rapiddweller.model.data.DefaultDescriptorProvider;
import com.rapiddweller.model.data.PartDescriptor;
import com.rapiddweller.model.data.SimpleTypeDescriptor;
import com.rapiddweller.model.data.TypeDescriptor;
import com.rapiddweller.model.data.TypeMapper;
import com.rapiddweller.script.expression.ConstantExpression;

import java.beans.PropertyDescriptor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Provides EntityDescriptors for JavaBean classes
 * Created: 27.06.2007 23:04:19
 * @author Volker Bergmann
 */
public class BeanDescriptorProvider extends DefaultDescriptorProvider {

  public static final String NAMESPACE = "bean";

  private TypeMapper mapper;

  public BeanDescriptorProvider() {
    this(new DataModel());
  }

  public BeanDescriptorProvider(DataModel dataModel) {
    super(NAMESPACE, dataModel, false);
    if (dataModel == null) {
      throw BeneratorExceptionFactory.getInstance().illegalArgument("DataModel is null");
    }
    initMapper();
  }

  // interface -------------------------------------------------------------------------------------------------------

  @Override
  public TypeDescriptor getTypeDescriptor(String abstractTypeName) {
    if (mapper.concreteType(abstractTypeName) != null) {
      return null;
    }
    TypeDescriptor result = super.getTypeDescriptor(abstractTypeName);
    if (result == null && BeanUtil.existsClass(abstractTypeName)) {
      result = createTypeDescriptor(BeanUtil.forName(abstractTypeName));
    }
    return result;
  }

  /**
   * @param concreteType the concrete type
   * @return the abstract type that corresponds to the specified concrete type
   * @see TypeMapper#abstractType(Class) TypeMapper#abstractType(Class)
   */
  public String abstractType(Class<?> concreteType) {
    String result = mapper.abstractType(concreteType);
    if (result == null) {
      result = concreteType.getName();
    }
    return result;
  }

  /**
   * @param primitiveType the primitive type
   * @return the abstract type that corresponds to the specified primitive type
   * @see TypeMapper#concreteType(java.lang.String) TypeMapper#concreteType(java.lang.String)
   */
  public Class<?> concreteType(String primitiveType) {
    try {
      Class<?> result = mapper.concreteType(primitiveType);
      if (result == null) {
        result = Class.forName(primitiveType);
      }
      return result;
    } catch (ClassNotFoundException e) {
      throw BeneratorExceptionFactory.getInstance().configurationError("No class mapping found for '" + primitiveType + "'", e);
    }
  }

  public void clear() {
    typeMap.clear();
    initMapper();
  }

  // private helpers -------------------------------------------------------------------------------------------------

  private void initMapper() {
    mapper = new TypeMapper(
        "byte", byte.class,
        "byte", Byte.class,

        "short", short.class,
        "short", Short.class,

        "int", int.class,
        "int", Integer.class,

        "long", long.class,
        "long", Long.class,

        "big_integer", BigInteger.class,

        "float", float.class,
        "float", Float.class,

        "double", double.class,
        "double", Double.class,

        "big_decimal", BigDecimal.class,

        "boolean", boolean.class,
        "boolean", Boolean.class,

        "char", char.class,
        "char", Character.class,

        "date", java.util.Date.class,
        "time", java.sql.Time.class,
        "timestamp", java.sql.Timestamp.class,

        "string", String.class,
        "object", Object.class,
        "binary", byte[].class
    );
  }

  @SuppressWarnings("checkstyle:Indentation")
  private TypeDescriptor createTypeDescriptor(Class<?> javaType) {
    // check for primitive type
    String className = javaType.getName();

    SimpleTypeDescriptor simpleTypePure = getDataModel().getPrimitiveTypeDescriptor(javaType);
    if (simpleTypePure != null) {
      return simpleTypePure;
    }

    // check for enum
    if (javaType.isEnum()) {
      SimpleTypeDescriptor simpleType = new SimpleTypeDescriptor(className, this, "string");
      Object[] instances = javaType.getEnumConstants();
      StringBuilder builder = new StringBuilder();
      for (int i = 0; i < instances.length; i++) {
        if (i > 0) {
          builder.append(",");
        }
        builder.append("'").append(instances[i]).append("'");
      }
      simpleType.setValues(builder.toString());
      addTypeDescriptor(simpleType);
      return simpleType;
    }

    // assert complex type
    ComplexTypeDescriptor td = new ComplexTypeDescriptor(className, this);
    addTypeDescriptor(td);
    for (PropertyDescriptor propertyDescriptor : BeanUtil.getPropertyDescriptors(javaType)) {
      createDescriptorForProperty(propertyDescriptor, td);
    }
    return td;
  }

  private void createDescriptorForProperty(PropertyDescriptor propertyDescriptor, ComplexTypeDescriptor td) {
    if ("class".equals(propertyDescriptor.getName())) {
      return;
    }
    Class<?> propertyType = propertyDescriptor.getPropertyType();
    TypeDescriptor propertyTypeDescriptor;
    if (java.util.Collection.class.isAssignableFrom(propertyType)) {
      ParameterizedType genericReturnType = (ParameterizedType) propertyDescriptor.getReadMethod().getGenericReturnType();
      Type componentType = genericReturnType.getActualTypeArguments()[0];
      propertyTypeDescriptor = dataModel.getTypeDescriptor(((Class<?>) componentType).getName());
    } else if (propertyType.isArray()) {
      Class<?> componentType = propertyType.getComponentType();
      propertyTypeDescriptor = dataModel.getTypeDescriptor(componentType.getName());
    } else {
      propertyTypeDescriptor = dataModel.getTypeDescriptor(propertyType.getName());
    }
    PartDescriptor pd = new PartDescriptor(propertyDescriptor.getName(), this, propertyTypeDescriptor);
    if (java.util.Collection.class.isAssignableFrom(propertyType) || propertyType.isArray()) {
      pd.setMinCount(new ConstantExpression<>(0L));
      pd.setMaxCount(null);
    }
    td.setComponent(pd);
  }

}

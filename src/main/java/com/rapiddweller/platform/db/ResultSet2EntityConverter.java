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

package com.rapiddweller.platform.db;

import com.rapiddweller.common.converter.AnyConverter;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.ComponentDescriptor;
import com.rapiddweller.model.data.DataModel;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.model.data.SimpleTypeDescriptor;
import com.rapiddweller.script.PrimitiveType;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * Converts a SQL {@link ResultSet} to a Benerator {@link Entity}.<br/><br/>
 * Created: 24.08.2010 12:29:56
 * @author Volker Bergmann
 * @since 0.6.4
 */
public class ResultSet2EntityConverter {

  private ResultSet2EntityConverter() {
    // private constructor to prevent instantiation
  }

  public static Entity convert(ResultSet resultSet, ComplexTypeDescriptor descriptor) throws SQLException {
    Entity entity = new Entity(descriptor);
    ResultSetMetaData metaData = resultSet.getMetaData();
    int columnCount = metaData.getColumnCount();
    for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
      String columnName = metaData.getColumnName(columnIndex);
      String typeName = null;
      if (descriptor != null) {
        ComponentDescriptor component = descriptor.getComponent(columnName);
        if (component != null) {
          SimpleTypeDescriptor type = (SimpleTypeDescriptor) component.getTypeDescriptor();
          PrimitiveType primitiveType = type.getPrimitiveType();
          typeName = (primitiveType != null ? primitiveType.getName() : "string");
        } else {
          typeName = "string";
        }
      } else {
        typeName = "string";
      }
      DataModel dataModel = (descriptor != null ? descriptor.getDataModel() : null);
      Object javaValue = javaValue(resultSet, columnIndex, typeName, dataModel);
      entity.setComponent(columnName, javaValue);
    }
    return entity;
  }

  // TODO v1.0 perf: use a dedicated converter for each column
  private static Object javaValue(ResultSet resultSet, int columnIndex,
                                  String primitiveType, DataModel dataModel)
      throws SQLException {
    if ("date".equals(primitiveType)) {
      return resultSet.getDate(columnIndex);
    } else if ("timestamp".equals(primitiveType)) {
      return resultSet.getTimestamp(columnIndex);
    } else if ("string".equals(primitiveType)) {
      return resultSet.getString(columnIndex);
    }
    // try generic conversion
    Object driverValue = resultSet.getObject(columnIndex);
    Object javaValue = driverValue;
    if (dataModel != null) {
      Class<?> javaType = dataModel.getBeanDescriptorProvider().concreteType(primitiveType);
      javaValue = AnyConverter.convert(driverValue, javaType);
    }
    return javaValue;
  }

}

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

package com.rapiddweller.benerator.composite;

import com.rapiddweller.common.ConversionException;
import com.rapiddweller.common.converter.AbstractConverter;
import com.rapiddweller.common.converter.AnyConverter;
import com.rapiddweller.model.data.ArrayElementDescriptor;
import com.rapiddweller.model.data.ArrayTypeDescriptor;
import com.rapiddweller.model.data.SimpleTypeDescriptor;
import com.rapiddweller.model.data.TypeDescriptor;
import com.rapiddweller.script.PrimitiveType;

/**
 * Converts an array's elements to the types defined in a related {@link ArrayTypeDescriptor}.<br/><br/>
 * Created: 05.05.2010 15:36:41
 *
 * @author Volker Bergmann
 * @since 0.6.1
 */
public class ArrayElementTypeConverter extends AbstractConverter<Object[], Object[]> {

  private final ArrayTypeDescriptor type;

  /**
   * Instantiates a new Array element type converter.
   *
   * @param type the type
   */
  public ArrayElementTypeConverter(ArrayTypeDescriptor type) {
    super(Object[].class, Object[].class);
    this.type = type;
  }

  @Override
  public Object[] convert(Object[] array) throws ConversionException {
    if (array == null) {
      return null;
    }
    for (int i = 0; i < array.length; i++) {
      ArrayElementDescriptor elementDescriptor = type.getElement(i, true);
      if (elementDescriptor != null) {
        TypeDescriptor elementType = elementDescriptor.getTypeDescriptor();
        Object elementValue = array[i];
        if (elementType instanceof SimpleTypeDescriptor) {
          PrimitiveType primitive = ((SimpleTypeDescriptor) elementType).getPrimitiveType();
          if (primitive == null) {
            primitive = PrimitiveType.STRING;
          }
          Class<?> javaType = primitive.getJavaType();
          Object javaValue = AnyConverter.convert(elementValue, javaType);
          array[i] = javaValue;
        } else {
          array[i] = elementValue;
        }
      }
    }
    return array;
  }

  @Override
  public boolean isParallelizable() {
    return false;
  }

  @Override
  public boolean isThreadSafe() {
    return false;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "[" + type + "]";
  }

}

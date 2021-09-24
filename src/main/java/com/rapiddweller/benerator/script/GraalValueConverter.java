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

package com.rapiddweller.benerator.script;

import com.rapiddweller.common.ConversionException;
import com.rapiddweller.common.converter.ThreadSafeConverter;
import org.graalvm.polyglot.Value;


/**
 * Convert Graal Values into Java Types
 * https://www.graalvm.org/sdk/javadoc/org/graalvm/polyglot/Value.html
 * <p>
 * Created at 30.12.2020
 *
 * @author Alexander Kell
 * @since 1.1.0
 */
public class GraalValueConverter extends ThreadSafeConverter<Value, Object> {

  /**
   * Instantiates a new Graal value converter.
   */
  public GraalValueConverter() {
    super(Value.class, Object.class);
  }

  /**
   * Value 2 java converter object.
   *
   * @param value the value
   * @return the object
   */
  public static Object value2JavaConverter(Value value) {
    return value.fitsInInt() ? value.asInt() :
        value.fitsInLong() ? value.asLong() :
            value.fitsInFloat() ? value.asFloat() :
                value.fitsInByte() ? value.asByte() :
                    value.fitsInDouble() ? value.asDouble() :
                        value.fitsInFloat() ? value.asFloat() :
                            value.isString() ? value.asString() :
                                value.isHostObject() ? value.asHostObject() :
                                    value.isBoolean() ? value.asBoolean() :
                                        value.isDate() ? value.asDate() :
                                            value.isNativePointer() ? value.asNativePointer() :
                                                value.hasArrayElements() ?
                                                    getArrayFromValue(value) :
                                                    value.canExecute() ? null :
                                                        null;
  }

  private static Object getArrayFromValue(Value val) {
    Object[] out = new Object[(int) val.getArraySize()];
    for (int i = 0; i < val.getArraySize(); i++) {
      out[i] = value2JavaConverter(val.getArrayElement(i));
    }

    return out;
  }

  @Override
  public Object convert(Value sourceValue) throws ConversionException {
    return value2JavaConverter(sourceValue);
  }
}

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
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.Entity;
import org.graalvm.polyglot.Value;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Converts GraalVM Polyglot Values into corresponding Java types.
 * Throws RuntimeException if a value exceeds the int limits.
 */
public class GraalValueConverter extends ThreadSafeConverter<Value, Object> {

  private static final Logger logger = Logger.getLogger(GraalValueConverter.class.getName());

  public GraalValueConverter() {
    super(Value.class, Object.class);
  }

  public static Object value2JavaConverter(Value value) {
    return value2JavaConverter(value, new HashMap<>(), 0);
  }

  private static Object value2JavaConverter(Value value, Map<Value, Object> referenceMap, int depth) throws ConversionException {
    logger.fine("Converting value at depth " + depth + ": " + value);
    if (referenceMap.containsKey(value)) {
      logger.fine("Value already processed, returning cached result.");
      return referenceMap.get(value);
    }

    Object result;
    try {
      if (value.fitsInInt()) {
        result = value.asInt();
      } else if (value.fitsInLong()) {
        result = handleLongValue(value);
      } else if (value.fitsInFloat()) {
        return value.asFloat();
      } else if (value.fitsInByte()) {
        return value.asByte();
      } else if (value.fitsInDouble()) {
        return value.asDouble();
      } else if (value.hasArrayElements()) {
        result = getArrayFromValue(value, referenceMap, depth);
      } else if (value.isString()) {
        result = value.asString();
      } else if (value.isHostObject()) {
        return value.asHostObject();
      } else if (value.isBoolean()) {
        return value.asBoolean();
      } else if (value.isDate()) {
        return value.asDate();
      } else if (value.isNativePointer()) {
        return value.asNativePointer();
      } else if (value.hasMembers()) {
        result = convertValueToEntity(value, referenceMap, depth);
      } else {
        result = null;
      }

      referenceMap.put(value, result);
    } catch (Exception e) {
      logger.severe("Error converting value: " + e.getMessage());
      throw new ConversionException("Error converting value", e);
    }

    return result;
  }

  private static Object handleLongValue(Value value) {
    long longValue = value.asLong();
    if (longValue > Integer.MAX_VALUE || longValue < Integer.MIN_VALUE) {
      throw new RuntimeException("Value exceeds int limits: " + longValue);
    }
    return (int) longValue;
  }

  private static Object[] getArrayFromValue(Value val, Map<Value, Object> referenceMap, int depth) throws ConversionException {
    Object[] out = new Object[(int) val.getArraySize()];
    for (int i = 0; i < val.getArraySize(); i++) {
      out[i] = value2JavaConverter(val.getArrayElement(i), referenceMap, depth + 1);
    }
    return out;
  }

  private static Entity convertValueToEntity(Value value, Map<Value, Object> referenceMap, int depth) {
    Entity result = new Entity((ComplexTypeDescriptor) null);
    value.getMemberKeys().forEach(key -> {
      try {
        result.setComponent(key, value2JavaConverter(value.getMember(key), referenceMap, depth + 1));
      } catch (ConversionException e) {
        logger.warning("Error converting entity member: " + key + "; " + e.getMessage());
      }
    });
    return result;
  }

  @Override
  public Object convert(Value sourceValue) throws ConversionException {
    return value2JavaConverter(sourceValue);
  }
}


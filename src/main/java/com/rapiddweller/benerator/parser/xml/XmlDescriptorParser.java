/*
 * (c) Copyright 2006-2021 by rapiddweller GmbH & Volker Bergmann. All rights reserved.
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

package com.rapiddweller.benerator.parser.xml;

import com.rapiddweller.common.Context;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.common.converter.AnyConverter;
import com.rapiddweller.common.converter.ToStringConverter;
import com.rapiddweller.format.script.ScriptUtil;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

/**
 * Utility class for parsing benerator descriptors in XML format.<br/><br/>
 * Created at 02.01.2009 17:27:31
 * @author Volker Bergmann
 * @since 0.5.7
 */
public class XmlDescriptorParser {

  private XmlDescriptorParser() {
  }

  // attribute parsing -----------------------------------------------------------------------------------------------

  public static String parseStringAttribute(Element element, String name, Context context) {
    return parseStringAttribute(element, name, context, true);
  }

  public static String parseStringAttribute(Element element, String name, Context context, boolean resolveScript) {
    Object value = parseAttribute(element, name, context, resolveScript);
    return ToStringConverter.convert(value, null);
  }

  public static String parseStringAttribute(Attr attribute, Context context) {
    Object value = resolveScript(attribute.getName(), attribute.getValue(), context);
    return StringUtil.unescape(ToStringConverter.convert(value, null));
  }

  public static int parseIntAttribute(Element element, String name, Context context, int defaultValue) {
    Object value = parseAttribute(element, name, context);
    if (value instanceof Number) {
      return ((Number) value).intValue();
    } else if (value == null || (value instanceof String && StringUtil.isEmpty((String) value))) {
      return defaultValue;
    } else {
      return AnyConverter.convert(value, Integer.class);
    }
  }

  public static long parseLongAttribute(Element element, String name, Context context, long defaultValue) {
    Object value = parseAttribute(element, name, context);
    if (value instanceof Number) {
      return ((Number) value).longValue();
    } else if (value == null || (value instanceof String && StringUtil.isEmpty((String) value))) {
      return defaultValue;
    } else {
      return AnyConverter.convert(value, Long.class);
    }
  }

  public static boolean parseBooleanAttribute(Element element, String name,
                                              Context context, boolean defaultValue) {
    Object value = parseAttribute(element, name, context);
    if (value instanceof Boolean) {
      return (Boolean) value;
    } else if (value == null || (value instanceof String && StringUtil.isEmpty((String) value))) {
      return defaultValue;
    } else {
      return AnyConverter.convert(value, Boolean.class);
    }
  }

  public static Object parseAttribute(Element element, String name, Context context) {
    return parseAttribute(element, name, context, true);
  }

  public static Object parseAttribute(Element element, String name, Context context, boolean resolveScript) {
    String value = element.getAttribute(name);
    if (value != null && value.length() == 0) {
      value = null;
    }
    return resolveScript(name, value, context);
  }

  public static Object parseAttribute(Attr attribute, Context context) {
    String name = attribute.getName();
    String value = attribute.getValue();
    return resolveScript(name, value, context);
  }

  public static Object resolveScript(String name, String value, Context context) {
    if (value == null || "script".equals(name) || "dynamicSource".equals(name)) {
      return value;
    } else {
      return ScriptUtil.evaluate(value, context);
    }
  }

}

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

package com.rapiddweller.platform.xml;

import com.rapiddweller.model.data.DataModel;
import com.rapiddweller.model.data.DefaultDescriptorProvider;
import com.rapiddweller.model.data.SimpleTypeDescriptor;

/**
 * Provides descriptors for the simple types predefined in the XML Schema definition.<br/><br/>
 * Created: 08.03.2008 11:04:04
 * @author Volker Bergmann
 * @since 0.5.0
 */
public class XMLNativeTypeDescriptorProvider extends DefaultDescriptorProvider {

  private static final String REGEX_YEAR = "\\-?\\d{4,}";
  private static final String REGEX_MONTH_NUMBER = "(0[1-9]|1[0-2])";
  private static final String REGEX_DAY_OF_MONTH = "(0[1-9]|[1-2][0-9]|3[01])";
  private static final String REGEX_TIMEZONE = "(Z|[+\\-]\\d{2}:\\d{2})";
  private static final String REGEX_OPTIONAL_TIMEZONE = REGEX_TIMEZONE + '?';

  public static final String STRING = "string";
  public static final String SHORT = "short";
  public static final String BIG_DECIMAL = "big_decimal";

  public XMLNativeTypeDescriptorProvider(String id, DataModel dataModel) {
    super(id, dataModel);

    // schema types that resemble the benerator primitives
    addTypeDescriptor(new SimpleTypeDescriptor(STRING, this, STRING));

    addTypeDescriptor(new SimpleTypeDescriptor("boolean", this, "boolean"));

    addTypeDescriptor(new SimpleTypeDescriptor("byte", this, "byte"));
    addTypeDescriptor(new SimpleTypeDescriptor(SHORT, this, SHORT));
    addTypeDescriptor(new SimpleTypeDescriptor("int", this, "int"));
    addTypeDescriptor(new SimpleTypeDescriptor("long", this, "long"));

    addTypeDescriptor(new SimpleTypeDescriptor("float", this, "float"));
    addTypeDescriptor(new SimpleTypeDescriptor("double", this, "double"));

    addTypeDescriptor(new SimpleTypeDescriptor("date", this, "date"));
    addTypeDescriptor(new SimpleTypeDescriptor("time", this, "time"));

    // schema specific types
    addTypeDescriptor(new SimpleTypeDescriptor("integer", this, "int"));
    addTypeDescriptor(new SimpleTypeDescriptor("nonPositiveInteger", this, "int").withMin("-2147483648").withMax("0"));
    addTypeDescriptor(new SimpleTypeDescriptor("negativeInteger", this, "int").withMin("-2147483648").withMax("-1"));
    addTypeDescriptor(new SimpleTypeDescriptor("nonNegativeInteger", this, "int").withMin("0"));
    addTypeDescriptor(new SimpleTypeDescriptor("positiveInteger", this, "int").withMin("1"));

    addTypeDescriptor(
        new SimpleTypeDescriptor("unsignedLong", this, BIG_DECIMAL).withMin("0").withMax("9223372036854775807")); // this is only Long.MAX_VALUE
    addTypeDescriptor(new SimpleTypeDescriptor("unsignedInt", this, "long").withMin("0").withMax("4294967295"));
    addTypeDescriptor(new SimpleTypeDescriptor("unsignedShort", this, "int").withMin("0").withMax("32767"));
    addTypeDescriptor(new SimpleTypeDescriptor("unsignedByte", this, SHORT).withMin("0").withMax("256"));

    addTypeDescriptor(new SimpleTypeDescriptor("decimal", this, BIG_DECIMAL));
    addTypeDescriptor(new SimpleTypeDescriptor("precisionDecimal", this, BIG_DECIMAL));

    addTypeDescriptor(new SimpleTypeDescriptor("dateTime", this, "timestamp"));

    addTypeDescriptor(new SimpleTypeDescriptor("duration", this, STRING).withPattern("\\-?P(\\d+Y)?(\\d+M)?(\\d+D)?(T(\\d+H)?(\\d+M)?(\\d+S)?)?"));
    addTypeDescriptor(
        new SimpleTypeDescriptor("gYearMonth", this, STRING).withPattern(REGEX_YEAR + "\\-" + REGEX_MONTH_NUMBER + REGEX_OPTIONAL_TIMEZONE));
    addTypeDescriptor(new SimpleTypeDescriptor("gYear", this, STRING).withPattern(REGEX_YEAR + REGEX_OPTIONAL_TIMEZONE));
    addTypeDescriptor(
        new SimpleTypeDescriptor("gMonthDay", this, STRING).withPattern(REGEX_MONTH_NUMBER + "\\-" + REGEX_DAY_OF_MONTH + REGEX_OPTIONAL_TIMEZONE));
    addTypeDescriptor(new SimpleTypeDescriptor("gDay", this, STRING).withPattern(REGEX_DAY_OF_MONTH + REGEX_OPTIONAL_TIMEZONE));
    addTypeDescriptor(new SimpleTypeDescriptor("gMonth", this, "int").withPattern(REGEX_MONTH_NUMBER + REGEX_OPTIONAL_TIMEZONE));

    addTypeDescriptor(new SimpleTypeDescriptor("hexBinary", this, STRING).withPattern("([0-9a-fA-F]{2})*"));
    addTypeDescriptor(new SimpleTypeDescriptor("base64Binary", this, STRING).withPattern("[a-zA-Z0-9+/= ]*"));

    addTypeDescriptor(new SimpleTypeDescriptor("anyURI", this, STRING));
    addTypeDescriptor(new SimpleTypeDescriptor("QName", this, STRING));
    addTypeDescriptor(new SimpleTypeDescriptor("NOTATION", this, STRING));
    addTypeDescriptor(new SimpleTypeDescriptor("normalizedString", this, STRING));
    addTypeDescriptor(new SimpleTypeDescriptor("token", this, STRING));
    addTypeDescriptor(new SimpleTypeDescriptor("language", this, STRING));
    addTypeDescriptor(new SimpleTypeDescriptor("NMTOKEN", this, STRING).withPattern("[A-Za-z:_\\-\\.0-9]*"));
    addTypeDescriptor(new SimpleTypeDescriptor("NMTOKENS", this, STRING));
    addTypeDescriptor(new SimpleTypeDescriptor("Name", this, STRING));
    addTypeDescriptor(new SimpleTypeDescriptor("NCName", this, STRING));
    addTypeDescriptor(new SimpleTypeDescriptor("ID", this, STRING)); // TODO support this in XML schema based generation
    addTypeDescriptor(new SimpleTypeDescriptor("IDREFS", this, STRING));
    addTypeDescriptor(new SimpleTypeDescriptor("ENTITY", this, STRING));
    addTypeDescriptor(new SimpleTypeDescriptor("ENTITIES", this, STRING));
  }

}

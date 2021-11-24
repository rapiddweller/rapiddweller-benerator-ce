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

package com.rapiddweller.domain.address;

import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;


/**
 * Formats a phone code.<br/>
 * The following pattern letters are defined:
 * <table>
 *   <tr><th>Letter</th><th>Phone Number Component</th></tr>
 *   <tr><td>c</td><td>country code</td></tr>
 *   <tr><td>a</td><td>area code</td></tr>
 *   <tr><td>l</td><td>local</td></tr>
 * </table>
 * Any other character will be used 'as is'.<br/>
 * Examples:
 * <table>
 *   <tr><th>Pattern</th><th>Rendered as</th></tr>
 *   <tr><td>+c-a-l</td><td>+49-1234-5678</td></tr>
 *   <tr><td>0al</td><td>012345678</td></tr>
 *   <tr><td>00c(a)l</td><td>0049(1234)5678</td></tr>
 * </table>
 * @author Volker Bergmann
 * @since 0.3.05
 */
public class PhoneNumberFormat extends Format {

  private static final long serialVersionUID = -7235352934060711517L;

  private final String pattern;

  public PhoneNumberFormat(String pattern) {
    this.pattern = pattern;
  }

  @Override
  public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
    PhoneNumber number = (PhoneNumber) obj;
    for (int i = 0; i < pattern.length(); i++) {
      char c = pattern.charAt(i);
      switch (c) {
        case 'c':
          toAppendTo.append(number.getCountryCode());
          break;
        case 'a':
          toAppendTo.append(number.getAreaCode());
          break;
        case 'l':
          toAppendTo.append(number.getLocalNumber());
          break;
        default:
          toAppendTo.append(c);
      }
    }
    return toAppendTo;
  }

  @Override
  public Object parseObject(String source, ParsePosition pos) {
    PhoneNumber number = new PhoneNumber();
    for (int i = 0; i < pattern.length(); i++) {
      char c = pattern.charAt(i);
      switch (c) {
        case 'c':
          number.setCountryCode(parseDigits(source, pos));
          break;
        case 'a':
          number.setAreaCode(parseDigits(source, pos));
          break;
        case 'l':
          number.setLocalNumber(parseDigits(source, pos));
          break;
        default:
          if (source.charAt(pos.getIndex()) != c) {
            throw BeneratorExceptionFactory.getInstance().illegalArgument(
                "Pattern '" + pattern + "' is not matched by String: " + source);
          }
          pos.setIndex(pos.getIndex() + 1);
      }
    }
    return number;
  }

  @Override
  public Object parseObject(String source) throws ParseException {
    try {
      return super.parseObject(source);
    } catch (IllegalArgumentException e) {
      throw BeneratorExceptionFactory.getInstance().syntaxErrorForText(
          source, "Failed to parse text as file number", -1, -1, e);
    }
  }

  private String parseDigits(String source, ParsePosition pos) {
    if (pos.getIndex() >= source.length()) {
      throw BeneratorExceptionFactory.getInstance().illegalArgument(
          "Text cannot be parsed unambiguously as phone number with pattern: " +
              pattern);
    }
    int start = pos.getIndex();
    int end;
    for (end = start; end < source.length(); end++) {
      char c = source.charAt(end);
      if (!Character.isDigit(c)) {
        break;
      }
    }
    pos.setIndex(end);
    return source.substring(start, end);
  }

}

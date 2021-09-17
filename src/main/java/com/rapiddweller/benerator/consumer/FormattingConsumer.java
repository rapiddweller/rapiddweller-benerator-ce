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

package com.rapiddweller.benerator.consumer;

import com.rapiddweller.common.Capitalization;
import com.rapiddweller.common.converter.ToStringConverter;

/**
 * Provides a datePattern property for child classes.<br/><br/>
 * Created at 08.04.2008 07:18:17
 * @author Volker Bergmann
 * @since 0.5.1
 */
public abstract class FormattingConsumer extends AbstractConsumer {

  protected final ToStringConverter plainConverter = new ToStringConverter();

  public String getNullString() {
    return plainConverter.getNullString();
  }

  public void setNullString(String nullString) {
    plainConverter.setNullString(nullString);
  }

  public String getDatePattern() {
    return plainConverter.getDatePattern();
  }

  public void setDatePattern(String datePattern) {
    plainConverter.setDatePattern(datePattern);
  }

  public Capitalization getDateCapitalization() {
    return plainConverter.getDateCapitalization();
  }

  public void setDateCapitalization(Capitalization dateCapitalization) {
    plainConverter.setDateCapitalization(dateCapitalization);
  }

  public String getDateTimePattern() {
    return plainConverter.getDateTimePattern();
  }

  public void setDateTimePattern(String dateTimePattern) {
    plainConverter.setDateTimePattern(dateTimePattern);
  }

  public String getTimestampPattern() {
    return plainConverter.getTimestampPattern();
  }

  public void setTimestampPattern(String timestampPattern) {
    plainConverter.setTimestampPattern(timestampPattern);
  }

  public Capitalization getTimestampCapitalization() {
    return plainConverter.getTimestampCapitalization();
  }

  public void setTimestampCapitalization(Capitalization timestampCapitalization) {
    plainConverter.setTimestampCapitalization(timestampCapitalization);
  }

  public String getDecimalPattern() {
    return plainConverter.getDecimalPattern();
  }

  public void setDecimalPattern(String decimalPattern) {
    plainConverter.setDecimalPattern(decimalPattern);
  }

  public char getDecimalSeparator() {
    return plainConverter.getDecimalSeparator();
  }

  public void setDecimalSeparator(char decimalSeparator) {
    plainConverter.setDecimalSeparator(decimalSeparator);
  }

  public String getTimePattern() {
    return plainConverter.getTimePattern();
  }

  public void setTimePattern(String timePattern) {
    plainConverter.setTimePattern(timePattern);
  }

  public String getIntegralPattern() {
    return plainConverter.getIntegralPattern();
  }

  public void setIntegralPattern(String integralPattern) {
    plainConverter.setIntegralPattern(integralPattern);
  }

  protected String format(Object o) {
    return plainConverter.convert(o);
  }

}

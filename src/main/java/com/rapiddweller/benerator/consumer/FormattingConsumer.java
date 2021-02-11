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
 *
 * @author Volker Bergmann
 * @since 0.5.1
 */
public abstract class FormattingConsumer extends AbstractConsumer {

  /**
   * The Plain converter.
   */
  protected final ToStringConverter plainConverter = new ToStringConverter();

  /**
   * Gets null string.
   *
   * @return the null string
   */
  public String getNullString() {
    return plainConverter.getNullString();
  }

  /**
   * Sets null string.
   *
   * @param nullString the null string
   */
  public void setNullString(String nullString) {
    plainConverter.setNullString(nullString);
  }

  /**
   * Gets date pattern.
   *
   * @return the date pattern
   */
  public String getDatePattern() {
    return plainConverter.getDatePattern();
  }

  /**
   * Sets date pattern.
   *
   * @param datePattern the date pattern
   */
  public void setDatePattern(String datePattern) {
    plainConverter.setDatePattern(datePattern);
  }

  /**
   * Gets date capitalization.
   *
   * @return the date capitalization
   */
  public Capitalization getDateCapitalization() {
    return plainConverter.getDateCapitalization();
  }

  /**
   * Sets date capitalization.
   *
   * @param dateCapitalization the date capitalization
   */
  public void setDateCapitalization(Capitalization dateCapitalization) {
    plainConverter.setDateCapitalization(dateCapitalization);
  }

  /**
   * Gets date time pattern.
   *
   * @return the date time pattern
   */
  public String getDateTimePattern() {
    return plainConverter.getDateTimePattern();
  }

  /**
   * Sets date time pattern.
   *
   * @param dateTimePattern the date time pattern
   */
  public void setDateTimePattern(String dateTimePattern) {
    plainConverter.setDateTimePattern(dateTimePattern);
  }

  /**
   * Gets timestamp pattern.
   *
   * @return the timestamp pattern
   */
  public String getTimestampPattern() {
    return plainConverter.getTimestampPattern();
  }

  /**
   * Sets timestamp pattern.
   *
   * @param timestampPattern the timestamp pattern
   */
  public void setTimestampPattern(String timestampPattern) {
    plainConverter.setTimestampPattern(timestampPattern);
  }

  /**
   * Gets timestamp capitalization.
   *
   * @return the timestamp capitalization
   */
  public Capitalization getTimestampCapitalization() {
    return plainConverter.getTimestampCapitalization();
  }

  /**
   * Sets timestamp capitalization.
   *
   * @param timestampCapitalization the timestamp capitalization
   */
  public void setTimestampCapitalization(Capitalization timestampCapitalization) {
    plainConverter.setTimestampCapitalization(timestampCapitalization);
  }

  /**
   * Gets decimal pattern.
   *
   * @return the decimal pattern
   */
  public String getDecimalPattern() {
    return plainConverter.getDecimalPattern();
  }

  /**
   * Sets decimal pattern.
   *
   * @param decimalPattern the decimal pattern
   */
  public void setDecimalPattern(String decimalPattern) {
    plainConverter.setDecimalPattern(decimalPattern);
  }

  /**
   * Gets decimal separator.
   *
   * @return the decimal separator
   */
  public char getDecimalSeparator() {
    return plainConverter.getDecimalSeparator();
  }

  /**
   * Sets decimal separator.
   *
   * @param decimalSeparator the decimal separator
   */
  public void setDecimalSeparator(char decimalSeparator) {
    plainConverter.setDecimalSeparator(decimalSeparator);
  }

  /**
   * Gets time pattern.
   *
   * @return the time pattern
   */
  public String getTimePattern() {
    return plainConverter.getTimePattern();
  }

  /**
   * Sets time pattern.
   *
   * @param timePattern the time pattern
   */
  public void setTimePattern(String timePattern) {
    plainConverter.setTimePattern(timePattern);
  }

  /**
   * Gets integral pattern.
   *
   * @return the integral pattern
   */
  public String getIntegralPattern() {
    return plainConverter.getIntegralPattern();
  }

  /**
   * Sets integral pattern.
   *
   * @param integralPattern the integral pattern
   */
  public void setIntegralPattern(String integralPattern) {
    plainConverter.setIntegralPattern(integralPattern);
  }

  /**
   * Format string.
   *
   * @param o the o
   * @return the string
   */
  protected String format(Object o) {
    return plainConverter.convert(o);
  }

}

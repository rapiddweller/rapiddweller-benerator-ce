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

package com.rapiddweller.platform.edi;

import freemarker.template.TemplateMethodModelEx;

import java.util.List;

/**
 * Provides Edifact character escaping in FreeMarker templates.<br/><br/>
 * Created: 30.06.2014 11:14:26
 *
 * @author Volker Bergmann
 * @since 0.9.7
 */
public class EdiEscapeFreeMarkerMethod implements TemplateMethodModelEx {

  private char componentSeparator = ':';
  private char elementSeparator = '+';
  private char escapeChar = '?';
  private char segmentSeparator = '\'';

  /**
   * Gets component separator.
   *
   * @return the component separator
   */
  public char getComponentSeparator() {
    return componentSeparator;
  }

  /**
   * Sets component separator.
   *
   * @param componentSeparator the component separator
   */
  public void setComponentSeparator(char componentSeparator) {
    this.componentSeparator = componentSeparator;
  }

  /**
   * Gets element separator.
   *
   * @return the element separator
   */
  public char getElementSeparator() {
    return elementSeparator;
  }

  /**
   * Sets element separator.
   *
   * @param elementSeparator the element separator
   */
  public void setElementSeparator(char elementSeparator) {
    this.elementSeparator = elementSeparator;
  }

  /**
   * Gets escape char.
   *
   * @return the escape char
   */
  public char getEscapeChar() {
    return escapeChar;
  }

  /**
   * Sets escape char.
   *
   * @param escapeChar the escape char
   */
  public void setEscapeChar(char escapeChar) {
    this.escapeChar = escapeChar;
  }

  /**
   * Gets segment separator.
   *
   * @return the segment separator
   */
  public char getSegmentSeparator() {
    return segmentSeparator;
  }

  /**
   * Sets segment separator.
   *
   * @param segmentSeparator the segment separator
   */
  public void setSegmentSeparator(char segmentSeparator) {
    this.segmentSeparator = segmentSeparator;
  }

  @Override
  public Object exec(List args) {
    StringBuilder builder = new StringBuilder();
    for (int index = 0; index < args.size(); index++) {
      if (index > 0) {
        builder.append(':');
      }
      String s = String.valueOf(args.get(index));
      for (int i = 0; i < s.length(); i++) {
        char c = s.charAt(i);
        if (needsEscaping(c)) {
          builder.append(escapeChar);
        }
        builder.append(c);
      }
    }
    return builder.toString();
  }

  private boolean needsEscaping(char c) {
    return (c == componentSeparator || c == elementSeparator ||
        c == escapeChar || c == segmentSeparator);
  }

}

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

package com.rapiddweller.benerator;

import com.rapiddweller.common.ConfigurationError;

import java.util.Arrays;
import java.util.List;

/**
 * Indicates invalid setup of a Generator.<br/>
 * <br/>
 * Created: 21.12.2006 08:04:49
 */
public class InvalidGeneratorSetupException extends ConfigurationError {

  private static final long serialVersionUID = 7613352958748575041L;

  private final List<PropertyMessage> propertyMessages;

  // constructors ----------------------------------------------------------------------------------------------------

  /**
   * Instantiates a new Invalid generator setup exception.
   *
   * @param propertyName    the property name
   * @param propertyMessage the property message
   */
  public InvalidGeneratorSetupException(String propertyName, String propertyMessage) {
    this(new PropertyMessage(propertyName, propertyMessage));
  }

  /**
   * Instantiates a new Invalid generator setup exception.
   *
   * @param propertyMessages the property messages
   */
  public InvalidGeneratorSetupException(PropertyMessage... propertyMessages) {
    this(null, null, propertyMessages);
  }

  /**
   * Instantiates a new Invalid generator setup exception.
   *
   * @param textMessage the text message
   */
  public InvalidGeneratorSetupException(String textMessage) {
    this(textMessage, (Throwable) null);
  }

  /**
   * Instantiates a new Invalid generator setup exception.
   *
   * @param cause the cause
   */
  public InvalidGeneratorSetupException(Throwable cause) {
    this(null, cause);
  }

  /**
   * Instantiates a new Invalid generator setup exception.
   *
   * @param textMessage the text message
   * @param cause       the cause
   */
  public InvalidGeneratorSetupException(String textMessage, Throwable cause) {
    this(textMessage, cause, new PropertyMessage[0]);
  }

  /**
   * Instantiates a new Invalid generator setup exception.
   *
   * @param textMessage      the text message
   * @param cause            the cause
   * @param propertyMessages the property messages
   */
  public InvalidGeneratorSetupException(String textMessage, Throwable cause, PropertyMessage... propertyMessages) {
    super(formatMessage(textMessage, propertyMessages), cause);
    this.propertyMessages = Arrays.asList(propertyMessages);
  }

  // interface -------------------------------------------------------------------------------------------------------

  /**
   * Get property messages property message [ ].
   *
   * @return the property message [ ]
   */
  public PropertyMessage[] getPropertyMessages() {
    PropertyMessage[] array = new PropertyMessage[propertyMessages.size()];
    return propertyMessages.toArray(array);
  }

  private static String formatMessage(String textMessage, PropertyMessage... propertyMessages) {
    StringBuilder buffer = new StringBuilder();
    if (textMessage != null) {
      buffer.append(textMessage).append(": ");
    }
    for (int i = 0; i < propertyMessages.length; i++) {
      PropertyMessage propertyMessage = propertyMessages[i];
      buffer.append(propertyMessage);
      if (i < propertyMessages.length - 1) {
        buffer.append(", ");
      }
    }
    return buffer.toString();
  }
}

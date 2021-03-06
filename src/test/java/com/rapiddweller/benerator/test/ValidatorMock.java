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

package com.rapiddweller.benerator.test;

import com.rapiddweller.common.Validator;

/**
 * Mock implementation of the {@link Validator} interface.<br/>
 * <br/>
 * Created at 29.12.2008 07:49:35
 *
 * @author Volker Bergmann
 * @since 0.5.7
 */
public class ValidatorMock implements Validator<Integer> {

  /**
   * The constant latestInstance.
   */
  public static ValidatorMock latestInstance;

  /**
   * The Value.
   */
  public int value;

  /**
   * Instantiates a new Validator mock.
   */
  public ValidatorMock() {
    this(1);
  }

  /**
   * Instantiates a new Validator mock.
   *
   * @param value the value
   */
  public ValidatorMock(int value) {
    this.value = value;
    latestInstance = this;
  }

  /**
   * Sets value.
   *
   * @param value the value
   */
  public void setValue(int value) {
    this.value = value;
  }

  @Override
  public boolean valid(Integer candidate) {
    return (candidate != null && value == candidate);
  }

}

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

package com.rapiddweller.benerator.primitive;

import com.rapiddweller.benerator.util.ThreadSafeNonNullGenerator;

import java.util.Random;

/**
 * Generates boolean values with a configurable quota of true values.<br/>
 * <br/>
 * Created: 09.06.2006 20:03:18
 */
public class BooleanGenerator extends ThreadSafeNonNullGenerator<Boolean> {

  /**
   * The quota of true values to create
   */
  private double trueQuota;

  private final Random random;

  // constructors ----------------------------------------------------------------------------------------------------

  /**
   * Initializes the generator to a trueQuota of 50%
   */
  public BooleanGenerator() {
    this(0.5f);
  }

  /**
   * Initializes the generator to a trueQuota
   *
   * @param trueQuota the true quota
   */
  public BooleanGenerator(double trueQuota) {
    this.trueQuota = trueQuota;
    this.random = new Random();
  }

  // config properties -----------------------------------------------------------------------------------------------

  /**
   * Sets the quota of true values to create
   *
   * @return the true quota
   */
  public double getTrueQuota() {
    return trueQuota;
  }

  /**
   * Returns the quota of true values to create
   *
   * @param trueQuota the true quota
   */
  public void setTrueQuota(double trueQuota) {
    this.trueQuota = trueQuota;
  }

  // Generator interface ---------------------------------------------------------------------------------------------

  @Override
  public Class<Boolean> getGeneratedType() {
    return Boolean.class;
  }

  /**
   * generates boolean values with a quota of true values according to the trueQuota property
   */
  @Override
  public Boolean generate() {
    return (random.nextFloat() <= trueQuota);
  }

  // java.lang.Object overrides --------------------------------------------------------------------------------------

  @Override
  public String toString() {
    return getClass().getSimpleName() + "[trueQuota=" + trueQuota + ']';
  }

}

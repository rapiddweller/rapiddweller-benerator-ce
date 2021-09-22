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

package com.rapiddweller.benerator.distribution.sequence;

import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.primitive.number.AbstractNonNullNumberGenerator;

/**
 * Generates 'Double' values for the 'wedge' sequence.<br/><br/>
 * Created: 13.02.2010 13:06:27
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class WedgeDoubleGenerator extends AbstractNonNullNumberGenerator<Double> {

  private Double cursor;
  private double end;

  public WedgeDoubleGenerator() {
    this(Long.MIN_VALUE, Long.MAX_VALUE);
  }

  public WedgeDoubleGenerator(double min, double max) {
    this(min, max, 1);
  }

  public WedgeDoubleGenerator(double min, double max, double granularity) {
    super(Double.class, min, max, granularity);
    this.cursor = min;
  }

  // generator interface ---------------------------------------------------------------------------------------------

  @Override
  public void init(GeneratorContext context) {
    assertNotInitialized();
    cursor = min;
    max = min + (max - min) / granularity * granularity;
    double steps = (max - min) / granularity + 1;
    end = min + Math.floor(steps / 2) * granularity;
    super.init(context);
  }

  @Override
  public Double generate() {
    assertInitialized();
    if (cursor == null) {
      return null;
    }
    double result = cursor;
    if (Math.abs(cursor - end) < granularity / 2) {
      cursor = null;
    } else {
      cursor = max - cursor + min;
      if (cursor < end) {
        cursor += granularity;
      }
    }
    return result;
  }

  @Override
  public synchronized void reset() {
    super.reset();
    this.cursor = min;
  }

  @Override
  public synchronized void close() {
    super.close();
    this.cursor = null;
  }

}

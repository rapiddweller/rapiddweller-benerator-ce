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

package com.rapiddweller.domain.family;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.wrapper.NonNullGeneratorProxy;
import com.rapiddweller.benerator.wrapper.WrapperFactory;
import com.rapiddweller.script.WeightedSample;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates Weighted {@link String} objects.<br/>
 * This can be used for generating status / case as strings with weight distribution<br/>
 *
 * @see String
 */
public class WeightedStringGenerator extends NonNullGeneratorProxy<String> {

  private double[] weight;
  private String[] value;

  // constructors ----------------------------------------------------------------------------------------------------

  /**
   * Instantiates a new Weighted String generator.
   */
  public WeightedStringGenerator() {
    this(new String[] {"yes", "no"}, new double[] {0.5,0.5});
  }

  /**
   * Instantiates a new Weighted String generator.<br/>
   * @param value as the string array.
   * @param weight as the weight of each String.
   */
  public WeightedStringGenerator(String[] value, double[] weight) {
    super(String.class);
    this.value = value;
    this.weight = weight;
  }

  // Generator interface implementation ------------------------------------------------------------------------------

  @Override
  public Class<String> getGeneratedType() {
    return String.class;
  }

  @Override
  public boolean isParallelizable() {
    return true;
  }

  @Override
  public boolean isThreadSafe() {
    return true;
  }

  @Override
  public synchronized void init(GeneratorContext context) {
    assertNotInitialized();
    List<WeightedSample<String>> weightedSamples = mergeTwoArraysInWeightedSample(value, weight);

    Generator<String> source = context.getGeneratorFactory()
        .createWeightedSampleGenerator(weightedSamples, String.class);
    setSource(WrapperFactory.asNonNullGenerator(source));
    super.init(context);
  }

  // properties ------------------------------------------------------------------------------------------------------

  public double[] getWeight() {
    return weight;
  }

  public void setWeight(double[] weight) {
    this.weight = weight;
  }

  public String[] getValue() {
    return value;
  }

  public void setValue(String[] value) {
    this.value = value;
  }

  // private helper ----------------------------------------------------------------------------------------------------
  public static List<WeightedSample<String>> mergeTwoArraysInWeightedSample(String[] value, double[] weight) {
    if (value == null || weight == null || value.length != weight.length) {
      throw new IllegalArgumentException("Arrays must be non-null and of the same size.");
    }
    List<WeightedSample<String>> mergedList = new ArrayList<>();
    for (int i = 0; i < value.length; i++) {
      mergedList.add(new WeightedSample<>(value[i], weight[i]));
    }
    return mergedList;
  }
}

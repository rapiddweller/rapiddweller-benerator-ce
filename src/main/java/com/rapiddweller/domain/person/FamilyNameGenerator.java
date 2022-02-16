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

package com.rapiddweller.domain.person;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.NonNullGenerator;
import com.rapiddweller.benerator.csv.WeightedDatasetCSVGenerator;
import com.rapiddweller.benerator.util.GeneratorUtil;
import com.rapiddweller.benerator.util.SharedGenerator;
import com.rapiddweller.common.Encodings;
import com.rapiddweller.domain.address.Country;

import java.util.HashMap;
import java.util.Map;

/**
 * Generates family names.<br/><br/>
 * Created: 09.06.2006 22:03:56
 * @author Volker Bergmann
 * @since 0.1
 */
public class FamilyNameGenerator extends WeightedDatasetCSVGenerator<String> implements NonNullGenerator<String> {

  // default instance management -------------------------------------------------------------------------------------

  private static final Map<String, Generator<String>> defaultInstances = new HashMap<>();

  public FamilyNameGenerator() {
    this(Country.getDefault().getIsoCode());
  }

  // Constructors ----------------------------------------------------------------------------------------------------

  public FamilyNameGenerator(String datasetName) {
    this(datasetName,
        "/com/rapiddweller/dataset/region",
        "/com/rapiddweller/domain/person/familyName_{0}.csv");
  }

  public FamilyNameGenerator(String datasetName, String nesting,
                             String fileNamePattern) {
    super(String.class, fileNamePattern, datasetName, nesting, true,
        Encodings.UTF_8);
    logger.debug("Instantiated FamilyNameGenerator for dataset '{}'",
        datasetName);
  }

  public static Generator<String> sharedInstance(String datasetName) {
    return defaultInstances.computeIfAbsent(
        datasetName, k -> new SharedGenerator<>(new FamilyNameGenerator(datasetName)));
  }

  @Override
  public boolean isThreadSafe() {
    return true;
  }

  @Override
  public double getWeight() {
    Country country = Country.getInstance(datasetName);
    return (country != null ? country.getPopulation() : super.getWeight());
  }

  // NonNullGenerator interface implementation -----------------------------------------------------------------------

  @Override
  public String generate() {
    return GeneratorUtil.generateNonNull(this);
  }

}

/*
 * (c) Copyright 2006-2021 by rapiddweller GmbH & Volker Bergmann. All rights reserved.
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

import com.rapiddweller.benerator.NonNullGenerator;
import com.rapiddweller.benerator.WeightedGenerator;
import com.rapiddweller.benerator.dataset.AbstractDatasetGenerator;
import com.rapiddweller.benerator.dataset.AtomicDatasetGenerator;
import com.rapiddweller.benerator.dataset.Dataset;
import com.rapiddweller.benerator.dataset.WeightedDatasetGenerator;
import com.rapiddweller.benerator.sample.ConstantGenerator;
import com.rapiddweller.benerator.util.GeneratorUtil;
import com.rapiddweller.benerator.wrapper.WeighingGeneratorWrapper;

/**
 * Generates {@link Country} objects in random order.<br/><br/>
 * Created: 11.06.2006 08:15:51
 * @author Volker Bergmann
 * @since 0.1
 */
public class CountryGenerator extends AbstractDatasetGenerator<Country> implements NonNullGenerator<Country> {

  private static final String REGION = "/com/rapiddweller/dataset/region";

  // constructors ----------------------------------------------------------------------------------------------------

  public CountryGenerator() {
    this("world");
  }

  public CountryGenerator(String datasetName) {
    super(Country.class, REGION, datasetName, true);
  }

  // interface -------------------------------------------------------------------------------------------------------

  @Override
  public boolean isThreadSafe() {
    return true;
  }

  @Override
  public boolean isParallelizable() {
    return false;
  }

  @Override
  public Country generate() {
    return GeneratorUtil.generateNonNull(this);
  }

  // non-public helpers ----------------------------------------------------------------------------------------------

  @Override
  protected boolean isAtomic(Dataset dataset) {
    Country country = Country.getInstance(dataset.getName(), false);
    return (country != null);
  }

  @Override
  protected WeightedGenerator<Country> createGeneratorForAtomicDataset(Dataset dataset) {
    WeightedDatasetGenerator<Country> result;
    Country country = Country.getInstance(dataset.getName(), false);
    result = createGeneratorForCountry(country);
    supportedDatasets.add(dataset.getName());
    return result;
  }

  protected WeightedDatasetGenerator<Country> createGeneratorForCountry(Country country) {
    ConstantGenerator<Country> coreGenerator = new ConstantGenerator<>(country);
    WeightedGenerator<Country> generator = new WeighingGeneratorWrapper<>(coreGenerator, country.getPopulation());
    totalWeight += generator.getWeight();
    return new AtomicDatasetGenerator<>(generator, nesting, country.getIsoCode());
  }

  // java.lang.Object overrides --------------------------------------------------------------------------------------

  @Override
  public String toString() {
    return getClass().getSimpleName() + "[" + getDataset() + "]";
  }

}

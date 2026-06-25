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

package com.rapiddweller.domain.address;

import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.NonNullGenerator;
import com.rapiddweller.benerator.WeightedGenerator;
import com.rapiddweller.benerator.dataset.AbstractDatasetGenerator;
import com.rapiddweller.benerator.dataset.Dataset;
import com.rapiddweller.benerator.distribution.FeatureWeight;
import com.rapiddweller.benerator.distribution.IndividualWeight;
import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.benerator.sample.IndividualWeightSampleGenerator;
import com.rapiddweller.benerator.util.GeneratorUtil;

/**
 * Generates {@link City} objects.<br/><br/>
 * Created: 14.10.2007 21:24:25
 * @author Volker Bergmann
 */
public class CityGenerator extends AbstractDatasetGenerator<City>
    implements NonNullGenerator<City> {

  private static final String REGION = "/com/rapiddweller/dataset/region";

  /** Optional filter: only generate cities of this state (matched against the state id or name). */
  private String stateFilter;
  /** Optional filter: only generate cities with this name. */
  private String cityFilter;

  public CityGenerator() {
    this(null);
  }

  public CityGenerator(String dataset) {
    super(City.class, REGION, dataset, true);
  }

  public void setStateFilter(String state) {
    this.stateFilter = trimToNull(state);
  }

  public void setCityFilter(String city) {
    this.cityFilter = trimToNull(city);
  }

  @Override
  public boolean isThreadSafe() {
    return true;
  }

  @Override
  public boolean isParallelizable() {
    return true;
  }

  @Override
  public synchronized void init(GeneratorContext context) {
    if (getDataset() == null)
      setDataset(context.getDefaultDataset());
    super.init(context);
  }

  @Override
  protected boolean isAtomic(Dataset dataset) {
    Country country = Country.getInstance(dataset.getName(), false);
    return (country != null);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  protected WeightedGenerator<City> createGeneratorForAtomicDataset(Dataset dataset) {
    IndividualWeightSampleGenerator<City> generator =
        new IndividualWeightSampleGenerator<>(City.class,
            (IndividualWeight) new FeatureWeight("population"));
    Country country = Country.getInstance(dataset.getName());
    country.checkCities();
    for (State state : country.getStates()) {
      if (!stateMatches(state)) {
        continue;
      }
      for (City city : state.getCities()) {
        if (cityMatches(city)) {
          generator.addValue(city);
        }
      }
    }
    if (hasFilter() && generator.getVariety() == 0) {
      throw BeneratorExceptionFactory.getInstance().configurationError(
          "No cities match the address filter in dataset '" + dataset.getName() + "'"
              + (stateFilter != null ? ", state='" + stateFilter + "'" : "")
              + (cityFilter != null ? ", city='" + cityFilter + "'" : ""));
    }
    return (generator.getVariety() > 0 ? generator : null);
  }

  private boolean hasFilter() {
    return stateFilter != null || cityFilter != null;
  }

  private boolean stateMatches(State state) {
    return stateFilter == null
        || stateFilter.equalsIgnoreCase(state.getId())
        || stateFilter.equalsIgnoreCase(state.getName());
  }

  private boolean cityMatches(City city) {
    return cityFilter == null || cityFilter.equalsIgnoreCase(city.getName());
  }

  private static String trimToNull(String s) {
    if (s == null) {
      return null;
    }
    String t = s.trim();
    return t.isEmpty() ? null : t;
  }

  @Override
  public City generate() {
    return GeneratorUtil.generateNonNull(this);
  }

}

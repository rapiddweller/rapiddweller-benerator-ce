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

package com.rapiddweller.domain.organization;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.NonNullGenerator;
import com.rapiddweller.benerator.WeightedGenerator;
import com.rapiddweller.benerator.csv.WeightedDatasetCSVGenerator;
import com.rapiddweller.benerator.dataset.AbstractDatasetGenerator;
import com.rapiddweller.benerator.dataset.Dataset;
import com.rapiddweller.benerator.dataset.DatasetUtil;
import com.rapiddweller.benerator.primitive.RegexStringGenerator;
import com.rapiddweller.benerator.primitive.TokenCombiner;
import com.rapiddweller.benerator.sample.ConstantGenerator;
import com.rapiddweller.benerator.sample.SequencedCSVSampleGenerator;
import com.rapiddweller.benerator.util.ThreadSafeNonNullGenerator;
import com.rapiddweller.benerator.wrapper.AlternativeGenerator;
import com.rapiddweller.benerator.wrapper.MessageGenerator;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.benerator.wrapper.WrapperFactory;
import com.rapiddweller.common.Assert;
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.common.Encodings;
import com.rapiddweller.common.bean.PropertyAccessConverter;
import com.rapiddweller.domain.address.CityGenerator;
import com.rapiddweller.domain.address.Country;
import com.rapiddweller.domain.person.FamilyNameGenerator;
import com.rapiddweller.domain.person.Gender;
import com.rapiddweller.domain.person.GivenNameGenerator;
import com.rapiddweller.format.text.NameNormalizer;

import java.util.HashMap;
import java.util.Map;

import static com.rapiddweller.benerator.util.GeneratorUtil.generateNonNull;
import static com.rapiddweller.benerator.util.GeneratorUtil.generateNullable;

/**
 * Generates company names.<br/><br/>
 * Created: 14.03.2008 08:26:44
 * @author Volker Bergmann
 * @since 0.5.0
 */
public class CompanyNameGenerator extends AbstractDatasetGenerator<CompanyName>
    implements NonNullGenerator<CompanyName> {

  private static final String ORG = "/com/rapiddweller/domain/organization/";

  protected static final Map<String, Generator<String>> locationGenerators =
      new HashMap<>();

  protected boolean sector;
  protected boolean location;
  protected boolean legalForm;


  // Constructors ----------------------------------------------------------------------------------------------------

  public CompanyNameGenerator() {
    this(true, true, true);
  }

  public CompanyNameGenerator(boolean sector, boolean location,
                              boolean legalForm) {
    this(sector, location, legalForm, Country.getDefault().getIsoCode());
  }

  public CompanyNameGenerator(String dataset) {
    this(true, true, true, dataset);
  }

  public CompanyNameGenerator(boolean sector, boolean location,
                              boolean legalForm, String datasetName) {
    super(CompanyName.class, DatasetUtil.REGION_NESTING, datasetName, true);
    logger.debug("Creating instance of {} for dataset {}", getClass(),
        datasetName);
    this.sector = sector;
    this.location = location;
    this.legalForm = legalForm;
    this.datasetName = datasetName;
    setDataset(datasetName);
  }


  // properties -----------------------------------------------------------------------------------------------------------

  public boolean isSector() {
    return sector;
  }

  public void setSector(boolean sector) {
    this.sector = sector;
  }

  public boolean isLocation() {
    return location;
  }

  public void setLocation(boolean location) {
    this.location = location;
  }

  public boolean isLegalForm() {
    return legalForm;
  }

  public void setLegalForm(boolean legalForm) {
    this.legalForm = legalForm;
  }


  // interface -------------------------------------------------------------------------------------------------------


  @Override
  public boolean isThreadSafe() {
    return true;
  }

  @Override
  public boolean isParallelizable() {
    return true;
  }

  @Override
  protected boolean isAtomic(Dataset dataset) {
    Country country = Country.getInstance(dataset.getName(), false);
    return (country != null);
  }

  @Override
  protected WeightedGenerator<CompanyName> createGeneratorForAtomicDataset(
      Dataset dataset) {
    String isoCode = dataset.getName();
    Country country = Country.getInstance(isoCode, false);
    if (country == null) {
      throw new ConfigurationError("Unknown country code: " + isoCode);
    }
    return new CountryCompanyNameGenerator(country);
  }

  @Override
  public CompanyName generate() {
    ProductWrapper<CompanyName> wrapper = generate(getResultWrapper());
    return (wrapper != null ? wrapper.unwrap() : null);
  }


  // helper class ----------------------------------------------------------------------------------------------------

  class CountryCompanyNameGenerator
      extends ThreadSafeNonNullGenerator<CompanyName>
      implements WeightedGenerator<CompanyName> {

    private final Country country;
    private AlternativeGenerator<String> shortNameGenerator;
    private SectorGenerator sectorGenerator;
    private WeightedDatasetCSVGenerator<String> legalFormGenerator;
    private Generator<String> locationGenerator;

    public CountryCompanyNameGenerator(Country country) {
      Assert.notNull(country, "country");
      this.country = country;
    }

    @Override
    public Class<CompanyName> getGeneratedType() {
      return CompanyName.class;
    }

    @Override
    public synchronized void init(GeneratorContext context) {
      try {
        super.init(context);
        initWithDataset(country.getIsoCode(), context);
      } catch (Exception e) {
        String fallbackDataset = DatasetUtil.fallbackRegionName();
        logger.warn("Error initializing location generator for dataset {}, falling back to {}",
            datasetName, fallbackDataset);
        initWithDataset(fallbackDataset, context);
      }
    }

    public void initWithDataset(String datasetToUse,
                                GeneratorContext context) {
      createAndInitLocationGenerator(datasetToUse);
      initLegalFormGenerator(datasetToUse);
      initSectorGenerator(datasetToUse);
      createAndInitShortNameGenerator(datasetToUse, context);
      super.init(context);
    }

    @Override
    public CompanyName generate() {
      CompanyName name = new CompanyName();
      name.setShortName(generateNonNull(shortNameGenerator));

      if (sectorGenerator != null) {
        String sector = generateNullable(sectorGenerator);
        if (sector != null) {
          name.setSector(sector);
        }
      }
      if (locationGenerator != null) {
        String location = generateNullable(locationGenerator);
        if (location != null) {
          name.setLocation(location);
        }
      }
      if (legalFormGenerator != null) {
        name.setLegalForm(generateNullable(legalFormGenerator));
      }
      name.setDatasetName(datasetName);
      return name;
    }

    @Override
    public double getWeight() {
      return country.getPopulation();
    }

    @Override
    public String toString() {
      return getClass().getSimpleName() + '[' + datasetName + ']';
    }

    // private helpers -------------------------------------------------------------------------------------------------

    private void createAndInitShortNameGenerator(String datasetToUse,
                                                 GeneratorContext context) {
      shortNameGenerator = new AlternativeGenerator<>(String.class);
      shortNameGenerator.addSource(createInitialsNameGenerator());
      addSourceIfNotNull(createPersonNameGenerator(datasetToUse),
          shortNameGenerator);
      addSourceIfNotNull(createArtificialNameGenerator(),
          shortNameGenerator);
      addSourceIfNotNull(createTechNameGenerator(), shortNameGenerator);
      shortNameGenerator.init(context);
    }

    private void addSourceIfNotNull(Generator<String> source,
                                    AlternativeGenerator<String> master) {
      if (source != null) {
        master.addSource(source);
      }
    }

    private RegexStringGenerator createInitialsNameGenerator() {
      return new RegexStringGenerator("[A-Z]{3}");
    }

    private MessageGenerator createTechNameGenerator() {
      try {
        return new MessageGenerator("{0}{1}",
            new SequencedCSVSampleGenerator<String>(
                ORG + "tech1.csv"),
            new SequencedCSVSampleGenerator<String>(
                ORG + "tech2.csv")
        );
      } catch (Exception e) {
        logger.info("Cannot create technical company name generator: {}", e.getMessage());
        return null;
      }
    }

    private TokenCombiner createArtificialNameGenerator() {
      try {
        return new TokenCombiner(ORG + "artificialName.csv", false, '-',
            Encodings.UTF_8, false);
      } catch (Exception e) {
        logger.info("Cannot create artificial company name generator: {}", e.getMessage());
        return null;
      }
    }

    private MessageGenerator createPersonNameGenerator(
        String datasetToUse) {
      try {
        return new MessageGenerator("{0} {1}",
            GivenNameGenerator
                .sharedInstance(datasetToUse, Gender.MALE),
            FamilyNameGenerator.sharedInstance(datasetToUse)
        );
      } catch (Exception e) {
        logger.info("Cannot create person-based company name generator: {}", e.getMessage());
        return null;
      }
    }

    private void initSectorGenerator(String datasetName) {
      if (sector) {
        try {
          Country country = Country.getInstance(datasetName);
          sectorGenerator = new SectorGenerator(
              country.getDefaultLanguageLocale());
          sectorGenerator.init(context);
        } catch (Exception e) {
          if ("US".equals(datasetName)) {
            throw new ConfigurationError(
                "Failed to initialize SectorGenerator with US dataset",
                e);
          }
          logger.info("Cannot create sector generator: {}. Falling back to US", e.getMessage());
          initSectorGenerator("US");
        }
      }
    }

    private void initLegalFormGenerator(String datasetName) {
      if (legalForm) {

        try {
          legalFormGenerator = new LegalFormGenerator(datasetName);
          legalFormGenerator.init(context);
        } catch (Exception e) {
          logger.error("Cannot create legal form generator: {}. Falling back to US. ", e.getMessage());
          initLegalFormGenerator("US");
        }
      }
    }

    private void createAndInitLocationGenerator(String datasetName) {
      locationGenerator = locationGenerators.get(datasetName);
      if (locationGenerator == null) {
        double nullQuota = 0.8;
        Country country = Country.getInstance(datasetName);
        Generator<String> locationBaseGen;
        if (location && country != null) {
          try {
            Generator<String> cityGen =
                WrapperFactory.applyConverter(
                    new CityGenerator(country.getIsoCode()),
                    new PropertyAccessConverter("name"),
                    new NameNormalizer());
            if (DatasetUtil.getDataset(DatasetUtil.REGION_NESTING,
                datasetName).isAtomic()) {
              locationBaseGen =
                  new AlternativeGenerator<>(String.class,
                      new ConstantGenerator<>(
                          country.getLocalName()),
                      cityGen);
            } else {
              locationBaseGen = cityGen;
            }
          } catch (Exception e) {
            logger.info("Cannot create location generator: {}", e.getMessage());
            locationBaseGen = new ConstantGenerator<>(null);
          }
        } else {
          locationBaseGen = new ConstantGenerator<>(null);
        }
        locationGenerator =
            WrapperFactory.injectNulls(locationBaseGen, nullQuota);
        locationGenerator.init(context);
        locationGenerators.put(datasetName, locationGenerator);
      }
    }

  }

}

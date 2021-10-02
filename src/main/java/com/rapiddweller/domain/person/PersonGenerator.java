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
import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.NonNullGenerator;
import com.rapiddweller.benerator.dataset.DatasetBasedGenerator;
import com.rapiddweller.benerator.dataset.DatasetUtil;
import com.rapiddweller.benerator.primitive.BooleanGenerator;
import com.rapiddweller.benerator.wrapper.CompositeGenerator;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.common.Converter;
import com.rapiddweller.domain.address.Country;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.rapiddweller.benerator.util.GeneratorUtil.generateNonNull;
import static com.rapiddweller.benerator.util.GeneratorUtil.generateNullable;

/**
 * Generates {@link Person} beans.<br/><br/>
 * Created: 09.06.2006 21:45:13
 * @author Volker Bergmann
 * @since 0.1
 */
public class PersonGenerator extends CompositeGenerator<Person>
    implements DatasetBasedGenerator<Person>, NonNullGenerator<Person> {

  private static final String REGION_NESTING = "com/rapiddweller/dataset/region";

  private String datasetName;
  private Locale locale;

  private final GenderGenerator genderGen;
  private GivenNameGenerator maleGivenNameGen;
  private GivenNameGenerator femaleGivenNameGen;
  private BooleanGenerator secondNameTest;
  private FamilyNameGenerator familyNameGen;
  private final Map<String, Converter<String, String>> femaleFamilyNameConverters;
  private AcademicTitleGenerator acadTitleGen;
  private NobilityTitleGenerator maleNobilityTitleGen;
  private NobilityTitleGenerator femaleNobilityTitleGen;
  private SalutationProvider salutationProvider;

  private final BirthDateGenerator birthDateGenerator;
  private EMailAddressBuilder emailGenerator;

  // constructors ----------------------------------------------------------------------------------------------------

  public PersonGenerator() {
    this(Country.getDefault().getIsoCode(), Locale.getDefault());
  }

  public PersonGenerator(String datasetName) {
    this(datasetName, DatasetUtil.defaultLanguageForRegion(datasetName));
  }

  public PersonGenerator(String datasetName, Locale locale) {
    super(Person.class);
    logger.debug("Instantiating PersonGenerator with dataset '{}' and locale '{}'", datasetName, locale);
    this.datasetName = datasetName;
    this.locale = locale;
    this.genderGen = registerComponent(new GenderGenerator(0.5));
    this.birthDateGenerator = registerComponent(new BirthDateGenerator(15, 105));
    this.femaleFamilyNameConverters = new HashMap<>();
  }

  // properties ------------------------------------------------------------------------------------------------------

  public void setMinAgeYears(int minAgeYears) {
    birthDateGenerator.setMinAgeYears(minAgeYears);
  }

  public void setMaxAgeYears(int maxAgeYears) {
    birthDateGenerator.setMaxAgeYears(maxAgeYears);
  }

  public double getFemaleQuota() {
    return genderGen.getFemaleQuota();
  }

  public void setFemaleQuota(double femaleQuota) {
    this.genderGen.setFemaleQuota(femaleQuota);
  }

  public double getNobleQuota() {
    return maleNobilityTitleGen.getNobleQuota();
  }

  public void setNobleQuota(double nobleQuota) {
    maleNobilityTitleGen.setNobleQuota(nobleQuota);
    femaleNobilityTitleGen.setNobleQuota(nobleQuota);
  }

  public Locale getLocale() {
    return acadTitleGen.getLocale();
  }

  public void setLocale(Locale locale) {
    this.locale = locale;
  }

  @Override
  public String getDataset() {
    return datasetName;
  }

  // DatasetBasedGenerator interface implementation ------------------------------------------------------------------

  public void setDataset(String datasetName) {
    this.datasetName = datasetName;
  }

  @Override
  public String getNesting() {
    return REGION_NESTING;
  }

  @Override
  public Person generateForDataset(String datasetToUse) {
    assertInitialized();
    Person person = new Person(acadTitleGen.getLocale());
    person.setGender(generateNonNull(genderGen));
    GivenNameGenerator givenNameGenerator
        = (Gender.MALE.equals(person.getGender()) ? maleGivenNameGen :
        femaleGivenNameGen);
    String givenName = givenNameGenerator.generateForDataset(datasetToUse);
    person.setGivenName(givenName);
    Boolean evaluation = generateNullable(secondNameTest);
    if (evaluation != null && evaluation) {
      do {
        person.setSecondGivenName(
            givenNameGenerator.generateForDataset(datasetToUse));
      } while (person.getGivenName().equals(person.getSecondGivenName()));
    }
    String familyName = familyNameGen.generateForDataset(datasetToUse);
    if (Gender.FEMALE.equals(person.getGender())) {
      familyName = getFemaleFamilyNameConverter(datasetToUse)
          .convert(familyName);
    }
    person.setFamilyName(familyName);
    person.setSalutation(salutationProvider.salutation(person.getGender()));
    person.setAcademicTitle(generateNullable(acadTitleGen));
    NobilityTitleGenerator nobTitleGenerator
        =
        (Gender.MALE.equals(person.getGender()) ? maleNobilityTitleGen :
            femaleNobilityTitleGen);
    person.setNobilityTitle(generateNullable(nobTitleGenerator));
    Date birthdate = generateNullable(birthDateGenerator);
    person.setBirthDate(birthdate);
    person.setAge(this.getAge(birthdate));
    person.setEmail(emailGenerator.generate(givenName, familyName));
    return person;
  }

  // Generator interface ---------------------------------------------------------------------------------------------

  @Override
  public synchronized void init(GeneratorContext context) {
    secondNameTest = registerAndInitComponent(new BooleanGenerator(0.2));
    genderGen.init(context);
    birthDateGenerator.init(context);
    acadTitleGen =
        registerAndInitComponent(new AcademicTitleGenerator(locale));
    acadTitleGen.setLocale(locale);
    maleNobilityTitleGen = registerAndInitComponent(
        new NobilityTitleGenerator(Gender.MALE, locale));
    femaleNobilityTitleGen = registerAndInitComponent(
        new NobilityTitleGenerator(Gender.FEMALE, locale));
    salutationProvider = new SalutationProvider(locale);

    try {
      initMembersWithDataset(context);
    } catch (RuntimeException e) {
      Country fallBackCountry = Country.getFallback();
      if (!fallBackCountry.getIsoCode().equals(datasetName)) {
        logger.error("Error initializing " + getClass().getSimpleName(), e);
        logger.error("Cannot generate persons for {}, falling back to {}", datasetName, fallBackCountry);
        this.datasetName = fallBackCountry.getIsoCode();
        initMembersWithDataset(context);
      } else {
        throw e;
      }
    }
    super.init(context);
  }

  protected <T extends Generator<U>, U> T registerAndInitComponent(T generator) {
    registerComponent(generator);
    generator.init(context);
    return generator;
  }

  @Override
  public ProductWrapper<Person> generate(ProductWrapper<Person> wrapper) {
    String usedDataset = randomDataset();
    Person person = generateForDataset(usedDataset);
    return wrapper.wrap(person).setTag(REGION_NESTING, usedDataset);
  }

  @Override
  public Person generate() {
    return generateForDataset(randomDataset());
  }

  // private helpers -------------------------------------------------------------------------------------------------

  private String randomDataset() {
    return maleGivenNameGen.generate(new ProductWrapper<>()).getTag(DatasetUtil.REGION_NESTING);
  }

  private Converter<String, String> getFemaleFamilyNameConverter(String usedDataset) {
    return femaleFamilyNameConverters.computeIfAbsent(usedDataset, k -> new FemaleFamilyNameConverter(datasetName));
  }

  private void initMembersWithDataset(GeneratorContext context) {
    maleGivenNameGen = registerAndInitComponent(new GivenNameGenerator(datasetName, Gender.MALE));
    femaleGivenNameGen = registerAndInitComponent(new GivenNameGenerator(datasetName, Gender.FEMALE));
    familyNameGen = registerAndInitComponent(new FamilyNameGenerator(datasetName));
    emailGenerator = new EMailAddressBuilder(datasetName);
    emailGenerator.init(context);
  }

  private Integer getAge(Date birthdate) {
    Calendar today = Calendar.getInstance();
    Calendar birthDate = Calendar.getInstance();

    int age = 0;

    birthDate.setTime(birthdate);
    if (birthDate.after(today)) {
      return age;
    }

    age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);

    // If birth date is greater than todays date (after 2 days adjustment of leap year) then decrement age one year
    if ((birthDate.get(Calendar.DAY_OF_YEAR) - today.get(Calendar.DAY_OF_YEAR) > 3) ||
        (birthDate.get(Calendar.MONTH) > today.get(Calendar.MONTH))) {
      age--;

      // If birth date and todays date are of same month and birth day of month is greater than todays day of month then decrement age
    } else if ((birthDate.get(Calendar.MONTH) == today.get(Calendar.MONTH)) &&
        (birthDate.get(Calendar.DAY_OF_MONTH) > today.get(Calendar.DAY_OF_MONTH))) {
      age--;
    }
    return age;
  }

  // java.lang.Object overrides --------------------------------------------------------------------------------------

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }

}

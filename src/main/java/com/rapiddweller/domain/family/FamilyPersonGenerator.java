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
import com.rapiddweller.benerator.NonNullGenerator;
import com.rapiddweller.benerator.dataset.DatasetBasedGenerator;
import com.rapiddweller.benerator.dataset.DatasetUtil;
import com.rapiddweller.benerator.primitive.BooleanGenerator;
import com.rapiddweller.benerator.primitive.IncrementGenerator;
import com.rapiddweller.benerator.wrapper.CompositeGenerator;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.common.Converter;
import com.rapiddweller.domain.address.Country;
import com.rapiddweller.domain.person.*;

import java.util.*;

import static com.rapiddweller.benerator.util.GeneratorUtil.generateNonNull;
import static com.rapiddweller.benerator.util.GeneratorUtil.generateNullable;

/**
 * Generates {@link FamilyPerson} beans.<br/>
 * Add method generateFromEntityAndRelation take argument of {@link FamilyPerson} and {@link RelationConstraints} to generate relative entity.<br/>
 */
public class FamilyPersonGenerator extends CompositeGenerator<FamilyPerson>
        implements DatasetBasedGenerator<FamilyPerson>, NonNullGenerator<FamilyPerson>, RelationGenerator<FamilyPerson> {

    private static final String REGION_NESTING = "com/rapiddweller/dataset/region";

    private Locale locale;
    private String datasetName;
    private final GenderGenerator genderGenerator;
    private GivenNameGenerator maleGivenNameGenerator;
    private GivenNameGenerator femaleGivenNameGenerator;
    private BooleanGenerator secondNameTest;
    private FamilyNameGenerator familyNameGenerator;
    private final Map<String, Converter<String, String>> femaleFamilyNameConverters;
    private AcademicTitleGenerator acadTitleGenerator;
    private NobilityTitleGenerator maleNobilityTitleGenerator;
    private NobilityTitleGenerator nobilityTitleGenerator;
    private SalutationProvider salutationProvider;
    private BirthDateGenerator birthDateGenerator;
    private BirthDateGenerator firstPersonBirthDateGenerator;
    private EMailAddressBuilder emailGenerator;
    private final IncrementGenerator personIDGenerator;

    // constructors ----------------------------------------------------------------------------------------------------

    public FamilyPersonGenerator() {
        this(Country.getDefault().getIsoCode(), Locale.getDefault());
    }

    public FamilyPersonGenerator(String datasetName) {
        this(datasetName, DatasetUtil.defaultLanguageForRegion(datasetName));
    }

    public FamilyPersonGenerator(String datasetName, Locale locale) {
        super(FamilyPerson.class);
        logger.debug("Instantiating PersonGenerator with dataset '{}' and locale '{}'", datasetName, locale);
        this.datasetName = datasetName;
        this.locale = locale;
        this.genderGenerator = registerComponent(new GenderGenerator(0.5));
        this.birthDateGenerator = registerComponent(new BirthDateGenerator(1, 105));
        this.firstPersonBirthDateGenerator = registerComponent(new BirthDateGenerator(21, 60));
        this.femaleFamilyNameConverters = new HashMap<>();
        this.personIDGenerator = registerComponent(new IncrementGenerator());
    }

    // properties ------------------------------------------------------------------------------------------------------

    public void setMinAgeYears(int minAgeYears) {
        firstPersonBirthDateGenerator.setMinAgeYears(minAgeYears);
    }

    public void setMaxAgeYears(int maxAgeYears) {
        firstPersonBirthDateGenerator.setMaxAgeYears(maxAgeYears);
    }

    public double getFemaleQuota() {
        return genderGenerator.getFemaleQuota();
    }

    public void setFemaleQuota(double femaleQuota) {
        this.genderGenerator.setFemaleQuota(femaleQuota);
    }

    public double getNobleQuota() {
        return maleNobilityTitleGenerator.getNobleQuota();
    }

    public void setNobleQuota(double nobleQuota) {
        maleNobilityTitleGenerator.setNobleQuota(nobleQuota);
        nobilityTitleGenerator.setNobleQuota(nobleQuota);
    }

    @Override
    public String getDataset() {
        return datasetName;
    }

    public void setDataset(String datasetName) {
        this.datasetName = datasetName;
    }

    @Override
    public String getNesting() {
        return REGION_NESTING;
    }

    public Locale getLocale() {
        return acadTitleGenerator.getLocale();
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    // DatasetBasedGenerator interface implementation ------------------------------------------------------------------



    @Override
    public FamilyPerson generateForDataset(String datasetToUse) {
        assertInitialized();
        FamilyPerson person = new FamilyPerson(acadTitleGenerator.getLocale());
        person.setGender(generateNonNull(genderGenerator));
        GivenNameGenerator givenNameGenerator
                = (Gender.MALE.equals(person.getGender()) ? this.maleGivenNameGenerator :
                femaleGivenNameGenerator);
        String givenName = givenNameGenerator.generateForDataset(datasetToUse);
        person.setGivenName(givenName);
        Boolean evaluation = generateNullable(secondNameTest);
        if (evaluation != null && evaluation) {
            do {
                person.setSecondGivenName(
                        givenNameGenerator.generateForDataset(datasetToUse));
            } while (person.getGivenName().equals(person.getSecondGivenName()));
        }
        String familyName = familyNameGenerator.generateForDataset(datasetToUse);
        if (Gender.FEMALE.equals(person.getGender())) {
            familyName = getFemaleFamilyNameConverter(datasetToUse)
                    .convert(familyName);
        }
        person.setFamilyName(familyName);
        person.setSalutation(salutationProvider.salutation(person.getGender()));
        person.setAcademicTitle(generateNullable(acadTitleGenerator));
        NobilityTitleGenerator nobTitleGenerator
                =
                (Gender.MALE.equals(person.getGender()) ? maleNobilityTitleGenerator :
                        nobilityTitleGenerator);
        person.setNobilityTitle(generateNullable(nobTitleGenerator));
        Date birthdate = generateNullable(firstPersonBirthDateGenerator);
        person.setBirthDate(birthdate);
        person.setAge(this.getAge(birthdate));
        person.setEmail(emailGenerator.generate(givenName, familyName));
        person.setPersonID(generateNonNull(personIDGenerator));
        return person;
    }

    // Generator interface ---------------------------------------------------------------------------------------------

    @Override
    public synchronized void init(GeneratorContext context) {
        secondNameTest = registerAndInitComponent(new BooleanGenerator(0.2));
        genderGenerator.init(context);
        birthDateGenerator.init(context);
        firstPersonBirthDateGenerator.init(context);
        acadTitleGenerator =
                registerAndInitComponent(new AcademicTitleGenerator(locale));
        acadTitleGenerator.setLocale(locale);
        maleNobilityTitleGenerator = registerAndInitComponent(
                new NobilityTitleGenerator(Gender.MALE, locale));
        nobilityTitleGenerator = registerAndInitComponent(
                new NobilityTitleGenerator(Gender.FEMALE, locale));
        salutationProvider = new SalutationProvider(locale);

        try {
            initMembersWithDataset(context);
        } catch (Exception e) {
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
    public ProductWrapper<FamilyPerson> generate(ProductWrapper<FamilyPerson> wrapper) {
        String usedDataset = randomDataset();
        FamilyPerson person = generateForDataset(usedDataset);
        return wrapper.wrap(person).setTag(REGION_NESTING, usedDataset);
    }

    @Override
    public FamilyPerson generateFromEntityAndRelation(FamilyPerson familyPerson, RelationConstraints relationConstraints) {
        assertInitialized();
        String datasetToUse = randomDataset();
        FamilyPerson generatedPerson = createPersonWithConstraintRole(familyPerson, relationConstraints);
        GivenNameGenerator givenNameGenerator
                = (Gender.MALE.equals(generatedPerson.getGender()) ? this.maleGivenNameGenerator :
                femaleGivenNameGenerator);
        String givenName = givenNameGenerator.generateForDataset(datasetToUse);
        generatedPerson.setGivenName(givenName);
        Boolean evaluation = generateNullable(secondNameTest);
        if (evaluation != null && evaluation) {
            do {
                generatedPerson.setSecondGivenName(
                        givenNameGenerator.generateForDataset(datasetToUse));
            } while (generatedPerson.getGivenName().equals(generatedPerson.getSecondGivenName()));
        }
        //check and set familyName constraint
        String familyName = null;
        if (relationConstraints.isConstraintNameExist("familyName")) {
            Constraint<String> familyNameConstraint = (SameStringConstraint) relationConstraints.getConstraintByName("familyName");
            familyName = familyNameConstraint.convert(familyPerson.getFamilyName());
            //dynamic change type, update later
        } else {
            familyName = familyNameGenerator.generateForDataset(datasetToUse);
            if (Gender.FEMALE.equals(generatedPerson.getGender())) {
                familyName = getFemaleFamilyNameConverter(datasetToUse)
                        .convert(familyName);
            }
        }
        generatedPerson.setFamilyName(familyName);

        generatedPerson.setSalutation(salutationProvider.salutation(generatedPerson.getGender()));
        generatedPerson.setAcademicTitle(generateNullable(acadTitleGenerator));
        NobilityTitleGenerator nobTitleGenerator
                =
                (Gender.MALE.equals(generatedPerson.getGender()) ? maleNobilityTitleGenerator :
                        nobilityTitleGenerator);
        generatedPerson.setNobilityTitle(generateNullable(nobTitleGenerator));
        //check and set birthday constraint
        Date birthdate = null;
        if (relationConstraints.isConstraintNameExist("age")) {
            Constraint<Integer> ageConstraint = (DiffAgeConstraint) relationConstraints.getConstraintByName("age");
            Integer age = ageConstraint.convert(familyPerson.getAge());
            this.birthDateGenerator = registerComponent(new BirthDateGenerator(age, age));
            this.birthDateGenerator.init(context);
        }
        birthdate = generateNullable(birthDateGenerator);
        generatedPerson.setBirthDate(birthdate);
        //continue to generate other attributes
        generatedPerson.setAge(this.getAge(birthdate));
        generatedPerson.setEmail(emailGenerator.generate(givenName, familyName));
        generatedPerson.setPersonID(generateNonNull(personIDGenerator));
        return generatedPerson;
    }

    @Override
    public FamilyPerson generate() {
        return generateForDataset(randomDataset());
    }

    // private helpers -------------------------------------------------------------------------------------------------

    private String randomDataset() {
        return maleGivenNameGenerator.generate(new ProductWrapper<>()).getTag(DatasetUtil.REGION_NESTING);
    }

    private Converter<String, String> getFemaleFamilyNameConverter(String usedDataset) {
        synchronized (femaleFamilyNameConverters) {
            return femaleFamilyNameConverters.computeIfAbsent(usedDataset, k -> new FemaleFamilyNameConverter(datasetName));
        }
    }

    private void initMembersWithDataset(GeneratorContext context) {
        maleGivenNameGenerator = registerAndInitComponent(new GivenNameGenerator(datasetName, Gender.MALE));
        femaleGivenNameGenerator = registerAndInitComponent(new GivenNameGenerator(datasetName, Gender.FEMALE));
        familyNameGenerator = registerAndInitComponent(new FamilyNameGenerator(datasetName));
        emailGenerator = new EMailAddressBuilder(datasetName);
        emailGenerator.init(context);
        personIDGenerator.init(context);
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
        // If birth date and todays date are of same month and birth day of month is greater than todays day of month then decrement age
        if ((birthDate.get(Calendar.DAY_OF_YEAR) - today.get(Calendar.DAY_OF_YEAR) > 3) ||
                (birthDate.get(Calendar.MONTH) > today.get(Calendar.MONTH)) || ((birthDate.get(Calendar.MONTH) == today.get(Calendar.MONTH)) &&
                (birthDate.get(Calendar.DAY_OF_MONTH) > today.get(Calendar.DAY_OF_MONTH)))) {
            age--;
        }
        return age;
    }

    private FamilyPerson createPersonWithConstraintRole(FamilyPerson relatedPerson, RelationConstraints relationConstraints) {
        FamilyPerson familyPerson = new FamilyPerson(acadTitleGenerator.getLocale());
        //check and set gender constraint
        if (relationConstraints.isConstraintNameExist("role")) {
            FamilyRole role = null;
            Gender gender = null;
            if (relationConstraints.getConstraintByName("role") instanceof PeerRoleConstraint) {
                Constraint<FamilyRole> roleConstraint = (PeerRoleConstraint) relationConstraints.getConstraintByName("role");
                role = roleConstraint.convert(relatedPerson.getFamilyRole());
            } else if (relationConstraints.getConstraintByName("role") instanceof HigherRoleConstraint) {
                Constraint<FamilyRole> roleConstraint = (HigherRoleConstraint) relationConstraints.getConstraintByName("role");
                role = roleConstraint.convert(relatedPerson.getFamilyRole());
            } else {
                role = FamilyRole.FATHER;
            }
            switch (role) {
                case FATHER:
                case GRANDFATHER:
                    gender = Gender.MALE;
                    break;
                case MOTHER:
                case GRANDMOTHER:
                    gender = Gender.FEMALE;
                    break;
                default:
                    break;
            }
            familyPerson.setGender(gender);
            familyPerson.setFamilyRole(role);
        } else {
            familyPerson.setGender(generateNonNull(genderGenerator));
            switch (familyPerson.getGender()) {
                case MALE:
                    familyPerson.setFamilyRole(FamilyRole.SON);
                    break;
                case FEMALE:
                    familyPerson.setFamilyRole(FamilyRole.DAUGHTER);
                    break;
                default:
                    break;
            }
        }
        return familyPerson;
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}

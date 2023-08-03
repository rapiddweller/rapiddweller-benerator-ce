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

import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.NonNullGenerator;
import com.rapiddweller.benerator.primitive.IncrementGenerator;
import com.rapiddweller.benerator.wrapper.CompositeGenerator;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.domain.address.Country;
import com.rapiddweller.domain.person.Gender;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.rapiddweller.benerator.util.GeneratorUtil.generateNonNull;
import static com.rapiddweller.benerator.util.RandomUtil.randomInt;

/**
 * Generates {@link FamilyContainer} that contain List of {@link FamilyPerson} and methods to check parent, children, grandparent count and retrieve each FamilyPerson.<br/><br/>
 */
public class FamilyGenerator extends CompositeGenerator<FamilyContainer>
        implements NonNullGenerator<FamilyContainer> {

    private final FamilyPersonGenerator familyPersonGen;
    private final IncrementGenerator familyIDGen;
    private final WeightedStringGenerator diverseCaseGen;
    private final WeightedStringGenerator divorcedCaseGen;
    private final RelationConstraints peerRelationConstraints;
    private final RelationConstraints lowerRelationConstraints;
    private final RelationConstraints higherRelationConstraints;
    private int maxBiologicalChildrenNumber;
    private int maxChildrenAdoptedNumber;
    // constrain name as String
    private static final String FAMILY_NAME = "familyName";
    private static final String AGE = "age";
    private static final String ROLE = "role";

    // Constructor ----------------------------------------------------------------------------------------------------

    public FamilyGenerator() {
        this(Country.getDefault().getIsoCode(), Locale.getDefault());
    }

    public FamilyGenerator(String datasetName, Locale locale) {
        super(FamilyContainer.class);
        logger.debug("Instantiating FamilyGenerator with dataset '{}' and locale '{}'", datasetName, locale);
        this.familyPersonGen = registerComponent(new FamilyPersonGenerator(datasetName, locale));
        this.familyIDGen = registerComponent(new IncrementGenerator());
        this.diverseCaseGen = registerComponent(new WeightedStringGenerator(new String[]{"diverse", "nonDiverse"}, new double[]{0, 1}));
        this.divorcedCaseGen = registerComponent(new WeightedStringGenerator(new String[]{"divorced", "nonDivorced"}, new double[]{0.2, 0.8}));
        //Create default Peer Relation Constraints
        this.peerRelationConstraints = new RelationConstraints();
        this.peerRelationConstraints.registerOrUpdateConstraint(AGE, new DiffAgeConstraint(-2, 5));
        this.peerRelationConstraints.registerOrUpdateConstraint(FAMILY_NAME, new SameStringConstraint());
        this.peerRelationConstraints.registerOrUpdateConstraint(ROLE, new PeerRoleConstraint());
        //Create default Lower Relation Constraints
        this.lowerRelationConstraints = new RelationConstraints();
        this.lowerRelationConstraints.registerOrUpdateConstraint(AGE, new DiffAgeConstraint(-40, -20));
        this.lowerRelationConstraints.registerOrUpdateConstraint(FAMILY_NAME, new SameStringConstraint());
        this.maxBiologicalChildrenNumber = 10;
        this.maxChildrenAdoptedNumber = 5;
        //Create default Higher Relation Constraints
        this.higherRelationConstraints = new RelationConstraints();
        this.higherRelationConstraints.registerOrUpdateConstraint(AGE, new DiffAgeConstraint(20, 40));
        this.higherRelationConstraints.registerOrUpdateConstraint(FAMILY_NAME, new SameStringConstraint());
        this.higherRelationConstraints.registerOrUpdateConstraint(ROLE, new HigherRoleConstraint());
    }

    // Attributes --------------------------------------------------------------------------------------
    // general locale and dataset setup
    public void setLocale(Locale locale) {
        this.familyPersonGen.setLocale(locale);
    }

    public void setDataset(String dataset) {
        this.familyPersonGen.setDataset(dataset);
    }

    // Age range for first parent (root)
    public void setFirstParentMinAgeYears(int minAgeYear) {
        this.familyPersonGen.setMinAgeYears(minAgeYear);
    }

    public void setFirstParentMaxAgeYears(int maxAgeYear) {
        this.familyPersonGen.setMaxAgeYears(maxAgeYear);
    }

    // Peer Relation Setup
    public void setMinDiffAgeInPeerRelation(int minDiffAge) {
        var temp = (DiffAgeConstraint) this.peerRelationConstraints.getConstraintByName(AGE);
        temp.setMinDiffAge(minDiffAge);
    }

    public void setMaxDiffAgeInPeerRelation(int maxDiffAge) {
        var temp = (DiffAgeConstraint) this.peerRelationConstraints.getConstraintByName(AGE);
        temp.setMaxDiffAge(maxDiffAge);
    }

    public void setParentFamilyNameEnable(boolean status) {
        if (status) {
            this.peerRelationConstraints.registerOrUpdateConstraint(FAMILY_NAME, new SameStringConstraint());
        } else {
            this.peerRelationConstraints.removeConstraintByName(FAMILY_NAME);
        }
    }

    public void setDivorcedParentQuota(double divorcedParentQuota) {
        this.divorcedCaseGen.setWeight(new double[]{divorcedParentQuota, 1 - divorcedParentQuota});
    }

    public void setDiverseParentQuota(double diverseParentQuota) {
        this.diverseCaseGen.setWeight(new double[]{diverseParentQuota, 1 - diverseParentQuota});
    }

    // Lower Relation Setup
    public void setMinDiffAgeInLowerRelation(int minDiffAge) {
        var temp = (DiffAgeConstraint) this.lowerRelationConstraints.getConstraintByName(AGE);
        temp.setMinDiffAge(minDiffAge);
    }

    public void setMaxDiffAgeInLowerRelation(int maxDiffAge) {
        var temp = (DiffAgeConstraint) this.lowerRelationConstraints.getConstraintByName(AGE);
        temp.setMaxDiffAge(maxDiffAge);
    }

    public void setMaxBiologicalChildrenNumber(int maxBiologicalChildrenNumber) {
        this.maxBiologicalChildrenNumber = maxBiologicalChildrenNumber;
    }

    public void setMaxChildrenAdoptedNumber(int maxChildrenAdoptedNumber) {
        this.maxChildrenAdoptedNumber = maxChildrenAdoptedNumber;
    }

    // Higher Relation Setup
    public void setMinDiffAgeInHigherRelation(int minDiffAge) {
        var temp = (DiffAgeConstraint) this.higherRelationConstraints.getConstraintByName(AGE);
        temp.setMinDiffAge(minDiffAge);
    }

    public void setMaxDiffAgeInHigherRelation(int maxDiffAge) {
        var temp = (DiffAgeConstraint) this.higherRelationConstraints.getConstraintByName(AGE);
        temp.setMaxDiffAge(maxDiffAge);
    }

    // Generator Interface --------------------------------------------------------------------------------------
    @Override
    public synchronized void init(GeneratorContext context) {
        familyPersonGen.init(context);
        familyIDGen.init(context);
        divorcedCaseGen.init(context);
        diverseCaseGen.init(context);
        super.init(context);
    }

    @Override
    public FamilyContainer generate() {
        FamilyContainer family = new FamilyContainer();
        long familyID = familyIDGen.generate();
        //generate first parent
        String diverseStatus = generateNonNull(diverseCaseGen);
        FamilyPerson firstParent = generateFirstParent(diverseStatus);
        family.addFamilyPerson(firstParent);
        //generate second parent
        firstParent.setFamilyID(familyID);
        String divorcedStatus = generateNonNull(divorcedCaseGen);
        FamilyPerson secondParent = generateParentFromParent(divorcedStatus, firstParent, peerRelationConstraints);
        family.addFamilyPerson(secondParent);
        //generate children
        List<FamilyPerson> children = generateChildrenFromParent(firstParent, secondParent, lowerRelationConstraints);
        family.addListOfFamilyPerson(children);
        //generate grandparents
        List<FamilyPerson> grandParents = generateGrandparentFromParent(firstParent, secondParent, higherRelationConstraints);
        family.addListOfFamilyPerson(grandParents);
        return family;
    }

    @Override
    public ProductWrapper<FamilyContainer> generate(ProductWrapper<FamilyContainer> wrapper) {
        return wrapper.wrap(generate());
    }

    // private helpers -------------------------------------------------------------------------------------------------

    private FamilyPerson generateFirstParent(String diverseCase) {
        FamilyPerson firstPerson = this.familyPersonGen.generate();
        Gender presetGender = firstPerson.getGender();
        if (diverseCase.equalsIgnoreCase("diverse")) {
            firstPerson.setGender(Gender.DIVERSE);
            firstPerson.setFamilyRole(presetGender.equals(Gender.MALE) ? FamilyRole.FATHER_DIVERSE : FamilyRole.MOTHER_DIVERSE);
        } else {
            firstPerson.setFamilyRole(presetGender.equals(Gender.MALE) ? FamilyRole.FATHER : FamilyRole.MOTHER);
        }
        return firstPerson;
    }

    private FamilyPerson generateParentFromParent(String divorcedCase, FamilyPerson firstFamilyPerson, RelationConstraints peerRelationConstraints) {
        FamilyPerson secondParent = this.familyPersonGen.generateFromEntityAndRelation(firstFamilyPerson, peerRelationConstraints);
        if (divorcedCase.equalsIgnoreCase("divorced")) {
            secondParent.addRelationStatusWithPerson(firstFamilyPerson, RelationStatus.DIVORCED);
        } else {
            secondParent.addRelationStatusWithPerson(firstFamilyPerson, RelationStatus.MARRIAGE);
        }
        return secondParent;
    }

    private List<FamilyPerson> generateChildrenFromParent(FamilyPerson firstFamilyPerson, FamilyPerson secondFamilyPerson, RelationConstraints lowerRelationConstraints) {
        List<FamilyPerson> result = new ArrayList<>();
        int parent1Age = firstFamilyPerson.getAge();
        int parent2Age = secondFamilyPerson.getAge();
        FamilyPerson personForGenerateChildren = parent1Age <= parent2Age ? firstFamilyPerson : secondFamilyPerson;

        int maxAllowableChild = (Math.min(parent1Age, parent2Age) - 18) / 2;
        if (this.maxBiologicalChildrenNumber >= maxAllowableChild) {
            this.maxBiologicalChildrenNumber = maxAllowableChild;
        }

        if (this.maxBiologicalChildrenNumber > 0) {
            int biologicalChildrenNumber = randomInt(1, this.maxBiologicalChildrenNumber);
            for (int i = 0; i < biologicalChildrenNumber; i++) {
                FamilyPerson tempChild = this.familyPersonGen.generateFromEntityAndRelation(personForGenerateChildren, lowerRelationConstraints);
                tempChild.addRelationStatusWithPerson(firstFamilyPerson, RelationStatus.CARETAKER);
                tempChild.addRelationStatusWithPerson(firstFamilyPerson, RelationStatus.CARETAKER);
                result.add(tempChild);
            }
        }
        if (this.maxChildrenAdoptedNumber > 0) {
            int childrenAdoptedNumber = randomInt(1, maxChildrenAdoptedNumber);
            for (int i = 0; i < childrenAdoptedNumber; i++) {
                FamilyPerson tempChild = this.familyPersonGen.generateFromEntityAndRelation(personForGenerateChildren, lowerRelationConstraints);
                tempChild.addRelationStatusWithPerson(firstFamilyPerson, RelationStatus.BENEFIT_PROVIDER);
                tempChild.addRelationStatusWithPerson(secondFamilyPerson, RelationStatus.BENEFIT_PROVIDER);
                result.add(tempChild);
            }
        }
        return result;
    }

    private List<FamilyPerson> generateGrandparentFromParent(FamilyPerson firstFamilyPerson, FamilyPerson secondFamilyPerson, RelationConstraints higherRelationConstraints) {
        //create grandparent 1&2 from second parent
        FamilyPerson father = null;
        FamilyPerson mother = null;
        if (firstFamilyPerson.getFamilyRole().equals(FamilyRole.FATHER)) {
            father = firstFamilyPerson;
            mother = secondFamilyPerson;
        } else {
            father = secondFamilyPerson;
            mother = firstFamilyPerson;
        }
        //grandparent of father will have same family name with father
        FamilyPerson grandparent1 = this.familyPersonGen.generateFromEntityAndRelation(father, higherRelationConstraints);
        grandparent1.addRelationStatusWithPerson(father, RelationStatus.BIOLOGICAL);
        FamilyPerson grandparent2 = this.familyPersonGen.generateFromEntityAndRelation(grandparent1, peerRelationConstraints);
        grandparent2.addRelationStatusWithPerson(grandparent1, RelationStatus.MARRIAGE);
        //grandparent of mother will have different family name with mother
        this.higherRelationConstraints.removeConstraintByName(FAMILY_NAME);
        FamilyPerson grandparent3 = this.familyPersonGen.generateFromEntityAndRelation(mother, higherRelationConstraints);
        grandparent3.addRelationStatusWithPerson(mother, RelationStatus.BIOLOGICAL);
        FamilyPerson grandparent4 = this.familyPersonGen.generateFromEntityAndRelation(grandparent3, peerRelationConstraints);
        grandparent4.addRelationStatusWithPerson(grandparent3, RelationStatus.MARRIAGE);
        //restore original status of higher relation constraint
        this.higherRelationConstraints.registerOrUpdateConstraint(FAMILY_NAME, new SameStringConstraint());
        return new ArrayList<>(List.of(grandparent1, grandparent2, grandparent3, grandparent4));
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}

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
import com.rapiddweller.benerator.wrapper.CompositeGenerator;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.domain.person.*;

import java.util.*;

import static com.rapiddweller.benerator.util.RandomUtil.randomBoolean;
import static com.rapiddweller.benerator.util.RandomUtil.randomInt;

/**
 * Generates {@link FamilyContext} beans.<br/><br/>
 *
 * @since 0.1
 */
public class FamilyContextGenerator extends CompositeGenerator<FamilyContext> implements NonNullGenerator<FamilyContext> {
    private FamilyContext familyContext;
    private FamilyNameGenerator familyNameGenerator;
    private long currentPersonId;
    private long currentFamilyId;

    public FamilyContextGenerator() {
        super(FamilyContext.class);
        logger.debug("Instantiating FamilyContextGenerator");
        this.familyNameGenerator = registerComponent(new FamilyNameGenerator());
        currentPersonId=1L;
        currentFamilyId=1L;
    }

    // properties ------------------------------------------------------------------------------------------------------

    public FamilyContext getFamilyContext() {
        return familyContext;
    }

    public void setFamilyContext(FamilyContext familyContext) {
        this.familyContext = familyContext;
    }

    public long getCurrentPersonId() {
        return currentPersonId;
    }

    public long getCurrentFamilyId() {
        return currentFamilyId;
    }

    // Generator interface ---------------------------------------------------------------------------------------------

    @Override
    public synchronized void init(GeneratorContext context) {
        familyNameGenerator.init(context);
        super.init(context);
    }

    @Override
    public ProductWrapper<FamilyContext> generate(
            ProductWrapper<FamilyContext> wrapper) {
        return wrapper.wrap(generate());
    }

    @Override
    public FamilyContext generate() {
        createRandomFamilyContext();
        return this.familyContext;
    }

    // private helpers -------------------------------------------------------------------------------------------------
    private void createRandomFamilyContext() {
        FamilyContext tempFamilyContext = new FamilyContext();
        //make random parents context (dad,mom)
        final int percentageOfSingleDad = 15;
        final int percentageOfSingleMom = 15;
        //percentage of family with Dad and Mom is 70
        final String familyLastName = familyNameGenerator.generate();
        final int minAgeParent = 20;
        final int maxAgeParent = 80;
        int familyContextCase = randomInt(1, 100);
        if (familyContextCase < percentageOfSingleDad) {
            //add dad in tempFamilyContext
            PersonNode dad = createPersonByRole(FamilyRole.FATHER, familyLastName);
            int dadAge = randomInt(minAgeParent, maxAgeParent);
            dad.getConstraint().setConstraintAge(dadAge);
            tempFamilyContext.addPersonNode(dad);
            //add children in tempFamilyContext
            ArrayList<PersonNode> childrenList = createChildrenFromParent(dad);
            tempFamilyContext.addListPersonNode(childrenList);
        } else if (familyContextCase < (percentageOfSingleDad + percentageOfSingleMom)) {
            //add mom in tempFamilyContext
            PersonNode mom = createPersonByRole(FamilyRole.MOTHER, familyLastName);
            int momAge = randomInt(minAgeParent, maxAgeParent);
            mom.getConstraint().setConstraintAge(momAge);
            tempFamilyContext.addPersonNode(mom);
            //add children in tempFamilyContext
            ArrayList<PersonNode> childrenList = createChildrenFromParent(mom);
            tempFamilyContext.addListPersonNode(childrenList);
        } else {
            //add dad in tempFamilyContext
            PersonNode dad = createPersonByRole(FamilyRole.FATHER, familyLastName);
            int dadAge = randomInt(minAgeParent, maxAgeParent);
            dad.getConstraint().setConstraintAge(dadAge);
            tempFamilyContext.addPersonNode(dad);
            //add mom in tempFamilyContext
            int momAge;
            if (randomBoolean()) {
                momAge = dadAge + randomInt(0, 5);
            } else {
                momAge = dadAge - randomInt(0, 5);
                if (momAge < minAgeParent) {
                    momAge = minAgeParent;
                }
            }
            PersonNode mom = createPersonByRole(FamilyRole.MOTHER, familyLastName);
            mom.getConstraint().setConstraintAge(momAge);
            tempFamilyContext.addPersonNode(mom);
            //add children in tempFamilyContext
            ArrayList<PersonNode> childrenList = createChildrenFromParent(dadAge > momAge ? mom : dad);
            tempFamilyContext.addListPersonNode(childrenList);
        }
        this.familyContext = tempFamilyContext;
        currentFamilyId++;
    }

    private ArrayList<PersonNode> createChildrenFromParent(PersonNode parent) {
        ArrayList<PersonNode> result = new ArrayList<>();
        final int maxChildrenAge = parent.getConstraint().getConstraintAge() - 18;
        final int minChildrenAge = parent.getConstraint().getConstraintAge() - 40 > 0 ? parent.getConstraint().getConstraintAge() - 40 : 1;
        int numberAllowedChildren = maxChildrenAge / 2;
        int percentageForTooManyChild = 10;
        //common case has less than 3 child is 90
        int childContextCase = randomInt(1, 100);
        int childNumber = 1;
        if (childContextCase < percentageForTooManyChild) {
            childNumber = randomInt(4, 10);
        } else {
            childNumber = randomInt(1, 3);
        }
        if (childNumber > numberAllowedChildren) {
            childNumber = numberAllowedChildren;
        }
        int[] childrenAges = createChildrenAgeFromRange(childNumber, minChildrenAge, maxChildrenAge);
        for (Integer childAge : childrenAges) {
            PersonNode child = createPersonByRole(randomBoolean() ? FamilyRole.SON : FamilyRole.DAUGHTER, parent.getConstraint().getConstraintLastName());
            child.getConstraint().setConstraintAge(childAge);
            result.add(child);
        }
        return result;
    }

    private int[] createChildrenAgeFromRange(int childNumber, int minChildrenAge, int maxChildrenAge) {
        final int minDiffAgeBetweenChild = 2;
        int[] result = new int[childNumber];
        if (childNumber == 1) {
            result[0] = randomInt(minChildrenAge, maxChildrenAge);
        } else {
            final int maxDiffAgeBetweenChild = (maxChildrenAge - minChildrenAge) / (childNumber - 1);
            for (int i = 0; i < result.length; i++) {
                if (i == 0) {
                    result[i] = minChildrenAge;
                } else {
                    result[i] = result[i - 1] + randomInt(minDiffAgeBetweenChild, maxDiffAgeBetweenChild);
                }
            }
            if (result[result.length - 1] < maxChildrenAge) {
                int diff = maxChildrenAge - result[result.length - 1];
                int randomOffset = randomInt(0, diff);
                for (int i = 0; i < result.length; i++) {
                    result[i] = result[i] + randomOffset;
                }
            }
        }
        return result;
    }

    private PersonNode createPersonByRole(FamilyRole role, String lastName) {
        PersonNode person = new PersonNode();
        person.setPersonID(currentPersonId);
        person.getRelation().setFamilyID(currentFamilyId);
        person.getRelation().setFamilyRole(role);
        person.getConstraint().setConstraintLastName(lastName);
        switch (role) {
            case FATHER:
                person.getConstraint().setConstraintGender("male");
                break;
            case MOTHER:
                person.getConstraint().setConstraintGender("female");
                break;
            case SON:
                person.getConstraint().setConstraintGender("male");
                break;
            case DAUGHTER:
                person.getConstraint().setConstraintGender("female");
                break;
            default:
                break;
        }
        currentPersonId++;
        return person;
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------
    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}

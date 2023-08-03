/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.domain.family;

import com.rapiddweller.benerator.test.GeneratorClassTest;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import org.junit.Test;

import javax.validation.constraints.AssertTrue;

import java.util.List;
import java.util.Locale;

import static org.junit.Assert.*;

/**
 * Tests the {@link FamilyGenerator}.<br/><br/>
 * Created: 07.12.2021 17:14:00
 *
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class FamilyGeneratorTest extends GeneratorClassTest {

    public FamilyGeneratorTest() {
        super(FamilyGenerator.class);
    }

    @Test
    public void testGenerateNotNull() {
        ProductWrapper<FamilyContainer> w = new ProductWrapper<>();
        FamilyGenerator g = new FamilyGenerator();
        g.init(context);
        for (int i = 0; i < 1; i++) {
            w = g.generate(w);
            FamilyContainer c = w.unwrap();

            //check familyPersonList
            assertNotNull(c.getFamilyPersonList());
            assertFalse(c.getFamilyPersonList().isEmpty());

            //check number of parents
            assertEquals(2, c.getParentCount());
            assertNotNull(c.getParents());
            for (FamilyPerson parent : c.getParents()) {
                assertNotNull(parent);
                assertNotNull(parent.getFamilyName());
                assertNotNull(parent.getGivenName());
                assertNotNull(parent.getFamilyRole());
            }

            //check number of grandparents
            assertEquals(4, c.getGrandparentCount());
            assertNotNull(c.getGrandparents());
            for (FamilyPerson grandparent : c.getGrandparents()) {
                assertNotNull(grandparent);
                assertNotNull(grandparent.getFamilyName());
                assertNotNull(grandparent.getGivenName());
                assertNotNull(grandparent.getFamilyRole());
            }

            // check children
            assertTrue(c.getChildrenCount() >= 0);
            assertNotNull(c.getChildren());
            for (FamilyPerson child : c.getChildren()) {
                assertNotNull(child);
                assertNotNull(child.getFamilyName());
                assertNotNull(child.getGivenName());
                assertNotNull(child.getFamilyRole());
            }

            //check familyID
            long familyID = c.getFamilyPersonList().get(0).getFamilyID();
            assertTrue(familyID > 0);

            //check other access methods
            int fatherCount = c.getNumberOfRoleInFamily(FamilyRole.FATHER);
            assertEquals(1, fatherCount);
            int motherCount = c.getNumberOfRoleInFamily(FamilyRole.MOTHER);
            assertEquals(1, motherCount);
            int grandfatherCount = c.getNumberOfRoleInFamily(FamilyRole.GRANDFATHER);
            assertEquals(2, grandfatherCount);
            int grandmotherCount = c.getNumberOfRoleInFamily(FamilyRole.GRANDMOTHER);
            assertEquals(2, grandmotherCount);
        }
    }

    @Test
    public void testGenerateNotNullWithCustomAttributes() {
        ProductWrapper<FamilyContainer> w = new ProductWrapper<>();
        FamilyGenerator g = new FamilyGenerator();
        //config general
        g.setDataset("DE");
        g.setLocale(new Locale("de_DE"));
        int customFirstParentMinAgeYears = 30;
        int customFirstParentMaxAgeYears = 50;
        g.setFirstParentMinAgeYears(customFirstParentMinAgeYears);
        g.setFirstParentMaxAgeYears(customFirstParentMaxAgeYears);
        //config peerRelation
        int customMinDiffAgeInPeerRelation = 0;
        int customMaxDiffAgeInPeerRelation = 3;
        g.setMinDiffAgeInPeerRelation(customMinDiffAgeInPeerRelation);
        g.setMaxDiffAgeInPeerRelation(customMaxDiffAgeInPeerRelation);
        //config higherRelation
        int customMinDiffAgeInHigherRelation = 30;
        int customMaxDiffAgeInHigherRelation = 40;
        g.setMinDiffAgeInHigherRelation(customMinDiffAgeInHigherRelation);
        g.setMaxDiffAgeInHigherRelation(customMaxDiffAgeInHigherRelation);
        //config lowerRelation
        int customMaxBiologicalChildrenNumber = 5;
        int customMaxChildrenAdoptedNumber = 1;
        g.setMaxBiologicalChildrenNumber(customMaxBiologicalChildrenNumber);
        g.setMaxChildrenAdoptedNumber(customMaxChildrenAdoptedNumber);
        //init context
        g.init(context);
        for (int i = 0; i < 1000; i++) {
            w = g.generate(w);
            FamilyContainer c = w.unwrap();

            //get familyPerson
            FamilyPerson father = c.getFamilyPersonList().stream()
                    .filter(familyPerson -> familyPerson.getFamilyRole().equals(FamilyRole.FATHER))
                    .findFirst()
                    .orElse(null);
            assertNotNull(father);
            int fatherAge = father.getAge();
            String fatherFamilyName = father.getFamilyName();
            FamilyPerson mother = c.getFamilyPersonList().stream()
                    .filter(familyPerson -> familyPerson.getFamilyRole().equals(FamilyRole.MOTHER))
                    .findFirst()
                    .orElse(null);
            assertNotNull(mother);
            int motherAge = mother.getAge();
            String motherFamilyName = mother.getFamilyName();
            FamilyPerson grandParentOfFather = c.getGrandparents().stream()
                    .filter(grandparent -> grandparent.getRelations().containsKey(father))
                    .findFirst()
                    .orElse(null);
            assertNotNull(grandParentOfFather);
            int grandParent1Age = grandParentOfFather.getAge();
            String grandParent1FamilyName = grandParentOfFather.getFamilyName();
            List<FamilyPerson> children = c.getChildren();
            assertNotNull(children);
            int childrenCount = children.size();

            //check general attributes
            assertTrue(c.getFamilyPersonList().get(0).getAge() >= customFirstParentMinAgeYears && c.getFamilyPersonList().get(0).getAge() <= customFirstParentMaxAgeYears);

            //check peer constraint
            assertEquals(fatherFamilyName, motherFamilyName);
            assertTrue(c.getFamilyPersonList().get(1).getAge() >= c.getFamilyPersonList().get(0).getAge() + customMinDiffAgeInPeerRelation
                    && c.getFamilyPersonList().get(1).getAge() <= c.getFamilyPersonList().get(0).getAge() + customMaxDiffAgeInPeerRelation);

            //check higher constraint
            assertEquals(fatherFamilyName, grandParent1FamilyName);
            assertTrue(grandParent1Age >= fatherAge + customMinDiffAgeInHigherRelation && grandParent1Age <= fatherAge + customMaxDiffAgeInHigherRelation);

            //check lower constraint
            assertTrue(childrenCount <= customMaxBiologicalChildrenNumber + customMaxChildrenAdoptedNumber);
            for (FamilyPerson child : children) {
                assertEquals(fatherFamilyName, child.getFamilyName());
                assertTrue(child.getAge() >= 1 && child.getAge() <= (Math.min(fatherAge, motherAge)) - 18);
            }
        }
    }
}

/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.domain.family;

import com.rapiddweller.benerator.test.GeneratorClassTest;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import org.junit.Test;

import javax.validation.constraints.AssertTrue;

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
        for (int i = 0; i < 100; i++) {
            w = g.generate(w);
            FamilyContainer c = w.unwrap();
            // print familyPerson toString method
            System.out.println(c.getFamilyPersonList().toString());

            //check familyPersonList
            assertNotNull(c);
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
        }
    }

}

/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */
package com.rapiddweller.domain.family;

import com.rapiddweller.benerator.test.GeneratorClassTest;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

/**
 * Tests the Constraint Classes.<br/><br/>
 *
 * @since 2.1.0
 */
public class ConstraintClassesTest {
    @Test
    public void RoleConstraintTest() {
        PeerRoleConstraint peer = new PeerRoleConstraint();
        HigherRoleConstraint higher = new HigherRoleConstraint();
        assertEquals(FamilyRole.class, peer.getTargetType());
        assertFalse(peer.isParallelizable());
        assertFalse(peer.isThreadSafe());
        assertEquals(FamilyRole.MOTHER, peer.convert(FamilyRole.FATHER));
        assertEquals(FamilyRole.FATHER, peer.convert(FamilyRole.MOTHER));
        assertEquals(FamilyRole.GRANDFATHER, higher.convert(FamilyRole.FATHER));
        assertEquals(FamilyRole.GRANDFATHER, higher.convert(FamilyRole.MOTHER));
    }

    @Test
    public void diffAgeConstraintTest() {
        DiffAgeConstraint diffAge = new DiffAgeConstraint(-10, 10);
        int initAge = 50;
        int targetAge = diffAge.convert(initAge);
        assertFalse(diffAge.isParallelizable());
        assertFalse(diffAge.isThreadSafe());
        assertTrue(targetAge >= initAge - 10 && targetAge <= initAge + 10);
    }

    @Test
    public void sameStringConstraintTest() {
        SameStringConstraint sameString = new SameStringConstraint();
        String initString = "ABCDEF";
        assertFalse(sameString.isParallelizable());
        assertFalse(sameString.isThreadSafe());
        assertEquals(initString, sameString.convert(initString));
    }
}

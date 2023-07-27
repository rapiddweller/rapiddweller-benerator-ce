/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.domain.family;

import com.rapiddweller.benerator.test.GeneratorClassTest;
import com.rapiddweller.domain.person.Person;
import com.rapiddweller.domain.person.PersonGenerator;
import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.assertNotNull;

/**
 * Tests the {@link FamilyPerson}.<br/><br/>
 * @since 2.1.0
 */
public class FamilyPersonGeneratorTest extends GeneratorClassTest {

  public FamilyPersonGeneratorTest() {
    super(FamilyPersonGenerator.class);
  }

  //Default Locale & Dataset
  @Test
  public void defaultGenerateTest() {
    FamilyPersonGenerator generator = new FamilyPersonGenerator();
    generator.init(context);
    for (int i = 0; i < 10; i++) {
      FamilyPerson familyPerson = generator.generate();
      assertNotNull(familyPerson);
      logger.debug("familyPerson: " + familyPerson.toString());
      System.out.println("familyPerson: " + familyPerson.toString());
    }
  }
  @Test
  public void defaultGenerateFromEntityAndRelationTest() {
    FamilyPersonGenerator generator = new FamilyPersonGenerator();
    generator.init(context);
    RelationConstraints testConstraint = new RelationConstraints();
    //add min and max different age constraint from related person
    testConstraint.registerOrUpdateConstraint("age", new DiffAgeConstraint(-2, 5));
    //add same family name constraint from related person
    testConstraint.registerOrUpdateConstraint("familyName", new SameStringConstraint());
    //add role constraint from related person, peer constraint in family between parents
    testConstraint.registerOrUpdateConstraint("role", new PeerRoleConstraint());
    for (int i = 0; i < 10; i++) {
      FamilyPerson firstFamilyPerson = generator.generate();
      firstFamilyPerson.setFamilyRole(FamilyRole.FATHER);
      FamilyPerson secondFamilyPerson = generator.generateFromEntityAndRelation(firstFamilyPerson, testConstraint);
      assertNotNull(firstFamilyPerson);
      assertNotNull(secondFamilyPerson);
      logger.debug("firstPerson: " + firstFamilyPerson.toString());
      logger.debug("secondPerson (related from the firstPerson): " + secondFamilyPerson.toString());
      System.out.println("firstPerson: " + firstFamilyPerson.toString());
      System.out.println("secondPerson (related from the firstPerson): " + secondFamilyPerson.toString());
    }

  }
  //custom default & dataset
  @Test
  public void customGenerateTest() {
    FamilyPersonGenerator generator = new FamilyPersonGenerator();
    generator.setDataset("DE");
    generator.setLocale(new Locale("de_DE"));
    generator.init(context);
    for (int i = 0; i < 10; i++) {
      FamilyPerson familyPerson = generator.generate();
      assertNotNull(familyPerson);
      logger.debug(familyPerson.toString());
      System.out.println(familyPerson.toString());
    }

  }
  @Test
  public void customGenerateFromEntityAndRelationTest() {
    FamilyPersonGenerator generator = new FamilyPersonGenerator();
    generator.setDataset("DE");
    generator.setLocale(new Locale("de_DE"));
    generator.init(context);
    RelationConstraints testConstraint = new RelationConstraints();
    //add min and max different age constraint from related person
    testConstraint.registerOrUpdateConstraint("age", new DiffAgeConstraint(-2, 5));
    //add same family name constraint from related person
    testConstraint.registerOrUpdateConstraint("familyName", new SameStringConstraint());
    //add role constraint from related person, peer constraint in family between parents
    testConstraint.registerOrUpdateConstraint("role", new PeerRoleConstraint());
    for (int i = 0; i < 10; i++) {
      FamilyPerson firstFamilyPerson = generator.generate();
      firstFamilyPerson.setFamilyRole(FamilyRole.FATHER);
      FamilyPerson secondFamilyPerson = generator.generateFromEntityAndRelation(firstFamilyPerson, testConstraint);
      assertNotNull(firstFamilyPerson);
      assertNotNull(secondFamilyPerson);
      logger.debug("firstPerson: " + firstFamilyPerson.toString());
      logger.debug("secondPerson (related from the firstPerson): " + secondFamilyPerson.toString());
      System.out.println("firstPerson: " + firstFamilyPerson.toString());
      System.out.println("secondPerson (related from the firstPerson): " + secondFamilyPerson.toString());
    }
  }
}

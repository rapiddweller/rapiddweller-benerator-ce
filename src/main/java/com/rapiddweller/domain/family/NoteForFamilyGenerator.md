Concept:
- FamilyGenerator generate FamilyContainer contain FamilyPerson extends Person Entity so it have all Person's fields. Family specific data: PersonID, FamilyID, FamilyRole, relations(FamilyPerson (related person), RelationStatus)
- FamilyContainer have utility methods to count number of a generation: grandparents-parents-children, and get list of these generations so that you can extract data base on count information.
Class Diagram for FamilyGenerator: https://lucid.app/lucidchart/7b68116a-8646-4fe9-8777-bdcfb5592837/edit?beaconFlowId=72C9C6C2765D86B6&invitationId=inv_3ce8f6dd-34d4-4587-bc1b-397e89416d02&page=HWEp-vi-RSFO#
- FamilyPerson Generator implement RelationGenerator with generateFromEntityAndRelation() method to create 1 FamilyPerson Entity from some constraint of a pre-defined FamilyPerson Entity.
- ConstraintRelation contain the related rule from same attribute of pre-defined FamilyPerson Entity (in family case, for example: same family name, roles and different age).
- Generation Flow (after generate, add to FamilyContainer): 
  + (parent1)
  + (parent2) from (parent1) + (peerRelationConstraint)
  + (children(biological/adopt/twin)) from (parent1) + (parent2) + (lowerRelationConstraint)
  + (grandparents) from (parent1) + (parent2) + (higherRelationConstraint)
Demo: please check familyGenerationTest1.ben.xml in test/resource folder:
```
src/test/resources/com/rapiddweller/domain/family/familyGeneratorTest1.ben.xml
```
Custom attribute up to now:
  + general: Locale, Dataset
  + first parent: FirstParentMinAgeYears(default=21, must be at least 21), FirstParentMaxAgeYears(default=60) (this is exactly as PersonGenerator, this case femaleQuota is set default)
  + peerRelation (between parents or grandparents - same rule): MinDiffAgeInPeerRelation(default=-2), MaxDiffAgeInPeerRelation(default=5), ParentFamilyNameEnable(default=true-parents have same familyName), DivorcedParentQuota(default=0.2-this set relation between parents is DIVORCED), DiverseParentQuota(default=1-this is not applicable, please check Limitation)
  + higherRelation (between parent and grandparent): MinDiffAgeInHigherRelation(default=20), MaxDiffAgeInPeerRelation(default=50)- if father role, grandparents has same family name as father, different for mother role.
  + lowerRelation (between parent and children): MinDiffAgeInLowerRation(default=-50), MaxDiffAgeInLowerRelation(default=-20), MaxBiologicalChildrenNumber(default=10), MaxChildrenAdoptedNumber(default=5), MaxChildrenTwinCase(default=2)

Limitation:

- Only fixed structure of family can be generated: 2 parent (father-mother), 4 grandparent (grandfather-grandmother of father/mother), children with maximum children number set default to 10, adopted to 5.

- Diverse Gender is not applied, FamilyPersonGenerator is base on logic of PersonGenerator, this type of gender is not supported in this model now (change DiverseParentQuota attribute can cause error).

- In this implementation, I re-construct new BirthdayGenerator, set attribute and init(context) again each time generate (to custom age from constraint), I think this can potentially cause performance issue. This will need to be improved.

- Some test for generate non-null FamilyContainer, FamilyPerson as link below. Need more test and feedback to improve this type of Generator.
```
src/test/java/com/rapiddweller/domain/family
```

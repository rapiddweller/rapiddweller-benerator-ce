# Changelog

<!--lint disable no-duplicate-headings-->

## 3.0.0

### Release Highlights

- Fixed support for 'time' data type
- Fixed regex based string generation for optional groups
- Added missing fields to benerator.xsd
- Fixed bug: <memstore> ignores entity identity

### Important Notes

- 


### Breaking Changes

- 

---

## 2.0.0

### Release Highlights

- Improved Benerator engine performance by 55-110% in average
- Added address domain package for the United Kingdom (GB)
- Supporting custom random implementations
- Reworked Benerator archetypes
- Improved JDBC metadata import
- Improved/reworked Benerator Project Wizard
- Improved/reworked Benerator Maven Plugin
- Add custom separator support for Benerator weighting files
- Add new [Hash Converters](/doc/component_reference.md#rapiddweller-converters)
- Add Benchmark Tool and [Performance Tuning](/doc/performance_tuning.md) section in Manual
- Extend PersonGenerator with attribute age
- Update Graalvm to 21.1.0
- Improved and extended Benerator Manual

### Important Notes

- Redesigned the Distribution class hierarchy, introduced the AbstractDistribution
  and DetachedSequence classes to compensate this for implementors.


### Breaking Changes

- See the manual's
  [Migration Guide](/doc/migrating_benerator_projects.md#migration-from-11x-to-200)
  what to do if you programmed custom Distributions.

---

## 1.1.3

### Release Highlights

- improved JDBC metadata import
- custom separator support for benerator weighting files
- further improvements docs

---

## 1.1.2

### Release Highlights

- improved way of migrating Benerator Objects as ProxyObject into GraalVM Context
- further improvement of handling multi schema databases
- upgarde to GraalVM 21
- fix(RandomVarLengthStingGenerator): limit to 1000 character if there is no limit set in DB
- switch to official Microsoft SQL Server JDBC Driver
- further improvements docs

---

## 1.1.1

### Release Highlights

- improved way of handling databases with multi schema references

### Important Notes

- removed __includeTables="#all"__ tag, this is not necessary anymore, because there is a mechanism now to identify
  related schemas automatically based on JDBC metadata.

- **known Issue:** when you have two tables with identical name in different schemas, imported as Database connection in
  your benerator context, for example: tableA in schema1 and tableA in schema2 ...
  ```xml
  <database id="schema1" url="{dbUrl}" driver="{dbDriver}" schema="schema1" user="{dbUser}" password="{dbPassword}" />
  <database id="schema2" url="{dbUrl}" driver="{dbDriver}" schema="schema2" user="{dbUser}" password="{dbPassword}" />
  ```  
  ... the Benerator won't be able to identify the right table when it comes to persisting entities to database.

### Breaking Changes

N/A

---

## 1.1.0

### Release Highlights

- GraalVM Script engine support (https://www.graalvm.org/)
- support for case-sensitive database names and multi schema references (for supported databases)
- new MKDocs manual (https://www.benerator.de/ce/1.1.0/doc/)
- several new DemoCases to show how to use ...
  - multi schema databases with references into foreign schemas
  - case-sensitive database names like "cataloG"."Schema"."PersoN"
  - GraalVM Script engine

### Important Notes

- Repository shift to github.com

### Breaking Changes

- removed Oracle Nashorn and replace it with GraalVM

---

## 1.0.1

### Release Highlights

Fix logging (log4j config)

### Important Notes

src/main/resources/log4j.xml changed to src/main/resources/log4j2.xml

### Breaking Changes

N/A

---

## 1.0.0

Adoption of rapiddweller-benerator-ce by [rapiddweller](https://rapiddweller.com). Project was hard forked, updated and
tidied however no logical changes have occurred since databene-benerator v0.9.8 was released by Volker Bergmann.

### Release Highlights

- Project and dependency update to Java 1.8 and Jave 11
- Update shop demo cases to get started
- Repository shift to gitlab.com (Build pipeline, E2ET)

### Important Notes

N/A

### Breaking Changes

N/A

---

### Changes since 0.9.8

- Migration of [databene-benerator](https://sourceforge.net/projects/benerator/)
  to [rapiddweller](https://rapiddweller.com)
- Update project and dependencies to Java 1.8 and Jave 11
- Migrate and update databene dependencies (e.g. databene-commons -> rapidweller-common)
- Create automated build pipeline
- Create README, CHANGELOG, CONTRIBUTING, RELEASE pages
- Minor improvements of code
- Adjust unit tests to align with new java and dependency versions
- Add E2ET for several DB for shop demo
- Rename project from org.databene.benerator to com.rapiddweller.benerator

---

### Changes before v0.9.8

- Please see previous [releasenotes](/releasenotes) in the releasenotes project folder.

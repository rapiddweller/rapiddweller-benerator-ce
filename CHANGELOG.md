# Changelog

<!--lint disable no-duplicate-headings-->

## 3.0.0

### Release Highlights
- Greatly improved syntax checking, error handling and error reporting. Syntax errors are reported with line number and file name, get assigned a unique error id and the beneration process returns a related process return code 
- New demos (watermark,dbenv-old, dbenvconf, memstore, scriptdb, shop-hsqlmem-adv-sql-exporter, shop-mongodb, shop-postgres-mongodb)
- New domains 'logistic', 'shipping' and 'container' (Enterprise Edition)
- Postgres: UUID and JSON data type support
- Completed support of the ```<part>``` element for hierarchical data generation and manipulationwith minCount, MaxCount, countGranularity and countDistribution
- Increased JavaScript GraalVM execution performance by a factor of 10+
- AVRO support in Kafka (Enterprise Edition)
- MongoDB support
- new memstore functionality (removeNotExistingIds, sumEntityColumn, totalEntityCount)
- Introduced execution modes: strict, lenient, turbo
- New 'zoneddatetime' data type with generators CurrentZonedDateTimeGenerator and ZonedDateTimeGenerator. They can be configured with an individual 'zone' in the generator or a global 'defaultTimeZone) in the ```<setup>``` element
- Improved and extended the configuration mechanism for environment definitions
- Improved multithreading support, code quality, testing procedures, test coverage and documentation with unique error ids and process return codes
- Improved Benerator and Benchmark log output for core information, brevity and readability 
- New beneration performance sensor mechanism
- New 'condition' attribute in ```<variable>```
- New command line options for Benerator and Benchmark Tool:
  - **--list env** to list all environment definitions available in the current project  
  - ***--list db** to list all database definitions available in the current project
  - **--list kafka** to list all kafka definitions available in the current project
- New command line option for cache invalidation: --clearCaches
- Addressed log4j-caused security issues

### Breaking Changes
- Requiring count or maxCount in ```<generate>```, supporting count="unbounded" means unlimited count
- Redesigned platform and domain definition
- All built-in platforms are imported by default
- Renamed class DBSystem to AbstractDBSystem
- Custom generators that inherit from GeneratorWrapper must explicitly overwrite the methods isThreadSafe() and isParallelizable()
- Console output format changed
- Redesigned XML parsing to exactly match the XML schema definition

### Important Notes
- The environment definition file format used in earlier versions is still supported but its support will be dropped in a future release.

#### Domains
- General: Allowing to load all domain generators by their simple name (calling the default constructor)
- Address:
  - Added properties to address generation: 'street2', 'companyName' and 'department'
  - Added population to US states
- Person: New DIVERSE Gender

#### Platforms:
- mongodb platform: Alpha-stage contribution of [Daniel Figia](https://github.com/DanielFGA)
- kafka **([Enterprise Edition](https://www.benerator.de))**:
  - New AVRO support
  - Added schema.url for AVRO schema lookup 
  - Added idle.timeout.seconds to make a kafka-importer stop automatically after a certain number of seconds without message reception.
  - Improved Kafka benchmark
- db (Database)
  - Supporting UUID and JSON types of Postgres
  - Improved support of dynamic ```<database>``` selectors
  - Default transaction isolation level is set to 'read committed'
- csv, fixedwidth, xml, dbunit: Evaluate contained scripts if configured by 'sourceScripted' or 'defaultSourceScripted' settings
- fixedwidth: Allowing for row format configuration of fixed with file in Benerator setup

#### Redesigned
- Parsing, error checking, exception handling and syntax error reporting
- Requiring count or maxCount in ```<generate>```, supporting count="unbounded"
- Removed dependency of Benerator from ContiPerf

### Components
- Added 'lang' attribute to ```<echo>``` element for specifying the script language used for the message text
- Using relative URLs in TemplateFileEntityExporter
  - ```<execute>``` and ```<evaluate>```: Assuming type="shell" if a 'shell' attribute is set
- Added 'offset' and 'maxLogs' feature to LoggingConsumer
- Improved JavaHash to always crate a hex string of 8 characters
- Created 'Append' converter which appends a string to its input texts
- New Converters 'Mask', 'MiddleMask' and 'CutLength'
- 'FileNameGenerator' got a 'pathType' which can be configured to generate 'absolute', 'canonical' or 'local' file names

#### Benchmark Tool
- Improved benchmark definition for shorter execution times
- Allowing to use both --ce and --ee at the same time
- Execution of a single benchmark by command line
- Restricting file generation to 1GB
- --list prints out a list of all available benchmarks
- New benchmark 'gen-big-entity.ben.xml'
- Rebalanced database benchmarks for more reads than writes
- Exporting benchmark results in file formats CSV, XLS and TXT

#### Bug Fixes
- 'time' data type support
- Regex based string generation for optional groups
- Missing fields in benerator.xsd
- 'memstore' ignores entity identity
- Granularity in number generators
- Expression evaluation issue in mapping of XML attributes to bean properties
- Syntax error for onError='ignore'
- Heap overflow on H2 varchar with unspecified length
- Postgres blocking table after query
- Entity comparison fails
- PartModifier: Nested Entities are ignored
- Exception on missing houseNumber
- Bug fix for granularity >= 1
- handling of default country/dataset
- thread capability checking
- Handling of empty ```<echo/>``` elements
- Errors in parsing stages are not logged
- MemStore.update()
- Shell invocation dos not resolve environment variables
- Quote escaping in CSVEntityExporter fails
- MemStore.queryEntities() returns all duplicates as only one Entity
- Mask fails for binary database columns
- Undefined component types
- NPE in multithreaded execution
- environment properties file not found
- For multiple database setups, meta data cache always writes to the same file
- If data is iterated and a converter is applied, then the input data is first overwritten then converted
- Parent type fields are not generated
- Regex based string generation for optional groups
- NPE when querying last element in GeneratorIterator
- 'minExclusive' and 'maxEclusive' settings are ignored
- Default constructor of RandomBigDecimalGenerator creates an illegal setup
- RandomBigDecimalGenerator refuses granularity of 1

#### Depenency Updated
- database_derbyclient from 10.15.2.0 to 10.7.1.1
- database_h2 from 1.4.200 to 2.1.210
- database_icu4j from 67.1 to 71.1
- database_jaybird from 4.0.1.java11 to 4.0.6.java11
- database_mssqldb from 9.2.1.jre11 to 11.1.2.jre11-preview
- database_mysql from 8.0.20 to 8.0.29
- database_oracle from >21.1.0.0 to 21.6.0.0.1
- database_postgresql from 42.2.23 to 42.4.1
- database_mongodb (new) 3.12.11
- antlr-runtime from 3.3 to 3.5.3
- xml-apis from 1.4.01 to 1.0.b2 (1.0.b2 is newest version)
- slf4j from 1.7.32 to 1.7.36
- log4j from 2.14.1 to 2.18.0
- scriptengine_freemarker from 2.3.20 to 2.3.31
- scriptengine_graalvm from 21.1.0 to 22.2.0


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
- Add new [Hash Converters](/doc/component_reference.md#Default Converters)
- Add Benchmark Tool and [Performance Tuning](/doc/performance_tuning.md) section in Manual
- Extend PersonGenerator with attribute age
- Update Graalvm to 21.1.0
- Improved and extended Benerator Manual

### Important Notes

- Redesigned the Distribution class hierarchy, introduced the AbstractDistribution
  and DetachedSequence classes to compensate this for implementors.


### Breaking Changes

- See the manual's
  [Migration Guide](/doc/migrating_benerator_projects.md#migration-from-11x-to-300)
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

- Please see previous releasenotes for details on sourceforge net repository

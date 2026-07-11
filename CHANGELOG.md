# Changelog

<!--lint disable no-duplicate-headings-->

## 4.0.1

### Overview
Converter fixes for constructs that converted cleanly and then either produced wrong data with no error
at all, or hard-crashed the run on a descriptor that had parsed and converted fine — both are variants of
the same failure mode a migration tool must avoid: nothing about the CONVERSION step tells you it's
wrong. All were found by converting real-world-shaped patterns the gated corpus did not previously
contain (aged Benerator idioms: memstore joins, weighted-value literals, unique CSV sampling, non-ISO
date bounds, nested ids); the corpus now contains all of them, so they are re-verified against the real
DATAMIMIC CE engine on every commit. One (date bounds, below) was already broken in the shipped 4.0.0
converter for the single most common way Benerator projects write a date range — not something this
release introduced. One (nested ids, below) is a correction to this release's OWN earlier `<id>` fix,
found by cross-checking against DATAMIMIC's own authoring documentation
(`AGENTS.md`/`cheatsheet.md`) in the engine's development repository, then confirmed against both real
engines side by side.

### Fixed
- **The memstore entity binding is no longer dropped.** A store-reading `<variable source="mem"
  type="customer">` names the ENTITY to read, not a field type. The `type` was silently discarded, so
  DATAMIMIC resolved the lookup against the VARIABLE name, found nothing (`Data having entity 'cust' is
  empty in memstore`) and produced unresolved references. It now converts to `sourceEntity="customer"`,
  the same rule the store-reading `<iterate>` already followed.
- **`<id type="int">` keeps its incremental semantics.** Benerator's `<id>` is an incremental unique id
  (1, 2, 3, …); a bare DATAMIMIC `<id type="int">` is a plain random int that repeats. A mode-less
  integer `<id>` now converts to `generator="IncrementGenerator()"`. Three descriptors in Benerator's own
  demo suite were affected. Verified unique across 10,000 records spread over 10 DATAMIMIC pages.
- **Weighted-value literals (`values="'A'^70,'B'^30"`) no longer crash the run.** This is Benerator's
  documented `randomFromWeightLiteral` syntax — the weight rides inside the `values=` string. DATAMIMIC
  has no `^` syntax: parsing the caret expression as a literal raises immediately at task startup, so a
  verbatim pass-through hard-crashed every converted descriptor using it. The converter now splits it into
  DATAMIMIC's native `values=`/`weights=` pair. Confirmed against the real engine: a 20-value draw at
  40/35/25 produced RETAIL 9, SME 8, CORP 3.
- **`unique="true"` on a weighted-CSV `<attribute source>` is flagged instead of crashing.**
  DATAMIMIC's `<key source>` reads a `.wgt.csv` WITH replacement and explicitly rejects `unique` at
  task-init, so this combination also converted clean and crashed on first run. A safe rewrite needs the
  CSV's column header (which the converter never reads), so - consistent with the converter's
  honest-reporting design - it is flagged with a concrete rewrite recipe (`<variable source unique="true">`
  + a script picking the value column) rather than guessed or silently passed through to the crash. See
  `MIGRATION_PLAYBOOK.md#unique-weighted-source`.
- **A bare ISO date `min`/`max` (no time component) on a `type="date"` field no longer crashes.**
  DATAMIMIC's `DateTimeGenerator` parses `min`/`max` with a FIXED `"%Y-%m-%d %H:%M:%S"` format; Benerator's
  own default date-bound format (used whenever no `pattern` is given) is plain `"yyyy-MM-dd"` — the
  single most common way real projects write a date range, e.g. `min="1970-01-01"`. This combination was
  **already broken in the shipped 4.0.0 converter**, unrelated to any other change in this release. Bounds
  are now reparsed at CONVERT TIME with Benerator's own format (its default, or an explicit `pattern`) and
  re-emitted in DATAMIMIC's expected format. This also fixes a second, related bug: Benerator's `pattern`
  on a date field is the `SimpleDateFormat` used to parse `min`/`max` — a completely different thing from
  DATAMIMIC's `pattern`, which is *always* a regex for string generation regardless of `type=`. Passed
  through verbatim, a European date pattern like `dd.MM.yyyy` silently hijacked the field into
  regex-generated garbage instead of a date (confirmed against the real engine: output like
  `"ddFMM9yyyy"`). `pattern` is now consumed when parsing date bounds and never reaches DATAMIMIC on a
  date-typed field; a genuinely string-typed `pattern` (regex generation) is untouched.
- **`unique="true"` on a regex `pattern`, a native min/max range, or a `generator=` is flagged instead of
  crashing.** DATAMIMIC validates `unique` at the model level: it draws distinct values from a FINITE
  POOL, which only `values=` or `source=` provide. A pattern/range/generator field converts clean and then
  hard-crashes Pydantic validation (`'unique' requires 'values' or 'source'`) — confirmed against the real
  engine, and hit for real by two fields in Benerator's own EDI test fixture (unique order/booking codes
  via regex `pattern`, a very ordinary idiom). No safe universal rewrite exists (a numeric range can be
  huge; a regex's value set isn't enumerable in general), so `unique` is dropped and flagged — the field
  still generates, just no longer guaranteed distinct — rather than passed through to the crash. A
  `values=`-backed field is untouched; DATAMIMIC natively supports unique sampling there — UNLESS it also
  names an explicit non-random `distribution` (`ordered`/`cumulated`/...), which hits a second, distinct
  crash ("'unique' only combines with distribution='random'"), also confirmed against the real engine and
  also handled the same way: `unique` dropped and flagged, the explicit `distribution` kept.
- **A nested `<id>` inside a `<part>` now flags a real value-level divergence this release's own earlier
  `<id>` fix (above) did not account for.** Benerator's `<id>` is GLOBALLY incremental across the whole
  run, including every invocation of an enclosing `<part>` — confirmed against the real Benerator engine:
  3 parent records × 2 children each gives child ids 1,2 / 3,4 / 5,6. DATAMIMIC's `IncrementGenerator`
  resets to 1 for every PARENT record inside a `nestedKey` instead — confirmed against the real DATAMIMIC
  engine on the identical shape: 1,2 / 1,2 / 1,2 — and is documented, intentional DATAMIMIC behavior
  (`AGENTS.md`/`cheatsheet.md` rule DM315: "IncrementGenerator counts per parent"), not something to
  "fix" in DATAMIMIC. Per-parent-local ids are frequently exactly what a child list wants, and a safe
  automatic global rewrite doesn't exist (it would need a multiplier bound above any realistic per-parent
  count, which the converter cannot know), so this is not dropped or changed — only flagged, under its
  own `migration-summary.md` section ("Verify nested id uniqueness"), with the composite-key recipe
  DATAMIMIC's own docs recommend. Scoped to `<part>` specifically after checking the other nesting shape
  DM315 also names, a `<generate>` nested inside a `<generate>`: there, BOTH engines reset the id per
  parent (confirmed on real Benerator: 1,2 / 1,2 / 1,2; confirmed on real DATAMIMIC: identical) — a
  separate nested `<generate>` is its own independent product/consume cycle in Benerator, unlike a
  `<part>` sub-structure sharing the enclosing entity's generator tree, so no divergence and no flag
  needed there.

### Also verified, no gap found
Investigated as plausible silent-corruption candidates and confirmed CORRECT against the real engine, so
no converter change was needed:
- `<part minCount="N" maxCount="M">` → `<nestedKey type="list">`: DATAMIMIC honors both bounds and
  varies the list length per record (measured: 30 records, lengths spread across the full 1–5 range).
- `<generate threads="N">` → `numProcess="N"` combined with the new `IncrementGenerator()` id fix:
  ids stay unique with zero duplicates across multiple worker processes (measured: 20,000 records over 4
  processes).
- A nested `<generate>` referencing an ancestor 3+ levels up, and a nested field name that shadows an
  ancestor's type name: DATAMIMIC resolves ancestor scopes by their original name at every depth, so both
  resolve correctly even where the converter's own scope-rewrite only rewrites the immediate parent/root.
- A `type="timestamp"`/`"datetime"` `min`/`max` WITH an explicit time-of-day `pattern` (e.g.
  `pattern="yyyy-MM-dd HH:mm:ss"`) round-trips correctly, time-of-day included (measured: bounds
  08:00–18:00 held across 10 generated timestamps). Benerator's OWN default date-bound parser
  (`DescriptorUtil.getPatternAsDateFormat`) silently truncates any time-of-day component when no
  `pattern` is given, for `date` AND `timestamp` alike (verified: `SimpleDateFormat("yyyy-MM-dd")` parses
  `"2020-01-01 12:30:00"` to midnight without error) — the converter's no-pattern fallback reproduces this
  faithfully rather than introducing a new divergence.
- `separator` (CSV column separator) and `constant` (literal passthrough) — swept the rest of
  `FIELD_ATTR_KEEP` for the same verbatim-passthrough risk class as the fixes above; both engines agree on
  these unconditionally, no format-translation boundary exists to diverge on.
- Benerator has no `<setup>`-level seed/reproducibility attribute at all (confirmed: zero matches for
  "seed" anywhere in the descriptor schema or docs beyond unrelated generator class names like
  `SeedWordGenerator`) — so DATAMIMIC's `rngSeed` (which forces single-process execution) has nothing to
  receive from a converted descriptor and never conflicts with the converter's `threads=`→`numProcess=`
  mapping. Checked because DATAMIMIC's own authoring docs (`AGENTS.md`) flag seeded-vs-multiprocess as a
  common authoring mistake; not a converter gap since there is no Benerator source attribute to translate.

### Added
- **`migration-summary.md` flags row-count risk.** Benerator tolerates drawing more values from a source
  than it holds; DATAMIMIC reads a source once and stops, so a `<generate>` requesting more records than
  its source silently emits fewer. This is an engine default difference, not a conversion error (`cyclic`
  converts verbatim), but it is invisible without counting rows — so every `<variable>`/`<reference>` read
  that declares no `cyclic` is now counted under a "Verify your row counts" heading.
- The memstore cross-entity reference (`memstore.ben.xml`) joins the CI round-trip corpus, so both fixes
  are asserted against the real DATAMIMIC CE engine on every commit.

## 4.0.0

### Overview
The migration release. Benerator 4.0.0 ships a built-in **Benerator → DATAMIMIC converter** that
translates whole projects — descriptors, data files, SQL scripts, DbUnit datasets, and environment
properties — into native DATAMIMIC descriptors, with a per-file report of everything that needs a
manual decision. Benerator itself is unchanged and stays in maintenance mode; this release exists to
give every Benerator project a tested, supported path onto rapiddweller's actively developed platform.

### The converter (#501, #512, #518, #519)
- **One command converts a whole project:**
  `java -cp benerator.jar com.rapiddweller.benerator.main.datamimic.DatamimicConverter <project> <out> report.txt`.
  See the README section "Migrate a project: step by step".
- **Broad construct coverage:** generate/iterate nesting, references (including FKs resolved against
  the target table's real primary key from the executed DDL), weighted and entity CSVs, fixed-width
  files (read and write), DbUnit datasets (split into per-table JSON sources), dynamic FTL includes,
  per-record SQL and MongoDB selectors, DB sequences (`DBSequenceGenerator` →
  `SequenceTableGenerator(sequence=…)`), memstore scripting, and environment-properties migration
  (JDBC URLs → host/port/database/dbms).
- **Honest reporting instead of silent misconversion:** every construct without a faithful DATAMIMIC
  equivalent is flagged in `migration-summary.md`, with recipes in `MIGRATION_PLAYBOOK.md`.
- **Continuously verified:** CI converts the complete Benerator demo suite and runs it through the
  real DATAMIMIC engine on every commit, including full round-trips against live PostgreSQL and
  MongoDB — the flagship `shop` demo passes its own row-count assertions on both.
- **Validated on production-scale input:** a real 42-descriptor project (3 schemas, DB sequences,
  memstore, JS scripts) converts with roughly 93% of constructs handled automatically; the remainder
  is reported for a manual pass.

### Known migration limitations
- Inline JavaScript (`<execute type="js">`, `{js:…}` scripts) must be rewritten in Python — DATAMIMIC
  scripting is Python.
- Columns typed only in the live database (Benerator's DB-metadata introspection) need explicit
  `type=`/`generator=` unless the descriptor itself executes the DDL.
- `<run-task>`, selector-only references without a `targetType`, and Benerator's
  `ScriptedEntityExporter` (FTL-templated output) have no direct equivalent and are flagged.

## 3.3.0

### Overview
A maintenance and hardening release. It modernises the build and release pipeline after the OSSRH
shutdown, refreshes dependencies for security, pulls in the rapiddweller library updates (including a
dependency-ordering fix for complex database schemas), and corrects documentation.

### Improvements
- Migrated Maven publishing from the retired OSSRH to the Central Portal (#468); pinned
  maven-gpg-plugin to 3.1.0 and added a fail-fast signing-key check (#466).
- Bumped GitHub Actions off deprecated Node 16/20 runners (#464).
- Clearer GraalVM multi-threading error messages.
- Extended the scripting demo to show using Java classes from JS functions.
- Address generation can be restricted to a region or city directly via `AddressGenerator`'s
  `stateFilter`/`cityFilter`, instead of over-generating the whole country and trimming afterwards
  (#481).

### Bug Fixes
- Fixed `Incomplete nodes left` when initialising a database adapter against a complex schema
  (DependencyModel stranded incomplete nodes in a single post-processing pass), via rd-lib-common
  2.1.0 (#474).
- Corrected the faker domain documentation: the generator class is `DataFakerGenerator`, not the
  removed `FakerGenerator`, and the topic/property tables now match the bundled datafaker (#454, #475).
- Fixed a CI build failure caused by the buildnumber plugin querying git inside the build container
  ("detected dubious ownership") (#482).
- Fixed handling of a null quota (#463).

### Dependency Updates
- rd-lib-common 2.1.0-jdk-11 (#474) and rd-lib-jdbacl 1.1.18-jdk-11 (aligned to common 2.1.0 and the
  same DB driver versions), log4j 2.26.0 (#473), mysql-connector-j 9.7.0 (#480), mssql-jdbc (#471),
  and patched vulnerable DB drivers and libraries (#465).
- Build plugins: maven-site-plugin 3.22.0 (#479), buildnumber-maven-plugin 3.3.0 (#470),
  maven-surefire and surefire-report plugins (#472, #469).

---

## 3.2.1

### Overview
Hotfix for Error BEN-0243: Domain not found: address after upgrading to v3.1.0 #423

### Bug Fixes
- Fail to import domain when exec "java -jar" #426

---

## 3.2.0

### Overview
This release introduces significant enhancements, including a new installation option for Mac users, enriched datasets, improved compatibility, and several key bug fixes. We've also made improvements under the hood with updated dependencies and more reliable scripting with GraalVM.

### New Features
- **Mac Installation Option**: Homebrew formula 'benerator' now available.
- **Enriched Datasets**: Enhanced region-specific functionality for diverse datasets (#413).

### Improvements
- **Default Values in Generators**: All generators now have default values applicable directly to an attribute (#414).
- **Vertica Compatibility**: Improved Benerator's compatibility with Vertica database (#394).
- **Dependency Updates**: Updated numerous dependencies to enhance performance and security.
- **GraalVM Script Implementation**: Enhanced for more reliable dynamic migration between Polyglot and Benerator Context.

### Bug Fixes
- Fixed an issue where the benerator-wizard didn't map SQL types correctly in database projects (#379).
- Addressed problems in the db archetype's functionality and performance (#386).
- Resolved TypeNotFoundException when updating MongoDB collection entries (#395).
- Fixed NullPointerException occurring when iterating from an empty CSV file (#405).

---

## 3.1.0

### Release Highlights
- integrated faker library for data generation as FakerGenerator 
- extend postgres dialect make geo data processable 

#### Bug Fixes
- #350 Support geometry data type for Postgres (postgis extension)

#### Depenency Updated
- downgrade slf4j-api to versions 1.7.36
- database_mssqldb to 12.2.0.jre11
- database_jaybird to 5.0.1.java11
- database_icu4j to 72.1
- database_oracle to 21.8.0.0
- database_postgresql to 42.5.3
- database_mariadb to 3.1.1
- dependency_log4j to 2.19.0
- scriptengine_freemarker to 2.3.32
- scriptengine_graalvm to 22.3.1
- _rd_jdbacl_version to 1.1.14-jdk-11

---

## 3.1.0

### Release Highlights
 - authDb and authMechanism for mongodb platform is configurable now
 - db functions to set actual timestamp or date is handled correctly now

#### Bug Fixes
 - #329 Default value of 'now()' in date column causes error
 - #354 Project wizard generated file returns Error BEN-0205 when run


---

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

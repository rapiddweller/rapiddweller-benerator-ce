# Benerator → DATAMIMIC converter — coverage & gaps

Measured by `CorpusSweepTest`, which runs the converter over the whole Benerator demo + test corpus
(`src/demo/resources/demo` + `src/test/resources/com/rapiddweller`), tallies every construct flagged
for manual migration, and **gates the totals against `gap-baseline.properties`** (a regression fails
the build; lowering the baseline is a deliberate commit). All numbers below come from the generated
`target/gap-report.txt` — do not edit them by hand, re-run the sweep.

## Coverage (sweep of 2026-07-02)

**303 files · 298 convert · 5 throw** (the 5 are non-standalone / deliberately-malformed XML
fixtures, not descriptors). No descriptor fails to produce output.

The report is tiered: `report.add(...)` = genuinely needs manual work; `report.info(...)` = converted
automatically, shown for transparency only (dropped `<import>`, a `<reference>` emitted as a `<key>`, a
defaulted `sourceKey`, an assertion converted to `<assert>`, an inlined generator `<bean>`,
`<variable generator>` → entity). Across the full corpus: **418 need manual attention, 346 informational**.

The 418, by report kind (top constructs in parentheses):
- **attribute** 94 (`<attribute>` 43, `<part>` 20, `<setup>` 17): unmapped attributes on mapped elements.
- **element** 65 (`<bean>` 33, `<value>` 11, `<pre-parse-generate>` 8, `<transcodingTask>` 5): no equivalent.
- **execute** 57 (`type='js'` 40, no-type 14): no JS engine in DATAMIMIC — rewrite python/sql/bash.
- **database** 42 (`'db'` 24 + schema/env variants): env-specific connection setup — flagged by design.
- **generator** 40 (`MongoDBObjectIdGenerator` 9, `AddressGenerator` 9, `new RegexStringGenerator{…}` 4):
  brace-arg generators outside `<variable>` + genuinely unsupported generators.
- **reference** 39 (`order_id` 16, `'x'` 8, `'ref'` 7): references without a `targetType` (selector-only,
  untyped) — column/selector must be migrated manually.
- **consumer** 28 (`NoConsumer` 11, `MultiExporter` 7, `new XLSEntityExporter(…)`): exporters with no
  DATAMIMIC counterpart or inline Java instantiation.
- **condition `<if>`** 22 (setup-level with a non-`<error>` body) + **`<evaluate>` without `assert`** 14:
  side-effect control flow with no DATAMIMIC home.
- **converter** 12, **type** 5: long tail.

### Now mapped

| Benerator | → DATAMIMIC | flags closed |
|---|---|---|
| inline `<execute type="sql"/"shell">` | inline `<execute type="sql"/"bash">` | — |
| inline `<execute target="db">` (no type) | `<execute type="sql">` (SQL against the store) | ~58 |
| `<while test>` | `<while condition>` (+ `maxIterations`) | 1 |
| `<setting>` / `<property>` name+value | `<variable name constant\|script>` | ~108 |
| `consumer=` attr AND `<consumer class="X">` element | `target="…"` (comma-split, per-entry; FQN → simple name) | ~211 |
| `<mongodb>` | native `<mongodb>` (connection copied) | 23 |
| `<if test="X"><error>MSG</error></if>` (assertion idiom, any level) | `<assert condition="not (X)" message="MSG"/>` | ~50 |
| `<evaluate assert="A" [target="db"]>BODY` | `<variable name="result" source/selector \| script>` + `<assert condition="A"/>` | ~22 |
| `<reference distribution="random/ordered/cumulated" cyclic>` | passed through (native DATAMIMIC `<reference>` attrs) | ~34 |
| `consumer="db.updater()/deleter()/upserter()"` | `target="db.update/.delete/.upsert"`; `inserter()` → plain store | ~24 |
| `type="int/string" min/max/granularity/minLength/maxLength` | native `<key>` range attrs (no generator string) | — |
| `new X(...)` bean generators / converters | `X(...)` (prefix stripped); `RandomDoubleGenerator` → `FloatGenerator`; `CaseConverter` → `UpperCase` | — |
| `jdbc:h2/hsqldb:mem:` | SQLite (Python-connectable) | — |
| `environment.env.properties` (JDBC URL) | `conf/environment.env.properties` (host/port/database/dbms) | — |

Net over the session: `element` 178→65, and the DB-backed round-trip now runs against a real postgres.

## Remaining gaps (prioritised)

Per-construct manual-migration recipes (before/after snippets) live in [MIGRATION_PLAYBOOK.md](MIGRATION_PLAYBOOK.md).

### A. Expected — not real gaps (no action)
- **`dropped <import>`** (106): Benerator `<import>` is auto-discovery in DATAMIMIC — intentionally removed.
- **`database 'db'`** (24) / connection flags (42): the DB connection is environment-specific; flagged for
  manual `dbms=`/`environment=` setup by design.
- **`reference` notes** (`category_id`, `customer_id`, `order_id`, …): informational — the converter
  defaults `sourceKey="id"` and asks the user to verify the FK column. Not failures.

### B. Auto-mappable later (needs more converter work)
- **`{ftl:…}` / `{js:…}` / `{dbUrl}` placeholders**: Benerator FreeMarker/JS templating in attribute values
  (e.g. `<mongodb host="{ftl:${mongoHost}}">`) — resolve from the project's single env properties, flag
  when ambiguous (`--env` CLI option planned).
- **Composite-generator args** (`new PersonGenerator{minAgeYears=…}` 13): DATAMIMIC already accepts
  `<variable entity="Person" ageMin=… ageMax=…>` and `entity="Person(min_age=…)"` — map the args.
- **`type` flags** (5 left): `type="object"`/`type="date"` structural conversion is done; remaining are
  odd aliases — extend `TYPE`/`GENERATOR_RENAME` as they surface.

### C. No DATAMIMIC equivalent (flag honestly — manual migration)
- **`<bean>`** (33): Benerator instantiates a Java bean; DATAMIMIC has no bean layer. Manual.
- **`<error>` outside the assertion idiom** / setup-level `<if>` with a non-`<error>` body (22): Benerator
  control flow at setup level. Manual.
- **`<pre-parse-generate>`** (8), **`<value>`** (11) inside settings, transcode/meta-model: rare
  Benerator-specific constructs. Manual.
- **inline `generator="new PersonGenerator{…}"` / `new RegexStringGenerator{…}`** (bean-style Java
  instantiation): kept verbatim + flagged — not valid DATAMIMIC generator syntax.
- **`consumer="MultiExporter"` / `new XLSEntityExporter(…)`** (~17): exporters without a DATAMIMIC
  counterpart — flagged for manual target config.
- **`<execute type="js">`** (40): DATAMIMIC has no JavaScript engine — rewrite in python/bash/sql.

## How to extend

Add a row to `VocabularyMap` (element/type/generator/consumer table) — the walk in `DescriptorConverter`
is data-driven off those tables. Structural conversions (object→nestedKey, date→generator) need a small
dedicated method like `convertReferenceNode`/`convertIfNode`. Re-run `CorpusSweepTest` to measure impact.

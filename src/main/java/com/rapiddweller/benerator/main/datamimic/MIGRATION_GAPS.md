# Benerator → DATAMIMIC converter — coverage & gaps

Measured by `CorpusSweepTest`, which runs the converter over the whole Benerator demo + test corpus
(`src/demo/resources/demo` + `src/test/resources/com/rapiddweller`) and tallies every construct flagged
for manual migration.

## Coverage

**301 files · 296 convert · 5 throw** (the 5 are non-standalone / deliberately-malformed XML
fixtures, not descriptors). No descriptor fails to produce output.

### Now mapped

| Benerator | → DATAMIMIC | flags closed |
|---|---|---|
| inline `<execute type="sql"/"shell">` | inline `<execute type="sql"/"bash">` | — |
| inline `<execute target="db">` (no type) | `<execute type="sql">` (SQL against the store) | ~58 |
| `<while test>` | `<while condition>` (+ `maxIterations`) | 1 |
| `<setting>` / `<property>` name+value | `<variable name constant\|script>` | ~108 |
| `consumer=` attr AND `<consumer class="X">` element | `target="…"` (comma-split, per-entry; FQN → simple name) | ~211 |
| `<mongodb>` | native `<mongodb>` (connection copied) | 23 |
| `<evaluate assert>` | dropped + flagged (assertion, no equivalent) | 34 |
| `type="int/string" min/max/granularity/minLength/maxLength` | native `<key>` range attrs (no generator string) | — |
| `new X(...)` bean generators / converters | `X(...)` (prefix stripped); `RandomDoubleGenerator` → `FloatGenerator`; `CaseConverter` → `UpperCase` | — |
| `jdbc:h2/hsqldb:mem:` | SQLite (Python-connectable) | — |
| `environment.env.properties` (JDBC URL) | `conf/environment.env.properties` (host/port/database/dbms) | — |

Net over the session: `element` 178→82, and the DB-backed round-trip now runs against a real postgres.

## Remaining gaps (prioritised)

### A. Expected — not real gaps (no action)
- **`dropped <import>`** (106): Benerator `<import>` is auto-discovery in DATAMIMIC — intentionally removed.
- **`database 'db'`** (60) / connection flags (103): the DB connection is environment-specific; flagged for
  manual `dbms=`/`environment=` setup by design.
- **`reference` notes** (`category_id`, `customer_id`, `order_id`, …): informational — the converter
  defaults `sourceKey="id"` and asks the user to verify the FK column. Not failures.

### B. Auto-mappable later (needs more converter work)
- **`type="object"`** (132): a Benerator nested entity/object field → DATAMIMIC `<nestedKey>` or a nested
  `<generate>`. Needs structural conversion (the field's own sub-`<attribute>`s become the nested body).
- **`type="date"`** (63): DATAMIMIC has no `date` *type* but has `DateGenerator`/`DateTimeGenerator`.
  Map `type="date"` (+ any `min`/`max`) → `generator="DateGenerator(...)"`.
- **`{ftl:…}` / `{js:…}` placeholders**: Benerator FreeMarker/JS templating in attribute values (e.g.
  `<mongodb host="{ftl:${mongoHost}}">`) — resolve from the demo's properties or flag.
- **`type="integer"`-style aliases** and generator-name casing: extend `TYPE` / `GENERATOR_RENAME` as new
  ones surface.

### C. No DATAMIMIC equivalent (flag honestly — manual migration)
- **`<bean>`** (50): Benerator instantiates a Java bean; DATAMIMIC has no bean layer. Manual.
- **`<error>`** (49): Benerator control-flow error element. Manual.
- **`<pre-parse-generate>`** (8), **`<value>`** (11) inside settings, transcode/meta-model: rare
  Benerator-specific constructs. Manual.
- **inline `generator="new PersonGenerator{…}"` / `new RandomDoubleGenerator(…)`** (bean-style Java
  instantiation): kept verbatim + flagged — not valid DATAMIMIC generator syntax.
- **`consumer="db.updater()"` / `mongo.inserter(…)` / `MultiExporter`** (~29): DATAMIMIC target semantics
  differ (update vs insert, multi-target) — flagged for manual target config.
- **`<execute type="js">`** (40): DATAMIMIC has no JavaScript engine — rewrite in python/bash/sql.

## How to extend

Add a row to `VocabularyMap` (element/type/generator/consumer table) — the walk in `DescriptorConverter`
is data-driven off those tables. Structural conversions (object→nestedKey, date→generator) need a small
dedicated method like `convertReferenceNode`/`convertIfNode`. Re-run `CorpusSweepTest` to measure impact.

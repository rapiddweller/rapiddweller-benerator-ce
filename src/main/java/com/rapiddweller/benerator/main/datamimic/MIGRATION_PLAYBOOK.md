# Benerator → DATAMIMIC migration playbook

Manual-migration recipes for every construct the converter deliberately does not translate
(see `MIGRATION_GAPS.md`, section C). The converter's `TODO(datamimic-migration)` comments and the
`migration-summary.md` written after a batch run link into the section anchors below — keep the
headings stable.

DATAMIMIC elements that DO exist and carry most of the load here: `<assert>`, `<condition>`/`<if>`
inside `<generate>`, `<while condition maxIterations>`, `<variable source/selector>`,
`<reference distribution/cyclic>`, `target="db.update/db.upsert/db.delete"`, `<memstore>`, and the
CSV/JSON/XML/Console/Log exporters.

## bean

Benerator `<bean id class|spec>` instantiated an arbitrary Java object (generator, converter, task)
that later elements referenced by id. Beans whose `spec` names a generator DATAMIMIC knows are
already inlined automatically (info finding, no work); only class-based or unknown beans flag here.
DATAMIMIC has no Java bean layer: express the value natively (generator string, `pattern=`,
`values=`, `entity=`), or port the Java logic to `<execute type="python">`.

```xml
<!-- before -->
<bean id="idGen" class="com.my.MyIdGenerator"/>
<attribute name="code" generator="idGen"/>
<!-- after -->
<key name="code" generator="IncrementGenerator(1000)"/>
```

## execute-js

`<execute type="js">` ran inline JavaScript at setup time (seed settings, mutate state, glue code).
DATAMIMIC has no JavaScript engine. Rewrite the snippet as `<execute type="python">` (same lifecycle
position, full stdlib), as `type="sql"` when the JS only issued SQL through a store, or as
`type="bash"` for shell-style glue. Pure sanity checks belong in `<assert>` or a test instead.

```xml
<!-- before -->
<execute type="js">var limit = base * 2;</execute>
<!-- after -->
<execute type="python">limit = base * 2</execute>
```

## setup-if

A setup-level `<if test>` with a non-`<error>` body is Benerator control flow outside any
generation loop. (The `<if test><error>MSG</error></if>` assertion idiom converts automatically to
`<assert>`.) DATAMIMIC's `<condition>`/`<if>` lives inside `<generate>` only. Either move the
conditional into the `<generate>` it guards, or port a guard-style check to
`<execute type="python">` with a `raise`, or drop it when it only picked an environment (use env
properties for that).

```xml
<!-- before -->
<if test="stage == 'prod'"><setting name="count" value="1000000"/></if>
<!-- after (inside the generate) -->
<condition><if condition="stage == 'prod'">...</if></condition>
```

## evaluate-without-assert

`<evaluate>` with `assert=` converts automatically (`<variable>` + `<assert>`). Without `assert=`
it either ran a statement for its side effect or bound a result (`id="x"`). Side effect → inline
`<execute type="sql">` (or `python`). Result binding → `<variable name="x" source="db"
selector="..."/>` for SQL, `<variable name="x" script="..."/>` otherwise. A pure verification query
is often better ported into a test than into the descriptor.

```xml
<!-- before -->
<evaluate id="cnt" target="db">select count(*) from orders</evaluate>
<!-- after -->
<variable name="cnt" source="db" selector="select count(*) from orders"/>
```

## value

`<value>` supplied positional (unnamed) column values for array-style entities, e.g. rows written
to CSV without a header model. DATAMIMIC entities are name-based: give every column an explicit
`<key name=...>` — the exporter then writes them in declaration order, which reproduces the
positional layout.

```xml
<!-- before -->
<generate type="row" count="10"><value constant="A"/><value type="int" min="1" max="9"/></generate>
<!-- after -->
<generate name="row" count="10" target="CSV">
  <key name="col1" constant="A"/><key name="col2" type="int" min="1" max="9"/>
</generate>
```

## pre-parse-generate

`<pre-parse-generate>` generated data *before* the rest of the descriptor was parsed, so the
descriptor itself could depend on the generated artifacts (typically XML-Schema-driven generation).
DATAMIMIC parses the whole descriptor up front and has no pre-parse phase. Split the model into two
descriptors and run them in sequence, or restructure so the dependent part reads the first stage's
output via `<iterate source=...>`.

## transcoding-meta-model

`<transcodingTask>`/`<transcode>`/`<meta-model>` copied consistent subsets of a source database into a target
database, renumbering primary and foreign keys from the JDBC meta-model. DATAMIMIC has no
meta-model-driven transcoder. Rebuild the copy explicitly: one
`<iterate source="sourcedb" type="table" target="targetdb">` per table, re-keying FKs with
`<reference>` (or `<variable source/selector>` lookups) — or use a dedicated ETL tool for large
schemas. This is the most manual of all the gaps.

## no-equivalent-consumer

Consumers the converter cannot turn into a `target=`:

- `NoConsumer` is actually mappable: `VocabularyMap.CONSUMER_TARGET` maps it to the empty target,
  and the converter already emits `target=""` (generate-and-capture-only — valid DATAMIMIC). The
  flag only asks you to confirm "no output" is intended; usually accept the output as-is.
- `MultiExporter(a, b)` → DATAMIMIC targets are a comma list anyway: `target="CSV,ConsoleExporter"`.
- `new XLSEntityExporter(...)` → no XLS exporter in DATAMIMIC CE: use `target="CSV"` and convert,
  or write XLSX from `<execute type="python">` (openpyxl/pandas).
- Any other inline `new SomeExporter(args)` Java instantiation → pick the closest declarative
  exporter (CSV/JSON/XML) or drop the output.

## unknown-generators

Generators not in DATAMIMIC's registry are kept verbatim and flagged (`MongoDBObjectIdGenerator`
is the most common). Recipes:

- `MongoDBObjectIdGenerator`: a mongodb target assigns `_id` automatically on insert — usually just
  delete the attribute. If the value must exist in the model, `<key name="_id" pattern="[0-9a-f]{24}"/>`.
- `new RegexStringGenerator{pattern='...'}` → the native attribute: `<key name="x" pattern="..."/>`.
- Anything else: check `VocabularyMap.KNOWN_GENERATORS` for a rename, otherwise replace with
  `pattern=` / `values=` / `script=`, or port the logic to `<execute type="python">`.

## reference-selector-type

A Benerator `<reference>` without `targetType` (selector-only or untyped) cannot be auto-mapped:
DATAMIMIC's `<reference>` needs the referenced table (`sourceType`) and column (`sourceKey`)
explicitly. Fill them in by hand. A `selector=`-driven reference becomes a
`<variable source="db" selector="..."/>` plus a `<key script="...">` pick, and
`distribution="random|ordered|cumulated"` / `cyclic` are native on the DATAMIMIC `<reference>`.
`type=`, `nullQuota=`, `mode=`, `offset=` have no reference equivalent — dropped and flagged.

```xml
<!-- before -->
<reference name="order_id" selector="select id from orders where state='OPEN'"/>
<!-- after -->
<variable name="open_order" source="db" selector="select id from orders where state='OPEN'"/>
<key name="order_id" script="open_order.id"/>
```

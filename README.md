<p align="center">
  <img width="180"
       src="https://github.com/rapiddweller/rapiddweller-benerator-ce/blob/development/doc/assets/benerator-icon.png"
       alt="Benerator">
</p>

<p align="center"><em>Benerator: model-driven test data generation. Now in maintenance mode.</em></p>

# rapiddweller-benerator-ce

[![Status: maintenance mode](https://img.shields.io/badge/status-maintenance%20mode-yellow.svg)](#still-on-benerator-existing-users)
[![CI](https://github.com/rapiddweller/rapiddweller-benerator-ce/actions/workflows/ci.yml/badge.svg)](https://github.com/rapiddweller/rapiddweller-benerator-ce/actions)
[![License: GPLv2 with exceptions](https://img.shields.io/badge/license-GPLv2%20with%20exceptions-blue.svg)](license.txt)
[![Java 11](https://img.shields.io/badge/Java-11-blue.svg)](#still-on-benerator-existing-users)
[![New projects: use DATAMIMIC](https://img.shields.io/badge/new%20projects-use%20DATAMIMIC-brightgreen.svg)](https://github.com/rapiddweller/datamimic)

> **Maintenance mode.** Benerator CE is maintained for bug fixes and security updates only. No new features are planned, and it stays free and open. Existing projects keep working. For new projects, **DATAMIMIC**, rapiddweller's actively developed platform, is the recommended path. This README shows you what you gain and how to move, and the team offers support with the migration.

---

## Editions

- **Benerator Community Edition (CE):** this repository. Maintained for bug fixes and security; open source (GPL v2 with exceptions). We are happy to help with issues and with migration to DATAMIMIC.
- **Benerator Enterprise Edition (EE):** discontinued. No longer sold or supported.
- **Need an enterprise platform?** That is DATAMIMIC. It comes as a free Community Edition and a commercial Platform (Enterprise) offered under a platform partnership. See https://datamimic.io.

---

## Why move to DATAMIMIC

DATAMIMIC is rapiddweller's own platform, carrying the model-driven test-data approach forward on a modern stack. Moving gives you:

- **Deterministic, reproducible output.** The same model and seed produce the same data, reproducible across machines, with a per-output content hash you can use as audit evidence.
- **A familiar approach, modern foundation.** If you have worked with model-driven generation, DATAMIMIC will feel familiar: XML descriptors and first-class Python, so you can bring your own Python as generators, converters, and validators.
- **Active development and regular releases**, not maintenance-only.
- **Install and run in CI.** `pip install datamimic_ce` and drive it from the CLI — it slots into Jenkins, GitLab CI, or any Python pipeline, the same place your Benerator step runs today. CE connects to PostgreSQL, Oracle, MySQL, MS SQL Server, SQLite, and MongoDB, plus CSV/JSON/XML files, and ships an MCP server so agentic workflows can use it as a deterministic data-generation tool. Streaming targets (Apache Kafka, RabbitMQ) are in DATAMIMIC EE; a REST API and scheduler for server-driven pipelines are in DATAMIMIC Platform.
- **On-premise and air-gapped deployment**, no telemetry and no call-home, for regulated environments.

Get started:

- GitHub: https://github.com/rapiddweller/datamimic
- Documentation: https://docs.datamimic.io
- Website: https://datamimic.io

---

## Coming from Benerator

DATAMIMIC Community Edition is the direct successor to Benerator's model-driven engine. The descriptor approach carries over; the runtime and the extension language change. You install it with `pip install datamimic_ce` and drive it from the CLI, so it slots into the same place in your pipeline where the Benerator shell or Maven step runs today. Migration scripts convert much of an existing descriptor; the parts that were redesigned need a manual pass, and we are happy to help with those.

| Benerator CE | DATAMIMIC CE |
|---|---|
| Model-driven generation in XML | Same approach: model-driven XML descriptors. |
| Descriptor structure (`setup`, `generate`, `iterate`, `variable`, `attribute`, `id`, `reference`) | Similar structure, some elements renamed — Benerator's `attribute` is DATAMIMIC's `key`. A compatibility mode aliases common Benerator elements (e.g. `iterate`→`generate`, `id`→`key`) so most descriptors run with minimal changes; unsupported elements are reported in the log instead of failing the run. |
| Custom logic in Java or JavaScript | Python generators, converters, and validators. Existing Java/JS extensions must be rewritten in Python — DATAMIMIC provides Converter and Generator interfaces to implement against. |
| Anonymizing / masking existing data | Deterministic generation with value-keyed pseudonymization converters — the same input maps to the same token across runs (referentially consistent, reproducible from the seed). |
| Runs as a CLI / Maven step in Jenkins or GitLab CI | `pip install datamimic_ce`, then the `datamimic` CLI as a pipeline step — same model, no server required. |
| Databases via JDBC, any driver (Oracle, DB2, MySQL, PostgreSQL, H2, …); files: CSV, fixed-width, XML, Excel, DbUnit | Oracle, MS SQL Server, PostgreSQL, MySQL, SQLite, MongoDB; files: CSV, JSON, XML. The major relational engines plus MongoDB (document); Benerator's any-JDBC reach is broader on exotic drivers. |

**Streaming targets (Apache Kafka, RabbitMQ) are in DATAMIMIC EE**; a REST API and scheduler for server-driven pipelines are in DATAMIMIC Platform. CE covers the relational, MongoDB, and file targets above, runs from the CLI in any pipeline, and ships an MCP server for agentic workflows. As a platform partner we solve your test-data challenges — if a system you need isn't supported yet, contact us.

### Migrate a project: step by step

This release ships a **built-in converter** that translates Benerator descriptors into native DATAMIMIC
descriptors — including your data files, SQL scripts, DbUnit datasets, environment properties, and a
per-file report of everything that needs a manual decision.

**1. Convert your project** (the whole directory — descriptors, CSVs, scripts, properties):

```bash
java -cp benerator.jar com.rapiddweller.benerator.main.datamimic.DatamimicConverter \
     /path/to/your/benerator-project  /path/to/output  migration-report.txt
```

Every `*.ben.xml` becomes a `*.datamimic.xml`; data files and SQL scripts are carried over;
DbUnit datasets are split into per-table JSON sources; `conf/*.env.properties` are migrated to
DATAMIMIC's environment format (JDBC URLs become host/port/database/dbms).

**2. Read `migration-summary.md`** in the output directory. It lists, per file, what converted
automatically and what needs a manual pass, with a link into `MIGRATION_PLAYBOOK.md` for each
recurring pattern (the playbook ships next to the converter sources). Typical manual items:

- **JavaScript** (`<execute type="js">`, `{js:...}` scripts): DATAMIMIC scripting is Python — rewrite
  the snippet (usually a few lines) or move it to a `.py` file referenced via `uri=`.
- **DB-metadata columns**: Benerator fills `NOT NULL` columns it discovers in the live database. The
  converter does the same **when the descriptor itself executes the DDL** (inline or `.sql` file);
  columns known only to the live DB need an explicit `type=`/`generator=`.
- **Exotic generators**: anything the converter flags as "not known to DATAMIMIC" — check the report;
  most have a close DATAMIMIC equivalent.

**3. Install DATAMIMIC and run the converted project:**

```bash
pip install datamimic_ce
cd /path/to/output
datamimic run your-main.datamimic.xml
```

**4. Verify against your database** — row counts and shape, e.g.
`SELECT count(*) FROM your_table`. The converter keeps your model's semantics (references, weighted
distributions, nested entities, per-record SQL/Mongo selectors), so the generated data should have
the same structure and cardinalities as before.

The converter is exercised in CI on every commit: the complete Benerator demo suite is converted and
run through the real DATAMIMIC engine, including full round-trips against live PostgreSQL and MongoDB
(the flagship `shop` demo passes its own row-count assertions on both).

**We offer support with the migration.** The converter handles the bulk; the team helps with the parts
that need rework and with integrating DATAMIMIC into your systems and pipelines. Get in touch at
support@rapiddweller.com or via https://datamimic.io.

Start with the DATAMIMIC repository and docs above. Where a concept does not map directly, open an issue in the DATAMIMIC repository.

---

## Still on Benerator? (existing users)

Benerator CE keeps working, and we keep it safe:

- **What we do:** bug fixes, security updates, dependency and CI maintenance, and occasional maintenance releases.
- **What we do not do:** new features. Those land in DATAMIMIC.
- **Requirements:** Java 11. For Java 8 or earlier, use versions `<= 1.0.1`.
- **Releases:** see the [Releases](https://github.com/rapiddweller/rapiddweller-benerator-ce/releases) page for the latest maintenance build.
- **Found a bug?** Open an [issue](https://github.com/rapiddweller/rapiddweller-benerator-ce/issues). Fixes are best-effort.

---

## About Benerator (historical)

rapiddweller Benerator is a model-driven test data generation tool designed to:

- Generate data that satisfies complex validity and distribution constraints.
- Anonymize production data for testing and demos.
- Create large, interconnected datasets early in a project.
- Work with databases, files, XML, and messaging systems.

---

## Documentation

- Benerator manual (legacy): https://docs.benerator.de
- DATAMIMIC documentation (current): https://docs.datamimic.io

---

## Contributing

Bug-fix and security contributions are welcome. New-feature work belongs in DATAMIMIC. See [CONTRIBUTING.md](CONTRIBUTING.md).

---

## License

Benerator CE is dual-licensed: the **GNU General Public License v2 with exceptions** (open source) or a **commercial license**. See [license.txt](license.txt) for the full terms.

## Contact

- https://rapiddweller.com
- support@rapiddweller.com

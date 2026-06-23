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

- **Deterministic, reproducible output.** Same seed, same data, every run and every machine, with a per-output content hash you can use as audit evidence.
- **A familiar approach, modern foundation.** If you have worked with model-driven generation, DATAMIMIC will feel familiar: XML descriptors and first-class Python, so you can bring your own Python as generators, converters, and validators.
- **Active development and regular releases**, not maintenance-only.
- **Modern integration.** REST API and a built-in scheduler for CI/CD, an MCP server for AI-agent workflows, and connectors for PostgreSQL, Oracle, MongoDB, Apache Kafka, and flat files.
- **On-premise and air-gapped deployment**, no telemetry and no call-home, for regulated environments.

Get started:

- GitHub: https://github.com/rapiddweller/datamimic
- Documentation: https://docs.datamimic.io
- Website: https://datamimic.io

---

## Coming from Benerator

DATAMIMIC is rapiddweller's own platform with its own DSL, but it shares the model-driven approach, so you do not start from zero. Migration scripts carry much of an existing Benerator setup across; some parts are designed differently and need rework, and we are happy to help with those. This table maps familiar concepts to how DATAMIMIC approaches them:

| Concept (familiar from Benerator) | DATAMIMIC's approach (independently designed) |
|---|---|
| Model-driven generation in XML | Model-driven XML descriptors, plus first-class Python |
| Descriptor structure (setup, generate, iterate, variable, attribute, id, reference) | A similar, independently designed structure; for example, Benerator's `attribute` corresponds to DATAMIMIC's `key`. Familiar, not identical |
| Anonymization and masking | Privacy-safe synthetic generation and value-keyed pseudonymization |
| Java or JavaScript extensions | Python generators, converters, and validators |
| CI integration (Jenkins, GitLab) | REST API plus built-in scheduler for CI/CD |
| Databases, files, XML | PostgreSQL, Oracle, MongoDB, Kafka, CSV / JSON / XML |

**We offer support with the migration.** Migration scripts handle much of the conversion; the team helps with the parts that need rework and with integrating DATAMIMIC into your systems and pipelines. Get in touch at support@rapiddweller.com or via https://datamimic.io.

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

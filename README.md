<p align="center">
  <img width="300" height="300"
       src="https://github.com/rapiddweller/rapiddweller-benerator-ce/blob/development/doc/assets/benerator-icon.png"
       alt="Benerator">
</p>

<p align="center">
  <em>Benerator — model-driven test data generation (legacy)</em>
</p>

# rapiddweller-benerator-ce (maintenance stopped)

## ⚠️ Maintenance Notice

**rapiddweller-benerator-ce is no longer actively maintained.**

This repository is kept for **legacy usage, historical reference, and existing builds** only.  
No new features, fixes, or releases are planned.

👉 **For all new projects, ongoing development, and modern workflows, please use DATAMIMIC instead:**

- **GitHub:** https://github.com/rapiddweller/datamimic  
- **Documentation:** https://docs.datamimic.io  
- **Website:** https://datamimic.io  

Existing Benerator releases remain available under **Releases** (legacy).

---

## Why the transition to DATAMIMIC?

Benerator pioneered **model-driven data generation** for testing, anonymization, and migration.
Over time, requirements evolved:

- deterministic and reproducible datasets
- richer JSON and XML pipelines
- privacy-safe synthetic data
- integration with modern Python tooling and AI/agent workflows

**DATAMIMIC is the actively maintained successor**, continuing Benerator’s philosophy while addressing these modern needs.

In short:

- **Benerator** → legacy Java-based generator  
- **DATAMIMIC** → modern, deterministic, Python-first platform with XML parity

---

## Can I still use Benerator?

Yes — **if you already rely on it**.

- Existing scripts and pipelines will continue to work with released versions
- No breaking changes will be introduced (because no changes are planned)
- Community support is limited to best-effort

If you are starting a **new project**, migrating systems, or need active support,  
**DATAMIMIC is the recommended path forward**.

---

## About Benerator (historical)

rapiddweller Benerator is a model-driven test data generation tool designed to:

- generate data that satisfies complex validity and distribution constraints
- anonymize production data for testing and showcases
- create large, interconnected datasets early in projects
- support databases, files, XML, and messaging systems
- enable low-code, declarative data modeling

Benerator is built for **Java 11**.

> If you need support for Java 8 or earlier, use versions `<= 1.0.1`.

---

## Documentation (legacy)

The original documentation remains available for reference:

- 📘 Manual: https://docs.benerator.de  
- 📄 PDF Manual: https://docs.benerator.de/latest/rapiddweller-benerator-manual-latest.pdf  

For modern documentation, examples, and guides, see **DATAMIMIC**:
https://docs.datamimic.io

---

## Migration guidance

There is no automatic drop-in replacement for Benerator XML.
However, the **conceptual model remains familiar**:

| Benerator concept        | DATAMIMIC equivalent              |
|--------------------------|-----------------------------------|
| Model-driven generation  | Deterministic domain services     |
| XML descriptors          | XML + Python parity               |
| Reproducibility          | Seeded determinism + proofs       |
| Anonymization pipelines  | Privacy-safe synthetic generation |

➡️ Migration guides and examples are maintained in the DATAMIMIC repository.

---

## Status Summary

- 🧊 **Project status:** Legacy / frozen  
- 🛠 **Maintenance:** Stopped  
- 🚀 **Successor:** DATAMIMIC  
- 📦 **Releases:** Available (legacy)  

---

## Contact

For questions about legacy usage or migration strategy:

- https://rapiddweller.com  
- support@rapiddweller.com

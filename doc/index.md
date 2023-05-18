<p align="center">
  <a href="https://benerator.de"><img width="300" height="300" src="https://github.com/rapiddweller/rapiddweller-benerator-ce/raw/development/doc/assets/benerator-icon.png" alt="Benerator - The model-driven data generation and obfuscation toolkit."></a>
</p>
<p align="center">
    <em>Benerator, the smart way to handle data</em>
</p>

![.github/workflows/ci.yml](https://github.com/rapiddweller/rapiddweller-benerator-ce/workflows/.github/workflows/ci.yml/badge.svg)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=rapiddweller_rapiddweller-benerator-ce&metric=coverage)](https://sonarcloud.io/dashboard?id=rapiddweller_rapiddweller-benerator-ce)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=rapiddweller_rapiddweller-benerator-ce&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=rapiddweller_rapiddweller-benerator-ce)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=rapiddweller_rapiddweller-benerator-ce&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=rapiddweller_rapiddweller-benerator-ce)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=rapiddweller_rapiddweller-benerator-ce&metric=security_rating)](https://sonarcloud.io/dashboard?id=rapiddweller_rapiddweller-benerator-ce)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.rapiddweller/rapiddweller-benerator-ce/badge.svg)](https://search.maven.org/artifact/com.rapiddweller/rapiddweller-benerator-ce)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=rapiddweller_rapiddweller-benerator-ce&metric=alert_status)](https://sonarcloud.io/dashboard?id=rapiddweller_rapiddweller-benerator-ce)


---

**Documentation**: <a href="https://docs.benerator.de" target="_blank">https://docs.benerator.de</a>

**Source Code**: <a href="https://github.com/rapiddweller/rapiddweller-benerator-ce" target="_blank">https://github.com/rapiddweller/rapiddweller-benerator-ce</a>

---

# rapiddweller BENERATOR

**[rapiddweller Benerator](https://www.benerator.de)** is a software solution to
**generate**, **obfuscate** (**anonymize** / **pseudonymize**) and **migrate** data for development,
testing and training purposes with a model-driven approach.

## Summary

**[rapiddweller Benerator](https://www.benerator.de)** allows creating realistic and valid high-volume test data, 
used for testing (unit/integration/load), training and showcase setup.

### Generate data synthetically

- Describe your data model on the most abstract level.
- Involve your business people or tester as no developer skills are necessary
- Write your own Benerator extensions in Javascript, Python or Java
Integrate your data generation processes into any CI Pipeline

### Mask and obfuscate sensitive production data

- Define processes to anonymize or pseudonymize data on abstract level.
- Stay GDPR compliant with your data and protect the privacy of your customers.
anonymize sensitive data for BI, test, development or training purposes
- Combine data from various sources (subsetting) and keep the data integrity

### Migrate data

- Migrate and transform your data in multisystem landscapes.
- Reuse your testing data models to migrate production environments.
- Keep your data consistent and reliable in a microsystem architecture


## Requirements

**rapiddweller Benerator** is built for Java 11.

!!! note

    If you need support for Java 8 or earlier, please consider using the versions `<= 1.0.1`.

## Preface of this manual

This document aims to provide a comprehensive guide for using Benerator. Use it efficiently and extend it as you need. This
reference is under construction and will update from time to time. Feel free to contribute your ideas in our repo
at: 

[https://github.com/rapiddweller/rapiddweller-benerator-ce/](https://github.com/rapiddweller/rapiddweller-benerator-ce/issues)

If problems remain unsolved after reading this doc, do not hesitate to contact us for help. 
rapiddweller-benerator-ce is and remains open-source and
is provided for free.

Since you can do quite a lot of different things with Benerator but surely are interested in just a part of it, here's some guidance:

**[Goals and Features](introduction_to_benerator.md)**, introduces you to the goals and features of Benerator. 

Find advise on how to install a binary distribution and how to get
the sources and set up an IntelliJ/Eclipse project for using, debugging, and customizing Benerator in **[Installation](installation.md)**.

**[Data Generation Concepts](data_generation_concepts.md)**, **[Descriptor File Format](quick_tour_through_the_descriptor_file_format.md)** and **[Advanced Topics](advanced_topics.md)** then provide you with a structured and complete introduction
into the Benerator descriptor file setup.

Benerator supports a multitude of service provider interfaces (SPIs). It comes along with some implementations for specific business domains 
(**[Domains](domains.md)**) and general-purpose classes in **[Component Reference](component_reference.md)**.

Finally, you are instructed how to write custom SPI implementations in **[Extending Benerator](extending_benerator.md)**.

## Further Support

**Ready to Take the Next Step?**

If you've made it this far, you're clearly serious about your data management. It's time to see what Benerator can do for your organization.

For a deeper understanding of how Benerator can transform your development, testing, and training processes, why not book a demo? Our team will show you firsthand how Benerator's features can be customized to your unique needs.

**[Book a Demo](https://info.rapiddweller.com/meetings/rapiddweller/benerator-demo)**

If you are interested in additional support, and our premium features, we encourage you to check the
website 

**[www.benerator.de](https://www.benerator.de/)**. 

We offer additional services to make your data generation project a success and provide
detailed use cases to ease your start into more complex scenarios.

If you have any questions or need further clarification on any aspect of Benerator, don't hesitate to reach out to our team. We're here to ensure your experience with Benerator is as smooth as possible.

**[Contact the rapiddweller Team](https://www.benerator.de/contact-us)**

Remember, the journey to robust, efficient, and compliant data management begins with a single step. Let that step be Benerator - your partner in data generation, obfuscation, and migration.

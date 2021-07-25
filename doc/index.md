<p align="center">
  <a href="https://benerator.de"><img width="300" height="300" src="https://github.com/rapiddweller/rapiddweller-benerator-ce/raw/development/doc/assets/benerator-icon.png" alt="Benerator"></a>
</p>
<p align="center">
    <em>Benerator, the smart way to generate data</em>
</p>


![.github/workflows/ci.yml](https://github.com/rapiddweller/rapiddweller-benerator-ce/workflows/.github/workflows/ci.yml/badge.svg)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/99c887d3153744e395f539551cdec103)](https://www.codacy.com/gh/rapiddweller/rapiddweller-benerator-ce/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=rapiddweller/rapiddweller-benerator-ce&amp;utm_campaign=Badge_Grade)
[![Codacy Badge](https://app.codacy.com/project/badge/Coverage/99c887d3153744e395f539551cdec103)](https://www.codacy.com/gh/rapiddweller/rapiddweller-benerator-ce/dashboard?utm_source=github.com&utm_medium=referral&utm_content=rapiddweller/rapiddweller-benerator-ce&utm_campaign=Badge_Coverage)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.rapiddweller/rapiddweller-benerator-ce/badge.svg)](https://search.maven.org/artifact/com.rapiddweller/rapiddweller-benerator-ce)


# rapiddweller 'Benerator'

[rapiddweller 'Benerator'](https://www.benerator.de) is a software solution to **generate**, **anonymize**, 
**pseudonymize** and **migrate** data for development, testing and training purposes.

## Summary

[rapiddweller 'Benerator'](https://www.benerator.de) allows creating realistic and valid high-volume test data, 
used for testing (unit/integration/load), training and showcase setup.

###Generate data synthetically
Describe your data model on the most abstract level.
Involve your business people or tester as no developer skills are necessary
Write your own benerator extensions in Javascript, Python or Java
Integrate your data generation processes into any CI Pipeline

###Mask and obfuscate sensitive production data
Define processes to anonymize or pseudonymize data on abstract level.
Stay GDPR compliant with your data and protect the privacy of your customers.
anonymize sensitive data for BI, test, development or training purposes
Combine data from various sources (subsetting) and keep the data integrity

###Migrate data
Migrate and transform your data in multisystem landscapes.
Reuse your testing data models to migrate production environments.
Keep your data consistent and reliable in a microsystem architecture


###requirements

rapiddweller 'Benerator' is built for Java 11.

If you need support for Java 8 or earlier, please consider using the versions `<= 1.0.1`.

## Preface of this manual

This document is supposed to become a complete summary of everything you need of benerator usage, use it efficiently and extend it as you need. This
reference is under construction and will update from time to time. Feel free to contribute your ideas in our repo
at: 

[https://github.com/rapiddweller/rapiddweller-benerator-ce/](https://github.com/rapiddweller/rapiddweller-benerator-ce/issues)

If problems remain unsolved after reading this book, do not hesitate to contact us for help. rapiddweller-benerator-ce is and remains open-source and
is provided for free.

If you are interested in additional support, and our premium features, we encourage you to check the
website **[www.benerator.de](https://www.benerator.de/)**. We offer additional services to make your data generation project a success and provide
detailed use cases to ease your start into more complex scenarios.

Since you can do quite a lot of different things with Benerator but surely are interested in just a part of it, here's some guidance:

**'[Goals and Features](benerator_goals_features.md)'**, introduces you to the goals and features of Benerator. 

Find advise on how to install a binary distribution and how to get
the sources and set up an IntelliJ/Eclipse project for using, debugging, and customizing Benerator in **'[Installation](installation.md)'**.

**'[Data Generation Concepts](data_generation_concepts.md)'**, **'[Descriptor File Format](quick_tour_through_the_descriptor_file_format.md)'** and **'[Advanced Topics](advanced_topics.md)'** then provide you with a structured and complete introduction
into the Benerator descriptor file setup.

Benerator supports a multitude of service provider interfaces (SPIs). It comes along with some implementations for specific business domains (**'
[Domains](domains.md)'**) and general-purpose classes in **'[Component Reference](component_reference.md)'**.

Finally, you are instructed how to write custom SPI implementations in **'[Extending Benerator](extending_benerator.md)'**.
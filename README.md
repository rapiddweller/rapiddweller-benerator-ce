<p align="center">
  <a href="https://benerator.de"><img width="300" height="300" src="https://github.com/rapiddweller/rapiddweller-benerator-ce/blob/development/doc/assets/benerator-icon.png" alt="Benerator"></a>
</p>
<p align="center">
    <em>Benerator, the smart way to generate data</em>
</p>

![.github/workflows/ci.yml](https://github.com/rapiddweller/rapiddweller-benerator-ce/workflows/.github/workflows/ci.yml/badge.svg)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/99c887d3153744e395f539551cdec103)](https://www.codacy.com/gh/rapiddweller/rapiddweller-benerator-ce/dashboard?utm_source=github.com&utm_medium=referral&utm_content=rapiddweller/rapiddweller-benerator-ce&utm_campaign=Badge_Grade)
[![Codacy Badge](https://app.codacy.com/project/badge/Coverage/99c887d3153744e395f539551cdec103)](https://www.codacy.com/gh/rapiddweller/rapiddweller-benerator-ce/dashboard?utm_source=github.com&utm_medium=referral&utm_content=rapiddweller/rapiddweller-benerator-ce&utm_campaign=Badge_Coverage)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.rapiddweller/rapiddweller-benerator-ce/badge.svg)](https://search.maven.org/artifact/com.rapiddweller/rapiddweller-benerator-ce)

# rapiddweller-benerator-ce

[rapiddweller 'Benerator'](https://www.benerator.de) is a software solution to
generate, anonymize, pseudonymize and migrate data for development,
testing and training purposes.

## Introduction

[rapiddweller 'Benerator'](https://www.benerator.de) allows creating realistic and valid high-volume test data, used for testing (unit/integration/load) and showcase setup.

Metadata constraints are imported from systems and/or configuration files. Data can be imported from and exported to files and systems, anonymized, or
generated from scratch. Domain packages provide reusable generators for creating domain-specific data as names and addresses internationalizable in
language and region. It is strongly customizable with plugins and configuration options.

rapiddweller 'Benerator' is built for Java 11.

If you need support for Java 8 or earlier, please consider using the versions `<= 1.0.1`.

## Prerequisites

- Java 11 JDK (we recommend [adoptopenjdk](https://adoptopenjdk.net/))
- [Maven](https://maven.apache.org/)

Check your local setup

```bash
java -version
mvn -version
```

## Installation

1. Choose how to install:

   a) Download a Prebuilt Distribution from **Project Overview > Releases**
   (current release is `1.1.0`, cp. rapiddweller-benerator-ce-1.1.0-jdk-11-dist.zip)
   and unzip the downloaded file in an appropriate directory, e.g.
   `/Developer/Applications` or `C:\Program Files\Development`.

   b) Checkout repository and build your own rapiddweller-benerator-ce using  
   maven command `mvn clean install`

2. Set **BENERATOR_HOME**  
   Create an environment variable BENERATOR_HOME that points to the path you extracted Benerator to.

   - Windows Details: Open the System Control Panel, choose Advanced Settings - Environment Variables. Choose New in the User Variables section.
     Enter BENERATOR_HOME as name and the path as value
     (e.g. `C:\Program Files\Development\rapiddweller-benerator-ce-1.1.0-jdk-11`). Click OK several times.

   - Mac/Unix/Linux Details: Add an entry that points to Benerator,
     e.g.: `export BENERATOR_HOME=/Developer/Applications/rapiddweller-benerator-ce-1.1.0-jdk-11`

3. On Unix/Linux/Mac systems: **Set permissions**  
   Open a shell on the installation's root directory and execute
   `chmod a+x bin/*.sh`

4. Mac OS X configuration **Set JAVA_HOME**
   On Mac OS X you need to provide benerator with an explicit configuration of the JAVA_HOME path.
   See http://developer.apple.com/qa/qa2001/qa1170.html for a good introduction to the OS X way of setting up Java. It is based on aliases
   conventions. If you are not familiar with that, you should read the article. If Java 8 (or newer) is the default version you will use, you can
   simply define JAVA_HOME by adding the following line to your .profile: in your user directory:
   `export JAVA_HOME=/Library/Java/Home`
   If it does not work or if you need to use different Java versions, it is easier to 'hard-code' JAVA_HOME like this:
   `export JAVA_HOME=/Library/Java/JavaVirtualMachines/adoptopenjdk-11.jdk/Contents/Home/`

**Note**: We currently recommend following projects for your proper Mac OS X setup:

- [brew](https://brew.sh/)
- [adoptopenjdk](https://adoptopenjdk.net/)
- [jenv](https://www.jenv.be/)

## Run

If you followed above installation steps carefully, run your benerator scripts from command line.

1. Start benerator from command line

```bash
benerator <YOUR_BENERATOR_SCRIPT>.xml
```

To get started please find some demo scripts in the folder
[/src/demo/resources/demo](/src/demo/resources/demo) of this repository.

## Docs / rapiddweller manual

There are various sources to get you started or extend your benerator knowledge:

- Read our docs on our [Benerator Manual site](https://www.benerator.de/ce/doc/) or
  create your own mkdocs with python `mkdocs build`. Keep in mind that you might need to install certain mkdocs dependencies first.

- Consider the [Maven Site Docs](https://www.benerator.de/ce/doc/mvn-site/) or create your own docs using maven `mvn site:site`. The generated docs
  include Javadoc, Test Reports and more.

- Download the [Benerator Manual](https://www.benerator.de/ce/rapiddweller-benerator-ce-manual.pdf)
  from our website.

- Checkout the maintainer's website [www.benerator.de](https://www.benerator.de/) for additional support resources.

## Table of Contents Manual

Navigate to the sources for our generated mkdocs:

- [Goals and Features](doc/benerator_goals_features.md)
- [Installation](doc/installation.md)
- [The Benerator Project Wizard](doc/the_benerator_project_wizard.md)
- [Quick tour through the descriptor file format](doc/quick_tour_through_the_descriptor_file_format.md)
- [Data Generation Concepts](doc/data_generation_concepts.md)
- [Regular Expression Support](doc/regular_expression_support.md)
- [Processing and creating CSV Files](doc/processing_and_creating_csv_files.md)
- [Using Relational Databases](doc/using_relational_databases.md)
- [Generating XML Files](doc/generating_xml_files.md)
- [Advanced Topics](doc/advanced_topics.md)
- [Generating Unique Data](doc/generating_unique_data.md)
- [Scripting](doc/scripting.md)
- [rapiddwellerScript](doc/rapiddwellerscript.md)
- [Command Line Tools](doc/command_line_tools.md)
- [Domains](doc/domains.md)
- [Component Reference](doc/component_reference.md)
- [Using DB Sanity](doc/using_db_sanity.md)
- [Maven Benerator Plugin](doc/maven_benerator_plugin.md)
- [Extending Benerator](doc/extending_benerator.md)
- [Using Benerator as Load Generator](doc/using_benerator_as_load_generator.md)
- [Troubleshooting](doc/troubleshooting.md)
- [Monitoring Benerator](doc/monitoring_benerator.md)
- [Benerator Performance Tuning](doc/benerator_performance_tuning.md)

## Development Quick Start (only Linux and Mac OS X)

If you want to start development or use the maven project to build rapiddweller 'Benerator' by yourself, on Linux or Mac OS X you can also try the
quickstart by using the helper scripts.  
**IMPORTANT:** If you want to use the command `benerator` in your shell session, you have to execute `source script/2_setup_benerator.sh`  
If you want to install Benerator permanently into your system, you have to modify your environment file or your `~/.profile`  
and add ENV variable `BENERATOR_HOME` and `PATH=$BENERATOR_HOME/bin:$PATH`

to execute the scripts you have can do ...

`bash script/1_install_mvn_dependencies.sh`  
`source script/2_setup_benerator.sh`  
`bash script/3_execute_demos.sh`

... alternatively, you can also set execute permissions like ...

`chmod +x script/1_install_mvn_dependencies.sh`  
`chmod +x script/2_setup_benerator.sh`  
`chmod +x script/3_execute_demos.sh`

... and execute scripts like this

`.script/1_install_mvn_dependencies.sh`  
`.script/2_setup_benerator.sh`  
`.script/3_execute_demos.sh`

- _1_install_mvn_dependencies.sh_ : This script is checking Prerequisites for you, cloning all rapiddweller-benerator-ce SubProjects and install via
  Maven locally.

- _2_setup_benerator.sh_ : This script is building on script no. 1 and using installed dependencies and packed jar, assemble it to a
  rapiddweller-benerator-ce.tar.gz and setup Benerator locally into your user home directory.

- _3_execute_demos.sh_ : This script is building on script no. 2 and use the unpacked and configure rapiddweller-benerator-ce application to execute
  existing demo files.

## Getting Involved

If you would like to reach out to the maintainers, contact us via our
[Contact-Form](https://www.benerator.de/contact-us) or email us at
[solution.benerator@rapiddweller.com](mailto://solution.benerator@rapiddweller.com).

## Contributing

Please see our [Contributing](CONTRIBUTING.md) guidelines.
For releasing see our [release creation guide](RELEASE.md).
Check out the maintainers [website](https://rapiddweller.com)!

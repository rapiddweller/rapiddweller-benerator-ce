# Installation

## Download the distribution binary

Download Benerator from: 

[https://github.com/rapiddweller/rapiddweller-benerator-ce/releases/](https://github.com/rapiddweller/rapiddweller-benerator-ce/releases/)

You should download the most recent version of the rapiddweller-benerator-dist archive from the download page, e.g.
_rapiddweller-benerator-ce-2.0.0-jdk-11-dist.zip_ .

!!! warning

      We highly recommend downloading a prebuild binary from our release tags as above to ease your start. 
      If you clone our GitHub repository, there are no binaries included and you need to build benerator yourself. 
      Building benerator requires a proper java/maven setup on your system. Learn more below 
      **[Development Quick Start](installation.md#development-quick-start-advanced)**.

## Unzip Benerator

Unzip the downloaded file in an appropriate directory, e.g. `/Developer/Applications` or `C:\Program Files\Development`.

## Set BENERATOR_HOME

Create an environment variable BENERATOR_HOME that points to the path you extracted benerator to.

**Windows Details**: Open the System Control Panel, choose Advanced Settings - Environment Variables. Choose New in the
User Variables section. Enter BENERATOR_HOME as name and the path as value (
e.g. `C:\Program Files\Development\rapiddweller-benerator-ce-2.0.0`). Click OK several times.

**Mac/Unix/Linux Details**: Open the file .profile in your user directory. Add an entry that points to benerator, e.g.:
`export BENERATOR_HOME=/Developer/Applications/rapiddweller-benerator-ce-2.0.0`

## Optional: Install JDBC drivers

Benerator comes with open-source JDBC drivers (for connecting to a database). No extra installation is necessary for
them:

- Microsoft SQL Server Driver (MS SQL Server)
- MySQL Connector
- HSQL DB
- H2 DB
- Derby Client
- PostgreSQL
- Jaybird (Firebird DB)

However, if you need to use a closed source database driver, the vendor usually requires you to accept usage conditions before you can download and
install their driver. So, if you are using Oracle DB or DB2, get the JDBC drivers from these sites:

• Oracle [http://www.oracle.com/technology/software/tech/java/sqlj_jdbc/index.html](http://www.oracle.com/technology/software/tech/java/sqlj_jdbc/index.html): Click on "Oracle Database 11g Release 2 (11.2.0.1.0) drivers".
Download ojdbc6.jar and put it into Benerator's lib directory. You will need to create a free Oracle account.

• DB2 [http://www-306.ibm.com/software/data/db2/java](http://www-306.ibm.com/software/data/db2/java): Follow the link 'IBM Data Server Driver for JDBC and SQLJ', download the driver archive and
copy the contained file db2jcc.jar to Benerator's lib directory.

## Optional: Set up log4j2

Troubleshooting is simpler if you make use of log4j's configuration capabilities. If you do not know (or care) about logging, simply skip this step.
Otherwise, put a custom log4j2.xml file into the BENERATOR_HOME/lib directory.

## Mac OS X configuration

On Mac OS X you need to provide benerator with an explicit configuration of the JAVA_HOME path. See http://developer.apple.com/qa/qa2001/qa1170.html
for a good introduction to the OS X way of setting up Java. It is based on aliases conventions. If you are not familiar with that, you should read the
article. If Java 6 (or newer) is the default version you will use, you can simply define JAVA_HOME by adding the following line to your .profile: in
your user directory:

```shell
export JAVA_HOME=/Library/Java/Home
```

If it does not work or if you need to use different Java versions, it is easier to 'hard-code' JAVA_HOME like this:

```shell
export JAVA_HOME=/Library/Java/JavaVirtualMachines/adoptopenjdk-11.jdk/Contents/Home/ 
```

## Verifying the settings

On any OS, open a console window (shell,cmd or powershell) and type ...

<div class="termy">

```shell
$ benerator -–version

Benerator Community Edition 2.1.0-jdk-11
Java version:  11.0.11
JVM product:   OpenJDK 64-Bit Server VM 11.0.11+9 (AdoptOpenJDK)
System:        Mac OS X 10.16 x86_64
CPU & RAM:     8 cores and 8 GB RAM, max 2 GB of RAM for this process
```
</div>

Benerator will then launch and print out version information about itself,
the Java version it uses, the operating system.

## Development Quick Start (Advanced)

If you want to start development or use the maven project to build Benerator by yourself,
on Linux or Mac OS X you can also try the quickstart by using the helper scripts. 
Checkout the project from [https://github.com/rapiddweller/rapiddweller-benerator-ce/](https://github.com/rapiddweller/rapiddweller-benerator-ce/).

!!! note

    Following instructions only work on Linux or Mac OS X. Scripts are not supported on Windows.

!!! note

    It might be required to run the scripts with `sudo`.

If you want to use the command `benerator` in your shell session, you have to execute `source script/2_setup_benerator.sh`  
If you want to install Benerator permanently into your system, you have to modify your environment file or your `~/.profile`  
and add ENV variable `BENERATOR_HOME` and `PATH=$BENERATOR_HOME/bin:$PATH`

to execute the scripts you can do ...

```term
$ bash script/1_install_mvn_dependencies.sh
$ source script/2_setup_benerator.sh
$ bash script/3_execute_demos.sh
```


... alternatively, you can also set execute permissions like ...

```term
$ chmod +x script/1_install_mvn_dependencies.sh
$ chmod +x script/2_setup_benerator.sh
$ chmod +x script/3_execute_demos.sh
```

... and execute scripts like this

```term
$ .script/1_install_mvn_dependencies.sh
$ .script/2_setup_benerator.sh
$ .script/3_execute_demos.sh
```

- _1_install_mvn_dependencies.sh_ : This script is checking prerequisites for you, 
  cloning all rapiddweller-benerator-ce sub-projects and installing them via maven locally.

- _2_setup_benerator.sh_ : This script is building on script no. 1 and using installed dependencies and packed jar, assembling it to a
  rapiddweller-benerator-ce.tar.gz and is setting up Benerator locally into your user home directory.

- _3_execute_demos.sh_ : This script is building on script no. 2 and is using the unpacked and configured 
   rapiddweller-benerator-ce application to execute existing demo files.
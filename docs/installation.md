# Installation 

## Download the distribution binary 

Download benerator from [https://gitlab.com/rapiddweller/benerator/rapiddweller-benerator-ce/files/](https://gitlab.com/rapiddweller/benerator/rapiddweller-benerator-ce/files/).

You should download the most recent version of the rapiddweller-benerator-dist archive from the download page, e.g. 
rapiddweller-benerator-ce-1.0.0+jdk-11-dist.zip.d

## Unzip Benerator 

Unzip the downloaded file in an appropriate directory, e.g. /Developer/Applications or C:\Program Files\Development.

## Set BENERATOR_HOME 

Create an environment variable BENERATOR_HOME that points to the path you extracted benerator to.

**Windows Details**: Open the System Control Panel, choose Advanced Settings - Environment Variables. Choose New in the User Variables section. Enter BENERATOR_HOME as name and the path as value (e.g. C:\Program Files\Development\rapiddweller-benerator-ce-1.0.0). Click OK several times.

**Mac/Unix/Linux Details**: Open the file .profile in your user directory. Add an entry that points to benerator, e.g.: export BENERATOR_HOME=/Developer/Applications/rapiddweller-benerator-ce-1.0.0

## Optional: Install JDBC drivers 

Benerator comes with open source JDBC drivers (for connecting to a database). No extra installation is necessary for them:

• jTDS Driver (MS SQL Server or Sybase)

• MySQL Connector

• HSQL DB

• H2 DB

• Derby Client

• PostgreSQL

• Jaybird (Firebird DB)

However, if you need to use a closed source database driver, the vendor usually requires you to accept usage conditions before you can download and install their driver. So, if you are using Oracle DB or DB2, get the JDBC drivers from these sites:

• Oracle [http://www.oracle.com/technology/software/tech/java/sqlj_jdbc/index.html]: Click on "Oracle Database 11g Release 2 (11.2.0.1.0) drivers". Download ojdbc6.jar and put it into benerator's lib directory. You will need to create a free Oracle account.

• DB2 [http://www-306.ibm.com/software/data/db2/java]: Follow the link 'IBM Data Server Driver for JDBC and SQLJ', download the driver archive and copy the contained file db2jcc.jar to benerator's lib directory.

## Optional: Set up log4j2 

Troubleshooting is simpler if you make use of log4j's configuration capabilities. If you do not know (or care) about 
logging, simply skip this step. Otherwise put a custom log4j2.xml file into the BENERATOR_HOME/lib directory.

## On Unix/Linux/Mac systems: Set permissions 

Open a shell on the installation's root directory and execute

```shell
chmod a+x bin/*.sh
```

## Mac OS X configuration 

On Mac OS X you need to provide benerator with an explicit configuration of the JAVA_HOME path. See http://developer.apple.com/qa/qa2001/qa1170.html for a good introduction to the OS X way of setting up Java. It is based on aliases conventions. If you are not familiar with that, you should read the article. If Java 6 (or newer) is the default version you will use, you can simply define JAVA_HOME by adding the following line to your .profile: in your user directory:

```shell
export JAVA_HOME=/Library/Java/Home
```
If it does not work or if you need to use different Java versions, it is easier to 'hard-code' JAVA_HOME like this:

```shell
export  JAVA_HOME=/Library/Java/JavaVirtualMachines/adoptopenjdk-11.jdk/Contents/Home/ 
```

## Verifying the settings 

On any OS, open a console window (shell,cmd or powershell) and type ...

<div class="termy">

```shell
$ benerator -–version

Benerator 1.1.0-jdk-11-SNAPSHOT build 1
Java version 11.0.9
JVM OpenJDK 64-Bit Server VM 11.0.9+11 (AdoptOpenJDK)
OS Linux 5.9.16-050916-generic (amd64)

// Benerator will then launch and print out version information about itself, 
// the Java version it uses, the operating system.
```

</div>



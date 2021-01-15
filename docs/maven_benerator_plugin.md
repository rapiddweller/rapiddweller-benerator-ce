# Maven Benerator Plugin 

The benerator plugin enables you to attach benerator to your build cycle or simply use maven and its configuration capabilities for benerator setup and invocation.

You can use the plugin to

*   Invoke Benerator with a descriptor file.

*   Create XML files from an XML Schema file, supporting XML Schema annotations for generation setup.

*   Create database snapshots in Excel, SQL or DbUnit data file format.

## System Requirements 

Maven 3.x or newer

JDK 11 or newer

## Getting started 

What ever goal you want to accomplish with Maven Benerator Plugin, you need to create a Maven project first. If you are about to create a completely new project, you may want to make use of Benerator's Maven Project Wizard. Otherwise you need to configure the benerator plugin in your project manually. The minimal configuration in Maven's pom.xml would be:

```xml
<build>

...

<plugins>

<plugin> <groupId>org.databene</groupId> <artifactId>maven-benerator-plugin</artifactId> <version>0.8.1</version> </plugin>

</plugins>

</build>
```

In order to make use of a plugin, it must be listed as dependency, e.g. dbsanity4ben and mongo4ben:

```xml
<dependencies>

<dependency>
<groupId>org.databene</groupId>
<artifactId>dbsanity4ben</artifactId>
<version>0.9.4</version>
</dependency>

<dependency>
<groupId>org.databene</groupId>
<artifactId>mongo4ben</artifactId>
<version>0.1</version>
</dependency>

...
```

When using proprietary database drivers (e.g. Oracle), you need to fetch and store them in your Maven repository manually and stated as depenency in the pom.xml.

The default descriptor path points to the file benerator.xml in the project's base directory. So put your benerator.xml into this directory and invoke from the command line:

mvn benerator:generate

Voilà!

## Common configuration elements 

You may configure certain aspects of benerator behavior, e.g. file encoding and scope in a `<configuration>` element in your pom.xml's plugin configuration, e.g.:

```xml
<plugin>

<groupId>org.databene</groupId>

<artifactId>maven-benerator-plugin</artifactId>

<version>0.8.1</version>

<configuration>

<encoding>iso-8859-1</encoding>

<scope>test</scope>

</configuration>

</plugin>
```

These options are applicable for all goals of the Maven Benerator plugin. Their meaning is:

*   scope: the scope of the project dependencies to include in the classpath when running benerator. It can be runtime or test. If left out, it defaults to runtime

*   encoding: the file encoding to use by default

## Executing descriptor files 

The configuration elements available for descriptor file execution are:

*   descriptor: the path to the benerator descriptor file

*   validate: turn internal (XML an data model) validations off or on

*   dbDriver: the JDBC driver class to use, this setting is provided to benerator as variable dbDriver

*   dbUrl: the JDBC driver class to use, this setting is provided to benerator as variable dbUrl

*   dbSchema: the database schema to use, this setting is provided to benerator as variable dbSchema

*   dbUser: the database user name, this setting is provided to benerator as variable dbUser

*   dbPassword: the database user's passord, this setting is provided to benerator as variable dbPassword

You can invoke descriptor file execution by calling the **generate** goal from the command line or your IDE:

mvn benerator:generate

The db* configuration is available to scripts in your descriptor file as well, e.g. `<database url=“{dbUrl}“... />`

## Creating database snapshots 

Use these `<configuration>` elements in your pom.xml:

*   dbDriver: the JDBC driver class to use, this setting is provided to benerator as variable dbDriver

*   dbUrl: the JDBC driver class to use, this setting is provided to benerator as variable dbUrl

*   dbSchema: the database schema to use, this setting is provided to benerator as variable dbSchema

*   dbUser: the database user name, this setting is provided to benerator as variable dbUser

*   dbPassword: the database user's passord, this setting is provided to benerator as variable dbPassword

*   snapshotFormat: The format in which to create the snapshot. Supported values: dbunit, sql, xls

*   snapshotDialect: When createing snapshots in SQL format, this defines the SQL dialect to use. Available values: db2, derby, firebird, hsql, h2, oracle, postgres, sql_server

*   snapshotFilename: The file name to use for the snapshot

Start snapshot creation by invoking the dbsnapshot goal:

mvn benerator:dbsnapshot

## Creating XML files from XML Schema files 

Use these `<configuration>` elements in your pom.xml:

*   xmlSchema:

*   xmlRoot:

*   filenamePattern:

*   fileCount:

Then invoke XML file generation using the **createxml** goal:

mvn benerator:createxml

## Creating a project assembly 

For being able to port a Maven project to a location in which no Maven installation is available or possible, you can make the plugin collect all dependencies in one directory and create a classpath file. Call

mvn benerator:assembly

and you will find the project files, all dependent binaries and a classpath.txt file in the directory target/assembly. The classpath.txt helps you to set up a classpath definition for your target execution environment more easily.

## Extending the classpath 

If you need to extend the classpath to libraries different to your project dependencies, you can add them as dependencies to your plugin configuration (this requires Maven 2.0.9 or newer):

```xml
<plugin>

<groupId>org.databene</groupId>

<artifactId>maven-benerator-plugin</artifactId>

<version>0.8.1</version>

<configuration>

...

</configuration>

<dependencies>

<dependency>

<groupId>oracle</groupId>

<artifactId>ojdbc</artifactId>

<version>1.4</version>

</dependency>

</dependencies>

</plugin>
```



## Profile-based configuration 

In cooperative software development you are supposed to keep your individual configuration private. E.g. you might have individual database configurations on your local development systems. You can then specify them as profile properties in a Maven settings.xml file in your user directory.

```xml
<profiles>

<profile>

<id>development</id>

<activation>

<activeByDefault>true</activeByDefault>

</activation>

<properties>

<database.driver>oracle.jdbc.driver.OracleDriver</database.driver>

<database.url>jdbc:oracle:thin:@localhost:1521:XE</database.url>

<database.user>user</database.user>

<database.pwd>user</database.pwd>

</properties>

</profile>

<profiles>

You would then refer them in your pom.xml:

<plugin>

<groupId>org.databene</groupId>

<artifactId>maven-benerator-plugin</artifactId>

<version>0.8.1</version>

<configuration>

<descriptor>src/test/benerator/myproject.ben.xml</descriptor>

<encoding>ISO-8859-1</encoding>

<dbDriver>${database.driver}</dbDriver>

<dbUrl>${database.url}</dbUrl>

<dbUser>${database.user}</dbUser>

<dbPassword>${database.pwd}</dbPassword>

<dbSchema>${database.user}</dbSchema>

</configuration>

</plugin>

## 
```

## Attaching the Mojo to the Build Lifecycle 

You can also configure the benerator plugin to attach specific goals to a particular phase of the build lifecycle. Here is an example:

```xml
<build>

<plugins>

<plugin>

<groupId>org.databene</groupId>

<artifactId>maven-benerator-plugin</artifactId>

<version>0.8.1</version>

<executions>

<execution>

<phase>integration-test</phase>

<goals>

<goal>generate</goal>

</goals>

</execution>

</executions>

</plugin>

</plugins>

</build>
```

This causes the benerator goal 'generate' to be executed whenever integration tests are run. For more information on binding a plugin to phases in the lifecycle, please refer to the Build Lifecycle documentation.

For more information, see Maven's "Guide to Configuring Plug-ins": [http://maven.apache.org/guides/mini/guide-configuring-plugins.html](http://maven.apache.org/guides/mini/guide-configuring-plugins.html)
Instructions
------------

Create a database snapshot by typing 'mvn benerator:dbsnapshot' on the command line.
By editing the 'maven-benerator-plugin' of 'pom.xml' you can change database login data and
the export file format.
If you need to use another logging framework, change the 'pom.xml's dependency to slf4j-log4j12 and log4j.
Instructions
------------

Start database data generation by typing 'mvn benerator:generate' on the command line.
Benerator will then create database tables of the databene shop example and populate them
with data amounts configurable in the file 'benerator.properties'.

If you need to use another logging framework, change the 'pom.xml's dependency to slf4j-log4j12 and log4j.
You can remove dependencies to the unused databases from 'pom.xml' as well.

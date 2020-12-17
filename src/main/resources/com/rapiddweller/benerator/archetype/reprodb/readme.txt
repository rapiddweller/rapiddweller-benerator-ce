Instructions
------------

Start database data generation by typing 'mvn benerator:generate' on the command line.
Benerator will then create database tables and populate them with the data of database snapshot.

For adding arbitrary amounts of data, the file 'benerator.xml' contains empty templates for 
all tables it has found: Their count is set to zero, and for each table column, there is an <id>, 
<attribute> or <reference> entry which is commented out. Adapt these templates to your needs if necessary.

If you need to use another logging framework, change the 'pom.xml's dependency to slf4j-log4j12 and log4j.
You can remove dependencies to the unused databases from 'pom.xml' as well.

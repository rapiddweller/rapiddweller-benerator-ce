# Troubleshooting

## (Out of) Memory

If you get an OutOfMemoryError, first increase the Java heap size by an -Xmx environment setting (e.g. by adding -Xmx1024m to the BENERATOR_OPTS in
the script files).

Another potential cause for OutOfMemoryErrors is application of distributions to very large data sets. Most sequences and all other types of
distribution require the source data to fit into RAM. So either use an 'unlimited' sequence like 'expand', 'repeat' or 'randomWalk' or simply repeat
data set iteration by adding cyclic="true" to the configuration.

## temp Directory

On some environments, the temp directory has a very restrictive disk quota. If you need more space for data generation, you can specify another
directory by the -Djava.io.tmpdir environment setting (e.g. by adding -Djava.io.tmpdir=/User/me/mytemp to the BENERATOR_OPTS in the script files)

## File Encoding

If no file encoding was specified, benerator uses the default file encoding of the system it runs on - except if the file itself contains encoding
info (like XML).

If all used files have the same encoding and it is different to your system's encoding, you can change set benerator's default encoding by the
-Dfile.encoding environment setting (e.g. by adding -Dfile.encoding=iso-8859-1 to the BENERATOR_OPTS in the script files)

When generating data in heterogeneous environments, it is good practice to set the defaultEncoding property of the benerator descriptor file's root
element. If only single files have a different encoding, you can specify an encoding properts for all built-in file importers and file-based
consumers.

A typical error that may arise from wrong file encoding configuration is that file import (e.g. for a CSV file) stops before the end of file is
reached.

## Logging

benerator logs its event using apache commons-logging. That service forwards output to Apache log4j or to the native JDK 1.4 logging. For avoiding
version conflicts with your environment, benerator uses JDK 1.4 logging by default, but for troubleshooting it is useful to switch to Log4j as the
underlying logging implementation and fine-tune log messages for tracking down your problem. In order to use log4j, download the binary of a new
version (e.g. log4j 1.2.15) from the Apache log4j 1.2 website, uncompress it and put the jar file log4j-1.2.15.jar into benerator's lib directory.
Edit the log4j.xml file in your BENERATOR_HOME/bin directory to adapt the log levels for interesting categories:

Set a category to debug for getting detailed information about its execution. The most important log categories are:

| name | description |
| --- | --- |
| com.rapiddweller.benerator.main | Events of benerator's main classes, e.g. detailed information about which entities are currently generated |
| com.rapiddweller.benerator.STATE | generator state handling for information which component generator caused termination of the composite generator |
| com.rapiddweller.benerator.factory | Creating generators from descriptor information |
| com.rapiddweller.benerator | Top-level directory for all generators and main classes |
| com.rapiddweller.SQL | SQL commands, e.g. DDL, queries, inserts, updates |
| com.rapiddweller.JDBC | JDBC operations, e.g. connection / transaction handling |
| com.rapiddweller.jdbacl.model.jdbc | Database meta data import |
| com.rapiddweller.platform.db | All database related information that does not fit into the SQL or JDBC category |
| com.rapiddweller.platform.xml | XML-related activities |
| com.rapiddweller.domain | benerator domain packages |
| com.rapiddweller.model descriptor | related information |
| com.rapiddweller.common | low-level operations like data conversion |

## Locating Errors

When configuring data generation you are likely to encounter error messages.

Depending on the settings it may be difficult to find out what caused the problem. For tracking database-related errors, set batch="false" in
your `<database>` setup and use pagesize="1" in the `<generate>`. These are default settings, so you do not need to specify them explicitly if you did
not change the default.

If that alone does not help, set the log category `com.rapiddweller.benerator.main` to debug level to find out which element caused the error. If there
is a stack trace, check it to get a hint which part of the element's generation went wrong. If that does not help, remove one attribute/reference/id
after the other for finding the actual troublemaker. If you still cannot solve the problem, post a message in the benerator forum. You can check out
the benerator sources from the SVN source repository, open it in Eclipse and debug through the code.

## Database Privilege Problems

When importing database metadata, you might encounter exceptions when Benerator tries to get metadata of catalogs or schemas it has no access
privileges to.

Usually can fix this by choosing the right schema for your database, e.g.

```xml
<database id="db" ... schema="PUBLIC" />
```

If you are not sure which schema is applicable in your case, edit the logging configuration in log4j.xml (as described above) and set the category **
com.rapiddweller.platform.db** to **debug**.

You will then get a list of schemas as Benerator scans the database metadata, e.g. for an Oracle system:

```sql
06:03:45,203 DEBUG [DBSystem] parsing metadata...

06:03:45,203 DEBUG [JDBC] opening connection to jdbc:oracle:thin:@10.37.129.3:1521:XE

06:03:45,226 DEBUG [JDBC] Created connection #4: oracle.jdbc.driver.T4CConnection@741827d1

06:03:45,385 DEBUG [JDBC] opening connection to jdbc:oracle:thin:@10.37.129.3:1521:XE

06:03:45,417 INFO [JDBCDBImporter] Importing database metadata. Be patient, this may take some time...

06:03:45,417 DEBUG [JDBCDBImporter] Product name: Oracle

06:03:45,419 INFO [JDBCDBImporter] Importing catalogs

06:03:45,430 INFO [JDBCDBImporter] Importing schemas

06:03:45,438 DEBUG [JDBCDBImporter] found schema ANONYMOUS

06:03:45,438 DEBUG [JDBCDBImporter] found schema DBSNMP

06:03:45,438 DEBUG [JDBCDBImporter] found schema DIP

06:03:45,438 DEBUG [JDBCDBImporter] found schema FLOWS_FILES

06:03:45,439 DEBUG [JDBCDBImporter] found schema FLOWS_020100

06:03:45,439 DEBUG [JDBCDBImporter] found schema HR

06:03:45,439 DEBUG [JDBCDBImporter] found schema MDSYS

06:03:45,440 DEBUG [JDBCDBImporter] found schema OUTLN

06:03:45,440 DEBUG [JDBCDBImporter] found schema SHOP

06:03:45,440 DEBUG [JDBCDBImporter] importing schema SHOP

06:03:45,441 DEBUG [JDBCDBImporter] found schema SYS

06:03:45,441 DEBUG [JDBCDBImporter] found schema SYSTEM

06:03:45,441 DEBUG [JDBCDBImporter] found schema TSMSYS

06:03:45,441 DEBUG [JDBCDBImporter] found schema XDB
```

Cross checking this with your access information should make it easy to figure out which one is appropriate in your case.

See the

Usual Database Settings

.

## Constraint Violations

Some constraint violations may arise when using database batch with nested create-entities. Switch batch off. If the problem does not occur any more,
stick with non-batch generation. Otherwise you need further investigation. When using Oracle, a constraint violation typically looks like this:

```java
java.sql.SQLException: ORA-00001: Unique Constraint (MYSCHEMA.SYS_C0011664) violated
```

It contains a constraint name you can look up on the database like this:

```sql
select * from user_constraints where constraint_name like '%SYS_C0011541%'
```

The query result will tell you the table name and the constraint type. The constraint types are encoded as follows:

P: Primary key constraint

U: Unique constraint

R: Foreign key constraint

## 'value too large for column' in Oracle

Depending on the character set, oracle may report a multiple of the real column with, e.g. 80 instead of 20\. So, automatic generation of varchar2
columns may fail. This typically results in Exceptions like this:

```java
java.sql.SQLException: ORA-12899: value too large for column "SCHEM"."TBL"."COL" (actual: 40, maximum: 10)
```

This is Oracle bug #4485954,
see [http://www.oracle.com/technology/software/tech/java/sqlj_jdbc/htdocs/readme_jdbc_10204.html](http://www.oracle.com/technology/software/tech/java/sqlj_jdbc/htdocs/readme_jdbc_10204.html)
and

[http://kr.forums.oracle.com/forums/thread.jspa?threadID=554236](http://kr.forums.oracle.com/forums/thread.jspa?threadID=554236)
.

The solution is using the newest JDBC driver, at least 10.2.0.4 or 11.0\. BTW: It is backwards compatible with the Oracle 9 databases.

## Time is cut off in Oracle dates

Oracle changed back and forth the mapping of internal types to JDBC times in Oracle 8 and 11, the mapping in Oracle 9 and 10 is wrong,
see [http://www.oracle.com/technetwork/database/enterprise-edition/jdbc-faq-090281.html#08_00](http://www.oracle.com/technetwork/database/enterprise-edition/jdbc-faq-090281.html#08_00)
. In order to fix the behaviour, use the newest available Oracle 11 JDBC driver you can get, it is backwards compatible down to Oracle 9 and provides
a proper mapping of the date type for all Oracle database versions.

## Composite Keys

Benerator expects single-valued ids. It does not automatically support composite keys and composie references

Since composite keys typically have a business meaning, most composite keys cannot be automatically generated. So there is no need to support this.

If you encounter a composite key, manually configure how to create each key component.

## Importing Excel Sheets

For a beginner it is sometimes confusing, how Benerator handles imported Excel sheets. For this task it completely relies on the cell type configured
in the original sheet. So if you have a Date cell in the Excel sheet and format it as number or text, benerator will interpret it as double or string.

Another popular error comes from columns that contain long code numbers and have the the default format: They are imported as numbers and e.g. leading
zeros are lost. In such case explicitly format the column as text in Excel.

Apache POI represents all numbers as variables of type 'double'. So there are numbers which are simple in decimal format but not so in binary: when
importing the number 1.95 from an Excel sheet, the user gets a value of 1.95000000002\. For now you need to round the values yourself, e.g. by a
converter.

## Number Generation on Oracle

When generating very large decimal values (> 1.000.000.000) in an Oracle database you may observe that smaller numbers are written to the database,
losing some trailing zeros or even cutting the whole number to a decimal with one prefix digit and several fractional digits. This results from a bug
in Oracles older JDBC drivers and can be fixed by using the newest driver version (note that you can even use 11.x JDBC drivers for 10.x databases).

## Unknown Column Type

If your application makes use of a database's proprietary column types, you may run into an exception when Benerator encounters it. If you know how to
create and handle data for this column type, you can do so by configuring the database to accept unknown column types :

```xml
<database ... acceptUnknownColumnTypes="true">
```

## Table 'X' not found in the expected catalog 'Y' and schema 'Z'

This message tells you, that you database configuration is wrong. Check and fix the 'schema' and 'catalog' settings in your database configuration,
e.g.

```xml
<database … catalog="main" schema="Z" />
```

or your environment configuration, e.g. xyz.env.properties:

```properties
db_catalog=main

db_schema=Z
```

Note: On most systems (e.g. Oracle, HSQL) no catalog needs to be specified.
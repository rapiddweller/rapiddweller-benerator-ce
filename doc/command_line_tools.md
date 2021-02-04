# Command Line Tools

## Benerator

Benerator expects a descriptor file name as the only command line parameter, e.g. on Windows systems

<div class="termy">
```bash
benerator test.ben.xml
```
</div>

or, on Unix and Mac OS X systems,

```bash
benerator test.ben.xml
```

You can change default behavior by Java VM parameters, e.g.

```bash
benerator.bat -Dfile.encoding=iso-8859-1 -Djava.io.tmpdir="C:\temp" test.ben.xml
```

Validation can be turned off from the command line alternatively using a VM parameter:

```bash
mvn benerator:generate -Dbenerator.validate=false
```

or

```bash
benerator myproject.ben.xml -Dbenerator.validate=false
```

## DB Snapshot Tool

The DbSnaphotTool creates a snapshot of a full database schema and stores it in a DbUnit XML file. It is invoked from the command line in Windows by
calling

`snapshot [VM-params] export-filename.dbunit.xml`

or, on Unix and Mac OS X systems,

`sh snapshot [VM-params] export-filename.dbunit.xml`

If the export filename is left out, the snapshot will be stored in a file called snapshot.dbunit.xml.

You need the following VM parameters to configure database access. Use them like -Ddb.user=me:

| Parameter | Description |
| --- | --- |
| dbUrl | The JDBC URL of the database |
| dbDriver | The JDBC driver class name |
| dbUser | user name |
| dbPassword | user password |
| dbSchema | Name of the schema to extract (defaults to the user name) |

## XML Creator

The XMLCreator reads a XML Schema file and creates a number of XML files that comply to the schema. It can read XML annotations which provide
benerator configuration in the XML schema file. It is invoked from the command line and has the following parameter order:

```bash
createxml <schemaUri> <root-element> <filename-pattern> <file-count> [<properties file name(s)>]
```

Their meaning is as follows:

* schemaUri: the location (typically file name) of the XML schema file

* root-element: the XML element of the schema file that should be used as root of the generated XML file(s)

* filename-pattern: the naming pattern to use for the generated XML files. It has the form of a java.text.MessageFormat pattern and takes the number
  of the generated file as parameter {0}.

* file-count: the number of XML files to generate

* properties file name(s): an optional (space-separated) list of properties files to include in the generation process

Under Windows, an example call would be:

```bash
createxml myschema.xsd product-list products-{0}.xml 10000 perftest.properties
```

or, on Unix and Mac OS X systems,

```bash
sh myschema.xsd product-list products-{0}.xml 10000 perftest.properties
```

for generation 10,000 XML files that comply to the XML Schema definition in file myschema.xsd and have product-list as root element. The files will be
named products-1.xml, products-2.xml, products-3.xml, ...
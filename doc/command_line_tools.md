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


## Benchmark Tool

Benerator provides a Benchmark Tool to assess and compare the performance 
of typical generation or anonymization approach. 

It is of special use for you if you want to assess the generation/anonymization 
performance of different hard- and software settings, like number sof cores, 
operating system, Java virtual machine, system software configuration and 
Benerator Enterprise Edition's multithreading configuration.

The different benchmarks used performs a list of predefined typical generation 
and anonymization tasks. 

To invoke the Benchmark Tool with standard settings, just open a text console 
or terminal and enter

`benerator-benchmark`

Then the benchmark runs for a few minutes and prints a measurement summary.
In Benerator Consumer Edition, only single-threaded execution is supported, 
so the report will look small like this:

```bash
+---------------------------------------------------------------------------+
| Benchmark throughput of Benerator Community Edition 1.2.0-jdk-11-SNAPSHOT |
| on a Mac OS X 11.4 aarch64 system with 4 cores                            |
| Java version 11.0.11                                                      |
| JVM OpenJDK 64-Bit Server VM 11.0.11+9-LTS (Azul Systems, Inc.)           |
| Date/Time: 2021-09-14T17:20:11.228901+02:00[Europe/Berlin]                |
|                                                                           |
| Numbers are million entities generated per hour                           |
+----------------------------------------------------------------+----------+
| Benchmark                                                      | 1 Thread |
+----------------------------------------------------------------+----------+
| gen-string.ben.xml                                             |       59 |
+----------------------------------------------------------------+----------+
| gen-person.ben.xml                                             |      124 |
+----------------------------------------------------------------+----------+
| anon-person-showcase.ben.xml                                   |      121 |
+----------------------------------------------------------------+----------+
| anon-person-hash.ben.xml                                       |      562 |
+----------------------------------------------------------------+----------+
| anon-person-constant.ben.xml                                   |    3,944 |
+----------------------------------------------------------------+----------+
```

In the header the system settings are reported, then each of the following rows 
displays the benchmark name and its performance, measured in million entities 
generated per hour. 
This means for example that the anon-person-constant.ben.xml anonymizes 
3,944 million = 3.944 billion data sets per hour running in a single thread.

The performance numbers above have been measured on a plain Macbook Air M1 of 2020.

For a Benerator Enerprise Edition installation running on a machine with several cores, 
the benchmark is executed for several characteristic threading settings in order to 
find the sweet spot of threading settings. 

A benchmark run on the same system with Benerator Enterprise Edition yields 
the following result:

```bash
+-----------------------------------------------------------------------------+
| Benchmark throughput of Benerator Enterprise Edition 1.2.0-jdk-11-SNAPSHOT  |
| on a Mac OS X 11.4 aarch64 system with 8 cores                              |
| Java version 11.0.11                                                        |
| JVM OpenJDK 64-Bit Server VM 11.0.11+9-LTS (Azul Systems, Inc.)             |
| Date/Time: 2021-09-14T17:52:27.582424+02:00[Europe/Berlin]                  |
|                                                                             |
| Numbers are million entities generated per hour                             |
+------------------------------+----------+-----------+-----------+-----------+
| Benchmark                    | 1 Thread | 2 Threads | 4 Threads | 6 Threads |
+------------------------------+----------+-----------+-----------+-----------+
| gen-string.ben.xml           |      339 |       495 |     1,007 |       992 |
+------------------------------+----------+-----------+-----------+-----------+
| gen-person.ben.xml           |      167 |       307 |       361 |       267 |
+------------------------------+----------+-----------+-----------+-----------+
| anon-person-showcase.ben.xml |      164 |       265 |       348 |       250 |
+------------------------------+----------+-----------+-----------+-----------+
| anon-person-hash.ben.xml     |    1,305 |     1,557 |     1,388 |     1,457 |
+------------------------------+----------+-----------+-----------+-----------+
| anon-person-constant.ben.xml |    3,866 |     4,898 |     2,326 |     1,960 |
+------------------------------+----------+-----------+-----------+-----------+
```

Note that we not only have improved the performance of the Community Edition, 
but optimized the Enterprise Edition to be even several times faster than 
the Community Edition.

For your performance optimization in Enterprise Edition, note that with additional 
threads comes additional performance, but after a certain level of concurrency 
is reached, performance does not improve or even can deteriorate seriously.

The sweet spot where you have optimum performance with low concurrency usually 
is where the number of threads equals the number of cores, or is only slightly larger. 
As you might guess from the performance, the test laptop has 4 cores. 
Actually it has more, but its 4 high performance cores are the only ones which matter 
for generation and anonymization performance.

The Benchmark Tool has some command line parameters to configure its test runs. 
For a short summary, type ```benerator-benchmark --help```

The general invocation format is

```benerator-benchmark [options]```

The command line options are as follows:


| Option | Meaning | Default Setting |
| --- | --- | --- |
| --ce | Run on Benerator Community Edition (CE) | This is the default on CE |
| --ee | Run on Benerator Enterprise Edition (EE) | This is the default on EE and only available on EE |
| --minSecs n | Choose a workload to have the benchmark run at least n seconds | 30 |
| --maxThreads k | Use only up to k cores for testing | a bit more than the number of reported cores |
| --help | print this help |

A **--minSecs** settings of 30 requires the benchmark to run with a workload 
that needs at least 30 seconds to process. It is advisable to choose times 
which are sufficiently large that the fix initialization time of Benerator 
has less impact on the measurement and that JVM hot spot optimizers get some 
time to make Benerator run even more efficiently.

By default, the Benchmark tool test thread settings from single-threaded to a concurrency 
slightly larger than the number of cores of the system it is running on. Unfortunately, 
some systems report a higher number of cores than are available for our tests.
For example a Macbook Air M1 of 2020 reports to have 8 cores, but only 4 of them are high 
performance cores, 4 are efficiency cores which do not contribute performance to for 
Benerator. So a setting of **--maxTreads 6** makes the Benchmark go up only to 6 threads 
instead of 10 threads it would have taken by default.

The reports above have been created using

```benerator-benchmark --ce --minDurationSecs 30 --maxThreads 6```

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
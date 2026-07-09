# Command Line Tools

## Benerator

Benerator can be called with a Benerator file name as command line parameter, like

```bash
benerator test.ben.xml
```

If no file is specified, Benerator expects a file `benerator.xml` 
in the current directory. 

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

You can specify the following options on the command line:

| Option | Description | Remarks |
| --- | --- | --- |
| --version,-v  | Display system and version information | |
| --help,-h     | Display help information | |
| --list <type> | List the available environments or systems. &lt;type&gt; may be `env`, `db` or `kafka`. | |
| --clearCaches | Clear all caches | |
| --mode <spec> | Activate Benerator mode `strict`, `lenient` or `turbo` | default is `lenient` |


## DB Snapshot Tool

The DbSnapshotTool creates a snapshot of a full database schema and stores it in a DbUnit XML file. It is invoked from the command line in Windows by
calling

`snapshot [VM-params] export-filename.dbunit.xml`

or, on Unix and Mac OS X systems,

`sh snapshot [VM-params] export-filename.dbunit.xml`

If the export filename is left out, the snapshot will be stored in a file called `snapshot.dbunit.xml`.

You need the following VM parameters to configure database access. 

Use them like `-Ddb.user=me`:

| Parameter | Description |
| --- | --- |
| dbUrl | The JDBC URL of the database |
| dbDriver | The JDBC driver class name |
| dbUser | user name |
| dbPassword | user password |
| dbSchema | Name of the schema to extract (defaults to the user name) |


## Benchmark Tool

Benerator provides a Benchmark Tool to assess and compare the performance 
of a typical generation or anonymization approaches. 

It is of special use for you if you want to assess the generation/anonymization 
performance of different hard- and software settings, like numbers of cores, 
operating system, Java virtual machine and system software configuration.

The different benchmarks used perform a list of predefined typical generation 
and anonymization tasks. 

To invoke the Benchmark Tool with standard settings, just open a text console 
or terminal and enter

`benerator-benchmark`

Then the benchmark runs for a few minutes and prints a measurement summary.
The report will look something like this:

```text
+---------------------------------------------------------------------------+
| Benchmark throughput of Benerator Community Edition 3.1.0-jdk-11          |
| on a Mac OS X 11.4 x86_64 with 8 cores                                    |
| Java version 11.0.11                                                      |
| OpenJDK 64-Bit Server VM 11.0.11+9 (AdoptOpenJDK)                         |
| Date/Time: 2021-09-17T10:41:43.510603+02:00[Europe/Berlin]                |
|                                                                           |
| Numbers are reported in million entities generated per hour               |
+----------------------------------------------------------------+----------+
| Benchmark                                                      | 1 Thread |
+----------------------------------------------------------------+----------+
| gen-string.ben.xml                                             |       37 |
+----------------------------------------------------------------+----------+
| gen-person-showcase.ben.xml                                    |       26 |
+----------------------------------------------------------------+----------+
| anon-person-showcase.ben.xml                                   |       31 |
+----------------------------------------------------------------+----------+
| anon-person-regex.ben.xml                                      |      346 |
+----------------------------------------------------------------+----------+
| anon-person-hash.ben.xml                                       |      386 |
+----------------------------------------------------------------+----------+
| anon-person-random.ben.xml                                     |      576 |
+----------------------------------------------------------------+----------+
| anon-person-constant.ben.xml                                   |    2,210 |
+----------------------------------------------------------------+----------+
```

In the header the system settings are reported, then each of the following rows 
displays the benchmark name and its performance, measured in a million entities 
generated per hour. 
This means for example that the `anon-person-constant.ben.xml` anonymizes 
2,210 million = 2.210 billion data sets per hour running in a single thread.

The performance numbers above have been measured on a plain Macbook Air M1 of 2020.

The Benchmark Tool has some command line parameters to configure its test runs. 
For a short summary, type ```benerator-benchmark --help```

The general invocation format is

```shell
benerator-benchmark [options] [name]
```

Name is an optional benchmark name. When specified, only this benchmark is executed. When left out, 
all benchmarks applicable to the given configuration are called.

Example: In order to only call the `gen-string` benchmark, type

```shell
benerator-benchmark [options] [name]
```

The command line options are as follows:

| Option | Meaning | Default Setting |
| --- | --- | --- |
| --minSecs n | Choose a workload to have the benchmark run at least n seconds | 10 |
| --maxThreads k | Use only up to k cores for testing | a bit more than the number of reported cores |
| --env &lt;spec&gt;  | Runs the tests applicable to the specified system(s). &lt;spec&gt; may be an environment name, a system (denoted by environment#system) or a comma-separated list of these (without whitespace) |
| --mode m | activates Benerator mode `strict`, `lenient` or `turbo` | `lenient` |
| --list   | lists the names of the predefined benchmarks |
| --help   | print this help |

A **--minSecs** settings of 30 requires the benchmark to run with a workload 
that needs at least 30 seconds to process. It is advisable to choose times 
which are sufficiently large that the fix initialization time of Benerator 
has less impact on the measurement and that JVM hotspot optimizers get some 
time to make Benerator run even more efficiently. 
If --minSecs is not specified, a default of 10 seconds is used, which is too 
short for optimum measurements, but was chosen as a defensive measure to 
save your system's hard drive space: The file generation benchmarks could easily 
fill up a terabyte of hard disk space in a few minutes. 
Even on standard developer hardware, a file export of one minute can produce 
a file of a size of 10 Gigabytes. Generated files are deleted automatically 
after each benchmark run, but take care not to fill up your disk during a 
benchmark run.

By default, the Benchmark tool tests thread settings from single-threaded to a concurrency 
slightly larger than the number of cores of the system it is running on. Unfortunately, 
some systems report a higher number of cores than are available for our tests.
For example a Macbook Air M1 of 2020 reports to have 8 cores, but only 4 of them are high 
performance cores, 4 are efficiency cores which do not contribute performance to 
Benerator. So a setting of `--maxTreads 6` makes the Benchmark go up only to 6 threads 
instead of 10 threads it would have taken by default.

The reports above have been created using

`benerator-benchmark --minDurationSecs 30 --maxThreads 6`


### Assessing Database Performance

In order to assess database processing performance, you need to configure the relevant database(s) 
in an environment repository, see [Environment Files](environment_files.md) for details. 

For a quick start, the Benchmark Tool comes with two built-in databases `h2` and `hsqlmem` in an environment `bultin`. 
This will run the database benchmarks on the built-in h2:
```shell
benerator-benchmark --builtin#h2
```

In order to test all systems defined in the environment `builtin`, specify just the environment name

```shell
benerator-benchmark --dev builtin
```

You can test your own databases in a similar manner by defining them in an environment file 
(eg. `local.env.properties`) and providing it in the command line options as above. 

You will notice that the database benchmark may run significantly longer than the simple tests. 
This is caused by measuring two access types, 'read' and 'write' and requiring each of these 
to run at least 'minSecs' seconds. For some databases, writing is significantly slower than 
reading (up to a factor of 20), so that you need to write data for 10 minutes in order to read 
data for 30 seconds. The database benchmarks alleviate that a bit, by performing two reads for 
each write (effectively halving execution time), but still will take long time. 
So please be patient.


## XML Creator

The XMLCreator reads a XML Schema file and creates a number of XML files that comply with the schema. It can read XML annotations which provide
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

for generation 10,000 XML files that comply with the XML Schema definition in file `myschema.xsd` and have product-list as root element. The files will be
named `products-1.xml`, `products-2.xml`, `products-3.xml`, ...
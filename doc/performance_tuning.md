# Benerator Performance Tuning

## Using Enterprise Edition

Benerator Enterprise Edition enables you to generate and anonymize data concurrently,
in multiple threads. More than this, its engine and many specialized data generators 
had significant performance improvements compared to the Community Edition. 
See the [Benchmark Tool Documentations](command_line_tools.md#benchmark-tool) 
to get some performance comparisons.

## pageSize (database and other transactional systems only)

'pageSize' is Benerator's abstraction of a kind of bracket put around a group of data objects, 
for databases and Kafka usually this bracket is mapped to a transaction. 
For example a pageSize of 10 for a database means that each group of 10 objects is committed 
to the database in one transaction. Transactions incur processing overhead, so performance 
improves if the number of transactions is reduced. This is achieved by using a larger pageSize.
  
By default, Benerator uses a defensive pageSize of 1, processing each entity in an own transaction, 
because this simplifies error diagnosis when working out a generation/anonymization process. 
But this has a tremendous impact on performance. When configuration is complete, and you need 
larger performance, use a large pagesize for critical `<generate>` and `<iterate>` elements, e.g. 
`<generate type="db_user" count="1000000" consumer="db" pagesize="1000">`

## JDBC batch (database only)

JDBC batches provide for significantly better database insertion performance than standard operation. In Benerator this is turned off by default, since
error messages that arise from bad generation setup are much harder to analyze in batch mode. When you are finished with defining data generation and
need performance for mass data, you can activate batch operation by the batch attribute of the database element:

```xml
<database ... batch="true" />
```

Benerator is optimized for performance. Thus, you may get problems when combining nested `<generate>` elements with batching. It typically results in
exceptions that indicate a violation of a foreign-key constraint.

## Query fetch size (database only)

If you are querying data with large result sets from your database, e.g. when anonymizing 
production data, you should tune the database's fetch size.
It determines how many rows are transmitted from the database to Benerator when accessing 
the first query result. So it reduces the number of network roundtrips. 
Benerator uses a fetch size of 100 by default which should be useful in most cases.

You can experiment with higher values, e.g. 1000 by configuring the **fetchSize and batch** 
attributes of the database element:

```xml
<database ... fetchSize="1000" batch="true" />
```

This is especially useful if the database is accessed over a slow network connection and 
query result sets are at least as large as the fetch size and are iterated to a relevant extent. 
When setting the fetch size to a value that is too high, performance may actually decrease.


## Restrict logging

Logging data generation/anonymization details may deteriorate performance tremendously.
Benerator is shipped with efficient logging settings, but if you changed the settings
in the process of debugging your data generation/anonymization, make sure to increase
log filters to `info` level.
A high level like 'error' usually do not have a performance impact, but may hide issues
which are reported using 'warn', so you should not be too restrictive about logging.


## Id Generation

The most efficient id generation strategy is increment since it works without connecting the database. It works fine for multithreaded generation,
too. But for concurrent execution of multiple Benerator processes or continuation of a canceled generation process you need an id generation that is
unique among several runs. The most efficient id strategies with such behavior are seqhilo (database-based) and uuid (universally unique string id).


## Relational Data Generation (database)

Nesting `<generate>` elements greatly simplifies development and maintenance of a data generation 
project but can decrease generation speed. 
At critical generation steps you might want to sacrifice maintainability for speed by replacing nested 
structures with 'relational' structures, e.g. replacing this code:

```xml
<generate name="db_customer" count="1000000" consumer="db">
    <generate name="db_order" minCount="0" maxCount="20 " consumer="db">
        <attribute name="customer_fk" script="{db_customer.id}"/>
        ...
    </generate>
</generate>
```

with something like:

```xml
<generate name="db_customer" count="1000000" consumer="db" />

<generate name="db_order" count="10000000" consumer="db">
    <reference name="customer_fk" source="db" targetType="db_customer" distribution="random" />
    ...
</generate>
```

## Java Virtual Machine (JVM) Products

Different Java Virtual Machines provide different performance.
Changing the JVM may easily impact Benerator performance by 20%,
up to more than 100% in exceptional cases.

You can choose among a wide range of JVM products and versions. 
Well known established and reliable products/providers are
OpenJDK, OpenJ9 (IBM), GraalVM (Oracle) and Azul. Different 
versions of a product may also exhibit notably different 
performance. 

Unfortunately, there is not an optimal JVM and version for 
any task you might want to perform: Some JVMs will perform 
significantly faster in single threaded execution but others 
will be better in multithreaded execution.


## Virtualization Costs

Running Benerator in a virtualization system will always have 
a certain performance impact. For example, in 2020 Apple 
discontinued the use of Intel processors and introduced 
a new architecture using M1 chips in its Mac products. 
Programs written for the Intel architecture are still supported, 
but run with in a virtualization system, Rosetta. 
Though Rosetta's performance is impressive, there is a 
performance cost which gives JVMs compiled for M1 an advantage 
over JVMs for the Intel architecture on a Mac. 
In a simple non-representative test, Azul's M1 JVM was 
at least 20% faster than OpenJDK for Intel (on average).

The performance cost of a container like Docker is small 
and usually outweighed by the other advantages this software 
offers in enterprise computing.


## Scripts

When using other script languages than rapiddwellerScript, the scripts expressions 
are parsed at runtime, thus are significantly slower than compiled code. 
For performing CPU-intensive operations or excessive looping, use rapiddwellerScript 
or program a Java task (See '[Custom Tasks](extending_benerator.md#custom-tasks)').


## Parsing (Oracle) metadata

On databases with many tables, scanning metadata can take several minutes. One source of superfluous tables is the Oracle recyclebin. You can speed up
parsing by including a 'purge recyclebin' in your SQL scripts.

Using regular expressions in the `<database>`'s excludeTables and includeTables settings, you can restrict the amount of metadata to be parsed.

If metadata retrieval still takes too long, you can use `<database... metaCache="true" />` for storing metadata in a cache file on your local hard
drive.

When Benerator executes SQL with an `<execute>` statement, it analyzes if the database structure is modified. In that case, the cached database 
metadata is invalidated and parsed again when they are needed the next time. If you are certain that the change is irrelevant to subsequent generations steps,
you can suppress the invalidation by

`<execute invalidate="false">`ALTER TABLE...`</execute>`.


## Anonymization Algorithms

Different anonymization algorithms have different performance:
An anonymization that takes care of consistency and 'pretty' output
useful for showcases must be slower than a stupid one which overwrites
the imported data with purely random data.

Advice: Do not exaggerate on anonymization performance optimization!
There are always independent data fields in a software, which will cause
consistency errors and program failure in the target system if they have
not been assessed properly.
This is what prototype-based generation was invented for!

Only **after** you have set up anonymization that fits consistency
requirements, checked for other JVMs, have applied other performance
improvement strategies, maybe tried scaling over multiple threads
in Enterprise Edition, you may fiddle around with anonymization
of single fields according to the following information.

Here some anonymization approaches in the order of increasing performance:

| Approach  | Showcase Applicability | Perftest Applicability | Speed Factor |
| ---       | ---                    | ---                    | ---:  |
| prototype | great                  | good                   |   1   |
| regex     | often passable         | passable               |   6   |
| hash      | poor                   | passable               |   7   |
| random    | poor                   | passable               |   9   |
| constant  | poor                   | poor                   |  14   |

For showcase generation, in which a user is faced with anonymization results,
only a prototype or regex approach makes sense.

When anonymizing data for performance tests, one concern is how good the results of
the chosen anonymization match the real world's data characteristics, especially
for fields used in caches and indexes. A constant approach will make indexes and
caches useless as well as the performance test results.

Using Regex, Hash or Random will provide you with a bigger diversity of data
than the real world, making the data slightly faster to look up in an index.

You see that the generation of constants is super-fast but useless in most cases.

In many cases, a regular expression is a good compromise between anonymization
performance and applicability for showcases and performance tests.

For example, a setup like this

```xml
    <iterate type='Person' source="..." ...>
        <attribute name='streetAndNumber' pattern='[A-Z][a-z]{4,10} Street [1-9][0-9]?'/>
        <attribute name='zip'             pattern='[0-9]{5}'/>
        <attribute name='city'            pattern='[A-Z][a-z]{4,15}'/>

        <attribute name='email'           pattern='[a-z]{4,10}@[a-z]{4,10}\.(com|org|net)'/>
        <attribute name='workPhone'       pattern='[0-9]{8,12}'/>

        <attribute name='firstName'       pattern='[A-Z][a-z]{1,8}'/>
        <attribute name='midName'         pattern='[A-Z][a-z]{1,8}'/>
        <attribute name='lastName'        pattern='[A-Z][a-z]{1,12}'/>

        <attribute name='CardExp'         pattern='([1-9])|(1[0-2])/2[1-9]'/>
        <attribute name='Cardno'          pattern='[0-9]{16}'/>
    </iterate>
```

produces results like this

```text
Person[
    streetAndNumber Hxpji Street 3
    zip             27200
    city            Ccxowbyozqti 
    email           likoakqcy@nyelekuki.org 
    workPhone       599209827
    firstName       Ogszcy
    midName         Jacxmni
    lastName        Pnn
    CardExp         12/24
    Cardno          2390195664099922
]
```

You see that usually regex expressions are a viable approach for generating codes like 
zip codes and phone numbers, but relatively poor for name-like data, but often acceptable.

In order to get an idea of the costs of different anonymization approaches,
check the performance numbers in the 
[Benchmark Tool Documentation](command_line_tools.md#benchmark-tool) or run it on
your system to get an individual insight into your machine's capabilities.


## Benerator Modes

Benerator can run in different modes, which have different performance and strictness characteristics. 

A mode ist activated by the command line argument `--mode`, eg.

```shell
benerator --mode turbo
```

### lenient

The default mode, Benerator is running in. The full feature set is supported and some configuration issues 
are handled automatically. This is best for users at the beginning of the learning curve.

### strict
Benerator stops on configurations which may have performance implications or may possibly be unintended, 
assuring the full feature set can run with optimum performance. In many cases this will be faster than 
lenient mode, but requires more knowledge about Benerator.

### turbo
Experimental feature: The fastest mode which in which Benerator can run, but this comes at a price: 
It uses a restricted feature set: capitalization of variables is ignored and simple-type sub elements 
may not occur multiple times. 
This will work fine for most projects in most homogeneous environments, involving only databases 
and plain file formats. 
For strongly heterogeneous projects, especially involving XML this may produce unintended results. 
Use this only when you are desperate for performance and have made sure everything works fine 
in`strict` mode.


## Distributed Generation

You can distribute generation over several machines. In oder to do so you will need two types of descriptor files: 

First an initialization descriptor
that initializes the systems and generates the deterministic core data, 

Second a mass data description file. The initialization file will need to run
first on a single machine, then the mass data file can be executed on multiple processes or machines concurrently. For mass data generation you will
need to take special care:

Choose an id generation strategy that is able to create unique ids under these circumstances 
(see '[Generating IDs](generating_unique_data.md#id-generation)').


## Distributed Anonymization

You can distribute anonymization over several machines. 
A simple and scalable approach is the use of a Kafka cluster: 
Using a Kafka connector, you can export the data you need to anonymize(eg. from a database table) 
to a Kafka queue and run several machines with Benerator instances that listen to the Kafka queue, 
import data, anonymize it and export it to the destination system directly or indirectly 
via another Kafka queue and Kafka connector.


## Ask the experts

Feel free to contact us for assistance on performance improvement: **[team@rapiddweller.com](mailto:team@rapiddweller.com)**.

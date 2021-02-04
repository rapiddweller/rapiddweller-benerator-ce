# Benerator Performance Tuning

## Performance

benerator is regularly optimized for performance, so single threaded data generation is efficient enough in the most cases. As a result, multithreaded
generation is tested much less intensively than singlethreaded operation. The following recommendations are ordered in 'bang-for-the-bucks' order: The
first tips have a better ratio of effort to gain than the later ones.

## pageSize

By default benerator stores each entity in an own transaction, because this simplifies error tracking when creating a descriptor file. But this has a
tremendous impact on performance. When configuration is complete, and you need performance, set the pagesize attribute of critical `<generate>`
elements, e.g. `<generate type="db_user" count="1000000" consumer="db" pagesize="1000">`

## JDBC batch

JDBC batches provide for significantly better database insertion performance than standard operation. In benerator this is turned of by default, since
error messages that arise from bad generation setup are much harder to analyze in batch mode. When you are finished with defining data generation and
need performance for mass data, you can activate batch operation by the batch attribute of the database element:

`<database ... batch="true" />`

benerator is optimized for performance. Thus you may get problems when combining nested `<generate>` elements with batching. It typically results in
exceptions that indicate violation of a foreign-key constraint.

## Query fetch size

If you are querying data with large result sets from your database, e.g. when anonymizing production data, you should tune the database's fetch size.
It determines how many rows are transmitted from the database to benerator when accessing the first query result. So it reduces the number of network
roundtrips. benerator uses a fetch size of 100 by default which should be useful in most cases.

You can experiment with higher values, e.g. 1000 by configuring the batch attribute of the database element: `<database ... batch="true" />`. This is
mainly useful if the database is accessed over a slow network connection and query result sets are at least as large as the fetch size and are
iterated to a relevant extent. When setting the fetch size to value that is too high, performance may actually decrease.

## Id Generation

The most efficient id generation strategy is increment since it works without connecting the database. It works fine for multithreaded generation,
too. But for concurrent execution of multiple benerator processes or continuation of a cancelled generation process you need an id generation that is
unique among several runs. The most efficient id strategies with such behavior are seqhilo (database-based) and uuid (universally unique string id).

Relational Data Generation

Nesting `<create-entities>` elements greatly improves maintanability of data generation but can decrease generation speed. At critical generation
steps you might want to sacrifice maintainability for speed by replacing nested structures with 'relational' structures, e.g. replacing this code:

```xml
<generate name="db_customer" count="1000000" consumer="db">

<generate name="db_order" minCount="0" maxCount="20 " consumer="db">

<attribute name="customer_fk" script="{db_customer.id}"/>

...

</generate>

</generate>
```

with something like this:

```xml
<generate name="db_customer" count="1000000" consumer="db" />

<generate name="db_order" count="10000000" consumer="db">

<reference name="customer_fk" source="db" targetType="db_customer" distribution="random" />

...

</generate>
```

## Scripts

When using other script languages that rapiddwellerScript, the scripts expressions are parsed at runtime, thus are significantly slower than compiled
code. For performing CPU-intensive operations or excessive looping, use rapiddwellerScript or program a Java task (See Manual Section 10, “Custom Tasks”).

## Parsing (Oracle) metadata

On databases with many tables, scanning metadata can take several minutes. One source of superfluous tables is the Oracle recyclebin. You can speed up
parsing by including a 'purge recyclebin' in your SQL scripts.

Using regular expressions in the `<database>`'s excludeTables and includeTables settings, you can restrict the amount of metadata to be parsed.

If metadata retrieval still takes too long, you can use `<database... metaCache="true" />` for storing metadata in a cache file on your local hard
drive.

When Benerator executes SQL with an `<execute>` statement, it analyzes if the database structure is modified. In that case, the cached database meta
data is invalidated and reparsed when they are needed the next time. If you are certain that the change is irrelevant to subsequent generations steps,
you can suppress the invalidation by

`<execute invalidate="false">`ALTER TABLE...`</execute>`.

## Distributed Execution

You can distribute generation over several machines. In oder to do so you will need two types of descriptor files: First an initialization descriptor
that initializes the systems and generates the deterministic core data, Second a mass data description file. The initialization file will need to run
first on a single machine, then the mass data file can be executed on multiple processes or machines concurrently. For mass data generation you will
need to take special care:
Choose an id generation strategy that is able to create unique ids under these circumstances (see Section 24, “Generating IDs”).
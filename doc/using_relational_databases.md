# Using Relational Databases

## Import and `<database>`

For using database-related features, you must import the 'db' package:

```xml
<import platforms="db" />
```

You can easily define a database:

```xml
<database id="db" url="jdbc:hsqldb:hsql://localhost" driver="org.hsqldb.jdbcDriver" user="sa" batch="false"/>
```

A database must have an id by which it can be referenced later. For starting a project, it is better to have batch="
false". In this mode, database errors are easier to track.

The following attributes are available in the `<database>` element:

| name | description |
| --- | --- |
| id | identifier under which the database is accessible in the context (required) |
| environment | Identifier of a database repository entry |
| url | JDBC database url |
| driver | JDBC driver class |
| user | user name for login |
| password | password for login |
| catalog | Database catalog to use |
| schema | database schema to use |
| includeTables | Regular expression for tables to be used |
| excludeTables | Regular expression for tables to be ignored |
| lazy | boolean flag to enable lazy metadata parsing. This improves performance on large systems of which only a small number of tables is actually used in generation. |
| metaCache | boolean flag which can be activated on databases with slow database access to cache database metadata on the local file system instead of reparsing it on each run |
| batch | boolean flag to specify if batch inserts and updates shall be done |
| fetchSize | JDBC fetch size for query results |
| readOnly | indicates if only read access shall be allowed in order to protect sensitive data |
| acceptUnknownColumnTypes | If set to true, Benerator accepts exotic database column types without complaining and relies on the user to take care of the appropriate data type when generating values for the column. |

**Attention**: Benerator has some built-in knowledge about the most widely used database systems and their conventions. So in most cases, it is
sufficient to provide url, driver user and password. In special cases, e.g. if you want to access a schema ehich is not the default schema of your
user, you may have to set schema (and possibly catalog)
explicitly.

## Usual Database Settings

| Database | URL Format | Driver Class | Default Catalog | Default Schema |
| --- | --- | --- | --- | --- |
| DB2 | jdbc:db2://host:50001/dbname | com.ibm.db2.jcc.DB2Driver |  | `<user name>` |
| Derby | jdbc:derby://host:1527/dbname |  |  | `<user name>` |
| Firebird | jdbc:firebirdsql:host/3050:dbname | org.firebirdsql.jdbc.FBDriver |  |  |
| H2 | Jdbc:h2:.... | org.h2.Driver |  | PUBLIC |
| HSQL | jdbc:hsqldb:hsql://host:9001/dbname | org.hsqldb.jdbcDriver |  | PUBLIC |
| MySQL | jdbc:mysql://host:3306/dbname | com.mysql.jdbc.Driver | `<user name>` |  |
| Oracle | jdbc:oracle:thin:@host:1521:SID | oracle.jdbc.driver.OracleDriver |  | `<user name>` |
| Postgres | jdbc:postgresql://host:5432/dbname | org.postgresql.Driver |  | `<user name>` |
| SQL Server | jdbc:jtds:sqlserver://host:1433;DatabaseName=dbname | net.sourceforge.jtds.jdbc.Driver | dbo |  |

## Using Database Repositories

For frequently-used databases it is more convenient to use a central database configuration repository. The repository is located in a folder '
databene' under your user home directory. You can define a database configuration with a name (
e.g. 'mydb') by storing a correspondingly named properties file there assigning the suffix '.env.properties' (e.g. '
mydb.env.properties', on Windows the file location would be `C:\Documents and Settings\<user_name>\mydb.env.properties`)
. In the file you can configure the JDBC connection information with the keys db_url, db_driver, db_user, db_password and db_url.

As an example, a file `mydb.env.properties` would configure the environment 'mydb' and would have a content like this for an HSQL database:

`db_url=jdbc:hsqldb:mem:DbRelatedTestdb_driver=org.hsqldb.jdbcDriverdb_user=sadb_password=db_schema=public`

Having done so, you can connect a database more simply using the `<database>`'s 'environment' attribute:

`<database id="db" environment="mydb"/>`

If you define a mydb.env.properties file in the directory in which Benerator executes, this file will be used. If not, the configuration is taken from
your database repository.

If you add conflicting attributes in your `<database>` element (like another user and password), they override the configuration details in the
database repository. This way you can have a central and convenient database lookup and can access the database with different users in the same run.
An example:

```xml
<database id="db1" environment="mydb" user="user1" password="password1"/>
```

```xaml
<database id="db2" environment="mydb" user="user2" password="password2"/>
```

## Caching Database Metadata

On very large databases, especially when accessed remotely, database metadata retrieval may take several minutes. In this case, you can make use of
the metaCache facility.

Two preconditions exist for using meta data caching: You need to

1. configure a database repository (environment) and

2. set metaCache to "true"

```xml
<database id="db2" environment="mydb" metaCache="true"/>
```

On the first run, you will not observe any speedup – meta data parsing may even take longer, since the cache needs to be built up on the first run and
Benerator is likely to read (much) more meta data than you absolutely need for your specific data generation. When done so, Benerator saves the meta
data in an XML file. On subsequent runs, Benerator notices the cache file and reads it within milliseconds.

**Cache Invalidation**: There are several reasons that make Benerator invalidate its cache information and reload it:

* **Execution of SQL code via `<execute>`**: Benerator is not so clever about interpreting SQL code. Thus it interprets any executed SQL code as a
  potential meta data change and invalidates the cache.

* **Cache time out**: If the cache file is older than twelve hours, Benerator throws it away just in case. If you are sure that the database has not
  changed meanwhile, you can perform a 'touch' on the cache file.

**Warning**: If you change the database structure from another client system and Benerator is configured to cache meta data, there is no way to become
aware of it and the old meta data cache file is used which has become obsolete. You need to delete the cache file manually in such cases!

## Executing SQL statements

SQL code can be executed, e.g. from a file:

```xml
<execute uri="drop-tables.sql" target="db" onError="warn"/>
```

Uris are resolved relative to the benerator file that declares them (as common in HTML). If the file is not found locally, it is searched relative to
the current working directory.

You can inline SQL code as well:

```xml
<execute target="db" onError="warn">

CREATE TABLE db_role (

id int NOT NULL,

name varchar(16) NOT NULL,

PRIMARY KEY (id)

);

</execute>
```

### Alternative Delimiters

By default, the semicolon is the delimiter between commands: Benerator splits SQL commands by their delimiter and sends one after the other to the
database. In some cases you need a different behaviour, e.g. if a procedure should be defined and/or called. In such cases, you can specify an
alternative delimiter in an `<execute>` statement:

```xml
<execute target="db" separator="/">

declare output_var varchar(500); 

begin 

EXECUTE_PROC_A(output_var); 

EXECUTE_PROC_B(output_var); 

end; 

</execute>
```

## Inserting entities into a database

When using a database as consumer in a `<generate>` or `<iterate>` element, the elements are _inserted_ by default. For information, how to _update_
entries, see the next chapter.

<generate type="db_user" count="50000" consumer="db"></generate>

If primary key generation should be performed by the database, you need to tell benerator to ignore the field, setting the mode to 'ignored'

```xml
<generate type="db_user" count="50000" consumer="db" >

<id mode="ignored" />

</generate>
```

## Database-related Id Generators

The following id generators make use of database features:

* **DBSequenceGenerator**: Retrieves id values from a database sequence. With default settings, it operates quite slowly, since it incurs an
  additional database call for obtaining the id value for each generated entity. When setting its property '**cached**' to true, it fetches the
  current value of its database sequence, creates ids offline in Benerator RAM and updates the database sequence in the end. Of course this requires
  Benerator to run in a single instance and no other client may be writing data to the system while Benerator is generating – otherwise a primary key
  conflict may arise.

* **DBSeqHiLoGenerator**: Combines a value retrieved from a database with a local counter to create unique values (with a strongly reduced performance
  burden compared to the DBSequenceGenerator)

* **QueryGenerator**: Uses a database query to calculate id values

* **QueryLongGenerator**: Uses a database to calculate id values of number type

* **QueryHiLoGenerator**: Works like a DBSeqHiLoGenerator, but based on a query instead of a sequence

* **SequenceTableGenerator**: Lets you read and increment values from database tables

Best performance with cluster-safe generators is achieved with the DBSeqHiLoGenerator, followed by the QueryHiLoGenerator.

### SequenceTableGenerator

The SequenceTableGenerator lets you create unique long values from a database table. Depending on the table structure this can be trivial or tricky.

You always need to specify a **database**, a **table** and a **column** from which to read the value, in non-trivial cases, you also need a selector.

Single-Value-Table

In the simplest case, you have a table which stores a single row with a single value:

```xml
<database id="db" environment="mydb" />

<bean id="sg" class="SequenceTableGenerator"> <property name="database" ref="db"> <property name="table" value="MY_TABLE"> <property name="column" value="SEQ_VALUE">
</bean>

<generate type="PERSON" count="100" consumer="db"> <id name="id" type="long" generator="sg" /> ...</generate>
```

Name-Value-Pair Table

In a slightly more difficult case, you have name-value-pairs of 'sequence identifier' and 'sequence value'. Then you must specify a selector, that
tells Benerator which row to use. For example, if the sequence for the PERSON table is specified by a row in which the SEQ_ID column has the value '
PERSON':

```xml
<database id="db" environment="mydb" />

<bean id="sg" class="SequenceTableGenerator"> <property name="database" ref="db"> <property name="table" value="MY_TABLE"> <property name="column" value="'SEQ_VALUE"> <property name="selector" value="SEQ_ID = 'PERSON'">
</bean>

<generate type="PERSON" count="100" consumer="cons"> <id name="id" type="int" generator="sg" />
</generate>`
```

Arbitrary Table

You can support arbitrary complex sequence tables with a **parameterized selector**. It marks each parameter with a question mark (**?**) and must be
invoked differently than the examples above, using a **script** that calls the **
generateWithParams(...)** method:

```xml
<database id="db" environment="mydb" />

<bean id="sg" class="SequenceTableGenerator"> <property name="database" ref="db"> <property name="table" value="MY_TABLE"> <property name="column" value="'SEQ_VALUE"> <property name="selector" value="MOD_ID = ? and ITEM_ID = ?">
</bean>

<generate type="PERSON" count="100" consumer="cons"> <!-- calculate mid and iid as required --> <id name="id" type="int" script="sg.generateWithParams(mid, iid)" />
</generate>`
```

## Handling of common Columns

In many databases, you encounter common columns like auditing information 'created_by', 'created_at', 'updated_by', '
updated_at' or optimistic locking columns. See the chapter 'Default Attribute Settings' for instructions how to define a common default generation
settings for these.

## Determining attribute values by a database query

You can use database queries to determine column values. A **source** attribute identifies the database to query and a selector the SQL query to
perform:

```xml
<attribute name="user_rank" source="db" selector="select rank from ranks where active = 1" />
```

You can use **source** and selector in `<attribute>`, `<id>`, `<reference>` and `<variable>` statements.

**Attention**: Whis this syntax, the query's result set is iterated throughout the `<generate>` loop until its end is reached. In the example above, a
result set with the rows [1], [2], [3] will result in the user_rank values 1 for the first generated entry, 2, for the second and 3 for the third.
After that the end of the result set is reached, the component signals, that it is unavailable and Benerator will terminate the generation loop. If
you configured more than 3 objects to be generated, you will get an exception that Benerator was not able to provide the requested number of data
sets. You have the following alternatives:

1. Cycling through the result set again and again (cyclic="true")

2. Apply a distribution choose a mode for selecting elements of the result set repeatedly or randomly

3. If the query should be performed for each generated entity and is supposed to provide a single result, this is called sub query and is supported by
   a 'subQuery' attribute

### Cycling through query result sets

When using a selector in combination with **cyclic="true"**, the query is automatically repeated when the end of the result set is reached:

```xml
<attribute name="user_rank" source="db" selector="select rank from ranks where active = 1" cyclic="true" />
```

### Applying a distribution to a query

When using a selector in combination with a **distribution**, the query's result set in processed by the selected distribution algorithm. Depending on
the distribution, the result set may be buffered. Result set elements may be provided uniquely or repeatedly in an ordered or a random fashion. See
the

distribution reference

. The most common distribution is the 'random' distribution which buffers the full result set and then provides a rnadomly chosen entry on each
invocation.

```xml
<attribute name="user_rank" source="db" selector="select rank from ranks where active = 1" distribution="random" />
```

### Sub selectors

Frequently you will encounter multi-field-constraints in an entity which can be matched by a query. Usually this means to first generate a random
value and then to impose a database query with the value generated before. The query's results are valid only for the currently generated entity, and
in general only one query result row is expected. You have a kind of „sub query“ which is handled best by using a **subSelector**. For example, you
might offer products (
table 'product') in different geographical regions and have a cross-reference table product_region that describes, which products are available in
which region:

```xml
<attribute name="region" values="'americas','emea','asia'" />

<reference name="product" source="db" subSelector="{{'select product_id from product_region where region_id = ' + this.region }}"/>

```

**Attention**: In the example, you need double brackets {{…}} in order to signal that **this.region** should be reevaluated on each invocation. When
using a single bracket, the query memorizes the value of **this.region** at the first invocation and reuses it on each subsequent call.

## Resolving Database Relations

### Automatic referencing

By default, benerator assumes that all relations are one-to-one as the most defensive choice. Thus the following setup in which a table db_user
references a table db_role will cause an error:

```xml
<generate type="db_role" count="10" consumer="db" />
```

```xml
<generate type="db_user" count="100" consumer="db" />
```

This is because, assuming a one-to-one relationship, you can only generate as many users as unique roles are available!
...and you have generated only 10 roles before. In other words, in fully automatic data generation, the number of user entries will be the number of
role entries.

In most cases you actually deal with many-to-one relationships and thus need to specify its characteristics explicitly, typically by using a
distribution. Basically, a reference is defined by (column) **name**, (dabase) **source** and **
targetType** (referenced table):

```xml
<generate type="db_role" count="10" consumer="db" />

<generate type="db_user" count="100" consumer="db">

<reference name="role_fk" targetType="db_role" source="db" distribution**="random"/>

</generate>
```

This will cause creation of 100 users which are evenly distributed over the roles.

### Null references

If you want to generate only null values, you can reduce the declaration to a **name** and **nullQuota="1"** element:

```xml
<generate type="db_user" count="100" consumer="db">

<reference name="role_fk"nullQuota="1"/>

</generate>
```

### Selective Referencing

For restricting the objects referred to or using an individual way to construct the reference values, you can specify a selector which will be
evaluated by the target system and return a reference value. For databases the selector needs to be a SQL where clause or complete query:

```xml
<generate type="db_role" count="10" consumer="db" />

<generate type="db_user" count="100" consumer="db">

<reference name="role_fk" targetType="db_role" source="db" selector="role_name != 'admin'" distribution="random"/>

</generate>
```

### Other referencing options

Besides selective referencing, you can use (almost) the full feature set of `<attribute>` elements to generate references, e.g. constant, pattern,
values, script, etc. You could, e.g., configure the use of each role type by itself:

```xml
<generate type="db_user" count="5" consumer="db">

<reference name="role_fk" constant="'admin'"/>

...

</generate>

<generate type="db_user" count="95" consumer="db">

<reference name="role_fk" constant="'customer'"/>

...

</generate>
```

## Composite Keys

Benerator does not provide an **automated** composite key handling, but you can configure it to handle them **
explicitly**. The typical approach for this is a prototype query.

## Prototype Queries

For the general idea of prototype-based generation, see the corresponding chapter. In addition to the core features, the prototype approach is a good
way to handle composite primary keys and composite foreign keys, since their components are available in combination.

### Prototype Queries on Entities

When querying entities, you specify the database to query as **source**, the where clause of the select as selector, and the table which is queried
as **type**. After that, you can access the results' attributes by their names:

```xml
<variable name="_product" type**="MY_PRODUCT" **source**="db" selector="sysdate between VALID_FROM and VALID_TO" distribution="random" />

<reference name="PRODUCT_ID" script="_product.PRODUCT_ID" />

<attribute name="ROUTING_TYPE" script="_product.ROUTING_TYPE" />
```

When aggregating data with a general query that is not (or cannot be) mapped to a type, you can access the results' column values as array elements (
indices are 0-based):

<variable name="_product" source**="db" selector="select PRODUCT_ID, ROUTING_TYPE from MY_PRODUCT where sysdate between VALID_FROM and VALID_TO"
distribution="random" />

```xml
<reference name="PRODUCT_ID" script="_product[0]" />

<reference name="ROUTNG_TYPE" script="_product[1]" />
```

## Exporting Database Content

The rows of a database table can be iterated simply. Here's an example for writing all users of a table 'db_user' to a file 'users.csv':

```xml
<iterate source="db" type="db_user" consumer="new CSVEntityConsumer('users.csv')" />
```

You can as well select a subset to iterate:

```xml
<iterate source="db" type="db_user" selector="active = 1"consumer="new CSVEntityConsumer('users.csv')" />
```

## Updating Database Entries

When using a database, you can update existing entries.

For example, if you have db_orders that refer (one-to-many) db_order_items, you can calculate the sum of the db_order_items' total_price values and
write it to the db_order's total_price column:

```xml
<iterate type="db_order" source="db" consumer="db.updater()">

<attribute name="total_price" source="db"

selector="{{ftl:select sum(total_price) from db_order_item where order_id = ${db_order.id}}}"

cyclic="true"/>

</iterate>
```

The described update mechanism can also be used to anonymize production data – see the chapter 'Production Data Anonymization'. For transfering user
data from a source database 'sourcedb' to a target database 'testdb', you would write

```xml
<iterate source="sourcedb" type="db_user" consumer="testdb">

<!-- anonymize here -->

</iterate>
```

If you have duplicated the database and want to anonymize the copy by updating the tables, you would write

```xml
<iterate type="db_user" source="db" consumer="db.updater()">

<!-- anonymize here -->

</iterate>
```

If you want to read data from one table, anonymize it and write it to a different table in the same database, you can use a special inserter:

```xml
<iterate type="prod_user" source="db" consumer="db.inserter('anon_user')">

<!-- anonymize here -->

</iterate>
```

## Controlling Transactions

By default, Benerator performs one transaction per data set that is generated. When generating huge amounts of data, transaction overhead quickly
becomes significant and you will want to insert several data set in a common transaction. You can use the pageSize argument to configure the number of
data elements per transaction. For most databases and tasks, pageSize="1000" is a reasonable setting:

```xml
<generate type="user" count="1000000" pageSize="1000" consumer="db" />
```

For further hints on improving performance, refer to the 'Performance' section.

If you are nesting creation loops, you can set the transaction control for each level separately:

```xml
<generate type="user" count="1000" pageSize="100" consumer="db">

<generate type="order" count="50" pageSize="500" consumer="db" />

</generate>
```

But an 'inner' transaction commit will commit the outer elements too, so you may get more transactions than you expect. The inner pageCount in the
descriptor example makes the outer pageSize 10 effectively, since there is a commit after 500 orders. With 50 orders per customer, it is a commit for
every 10th customer.

In most cases it is feasible and more intuitive to make the sub creation loops simply join the outer transaction control, by setting their pageSize to
zero:

```xml
<generate type="user" count="1000" pageSize="100" consumer="db">

<generate type="order" count="50" pageSize="0" consumer="db" />

</generate>
```

Any `<generate>` loop with pageSize >` 0 is flushed when finished. For databases this means a commit.

## Transcoding Database Data

Benerator's transcoding feature enables you to

1. copy database entries from one database to another

2. assign new primary key values while copying

3. transcode relationships (automatically translate foreign key relationships to the new primary key values)

4. merge relationships (make copied data refer to pre-existing data in the target database)

Features 1-3 are can be performed easily, for feature 4 you need so fulfill some preconditions.

### Copying database entries

A transcoding task that involves one or more database tables is wrapped with a `<transcodingTask>` and for each table to be transcoded, there needs to
be a `<transcode>` element that specifies the table name. The `<transcode>` steps must be specified in the order in which they can be applied without
violating foreign key constraints. For example, if you have a table USER which references a table ROLE, you need to transcode ROLE first, then USER.

```xml
<transcodingTask defaultSource="db1" target="db2">

<transcode table="ROLE"/> <transcode table="USER"/>

</transcodingTask>
```

This copies the full content of the tables ROLE and USER from db1 to db2\.

### Restricting the copied Set

You can resctrict the set of database entries to copy by using a 'selector' attribute in the `<transcode>` element:

```xml
<transcodingTask defaultSource="s" target="t">

<transcode table="ROLE" selector="ID = 1" /> <transcode table="USER" selector="ROLE_ID = 1" />

</transcodingTask>
```

Only the ROLE with id 1 and the USERs that refer role #1 are copied.

### Transcoding

You can overwrite primary key values and other attributes while you are transfering data using the normal Benerator syntax:

```xml
<transcodingTask defaultSource="s" target="t">

<transcode table="ROLE"> <id name="id" generator="IncrementalIdGenerator" /> </transcode>

<transcode table="USER">
<id name="id" generator="IncrementalIdGenerator" /> </transcode>

</transcodingTask>
```

Each ROLE and USER gets a new primary key value and the foreign key references from USER to ROLE are reassigned to match the new id values.

### Cascaded Transcoding

As an easy approach to transcode graphs of dependent objects along with their parent object, they can be transcoded in cascade along with their '
owner' object. This means, for each transcoded owner object, Benerator looks up, which database rows relate to it as defined in the cascade and
transcodes them too. Thus, if you restrict the owners (e.g. company) to a subset of all available owners, the cascade statement assures that only
database rows (e.g. department), which relate to this subset (company), are transcoded.

A cascade statement consists of `<cascade>` element nested in a `<transcode>` element specifying a ref that tells, which columns of which table make
up the table relationship. The Syntax is table(column1 [, column2 [, …]]), depending on the number of columns used as foreign key. Benerator looks up
the corresponding foreign key constraint in the database and finds out the type of relationship.

As an example, if you want to transcode the rows of a company table and cascade to their departments, you would write

```xml
<transcode table="company"> <cascade ref="department(company_id)" /> </transcode>
```

Cascades can be nested:

```xml
<transcode table="company"> <cascade ref="department(company_id)"> <cascade ref="employee(department_id)" /> </cascade> </transcode>
```

In `<cascade>`, you can overwrite attributes, ids and references like in `<transcode>`:

```xml
<transcode table="company">
<id name="id" generator="IncrementalIdGenerator" /> <cascade ref="department(company_id)" /> </transcode>
```

### Merging Foreign Key Relations

Benerator allows you to merge data from different databases. To continue the example above, you could have ROLES and USERS in different databases and
merge them into one single target database. This introduces a new requirement: Since you might have automatically assigned technical ids, an object
with a given 'business' identity (e.g. user 'Volker Bergmann') might have different 'technical' ids (primary keys of value e.g. 10 or 1000) in
different databases. Thus, you need to provide Benerator with a description, which technical id relates to which business id in which database.

There are several alternatives available, so I start with one of the simplest and most widely used in order to give you an overview of the approach
and then provide you with a complete list of possibilites.

Let's assume the tables ROLE and USER each have a NAME column with unique identifiers. In this case, you can apply the unique-key identity mapping ans
store it in a file with the suffix .id.xml, e.g. identities.id.xml:

```xml
<?xml version="1.0" encoding="UTF-8"?>

<dbsanity>

<identity table="ROLE" type="unique-key" columns="NAME" />

<identity table="USER" type="unique-key" columns="NAME" />

</dbsanity>
```

This identity file enables Benerator to merge foreign key relationships in the transcoding process, for example:

```xml
<transcodingTask target="db3" identity="identity-def.xml">

<transcode table="ROLE" source="db1"/>

<transcode table="USER" source="db2"/>

</transcodingTask>
```

### Defining Identities

Identites are definied in a file format taken from DB Sanity. Thus, it is XML and its root element is `<dbsanity>`. Under the root, all `<identity>`
definitions are listed, each referring to a **table** and having a certain identity **
type**, e.g.

```xml
<?xml version="1.0" encoding="UTF-8"?>

<dbsanity>

<identity table="ROLE" type="..." ... />

<identity table="USER" type="..." ... />

</dbsanity>
```

There are different types of entity definitions available:

natural-pk

The simplest type of identity applies if the primary key (PK) is a business key (natural key). In this case, use a `<natural-pk>` definition:

```xml
<identity table="CURRENCY" type="natural-pk" />
```

unique-key

If a non-pk-column or a combination of several columns is unique, use the `<unique-key>` identity and list the key components in a comma-separated
list in a 'columns' attribute:

```xml
<identity table="PRODUCT" type="unique-key" columns="CATEGORY,CODE"/>
```

You may as well include foreign keys in the list: Benerator will use the business id of the referenced objects to determine the identity of the
referer.

nk-pk-query

In more complicated cases, you might need a definition style that gives you more freedom. The `<nk-pk-query>` allows you to specify an arbitrary SQL
query which is supposed to return a pair of natural key and primary key values for each table row:

```xml
<identity table="COUNTRY" type="nk-pk-query">

select COUNTRY_NAME.NAME as NK, COUNTRY.ID as PK from COUNTRY_NAME join COUNTRY on COUNTRY_NAME.COUNTRY_ID = COUNTRY.ID where COUNTRY_NAME.LANGUAGE = 'EN'

</identity>
```

sub-nk-pk-query

A table's rows may have a complex identity definition, which is only unique in the context of a 'parent' row in another table. A good example is if a
state name is only unique within a given country, nut different countries may have a state of the same name, but different identities. The provided
query must have the parent's primary key as '?' parameter:

```xml
<identity type="sub-nk-pk-query" table="" parents="COUNTRY">

select sn.STATE_NAME as SUB_NK, s.STATE_ID as PK from STATE s join STATE_NAME sn on sn.STATE_ID = s.STATE_ID where sn.LANGUAGE_ID = 'ENG' AND **s.COUNTRY_ID = ?**

</identity>
```

### Limitations

Currently, the amount of data that can be transcoded is limited by the amount of avaliable Java heap memory, but as long as you do not transcode
billions of data sets you are not supposed to get problems. Composite primary keys are not supported.
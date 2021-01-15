# Using mongoDB 

MongoDB can be used in am manner very similar to relational databases using the mongo4ben plugin (see [http://databene.org/mongo4ben](http://databene.org/mongo4ben)). It supports data import from and export of generated data to a mongoDB instance and modification of existing database data. The implementation is still experimental, so do not excpect too much yet. One restriction is for example, that selective queries are not yet supported – all data import refers to the collection by the specified 'type' name and iterates through all elements.

## mongo4ben Installation 

Download the mongo4ben distribution from [http://databene.org/mongo4ben](http://databene.org/mongo4ben), extract it and copy the jar files from the distribution's lib folder into your Benerator installation's lib folder.

If you want to distribute a mongoDB data generation project to run on a fresh Benerator installation, put the jars mentioned above into a 'lib' folder inside your project folder.

## Usage 

At the beginning, import mongo4ben features in your Benerator project:

```xml
<import platforms='mongodb' />
```

Then, a mongodb instance can be declared using a `<mongodb>` element, using an environment definition file:

```xml
<mongodb id='db' environment='mymongo' />
```

The environment definition file has to be in the project folder or at ${user.home}/databene/ and has to carry the name specified in the `<mongodb>` declaration before, in this case mymongo.env.properties with content like this:

db_url=127.0.0.1:27017db_catalog=testDatabasedb_user=medb_password=secret

The db_catalog setting has to be set to mongoDB's database name.

That's it! Now you can use mongodb for data import and export.

## Examples 

A simple data generation example to help you get started. It generates 100 users with 1 to 3 addresses:

```xml
<setup>
<import platforms='mongodb' />
<mongodb id='db' environment='mymongo' />
<generate type='mit_user' minCount='100' consumer='db'>
<attribute name='name' type='string' />
<attribute name='age' type='int' min='18' max='78' />
<part name='addresses' container='list' count='2'>
<attribute name='street' pattern='[A-Z][a-z]{4} Street'/>
<attribute name='houseNo' type='int' min='2' max='9' />
</part>
</generate> </setup>
```

An example that exports (prints) all user data to the text console:

```xml
<setup>
<import platforms='mongodb' />
<mongodb id='db' environment='mymongo' />
<iterate type='mit_user' source='db' consumer='ConsoleExporter'/>
</setup>
```

A trivial anonymization-style example that sets each user's age to 33 and house number to 123:

```xml
<setup>
<import platforms='mongodb' />
<mongodb id='db' environment='mymongo' />
<iterate type='mit_user' source='db' consumer='db.updater(),ConsoleExporter'>
<attribute name='age' constant='33' />
<part name='addresses' source='mit_user' container='list'>
<attribute name='houseNo' constant='123' />
</part> 

</iterate> 

</setup>
```


# NoSQL Support

You can implement your own custom implementation for NoSQL databases by 
- providing a StorageSystem which extends `CustomStorageSystem`, 
- providing a Parser which extends `AbstractBeneratorDescriptorParser`,
- providing a Statement which extends `Statement`,
- register the Parser in the `BeneratorParseContext` and
- defining an own tag for the descriptor

The tag element for your own custom storage system will be parsed in the `AbstractBeneratorDescriptorParser`. It also will create a `Statement`, where the parsed tag elements will be handled and finally handed over to the `CustomStorageSystem`.

> ⚠ **Information**: For a custom storage system reference implementation see ``com.rapiddweller.platform.mongodb.MongoDBSystem``

## How to handle schema free storage systems

### Pre parse generate tags

Some storage systems may not have a schema (e.g. MongoDB). But Benerator assumes that the storage systems knows and can provide a description about the data, which the storage system is managing. Therefore, we need to provide this description and to do so, the  ``<pre-parse-generate>`` tag can be used. This tag pre parsed all ``<generate>`` tags and creates type descriptors for each ``<generate>`` and then provide them to the target custom storage system.

```xml
<pre-parse-generate target="db"/>
```

The ``target`` attribute will search for all ``<generate>`` tags where the ``consumer`` equals the ``target``. If no data should be generated, but type descriptions still are needed, then you can set the ``count`` attribute in ``<generate>`` to 0. No data will be generated, but the storage system will still receive the type description.

> ⚠ **Attention**: The  ``<pre-parse-generate>`` tag must be used after the storage systems are defined and before the first ``<generate>``.

You can find an example of the usage in the demo scripts ``shop-mongodb.ben.xml`` and ``shop-postgres-mongodb.ben.xml``.

### Provide a meta-model

You can also provide a meta-model directly to a custom system storage by using the ``<meta-model>`` tag. The tag provides a subset of the element and attributes of the ``<generate>`` tag (only these which are important for the description of the data but not for the generation). 

```xml
<meta-model type="db_category" consumer="mongodb">
    <id name="id" type="string" unique="true"/>
    <attribute name="name" type="string"/>
    <attribute name="parent_id" type="string"/>
</meta-model>
```

You can find an example of the usage in the demo scripts ``shop-mongodb.ben.xml`` and ``shop-postgres-mongodb.ben.xml``.

## MongoDB Support

Benerator supports MongoDB and can be used like this:

```xml
<mongodb id="mongodb" 
         host="{ftl:${mongoHost}}" 
         port="{ftl:${mongoPort}}" 
         database="{ftl:${mongoDatabase}}"
         user="{ftl:${mongoUser}}" 
         password="{ftl:${mongoPassword}}" 
         authenticationDatabase="{ftl:${mongoAuthenticationDatabase}}"
         authenticationMechanism="{ftl:${mongoAuthenticationMechanism}}"
         clean="{ftl:${mongoClean?c}}"
/>
```

The parameter **authenticationDatabase** and **authenticationMechanism** are optional. 
If **authenticationDatabase** is not set, the admin database will be used. 
If **authenticationMechanism** is not set, SCRAM-SHA-1 will be used.

An [environment file](./environment_files.md#Environment Files) can also be used:

```xml
<mongodb id="db" environment="mongodb" system="target"/>
```

The parameter clean is optional and if it set to true, the database will be cleaned at start (all data removed). The default value is ``false``. **Be careful with this parameter**

You can find an example of the usage in the demo scripts ``shop-mongodb.ben.xml`` and ``shop-postgres-mongodb.ben.xml``.

### Limitations 

- The import of nested entities is currently not possible.
- The import of entities requires type descriptions.
- Types cannot be automatically derived. Please provide a type in ``<generate>``. Otherwise, the default type is string.
- Queries/Selectors uses ``comamnd`` from ``db.runCommands(command)`` internal. The commands ``find`` and ``aggregate`` are implemented. The commands ``count``, ``distinct`` and ``mapReduce`` could also be used, but are not tested yet.

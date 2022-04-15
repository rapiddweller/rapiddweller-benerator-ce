# NoSQL Support

You can implement your own custom implementation for NoSQL databases (or others) by extending the abstract class `CustomStorageSystem`. Then you can use the `<storage-system>` tag to define your storage system in the descriptor file with the similar functionality as the ``<database>`` tag.

> ⚠ **Information**: For a custom storage system reference implementation see ``com.rapiddweller.platform.nosql.mongo.MongoDBSystem``

##The `<storage-system>` Tag

The `<storage-system>` looks like this:

```xml
<storage-system id="example_id">
    <class>example.package.ExampleStorageSystemClass</class>
    <params>
        <param name="host">...</param>
        <param name="port">...</param>
        ...
    </params>
</storage-system>
```

- The ``id`` of the ``storage-system`` is reusable within the descriptor script and can be used as a reference.
- The `<class>` tag should point to the DefaultStorageSystem implementation.
- The ``<params>`` tag defines the parameter for the storage system such as ``host`` or ``port``.

##Extending DefaultStorageSystem

Use the abstract class ``DefaultStorageSystem`` to implement your own custom storage system. The parameter for your custom storage system can be provided by the ``<params>`` tag within the ``<storage-system>``. For a reference implementation see ``com.rapiddweller.platform.nosql.mongo.MongoDBSystem``.

## How to handle schema free storage systems

Some storage systems may not have a schema (e.g. MongoDB). But Benerator assumes that the storage systems knows and can provide a description about the data, which the storage system is managing. Therefore, we need to provide this description and to do so, the  ``<pre-parse-generate>`` tag can be used. This tag pre parsed all ``<generate>`` tags and creates type descriptors for each ``<generate>`` and then provide them to the target storage system.

```xml
    <pre-parse-generate target="db"/>
```

The ``target`` attribute will search for all ``<generate>`` tags where the ``consumer`` equals the ``target``. If no data should be generated, but type descriptions still should be created, then you can set the ``count`` attribute in ``<generate>`` to 0. No data will be generated, but the storage system will still receive the type description.

> ⚠ **Attention**: The  ``<pre-parse-generate>`` tag must be used after the storage systems are defined and before the first ``<generate>``.

You can find an example of the usage in the demo scripts ``shop-mongodb.ben.xml`` and ``shop-postgres-mongodb.ben.xml``.

## MongoDB Support

Benerator supports MongoDB and can be used like this:

```xml
    <storage-system id="db">
        <type>mongodb</type>
        <params>
            <param name="host">...</param>
            <param name="port">...</param>
            <param name="database">...</param>
            <param name="user">...</param>
            <param name="password">...</param>
            <param name="clean">...</param>
        </params>
    </storage-system>
```

The parameter clean is optional and if it set to true, the database will be cleaned at start (all data removed). The default value is ``false``. **Be careful with this parameter**

You can find an example of the usage in the demo scripts ``shop-mongodb.ben.xml`` and ``shop-postgres-mongodb.ben.xml``.

### Limitations 

- The import of nested entities is currently not possible.
- The import of entities requires type descriptions.
- Types cannot be automatically derived. Please provide a type in ``<generate>``. Otherwise, the default type is string.
- Queries/Selectors uses ``comamnd`` from ``db.runCommands(command)`` internal. The commands ``find`` and ``aggregate`` are implemented. The commands ``count``, ``distinct`` and ``mapReduce`` could also be used, but are not tested yet.

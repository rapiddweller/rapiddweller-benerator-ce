# Scripting

Benerator supports arbitrary scripting languages that are supported by Java and has its own scripting language
**rapiddwellerScript** which is designed specifically for the purpose of data generation.

The invocation syntax is as described for SQL invocation and inlining: You can include the script inline like this:

```xml

<setup>
    <database id="db" url="jdbc:hsqldb:mem:example" driver="org.hsqldb.jdbcDriver" user="sa" schema="PUBLIC"/>

    <execute type="js">
        print('DB-URL' + db.getUrl());

        // create user Alice
        const Entity = Java.type('com.rapiddweller.model.data.Entity');

        let alice = new Entity('db_user',context.getLocalDescriptorProvider());");
        alice.set('id', 1);
        alice.set('name', 'Alice');
        db.store(alice);

        // create user Bob
        let bob = new Entity('db_user', context.getLocalDescriptorProvider());
        alice.set('id', 2);
        alice.set('name', 'Bob');
        db.store(bob);

        // persist everything
        db.flush();
    </execute>
</setup>
```

As you see with the db variable, all objects of the benerator context are made provided to the script. In this case, it
is a DBSystem bean, which is used to store Entities created by the script. So, you can import objects of arbitrary Java
classes and use them in your favorite scripting language.

Alternatively to inlining script text, you can put it in a script file and invoke this:

```xml

<execute type="js" uri="test.js"/>
```

You can bind a language of choice by using the mechanisms of _GraalVM_: Scripting for the Java Platform.

With rapiddweller Benerator, GraalVM[js] implementation is shipped. For all other platforms and languages you need to
configure language support.

The following attributes are available for the `<execute>` element:

| name     | description                                                  |
| -------- | ------------------------------------------------------------ |
| uri      | the URI of the script file to execute                        |
| encoding | the encoding of the script file                              |
| type     | Type (language) of the script                                |
| target   | a target to execute the script on, typically a database for a SQL script |
| onError  | How to handle errors. <br/>One of (`ignore`, `trace`, `debug`, `info`, `warn`,  `error`, `fatal`). <br />`fatal` causes  Benerator to cancel execution in case of an error. |
| optimize | boolean flag that tells Benerator whether it may optimize script  execution for the sake of performance. <br />E.g. For an Oracle SQL script, this would leave out comments for  faster table creation. |

Benerator supports the following script types:

| script | description                                                  |
| ------ | ------------------------------------------------------------ |
| shell  | system shell invocations, e.g. for invoking batch files.     |
| sql    | SQL, it requires specification of the database in a target property. |
| jar    | java library files with a configured main-class              |
| ben    | rapiddwellerScript, which is the default script language     |
| ftl    | FreeMarker                                                   |
| js     | JavaScript is shipped with Benerator GraalVM dependencies    |
| py     | is not enabled by DEFAULT - it  requires a [GraalVM](https://www.graalvm.org/downloads/) as JVM with Python installed ( this is not  supported on Windows at the moment ) |

Example:

```XML

<setup>

    <execute type="js">
        let c = 1;
        const d = 6;

        function add(number1){
        c += 1;
        return number1 + c + d;
        }
    </execute>

    <iterate type="person" source="person.ent.csv" consumer="ConsoleExporter">
        <variable name="age" type="integer" script="this.age"/>
        <attribute name="multiply" type="int" script="{js:add(age)}"/>
    </iterate>

    <iterate type="person2" source="person.ent.csv" consumer="ConsoleExporter">
        <attribute name="multiply" script="{js:add(person2.age)}"/>
        <attribute name="multiply2" script="{js:add(person2.age)}"/>
        <attribute name="multiply3" script="{js:add(person2.age)}"/>
    </iterate>

    <generate type="script" count="5" consumer="ConsoleExporter">
        <variable name="count" type="integer" generator="IncrementalIdGenerator"/>
        <attribute name="multiply" script="{js:add(count)}"/>
    </generate>

</setup>
```

!!! danger

    Make sure not to redefine a variable or method. The defined variables and functions are valid for the whole runtime.

## Shell scripting

You can call shell files or issue shell commands. When in-lining shell commands, script expressions will be resolved. So
you could, for example, use global properties for setting parameters of a _sqlplus_ call:

```xml

<execute type="shell">{ftl:sqlplus ${dbUser}/${dbPassword}@${database} @create_tables.sql}</execute>
```

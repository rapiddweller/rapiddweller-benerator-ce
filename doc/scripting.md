# Scripting

Benerator supports arbitrary scripting languages that are supported by Java and has an own scripting language rapiddwellerScript which is designed
specifically for the purpose of data generation.

The invocation syntax is as described for SQL invocation and inlining: You can include the script inline like this:

```xml
<setup>
  <database id="db" url="jdbc:hsqldb:mem:example" driver="org.hsqldb.jdbcDriver" user="sa" schema="PUBLIC"/>
  
  <execute type="js">
  
  importPackage(com.rapiddweller.model.data);
  
  print('DB-URL' + db.getUrl());
  
  // create user Alice var alice = new Entity('db_user'); alice.set('id', 1); alice.set('name', 'Alice'); db.store(alice);
  
  // create user Bob var bob = new Entity('db_user', 'id', '2', 'name', 'Bob'); db.store(bob);
  
  // persist everything db.flush();
  
  </execute>
</setup>
```

As you see with the db variable, all objects of the benerator context are made provided to the script. In this case, it is a DBSystem bean, which is
used to store Entities created by the script. So, you can import objects of arbitrary Java classes and use them in your favorite scripting language.

Alternatively to inlining script text, you can put it in a script file and invoke this:

```xml
<execute uri="test.js" />
```

You can bind a language of choice by using the mechanisms of GraalVM: Scripting for the Java Platform.

With rapiddweller Benerator, GraalVM[js] implementation is shipped. For all other platforms 
and languages you need to configure language support.

The following attributes are available for the `<execute>` element:

* uri: the URI of the script file to execute

* encoding: the encoding of the script file

* type: Type (language) of the script

* target: a target to execute the script on, typically a database for a SQL script

* onError: How to handle errors. One of (ignore, trace, debug, info, warn, error, fatal. fatal causes 
  Benerator to cancel execution in case of an error.

* optimize: boolean flag that tells Benerator whether it may optimize script execution for the sake of performance. 
  E.g. For an Oracle SQL script, this would leave out comments for faster table creation.

Benerator supports the following script types:

* shell: system shell invocations, e.g. for invoking batch files.
* sql: SQL, it requires specification of the database in a target property.
* jar: java library files with a configured main-class
* ben: rapiddwellerScript, which is the default script language
* ftl: FreeMarker
* js: JavaScript is shipped with Benerator GraalVM dependencies
* py: is not enabled by DEFAULT - it requires a [GraalVM](https://www.graalvm.org/downloads/) as JVM  
  with Python installed ( this is not supported on Windows at the moment )

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

Make sure not to redefine a variable or method, because these variable and functions you are defining 
in your Benerator script are valid for the whole runtime.

## Shell scripting

You can call shell files or issue shell commands. When in-lining shell commands, script expressions will be resolved. 
So you could, for example, use global properties for setting parameters of a sqlplus call:

```xml
<execute type="shell">{ftl:sqlplus ${dbUser}/${dbPassword}@${database} @create_tables.sql}</execute>
```


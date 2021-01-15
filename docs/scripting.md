# Scripting 

Benerator supports arbitrary scripting languages that are supported by Java and has an own scripting language DatabeneScript which is designed specifically for the purpose of data generation.

The invocation syntax is as described for SQL invocation and inlining: You can include the script inline like this:

```xml
<database id="db" url="jdbc:hsqldb:hsql://localhost:9001"

driver="org.hsqldb.jdbcDriver" schema="public" user="sa"/>

<execute type="js">

importPackage(com.rapiddweller.model.data);

print('DB-URL' + db.getUrl());

// create user Alice var alice = new Entity('db_user'); alice.set('id', 1); alice.set('name', 'Alice'); db.store(alice);

// create user Bob var bob = new Entity('db_user', 'id', '2', 'name', 'Bob'); db.store(bob);

// persist everything db.flush();

</execute>
```

As you see with the db variable, all objects of the benerator context are made provided to the script. In this case, it is a DBSystem bean, which is used to store Entities created by the script. So, you can import objects of arbitrary Java classes and use them in your favorite scripting language.

Alternatively to inlining script text, you can put it in a script file and invoke this:

```xml
<execute uri="test.js" />
```

You can bind a language of choice by using the mechanisms of GraalVM: Scripting for the Java Platform.

With rapiddweller Benerator, GraalVM[js] implementation is shipped. For all other platforms and languages you need to configure language support.

The following attributes are available for the `<execute>` element:

*   uri: the URI of the script file to execute

*   encoding: the encoding of the script file

*   type: Type (language) of the script

*   target: a target to execute the script on, typically a database for a SQL script

*   onError: How to handle errors. One of (ignore, trace, debug, info, warn, error, fatal. fatal causes benerator to cancel execution in case of an error.

*   optimize: boolean flag that tells benerator whether it may optimize script execution for the sake of performance. E.g. For an Oracle SQL script this would leave out comments for faster table creation.

benerator supports the following script types:

*   shell: system shell invocations, e.g. for invoking batch files.

*   sql: SQL, it requires specification of the database in a target property.

*   jar: java library files with a configured main-class

*   ben: DatabeneScript, which is the default script language

*   ftl: FreeMarker

*   GraalVM: Any language that has been plugged into the local Java environment, e.g. JavaScript, Python, Ruby, Groovy and many more.

## Shell scripting 

You can call shell files or issue shell commands. When inlining shell commands, script expressions will be resolved. So you could, for example, use global properties for setting parameters of a sqlplus call:

```xml
<execute type="shell">{ftl:sqlplus ${dbUser}/${dbPassword}@${database} @create_tables.sql}</execute>
```


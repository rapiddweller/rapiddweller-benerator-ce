# Quick tour through the descriptor file format

## `<setup>`

The benerator configuration file is XML based. An XML schema is provided. The document root is a setup element:

```xml
<?xml version="1.0" encoding="utf-8"?>
<setup xmlns="https://www.benerator.de/schema/2.0.0"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="https://www.benerator.de/schema/2.0.0 https://www.benerator.de/schema/rapiddweller-benerator-ce-2.0.0.xsd">
    <!-- content here -->
</setup>
```

benerator descriptor files are supposed to be named `benerator.xml` or end with the suffix `.ben.xml`.

## benerator properties

Several global benerator properties allow for customization of its behavior:

| name | description | default setting |
| --- | --- | --- |
| defaultEncoding | the default file encoding to use for reading and writing text files | the system's file encoding |
| defaultLineSeparator | the line separator to use by default | the system's line separator |
| defaultTimeZone | The time zone to use | The system's time zone |
| defaultLocale | The locale to use if none has been specified explicitly | The system's language code, e.g. 'de' |
| defaultDataset | The dataset to use if none has been specified explicitly | The system's country's two-letter ISO code, e.g. 'US' |
| defaultPageSize | the number of entities to create in one 'run', typically a transaction | 1 |
| defaultScript | The default script engine to use for evaluating script expressions | ben (rapiddwellerScript) |
| defaultNull | tells if nullable attribute should always be generated as null by default | true |
| defaultSeparator | the default column separator to use for csv files | , |
| defaultErrorHandler | the default error handling mechanism to use | fatal |
| validate | Boolean flag to turn off validation (e.g. of XML validity and type definition consistency). | true |
| maxCount | limits the maximum cardinality of all entity and association generations. If set to 0, cardinalities will not be limited. | -1 |
| defaultOneToOne | When set to to Benerator assumes each relation is one-to-one. | false |
| acceptUnknownSimpleTypes | When set to true, Benerator accepts unknown simple data types from its DescriptorProviders, relying on the user to choose the correct data type when generating. | false |

You can configure them in the `<setup>` element, e.g.

```xml

<setup xmlns=...
        defaultencoding="utf-8"
        defaultPageSize="1000">
```

## `<include>`

### Inclusion of properties files

An alternative way to specify the Benerator properties from the previous chapter is to specify them in a properties
file, e.g.

```properties
context.defaultEncoding=UTF-8
context.defaultPageSize=1000
```

and include the properties file in the benerator descriptor file:

```xml

<include uri="my.properties"/>
```

This way you can easily use different settings in different environments (see '[Staging](advanced_topics.md#staging)').

File entries that do not begin with 'benerator“ are simply put into the generation context and can be used to configure
generation behavior.

### Sub-Invocation of descriptor files

Besides properties files, Benerator descriptor files can be included too, e.g.

```xml

<include uri="subgeneration.ben.xml"/>
```

## Global settings

benerator supports global settings. They can be evaluated using script expressions, e.g. {user_count}. This way,
different types of settings may be evaluated:

* system environment

* Java virtual machine parameters

* context variables

A setting is explicitly defined using a setting element:

```xml

<setting name="threshold" value="5"/>
```

## `<import>`

Benerator has lots of plugin interfaces but is agnostic of most implementors. So you need to explicitly import what you
need.

The following packages are imported by default (providing, for example, the ConsoleExporter):

| com.rapiddweller.benerator.consumer | General-purpose consumer classes |
| --- | --- |
| com.rapiddweller.benerator.primitive | Generators for primitive data types |
| com.rapiddweller.benerator.primitive.datetime | Generators for date, time and timestamp data |
| com.rapiddweller.benerator.distribution.sequence | Distributions of 'Sequence' type |
| com.rapiddweller.benerator.distribution.function | Distributions of 'Function' type |
| com.rapiddweller.benerator.distribution.cumulative | Distributions of type 'CumulativeDistributionFunction' |
| com.rapiddweller.benerator.sample | Generator components that use sample sets or seeds |
| com.rapiddweller.model.consumer | ConsoleExporter and LoggingConsumer |
| com.rapiddweller.common.converter | Converter components from rd-lib-common |
| com.rapiddweller.common.format | Format components from rd-lib-common |
| com.rapiddweller.common.validator | Validator components from rd-lib-common |
| com.rapiddweller.platform.fixedwidth | Fixed column width file importer and exporter |
| com.rapiddweller.platform.csv | CSV file importer and exporter |
| com.rapiddweller.platform.dbunit | DbUnit file importer and exporter |
| com.rapiddweller.platform.xls | Excel(TM) Sheet importer and exporter |

Benerator extensions can be bundled as domains (logical extensions) or platforms (technical extensions). You can export
different bundles as comma-separated lists:

```xml

<import domains="address, net"/>
<import domains="organization"/>
<import platforms="csv, db"/>
```

Imports must be the first elements used in a descriptor file.

When using a Benerator plugin or another library, you need to make sure that Benerator finds its binary. There are three
alternatives:

1. Putting the associated jar file(s) into the lib folder of your Benerator installation. This way it is available for
   all data generation projects on your machine. If you work in a team where everyone is familiar with Benerator and the
   toolset is not based on Maven, this is generally the preferred approach.

2. Create a subfolder named lib under the data generation project folder and put the jar file(s) there. When
   distributing the project to be executed on machines with plain Benerator installations, distribute the full folder
   content including the lib subfolder.

3. When using Maven to run Benerator, simply create the necessary Maven dependencies and Maven will acquire all needed
   libraries dynamically. Read more about this in '[Maven Benerator Plugin](maven_benerator_plugin.md)'

## `<generate>`

`<generate>` elements are used to generate data from scratch. There are lots of configuration options. The minimal
configuration specifies the type of data to be generated. For now, all generated data are 'entities' (composite data).

```xml

<generate type="Person" count="10" consumer="ConsoleExporter"/>
```

This will make Benerator generate 10 'Person' Entities and send them to a ConsoleExporter that prints out the persons to
the console. But what is a Person? Benerator will figure it out by itself if it knows e.g. a database with a 'PERSON'
table, an XML schema with a 'Person' element, or any other 'DescriptorProvider'. Benerator will generate database-valid
or XML-Schema-valid data automatically. More about this later.

Let us start without DescriptorProviders, manually putting together what we need.

Entities consist of members, e.g. `<attribute>`s, `<id>`s or `<reference>`s. I will concentrate on attributes in the
following sections and explain ids and references later.

### "constant"

The simplest way to define data generation is using the same value for all generated data:

```xml

<generate type="Person" count="10" consumer="ConsoleExporter">
    <attribute name="active" type="boolean" constant="true"/>
</generate>
```

So we define, that all Person entities are generated with an 'active' attribute of type 'boolean' that is set to 'true'.

### "values"

Attributes may be randomly set from a list of comma-separated values

```xml

<generate type="Person" count="10" consumer="ConsoleExporter">
    <attribute name="firstName" type="string" values="'Alice','Bob','Charly'"/>
    <attribute name="rank" type="int" values="1,2,3"/>
</generate>
```

So we define, that Person entities have a 'firstName' attribute that is 'Alice', 'Bob' or 'Charly' and a rank of 1, 2 or
3\. Note that string literals must be 'quoted', while number or Boolean literals do not.

### "pattern": Generation by Regular Expression

String attribute generation can be configured using the "pattern" attribute with a regular expression, for example:

```xml

<generate type="Person" count="10" consumer="ConsoleExporter">
    <attribute name="salutation" type="string" pattern="(Mr|Mrs)"/>
    <attribute name="postalCode" type="string" pattern="[1-9][0-9]{4}"/>
</generate>
```

You can find a detailed description of Benerator's regular expression support
in [Regular Expression Support](regular_expression_support.md).

## `<iterate>`

The `<iterate>` element is used to iterate through pre-existing data, e.g. in a data file or database. The general form
is

```xml

<iterate type="Person" source="persons.csv"/>
```

which iterates through all Persons defined in a CSV-file called 'persons.csv'.

!!! note

      By default, iteration goes once from beginning to the end. Consider using the parameter `cyclic="true"` for iterating
      repeatedly and check this manual for applying distributions or filter the data to iterate through. Learn more in
      '[Relational Databases->Determining attribute values](using_relational_databases.md#determining-attribute-values-by-a-database-query)'

### "offset"

In whatever type of data generation or iteration, an **offset** can be applied to skip the heading entries of a data
source, e.g.

```xml

<iterate type="Person" source="persons.csv" offset="10"/>
```

leaves out the first ten entries of the persons.csv file.

## `<echo>`

The meaning of the `<echo>` element is similar to the echo command in batch files: Simply writing information to the
console to inform the user what is happening, e.g.

```xml

<echo>Running...</echo>
```

For Mac OS X users there is a nice extra feature: When using **type='speech'**, Benerator uses Mac OS X's speech
facility to speak the text. When executed on other operating systems, the text is only printed to the console:

```xml

<echo type='speech'>Generation Finished</echo>
```

## `<beep/>`

makes Benerator emit a short beep

## `<comment>`

The `<comment>` element also prints output, not to the console, but to a logger. Thus you have the option of configuring
whether to ignore the output or where to send it to.

```xml

<comment>`Here we reach the critical part...`</comment>
```

Using XML comments `<!-- -->` instead of comment descriptors would make it harder for you to comment out larger portions
of a file for testing and debugging.

## `<execute type="shell">`

The `<execute>` element serves to execute different kinds of code. One option is the execution of shell commands:

```xml

<execute type="shell">start-database.sh</execute>
```

The program output is printed to the console.

Note that some windows shell commands are only available in the command-line interpreter. In order to invoke them, you
need to call cmd /C, e.g.

```xml

<execute type="shell">cmd /C type myGeneratedFile.csv</execute>
```

You can use `<execute>` for invoking scripts too (SQL, rapiddwellerScript, JavaScript, FreeMarker and more), but that
will be explained later.

## `<wait>`

The `<wait>` element makes Benerator wait for a fixed or a random amount of time.

A fixed amount of time is useful, e.g. for waiting until a system is initialized:

```xml

<wait duration="20000"/>
```

The duration is the time in milliseconds.

Random periods of wait time are useful when using Benerator to simulate client activity on a system. For this, you can
nest `<wait>` elements in `<generate>` elements. More about this later.

## `<error>`

You can make Benerator signal an error with a message and code:

```xml

<error code="-3">An error has occured</error>
```

If Benerator is not configured to do otherwise, it prints out the error message, cancels execution, finishes the process
and returns the exit code to the operating system. If no exit code is specified, Benerator uses -1.

## `<if>`

Evaluates a script expression and executes sub-elements depending on the result.

Either a decision to execute something or not:

```xml

<if test="com.rapiddweller.common.SystemInfo.isWindows()">
    <echo>Running under Windows</echo>
</if>
```

or a decision between alternatives:

```xml

<if test="com.rapiddweller.common.SystemInfo.isWindows()">
    <then>
        <execute type="shell">cmd /C type export.csv</execute>
    </then>
    <else>
        <execute type="shell">cat export.csv</execute>
    </else>
</if>
```

A typical application of the `<if>` element is to check if a required configuration is defined, and if not, to fall back
to a default...:

```xml

<if test="!context.contains('stage')">
    <echo>No stage defined, falling back to 'dev'</echo>
    <setting name="stage" value="dev"/>
</if>
```

...or to report an error:

```xml

<if test="com.rapiddweller.common.SystemInfo.isWindows()">
    <error>No stage has been set</error>
</if>
```

## `<while>`

The `<while>` element executes sub elements as long as a boolean 'test' expression resolves to **true**:

```xml

<generate type="test" count="10" consumer="ConsoleExporter">
    <id name="identifier" type="long"/>
    <while test="this.identifier == 5">
        <echo>add 10 to this.identifier</echo>
        <execute type="ben">this.identifier = this.identifier + 10</execute>
        <wait duration="1000"/>
    </while>
</generate>
```

## `<id>` - Generating unique identifiers

For marking an entity member as an identifier, it is declared with an `<id>` element, e.g.

```xml

<id name="identifier" type="long"/>
```

There are several special id generators available. If you do not specify one explicitly, Benerator takes the
IncrementalIdGenerator.

For explicitly choosing or initializing an id generator, use the generator attribute, e.g.:

```xml

<id name="identifier" type="long" generator="new IncrementalIdGenerator(100)"/>
```

for using an IncrementalIdGenerator, that starts with the value 100.

See '[Common ID Generators](component_reference.md#common-id-generators)' for a complete ID generator reference and
'[Using Relational Databases](using_relational_databases.md)' for database-related id generators.

Instead of using a generator, you can as well use other `<attribute>`-like features, e.g. scripts:

```xml

<id name="id" type="long" script="parent.id"/>
```

## Naming Conventions

For automatic support of special file content, the following naming conventions apply:

| File Name | File Type |
| --- | --- |
| *.ben.xml | benerator descriptor file |
| *.dbunit.xml | DbUnit data field |
| *.csv | CSV file with data of simple type |
| *.ent.csv | CSV file with entity data |
| *.wgt.csv | CSV file with weighted data of simple type |
| *.fcw | Fixed column width files with entity data |
| *.set.properties | Dataset nesting definition |
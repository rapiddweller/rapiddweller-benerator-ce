# Component Reference

Benerator has lots of predefined generators which are available implicitly from the descriptor. Most of them only need to be created explicitly when
using the Benerator API programmatically.

## Generators

### Domain Generators

For domain-specific generators (e.g. person, address, finance), see **'[Domains](domains.md)'**.

### Common Id Generators

Benerator contains the following common predefined and platform-independent generators:

* **IncrementalIdGenerator**: Creates consecutive id values, starting with 1 by default.

* **UUIDGenerator**: Creates UUIDs by the JDK class java.util.UUID

* **HibUUIDGenerator**: Creates UUIDs like the Hibernate UUID key generator

* **LocalSequenceGenerator**: Mimics the behavior of a (named) database sequence on a single client VM. Its property '
  cached' (true by default) specifies if sequence value changes shall be persisted immediately or in the end.

### Database-related Generators

See **'[Using Relational Databases](using_relational_databases.md)'**.

### simple type generators

* **CharacterGenerator**:

* **IncrementGenerator**: Generates numbers starting with one and incrementing the number on each subsequent call

* **StringGenerator**: Generates strings based on character set, prefix, suffix and length characteristics. This is the typical component for
  generating code numbers. Properties: charSet (regular expression for a character class), locale, unique, ordered, prefix, minInitial, suffix,
  minLength, maxLength, lengthGranularity, lengthDistribution

* **RegexStringGenerator**: Generates strings that match a given regular expression. This is the typical component for generating strings that are
  composed of different sub patterns. Properties: pattern (regular expression), unique, ordered, locale, minLength, maxLength

* **MessageGenerator**: Composes strings using a MessageFormat

* **LuhnGenerator**: Generates Luhn-valid strings like credit card numbers

### current date / time generators

* **CurrentDateGenerator**: Generates java.util.Date objects that represent the current date

* **CurrentDateTimeGenerator**: Generates java.util.Date objects that represent the current date and time

* **CurrentMilliTimeGenerator**: Generates long values that denote the number of milliseconds since 1970-01-01 00:00:00

* **CurrentNanoTimeGenerator**: Generates long values that denote a number of milliseconds since an arbitrary point in time (possible even in the
  future, so values may be negative)

* **CurrentTimeGenerator**: Generates java.util.Date objects that represent the current time of the day

### arbitrary date / time generators

* **DateGenerator**: Generates date values that represent a certain time at a certain day based on a common Distribution

* **DayGenerator**: Generates date values that represent „day“ dates – dates at midnight

* **DateTimeGenerator**: Generates date values with date and time configurable independentlyIts properties are: minDate, maxDate, dateGranularity,
  dateDistribution, minTime, maxTime, timeGranularity, timeDistribution. For a 9-to-5 datetime on odd days in August 2010, configure

```xml

<bean id="dtGen" class="DateTimeGenerator">
  <property name='minDate' value='2010-08-01'/>
  <property name='maxDate' value='2010-08-31'/>
  <property name='dateGranularity' value='00-00-02'/>
  <property name='dateDistribution' value='random'/>
  <property name='minTime' value='08:00:00'/>
  <property name='maxTime' value='17:00:00'/>
  <property name='timeGranularity' value='00:00:01'/>
  <property name='timeDistribution' value='random'/>
</bean>
```

### file related generators

* **FileGenerator**: generates java.io.File objects representing files in a given directory structure

* **FileNameGenerator**: generates file names representing files in a given directory structure

* **TextFileContentGenerator**: provides text file contents as String

* **BinaryFileContentGenerator**: provides binary file contents as byte[]

### State Generators

* **StateGenerator**: Generates states based on a state machine

* **StateTransitionGenerator**: Like the StateGenerator, but generating Transition objects

### Seed Based Generators

* **SeedWordGenerator**: Generates new words based on rules derived from a dictionary.

* **SeedSentenceGenerator**: Generates sentences based on rules derived from a text file.

## Distributions

A Distribution describes stochastic properties for distributing the data that Benerator generates. You can use the predefined distributions or
implement and introduce custom implementations. The most important types of distribution are _Sequence_, _WeightFunction_ and _
CumulativeDistributionFunction_.

A Distribution implements a common concept for generating numbers or taking values from a data source and providing them in a rearranged order or
distribution with similar semantics as the number generation feature.

As an example, a 'Skip2' sequence might generate numbers with an increment of 2: 1, 3, 5, 7,… When it is used to redistribute given data item1, item2,
item3, item4,... , it would provide the values item1, item3, ...

While most Distribution components implement number generation as well data rearrangement, they are not required to support both concepts.

All Distributions listed below are included in the default imports.

### Memory consumption

Distributions that are based on number generation may adopt data redistribution by simply loading all available data into a long list in RAM and then
using their number generation feature to determine indices of the data to provide. If the data amount is large, you may get memory problems. In order
to provide an easy start, Benerator reduces the default size of these lists to 100,000 elements, prints out an error message if the number is
exceeded, but simply continues to work with the reduced amount of data. You can allow Benerator to use a larger cache by adding a benerator.cacheSize
to your **BENERATOR_OPTS**, e.g. 

`-Dbenerator.cacheSize=2000000`

If this makes you run into an OutOfMemoryError, check the '
[Troubleshooting](troubleshooting.md)' section on how to allocate a larger Java heap in Benerator.

### Sequences

Sequences reflect the idea of a mathematical sequence. They primarily focus in number generation, but they can be applied for data redistribution as
well. Most sequences have a default instance which can be used by their literal, e.g. `distribution="random"` uses the 'random' literal for the
distribution defined in the class RandomSequence.

| Class | RandomSequence |
| --- | --- |
| Description | Creates uniformly distributed random values |
| Default Instance | random |

| Class | CumulatedSequence |
| --- | --- |
| Description | Creates random values with a bell-shape probability distribution |
| Default Instance | cumulated |

| Class | StepSequence |
| --- | --- |
| Description | Depending on the settings of property 'delta', it starts with the min or max value of the specified range. With each further invocation, the 'increment' value is added. If addition makes the current value exceed the specified number range, the Sequence becomes unavailable. So the numbers provided are unique. Example: increment = -2, range=1..7: 7, 5, 3, 1 |
| Default Instances | increment: Uses delta = 1 to create incremental values, e.g. 1, 2, 3,... |
| Property | Property Description | Default Value |
| delta | The difference between the next value and the previous one | 1 |

| Class | RandomWalkSequence |
| --- | --- |
| Description | Starting with an → **initial** value, a random value between → **minStep** and → **
maxStep** is added on each subsequent invocation |
| Default Instance | randomWalk |
| Property | Property Description | Default Value |
| minStep | The maximum delta between the next and the previous value | -1 |
| maxStep | The maximum delta between the next and the previous value | 1 |
| initial | If no initial value was configured explicitly, number generation starts with the min, max or medium value of the specified range – depending on the settings of minStep and maxStep | null |

| Class | ShuffleSequence |
| --- | --- |
| Description | Can be used to iterate quickly through a large number range with avoiding duplicate values. It starts from an offset of 0 and iterates the number range with a fix increment. After the range is covered, it increases the offset by one and reiterates the range. When the offset reaches the same value as the increment, it is set back to 0 again. For an increment of 3 in a range 1..7, the generated numbers would be 1, 4, 7, 2, 5, 3, 6, 1, 4, ... |
| Default Instance | shuffle |
| Property | Property Description | Default Value |
| increment | See the class description | 2 |

| Class | WedgeSequence |
| --- | --- |
| Description | Starting with first the lowest, then the highest available number, this alternatively provides increasing small numbers and decreasing large numbers until they converge in the middle and the Sequence becomes unavailable. So this generation is unique. For a number range 1..7, the generated numbers would be: 1, 7, 2, 6, 3, 5, 4. |
| Default Instance | wedge |

| Class | BitReverseSequence |
| --- | --- |
| Description | Creates numbers by continually increasing an internal counter and providing its value in bit-reversed order. This stops when each available number has been generated once, thus providing unique number generation. This comes close to a unique random distribution. |
| Default Instance | bitreverse |

| Class | ExpandSequence |
| --- | --- |
| Description | Distributes numbers or data of unlimited volume in a unique or non-unique manner, by starting with a limited lower range and continuously expanding data region as data is generated. This comes close to a unique random distribution and can be used to iterate over very huge amounts of data. |
| Default Instance | expand |
| Property | Property Description | Default Value |
| cacheSize | The maximum number of elements to keep in RAM at the same time | 100 |
| bucketSize | The size of 'buckets' over which to distribute the iterated data. The smaller the number, the more buckets are used and the more erratic the generated data looks. | 10 |
| duplicationQuota | The probability by which a data element will be reused in a later call | 0 |

| Class | HeadSequence |
| --- | --- |
| Description | When applied to a data source or generator, only the first few elements are provided. The number of elements is defined by the **
size** property. |
| Default Instance | head |
| Property | Property Description | Default Value |
| size | The size of the buffer | 1 |

| Class | LiteralSequence |
| --- | --- |
| Description | Defines a number sequence using a comma-separated list literal. |
| Default Instance | – |
| Property | Property Description | Default Value |
| spec | A comma-separated list with all values in the order in which they shall be provided ,e.g. '2,3,5,7,11' | – |

| Class | WeightedNumbers |
| --- | --- |
| Description | Creates numbers based on a weighted-number literal , e.g. '1^70, 3^30' for generating 70% '1' values and 30% '3' values. This is a convenient and simple approach for controlling parent-child cardinalities in nested data generation. |
| Default Instance | – |
| Property | Property Description | Default Value |
| spec | A weighted-number literal. It lists weighted values in a comma-separated list. Each weighted value is specified by the numeric value followed by a circumflex (^) and the weight value, for example '1^70,3^30' | – |

| Class | FibonacciSequence |
| --- | --- |
| Description | Generates numbers based on the Fibonacci Sequence |
| Default Instance | fibonacci |

| Class | PadovanSequence |
| --- | --- |
| Description | Generates numbers based on the Padovan Sequence |
| Default Instance | padovan |


### Weight Functions

Weight functions are another special case of Distributions. 
They are based on a function which is supposed to allow continuous value generation, 
but since Benerator needs to perform a numerical integration for deriving random values, 
a granularity must be applied. This way, the generated value set is quantized. 
Another drawback of the approach is that fine-grained generation is memory-consuming and slow.

Thus, it is recommended to avoid weight functions if possible and choose a similar 
Sequence or CumulativeDistributionFunction instead.


#### GaussianFunction

Implements the well-known Gaussian Function.

Full class name: `com.rapiddweller.benerator.distribution.function.GaussianFunction`

Parameters: `average [, deviation]`

Example:

```xml
<import class="com.rapiddweller.benerator.distribution.function.*"/>
    ...
<attribute name="price" type="big_decimal" min="0.1" max="99.90" granularity="0.1"
           distribution="new GaussianFunction(50,20)"/>
```

#### ExponentialFunction

The Exponential Function.

Full class name: `com.rapiddweller.benerator.distribution.function.ExponentialFunction`

Parameters: `[scale,] frequency`

Example:

```xml
<import class="com.rapiddweller.benerator.distribution.function.*"/>
    ...
<attribute name="category" type="char" values="A,B,C" distribution="new ExponentialFunction(0.5)"/>
```

#### DiscreteFunction

Discrete Function that specifies an explicit weight for each possible value

Full class name: `com.rapiddweller.benerator.distribution.function.DiscreteFunction`

Parameters: `weight1 [, weight2 [, weight3 ...]]`

Example:

```xml
<import class="com.rapiddweller.benerator.distribution.function.*"/>
    ...
<attribute name="rating" type="int" min="1", max="3" distribution="new DiscreteFunction(1, 2, 1)"/>
```

### CumulativeDistributionFunction

The CumulativeDistributionFunction is another special case of a Distribution, which allows for continuous value generation as opposed to Sequence and
WeightFunction.

### ExponentialDensityIntegral

Inverse of the integral of the probability density f(x) = a e^{-ax} (x >` 0), which resolves to F^{-1}(x) = - log(1 - x)
/ a.


## Converters

Benerator supports two different types of converter interfaces:

* com.rapiddweller.common.Converter

* java.text.Format

### rapiddweller Converters

The following converter classes are located in the package **com.rapiddweller.common.converters** and are imported with the default imports:

* **ByteArrayToBase64Converter**: Converts byte arrays to strings which are base-64-encoded

* **ToLowerCaseConverter**: Converts strings to lowercase

* **ToUpperCaseConverter**: Converts strings to uppercase

* **LiteralParser**: Parses strings as numbers, strings, dates and times

* **MD5Hash**, **SHA1Hash**, **SHA256Hash**: Convert any data to a hexadecimal hash code

* **MD5HashBase64**, **SHA1HashBase64**, **SHA256HashBase64**: Convert any data to a hash code in Base64 format

* **JavaHash**: Convert any data to a hexadecimal hash code. This implementation is faster than the hash converters above

* **MessageConverter**: Converts an object, wrapping it with a message string, using a java.text.MessageFormat

* **PropertyResourceBundleConverter**: Uses a Java PropertyResourceBundle to translate keywords to translations in a given Java Locale

* **ToStringConverter**: Converts arbitrary objects to strings

* **UniqueStringConverter**: Assures uniqueness for all processed Strings by appending unique numbers to recurring instances (attention: limited to a
  few 100.000 elements)

* **URLEncodeConverter**: Applies a URL encoding to strings

* **URLDecodeConverter**: decodes URL encoded strings

* **PrintfConverter**: formats objects using a pattern in printf format

* **RegexReplacer**: Uses a regular expression to replace parts of the processed strings

* **SubstringExtractor**: Extracts substrings from strings. It has the properties '**from**' and '**to**'. If '**to**'
  is not set, it extracts from '**from**' until the end. If '**to**' or '**from**' is negative, it denotes a backwards position count, making e.g. -1
  the last character position.

* **EscapingConverter**: Escapes strings in Java style, like "A\tB"

* **Number2CharConverter**: Converts a number to a character of the corresponding ASCII code

* **Char2StringConverter**: Converts a character to a string of length 1

* **EscapingConverter**: Escapes control codes in a string in C and Java style, e.g. with \r, \n, \t

* **Number2CharConverter**: Converts a number to a character with the corresponding ASCII code, e.g. 65 → 'A'

The package **com.rapiddweller.text** provides the following converters:

* **DelocalizingConverter**: Converts strings with non-ASCII letters to ASCII strings, e.g. Müller → Mueller, Sœr → Soer

* **NameNormalizer**: Normalizes a string by trimming it, normalizing inner white space and formatting each word to start with an uppercase character
  and continue with lowercase characters

* **NormalizeSpaceConverter**: Trims a string and normalizes inner white space to one space character

* **ToHexConverter**: Renders characters, strings snd integral numbers in hexadecimal representation

In the package **com.rapiddweller.benerator.primitive.number** there are two converters that can be used to quantize numerical values:

* **FloatingPointQuantizer**, **IntegralQuantizer, NumberQuantizer**: Quantize numbers to be a **min** value plus an integral multiple of a **
  granularity**

* **NoiseInducer**: Adds numerical noise to numbers. The noise characteristics can be configured with the properties minNoise, maxNoise,
  noiseGranularity and noiseDistribution. When setting the boolean property relative to true, noise is relative, where maxCount=1 corresponds to 100%
  noise-to-signal ratio. If relative=false, the absolute value of the noise is added or subtracted. Example:

NoiseInducer example:

```xml
<bean id="inducer" class="com.rapiddweller.benerator.primitive.number.NoiseInducer">
    <property name="minNoise" value="-0.2"/>
    <property name="maxNoise" value="0.2"/>
    <property name="noiseGranularity" value="0.01"/>
    <property name="noiseDistribution" value="cumulated"/>
    <property name="relative" value="true"/>
</bean>
<generate count="5" consumer="ConsoleExporter">
    <attribute name="x" type="int" constant="100" converter="inducer"/>
</generate>
```

produces the result:

```bash
entity[x=99]
entity[x=105]
entity[x=92]
entity[x=104]
entity[x=99]
```

### Java Formats

Beware that the java.text.Format classes are not thread-safe!

* **SimpleDateFormat**: Uses a pattern to format dates as strings

* **DecimalFormat**: Uses a pattern to format numbers as strings

## Validators

### Domain Validators

For the validators from the domains see **'[Domains](domains.md)'**.

### Common validators

* **CharacterRangeValidator**: Validates if a character is in a certain range

* **NotNullValidator**: Requires the validated data to be not null

* **StringLengthValidator**: Limits allowed strings to a minimum and/or maximum length

* **StringValidator**: Validates string by min length, max length and a charactor validator

* **UniqueValidator**: Requires data to be unique (attention: limited to some 100.000 elements)

* **UnluckyNumberValidator**: Checks if a String contains an 'unlucky' number like 13 in western cultures or 4 in east-asian cultures

* **DayOfWeekValidator**: Accepts only Dates of certain (configurable) weekdays

* **RegexValidator**: Validates if a string matches a regular expression

* **LuhnValidator**: Checks if a number string (e.g. credit card number) is Luhn-valid

### Tasks

* **FileJoiner**: Joins several files (**sources**) into a **destination** file, optionally **append**ing the joint data to an existing destination
  file, or overwriting it. If **deleteSources** is set to true, the sources are deleted afterwards.

* **FileDeleter**: Deletes a number of **files**.

## Consumers

A Consumer consumes generated data and usually is used for exporting or persisting the data.

### LoggingConsumer

| Class Name | LoggingConsumer |
| --- | --- |
| Import | default |
| Class Description | Logs all Consumer invocations to a logger |

### ConsoleExporter

| Class Name | ConsoleExporter |
| --- | --- |
| Import | default |
| Class Description | Prints entities in the console |
| Constructors | Default constructorConstructor with 'limit' argument (see below) |
| Property | Property Description | Default Value |
| limit | The maximum number of entries per type to print out | unlimited |
| nullString | Text to represent _null_ values | "" |
| datePattern | The pattern to render date values | "yyyy-MM-dd" |
| timePattern | The pattern to render time values | "HH:mm:ss" |
| timestampPattern | The pattern to render timestamp values | "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS" |
| decimalPattern | The pattern to render decimal values | System default |
| decimalSeparator | The decimal separator to use for decimal values | System default |
| integralPattern | The pattern to integral number values | System default |

### JavaInvoker

| Class Name | JavaInvoker |
| --- | --- |
| Import | `<import platforms="java"/>` |
| Class Description | Maps entity components to method parameters and invokes a method on a Java object with these parameters. |
| Property | Property Description | Default Value |
| target | The Java object on which to invoke the method |  |
| methodName | The name of the Java method to invoke |  |

Usage example:

```xml
<bean id="service" spec="..." />
<bean id="invoker" spec="new JavaInvoker(ejb, 'enrolCustomer')" />
```

### DbUnitEntityExporter

| Class Name | DbUnitEntityExporter |
| --- | --- |
| Import | `<import platforms="dbunit"/>` |
| Class Description | Exports entities to a file in DbUnit XML format. |
| Property | Property Description | Default Value |
| uri | The URI of the file to create | "data.dbunit.xml" |
| encoding | The character encoding to use for the file | The system default |

### XMLEntityExporter

| Class Name | XMLEntityExporter |
| --- | --- |
| Import | `<import platforms="xml"/>` |
| Class Description | Exports entities to an XML file |
| Property | Property Description | Default Value |
| uri | The URI of the file to create | "export.xml" |
| encoding | The character encoding to use for the file | The system default |

### NoConsumer

| Class Name | NoConsumer |
| --- | --- |
| Import | default |
| Class Description | In some cases a pseudo `<generate>` statements acts as a mechanism to perform a loop. In such cases a consumer does not make sense but causes Benerator to emit a warning „No consumers defined for `<loop name>`“. In order to avoid this warning, you can use the NoConsumer class, which is an empty implementation of the Consumer interface. |

### ScriptedEntityExporter

| Class Name | ScriptedEntityExporter |
| --- | --- |
| Import | `<import platforms="script"/>` |
| Class Description | Exports entities to a file in custom format, rendered using a script language, e.g. FreeMarker. Three different script expressions may be applied for header (headerScript property), entity (partScript) and footer (footerScript). |
| Property | Property Description | Default Value |
| uri | The URI of the file to create |  |
| encoding | The character encoding to use for the file | The system default |
| headerScript | Script to format an eventual header line |  |
| partScript | Script to format an exported entity |  |
| footerScript | Script to format an eventual footer line |  |
| nullString | Text to represent _null_ values | "" |
| datePattern | The pattern to render date values | "yyyy-MM-dd" |
| dateCapitalization | The capitalization to use when rendering a month name in a date: 'upper', 'lower' or 'mixed' | mixed |
| timePattern | The pattern to render time values | "HH:mm:ss" |
| timestampPattern | The pattern to render timestamp values | "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS" |
| timestampCapitalization | The capitalization to use when rendering a month name in a timestamp: 'upper', 'lower' or 'mixed' | mixed |
| decimalPattern | The pattern to render decimal values | System default |
| decimalSeparator | The decimal separator to use for decimal values | System default |
| integralPattern | The pattern to integral number values | System default |

### FixedWidthEntityExporter

| Class Name | FixedWidthEntityExporter |
| --- | --- |
| Import | `<import platforms="fixedwidth"/>` |
| Class Description | Exports entities to a fixed column width file. |
| Property | Property Description | Default Value |
| uri | The URI of the file to create | "export.fcw" |
| columns | A comma-separated list of column format specifications |  |
| encoding | The character encoding to use for the file | System default |
| lineSeparator | The line separator to use in the generated file | System default |
| append | If set to true, data is appended to existing files, otherwise existing files are overwritten | false |
| nullString | Text to represent _null_ values | "" |
| datePattern | The pattern to render date values | "yyyy-MM-dd" |
| dateCapitalization | The capitalization to use when rendering a month name in a date: 'upper', 'lower' or 'mixed' | mixed |
| timePattern | The pattern to render time values | "HH:mm:ss" |
| timestampPattern | The pattern to render timestamp values | "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS" |
| timestampCapitalization | The capitalization to use when rendering a month name in a timestamp: 'upper', 'lower' or 'mixed' | mixed |
| decimalPattern | The pattern to render decimal values | System default |
| decimalSeparator | The decimal separator to use for decimal values | System default |
| integralPattern | The pattern to integral number values | System default |

The line format is described as a comma-separated list of property names with format spec, e.g. name[20],age[3r]
,points[5.2r0]. The format spec consists of

* [] brackets

* the (required) column width

* an optional alignment flag l, r or c (for left, right, center), left by default

* an optional pad character, space by default

So a property configuration of name[20],age[3r],points[5.2r0] would resolve to three columns,

first, a name entry, padded to 20 columns using spaces (default), aligned to the left (default)

second, an age entry, padded to 3 columns using spaces (default), aligned to the right

third, a points column, padded to 5 columns using zeros, having two fraction digits, aligned to the right

and would be rendered like this:

```
Alice Hamilton 2310.05
Bob Durand 4601.23
Helmut Schmidt 10226.14
```

### XLSEntityExporter

| Class Name | XLSEntityExporter |
| --- | --- |
| Import | `<import platforms="xls"/>` |
| Class Description | Exports entities to Excel XLS files. For using this exporter you need to add the Apache POI library to the Benerator's lib directory. |
| Property | Property Description | Default Value |
| uri | The URI of the file to create | "export.xls" |
| nullString | Text to represent _null_ values | "" |

### CSVEntityExporter

| Class Name | CSVEntityExporter |
| --- | --- |
| Import | `<import platforms="csv"/>` |
| Class Description | Exports entities to a CSV file |
| Property | Property Description | Default Value |
| uri | The URI of the file to create | "export.csv" |
| columns | A comma-separated list of column names |  |
| headless | Flag to leave out column headers | false |
| separator | The character to use as column separator | "," |
| encoding | The character encoding to use for the file | System default |
| lineSeparator | The line separator to use in the generated file | System default |
| endWithNewLine | Specifies if the last row in the file should end with a line break | false |
| append | If set to true, data is appended to existing files, otherwise existing files are overwritten | false |
| nullString | Text to represent _null_ values | Empty string |
| quoteEmpty | When set to 'true', empty strings are formatted with double quotes ("",""), otherwise an empty field (,) | false |
| datePattern | The pattern to render date values | "yyyy-MM-dd" |
| dateCapitalization | The capitalization to use when rendering a month name in a date: 'upper', 'lower' or 'mixed' | mixed |
| timePattern | The pattern to render time values | "HH:mm:ss" |
| timestampPattern | The pattern to render timestamp values | "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS" |
| timestampCapitalization | The capitalization to use when rendering a month name in a timestamp: 'upper', 'lower' or 'mixed' | mixed |
| decimalPattern | The pattern to render decimal values | System default |
| decimalSeparator | The decimal separator to use for decimal values | System default |
| integralPattern | The pattern to integral number values | System default |

### SQLEntityExporter

| Class Name | SQLEntityExporter |
| --- | --- |
| Import | `<import platforms="db"/>` |
| Class Description | Exports entities as 'INSERT' commands to a SQL file |
| Property | Property Description | Default Value |
| uri | The URI of the file to create | "export.sql" |
| encoding | The character encoding to use for the file | System default |
| lineSeparator | The line separator to use in the generated file | System default |
| append | If set to true, data is appended to existing files, otherwise existing files are overwritten | false |
| dialect | The SQL dialect to use in the generated file. Available values: db2, derby, firebird, hsql, h2, oracle, postgres, sql_server |  |
| datePattern | The pattern to render date values | "yyyy-MM-dd" |
| timePattern | The pattern to render time values | "HH:mm:ss" |
| timestampPattern | The pattern to render timestamp values | "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS" |
| decimalPattern | The pattern to render decimal values | System default |
| decimalSeparator | The decimal separator to use for decimal values | System default |
| integralPattern | The pattern to integral number values | System default |

## EntitySources (Importers)

Benerator provides the following implementations of the EntitySource interface:

### DbUnitEntitySource

| Class Name | DbUnitEntitySource |
| --- | --- |
| Import | `<import platforms="dbunit"/>` |
| Class Description | Imports entities from a DbUnit XML file |
| Property | Property Description | Default Value |
| uri | The URI of the file to read | "export.sql" |

### CSVEntitySource

| Class Name | CSVEntitySource |
| --- | --- |
| Import | `<import platforms="csv"/>` |
| Class Description | Imports entities from a CSV file |
| Property | Property Description | Default Value |
| uri | The URI of the file to read | "export.sql" |
| encoding | The character encoding used in the file | System default |
| separator | The line separator used in the file | "," |
| columns | When set, the input file is expected to have no header row |  |

### FixedColumnWidthEntitySource

| Class Name | CSVEntitySource |
| --- | --- |
| Import | `<import platforms="fixedwidth"/>` |
| Class Description | Imports entities from a fixed column width file |
| Property | Property Description | Default Value |
| uri | The URI of the file to read | "export.sql" |
| encoding | The character encoding of the file | System default |
| columns | The columns specification (See the FixedWidthEntityExporter for documentation) |  |

### XLSEntitySource

| Class Name | XLSEntitySource |
| --- | --- |
| Import | `<import platforms="xls"/>` |
| Class Description | Imports entities from an Excel(TM) document |
| Property | Property Description | Default Value |
| uri | The URI of the file to read |  |

## Benerator Utility Classes

### RandomUtil

| Class Name | RandomUtil |
| --- | --- |
| Import | `<import class="com.rapiddweller.benerator.util.RandomUtil"/>` |
| Class Description | Provides basic random functions |
| Method | Method Description |
| randomLong(min, max) | Returns a random long between min (inclusively) and max (inclusively) |
| randomInt(min, max) | Returns a random int between min (inclusively) and max (inclusively) |
| randomElement(value1, value2, ...) | Returns a random element of the ones listed as parameters |
| randomElement(List values) | Returns a random element from the 'values' list |
| randomIndex(List values) | Returns a random index for the 'values' list |
| randomDigit(min) | Returns a random numerical character with a value of at least min. Example: randomDigit(1) produces characters between '1' and '9'. |
| randomProbability() | Returns a float between 0 and 1 |
| randomDate(min, max) | Returns a random date between min (inclusively) and max (inclusively) |
| randomFromWeightLiteral(literal) | Evaluates the weight literal and returns one of the specified values with the specified probability. Example literal: 'A'^3,'B'^1 will produce 75% of 'A' values and 25% of 'B' values. |

## rapiddweller Commons Library

The library rd-lib-common derived from Databene Commons from Volker Bergmann is a general-purpose utility collection which also provides some features useful for data generation and manipulation.
Its converters and validators are liste above, but there are some general utility classes too. They can be invoked directly using rapiddwellerScript or
other supported script languages.

### TimeUtil

| Class Name | TimeUtil |
| --- | --- |
| Import | `<import class="com.rapiddweller.common.TimeUtil"/>` |
| Class Description | Provides time and date related utility methods |
| Method | Method Description |
| currentYear() | returns the current year as integer value |
| today() | returns the current day as date object |
| yesterday() | returns the previous date as date object |
| tomorrow() | returns the next day as date object |
| date(year, month, day) | creates a date object for the specified day in the user's default time zone. 'month' is a zero-based integer, January = 0, February = 1, ... |
| gmtDate(year, month, day) | creates a date object for the specified day in the time zone GMT. 'month' is a zero-based integer, January = 0, February = 1, ... |
| date(year, month, day, hours, minutes, seconds, milliseconds) | creates a date object for the specified day and time in the user's default time zone. 'month' is a zero-based integer, January = 0, February = 1, ... |
| date(millis) | creates a date object in the user's default time zone. The time is specified as milliseconds since 1970-01-01 |
| year(date) | returns the year of the specified date as integer |
| month(date) | returns the month of the specified date as integer |
| dayOfMonth(date) | returns the day of month of the specified date as integer |
| firstDayOfMonth(date) | returns the first day of the specified date's month |
| lastDayOfMonth(date) | returns the last day of the specified date's month |
| millis(year, month, day, hour, minute, second) | Calculates the number of milliseconds since 1970-01-01\. 'month' is a zero-based integer, January = 0, February = 1, ... |
| time(hour, minute, second) | Creates a time object for the specified time |
| time(hour, minute, second, millisecond) | Creates a time object for the specified time |
| timestamp(year, month, day, hour, minute, second, nanosecond) | Creates a timestamp value for the specified time. 'month' is a zero-based integer, January = 0, February = 1, ... |
| currentTime() | Creates a time object representing the current time |
| midnightOf(date) | Rounds down a date value that may include a time to a value that represents midnight (time = 0) |
| addDays(date, noOfDays) | Calculates a date a given number of days past a given date |
| addMonths(date, noOfMonths) | Calculates a date a given number of months past a given date |
| addYears(date, noOfYears) | Calculates a date a given number of years past a given date |

### Period

| Class Name | Period |
| --- | --- |
| Import | `<import class="com.rapiddweller.common.Period"/>` |
| Class Description | Provides constants for some time periods |
| Invocation | Description |
| Period.SECOND.millis | The number of milliseconds in a second |
| Period.MINUTE.millis | The number of milliseconds in a minute |
| Period.HOUR.millis | The number of milliseconds in an hour |
| Period.DAY.millis | The number of milliseconds in a day |
| Period.WEEK.millis | The number of milliseconds in a week |

### IOUtil

| Class Name | IOUtil |
| --- | --- |
| Import | `<import class="com.rapiddweller.common.IOUtil"/>` |
| Class Description | Provides I/O related utility methods |
| Method | Method Description |
| isURIAvailable(uri) | Tells if the file specified by the given URI exists |
| getContentOfURI(uri) | Provides the content of the specified file as string |
| getBinaryContentOfUri(uri) | Provides the content of the specified file as byte array |
| getParentUri(uri) | Determines the parent URI (folder) of the specified URI |
| getProtocol(uri) | Determines the protocol specified in the URI |
| download(sourceUrl, targetFile) | Downloads the content of a remote URI to the local file system |
| copyFile(sourceUri, targetUri) | Copies a file on the local file system |

### CharUtil

| Class Name | CharUtil |
| --- | --- |
| Import | `<import class="com.rapiddweller.common.CharUtil"/>` |
| Class Description | Provides character related utility methods |
| Method | Method Description |
| ordinal(character) | Returns a character's ordinal as integer |
| character(ordinal) | Returns the character that corresponds to the given ordinal |
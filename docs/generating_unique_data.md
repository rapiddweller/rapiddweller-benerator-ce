# Generating Unique Data 

## ID Generation 

For generating unique data for dataset identifiers like primary keys in a database, see the chapters „Common ID Generators“ for a complete ID generator reference and „Using databases“ for database-related id generators.

## Unique Number Generation 

Most → Sequences are able to generate unique numbers. Just apply a unique="true" to the number configuration:

`<attribute name="n" type="int" min="3" max="99" unique="true" />`

## Unique String Generation 

### Uniqueness with Regular Expression 

There are ID generators which generate UUID strings, but in most cases you have constraints on string length and character select which require you to have something more individual and configurable.

One of the best general approches is to use Benerator's feature to generate unique strings that match a regular expression. For example, for generating unique phone numbers, you could write:

`<attribute name="phone" type="string"pattern="[1-9][0-9]{2}\-[0-9]{4}\-[0-9]{5}" unique="true" />`

For an introduction to regular expressions, read the chapter „Regular Expressions“.

### Making Strings unique 

Sometimes you have less strict constraints on the strings you want to make unique. A good example is a common derivation of user names from their real names which takes the first letter of the first name and appends the last name. This might lead to non-unique results, since John Smith and Joshua Smith would get the same user name jsmith. The usual solution is to append a number to make the string for the second jsmith unique again: jsmith2\. This is exactly, what the UniqueStringConverter does:

`<generate type="user" count="10" consumer="ConsoleExporter" />`

`<variable name="person" generator="PersonGenerator"/>`

`<attribute name="user_name" script="person.givenName.substring(0, 1) + person.lastName"converter="ToLowerCaseConverter, UniqueStringConverter"/>`

`</generate>`

Note: The UniqueStringConverter keeps all used strings in memory, so when generating some billion strings, you might get memory problems.

## Removing Duplicate Values 

If you need a more individual generation algorithm of which you do not know (or care) how to make it unique, you can append a UniqueValidator to filter out duplicate values.

`<attribute name="code" pattern="[A-Z]{6,12}" validator="UniqueValidator"/>`

Note: The UniqueValidator keeps all used strings in memory, so when generating some billion strings, you might get memory problems.

## Unique iteration through a source 

When iterating data from a data source and requiring uniqueness, you need to assure for yourself, that the source data is unique:

`<attribute name="code" type="string" source="codes.csv" />`

When applying a distribution to the iterated data, configure unique="true" for assuring that the distribution does not repeat itself:

`<attribute name="code" type="string" source="codes.csv" distribution="random" unique="true" />`

## Unique Generation of Composite Keys 

As an example, let's have a look the following code:

`<generate type="product" count="6" consumer="ConsoleExporter">`

`<attribute name="key1" type="int" />` `<attribute name="key2" type="int" />`

`</generate>`

If we need to generate unique combinations of key1 and key2 we have differrent alternatives:

### Simplistic Approach 

If each value is unique, the combination of them is unique too. The following setting:

`<generate type="product" count="6" consumer="ConsoleExporter">`

`<attribute name="key1" type="int" distribution="increment" unique="true" />` `<attribute name="key2" type="int" distribution="increment" unique="true" />`

`</generate>`

The generated values are:

product[key1=1, key2=1]

product[key1=2, key2=2]

product[key1=3, key2=3]

product[key1=4, key2=4]

product[key1=5, key2=5]

product[key1=6, key2=6]

### Cartesian Product 

For generating unique composite keys the most convenient way is to create a cartesian product of unique components by nesting two creation loops.

For making the combination of key1 and key2 in the following descriptor unique:

one would add an outer 'dummy' loop and create helper variables x and y in a way that they can be combined like in a cartesian product:

`<generate type="dummy" count="2">` `<!-- no consumer! -->`

`<variable name="x" type="int" distribution="increment" unique="true" />`

`<generate type="product" count="3" consumer="ConsoleExporter">`

`<variable name="y" type="int" distribution="increment" unique="true" />`

`<attribute name="key1" type="int" script="x"/>`

`<attribute name="key2" type="int" script="y"/>`

`</generate>`

`</generate>`

The generated values are:

product[key1=1, key2=1]

product[key1=1, key2=2]

product[key1=1, key2=3]

product[key1=2, key2=1]

product[key1=2, key2=2]

product[key1=2, key2=3]

### Prototype Approach 

You can use the prototype approach for getting unique compsite keys: A variable's generation algorithm needs to assure uniqueness of the combination:

`<generate type="product" count="6" consumer="ConsoleExporter">`

`<variable name="p" generator="my.HelperClass"/>`

`<attribute name="key1" type="int" script="p.x" />` `<attribute name="key2" type="int" script="p.y" />`

`</generate>`

The most frequent application of this approach is the generation of unique database references using a prototype query. See the chapter

Prototype Queries

.

## Achieving local uniqueness 

Sometimes values need to have uniqueness of an identity component of a 'child' entity only in the context of another ('parent') item.

One simple solution is of course to have it globally unique.

If there are more constraints involved, you can of course use an appropriate generator but need to nest the generation of parent and child:

`<generate type="parent" count="5" consumer="ConsoleExporter">`

`<generate type="product" count="5" consumer="ConsoleExporter">`

`<variable name="y" type="int" distribution="increment" **unique="true">`

`<attribute name="key1" type="int" script="x"/>`

`<attribute name="key2" type="int" script="y"/>`

`</generate>`
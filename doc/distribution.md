# Distribution Concepts

Distributions enable Benerator to generate numbers with desired distribution characteristics or 
following certain a sequence. A distribution may also be applied to groups of data objects to 
provide them with certain distribution characteristics.

Distributions come in two flavors:

- Sequences: Algorithm for generating numbers
- Weights: Functions which provide the probability of a certain number

For most common needs there exist predefined sequences and weights. 

For special needs, you can define and add your own custom ones.

## Basic Configuration

A Distribution is selected with a `distribution` attribute an can be parameterized with a 
`min` and `max` value and a `granularity`. The granularity is applied in a way, 
that any generated number is `min` plus an integer multiple of `granularity`. 

Thus, a configuration 

```xml
<attribute name="price" type="double" distribution="increment" min="0.25" max="100" granularity="0.25"/>
```

yields the numbers  

```
0.25, 0.50, 0.75, 1.00, ..., 99.75, 100.00
```


## Sequence

A Sequence is basically a number generator. It can provide a custom random algorithm, 
a custom weighted number generator or a unique number generation algorithm.

The Sequences used most often are `random`, `increment` and `cumulated`. 

For a complete list of predefined sequences, 
see the '[Component Reference](component_reference.md#sequences)', 
for defining a custom Sequence, 
see '[Extending Benerator](extending_benerator.md#custom-sequences)'.


## Weight Functions

A weight function basically is a mathematical function that tells which weight 
to apply to which number.
The most frequently used weight functions are `GaussianFunction` 
and `ExponentialFunction`.

For a complete list of predefined weight functions, 
see the [Component Reference](component_reference.md#weight-functions),
for defining a custom weight function, 
see [Extending Benerator](extending_benerator.md#custom-weightfunctions).


## WeightedNumbers

WeightedNumbers is a special component for creating a small set of numbers 
based on a weighted-number literal, for example `1^70, 3^30` for generating 70% 
`1` values and 30% `3` values. 

This is a very convenient and simple approach for controlling parent-child cardinalities 
in nested data generation.

Example:

```xml
<attribute name="n" type="int" distribution="new WeightedNumbers('1^70,3^30')"/>
```

When using WeightedNumbers to determine the cardinality of an Entity part which is a container,
then the container type must be declared. Typical settings are `container='array'` 
or, in some cases, `container='list'`: 

```xml
<part name='y' container='array' countDistribution="new WeightedNumbers('0^70,1^20,2^10')">
  <attribute name='z' pattern='AAA'/>
</part>
```


## Distributing other data than numbers

'Other data' usually comes from a data source and is imported by an 
`<iterate>` or `<attribute>` element:

```xml
<attribute name="code" type="string" source="codes.csv"/>
```

When iterating through data (e.g. imported from file or database), Benerator's
default behavior is to serve each item exactly once and in the order as provided. 
When the end of the data set is reached, Benerator stops.

When setting `cyclic="true"` Benerator serves the imported data consecutively too
but does not stop when it reaches the end. Instead, it restarts iteration.

**Beware**: For SQL queries this means that the query is reissued, so it may have
a different result set than the former invocation.

```xml
<attribute name="code" type="string" source="codes.csv" cyclic="true"/>
```

But that is not really a distribution. We can do better and get probability effects:


## Importing Weights

When importing data from data sources, you can specify weights. 
They are different when importing simple data or entities:


### Importing primitive data weights

When importing primitive data from a CSV file, each value is expected to be in an extra row. If a row has more than one column, the content of the
second column is interpreted as weight. If there is no such column, a weight of 1 is assumed. Benerator automatically normalizes over all data
objects, so there is no need to care about manual weight normalization. Remember to use a filename that indicates the weight character, using a suffix
like `.wgt.csv` or `wgt.xls`.

If you, for example, create a CSV file `roles.wgt.csv`:

```
customer,7
clerk,2
admin,1
```

and use it in a configuration like this:

```xml
<generate type="user" count="100">
    <attribute name="role" source="roles.wgt.csv" />
</generate>
```

this will create 100 users of which about 70 will have the role `customer`, 20 `clerk` and 10 `admin`.


### Weighing imported entities by attribute

When importing entities, one entity attribute can be chosen to represent the weight
by specifying `distribution="weighted[attribute-name]"`.
Remember to indicate, that the source file contains entity data by using the correct
file suffix, e.g. `.ent.csv` or `.ent.xls`.

Example: If you are importing cities and want to weigh them by their population,
you can define a CSV file `cities.ent.csv`:

```
name,population
New York,8274527
Los Angeles,3834340
San Francisco,764976
```

and e.g. create addresses with city names weighted by population, when specifying

```xml
<generate type="address" count="100" consumer="ConsoleExporter">
    <variable name="city_data" source="cities.ent.csv" distribution="weighted[population]"/>
    <id name="id" type="long" />
    <attribute name="city" script="city_data.name"/>
</generate>
```


## Distributing unweighted Data

If the imported data does not come with weight information, you can apply a Distribution 
to control probability:

```xml
<attribute name="code" type="string" source="codes.csv" distribution="random"/>
```

For WeightFunctions, all available data is loaded into RAM and then the Weight Function's 
number generation feature is used to generate indices of the data items. 

Most Sequences implement data distribution as described above for Weight Functions, 
but can be programmed individually for each Sequence.
 
Attention: Most distributions load all available data to distribute into RAM. 

Most sequences should not be applied to data sets of more than 100.000 elements, 
a weight function should be restricted to at most 10.000 elements.

'Unlimited' Sequences which are suitable for arbitrarily large data sets are

- expand
- randomWalk
- repeat
- step

For the complete list of predefined Distributions, 
see the '[Component Reference](component_reference.md#distributions)', 
for programming custom Distributions, 
see '[Custom Sequences](extending_benerator.md#custom-sequences) '
and '[Custom WeightFunctions](extending_benerator.md#custom-weightfunctions)'.

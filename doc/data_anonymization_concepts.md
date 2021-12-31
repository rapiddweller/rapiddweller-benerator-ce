# Data Anonymization Concepts

## Basic Concepts

The approach for anonymizing (also obfuscating) production data is to `<iterate>` over existing data and
overwrite data fields with privacy concerns making use of all the features you learned
in [Data Generation Concepts](data_generation_concepts.md).

### Prototype-based anonymization

If you need to assure multi-field-dependencies in anonymization or just overwrite data with real-looking substitutes,  
you can choose a prototype-based approach: import data from one source and merge it with
prototypes that are generated or imported from another source. Benerator comes with many predefined
prototype generators for different domains, and you can easily set up custom prototype-based anonymization approaches.

When importing data for functional and performance testing, you may need to add
a [Data Postprocessing Stage](data_generation_concepts.md#data-postprocessing-stage).

In the following example, customers are imported from a database table in a production database (prod_db),
anonymized and exported to a test database (test_db). All attributes that are not overwritten, will be exported as is.
Since customer names and birthdates need to be anonymized, a prototype generator
([PersonGenerator](domains.md#persongenerator)) is used to generate prototypes (named person)
whose attributes are used to overwrite production customer attributes:

```xml
<iterate source="prod_db" type="db_customer" consumer="test_db">
    <variable name="person" generator="com.rapiddweller.domain.person.PersonGenerator"/>
    <attribute name="salutation" script="person.salutation" />
    <attribute name="first_name" script="person.givenName" />
    <attribute name="last_name" script="person.familyName" />
    <attribute name="birth_date" nullable="false" />
</iterate>
```

![](assets/grafik14.png)

### Data Masking

The following ones are Converters useful for data masking, making the manipulation obvious:

| Classes | Description | Example |
| --- | --- | --- |
| **Mask** | Replaces each character of a string with an asterisk '*' or another configurable character. | ***** |
| **MiddleMask** | Replaces each character of a string with an asterisk '*' or another configurable character, leaving a configurable number of characters unmasked at the beginning and/or the end of the string. | 38***********329 |
| **MD5Hash**, **SHA1Hash**, **SHA256Hash** | Convert any data to a hexadecimal hash code | D41D8CD98F00B204E9800998ECF8427E |
| **MD5HashBase64**, **SHA1HashBase64**, **SHA256HashBase64** | Convert any data to a hash code in Base64 format | 1B2M2Y8AsgTpgAmY7PhCfg== |
| **JavaHash**| Convert any data to a hexadecimal hash code. This implementation is faster than the hash converters above | 0027b8b2 |

The following example shows you a mix of prototype usage and masking:

Imagine having a set of personal data, like this:

| given Name | family Name | alias | street | house Number | postal Code | city | accountNo | ssn | creditCardNo | secret1 | secret2 | secret3 | secret4 | secret5
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | ---
| Hannah    | Hopkins | hahop | Main Street | 3456 | 9876 | Central City | 1234567890 | 123456789 | 1234567890123456 | secret1 | secret2 | secret3 | secret4 | secret5
| Bob       | Baker | bobo | Orchard Lane | 12 | 65395 | Countryside | 2345678901 | 234567890 | 2345678901234567 | secret1 | secret2 | secret3 | secret4 | secret5
| Alice     | Anderson | alan | Shiny Street | 123 | 93748 | Gloryville | 3456789012 | 345678901 | 3456789012345678 | secret1 | secret2 | secret3 | secret4 | secret5



Applying the Benerator setup

```xml
<?xml version="1.0" encoding="UTF-8"?>
<setup xmlns="https://www.benerator.de/schema/2.1.0" defaultDataset="US">
    
    <import domains="address"/>

    <iterate source="persons.csv" type="data" consumer="ConsoleExporter">
        <variable name="addr" generator="AddressGenerator"/>
        <attribute name="familyName" converter="new CutLength(3)"/>
        <attribute name="givenName" converter="Mask"/>
        <attribute name="alias" converter="new Append('_demo')"/>
        <attribute name="street" script="addr.street"/>
        <attribute name="houseNumber" script="addr.houseNumber"/>
        <attribute name="postalCode" script="addr.postalCode"/>
        <attribute name="city" script="addr.city"/>
        <attribute name="country" constant="US"/>
        <attribute name="accountNo" converter="SHA256Hash"/>
        <attribute name="ssn" converter="new MiddleMask(2,3)"/>
        <attribute name="creditCardNo" converter="SHA1HashBase64"/>
        <attribute name="secret1" converter="MD5Hash"/>
        <attribute name="secret2" converter="MD5HashBase64"/>
        <attribute name="secret3" converter="SHA1Hash"/>
        <attribute name="secret4" converter="SHA1HashBase64"/>
        <attribute name="secret5" converter="JavaHash"/>
    </iterate>

</setup>
```

Yields the following result:

| given Name | family Name | alias | street | house Number | postal Code | city | country |accountNo | ssn | creditCardNo | secret1 | secret2 | secret3 | secret4 | secret5 |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| ****** | Hop | hahop_demo | Williams Street | 24 | 11940 | EAST MORICHES  | US | C775E7B757EDE630CD0AA1113BD10266 1AB38829CA52A6422AB782862F268646 | 12****789 | 3u0qiOc9zKowqeb ilvYr4ji+St4= | E52D98C459819A11 775936D8DFBB7929 | 5Uz7NxT3bO3 UsniJ4fahdA== | 418EE516F1CB095C50FF 2F10A76192889C281F3A | OYLHmdHE864xkK 1OCSSFhcZRqUY= | 756e8785 |
| ***    | Bak | bobo_demo  | Ridge Street    | 42 | 57373 | SAINT LAWRENCE | US | 4191597AA1B3449DEE4F86976B855E03 7C3AA38B72FCE597A3651FA9036962A2 | 23****890 | ZS88gHOnttsthvq qf56nTVcmEjM= | E52D98C459819A11 775936D8DFBB7929 | 5Uz7NxT3bO3 UsniJ4fahdA== | 418EE516F1CB095C50FF 2F10A76192889C281F3A | OYLHmdHE864xkK 1OCSSFhcZRqUY= | 756e8785 |
| *****  | And | alan_demo  | Lincoln Street  | 47 | 88006 | LAS CRUCES     | US | 26CC49F1A2133F3784B937017F9CC86E 05B5413C7F91B0B6BD6375631B68371E | 34****901 | iiAREgvD0AkATPQ Qu7SmZYD1p0s= | E52D98C459819A11 775936D8DFBB7929 | 5Uz7NxT3bO3 UsniJ4fahdA== | 418EE516F1CB095C50FF 2F10A76192889C281F3A | OYLHmdHE864xkK 1OCSSFhcZRqUY= | 756e8785 |


## Import filtering

You can choose a subset of the available data by using 
- features of the source system like a select statement for a ```<database>``` or ```memstore```
- a 'filter' expression for any kind of data source (Enterprise Edition only).

### &lt;Iterate filter&gt; Option (Enterprise Edition)

In order to filter data import from any source, use a filter expression. 
Within it, you can use the variable _candidate to access the data entity 
that is a candidate to import and return true in order to accept it or 
false in order to drop it.

An example for iterating through orders only of high priority:

```xml
<iterate source="orders.xls" type="order" filter="_candidate.priority == 'high'" consumer="ConsoleExporter"/>
```

## Anonymization Report (Enterprise Edition)

For compliance checking, Enterprise Edition provides an anonymization Tracker.
It is activated on the command line by calling 

```benerator --anonreport```

and creates a report listing each field name and which percentage of its occurrences 
were changed in anonymization. The report is exported to a tab-separated CSV file named 
```anonymization-report.csv``` and to the console:

```
+-------------------------------------------------------------+
| Anonymization Report:                                       |
| Date/Time  2021-09-16 21:59:58 MESZ                         |
| Anonymized 500000 entities                                  |
| Checked    500000 entities (100%)                           |
+-------------------------------------+-----------+-----------+
| Field                               | # Checked | % Changed |
+-------------------------------------+-----------+-----------+
| order.street                        |   500,000 |    100.0% |
+-------------------------------------+-----------+-----------+
| order.city                          |   500,000 |     99.3% |
+-------------------------------------+-----------+-----------+
| order.cardno                        |   500,000 |       all |
+-------------------------------------+-----------+-----------+
| order.date                          |   500,000 |      none |
+-------------------------------------+-----------+-----------+
| order.express                       |   500,000 |      none |
+-------------------------------------+-----------+-----------+
| order.gift                          |   500,000 |      none |
+-------------------------------------+-----------+-----------+
```

When anonymizing large amounts of complex data structures, you will notice that this 
tracking has a substantial performance impact, which may slow down performance 
by a factor of 10 or more. There are two combinable approaches to address this:

1. reducing the check to the fields which are relevant for privacy
2. Partial anonymization checking


### Reducing the check to relevant fields

Anonymization is not done for fun but for privacy protection, so it is legitimate to 
reduce the anonymization check to the fields which are relevant for this purpose. 
In most applications, sensitive data is only a small part of all data, and 
anonymization checking overhead is strongly reduced.

This restriction is defined by listing the fields to be checked in an 
```<anon-check>``` element:

```xml
<anon-check>street, city, cardno</anon-check>
```

You will then notice faster execution and a shorter report 
(restricted to the fields above):

```
+-------------------------------------------------------------+
| Anonymization Report:                                       |
| Date/Time  2021-09-16 21:59:58 MESZ                         |
| Anonymized 500,000 entities                                 |
| Checked    500,000 entities (100%)                          |
+-------------------------------------+-----------+-----------+
| Field                               | # Checked | % Changed |
+-------------------------------------+-----------+-----------+
| order.street                        |   500,000 |    100.0% |
+-------------------------------------+-----------+-----------+
| order.city                          |   500,000 |     99.3% |
+-------------------------------------+-----------+-----------+
| order.cardno                        |   500,000 |       all |
+-------------------------------------+-----------+-----------+
```


### Anonymization Sample Checking

If the reduction of fields to check is not fast enough (or undesired), 
you can additionally (or alternatively) reduce the checks to random samples of the 
anonymized data. In order to do so, you specify the percentage of data samples 
to be checked as command line argument on the Benerator call, for example a 
sample size of 10% of all data would be checked when specifying

```benerator --anonreport 10```

Then your report may look like this (when combined with field reduction):

```
+-------------------------------------------------------------+
| Anonymization Report:                                       |
| Date/Time  2021-09-16 21:59:58 MESZ                         |
| Anonymized 500,000 entities                                 |
| Checked    50,075 entities (10%)                            |
+-------------------------------------+-----------+-----------+
| Field                               | # Checked | % Changed |
+-------------------------------------+-----------+-----------+
| order.street                        |    50,075 |    100.0% |
+-------------------------------------+-----------+-----------+
| order.city                          |    50,075 |     99.3% |
+-------------------------------------+-----------+-----------+
| order.cardno                        |    50,075 |       all |
+-------------------------------------+-----------+-----------+
```

### Comparing Anonymization Tracking Performance

Which approach performs better is heavily dependent on the properties of your individual data structures.

!!! tip

    A rule of thumb: For projects with large data structures, the fields restriction approach performs better,
    for projects with smaller data structures the sampling approach.

A non-representative example project with large data objects of which only a small number of fields 
needed to be anonymized, exhibited the following performance:

| Method                             | Performance |
| ---                                |        ---: |
| No anonymization tracking          |    120 ME/h |
| Full anonymization tracking        |     10 ME/h |
| 10% samples tracking               |     56 ME/h |
| Restricted fields tracking         |     65 ME/h |
| Restricted fields and 10% samples  |     75 ME/h |

!!! note

    ME/h stands for a million entities per hour.


## 'condition'

When anonymizing or importing data, you may need to match multi-field constraints of the form 
__"if field A is set then field B must be set and field C must be null“__. In many cases, 
an easy solution is to import data, mutate only non-null fields and leave null-valued fields 
as they are. 

A shorter syntax element to do so is the `condition` attribute. 

It contains a condition and when added to a component generator, the generator is only
applied if the condition resolves to true:

```xml
<iterate source="db1" type="customer" consumer="">
    <attribute name="vat_no" condition="this.vat_no != null" pattern="DE[1-9][0-9]{8}" unique="true" />
</iterate>
```

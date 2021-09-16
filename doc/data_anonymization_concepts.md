# Data Anonymization Concepts

## The Basic Idea

The approach for anonymizing production data is to ```<iterate>``` over existing data and
overwrite data fields with privacy concerns making use of all the features you learned above  
in [Data Generation Concepts]. 

If you need to assure multi-field-dependencies when overwriting, 
you can choose a prototype-base approach: import data from one source and merge it with
prototypes that are generated or imported from another source.

When importing data for functional and performance testing, you may need to add a 
[Data Postprocessing Stage]. 

In the following example, customers are imported from a database table in a production database (prod_db), anonymized and exported to a test
database (test_db). All attributes that are not overwritten, will be exported as is. Since customer names and birth dates need to be anonymized, a
prototype generator (...PersonGenerator) is used to generate prototypes (named person) whose attributes are used to overwrite production customer
attributes:

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

## Anonimization Conditions

When anoymizing or importing data, you may need to match multi-field-constraints of the form 
"if field A is set then field B must be set and field C must be null“. It many cases, 
an easy solution is to import data, mutate only non-null fields and leave null-valued fields 
as they are. 

A shorter syntax element to do so is the ```condition``` attribute. 
It contains a condition and when added to a component generator, the generator is only
applied if the condition resolves to true:

```xml
<iterate source="db1" type="customer" consumer="">
    <attribute name="vat_no" condition="this.vat_no != null" pattern="DE[1-9][0-9]{8}" unique="true" />
</iterate>
```

## Data Converters

Converters are useful for supporting using custom data types (e.g. a three-part phone number) and common conversions (
e.g. formatting a date as string). Converters can be applied to entities as well as attributes by specifying a converter attribute:

```xml
<generate type="TRANSACTION" consumer="db">
  <id name="ID" type="long" strategy="increment" param="1000" />
  <attribute name="PRODUCT" source="{TRANSACTION.PRODUCT}" converter="CaseConverter"/>
</generate>
```

For specifying Converters, you can

- use the class name
- refer a JavaBean in the Benerator context
- provide a comma-separated Converter list in the two types above

Benerator supports two types of converters:

1. Classes that implement Benerator's service provider interface (SPI) com.rapiddweller.common.Converter
2. Classes that extend the class java.text.Format

If the class has a 'pattern' property, Benerator maps a descriptor's pattern attribute to the bean instance property.

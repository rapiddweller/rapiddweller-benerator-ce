For JSON Generation, there are 3 main type you will encounter:
1. Key: String value
2. Key: {...} Object
3. Key: [{.A.},{.B.},{.C.}] Array

This is some base example script to reproduce each type, you can customize more with other component.
<h3>1. Key: String value</h3>

Use attribute tag with key=name-attribute and value=[constant/generator/script]-attribute. Constant shall give you exact value you assign to that attribute. You can use Generarators to create values by using attribute:generator/script. Use generator will create value that have no relation to other attribute-tags. Script using with Variable Tag will make context for some other attributes and generated data look like real. The limitation is that we can not declare variable in part-tag to make context in some array below. This can be created by predefined data using "entity.ent.csv" or "weight.wgt.csv".

For example:

Script
```
<?xml version="1.0" encoding="UTF-8"?>
<setup>
    <!--Import domains and platforms-->
    <import domains="person, organization, address" platforms="json"/>

    <comment>Generating Store</comment>
    <generate type="store" count="1"
              consumer="new JsonFileExporter('src/demo/resources/demo/json/demoTest/result.json'),ConsoleExporter">
        <!--Declare variable use for attribute-->
        <variable name="address"
                  generator="new AddressGenerator{dataset='US'}"/>
        <!--Start JSON-->
        <!--Value as constant-->
        <attribute name="name" type="string" constant="ElectronicMart"/>
        <!--next 3 attributes have same context from address-variable declared above-->
        <attribute name="address" type="string" script="{address.houseNumber+'-'+address.street+'-'+address.state.name}"/>
        <attribute name="city" type="string" script="address.city.name"/>
        <attribute name="country" type="string" script="address.country.name"/>
        <!--Separate data from generator, no connect to ther attributes context-->
        <attribute name="representative" generator="new PersonGenerator{dataset='US',locale='us',minAgeYears='18', maxAgeYears='75',femaleQuota='0.5'}" />
    </generate>
</setup>
```
make JSON
```
[
  {
    "name": "ElectronicMart",
    "address": "37-Lake Street-Kentucky",
    "city": "LORETTO",
    "country": "United States",
    "representative": "Mr. Larry Gallagher"
  }
]
```
Note: generate name-attribute shall not appear as a initial key ({store: {...}}). To make initial key, you should use part-tag with name=initial-key as example below.

<h3>2. Key: {...} Object</h3>
You can use part-tag to create nested object (don't use container-attribute cause it make array). Inside part-tag, you can use other nested part-tag to make array or object.

For example:

script:
```
<?xml version="1.0" encoding="UTF-8"?>
<setup>
    <import domains="person, organization, address" platforms="json"/>

    <comment>Generating People</comment>
    <generate type="store" count="1"
              consumer="new JsonFileExporter('src/demo/resources/demo/json/demoTest/result.json'),ConsoleExporter">
        <!--Declare variable use for attribute-->
        <variable name="addressGen"
                  generator="new AddressGenerator{dataset='US'}"/>
        <!--Start JSON-->
        <part name="store">
            <attribute name="name" type="string" constant="ElectronicMart"/>
            <part name="address">
                <attribute name="address" type="string" script="{addressGen.houseNumber+'-'+addressGen.street+'-'+addressGen.state.name}"/>
                <attribute name="city" type="string" script="addressGen.city.name"/>
                <attribute name="country" type="string" script="addressGen.country.name"/>
            </part>
            <attribute name="representative" generator="new PersonGenerator{dataset='US',locale='us',minAgeYears='18', maxAgeYears='75',femaleQuota='0.5'}" />
        </part>
    </generate>
</setup>
```
make JSON
```
[
  {
    "store": {
      "name": "ElectronicMart",
      "address": {
        "address": "34-Meadow Street-Oregon",
        "city": "ARLINGTON",
        "country": "United States"
      },
      "representative": "Mr. Bill Powell"
    }
  }
]
```
Note1: you must declare name-attribute as key of the object in part-tag or it have error. This mean, we can not make object body alone (with no key, ex: {...}). And hence, can not make Array with different structure for each element inside it. Please check array section below.

Note2: please be care full with name-attribute of part-tag and variable-tag. Same name can cause mistake attribute value=null.

<h3>Key: [{.A.},{.B.},{.C.}] Array</h3>

- In case A/B/C have same structure, we use part with container-attribute=[array/list/set] (set-option is not working now) to make array of object/array (inside outer array). In this part-tag, we use minCount&maxCount-attribute to specify number of element in array. We only make the first element, after generation, all other element shall be the same as first element. You can not create variable inside a part. To customize this array, we can use source-attribute with "entity.ent.csv" or "weight.wgt.csv".

- If A/B/C are object with each element have different structure, I have not found the way to produce it now. In case, A/B/C are arrays with different structure(only nested array inside, no object body), we can use JS-script to put pre-defined data into this array.
For example:

script
```
<?xml version="1.0" encoding="UTF-8"?>
<setup>
    <import domains="person, organization, address" platforms="json"/>

    <comment>Generating People</comment>
    <generate type="store" count="1"
              consumer="new JsonFileExporter('src/demo/resources/demo/json/demoTest/result.json'),ConsoleExporter">
        <!--Declare variable use for attribute-->
        <variable name="addressGen"
                  generator="new AddressGenerator{dataset='US'}"/>
        <!--Start JSON-->
        <part name="store">
            <attribute name="name" type="string" constant="ElectronicMart"/>
            <!--container can make array, min/maxCount to set quantity-->
            <part name="address" container="list" minCount="2" maxCount="2">
                <attribute name="address" type="string"
                           script="{addressGen.houseNumber+'-'+addressGen.street+'-'+addressGen.state.name}"/>
                <attribute name="city" type="string" script="addressGen.city.name"/>
                <attribute name="country" type="string" script="addressGen.country.name"/>
            </part>
            <!--we can use JS script, but only work with nested arrays-->
            <attribute name="branch" type="object"
                       script="{js:[[['e1','e2','e3'],'e4'],['e5','e6','e7'],'e8','e9']}"/>
            <attribute name="representative"
                       generator="new PersonGenerator{dataset='US',locale='us',minAgeYears='18', maxAgeYears='75',femaleQuota='0.5'}"/>
            <!--use customer.ent.csv-->
            <part name="customer" source="customer.ent.csv" minCount="10" maxCount="10" distribution="random"/>
        </part>
    </generate>
</setup>
```
customer.ent.csv
```
name,age,member
Tim,21,yes
Mark,36,no
Lisa,15,yes
Ferris,30,yes
Rosie,58,no
```
make JSON
```
[
  {
    "store": {
      "name": "ElectronicMart",
      "address": [
        {
          "address": "40-2nd Street-Pennsylvania",
          "city": "BUNOLA",
          "country": "United States"
        },
        {
          "address": "40-2nd Street-Pennsylvania",
          "city": "BUNOLA",
          "country": "United States"
        }
      ],
      "branch": [
        [
          [
            "e1",
            "e2",
            "e3"
          ],
          "e4"
        ],
        [
          "e5",
          "e6",
          "e7"
        ],
        "e8",
        "e9"
      ],
      "representative": "Mr. William Alexander",
      "customer": [
        {
          "name": "Mark",
          "age": "36",
          "member": "no"
        },
        {
          "name": "Rosie",
          "age": "58",
          "member": "no"
        },
        {
          "name": "Mark",
          "age": "36",
          "member": "no"
        },
        {
          "name": "Lisa",
          "age": "15",
          "member": "yes"
        },
        {
          "name": "Rosie",
          "age": "58",
          "member": "no"
        },
        {
          "name": "Rosie",
          "age": "58",
          "member": "no"
        },
        {
          "name": "Lisa",
          "age": "15",
          "member": "yes"
        },
        {
          "name": "Ferris",
          "age": "30",
          "member": "yes"
        },
        {
          "name": "Mark",
          "age": "36",
          "member": "no"
        },
        {
          "name": "Mark",
          "age": "36",
          "member": "no"
        }
      ]
    }
  }
]

```

You can check my example to see more nested structure.
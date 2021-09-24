# Generating XML Files

Benerator offers several options to generate XML files.

They come in two different flavors: 

1. Explicit XML construction: You manually compose and configure XML generation
2. Implicit (schema-based) XML definition: You provide an XML Schema file and Benerator figures out the rest

## Explicit XML construction

With explicit XML construction, you manually define data generation and nesting. 
An XML consumer then simply outputs them as XML file.

Two such XML consumers are provided with Benerator:

- DbUnitEntityExporter
- XMLEntityExporter

Each one of both uses a fix defined format to render data as XML. 
If both formats do not meet your needs, you can apply an XML 
transformation afterwards, for example invoked with an `execute` directive:

```
<execute type="shell">tx-xml transactions.dbunit.xml tx-trans.xslt tx.xml</execute>
```

### DbUnitEntityExporter
A consumer writes any created entity to a file in DbUnit XML format. 
Use this if you only need a DbUnit file or want a simple,
flat XML-based export for import in other tools. 
See the '[Component Reference](component_reference.md)' for more information.

```xml
<setup>
    <generate type="order" count="7" consumer="new DbUnitEntityExporter('tx.dbunit.xml')">
      <id name="id" tyoe="int"/>
      <attribute name="items" type="short" min="1" max="27" distribution="cumulated"/>
      <attribute name="price" type="double" min="0.ß1" max="100.00" increment="0.01"/>
      <attribute name="comment" pattern="[A-Z][a-z]{5,12}"/>
      <attribute name="ean_code" pattern="[0-9]{13}"/>
    </generate>
</setup>
```

Running this setup creates an XML file like this:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<dataset>
	<order id="1" items="11" price="78.17" comment="Drwhcqpfbga" ean_code="9919144617610"/>
	<order id="2" items="12" price="58.36" comment="Loxzhlvgs" ean_code="6603356278824"/>
	<order id="3" items="15" price="19.41" comment="Lpphxx" ean_code="6843307402222"/>
	<order id="4" items="18" price="62.45" comment="Gfotkrhegmbbx" ean_code="8264224421213"/>
	<order id="5" items="10" price="12.08" comment="Sdhvne" ean_code="5261265548872"/>
	<order id="6" items="18" price="27.81" comment="Svtrtrhlwmdu" ean_code="3677815958330"/>
	<order id="7" items="17" price="75.28" comment="Helqgd" ean_code="5931484187271"/>
</dataset>
```

Note that the DbUnit file format is limited to flat data structures.
For nested XML elements use the following consumer:

### XMLEntityExporter
A consumer which is more powerful than the DbUnitEntityExporter: 
It renders each simple-type entity attribute as an XML attribute 
and each sub-entity as a nested XML element.
See the '[Component Reference](component_reference.md)' for more information.

The example shows how to nest generation configuration:

```xml
<setup>
    <import platforms="xml"/>
    <bean id="xml" spec="new XMLEntityExporter('tx.xml')"/>
    <generate type="orders" count="1" consumer="xml">
        <generate type="order" count="7" consumer="xml">
            <id name="id" type="int"/>
            <attribute name="items" type="short" min="1" max="27" distribution="cumulated"/>
            <attribute name="price" type="double" min="0.01" max="100.00" granularity="0.01"/>
            <attribute name="comment" pattern="[A-Z][a-z]{5,12}"/>
            <attribute name="ean_code" pattern="[0-9]{13}"/>
        </generate>
    </generate>
</setup>
```

for the following result:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<orders>
    <order id="1" items="11" price="77.99000000000001" comment="Rvjloach" ean_code="9780700577140"/>
    <order id="2" items="22" price="62.13" comment="Ezviominvbvol" ean_code="2224896066588"/>
    <order id="3" items="19" price="84.25" comment="Zkrryifmoo" ean_code="1193496860138"/>
    <order id="4" items="12" price="72.53" comment="Ufecgkxxqyen" ean_code="3292695014212"/>
    <order id="5" items="8" price="68.67" comment="Mrxisnwbdyoe" ean_code="8835687800915"/>
    <order id="6" items="7" price="20.610000000000003" comment="Rdrutwfkpuly" ean_code="4455177396063"/>
    <order id="7" items="12" price="24.630000000000003" comment="Puecbk" ean_code="8930345219912"/>
</orders>
```

### Using data types from XML schema files

Including an XML schema in a classic descriptor file makes its data types available for explicit data generation.
This can be used with the DbUnitExporter as well for the XmlExporter:

```xml
<include uri="shop.xsd" />

<generate type="product" count="5" consumer="ConsoleExporter"/>
```

## Implicit (schema-based) XML definition

This is the most powerful XML generation approach:
An XML schema file is used as the central definition for data generation. 
Benerator is able to automatically generate data purely based on a plain schema file, 
but, of course, you will want more detailed control over data generation. 
You get this by inserting generation descriptors in the well-known XML descriptor syntax. 
This makes data generation almost as versatile as with the classic descriptor-file-based approach.
More than that, you can generate an arbitrary number of XML files with one program call.

Since XML schema offers a very wide range of options, Benerator concentrates on features 
usually needed for data files but does not yet support the complete schema feature set.
If you are interested in improvements, please email us at [team@rapiddweller.com](mailto:team@rapiddweller.com).

### Introduction

For the first trials, use a simple XML Schema file. We are beginning without annotations and save the following XML Schema with the name
`transactions.xsd`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" 
           xmlns:ben="https://www.benerator.de/schema/benerator-1.2.0.xsd"
           xmlns="https://www.benerator.de/shop-0.8.1.xsd"
           targetNamespace="https://www.benerator.de/shop-0.8.1.xsd"
           elementFormDefault="qualified">`

    <xs:element name="transactions">
        <xs:complexType>
            <xs:sequence>
                <xs:element ref="transaction" minOccurs="0" maxOccurs="unbounded" />
            </xs:sequence>
        </xs:complexType>
    </xs:element>

    <xs:element name="transaction">
        <xs:complexType>
            <xs:attribute name="id" type="xs:long" use="required" />
            <xs:attribute name="comment" type="xs:string" use="required" />
            <xs:attribute name="ean_code" type="xs:string" use="required" />
            <xs:attribute name="price" type="price-type" use="required" />
            <xs:attribute name="items" type="xs:integer" />
        </xs:complexType>
    </xs:element>

    <xs:simpleType name="price-type">
        <xs:restriction base="xs:decimal">
            <xs:minInclusive value="0"/>
            <xs:totalDigits value="8" />
            <xs:fractionDigits value="2" />
        </xs:restriction>
    </xs:simpleType>

</xs:schema>
```

This defines an XML file format which has a `<transactions>` attribute as root element which contains an arbitrary number of `<transaction>` elements.
Each `<transaction>` has the attributes 'id', 'comment', ean_code', 'price' and '
items'. The 'price-type' specifies decimal values with a total of 8 digits, 2 of which are decimal digits.

You can invoke XML file generation using Benerator directly or using the Maven Benerator Plugin. Let's call Benerator directly from the shell for now.
Open a console, go to the directory which contains your schema file and invoke (under Windows):

```shell
createXML transactions.xsd transactions tx-{0}.xml 2
```

On Unix systems type:

```
createXML transactions.xsd transactions tx-{0}.xml 2
```

This tells Benerator to generate 2 xml files named `tx-1.xml` and `tx-2.xml` based on the schema file `transactions.xsd` using the `transactions`
element as root element.

Open one of the generated files, and you will see the following content:

```xml
<?xml version="1.0" encoding="UTF-8"?>

<transactions elementFormDefault="unqualified"/>
```

So, what did Benerator do wrong? Nothing, it is a perfectly schema-valid document. Since minOccurs of the transaction ref is zero, Benerator takes the
easy choice: Remember: One of Benerator's strengths is to configure the generation of valid data as easily as possible and in project stages as early as
possible. With the chosen approach, you need to spend less time explicitly configuring element removal which is not yet supported by your
application.

For configuring the generation of `<transaction>` elements, you need to add an annotation to your schema. The `ref` configuration in the `sequence` is
the right place to configure cardinalities of included sub elements:

```xml
<xs:element name="transactions">
    <xs:complexType>
        <xs:sequence maxOccurs="unbounded">
            <xs:element _ref="transaction">
                <xs:annotation>
                    <xs:appinfo>
                        <ben:part minCount="5" maxCount="10"/>
                    </xs:appinfo>
                </xs:annotation>
            </xs:element>
        </xs:sequence>
    </xs:complexType>
</xs:element>
```

Run Benerator again, and you will notice, that Benerator generated files like the following, each containing 5 to 10 transactions:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<transactions elementFormDefault="unqualified">
    <transaction id="4" comment="OBNBHQWMJYSPAHOCNBGDPGUXUQK" ean_code="KJCDPUJNK" price="1" items="6"/>
    <transaction id="6" comment="UIXSXLGFBIRP" ean_code="MW" price="3" items="7"/>
    <transaction id="4" comment="CRWGBGEKLRTZQADE" ean_code="MXIESHSXQVLFJIBC" price="5" items="5"/>
    <transaction id="9" comment="FVBABHSYXZJHQYCVCWJ" ean_code="FRPHJBOKUWHYKWHCWEIJBHVHIMV" price="1" items="9"/>
    <transaction id="9" comment="FZBNSLBEBZMTGPZJUG" ean_code="MNYYPRKM" price="7" items="5"/>
    <transaction id="7" comment="KIWPOOHNV" ean_code="CRXMHAGAC" price="3" items="7"/>
    <transaction id="9" comment="JETNYCMECHGUPSUKLKSEA" ean_code="ICY" price="1" items="5"/>
</transactions>
```

Now we have `<transactions>`, but their attribute values are not necessarily meaningful for our application. We need to configure attribute
generation, too. Note however, that Benerator understands the definition of custom data types like the 'price-type' and automatically generates valid
data, though taking the easy way of defaulting to integral numbers.

### Configuring Attribute Generation

Now it is time to configure the attribute details. Let us start by declaring the `id` attribute as ID:

```xml
<xs:attribute name="id" type="xs:long" use="required">
    <xs:annotation>
        <xs:appinfo>
            <ben:id/>
        </xs:appinfo>
    </xs:annotation>
</xs:attribute>
```

BTW: The XML Schema type ID is not yet handled automatically. You need to add an explicit `<ben:id/>` annotation for generating unique identifiers of
the desired type.

Shorter random comments are generated based on a regular expression:

```xml
<xs:attribute name="comment" type="xs:string" use="required">
    <xs:annotation>
        <xs:appinfo>
            <ben:attribute pattern="[A-Z][a-z]{5,12}"/>
        </xs:appinfo>
    </xs:annotation>
</xs:attribute>
```

You can configure number generation for the `items` attribute by setting `min`, `max`, `resolution` and `distribution` values:

```xml
<xs:attribute name="items">
    <xs:annotation>
        <xs:appinfo>
            <ben:attribute type="short" min="1" max="27" distribution="cumulated"/>
        </xs:appinfo>
    </xs:annotation>
</xs:attribute>
```

This makes benerator create `items` numbers from 1 to 27 with a cumulated distribution which has its maximum at 14.


### Using `<variables>` in XML Schema

Now for more complex data generation: You can use `<variables>` like in descriptor files. They need to be placed inside an `<element>`. Let us, for
example, use a CSV file with product definitions, containing EAN and price for each article. First, the variable declaration:

```xml
<xs:element name="transaction">
    <xs:annotation>
        <xs:appinfo>
            <variable name="product" source="products.ent.csv" distribution="random"/>
        </xs:appinfo>
</xs:annotation>
...
```

For each generation of a transaction, the `<variable>` is called to generate a new helper object, in this case providing a CSV data line with product
data. The contents of this data are mapped using script expressions:

```xml
...

<xs:attribute name="ean_code" type="xs:string" use="required">
    <xs:annotation>
        <xs:appinfo>
            <ben:attribute script="product.ean_code"/>
        </xs:appinfo>
    </xs:annotation>
</xs:attribute>

<xs:attribute name="price" type="price-type" use="required">
    <xs:annotation>
        <xs:appinfo>
            <ben:attribute script="product.price"/>
        </xs:appinfo>
    </xs:annotation>
</xs:attribute>
```

Using a CSV file with product definitions

```
ean_code,name,category_id,price,manufacturer
8000353006386,Limoncello Liqueur,DRNK/ALCO,9.85,Luxardo
3068320018430,Evian 1.0 l,DRNK/SOFT,1.95,Danone
8076800000085,le Lasagnette,FOOD/MISC,0.89,Barilla
7610400071680,Connaisseurs,FOOD/CONF,16.95,Lindt
```

we finally get a satisfactory result:

```xml
<?xml version="1.0" encoding="UTF-8"?>
    <transactions elementFormDefault="unqualified">
    <transaction id="1" comment="Sczexyozcpc" ean_code="8076800000085" price="0.89" items="9"/>
    <transaction id="2" comment="Nglija" ean_code="8000353006386" price="9.85" items="11"/>
    <transaction id="3" comment="Miejztqhgaoc" ean_code="7610400071680" price="16.95" items="14"/>
    <transaction id="4" comment="Tzoxzrupygjfi" ean_code="8000353006386" price="9.85" items="11"/>
    <transaction id="5" comment="Sufqdrku" ean_code="3068320018430" price="1.95" items="17"/>
    <transaction id="6" comment="Jbtqsft" ean_code="8000353006386" price="9.85" items="14"/>
    <transaction id="7" comment="Lvivruwwxmay" ean_code="8076800000085" price="0.89" items="10"/>
</transactions>
```

You might as well want to calculate the total price. You can easily do so using a script expression, e.g. `script="
this.price * this.items"`. Note that the elements are evaluated and generated in the order in which they are declared, so the `total sum` field must be
defined after the used terms `price` and `items`.


### Importing Properties File Data

You can import settings from properties files by placing `<include>`s in the schema's root node annotation:

```xml
<xs:schema ...>

    <xs:annotation>
        <xs:appinfo>
            <ben:include uri="benerator.properties"/>
        </xs:appinfo>
    </xs:annotation>
    ...
```


### Current Limitations
The current XML schema limitations are:

- No support for recursion of the same element type, e.g. categories containing other categories
- No support for mixed content. Benerator is concerned with the generation of data structures, while mixed-type documents generally apply to natural-language documents.
- groups are not supported
- sequences may not have `maxOccurs > 1`
- namespace support is only rudimentary, problems may arise on different types with equal names
- schema `include` is not supported yet
- ids and idrefs are not resolved automatically


## Conclusion

Almost the full feature set of Benerator descriptor files is available for XML Schema-based file generation. 
If you know the Benerator descriptor file syntax well, it is a straightforward and relatively simple process 
to annotate XML Schema files. 

However, if you just need to export XML-formatted data and write your own XML parser for importing the data 
somewhere else, you might prefer to use the DbUnitEntityExporter (flat structure) or 
XMLEntityExporter (hierarchical structure), possibly in combination with an XSL transformation.

You can as well combine the approaches: Annotating the XML Schema with Benerator descriptors and using its 
internal type definition in a Benerator setup with an XMLEntityExporter.
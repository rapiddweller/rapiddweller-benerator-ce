# Generating XML Files 

Benerator offers different options to generate XML files:

*   DbUnitEntityExporter: A consumer writes any created entity to a file in DbUnit XML format. Use this if you only need a DbUnit file or want a simple, flat XML-based export for import in other tools. See the

    component reference

    for more information.

*   XMLEntityExporter: A consumer which is not much more powerful than the DbUnitEntityExporter: It renders each simple-type entity attribute as an XML attribute and each sub entity as nested XML element. See the

    component reference

    for more information.

*   Schema-based generation: An approach that uses an XML Schema file to automatically generate an arbitrary number of XML files. The schema files may be annotated with a similar syntax like used in Benerator descriptor files. This is the most powerful XML generation option:

## Schema-based XML file generation 

In this approach, an XML schema is used as the central descriptor file. Benerator is able to generate from a plain schema file automatically, but inserting XML schema annotations, you can configure test data generation almost as versatile as with the classic descriptor-file-based approach.

XML schema support is not yet fully implemented. The limitations are:

*   No support for recursion of the same element type, e.g. categories containing other categories

*   No support for mixed content. benerator is concerned with generation of data structures, while mixed-type documents generally apply for natural-language documents.

*   groups are not supported

*   sequences may not have maxOccurs >` 1

*   namespace support is only rudimentary, problems may arise on different types with equal names

*   schema include is not supported yet

*   ids and idrefs are not resolved automatically

If Benerator is your tool of choice and you need a feature urgently, please contact Volker Bergmann by E-Mail or forum.

### Introduction 

For the first trials, use a simple XML Schema file. We are beginning without annotations and save the following XML Schema with the name transactions.xsd:

```xml
<?xml version="1.0" encoding="UTF-8"?>

`<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema"

xmlns:ben="http://databene.org/benerator-0.8.1.xsd"

xmlns="http://databene.org/shop-0.8.1.xsd"

targetNamespace="http://databene.org/shop-0.8.1.xsd"

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

This defines an XML file format which has a `<transactions>` attribute as root element which contains an arbitrary number of `<transaction>` elements. Each `<transaction>` has the attributes 'id', 'comment', ean_code', 'price' and 'items'. The 'price-type' specifies decimal values with a total of 8 digits, 2 of which are decimal digits.

You can invoke XML file generation using Benerator directly or using the Maven Benerator Plugin. Let's call Benerator directly from the shell for now. Open a console, go to the directory which contains your schema file and invoke (under Windows):

```shell
createXML transactions.xsd transactions tx-{0}.xml 2
```

On Unix systems type:

```
createXML transactions.xsd transactions tx-{0}.xml 2
```

This tells Benerator to generate 2 xml files named 'tx-1.xml' and 'tx-2.xml' based on the schema file 'transactions.xsd' using the 'transactions' element as root element.

Open one of the generated files and you will see the following content:

```xml
<?xml version="1.0" encoding="UTF-8"?>

<transactions elementFormDefault="unqualified"/>
```

So, what did Benerator do wrong? Nothing, it is a perfectly schema-valid document. Since minOccurs of the transaction ref is zero, Benerator takes the easy choice: Remember: One of Benerator's strengths is to configure generation of valid data as easy as possible and in project stages as early as possible. With the chosen approach, you need to spend less time for explicitly configuring element removal which are not yet supported by your application.

For configuring the generation of `<transaction>` elements, you need to add an annotation to your schema. The 'ref' configuration in the 'sequence' is the right place to configure cardinalities of included sub elements:

```xml
<xs:element name="transactions">

<xs:complexType>

<xs:sequence maxOccurs="unbounded">

<xs:element _ref="transaction"_>

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

Run Benerator again and you will notice, that Benerator generated files like this one, each containing 5 to 10 transactions:

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

Now we have `<transactions>`, but their attribute values are not necessarily meaningful for our application. We need to configure attribute generation, too. Note however, that Benerator understands the definition of custom data types like the 'price-type' and automatically generates valid data, though taking the easy way of defaulting to integral numbers.

### Configuring Attribute Generation 

Now it is time to configure the attribute details. Let us start by declaring the 'id' attribute as ID

```xml
<xs:attribute name="id" type="xs:long" use="required">

<xs:annotation>

<xs:appinfo>

<ben:id/>

</xs:appinfo>

</xs:annotation>

</xs:attribute>
```

BTW: The XML Schema type ID is not yet handled automatically. You need to add an explicit `<ben:id/>` annotation for generating unique identifiers of the desired type.

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

You can configure number generation for the 'items' attribute by setting min, max, resolution and distribution values:

```xml
<xs:attribute name="items">

<xs:annotation>

<xs:appinfo>

<ben:attribute type="short" min="1" max="27" distribution="cumulated"/>

</xs:appinfo>

</xs:annotation>

</xs:attribute>
```

This makes benerator create 'items' numbers from 1 to 27 with a cumulated distribution which has its maximum at 14.

### Using `<variables>` in XML Schema 

Now for more complex data generation: You can use `<variables>` like in descriptor files. They need to be placed inside an `<element>`. Let us, for example, use a CSV file with product definitions, containing EAN and price for each article. First, the variable declaration:

```xml
<xs:element name="transaction">

<xs:annotation>

<xs:appinfo>

<variable name="product" source="products.ent.csv" distribution="random"/>

</xs:appinfo>

</xs:annotation>

...
```

For each generation of a transaction, the `<variable>` is called to generated a new helper object, in this case providing a CSV data line with product data. The contents of this data are mapped using script expressions:

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

Using a CSV file with product definitions:

ean_code,name,category_id,price,manufacturer

8000353006386,Limoncello Liqueur,DRNK/ALCO,9.85,Luxardo

3068320018430,Evian 1.0 l,DRNK/SOFT,1.95,Danone

8076800000085,le Lasagnette,FOOD/MISC,0.89,Barilla

7610400071680,Connaisseurs,FOOD/CONF,16.95,Lindt

we finally get a satisfactory result:

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

You might as well want to calculate the total price. You can easily do so using a script expression, e.g. script="this.price * this.items". Note that the elements are evaluated and generated in the order in which they are declared, so the 'total sum' field must be defined after the used terms 'price' and 'items'.

### Importing Properties File Data 

You can import settings from properties files by placing `<include>`s in the schema's root node annotation:

```xml
<xs:schema ...>

<xs:annotation>

<xs:appinfo>
<ben:include uri="benerator.properties"/>
</xs:appinfo>

</xs:annotation>
```



## Generating XML in classic descriptor files 

Generating data from an XML schema file is somewhat limited. Alternatively, you can use the classic Benerator descriptor files to generate entity data and write it to XML with a special consumer. If you do not need to adhere to a predefined XML schema, but simply want some XML for easy postprocessing, you might get what you need, if you use the XMLEntityExporter or the even simple DbUnitEntityExporter. Future Benerator versions will provide better options.

### Using data types from XML schema files 

Including an XML schema in a classic descriptor file makes its data types available for explicit data generation:

```xml
<include uri="shop.xsd" />

<generate type="product" count="5" consumer="ConsoleExporter"/>
```



## Conclusion 

Almost the full feature set of Benerator descriptor files is available for XML Schema-based file generation. If you know the Benerator descriptor file syntax, it is a straightforward and relatively simple process to annotate descriptor files. However, if you just need to export XML-formatted data and write an own XML parser for importing the data somewhere else, you might prefer to use the DbUnitEntityExporter (flat structure) or XMLEntityExporter (hierarchical structure), possibly in combination with an XSL transformation.
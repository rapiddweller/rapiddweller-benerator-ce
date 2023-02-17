# Domains

Benerator domains are a vehicle for defining, bundling and reusing domain specific data generation, e.g. for personal data, addresses, internet,
banking, telecom. They may be localized to specific languages and be grouped to hierarchical datasets, e.g. for continents, countries and regions.

Benerator comes packaged with several domains that have simple implementation of specific data generation and may serve as a base for deriving own
specific data generation domains:

* **person**: Data related to a person

* **address**: Data related to contacting a person by post

* **net**: Internet and network related data

* **finance**: finance data

* **organization**: Organization data

* **product**: Product-related data

## person domain

The person domain has three major components:

* PersonGenerator: Generates Person beans

* AcademicTitleGenerator: Generates academic titles

* NobilityTitleGenerator: Generates nobility titles

* GivenNameGenerator: Generates given names

* FamilyNameGenerator: Generates family names

* BirthDateGenerator: Generates birth dates

* GenderGenerator: Generates Gender values. 
  The generated gender can be one of the values `MALE`, `FEMALE` and `DIVERSE`.
  The generator is configured with the properties `femaleQuota`and `diverseQuota`.

* EmailAddressGenerator: Generates Email addresses

* GenderConverter: Converts Gender values to predefined strings

* TIN: Marks a Java attribute or property as a European Tax Identification Number

* TINGenerator: Creates European Tax Identification Numbers

* TINValidator: Validates European Tax Identification Numbers

![](assets/domain_person.png)

### PersonGenerator

Creates Person beans to be used for prototype-based data generation. It can be configured with dataset and locale property. The generated Person
JavaBeans exhibits the properties _salutation_, _title_ (both locale-dependent), _givenName_, _familyName_ (both dataset-dependent), _gender_, _birthDate_, _age_. If
the chosen dataset definition provides name weights, benerator generates person names according to their statistical probability. Of course, gender,
salutation and givenName are consistent.

You can use the PersonGenerator like this:

```xml
<import domains="person"/>

<generate type="user" count="5" consumer="ConsoleExporter">
    <variable name="person" generator="PersonGenerator" dataset="FR" locale="fr" />
    <attribute name="salutation" source="person.salutation" />
    <attribute name="name" script="{person.givenName +' '+ person.familyName}}" />
</generate>
```

to get output similar to this:

```
user[salutation=Mr, name=David Morel]
user[salutation=Mr, name=Robert Robert]
user[salutation=Mr, name=Eric Morel]
user[salutation=Mr, name=Patrick Lefebvre]
user[salutation=Mme, name=Helene Fournier]
```

### PersonGenerator Properties

The PersonGenerator can be configured with several properties:

| Property | Description | Default Value |
| --- | --- | --- |
| dataset | Either a region name or the two-letter-ISO-code of a country, e.g. US for the USA. See '[Advanced Topics > Region nesting](advanced_topics.md#region-nesting)' | The user's default country | 
| locale | Two-letter-ISO-code of the language in which to create salutation and titles, e.g. en for English | The user's default language | 
| minAgeYears | The minimum age of generated persons | 15 | 
| maxAgeYears | The maximum age of generated persons | 105 | 
| femaleQuota | The quota of generated women (1 → 100%) | 0.5 |
| nobleQuota | The quota of generated noble persons (1 → 100%) | 0.005 |

### Person Class

The Person class has the following properties:

| property name | type | property description |
| --- | --- | --- |
| salutation | String | Salutation (e.g. Mr/Mrs) |
| academicTitle | String | Academic title (e.g. Dr) |
| nobilityTitle | String | Nobility title (like Duke) |
| givenName | String | Given name ('first name' in western countries) |
| secondGivenName | String | An eventual second given name |
| familyName | String | Family name ('surname' in western countries) |
| gender | Gender | Gender (MALE or FEMALE) |
| birthDate | Date | Birth date |
| age | Integer | actual age |
| email | String | eMail address |
| locale | Locale | Language of the person instance (used e.g. for salutation) |

### Supported countries

| country | code | remarks |
| --- | --- | --- |
| Austria | AT | most common 120 given names with absolute weight, most common 40 family names with absolute weight |
| Australia | AU | most common 40 given names (unweighted), most common 20 family names with absolute weight |
| Brazil | BR | most common 100 given names (unweighted), most common 29 family names (unweighted) |
| Canada | CA | most common 80 given names (unweighted), most common 20 family names (unweighted). No coupling between given name locale and family name locale |
| Switzerland | CH | most common 30 given names with absolute weight, most common 20 family names with absolute weight |
| Czech Republic | CZ | most common 20 given names with absolute weight, most common 20 family names with absolute weight. Female surnames are supported. |
| Spain | ES | most common 40 given names (unweighted), most common 40 family names with absolute weight |
| Finland | FI | most common 785 given names (unweighted), most common 448 family names (unweighted) |
| France | FR | most common 100 given names (unweighted), most common 25 family names with relative weight |
| Germany | DE | most common 1998 given names with absolute weight, most common 3421 family names with absolute weight2 |
| Israel | IL | 264 given names (unweighted), most common 30 family names with relative weight |
| India | IN | most common 155 given names (unweighted), most common 50 family names (unweighted) |
| Italy | IT | most common 60 given names (unweighted), most common 20 family names (unweighted) |
| Japan | JP | Kanji letters. Most common 109 given names (unweighted), most common 50 family names with absolute weight |
| Netherlands | NL | 3228 given names (unweighted), most common 10 family names with absolute weight |
| Norway | NO | most common 300 given names (unweighted), most common 100 family names with absolute weight |
| Poland | PL | most common 67 given names with absolute weight, most common 20,000 family names with absolute weight. Female surnames are supported. |
| Russia | RU | Cyrillic letters. Most common 33 given names with relative weight, most common 20 family names with relative weight. Female surnames are supported. |
| Sweden | SE | 779 given names (unweighted), most common 22 family names with relative weight |
| Turkey | TR | 1077 given names (unweighted), 37 family names (unweighted) |
| United Kingdom | GB | most common 20 given (unweighted), most common 25 family names (unweighted) |
| USA | US | most common 600 given names and most common 1000 family names both with absolute weight |

Your use-cases are more sophisticated and require additional countries, cities or valid addresses down to 
street level and number, check our [Benerator Editions](https://www.benerator.de/) or feel 
free to contact us for assistance: **[team@rapiddweller.com](mailto:team@rapiddweller.com)**.

## Address domain

* **AddressGenerator**: Generates addresses that match simple validity checks: 
  The City exists, the ZIP code matches and the phone number area codes
  are right. The street names are random, so most addresses will not stand validation of real existence.

* **PhoneNumberGenerator**: Generates landline telephone numbers for a country

* **MobilePhoneNumberGenerator**: Generates mobile phone numbers for a country

* **CountryGenerator**: Generates countries

* **CountryCode2**: Annotation that marks a Java attribute or property as ISO-3166-1 alpha-2 code

* **CountryCode2Validator**: Java Bean Validation ConstraintValidator for ISO-3166-1 alpha-2 codes

* **CityGenerator**: Generates Cities for a given country

* **StreetNameGenerator**: Generates street names for a given country

* **PostalCodeValidator**: Validates if a given postal code is valid in a given country

![](assets/domain_address.png)

The following countries are supported:

| country        | code | remarks |
| ---            | ---  | --- |
| USA            |  US  | Valid ZIP codes and area codes, no assurance that the street exists in this city. |
| United Kingdom |  GB  | Valid area codes, no postcodes, no assurance that the street exists in this city or the local phone number has the appropriate length. Contributions are welcome |
| Germany        |  DE  | Valid ZIP codes and area codes, no assurance that the street exists in this city or the local phone number has the appropriate length |
| Switzerland    |  CH  | Valid ZIP codes and area codes, no assurance that the street exists in this city or the local phone number has the appropriate length |
| Brazil         |  BR  | Valid ZIP codes and area codes, no assurance that the street exists in this city or the local phone number has the appropriate length |

The generated Address objects have the following data fields:

* **street**: The regular street address

* **street2**: Additions to the street address, like c/o or postbox

* **postalCode**: the postal code

* **city**: A reference to a City object, which can be navigated and queried by a script expression, for example `{address.city.name}`

* **state**: A reference to a State object, which can be navigated and queried by a script expression, for example `{address.state.name}`

* **country**: A reference to a Country object, which can be navigated and queried by a script expression, for example `{address.country.name}`

* **privatePhone**, **officePhone**, **mobilePhone** and **fax**: References to PhoneNumber objects, which can be navigated and queried by a script expression

* **organization** and **department**: Names of an organization and a department.


## net domain

The net domain provides the

* **DomainGenerator**, which generates Internet domain names

## organization domain

Provides the following generators:

* **CompanyNameGenerator**, a generator for company names.

* **DepartmentNameGenerator**, a generator for department names

![](assets/domain_organization.png)

If you use the CompanyNameGenerator like this:

```xml
<import domains="organization" />

<generate type="company" count="5" consumer="ConsoleExporter">
    <attribute name="name" generator="CompanyNameGenerator" dataset="DE" locale="de_DE"/>
</generate>
```

you get output like this:

```
company[name=Belanda Aktiengesellschaft &amp; Co. KG]
company[name=MyWare Technologies GmbH]
company[name=WebBox AG]
company[name=Altis AG]
company[name=Ernst Fischer Technik GmbH]
```

Company names can be generated for the following countries:

| country | code | remarks |
| --- | --- | --- |
| Germany | DE | none |
| USA | US | none |

The Generator creates objects of type CompanyName, consisting of **shortName**, **sector**, **location** and **
legalForm** information. You can make use of the object as a whole which is converted to a string automatically using the '**fullName**' version as
shown in the example above. But you can also make direct use of the basic properties:

```xml
<import domains="organization" />

<generate type="company" count="5" consumer="ConsoleExporter">
    <variable name="c" generator="CompanyNameGenerator" dataset="DE" locale="de_DE"/>
    <attribute name="name" script="c.shortName + ' ' + c.legalForm" />
</generate>
```

## finance domain

Generates and validates finance related data:

The following classes are provided:

* **BankAccountGenerator**: Generates BankAccount JavaBeans

* **BankAccountValidator**: Validates BankAccount JavaBeans

* **CreditCardNumberGenerator**: Generates strings which represent credit card numbers

* **CreditCardNumberValidator**: Validates strings as credit card numbers

* **IBAN**: Annotation for Java Bean Validation, marking a Java attribute/property as IBAN

* **IBANValidator**: Validates strings with IBANs

## product domain

The product package provides you with Generator classes for EAN codes:

* **EAN8Generator**: Generates 8-digit EAN codes

* **EAN8**: Annotation that marks a Java attribute or property as 8-digit-EAN for bean validation

* **EAN8Validator**: Validates 8-digit EAN codes

* **EAN13Generator**: Generates 13-digit EAN codes

* **EAN13**: Annotation that marks a Java attribute or property as 13-digit-EAN for bean validation

* **EAN13Validator**: Validates 13-digit EAN codes

* **EANGenerator**: Generates both 8-digit and 13-digit EAN codes

* **EAN**: Annotation that marks a Java attribute or property as an EAN for bean validation

* **EANValidator**: Validates 8- and 13-digit-EAN codes

Each generator has a property **'unique'**: If set to true the generator assures that no two identical EAN codes are generated.

## br domain

Provides classes specific to Brazil:

* **CPNJ**: Annotation to mark a Java attribute or property as a CPNJ (Cadastro Nacional da Pessoa Jurídica)

* **CPNJGenerator**: Generates CPNJs

* **CPNJValidator**: Validates CPNJs and can be used as Benerator validator and as ConstraintValidator in Java Bean Validation (JSR 303)

* **CPF**: Annotation to mark a Java attribute or property as a CPF (Cadastro de Pessoa Fisica)

* **CPFGenerator**: Generates CPFs

* **CPFValidator**: Validates CPFs

## us domain

Provides classes specific for the United States of America:

* **SSN**: Annotation to mark a Java attribute or property as a Social Security Number

* **SSNGenerator**: Generates Social Security Numbers

* **SSNValidator**: Validates Social Security Numbers and can be used as Benerator validator and as ConstraintValidator in Java Bean Validation (JSR 303)

## faker domain

The faker package provides the Generator class with library base on Java Faker (as well as Perl's Data::Faker library)

* **FakerGenerator** : Generates data for many topics such as book, food, music... 

Because this Generator has many topics, each topic has many properties, you have to choose topic and property and put it into 
the 'generator' as parameters (like this `generator="new FakerGenerator('topic'','property')"`).

Some topics have different data which base on the locale, you can change it by manual setting 'locale'.

You can use the FakerGenerator like this:

```xml
<import domains="faker"/>

<generate type="data" count="5" consumer="ConsoleExporter">
    <attribute name="name" generator="new FakerGenerator('name','fullName')" locale="en_US"/>
    <attribute name="HarryPotter" generator="new FakerGenerator('harryPotter','character')" locale="en"/>
    <attribute name="dinner" type="string" generator="new FakerGenerator('food','dish')" locale="de_CH"/>
</generate>
```

to get output similar to this:

```
data[name=Maira Lubowitz MD, HarryPotter=Pomona Sprout, dinner=French Fries with Sausages]
data[name=Mr. Josue Kreiger, HarryPotter=Fang, dinner=Chicken Milanese]
data[name=Henry Skiles, HarryPotter=Parvati Patil, dinner=Scotch Eggs]
data[name=Minda Breitenberg, HarryPotter=Helga Hufflepuff, dinner=Lasagne]
data[name=Nakita Schamberger, HarryPotter=Aragog, dinner=Poke]
```

### Supported topics:

FakerGenerator can generate data for the following topics (with properties):

### Topic: address

| Property name | Type |
|---|---|
| state | String |
| country | String |
| lastName | String |
| firstName | String |
| streetName | String |
| zipCode | String |
| stateAbbr | String |
| citySuffix | String |
| cityPrefix | String |
| city | String |
| cityName | String |
| latitude | String |
| longitude | String |
| timeZone | String |
| streetAddressNumber | String |
| streetAddress | String |
| secondaryAddress | String |
| streetSuffix | String |
| streetPrefix | String |
| countryCode | String |
| buildingNumber | String |
| fullAddress | String |

### Topic: ancient

| Property name | Type |
|---|---|
| god | String |
| primordial | String |
| titan | String |
| hero | String |

### Topic: animal

| Property name | Type |
|---|---|
| name | String |

### Topic: app

| Property name | Type |
|---|---|
| name | String |
| version | String |
| author | String |

### Topic: aquaTeenHungerForce

| Property name | Type |
|---|---|
| character | String |

### Topic: artist

| Property name | Type |
|---|---|
| name | String |

### Topic: avatar

| Property name | Type |
|---|---|
| image | String |

### Topic: aviation

| Property name | Type |
|---|---|
| aircraft | String |
| airport | String |
| METAR | String |

### Topic: backToTheFuture

| Property name | Type |
|---|---|
| character | String |
| quote | String |
| date | String |

### Topic: beer

| Property name | Type |
|---|---|
| name | String |
| style | String |
| hop | String |
| yeast | String |
| malt | String |

### Topic: book

| Property name | Type |
|---|---|
| genre | String |
| title | String |
| author | String |
| publisher | String |

### Topic: bool

| Property name | Type |
|---|---|
| bool | boolean |

### Topic: buffy

| Property name | Type |
|---|---|
| characters | String |
| quotes | String |
| bigBads | String |
| episodes | String |
| celebrities | String |

### Topic: business

| Property name | Type |
|---|---|
| creditCardNumber | String |
| creditCardType | String |
| creditCardExpiry | String |

### Topic: cat

| Property name | Type |
|---|---|
| name | String |
| registry | String |
| breed | String |

### Topic: chuckNorris

| Property name | Type |
|---|---|
| fact | String |

### Topic: code

| Property name | Type |
|---|---|
| asin | String |
| isbnGs1 | String |
| isbnGroup | String |
| isbn10 | String |
| isbn13 | String |
| imei | String |
| ean8 | String |
| gtin8 | String |
| ean13 | String |
| gtin13 | String |
| isbnRegistrant | String |

### Topic: color

| Property name | Type |
|---|---|
| name | String |
| hex | String |

### Topic: commerce

| Property name | Type |
|---|---|
| color | String |
| department | String |
| material | String |
| price | String |
| productName | String |
| promotionCode | String |

### Topic: company

| Property name | Type |
|---|---|
| name | String |
| bs | String |
| suffix | String |
| url | String |
| industry | String |
| profession | String |
| buzzword | String |
| logo | String |
| catchPhrase | String |

### Topic: country

| Property name | Type |
|---|---|
| name | String |
| flag | String |
| currencyCode | String |
| currency | String |
| capital | String |
| countryCode2 | String |
| countryCode3 | String |

### Topic: crypto

| Property name | Type |
|---|---|
| md5 | String |
| sha1 | String |
| sha256 | String |
| sha512 | String |

### Topic: currency

| Property name | Type |
|---|---|
| name | String |
| code | String |

### Topic: date

| Property name | Type |
|---|---|
| birthday | Date |

### Topic: demographic

| Property name | Type |
|---|---|
| race | String |
| demonym | String |
| sex | String |
| educationalAttainment | String |
| maritalStatus | String |

### Topic: dog

| Property name | Type |
|---|---|
| name | String |
| size | String |
| breed | String |
| sound | String |
| memePhrase | String |
| age | String |
| coatLength | String |
| gender | String |

### Topic: dragonBall

| Property name | Type |
|---|---|
| character | String |

### Topic: dune

| Property name | Type |
|---|---|
| character | String |
| quote | String |
| title | String |
| planet | String |
| saying | String |

### Topic: educator

| Property name | Type |
|---|---|
| university | String |
| course | String |
| campus | String |
| secondarySchool | String |

### Topic: elderScrolls

| Property name | Type |
|---|---|
| quote | String |
| region | String |
| lastName | String |
| firstName | String |
| city | String |
| race | String |
| dragon | String |
| creature | String |

### Topic: esports

| Property name | Type |
|---|---|
| event | String |
| game | String |
| player | String |
| league | String |
| team | String |

### Topic: file

| Property name | Type |
|---|---|
| fileName | String |
| extension | String |
| mimeType | String |

### Topic: finance

| Property name | Type |
|---|---|
| creditCard | String |
| bic | String |
| iban | String |

### Topic: food

| Property name | Type |
|---|---|
| ingredient | String |
| spice | String |
| dish | String |
| fruit | String |
| vegetable | String |
| sushi | String |
| measurement | String |

### Topic: friends

| Property name | Type |
|---|---|
| location | String |
| character | String |
| quote | String |

### Topic: funnyName

| Property name | Type |
|---|---|
| name | String |

### Topic: gameOfThrones

| Property name | Type |
|---|---|
| character | String |
| quote | String |
| city | String |
| dragon | String |
| house | String |

### Topic: hacker

| Property name | Type |
|---|---|
| adjective | String |
| noun | String |
| verb | String |
| ingverb | String |
| abbreviation | String |

### Topic: harryPotter

| Property name | Type |
|---|---|
| location | String |
| character | String |
| quote | String |
| book | String |
| spell | String |
| house | String |

### Topic: hipster

| Property name | Type |
|---|---|
| word | String |

### Topic: hitchhikersGuideToTheGalaxy

| Property name | Type |
|---|---|
| location | String |
| character | String |
| quote | String |
| planet | String |
| specie | String |
| starship | String |
| marvinQuote | String |

### Topic: hobbit

| Property name | Type |
|---|---|
| location | String |
| character | String |
| quote | String |
| thorinsCompany | String |

### Topic: howIMetYourMother

| Property name | Type |
|---|---|
| character | String |
| quote | String |
| highFive | String |
| catchPhrase | String |

### Topic: idNumber

| Property name | Type |
|---|---|
| valid | String |
| invalid | String |
| ssnValid | String |
| validSvSeSsn | String |
| invalidSvSeSsn | String |

### Topic: internet

| Property name | Type |
|---|---|
| url | String |
| image | String |
| domainName | String |
| domainWord | String |
| password | String |
| macAddress | String |
| ipV4Cidr | String |
| ipV6Cidr | String |
| slug | String |
| uuid | String |
| avatar | String |
| emailAddress | String |
| safeEmailAddress | String |
| domainSuffix | String |
| ipV4Address | String |
| privateIpV4Address | String |
| publicIpV4Address | String |
| ipV6Address | String |
| userAgentAny | String |

### Topic: job

| Property name | Type |
|---|---|
| position | String |
| field | String |
| title | String |
| seniority | String |
| keySkills | String |

### Topic: leagueOfLegends

| Property name | Type |
|---|---|
| location | String |
| quote | String |
| champion | String |
| masteries | String |
| rank | String |
| summonerSpell | String |

### Topic: lebowski

| Property name | Type |
|---|---|
| character | String |
| quote | String |
| actor | String |

### Topic: lordOfTheRings

| Property name | Type |
|---|---|
| location | String |
| character | String |

### Topic: lorem

| Property name | Type           |
|---|----------------|
| character | char           |
| word | String         |
| words | List\<String\> |
| characters | String         |
| sentence | String         |
| paragraph | String         |

### Topic: matz

| Property name | Type |
|---|---|
| quote | String |

### Topic: medical

| Property name | Type |
|---|---|
| symptoms | String |
| medicineName | String |
| diseaseName | String |
| hospitalName | String |

### Topic: music

| Property name | Type |
|---|---|
| key | String |
| chord | String |
| genre | String |
| instrument | String |

### Topic: name

| Property name | Type |
|---|---|
| name | String |
| prefix | String |
| suffix | String |
| lastName | String |
| fullName | String |
| firstName | String |
| title | String |
| username | String |
| bloodGroup | String |
| nameWithMiddle | String |

### Topic: nation

| Property name | Type |
|---|---|
| flag | String |
| language | String |
| nationality | String |
| capitalCity | String |

### Topic: number

| Property name | Type |
|---|---|
| digit | String |
| randomDigit | int |
| randomDigitNotZero | int |
| randomNumber | long |

### Topic: overwatch

| Property name | Type |
|---|---|
| location | String |
| quote | String |
| hero | String |

### Topic: phoneNumber

| Property name | Type |
|---|---|
| extension | String |
| cellPhone | String |
| phoneNumber | String |
| subscriberNumber | String |

### Topic: pokemon

| Property name | Type |
|---|---|
| name | String |
| location | String |

### Topic: princessBride

| Property name | Type |
|---|---|
| character | String |
| quote | String |

### Topic: programmingLanguage

| Property name | Type |
|---|---|
| name | String |
| creator | String |

### Topic: relationships

| Property name | Type |
|---|---|
| parent | String |
| direct | String |
| any | String |
| extended | String |
| inLaw | String |
| spouse | String |
| sibling | String |

### Topic: rickAndMorty

| Property name | Type |
|---|---|
| location | String |
| character | String |
| quote | String |

### Topic: robin

| Property name | Type |
|---|---|
| quote | String |

### Topic: rockBand

| Property name | Type |
|---|---|
| name | String |

### Topic: shakespeare

| Property name | Type |
|---|---|
| hamletQuote | String |
| asYouLikeItQuote | String |
| kingRichardIIIQuote | String |
| romeoAndJulietQuote | String |

### Topic: slackEmoji

| Property name | Type |
|---|---|
| people | String |
| nature | String |
| activity | String |
| custom | String |
| emoji | String |
| foodAndDrink | String |
| celebration | String |
| travelAndPlaces | String |
| objectsAndSymbols | String |

### Topic: space

| Property name | Type |
|---|---|
| planet | String |
| moon | String |
| galaxy | String |
| nebula | String |
| star | String |
| agency | String |
| meteorite | String |
| company | String |
| starCluster | String |
| constellation | String |
| agencyAbbreviation | String |
| nasaSpaceCraft | String |
| distanceMeasurement | String |

### Topic: starTrek

| Property name | Type |
|---|---|
| location | String |
| character | String |
| specie | String |
| villain | String |

### Topic: stock

| Property name | Type |
|---|---|
| nsdqSymbol | String |
| nyseSymbol | String |

### Topic: superhero

| Property name | Type |
|---|---|
| name | String |
| prefix | String |
| suffix | String |
| descriptor | String |
| power | String |

### Topic: team

| Property name | Type |
|---|---|
| name | String |
| state | String |
| sport | String |
| creature | String |

### Topic: twinPeaks

| Property name | Type |
|---|---|
| location | String |
| character | String |
| quote | String |

### Topic: university

| Property name | Type |
|---|---|
| name | String |
| prefix | String |
| suffix | String |

### Topic: weather

| Property name | Type |
|---|---|
| description | String |
| temperatureCelsius | String |
| temperatureFahrenheit | String |

### Topic: witcher

| Property name | Type |
|---|---|
| location | String |
| character | String |
| quote | String |
| school | String |
| monster | String |
| witcher | String |

### Topic: yoda

| Property name | Type |
|---|---|
| quote | String |

### Topic: zelda

| Property name | Type |
|---|---|
| character | String |
| game | String |

### Supported Locales

| Language | code              |
|----------|-------------------|
|Bulgarian| bg                |
|Catalan| ca, ca_CAT, da_DK |
|German|de, de_AT, de_CH|
|English|en, en_AU, en_au_ocker, en_BORK, en_CA, en_GB, en_IND, en_MS, en_NEP, en_NG, en_NZ, en_PAK, en_SG, en_UG, en_US, en_ZA|
|Spanish|es, es_MX|
|Finnish|fi_FI|
|French|fr|
|Hungarian|hu|
|Indonesian|in_ID|
|Italian|it|
|Japanese|ja|
|Korean|ko|
|Norwegian Bokmål|nb_NO|
|Dutch|nl|
|Polish|pl|
|Portuguese|pt, pt_BR|
|Russian|ru|
|Slovak|sk|
|Swedish|sv, sv_SE|
|Turkish|tr|
|Ukrainian|uk|
|Vietnamese|vi|
|Chinese|zh_CN, zh_TW|



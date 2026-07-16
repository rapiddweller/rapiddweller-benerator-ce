# From Benerator to DataMimic: Migrating a Complex E-Commerce Pipeline

**July 2026 — Alexander Kell, rapiddweller**

---

Your Benerator descriptors have served you well. Hundreds of XML files, thousands of lines,
years of tuning. But now you're eyeing DataMimic CE — Python-native, MIT-licensed, MCP-ready.
The question isn't *whether* to migrate. It's *how much work* it will be.

We built a converter that answers that question. Here's the proof: a realistic, complex
e-commerce pipeline migrated with **zero manual intervention** and running in DataMimic CE.

## The Pipeline: 7 Phases, 340 Generated Records

Our demo models a full order-fulfillment system — products, customers, orders, line items,
payments, and a reporting dashboard:

```
Phase 1: 30 products   — catalog with weighted categories, EAN codes, date ranges
Phase 2: 20 customers  — Person entities, nested addresses, tier assignments
Phase 3: 60 orders     — memstore cross-references, conditional premium handling
Phase 4: 180 line items — multi-source joins, list-type nested discounts
Phase 5: 50 payments   — while-loop installment calculation
Phase 6: 10 dashboard  — ordered read-back, multi-entity enrichment
Phase 7: assertions     — data quality checks converted to native <assert>
```

## Before: The Benerator Descriptor

Here's a condensed look at the source — 170 lines of Benerator XML using composite generators,
nested parts, memstore pipelines, weighted values, conditions, and while loops:

```xml
<setup xmlns="https://www.benerator.de/schema/3.0.0"
       defaultDataset="US" defaultLocale="en_US">

    <memstore id="store"/>

    <!-- Phase 1: Products with weighted categories -->
    <generate type="product" count="30" consumer="store">
        <id name="ean_code" generator="new EANGenerator(true)"/>
        <attribute name="category"
            values="'Electronics'^35,'Clothing'^25,'Home'^20,'Sports'^15,'Books'^5"/>
        <attribute name="price" type="double" min="0.99" max="999.99" granularity="0.01"/>
        <attribute name="listedSince" type="date" min="2020-01-01" max="2025-12-31"/>
        <attribute name="inStock" type="boolean"/>
        <attribute name="rating" type="double" min="1.0" max="5.0" nullQuota="0.1"/>
    </generate>

    <!-- Phase 2: Customers with Person entity + nested address -->
    <generate type="customer" count="20" consumer="store">
        <variable name="person" generator="PersonGenerator" dataset="US"/>
        <variable name="addr" generator="AddressGenerator" dataset="US"/>
        <id name="id" type="int"/>
        <attribute name="email" generator="EMailAddressGenerator"/>
        <attribute name="fullName"
            script="person.givenName + ' ' + person.familyName"/>
        <part name="shippingAddress">
            <attribute name="street" script="addr.street"/>
            <attribute name="city" script="addr.city"/>
            <attribute name="country" script="addr.country"/>
        </part>
    </generate>

    <!-- Phase 3: Orders with conditional premium handling -->
    <generate type="order" count="60" consumer="store">
        <variable name="cust" source="store" type="customer"
            distribution="random" cyclic="true"/>
        <id name="id" type="int"/>
        <attribute name="customerTier" script="cust.tier"/>

        <if test="customerTier == 'gold' || customerTier == 'platinum'">
            <then>
                <attribute name="priority" constant="true"/>
                <attribute name="handlingFee" constant="0.0"/>
            </then>
            <else>
                <attribute name="handlingFee" type="double" min="4.99" max="14.99"/>
            </else>
        </if>
    </generate>

    <!-- Phase 5: Payments with while-loop installments -->
    <generate type="payment" count="50" consumer="store">
        <variable name="ord" source="store" type="order"
            distribution="random" cyclic="true"/>
        <attribute name="baseAmount" type="double" min="20.0" max="500.0"/>
        <attribute name="remaining" script="baseAmount"/>
        <attribute name="installments" type="int" constant="0"/>

        <while test="remaining &gt; 0 &amp;&amp; installments &lt; 6">
            <attribute name="remaining" script="remaining - 10"/>
            <attribute name="installments" script="installments + 1"/>
        </while>
    </generate>

    <!-- Phase 7: Assertions -->
    <if test="totalProducts != 30">
        <error>Expected 30 products, check catalog generation</error>
    </if>
</setup>
```

## After: What the Converter Produces

Run `DatamimicConverter` on the descriptor. Result: **0 manual findings**. Every construct
maps to native DataMimic vocabulary:

```xml
<setup defaultDataset="US" defaultLocale="en_US" defaultSeparator=",">
  <memstore id="store"/>

  <!-- Phase 1: Products — weighted values split, date bounds reformatted -->
  <generate count="30" name="product" target="store">
    <id generator="EANGenerator(unique=True)" name="ean_code"/>
    <key name="category"
        values="'Electronics','Clothing','Home','Sports','Books'"
        weights="35,25,20,15,5"/>
    <key granularity="0.01" max="999.99" min="0.99" name="price" type="float"/>
    <key generator="DateTimeGenerator(min='2020-01-01 00:00:00', max='2025-12-31 00:00:00')"
        name="listedSince"/>
    <key name="inStock" type="bool"/>
    <key granularity="0.5" max="5.0" min="1.0" name="rating" nullQuota="0.1" type="float"/>
  </generate>

  <!-- Phase 2: PersonGenerator -> entity="Person", <part> -> <nestedKey> -->
  <generate count="20" name="customer" target="store">
    <variable dataset="US" entity="Person" name="person"/>
    <variable dataset="US" entity="Address" name="addr"/>
    <id generator="IncrementGenerator()" name="id"/>
    <key generator="EmailAddressGenerator" name="email"/>
    <key name="fullName" script="person.givenName + ' ' + person.familyName"/>
    <nestedKey name="shippingAddress" type="dict">
      <key name="street" script="addr.street"/>
      <key name="city" script="addr.city"/>
      <key name="country" script="addr.country"/>
    </nestedKey>
  </generate>

  <!-- Phase 3: || -> or, <if>/<then>/<else> -> <condition>/<if>/<else> -->
  <generate count="60" name="order" target="store">
    <variable cyclic="true" distribution="random" name="cust"
        source="store" sourceEntity="customer"/>
    <id generator="IncrementGenerator()" name="id"/>
    <key name="customerTier" script="cust.tier"/>
    <condition>
      <if condition="customerTier == 'gold' or customerTier == 'platinum'">
        <key constant="true" name="priority"/>
        <key constant="0.0" name="handlingFee"/>
      </if>
      <else>
        <key granularity="0.01" max="14.99" min="4.99" name="handlingFee" type="float"/>
      </else>
    </condition>
  </generate>

  <!-- Phase 5: && -> and, <while test> -> <while condition> -->
  <generate count="50" name="payment" target="store">
    <variable cyclic="true" distribution="random" name="ord"
        source="store" sourceEntity="order"/>
    <key granularity="0.01" max="500.0" min="20.0" name="baseAmount" type="float"/>
    <key name="remaining" script="baseAmount"/>
    <key constant="0" name="installments" type="int"/>
    <while condition="remaining > 0 and installments < 6">
      <key name="remaining" script="remaining - 10"/>
      <key name="installments" script="installments + 1"/>
    </while>
  </generate>

  <!-- Phase 7: <if>/<error> -> <assert> -->
  <assert condition="not (totalProducts != 30)"
      message="Expected 30 products, check catalog generation"/>
</setup>
```

## Execution: DataMimic CE Runs It

```
$ datamimic run complex_ecommerce.datamimic.xml

Generating 30 records 'product'     … 24,077 records/second
Generating 20 records 'customer'    …    774 records/second
Generating 60 records 'order'       …  7,301 records/second
Generating 180 records 'orderItem'  … 10,814 records/second
Generating 50 records 'payment'     …  4,535 records/second
Generating 10 records 'dashboard'   … 21,194 records/second

dashboard: {'orderId': 1, 'status': 'delivered', 'priority': 'true',
            'custName': 'Mattie Clay', 'custTier': 'silver',
            'custCountry': 'United States'}
dashboard: {'orderId': 2, 'status': 'delivered', 'priority': 'false',
            'handlingFee': 8.82, 'custName': 'Leon Fischer',
            'custTier': 'gold', 'custCountry': 'United States'}
…
```

340 records generated and exported. Assertions pass. Zero errors.

## What the Converter Does Automatically

Here's the full list of transformations applied to this single descriptor:

| Benerator | → DataMimic |
|---|---|
| `<generate type="X">` | `<generate name="X">` |
| `<attribute>` | `<key>` |
| `<part>` | `<nestedKey type="dict\|list">` |
| `<if test>/<then>/<else>` | `<condition>/<if condition>/<else>` |
| `<if test><error>MSG</error></if>` | `<assert condition="not(...)" message="MSG">` |
| `<while test>` | `<while condition>` |
| `<variable generator="PersonGenerator">` | `<variable entity="Person">` |
| `<id type="int">` | `<id generator="IncrementGenerator()">` |
| `consumer="mem"` | `target="mem"` |
| `<variable source="mem" type="X">` | `<variable source="mem" sourceEntity="X">` |
| `values="'A'^70,'B'^30"` | `values="'A','B'" weights="70,30"` |
| `type="date" min="2020-01-01"` | `generator="DateTimeGenerator(min='…')"` |
| `type="boolean"` | `type="bool"` |
| `generator="EMailAddressGenerator"` | `generator="EmailAddressGenerator"` |
| `converter="CaseConverter"` | `converter="UpperCase"` |
| `\|\|` / `&&` in conditions | `or` / `and` |
| `true`/`false`/`null` | `True`/`False`/`None` |
| Java ternary `a ? b : c` | Python `(b) if (a) else (c)` |
| `consumer="ConsoleExporter"` | `target="ConsoleExporter"` |

## The Migration Flow

```
┌──────────────────┐     ┌──────────────────┐     ┌──────────────────┐
│  shop.ben.xml    │ ──→ │  DatamimicConverter│ ──→ │ shop.datamimic.xml│
│  (Benerator DSL) │     │  (zero-config)    │     │  (DataMimic DSL)  │
└──────────────────┘     └──────────────────┘     └──────────────────┘
                                                         │
                                                    ┌────▼────┐
                                                    │datamimic│
                                                    │   run   │
                                                    └────┬────┘
                                                         │
                                                    ┌────▼────┐
                                                    │ 340 rows│
                                                    │ 0 errors│
                                                    └─────────┘
```

## What Still Needs Manual Attention

Not everything converts automatically — and that's by design. The converter flags what it
can't map, giving you an honest assessment:

- **`<execute type="js">`** — rewrite in Python (DataMimic has no JS engine)
- **`<bean>`** — DataMimic has no bean layer; inline the definition
- **Database connections** — environment-specific; set `dbms=` manually
- **Setup-level `<if>`** — DataMimic's `<condition>` lives inside `<generate>`

Our sweep over 317 Benerator descriptors shows **341 manual findings across the entire
corpus** — down from 436 before this session's converter improvements. Most are
deliberately flagged constructs that genuinely need human judgment.

## Try It Yourself

```bash
# 1. Convert your descriptor
java -cp benerator-ce.jar \
  com.rapiddweller.benerator.main.datamimic.DatamimicConverter \
  shop.ben.xml converted/

# 2. Run the converted output in DataMimic CE
pip install datamimic-ce
datamimic run converted/shop.datamimic.xml
```

The converter is in [rapiddweller-benerator-ce](https://github.com/rapiddweller/rapiddweller-benerator-ce)
(`datamimic-converter-4.0.1` branch). The full complex demo lives in
`src/test/resources/…/roundtrip_corpus/complex_ecommerce.ben.xml`.

---

*This post is part of the Benerator → DataMimic CE migration series. The converter is
MIT-licensed and ships with Benerator CE 4.0.1.*

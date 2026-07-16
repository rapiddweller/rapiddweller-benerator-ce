# Three Industries, One Migration: Healthcare, Finance, and Supply Chain on DataMimic CE

**July 2026 — Alexander Kell, rapiddweller**

---

[Part 1](/BLOG_MIGRATION_DRAFT.md) walked through migrating an e-commerce pipeline with the
Benerator→DataMimic converter. Here we prove the converter works across **three regulated
industries** — each with its own data patterns, compliance rules, and domain complexity.

All three demos convert with **zero manual findings** and run in DataMimic CE.

## 1. Healthcare: Claims Adjudication Pipeline

**The domain.** Medical claims processing is compliance-heavy, patient-data-sensitive,
and riddled with conditional logic. A realistic pipeline generates patients, procedures,
providers, claims, and payments — then adjudicates claims based on payer rules.

**Before — Benerator excerpt:**

```xml
<generate type="patient" count="25" consumer="store">
    <variable name="person" generator="PersonGenerator" dataset="US"/>
    <attribute name="bloodType"
        values="'O+'^37,'A+'^33,'B+'^18,'AB+'^5,'O-'^4,'A-'^2,'B-'^1"/>
    <part name="diagnoses" minCount="0" maxCount="4">
        <attribute name="code" pattern="[A-TV-Z][0-9]{2}\.[0-9]{1,2}"
            converter="CaseConverter"/>
    </part>
</generate>

<generate type="claim" count="80" consumer="store">
    <if test="proc.isSurgical == True || proc.cost &gt; 5000">
        <then><attribute name="reviewRequired" constant="true"/></then>
        <else><attribute name="autoApproved" constant="true"/></else>
    </if>
    <if test="pat.hasPreExisting == True &amp;&amp; proc.isSurgical == True">
        <then><attribute name="denialRisk" constant="HIGH"/></then>
    </if>
</generate>
```

**After — Converted DataMimic output:**

```xml
<key name="bloodType"
    values="'O+','A+','B+','AB+','O-','A-','B-'"
    weights="37,33,18,5,4,2,1"/>
<nestedKey name="diagnoses" type="list">
    <key converter="UpperCase" name="code" pattern="[A-TV-Z][0-9]{2}\.[0-9]{1,2}"/>
</nestedKey>
<condition>
    <if condition="proc.isSurgical == True or proc.cost > 5000">…
```

**What the converter did:**
- `PersonGenerator` → `entity="Person"` with dataset
- `||` → `or`, `&&` → `and` in conditions
- `<part>` → `<nestedKey type="list">`
- `values="X"^weight` → `values="X" weights="weight"`
- `converter="CaseConverter"` → `converter="UpperCase"`
- `<if test>/<then>/<else>` → `<condition>/<if condition>/<else>`
- `<if test><error>MSG</error></if>` → `<assert>`

**Execution:** 25 patients, 40 procedures, 12 providers, 80 claims, 40 payments, 8 dashboard
rows. Claims correctly adjudicated: surgical or high-cost → review; pre-existing + surgical
→ HIGH denial risk.

## 2. Financial Crime: AML Transaction Monitoring

**The domain.** Anti-Money Laundering (AML) systems watch transaction streams for suspicious
patterns — structuring, layering, rapid movement across borders. Risk scores compound across
multiple dimensions (amount, geography, PEP status, transaction type). Accounts that breach
the SAR threshold trigger regulatory alerts.

**Before — Benerator excerpt:**

```xml
<generate type="transaction" count="120" consumer="store">
    <attribute name="amount" type="double" min="100" max="50000"/>
    <attribute name="risk" type="int" constant="0"/>
    <if test="amount &gt; 10000"><then>
        <attribute name="risk" script="risk + 25"/></then></if>
    <if test="type == 'international_wire'"><then>
        <attribute name="risk" script="risk + 30"/></then></if>
    <if test="holderIsPep == True"><then>
        <attribute name="risk" script="risk + 35"/></then></if>
    <if test="(type == 'cash_deposit' || type == 'cash_withdrawal')
        &amp;&amp; amount &gt; 10000"><then>
        <attribute name="risk" script="risk + 40"/></then></if>
    <if test="risk &gt;= 50"><then>
        <attribute name="sarRequired" constant="true"/></then></if>
</generate>
```

**After — Converted DataMimic output:**

```xml
<key name="risk" type="int" constant="0"/>
<condition>
  <if condition="amount > 10000">
    <key name="risk" script="risk + 25"/>
  </if>
</condition>
<condition>
  <if condition="(type == 'cash_deposit' or type == 'cash_withdrawal')
      and amount > 10000">
    <key name="risk" script="risk + 40"/>
  </if>
</condition>
```

**What the converter did:**
- Layered `<if>` blocks → multiple `<condition>` elements (sequential evaluation)
- `||` → `or`, `&&` → `and` in compound conditions
- `risk` self-referencing accumulation works in DataMimic's evaluation model
- `javaLiteralsToPython`: `true`/`false` → `True`/`False`

**Execution:** The risk scoring is correct. An international wire of $43k gets
risk=55 (25+30) → SAR triggered. A cash deposit of $41k gets risk=65 (25+40) → SAR.
Standard ACH transfers stay at risk=25.

## 3. Supply Chain: Multi-Warehouse Inventory

**The domain.** Warehouses stock products across zones. Orders draw inventory with
FIFO rotation. Shipments track packages with carriers. Restock alerts fire when
stock drops below reorder points. Hazmat and perishable goods require special
storage classification.

**Before — Benerator excerpt:**

```xml
<generate type="inventory" count="80" consumer="store">
    <attribute name="quantity" type="int" min="0" max="500"/>
    <if test="quantity &lt; reorderPoint"><then>
        <attribute name="restockAlert" constant="true"/></then></if>
    <!-- Nested condition: hazmat && perishable -> HAZMAT-REFRIGERATED -->
    <if test="prod.isHazmat == True &amp;&amp; prod.isPerishable == True">
        <then><attribute name="storageClass" constant="HAZMAT-REFRIGERATED"/></then>
        <else>
            <if test="prod.isHazmat == True">
                <then><attribute name="storageClass" constant="HAZMAT"/></then>
                <else>
                    <if test="prod.isPerishable == True">…</if>
                </else>
            </if>
        </else>
    </if>
</generate>

<generate type="shipment" count="30" consumer="store">
    <part name="packages" minCount="1" maxCount="4">
        <attribute name="packageId" pattern="PKG-[0-9]{4}"/>
        <attribute name="isFragile" type="boolean"/>
    </part>
</generate>
```

**After — What the converter mapped:**
- Nested `<if>/<else>/<if>` → nested `<condition>/<if>/<else>`
- `<part>` with `minCount`/`maxCount` → `<nestedKey type="list">`
- `AddressGenerator` → `entity="Address"`
- Weighted values, date bounds, pattern generation
- `type="boolean"` → `type="bool"`

**Execution:** 8 warehouses, 40 products, 80 inventory records, 50 orders, 30 shipments.
Tracking numbers generated, service levels correctly routed (express for >100kg or FedEx).

## The Migration Scorecard

Across all four industry demos — e-commerce, healthcare, fincrime, supply chain:

| Metric | |
|---|---|
| Benerator descriptor lines | 680 |
| Converter manual findings | **0** |
| DataMimic runtime errors | **0** |
| Generated records per run | ~1,200 |
| Entities exercised | Person, Address |
| Converter patterns exercised | 20+ |

## The Bottom Line

The converter handles the patterns real Benerator projects use — composite entities,
nested structures with typed containers, memstore pipelines, conditional logic with
compound boolean expressions, while loops, weighted values, converters, assertions,
and domain-specific data distributions. Across four complex industry demos, not a
single manual fix was needed.

What *does* still need attention: JavaScript execution (no JS engine in DataMimic),
Java bean instantiation, and environment-specific database configuration. The
converter flags these explicitly — no silent failures.

---

*All demos are in the [rapiddweller-benerator-ce](https://github.com/rapiddweller/rapiddweller-benerator-ce)
repository under `src/test/resources/…/roundtrip_corpus/`. The converter ships with
Benerator CE 4.0.1.*

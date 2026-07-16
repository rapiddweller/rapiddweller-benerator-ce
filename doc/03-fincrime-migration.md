# From Benerator to DataMimic: AML Transaction Monitoring

**July 2026 — Alexander Kell, rapiddweller**

---

Anti-Money Laundering systems watch transactions for suspicious patterns — structuring,
layering, rapid movement across borders. Risk scores compound across dimensions (amount,
geography, PEP status, transaction type). Here's the migration.

## The Pipeline: 6 Phases, 225 Records

Account holders → Accounts → Transactions (risk scoring) → Alerts (layering detection) → Dashboard → Compliance assertions.

## The Migration at Work

**Before — Benerator:**
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

**After — DataMimic (converter output):**
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

**Key mappings:** Sequential `<if>`→multiple `<condition>` elements, `||`→`or`, `&&`→`and`, `true`/`false`→`True`/`False`, self-referencing `risk` accumulation works in DataMimic's evaluation model.

**Execution:** An international wire of $43k → risk=55 (25+30) → SAR triggered. Cash deposit $41k → risk=65 (25+40) → SAR. Standard ACH transfers stay at risk=25, no SAR. Correct.

Full demo: `complex_fincrime.ben.xml`.

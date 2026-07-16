# From Benerator to DataMimic: Real Estate Portfolio Management

**July 2026 — Alexander Kell, rapiddweller**

---

Commercial real estate portfolio with investment grading, mortgage amortization,
and tenant lease management. Exercises CompanyNameGenerator, SectorGenerator,
and while-loop financial calculations.

## The Pipeline: 6 Phases, 175 Records

Firms → Properties (investment grading) → Mortgages (amortization) → Leases (renewal options) → Dashboard → Compliance.

## The Migration at Work

**Before — Benerator:**
```xml
<generate type="firm" count="6" consumer="store">
    <attribute name="name" generator="CompanyNameGenerator"/>
    <attribute name="sector" generator="SectorGenerator"/>
    <attribute name="department" generator="DepartmentNameGenerator"/>
    <attribute name="assetsUnderMgmt" type="double"
        min="50000000" max="50000000000" granularity="1000000"/>
</generate>

<generate type="property" count="30" consumer="store">
    <if test="yearBuilt &gt;= 2010 &amp;&amp; capRate &lt;= 5.5">
        <then><attribute name="investmentGrade" constant="CLASS_A"/></then>
        <else>
            <if test="yearBuilt &gt;= 2000 || capRate &lt;= 7.5">
                <then><attribute name="investmentGrade" constant="CLASS_B"/></then>
                <else>…</else>
            </if>
        </else>
    </if>
</generate>

<generate type="mortgage" count="20" consumer="store">
    <attribute name="remainingBalance" script="loanAmount"/>
    <while test="remainingBalance &gt; 0 &amp;&amp; monthsElapsed &lt; 12">
        <attribute name="remainingBalance"
            script="remainingBalance * (1 + interestRate/1200) - monthlyPayment"/>
        <attribute name="monthsElapsed" script="monthsElapsed + 1"/>
    </while>
</generate>
```

**After — DataMimic (converter output):**
```xml
<key generator="CompanyNameGenerator" name="name"/>
<key generator="SectorGenerator" name="sector"/>
<key generator="DepartmentNameGenerator" name="department"/>

<condition>
  <if condition="yearBuilt >= 2010 and capRate <= 5.5">
    <key constant="CLASS_A" name="investmentGrade"/>
  </if>
  <else>
    <condition>
      <if condition="yearBuilt >= 2000 or capRate <= 7.5">…

<while condition="remainingBalance > 0 and monthsElapsed < 12">
  <key name="remainingBalance"
      script="remainingBalance * (1 + interestRate/1200) - monthlyPayment"/>
</while>
```

**Key mappings:** `CompanyNameGenerator`/`SectorGenerator`/`DepartmentNameGenerator`→all native, `&&`→`and`, `||`→`or` in conditions, three-level nested `<if>`→nested `<condition>`, `<while test>`→`<while condition>`, `AddressGenerator`→`entity="Address"`.

**Execution:** 6 firms, 30 properties with Class A/B/C/D grading, 20 mortgages with 12-month amortization schedules, 60 tenant leases with renewal options. 0 manual findings. Full demo: `complex_realestate.ben.xml`.

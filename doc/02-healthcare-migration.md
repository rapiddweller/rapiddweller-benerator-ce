# From Benerator to DataMimic: Healthcare Claims Adjudication

**July 2026 — Alexander Kell, rapiddweller**

---

Medical claims processing is compliance-heavy and riddled with conditional logic.
Here's a claims pipeline migrating with **zero manual intervention**.

## The Pipeline: 7 Phases, 280 Records

Patients → Procedures → Providers → Claims (adjudication) → Payments → Dashboard → Compliance assertions.

## The Migration at Work

**Before — Benerator:**
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
</generate>
```

**After — DataMimic (converter output):**
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

**Key mappings:** `PersonGenerator`→`entity="Person"`, `values="X"^W`→`values`+`weights`, `<part>`→`<nestedKey type="list">`, `||`→`or`, `<if>/<then>/<else>`→`<condition>/<if>/<else>`, `CaseConverter`→`UpperCase`.

**Execution:** 0 manual findings, 0 runtime errors. Full demo: `complex_healthcare.ben.xml`.

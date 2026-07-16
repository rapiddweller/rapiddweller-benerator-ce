# From Benerator to DataMimic: Insurance Underwriting Engine

**July 2026 — Alexander Kell, rapiddweller**

---

Life/health insurance underwriting with multi-factor risk scoring, hash-based policy
identifiers, premium class assignment, and claims adjudication. This demo exercises
the converter's most advanced mappings — SHA256 hashes, entity constructor arguments,
and converter chains.

## The Pipeline: 7 Phases, 235 Records

Applicants → Policies (risk scoring) → Benefits → Claims → Amortization → Dashboard → Compliance.

## The Migration at Work

**Before — Benerator:**
```xml
<generate type="applicant" count="40" consumer="store">
    <variable name="person" generator="new PersonGenerator{
        minAgeYears='25', maxAgeYears='75',
        femaleQuota='0.48', nobleQuota='0.005',
        academicTitleQuota='0.25'}"/>
    <attribute name="ssn" generator="SSNGenerator" converter="Mask"/>
    <attribute name="academicTitle" generator="AcademicTitleGenerator"/>
    <attribute name="birthDate" generator="BirthdateGenerator"/>
</generate>

<generate type="policy" count="35" consumer="store">
    <attribute name="policyNumber" pattern="POL-[0-9]{4}-[A-Z]{4}"
        converter="SHA256Hash"/>
    <attribute name="displayId" script="policyNumber"
        converter="new SubstringExtractor(0,12)"/>
</generate>
```

**After — DataMimic (converter output):**
```xml
<variable entity="Person(min_age=25, max_age=75, female_quota=0.48,
    noble_quota=0.005, academic_title_quota=0.25)" name="person"/>
<key converter="Mask" generator="SSNGenerator" name="ssn"/>
<key generator="AcademicTitleGenerator" name="academicTitle"/>
<key generator="BirthdateGenerator" name="birthDate"/>

<key converter="Hash('sha256','hex')" name="policyNumber"
    pattern="POL-[0-9]{4}-[A-Z]{4}"/>
<key converter="Substring(0,12)" name="displayId" script="policyNumber"/>
```

**Key mappings:** `PersonGenerator{k=v}`→`entity="Person(kwarg=…)"`, `SHA256Hash`→`Hash('sha256','hex')`, `SHA1Hash`→`Hash('sha1','hex')`, `SubstringExtractor`→`Substring`, `SSNGenerator`→native, `Mask`→native, `AcademicTitleGenerator`/`NobilityTitleGenerator`/`BirthdateGenerator`→all native.

**Risk scoring:** Age (50+: +10, 65+: +20), BMI (30+: +15, 40+: +25), Smoker (+30), Occupation hazard (medium: +5, high: +15, extreme: +30), Family history (+10). Score ≥60 → DECLINED, ≥40 → HIGH_RISK, ≥20 → STANDARD, else PREFERRED.

**Execution:** 0 manual findings, 0 runtime errors. Risk scores correctly compound across all 5 dimensions. Full demo: `complex_insurance.ben.xml`.

# From Benerator to DataMimic: Supply Chain & Inventory Management

**July 2026 — Alexander Kell, rapiddweller**

---

Multi-warehouse inventory with FIFO rotation, restock alerts, hazmat classification,
and shipment tracking. A full logistics pipeline, migrated with zero manual work.

## The Pipeline: 7 Phases, 255 Records

Warehouses → Products → Inventory (restock logic) → Orders (FIFO) → Shipments (nested packages) → Dashboard → Compliance.

## The Migration at Work

**Before — Benerator:**
```xml
<generate type="inventory" count="80" consumer="store">
    <attribute name="quantity" type="int" min="0" max="500"/>
    <if test="quantity &lt; reorderPoint"><then>
        <attribute name="restockAlert" constant="true"/></then></if>
    <if test="prod.isHazmat == True &amp;&amp; prod.isPerishable == True">
        <then><attribute name="storageClass" constant="HAZMAT-REFRIGERATED"/></then>
        <else>
            <if test="prod.isHazmat == True">…</if>
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

**After — DataMimic (converter output):**
```xml
<condition>
  <if condition="quantity < reorderPoint">
    <key constant="true" name="restockAlert"/>
  </if>
</condition>
<!-- Nested hazmat+perishable → three-level condition chain -->
<nestedKey maxCount="4" minCount="1" name="packages" type="list">
  <key name="packageId" pattern="PKG-[0-9]{4}"/>
  <key name="isFragile" type="bool"/>
</nestedKey>
```

**Key mappings:** Nested `<if>/<else>/<if>`→nested `<condition>`, `<part minCount/maxCount>`→`<nestedKey type="list">`, `AddressGenerator`→`entity="Address"`, `type="boolean"`→`type="bool"`, order-based distribution for FIFO.

**Execution:** 8 warehouses, 40 products, 80 inventory records, 50 orders, 30 shipments. Tracking numbers generated, service levels correctly routed (express for >100kg or FedEx). Full demo: `complex_supplychain.ben.xml`.

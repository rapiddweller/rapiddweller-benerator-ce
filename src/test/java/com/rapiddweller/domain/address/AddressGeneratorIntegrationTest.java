package com.rapiddweller.domain.address;

import com.rapiddweller.benerator.test.AbstractBeneratorIntegrationTest;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.platform.memstore.MemStore;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Verifies the documented AddressGenerator state/city examples (doc/domains.md, Address domain) work
 * through the XML DSL -- i.e. that the {@code <property name="stateFilter"/>} / {@code cityFilter}
 * wiring reaches the generator and produces correlated, filtered addresses. Guards the documented
 * snippets the same way DataFakerIntegrationTest guards the faker example, across several countries.
 */
public class AddressGeneratorIntegrationTest extends AbstractBeneratorIntegrationTest {

    private static final String PREFIX = "com/rapiddweller/domain/address";

    /** US: Orlando, FL. */
    @Test
    public void testDocumentedStateCityExampleUS() {
        runAndAssert(PREFIX + "/address_state_city.ben.xml", "FL", "ORLANDO");
    }

    /** France as a benerator model: Paris, Île-de-France (numeric state id "11"). */
    @Test
    public void testStateCityExampleFrance() {
        runAndAssert(PREFIX + "/address_state_city_fr.ben.xml", "11", "PARIS");
    }

    private void runAndAssert(String benFile, String expectedState, String expectedCityUpperCase) {
        MemStore mem = new MemStore("mem", context.getDataModel());
        context.setGlobal("mem", mem);

        parseAndExecuteFile(benFile);

        Collection<Entity> rows = mem.getEntities("address");
        assertEquals(10, rows.size());
        for (Entity row : rows) {
            assertEquals(expectedState, row.get("state"));
            assertEquals(expectedCityUpperCase, String.valueOf(row.get("city")).toUpperCase());
            assertNotNull("zip should be generated", row.get("zip"));
        }
    }
}

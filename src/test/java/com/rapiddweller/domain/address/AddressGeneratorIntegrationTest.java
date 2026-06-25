package com.rapiddweller.domain.address;

import com.rapiddweller.benerator.test.AbstractBeneratorIntegrationTest;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.platform.memstore.MemStore;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Verifies the documented AddressGenerator state/city example (doc/domains.md, Address domain) works
 * through the XML DSL -- i.e. that the {@code <property name="state"/>} / {@code city} wiring reaches
 * the generator and produces correlated, filtered addresses. This guards the documented snippet the
 * same way DataFakerIntegrationTest guards the faker example.
 */
public class AddressGeneratorIntegrationTest extends AbstractBeneratorIntegrationTest {

    @Test
    public void testDocumentedStateCityExample() {
        MemStore mem = new MemStore("mem", context.getDataModel());
        context.setGlobal("mem", mem);

        parseAndExecuteFile("com/rapiddweller/domain/address/address_state_city.ben.xml");

        Collection<Entity> rows = mem.getEntities("address");
        assertEquals(10, rows.size());
        for (Entity row : rows) {
            assertEquals("FL", row.get("state"));
            assertEquals("ORLANDO", String.valueOf(row.get("city")).toUpperCase());
            assertNotNull("zip should be generated", row.get("zip"));
        }
    }
}

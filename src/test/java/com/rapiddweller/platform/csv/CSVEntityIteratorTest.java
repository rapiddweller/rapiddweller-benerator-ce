/*
 * (c) Copyright 2006-2020 by rapiddweller GmbH & Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General Public License.
 *
 * For redistributing this software or a derivative work under a license other
 * than the GPL-compatible Free Software License as defined by the Free
 * Software Foundation or approved by OSI, you must first obtain a commercial
 * license to this software product from rapiddweller GmbH & Volker Bergmann.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED CONDITIONS,
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE
 * HEREBY EXCLUDED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.rapiddweller.platform.csv;

import org.junit.Test;
import static org.junit.Assert.*;

import com.rapiddweller.common.Encodings;
import com.rapiddweller.common.converter.NoOpConverter;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.platform.AbstractEntityIteratorTest;

/**
 * Tests the {@link CSVEntityIterator}.<br/>
 * <br/>
 * Created: 07.04.2008 12:30:17
 * @since 0.5.1
 * @author Volker Bergmann
 */
public class CSVEntityIteratorTest extends AbstractEntityIteratorTest {

    private static final String PLAIN_URI = "com/rapiddweller/platform/csv/person-bean.csv";
    private static final String GRAPH_URI = "com/rapiddweller/platform/csv/person-graph.csv";

    // test methods ----------------------------------------------------------------------------------------------------

    @Test
    public void testWithHeader() throws Exception {
    	ComplexTypeDescriptor countryDescriptor = createCountryDescriptor();
    	ComplexTypeDescriptor personDescriptor = createPersonDescriptor(countryDescriptor);
    	CSVEntityIterator iterator = new CSVEntityIterator(PLAIN_URI, personDescriptor, new NoOpConverter<String>(), ',', Encodings.UTF_8);
        assertEquals(new Entity(personDescriptor, "name", "Alice", "age", 23), nextOf(iterator));
        assertEquals(new Entity(personDescriptor, "name", "Bob", "age", 34), nextOf(iterator));
        assertEquals(new Entity(personDescriptor, "name", "Charly", "age", 45), nextOf(iterator));
        assertUnavailable(iterator);
    }

    @Test
    public void testWithoutHeader() throws Exception {
    	ComplexTypeDescriptor countryDescriptor = createCountryDescriptor();
    	ComplexTypeDescriptor personDescriptor = createPersonDescriptor(countryDescriptor);
    	CSVEntityIterator iterator = new CSVEntityIterator(PLAIN_URI, personDescriptor, new NoOpConverter<String>(), ',', Encodings.UTF_8);
    	iterator.setColumns(new String[] { "c1", "c2" });
        assertEquals(new Entity(personDescriptor, "c1", "name", "c2", "age"), nextOf(iterator));
        assertEquals(new Entity(personDescriptor, "c1", "Alice", "c2", "23"), nextOf(iterator));
        assertEquals(new Entity(personDescriptor, "c1", "Bob", "c2", "34"), nextOf(iterator));
        assertEquals(new Entity(personDescriptor, "c1", "Charly", "c2", "45"), nextOf(iterator));
        assertUnavailable(iterator);
    }

    @Test
    public void testGraph() throws Exception {
    	ComplexTypeDescriptor countryDescriptor = createCountryDescriptor();
    	ComplexTypeDescriptor personDescriptor = createPersonDescriptor(countryDescriptor);
    	// Define expected countries
    	Entity germany = new Entity(countryDescriptor, "isoCode", "DE", "name", "Germany");
    	Entity usa = new Entity(countryDescriptor, "isoCode", "US", "name", "USA");
    	// Define expected persons
    	Entity alice = new Entity(personDescriptor, "name", "Alice", "age", 23, "country", germany);
    	Entity bob   = new Entity(personDescriptor, "name", "Bob", "age", 34, "country", usa);
    	// iterate CSV file and check iterator output 
    	CSVEntityIterator iterator = new CSVEntityIterator(GRAPH_URI, personDescriptor, null, ',', Encodings.UTF_8);
        assertEquals(alice, nextOf(iterator));
        assertEquals(bob, nextOf(iterator));
        assertUnavailable(iterator);
    }

	private ComplexTypeDescriptor createPersonDescriptor(ComplexTypeDescriptor countryDescriptor) {
		ComplexTypeDescriptor personDescriptor = createComplexType("Person");
    	personDescriptor.addComponent(createPart("name", "string"));
    	personDescriptor.addComponent(createPart("age", "int"));
    	personDescriptor.addComponent(createPart("country", countryDescriptor));
		return personDescriptor;
	}

    // private helpers -------------------------------------------------------------------------------------------------

	private ComplexTypeDescriptor createCountryDescriptor() {
		ComplexTypeDescriptor countryDescriptor = createComplexType("Country");
    	countryDescriptor.addComponent(createPart("isoCode", "string"));
    	countryDescriptor.addComponent(createPart("name", "string"));
		return countryDescriptor;
	}

}

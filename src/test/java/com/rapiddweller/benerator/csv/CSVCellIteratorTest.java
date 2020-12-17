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

package com.rapiddweller.benerator.csv;

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.IOException;

import com.rapiddweller.commons.Encodings;
import com.rapiddweller.formats.DataContainer;
import com.rapiddweller.formats.csv.CSVCellIterator;

/**
 * Tests the {@link CSVCellIterator}.<br/><br/>
 * Created: 11.10.2006 23:14:33
 * @since 0.1
 * @author Volker Bergmann
 */
public class CSVCellIteratorTest {

	@Test
    public void test() throws IOException {
        CSVCellIterator iterator = new CSVCellIterator("com/rapiddweller/csv/names-abc.csv", ',', Encodings.UTF_8);
        DataContainer<String> container = new DataContainer<String>();
        assertEquals("Alice",  iterator.next(container).getData());
        assertEquals("Bob",    iterator.next(container).getData());
        assertEquals("Charly", iterator.next(container).getData());
        assertNull(iterator.next(container));
    }
	
}

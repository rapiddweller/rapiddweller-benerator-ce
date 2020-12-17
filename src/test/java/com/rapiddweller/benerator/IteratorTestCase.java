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

package com.rapiddweller.benerator;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Parent class for {@link Iterator} test classes.<br/><br/>
 * @author Volker Bergmann
 */
public abstract class IteratorTestCase {

    public static <T> void checkUniqueIteration(Iterator<T> iterator, int count) {
        Set<T> items = new HashSet<>(count);
        for (int i = 0; i < count; i++) {
            assertTrue(iterator.hasNext());
            T item = iterator.next();
            assertFalse(items.contains(item)); // check uniqueness
            items.add(item);
        }
    }

	@SafeVarargs
	public static <T> NextHelper expectNextElements(Iterator<?> iterator, T... expectedValues) {
		for (T expected : expectedValues)
			assertNext(expected, iterator);
		return new NextHelper(iterator);
	}

	public static <T> void assertNext(T expectedNext, Iterator<?> iterator) {
		assertTrue("Iterator is expected to have a next, but does not", iterator.hasNext());
		Object actual = iterator.next();
		assertEquals(expectedNext, actual);
	}
	
	public static class NextHelper {
		
		Iterator<?> iterator;

		public NextHelper(Iterator<?> iterator) {
			this.iterator = iterator;
		}
		
		public void withNext() {
			assertTrue("Iterator is expected to have a next, but it does not", iterator.hasNext());
		}
		
		public void withNoNext() {
			assertFalse("Iterator is expected to have no next, but it has", iterator.hasNext());
		}
	}
	
}

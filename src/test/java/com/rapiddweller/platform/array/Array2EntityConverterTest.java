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

package com.rapiddweller.platform.array;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

import com.rapiddweller.benerator.test.ModelTest;
import com.rapiddweller.common.Escalation;
import com.rapiddweller.common.Escalator;
import com.rapiddweller.common.LoggerEscalator;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.model.data.ComplexTypeDescriptor;

/**
 * Tests the Array2EntityConverter.<br/>
 * <br/>
 * Created: 29.08.2007 19:09:05
 * @author Volker Bergmann
 */
public class Array2EntityConverterTest extends ModelTest {
	
	@Test
    public void testSimple() {
        ComplexTypeDescriptor descriptor = createComplexType("Person");
        Entity entity = new Entity(descriptor, "name", "Alice", "age", 23);
        String[] featureNames = { "name", "age" };
        Object[] array = new Object[] { "Alice", 23 };
        assertEquals(entity, new Array2EntityConverter(descriptor, featureNames, false).convert(array));
    }
	
	@Test
    public void testOverflow() {
        ComplexTypeDescriptor descriptor = createComplexType("Person");
        Entity entity = new Entity(descriptor, "name", "Alice", "age", 23);
        String[] featureNames = { "name", "age" };
        Object[] array = new Object[] { "Alice", 23, "superfluous" };
        Array2EntityConverter converter = new Array2EntityConverter(descriptor, featureNames, false);
        EscalatorMock escalator = new EscalatorMock();
        converter.escalator = escalator;
		assertEquals(entity, converter.convert(array));
		assertEquals(1, escalator.escalations.size());
    }
    
    public static final class EscalatorMock implements Escalator {
    	
    	private static LoggerEscalator loggerEscalator = new LoggerEscalator();

    	List<Escalation> escalations = new ArrayList<Escalation>();

		@Override
		public void escalate(String message, Object originator, Object cause) {
			escalations.add(new Escalation(message, originator, cause));
			loggerEscalator.escalate(message, originator, cause);
		}
    }
    
}

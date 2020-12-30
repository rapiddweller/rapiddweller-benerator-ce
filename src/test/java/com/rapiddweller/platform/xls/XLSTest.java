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

package com.rapiddweller.platform.xls;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import com.rapiddweller.benerator.IteratorTestCase;
import com.rapiddweller.common.TimeUtil;
import com.rapiddweller.format.DataContainer;
import com.rapiddweller.format.DataIterator;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.DataModel;
import com.rapiddweller.model.data.DefaultDescriptorProvider;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.model.data.PartDescriptor;

/**
 * Parent class for XLS-related tests.<br/>
 * <br/>
 * Created at 18.07.2009 23:45:02
 * @since 0.6.0
 * @author Volker Bergmann
 */

public abstract class XLSTest extends IteratorTestCase {

	protected static final String     EAN1   = "1234567890123";
    protected static final BigDecimal PRICE1 = new BigDecimal("123.234");
    protected static final Date		  DATE1 = TimeUtil.date(2009, 6, 18);
    protected static final Boolean	  AVAIL1 = true;
    protected static final Timestamp  UPDATED1 = TimeUtil.timestamp(2009, 6, 18, 0, 27, 38, 0);
    
    protected static final String     EAN2   = "9876543210987";
    protected static final BigDecimal PRICE2 = new BigDecimal("1.95");
    protected static final Date		  DATE2 = DATE1;
    protected static final Boolean	  AVAIL2 = false;
    protected static final Timestamp  UPDATED2 = TimeUtil.timestamp(2009, 6, 18, 0, 27, 38, 0);

    protected static final String PERSON1_NAME = "Alice";
    protected static final int PERSON1_AGE = 23;

    protected static final ComplexTypeDescriptor XYZ_DESCRIPTOR;
    
    protected static DefaultDescriptorProvider p = new DefaultDescriptorProvider("Test", new DataModel());
    
	static {
		XYZ_DESCRIPTOR = new ComplexTypeDescriptor("XYZ", p);
		XYZ_DESCRIPTOR.addComponent(new PartDescriptor("ean", p, "string"));
		XYZ_DESCRIPTOR.addComponent(new PartDescriptor("price", p, "big_decimal"));
		XYZ_DESCRIPTOR.addComponent(new PartDescriptor("date", p, "date"));
		XYZ_DESCRIPTOR.addComponent(new PartDescriptor("avail", p, "boolean"));
		XYZ_DESCRIPTOR.addComponent(new PartDescriptor("updated", p, "timestamp"));
	}
	
    protected static final ComplexTypeDescriptor PRODUCT_DESCRIPTOR;
	static {
		PRODUCT_DESCRIPTOR = new ComplexTypeDescriptor("Product", p);
		PRODUCT_DESCRIPTOR.addComponent(new PartDescriptor("ean", p, "string"));
		PRODUCT_DESCRIPTOR.addComponent(new PartDescriptor("price", p, "big_decimal"));
		PRODUCT_DESCRIPTOR.addComponent(new PartDescriptor("date", p, "date"));
		PRODUCT_DESCRIPTOR.addComponent(new PartDescriptor("avail", p, "boolean"));
		PRODUCT_DESCRIPTOR.addComponent(new PartDescriptor("updated", p, "timestamp"));
	}
	
	protected static final ComplexTypeDescriptor PERSON_DESCRIPTOR;
	static {
		PERSON_DESCRIPTOR = new ComplexTypeDescriptor("Person", p);
		PERSON_DESCRIPTOR.addComponent(new PartDescriptor("name", p, "string"));
		PERSON_DESCRIPTOR.addComponent(new PartDescriptor("age", p, "int"));
	}
	
	protected static final Entity PROD1 = new Entity(PRODUCT_DESCRIPTOR, 
			"ean", EAN1, 
			"price", PRICE1,
			"date", DATE1,
			"avail", AVAIL1,
			"updated", UPDATED1
	);
	
	protected static final Entity PROD2 = new Entity(PRODUCT_DESCRIPTOR, 
			"ean", EAN2, 
			"price", PRICE2,
			"date", DATE2,
			"avail", AVAIL2,
			"updated", UPDATED2
	);
	
	protected static final Entity PERSON1 = new Entity(PERSON_DESCRIPTOR, 
			"name", PERSON1_NAME, 
			"age", PERSON1_AGE
	);
	
	protected static final Entity XYZ11 = new Entity(XYZ_DESCRIPTOR, 
			"ean", EAN1, 
			"price", PRICE1,
			"date", DATE1,
			"avail", AVAIL1,
			"updated", UPDATED1
	);
	
	protected static final Entity XYZ12 = new Entity(XYZ_DESCRIPTOR, 
			"ean", EAN2, 
			"price", PRICE2,
			"date", DATE2,
			"avail", AVAIL2,
			"updated", UPDATED2
	);
	
	protected static final Entity XYZ21 = new Entity(XYZ_DESCRIPTOR, 
			"name", PERSON1_NAME, 
			"age", PERSON1_AGE
	);
	
	protected static void assertXYZ(Entity expected, Entity actual) {
		assertEquals("XYZ", actual.type());
		assertEquals(expected.getComponent("ean"), actual.getComponent("ean"));
		assertEquals(((Number) expected.getComponent("price")).doubleValue(), ((Number) actual.getComponent("price")).doubleValue(), 0.000001);
		assertEquals(expected.getComponent("date"), actual.getComponent("date"));
		assertEquals(expected.getComponent("avail"), actual.getComponent("avail"));
		assertEquals(((Date) expected.getComponent("updated")).getTime(), 
				((Date) actual.getComponent("updated")).getTime());
    }

	protected static void assertProduct(Entity expected, Entity actual) {
		assertEquals("Product", actual.type());
		assertEquals(expected.getComponent("ean"), actual.getComponent("ean"));
		assertEquals(((Number) expected.getComponent("price")).doubleValue(), ((Number) actual.getComponent("price")).doubleValue(), 0.000001);
		assertEquals(expected.getComponent("date"), actual.getComponent("date"));
		assertEquals(expected.getComponent("avail"), actual.getComponent("avail"));
		assertEquals(((Date) expected.getComponent("updated")).getTime(), 
				((Date) actual.getComponent("updated")).getTime());
    }

	protected static void assertPerson(Entity expected, Entity actual) {
		assertEquals("Person", actual.type());
		assertEquals(expected.get("name"), actual.get("name"));
		assertEquals(expected.get("age"), ((Number) actual.get("age")).intValue());
    }

	protected static void assertUnavailable(DataIterator<Entity> iterator) {
		assertNull(iterator.next(new DataContainer<Entity>()));
	}
    
}

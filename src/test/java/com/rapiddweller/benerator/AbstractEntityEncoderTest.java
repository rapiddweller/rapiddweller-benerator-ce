/* (c) Copyright 2022 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator;

import com.rapiddweller.common.Encodings;
import com.rapiddweller.model.data.DataModel;
import com.rapiddweller.model.data.DefaultDescriptorProvider;
import com.rapiddweller.model.data.DescriptorProvider;
import com.rapiddweller.model.data.Entity;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Tests the {@link AbstractEntityEncoder}.<br/><br/>
 * Created: 04.06.2022 14:08:06
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class AbstractEntityEncoderTest {

	@Test
	public void testGetSetEncoding() {
		AbstractEntityEncoder e = createEntityEncoder();
		assertEquals(Encodings.UTF_8, e.getEncoding());
		e.setEncoding(Encodings.ISO_8859_1);
		assertEquals(Encodings.ISO_8859_1, e.getEncoding());
	}

	@Test
	public void testEncodeAsString() {
		AbstractEntityEncoder e = createEntityEncoder();
		String s = e.encodeEntityAsString(createEntity());
		assertEquals("person[name=Otto]", s);
	}

	@Test
	public void testEncodeAsBytes() {
		AbstractEntityEncoder e = createEntityEncoder();
		byte[] actualBytes = e.encodeEntityAsBytes(createEntity());
		byte[] expectedBytes = new byte[] { 112, 101, 114, 115, 111, 110, 91, 110, 97, 109, 101,
			61, 79, 116, 116, 111, 93 };
		assertArrayEquals(expectedBytes, actualBytes);
	}

	private Entity createEntity() {
		DataModel model = new DataModel();
		DescriptorProvider provider = new DefaultDescriptorProvider("provider", model);
		return new Entity("person", provider, "name", "Otto");
	}

	private AbstractEntityEncoder createEntityEncoder() {
		return new AbstractEntityEncoder() {
			@Override
			public String encodeEntityAsString(Entity entity) {
				return entity.toString();
			}
		};
	}

}

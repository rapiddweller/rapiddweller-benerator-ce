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

package com.rapiddweller.benerator.primitive.number;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Test;

/**
 * Tests the {@link DecimalQuantizer}.<br/><br/>
 * Created: 11.04.2011 16:28:24
 * @since 0.6.6
 * @author Volker Bergmann
 */
public class DecimalQuantizerTest {
	
	@Test
	public void testPositiveMin() {
		DecimalQuantizer posQuantizer = new DecimalQuantizer(new BigDecimal("0.1"), new BigDecimal("0.2"));
		checkConversion( "0.1", "0.1", posQuantizer);
		checkConversion( "0.1", "0.2", posQuantizer);
		checkConversion( "0.3", "0.3", posQuantizer);
	}

	@Test
	public void testNegativeMin() {
		DecimalQuantizer posQuantizer = new DecimalQuantizer(new BigDecimal("-0.1"), new BigDecimal("0.2"));
		checkConversion("-0.1", "-0.1", posQuantizer);
		checkConversion("-0.1",  "0.0",  posQuantizer);
		checkConversion( "0.1",  "0.1", posQuantizer);
		checkConversion( "0.1",  "0.2", posQuantizer);
	}

	private static void checkConversion(String expectedResult, String sourceValue, DecimalQuantizer quantizer) {
		assertEquals(0, new BigDecimal(expectedResult).compareTo(quantizer.convert(new BigDecimal(sourceValue))));
	}
	
}

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

package com.rapiddweller.domain.address;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Tests the {@link PostalCodeValidator}.<br/><br/>
 * Created: 28.08.2010 15:43:51
 * @since 0.6.4
 * @author Volker Bergmann
 */
public class PostalCodeValidatorTest {

	@Test
	public void testGermany() {
		PostalCodeValidator validator = new PostalCodeValidator("DE");
		assertTrue(validator.valid("12345"));
		assertTrue(validator.valid("01234"));
		assertTrue(validator.valid("D12345"));
		assertTrue(validator.valid("D 12345"));
		assertTrue(validator.valid("DE12345"));
		assertTrue(validator.valid("DE 12345"));
		assertTrue(validator.valid("D-12345"));
		assertTrue(validator.valid("DE-12345"));
		assertFalse(validator.valid("123456"));
		assertFalse(validator.valid("1234"));
		assertFalse(validator.valid(""));
		assertFalse(validator.valid(null));
	}

	@Test
	public void testUS() {
		PostalCodeValidator validator = new PostalCodeValidator("US");
		assertTrue(validator.valid("12345"));
		assertTrue(validator.valid("01234"));
		assertFalse(validator.valid("123456"));
		assertFalse(validator.valid("1234"));
		assertFalse(validator.valid(""));
		assertFalse(validator.valid(null));
	}

}

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

package com.rapiddweller.domain.us;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests the {@link SSNValidator}.<br/>
 * <br/>
 * Created at 17.11.2008 07:32:50
 * @since 0.5.6
 * @author Volker Bergmann
 */

public class SSNValidator_USTest {
	
	private SSNValidator validator = new SSNValidator();

	@Test
	public void testInvalidNumbers() {
		assertFalse(validator.isValid(null, null));
		assertFalse(validator.isValid("ABC-65-4329", null));
		assertFalse(validator.isValid("001654329", null));
		assertFalse(validator.isValid("987-65-43292", null));
		assertFalse(validator.isValid("987-651-4329", null));
		assertFalse(validator.isValid("0001-65-432", null));
	}
	
	@Test
	public void testAdvertisementNumbers() {
		assertFalse(validator.isValid("987-65-4320", null));
		assertFalse(validator.isValid("987-65-4329", null));
	}
	
}

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

package com.rapiddweller.domain.product;

import com.rapiddweller.common.validator.SimpleValidatorTest;
import org.junit.Test;

/**
 * Tests the validation of EAN codes with the {@link EANValidator}.<br/>
 * <br/>
 * Created: 29.07.2007 08:04:09
 * @author Volker Bergmann
 */
public class EANValidatorTest extends SimpleValidatorTest<String> {

    public EANValidatorTest() {
	    super(new EANValidator());
    }

	private static final String EAN_VOLVIC           = "3057640182693";
    private static final String ISBN_ISM2            = "9783981304602";
    private static final String EAN_INVALID_CHECKSUM = "3057640182692";
    private static final String EAN_INVALID_LENGTH   = "3057640182";

    @Test
    public void testValidEAN() {
        assertValid(EAN_VOLVIC);
    }
    
    @Test
    public void testValidISBN() {
        assertValid(ISBN_ISM2);
    }
    
    @Test
    public void testIllegalValues() {
        assertInvalid(null);
        assertInvalid("");
    }
    
    @Test
    public void testInvalidChecksums() {
    	assertInvalid(EAN_INVALID_CHECKSUM);
    	assertInvalid(EAN_INVALID_LENGTH);
    }
    
}

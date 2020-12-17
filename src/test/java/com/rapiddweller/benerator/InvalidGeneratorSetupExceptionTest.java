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

import java.util.Arrays;

import com.rapiddweller.benerator.InvalidGeneratorSetupException;
import com.rapiddweller.benerator.PropertyMessage;
import com.rapiddweller.commons.ArrayUtil;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests the {@link InvalidGeneratorSetupException}.<br/><br/>
 * Created at 03.05.2008 12:31:14
 * @since 0.5.3
 * @author Volker Bergmann
 */
public class InvalidGeneratorSetupExceptionTest {
	
	@Test
	public void test() {
		PropertyMessage m1 = new PropertyMessage("p1", "is null");
		PropertyMessage m2 = new PropertyMessage("p2", "is too long");
		PropertyMessage[] mm = ArrayUtil.toArray(m1, m2);
		InvalidGeneratorSetupException e = new InvalidGeneratorSetupException(mm);
		assertEquals("'p1' is null, 'p2' is too long", e.getMessage());
		assertTrue(Arrays.equals(mm, e.getPropertyMessages()));
	}
	
}

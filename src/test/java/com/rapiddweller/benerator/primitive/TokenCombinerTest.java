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

package com.rapiddweller.benerator.primitive;

import com.rapiddweller.benerator.test.GeneratorTest;
import com.rapiddweller.common.Validator;
import com.rapiddweller.common.validator.UniqueValidator;
import org.junit.Test;

/**
 * Tests the {@link TokenCombiner}.<br/><br/>
 * Created: 01.08.2010 15:10:43
 * @since 0.6.3
 * @author Volker Bergmann
 */
public class TokenCombinerTest extends GeneratorTest {

	@Test
	public void testNonUnique() {
		TokenCombiner combinator = createCombinator(false, false);
		expectGenerations(combinator, 100, new CombinationValidator(false)).withContinuedAvailability();
	}

	@Test
	public void testNonUniqueSeedExcluded() {
		TokenCombiner combinator = createCombinator(false, true);
		expectGenerations(combinator, 100, new CombinationValidator(true)).withContinuedAvailability();
	}
	
	@Test
	public void testUnique() {
		TokenCombiner combinator = createCombinator(true, false);
		expectGenerations(combinator, 9, new CombinationValidator(false), new UniqueValidator<String>()).withCeasedAvailability();
	}
	
	@Test
	public void testUniqueSeedExcluded() {
		TokenCombiner combinator = createCombinator(true, true);
		expectGenerations(combinator, 6, new CombinationValidator(true), new UniqueValidator<String>()).withCeasedAvailability();
	}
	
    public static class CombinationValidator implements Validator<String> {
    	
    	final boolean excludeSeed;

    	public CombinationValidator(boolean excludeSeed) {
	        this.excludeSeed = excludeSeed;
        }

		@Override
		public boolean valid(String value) {
	    	if (value == null || value.length() != 2)
	    		return false;
	    	char c0 = value.charAt(0);
	    	if (c0 < 'A' || c0 > 'C')
	    		return false;
	    	char c1 = value.charAt(1);
	    	if (c1 < 'a' || c1 > 'c')
	    		return false;
	    	if (excludeSeed && Character.toLowerCase(c0) == c1)
	    		return false;
	    	return true;
	    }

    }

	private TokenCombiner createCombinator(boolean unique, boolean excludeSeed) {
	    TokenCombiner combinator = new TokenCombiner("com/rapiddweller/benerator/primitive/TokenCombinerTest.csv");
		combinator.setSeparator('|');
		combinator.setUnique(unique);
		combinator.setExcludeSeed(excludeSeed);
		combinator.init(context);
	    return combinator;
    }
	
}

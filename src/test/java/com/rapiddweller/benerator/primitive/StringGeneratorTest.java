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

import static org.junit.Assert.*;

import java.util.Locale;
import java.util.Set;

import com.rapiddweller.benerator.distribution.SequenceManager;
import com.rapiddweller.benerator.test.GeneratorTest;
import com.rapiddweller.commons.CollectionUtil;
import com.rapiddweller.commons.Validator;
import com.rapiddweller.commons.validator.PrefixValidator;
import com.rapiddweller.commons.validator.RegexValidator;
import com.rapiddweller.commons.validator.StringLengthValidator;
import com.rapiddweller.commons.validator.SuffixValidator;
import org.junit.Test;

/**
 * Tests the {@link StringGenerator}.<br/><br/>
 * Created: 01.08.2011 20:15:04
 * @since 0.7.0
 * @author Volker Bergmann
 */
public class StringGeneratorTest extends GeneratorTest {

	public static final int N = 500;
	
	@Test
	public void testDefault() {
		StringGenerator generator = initialize(new StringGenerator());
		expectGenerations(generator, N, 
				new StringLengthValidator(1, 8),
				new RegexValidator("\\w{1,8}"));
	}
	
	@Test
	public void testPattern() {
		StringGenerator generator = new StringGenerator();
		generator.setCharSet("[AB]");
		generator.setMinLength(2);
		generator.setMaxLength(3);
		initialize(generator);
		expectGeneratedSet(generator, N, 
				"AA", "AB", "BA", "BB", 
				"AAA", "AAB", "ABA", "ABB", "BAA", "BAB", "BBA", "BBB");
	}
	
	@Test
	public void testPrefix() {
		StringGenerator generator = new StringGenerator();
		generator.setPrefix("pp");
		generator.setMinLength(4);
		generator.setMaxLength(8);
		initialize(generator);
		expectGenerations(generator, N, 
				new StringLengthValidator(4, 8),
				new PrefixValidator("pp"));
	}
	
	@Test
	public void testSuffix() {
		StringGenerator generator = new StringGenerator();
		generator.setSuffix("ss");
		generator.setMinLength(4);
		generator.setMaxLength(8);
		initialize(generator);
		expectGenerations(generator, N, 
				new StringLengthValidator(4, 8),
				new SuffixValidator("ss"));
	}
	
	@Test
	public void testPrefixAndSuffix() {
		StringGenerator generator = new StringGenerator();
		generator.setPrefix("pp");
		generator.setSuffix("ss");
		generator.setMinLength(4);
		generator.setMaxLength(8);
		initialize(generator);
		expectGenerations(generator, N, 
				new StringLengthValidator(4, 8),
				new PrefixValidator("pp"),
				new SuffixValidator("ss"));
	}
	
	@Test
	public void testMinInitial() {
		StringGenerator generator = new StringGenerator();
		generator.setCharSet("[0-9]");
		generator.setMinInitial('9');
		generator.setMinLength(4);
		generator.setMaxLength(8);
		initialize(generator);
		expectGenerations(generator, N, 
				new StringLengthValidator(4, 8),
				new PrefixValidator("9"));
	}
	
	@Test
	public void testPrefixAndMinInitial() {
		StringGenerator generator = new StringGenerator();
		generator.setPrefix("pp");
		generator.setCharSet("[0-9]");
		generator.setMinInitial('9');
		generator.setMinLength(4);
		generator.setMaxLength(8);
		initialize(generator);
		expectGenerations(generator, N, 
				new StringLengthValidator(4, 8),
				new PrefixValidator("pp9"));
	}
	
	@Test
	public void testMinInitialAndSuffix() {
		StringGenerator generator = new StringGenerator();
		generator.setSuffix("ss");
		generator.setCharSet("[0-9]");
		generator.setMinInitial('9');
		generator.setMinLength(4);
		generator.setMaxLength(8);
		initialize(generator);
		expectGenerations(generator, N, 
				new StringLengthValidator(4, 8),
				new PrefixValidator("9"),
				new SuffixValidator("ss"));
	}
	
	@Test
	public void testPrefixMinInitialAndSuffix() {
		StringGenerator generator = new StringGenerator();
		generator.setPrefix("pp");
		generator.setSuffix("ss");
		generator.setCharSet("[0-9]");
		generator.setMinInitial('9');
		generator.setMinLength(5);
		generator.setMaxLength(8);
		initialize(generator);
		expectGenerations(generator, N, 
				new StringLengthValidator(5, 8),
				new PrefixValidator("pp9"),
				new SuffixValidator("ss"));
	}
	
	@Test
	public void testGermanLocale() {
		StringGenerator generator = new StringGenerator();
		generator.setCharSet("[\\w^A-Za-z0-9_]");
		generator.setLocale(Locale.GERMAN);
		generator.setMinLength(5);
		generator.setMaxLength(8);
		initialize(generator);
		expectGenerations(generator, N, 
				new StringLengthValidator(5, 8),
				new UmlautValidator());
	}
	
	@Test
	public void testLengthLimit_nonUnique() {
		StringGenerator generator = new StringGenerator();
		generator.setCharSet("[AB]");
		generator.setMinLength(5);
		generator.setMaxLength(1000);
		initialize(generator);
		expectGenerations(generator, 100, new StringLengthValidator(5, 1000));
	}
	
	@Test
	public void testLengthLimit_unique() {
		StringGenerator generator = new StringGenerator();
		generator.setCharSet("[AB]");
		generator.setMinLength(5);
		generator.setMaxLength(1000);
		generator.setOrdered(false);
		generator.setUnique(true);
		initialize(generator);
		expectGenerations(generator, 100, new StringLengthValidator(5, 1000));
	}
	
	@Test
	public void testLengthLimit_ordered() {
		StringGenerator generator = new StringGenerator();
		generator.setCharSet("[AB]");
		generator.setMinLength(5);
		generator.setMaxLength(1000);
		generator.setOrdered(true);
		generator.setUnique(true);
		initialize(generator);
		expectGenerations(generator, 100, new StringLengthValidator(5, 1000));
	}
	
	@Test
	public void testLengthGranularity() {
		StringGenerator generator = new StringGenerator();
		generator.setCharSet("[AB]");
		generator.setMinLength(500);
		generator.setMaxLength(1000);
		generator.setLengthGranularity(250);
		generator.setOrdered(true);
		generator.setUnique(true);
		initialize(generator);
		expectGenerations(generator, 100, new LengthsValidator());
	}
	
	@Test
	public void testLengthDistribution() {
		StringGenerator generator = new StringGenerator();
		generator.setCharSet("x");
		generator.setMinLength(1);
		generator.setMaxLength(5);
		generator.setLengthGranularity(2);
		generator.setLengthDistribution(SequenceManager.STEP_SEQUENCE);
		generator.setOrdered(true);
		generator.setUnique(true);
		initialize(generator);
		assertEquals("x", generator.generate());
		assertEquals("xxx", generator.generate());
		assertEquals("xxxxx", generator.generate());
		assertUnavailable(generator);
		generator.reset();
		assertEquals("x", generator.generate());
		assertEquals("xxx", generator.generate());
		assertEquals("xxxxx", generator.generate());
		assertUnavailable(generator);
		generator.close();
	}
	
	public void testNonUnique() {
		StringGenerator generator = new StringGenerator();
		generator.setCharSet("[AB]");
		generator.setMinLength(2);
		generator.setMaxLength(3);
		generator.setUnique(false);
		generator.setOrdered(false);
		initialize(generator);
		expectGeneratedSet(generator, 100, 
				"AA", "AB", "BA", "BB", 
				"AAA", "AAB", "ABA", "ABB", "BAA", "BAB", "BBA", "BBB");
	}
	
	@Test
	public void testUniqueUnordered() {
		StringGenerator generator = new StringGenerator();
		generator.setCharSet("[AB]");
		generator.setMinLength(2);
		generator.setMaxLength(3);
		generator.setUnique(true);
		generator.setOrdered(false);
		initialize(generator);
		expectUniquelyGeneratedSet(generator, 
				"AA", "AB", "BA", "BB", 
				"AAA", "AAB", "ABA", "ABB", "BAA", "BAB", "BBA", "BBB");
	}
	
	@Test
	public void testUniqueOrdered() {
		StringGenerator generator = new StringGenerator();
		generator.setCharSet("[AB]");
		generator.setMinLength(2);
		generator.setMaxLength(3);
		generator.setUnique(true);
		generator.setOrdered(true);
		initialize(generator);
		expectGeneratedSequence(generator, 
				"AA", "AB", "BA", "BB", 
				"AAA", "AAB", "ABA", "ABB", "BAA", "BAB", "BBA", "BBB");
	}
	
	public class UmlautValidator implements Validator<String> {
		private Set<Character> allowedValues = CollectionUtil.toSet('Ä', 'ä', 'Ö', 'ö', 'Ü', 'ü', 'ß');
		@Override
		public boolean valid(String value) {
			for (int i = 0; i < value.length(); i++)
				if (!allowedValues.contains(value.charAt(i)))
					return false;
			return true;
		}
	}

	public class LengthsValidator implements Validator<String> {
		@Override
		public boolean valid(String value) {
			return value.length() == 500 || value.length() == 750 || value.length() == 1000;
		}

	}

}

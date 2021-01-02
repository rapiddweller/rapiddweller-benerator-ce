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

package com.rapiddweller.benerator.factory;

import java.util.regex.Pattern;
import java.util.Locale;

import com.rapiddweller.benerator.NonNullGenerator;
import com.rapiddweller.benerator.test.GeneratorTest;
import com.rapiddweller.common.LocaleUtil;
import com.rapiddweller.model.data.Uniqueness;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests the behavior of the {@link RegexGeneratorFactory}
 * when using the {@link StochasticGeneratorFactory}.<br/>
 * <br/>
 * Created: 20.08.2006 09:21:19
 * @since 0.1
 * @author Volker Bergmann
 */
public class RegexStringGeneratorFactory_stocasticTest extends GeneratorTest {

    private static final Logger logger = LogManager.getLogger(RegexStringGeneratorFactory_stocasticTest.class);

    private static Locale realLocale;
    
    @BeforeClass
    public static void setUpFallbackLocale() {
    	realLocale = Locale.getDefault();
        Locale.setDefault(LocaleUtil.getFallbackLocale());
    }
    
    @AfterClass
    public static void restoreRealLocale() {
    	Locale.setDefault(realLocale);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullPattern() throws Exception {
        checkRegexGeneration(null);
    }

    @Test
    public void testEmptyPattern() throws Exception {
        checkRegexGeneration("");
    }

    @Test
    public void testConstant() {
        assertEquals("a", create("a").generate());
        assertEquals("ab", create("ab").generate());
        assertEquals("abc@xyz.com", create("abc@xyz\\.com", 16, Uniqueness.NONE).generate());
    }

    @Test
    public void testEscapeCharacters() {
        assertEquals("-+*.?,&^$\\|", create("\\-\\+\\*\\.\\?\\,\\&\\^\\$\\\\\\|").generate());
        assertEquals("()", create("\\(\\)").generate());
        assertEquals("[]", create("\\[\\]").generate());
        assertEquals("{}", create("\\{\\}").generate());
    }
    
    @Test
    public void testStartEndCharacters() {
        assertEquals("ABC", create("^ABC$").generate());
    }

    @Test
    public void testCardinalities() throws Exception {
        checkRegexGeneration("a");
        checkRegexGeneration("a?");
        checkRegexGeneration("a*");
        checkRegexGeneration("a+");
        checkRegexGeneration("[a]");
        checkRegexGeneration("a{3}");
        checkRegexGeneration("a{3,}");
        checkRegexGeneration("a{3,5}");
    }

    @Test
    public void testRanges() throws Exception {
        checkRegexGeneration("[a-c]");
        checkRegexGeneration("[a-cA-C]");

    }

    @Test
    public void testPredefinedClasses() throws Exception {
        checkRegexGeneration("\\d");
        checkRegexGeneration("\\s");
        checkRegexGeneration("\\w");
    }

    @Test
    public void testCombinations() throws Exception {
        checkRegexGeneration("[^\\w]");
        checkRegexGeneration("[^0-1]");

        checkRegexGeneration("\\w+\\d+");
        checkRegexGeneration("[BL][au]");
        checkRegexGeneration("[1-9]\\d{0,3}");
        checkRegexGeneration("[F-H][aeiou]{2}");

        checkRegexGeneration("\\+[1-9]\\d{1,2}/\\d{1,5}/\\d{5,8}");
        checkRegexGeneration("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}");
    }

    @Test
    public void testGroups() throws Exception {
        checkRegexGeneration("(abc){1,3}");
        checkRegexGeneration("(a+b?c*){1,3}");
    }

    @Test
    public void testAlternatives() throws Exception {
        checkRegexGeneration("(a|b|c){1,3}");
        String byteValuePattern = "[1-9]?[0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]";
        String ipAddressPattern = "(" + byteValuePattern + "\\.){3}" + byteValuePattern;
		checkRegexGeneration(ipAddressPattern);
    }

    @Test
    public void testUniqueCharSets() {
        expectUniquelyGeneratedSet(create("[a]{1,2}", 30, Uniqueness.SIMPLE),
                "a", "aa").withCeasedAvailability();
        expectUniquelyGeneratedSet(create("[ab]{3}", 30, Uniqueness.SIMPLE),
                "aaa", "aab", "aba", "abb", "baa", "bab", "bba", "bbb").withCeasedAvailability();
        expectUniquelyGeneratedSet(create("x[ab]{3}x", 30, Uniqueness.SIMPLE),
                "xaaax", "xaabx", "xabax", "xabbx", "xbaax", "xbabx", "xbbax", "xbbbx").withCeasedAvailability();
        expectUniqueProducts(create("[01]{2}/[01]{2}", 30, Uniqueness.SIMPLE), 16).withCeasedAvailability();
        expectUniquelyGeneratedSet(create("[01]{2,3}", 30, Uniqueness.SIMPLE),
                "00", "01", "10", "11", "000", "001", "010", "011", "100", "101", "110", "111").withCeasedAvailability();
        expectUniqueProducts(create("0[0-9]{2,4}/[1-9][0-9]5", 30, Uniqueness.SIMPLE), 5).withContinuedAvailability();
        expectUniqueProducts(create("[0-9]{5}", 0, Uniqueness.SIMPLE), 1000).withContinuedAvailability();
    }

    @Test
    public void testUniqueGroups() {
        expectUniquelyGeneratedSet(create("x(ab){1,2}x", 30, Uniqueness.SIMPLE), "xabx", "xababx").withCeasedAvailability();
        expectUniquelyGeneratedSet(create("x(a[01]{2}){1,2}x", 30, Uniqueness.SIMPLE),
                "xa00x", "xa01x", "xa10x", "xa11x",
                "xa00a00x", "xa01a00x", "xa10a00x", "xa11a00x",
                "xa00a01x", "xa01a01x", "xa10a01x", "xa11a01x",
                "xa00a10x", "xa01a10x", "xa10a10x", "xa11a10x",
                "xa00a11x", "xa01a11x", "xa10a11x", "xa11a11x"
        ).withCeasedAvailability();
    }
    
    @Test
    public void testUniqueAlternatives() {
        expectUniquelyGeneratedSet(create("x(a|b)x", 30, Uniqueness.SIMPLE), "xax", "xbx").withCeasedAvailability();
        expectUniquelyGeneratedSet(create("x(a|b){2}x", 30, Uniqueness.SIMPLE), "xaax", "xabx", "xbax", "xbbx").withCeasedAvailability();
        expectUniquelyGeneratedSet(create("x(a|b){1,2}x", 30, Uniqueness.SIMPLE), "xax", "xbx", "xaax", "xabx", "xbax", "xbbx").withCeasedAvailability();
        expectUniquelyGeneratedSet(create("([a]{1,2}|b)", 30, Uniqueness.SIMPLE), "a", "aa", "b").withCeasedAvailability();
        expectUniquelyGeneratedSet(create("x([01]{1,2}|b)x", 30, Uniqueness.SIMPLE),
                "x0x", "x1x", "x00x", "x01x", "x10x", "x11x", "xbx").withCeasedAvailability();
    }
    
    @Test
    public void testAlternativesBugOfAdgadg() {
    	// Tests a bug posted by adgadg at 2009-07-18, see http://databene.org/phpBB3/viewtopic.php?f=3&t=110
    	String pattern = "(0[1239] [1-9]([0-9]{2}) ([0-9]{2} ){2})|(0[1-9][0-9] ([0-9]{2} ){3})";
    	NonNullGenerator<String> generator = create(pattern);
    	boolean alt1 = false;
    	boolean alt2 = false;
    	for (int i = 0; i < 20; i++) {
    		String product = generator.generate();
    		switch (product.indexOf(' ')) {
	    		case 2: alt1 = true; break;
	    		case 3: alt2 = true; break;
	    		default: fail("Regex generation failed for " + pattern);
    		}
			logger.debug(product);
    	}
    	assertTrue("Regex generator does not use alternative #1 of " + pattern, alt1);
    	assertTrue("Regex generator does not use alternative #2 of " + pattern, alt2);
    }

    // helpers ---------------------------------------------------------------------------------------------------------

    private void checkRegexGeneration(String regex) {
        logger.debug("checking generation for regex '" + regex + "'");
        NonNullGenerator<String> generator = create(regex);
        checkRegexGeneration(generator, regex, 0, 255, true);
    }

    public static void checkRegexGeneration(NonNullGenerator<String> generator, String regex, int minLength, Integer maxLength, boolean nullable) {
        String message = "Generation failed for: '" + regex + "': ";
        for (int i = 0; i < 20; i++) {
            String output = generator.generate();
            logger.debug("checking sample '" + output + "' against regex '" + regex + "'");
            if (!nullable)
                assertNotNull(output);
            if (regex == null)
                assertNull(output);
            else {
                if (regex.length() == 0)
                    assertEquals("", output);
                else
                    assertTrue(message + output, Pattern.matches(regex, output));
                assertTrue(output.length() >= minLength);
                if (maxLength != null)
                	assertTrue(output.length() <= maxLength);
            }
        }
    }
    
    private NonNullGenerator<String> create(String pattern) {
    	NonNullGenerator<String> generator = RegexGeneratorFactory.create(pattern, new StochasticGeneratorFactory());
    	generator.init(context);
		return generator;
    }
    
    private NonNullGenerator<String> create(String pattern, int maxLimit, Uniqueness uniqueness) {
    	NonNullGenerator<String> generator = RegexGeneratorFactory.create(pattern, 0, null, uniqueness, 
    			new StochasticGeneratorFactory());
    	generator.init(context);
		return generator;
    }
    
}

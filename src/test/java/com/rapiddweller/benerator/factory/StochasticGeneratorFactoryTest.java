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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import com.rapiddweller.benerator.sample.AttachedWeightSampleGenerator;
import com.rapiddweller.benerator.test.GeneratorTest;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.benerator.distribution.Sequence;
import com.rapiddweller.benerator.distribution.SequenceManager;
import com.rapiddweller.benerator.distribution.WeightFunction;
import com.rapiddweller.benerator.distribution.function.ConstantFunction;
import com.rapiddweller.benerator.distribution.function.GaussianFunction;
import com.rapiddweller.benerator.distribution.sequence.HeadSequence;
import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.NonNullGenerator;
import com.rapiddweller.common.*;
import com.rapiddweller.model.data.Uniqueness;
import com.rapiddweller.script.WeightedSample;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests the {@link StochasticGeneratorFactory}.<br/><br/>
 * Created: 24.08.2006 07:03:03
 * @since 0.1
 * @author Volker Bergmann
 */
public class StochasticGeneratorFactoryTest extends GeneratorTest {

    private static Logger logger = LogManager.getLogger(StochasticGeneratorFactoryTest.class);
    
    private GeneratorFactory generatorFactory = new StochasticGeneratorFactory();

    // boolean source -----------------------------------------------------------------------------------------------

    public void testGetBooleanGenerator() {
        initAndUseGenerator(generatorFactory.createBooleanGenerator(0.5));
    }

    // number generators -----------------------------------------------------------------------------------------------

    @Test
    public void testGetNumberGenerator() {
        checkNumberGenerator(Byte.class, (byte) -10, (byte)10, (byte)1);
        checkNumberGenerator(Short.class, (short)-10, (short)10, (short)1);
        checkNumberGenerator(Integer.class, -10, 10, 1);
        checkNumberGenerator(Long.class, (long) -10, (long)10, (long)1);
        checkNumberGenerator(BigInteger.class, new BigInteger("-10"), new BigInteger("10"), new BigInteger("1"));
        checkNumberGenerator(Double.class, (double) -10, (double)10, (double)1);
        checkNumberGenerator(Float.class, (float) -10, (float)10, (float)1);
        checkNumberGenerator(BigDecimal.class, new BigDecimal(-10), new BigDecimal(10), new BigDecimal(1));
    }

    @Test
    public void testGetNumberGeneratorWithoutMax() {
        checkNumberGenerator(Byte.class, (byte) -10, null, (byte) 1);
        checkNumberGenerator(Short.class, (short) -10, null, (short) 1);
        checkNumberGenerator(Integer.class, -10, null, 1);
        checkNumberGenerator(Long.class, (long) -10, null, (long) 1);
        checkNumberGenerator(BigInteger.class, new BigInteger("-10"), null, new BigInteger("1"));
        checkNumberGenerator(Double.class, (double) -10, null, (double) 1);
        checkNumberGenerator(Float.class, (float) -10, null, (float) 1);
        checkNumberGenerator(BigDecimal.class, new BigDecimal(-10), null, new BigDecimal(1));
    }


    private <T extends Number> void checkNumberGenerator(Class<T> type, T min, T max, T granularity) {
        for (Sequence sequence : SequenceManager.registeredSequences())
        	if (!(sequence instanceof HeadSequence))
        		checkNumberGenerator(type, min, max, granularity, sequence);
        if (max != null)
	        for (WeightFunction function : getDistributionFunctions(min.doubleValue(), max.doubleValue()))
	            checkNumberGenerator(type, min, max, granularity, function);
    }
    
    private <T extends Number> void checkNumberGenerator(Class<T> type, T min, T max, T granularity, Sequence sequence) {
        Generator<T> generator = generatorFactory.createNumberGenerator(type, min, true, max, true, granularity, sequence, Uniqueness.NONE);
        generator.init(context);
        ProductWrapper<T> wrapper = new ProductWrapper<T>();
        for (int i = 0; i < 5; i++) {
            T n = generator.generate(wrapper).unwrap();
            assertNotNull("Generator not available: " + generator, n);
            if (min != null)
            	assertTrue("Generated value (" + n + ") is smaller than min (" + min + ") using sequence '" + sequence + "'", 
            		n.doubleValue() >= min.doubleValue());
            if (max != null)
            	assertTrue(n.doubleValue() <= max.doubleValue());
        }
    }

    private <T extends Number> void checkNumberGenerator(Class<T> type, T min, T max, T granularity, WeightFunction weightFunction) {
        Generator<T> generator = generatorFactory.createNumberGenerator(type, min, true, max, true, granularity, weightFunction, Uniqueness.NONE);
        generator.init(context);
        int range = (int)((max.doubleValue() - min.doubleValue() + granularity.doubleValue()) / granularity.doubleValue());
        int[] count = new int[range];
        ProductWrapper<T> wrapper = new ProductWrapper<T>();
        for (int i = 0; i < 1000; i++) {
            T n = generator.generate(wrapper).unwrap();
            double d = n.doubleValue();
            assertTrue(d >= min.doubleValue());
            assertTrue(d <= max.doubleValue());
            int index = (int)((d - min.doubleValue()) / granularity.doubleValue());
            count[index]++;
        }
        logger.debug(weightFunction + ": " + ArrayFormat.formatInts(", ", count));
    }

    // sample source ------------------------------------------------------------------------------------------------

    @Test
    public void testGetSampleGenerator() {
        List<Integer> samples = new ArrayList<Integer>();
        for (int i = 0; i < 10; i++)
            samples.add(i);
        Generator<Integer> generator = generatorFactory.createSampleGenerator(samples, Integer.class, false);
        initAndUseGenerator(generator);
    }

    public void testGetEmptySampleGenerator() {
        Generator<Integer> generator = generatorFactory.createSampleGenerator(new HashSet<Integer>(), Integer.class, false);
		generator.init(context);
		assertNull(generator.generate(new ProductWrapper<Integer>()));
    }

    // date source --------------------------------------------------------------------------------------------------

    @Test
    public void testGetDateGeneratorByDistributionType() {
        for (Sequence sequence : SequenceManager.registeredSequences())
            generatorFactory.createDateGenerator(date(2006, 0, 1), date(2006, 11, 31), Period.DAY.getMillis(), sequence);
    }

    @Test
    public void testGetDateGeneratorByDistributionFunction() {
        Date min = date(2006, 0, 1);
        Date max = date(2006, 11, 31);
        for (WeightFunction distributionFunction : getDistributionFunctions(min.getTime(), max.getTime())) {
            Generator<Date> generator = generatorFactory.createDateGenerator(min, max, Period.DAY.getMillis(), distributionFunction);
            initAndUseGenerator(generator);
        }
    }

    private static Date date(int year, int zeroBasedMonth, int day) {
        return new GregorianCalendar(year, zeroBasedMonth, day).getTime();
    }

    // text source --------------------------------------------------------------------------------------------------

    @Test
    public void testGetCharacterGeneratorByLocale() {
        checkCharacterGeneratorOfLocale(Locale.GERMANY);
        checkCharacterGeneratorOfLocale(Locale.UK);
        checkCharacterGeneratorOfLocale(Locale.US);
        checkCharacterGeneratorOfLocale(new Locale("de", "ch"));
        checkCharacterGeneratorOfLocale(new Locale("de", "at"));
        checkCharacterGeneratorOfLocale(new Locale("fr", "ch"));
        checkCharacterGeneratorOfLocale(new Locale("it", "ch"));
        checkCharacterGeneratorOfLocale(Locale.GERMANY);
    }

    private void checkCharacterGeneratorOfLocale(Locale locale) {
        Generator<Character> generator = generatorFactory.createCharacterGenerator(null, locale, false);
        generator.init(context);
        List<Character> specialChars;
        specialChars = new ArrayList<Character>(LocaleUtil.letters(locale));
        int[] specialCount = new int[specialChars.size()];
        ProductWrapper<Character> wrapper = new ProductWrapper<Character>();
        for (int i = 0; i < 1000; i++) {
            Character c = generator.generate(wrapper).unwrap();
            int index = specialChars.indexOf(c);
            if (index >= 0)
                specialCount[index]++;
        }
        for (int i = 0; i < specialCount.length; i++)
            assertTrue("Character '" + specialChars.get(i) + "' not found in products for " + locale,
                    specialCount[i] > 0);
    }

    @Test
    public void testGetCharacterGeneratorByRegex() {
        String pattern = "[A-Za-z0-1]";
        Generator<Character> generator = generatorFactory.createCharacterGenerator(pattern, Locale.GERMAN, false);
        initAndUseGenerator(generator);
    }

    @Test
    public void testGetCharacterGeneratorBySet() {
        Set<Character> set = new CharSet('A', 'Z').getSet();
        Generator<Character> generator = generatorFactory.createCharacterGenerator(set);
        initAndUseGenerator(generator);
    }

    @Test
    public void testGetRegexGenerator() {
//      checkRegexGenerator(null, 0, 0, true);
//      checkRegexGenerator("", 0, 0, false);
        checkRegexGeneration("[1-9]\\d{0,3}", 1, 4, false);
        checkRegexGeneration("12345678901234567890123456789012", 1, null, false);
  }

    @Test
    public void testGetUniqueRegexGenerator() {
      Generator<String> generator = generatorFactory.createRegexStringGenerator("[0-9]{3}", 3, 3, Uniqueness.SIMPLE);
      generator.init(context);
      expectUniqueGenerations(generator, 1000).withCeasedAvailability();
  }

    private void checkRegexGeneration(String pattern, int minLength, Integer maxLength, boolean nullable) {
    	NonNullGenerator<String> generator = generatorFactory.createRegexStringGenerator(pattern, minLength, maxLength, Uniqueness.NONE);
        generator.init(context);
        RegexStringGeneratorFactory_stocasticTest.checkRegexGeneration(generator, pattern, minLength, maxLength, nullable);
    }

    // weighted sample source ---------------------------------------------------------------------------------------

    @Test
    public void testGetWeightedSampleGeneratorByValues() {
        List<WeightedSample<Integer>> samples = new ArrayList<WeightedSample<Integer>>();
        int n = 10;
        for (int i = 0; i < n; i++) {
            WeightedSample<Integer> sample = new WeightedSample<Integer>(i, i * 2. / (n * (n + 1)));
            samples.add(sample);
        }
        Generator<Integer> generator = generatorFactory.createWeightedSampleGenerator(samples, Integer.class);
        initAndUseGenerator(generator);
    }
    
    
    
    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testGetHeterogenousArrayGenerator() {
        List<String> salutations = Arrays.asList("Hello", "Hi");
        AttachedWeightSampleGenerator<String> salutationGenerator = new AttachedWeightSampleGenerator<String>(
        		String.class, salutations);
        List<String> names = Arrays.asList("Alice", "Bob", "Charly");
        Generator<String> nameGenerator = new AttachedWeightSampleGenerator<String>(String.class, names);
        Generator[] sources = new Generator[] { salutationGenerator, nameGenerator };
        Generator<Object[]> generator = generatorFactory.createCompositeArrayGenerator(
        		Object.class, sources, Uniqueness.NONE);
        generator.init(context);
        ProductWrapper<Object[]> wrapper = new ProductWrapper<Object[]>();
        for (int i = 0; i < 10; i++) {
			Object[] array = generator.generate(wrapper).unwrap();
            assertEquals(2, array.length);
            assertTrue(salutations.contains(array[0]));
            assertTrue(names.contains(array[1]));
        }
    }

    // source generators -----------------------------------------------------------------------------------------------

    // helpers ---------------------------------------------------------------------------------------------------------
/*
    private <T> void checkGenerator(Generator<T> generator, boolean unique, float nullQuota) {
        Set<T> products = new HashSet<T>();
        for (int i = 0; i < 10; i++) {
            T product = generator.generate();
            if (nullQuota == 0)
                assertNotNull(product);
            if (unique) {
                assertFalse(products.contains(product));
                products.add(product);
            }
        }
        checkNullQuota(generator, nullQuota);
    }

    private <T> void checkNullQuota(Generator<T> generator, double nullQuota) {
        int totalCount = 1000;
        int nullCount = 0;
        for (int i = 0; i < totalCount; i++) {
            T product = generator.generate();
            if (product == null)
                nullCount++;
            if (nullQuota == 0)
                assertNotNull(product);
            else if (nullQuota == 1)
                assertNull(product);
        }
        double measuredQuota = (double)nullCount / totalCount;
        assertEquals(nullQuota, measuredQuota, 0.05);
    }
*/
    private <T> void initAndUseGenerator(Generator<T> generator) {
    	generator.init(context);
        for (int i = 0; i < 5; i++) {
            T product = generator.generate(new ProductWrapper<T>()).unwrap();
        	assertNotNull("Generator unexpectedly invalid: " + generator.toString(), product);
        }
    }

    private static WeightFunction[] getDistributionFunctions(double min, double max) {
        return new WeightFunction[] {
            new ConstantFunction(1. / (max - min)),
            new GaussianFunction((min + max) / 2, (max - min) / 4),
        };
    }

}

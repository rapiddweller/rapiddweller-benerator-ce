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

package com.rapiddweller.benerator.wrapper;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.NonNullGenerator;
import com.rapiddweller.common.Converter;
import com.rapiddweller.common.Validator;
import com.rapiddweller.common.converter.MessageConverter;
import com.rapiddweller.common.validator.StringLengthValidator;

/**
 * Provides wrappers for number {@link Generator}s that converts 
 * their products to a target {@link Number} type.<br/>
 * <br/>
 * Created at 30.06.2009 10:48:59
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class WrapperFactory {

    public static <T extends Number> NonNullGenerator<T> asNonNullNumberGeneratorOfType(
    		Class<T> numberType, NonNullGenerator<? extends Number> source, T min, T granularity) {
    	return asNonNullGenerator(asNumberGeneratorOfType(numberType, source, min, granularity));
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T extends Number> Generator<T> asNumberGeneratorOfType(
    		Class<T> numberType, Generator<? extends Number> source, T min, T granularity) {
    	if (numberType.equals(source.getGeneratedType()))
    	 	return (Generator<T>) source;
    	if (Integer.class.equals(numberType))
    		return new AsIntegerGeneratorWrapper(source);
    	else if (Long.class.equals(numberType))
    		return new AsLongGeneratorWrapper(source);
    	else if (Short.class.equals(numberType))
    		return new AsShortGeneratorWrapper(source);
    	else if (Byte.class.equals(numberType))
    		return new AsByteGeneratorWrapper(source);
    	else if (Double.class.equals(numberType))
    		return new AsDoubleGeneratorWrapper(source);
    	else if (Float.class.equals(numberType))
    		return new AsFloatGeneratorWrapper(source);
    	else if (BigDecimal.class.equals(numberType))
    		return new AsBigDecimalGeneratorWrapper(source, (BigDecimal) min, (BigDecimal) granularity);
    	else if (BigInteger.class.equals(numberType))
    		return new AsBigIntegerGeneratorWrapper(source);
    	else 
    		throw new UnsupportedOperationException("Not a supported number type: " + numberType);
    }
    
	public static <T> NonNullGenerator<T> asNonNullGenerator(Generator<T> source) {
		if (source instanceof AsNonNullGenerator)
			return (NonNullGenerator<T>) source;
		else
			return new AsNonNullGenerator<>(source);
	}

    // formatting generators -------------------------------------------------------------------------------------------

    /**
     * Creates a generator that accepts products from a source generator
     * and converts them to target products by the converter
     *
     * @param source    the source generator
     * @param converter the converter to apply to the products of the source generator
     * @return a generator of the desired characteristics
     */
    @SuppressWarnings("rawtypes")
	public static <S, T> Generator<T> applyConverter(Generator<S> source, Converter... converter) {
        return new ConvertingGenerator<>(source, converter);
    }

    /**
     * Creates a generator that generates messages by reading the products of several source generators and
     * combining them by a Java MessageFormat.
     *
     * @param pattern   the MessageFormat pattern
     * @param minLength the minimum length of the generated value
     * @param maxLength the maximum length of the generated value
     * @param sources   the source generators of which to assemble the products
     * @return a generator of the desired characteristics
     * @see java.text.MessageFormat
     */
    @SuppressWarnings({"rawtypes" })
    public static Generator<String> createMessageGenerator(
            String pattern, int minLength, int maxLength, Generator ... sources) {
        SimpleMultiSourceArrayGenerator<Object> source = new SimpleMultiSourceArrayGenerator<Object>(
        		Object.class, sources);
		Converter converter = new MessageConverter(pattern, null);
		Generator<String> generator = WrapperFactory.applyConverter(source, converter);
        generator = applyValidator(new StringLengthValidator(minLength, maxLength), generator);
        return generator;
    }

	@SuppressWarnings("unchecked")
	public static Generator<String>[] asStringGenerators(Generator<?>[] sources) {
		Generator<String>[] result = new Generator[sources.length];
		for (int i = 0; i < sources.length; i++)
			result[i] = asStringGenerator(sources[i]);
		return result;
	}

    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static Generator<String> asStringGenerator(Generator<?> source) {
		if (source.getGeneratedType() == String.class)
			return (Generator<String>) source;
		else
			return new AsStringGenerator(source);
	}

    public static <T> OffsetBasedGenerator<T> applyOffset(Generator<T> generator, int offset) {
		return new OffsetBasedGenerator<>(generator, offset);
	}

	public static <T> Generator<T> preventClosing(Generator<T> generator) {
		return new NonClosingGeneratorProxy<>(generator);
	}

	public static <T> Generator<T> applyValidator(Validator<T> validator, Generator<T> generator) {
		return new ValidatingGeneratorProxy<>(generator, validator);
	}

	public static <T> Generator<T> applyCycler(Generator<T> generator) {
		return new CyclicGeneratorProxy<>(generator);
	}

	public static <T> Generator<T> applyHeadCycler(Generator<T> source) {
		return new CyclicGeneratorProxy<>(new NShotGeneratorProxy<>(source, 1));
	}

	public static <T> Generator<T> prependNull(Generator<T> source) {
		return new NullStartingGenerator<>(source);
	}

	public static <T> Generator<T> injectNulls(Generator<T> source, double nullQuota) {
		if (nullQuota == 0.)
			return source;
		else
			return new NullInjectingGeneratorProxy<>(source, nullQuota);
	}

	public static <T> Generator<T> applyLastProductDetector(Generator<T> generator) {
		return new LastProductDetector<>(generator);
	}

}

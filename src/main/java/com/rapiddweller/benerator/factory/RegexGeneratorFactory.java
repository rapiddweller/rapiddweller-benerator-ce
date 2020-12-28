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

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.GeneratorProvider;
import com.rapiddweller.benerator.NonNullGenerator;
import com.rapiddweller.benerator.sample.ConstantGenerator;
import com.rapiddweller.benerator.wrapper.AlternativeGenerator;
import com.rapiddweller.benerator.wrapper.ConcatenatingGenerator;
import com.rapiddweller.benerator.wrapper.WrapperFactory;
import com.rapiddweller.commons.CharSet;
import com.rapiddweller.commons.CollectionUtil;
import com.rapiddweller.commons.ConfigurationError;
import com.rapiddweller.formats.regex.Choice;
import com.rapiddweller.formats.regex.Factor;
import com.rapiddweller.formats.regex.Group;
import com.rapiddweller.formats.regex.Quantifier;
import com.rapiddweller.formats.regex.RegexChar;
import com.rapiddweller.formats.regex.RegexCharClass;
import com.rapiddweller.formats.regex.RegexParser;
import com.rapiddweller.formats.regex.RegexPart;
import com.rapiddweller.formats.regex.RegexString;
import com.rapiddweller.formats.regex.Sequence;
import com.rapiddweller.model.data.Uniqueness;

/**
 * Creates generators for regular expressions and their sub parts.<br/>
 * <br/>
 * Created: 17.11.2007 16:30:09
 * @author Volker Bergmann
 */
public class RegexGeneratorFactory {
	
    public static NonNullGenerator<String> create(String pattern, GeneratorFactory factory) {
        return create(pattern, 0, null, Uniqueness.NONE, factory);
    }

    public static NonNullGenerator<String> create(String pattern, int minLength, Integer maxLength, 
    		Uniqueness uniqueness, GeneratorFactory factory) {
    	if (pattern == null)
    		throw new IllegalArgumentException("Not a regular expression: null");
        RegexPart regex = new RegexParser().parseRegex(pattern);
        return createFromObject(regex, minLength, maxLength, uniqueness, factory);
	}

    // private helpers -------------------------------------------------------------------------------------------------

    static NonNullGenerator<String> createFromObject(RegexPart part, int minLength, Integer maxLength, 
    		Uniqueness uniqueness, GeneratorFactory factory) {
        if (part instanceof Factor)
            return createFromFactor((Factor) part, minLength, maxLength, uniqueness, factory);
        else 
        	return createFromObject(part, 1, 1, minLength, maxLength, uniqueness, factory);
    }

    private static NonNullGenerator<String> createFromFactor(Factor part, int minLength, Integer maxLength, 
    		Uniqueness uniqueness, GeneratorFactory factory) {
        Quantifier quantifier = part.getQuantifier();
		int minQuant = quantifier.getMin();
        Integer maxQuant = quantifier.getMax();
        RegexPart atom = part.getAtom();
        return createFromObject(atom, minQuant, maxQuant, minLength, maxLength, uniqueness, factory);
    }

    private static NonNullGenerator<String> createFromObject(RegexPart object, int minQuant, Integer maxQuant, 
    		int minLength, Integer maxLength, Uniqueness uniqueness, GeneratorFactory factory) {
        if (object instanceof Factor)
            return createFromFactor((Factor) object, minLength, maxLength, uniqueness, factory);
        else if (object instanceof RegexChar)
        	return createFromCharacter(((RegexChar) object).getChar(), minQuant, maxQuant, minLength, maxLength, 
        			uniqueness.isUnique(), factory);
        else if (object instanceof RegexCharClass)
        	return createCharSetGenerator(((RegexCharClass) object).getCharSet(), minQuant, maxQuant, minLength, maxLength, 
        			uniqueness, factory);
        else if (object instanceof Sequence)
            return createFromSequence((Sequence) object, minQuant, maxQuant, minLength, maxLength, uniqueness, factory);
        else if (object instanceof Group)
            return createFromGroup((Group) object, minQuant, maxQuant, minLength, maxLength, uniqueness, factory);
        else if (object instanceof Choice)
            return createFromChoice((Choice) object, minQuant, maxQuant, minLength, maxLength, uniqueness, factory);
        else if (object instanceof RegexString)
        	return WrapperFactory.asNonNullGenerator(factory.createSingleValueGenerator(
        			((RegexString) object).getString(), uniqueness.isUnique()));
        else if (object == null)
        	return WrapperFactory.asNonNullGenerator(new ConstantGenerator<>(null, String.class));
        else
            throw new UnsupportedOperationException("Unsupported regex part type: " + object.getClass().getName());
    }

    @SuppressWarnings("unchecked")
    private static NonNullGenerator<String> createFromSequence(Sequence sequence, int minCount, Integer maxCount, 
    		int minLength, Integer maxLength, Uniqueness uniqueness, GeneratorFactory factory) {
    	RegexPart[] parts = sequence.getFactors();
		Generator<String>[] componentGenerators = createComponentGenerators(
				parts, maxLength, maxLength, uniqueness, factory);
    	Generator<String[]> partGenerator = factory.createCompositeArrayGenerator(
    			String.class, componentGenerators, uniqueness);
    	return WrapperFactory.asNonNullGenerator(new ConcatenatingGenerator(partGenerator));
    }

    @SuppressWarnings("rawtypes")
	static NonNullGenerator[] createComponentGenerators(RegexPart[] parts, Integer maxComponentLength, 
			Integer maxTotalLength, Uniqueness uniqueness, GeneratorFactory factory) {
    	NonNullGenerator<?>[] components = new NonNullGenerator<?>[parts.length];
    	Integer remainingLength = maxTotalLength;
    	for (int i = 0; i < parts.length; i++) {
    		RegexPart part = parts[i];
    		Integer componentLength = part.maxLength();
    		if (componentLength != null && maxComponentLength != null)
    			componentLength = Math.min(componentLength, maxComponentLength);
    		if (componentLength != null && remainingLength != null)
    			componentLength = Math.min(componentLength, remainingLength);
			components[i] = createFromObject(part, part.minLength(), componentLength, uniqueness, factory);
			if (remainingLength != null) {
				remainingLength -= part.minLength();
				if (remainingLength < 0)
					throw new ConfigurationError("Remaining length is negative: " + remainingLength);
			}
    	}
	    return components;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static NonNullGenerator<String> createFromChoice(
            final Choice choice, final int minCount, final int maxCount, final int minLength, final Integer maxLength, 
            final Uniqueness uniqueness, final GeneratorFactory factory) {
    	final RegexPart[] alternatives = choice.getAlternatives();
    	GeneratorProvider<String> generatorProvider = () -> {
			final Generator[] altGens = createComponentGenerators(
					alternatives, maxLength, null, uniqueness, factory);
			return new AlternativeGenerator<String>(String.class, altGens);
		};
    	return factory.createCompositeStringGenerator(generatorProvider, minCount, maxCount, uniqueness);
    }

	private static NonNullGenerator<String> createFromGroup(
            final Group group, final int minCount, final Integer maxCount, 
            final int minLength, final Integer maxLength, 
            final Uniqueness uniqueness, final GeneratorFactory factory) {
		GeneratorProvider<String> partGeneratorProvider = () -> createFromObject(group.getRegex(), minLength, maxLength, uniqueness, factory);
		return factory.createCompositeStringGenerator(partGeneratorProvider, minCount, maxCount, uniqueness);
    }

    private static NonNullGenerator<String> createFromCharacter(char c, int minCount, Integer maxCount, 
    		int minLength, Integer maxLength, boolean unique, GeneratorFactory factory) {
    	DefaultsProvider defaultsProvider = factory.getDefaultsProvider();
		int minReps = max(minLength, minCount, defaultsProvider.defaultMinLength()); 
		int maxReps = min(maxLength, maxCount, defaultsProvider.defaultMaxLength()); 
		return factory.createStringGenerator(CollectionUtil.toSet(c), minReps, maxReps, 1, null, (unique ? Uniqueness.ORDERED : Uniqueness.NONE));
    }
    
	private static NonNullGenerator<String> createCharSetGenerator(
			CharSet charSet, int minCount, Integer maxCount, int minLength, Integer maxLength, 
			Uniqueness uniqueness, GeneratorFactory factory) {
		int min = Math.max(minCount, minLength);
		Integer max = maxCount;
		if (max == null)
			max = maxLength;
		else if (maxLength != null)
			max = Math.min(max, maxLength);
		if (max == null)
			max = factory.getDefaultsProvider().defaultMaxLength();
		return factory.createStringGenerator(charSet.getSet(), min, max, 1, null, uniqueness);
    }
    
	private static int min(Integer v1, Integer v2, int defaultValue) {
		if (v1 != null) {
			if (v2 != null)
				return Math.min(v1, v2);
			else
				return v1;
		} else if (v2 != null)
			return v2;
		else
			return defaultValue;
	}

    private static int max(Integer v1, Integer v2, int defaultValue) {
		if (v1 != null) {
			if (v2 != null)
				return Math.max(v1, v2);
			else
				return v1;
		} else if (v2 != null)
			return v2;
		else
			return defaultValue;
	}

}

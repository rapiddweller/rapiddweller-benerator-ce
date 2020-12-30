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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.NonNullGenerator;
import com.rapiddweller.benerator.distribution.Distribution;
import com.rapiddweller.benerator.factory.GeneratorFactory;
import com.rapiddweller.benerator.wrapper.NonNullGeneratorProxy;
import com.rapiddweller.common.Filter;
import com.rapiddweller.common.LocaleUtil;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.common.filter.FilterUtil;
import com.rapiddweller.format.regex.RegexParser;
import com.rapiddweller.model.data.Uniqueness;

/**
 * {@link String} {@link Generator} which offers a wide range of options for generating strings.<br/><br/>
 * Created: 31.07.2011 07:15:05
 * @since 0.7.0
 * @author Volker Bergmann
 */
public class StringGenerator extends NonNullGeneratorProxy<String> {

	private String charSet;
	private Locale locale;
	private boolean unique;
	private boolean ordered;
	private String prefix;
	Character minInitial;
	private String suffix;
	private int minLength;
	private int maxLength;
	private int lengthGranularity;
	private Distribution lengthDistribution;
	
	private NonNullGenerator<Character> minInitialGenerator;
	
	public StringGenerator() {
		this("\\w", LocaleUtil.getFallbackLocale(), false, false, null, null, null, 1, 8, 1, null);
	}
	public StringGenerator(String charSet, Locale locale, boolean unique,
			boolean ordered, String prefix, Character minInitial,
			String suffix, int minLength, int maxLength, int lengthGranularity, 
			Distribution lengthDistribution) {
	    super(String.class);
		this.charSet = charSet;
		this.locale = locale;
		this.unique = unique;
		this.ordered = ordered;
		this.prefix = prefix;
		this.minInitial = minInitial;
		this.suffix = suffix;
		this.minLength = minLength;
		this.maxLength = maxLength;
		this.lengthGranularity = lengthGranularity;
		this.lengthDistribution = lengthDistribution;
	}

	public String getCharSet() {
		return charSet;
	}

	public void setCharSet(String charSet) {
		this.charSet = charSet;
	}
	
	public Locale getLocale() {
		return locale;
	}
	
	public void setLocale(Locale locale) {
		this.locale = locale;
	}
	
	public boolean isUnique() {
		return unique;
	}

	public void setUnique(boolean unique) {
		this.unique = unique;
	}

	public boolean isOrdered() {
		return ordered;
	}

	public void setOrdered(boolean ordered) {
		this.ordered = ordered;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public Character getMinInitial() {
		return minInitial;
	}

	public void setMinInitial(Character minInitial) {
		this.minInitial = minInitial;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public int getMinLength() {
		return minLength;
	}

	public void setMinLength(int minLength) {
		this.minLength = minLength;
	}

	public int getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	public int getLengthGranularity() {
		return lengthGranularity;
	}
	
	public void setLengthGranularity(int lengthGranularity) {
		this.lengthGranularity = lengthGranularity;
	}
	
	public Distribution getLengthDistribution() {
		return lengthDistribution;
	}

	public void setLengthDistribution(Distribution lengthDistribution) {
		this.lengthDistribution = lengthDistribution;
	}
	
	@Override
	public boolean isParallelizable() {
		return super.isParallelizable() && (minInitialGenerator == null || minInitialGenerator.isParallelizable());
	}
	
	@Override
	public boolean isThreadSafe() {
		return super.isThreadSafe() && (minInitialGenerator == null || minInitialGenerator.isThreadSafe());
	}
	
	@Override
	public synchronized void init(GeneratorContext context) {
		Set<Character> chars = new RegexParser(locale).parseSingleChar(charSet).getCharSet().getSet();
		GeneratorFactory factory = context.getGeneratorFactory();
		if (minInitial != null) {
			Filter<Character> initialFilter = candidate -> (candidate >= minInitial);
			Set<Character> initialSet = new HashSet<>(FilterUtil.filter(new ArrayList<>(chars), initialFilter));
			this.minInitialGenerator = factory.createCharacterGenerator(initialSet);
			this.minInitialGenerator.init(context);
		}
		Generator<String> source = factory.createStringGenerator(chars, minLength, maxLength, 
				lengthGranularity, lengthDistribution, Uniqueness.instance(unique, ordered));
		setSource(source);
		super.init(context);
	}

	@Override
	public String generate() {
		assertInitialized();
		StringBuilder builder = new StringBuilder();
		String base = super.generate();
		if (base == null)
			return null;
		if (!StringUtil.isEmpty(prefix)){
			builder.append(prefix);
			base = base.substring(prefix.length());
		}
		if (minInitialGenerator != null) {
			builder.append(minInitialGenerator.generate());
			base = base.substring(1);
		}
		if (!StringUtil.isEmpty(suffix)) {
			base = base.substring(0, base.length() - suffix.length());
			builder.append(base).append(suffix);
		} else
			builder.append(base);
		return builder.toString();
	}
	
	@Override
	public void reset() {
		if (minInitialGenerator != null)
			minInitialGenerator.reset();
		super.reset();
	}
	
}

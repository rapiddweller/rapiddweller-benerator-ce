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

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.NonNullGenerator;
import com.rapiddweller.benerator.sample.NonNullSampleGenerator;
import com.rapiddweller.benerator.util.GeneratorUtil;
import com.rapiddweller.benerator.wrapper.CompositeStringGenerator;
import com.rapiddweller.benerator.wrapper.GeneratorProxy;
import com.rapiddweller.benerator.wrapper.WrapperFactory;
import com.rapiddweller.commons.ConfigurationError;
import com.rapiddweller.commons.Encodings;
import com.rapiddweller.commons.StringUtil;
import com.rapiddweller.commons.SystemInfo;
import com.rapiddweller.commons.validator.BlacklistValidator;
import com.rapiddweller.formats.DataContainer;
import com.rapiddweller.formats.csv.CSVLineIterator;

/**
 * {@link Generator} implementation which takes cells from a CSV file as input 
 * and combines the cells by taking a cell value from a random row for each column
 * and concatenating them to a string.<br/><br/>
 * Created: 01.08.2010 14:48:50
 * @since 0.6.3
 * @author Volker Bergmann
 */
public class TokenCombiner extends GeneratorProxy<String> implements NonNullGenerator<String> {

	protected String uri;
	private boolean unique;
	protected char separator = ',';
	protected String encoding = Encodings.UTF_8;
	protected boolean excludeSeed = false;
	
	protected Set<String> seed = new HashSet<String>();
	
	public TokenCombiner(String uri) {
	    this(uri, false);
    }

	public TokenCombiner(String uri, boolean unique) {
	    this(uri, unique, ',', SystemInfo.getFileEncoding(), false);
    }

	public TokenCombiner(String uri, boolean unique, char separator, String encoding, boolean excludeSeed) {
		super(String.class);
	    this.uri = uri;
		this.unique = unique;
		this.separator = separator;
		this.encoding = encoding;
		this.excludeSeed = excludeSeed;
    }

	public void setUri(String uri) {
    	this.uri = uri;
    }

	public void setUnique(boolean unique) {
    	this.unique = unique;
    }

	public void setSeparator(char separator) {
    	this.separator = separator;
    }

	public void setEncoding(String encoding) {
    	this.encoding = encoding;
    }

	public void setExcludeSeed(boolean excludeSeed) {
    	this.excludeSeed = excludeSeed;
    }
	
	@SuppressWarnings("resource")
	@Override
	public synchronized void init(GeneratorContext context) {
		Generator<String> source = new SimpleTokenCombinator(unique);
		if (excludeSeed) { 
			BlacklistValidator<String> validator = new BlacklistValidator<String>(seed);
			source = WrapperFactory.applyValidator(validator, source);
		}
		super.setSource(source);
	    super.init(context);
	}

	@Override
	public String generate() {
		return GeneratorUtil.generateNonNull(this);
	}
	
	
	
	protected class SimpleTokenCombinator extends CompositeStringGenerator {
		
		@SuppressWarnings("unchecked")
		SimpleTokenCombinator(boolean unique) {
	        super(unique);
        }

		@Override
		@SuppressWarnings("unchecked")
	    public void init(GeneratorContext context) {
			try {
				NonNullSampleGenerator<String>[] sources = null;
				String absoluteUri = context.resolveRelativeUri(uri);
		        CSVLineIterator iterator = new CSVLineIterator(absoluteUri, separator, true, encoding);
		        int tokenCount = -1;
		        DataContainer<String[]> container = new DataContainer<String[]>();
		        while ((container = iterator.next(container)) != null) {
			        String[] tokens = container.getData();
		        	if (sources == null) {
		        		tokenCount = tokens.length;
		        		sources = new NonNullSampleGenerator[tokenCount];
		        		for (int i = 0; i < tokenCount; i++) {
		        			sources[i] = new NonNullSampleGenerator<String>(String.class);
		        			sources[i].setUnique(unique);
		        		}
		        	}
		        	for (int i = 0; i < tokens.length; i++)
		        		if (!unique || !sources[i].contains(tokens[i]))
		        			sources[i].addValue(tokens[i]);
		        	if (excludeSeed)
		        		seed.add(StringUtil.concat(null, tokens));
		        }
		        setSources(sources);
		        super.init(context);
	        } catch (IOException e) {
	    		throw new ConfigurationError("Error initializing " + getClass().getSimpleName() + " from URI " + uri, e);
	        }
	    }
	}

}

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

import com.rapiddweller.benerator.Generator;

/**
 * {@link Generator} implementation that wraps several String generators 
 * and concatenates their results to a composite {@link String}.<br/>
 * <br/>
 * Created at 21.09.2009 18:54:32
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class ConcatenatingGenerator extends GeneratorWrapper<String[], String> {

	private final String separator;
	
    public ConcatenatingGenerator(Generator<String[]> source) {
	    this(source, "");
    }
    
    public ConcatenatingGenerator(Generator<String[]> source, String separator) {
	    super(source);
	    this.separator = separator;
    }
    
    public String getSeparator() {
	    return separator;
    }
    
    // Generator interface implementation ------------------------------------------------------------------------------
    
	@Override
	public Class<String> getGeneratedType() {
	    return String.class;
    }

	@Override
	public ProductWrapper<String> generate(ProductWrapper<String> wrapper) {
		ProductWrapper<String[]> sourceWrapper = generateFromSource();
        if (sourceWrapper == null)
        	return null;
        String[] parts = sourceWrapper.unwrap();
        if (parts.length > 0) {
	        StringBuilder builder = new StringBuilder();
	        builder.append(parts[0]);
	        for (int i = 1; i < parts.length; i++) {
	        	String part = parts[i];
	            builder.append(separator).append(part);
	        }
	        return wrapper.wrap(builder.toString());
        } else
        	return wrapper.wrap("");
	}

}

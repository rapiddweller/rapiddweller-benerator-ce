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

import com.rapiddweller.benerator.sample.SeedGenerator;
import com.rapiddweller.benerator.wrapper.NonNullGeneratorWrapper;
import com.rapiddweller.commons.IOUtil;
import com.rapiddweller.commons.ReaderLineIterator;
import com.rapiddweller.commons.StringUtil;

/**
 * Generates sentences based on a seed sentence set.<br/>
 * <br/>
 * Created at 16.07.2009 20:02:32
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class SeedSentenceGenerator extends NonNullGeneratorWrapper<String[], String> {

    private static final int DEFAULT_DEPTH = 4;

	public SeedSentenceGenerator(String seedUri) throws IOException {
		this(seedUri, DEFAULT_DEPTH);
	}

    public SeedSentenceGenerator(String seedUri, int depth) throws IOException {
		super(new SeedGenerator<String>(String.class, depth));
		ReaderLineIterator iterator = new ReaderLineIterator(IOUtil.getReaderForURI(seedUri));
		while (iterator.hasNext()) {
			String line = iterator.next();
			if (StringUtil.isEmpty(line))
				continue;
	    	((SeedGenerator<String>) getSource()).addSample(line.split("\\s"));
		}
    }

	@Override
	public String generate() {
	    return toString(generateFromNotNullSource());
    }
	
    @Override
	public Class<String> getGeneratedType() {
	    return String.class;
    }

	// helpers ---------------------------------------------------------------------------------------------------------

    private static String toString(String[] tokens) {
	    StringBuilder builder = new StringBuilder();
	    for (String token : tokens)
	    	builder.append(token).append(' ');
	    return builder.toString();
    }

    public void printState() {
	    System.out.println(getClass().getSimpleName());
	    ((SeedGenerator<String>) getSource()).printState("  ");
    }

}

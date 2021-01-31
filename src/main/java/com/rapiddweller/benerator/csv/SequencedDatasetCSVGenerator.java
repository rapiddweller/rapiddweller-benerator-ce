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

package com.rapiddweller.benerator.csv;

import com.rapiddweller.benerator.distribution.Distribution;
import com.rapiddweller.benerator.distribution.Sequence;
import com.rapiddweller.benerator.sample.SampleGenerator;
import com.rapiddweller.benerator.sample.SampleGeneratorUtil;
import com.rapiddweller.benerator.wrapper.GeneratorProxy;
import com.rapiddweller.common.Context;
import com.rapiddweller.common.Converter;
import com.rapiddweller.format.script.ScriptConverterForStrings;
import com.rapiddweller.script.WeightedSample;

import java.util.List;

/**
 * Generates values from a dataset based on a {@link Sequence}.<br/><br/>
 * Created: 17.02.2010 23:22:52
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class SequencedDatasetCSVGenerator<E> extends GeneratorProxy<E> {
	
    @SuppressWarnings("unchecked")
	public SequencedDatasetCSVGenerator(String filenamePattern, char separator, String datasetName, String nesting,
            Distribution distribution, String encoding, Context context) {
        this(filenamePattern, separator, datasetName, nesting, distribution, encoding, 
        		(Converter<String, E>) new ScriptConverterForStrings(context));
    }

	@SuppressWarnings("unchecked")
    public SequencedDatasetCSVGenerator(String filenamePattern, char separator, String datasetName, String nesting,
            Distribution distribution, String encoding, Converter<String, E> preprocessor) {
		super((Class<E>) Object.class);
        List<E> samples = parseFiles(datasetName, separator, nesting, filenamePattern, encoding, preprocessor);
		setSource(new SampleGenerator<>((Class<E>) samples.get(0).getClass(), distribution, false, samples));
    }

	private static <T> List<T> parseFiles(String datasetName, char separator, String nesting, String filenamePattern,
            String encoding, Converter<String, T> preprocessor) {
        List<WeightedSample<T>> weightedSamples = CSVGeneratorUtil.parseDatasetFiles(
        		datasetName, separator, nesting, filenamePattern, encoding, preprocessor);
        return SampleGeneratorUtil.extractValues(weightedSamples);
    }

}

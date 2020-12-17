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

import java.util.List;

import com.rapiddweller.benerator.WeightedGenerator;
import com.rapiddweller.benerator.dataset.AbstractDatasetGenerator;
import com.rapiddweller.benerator.dataset.AtomicDatasetGenerator;
import com.rapiddweller.benerator.dataset.Dataset;
import com.rapiddweller.benerator.dataset.DatasetUtil;
import com.rapiddweller.benerator.sample.AttachedWeightSampleGenerator;
import com.rapiddweller.commons.Converter;
import com.rapiddweller.commons.IOUtil;
import com.rapiddweller.commons.SystemInfo;
import com.rapiddweller.commons.converter.NoOpConverter;
import com.rapiddweller.script.WeightedSample;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Generates data from a csv file set that is organized as {@link Dataset}.
 * For different regions, different CSV versions may be provided by appending region suffixes,
 * similar to the JDK ResourceBundle handling.<br/>
 * <br/>
 * Created: 21.03.2008 16:32:04
 *
 * @author Volker Bergmann
 * @since 0.5.0
 */
public class WeightedDatasetCSVGenerator<E> extends AbstractDatasetGenerator<E> {

    private static final Logger LOGGER = LogManager.getLogger(WeightedDatasetCSVGenerator.class);

    protected String filenamePattern;
    protected String encoding;
    protected char separator;
    protected Converter<String, E> converter;


    // constructors ----------------------------------------------------------------------------------------------------

    public WeightedDatasetCSVGenerator(Class<E> generatedType, String filenamePattern, String datasetName, String nesting, boolean fallback) {
        this(generatedType, filenamePattern, ',', datasetName, nesting, fallback, SystemInfo.getFileEncoding());
    }

    @SuppressWarnings({"unchecked", "cast", "rawtypes"})
    public WeightedDatasetCSVGenerator(Class<E> generatedType, String filenamePattern, char separator, String datasetName, String nesting, boolean fallback, String encoding) {
        this(generatedType, filenamePattern, separator, datasetName, nesting, fallback, encoding, (Converter<String, E>) new NoOpConverter());
    }

    @SuppressWarnings({"cast", "unchecked", "rawtypes"})
    public WeightedDatasetCSVGenerator(Class<E> generatedType, String filenamePattern, String datasetName, String nesting, boolean fallback, String encoding) {
        this(generatedType, filenamePattern, ',', datasetName, nesting, fallback, encoding, (Converter<String, E>) new NoOpConverter());
    }

    public WeightedDatasetCSVGenerator(Class<E> generatedType, String filenamePattern, char separator, String datasetName, String nesting, boolean fallback,
                                       String encoding, Converter<String, E> converter) {
        super(generatedType, nesting, datasetName, fallback);
        this.filenamePattern = filenamePattern;
        this.separator = separator;
        this.encoding = encoding;
        this.converter = converter;
    }


    // properties ------------------------------------------------------------------------------------------------------

    public void setFilenamePattern(String filenamePattern) {
        this.filenamePattern = filenamePattern;
    }

    public String getFilenamePattern() {
        return filenamePattern;
    }

    @Override
    protected WeightedGenerator<E> createGeneratorForAtomicDataset(Dataset dataset) {
        String filename = DatasetUtil.filenameOfDataset(dataset.getName(), filenamePattern);
        LOGGER.debug("Creating weighted data set CSV generator for file {}", filename);
        if (IOUtil.isURIAvailable(filename)) {
            List<WeightedSample<E>> samples = CSVGeneratorUtil.parseFile(filename, separator, encoding, converter);
            AttachedWeightSampleGenerator<E> generator = new AttachedWeightSampleGenerator<E>(generatedType);
            for (WeightedSample<E> sample : samples)
                generator.addSample(sample.getValue(), sample.getWeight());
            if (samples.size() > 0)
                return new AtomicDatasetGenerator<E>(generator, filename, dataset.getName());
        }
        return null;
    }


    // java.lang.Object overrides --------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + '[' + filenamePattern + ',' + nesting + ':' + datasetName + ']';
    }

}

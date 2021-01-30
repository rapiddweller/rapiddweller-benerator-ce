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

package com.rapiddweller.domain.address;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.NonNullGenerator;
import com.rapiddweller.benerator.csv.WeightedDatasetCSVGenerator;
import com.rapiddweller.benerator.dataset.DatasetBasedGenerator;
import com.rapiddweller.benerator.dataset.DatasetUtil;
import com.rapiddweller.benerator.util.GeneratorUtil;
import com.rapiddweller.benerator.wrapper.GeneratorProxy;
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.common.Encodings;
import com.rapiddweller.common.StringUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Locale;
import java.util.Stack;

/**
 * Generates a street name for a region.<br/>
 * <br/>
 * Created: 12.06.2006 00:08:28
 *
 * @author Volker Bergmann
 * @since 0.1
 */
public class StreetNameGenerator extends GeneratorProxy<String>
        implements DatasetBasedGenerator<String>, NonNullGenerator<String> {

    private static final Logger LOGGER =
            LogManager.getLogger(StreetNameGenerator.class);

    private static final String REGION_NESTING =
            "com/rapiddweller/dataset/region";
    private static final String FILENAME_PATTERN =
            "/com/rapiddweller/domain/address/street_{0}.csv";

    private String datasetName;

    // construction ----------------------------------------------------------------------------------------------------

    public StreetNameGenerator() {
        this(null);
    }

    public StreetNameGenerator(String datasetName) {
        super(String.class);
        this.datasetName = datasetName;
    }

    // DatasetBasedGenerator interface implementation ------------------------------------------------------------------

    private static Generator<String> createSource(String datasetName) {
        return new WeightedDatasetCSVGenerator<>(String.class, FILENAME_PATTERN,
                datasetName, REGION_NESTING, true, Encodings.UTF_8);
    }

    @Override
    public String getNesting() {
        return REGION_NESTING;
    }

    @Override
    public String getDataset() {
        return datasetName;
    }

    public void setDataset(String datasetName) {
        this.datasetName = datasetName;
    }

    @Override
    public String generateForDataset(String dataset) {
        return getSource().generateForDataset(dataset);
    }

    @Override
    public synchronized void init(GeneratorContext context) {
        Stack<String> datasetOptions = new Stack<>();
        datasetOptions.push(DatasetUtil.fallbackRegionName());
        datasetOptions.push(DatasetUtil.defaultRegionName());
        if (!StringUtil.isEmpty(datasetName)) {
            datasetOptions.push(datasetName);
        }
        init(datasetOptions, context);
    }

    private void init(Stack<String> datasetOptions, GeneratorContext context) {
        String currentOption = datasetOptions.pop();
        try {
            setSource(createSource(currentOption));
            super.init(context);
        } catch (Exception e) {
            // if the call fails, try another option
            if (datasetOptions.isEmpty()) {
                throw new ConfigurationError(getClass().getSimpleName() +
                        " could not be initialized");
            }
            String nextOption = datasetOptions.peek();
            LOGGER.error("Error initializing " + getClass().getSimpleName() +
                    " with dataset '" + currentOption + "': " +
                    e.getMessage() + ". Falling back to '" + nextOption + "'");
            init(datasetOptions, context);
        }
    }

    @Override
    public WeightedDatasetCSVGenerator<String> getSource() {
        return (WeightedDatasetCSVGenerator<String>) super.getSource();
    }

    @Override
    public String generate() {
        return GeneratorUtil.generateNonNull(this);
    }

    // helpers ---------------------------------------------------------------------------------------------------------

    public String generateForCountryAndLocale(String countryCode,
                                              Locale language) {
        WeightedDatasetCSVGenerator<String> source = getSource();
        String subset = countryCode + '_' + language.getLanguage();
        if (source.supportsDataset(subset)) {
            return source.generateForDataset(subset);
        }
        subset = countryCode;
        return source.generateForDataset(countryCode);
    }

}

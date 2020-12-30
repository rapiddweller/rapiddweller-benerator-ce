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

package com.rapiddweller.domain.person;

import com.rapiddweller.benerator.dataset.DatasetUtil;
import com.rapiddweller.common.ArrayBuilder;
import com.rapiddweller.common.ConversionException;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.common.converter.ThreadSafeConverter;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.Map;

/**
 * Can convert the male form of a family name to the female form by a suffix mapping.
 * This is used for generating names in some slavic languages.<br/>
 * <br/>
 * Created at 15.03.2009 18:06:55
 *
 * @author Volker Bergmann
 * @since 0.5.8
 */

public class FemaleFamilyNameConverter extends ThreadSafeConverter<String, String> {

    private static final Logger logger = LogManager.getLogger(FemaleFamilyNameConverter.class);
    private final String[][] mappings;

    public FemaleFamilyNameConverter() {
        this(null);
    }

    public FemaleFamilyNameConverter(String datasetName) {
        super(String.class, String.class);
        ArrayBuilder<String[]> builder = new ArrayBuilder<>(String[].class);
        try {
            if (datasetName != null) {
                String dataFileName = DatasetUtil.filenameOfDataset(datasetName,
                        "/com/rapiddweller/domain/person/ffn_{0}.properties");
                if (IOUtil.isURIAvailable(dataFileName)) {
                    Map<String, String> props = IOUtil.readProperties(dataFileName);
                    for (Map.Entry<String, String> entry : props.entrySet())
                        builder.add(new String[]{entry.getKey(), entry.getValue()});
                }
            }
        } catch (Exception e) {
            logger.debug("No female family name conversion defined for dataset '" + datasetName + "'");
        }
        this.mappings = builder.toArray();
    }

    @Override
    public String convert(String name) throws ConversionException {
        for (String[] mapping : mappings)
            if (mapping[0].length() == 0 || name.endsWith(mapping[0]))
                return name.substring(0, name.length() - mapping[0].length()) + mapping[1];
        return name;
    }

}

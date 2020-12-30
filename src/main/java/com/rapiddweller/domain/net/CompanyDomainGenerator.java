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

package com.rapiddweller.domain.net;

import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.util.AbstractNonNullGenerator;
import com.rapiddweller.domain.address.Country;
import com.rapiddweller.domain.organization.CompanyName;
import com.rapiddweller.domain.organization.CompanyNameGenerator;
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.common.Converter;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.common.converter.ThreadSafeConverter;
import com.rapiddweller.format.text.DelocalizingConverter;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;

import static com.rapiddweller.benerator.util.GeneratorUtil.generateNonNull;

/**
 * Generates web domains for companies.<br/><br/>
 * Created at 23.04.2008 23:04:10
 *
 * @author Volker Bergmann
 * @since 0.5.2
 */
public class CompanyDomainGenerator extends AbstractNonNullGenerator<String> {

    private static final Logger LOGGER = LogManager.getLogger(CompanyDomainGenerator.class);

    private final CompanyNameGenerator companyNameGenerator;
    private final TopLevelDomainGenerator tldGenerator;
    private final Converter<String, String> normalizer;

    public CompanyDomainGenerator() {
        this(Country.getDefault().getIsoCode());
    }

    public CompanyDomainGenerator(String datasetName) {
        LOGGER.debug("Creating instance of {} for dataset {}", getClass(), datasetName);
        companyNameGenerator = new CompanyNameGenerator(false, false, false, datasetName);
        tldGenerator = new TopLevelDomainGenerator();
        normalizer = new Normalizer();
    }

    public void setDataset(String datasetName) {
        companyNameGenerator.setDataset(datasetName);
    }

    @Override
    public synchronized void init(GeneratorContext context) {
        companyNameGenerator.init(context);
        tldGenerator.init(context);
        super.init(context);
    }

    @Override
    public Class<String> getGeneratedType() {
        return String.class;
    }

    @Override
    public String generate() {
        CompanyName name = generateNonNull(companyNameGenerator);
        String tld = generateNonNull(tldGenerator);
        return normalizer.convert(name.getShortName()) + '.' + tld;
    }

    @Override
    public boolean isThreadSafe() {
        return companyNameGenerator.isThreadSafe() && tldGenerator.isThreadSafe() && normalizer.isThreadSafe();
    }

    @Override
    public boolean isParallelizable() {
        return companyNameGenerator.isParallelizable() && tldGenerator.isParallelizable() && normalizer.isParallelizable();
    }

    private static final class Normalizer extends ThreadSafeConverter<String, String> {

        private final DelocalizingConverter delocalizer;

        public Normalizer() {
            super(String.class, String.class);
            try {
                this.delocalizer = new DelocalizingConverter();
            } catch (IOException e) {
                throw new ConfigurationError(e);
            }
        }

        @Override
        public String convert(String sourceValue) {
            sourceValue = StringUtil.normalizeSpace(sourceValue);
            sourceValue = delocalizer.convert(sourceValue);
            sourceValue = sourceValue.replace(' ', '-');
            return sourceValue.toLowerCase();
        }
    }

}

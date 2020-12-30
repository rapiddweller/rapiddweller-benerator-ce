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

import com.rapiddweller.benerator.sample.WeightedCSVSampleGenerator;
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.common.Converter;
import com.rapiddweller.common.LocaleUtil;
import com.rapiddweller.common.converter.NoOpConverter;

import java.util.Locale;

/**
 * Generates data from a localized csv file.
 * For different locales, different CSV versions may be provided by appending region suffixes,
 * similar to the JDK ResourceBundle handling.<br/>
 * <br/>
 * Created: 07.06.2007 17:21:24
 *
 * @author Volker Bergmann
 */
public class LocalCSVGenerator<E> extends WeightedCSVSampleGenerator<E> {

    private final String baseName;
    private Locale locale;
    private final String suffix;

    // constructors ----------------------------------------------------------------------------------------------------

    public LocalCSVGenerator(Class<E> targetType, String baseName, String suffix, String encoding) {
        this(targetType, baseName, Locale.getDefault(), suffix, encoding);
    }

    @SuppressWarnings("unchecked")
    public LocalCSVGenerator(Class<E> targetType, String baseName, Locale locale, String suffix, String encoding) {
        this(targetType, baseName, locale, suffix, encoding, NoOpConverter.getInstance());
    }

    public LocalCSVGenerator(Class<E> targetType, String baseName, Locale locale, String suffix, String encoding,
                             Converter<String, E> converter) {
        super(targetType, availableUri(baseName, locale, suffix), encoding, converter);
        this.baseName = baseName;
        this.locale = locale;
        this.suffix = suffix;
    }

    // properties ------------------------------------------------------------------------------------------------------

    private static String availableUri(String baseName, Locale locale, String suffix) {
        if (baseName == null || suffix == null)
            return null;
        String uri = LocaleUtil.availableLocaleUrl(baseName, locale, suffix);
        if (uri == null)
            throw new ConfigurationError("No localization found for " + baseName + suffix + " on locale " + locale);
        return uri;
    }

    public Locale getLocale() {
        return locale;
    }

    // private helpers -------------------------------------------------------------------------------------------------

    public void setLocale(Locale locale) {
        this.uri = availableUri(baseName, locale, suffix);
        this.locale = locale;
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + '[' + baseName + ',' + locale + ',' + suffix + ']';
    }
}

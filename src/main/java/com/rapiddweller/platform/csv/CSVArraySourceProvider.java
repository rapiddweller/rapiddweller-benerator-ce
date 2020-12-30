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

package com.rapiddweller.platform.csv;

import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.factory.DataSourceProvider;
import com.rapiddweller.common.Converter;
import com.rapiddweller.common.converter.ArrayConverter;
import com.rapiddweller.format.DataSource;
import com.rapiddweller.format.csv.CSVSource;
import com.rapiddweller.format.util.ConvertingDataSource;
import com.rapiddweller.format.util.OffsetDataSource;

/**
 * {@link DataSourceProvider} which creates array {@link Iterable}s for CSV files.<br/><br/>
 * Created: 19.07.2011 08:23:39
 *
 * @author Volker Bergmann
 * @since 0.7.0
 */
public class CSVArraySourceProvider implements DataSourceProvider<Object[]> {

    private final Converter<String, ?> preprocessor;
    private final boolean rowBased;
    private final char separator;
    private final String encoding;

    public CSVArraySourceProvider(String type, Converter<String, ?> preprocessor, boolean rowBased, char separator, String encoding) {
        this.preprocessor = preprocessor;
        this.rowBased = rowBased;
        this.separator = separator;
        this.encoding = encoding;
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public DataSource<Object[]> create(String uri, BeneratorContext context) {
        DataSource<String[]> source;
        source = new CSVSource(uri, separator, encoding, true, rowBased);
        Converter<String[], Object[]> converter = new ArrayConverter(String.class, Object.class, preprocessor);
        DataSource<Object[]> result = new ConvertingDataSource<>(source, converter);
        result = new OffsetDataSource<>(result, 1); // offset = 1 in order to skip header row
        return result;
    }

}

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

package com.rapiddweller.platform.xls;

import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.factory.DataSourceProvider;
import com.rapiddweller.commons.Converter;
import com.rapiddweller.commons.converter.ArrayConverter;
import com.rapiddweller.formats.DataSource;
import com.rapiddweller.formats.util.ConvertingDataSource;
import com.rapiddweller.formats.util.OffsetDataSource;
import com.rapiddweller.formats.xls.XLSSource;

/**
 * {@link DataSourceProvider} implementation which creates {@link XLSSource}s.<br/><br/>
 * Created: 19.07.2011 08:31:10
 *
 * @author Volker Bergmann
 * @since 0.7.0
 */
public class XLSArraySourceProvider implements DataSourceProvider<Object[]> {

    private final boolean formatted;
    private final Converter<?, ?> scriptConverter;
    private final String emptyMarker;
    private final String nullMarker;
    private final boolean rowBased;

    public XLSArraySourceProvider(boolean formatted, Converter<?, ?> scriptConverter, String emptyMarker, String nullMarker, boolean rowBased) {
        this.formatted = formatted;
        this.scriptConverter = scriptConverter;
        this.emptyMarker = emptyMarker;
        this.nullMarker = nullMarker;
        this.rowBased = rowBased;
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public DataSource<Object[]> create(String uri, BeneratorContext context) {
        DataSource<Object[]> source = new XLSSource(uri, formatted, emptyMarker, nullMarker, rowBased);
        source = new OffsetDataSource<>(source, 1); // skip header row
        Converter<Object[], Object[]> converter = new ArrayConverter(Object.class, Object.class, scriptConverter);
        return new ConvertingDataSource<>(source, converter);
    }

}

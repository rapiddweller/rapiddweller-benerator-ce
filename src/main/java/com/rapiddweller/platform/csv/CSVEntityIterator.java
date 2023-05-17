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

import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.common.ArrayUtil;
import com.rapiddweller.common.Converter;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.common.Tabular;
import com.rapiddweller.common.converter.ArrayConverter;
import com.rapiddweller.common.converter.ConverterChain;
import com.rapiddweller.format.DataContainer;
import com.rapiddweller.format.DataIterator;
import com.rapiddweller.format.csv.CSVLineIterator;
import com.rapiddweller.format.util.ConvertingDataIterator;
import com.rapiddweller.format.util.DataIteratorAdapter;
import com.rapiddweller.format.util.OrthogonalArrayIterator;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.platform.array.Array2EntityConverter;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Iterates Entities in a CSV file.
 * When the property 'columns' is set, the CSV file is assumed to have no header row.<br/><br/>
 * Created: 07.04.2008 09:49:08
 *
 * @author Volker Bergmann
 * @since 0.5.1
 */
public class CSVEntityIterator implements DataIterator<Entity>, Tabular {

    private final String uri;
    private final char separator;
    private final String encoding;
    private String[] columns;
    private final Converter<String, ?> preprocessor;
    private boolean expectingHeader;
    private boolean rowBased;

    private DataIterator<Entity> source;

    private boolean initialized;
    private final ComplexTypeDescriptor entityDescriptor;

    // constructors ----------------------------------------------------------------------------------------------------

    public CSVEntityIterator(String uri, ComplexTypeDescriptor descriptor,
                             Converter<String, ?> preprocessor, char separator,
                             String encoding) {
        if (!IOUtil.isURIAvailable(uri)) {
            throw BeneratorExceptionFactory.getInstance().fileNotFound(uri, null);
        }
        this.uri = uri;
        this.preprocessor = preprocessor;
        this.separator = separator;
        this.encoding = encoding;
        this.entityDescriptor = descriptor;
        this.initialized = false;
        this.expectingHeader = true;
        this.rowBased = (descriptor == null || descriptor.isRowBased() == null || descriptor.isRowBased());
    }

    // properties ------------------------------------------------------------------------------------------------------

    public static List<Entity> parseAll(String uri, char separator,
                                        String encoding,
                                        ComplexTypeDescriptor descriptor,
                                        Converter<String, String> preprocessor)
            throws FileNotFoundException {
        List<Entity> list = new ArrayList<>();
        try (CSVEntityIterator iterator = new CSVEntityIterator(uri, descriptor, preprocessor, separator, encoding)) {
            DataContainer<Entity> container = new DataContainer<>();
            while ((container = iterator.next(container)) != null) {
                list.add(container.getData());
            }
            return list;
        }
    }

    public void setExpectingHeader(boolean expectHeader) {
        this.expectingHeader = expectHeader;
    }

    public boolean isRowBased() {
        return rowBased;
    }

    public void setRowBased(boolean rowBased) {
        this.rowBased = rowBased;
    }

    @Override
    public String[] getColumnNames() {
        return columns;
    }

    // DataIterator interface ------------------------------------------------------------------------------------------

    public void setColumns(String[] columns) {
        this.expectingHeader = false;
        if (ArrayUtil.isEmpty(columns)) {
            this.columns = null;
        } else {
            this.columns = columns.clone();
            StringUtil.trimAll(this.columns);
        }
    }

    @Override
    public Class<Entity> getType() {
        return Entity.class;
    }

    @Override
    public DataContainer<Entity> next(DataContainer<Entity> container) {
        assureInitialized();
        if (source == null){
            return null;
        }
        return source.next(container);
    }

    @Override
    public void close() {
        IOUtil.close(source);
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[uri=" + uri + ", encoding=" +
                encoding + ", separator=" + separator +
                ", entityName=" + entityDescriptor.getName() + "]";
    }

    // private helpers -------------------------------------------------------------------------------------------------

    private void assureInitialized() {
        if (!initialized) {
            init();
            initialized = true;
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void init() {
        DataIterator<String[]> cellIterator;
        cellIterator = new CSVLineIterator(uri, separator, true, encoding);
        if (!rowBased) {
            cellIterator = new OrthogonalArrayIterator<>(cellIterator);
        }
        if (expectingHeader) {
            DataContainer<String[]> dataContainer = cellIterator.next(new DataContainer<>());
            if (dataContainer == null) {
                setColumns(getColumnNames());
            } else {
                setColumns(dataContainer.getData());
            }
        }

        if (columns == null) {
            this.source = null;
        } else {
            Converter<String[], Object[]> arrayConverter = new ArrayConverter(String.class, Object.class, preprocessor);
            Array2EntityConverter a2eConverter = new Array2EntityConverter(entityDescriptor, columns, true);
            Converter<String[], Entity> converter = new ConverterChain<>(arrayConverter, a2eConverter);
            this.source = new ConvertingDataIterator<>(cellIterator, converter);
        }
    }
}

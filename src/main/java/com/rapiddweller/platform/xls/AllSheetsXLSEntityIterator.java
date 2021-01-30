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
import com.rapiddweller.benerator.engine.DefaultBeneratorContext;
import com.rapiddweller.common.Context;
import com.rapiddweller.common.Converter;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.common.context.ContextAware;
import com.rapiddweller.common.converter.NoOpConverter;
import com.rapiddweller.format.DataContainer;
import com.rapiddweller.format.DataIterator;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.Entity;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Iterates an Excel sheet and maps its rows to {@link Entity} instances.<br/>
 * <br/>
 * Created at 27.01.2009 21:38:31
 *
 * @author Volker Bergmann
 * @since 0.5.8
 */

public class AllSheetsXLSEntityIterator
        implements DataIterator<Entity>, ContextAware {

    private final String uri;

    private final Workbook workbook;

    private final boolean formatted;
    private boolean rowBased;
    private final String emptyMarker;

    private final Converter<String, ?> preprocessor;
    private DataIterator<Entity> source;
    private BeneratorContext context;

    private final ComplexTypeDescriptor entityDescriptor;

    private int sheetNo;

    // constructors ----------------------------------------------------------------------------------------------------

    public AllSheetsXLSEntityIterator(String uri)
            throws IOException, InvalidFormatException {
        this(uri, new NoOpConverter<>(), null, false);
    }

    public AllSheetsXLSEntityIterator(String uri,
                                      Converter<String, ?> preprocessor,
                                      ComplexTypeDescriptor entityDescriptor,
                                      boolean formatted)
            throws IOException {
        this.uri = uri;
        this.preprocessor = preprocessor;
        this.entityDescriptor = entityDescriptor;
        this.rowBased = (entityDescriptor != null &&
                entityDescriptor.isRowBased() != null ?
                entityDescriptor.isRowBased() : true);
        this.emptyMarker = (entityDescriptor != null &&
                entityDescriptor.getEmptyMarker() != null ?
                entityDescriptor.getEmptyMarker() : null);
        this.workbook =
                WorkbookFactory.create(IOUtil.getInputStreamForURI(uri));
        this.sheetNo = -1;
        this.formatted = formatted;
    }

    // properties ------------------------------------------------------------------------------------------------------

    public static List<Entity> parseAll(String uri,
                                        Converter<String, ?> preprocessor,
                                        boolean formatted)
            throws IOException, InvalidFormatException {
        List<Entity> list = new ArrayList<>();
        AllSheetsXLSEntityIterator iterator =
                new AllSheetsXLSEntityIterator(uri, preprocessor, null,
                        formatted);
        iterator.setContext(new DefaultBeneratorContext());
        DataContainer<Entity> container = new DataContainer<>();
        while ((container = iterator.next(container)) != null) {
            list.add(container.getData());
        }
        return list;
    }

    public void setRowBased(boolean rowBased) {
        this.rowBased = rowBased;
    }


    // ContextAware interface implementation ---------------------------------------------------------------------------

    public String getUri() {
        return uri;
    }


    // DataSource interface implementation -----------------------------------------------------------------------------

    @Override
    public void setContext(Context context) {
        this.context = (BeneratorContext) context;
    }

    @Override
    public Class<Entity> getType() {
        return Entity.class;
    }

    @Override
    public synchronized DataContainer<Entity> next(
            DataContainer<Entity> container) {
        if (sheetNo == -1) {
            nextSheet();
        }
        DataContainer<Entity> result;
        do {
            if (source == null) {
                return null;
            }
            result = source.next(container);
            if (result == null) {
                nextSheet();
            }
        } while (source != null && result == null);
        return result;
    }


    // convenience methods ---------------------------------------------------------------------------------------------

    @Override
    public synchronized void close() {
        IOUtil.close(source);
    }


    // java.lang.Object overrides --------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + uri + "]";
    }

    // private helpers -------------------------------------------------------------------------------------------------

    private void nextSheet() {
        // check if a sheet is available
        if (sheetNo >= workbook.getNumberOfSheets() - 1) {
            source = null;
            return;
        }

        // if a sheet was already opened, then close it
        if (source != null) {
            IOUtil.close(source);
        }

        // select sheet
        this.sheetNo++;

        // create iterator
        Sheet sheet = workbook.getSheetAt(sheetNo);
        source = new SingleSheetXLSEntityIterator(sheet, preprocessor,
                entityDescriptor, context, rowBased, formatted, emptyMarker);
    }

}

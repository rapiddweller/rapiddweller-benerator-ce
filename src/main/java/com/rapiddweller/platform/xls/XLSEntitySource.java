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

import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.model.data.EntitySource;
import com.rapiddweller.model.data.FileBasedEntitySource;
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.common.Converter;
import com.rapiddweller.format.DataIterator;

/**
 * Implements an {@link EntitySource} that reads Entities from an Excel sheet.<br/>
 * <br/>
 * Created at 27.01.2009 21:31:54
 *
 * @author Volker Bergmann
 * @since 0.5.7
 */

public class XLSEntitySource extends FileBasedEntitySource {

    private final ComplexTypeDescriptor entityType;
    private final Converter<String, ?> preprocessor;
    private final String sheetName;
    private final boolean formatted;

    // constructors ----------------------------------------------------------------------------------------------------

    public XLSEntitySource(String uri, Converter<String, ?> preprocessor, ComplexTypeDescriptor entityType, String sheetName, boolean formatted) {
        super(uri);
        this.entityType = entityType;
        this.preprocessor = preprocessor;
        this.sheetName = sheetName;
        this.formatted = formatted;
    }

    // EntityIterable interface ----------------------------------------------------------------------------------------

    @Override
    public DataIterator<Entity> iterator() {
        try {
            if (sheetName != null)
                return new SingleSheetXLSEntityIterator(resolveUri(), sheetName, preprocessor, entityType, context, true, formatted, null);
            else
                return new AllSheetsXLSEntityIterator(resolveUri(), preprocessor, entityType, formatted);
        } catch (Exception e) {
            throw new ConfigurationError("Cannot create iterator. ", e);
        }
    }

}

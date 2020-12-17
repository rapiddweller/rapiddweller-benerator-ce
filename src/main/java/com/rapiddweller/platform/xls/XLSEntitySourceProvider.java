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
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.model.data.EntitySource;
import com.rapiddweller.commons.Converter;

/**
 * {@link DataSourceProvider} implementation which creates XLS entity sources.<br/><br/>
 * Created: 05.05.2010 15:08:03
 *
 * @author Volker Bergmann
 * @since 0.6.1
 */
public class XLSEntitySourceProvider implements DataSourceProvider<Entity> {

    private final ComplexTypeDescriptor entityType;
    private final Converter<String, ?> scriptConverter;
    private final boolean formatted;

    public XLSEntitySourceProvider(ComplexTypeDescriptor entityType, boolean formatted, Converter<String, ?> scriptConverter) {
        this.entityType = entityType;
        this.scriptConverter = scriptConverter;
        this.formatted = formatted;
    }

    @Override
    public EntitySource create(String uri, BeneratorContext context) {
        XLSEntitySource source = new XLSEntitySource(uri, scriptConverter, entityType, entityType.getSegment(), formatted);
        source.setContext(context);
        return source;
    }

}

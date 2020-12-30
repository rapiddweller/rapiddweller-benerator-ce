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

package com.rapiddweller.platform.dbunit;

import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.model.data.FileBasedEntitySource;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.format.DataIterator;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

/**
 * Imports entities from a DbUnit XML file.<br/>
 * <br/>
 * Created at 07.11.2008 18:07:54
 *
 * @author Volker Bergmann
 * @since 0.5.6
 */
public class DbUnitEntitySource extends FileBasedEntitySource {

    Boolean flat;

    public DbUnitEntitySource(String uri, BeneratorContext context) {
        super(uri, context);
    }

    private static Boolean isFlat(String uri) {
        try {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader reader = factory.createXMLStreamReader(IOUtil.getInputStreamForURI(uri));
            DbUnitUtil.skipRootElement(reader);
            DbUnitUtil.skipNonStartTags(reader);
            return !"table".equals(reader.getLocalName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public DataIterator<Entity> iterator() {
        String resolvedUri = resolveUri();
        if (flat == null)
            flat = isFlat(resolvedUri);
        if (flat)
            return new FlatDbUnitEntityIterator(resolvedUri, context);
        else
            return new NestedDbUnitEntityIterator(resolvedUri, context);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + '[' + uri + ']';
    }

}

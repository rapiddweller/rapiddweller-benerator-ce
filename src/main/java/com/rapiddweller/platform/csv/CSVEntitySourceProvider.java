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
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.model.data.EntitySource;

/**
 * {@link EntitySource} implementation which creates {@link Iterable}s that iterate through CSV files.<br/><br/>
 * Created: 05.05.2010 14:52:01
 *
 * @author Volker Bergmann
 * @since 0.6.1
 */
public class CSVEntitySourceProvider implements DataSourceProvider<Entity> {

  private final ComplexTypeDescriptor entityType;
  private final Converter<String, ?> converter;
  private final char separator;
  private final String encoding;

  public CSVEntitySourceProvider(ComplexTypeDescriptor entityType,
                                 Converter<String, ?> converter,
                                 char separator, String encoding) {
    this.entityType = entityType;
    this.converter = converter;
    this.separator = separator;
    this.encoding = encoding;
  }

  @Override
  public EntitySource create(String uri, BeneratorContext context) {
    CSVEntitySource source =
        new CSVEntitySource(uri, entityType, encoding, converter,
            separator);
    source.setContext(context);
    return source;
  }

}

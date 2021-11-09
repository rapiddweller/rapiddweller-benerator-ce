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

package com.rapiddweller.platform.db;

import com.rapiddweller.format.DataIterator;
import com.rapiddweller.format.DataSource;
import com.rapiddweller.model.data.AbstractEntitySource;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.Entity;

import java.sql.ResultSet;

/**
 * Iterates a ResultSet, returning Entities.
 * @author Volker Bergmann
 * @since 0.3.04
 */
public class EntityResultSetDataSource extends AbstractEntitySource {

  private final DataSource<ResultSet> source;
  private final ComplexTypeDescriptor entityDescriptor;

  public EntityResultSetDataSource(DataSource<ResultSet> source, ComplexTypeDescriptor entityDescriptor) {
    this.source = source;
    this.entityDescriptor = entityDescriptor;
  }

  @Override
  public DataIterator<Entity> iterator() {
    return new ResultSetEntityIterator(source.iterator(), entityDescriptor);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + '[' + source + ']';
  }

}

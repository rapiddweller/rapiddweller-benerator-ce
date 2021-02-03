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

import com.rapiddweller.common.IOUtil;
import com.rapiddweller.format.DataContainer;
import com.rapiddweller.format.DataIterator;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.Entity;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Iterates a ResultSet returning Entity objects.
 *
 * @author Volker Bergmann |since 0.3.04
 */
public class ResultSetEntityIterator implements DataIterator<Entity> {

  private final DataIterator<ResultSet> source;

  private final ComplexTypeDescriptor descriptor;

  /**
   * Instantiates a new Result set entity iterator.
   *
   * @param source     the source
   * @param descriptor the descriptor
   */
  public ResultSetEntityIterator(DataIterator<ResultSet> source,
                                 ComplexTypeDescriptor descriptor) {
    this.source = source;
    this.descriptor = descriptor;
  }

  @Override
  public Class<Entity> getType() {
    return Entity.class;
  }

  @Override
  public DataContainer<Entity> next(DataContainer<Entity> container) {
    try {
      DataContainer<ResultSet> feed = source.next(new DataContainer<>());
      if (feed == null) {
        return null;
      }
      ResultSet resultSet = feed.getData();
      Entity result =
          ResultSet2EntityConverter.convert(resultSet, descriptor);
      return container.setData(result);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void close() {
    IOUtil.close(source);
  }

}

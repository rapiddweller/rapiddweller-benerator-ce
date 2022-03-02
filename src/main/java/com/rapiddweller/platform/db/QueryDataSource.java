/*
 * (c) Copyright 2006-2021 by rapiddweller GmbH & Volker Bergmann. All rights reserved.
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

import com.rapiddweller.common.Assert;
import com.rapiddweller.common.Context;
import com.rapiddweller.common.Converter;
import com.rapiddweller.common.ThreadAware;
import com.rapiddweller.common.converter.NoOpConverter;
import com.rapiddweller.format.DataIterator;
import com.rapiddweller.format.DataSource;
import com.rapiddweller.format.script.ScriptConverterForStrings;
import com.rapiddweller.format.util.AbstractDataSource;
import com.rapiddweller.jdbacl.QueryDataIterator;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link DataSource} implementation which is able to resolve script expressions, performs a query and
 * provides the result of a query as a {@link DataIterator} of {@link ResultSet} objects.<br/><br/>
 * Created: 03.08.2011 19:55:52
 * @author Volker Bergmann
 * @since 0.7.0
 */
public class QueryDataSource extends AbstractDataSource<ResultSet> implements ThreadAware {

  private static final Logger logger = LoggerFactory.getLogger(QueryDataSource.class);

  protected final ConnectionProvider connectionProvider;
  protected final String query;
  protected final int fetchSize;

  protected final Converter<String, ?> queryPreprocessor;

  private List<DataIterator<ResultSet>> iterators;

  public QueryDataSource(ConnectionProvider connectionProvider, String query, int fetchSize, Context context) {
    super(ResultSet.class);
    Assert.notNull(connectionProvider, "connectionProvider");
    Assert.notEmpty(query, "'query' is empty or null");
    this.connectionProvider = connectionProvider;
    this.query = query;
    this.fetchSize = fetchSize;
    if (context != null) {
      this.queryPreprocessor = new ScriptConverterForStrings(context);
    } else {
      this.queryPreprocessor = new NoOpConverter<>();
    }
    this.iterators = new ArrayList<>();
    logger.debug("Constructed QueryIterable: {}", query);
  }

  @Override
  public boolean isThreadSafe() {
    return true;
  }

  @Override
  public boolean isParallelizable() {
    return false;
  }

  public String getQuery() {
    return query;
  }

  @Override
  public DataIterator<ResultSet> iterator() {
    String renderedQuery = queryPreprocessor.convert(query).toString();
    return new QueryDataIterator(renderedQuery, connectionProvider.getConnection(), fetchSize);
  }

  @Override
  public void close() {
    synchronized (this) {
      super.close();
      for (DataIterator<ResultSet> iterator : iterators) {
        iterator.close();
      }
      this.iterators.clear();
    }
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + '[' + query + ']';
  }

}

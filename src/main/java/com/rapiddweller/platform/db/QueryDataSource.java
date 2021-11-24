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

import com.rapiddweller.common.Assert;
import com.rapiddweller.common.Context;
import com.rapiddweller.common.Converter;
import com.rapiddweller.common.converter.NoOpConverter;
import com.rapiddweller.format.DataIterator;
import com.rapiddweller.format.DataSource;
import com.rapiddweller.format.script.ScriptConverterForStrings;
import com.rapiddweller.format.util.AbstractDataSource;
import com.rapiddweller.jdbacl.QueryDataIterator;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;

/**
 * {@link DataSource} implementation which is able to resolve script expressions, performs a query and
 * provides the result of a query as a {@link DataIterator} of {@link ResultSet} objects.<br/><br/>
 * Created: 03.08.2011 19:55:52
 * @author Volker Bergmann
 * @since 0.7.0
 */
public class QueryDataSource extends AbstractDataSource<ResultSet> {

  private static final Logger logger = LoggerFactory.getLogger(QueryDataSource.class);

  private final Connection connection;
  private final String query;
  private final int fetchSize;

  private final Converter<String, ?> queryPreprocessor;
  private String renderedQuery;

  public QueryDataSource(Connection connection, String query, int fetchSize, Context context) {
    super(ResultSet.class);
    Assert.notNull(connection, "connection");
    Assert.notEmpty(query, "'query' is empty or null");
    this.connection = connection;
    this.query = query;
    this.fetchSize = fetchSize;
    if (context != null) {
      this.queryPreprocessor = new ScriptConverterForStrings(context);
    } else {
      this.queryPreprocessor = new NoOpConverter<>();
    }
    logger.debug("Constructed QueryIterable: {}", query);
  }

  public String getQuery() {
    return query;
  }

  @Override
  public DataIterator<ResultSet> iterator() {
    renderedQuery = queryPreprocessor.convert(query).toString();
    return new QueryDataIterator(renderedQuery, connection, fetchSize);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + '[' +
        (renderedQuery != null ? renderedQuery : query) + ']';
  }

}

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

import com.rapiddweller.common.Context;
import com.rapiddweller.common.Converter;
import com.rapiddweller.common.HeavyweightIterable;
import com.rapiddweller.common.HeavyweightIterator;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.common.converter.NoOpConverter;
import com.rapiddweller.format.script.ScriptConverterForStrings;
import com.rapiddweller.jdbacl.QueryIterator;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;

/**
 * Creates Iterators for stepping through query results.<br/>
 * <br/>
 * Created: 17.08.2007 18:48:20
 *
 * @author Volker Bergmann
 */
public class QueryIterable implements HeavyweightIterable<ResultSet> {

  private static final Logger logger =
      LoggerFactory.getLogger(QueryIterable.class);

  private final Connection connection;
  private final String query;
  private final int fetchSize;

  private final Converter<String, ?> queryPreprocessor;
  private String renderedQuery;

  /**
   * Instantiates a new Query iterable.
   *
   * @param connection the connection
   * @param query      the query
   * @param fetchSize  the fetch size
   * @param context    the context
   */
  public QueryIterable(Connection connection, String query, int fetchSize,
                       Context context) {
    if (connection == null) {
      throw new IllegalStateException("'connection' is null");
    }
    if (StringUtil.isEmpty(query)) {
      throw new IllegalStateException("'query' is empty or null");
    }
    this.connection = connection;
    this.query = query;
    this.fetchSize = fetchSize;
    if (context != null) {
      this.queryPreprocessor = new ScriptConverterForStrings(context);
    } else {
      this.queryPreprocessor = new NoOpConverter<>();
    }
    if (logger.isDebugEnabled()) {
      logger.debug("Constructed QueryIterable: " + query);
    }
  }

  /**
   * Gets query.
   *
   * @return the query
   */
  public String getQuery() {
    return query;
  }

  @Override
  public HeavyweightIterator<ResultSet> iterator() {
    renderedQuery = queryPreprocessor.convert(query).toString();
    return new QueryIterator(renderedQuery, connection, fetchSize);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + '[' +
        (renderedQuery != null ? renderedQuery : query) + ']';
  }

}

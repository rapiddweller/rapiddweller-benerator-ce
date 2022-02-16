/*
 * (c) Copyright 2006-2022 by rapiddweller GmbH & Volker Bergmann. All rights reserved.
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

import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.GeneratorState;
import com.rapiddweller.benerator.InvalidGeneratorSetupException;
import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.benerator.util.UnsafeNonNullGenerator;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.format.DataContainer;
import com.rapiddweller.format.DataIterator;
import com.rapiddweller.format.DataSource;
import com.rapiddweller.format.script.ScriptUtil;
import com.rapiddweller.jdbacl.DBUtil;
import com.rapiddweller.jdbacl.DatabaseDialect;
import com.rapiddweller.jdbacl.SQLUtil;

import java.io.Closeable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * Uses a database table to fetch and increment values like a database sequence.<br/><br/>
 * Created: 09.08.2010 14:44:06
 * @param <E> the type parameter
 * @author Volker Bergmann
 * @since 0.6.4
 */
public class SequenceTableGenerator<E extends Number> extends UnsafeNonNullGenerator<E> {

  protected long increment;
  private String table;
  private String column;
  private AbstractDBSystem database;
  private String selector;
  private String query;
  private IncrementorStrategy incrementorStrategy;
  private PreparedStatement parameterizedAccessorStatement;

  public SequenceTableGenerator() {
    this(null, null, null);
  }

  public SequenceTableGenerator(String table, String column, AbstractDBSystem db) {
    this(table, column, db, null);
  }

  public SequenceTableGenerator(String table, String column, AbstractDBSystem db,
                                String selector) {
    this.table = table;
    this.column = column;
    this.database = db;
    this.selector = selector;
    this.increment = 1L;
  }

  public void setTable(String table) {
    this.table = table;
  }

  public void setColumn(String column) {
    this.column = column;
  }

  public void setDatabase(AbstractDBSystem db) {
    this.database = db;
  }

  public void setSelector(String selector) {
    this.selector = selector;
  }

  public void setIncrement(long increment) {
    this.increment = increment;
  }

  // Generator interface implementation ------------------------------------------------------------------------------

  @Override
  @SuppressWarnings("unchecked")
  public Class<E> getGeneratedType() {
    return (Class<E>) Number.class;
  }

  @Override
  public void init(GeneratorContext context) throws InvalidGeneratorSetupException {
    // check preconditions
    assertNotInitialized();
    if (database == null) {
      throw new InvalidGeneratorSetupException("db is null");
    }
    // initialize
    query = SQLUtil.renderQuery(database.getCatalog(), database.getSchema(),
        table, column, selector, database.getDialect());
    incrementorStrategy = createIncrementor();
    super.init(context);
  }

  private IncrementorStrategy createIncrementor() {
    DatabaseDialect dialect = database.getDialect();
    String renderedTableName = (dialect.quoteTableNames ? '"' + table + '"' : table);
    String incrementorSql = "update " + renderedTableName + " set " + column + " = ?";
    if (selector != null) {
      incrementorSql = ScriptUtil.combineScriptableParts(incrementorSql, " where ", selector);
    }
    if (selector == null || !ScriptUtil.isScript(selector)) {
      return new PreparedStatementStrategy(incrementorSql, database);
    } else {
      return new StatementStrategy(incrementorSql, database);
    }
  }

  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public E generate() {
    if (this.state == GeneratorState.CLOSED) {
      return null;
    }
    assertInitialized();
    DataSource<?> iterable = database.query(query, true, context);
    DataIterator<?> iterator = null;
    E result;
    try {
      iterator = iterable.iterator();
      DataContainer<?> container = iterator.next(new DataContainer());
      if (container == null) {
        close();
        return null;
      }
      result = (E) container.getData();
      incrementorStrategy.run(result.longValue(), (BeneratorContext) context);
    } finally {
      IOUtil.close(iterator);
    }
    return result;
  }

  @SuppressWarnings({"unchecked", "cast"})
  public E generateWithParams(Object... params) {
    if (this.state == GeneratorState.CLOSED) {
      return null;
    }
    ResultSet resultSet = null;
    E result = null;
    try {
      if (parameterizedAccessorStatement == null) {
        String queryText = String.valueOf(ScriptUtil.parseUnspecificText(query).evaluate(context));
        parameterizedAccessorStatement = database.getConnection().prepareStatement(queryText);
      }
      for (int i = 0; i < params.length; i++) {
        parameterizedAccessorStatement.setObject(i + 1, params[i]);
      }
      resultSet = parameterizedAccessorStatement.executeQuery();
      if (!resultSet.next()) {
        close();
        return null;
      }
      result = (E) resultSet.getObject(1);
      incrementorStrategy.run(result.longValue(), (BeneratorContext) context, params);
    } catch (SQLException e) {
      throw BeneratorExceptionFactory.getInstance().queryFailed(
          "Error fetching value in " + getClass().getSimpleName(), e);
    } finally {
      DBUtil.close(resultSet);
    }
    return result;
  }

  @Override
  public void close() {
    IOUtil.close(incrementorStrategy);
    DBUtil.close(parameterizedAccessorStatement);
    super.close();
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "[" + selector + "]";
  }

  // IncrementorStrategy ---------------------------------------------------------------------------------------------

  interface IncrementorStrategy extends Closeable {
    void run(long currentValue, BeneratorContext context, Object... params);

    @Override
    void close();
  }


  class PreparedStatementStrategy implements IncrementorStrategy {

    private final PreparedStatement statement;

    public PreparedStatementStrategy(String incrementorSql, AbstractDBSystem db) {
      try {
        statement = db.getConnection().prepareStatement(incrementorSql);
      } catch (SQLException e) {
        throw BeneratorExceptionFactory.getInstance().operationFailed(
            "Error preparing statement: " + e.getMessage(), e);
      }
    }

    @Override
    public void run(long currentValue, BeneratorContext context,
                    Object... params) {
      try {
        statement.setLong(1, currentValue + increment);
        for (int i = 0; i < params.length; i++) {
          statement.setObject(2 + i, params[i]);
        }
        statement.executeUpdate();
      } catch (SQLException e) {
        throw BeneratorExceptionFactory.getInstance().operationFailed("Failed to run SQL", e);
      }
    }

    @Override
    public void close() {
      DBUtil.close(statement);
    }
  }


  class StatementStrategy implements IncrementorStrategy {

    private final Statement statement;
    private final String sql;

    public StatementStrategy(String sql, AbstractDBSystem db) {
      try {
        this.statement = db.getConnection().createStatement();
        this.sql = sql;
      } catch (SQLException e) {
        throw BeneratorExceptionFactory.getInstance().operationFailed(
            "Statement creation failed: " + sql, e);
      }
    }

    @Override
    public void run(long currentValue, BeneratorContext context,
                    Object... params) {
      try {
        String cmd = sql.replace("?",
            String.valueOf(currentValue + increment));
        cmd = ScriptUtil.parseUnspecificText(cmd).evaluate(context)
            .toString();
        statement.executeUpdate(cmd);
      } catch (SQLException e) {
        throw BeneratorExceptionFactory.getInstance().operationFailed(
            "Statement execution failed: " + sql, e);
      }
    }

    @Override
    public void close() {
      DBUtil.close(statement);
    }
  }

}

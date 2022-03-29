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

package com.rapiddweller.benerator.engine.statement;

import com.rapiddweller.benerator.BeneratorErrorIds;
import com.rapiddweller.benerator.StorageSystem;
import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.common.Assert;
import com.rapiddweller.common.BeanUtil;
import com.rapiddweller.common.Context;
import com.rapiddweller.common.ConversionException;
import com.rapiddweller.common.ErrorHandler;
import com.rapiddweller.common.ExceptionUtil;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.common.Level;
import com.rapiddweller.common.LogCategoriesConstants;
import com.rapiddweller.common.ReaderLineIterator;
import com.rapiddweller.common.ShellUtil;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.common.SystemInfo;
import com.rapiddweller.common.TextFileLocation;
import com.rapiddweller.common.converter.LiteralParserConverter;
import com.rapiddweller.common.exception.ExceptionFactory;
import com.rapiddweller.common.ui.ConsolePrinter;
import com.rapiddweller.format.script.Script;
import com.rapiddweller.format.script.ScriptUtil;
import com.rapiddweller.jdbacl.DBExecutionResult;
import com.rapiddweller.jdbacl.DBUtil;
import com.rapiddweller.platform.db.AbstractDBSystem;
import com.rapiddweller.common.Expression;
import com.rapiddweller.script.expression.ExpressionUtil;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Executes an &lt;evaluate/&gt; from an XML descriptor.<br/><br/>
 * Created at 23.07.2009 17:59:36
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class EvaluateStatement extends AbstractStatement {

  private static final Logger logger = LoggerFactory.getLogger(EvaluateStatement.class);

  public static final String TYPE_SHELL = "shell";
  public static final String TYPE_SQL = "sql";
  public static final String EXECUTE_SQL = "execute_sql";

  private static final Map<String, String> extensionMap;

  static {
    try {
      extensionMap = IOUtil.readProperties("com/rapiddweller/benerator/engine/statement/fileTypes.properties");
    } catch (Exception e) {
      throw ExceptionFactory.getInstance().configurationError("Failed to read extension type map", e);
    }
  }

  protected final boolean evaluate;
  protected final String id;
  protected final Expression<String> textEx;
  protected final Expression<String> uriEx;
  protected final String type;
  protected final Expression<?> targetObjectEx;
  protected final Expression<Character> separatorEx;
  protected final Expression<String> onErrorEx;
  protected final Expression<String> encodingEx;
  protected final Expression<Boolean> optimizeEx;
  protected final Expression<Boolean> invalidateEx;
  protected final Expression<?> assertionEx;
  protected final Expression<String> shellEx;
  protected final TextFileLocation location;

  public EvaluateStatement(
      boolean evaluate, String id, Expression<String> textEx,
      Expression<String> uriEx, String type, Expression<?> targetObjectEx, Expression<String> shellEx,
      Expression<Character> separatorEx, Expression<String> onErrorEx, Expression<String> encodingEx,
      Expression<Boolean> optimizeEx, Expression<Boolean> invalidateEx, Expression<?> assertionEx,
      TextFileLocation location) {
    this.evaluate = evaluate;
    this.id = id;
    this.textEx = textEx;
    this.uriEx = uriEx;
    this.type = type;
    this.targetObjectEx = targetObjectEx;
    this.shellEx = shellEx;
    this.separatorEx = separatorEx;
    this.onErrorEx = onErrorEx;
    this.encodingEx = encodingEx;
    this.optimizeEx = optimizeEx;
    this.invalidateEx = invalidateEx;
    this.assertionEx = assertionEx;
    this.location = location;
  }

  @Override
  public boolean execute(BeneratorContext context) {
    try {
      String onErrorValue = ExpressionUtil.evaluateWithDefault(onErrorEx, "fatal", context);
      String uriValue = evaluateUri(context);
      Object targetObject = ExpressionUtil.evaluate(targetObjectEx, context);
      String typeValue = evaluateType(context, uriValue, targetObject);
      String encoding = ExpressionUtil.evaluate(encodingEx, context);
      String text = ExpressionUtil.evaluate(textEx, context);
      String shell = ExpressionUtil.evaluate(shellEx, context);

      // run
      Object result;
      if (TYPE_SQL.equals(typeValue)) {
        result = evaluateAsSql(context, onErrorValue, uriValue, targetObject, encoding, text);
      } else if (TYPE_SHELL.equals(typeValue) || !StringUtil.isEmpty(shell)) {
        result = runShell(uriValue, text, shell, onErrorValue);
      } else if (EXECUTE_SQL.equals(typeValue)) {
        result = ((StorageSystem) targetObject).execute(text);
      } else {
        result = evaluateAsScript(context, onErrorValue, uriValue, typeValue, text);
      }
      context.setGlobal("result", result);
      evaluateAssertion(result, onErrorValue, context);
      exportResultWithId(result, context);
      return true;
    } catch (ConversionException e) {
      throw ExceptionFactory.getInstance().configurationError("Error executing statement", e);
    }
  }

  // private helpers -------------------------------------------------------------------------------------------------

  private void exportResultWithId(Object result, BeneratorContext context) {
    if (id != null) {
      context.setGlobal(id, result);
    }
  }

  private void evaluateAssertion(Object result, String onErrorValue, BeneratorContext context) {
    Object assertionValue = ExpressionUtil.evaluate(assertionEx, context);
    if (assertionValue instanceof String) {
      assertionValue = LiteralParserConverter.parse((String) assertionValue);
    }
    if (assertionValue != null && !(assertionValue instanceof String && ((String) assertionValue).length() == 0)) {
      if (assertionValue instanceof Boolean) {
        if (!(boolean) assertionValue) {
          getErrorHandler(onErrorValue).handleError("Assertion failed: '" + assertionEx + "'");
        }
      } else {
        if (!BeanUtil.equalsIgnoreType(assertionValue, result)) {
          getErrorHandler(onErrorValue).handleError("Assertion failed. Expected: '" + assertionValue + "', found: '" + result + "'");
        }
      }
    }
  }

  private Object evaluateAsSql(BeneratorContext context, String onErrorValue, String uriValue, Object targetObject, String encoding, String text) {
    Object result;
    Character separator = ExpressionUtil.evaluate(separatorEx, context);
    if (separator == null) {
      separator = ';';
    }
    boolean optimize = ExpressionUtil.evaluateWithDefault(optimizeEx, false, context);
    Boolean invalidate = ExpressionUtil.evaluate(invalidateEx, context);
    DBExecutionResult executionResult = runSql(uriValue, targetObject, onErrorValue, encoding,
        text, separator, optimize, invalidate);
    result = (executionResult != null ? executionResult.result : null);
    return result;
  }

  private Object evaluateAsScript(
      BeneratorContext context, String onErrorValue, String uriValue, String typeValue, String text) {
    Object result;
    if (typeValue == null) {
      typeValue = context.getDefaultScript();
    }
    if (!StringUtil.isEmpty(uriValue)) {
      text = IOUtil.getContentOfURI(uriValue);
    }
    result = runScript(text, typeValue, onErrorValue, context);
    return result;
  }

  private String evaluateUri(BeneratorContext context) {
    String uriValue = ExpressionUtil.evaluate(uriEx, context);
    if (uriValue != null) {
      uriValue = context.resolveRelativeUri(uriValue);
    }
    return uriValue;
  }

  private String evaluateType(BeneratorContext context, String uriValue, Object targetObject) {
    String typeValue = type;
    if (type == null && uriEx != null) {
      typeValue = mapExtensionOf(uriValue); // if type is not defined, derive it from the file extension
      typeValue = checkOs(uriValue, typeValue); // for shell scripts, check the OS
    }
    if (typeValue == null) {
      if (targetObject instanceof AbstractDBSystem) {
        typeValue = TYPE_SQL;
      } else if (targetObject instanceof StorageSystem) {
        typeValue = EXECUTE_SQL;
      }
    }
    return typeValue;
  }

  private String checkOs(String uriValue, String typeValue) {
    if ("winshell".equals(typeValue)) {
      if (!SystemInfo.isWindows()) {
        throw ExceptionFactory.getInstance().configurationError("Need Windows to run file: " + uriValue);
      } else {
        typeValue = TYPE_SHELL;
      }
    } else if ("unixshell".equals(typeValue)) {
      if (SystemInfo.isWindows()) {
        throw ExceptionFactory.getInstance().configurationError("Need Unix system to run file: " + uriValue);
      } else {
        typeValue = TYPE_SHELL;
      }
    }
    return typeValue;
  }

  private static String mapExtensionOf(String uri) {
    String lcUri = uri.toLowerCase();
    for (Entry<String, String> entry : extensionMap.entrySet()) {
      if (lcUri.endsWith(entry.getKey())) {
        return entry.getValue();
      }
    }
    return null;
  }

  private ErrorHandler getErrorHandler(String level) {
    return new ErrorHandler(getClass().getName(), Level.valueOf(level));
  }

  private Object runScript(String text, String type, String onError, Context context) {
    ErrorHandler errorHandler = new ErrorHandler(getClass().getName(), Level.valueOf(onError));
    boolean evaluating = false;
    try {
      Script script = ScriptUtil.parseScriptText(text, type);
      evaluating = true;
      return script.evaluate(context);
    } catch (Exception e) {
      if (evaluating) {
        RuntimeException e2 = BeneratorExceptionFactory.getInstance().scriptEvaluationFailed(
                "Error evaluating script", e, text, location);
        errorHandler.handleError(e2.getMessage(), e2);
        return null;
      } else {
        RuntimeException e2 = BeneratorExceptionFactory.getInstance().syntaxErrorForText(
                "Error parsing script", e, BeneratorErrorIds.SYN_EVALUATE, location);
        errorHandler.handleError(e2.getMessage(), e2);
        return null;
      }
    }
  }

  private Object runShell(String uri, String text, String shell, String onError) {
    ErrorHandler errorHandler = new ErrorHandler(getClass().getName(), Level.valueOf(onError));
    StringWriter writer = new StringWriter();
    if (text != null) {
      ShellUtil.runShellCommands(new ReaderLineIterator(new StringReader(text)), shell, writer, errorHandler);
    } else if (uri != null) {
      ShellUtil.runShellCommand(uri, shell, writer, errorHandler);
    } else {
      throw ExceptionFactory.getInstance().configurationError("At least uri or text must be provided in <execute> and <evaluate>");
    }
    String output = writer.toString();
    ConsolePrinter.printStandard(output);
    return LiteralParserConverter.parse(output);
  }

  private DBExecutionResult runSql(String uri, Object targetObject, String onError, String encoding, String text,
      char separator, boolean optimize, Boolean invalidate) {
    if (targetObject == null) {
      throw ExceptionFactory.getInstance().configurationError("Please specify the 'target' database to execute the SQL script");
    }
    Assert.instanceOf(targetObject, AbstractDBSystem.class, "target");
    AbstractDBSystem db = (AbstractDBSystem) targetObject;
    if (uri != null) {
      logger.info("Executing script {}", uri);
    } else if (text != null) {
      logger.info("Executing inline script");
    } else {
      throw BeneratorExceptionFactory.getInstance().missingInfo("No uri or content");
    }
    Connection connection = null;
    DBExecutionResult result = null;
    ErrorHandler errorHandler = new ErrorHandler(LogCategoriesConstants.SQL, Level.valueOf(onError));
    try {
      connection = db.getConnection();
      if (text != null) {
        result = DBUtil.executeScript(text, separator, connection, optimize, errorHandler);
      } else {
        result = DBUtil.executeScriptFile(uri, encoding, separator, connection, optimize, errorHandler);
      }
      if (Boolean.TRUE.equals(invalidate) || (invalidate == null && !evaluate && result.changedStructure)) {
        db.invalidate(); // possibly we changed the database structure
      }
    } catch (Exception sqle) {
      if (connection != null) {
        try {
          connection.rollback();
        } catch (SQLException e) {
          // ignore this 2nd exception, we have other problems now (-> sqle)
        }
      }
      Throwable cause = ExceptionUtil.getRootCause(sqle);
      errorHandler.handleError("Error in SQL script execution", cause);
    }
    return result;
  }

}

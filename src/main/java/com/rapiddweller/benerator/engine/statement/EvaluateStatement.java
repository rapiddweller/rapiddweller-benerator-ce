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

import com.rapiddweller.benerator.StorageSystem;
import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.common.Assert;
import com.rapiddweller.common.BeanUtil;
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.common.Context;
import com.rapiddweller.common.ConversionException;
import com.rapiddweller.common.ErrorHandler;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.common.Level;
import com.rapiddweller.common.LogCategoriesConstants;
import com.rapiddweller.common.ReaderLineIterator;
import com.rapiddweller.common.ShellUtil;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.common.SystemInfo;
import com.rapiddweller.common.converter.LiteralParser;
import com.rapiddweller.format.script.Script;
import com.rapiddweller.format.script.ScriptUtil;
import com.rapiddweller.jdbacl.DBExecutionResult;
import com.rapiddweller.jdbacl.DBUtil;
import com.rapiddweller.platform.db.AbstractDBSystem;
import com.rapiddweller.script.Expression;
import com.rapiddweller.script.expression.ExpressionUtil;
import com.rapiddweller.task.TaskException;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.IOException;
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

  private static final String TYPE_SHELL = "shell";

  private static final Map<String, String> extensionMap;

  static {
    try {
      extensionMap = IOUtil.readProperties("com/rapiddweller/benerator/engine/statement/fileTypes.properties");
    } catch (Exception e) {
      throw new ConfigurationError("Failed to read extension type map", e);
    }
  }

  protected final boolean evaluate;
  protected final Expression<String> idEx;
  protected final Expression<String> textEx;
  protected final Expression<String> uriEx;
  protected final Expression<String> typeEx;
  protected final Expression<?> targetObjectEx;
  protected final Expression<Character> separatorEx;
  protected final Expression<String> onErrorEx;
  protected final Expression<String> encodingEx;
  protected final Expression<Boolean> optimizeEx;
  protected final Expression<Boolean> invalidateEx;
  protected final Expression<?> assertionEx;
  protected final Expression<String> shellEx;

  public EvaluateStatement(
      boolean evaluate, Expression<String> idEx, Expression<String> textEx,
     Expression<String> uriEx, Expression<String> typeEx, Expression<?> targetObjectEx, Expression<String> shellEx,
     Expression<Character> separatorEx, Expression<String> onErrorEx, Expression<String> encodingEx,
     Expression<Boolean> optimizeEx, Expression<Boolean> invalidateEx, Expression<?> assertionEx) {
    this.evaluate = evaluate;
    this.idEx = idEx;
    this.textEx = textEx;
    this.uriEx = uriEx;
    this.typeEx = typeEx;
    this.targetObjectEx = targetObjectEx;
    this.shellEx = shellEx;
    this.separatorEx = separatorEx;
    this.onErrorEx = onErrorEx;
    this.encodingEx = encodingEx;
    this.optimizeEx = optimizeEx;
    this.invalidateEx = invalidateEx;
    this.assertionEx = assertionEx;
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
      Object result = null;
      if ("sql".equals(typeValue)) {
        result = evaluateAsSql(context, onErrorValue, uriValue, targetObject, encoding, text);
      } else if (TYPE_SHELL.equals(typeValue) || !StringUtil.isEmpty(shell)) {
        result = runShell(uriValue, text, shell, onErrorValue);
      } else if ("execute".equals(typeValue)) {
        result = ((StorageSystem) targetObject).execute(text);
      } else {
        result = evaluateAsScript(context, onErrorValue, uriValue, typeValue, text);
      }
      context.setGlobal("result", result);
      evaluateAssertion(result, onErrorValue, context);
      exportResultWithId(result, context);
      return true;
    } catch (ConversionException e) {
      throw new ConfigurationError("Error executing statement", e);
    }
  }

  // private helpers -------------------------------------------------------------------------------------------------

  private void exportResultWithId(Object result, BeneratorContext context) {
    String idValue = ExpressionUtil.evaluate(idEx, context);
    if (idValue != null) {
      context.setGlobal(idValue, result);
    }
  }

  private void evaluateAssertion(Object result, String onErrorValue, BeneratorContext context) {
    Object assertionValue = ExpressionUtil.evaluate(assertionEx, context);
    if (assertionValue instanceof String) {
      assertionValue = LiteralParser.parse((String) assertionValue);
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

  private Object evaluateAsScript(BeneratorContext context, String onErrorValue, String uriValue, String typeValue, String text) {
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
    String typeValue = ExpressionUtil.evaluate(typeEx, context);
    if (typeValue == null && uriEx != null) {
      typeValue = mapExtensionOf(uriValue); // if type is not defined, derive it from the file extension
      typeValue = checkOs(uriValue, typeValue); // for shell scripts, check the OS
    }
    if (typeValue == null) {
      if (targetObject instanceof AbstractDBSystem) {
        typeValue = "sql";
      } else if (targetObject instanceof StorageSystem) {
        typeValue = "execute";
      }
    }
    return typeValue;
  }

  private String checkOs(String uriValue, String typeValue) {
    if ("winshell".equals(typeValue)) {
      if (!SystemInfo.isWindows()) {
        throw new ConfigurationError("Need Windows to run file: " + uriValue);
      } else {
        typeValue = TYPE_SHELL;
      }
    } else if ("unixshell".equals(typeValue)) {
      if (SystemInfo.isWindows()) {
        throw new ConfigurationError("Need Unix system to run file: " + uriValue);
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
    ErrorHandler errorHandler = new ErrorHandler(getClass().getName(),
        Level.valueOf(onError));
    try {
      Script script = ScriptUtil.parseScriptText(text, type);
      return script.evaluate(context);
    } catch (Exception e) {
      errorHandler.handleError("Error in script evaluation", e);
      return null;
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
      throw new ConfigurationError("At least uri or text must be provided in <execute> and <evaluate>");
    }
    String output = writer.toString();
    System.out.println(output);
    return LiteralParser.parse(output);
  }

  private DBExecutionResult runSql(
      String uri, Object targetObject, String onError, String encoding, String text,
      char separator, boolean optimize, Boolean invalidate) {
    if (targetObject == null) {
      throw new ConfigurationError("Please specify the 'target' database to execute the SQL script");
    }
    Assert.instanceOf(targetObject, AbstractDBSystem.class, "target");
    AbstractDBSystem db = (AbstractDBSystem) targetObject;
    if (uri != null) {
      logger.info("Executing script {}", uri);
    } else if (text != null) {
      logger.info("Executing inline script");
    } else {
      throw new TaskException("No uri or content");
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
          // ignore this 2nd exception, we have other problems now (sqle)
        }
      }
      errorHandler.handleError("Error in SQL script execution", sqle);
    }
    return result;
  }

}

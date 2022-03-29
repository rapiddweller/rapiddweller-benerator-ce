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

import com.rapiddweller.benerator.engine.DefaultBeneratorContext;
import com.rapiddweller.benerator.engine.expression.ScriptExpression;
import com.rapiddweller.benerator.storage.AbstractStorageSystem;
import com.rapiddweller.common.Context;
import com.rapiddweller.common.Encodings;
import com.rapiddweller.common.OperationFailed;
import com.rapiddweller.format.DataSource;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.model.data.TypeDescriptor;
import com.rapiddweller.common.Expression;
import com.rapiddweller.script.expression.ConstantExpression;
import com.rapiddweller.script.expression.ExpressionUtil;
import org.junit.Assume;
import org.junit.Test;

import static com.rapiddweller.common.SystemInfo.isLinux;
import static com.rapiddweller.common.SystemInfo.isMacOsx;
import static com.rapiddweller.common.SystemInfo.isSolaris;
import static com.rapiddweller.common.SystemInfo.isWindows;
import static com.rapiddweller.script.expression.ExpressionUtil.constant;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests the {@link EvaluateStatement}.<br/><br/>
 * Created: 12.02.2010 13:18:42
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class EvaluateStatementTest extends AbstractStatementTest {

  @Test
  public void testInlineJavaScript() {
    EvaluateStatement stmt = new EvaluateStatement(
        true, "message", constant("'Hello World'"), null, null,
        null, null, null, constant("fatal"), constant(Encodings.UTF_8),
        constant(false), null, null);
    stmt.execute(context);
    assertEquals("Hello World", context.get("message"));
  }

  @Test
  public void testUriMapping() {
    EvaluateStatement stmt = new EvaluateStatement(
        true, "message", null, constant("/com/rapiddweller/benerator/engine/statement/HelloWorld.js"),
        null, null, null, null, constant("fatal"),
        constant(Encodings.UTF_8), constant(false), null, null);
    stmt.execute(context);
    assertEquals("Hello World", context.get("message"));
  }

  @Test
  public void testShell_macos() {
    Assume.assumeTrue(isMacOsx());
    checkEchoJavaHomeIx(null);
  }

  @Test
  public void testShell_macos_default() {
    Assume.assumeTrue(isMacOsx());
    checkEchoJavaHomeIx(null);
  }

  @Test
  public void testShell_macos_bsh() {
    Assume.assumeTrue(isMacOsx());
    checkEchoJavaHomeIx("bash");
  }

  @Test
  public void testShell_solaris() {
    Assume.assumeTrue(isSolaris());
    checkEchoJavaHomeIx(null);
  }

  @Test
  public void testShell_unix() {
    Assume.assumeTrue(isLinux());
    checkEchoJavaHomeIx(null);
  }

  @Test
  public void testShell_windows() {
    Assume.assumeTrue(isWindows());
    checkEchoJavaHomeWin();
  }

  @Test
  public void testStorageSystem() {
    StSys stSys = new StSys();
    Expression<StSys> stSysEx = ExpressionUtil.constant(stSys);
    EvaluateStatement stmt = new EvaluateStatement(
        true, "message", constant("HelloHi"), null,
        null, stSysEx, null, null, constant("fatal"),
        constant(Encodings.UTF_8), constant(false), null, null);
    stmt.execute(context);
    assertEquals("HelloHi", stSys.execInfo);
  }

  @Test(expected = OperationFailed.class)
  public void testAssertFailed() {
    DefaultBeneratorContext context = new DefaultBeneratorContext();
    EvaluateStatement s = new EvaluateStatement(true, null, new ConstantExpression<>("1"),
        null, null, null, null,
        null, new ConstantExpression<>("fatal"),
        null, null, null, new ScriptExpression<>("result == 2"));
    s.execute(context);
  }

  // private helpers ---------------------------------------------------------------------------------------------------

  private void checkEchoJavaHomeIx(String shell) {
    String expectedResult = System.getenv("JAVA_HOME");
    assertNotNull(expectedResult);
    EvaluateStatement stmt = new EvaluateStatement(
        true, "result", constant("echo $JAVA_HOME"), null,
        "shell", null, constant(shell), null, constant("fatal"),
        constant(Encodings.UTF_8), constant(false), null, null);
    stmt.execute(context);
    assertEquals(expectedResult, context.get("result"));

  }

  private void checkEchoJavaHomeWin() {
    String expectedResult = System.getenv("JAVA_HOME");
    assertNotNull(expectedResult);
    EvaluateStatement stmt = new EvaluateStatement(
        true, "result", constant("echo %JAVA_HOME%"), null,
        "shell", null, null, null, constant("fatal"),
        constant(Encodings.UTF_8), constant(false), null, null);
    stmt.execute(context);
    assertEquals(expectedResult, context.get("result"));
  }


  public static class StSys extends AbstractStorageSystem {

    protected String execInfo;

    @Override
    public TypeDescriptor[] getTypeDescriptors() {
      return new TypeDescriptor[0];
    }

    @Override
    public TypeDescriptor getTypeDescriptor(String typeName) {
      return null;
    }

    @Override
    public String getId() {
      return "id";
    }

    @Override
    public DataSource<Entity> queryEntities(String type,
                                            String selector, Context context) {
      return null;
    }

    @Override
    public DataSource<?> queryEntityIds(String entityName,
                                        String selector, Context context) {
      return null;
    }

    @Override
    public DataSource<?> query(String selector, boolean simplify, Context context) {
      return null;
    }

    @Override
    public void store(Entity entity) {
    }

    @Override
    public void update(Entity entity) {
    }

    @Override
    public Object execute(String command) {
      this.execInfo = command;
      return command;
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() {
    }

  }

}

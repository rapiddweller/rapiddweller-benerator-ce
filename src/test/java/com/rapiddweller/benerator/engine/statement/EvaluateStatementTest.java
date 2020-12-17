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

package com.rapiddweller.benerator.engine.statement;

import com.rapiddweller.benerator.storage.AbstractStorageSystem;
import com.rapiddweller.commons.Context;
import com.rapiddweller.commons.Encodings;
import com.rapiddweller.commons.SystemInfo;
import com.rapiddweller.formats.DataSource;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.model.data.TypeDescriptor;
import com.rapiddweller.script.Expression;
import com.rapiddweller.script.expression.ExpressionUtil;
import org.junit.Ignore;
import org.junit.Test;

import static com.rapiddweller.script.expression.ExpressionUtil.*;
import static org.junit.Assert.*;

/**
 * Tests the {@link EvaluateStatement}.<br/><br/>
 * Created: 12.02.2010 13:18:42
 *
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class EvaluateStatementTest extends AbstractStatementTest {

    @Test
    public void testInlineJavaScript() {
        EvaluateStatement stmt = new EvaluateStatement(
                true,
                constant("message"),
                constant("'Hello World'"),
                null,
                null,
                null,
                null,
                constant("fatal"),
                constant(Encodings.UTF_8),
                constant(false),
                null,
                null);
        stmt.execute(context);
        assertEquals("Hello World", context.get("message"));
    }

    @Ignore
    @Test
    public void testUriMapping() {
        EvaluateStatement stmt = new EvaluateStatement(
                true,
                constant("message"),
                null,
                constant("/com/rapiddweller/benerator/engine/statement/HelloWorld.js"),
                null,
                null,
                null,
                constant("fatal"),
                constant(Encodings.UTF_8),
                constant(false),
                null,
                null);
        stmt.execute(context);
        assert(context.get("message") == "\"Hello World\"" || context.get("message") == "Hello World");
    }

    @Test
    public void testShell() {
        String cmd = "echo 42";
        if (SystemInfo.isWindows())
            cmd = "cmd.exe /C " + cmd;
        EvaluateStatement stmt = new EvaluateStatement(
                true,
                constant("result"),
                constant(cmd),
                null,
                constant("shell"),
                null,
                null,
                constant("fatal"),
                constant(Encodings.UTF_8),
                constant(false),
                null,
                null);
        stmt.execute(context);
        assertEquals(42, context.get("result"));
    }

    @Test
    public void testStorageSystem() {
        StSys stSys = new StSys();
        Expression<StSys> stSysEx = ExpressionUtil.constant(stSys);
        EvaluateStatement stmt = new EvaluateStatement(
                true,
                constant("message"),
                constant("HelloHi"),
                null,
                null,
                stSysEx,
                null,
                constant("fatal"),
                constant(Encodings.UTF_8),
                constant(false),
                null,
                null);
        stmt.execute(context);
        assertEquals("HelloHi", stSys.execInfo);
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

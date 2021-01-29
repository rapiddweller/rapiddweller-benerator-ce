/*
 *
 *  * (c) Copyright 2006-2021 by rapiddweller GmbH & Volker Bergmann. All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, is permitted under the terms of the
 *  * GNU General Public License.
 *  *
 *  * For redistributing this software or a derivative work under a license other
 *  * than the GPL-compatible Free Software License as defined by the Free
 *  * Software Foundation or approved by OSI, you must first obtain a commercial
 *  * license to this software product from rapiddweller GmbH & Volker Bergmann.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *  * WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED CONDITIONS,
 *  * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF
 *  * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE
 *  * HEREBY EXCLUDED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 *  * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 *  * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 *  * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 *  * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 *  * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 *  * POSSIBILITY OF SUCH DAMAGE.
 *
 */

package com.rapiddweller.benerator.script;

import com.rapiddweller.benerator.script.graaljs.GraalJsScriptFactory;
import com.rapiddweller.benerator.script.graalpy.GraalPyScriptFactory;
import com.rapiddweller.common.Context;
import com.rapiddweller.common.context.DefaultContext;
import com.rapiddweller.format.script.ScriptUtil;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test the ScriptSupport class.
 * Created: 29.01.2021 17:38:51
 * @author Alexander Kell
 */

public class ScriptSupportTest {

    @Test
    public void GraalJsTest() {
        ScriptUtil.addFactory("js", new GraalJsScriptFactory());
        Context context = new DefaultContext();
        context.set("i", 5);
        assertEquals(14, ScriptUtil.evaluate("{js:4+5+i}", context));
        assertEquals("The number is 0\n" +
                "The number is 1\n" +
                "The number is 2\n" +
                "The number is 3\n" +
                "The number is 4\n", ScriptUtil.evaluate("{js:var text = \"\";\n" +
                "var GraalJsTest;\n" +
                "for (GraalJsTest = 0; GraalJsTest < 5; GraalJsTest++) {\n" +
                "  text += \"The number is \" + GraalJsTest + \"\\n\";\n" +
                "}}", context));
    }

    @Ignore
    @Test
    public void GraalPythonTest() {
        Context context = new DefaultContext();
        ScriptUtil.addFactory("py", new GraalPyScriptFactory());
        context.set("i", 5);

        System.out.println("Evaluate Python calc ...");
        Integer IntResultExpected = 14;
        assertEquals(IntResultExpected, ScriptUtil.evaluate("{py:4+5+i}", context));

        String StringResultExpected = "this is my number 5 ...";
        System.out.println("Evaluate Python fstring ...");
        ScriptUtil.evaluate("{py: ", context);
        assertEquals(StringResultExpected, ScriptUtil.evaluate("{py:f'this is my number {i} ...'}", context));
    }
}

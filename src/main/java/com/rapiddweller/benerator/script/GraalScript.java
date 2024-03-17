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

package com.rapiddweller.benerator.script;

import com.rapiddweller.common.Assert;
import com.rapiddweller.common.Context;
import com.rapiddweller.common.exception.ScriptException;
import com.rapiddweller.format.script.Script;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.Value;

import java.io.IOException;
import java.io.Writer;

/**
 * Provides {@link Script} functionality based on GraalVM: Scripting for the Java platform.<br/><br/>
 * Created at 30.12.2020
 *
 * @author Alexander Kell
 * @since 1.1.0
 */
public class GraalScript implements Script {
    static final PolyglotContext globalPolyglotCtx = new PolyglotContext();
    private final String text;
    private final String language;

    public GraalScript(String text, Engine scriptEngine, String languageId) {
        Assert.notEmpty(text, "text");
        Assert.notNull(scriptEngine, "engine");
        this.text = text;
        this.language = languageId;
    }

    @Override
    public synchronized Object evaluate(Context context) throws ScriptException {
        try {
            Value returnValue = globalPolyglotCtx.evalScript(context, text, language);
            GraalValueConverter converter = new GraalValueConverter();
            return converter.convert(returnValue);
        } catch (IllegalStateException e) {
            if(e.getMessage().contains("Multi threaded access requested")) {
                throw new ScriptException(String.format("Multi thread access requested. GraalVM Context is not thread safe. " +
                        "Please check your script and make sure there is no multi thread access with '%s'", text), null);
            }
            else {
                throw new ScriptException("Error evaluating script: " + text, e);
            }
        }
    }

    @Override
    public void execute(Context context, Writer out) throws ScriptException, IOException {
        out.write(String.valueOf(evaluate(context)));
    }

    @Override
    public String toString() {
        return text;
    }
}



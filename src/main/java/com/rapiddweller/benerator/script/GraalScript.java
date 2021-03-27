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
import com.rapiddweller.common.converter.GraalValueConverter;
import com.rapiddweller.common.converter.AnyConverter;
import com.rapiddweller.domain.address.Address;
import com.rapiddweller.domain.address.City;
import com.rapiddweller.domain.organization.CompanyName;
import com.rapiddweller.domain.person.Person;
import com.rapiddweller.format.script.Script;
import com.rapiddweller.format.script.ScriptException;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.platform.map.Entity2MapConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyObject;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides {@link Script} functionality based on GraalVM: Scripting for the Java platform.
 * <p>
 * Created at 30.12.2020
 *
 * @author Alexander Kell
 * @since 1.1.0
 */
public class GraalScript implements Script {

  private final String text;
  private final String language;
  private static final org.graalvm.polyglot.Context polyglotCtx =
      org.graalvm.polyglot.Context
          .newBuilder("js")
//          .allowHostAccess(HostAccess.ALL)
//          //allows access to all Java classes
//          .allowHostClassLookup(className -> true)
          .allowAllAccess(true).build();
  private static final Logger LOGGER = LogManager.getLogger(GraalScript.class);

  /**
   * Instantiates a new Graal script.
   *
   * @param text         the text
   * @param scriptEngine the script engine
   * @param languageId   the language id
   */
  public GraalScript(String text, Engine scriptEngine, String languageId) {
    Assert.notEmpty(text, "text");
    Assert.notNull(scriptEngine, "engine");
    this.text = text;
    this.language = languageId;
  }

  @Override
  public Object evaluate(Context context) throws ScriptException {
    migrateBeneratorContext2GraalVM(context);

    Value returnValue = polyglotCtx.eval(this.language, text);
    GraalValueConverter converter = new GraalValueConverter();
    return converter.convert(returnValue);
  }

  private void migrateBeneratorContext2GraalVM(Context context) {
    // add benerator context to graalvm script context
    Object valueType;
    try {
      for (Map.Entry<String, Object> entry : context.entrySet()) {
        try {
          valueType = entry.getValue() != null ? entry.getValue().getClass() : null;
        } catch (NullPointerException e) {
          LOGGER.fatal("Key {} produced NullPointerException, this should not happen!", entry.getKey());
          continue;
        }
        if (valueType == null) {
          continue;
        }
        // check if Entity Object
        if (Entity.class.equals(valueType)) {
          LOGGER.debug("Entity found : {}", entry.getKey());
          Map<String, Object> map = new Entity2MapConverter().convert((Entity) entry.getValue());
          // to access items of map in polyglotCtx it is nessesary to create an ProxyObject
          // TODO: might should create an Entity2ProxyObjectConverter in 1.2.0
          ProxyObject proxy = ProxyObject.fromMap(map);
          polyglotCtx.getBindings(this.language).putMember(entry.getKey(), proxy);
        } else{
          LOGGER.debug("{} found : {}", valueType.getClass(), entry.getKey());
          polyglotCtx.getBindings(this.language).putMember(entry.getKey(), entry.getValue());
        }
      }
    } catch (NullPointerException e) {
      LOGGER.fatal("Context {} was NULL, this should not happen!", context);
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

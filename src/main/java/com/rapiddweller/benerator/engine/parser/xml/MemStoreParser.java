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

package com.rapiddweller.benerator.engine.parser.xml;

import com.rapiddweller.benerator.engine.BeneratorRootStatement;
import com.rapiddweller.benerator.engine.Statement;
import com.rapiddweller.benerator.engine.statement.IfStatement;
import com.rapiddweller.benerator.engine.statement.MemStoreStatement;
import com.rapiddweller.common.CollectionUtil;
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.common.ConversionException;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.common.xml.XMLUtil;
import org.w3c.dom.Element;

import java.util.Map;

import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_ID;
import static com.rapiddweller.benerator.engine.DescriptorConstants.EL_MEMSTORE;
import static com.rapiddweller.benerator.engine.parser.xml.DescriptorParserUtil.getAttribute;

/**
 * Parses a &lt;memstore%gt; statement.<br/><br/>
 * Created: 08.03.2011 13:28:55
 *
 * @author Volker Bergmann
 * @since 0.6.6
 */
public class MemStoreParser extends AbstractBeneratorDescriptorParser {

  /**
   * Instantiates a new Mem store parser.
   */
  public MemStoreParser() {
    super(EL_MEMSTORE, CollectionUtil.toSet(ATT_ID), null, BeneratorRootStatement.class, IfStatement.class);
  }

  @Override
  public MemStoreStatement doParse(Element element, Statement[] parentPath, BeneratorParseContext context) {
    checkAttributeSupport(XMLUtil.getAttributes(element));
    try {
      String id = getAttribute(ATT_ID, element);
      return new MemStoreStatement(id, context.getResourceManager());
    } catch (ConversionException e) {
      throw new ConfigurationError("Error parsing memstore definition", e);
    }
  }

  private static void checkAttributeSupport(Map<String, String> attributes) {
    if (StringUtil.isEmpty(attributes.get(ATT_ID))) {
      throw new ConfigurationError("No id specified for <store>");
    }
    for (String key : attributes.keySet()) {
      if (!"id".equals(key)) {
        throw new ConfigurationError("Not a supported attribute of <store>: " + key);
      }
    }
  }

}

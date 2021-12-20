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

package com.rapiddweller.benerator.engine.parser.xml;

import com.rapiddweller.benerator.BeneratorFactory;
import com.rapiddweller.benerator.engine.BeneratorRootStatement;
import com.rapiddweller.benerator.engine.Statement;
import com.rapiddweller.benerator.engine.parser.string.GlobalErrorHandlerParser;
import com.rapiddweller.benerator.engine.parser.string.IdParser;
import com.rapiddweller.benerator.engine.parser.string.PlainEncodingParser;
import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.common.ArrayUtil;
import com.rapiddweller.common.Assert;
import com.rapiddweller.common.Encodings;
import com.rapiddweller.common.ErrorHandler;
import com.rapiddweller.common.LocaleUtil;
import com.rapiddweller.common.SystemInfo;
import com.rapiddweller.common.exception.SyntaxError;
import com.rapiddweller.common.parser.AbstractParser;
import com.rapiddweller.common.parser.BooleanParser;
import com.rapiddweller.common.parser.CharacterParser;
import com.rapiddweller.common.parser.FullyQualifiedClassNameParser;
import com.rapiddweller.common.parser.NonNegativeLongParser;
import com.rapiddweller.common.parser.PositiveIntegerParser;
import com.rapiddweller.common.parser.RegexBasedStringParser;
import com.rapiddweller.format.script.ScriptUtil;
import com.rapiddweller.format.xml.AttrInfo;
import com.rapiddweller.format.xml.AttrInfoSupport;
import com.rapiddweller.format.xml.XMLElementParser;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.rapiddweller.benerator.BeneratorErrorIds.*;
import static com.rapiddweller.benerator.engine.DescriptorConstants.*;

/**
 * {@link XMLElementParser} implementation for parsing a Benerator descriptor file's root XML element.<br/><br/>
 * Created: 14.12.2010 19:48:00
 * @author Volker Bergmann
 * @since 0.6.4
 */
public class SetupParser extends AbstractBeneratorDescriptorParser {

  public static final String FALSE = "false";

  protected static final AttrInfo<Long> COUNT = new AttrInfo<>(
      ATT_MAX_COUNT, false, SYN_SETUP_MAX_COUNT, new NonNegativeLongParser());

  protected static final AttrInfo<String> DEFAULT_SCRIPT = new AttrInfo<>(
      ATT_DEFAULT_SCRIPT, false, SYN_SETUP_DEF_SCRIPT, new DefaultScriptParser(), "ben");

  protected static final AttrInfo<Boolean> DEFAULT_NULL = new AttrInfo<>(
      ATT_DEFAULT_NULL, false, SYN_SETUP_DEF_NULL, new BooleanParser(), FALSE);

    protected static final AttrInfo<String> DEFAULT_ENCODING = new AttrInfo<>(
      ATT_DEFAULT_ENCODING, false, SYN_SETUP_DEF_ENCODING, new PlainEncodingParser(), Encodings.UTF_8);

  protected static final AttrInfo<String> DEFAULT_LINE_SEPARATOR = new AttrInfo<>(
    ATT_DEFAULT_LINE_SEPARATOR, false, SYN_SETUP_DEF_LINE_SEPARATOR,
      new RegexBasedStringParser("line separator", "(\\r)?\\n"), SystemInfo.LF);

  protected static final AttrInfo<String> DEFAULT_LOCALE = new AttrInfo<>(
    ATT_DEFAULT_LOCALE, false, SYN_SETUP_DEF_LOCALE, new IdParser(), Locale.getDefault().toString());

  protected static final AttrInfo<String> DEFAULT_DATASET = new AttrInfo<>(
    ATT_DEFAULT_DATASET, false, SYN_SETUP_DEF_DATASET, new IdParser(),
      LocaleUtil.getDefaultCountryCode());

  protected static final AttrInfo<Integer> DEFAULT_PAGE_SIZE = new AttrInfo<>(
    ATT_DEFAULT_PAGE_SIZE, false, SYN_SETUP_DEF_PAGE_SIZE, new PositiveIntegerParser(), "1");

  protected static final AttrInfo<Character> DEFAULT_SEPARATOR = new AttrInfo<>(
      ATT_DEFAULT_SEPARATOR, false, SYN_SETUP_DEF_SEPARATOR, new CharacterParser(), ",");

  protected static final AttrInfo<Boolean> DEFAULT_ONE_TO_ONE = new AttrInfo<>(
      ATT_DEFAULT_ONE_TO_ONE, false, SYN_SETUP_DEF_ONE_TO_ONE, new BooleanParser(), FALSE);

  protected static final AttrInfo<ErrorHandler> DEFAULT_ERR_HANDLER = new AttrInfo<>(
      ATT_DEFAULT_ERR_HANDLER, false, SYN_SETUP_DEF_ERR_HANDLER, new GlobalErrorHandlerParser(), "fatal");

  protected static final AttrInfo<Boolean> DEFAULT_IMPORTS = new AttrInfo<>(
      ATT_DEFAULT_IMPORTS, false, SYN_SETUP_DEF_IMPORTS, new BooleanParser(), "true");

  protected static final AttrInfo<Boolean> DEFAULT_SOURCE_SCRIPTED = new AttrInfo<>(
      ATT_DEFAULT_SOURCE_SCRIPTED, false, SYN_SETUP_DEF_SOURCE_SCRIPTED, new BooleanParser(), FALSE);

  protected static final AttrInfo<Boolean> ACCEPT_UNKNOWN_SIMPLE_TYPES = new AttrInfo<>(
      ATT_ACCEPT_UNKNOWN_SIMPLE_TYPES, false, SYN_SETUP_ACCEPT_UNK_SIMPLE_TYPES,
      new BooleanParser(), FALSE);

  protected static final AttrInfo<String> GENERATOR_FACTORY = new AttrInfo<>(
      ATT_GENERATOR_FACTORY, false, SYN_SETUP_GENERATOR_FACTORY,
      new FullyQualifiedClassNameParser(true));

  protected static final AttrInfoSupport ATTR_SUPPORT;

  static {
    ATTR_SUPPORT = new AttrInfoSupport(SYN_SETUP_ILLEGAL_ATTRIBUTE,
        COUNT, DEFAULT_SCRIPT, DEFAULT_NULL, DEFAULT_ENCODING, DEFAULT_LINE_SEPARATOR,
        DEFAULT_LOCALE, DEFAULT_DATASET, DEFAULT_PAGE_SIZE, DEFAULT_SEPARATOR, DEFAULT_ONE_TO_ONE,
        DEFAULT_ERR_HANDLER, DEFAULT_IMPORTS, DEFAULT_SOURCE_SCRIPTED, ACCEPT_UNKNOWN_SIMPLE_TYPES,
        GENERATOR_FACTORY);
  }

  public SetupParser() {
    super(EL_SETUP, ATTR_SUPPORT);
  }

  @Override
  public boolean supports(Element element, Element[] parentXmlPath, Statement[] parentComponentPath) {
    return (supportsElementName(element.getNodeName()) && ArrayUtil.isEmpty(parentXmlPath));
  }

  @Override
  public Statement doParse(Element element, Element[] parentXmlPath, Statement[] parentPath, BeneratorParseContext context) {
    attrSupport.validate(element);
    NamedNodeMap attrMap = element.getAttributes();
    // remove standard XML root attributes and verify that the remaining ones are legal
    Map<String, String> map = new HashMap<>(attrMap.getLength());
    for (int i = 0; i < attrMap.getLength(); i++) {
      Attr attr = (Attr) attrMap.item(i);
      if (ATTR_SUPPORT.hasAttribute(attr.getName())) {
        try {
          map.put(attr.getName(), attr.getValue());
        } catch (Exception e) {
          throw illegalAttributeValue(attr);
        }
      } else if (!isStandardXmlRootAttribute(attr.getName())) {
        throw BeneratorExceptionFactory.getInstance().illegalXmlAttributeName(
            null, null, attrSupport.getErrorIdForIllegalAttribute(), attr, attrSupport);
      }
    }
    Boolean defaultImports = parseOptionalBoolean(ATT_DEFAULT_IMPORTS, element);
    if (defaultImports == null || defaultImports) {
      BeneratorFactory.getInstance().importDefaultParsers(context);
    }
    // create root statement and configure its children
    BeneratorRootStatement rootStatement = new BeneratorRootStatement(map);
    Statement[] currentComponentPath = new Statement[] { rootStatement };
    Element[] currentXmlPath = new Element[] { element };
    List<Statement> subStatements = context.parseChildElementsOf(element, currentXmlPath, currentComponentPath);
    rootStatement.setSubStatements(subStatements);
    return rootStatement;
  }

  private SyntaxError illegalAttributeValue(Attr attr) {
    String errorId = attrSupport.getErrorId(attr.getName());
    return BeneratorExceptionFactory.getInstance().illegalXmlAttributeValue(null, null, errorId, attr);
  }

  static class DefaultScriptParser extends AbstractParser<String> {

    protected DefaultScriptParser() {
      super("script engine id");
    }

    @Override
    protected String parseImpl(String spec) {
      Assert.isTrue(ScriptUtil.supportsEngine(spec), "Unknown script engine: " + spec);
      return spec;
    }
  }
}

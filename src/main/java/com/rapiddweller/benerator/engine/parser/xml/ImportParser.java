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

import com.rapiddweller.benerator.BeneratorErrorIds;
import com.rapiddweller.benerator.DomainDescriptor;
import com.rapiddweller.benerator.PlatformDescriptor;
import com.rapiddweller.benerator.engine.Importer;
import com.rapiddweller.benerator.engine.Statement;
import com.rapiddweller.benerator.engine.parser.string.ListParser;
import com.rapiddweller.benerator.engine.statement.ImportStatement;
import com.rapiddweller.common.parser.BooleanParser;
import com.rapiddweller.common.parser.FullyQualifiedNameParser;
import com.rapiddweller.common.parser.RegexBasedStringParser;
import com.rapiddweller.format.xml.AttrInfoSupport;
import com.rapiddweller.format.xml.AttributeInfo;
import org.w3c.dom.Element;

import static com.rapiddweller.benerator.engine.DescriptorConstants.*;

/**
 * Parses an &lt;import&gt; element in a Benerator descriptor file.<br/><br/>
 * Created: 25.10.2009 00:53:06
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class ImportParser extends AbstractBeneratorDescriptorParser {

  // format spec -----------------------------------------------------------------------------------------------------

  public static final String IMPORT_CLASS_REGEX = "([a-zA-Z_$][a-zA-Z\\d_$]*\\.)*[a-zA-Z_$][a-zA-Z\\d_$]*(\\.\\*)?";

  private static final AttributeInfo<String> CLASS = new AttributeInfo<>(
    ATT_CLASS, false, BeneratorErrorIds.SYN_IMPORT_CLASS, null,
      new RegexBasedStringParser("class import", IMPORT_CLASS_REGEX));

  private static final AttributeInfo<Boolean> DEFAULTS = new AttributeInfo<>(
    ATT_DEFAULTS, false, BeneratorErrorIds.SYN_IMPORT_DEFAULTS, "false", new BooleanParser());

  private static final AttributeInfo<String[]> DOMAINS = new AttributeInfo<>(
    ATT_DOMAINS, false, BeneratorErrorIds.SYN_IMPORT_DOMAINS, null,
      new ListParser<>(new FullyQualifiedNameParser()));

  private static final AttributeInfo<String[]> PLATFORMS = new AttributeInfo<>(
    ATT_PLATFORMS, false, BeneratorErrorIds.SYN_IMPORT_PLATFORMS, null,
      new ListParser<>(new FullyQualifiedNameParser()));

  private static final AttrInfoSupport ATTR_INFO = new AttrInfoSupport(BeneratorErrorIds.SYN_IMPORT_ILLEGAL_ATTR,
      CLASS, DEFAULTS, DOMAINS, PLATFORMS);

  // constructor & interface -----------------------------------------------------------------------------------------

  public ImportParser() {
    super(EL_IMPORT, ATTR_INFO);
  }

  @Override
  public ImportStatement doParse(Element element, Element[] parentXmlPath, Statement[] parentComponentPath,
                                 BeneratorParseContext parseContext) {
    // check syntax
    assertAtLeastOneAttributeIsSet(element, ATT_DEFAULTS, ATT_DOMAINS, ATT_PLATFORMS, ATT_CLASS);
    // parse attributes
    boolean defaults = DEFAULTS.parse(element);
    String classImport = parseClass(element);
    DomainDescriptor[] domainImports = parseDomains(element);
    PlatformDescriptor[] platformImports = parsePlatforms(element);
    importPlatformParsers(parseContext, platformImports);
    return new ImportStatement(defaults, classImport, domainImports, platformImports);
  }

  // non-public helpers ----------------------------------------------------------------------------------------------

  protected String parseClass(Element element) {
    String attribute = CLASS.parse(element);
    if (attribute != null) {
      attribute = attribute.trim();
    }
    return attribute;
  }

  protected DomainDescriptor[] parseDomains(Element element) {
    String[] domainSpecs = DOMAINS.parse(element);
    if (domainSpecs != null) {
      return Importer.findDomains(domainSpecs);
    } else {
      return new DomainDescriptor[0];
    }
  }

  public static PlatformDescriptor[] parsePlatforms(Element element) {
    String[] platformSpecs = PLATFORMS.parse(element);
    if (platformSpecs != null) {
      return Importer.findPlatforms(platformSpecs, true);
    } else {
      return new PlatformDescriptor[0];
    }
  }

  protected void importPlatformParsers(BeneratorParseContext parseContext, PlatformDescriptor[] platformImports) {
    for (PlatformDescriptor descriptor : platformImports) {
      Importer.importPlatformParsers(descriptor, parseContext);
    }
  }

}


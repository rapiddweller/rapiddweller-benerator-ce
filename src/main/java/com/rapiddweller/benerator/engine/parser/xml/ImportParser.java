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

import com.rapiddweller.benerator.DefaultPlatformDescriptor;
import com.rapiddweller.benerator.PlatformDescriptor;
import com.rapiddweller.benerator.engine.Statement;
import com.rapiddweller.benerator.engine.statement.ImportStatement;
import com.rapiddweller.common.ArrayBuilder;
import com.rapiddweller.common.BeanUtil;
import com.rapiddweller.common.CollectionUtil;
import com.rapiddweller.common.ExceptionUtil;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.format.xml.XMLElementParser;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_CLASS;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_DEFAULTS;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_DOMAINS;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_PLATFORMS;
import static com.rapiddweller.benerator.engine.DescriptorConstants.EL_IMPORT;

/**
 * Parses an &lt;import&gt; element in a Benerator descriptor file.<br/><br/>
 * Created: 25.10.2009 00:53:06
 *
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class ImportParser extends AbstractBeneratorDescriptorParser {

  private static final Set<String> OPTIONAL_ATTRIBUTES = CollectionUtil.toSet(
      ATT_CLASS, ATT_DEFAULTS, ATT_DOMAINS, ATT_PLATFORMS);

  /**
   * Instantiates a new Import parser.
   */
  public ImportParser() {
    super(EL_IMPORT, null, OPTIONAL_ATTRIBUTES);
  }

  @Override
  public ImportStatement doParse(Element element, Statement[] parentPath, BeneratorParseContext context) {
    // check syntax
    assertAtLeastOneAttributeIsSet(element, ATT_DEFAULTS, ATT_DOMAINS, ATT_PLATFORMS, ATT_CLASS);

    // prepare parsing
    ArrayBuilder<String> classImports = new ArrayBuilder<>(String.class);
    ArrayBuilder<String> domainImports = new ArrayBuilder<>(String.class);

    // defaults import
    boolean defaults = ("true".equals(element.getAttribute("defaults")));

    // check class import
    String attribute = element.getAttribute("class");
    if (!StringUtil.isEmpty(attribute)) {
      classImports.add(attribute);
    }

    // (multiple) domain import
    attribute = element.getAttribute("domains");
    if (!StringUtil.isEmpty(attribute)) {
      domainImports.addAll(StringUtil.trimAll(StringUtil.tokenize(attribute, ',')));
    }

    // (multiple) platform import
    attribute = element.getAttribute("platforms");

    List<PlatformDescriptor> platformImports = null;
    if (!StringUtil.isEmpty(attribute)) {
      platformImports = importPlatforms(StringUtil.trimAll(attribute.split(",")), context);
    }

    return new ImportStatement(defaults, classImports.toArray(), domainImports.toArray(), platformImports);
  }

  private static List<PlatformDescriptor> importPlatforms(String[] platformNames, BeneratorParseContext context) {
    List<PlatformDescriptor> platforms = new ArrayList<>(platformNames.length);
    for (String platformName : platformNames) {
      PlatformDescriptor platformDescriptor = createPlatformDescriptor(platformName);
      List<XMLElementParser<Statement>> parsers = platformDescriptor.getParsers();
      if (parsers != null) {
        for (XMLElementParser<Statement> parser : parsers) {
          context.addParser(parser);
        }
      }
      platforms.add(platformDescriptor);
    }
    return platforms;
  }

  private static PlatformDescriptor createPlatformDescriptor(String platformName) {
    String platformPackage = (platformName.indexOf('.') < 0 ? "com.rapiddweller.platform." + platformName : platformName);
    String descriptorClassName = platformPackage + ".PlatformDescriptor";
    try {
      // if there is a platform descriptor, then use it
      return (PlatformDescriptor) BeanUtil.newInstance(descriptorClassName);
    } catch (RuntimeException e) {
      if (ExceptionUtil.getRootCause(e) instanceof ClassNotFoundException) { // TODO test
        return new DefaultPlatformDescriptor(platformPackage);
      } else {
        throw e;
      }
    }
  }

}

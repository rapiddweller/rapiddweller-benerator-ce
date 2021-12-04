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

import com.rapiddweller.benerator.BeneratorErrorIds;
import com.rapiddweller.benerator.BeneratorFactory;
import com.rapiddweller.benerator.DefaultPlatformDescriptor;
import com.rapiddweller.benerator.DomainDescriptor;
import com.rapiddweller.benerator.PlatformDescriptor;
import com.rapiddweller.benerator.engine.Statement;
import com.rapiddweller.benerator.engine.statement.ImportStatement;
import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.common.BeanUtil;
import com.rapiddweller.common.ExceptionUtil;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.format.xml.AttrInfoSupport;
import org.w3c.dom.Element;

import static com.rapiddweller.benerator.engine.DescriptorConstants.*;

/**
 * Parses an &lt;import&gt; element in a Benerator descriptor file.<br/><br/>
 * Created: 25.10.2009 00:53:06
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class ImportParser extends AbstractBeneratorDescriptorParser {

  private static final AttrInfoSupport ATTR_INFO;
  static {
    ATTR_INFO = new AttrInfoSupport(BeneratorErrorIds.SYN_IMPORT_ILLEGAL_ATTR);
    ATTR_INFO.add(ATT_CLASS, false, BeneratorErrorIds.SYN_IMPORT_CLASS);
    ATTR_INFO.add(ATT_DEFAULTS, false, BeneratorErrorIds.SYN_IMPORT_DEFAULTS);
    ATTR_INFO.add(ATT_DOMAINS, false, BeneratorErrorIds.SYN_IMPORT_DOMAINS);
    ATTR_INFO.add(ATT_PLATFORMS, false, BeneratorErrorIds.SYN_IMPORT_PLATFORMS);
  }

  public ImportParser() {
    super(EL_IMPORT, ATTR_INFO);
  }

  @Override
  public ImportStatement doParse(
      Element element, Element[] parentXmlPath, Statement[] parentComponentPath, BeneratorParseContext context) {
    // check syntax
    assertAtLeastOneAttributeIsSet(element, ATT_DEFAULTS, ATT_DOMAINS, ATT_PLATFORMS, ATT_CLASS);
    // defaults import
    boolean defaults = parseDefaults(element);
    String classImport = parseClass(element);
    DomainDescriptor[] domainImports = parseDomains(element);
    PlatformDescriptor[] platformImports = parsePlatforms(element, context);
    return new ImportStatement(defaults, classImport, domainImports, platformImports);
  }

  // non-public helpers ----------------------------------------------------------------------------------------------

  protected boolean parseDefaults(Element element) {
    return ("true".equals(element.getAttribute("defaults")));
  }

  protected String parseClass(Element element) {
    String attribute = element.getAttribute(ATT_CLASS);
    if (StringUtil.isEmpty(attribute)) {
      return null;
    } else {
      return attribute.trim();
    }
  }

  protected DomainDescriptor[] parseDomains(Element element) {
    String attribute = element.getAttribute(ATT_DOMAINS);
    if (StringUtil.isEmpty(attribute)) {
      return new DomainDescriptor[0];
    } else {
      String[] domainSpecs = StringUtil.trimAll(attribute.split(","));
      return importDomains(domainSpecs);
    }
  }

  private DomainDescriptor[] importDomains(String[] domainSpecs) {
    DomainDescriptor[] result = new DomainDescriptor[domainSpecs.length];
    for (int i = 0; i < domainSpecs.length; i++) {
      String domainSpec = domainSpecs[i];
      result[i] = findDescriptorForDomain(domainSpec);
    }
    return result;
  }

  private DomainDescriptor findDescriptorForDomain(String domainSpec) {
    String[] pkgs = domainPkgCandidates(domainSpec);
    for (String pkg : pkgs) {
      DomainDescriptor result = findDomainDescriptorForPackage(pkg);
      if (result != null) {
        return result;
      }
    }
    throw BeneratorExceptionFactory.getInstance().illegalArgument(
        "Domain not found: " + domainSpec, null, BeneratorErrorIds.SYN_IMPORT_DOMAINS);
  }

  private DomainDescriptor findDomainDescriptorForPackage(String pkg) {
    if (!BeanUtil.getClasses(pkg).isEmpty()) {
      // if the package exists, then create a DomainDescriptor...
      return new DomainDescriptor(pkg);
    } else {
      // ...otherwise return null
      return null;
    }
  }

  protected PlatformDescriptor[] parsePlatforms(Element element, BeneratorParseContext context) {
    String attribute = element.getAttribute(ATT_PLATFORMS);
    if (StringUtil.isEmpty(attribute)) {
      return new PlatformDescriptor[0];
    } else {
      return importPlatforms(StringUtil.trimAll(attribute.split(",")), context);
    }
  }

  private PlatformDescriptor[] importPlatforms(String[] platformNames, BeneratorParseContext context) {
    PlatformDescriptor[] platforms = new PlatformDescriptor[platformNames.length];
    for (int i = 0; i < platforms.length; i++) {
      String platformName = platformNames[i];
      PlatformDescriptor platformDescriptor = findDescriptorForPlatform(platformName);
      // the imported parsers must be registered in the phase of parsing
      // in order to be available for parsing subsequent elements
      registerParsers(platformDescriptor, context);
      platforms[i] = platformDescriptor;
    }
    return platforms;
  }

  private PlatformDescriptor findDescriptorForPlatform(String platformName) {
    String[] pkgs = platformPkgCandidates(platformName);
    for (String pkg : pkgs) {
      PlatformDescriptor result = findDescriptorForPackage(pkg);
      if (result != null) {
        return result;
      }
    }
    throw BeneratorExceptionFactory.getInstance().illegalArgument(
        "Platform not found: " + platformName, null, BeneratorErrorIds.SYN_IMPORT_PLATFORMS);
  }

  private PlatformDescriptor findDescriptorForPackage(String pkg) {
    String descriptorClassName = pkg + ".PlatformDescriptor";
    try {
      // if there is a platform descriptor, then use it...
      return (PlatformDescriptor) BeanUtil.newInstance(descriptorClassName);
    } catch (Exception e) {
      // ...otherwise check if there exists a package of this name
      if (ExceptionUtil.getRootCause(e) instanceof ClassNotFoundException) {
        return checkForPackageWithoutDescriptor(pkg);
      } else {
        throw e;
      }
    }
  }

  private DefaultPlatformDescriptor checkForPackageWithoutDescriptor(String pkg) {
    if (!BeanUtil.getClasses(pkg).isEmpty()) {
      // if the package exists, then create a DefaultPlatformDescriptor...
      return new DefaultPlatformDescriptor(pkg);
    } else {
      // ...otherwise don't return a descriptor
      return null;
    }
  }

  protected String[] platformPkgCandidates(String platformName) {
    if (platformName.indexOf('.') < 0) {
      return new String[] { "com.rapiddweller.platform." + platformName };
    } else {
      return new String[] { platformName };
    }
  }

  protected String[] domainPkgCandidates(String platformName) {
    if (platformName.indexOf('.') < 0) {
      return new String[] { "com.rapiddweller.domain." + platformName };
    } else {
      return new String[] { platformName };
    }
  }

  private static void registerParsers(PlatformDescriptor platformDescriptor, BeneratorParseContext context) {
    XMLStatementParser[] parsers = platformDescriptor.getParsers();
    if (parsers != null) {
      for (XMLStatementParser parser : parsers) {
        context.addParser(parser);
        BeneratorFactory.getInstance().addCustomParser(parser);
      }
    }
  }

}


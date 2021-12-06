/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine;

import com.rapiddweller.benerator.BeneratorErrorIds;
import com.rapiddweller.benerator.BeneratorFactory;
import com.rapiddweller.benerator.DefaultPlatformDescriptor;
import com.rapiddweller.benerator.DomainDescriptor;
import com.rapiddweller.benerator.PlatformDescriptor;
import com.rapiddweller.benerator.engine.parser.xml.BeneratorParseContext;
import com.rapiddweller.benerator.engine.parser.xml.XMLStatementParser;
import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.common.BeanUtil;
import com.rapiddweller.common.ExceptionUtil;
import com.rapiddweller.common.StringUtil;

/**
 * Helper class for importing Benerator components.<br/><br/>
 * Created: 06.12.2021 19:50:06
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class Importer {

  /** private constructor to prevent instantiation of this utility class. */
  private Importer() {
    // private constructor to prevent instantiation of this utility class
  }

  private static PlatformDescriptor findDescriptorForPlatform(String platformName, boolean required) {
    String[] pkgs = BeneratorFactory.getInstance().platformPkgCandidates(platformName);
    for (String pkg : pkgs) {
      PlatformDescriptor result = findDescriptorForPackage(pkg);
      if (result != null) {
        return result;
      }
    }
    if (required) {
      throw BeneratorExceptionFactory.getInstance().illegalArgument(
          "Platform not found: " + platformName, null, BeneratorErrorIds.SYN_IMPORT_PLATFORMS);
    } else {
      return null;
    }
  }

  public static PlatformDescriptor findDescriptorForPackage(String pkg) {
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

  private static DefaultPlatformDescriptor checkForPackageWithoutDescriptor(String pkg) {
    if (!BeanUtil.getClasses(pkg).isEmpty()) {
      // if the package exists, then create a DefaultPlatformDescriptor...
      return new DefaultPlatformDescriptor(StringUtil.suffix(pkg, '.'), pkg);
    } else {
      // ...otherwise don't return a descriptor
      return null;
    }
  }

  private static void registerParsers(PlatformDescriptor platformDescriptor, BeneratorParseContext parseContext) {
    XMLStatementParser[] parsers = platformDescriptor.getParsers();
    if (parsers != null) {
      for (XMLStatementParser parser : parsers) {
        parseContext.addParser(parser);
        BeneratorFactory.getInstance().addCustomParser(parser);
      }
    }
  }

  public static void importPlatform(PlatformDescriptor platformDescriptor, BeneratorContext context) {
    for (String pkg : platformDescriptor.getPackagesToImport()) {
      context.importPackage(pkg);
    }
    for (String cls : platformDescriptor.getClassesToImport()) {
      context.importClass(cls);
    }
  }

  public static DomainDescriptor[] findDomains(String[] domainSpecs) {
    DomainDescriptor[] result = new DomainDescriptor[domainSpecs.length];
    for (int i = 0; i < domainSpecs.length; i++) {
      String domainSpec = domainSpecs[i];
      result[i] = findDescriptorForDomain(domainSpec);
    }
    return result;
  }

  private static DomainDescriptor findDescriptorForDomain(String domainSpec) {
    String[] pkgs = BeneratorFactory.getInstance().domainPkgCandidates(domainSpec);
    for (String pkg : pkgs) {
      DomainDescriptor result = findDomainDescriptorForPackage(pkg);
      if (result != null) {
        return result;
      }
    }
    throw BeneratorExceptionFactory.getInstance().illegalArgument(
        "Domain not found: " + domainSpec, null, BeneratorErrorIds.SYN_IMPORT_DOMAINS);
  }

  private static DomainDescriptor findDomainDescriptorForPackage(String pkg) {
    if (!BeanUtil.getClasses(pkg).isEmpty()) {
      // if the package exists, then create a DomainDescriptor...
      return new DomainDescriptor(pkg);
    } else {
      // ...otherwise return null
      return null;
    }
  }

  public static void importPlatforms(
      String[] platformNames, boolean required, BeneratorParseContext parseContext, BeneratorContext context) {
    PlatformDescriptor[] descriptors = findPlatforms(platformNames, required, parseContext);
    for (PlatformDescriptor descriptor : descriptors) {
      for (String pkg : descriptor.getPackagesToImport()) {
        context.importPackage(pkg);
      }
      for (String cls : descriptor.getClassesToImport()) {
        context.importClass(cls);
      }
    }
  }

  public static PlatformDescriptor[] findPlatforms(String[] platformNames, boolean required,
                                                   BeneratorParseContext parseContext) {
    PlatformDescriptor[] platforms = new PlatformDescriptor[platformNames.length];
    for (int i = 0; i < platforms.length; i++) {
      String platformName = platformNames[i];
      PlatformDescriptor platformDescriptor = findDescriptorForPlatform(platformName, required);
      if (platformDescriptor != null) {
        if (parseContext != null) {
          // the imported parsers must be registered in the phase of parsing
          // in order to be available for parsing subsequent elements
          registerParsers(platformDescriptor, parseContext);
        }
        platforms[i] = platformDescriptor;
      }
    }
    return platforms;
  }

}

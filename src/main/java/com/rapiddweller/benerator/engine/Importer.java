/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine;

import com.rapiddweller.benerator.BeneratorErrorIds;
import com.rapiddweller.benerator.BeneratorFactory;
import com.rapiddweller.benerator.DomainDescriptor;
import com.rapiddweller.benerator.PlatformDescriptor;
import com.rapiddweller.benerator.engine.parser.xml.BeneratorParseContext;
import com.rapiddweller.benerator.engine.parser.xml.XMLStatementParser;
import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.common.BeanUtil;
import com.rapiddweller.common.ExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for importing Benerator components.<br/><br/>
 * Created: 06.12.2021 19:50:06
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class Importer {

  private static final Logger logger = LoggerFactory.getLogger(Importer.class);

  /** private constructor to prevent instantiation of this utility class. */
  private Importer() {
    // private constructor to prevent instantiation of this utility class
  }

  // platform parsers --------------------------------------------------------------------------------------------------

  public static void importPlatformParsers(String[] platformNames, boolean required,
                                           BeneratorParseContext parseContext) {
    PlatformDescriptor[] descriptors = findPlatforms(platformNames, required);
    for (PlatformDescriptor descriptor : descriptors) {
      importPlatformParsers(descriptor, parseContext);
    }
  }

  public static void importPlatformParsers(PlatformDescriptor descriptor, BeneratorParseContext parseContext) {
    if (parseContext != null) {
      // the imported parsers must be registered in the phase of parsing
      // in order to be available for parsing subsequent elements
      registerParsers(descriptor, parseContext);
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

  // platform classes --------------------------------------------------------------------------------------------------

  public static void importPlatformClasses(String[] platformNames, boolean required, BeneratorContext context) {
    List<String> importedPlatforms = new ArrayList<>();
    List<String> absentPlatforms = new ArrayList<>();
    PlatformDescriptor[] descriptors = findPlatforms(platformNames, required);
    for (int i = 0; i < platformNames.length; i++) {
      PlatformDescriptor descriptor = descriptors[i];
      String platformName = platformNames[i];
      if (descriptor == null) {
        absentPlatforms.add(platformName);
      } else {
        importPlatformClasses(descriptor, context);
        importedPlatforms.add(platformName);
      }
    }
    // log platform import results
    String imports = importedPlatforms.toString();
    imports = imports.substring(1, imports.length() - 1);
    logger.info("Imported platforms: {}", imports);
    if (!absentPlatforms.isEmpty()) {
      String absences = absentPlatforms.toString();
      absences = absences.substring(1, absences.length() - 1);
      logger.info("Absent platforms: {}", absences);
    }
  }

  public static void importPlatformClasses(PlatformDescriptor descriptor, BeneratorContext context) {
    for (String pkg : descriptor.getPackagesToImport()) {
      context.importPackage(pkg);
    }
    for (String cls : descriptor.getClassesToImport()) {
      context.importClass(cls);
    }
  }

  // platform lookup ---------------------------------------------------------------------------------------------------

  public static PlatformDescriptor[] findPlatforms(String[] platformNames, boolean required) {
    PlatformDescriptor[] platforms = new PlatformDescriptor[platformNames.length];
    for (int i = 0; i < platforms.length; i++) {
      String platformName = platformNames[i];
      platforms[i] = findDescriptorForPlatform(platformName, required);
    }
    return platforms;
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
        return null;
      } else {
        throw e;
      }
    }
  }

  // domains -----------------------------------------------------------------------------------------------------------

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

}

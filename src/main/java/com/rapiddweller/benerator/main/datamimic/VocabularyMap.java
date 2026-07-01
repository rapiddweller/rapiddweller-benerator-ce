/* (c) Copyright 2025 by rapiddweller GmbH. All rights reserved. */

package com.rapiddweller.benerator.main.datamimic;

import java.util.Map;
import java.util.Set;

/**
 * Data-driven Benerator-&gt;DATAMIMIC vocabulary tables. Extending the converter's coverage means
 * adding rows here, not editing the walk in {@link DescriptorConverter}.
 */
public final class VocabularyMap {

  private VocabularyMap() {
  }

  /** Benerator element -&gt; DATAMIMIC element. Anything absent is flagged for manual migration. */
  public static final Map<String, String> ELEMENT = Map.ofEntries(
      Map.entry("setup", "setup"),
      Map.entry("generate", "generate"),
      Map.entry("iterate", "generate"),
      Map.entry("attribute", "key"),
      Map.entry("id", "id"),
      Map.entry("part", "nestedKey"),
      Map.entry("variable", "variable"),
      Map.entry("comment", "comment"),
      Map.entry("echo", "echo"));

  /** Benerator elements that DATAMIMIC does not need - omitted from the output (not flagged as TODO). */
  public static final Set<String> DROP_ELEMENTS = Set.of("import");

  /** Benerator simple type -&gt; DATAMIMIC type. Absent types (date, entity, binary, ...) are flagged. */
  public static final Map<String, String> TYPE = Map.ofEntries(
      Map.entry("int", "int"),
      Map.entry("long", "int"),
      Map.entry("short", "int"),
      Map.entry("byte", "int"),
      Map.entry("double", "float"),
      Map.entry("float", "float"),
      Map.entry("big_decimal", "decimal"),
      Map.entry("big_integer", "int"),
      Map.entry("string", "string"),
      Map.entry("boolean", "bool"),
      Map.entry("bool", "bool"));

  /** DATAMIMIC numeric types that use an IntegerGenerator for a min/max/granularity range. */
  public static final Set<String> INTEGER_TYPES = Set.of("int");

  /** DATAMIMIC numeric types that use a FloatGenerator for a min/max/granularity range. */
  public static final Set<String> FLOAT_TYPES = Set.of("float", "decimal");

  /** &lt;setup&gt; attributes DATAMIMIC understands under the same name (see CE setup_model). */
  public static final Set<String> SETUP_ATTR_KEEP = Set.of(
      "defaultSeparator", "defaultDataset", "defaultLocale", "defaultLineSeparator",
      "defaultSourceScripted");

  /** Field (&lt;key&gt;/&lt;id&gt;/&lt;nestedKey&gt;/&lt;variable&gt;) attributes DATAMIMIC accepts verbatim. */
  public static final Set<String> FIELD_ATTR_KEEP = Set.of(
      "name", "pattern", "values", "constant", "script", "source", "selector",
      "separator", "unique", "nullQuota", "converter", "minCount", "maxCount",
      "dataset", "locale", "cyclic");

  /** Benerator generator name -&gt; DATAMIMIC generator name. Absent names are kept verbatim + reported. */
  public static final Map<String, String> GENERATOR_RENAME = Map.of(
      "IncrementalIdGenerator", "IncrementGenerator");

  /**
   * Generator names shared by both products (kept verbatim, no report). Verified against CE's
   * reserved-name list in parsers/statement_parser.py.
   */
  public static final Set<String> KNOWN_GENERATORS = Set.of(
      "IncrementGenerator", "DateTimeGenerator", "DateGenerator", "EANGenerator", "EAN8Generator",
      "EAN13Generator", "SSNGenerator", "CPFGenerator", "CNPJGenerator", "CompanyNameGenerator",
      "FamilyNameGenerator", "GivenNameGenerator", "StreetNameGenerator", "DataFakerGenerator",
      "UUIDGenerator", "BooleanGenerator", "IntegerGenerator", "StringGenerator", "FloatGenerator",
      "PhoneNumberGenerator", "DomainGenerator", "EmailAddressGenerator", "UrlGenerator",
      "AcademicTitleGenerator", "SectorGenerator", "DepartmentNameGenerator", "BirthdateGenerator",
      "NobilityTitleGenerator", "SequenceTableGenerator");

  /** Distribution names native to both products. */
  public static final Set<String> KNOWN_DISTRIBUTIONS = Set.of("random", "cumulated", "ordered");
}

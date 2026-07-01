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
      Map.entry("iterate", "iterate"), // DATAMIMIC keeps <iterate> (iterate-over-source vs generate-new)
      Map.entry("attribute", "key"),
      Map.entry("id", "id"),
      Map.entry("part", "nestedKey"),
      Map.entry("variable", "variable"),
      Map.entry("setting", "variable"), // Benerator <setting name value> -> DATAMIMIC <variable name constant/script>
      Map.entry("property", "variable"), // Benerator <property name value> -> DATAMIMIC <variable>

      Map.entry("reference", "reference"),
      Map.entry("database", "database"),
      Map.entry("mongodb", "mongodb"), // DATAMIMIC has a native <mongodb> store
      Map.entry("memstore", "memstore"),
      Map.entry("execute", "execute"),
      Map.entry("while", "while"), // Benerator <while test> -> DATAMIMIC <while condition>
      Map.entry("include", "include"),
      Map.entry("comment", "comment"),
      Map.entry("echo", "echo"));

  /** Benerator inline &lt;execute type&gt; -&gt; DATAMIMIC inline &lt;execute type&gt;. Absent (js/ftl/ben) is flagged. */
  public static final Map<String, String> EXECUTE_TYPE = Map.ofEntries(
      Map.entry("sql", "sql"),
      Map.entry("shell", "bash"));

  /** Benerator JDBC driver / url fragment -&gt; DATAMIMIC dbms. */
  public static final Map<String, String> DBMS = Map.ofEntries(
      Map.entry("postgresql", "postgresql"),
      Map.entry("mysql", "mysql"),
      Map.entry("mariadb", "mysql"),
      Map.entry("hsqldb", "hsqldb"),
      Map.entry("h2", "h2"),
      Map.entry("oracle", "oracle"),
      Map.entry("sqlite", "sqlite"),
      Map.entry("sqlserver", "mssql"),
      Map.entry("mssql", "mssql"));

  /** Benerator elements that DATAMIMIC does not need - omitted from the output (not flagged as TODO). */
  public static final Set<String> DROP_ELEMENTS = Set.of("import");

  /** Benerator consumer -&gt; DATAMIMIC target/exporter name (see CE exporters/exporter_util.py). */
  public static final Map<String, String> CONSUMER_TARGET = Map.ofEntries(
      Map.entry("ConsoleExporter", "ConsoleExporter"),
      Map.entry("LogExporter", "LogExporter"),
      Map.entry("LoggingConsumer", "LogExporter"),
      Map.entry("CSVEntityExporter", "CSV"),
      Map.entry("JSONEntityExporter", "JSON"),
      Map.entry("XMLEntityExporter", "XML"),
      Map.entry("NoConsumer", "")); // no output -> empty target (capture only)

  /** Benerator simple type -&gt; DATAMIMIC type. Absent types (date, entity, binary, ...) are flagged. */
  public static final Map<String, String> TYPE = Map.ofEntries(
      Map.entry("int", "int"),
      Map.entry("integer", "int"),
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
      "min", "max", "granularity", "minLength", "maxLength", "dataset", "locale", "cyclic");

  /**
   * Benerator composite generator -&gt; DATAMIMIC entity name (validated against CE's entity registry,
   * {@code list_entity_specs()}). On a {@code <variable>} these become {@code entity="X"}; DATAMIMIC's
   * tolerant field access then resolves Benerator's camelCase {@code x.givenName} to {@code given_name}.
   * Not a suffix strip: {@code CreditCardNumberGenerator} -&gt; {@code CreditCard}.
   */
  public static final Map<String, String> GENERATOR_TO_ENTITY = Map.ofEntries(
      Map.entry("PersonGenerator", "Person"),
      Map.entry("AddressGenerator", "Address"),
      Map.entry("CountryGenerator", "Country"),
      Map.entry("CityGenerator", "City"),
      Map.entry("CompanyGenerator", "Company"),
      Map.entry("BankAccountGenerator", "BankAccount"),
      Map.entry("BankGenerator", "Bank"),
      Map.entry("CreditCardGenerator", "CreditCard"),
      Map.entry("CreditCardNumberGenerator", "CreditCard"));

  /** Benerator generator name -&gt; DATAMIMIC generator name. Absent names are kept verbatim + reported. */
  public static final Map<String, String> GENERATOR_RENAME = Map.of(
      "IncrementalIdGenerator", "IncrementGenerator",
      "EMailAddressGenerator", "EmailAddressGenerator"); // Benerator casing -> DATAMIMIC casing

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

  /** Converters DATAMIMIC understands by name (see CE enums/converter_enums.py). */
  public static final Set<String> KNOWN_CONVERTERS = Set.of(
      "UpperCase", "LowerCase", "DateFormat", "Mask", "MiddleMask", "CutLength", "Append", "Hash",
      "Timestamp2Date", "JavaHash", "Date2Timestamp", "RemoveNoneOrEmptyElement");

  /** Benerator converter -&gt; DATAMIMIC converter name. */
  public static final Map<String, String> CONVERTER_RENAME = Map.of(
      "CaseConverter", "UpperCase"); // Benerator CaseConverter defaults to upper-casing

  /** Benerator hash converter -&gt; DATAMIMIC {@code Hash(algorithm, output_format)} full expression. */
  public static final Map<String, String> CONVERTER_EXPANSION = Map.ofEntries(
      Map.entry("SHA1Hash", "Hash('sha1','hex')"),
      Map.entry("SHA1HashBase64", "Hash('sha1','base64')"),
      Map.entry("SHA256Hash", "Hash('sha256','hex')"),
      Map.entry("SHA256HashBase64", "Hash('sha256','base64')"),
      Map.entry("SHA512Hash", "Hash('sha512','hex')"),
      Map.entry("MD5Hash", "Hash('md5','hex')"),
      Map.entry("MD5HashBase64", "Hash('md5','base64')"));
}

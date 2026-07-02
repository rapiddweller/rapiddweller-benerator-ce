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
      Map.entry("XLSEntityExporter", "XLSX"),
      Map.entry("XLSXEntityExporter", "XLSX"),
      Map.entry("NoConsumer", "")); // no output -> empty target (capture only)

  /**
   * Benerator CRUD consumer method -&gt; DATAMIMIC target suffix: {@code db.updater()} becomes target
   * {@code db.update} (PK-based, SQL and mongodb). {@code inserter} maps to the empty suffix - a plain
   * store target already means insert.
   */
  public static final Map<String, String> CRUD_CONSUMER_OP = Map.of(
      "updater", "update",
      "inserter", "",
      "deleter", "delete",
      "upserter", "upsert");

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
      Map.entry("bool", "bool"),
      Map.entry("binary", "binary")); // native since DM PR #170 (random bytes, minLength/maxLength)

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

  /**
   * Composite-generator brace args (Benerator {@code PersonGenerator{minAgeYears='21'}}) that have a
   * dedicated DATAMIMIC entity attribute on {@code <variable entity=...>} (ageMin/ageMax/dataset/locale).
   */
  public static final Map<String, String> ENTITY_ARG_TO_ATTR = Map.of(
      "minAgeYears", "ageMin",
      "maxAgeYears", "ageMax",
      "dataset", "dataset",
      "locale", "locale");

  /**
   * Composite-generator brace args -&gt; DATAMIMIC entity service constructor params (validated against
   * CE's Person service signature). Used when at least one arg is constructor-only (the quotas), so the
   * whole call becomes {@code entity="Person(min_age=21, female_quota=0.5)"}. {@code locale} is
   * deliberately absent: the service constructor does not take it, so it stays a dedicated attribute.
   */
  public static final Map<String, String> ENTITY_ARG_TO_CTOR_PARAM = Map.of(
      "minAgeYears", "min_age",
      "maxAgeYears", "max_age",
      "dataset", "dataset",
      "femaleQuota", "female_quota",
      "otherGenderQuota", "other_gender_quota",
      "nobleQuota", "noble_quota",
      "academicTitleQuota", "academic_title_quota");

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

  /**
   * Benerator DataFaker method -&gt; Python Faker method (faker 33.x), only where plain camel-to-snake
   * ({@code fakerSnake}) does NOT already produce the right name (e.g. {@code getYear} -&gt; {@code year},
   * not {@code get_year}). Names that just need snake_case are handled by the fallback, not listed here.
   * Corpus-derived from the 990-call demo/faker sweep; every target verified zero-arg-callable on
   * Python Faker 33.x ({@code Faker('en_US')}).
   */
  public static final Map<String, String> FAKER_METHOD_RENAME = Map.ofEntries(
      Map.entry("birthday", "date_of_birth"),
      Map.entry("bool", "boolean"),
      Map.entry("cellPhone", "phone_number"),
      Map.entry("cityName", "city"),
      Map.entry("countryCode2", "country_code"),
      Map.entry("creditCard", "credit_card_number"),
      Map.entry("creditCardExpiry", "credit_card_expire"),
      Map.entry("creditCardType", "credit_card_provider"),
      Map.entry("digit", "random_digit"),
      Map.entry("domainSuffix", "tld"),
      Map.entry("emailAddress", "email"),
      Map.entry("emails", "email"),
      Map.entry("fullAddress", "address"),
      Map.entry("fullName", "name"),
      Map.entry("getIpV4Address", "ipv4"),
      Map.entry("getIpV6Address", "ipv6"),
      Map.entry("getLocale", "locale"),
      Map.entry("getPrivateIpV4Address", "ipv4_private"),
      Map.entry("getPublicIpV4Address", "ipv4_public"),
      Map.entry("getYear", "year"),
      Map.entry("ipV4Address", "ipv4"),
      Map.entry("ipV6Address", "ipv6"),
      Map.entry("isoCountry", "country_code"),
      Map.entry("isoLanguage", "language_code"),
      Map.entry("language", "language_name"),
      Map.entry("languages", "language_name"),
      Map.entry("localeString", "locale"),
      Map.entry("name1", "name"),
      Map.entry("name2", "name"),
      Map.entry("price", "pricetag"),
      Map.entry("privateIpV4Address", "ipv4_private"),
      Map.entry("publicIpV4Address", "ipv4_public"),
      Map.entry("randomDigitNotZero", "random_digit_not_null"),
      Map.entry("safeEmailAddress", "safe_email"),
      Map.entry("states", "state"),
      Map.entry("streetAddressNumber", "building_number"),
      Map.entry("timeZone", "timezone"),
      Map.entry("username", "user_name"),
      Map.entry("uuid", "uuid4"),
      Map.entry("uuidv3", "uuid4"),
      Map.entry("zipCode", "zipcode"));

  /**
   * Benerator DataFaker methods (Java-datafaker-only providers: fiction/sport/hobby/medical/commerce/
   * cloud/etc.) with NO zero-arg Python Faker 33.x equivalent - converted to {@code word} and flagged so
   * the user routes them to a DATAMIMIC entity or a value list. Corpus-derived from the demo/faker sweep.
   */
  public static final Set<String> FAKER_UNAVAILABLE_METHODS = Set.of(
      "METAR", "abbreviation", "aboTypes", "accountId", "acmARN", "active", "activity", "actor", "actors",
      "adjective", "adultMusical", "age", "agency", "agencyAbbreviation", "ages", "airForceRank", "aircraft",
      "airport", "akumasNoMi", "albARN", "albTargetGroupARN", "alignments", "allSupportedLocales", "allomancers",
      "alteredItem", "alteredWorldEvent", "alternateCharacterSpelling", "ancientOlympics", "animalName", "animals",
      "any", "aons", "aperture", "app", "appServiceEnvironment", "appServicePlan", "applicationGateway", "armyRank",
      "artist", "asYouLikeItQuote", "asin", "author", "backgrounds", "base", "bastionHost", "batteryCapacity",
      "batteryType", "batteryVoltage", "batteryWeight", "battleground", "battlegroundsScore", "bic", "bigBads",
      "binaryTypes", "biologicalAgent", "blendName", "bloodGroup", "body", "bodyBytes", "bodyString", "book",
      "books", "bosses", "boyBands", "brand", "brandWithModel", "brands", "breed", "buffs", "build", "building",
      "business", "buzzword", "buzzwords", "camera", "campus", "cannabinoidAbbreviations", "cannabinoids", "capital",
      "capitalCity", "car", "carOptions", "carType", "cat", "categories", "celebration", "celebrities", "champion",
      "character", "characterFirstName", "characterLastName", "characterName", "characteristic", "characters",
      "chargingTemperature", "chord", "circuit", "cities", "civs", "classes", "clientErrorResponseCode",
      "clientErrorResponsePhrase", "cluster", "coach", "coaches", "coatLength", "code", "codeWord", "coin",
      "competitions", "constellation", "containerApps", "containerAppsEnvironment", "containerInstance",
      "containerRegistry", "contentType", "cosmosDBDatabase", "countryCode3", "course", "covenants", "creator",
      "creature", "creatures", "cultureShipClassAbvs", "cultureShipClasses", "cultureShips", "custom",
      "defensiveBuilding", "demon", "demonym", "department", "departments", "dermatology", "dermatolory",
      "description", "descriptor", "direct", "diseaseName", "dish", "displayName", "distanceMeasurement", "doctor",
      "dodPaygrade", "dragon", "dragons", "driveType", "driver", "droids", "educationalAttainment",
      "electromechanical", "element", "emotion", "enemies", "enemy", "entityName", "episode", "episodes",
      "equipment", "event", "expressions", "extended", "extension", "eye", "fact", "faction", "factions", "feeling",
      "feruchemists", "field", "fighter", "firewall", "flag", "flavor", "flightTime", "flip", "food", "foodAndDrink",
      "formation", "formats", "fruit", "fuelType", "furniture", "gadget", "gadgets", "galaxy", "game", "gameName",
      "games", "gender", "genre", "getChoice", "getLocaleChain", "getRandomService", "getSingletonLocale",
      "girlGroups", "globalErrorResponseCode", "globalErrorResponsePhrase", "god", "grandPrix", "gtin12", "gtin13",
      "gtin14", "gtin8", "gynecologyAndObstetrics", "hamletQuote", "healthBenefits", "heavyEquipment", "height",
      "heist", "heralds", "hermesCatchPhrase", "hero", "heroClass", "heroine", "heros", "hex", "highFive", "hiss",
      "hop", "hospitalName", "house", "iGroups", "iiGroups", "iiiGroups", "image", "imageTag", "imei", "inLaw",
      "inValidEnZaSsn", "industry", "ingForm", "ingredient", "ingverb", "instrument", "intensifier",
      "internalDisease", "invalid", "invalidEsMXSsn", "invalidPtNif", "invalidSvSeSsn", "invention", "ipV4Cidr",
      "ipV6Cidr", "isbnGroup", "isbnGs1", "isbnRegistrant", "island", "iso", "item", "itemName", "jokes", "key",
      "keySkills", "keyVault", "kidsMusical", "kingRichardIIIQuote", "klasses", "knightsRadiant", "lastWords",
      "latLon", "league", "length", "lens", "linux", "loadBalancer", "loadTesting", "localeStringWithoutReplacement",
      "location", "locations", "logAnalytics", "logo", "lonLat", "lowercaseCharacter", "macos", "mainCharacter",
      "mainPattern", "mainProfession", "make", "makeAndModel", "malt", "managementGroup", "manufacturer", "map",
      "marinesRank", "maritalStatus", "marvinQuote", "masteries", "material", "materials", "maxAltitude",
      "maxAngularVelocity", "maxAscentSpeed", "maxChargingPower", "maxDescentSpeed", "maxFlightDistance",
      "maxResolution", "maxShutterSpeed", "maxSpeed", "maxTiltAngle", "maxWindResistance", "md2", "measurement",
      "medicalUses", "medicineName", "meleeWeapons", "memePhrase", "merit", "messagingPort", "metals", "meteorite",
      "method", "metricHeight", "metricLength", "metricVolume", "metricWeight", "minShutterSpeed", "model",
      "modelName", "monster", "monsterName", "monsters", "moon", "motto", "mountaineer", "move", "moves", "movie",
      "movieName", "mysqlDatabase", "nameAddress", "namePrefix", "nameSuffix", "nameWithMiddle", "nasaSpaceCraft",
      "nationality", "nature", "navyRank", "nebula", "negative", "networkSecurityGroup", "neurology", "notes",
      "noun", "npc", "nsdqSymbol", "nyseSymbol", "objectOfPower", "objectsAndSymbols", "opening", "operatingSystem",
      "operatingTemperature", "ophthalmologyAndOtorhinolaryngology", "organization", "pTypes", "paediatrics",
      "parent", "passive", "past", "pastParticiple", "paymentMethods", "paymentTerms", "people", "personage",
      "peselNumber", "phoneNumberNational", "photoFormat", "places", "planet", "planets", "plans", "platform",
      "play", "player", "players", "port", "position", "positions", "positive", "postgreSQLDatabase", "power",
      "primordial", "productName", "profession", "promotionCode", "provisionalResponseCode",
      "provisionalResponsePhrase", "publisher", "queen", "quote", "quotes", "race", "races", "range",
      "rangedWeapons", "rank", "realName", "redirectResponseCode", "redirectResponsePhrase", "region", "registry",
      "releaseDate", "resourceGroup", "review", "rhTypes", "roles", "romeoAndJulietQuote", "room", "route53ZoneId",
      "rtpPort", "saying", "school", "schools", "scientist", "sea", "secondarySchool", "sector", "securityGroupId",
      "seniority", "serial", "serverErrorResponseCode", "serverErrorResponsePhrase", "serviceBus", "serviceBusQueue",
      "serviceBusTopic", "sex", "sha384", "sha512", "shardWorlds", "shards", "shield", "ships", "shutter",
      "shutterSpeedUnits", "sibling", "sign", "simplePresent", "singaporeanFin", "singaporeanFinBefore2000",
      "singaporeanUin", "singaporeanUinBefore2000", "size", "skill", "smiley", "solo", "song", "songs", "sound",
      "specie", "species", "speeches", "spell", "spice", "sport", "spouse", "sprens", "sqlDatabase", "ssnValid",
      "stage", "stages", "standardCostCodes", "standardRank", "standardSpecs", "star", "starCluster", "starship",
      "staticWebApp", "stats", "statues", "statuses", "storageAccount", "strains", "streetPrefix", "style",
      "subSector", "subcontractCategories", "subjectWithNumber", "subnetId", "subscriberNumber", "subscriptionId",
      "subscriptionTerms", "successResponseCode", "successResponsePhrase", "summerOlympics", "summerParalympics",
      "summonerSpell", "superSector", "surgery", "surges", "sushi", "symptoms", "tagline", "talent", "team", "teams",
      "temperatureCelsius", "temperatureFahrenheit", "tenantId", "term", "terms", "terpenes", "theBoard",
      "thorinsCompany", "tileItemName", "tileName", "titan", "title", "tone", "tongueTwisters", "topping",
      "tournament", "tournaments", "trackName", "trades", "transmission", "travelAndPlaces", "troop", "type",
      "types", "unit", "university", "unusual", "uppercaseCharacter", "userId", "valid", "validEnZaSsn",
      "validEsMXSsn", "validPtNif", "validSvSeSsn", "validZhCNSsn", "variety", "vegetable", "vehicle", "vehicles",
      "verb", "version", "videoFormat", "village", "villain", "virtualMachine", "virtualNetwork", "virtualWan",
      "volume", "vpcId", "watch", "weakness", "weapon", "weapons", "weight", "wildRank", "windows", "winterOlympics",
      "winterParalympics", "witcher", "wookieWords", "yeast", "zone");

  /** Distribution names native to both products. */
  public static final Set<String> KNOWN_DISTRIBUTIONS = Set.of("random", "cumulated", "ordered");

  /** Converters DATAMIMIC understands by name (see CE enums/converter_enums.py). */
  public static final Set<String> KNOWN_CONVERTERS = Set.of(
      "UpperCase", "LowerCase", "DateFormat", "Mask", "MiddleMask", "CutLength", "Substring", "Append",
      "Hash", "Timestamp2Date", "JavaHash", "Date2Timestamp", "RemoveNoneOrEmptyElement");

  /** Benerator converter -&gt; DATAMIMIC converter name. */
  public static final Map<String, String> CONVERTER_RENAME = Map.of(
      "CaseConverter", "UpperCase", // Benerator CaseConverter defaults to upper-casing
      "SubstringExtractor", "Substring"); // python slice semantics (DM PR #172)

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

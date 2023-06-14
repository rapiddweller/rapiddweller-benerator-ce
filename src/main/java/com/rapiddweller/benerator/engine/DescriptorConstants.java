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

package com.rapiddweller.benerator.engine;

import com.rapiddweller.common.CollectionUtil;

import java.util.Collection;
import java.util.Collections;

/**
 * Collects element and attribute names used in Benerator's XML descriptor files.<br/><br/>
 * Created at 24.07.2009 07:39:19
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class DescriptorConstants {

  public static final String EL_SETUP = "setup";
  public static final String ATT_DEFAULT_SCRIPT = "defaultScript";
  public static final String ATT_DEFAULT_NULL = "defaultNull";
  public static final String ATT_DEFAULT_ENCODING = "defaultEncoding";
  public static final String ATT_DEFAULT_LINE_SEPARATOR = "defaultLineSeparator";
  public static final String ATT_DEFAULT_TIME_ZONE = "defaultTimeZone";
  public static final String ATT_DEFAULT_LOCALE = "defaultLocale";
  public static final String ATT_DEFAULT_DATASET = "defaultDataset";
  public static final String ATT_DEFAULT_PAGE_SIZE = "defaultPageSize";
  public static final String ATT_DEFAULT_SEPARATOR = "defaultSeparator";
  public static final String ATT_DEFAULT_ONE_TO_ONE = "defaultOneToOne";
  public static final String ATT_DEFAULT_ERR_HANDLER = "defaultErrorHandler";
  public static final String ATT_DEFAULT_SOURCE_SCRIPTED = "defaultSourceScripted";
  public static final String ATT_ACCEPT_UNKNOWN_SIMPLE_TYPES = "acceptUnknownSimpleTypes";
  public static final String ATT_GENERATOR_FACTORY = "generatorFactory";
  public static final String ATT_DEFAULT_IMPORTS = "defaultImports";

  public static final String EL_IF = "if";
  public static final String EL_THEN = "then";
  public static final String EL_ELSE = "else";
  public static final String EL_WHILE = "while";
  public static final String EL_BEAN = "bean";
  public static final String EL_PROPERTY = "property";
  public static final String EL_META_MODEL = "meta-model";
  public static final String EL_PRE_PARSE_GENERATE = "pre-parse-generate";
  public static final String EL_GENERATE = "generate";
  public static final String EL_ITERATE = "iterate";
  public static final String EL_CONSUMER = "consumer";
  public static final String EL_COMMENT = "comment";
  public static final String EL_DEFAULT_COMPONENTS = "defaultComponents";
  public static final String EL_EVALUATE = "evaluate";
  public static final String EL_EXECUTE = "execute";
  public static final String EL_MONGO_DB = "mongodb";
  public static final String EL_DATABASE = "database";
  public static final String EL_MEMSTORE = "memstore";
  public static final String EL_DOMTREE = "domtree";
  public static final String EL_ECHO = "echo";
  public static final String EL_ERROR = "error";
  public static final String EL_IMPORT = "import";
  public static final String EL_INCLUDE = "include";
  public static final String EL_SETTING = "setting";
  public static final String EL_RUN_TASK = "run-task";
  public static final String EL_VARIABLE = "variable";
  public static final String EL_VALUE = "value";
  public static final String EL_REFERENCE = "reference";
  public static final String EL_ID = "id";
  public static final String EL_COMPOSITE_ID = "composite-id";
  public static final String EL_PART = "part";
  public static final String EL_ATTRIBUTE = "attribute";
  public static final String EL_WAIT = "wait";
  public static final String EL_ITEM = "item";
  public static final String EL_LIST = "list";

  public static final String EL_TRANSCODING_TASK = "transcodingTask";
  public static final String EL_TRANSCODE = "transcode";
  public static final String EL_CASCADE = "cascade";
  public static final String ATT_TABLE = "table";

  public static final String ATT_PASSWORD = "password";

  //mongo db authentication database
  public static final String ATT_AUTH_DB = "authenticationDatabase";

  //mongo db authentication mechanism
    public static final String ATT_AUTH_MECHANISM = "authenticationMechanism";
  public static final String ATT_USER = "user";
  public static final String ATT_DRIVER = "driver";
  public static final String ATT_URL = "url";
  public static final String ATT_ID = EL_ID;
  public static final String ATT_MESSAGE = "message";
  public static final String ATT_TARGET_TYPE = "targetType";
  public static final String ATT_SELECTOR = "selector";
  public static final String ATT_SOURCE = "source";
  public static final String ATT_SOURCE_SCRIPTED = "sourceScripted";
  public static final String ATT_SEGMENT = "segment";
  public static final String ATT_FORMAT = "format";
  public static final String ATT_ROW_BASED = "rowBased";
  public static final String ATT_EMPTY_MARKER = "emptyMarker";
  public static final String ATT_OFFSET = "offset";
  public static final String ATT_REF = "ref";
  public static final String ATT_VALUE = "value";
  public static final String ATT_DEFAULT = "default";
  public static final String ATT_NAME = "name";
  public static final String ATT_ON_ERROR = "onError";
  public static final String ATT_CONSUMER = "consumer";
  public static final String ATT_THREADS = "threads";
  public static final String ATT_PAGESIZE = "pageSize";
  public static final String ATT_PAGER = "pager";
  public static final String ATT_HOST = "host";
  public static final String ATT_PORT = "port";
  public static final String ATT_DATABASE = "database";
  public static final String ATT_CLEAN = "clean";

  public static final String ATT_COUNT = "count";
  public static final String ATT_MIN_COUNT = "minCount";
  public static final String ATT_MAX_COUNT = "maxCount";
  public static final String ATT_COUNT_GRANULARITY = "countGranularity";
  public static final String ATT_COUNT_DISTRIBUTION = "countDistribution";

  public static final String ATT_ASSERT = "assert";
  public static final String ATT_LANG = "lang";
  public static final String ATT_TYPE = "type";
  public static final String ATT_SCOPE = "scope";
  public static final String ATT_EXIT_CODE = "exitCode";
  public static final String ATT_CONTAINER = "container";
  public static final String ATT_OPTIMIZE = "optimize";
  public static final String ATT_INVALIDATE = "invalidate";
  public static final String ATT_ENCODING = "encoding";
  public static final String ATT_TARGET = "target";
  public static final String ATT_SHELL = "shell";
  public static final String ATT_URI = "uri";
  public static final String ATT_READ_ONLY = "readOnly";
  public static final String ATT_LAZY = "lazy";
  public static final String ATT_ACC_UNK_COL_TYPES = "acceptUnknownColumnTypes";
  public static final String ATT_FETCH_SIZE = "fetchSize";
  public static final String ATT_BATCH = "batch";
  public static final String ATT_META_CACHE = "metaCache";
  public static final String ATT_CATALOG = "catalog";
  public static final String ATT_SCHEMA = "schema";
  public static final String ATT_ENVIRONMENT = "environment";
  public static final String ATT_SYSTEM = "system";
  public static final String ATT_TABLE_FILTER = "tableFilter";
  public static final String ATT_INCL_TABLES = "includeTables";
  public static final String ATT_EXCL_TABLES = "excludeTables";
  public static final String ATT_TEST = "test";
  public static final String ATT_DURATION = "duration";
  public static final String ATT_MIN = "min";
  public static final String ATT_MIN_INCLUSIVE = "minInclusive";
  public static final String ATT_MAX = "max";
  public static final String ATT_MAX_INCLUSIVE = "maxInclusive";
  public static final String ATT_GRANULARITY = "granularity";
  public static final String ATT_DISTRIBUTION = "distribution";
  public static final String ATT_STATS = "stats";

  public static final String ATT_TEMPLATE = "template";
  public static final String ATT_CONSTANT = "constant";
  public static final String ATT_VALUES = "values";
  public static final String ATT_PATTERN = "pattern";
  public static final String ATT_SCRIPT = "script";
  public static final String ATT_GENERATOR = "generator";

  public static final String ATT_VALIDATOR = "validator";
  public static final String ATT_CONVERTER = "converter";
  public static final String ATT_NULL_QUOTA = "nullQuota";
  public static final String ATT_TRUE_QUOTA = "trueQuota";
  public static final String ATT_MODE = "mode";
  public static final String ATT_NULLABLE = "nullable";
  public static final String ATT_UNIQUE = "unique";
  public static final String ATT_CYCLIC = "cyclic";
  public static final String ATT_SEPARATOR = "separator";
  public static final String ATT_SUB_SELECTOR = "subSelector";
  public static final String ATT_DATASET = "dataset";
  public static final String ATT_NESTING = "nesting";
  public static final String ATT_LOCALE = "locale";
  public static final String ATT_FILTER = "filter";
  public static final String ATT_MIN_LENGTH = "minLength";
  public static final String ATT_MAX_LENGTH = "maxLength";
  public static final String ATT_UNIQUE_KEY = "uniqueKey";
  public static final String ATT_CONDITION = "condition";
  public static final String ATT_MAP = "map";

  public static final String ATT_SENSOR = "sensor";

  public static final String ATT_CLASS = "class";
  public static final String ATT_SPEC = "spec";

  public static final String ATT_DEFAULTS = "defaults";
  public static final String ATT_PLATFORMS = "platforms";
  public static final String ATT_DOMAINS = "domains";

  public static final String ATT_DEFAULT_SOURCE = "defaultSource";
  public static final String ATT_IDENTITY = "identity";

  public static final String ATT_NAMESPACE_AWARE = "namespaceAware";
  public static final String ATT_INPUT_URI = "inputUri";
  public static final String ATT_OUTPUT_URI = "outputUri";

  public static final Collection<String> CREATE_ENTITIES_EXT_SETUP = Collections.unmodifiableSet(
      CollectionUtil.toSet(ATT_PAGESIZE, ATT_THREADS, ATT_SENSOR, ATT_CONSUMER, ATT_ON_ERROR));

  public static final Collection<String> COMPONENT_TYPES = Collections.unmodifiableSet(
      CollectionUtil.toSet(EL_ATTRIBUTE, EL_ID, EL_REFERENCE, EL_PART));

  protected DescriptorConstants() {
    // private constructor to prevent instantiation
  }

}

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

package com.rapiddweller.benerator.engine;

import com.rapiddweller.common.CollectionUtil;

import java.util.Collection;

/**
 * Collects element and attribute names used in Benerator's XML descriptor files.<br/>
 * <br/>
 * Created at 24.07.2009 07:39:19
 *
 * @author Volker Bergmann
 * @since 0.6.0
 */
public interface DescriptorConstants {

  /**
   * The constant EL_SETUP.
   */
  String EL_SETUP = "setup";

  /**
   * The constant ATT_DEFAULT_SCRIPT.
   */
  String ATT_DEFAULT_SCRIPT = "defaultScript";
  /**
   * The constant ATT_DEFAULT_NULL.
   */
  String ATT_DEFAULT_NULL = "defaultNull";
  /**
   * The constant ATT_DEFAULT_ENCODING.
   */
  String ATT_DEFAULT_ENCODING = "defaultEncoding";
  /**
   * The constant ATT_DEFAULT_LINE_SEPARATOR.
   */
  String ATT_DEFAULT_LINE_SEPARATOR = "defaultLineSeparator";
  /**
   * The constant ATT_DEFAULT_TIME_ZONE.
   */
  String ATT_DEFAULT_TIME_ZONE = "defaultTimeZone";
  /**
   * The constant ATT_DEFAULT_LOCALE.
   */
  String ATT_DEFAULT_LOCALE = "defaultLocale";
  /**
   * The constant ATT_DEFAULT_DATASET.
   */
  String ATT_DEFAULT_DATASET = "defaultDataset";
  /**
   * The constant ATT_DEFAULT_PAGE_SIZE.
   */
  String ATT_DEFAULT_PAGE_SIZE = "defaultPageSize";
  /**
   * The constant ATT_DEFAULT_SEPARATOR.
   */
  String ATT_DEFAULT_SEPARATOR = "defaultSeparator";
  /**
   * The constant ATT_DEFAULT_ONE_TO_ONE.
   */
  String ATT_DEFAULT_ONE_TO_ONE = "defaultOneToOne";
  /**
   * The constant ATT_DEFAULT_ERR_HANDLER.
   */
  String ATT_DEFAULT_ERR_HANDLER = "defaultErrorHandler";
  /**
   * The constant ATT_ACCEPT_UNKNOWN_SIMPLE_TYPES.
   */
  String ATT_ACCEPT_UNKNOWN_SIMPLE_TYPES = "acceptUnknownSimpleTypes";
  /**
   * The constant ATT_GENERATOR_FACTORY.
   */
  String ATT_GENERATOR_FACTORY = "generatorFactory";
  /**
   * The constant ATT_DEFAULT_IMPORTS.
   */
  String ATT_DEFAULT_IMPORTS = "defaultImports";

  /**
   * The constant EL_IF.
   */
  String EL_IF = "if";
  /**
   * The constant EL_THEN.
   */
  String EL_THEN = "then";
  /**
   * The constant EL_ELSE.
   */
  String EL_ELSE = "else";
  /**
   * The constant EL_WHILE.
   */
  String EL_WHILE = "while";
  /**
   * The constant EL_BEAN.
   */
  String EL_BEAN = "bean";
  /**
   * The constant EL_PROPERTY.
   */
  String EL_PROPERTY = "property";
  /**
   * The constant EL_GENERATE.
   */
  String EL_GENERATE = "generate";
  /**
   * The constant EL_ITERATE.
   */
  String EL_ITERATE = "iterate";
  /**
   * The constant EL_CONSUMER.
   */
  String EL_CONSUMER = "consumer";
  /**
   * The constant EL_COMMENT.
   */
  String EL_COMMENT = "comment";
  /**
   * The constant EL_DEFAULT_COMPONENTS.
   */
  String EL_DEFAULT_COMPONENTS = "defaultComponents";
  /**
   * The constant EL_EVALUATE.
   */
  String EL_EVALUATE = "evaluate";
  /**
   * The constant EL_EXECUTE.
   */
  String EL_EXECUTE = "execute";
  /**
   * The constant EL_DATABASE.
   */
  String EL_DATABASE = "database";
  /**
   * The constant EL_MEMSTORE.
   */
  String EL_MEMSTORE = "memstore";
  /**
   * The constant EL_DOMTREE.
   */
  String EL_DOMTREE = "domtree";
  /**
   * The constant EL_ECHO.
   */
  String EL_ECHO = "echo";
  /**
   * The constant EL_ERROR.
   */
  String EL_ERROR = "error";
  /**
   * The constant EL_IMPORT.
   */
  String EL_IMPORT = "import";
  /**
   * The constant EL_INCLUDE.
   */
  String EL_INCLUDE = "include";
  /**
   * The constant EL_SETTING.
   */
  String EL_SETTING = "setting";
  /**
   * The constant EL_RUN_TASK.
   */
  String EL_RUN_TASK = "run-task";
  /**
   * The constant EL_VARIABLE.
   */
  String EL_VARIABLE = "variable";
  /**
   * The constant EL_VALUE.
   */
  String EL_VALUE = "value";
  /**
   * The constant EL_REFERENCE.
   */
  String EL_REFERENCE = "reference";
  /**
   * The constant EL_ID.
   */
  String EL_ID = "id";
  /**
   * The constant EL_COMPOSITE_ID.
   */
  String EL_COMPOSITE_ID = "composite-id";
  /**
   * The constant EL_PART.
   */
  String EL_PART = "part";
  /**
   * The constant EL_ATTRIBUTE.
   */
  String EL_ATTRIBUTE = "attribute";
  /**
   * The constant EL_WAIT.
   */
  String EL_WAIT = "wait";

  /**
   * The constant EL_TRANSCODING_TASK.
   */
  String EL_TRANSCODING_TASK = "transcodingTask";
  /**
   * The constant EL_TRANSCODE.
   */
  String EL_TRANSCODE = "transcode";
  /**
   * The constant EL_CASCADE.
   */
  String EL_CASCADE = "cascade";
  /**
   * The constant ATT_TABLE.
   */
  String ATT_TABLE = "table";

  /**
   * The constant ATT_PASSWORD.
   */
  String ATT_PASSWORD = "password";
  /**
   * The constant ATT_USER.
   */
  String ATT_USER = "user";
  /**
   * The constant ATT_DRIVER.
   */
  String ATT_DRIVER = "driver";
  /**
   * The constant ATT_URL.
   */
  String ATT_URL = "url";
  /**
   * The constant ATT_ID.
   */
  String ATT_ID = EL_ID;
  /**
   * The constant ATT_MESSAGE.
   */
  String ATT_MESSAGE = "message";
  /**
   * The constant ATT_SELECTOR.
   */
  String ATT_SELECTOR = "selector";
  /**
   * The constant ATT_SOURCE.
   */
  String ATT_SOURCE = "source";
  /**
   * The constant ATT_SEGMENT.
   */
  String ATT_SEGMENT = "segment";
  /**
   * The constant ATT_FORMAT.
   */
  String ATT_FORMAT = "format";
  /**
   * The constant ATT_OFFSET.
   */
  String ATT_OFFSET = "offset";
  /**
   * The constant ATT_REF.
   */
  String ATT_REF = "ref";
  /**
   * The constant ATT_VALUE.
   */
  String ATT_VALUE = "value";
  /**
   * The constant ATT_DEFAULT.
   */
  String ATT_DEFAULT = "default";
  /**
   * The constant ATT_NAME.
   */
  String ATT_NAME = "name";
  /**
   * The constant ATT_ON_ERROR.
   */
  String ATT_ON_ERROR = "onError";
  /**
   * The constant ATT_CONSUMER.
   */
  String ATT_CONSUMER = "consumer";
  /**
   * The constant ATT_THREADS.
   */
  String ATT_THREADS = "threads";
  /**
   * The constant ATT_PAGESIZE.
   */
  String ATT_PAGESIZE = "pageSize";
  /**
   * The constant ATT_PAGER.
   */
  String ATT_PAGER = "pager";

  /**
   * The constant ATT_COUNT.
   */
  String ATT_COUNT = "count";
  /**
   * The constant ATT_MIN_COUNT.
   */
  String ATT_MIN_COUNT = "minCount";
  /**
   * The constant ATT_MAX_COUNT.
   */
  String ATT_MAX_COUNT = "maxCount";
  /**
   * The constant ATT_COUNT_DISTRIBUTION.
   */
  String ATT_COUNT_DISTRIBUTION = "countDistribution";

  /**
   * The constant ATT_ASSERT.
   */
  String ATT_ASSERT = "assert";
  /**
   * The constant ATT_TYPE.
   */
  String ATT_TYPE = "type";
  /**
   * The constant ATT_CONTAINER.
   */
  String ATT_CONTAINER = "container";
  /**
   * The constant ATT_OPTIMIZE.
   */
  String ATT_OPTIMIZE = "optimize";
  /**
   * The constant ATT_INVALIDATE.
   */
  String ATT_INVALIDATE = "invalidate";
  /**
   * The constant ATT_ENCODING.
   */
  String ATT_ENCODING = "encoding";
  /**
   * The constant ATT_TARGET.
   */
  String ATT_TARGET = "target";
  /**
   * The constant ATT_URI.
   */
  String ATT_URI = "uri";
  /**
   * The constant ATT_READ_ONLY.
   */
  String ATT_READ_ONLY = "readOnly";
  /**
   * The constant ATT_LAZY.
   */
  String ATT_LAZY = "lazy";
  /**
   * The constant ATT_ACC_UNK_COL_TYPES.
   */
  String ATT_ACC_UNK_COL_TYPES = "acceptUnknownColumnTypes";
  /**
   * The constant ATT_FETCH_SIZE.
   */
  String ATT_FETCH_SIZE = "fetchSize";
  /**
   * The constant ATT_BATCH.
   */
  String ATT_BATCH = "batch";
  /**
   * The constant ATT_META_CACHE.
   */
  String ATT_META_CACHE = "metaCache";
  /**
   * The constant ATT_CATALOG.
   */
  String ATT_CATALOG = "catalog";
  /**
   * The constant ATT_SCHEMA.
   */
  String ATT_SCHEMA = "schema";
  /**
   * The constant ATT_ENVIRONMENT.
   */
  String ATT_ENVIRONMENT = "environment";
  /**
   * The constant ATT_TABLE_FILTER.
   */
  String ATT_TABLE_FILTER = "tableFilter";
  /**
   * The constant ATT_INCL_TABLES.
   */
  String ATT_INCL_TABLES = "includeTables";
  /**
   * The constant ATT_EXCL_TABLES.
   */
  String ATT_EXCL_TABLES = "excludeTables";
  /**
   * The constant ATT_TEST.
   */
  String ATT_TEST = "test";
  /**
   * The constant ATT_DURATION.
   */
  String ATT_DURATION = "duration";
  /**
   * The constant ATT_MIN.
   */
  String ATT_MIN = "min";
  /**
   * The constant ATT_MAX.
   */
  String ATT_MAX = "max";
  /**
   * The constant ATT_GRANULARITY.
   */
  String ATT_GRANULARITY = "granularity";
  /**
   * The constant ATT_DISTRIBUTION.
   */
  String ATT_DISTRIBUTION = "distribution";
  /**
   * The constant ATT_STATS.
   */
  String ATT_STATS = "stats";
  /**
   * The constant ATT_TEMPLATE.
   */
  String ATT_TEMPLATE = "template";
  /**
   * The constant ATT_GENERATOR.
   */
  String ATT_GENERATOR = "generator";
  /**
   * The constant ATT_VALIDATOR.
   */
  String ATT_VALIDATOR = "validator";
  /**
   * The constant ATT_CONVERTER.
   */
  String ATT_CONVERTER = "converter";
  /**
   * The constant ATT_NULL_QUOTA.
   */
  String ATT_NULL_QUOTA = "nullQuota";
  /**
   * The constant ATT_UNIQUE.
   */
  String ATT_UNIQUE = "unique";
  /**
   * The constant ATT_CYCLIC.
   */
  String ATT_CYCLIC = "cyclic";
  /**
   * The constant ATT_SEPARATOR.
   */
  String ATT_SEPARATOR = "separator";
  /**
   * The constant ATT_SUB_SELECTOR.
   */
  String ATT_SUB_SELECTOR = "subSelector";
  /**
   * The constant ATT_DATASET.
   */
  String ATT_DATASET = "dataset";
  /**
   * The constant ATT_NESTING.
   */
  String ATT_NESTING = "nesting";
  /**
   * The constant ATT_LOCALE.
   */
  String ATT_LOCALE = "locale";
  /**
   * The constant ATT_FILTER.
   */
  String ATT_FILTER = "filter";

  /**
   * The constant ATT_CLASS.
   */
  String ATT_CLASS = "class";
  /**
   * The constant ATT_SPEC.
   */
  String ATT_SPEC = "spec";

  /**
   * The constant ATT_DEFAULTS.
   */
  String ATT_DEFAULTS = "defaults";
  /**
   * The constant ATT_PLATFORMS.
   */
  String ATT_PLATFORMS = "platforms";
  /**
   * The constant ATT_DOMAINS.
   */
  String ATT_DOMAINS = "domains";

  /**
   * The constant ATT_DEFAULT_SOURCE.
   */
  String ATT_DEFAULT_SOURCE = "defaultSource";
  /**
   * The constant ATT_IDENTITY.
   */
  String ATT_IDENTITY = "identity";

  /**
   * The constant ATT_NAMESPACE_AWARE.
   */
  String ATT_NAMESPACE_AWARE = "namespaceAware";
  /**
   * The constant ATT_INPUT_URI.
   */
  String ATT_INPUT_URI = "inputUri";
  /**
   * The constant ATT_OUTPUT_URI.
   */
  String ATT_OUTPUT_URI = "outputUri";

  /**
   * The Create entities ext setup.
   */
  Collection<String> CREATE_ENTITIES_EXT_SETUP = CollectionUtil
      .toSet(ATT_PAGESIZE, ATT_THREADS, ATT_CONSUMER, ATT_ON_ERROR);

  /**
   * The Component types.
   */
  Collection<String> COMPONENT_TYPES = CollectionUtil.toSet(EL_ATTRIBUTE, EL_ID, EL_REFERENCE, EL_PART);

}

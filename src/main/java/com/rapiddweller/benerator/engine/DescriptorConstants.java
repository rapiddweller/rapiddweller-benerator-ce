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

import java.util.Collection;

import com.rapiddweller.common.CollectionUtil;

/**
 * Collects element and attribute names used in Benerator's XML descriptor files.<br/>
 * <br/>
 * Created at 24.07.2009 07:39:19
 * @since 0.6.0
 * @author Volker Bergmann
 */

public interface DescriptorConstants {
	
    String EL_SETUP = "setup";
    
    String ATT_DEFAULT_SCRIPT = "defaultScript";
    String ATT_DEFAULT_NULL = "defaultNull";
    String ATT_DEFAULT_ENCODING = "defaultEncoding";
    String ATT_DEFAULT_LINE_SEPARATOR = "defaultLineSeparator";
    String ATT_DEFAULT_TIME_ZONE = "defaultTimeZone";
    String ATT_DEFAULT_LOCALE = "defaultLocale";
    String ATT_DEFAULT_DATASET = "defaultDataset";
    String ATT_DEFAULT_PAGE_SIZE = "defaultPageSize";
    String ATT_DEFAULT_SEPARATOR = "defaultSeparator";
    String ATT_DEFAULT_ONE_TO_ONE = "defaultOneToOne";
    String ATT_DEFAULT_ERR_HANDLER = "defaultErrorHandler";
    String ATT_ACCEPT_UNKNOWN_SIMPLE_TYPES = "acceptUnknownSimpleTypes";
    String ATT_GENERATOR_FACTORY = "generatorFactory";
    String ATT_DEFAULT_IMPORTS = "defaultImports";
    
    String EL_IF = "if";
    String EL_THEN = "then";
    String EL_ELSE = "else";
    String EL_WHILE = "while";
    String EL_BEAN = "bean";
    String EL_PROPERTY = "property";
	String EL_GENERATE = "generate";
	String EL_ITERATE = "iterate";
	String EL_CONSUMER = "consumer";
	String EL_COMMENT = "comment";
    String EL_DEFAULT_COMPONENTS = "defaultComponents";
    String EL_EVALUATE = "evaluate";
    String EL_EXECUTE = "execute";
    String EL_DATABASE = "database";
    String EL_MEMSTORE = "memstore";
    String EL_DOMTREE = "domtree";
    String EL_ECHO = "echo";
    String EL_ERROR = "error";
    String EL_IMPORT = "import";
    String EL_INCLUDE = "include";
    String EL_SETTING = "setting";
    String EL_RUN_TASK = "run-task";
    String EL_VARIABLE = "variable";
    String EL_VALUE = "value";
    String EL_REFERENCE = "reference";
    String EL_ID = "id";
    String EL_COMPOSITE_ID = "composite-id";
    String EL_PART = "part";
    String EL_ATTRIBUTE = "attribute";
    String EL_WAIT = "wait";

    String EL_TRANSCODING_TASK = "transcodingTask";
    String EL_TRANSCODE = "transcode";
    String EL_CASCADE = "cascade";
    String ATT_TABLE = "table";

	String ATT_PASSWORD = "password";
	String ATT_USER = "user";
	String ATT_DRIVER = "driver";
	String ATT_URL = "url";
	String ATT_ID = EL_ID;
	String ATT_MESSAGE = "message";
	String ATT_SELECTOR = "selector";
	String ATT_SOURCE = "source";
	String ATT_SEGMENT = "segment";
	String ATT_FORMAT = "format";
	String ATT_OFFSET = "offset";
    String ATT_REF = "ref";
    String ATT_VALUE = "value";
    String ATT_DEFAULT = "default";
    String ATT_NAME = "name";
    String ATT_ON_ERROR = "onError";
    String ATT_CONSUMER = "consumer";
    String ATT_THREADS = "threads";
    String ATT_PAGESIZE = "pageSize";
    String ATT_PAGER = "pager";
    
    String ATT_COUNT = "count";
    String ATT_MIN_COUNT = "minCount";
    String ATT_MAX_COUNT = "maxCount";
    String ATT_COUNT_DISTRIBUTION = "countDistribution";
    
    String ATT_ASSERT = "assert";
    String ATT_TYPE = "type";
    String ATT_CONTAINER = "container";
    String ATT_OPTIMIZE = "optimize";
    String ATT_INVALIDATE = "invalidate";
    String ATT_ENCODING = "encoding";
    String ATT_TARGET = "target";
    String ATT_URI = "uri";
    String ATT_READ_ONLY = "readOnly";
    String ATT_LAZY = "lazy";
    String ATT_ACC_UNK_COL_TYPES = "acceptUnknownColumnTypes";
    String ATT_FETCH_SIZE = "fetchSize";
    String ATT_BATCH = "batch";
    String ATT_META_CACHE = "metaCache";
    String ATT_CATALOG = "catalog";
    String ATT_SCHEMA = "schema";
    String ATT_ENVIRONMENT = "environment";
    String ATT_TABLE_FILTER = "tableFilter";
    String ATT_INCL_TABLES = "includeTables";
    String ATT_EXCL_TABLES = "excludeTables";
    String ATT_TEST = "test";
    String ATT_DURATION = "duration";
    String ATT_MIN = "min";
    String ATT_MAX = "max";
    String ATT_GRANULARITY = "granularity";
    String ATT_DISTRIBUTION = "distribution";
    String ATT_STATS = "stats";
	String ATT_TEMPLATE = "template";
	String ATT_GENERATOR = "generator";
	String ATT_VALIDATOR = "validator";
	String ATT_CONVERTER = "converter";
	String ATT_NULL_QUOTA = "nullQuota";
	String ATT_UNIQUE = "unique";
	String ATT_CYCLIC = "cyclic";
	String ATT_SEPARATOR = "separator";
	String ATT_SUB_SELECTOR = "subSelector";
	String ATT_DATASET = "dataset";
	String ATT_NESTING = "nesting";
    String ATT_LOCALE = "locale";
	String ATT_FILTER = "filter";
	
    String ATT_CLASS = "class";
    String ATT_SPEC = "spec";

    String ATT_DEFAULTS = "defaults";
    String ATT_PLATFORMS = "platforms";
    String ATT_DOMAINS = "domains";

    String ATT_DEFAULT_SOURCE = "defaultSource";
    String ATT_IDENTITY = "identity";
    
    String ATT_NAMESPACE_AWARE = "namespaceAware";
    String ATT_INPUT_URI = "inputUri";
    String ATT_OUTPUT_URI = "outputUri";

    Collection<String> CREATE_ENTITIES_EXT_SETUP = CollectionUtil
		.toSet(ATT_PAGESIZE, ATT_THREADS, ATT_CONSUMER, ATT_ON_ERROR);

	Collection<String> COMPONENT_TYPES = CollectionUtil.toSet(EL_ATTRIBUTE, EL_ID, EL_REFERENCE, EL_PART);

}

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

import com.rapiddweller.commons.CollectionUtil;

/**
 * Collects element and attribute names used in Benerator's XML descriptor files.<br/>
 * <br/>
 * Created at 24.07.2009 07:39:19
 * @since 0.6.0
 * @author Volker Bergmann
 */

public interface DescriptorConstants {
	
    static final String EL_SETUP = "setup";
    
    static final String ATT_DEFAULT_SCRIPT = "defaultScript";
    static final String ATT_DEFAULT_NULL = "defaultNull";
    static final String ATT_DEFAULT_ENCODING = "defaultEncoding";
    static final String ATT_DEFAULT_LINE_SEPARATOR = "defaultLineSeparator";
    static final String ATT_DEFAULT_TIME_ZONE = "defaultTimeZone";
    static final String ATT_DEFAULT_LOCALE = "defaultLocale";
    static final String ATT_DEFAULT_DATASET = "defaultDataset";
    static final String ATT_DEFAULT_PAGE_SIZE = "defaultPageSize";
    static final String ATT_DEFAULT_SEPARATOR = "defaultSeparator";
    static final String ATT_DEFAULT_ONE_TO_ONE = "defaultOneToOne";
    static final String ATT_DEFAULT_ERR_HANDLER = "defaultErrorHandler";
    static final String ATT_ACCEPT_UNKNOWN_SIMPLE_TYPES = "acceptUnknownSimpleTypes";
    static final String ATT_GENERATOR_FACTORY = "generatorFactory";
    static final String ATT_DEFAULT_IMPORTS = "defaultImports";
    
    static final String EL_IF = "if";
    static final String EL_THEN = "then";
    static final String EL_ELSE = "else";
    static final String EL_WHILE = "while";
    static final String EL_BEAN = "bean";
    static final String EL_PROPERTY = "property";
	static final String EL_GENERATE = "generate";
	static final String EL_ITERATE = "iterate";
	static final String EL_CONSUMER = "consumer";
	static final String EL_COMMENT = "comment";
    static final String EL_DEFAULT_COMPONENTS = "defaultComponents";
    static final String EL_EVALUATE = "evaluate";
    static final String EL_EXECUTE = "execute";
    static final String EL_DATABASE = "database";
    static final String EL_MEMSTORE = "memstore";
    static final String EL_DOMTREE = "domtree";
    static final String EL_ECHO = "echo";
    static final String EL_ERROR = "error";
    static final String EL_IMPORT = "import";
    static final String EL_INCLUDE = "include";
    static final String EL_SETTING = "setting";
    static final String EL_RUN_TASK = "run-task";
    static final String EL_VARIABLE = "variable";
    static final String EL_VALUE = "value";
    static final String EL_REFERENCE = "reference";
    static final String EL_ID = "id";
    static final String EL_COMPOSITE_ID = "composite-id";
    static final String EL_PART = "part";
    static final String EL_ATTRIBUTE = "attribute";
    static final String EL_WAIT = "wait";

    static final String EL_TRANSCODING_TASK = "transcodingTask";
    static final String EL_TRANSCODE = "transcode";
    static final String EL_CASCADE = "cascade";
    static final String ATT_TABLE = "table";

	static final String ATT_PASSWORD = "password";
	static final String ATT_USER = "user";
	static final String ATT_DRIVER = "driver";
	static final String ATT_URL = "url";
	static final String ATT_ID = EL_ID;
	static final String ATT_MESSAGE = "message";
	static final String ATT_SELECTOR = "selector";
	static final String ATT_SOURCE = "source";
	static final String ATT_SEGMENT = "segment";
	static final String ATT_FORMAT = "format";
	static final String ATT_OFFSET = "offset";
    static final String ATT_REF = "ref";
    static final String ATT_VALUE = "value";
    static final String ATT_DEFAULT = "default";
    static final String ATT_NAME = "name";
    static final String ATT_ON_ERROR = "onError";
    static final String ATT_CONSUMER = "consumer";
    static final String ATT_THREADS = "threads";
    static final String ATT_PAGESIZE = "pageSize";
    static final String ATT_PAGER = "pager";
    
    static final String ATT_COUNT = "count";
    static final String ATT_MIN_COUNT = "minCount";
    static final String ATT_MAX_COUNT = "maxCount";
    static final String ATT_COUNT_DISTRIBUTION = "countDistribution";
    
    static final String ATT_ASSERT = "assert";
    static final String ATT_TYPE = "type";
    static final String ATT_CONTAINER = "container";
    static final String ATT_OPTIMIZE = "optimize";
    static final String ATT_INVALIDATE = "invalidate";
    static final String ATT_ENCODING = "encoding";
    static final String ATT_TARGET = "target";
    static final String ATT_URI = "uri";
    static final String ATT_READ_ONLY = "readOnly";
    static final String ATT_LAZY = "lazy";
    static final String ATT_ACC_UNK_COL_TYPES = "acceptUnknownColumnTypes";
    static final String ATT_FETCH_SIZE = "fetchSize";
    static final String ATT_BATCH = "batch";
    static final String ATT_META_CACHE = "metaCache";
    static final String ATT_CATALOG = "catalog";
    static final String ATT_SCHEMA = "schema";
    static final String ATT_ENVIRONMENT = "environment";
    static final String ATT_TABLE_FILTER = "tableFilter";
    static final String ATT_INCL_TABLES = "includeTables";
    static final String ATT_EXCL_TABLES = "excludeTables";
    static final String ATT_TEST = "test";
    static final String ATT_DURATION = "duration";
    static final String ATT_MIN = "min";
    static final String ATT_MAX = "max";
    static final String ATT_GRANULARITY = "granularity";
    static final String ATT_DISTRIBUTION = "distribution";
    static final String ATT_STATS = "stats";
	static final String ATT_TEMPLATE = "template";
	static final String ATT_GENERATOR = "generator";
	static final String ATT_VALIDATOR = "validator";
	static final String ATT_CONVERTER = "converter";
	static final String ATT_NULL_QUOTA = "nullQuota";
	static final String ATT_UNIQUE = "unique";
	static final String ATT_CYCLIC = "cyclic";
	static final String ATT_SEPARATOR = "separator";
	static final String ATT_SUB_SELECTOR = "subSelector";
	static final String ATT_DATASET = "dataset";
	static final String ATT_NESTING = "nesting";
    static final String ATT_LOCALE = "locale";
	static final String ATT_FILTER = "filter";
	
    static final String ATT_CLASS = "class";
    static final String ATT_SPEC = "spec";

    static final String ATT_DEFAULTS = "defaults";
    static final String ATT_PLATFORMS = "platforms";
    static final String ATT_DOMAINS = "domains";

    static final String ATT_DEFAULT_SOURCE = "defaultSource";
    static final String ATT_IDENTITY = "identity";
    
    static final String ATT_NAMESPACE_AWARE = "namespaceAware";
    static final String ATT_INPUT_URI = "inputUri";
    static final String ATT_OUTPUT_URI = "outputUri";

    static final Collection<String> CREATE_ENTITIES_EXT_SETUP = CollectionUtil
		.toSet(ATT_PAGESIZE, ATT_THREADS, ATT_CONSUMER, ATT_ON_ERROR);

	static final Collection<String> COMPONENT_TYPES = CollectionUtil.toSet(EL_ATTRIBUTE, EL_ID, EL_REFERENCE, EL_PART);

}

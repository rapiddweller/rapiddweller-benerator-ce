/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator;

/**
 * Lists Benerator error codes.<br/><br/>
 * Created: 17.11.2021 23:33:15
 * @author Volker Bergmann
 * @since 1.1.4
 */
public class BeneratorErrorIds {

  // BEN-0001...0009 Unspecific error ids concerning command line usage (mapped from CLI-xxxx) =======================

  public static final String CLI_ILLEGAL_ARGUMENT = "BEN-0001";
  public static final String CLI_ILLEGAL_OPTION = "BEN-0002";
  public static final String CLI_MISSING_OPTION_VALUE = "BEN-0003";
  public static final String CLI_ILLEGAL_OPTION_VALUE = "BEN-0004";

  // BEN-0010...0049 Specific error ids concerning command line usage ================================================

  public static final String BEN_FILE_NOT_FOUND = "BEN-0010";
  public static final String CLI_ILLEGAL_MODE = "BEN-0011";
  public static final String CLI_ILLEGAL_LIST = "BEN-0012";
  public static final String CLI_ILLEGAL_ANON_REPORT = "BEN-0013";

  // BEN-0050...0099 Benerator component initialization failure ======================================================

  /** Unspecific error id for an initialization failure of benerator
   *  (a failure which is not related to the content of any specific benerator file). */
  public static final String BENERATOR_INITIALIZATION_FAILED = "BEN-0050";

  // Specific error ids for initialization failures of specific benerator components

  public static final String COMP_INIT_FAILED_CONVERTER = "BEN-0051";
  public static final String COMP_INIT_FAILED_DELOCALIZING = "BEN-0052";
  public static final String COMP_INIT_FAILED_SCRIPT = "BEN-0053";
  public static final String COMP_INIT_FAILED_JDBACL = "BEN-0054";
  public static final String COMP_INIT_FAILED_COUNTRY = "BEN-0055";
  public static final String COMP_INIT_FAILED_BEN_MONITOR = "BEN-0056";

  public static final String OUT_OF_MEMORY = "BEN-0099";

  // BEN-0100...0199 Benerator file syntax errors (in benerator.xml or *.ben.xml) ====================================

  public static final String SYN_EMPTY_BEN_FILE = "BEN-0100";
  public static final String SYN_BEN_FILE_NO_XML = "BEN-0101";
  public static final String SYN_ILLEGAL_ROOT = "BEN-0102";

  /** Generic error code for an illegal XML element in a Benerator file
   *  (as fallback for elements for which no custom error id has been specified). */
  public static final String SYN_ILLEGAL_ELEMENT = "BEN-0103";

  /** Generic error code for a legal XML element in a Benerator file that is placed in an inappropriate location
   *  (as fallback for elements for which no custom error id has been specified). */
  public static final String SYN_MISPLACED_ELEMENT = "BEN-0104";

  /** Generic error code for an illegal XML attribute in a legal XML element of a Benerator file. */
  public static final String SYN_ILLEGAL_ATTRIBUTE = "BEN-0105";

  /** Generic error code for a missing but required XML attribute in a Benerator file. */
  public static final String SYN_MISSING_ATTRIBUTE = "BEN-0106";

  /** Generic error code for an illegal combination of XML attributes in a legal XML element of a Benerator file. */
  public static final String SYN_ILLEGAL_ATTRIBUTE_COMBO = "BEN-0107";

  /** Generic error code for an illegal value in a legal XML attribute in a legal XML element of a Benerator file. */
  public static final String SYN_ILLEGAL_ATTRIBUTE_VALUE = "BEN-0108";


  // BEN-0130...0149 file issues =====================================================================================

  public static final String FILE_REF_NOT_FOUND = "BEN-0130";
  public static final String FILE_REF_ACCESS = "BEN-0131";
  public static final String FILE_REF_PERMISSION = "BEN-0132";


  // BEN-0150...0200 Reserved for file syntax errors =================================================================

  public static final String SYN_EMPTY_XML_FILE = "BEN-0150";
  public static final String SYN_NO_XML_FILE = "BEN-0151";

  // BEN-0200...0499 Syntax Errors ===================================================================================

  // Syntax Errors regarding <setup> ---------------------------------------------------------------------------------

  public static final String SYN_SETUP_ILLEGAL_ATTRIBUTE       = "BEN-0200";
  public static final String SYN_SETUP_MAX_COUNT               = "BEN-0201";
  public static final String SYN_SETUP_DEF_SCRIPT              = "BEN-0202";
  public static final String SYN_SETUP_DEF_NULL                = "BEN-0203";
  public static final String SYN_SETUP_DEF_ENCODING            = "BEN-0204";
  public static final String SYN_SETUP_DEF_LINE_SEPARATOR      = "BEN-0205";
  public static final String SYN_SETUP_DEF_LOCALE              = "BEN-0206";
  public static final String SYN_SETUP_DEF_DATASET             = "BEN-0207";
  public static final String SYN_SETUP_DEF_PAGE_SIZE           = "BEN-0208";
  public static final String SYN_SETUP_DEF_SEPARATOR           = "BEN-0209";
  public static final String SYN_SETUP_DEF_ONE_TO_ONE          = "BEN-0210";
  public static final String SYN_SETUP_DEF_ERR_HANDLER         = "BEN-0211";
  public static final String SYN_SETUP_DEF_IMPORTS             = "BEN-0212";
  public static final String SYN_SETUP_DEF_SOURCE_SCRIPTED     = "BEN-0213";
  public static final String SYN_SETUP_ACCEPT_UNK_SIMPLE_TYPES = "BEN-0214";
  public static final String SYN_SETUP_GENERATOR_FACTORY       = "BEN-0215";

  // Syntax Errors regarding <comment> -------------------------------------------------------------------------------

  public static final String SYN_COMMENT = "BEN-0230";

  // Syntax Errors regarding <echo> ----------------------------------------------------------------------------------

  public static final String SYN_ECHO         = "BEN-0233";
  public static final String SYN_ECHO_TYPE    = "BEN-0234";
  public static final String SYN_ECHO_MESSAGE = "BEN-0235";

  // Syntax Errors regarding <beep> ----------------------------------------------------------------------------------

  public static final String SYN_BEEP = "BEN-0236";

  // Syntax Errors regarding <import> --------------------------------------------------------------------------------

  public static final String SYN_IMPORT_ILLEGAL_ATTR = "BEN-0240";
  public static final String SYN_IMPORT_NO_ATTR      = "BEN-0241";
  public static final String SYN_IMPORT_CLASS        = "BEN-0242";
  public static final String SYN_IMPORT_DOMAINS      = "BEN-0243";
  public static final String SYN_IMPORT_PLATFORMS    = "BEN-0244";
  public static final String SYN_IMPORT_DEFAULTS     = "BEN-0245";

  // Syntax Errors regarding <setting> -------------------------------------------------------------------------------

  public static final String SYN_SETTING_ILLEGAL_ATTR = "BEN-0250";
  public static final String SYN_SETTING_NAME         = "BEN-0251";
  public static final String SYN_SETTING_VALUE        = "BEN-0252";
  public static final String SYN_SETTING_DEFAULT      = "BEN-0253";
  public static final String SYN_SETTING_REF          = "BEN-0254";
  public static final String SYN_SETTING_SOURCE       = "BEN-0255";

  // Syntax Errors regarding <include> -------------------------------------------------------------------------------

  public static final String SYN_INCLUDE_ILLEGAL_ATTR  = "BEN-0260";
  public static final String SYN_INCLUDE_URI           = "BEN-0261";
  public static final String SYN_INCLUDE_URI_NOT_FOUND = "BEN-0262";
  public static final String SYN_INCLUDE_URI_NO_ACCESS = "BEN-0263";

  // Syntax Errors regarding <bean> ----------------------------------------------------------------------------------

  public static final String SYN_BEAN_ILLEGAL_ATTR = "BEN-0270";
  public static final String SYN_BEAN_ID           = "BEN-0271";
  public static final String SYN_BEAN_CLASS        = "BEN-0272";
  public static final String SYN_BEAN_SPEC         = "BEN-0273";
  public static final String SYN_BEAN_MUST_HAVE_CLASS_OR_SPEC = "BEN-0274";
  public static final String SYN_BEAN_PROP_ELEMENT = "BEN-0275";
  public static final String SYN_BEAN_PROP_NAME    = "BEN-0276";
  public static final String SYN_BEAN_PROP_VALUE   = "BEN-0277";
  public static final String SYN_BEAN_PROP_DEFAULT = "BEN-0278";
  public static final String SYN_BEAN_PROP_REF     = "BEN-0279";
  public static final String SYN_BEAN_PROP_SOURCE  = "BEN-0280";

  // Syntax Errors regarding <memstore> ------------------------------------------------------------------------------

  public static final String SYN_MEMSTORE_ILLEGAL_ATTR = "BEN-0285";
  public static final String SYN_MEMSTORE_ID           = "BEN-0286";

  // Syntax Errors regarding <run-task> ------------------------------------------------------------------------------

  public static final String SYN_RUN_TASK_ILLEGAL_ATTR = "BEN-0290";
  public static final String SYN_RUN_TASK_COUNT        = "BEN-0291";
  public static final String SYN_RUN_TASK_THREADS      = "BEN-0292";
  public static final String SYN_RUN_TASK_PAGE_SIZE    = "BEN-0293";
  public static final String SYN_RUN_TASK_STATS        = "BEN-0294";
  public static final String SYN_RUN_TASK_ON_ERROR     = "BEN-0295";
  public static final String SYN_RUN_TASK_PAGER        = "BEN-0296";
  public static final String SYN_RUN_TASK_CLASS        = "BEN-0297";
  public static final String SYN_RUN_TASK_SPEC         = "BEN-0298";

  // Syntax Errors regarding <execute> -------------------------------------------------------------------------------

  public static final String SYN_EXECUTE_ILLEGAL_ATTR  = "BEN-0300";
  public static final String SYN_EXECUTE_URI           = "BEN-0301";
  public static final String SYN_EXECUTE_ENCODING      = "BEN-0302";
  public static final String SYN_EXECUTE_TARGET        = "BEN-0303";
  public static final String SYN_EXECUTE_SEPARATOR     = "BEN-0304";
  public static final String SYN_EXECUTE_TYPE          = "BEN-0305";
  public static final String SYN_EXECUTE_SHELL         = "BEN-0306";
  public static final String SYN_EXECUTE_ON_ERROR      = "BEN-0307";
  public static final String SYN_EXECUTE_OPTIMIZE      = "BEN-0308";
  public static final String SYN_EXECUTE_INVALIDATE    = "BEN-0309";

  // Syntax Errors regarding <evaluate> ------------------------------------------------------------------------------

  public static final String SYN_EVALUATE            = "BEN-0320";
  public static final String SYN_EVALUATE_URI        = "BEN-0321";
  public static final String SYN_EVALUATE_ENCODING   = "BEN-0322";
  public static final String SYN_EVALUATE_TARGET     = "BEN-0323";
  public static final String SYN_EVALUATE_SEPARATOR  = "BEN-0324";
  public static final String SYN_EVALUATE_TYPE       = "BEN-0325";
  public static final String SYN_EVALUATE_SHELL      = "BEN-0326";
  public static final String SYN_EVALUATE_ON_ERROR   = "BEN-0327";
  public static final String SYN_EVALUATE_OPTIMIZE   = "BEN-0328";
  public static final String SYN_EVALUATE_INVALIDATE = "BEN-0329";
  public static final String SYN_EVALUATE_ID         = "BEN-0330";
  public static final String SYN_EVALUATE_ASSERT     = "BEN-0331";

  // Syntax Errors regarding <error> ---------------------------------------------------------------------------------

  public static final String SYN_ERROR_ILLEGAL_ATTR = "BEN-0340";
  public static final String SYN_ERROR_MESSAGE      = "BEN-0341";
  public static final String SYN_ERROR_ID           = "BEN-0342";
  public static final String SYN_ERROR_EXIT_CODE    = "BEN-0343";

  // Syntax Errors regarding <if> ------------------------------------------------------------------------------------

  public static final String SYN_IF_ILLEGAL_ATTR  = "BEN-0350";
  public static final String SYN_IF_ILLEGAL_CHILD = "BEN-0351";
  public static final String SYN_IF_TEST          = "BEN-0352";
  public static final String SYN_IF_THEN          = "BEN-0353";
  public static final String SYN_IF_ELSE          = "BEN-0354";
  public static final String SYN_IF_ELSE_WO_THEN  = "BEN-0355";

  // Syntax Errors regarding <while> ---------------------------------------------------------------------------------

  public static final String SYN_WHILE_ILLEGAL_ATTR = "BEN-0360";
  public static final String SYN_WHILE_TEST         = "BEN-0361";

  // Syntax Errors regarding <anon-check> ----------------------------------------------------------------------------

  public static final String SYN_ANON_CHECK = "BEN-0363";

  // Syntax Errors regarding <defaultComponents> ---------------------------------------------------------------------

  public static final String SYN_DEFAULT_COMPONENTS = "BEN-0366";

  // Syntax Errors regarding <wait> ----------------------------------------------------------------------------------

  public static final String SYN_WAIT_ILLEGAL_ATTRIBUTE = "BEN-0370";
  public static final String SYN_WAIT_DURATION          = "BEN-0371";
  public static final String SYN_WAIT_MIN               = "BEN-0372";
  public static final String SYN_WAIT_MAX               = "BEN-0373";
  public static final String SYN_WAIT_GRANULARITY       = "BEN-0374";
  public static final String SYN_WAIT_DISTRIBUTION      = "BEN-0375";
  public static final String SYN_WAIT_MUTUALLY_EXCLUDED = "BEN-0376";
  // Syntax Errors regarding <domtree> -------------------------------------------------------------------------------

  public static final String SYN_DOMTREE_ID         = "BEN-0380";
  public static final String SYN_DOMTREE_INPUT_URI  = "BEN-0381";
  public static final String SYN_DOMTREE_OUTPUT_URI = "BEN-0382";
  public static final String SYN_DOMTREE_NS_AWARE   = "BEN-0383";

  // Syntax Errors regarding <template> ------------------------------------------------------------------------------

  public static final String SYN_TEMPLATE_NAME = "BEN-0390";
  public static final String SYN_TEMPLATE_TYPE = "BEN-0391";

  // Syntax Errors regarding <generate> ------------------------------------------------------------------------------

  public static final String SYN_GENERATE_ILLEGAL_ATTR = "BEN-0400";
  public static final String SYN_GENERATE_NAME         = "BEN-0401";
  public static final String SYN_GENERATE_TYPE         = "BEN-0402";
  public static final String SYN_GENERATE_SCOPE        = "BEN-0403";
  public static final String SYN_GENERATE_CONVERTER    = "BEN-0404";
  public static final String SYN_GENERATE_NULL_QUOTA   = "BEN-0405";
  public static final String SYN_GENERATE_UNIQUE       = "BEN-0406";
  public static final String SYN_GENERATE_DISTRIBUTION = "BEN-0407";
  public static final String SYN_GENERATE_CYCLIC       = "BEN-0408";
  public static final String SYN_GENERATE_OFFSET       = "BEN-0409";
  public static final String SYN_GENERATE_SENSOR       = "BEN-0410";
  public static final String SYN_GENERATE_GENERATOR    = "BEN-0411";
  public static final String SYN_GENERATE_VALIDATOR    = "BEN-0412";
  public static final String SYN_GENERATE_MIN_COUNT    = "BEN-0413";
  public static final String SYN_GENERATE_MAX_COUNT    = "BEN-0414";
  public static final String SYN_GENERATE_COUNT_DISTRIBUTION = "BEN-0415";
  public static final String SYN_GENERATE_COUNT        = "BEN-0416";
  public static final String SYN_GENERATE_THREADS      = "BEN-0417";
  public static final String SYN_GENERATE_PAGE_SIZE    = "BEN-0418";
  public static final String SYN_GENERATE_STATS        = "BEN-0419";
  public static final String SYN_GENERATE_ON_ERROR     = "BEN-0420";
  public static final String SYN_GENERATE_TEMPLATE     = "BEN-0421";
  public static final String SYN_GENERATE_CONSUMER     = "BEN-0422";

  // Syntax Errors regarding <iterate> -------------------------------------------------------------------------------

  public static final String SYN_ITERATE_ILLEGAL_ATTR    = "BEN-0450";
  public static final String SYN_ITERATE_NAME            = "BEN-0451";
  public static final String SYN_ITERATE_TYPE            = "BEN-0452";
  public static final String SYN_ITERATE_SCOPE           = "BEN-0453";
  public static final String SYN_ITERATE_CONVERTER       = "BEN-0454";
  public static final String SYN_ITERATE_NULL_QUOTA      = "BEN-0455";
  public static final String SYN_ITERATE_UNIQUE          = "BEN-0456";
  public static final String SYN_ITERATE_DISTRIBUTION    = "BEN-0457";
  public static final String SYN_ITERATE_CYCLIC          = "BEN-0458";
  public static final String SYN_ITERATE_OFFSET          = "BEN-0459";
  public static final String SYN_ITERATE_SENSOR          = "BEN-0460";
  public static final String SYN_ITERATE_SOURCE          = "BEN-0461";
  public static final String SYN_ITERATE_SOURCE_SCRIPTED = "BEN-0462";
  public static final String SYN_ITERATE_SEPARATOR       = "BEN-0463";
  public static final String SYN_ITERATE_FORMAT          = "BEN-0464";
  public static final String SYN_ITERATE_ROW_BASED       = "BEN-0465";
  public static final String SYN_ITERATE_EMPTY_MARKER    = "BEN-0466";
  public static final String SYN_ITERATE_ENCODING        = "BEN-0467";
  public static final String SYN_ITERATE_SEGMENT         = "BEN-0468";
  public static final String SYN_ITERATE_SELECTOR        = "BEN-0469";
  public static final String SYN_ITERATE_SUB_SELECTOR    = "BEN-0470";
  public static final String SYN_ITERATE_DATASET         = "BEN-0471";
  public static final String SYN_ITERATE_NESTING         = "BEN-0472";
  public static final String SYN_ITERATE_LOCALE          = "BEN-0473";
  public static final String SYN_ITERATE_FILTER          = "BEN-0474";
  public static final String SYN_ITERATE_VALIDATOR       = "BEN-0475";
  public static final String SYN_ITERATE_MIN_COUNT       = "BEN-0476";
  public static final String SYN_ITERATE_MAX_COUNT       = "BEN-0477";
  public static final String SYN_ITERATE_COUNT_DIST      = "BEN-0478";
  public static final String SYN_ITERATE_COUNT           = "BEN-0479";
  public static final String SYN_ITERATE_THREADS         = "BEN-0480";
  public static final String SYN_ITERATE_PAGE_SIZE       = "BEN-0481";
  public static final String SYN_ITERATE_STATS           = "BEN-0482";
  public static final String SYN_ITERATE_ON_ERROR        = "BEN-0483";
  public static final String SYN_ITERATE_TEMPLATE        = "BEN-0484";
  public static final String SYN_ITERATE_CONSUMER        = "BEN-0485";

  // BEN-0500...0599 relational database issues ======================================================================

  // Syntax Errors regarding <database> ------------------------------------------------------------------------------

  public static final String SYN_DATABASE_ILLEGAL_ATTR         = "BEN-0500";
  public static final String SYN_DATABASE_ID                   = "BEN-0501";
  public static final String SYN_DATABASE_ENVIRONMENT          = "BEN-0502";
  public static final String SYN_DATABASE_SYSTEM               = "BEN-0503";
  public static final String SYN_DATABASE_URL                  = "BEN-0504";
  public static final String SYN_DATABASE_DRIVER               = "BEN-0505";
  public static final String SYN_DATABASE_USER                 = "BEN-0506";
  public static final String SYN_DATABASE_PASSWORD             = "BEN-0507";
  public static final String SYN_DATABASE_CATALOG              = "BEN-0508";
  public static final String SYN_DATABASE_SCHEMA               = "BEN-0509";
  public static final String SYN_DATABASE_TABLE_FILTER         = "BEN-0510";
  public static final String SYN_DATABASE_QUOTE_TABLE_NAMES    = "BEN-0511";
  public static final String SYN_DATABASE_INCLUDE_TABLES       = "BEN-0512";
  public static final String SYN_DATABASE_EXCLUDE_TABLES       = "BEN-0513";
  public static final String SYN_DATABASE_BATCH                = "BEN-0514";
  public static final String SYN_DATABASE_FETCH_SIZE           = "BEN-0515";
  public static final String SYN_DATABASE_READ_ONLY            = "BEN-0516";
  public static final String SYN_DATABASE_LAZY                 = "BEN-0517";
  public static final String SYN_DATABASE_META_CACHE           = "BEN-0518";
  public static final String SYN_DATABASE_ACCEPT_UNK_COL_TYPES = "BEN-0519";

  public static final String DB_CONNECT_FAILED                 = "BEN-0520";

  // Syntax Errors regarding <transcodingTask> -----------------------------------------------------------------------

  public static final String SYN_TRANSCODING_TASK_ILLEGAL_ATTR   = "BEN-0530";
  public static final String SYN_TRANSCODING_TASK_IDENTITY       = "BEN-0531";
  public static final String SYN_TRANSCODING_TASK_DEFAULT_SOURCE = "BEN-0532";
  public static final String SYN_TRANSCODING_TASK_TARGET         = "BEN-0533";
  public static final String SYN_TRANSCODING_TASK_PAGE_SIZE      = "BEN-0534";
  public static final String SYN_TRANSCODING_TASK_ON_ERROR       = "BEN-0535";

  public static final String SYN_TRANSCODE_ILLEGAL_ATTR = "BEN-0540";
  public static final String SYN_TRANSCODE_SOURCE       = "BEN-0541";
  public static final String SYN_TRANSCODE_SELECTOR     = "BEN-0542";
  public static final String SYN_TRANSCODE_TARGET       = "BEN-0543";
  public static final String SYN_TRANSCODE_PAGE_SIZE    = "BEN-0544";
  public static final String SYN_TRANSCODE_ON_ERROR     = "BEN-0545";

  public static final String SYN_CASCADE_ILLEGAL_ATTR   = "BEN-0550";
  public static final String SYN_CASCADE_REF            = "BEN-0551";

  // Syntax Errors regarding <dbsanity> ------------------------------------------------------------------------------

  public static final String SYN_DBSANITY_ENVIRONMENT = "BEN-0580";
  public static final String SYN_DBSANITY_SYSTEM      = "BEN-0581";
  public static final String SYN_DBSANITY_APP_VERSION = "BEN-0582";
  public static final String SYN_DBSANITY_IN          = "BEN-0583";
  public static final String SYN_DBSANITY_OUT         = "BEN-0584";
  public static final String SYN_DBSANITY_TABLES      = "BEN-0585";
  public static final String SYN_DBSANITY_SKIN        = "BEN-0586";
  public static final String SYN_DBSANITY_LOCALE      = "BEN-0587";
  public static final String SYN_DBSANITY_MODE        = "BEN-0588";
  public static final String SYN_DBSANITY_ON_ERROR    = "BEN-0589";

  // BEN-0600...0699 JMS issues ======================================================================================

  // Syntax Errors regarding <jms-destination> -----------------------------------------------------------------------

  public static final String SYN_JMS_DEST_ILLEGAL_ATTR = "BEN-0600";
  public static final String SYN_JMS_DEST_ID           = "BEN-0601";
  public static final String SYN_JMS_DEST_FACTORY      = "BEN-0602";
  public static final String SYN_JMS_DEST_URL          = "BEN-0603";
  public static final String SYN_JMS_DEST_NAME         = "BEN-0604";
  public static final String SYN_JMS_DEST_TYPE         = "BEN-0605";
  public static final String SYN_JMS_DEST_FORMAT       = "BEN-0606";

  // BEN-0700...0899 Kafka issues ====================================================================================

  // Syntax Errors in common attributes of <kafka-importer> and <kafka-exporter> -------------------------------------

  public static final String SYN_KK = "BEN-0700";
  public static final String SYN_KK_ID = "BEN-0701";
  public static final String SYN_KK_ENVIRONMENT = "BEN-0702";
  public static final String SYN_KK_SYSTEM = "BEN-0703";
  public static final String SYN_KK_PAGE_SIZE = "BEN-0704";
  public static final String SYN_KK_BOOTSTRAP_SERVERS = "BEN-0705";
  public static final String SYN_KK_TOPIC = "BEN-0706";
  public static final String SYN_KK_FORMAT = "BEN-0707";
  public static final String SYN_KK_ENCODING = "BEN-0708";
  public static final String SYN_KK_CLIENT_DNS_LOOKUP = "BEN-0709";
  public static final String SYN_KK_CLIENT_ID = "BEN-0710";
  public static final String SYN_KK_CONNECTIONS_MAX_IDLE_MS = "BEN-0711";
  public static final String SYN_KK_SOCKET_CONNECTION_SETUP_TIMEOUT_MAX_MS = "BEN-0712";
  public static final String SYN_KK_SOCKET_CONNECTION_SETUP_TIMEOUT_MS = "BEN-0713";
  public static final String SYN_KK_INTERCEPTOR_CLASSES = "BEN-0714";
  public static final String SYN_KK_METADATA_MAX_AGE_MS = "BEN-0715";
  public static final String SYN_KK_METRIC_REPORTERS = "BEN-0716";
  public static final String SYN_KK_METRICS_NUM_SAMPLES = "BEN-0717";
  public static final String SYN_KK_METRICS_RECORDING_LEVEL = "BEN-0718";
  public static final String SYN_KK_METRICS_SAMPLE_WINDOW_MS = "BEN-0719";
  public static final String SYN_KK_RECEIVE_BUFFER_BYTES = "BEN-0720";
  public static final String SYN_KK_RECONNECT_BACKOFF_MAX_MS = "BEN-0721";
  public static final String SYN_KK_RECONNECT_BACKOFF_MS = "BEN-0722";
  public static final String SYN_KK_REQUEST_TIMEOUT_MS = "BEN-0723";
  public static final String SYN_KK_RETRY_BACKOFF_MS = "BEN-0724";
  public static final String SYN_KK_SEND_BUFFER_BYTES = "BEN-0725";
  public static final String SYN_KK_SECURITY_PROTOCOL = "BEN-0726";
  public static final String SYN_KK_SECURITY_PROVIDERS = "BEN-0727";
  public static final String SYN_KK_SASL_MECHANISM = "BEN-0728";
  public static final String SYN_KK_SASL_CLIENT_CALLBACK_HANDLER_CLASS = "BEN-0729";
  public static final String SYN_KK_SASL_JAAS_CONFIG = "BEN-0730";
  public static final String SYN_KK_SASL_KERBEROS_SERVICE_NAME = "BEN-0731";
  public static final String SYN_KK_SASL_KERBEROS_TICKET_RENEW_JITTER = "BEN-0732";
  public static final String SYN_KK_SASL_KERBEROS_KINIT_CMD = "BEN-0733";
  public static final String SYN_KK_SASL_KERBEROS_MIN_TIME_BEFORE_RELOGIN = "BEN-0734";
  public static final String SYN_KK_SASL_KERBEROS_TICKET_RENEW_WINDOW_FACTOR = "BEN-0735";
  public static final String SYN_KK_SASL_LOGIN_CALLBACK_HANDLER_CLASS = "BEN-0736";
  public static final String SYN_KK_SASL_LOGIN_CLASS = "BEN-0737";
  public static final String SYN_KK_SASL_LOGIN_REFRESH_BUFFER_SECONDS = "BEN-0738";
  public static final String SYN_KK_SASL_LOGIN_REFRESH_MIN_PERIOD_SECONDS = "BEN-0739";
  public static final String SYN_KK_SASL_LOGIN_REFRESH_WINDOW_FACTOR = "BEN-0740";
  public static final String SYN_KK_SASL_LOGIN_REFRESH_WINDOW_JITTER = "BEN-0741";
  public static final String SYN_KK_SSL_ENGINE_FACTORY_CLASS = "BEN-0742";
  public static final String SYN_KK_SSL_ENABLED_PROTOCOLS = "BEN-0743";
  public static final String SYN_KK_SSL_PROTOCOL = "BEN-0744";
  public static final String SYN_KK_SSL_PROVIDER = "BEN-0745";
  public static final String SYN_KK_SSL_SECURE_RANDOM_IMPLEMENTATION = "BEN-0746";
  public static final String SYN_KK_SSL_CIPHER_SUITES = "BEN-0747";
  public static final String SYN_KK_SSL_ENDPOINT_IDENTIFICATION_ALGORITHM = "BEN-0748";
  public static final String SYN_KK_SSL_TRUSTSTORE_LOCATION = "BEN-0749";
  public static final String SYN_KK_SSL_TRUSTSTORE_PASSWORD = "BEN-0750";
  public static final String SYN_KK_SSL_TRUSTSTORE_TYPE = "BEN-0751";
  public static final String SYN_KK_SSL_TRUSTSTORE_CERTIFICATES = "BEN-0752";
  public static final String SYN_KK_SSL_KEY_PASSWORD = "BEN-0753";
  public static final String SYN_KK_SSL_KEYMANAGER_ALGORITHM = "BEN-0754";
  public static final String SYN_KK_SSL_KEYSTORE_LOCATION = "BEN-0755";
  public static final String SYN_KK_SSL_KEYSTORE_TYPE = "BEN-0756";
  public static final String SYN_KK_SSL_KEYSTORE_KEY = "BEN-0757";
  public static final String SYN_KK_SSL_KEYSTORE_PASSWORD = "BEN-0758";
  public static final String SYN_KK_SSL_KEYSTORE_CERTIFICATE_CHAIN = "BEN-0759";
  public static final String SYN_KK_SSL_TRUSTMANAGER_ALGORITHM = "BEN-0760";

  // Syntax Errors regarding <kafka-importer> ------------------------------------------------------------------------

  public static final String SYN_KKIMP_ILLEGAL_ATTR = "BEN-0770";
  public static final String SYN_KKIMP_IDLE_TIMEOUT_SECONDS = "BEN-0771";
  public static final String SYN_KKIMP_KEY_DESERIALIZER = "BEN-0772";
  public static final String SYN_KKIMP_GROUP_ID = "BEN-0773";
  public static final String SYN_KKIMP_AUTO_OFFSET_RESET = "BEN-0774";
  public static final String SYN_KKIMP_ENABLE_AUTO_COMMIT = "BEN-0775";
  public static final String SYN_KKIMP_AUTO_COMMIT_INTERVAL_MS = "BEN-0776";
  public static final String SYN_KKIMP_ALLOW_AUTO_CREATE_TOPICS = "BEN-0777";
  public static final String SYN_KKIMP_CHECK_CRCS = "BEN-0778";
  public static final String SYN_KKIMP_CLIENT_RACK = "BEN-0779";
  public static final String SYN_KKIMP_DEFAULT_API_TIMEOUT_MS = "BEN-0780";
  public static final String SYN_KKIMP_EXCLUDE_INTERNAL_TOPICS = "BEN-0781";
  public static final String SYN_KKIMP_FETCH_MAX_BYTES = "BEN-0782";
  public static final String SYN_KKIMP_FETCH_MAX_WAIT_MS = "BEN-0783";
  public static final String SYN_KKIMP_FETCH_MIN_BYTES = "BEN-0784";
  public static final String SYN_KKIMP_GROUP_INSTANCE_ID = "BEN-0785";
  public static final String SYN_KKIMP_HEARTBEAT_INTERVAL_MS = "BEN-0786";
  public static final String SYN_KKIMP_ISOLATION_LEVEL = "BEN-0787";
  public static final String SYN_KKIMP_MAX_PARTITION_FETCH_BYTES = "BEN-0788";
  public static final String SYN_KKIMP_MAX_POLL_INTERVAL_MS = "BEN-0789";
  public static final String SYN_KKIMP_MAX_POLL_RECORDS = "BEN-0790";
  public static final String SYN_KKIMP_PARTITION_ASSIGNMENT_STRATEGY = "BEN-0791";
  
  // Syntax Errors regarding <kafka-exporter> -------------------------------------------------------------------------------

  public static final String SYN_KKEXP_ILLEGAL_ATTR          = "BEN-0800";
  public static final String SYN_KKEXP_KEY_ATTRIBUTE         = "BEN-0801";
  public static final String SYN_KKEXP_KEY_SERIALIZER        = "BEN-0802";
  public static final String SYN_KKEXP_ACKS                  = "BEN-0803";
  public static final String SYN_KKEXP_BUFFER_MEMORY         = "BEN-0804";
  public static final String SYN_KKEXP_COMPRESSION_TYPE      = "BEN-0805";
  public static final String SYN_KKEXP_RETRIES               = "BEN-0806";
  public static final String SYN_KKEXP_BATCH_SIZE            = "BEN-8097";
  public static final String SYN_KKEXP_DELIVERY_TIMEOUT_MS   = "BEN-0808";
  public static final String SYN_KKEXP_ENABLE_IDEMPOTENCE    = "BEN-0809";
  public static final String SYN_KKEXP_LINGER_MS             = "BEN-0810";
  public static final String SYN_KKEXP_MAX_BLOCK_MS          = "BEN-0811";
  public static final String SYN_KKEXP_MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION = "BEN-0812";
  public static final String SYN_KKEXP_MAX_REQUEST_SIZE       = "BEN-0813";
  public static final String SYN_KKEXP_METADATA_MAX_IDLE_MS   = "BEN-0814";
  public static final String SYN_KKEXP_PARTITIONER_CLASS      = "BEN-0815";
  public static final String SYN_KKEXP_TRANSACTION_TIMEOUT_MS = "BEN-0816";
  public static final String SYN_KKEXP_TRANSACTIONAL_ID       = "BEN-0817";

  // constructor -----------------------------------------------------------------------------------------------------

  private BeneratorErrorIds() {
    // private constructor to prevent instantiation of this utility CLASS
  }

}

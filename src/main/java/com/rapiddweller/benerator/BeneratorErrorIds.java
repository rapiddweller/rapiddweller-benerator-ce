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

  public static final String SYN_ILLEGAL_ROOT = "BEN-0110";

  /** Generic error code for an illegal XML element in a Benerator file
   *  (as fallback for elements for which no custom error id has been specified). */
  public static final String SYN_ILLEGAL_ELEMENT = "BEN-0111";

  /** Generic error code for a legal XML element in a Benerator file that is placed in an inappropriate location
   *  (as fallback for elements for which no custom error id has been specified). */
  public static final String SYN_MISPLACED_ELEMENT = "BEN-0112";

  /** Generic error code for an illegal XML attribute in a legal XML element of a Benerator file. */
  public static final String SYN_ILLEGAL_ATTRIBUTE = "BEN-0113";

  /** Generic error code for a missing but required XML attribute in a Benerator file. */
  public static final String SYN_MISSING_ATTRIBUTE = "BEN-0114";

  /** Generic error code for an illegal combination of XML attributes in a legal XML element of a Benerator file. */
  public static final String SYN_ILLEGAL_ATTRIBUTE_COMBO = "BEN-0115";

  /** Generic error code for an illegal value in a legal XML attribute in a legal XML element of a Benerator file. */
  public static final String SYN_ILLEGAL_ATTRIBUTE_VALUE = "BEN-0116";


  public static final String SCRIPT_FAILED = "BEN-0120";
  public static final String SEGMENT_NOT_FOUND = "BEN-0121";


  // BEN-0130...0149 file issues =====================================================================================

  public static final String FILE_REF_NOT_FOUND = "BEN-0130";
  public static final String FILE_REF_ACCESS = "BEN-0131";
  public static final String FILE_REF_PERMISSION = "BEN-0132";


  // BEN-0150...0200 Reserved for file syntax errors =================================================================

  public static final String SYN_INVALID_XML_FILE = "BEN-0150";
  public static final String SYN_EMPTY_XML_FILE = "BEN-0151";
  public static final String SYN_NO_XML_FILE = "BEN-0152";
  public static final String SYN_MISSING_XML_END_TAG = "BEN-0153";
  public static final String SYN_ILLEGAL_XML_END_TAG = "BEN-0154";

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

  public static final String SYN_ECHO          = "BEN-0233";
  public static final String SYN_ECHO_ILL_ATTR = "BEN-0234";
  public static final String SYN_ECHO_TYPE     = "BEN-0235";
  public static final String SYN_ECHO_MESSAGE  = "BEN-0236";

  // Syntax Errors regarding <beep> ----------------------------------------------------------------------------------

  public static final String SYN_BEEP = "BEN-0238";

  // Syntax Errors regarding <import> --------------------------------------------------------------------------------

  public static final String SYN_IMPORT              = "BEN-0240";
  public static final String SYN_IMPORT_ILLEGAL_ATTR = "BEN-0241";
  public static final String SYN_IMPORT_CLASS        = "BEN-0242";
  public static final String SYN_IMPORT_DOMAINS      = "BEN-0243";
  public static final String SYN_IMPORT_PLATFORMS    = "BEN-0244";
  public static final String SYN_IMPORT_DEFAULTS     = "BEN-0245";

  // Syntax Errors regarding <setting> -------------------------------------------------------------------------------

  public static final String SYN_SETTING              = "BEN-0250";
  public static final String SYN_SETTING_ILLEGAL_ATTR = "BEN-0251";
  public static final String SYN_SETTING_NAME         = "BEN-0252";
  public static final String SYN_SETTING_VALUE        = "BEN-0253";
  public static final String SYN_SETTING_DEFAULT      = "BEN-0254";
  public static final String SYN_SETTING_REF          = "BEN-0255";
  public static final String SYN_SETTING_SOURCE       = "BEN-0256";

  // Syntax Errors regarding <include> -------------------------------------------------------------------------------

  public static final String SYN_INCLUDE_ILLEGAL_ATTR  = "BEN-0260";
  public static final String SYN_INCLUDE_URI           = "BEN-0261";
  public static final String SYN_INCLUDE_URI_NOT_FOUND = "BEN-0262";
  public static final String SYN_INCLUDE_URI_NO_ACCESS = "BEN-0263";

  // Syntax Errors regarding <bean> ----------------------------------------------------------------------------------

  public static final String SYN_BEAN              = "BEN-0270";
  public static final String SYN_BEAN_ILLEGAL_ATTR = "BEN-0271";
  public static final String SYN_BEAN_ID           = "BEN-0272";
  public static final String SYN_BEAN_CLASS        = "BEN-0273";
  public static final String SYN_BEAN_SPEC         = "BEN-0274";
  public static final String SYN_BEAN_MUST_HAVE_CLASS_OR_SPEC = "BEN-0275";
  public static final String SYN_BEAN_PROP_ELEMENT = "BEN-0276";
  public static final String SYN_BEAN_PROP_NAME    = "BEN-0277";
  public static final String SYN_BEAN_PROP_VALUE   = "BEN-0278";
  public static final String SYN_BEAN_PROP_DEFAULT = "BEN-0279";
  public static final String SYN_BEAN_PROP_REF     = "BEN-0280";
  public static final String SYN_BEAN_PROP_SOURCE  = "BEN-0281";

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

  public static final String SYN_EXECUTE               = "BEN-0300";
  public static final String SYN_EXECUTE_ILLEGAL_ATTR  = "BEN-0301";
  public static final String SYN_EXECUTE_URI           = "BEN-0302";
  public static final String SYN_EXECUTE_ENCODING      = "BEN-0303";
  public static final String SYN_EXECUTE_TARGET        = "BEN-0304";
  public static final String SYN_EXECUTE_SEPARATOR     = "BEN-0305";
  public static final String SYN_EXECUTE_TYPE          = "BEN-0306";
  public static final String SYN_EXECUTE_SHELL         = "BEN-0307";
  public static final String SYN_EXECUTE_ON_ERROR      = "BEN-0308";
  public static final String SYN_EXECUTE_OPTIMIZE      = "BEN-0309";
  public static final String SYN_EXECUTE_INVALIDATE    = "BEN-0310";

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

  public static final String SYN_WAIT                   = "BEN-0370";
  public static final String SYN_WAIT_ILLEGAL_ATTRIBUTE = "BEN-0371";
  public static final String SYN_WAIT_DURATION          = "BEN-0372";
  public static final String SYN_WAIT_MIN               = "BEN-0373";
  public static final String SYN_WAIT_MAX               = "BEN-0374";
  public static final String SYN_WAIT_GRANULARITY       = "BEN-0375";
  public static final String SYN_WAIT_DISTRIBUTION      = "BEN-0376";
  public static final String SYN_WAIT_MUTUALLY_EXCLUDED = "BEN-0377";
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
  public static final String SYN_GENERATE_COUNT_GRANULARITY = "BEN-0415";
  public static final String SYN_GENERATE_COUNT_DIST   = "BEN-0416";
  public static final String SYN_GENERATE_COUNT        = "BEN-0417";
  public static final String SYN_GENERATE_THREADS      = "BEN-0418";
  public static final String SYN_GENERATE_PAGE_SIZE    = "BEN-0419";
  public static final String SYN_GENERATE_STATS        = "BEN-0420";
  public static final String SYN_GENERATE_ON_ERROR     = "BEN-0421";
  public static final String SYN_GENERATE_TEMPLATE     = "BEN-0422";
  public static final String SYN_GENERATE_CONSUMER     = "BEN-0423";

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
  public static final String SYN_ITERATE_COUNT_GRANULARITY = "BEN-0478";
  public static final String SYN_ITERATE_COUNT_DIST      = "BEN-0479";
  public static final String SYN_ITERATE_COUNT           = "BEN-0480";
  public static final String SYN_ITERATE_THREADS         = "BEN-0481";
  public static final String SYN_ITERATE_PAGE_SIZE       = "BEN-0482";
  public static final String SYN_ITERATE_STATS           = "BEN-0483";
  public static final String SYN_ITERATE_ON_ERROR        = "BEN-0484";
  public static final String SYN_ITERATE_TEMPLATE        = "BEN-0485";
  public static final String SYN_ITERATE_CONSUMER        = "BEN-0486";

  // Syntax Errors regarding <variable> -----------------------------------------------------------------------------

  public static final String SYN_VAR                = "BEN-0500";
  public static final String SYN_VAR_ILLEGAL_ATTR   = "BEN-0501";
  public static final String SYN_VAR_NAME           = "BEN-0502";

  // Syntax Errors regarding <attribute> -----------------------------------------------------------------------------

  public static final String SYN_ATTR               = "BEN-0550";
  public static final String SYN_ATTR_ILLEGAL_ATTR  = "BEN-0551";
  public static final String SYN_ATTR_NAME          = "BEN-0552";
  public static final String SYN_ATTR_TYPE          = "BEN-0553";

  public static final String SYN_ATTR_ROOT_INFO     = "BEN-0554";
  public static final String SYN_ATTR_MODE          = "BEN-0555";
  public static final String SYN_ATTR_SCOPE         = "BEN-0556";
  public static final String SYN_ATTR_OFFSET        = "BEN-0557";
  public static final String SYN_ATTR_CONDITION     = "BEN-0558";
  public static final String SYN_ATTR_FILTER        = "BEN-0559";
  public static final String SYN_ATTR_UNIQUE        = "BEN-0560";
  public static final String SYN_ATTR_UNIQUE_KEY    = "BEN-0561";

  public static final String SYN_ATTR_CONSTANT      = "BEN-0562";
  public static final String SYN_ATTR_VALUES        = "BEN-0563";
  public static final String SYN_ATTR_PATTERN       = "BEN-0564";
  public static final String SYN_ATTR_SCRIPT        = "BEN-0565";
  public static final String SYN_ATTR_GENERATOR     = "BEN-0566";
  public static final String SYN_ATTR_MIN_LENGTH    = "BEN-0567";
  public static final String SYN_ATTR_MAX_LENGTH    = "BEN-0568";
  public static final String SYN_ATTR_NULL_QUOTA    = "BEN-0569";

  public static final String SYN_ATTR_SOURCE        = "BEN-0570";
  public static final String SYN_ATTR_ENCODING      = "BEN-0571";
  public static final String SYN_ATTR_SEGMENT       = "BEN-0572";
  public static final String SYN_ATTR_SEPARATOR     = "BEN-0573";
  public static final String SYN_ATTR_SELECTOR      = "BEN-0574";
  public static final String SYN_ATTR_SUB_SELECTOR  = "BEN-0575";
  public static final String SYN_ATTR_ROW_BASED     = "BEN-0576";
  public static final String SYN_ATTR_FORMAT        = "BEN-0577";
  public static final String SYN_ATTR_EMPTY_MARKER  = "BEN-0578";

  public static final String SYN_ATTR_NULLABLE      = "BEN-0579";
  public static final String SYN_ATTR_TRUE_QUOTA    = "BEN-0580";

  public static final String SYN_ATTR_MIN           = "BEN-0581";
  public static final String SYN_ATTR_MIN_INCLUSIVE = "BEN-0582";
  public static final String SYN_ATTR_MAX           = "BEN-0583";
  public static final String SYN_ATTR_MAX_INCLUSIVE = "BEN-0584";
  public static final String SYN_ATTR_GRANULARITY   = "BEN-0585";
  public static final String SYN_ATTR_DISTRIBUTION  = "BEN-0586";

  public static final String SYN_ATTR_DATASET       = "BEN-0587";
  public static final String SYN_ATTR_NESTING       = "BEN-0588";
  public static final String SYN_ATTR_LOCALE        = "BEN-0589";

  public static final String SYN_ATTR_CONVERTER     = "BEN-0590";
  public static final String SYN_ATTR_VALIDATOR     = "BEN-0591";
  public static final String SYN_ATTR_CYCLIC        = "BEN-0592";
  public static final String SYN_ATTR_MAP           = "BEN-0593";

  public static final String SYN_ATTR_SOURCE_SCRIPTED = "BEN-0594";

  // Syntax Errors regarding <id> ------------------------------------------------------------------------------------

  public static final String SYN_ID                = "BEN-0600";
  public static final String SYN_ID_ILLEGAL_ATTR   = "BEN-0601";
  public static final String SYN_ID_NAME           = "BEN-0602";

  // Syntax Errors regarding <reference> -----------------------------------------------------------------------------

  public static final String SYN_REF               = "BEN-0650";
  public static final String SYN_REF_ILLEGAL_ATTR  = "BEN-0651";
  public static final String SYN_REF_NAME          = "BEN-0652";

  // Syntax Errors regarding <part> ----------------------------------------------------------------------------------

  public static final String SYN_PART              = "BEN-0800";
  public static final String SYN_PART_ILLEGAL_ATTR = "BEN-0701";
  public static final String SYN_PART_NAME         = "BEN-0702";


  // BEN-0500...0599 relational database issues ======================================================================

  // Syntax Errors regarding <database> ------------------------------------------------------------------------------

  public static final String SYN_DB                      = "BEN-1000";
  public static final String SYN_DB_ILLEGAL_ATTR         = "BEN-1001";
  public static final String SYN_DB_ID                   = "BEN-1002";
  public static final String SYN_DB_ENVIRONMENT          = "BEN-1003";
  public static final String SYN_DB_SYSTEM               = "BEN-1004";
  public static final String SYN_DB_URL                  = "BEN-1005";
  public static final String SYN_DB_DRIVER               = "BEN-1006";
  public static final String SYN_DB_USER                 = "BEN-1007";
  public static final String SYN_DB_PASSWORD             = "BEN-1008";
  public static final String SYN_DB_CATALOG              = "BEN-1009";
  public static final String SYN_DB_SCHEMA               = "BEN-1010";
  public static final String SYN_DB_TABLE_FILTER         = "BEN-1011";
  public static final String SYN_DB_QUOTE_TABLE_NAMES    = "BEN-1012";
  public static final String SYN_DB_INCLUDE_TABLES       = "BEN-1013";
  public static final String SYN_DB_EXCLUDE_TABLES       = "BEN-1014";
  public static final String SYN_DB_BATCH                = "BEN-1015";
  public static final String SYN_DB_FETCH_SIZE           = "BEN-1016";
  public static final String SYN_DB_READ_ONLY            = "BEN-1017";
  public static final String SYN_DB_LAZY                 = "BEN-1018";
  public static final String SYN_DB_META_CACHE           = "BEN-1019";
  public static final String SYN_DB_ACCEPT_UNK_COL_TYPES = "BEN-1020";

  public static final String SYN_DB_URL_GROUP_INCOMPLETE = "BEN-1021";
  public static final String SYN_DB_ENV_GROUP_INCOMPLETE = "BEN-1022";
  public static final String SYN_DB_URL_AND_ENV_GROUP    = "BEN-1023";
  public static final String SYN_DB_NO_URL_AND_ENV_GROUP = "BEN-1024";


  public static final String DB_CONNECT_FAILED = "BEN-1100";
  public static final String DB_QUERY_FAILED   = "BEN-1101";

  // Syntax Errors regarding <transcodingTask> -----------------------------------------------------------------------

  public static final String SYN_TRANSCODING_TASK_ILLEGAL_ATTR   = "BEN-1200";
  public static final String SYN_TRANSCODING_TASK_IDENTITY       = "BEN-1201";
  public static final String SYN_TRANSCODING_TASK_DEFAULT_SOURCE = "BEN-1202";
  public static final String SYN_TRANSCODING_TASK_TARGET         = "BEN-1203";
  public static final String SYN_TRANSCODING_TASK_PAGE_SIZE      = "BEN-1204";
  public static final String SYN_TRANSCODING_TASK_ON_ERROR       = "BEN-1205";

  public static final String SYN_TRANSCODE_ILLEGAL_ATTR = "BEN-1210";
  public static final String SYN_TRANSCODE_SOURCE       = "BEN-1211";
  public static final String SYN_TRANSCODE_SELECTOR     = "BEN-1212";
  public static final String SYN_TRANSCODE_TARGET       = "BEN-1213";
  public static final String SYN_TRANSCODE_PAGE_SIZE    = "BEN-1214";
  public static final String SYN_TRANSCODE_ON_ERROR     = "BEN-1215";

  public static final String SYN_CASCADE_ILLEGAL_ATTR   = "BEN-1220";
  public static final String SYN_CASCADE_REF            = "BEN-1221";

  // BEN-0600...0699 JMS issues ======================================================================================

  // Syntax Errors regarding <jms-destination> -----------------------------------------------------------------------

  public static final String SYN_JMS_DEST_ILLEGAL_ATTR = "BEN-1500";
  public static final String SYN_JMS_DEST_ID           = "BEN-1501";
  public static final String SYN_JMS_DEST_FACTORY      = "BEN-1502";
  public static final String SYN_JMS_DEST_URL          = "BEN-1503";
  public static final String SYN_JMS_DEST_NAME         = "BEN-1504";
  public static final String SYN_JMS_DEST_TYPE         = "BEN-1505";
  public static final String SYN_JMS_DEST_FORMAT       = "BEN-1506";

  // BEN-0700...0899 Kafka issues ====================================================================================

  // Syntax Errors in common attributes of <kafka-importer> and <kafka-exporter> -------------------------------------

  public static final String SYN_KK                          = "BEN-2000";
  public static final String SYN_KK_ID                       = "BEN-2001";
  public static final String SYN_KK_ENVIRONMENT              = "BEN-2002";
  public static final String SYN_KK_SYSTEM                   = "BEN-2003";
  public static final String SYN_KK_PAGE_SIZE                = "BEN-2004";
  public static final String SYN_KK_BOOTSTRAP_SERVERS        = "BEN-2005";
  public static final String SYN_KK_TOPIC                    = "BEN-2006";
  public static final String SYN_KK_FORMAT                   = "BEN-2007";
  public static final String SYN_KK_ENCODING                 = "BEN-2008";
  public static final String SYN_KK_CLIENT_DNS_LOOKUP        = "BEN-2009";
  public static final String SYN_KK_CLIENT_ID                = "BEN-2010";
  public static final String SYN_KK_CONNECTIONS_MAX_IDLE_MS  = "BEN-2011";
  public static final String SYN_KK_SOCKET_CONNECTION_SETUP_TIMEOUT_MAX_MS = "BEN-2012";
  public static final String SYN_KK_SOCKET_CONNECTION_SETUP_TIMEOUT_MS = "BEN-2013";
  public static final String SYN_KK_INTERCEPTOR_CLASSES      = "BEN-2014";
  public static final String SYN_KK_METADATA_MAX_AGE_MS      = "BEN-2015";
  public static final String SYN_KK_METRIC_REPORTERS         = "BEN-2016";
  public static final String SYN_KK_METRICS_NUM_SAMPLES      = "BEN-2017";
  public static final String SYN_KK_METRICS_RECORDING_LEVEL  = "BEN-2018";
  public static final String SYN_KK_METRICS_SAMPLE_WINDOW_MS = "BEN-2019";
  public static final String SYN_KK_RECEIVE_BUFFER_BYTES     = "BEN-2020";
  public static final String SYN_KK_RECONNECT_BACKOFF_MAX_MS = "BEN-2021";
  public static final String SYN_KK_RECONNECT_BACKOFF_MS     = "BEN-2022";
  public static final String SYN_KK_REQUEST_TIMEOUT_MS       = "BEN-2023";
  public static final String SYN_KK_RETRY_BACKOFF_MS         = "BEN-2024";
  public static final String SYN_KK_SEND_BUFFER_BYTES        = "BEN-2025";
  public static final String SYN_KK_SECURITY_PROTOCOL        = "BEN-2026";
  public static final String SYN_KK_SECURITY_PROVIDERS       = "BEN-2027";
  public static final String SYN_KK_SASL_MECHANISM           = "BEN-2028";
  public static final String SYN_KK_SASL_CLIENT_CALLBACK_HANDLER_CLASS = "BEN-2029";
  public static final String SYN_KK_SASL_JAAS_CONFIG         = "BEN-2030";
  public static final String SYN_KK_SASL_KERBEROS_SERVICE_NAME = "BEN-2031";
  public static final String SYN_KK_SASL_KERBEROS_TICKET_RENEW_JITTER = "BEN-2032";
  public static final String SYN_KK_SASL_KERBEROS_KINIT_CMD  = "BEN-2033";
  public static final String SYN_KK_SASL_KERBEROS_MIN_TIME_BEFORE_RELOGIN = "BEN-2034";
  public static final String SYN_KK_SASL_KERBEROS_TICKET_RENEW_WINDOW_FACTOR = "BEN-2035";
  public static final String SYN_KK_SASL_LOGIN_CALLBACK_HANDLER_CLASS = "BEN-2036";
  public static final String SYN_KK_SASL_LOGIN_CLASS         = "BEN-2037";
  public static final String SYN_KK_SASL_LOGIN_REFRESH_BUFFER_SECONDS = "BEN-2038";
  public static final String SYN_KK_SASL_LOGIN_REFRESH_MIN_PERIOD_SECONDS = "BEN-2039";
  public static final String SYN_KK_SASL_LOGIN_REFRESH_WINDOW_FACTOR = "BEN-2040";
  public static final String SYN_KK_SASL_LOGIN_REFRESH_WINDOW_JITTER = "BEN-2041";
  public static final String SYN_KK_SSL_ENGINE_FACTORY_CLASS = "BEN-2042";
  public static final String SYN_KK_SSL_ENABLED_PROTOCOLS    = "BEN-2043";
  public static final String SYN_KK_SSL_PROTOCOL             = "BEN-2044";
  public static final String SYN_KK_SSL_PROVIDER             = "BEN-2045";
  public static final String SYN_KK_SSL_SECURE_RANDOM_IMPLEMENTATION = "BEN-2046";
  public static final String SYN_KK_SSL_CIPHER_SUITES        = "BEN-2047";
  public static final String SYN_KK_SSL_ENDPOINT_IDENTIFICATION_ALGORITHM = "BEN-2048";
  public static final String SYN_KK_SSL_TRUSTSTORE_LOCATION  = "BEN-2049";
  public static final String SYN_KK_SSL_TRUSTSTORE_PASSWORD  = "BEN-2050";
  public static final String SYN_KK_SSL_TRUSTSTORE_TYPE      = "BEN-2051";
  public static final String SYN_KK_SSL_TRUSTSTORE_CERTIFICATES = "BEN-2052";
  public static final String SYN_KK_SSL_KEY_PASSWORD         = "BEN-2053";
  public static final String SYN_KK_SSL_KEYMANAGER_ALGORITHM = "BEN-2054";
  public static final String SYN_KK_SSL_KEYSTORE_LOCATION    = "BEN-2055";
  public static final String SYN_KK_SSL_KEYSTORE_TYPE        = "BEN-2056";
  public static final String SYN_KK_SSL_KEYSTORE_KEY         = "BEN-2057";
  public static final String SYN_KK_SSL_KEYSTORE_PASSWORD    = "BEN-2058";
  public static final String SYN_KK_SSL_KEYSTORE_CERTIFICATE_CHAIN = "BEN-2059";
  public static final String SYN_KK_SSL_TRUSTMANAGER_ALGORITHM = "BEN-2060";
  public static final String SYN_KK_SCHEMA_URL                = "BEN-2061";

  public static final String SYN_KK_AVRO_WITHOUT_SCHEMA       = "BEN-2070";


  // Syntax Errors regarding <kafka-importer> ------------------------------------------------------------------------

  public static final String SYN_KKIMP_ILLEGAL_ATTR              = "BEN-2100";
  public static final String SYN_KKIMP_IDLE_TIMEOUT_SECONDS      = "BEN-2101";
  public static final String SYN_KKIMP_KEY_DESERIALIZER          = "BEN-2102";
  public static final String SYN_KKIMP_GROUP_ID                  = "BEN-2103";
  public static final String SYN_KKIMP_AUTO_OFFSET_RESET         = "BEN-2104";
  public static final String SYN_KKIMP_ENABLE_AUTO_COMMIT        = "BEN-2105";
  public static final String SYN_KKIMP_AUTO_COMMIT_INTERVAL_MS   = "BEN-2106";
  public static final String SYN_KKIMP_ALLOW_AUTO_CREATE_TOPICS  = "BEN-2107";
  public static final String SYN_KKIMP_CHECK_CRCS                = "BEN-2108";
  public static final String SYN_KKIMP_CLIENT_RACK               = "BEN-2109";
  public static final String SYN_KKIMP_DEFAULT_API_TIMEOUT_MS    = "BEN-2110";
  public static final String SYN_KKIMP_EXCLUDE_INTERNAL_TOPICS   = "BEN-2111";
  public static final String SYN_KKIMP_FETCH_MAX_BYTES           = "BEN-2112";
  public static final String SYN_KKIMP_FETCH_MAX_WAIT_MS         = "BEN-2113";
  public static final String SYN_KKIMP_FETCH_MIN_BYTES           = "BEN-2114";
  public static final String SYN_KKIMP_GROUP_INSTANCE_ID         = "BEN-2115";
  public static final String SYN_KKIMP_HEARTBEAT_INTERVAL_MS     = "BEN-2116";
  public static final String SYN_KKIMP_ISOLATION_LEVEL           = "BEN-2117";
  public static final String SYN_KKIMP_MAX_PARTITION_FETCH_BYTES = "BEN-2118";
  public static final String SYN_KKIMP_MAX_POLL_INTERVAL_MS      = "BEN-2119";
  public static final String SYN_KKIMP_MAX_POLL_RECORDS          = "BEN-2120";
  public static final String SYN_KKIMP_PARTITION_ASSIGNMENT_STRATEGY = "BEN-2121";
  
  // Syntax Errors regarding <kafka-exporter> -------------------------------------------------------------------------------

  public static final String SYN_KKEXP_ILLEGAL_ATTR          = "BEN-2200";
  public static final String SYN_KK_KEYEXP_ATTRIBUTE         = "BEN-2201";
  public static final String SYN_KKEXP_KEY_SERIALIZER        = "BEN-2202";
  public static final String SYN_KKEXP_ACKS                  = "BEN-2203";
  public static final String SYN_KKEXP_BUFFER_MEMORY         = "BEN-2204";
  public static final String SYN_KKEXP_COMPRESSION_TYPE      = "BEN-2205";
  public static final String SYN_KKEXP_RETRIES               = "BEN-2206";
  public static final String SYN_KKEXP_BATCH_SIZE            = "BEN-2207";
  public static final String SYN_KKEXP_DELIVERY_TIMEOUT_MS   = "BEN-2208";
  public static final String SYN_KKEXP_ENABLE_IDEMPOTENCE    = "BEN-2209";
  public static final String SYN_KKEXP_LINGER_MS             = "BEN-2210";
  public static final String SYN_KKEXP_MAX_BLOCK_MS          = "BEN-2211";
  public static final String SYN_KKEXP_MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION = "BEN-2212";
  public static final String SYN_KKEXP_MAX_REQUEST_SIZE       = "BEN-2213";
  public static final String SYN_KKEXP_METADATA_MAX_IDLE_MS   = "BEN-2214";
  public static final String SYN_KKEXP_PARTITIONER_CLASS      = "BEN-2215";
  public static final String SYN_KKEXP_TRANSACTION_TIMEOUT_MS = "BEN-2216";
  public static final String SYN_KKEXP_TRANSACTIONAL_ID       = "BEN-2217";

  // Syntax Errors regarding the 'xml' platform ----------------------------------------------------------------------

  public static final String SYN_XML_SIMPLE_NAME = "BEN-2300";
  public static final String SYN_XML_SIMPLE_TYPE = "BEN-2301";

	// end of constants list -------------------------------------------------------------------------------------------

  /** Private constructor to prevent instantiation of this utility class. */
  private BeneratorErrorIds() {
    // private constructor to prevent instantiation of this utility class
  }

}

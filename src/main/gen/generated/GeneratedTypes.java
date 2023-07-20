// This is a generated file. Not intended for manual editing.
package generated;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import org.intellij.sdk.language.psi.SqlppElementType;
import org.intellij.sdk.language.psi.SqlppTokenType;
import generated.psi.impl.*;

public interface GeneratedTypes {

  IElementType ADVISE_STATEMENT = new SqlppElementType("ADVISE_STATEMENT");
  IElementType AGGREGATE_FUNCTION = new SqlppElementType("AGGREGATE_FUNCTION");
  IElementType AGGREGATE_FUNCTION_NAME = new SqlppElementType("AGGREGATE_FUNCTION_NAME");
  IElementType AGGREGATE_QUANTIFIER = new SqlppElementType("AGGREGATE_QUANTIFIER");
  IElementType ALIAS = new SqlppElementType("ALIAS");
  IElementType ALTER_INDEX = new SqlppElementType("ALTER_INDEX");
  IElementType AND_EXPR = new SqlppElementType("AND_EXPR");
  IElementType ANSI_HINT_TERMS = new SqlppElementType("ANSI_HINT_TERMS");
  IElementType ANSI_JOIN_CLAUSE = new SqlppElementType("ANSI_JOIN_CLAUSE");
  IElementType ANSI_JOIN_HINTS = new SqlppElementType("ANSI_JOIN_HINTS");
  IElementType ANSI_JOIN_PREDICATE = new SqlppElementType("ANSI_JOIN_PREDICATE");
  IElementType ANSI_JOIN_RHS = new SqlppElementType("ANSI_JOIN_RHS");
  IElementType ANSI_JOIN_TYPE = new SqlppElementType("ANSI_JOIN_TYPE");
  IElementType ANSI_MERGE = new SqlppElementType("ANSI_MERGE");
  IElementType ANSI_MERGE_ACTIONS = new SqlppElementType("ANSI_MERGE_ACTIONS");
  IElementType ANSI_MERGE_INSERT = new SqlppElementType("ANSI_MERGE_INSERT");
  IElementType ANSI_MERGE_PREDICATE = new SqlppElementType("ANSI_MERGE_PREDICATE");
  IElementType ANSI_MERGE_SOURCE = new SqlppElementType("ANSI_MERGE_SOURCE");
  IElementType ANSI_NEST_CLAUSE = new SqlppElementType("ANSI_NEST_CLAUSE");
  IElementType ANSI_NEST_PREDICATE = new SqlppElementType("ANSI_NEST_PREDICATE");
  IElementType ANSI_NEST_RHS = new SqlppElementType("ANSI_NEST_RHS");
  IElementType ANSI_NEST_TYPE = new SqlppElementType("ANSI_NEST_TYPE");
  IElementType ARITHMETIC_TERM = new SqlppElementType("ARITHMETIC_TERM");
  IElementType ARRAY_EXPR = new SqlppElementType("ARRAY_EXPR");
  IElementType BEGIN_TRANSACTION = new SqlppElementType("BEGIN_TRANSACTION");
  IElementType BETWEEN_EXPR = new SqlppElementType("BETWEEN_EXPR");
  IElementType BLOCK_COMMENT = new SqlppElementType("BLOCK_COMMENT");
  IElementType BLOCK_HINT_COMMENT = new SqlppElementType("BLOCK_HINT_COMMENT");
  IElementType BODY = new SqlppElementType("BODY");
  IElementType BOOL = new SqlppElementType("BOOL");
  IElementType BUCKET_REF = new SqlppElementType("BUCKET_REF");
  IElementType BUILD_INDEX = new SqlppElementType("BUILD_INDEX");
  IElementType BUILTIN_FUNCTION = new SqlppElementType("BUILTIN_FUNCTION");
  IElementType CASE_EXPR = new SqlppElementType("CASE_EXPR");
  IElementType COLLECTION_EXPR = new SqlppElementType("COLLECTION_EXPR");
  IElementType COLLECTION_REF = new SqlppElementType("COLLECTION_REF");
  IElementType COMMA_SEPARATED_JOIN = new SqlppElementType("COMMA_SEPARATED_JOIN");
  IElementType COMMENT = new SqlppElementType("COMMENT");
  IElementType COMMIT_TRANSACTION = new SqlppElementType("COMMIT_TRANSACTION");
  IElementType COMPARISON_TERM = new SqlppElementType("COMPARISON_TERM");
  IElementType CONCATENATION_TERM = new SqlppElementType("CONCATENATION_TERM");
  IElementType COND = new SqlppElementType("COND");
  IElementType CREATE_COLLECTION = new SqlppElementType("CREATE_COLLECTION");
  IElementType CREATE_FUNCTION = new SqlppElementType("CREATE_FUNCTION");
  IElementType CREATE_FUNCTION_EXTERNAL = new SqlppElementType("CREATE_FUNCTION_EXTERNAL");
  IElementType CREATE_FUNCTION_INLINE = new SqlppElementType("CREATE_FUNCTION_INLINE");
  IElementType CREATE_INDEX = new SqlppElementType("CREATE_INDEX");
  IElementType CREATE_PRIMARY_INDEX = new SqlppElementType("CREATE_PRIMARY_INDEX");
  IElementType CREATE_SCOPE = new SqlppElementType("CREATE_SCOPE");
  IElementType CREATE_STATEMENT = new SqlppElementType("CREATE_STATEMENT");
  IElementType DCL_STATEMENT = new SqlppElementType("DCL_STATEMENT");
  IElementType DDL_STATEMENT = new SqlppElementType("DDL_STATEMENT");
  IElementType DELETE_ALL = new SqlppElementType("DELETE_ALL");
  IElementType DELETE_CLAUSE = new SqlppElementType("DELETE_CLAUSE");
  IElementType DELETE_EXPR = new SqlppElementType("DELETE_EXPR");
  IElementType DELETE_STATEMENT = new SqlppElementType("DELETE_STATEMENT");
  IElementType DML_STATEMENT = new SqlppElementType("DML_STATEMENT");
  IElementType DOUBLE_QUOTED_STRING = new SqlppElementType("DOUBLE_QUOTED_STRING");
  IElementType DOUBLE_QUOTED_STRING_CHARACTER = new SqlppElementType("DOUBLE_QUOTED_STRING_CHARACTER");
  IElementType DQL_STATEMENT = new SqlppElementType("DQL_STATEMENT");
  IElementType DROP_COLLECTION = new SqlppElementType("DROP_COLLECTION");
  IElementType DROP_FUNCTION = new SqlppElementType("DROP_FUNCTION");
  IElementType DROP_INDEX = new SqlppElementType("DROP_INDEX");
  IElementType DROP_PRIMARY_INDEX = new SqlppElementType("DROP_PRIMARY_INDEX");
  IElementType DROP_SCOPE = new SqlppElementType("DROP_SCOPE");
  IElementType DROP_STATEMENT = new SqlppElementType("DROP_STATEMENT");
  IElementType ELEMENT_EXPR = new SqlppElementType("ELEMENT_EXPR");
  IElementType EXECUTE_FUNCTION = new SqlppElementType("EXECUTE_FUNCTION");
  IElementType EXISTS_EXPR = new SqlppElementType("EXISTS_EXPR");
  IElementType EXPLAIN_STATEMENT = new SqlppElementType("EXPLAIN_STATEMENT");
  IElementType EXPR = new SqlppElementType("EXPR");
  IElementType FIELD_EXPR = new SqlppElementType("FIELD_EXPR");
  IElementType FILTER_CLAUSE = new SqlppElementType("FILTER_CLAUSE");
  IElementType FROM_CLAUSE = new SqlppElementType("FROM_CLAUSE");
  IElementType FROM_GENERIC = new SqlppElementType("FROM_GENERIC");
  IElementType FROM_KEYSPACE = new SqlppElementType("FROM_KEYSPACE");
  IElementType FROM_SELECT = new SqlppElementType("FROM_SELECT");
  IElementType FROM_SUBQUERY = new SqlppElementType("FROM_SUBQUERY");
  IElementType FROM_TERMS = new SqlppElementType("FROM_TERMS");
  IElementType FTS_HINT_JSON = new SqlppElementType("FTS_HINT_JSON");
  IElementType FTS_HINT_SIMPLE = new SqlppElementType("FTS_HINT_SIMPLE");
  IElementType FULL_ARRAY_EXPR = new SqlppElementType("FULL_ARRAY_EXPR");
  IElementType FUNCTION_CALL = new SqlppElementType("FUNCTION_CALL");
  IElementType FUNCTION_NAME = new SqlppElementType("FUNCTION_NAME");
  IElementType FUNCTION_REF = new SqlppElementType("FUNCTION_REF");
  IElementType GRANT_STATEMENT = new SqlppElementType("GRANT_STATEMENT");
  IElementType GROUP_BY_CLAUSE = new SqlppElementType("GROUP_BY_CLAUSE");
  IElementType GROUP_TERM = new SqlppElementType("GROUP_TERM");
  IElementType GSI_HINT_JSON = new SqlppElementType("GSI_HINT_JSON");
  IElementType GSI_HINT_SIMPLE = new SqlppElementType("GSI_HINT_SIMPLE");
  IElementType HASH_ARRAY = new SqlppElementType("HASH_ARRAY");
  IElementType HASH_HINT_JSON = new SqlppElementType("HASH_HINT_JSON");
  IElementType HASH_HINT_SIMPLE = new SqlppElementType("HASH_HINT_SIMPLE");
  IElementType HASH_OBJECT = new SqlppElementType("HASH_OBJECT");
  IElementType HAVING_CLAUSE = new SqlppElementType("HAVING_CLAUSE");
  IElementType HINTS = new SqlppElementType("HINTS");
  IElementType HINT_COMMENT = new SqlppElementType("HINT_COMMENT");
  IElementType IDENTIFIER_REF = new SqlppElementType("IDENTIFIER_REF");
  IElementType INCLUDE_MISSING = new SqlppElementType("INCLUDE_MISSING");
  IElementType INDEXES_CLAUSE = new SqlppElementType("INDEXES_CLAUSE");
  IElementType INDEXES_PROPERTY = new SqlppElementType("INDEXES_PROPERTY");
  IElementType INDEX_ARRAY = new SqlppElementType("INDEX_ARRAY");
  IElementType INDEX_CLAUSE = new SqlppElementType("INDEX_CLAUSE");
  IElementType INDEX_JOIN_CLAUSE = new SqlppElementType("INDEX_JOIN_CLAUSE");
  IElementType INDEX_JOIN_PREDICATE = new SqlppElementType("INDEX_JOIN_PREDICATE");
  IElementType INDEX_JOIN_RHS = new SqlppElementType("INDEX_JOIN_RHS");
  IElementType INDEX_JOIN_TYPE = new SqlppElementType("INDEX_JOIN_TYPE");
  IElementType INDEX_KEY = new SqlppElementType("INDEX_KEY");
  IElementType INDEX_KEY_OBJECT = new SqlppElementType("INDEX_KEY_OBJECT");
  IElementType INDEX_NAME = new SqlppElementType("INDEX_NAME");
  IElementType INDEX_NEST_CLAUSE = new SqlppElementType("INDEX_NEST_CLAUSE");
  IElementType INDEX_NEST_PREDICATE = new SqlppElementType("INDEX_NEST_PREDICATE");
  IElementType INDEX_NEST_RHS = new SqlppElementType("INDEX_NEST_RHS");
  IElementType INDEX_NEST_TYPE = new SqlppElementType("INDEX_NEST_TYPE");
  IElementType INDEX_OBJECT = new SqlppElementType("INDEX_OBJECT");
  IElementType INDEX_ORDER = new SqlppElementType("INDEX_ORDER");
  IElementType INDEX_PARTITION = new SqlppElementType("INDEX_PARTITION");
  IElementType INDEX_PATH = new SqlppElementType("INDEX_PATH");
  IElementType INDEX_REF = new SqlppElementType("INDEX_REF");
  IElementType INDEX_TERM = new SqlppElementType("INDEX_TERM");
  IElementType INDEX_TYPE = new SqlppElementType("INDEX_TYPE");
  IElementType INDEX_USING = new SqlppElementType("INDEX_USING");
  IElementType INDEX_WITH = new SqlppElementType("INDEX_WITH");
  IElementType INFER_STATEMENT = new SqlppElementType("INFER_STATEMENT");
  IElementType INSERT_SELECT = new SqlppElementType("INSERT_SELECT");
  IElementType INSERT_STATEMENT = new SqlppElementType("INSERT_STATEMENT");
  IElementType INSERT_VALUES = new SqlppElementType("INSERT_VALUES");
  IElementType IN_EXPR = new SqlppElementType("IN_EXPR");
  IElementType IS_EXPR = new SqlppElementType("IS_EXPR");
  IElementType JOIN_CLAUSE = new SqlppElementType("JOIN_CLAUSE");
  IElementType JSON_HINT = new SqlppElementType("JSON_HINT");
  IElementType JSON_HINT_OBJECT = new SqlppElementType("JSON_HINT_OBJECT");
  IElementType KEYSPACE_ARRAY = new SqlppElementType("KEYSPACE_ARRAY");
  IElementType KEYSPACE_FULL = new SqlppElementType("KEYSPACE_FULL");
  IElementType KEYSPACE_OBJECT = new SqlppElementType("KEYSPACE_OBJECT");
  IElementType KEYSPACE_PARTIAL = new SqlppElementType("KEYSPACE_PARTIAL");
  IElementType KEYSPACE_PATH = new SqlppElementType("KEYSPACE_PATH");
  IElementType KEYSPACE_PREFIX = new SqlppElementType("KEYSPACE_PREFIX");
  IElementType KEYSPACE_PROPERTY = new SqlppElementType("KEYSPACE_PROPERTY");
  IElementType KEYSPACE_REF = new SqlppElementType("KEYSPACE_REF");
  IElementType KEYSPACE_STATEMENT = new SqlppElementType("KEYSPACE_STATEMENT");
  IElementType KEY_ATTRIBS = new SqlppElementType("KEY_ATTRIBS");
  IElementType KEY_EXPR = new SqlppElementType("KEY_EXPR");
  IElementType LEAD_KEY_ATTRIBS = new SqlppElementType("LEAD_KEY_ATTRIBS");
  IElementType LETTING_CLAUSE = new SqlppElementType("LETTING_CLAUSE");
  IElementType LET_CLAUSE = new SqlppElementType("LET_CLAUSE");
  IElementType LIKE_EXPR = new SqlppElementType("LIKE_EXPR");
  IElementType LIMIT_CLAUSE = new SqlppElementType("LIMIT_CLAUSE");
  IElementType LINE_HINT_COMMENT = new SqlppElementType("LINE_HINT_COMMENT");
  IElementType LITERAL = new SqlppElementType("LITERAL");
  IElementType LOGICAL_TERM = new SqlppElementType("LOGICAL_TERM");
  IElementType LOOKUP_JOIN_CLAUSE = new SqlppElementType("LOOKUP_JOIN_CLAUSE");
  IElementType LOOKUP_JOIN_PREDICATE = new SqlppElementType("LOOKUP_JOIN_PREDICATE");
  IElementType LOOKUP_JOIN_RHS = new SqlppElementType("LOOKUP_JOIN_RHS");
  IElementType LOOKUP_JOIN_TYPE = new SqlppElementType("LOOKUP_JOIN_TYPE");
  IElementType LOOKUP_MERGE = new SqlppElementType("LOOKUP_MERGE");
  IElementType LOOKUP_MERGE_ACTIONS = new SqlppElementType("LOOKUP_MERGE_ACTIONS");
  IElementType LOOKUP_MERGE_INSERT = new SqlppElementType("LOOKUP_MERGE_INSERT");
  IElementType LOOKUP_MERGE_PREDICATE = new SqlppElementType("LOOKUP_MERGE_PREDICATE");
  IElementType LOOKUP_MERGE_SOURCE = new SqlppElementType("LOOKUP_MERGE_SOURCE");
  IElementType LOOKUP_NEST_CLAUSE = new SqlppElementType("LOOKUP_NEST_CLAUSE");
  IElementType LOOKUP_NEST_PREDICATE = new SqlppElementType("LOOKUP_NEST_PREDICATE");
  IElementType LOOKUP_NEST_RHS = new SqlppElementType("LOOKUP_NEST_RHS");
  IElementType LOOKUP_NEST_TYPE = new SqlppElementType("LOOKUP_NEST_TYPE");
  IElementType MERGE_DELETE = new SqlppElementType("MERGE_DELETE");
  IElementType MERGE_SOURCE_EXPR = new SqlppElementType("MERGE_SOURCE_EXPR");
  IElementType MERGE_SOURCE_KEYSPACE = new SqlppElementType("MERGE_SOURCE_KEYSPACE");
  IElementType MERGE_SOURCE_SUBQUERY = new SqlppElementType("MERGE_SOURCE_SUBQUERY");
  IElementType MERGE_STATEMENT = new SqlppElementType("MERGE_STATEMENT");
  IElementType MERGE_UPDATE = new SqlppElementType("MERGE_UPDATE");
  IElementType MULTIPLE_HINTS = new SqlppElementType("MULTIPLE_HINTS");
  IElementType NAME = new SqlppElementType("NAME");
  IElementType NAMESPACE_REF = new SqlppElementType("NAMESPACE_REF");
  IElementType NAME_VAR = new SqlppElementType("NAME_VAR");
  IElementType NESTED_EXPR = new SqlppElementType("NESTED_EXPR");
  IElementType NEST_CLAUSE = new SqlppElementType("NEST_CLAUSE");
  IElementType NL_HINT_JSON = new SqlppElementType("NL_HINT_JSON");
  IElementType NL_HINT_SIMPLE = new SqlppElementType("NL_HINT_SIMPLE");
  IElementType NOT_EXPR = new SqlppElementType("NOT_EXPR");
  IElementType NTHVAL_FROM = new SqlppElementType("NTHVAL_FROM");
  IElementType NULLS_TREATMENT = new SqlppElementType("NULLS_TREATMENT");
  IElementType OBJ = new SqlppElementType("OBJ");
  IElementType OFFSET_CLAUSE = new SqlppElementType("OFFSET_CLAUSE");
  IElementType OPTION_PROPERTY = new SqlppElementType("OPTION_PROPERTY");
  IElementType ORDERED_HINT_JSON = new SqlppElementType("ORDERED_HINT_JSON");
  IElementType ORDERED_HINT_SIMPLE = new SqlppElementType("ORDERED_HINT_SIMPLE");
  IElementType ORDERING_TERM = new SqlppElementType("ORDERING_TERM");
  IElementType ORDER_BY_CLAUSE = new SqlppElementType("ORDER_BY_CLAUSE");
  IElementType ORDINARY_FUNCTION = new SqlppElementType("ORDINARY_FUNCTION");
  IElementType OR_EXPR = new SqlppElementType("OR_EXPR");
  IElementType OTHER_HINT_TERMS = new SqlppElementType("OTHER_HINT_TERMS");
  IElementType OTHER_STATEMENT = new SqlppElementType("OTHER_STATEMENT");
  IElementType OVER_CLAUSE = new SqlppElementType("OVER_CLAUSE");
  IElementType PAIRS_FUNCTION = new SqlppElementType("PAIRS_FUNCTION");
  IElementType PARAMETERS = new SqlppElementType("PARAMETERS");
  IElementType PARAMS = new SqlppElementType("PARAMS");
  IElementType PARTITION_KEY_EXPR = new SqlppElementType("PARTITION_KEY_EXPR");
  IElementType PATH = new SqlppElementType("PATH");
  IElementType PROJECTION = new SqlppElementType("PROJECTION");
  IElementType RANGE_COND = new SqlppElementType("RANGE_COND");
  IElementType RANGE_EXPR = new SqlppElementType("RANGE_EXPR");
  IElementType RELATIONAL_EXPR = new SqlppElementType("RELATIONAL_EXPR");
  IElementType RESULT_EXPR = new SqlppElementType("RESULT_EXPR");
  IElementType RETURNING_CLAUSE = new SqlppElementType("RETURNING_CLAUSE");
  IElementType REVOKE_STATEMENT = new SqlppElementType("REVOKE_STATEMENT");
  IElementType RHS_GENERIC = new SqlppElementType("RHS_GENERIC");
  IElementType RHS_KEYSPACE = new SqlppElementType("RHS_KEYSPACE");
  IElementType RHS_SUBQUERY = new SqlppElementType("RHS_SUBQUERY");
  IElementType ROLE = new SqlppElementType("ROLE");
  IElementType ROLLBACK_TRANSACTION = new SqlppElementType("ROLLBACK_TRANSACTION");
  IElementType SAVEPOINTNAME = new SqlppElementType("SAVEPOINTNAME");
  IElementType SAVEPOINT_STATEMENT = new SqlppElementType("SAVEPOINT_STATEMENT");
  IElementType SCOPE_REF = new SqlppElementType("SCOPE_REF");
  IElementType SEARCHED_CASE_EXPR = new SqlppElementType("SEARCHED_CASE_EXPR");
  IElementType SELECT_CLAUSE = new SqlppElementType("SELECT_CLAUSE");
  IElementType SELECT_FROM = new SqlppElementType("SELECT_FROM");
  IElementType SELECT_STATEMENT = new SqlppElementType("SELECT_STATEMENT");
  IElementType SELECT_TERM = new SqlppElementType("SELECT_TERM");
  IElementType SET_CLAUSE = new SqlppElementType("SET_CLAUSE");
  IElementType SET_OP = new SqlppElementType("SET_OP");
  IElementType SET_TRANSACTION = new SqlppElementType("SET_TRANSACTION");
  IElementType SIMPLE_ARRAY_EXPR = new SqlppElementType("SIMPLE_ARRAY_EXPR");
  IElementType SIMPLE_CASE_EXPR = new SqlppElementType("SIMPLE_CASE_EXPR");
  IElementType SIMPLE_HINT = new SqlppElementType("SIMPLE_HINT");
  IElementType SIMPLE_HINT_SEQUENCE = new SqlppElementType("SIMPLE_HINT_SEQUENCE");
  IElementType SINGLE_QUOTED_STRING = new SqlppElementType("SINGLE_QUOTED_STRING");
  IElementType SINGLE_QUOTED_STRING_CHARACTER = new SqlppElementType("SINGLE_QUOTED_STRING_CHARACTER");
  IElementType SLICE_EXPR = new SqlppElementType("SLICE_EXPR");
  IElementType STATEMENT = new SqlppElementType("STATEMENT");
  IElementType STR = new SqlppElementType("STR");
  IElementType SUBQUERY_EXPR = new SqlppElementType("SUBQUERY_EXPR");
  IElementType SUBSELECT = new SqlppElementType("SUBSELECT");
  IElementType TARGET_KEYSPACE = new SqlppElementType("TARGET_KEYSPACE");
  IElementType TCL_STATEMENT = new SqlppElementType("TCL_STATEMENT");
  IElementType UNNEST_CLAUSE = new SqlppElementType("UNNEST_CLAUSE");
  IElementType UNNEST_TYPE = new SqlppElementType("UNNEST_TYPE");
  IElementType UNSET_CLAUSE = new SqlppElementType("UNSET_CLAUSE");
  IElementType UPDATE_FOR = new SqlppElementType("UPDATE_FOR");
  IElementType UPDATE_STATEMENT = new SqlppElementType("UPDATE_STATEMENT");
  IElementType UPDATE_STATISTICS = new SqlppElementType("UPDATE_STATISTICS");
  IElementType UPDATE_STATISTICS_DELETE = new SqlppElementType("UPDATE_STATISTICS_DELETE");
  IElementType UPDATE_STATISTICS_EXPR = new SqlppElementType("UPDATE_STATISTICS_EXPR");
  IElementType UPDATE_STATISTICS_INDEX = new SqlppElementType("UPDATE_STATISTICS_INDEX");
  IElementType UPDATE_STATISTICS_INDEXES = new SqlppElementType("UPDATE_STATISTICS_INDEXES");
  IElementType UPSERT_STATEMENT = new SqlppElementType("UPSERT_STATEMENT");
  IElementType USER = new SqlppElementType("USER");
  IElementType USE_CLAUSE = new SqlppElementType("USE_CLAUSE");
  IElementType USE_HASH_HINT = new SqlppElementType("USE_HASH_HINT");
  IElementType USE_HASH_TERM = new SqlppElementType("USE_HASH_TERM");
  IElementType USE_INDEX_CLAUSE = new SqlppElementType("USE_INDEX_CLAUSE");
  IElementType USE_INDEX_TERM = new SqlppElementType("USE_INDEX_TERM");
  IElementType USE_KEYS = new SqlppElementType("USE_KEYS");
  IElementType USE_KEYS_CLAUSE = new SqlppElementType("USE_KEYS_CLAUSE");
  IElementType USE_KEYS_TERM = new SqlppElementType("USE_KEYS_TERM");
  IElementType USE_NL_HINT = new SqlppElementType("USE_NL_HINT");
  IElementType USE_NL_TERM = new SqlppElementType("USE_NL_TERM");
  IElementType UTILITY_STATEMENT = new SqlppElementType("UTILITY_STATEMENT");
  IElementType VALUES_CLAUSE = new SqlppElementType("VALUES_CLAUSE");
  IElementType VALUE_EXPR = new SqlppElementType("VALUE_EXPR");
  IElementType VAR = new SqlppElementType("VAR");
  IElementType WHERE_CLAUSE = new SqlppElementType("WHERE_CLAUSE");
  IElementType WINDOW_CLAUSE = new SqlppElementType("WINDOW_CLAUSE");
  IElementType WINDOW_DECLARATION = new SqlppElementType("WINDOW_DECLARATION");
  IElementType WINDOW_DEFINITION = new SqlppElementType("WINDOW_DEFINITION");
  IElementType WINDOW_FRAME_CLAUSE = new SqlppElementType("WINDOW_FRAME_CLAUSE");
  IElementType WINDOW_FRAME_EXCLUSION = new SqlppElementType("WINDOW_FRAME_EXCLUSION");
  IElementType WINDOW_FRAME_EXTENT = new SqlppElementType("WINDOW_FRAME_EXTENT");
  IElementType WINDOW_FUNCTION = new SqlppElementType("WINDOW_FUNCTION");
  IElementType WINDOW_FUNCTION_ARGUMENTS = new SqlppElementType("WINDOW_FUNCTION_ARGUMENTS");
  IElementType WINDOW_FUNCTION_NAME = new SqlppElementType("WINDOW_FUNCTION_NAME");
  IElementType WINDOW_FUNCTION_OPTIONS = new SqlppElementType("WINDOW_FUNCTION_OPTIONS");
  IElementType WINDOW_NAME = new SqlppElementType("WINDOW_NAME");
  IElementType WINDOW_ORDER_CLAUSE = new SqlppElementType("WINDOW_ORDER_CLAUSE");
  IElementType WINDOW_PARTITION_CLAUSE = new SqlppElementType("WINDOW_PARTITION_CLAUSE");
  IElementType WINDOW_REF = new SqlppElementType("WINDOW_REF");
  IElementType WITHIN_EXPR = new SqlppElementType("WITHIN_EXPR");
  IElementType WITH_CLAUSE = new SqlppElementType("WITH_CLAUSE");

  IElementType ADVISE = new SqlppTokenType("ADVISE");
  IElementType ALL = new SqlppTokenType("ALL");
  IElementType ALTER = new SqlppTokenType("ALTER");
  IElementType ANALYZE = new SqlppTokenType("ANALYZE");
  IElementType AND = new SqlppTokenType("AND");
  IElementType ANY = new SqlppTokenType("ANY");
  IElementType ARRAY = new SqlppTokenType("ARRAY");
  IElementType AS = new SqlppTokenType("AS");
  IElementType ASC = new SqlppTokenType("ASC");
  IElementType ASTERISK = new SqlppTokenType("ASTERISK");
  IElementType AT = new SqlppTokenType("AT");
  IElementType BEGIN = new SqlppTokenType("BEGIN");
  IElementType BETWEEN = new SqlppTokenType("BETWEEN");
  IElementType BLOCK_COMMENT_OPEN = new SqlppTokenType("BLOCK_COMMENT_OPEN");
  IElementType BLOCK_HINT_OPEN = new SqlppTokenType("BLOCK_HINT_OPEN");
  IElementType BUILD = new SqlppTokenType("BUILD");
  IElementType BY = new SqlppTokenType("BY");
  IElementType CASE = new SqlppTokenType("CASE");
  IElementType COLLECTION = new SqlppTokenType("COLLECTION");
  IElementType COLON = new SqlppTokenType("COLON");
  IElementType COMMA = new SqlppTokenType("COMMA");
  IElementType COMMENT_CLOSE = new SqlppTokenType("COMMENT_CLOSE");
  IElementType COMMIT = new SqlppTokenType("COMMIT");
  IElementType COMMITTED = new SqlppTokenType("COMMITTED");
  IElementType CREATE = new SqlppTokenType("CREATE");
  IElementType CURRENT = new SqlppTokenType("CURRENT");
  IElementType DBLPIPE = new SqlppTokenType("DBLPIPE");
  IElementType DELETE = new SqlppTokenType("DELETE");
  IElementType DESC = new SqlppTokenType("DESC");
  IElementType DISTINCT = new SqlppTokenType("DISTINCT");
  IElementType DOT = new SqlppTokenType("DOT");
  IElementType DOUBLE_EQUAL = new SqlppTokenType("DOUBLE_EQUAL");
  IElementType DQUOTE = new SqlppTokenType("DQUOTE");
  IElementType DROP = new SqlppTokenType("DROP");
  IElementType ELEMENT = new SqlppTokenType("ELEMENT");
  IElementType ELLIPSIS = new SqlppTokenType("ELLIPSIS");
  IElementType ELSE = new SqlppTokenType("ELSE");
  IElementType END = new SqlppTokenType("END");
  IElementType EQUAL = new SqlppTokenType("EQUAL");
  IElementType ESCAPED_DQUOTE = new SqlppTokenType("ESCAPED_DQUOTE");
  IElementType ESCAPED_IDENTIFIER = new SqlppTokenType("ESCAPED_IDENTIFIER");
  IElementType ESCAPED_QUOTE = new SqlppTokenType("ESCAPED_QUOTE");
  IElementType ESCAPE_SEQUENCE = new SqlppTokenType("ESCAPE_SEQUENCE");
  IElementType EVERY = new SqlppTokenType("EVERY");
  IElementType EXCEPT = new SqlppTokenType("EXCEPT");
  IElementType EXCLUDE = new SqlppTokenType("EXCLUDE");
  IElementType EXECUTE = new SqlppTokenType("EXECUTE");
  IElementType EXISTS = new SqlppTokenType("EXISTS");
  IElementType EXPLAIN = new SqlppTokenType("EXPLAIN");
  IElementType FALSE = new SqlppTokenType("FALSE");
  IElementType FILTER = new SqlppTokenType("FILTER");
  IElementType FIRST = new SqlppTokenType("FIRST");
  IElementType FLATTEN = new SqlppTokenType("FLATTEN");
  IElementType FOLLOWING = new SqlppTokenType("FOLLOWING");
  IElementType FOR = new SqlppTokenType("FOR");
  IElementType FROM = new SqlppTokenType("FROM");
  IElementType FTS = new SqlppTokenType("FTS");
  IElementType FUNCS = new SqlppTokenType("FUNCS");
  IElementType FUNCTION = new SqlppTokenType("FUNCTION");
  IElementType GRANT = new SqlppTokenType("GRANT");
  IElementType GROUP = new SqlppTokenType("GROUP");
  IElementType GROUPS = new SqlppTokenType("GROUPS");
  IElementType GSI = new SqlppTokenType("GSI");
  IElementType HASH = new SqlppTokenType("HASH");
  IElementType HAVING = new SqlppTokenType("HAVING");
  IElementType IDENTIFIER = new SqlppTokenType("IDENTIFIER");
  IElementType IF = new SqlppTokenType("IF");
  IElementType IGNORE = new SqlppTokenType("IGNORE");
  IElementType IN = new SqlppTokenType("IN");
  IElementType INCLUDE = new SqlppTokenType("INCLUDE");
  IElementType INDEX = new SqlppTokenType("index");
  IElementType INDEX_FTS = new SqlppTokenType("INDEX_FTS");
  IElementType INFER = new SqlppTokenType("INFER");
  IElementType INLINE = new SqlppTokenType("INLINE");
  IElementType INNER = new SqlppTokenType("INNER");
  IElementType INSERT = new SqlppTokenType("INSERT");
  IElementType INTERSECT = new SqlppTokenType("INTERSECT");
  IElementType INTO = new SqlppTokenType("INTO");
  IElementType IS = new SqlppTokenType("IS");
  IElementType ISOLATION = new SqlppTokenType("ISOLATION");
  IElementType JAVASCRIPT = new SqlppTokenType("JAVASCRIPT");
  IElementType JOIN = new SqlppTokenType("JOIN");
  IElementType KEY = new SqlppTokenType("KEY");
  IElementType KEYS = new SqlppTokenType("KEYS");
  IElementType KEYSPACE = new SqlppTokenType("KEYSPACE");
  IElementType LANGUAGE = new SqlppTokenType("LANGUAGE");
  IElementType LAST = new SqlppTokenType("LAST");
  IElementType LBRACE = new SqlppTokenType("LBRACE");
  IElementType LBRACKET = new SqlppTokenType("LBRACKET");
  IElementType LEFT = new SqlppTokenType("LEFT");
  IElementType LESSTHAN = new SqlppTokenType("LESSTHAN");
  IElementType LESSTHAN_OR_EQUAL = new SqlppTokenType("LESSTHAN_OR_EQUAL");
  IElementType LESSTHAN_OR_MORETHAN = new SqlppTokenType("LESSTHAN_OR_MORETHAN");
  IElementType LET = new SqlppTokenType("LET");
  IElementType LETTING = new SqlppTokenType("LETTING");
  IElementType LEVEL = new SqlppTokenType("LEVEL");
  IElementType LF = new SqlppTokenType("LF");
  IElementType LIKE = new SqlppTokenType("LIKE");
  IElementType LIMIT = new SqlppTokenType("LIMIT");
  IElementType LINE_HINT_OPEN = new SqlppTokenType("LINE_HINT_OPEN");
  IElementType LPAREN = new SqlppTokenType("LPAREN");
  IElementType MATCHED = new SqlppTokenType("MATCHED");
  IElementType MERGE = new SqlppTokenType("MERGE");
  IElementType MINUS_SIGN = new SqlppTokenType("MINUS_SIGN");
  IElementType MISSING = new SqlppTokenType("MISSING");
  IElementType MORETHAN = new SqlppTokenType("MORETHAN");
  IElementType MORETHAN_OR_EQUAL = new SqlppTokenType("MORETHAN_OR_EQUAL");
  IElementType NBR = new SqlppTokenType("nbr");
  IElementType NEST = new SqlppTokenType("NEST");
  IElementType NL = new SqlppTokenType("NL");
  IElementType NO = new SqlppTokenType("NO");
  IElementType NOT = new SqlppTokenType("NOT");
  IElementType NOT_EQUAL = new SqlppTokenType("NOT_EQUAL");
  IElementType NULL = new SqlppTokenType("NULL");
  IElementType NULLS = new SqlppTokenType("NULLS");
  IElementType OFFSET = new SqlppTokenType("OFFSET");
  IElementType ON = new SqlppTokenType("ON");
  IElementType OPTIONS = new SqlppTokenType("OPTIONS");
  IElementType OR = new SqlppTokenType("OR");
  IElementType ORDER = new SqlppTokenType("ORDER");
  IElementType ORDERED = new SqlppTokenType("ORDERED");
  IElementType OTHERS = new SqlppTokenType("OTHERS");
  IElementType OUTER = new SqlppTokenType("OUTER");
  IElementType OVER = new SqlppTokenType("OVER");
  IElementType PAIRS = new SqlppTokenType("PAIRS");
  IElementType PARTITION = new SqlppTokenType("PARTITION");
  IElementType PERCENT = new SqlppTokenType("PERCENT");
  IElementType PLUS = new SqlppTokenType("PLUS");
  IElementType PRECEDING = new SqlppTokenType("PRECEDING");
  IElementType PRIMARY = new SqlppTokenType("PRIMARY");
  IElementType PROBE = new SqlppTokenType("PROBE");
  IElementType QUOTE = new SqlppTokenType("QUOTE");
  IElementType QUOTED_ALIAS = new SqlppTokenType("QUOTED_ALIAS");
  IElementType QUOTED_BUILD = new SqlppTokenType("QUOTED_BUILD");
  IElementType QUOTED_INDEX = new SqlppTokenType("QUOTED_INDEX");
  IElementType QUOTED_INDEXES = new SqlppTokenType("QUOTED_INDEXES");
  IElementType QUOTED_INDEX_FTS = new SqlppTokenType("QUOTED_INDEX_FTS");
  IElementType QUOTED_KEYSPACE = new SqlppTokenType("QUOTED_KEYSPACE");
  IElementType QUOTED_OPTION = new SqlppTokenType("QUOTED_OPTION");
  IElementType QUOTED_ORDERED = new SqlppTokenType("QUOTED_ORDERED");
  IElementType QUOTED_PROBE = new SqlppTokenType("QUOTED_PROBE");
  IElementType QUOTED_USE_HASH = new SqlppTokenType("QUOTED_USE_HASH");
  IElementType QUOTED_USE_NS = new SqlppTokenType("QUOTED_USE_NS");
  IElementType RANGE = new SqlppTokenType("RANGE");
  IElementType RAW = new SqlppTokenType("RAW");
  IElementType RBRACE = new SqlppTokenType("RBRACE");
  IElementType RBRACKET = new SqlppTokenType("RBRACKET");
  IElementType READ = new SqlppTokenType("READ");
  IElementType REPLACE = new SqlppTokenType("REPLACE");
  IElementType RESPECT = new SqlppTokenType("RESPECT");
  IElementType RETURNING = new SqlppTokenType("RETURNING");
  IElementType REVOKE = new SqlppTokenType("REVOKE");
  IElementType RIGHT = new SqlppTokenType("RIGHT");
  IElementType ROLLBACK = new SqlppTokenType("ROLLBACK");
  IElementType ROW = new SqlppTokenType("ROW");
  IElementType ROWS = new SqlppTokenType("ROWS");
  IElementType RPAREN = new SqlppTokenType("RPAREN");
  IElementType SATISFIES = new SqlppTokenType("SATISFIES");
  IElementType SAVEPOINT = new SqlppTokenType("SAVEPOINT");
  IElementType SCOPE = new SqlppTokenType("SCOPE");
  IElementType SELECT = new SqlppTokenType("SELECT");
  IElementType SELF = new SqlppTokenType("SELF");
  IElementType SEMICOLON = new SqlppTokenType("SEMICOLON");
  IElementType SET = new SqlppTokenType("SET");
  IElementType SLASH = new SqlppTokenType("SLASH");
  IElementType SOME = new SqlppTokenType("SOME");
  IElementType START = new SqlppTokenType("START");
  IElementType STATISTICS = new SqlppTokenType("STATISTICS");
  IElementType STRING_CHAR = new SqlppTokenType("STRING_CHAR");
  IElementType THEN = new SqlppTokenType("THEN");
  IElementType TIES = new SqlppTokenType("TIES");
  IElementType TO = new SqlppTokenType("TO");
  IElementType TRAN = new SqlppTokenType("TRAN");
  IElementType TRANSACTION = new SqlppTokenType("TRANSACTION");
  IElementType TRUE = new SqlppTokenType("TRUE");
  IElementType UNBOUNDED = new SqlppTokenType("UNBOUNDED");
  IElementType UNION = new SqlppTokenType("UNION");
  IElementType UNNEST = new SqlppTokenType("UNNEST");
  IElementType UNSET = new SqlppTokenType("UNSET");
  IElementType UPDATE = new SqlppTokenType("UPDATE");
  IElementType UPSERT = new SqlppTokenType("UPSERT");
  IElementType USE = new SqlppTokenType("USE");
  IElementType USE_HASH = new SqlppTokenType("USE_HASH");
  IElementType USE_NL = new SqlppTokenType("USE_NL");
  IElementType USING = new SqlppTokenType("USING");
  IElementType VALUE = new SqlppTokenType("VALUE");
  IElementType VALUED = new SqlppTokenType("VALUED");
  IElementType VALUES = new SqlppTokenType("VALUES");
  IElementType WHEN = new SqlppTokenType("WHEN");
  IElementType WHERE = new SqlppTokenType("WHERE");
  IElementType WINDOW = new SqlppTokenType("WINDOW");
  IElementType WITH = new SqlppTokenType("WITH");
  IElementType WITHIN = new SqlppTokenType("WITHIN");
  IElementType WORK = new SqlppTokenType("WORK");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == ADVISE_STATEMENT) {
        return new AdviseStatementImpl(node);
      }
      else if (type == AGGREGATE_FUNCTION) {
        return new AggregateFunctionImpl(node);
      }
      else if (type == AGGREGATE_FUNCTION_NAME) {
        return new AggregateFunctionNameImpl(node);
      }
      else if (type == AGGREGATE_QUANTIFIER) {
        return new AggregateQuantifierImpl(node);
      }
      else if (type == ALIAS) {
        return new AliasImpl(node);
      }
      else if (type == ALTER_INDEX) {
        return new AlterIndexImpl(node);
      }
      else if (type == AND_EXPR) {
        return new AndExprImpl(node);
      }
      else if (type == ANSI_HINT_TERMS) {
        return new AnsiHintTermsImpl(node);
      }
      else if (type == ANSI_JOIN_CLAUSE) {
        return new AnsiJoinClauseImpl(node);
      }
      else if (type == ANSI_JOIN_HINTS) {
        return new AnsiJoinHintsImpl(node);
      }
      else if (type == ANSI_JOIN_PREDICATE) {
        return new AnsiJoinPredicateImpl(node);
      }
      else if (type == ANSI_JOIN_RHS) {
        return new AnsiJoinRhsImpl(node);
      }
      else if (type == ANSI_JOIN_TYPE) {
        return new AnsiJoinTypeImpl(node);
      }
      else if (type == ANSI_MERGE) {
        return new AnsiMergeImpl(node);
      }
      else if (type == ANSI_MERGE_ACTIONS) {
        return new AnsiMergeActionsImpl(node);
      }
      else if (type == ANSI_MERGE_INSERT) {
        return new AnsiMergeInsertImpl(node);
      }
      else if (type == ANSI_MERGE_PREDICATE) {
        return new AnsiMergePredicateImpl(node);
      }
      else if (type == ANSI_MERGE_SOURCE) {
        return new AnsiMergeSourceImpl(node);
      }
      else if (type == ANSI_NEST_CLAUSE) {
        return new AnsiNestClauseImpl(node);
      }
      else if (type == ANSI_NEST_PREDICATE) {
        return new AnsiNestPredicateImpl(node);
      }
      else if (type == ANSI_NEST_RHS) {
        return new AnsiNestRhsImpl(node);
      }
      else if (type == ANSI_NEST_TYPE) {
        return new AnsiNestTypeImpl(node);
      }
      else if (type == ARITHMETIC_TERM) {
        return new ArithmeticTermImpl(node);
      }
      else if (type == ARRAY_EXPR) {
        return new ArrayExprImpl(node);
      }
      else if (type == BEGIN_TRANSACTION) {
        return new BeginTransactionImpl(node);
      }
      else if (type == BETWEEN_EXPR) {
        return new BetweenExprImpl(node);
      }
      else if (type == BLOCK_COMMENT) {
        return new BlockCommentImpl(node);
      }
      else if (type == BLOCK_HINT_COMMENT) {
        return new BlockHintCommentImpl(node);
      }
      else if (type == BODY) {
        return new BodyImpl(node);
      }
      else if (type == BOOL) {
        return new BoolImpl(node);
      }
      else if (type == BUCKET_REF) {
        return new BucketRefImpl(node);
      }
      else if (type == BUILD_INDEX) {
        return new BuildIndexImpl(node);
      }
      else if (type == BUILTIN_FUNCTION) {
        return new BuiltinFunctionImpl(node);
      }
      else if (type == CASE_EXPR) {
        return new CaseExprImpl(node);
      }
      else if (type == COLLECTION_EXPR) {
        return new CollectionExprImpl(node);
      }
      else if (type == COLLECTION_REF) {
        return new CollectionRefImpl(node);
      }
      else if (type == COMMA_SEPARATED_JOIN) {
        return new CommaSeparatedJoinImpl(node);
      }
      else if (type == COMMENT) {
        return new CommentImpl(node);
      }
      else if (type == COMMIT_TRANSACTION) {
        return new CommitTransactionImpl(node);
      }
      else if (type == COMPARISON_TERM) {
        return new ComparisonTermImpl(node);
      }
      else if (type == CONCATENATION_TERM) {
        return new ConcatenationTermImpl(node);
      }
      else if (type == COND) {
        return new CondImpl(node);
      }
      else if (type == CREATE_COLLECTION) {
        return new CreateCollectionImpl(node);
      }
      else if (type == CREATE_FUNCTION) {
        return new CreateFunctionImpl(node);
      }
      else if (type == CREATE_FUNCTION_EXTERNAL) {
        return new CreateFunctionExternalImpl(node);
      }
      else if (type == CREATE_FUNCTION_INLINE) {
        return new CreateFunctionInlineImpl(node);
      }
      else if (type == CREATE_INDEX) {
        return new CreateIndexImpl(node);
      }
      else if (type == CREATE_PRIMARY_INDEX) {
        return new CreatePrimaryIndexImpl(node);
      }
      else if (type == CREATE_SCOPE) {
        return new CreateScopeImpl(node);
      }
      else if (type == CREATE_STATEMENT) {
        return new CreateStatementImpl(node);
      }
      else if (type == DCL_STATEMENT) {
        return new DclStatementImpl(node);
      }
      else if (type == DDL_STATEMENT) {
        return new DdlStatementImpl(node);
      }
      else if (type == DELETE_ALL) {
        return new DeleteAllImpl(node);
      }
      else if (type == DELETE_CLAUSE) {
        return new DeleteClauseImpl(node);
      }
      else if (type == DELETE_EXPR) {
        return new DeleteExprImpl(node);
      }
      else if (type == DELETE_STATEMENT) {
        return new DeleteStatementImpl(node);
      }
      else if (type == DML_STATEMENT) {
        return new DmlStatementImpl(node);
      }
      else if (type == DOUBLE_QUOTED_STRING) {
        return new DoubleQuotedStringImpl(node);
      }
      else if (type == DOUBLE_QUOTED_STRING_CHARACTER) {
        return new DoubleQuotedStringCharacterImpl(node);
      }
      else if (type == DQL_STATEMENT) {
        return new DqlStatementImpl(node);
      }
      else if (type == DROP_COLLECTION) {
        return new DropCollectionImpl(node);
      }
      else if (type == DROP_FUNCTION) {
        return new DropFunctionImpl(node);
      }
      else if (type == DROP_INDEX) {
        return new DropIndexImpl(node);
      }
      else if (type == DROP_PRIMARY_INDEX) {
        return new DropPrimaryIndexImpl(node);
      }
      else if (type == DROP_SCOPE) {
        return new DropScopeImpl(node);
      }
      else if (type == DROP_STATEMENT) {
        return new DropStatementImpl(node);
      }
      else if (type == ELEMENT_EXPR) {
        return new ElementExprImpl(node);
      }
      else if (type == EXECUTE_FUNCTION) {
        return new ExecuteFunctionImpl(node);
      }
      else if (type == EXISTS_EXPR) {
        return new ExistsExprImpl(node);
      }
      else if (type == EXPLAIN_STATEMENT) {
        return new ExplainStatementImpl(node);
      }
      else if (type == EXPR) {
        return new ExprImpl(node);
      }
      else if (type == FIELD_EXPR) {
        return new FieldExprImpl(node);
      }
      else if (type == FILTER_CLAUSE) {
        return new FilterClauseImpl(node);
      }
      else if (type == FROM_CLAUSE) {
        return new FromClauseImpl(node);
      }
      else if (type == FROM_GENERIC) {
        return new FromGenericImpl(node);
      }
      else if (type == FROM_KEYSPACE) {
        return new FromKeyspaceImpl(node);
      }
      else if (type == FROM_SELECT) {
        return new FromSelectImpl(node);
      }
      else if (type == FROM_SUBQUERY) {
        return new FromSubqueryImpl(node);
      }
      else if (type == FROM_TERMS) {
        return new FromTermsImpl(node);
      }
      else if (type == FTS_HINT_JSON) {
        return new FtsHintJsonImpl(node);
      }
      else if (type == FTS_HINT_SIMPLE) {
        return new FtsHintSimpleImpl(node);
      }
      else if (type == FULL_ARRAY_EXPR) {
        return new FullArrayExprImpl(node);
      }
      else if (type == FUNCTION_CALL) {
        return new FunctionCallImpl(node);
      }
      else if (type == FUNCTION_NAME) {
        return new FunctionNameImpl(node);
      }
      else if (type == FUNCTION_REF) {
        return new FunctionRefImpl(node);
      }
      else if (type == GRANT_STATEMENT) {
        return new GrantStatementImpl(node);
      }
      else if (type == GROUP_BY_CLAUSE) {
        return new GroupByClauseImpl(node);
      }
      else if (type == GROUP_TERM) {
        return new GroupTermImpl(node);
      }
      else if (type == GSI_HINT_JSON) {
        return new GsiHintJsonImpl(node);
      }
      else if (type == GSI_HINT_SIMPLE) {
        return new GsiHintSimpleImpl(node);
      }
      else if (type == HASH_ARRAY) {
        return new HashArrayImpl(node);
      }
      else if (type == HASH_HINT_JSON) {
        return new HashHintJsonImpl(node);
      }
      else if (type == HASH_HINT_SIMPLE) {
        return new HashHintSimpleImpl(node);
      }
      else if (type == HASH_OBJECT) {
        return new HashObjectImpl(node);
      }
      else if (type == HAVING_CLAUSE) {
        return new HavingClauseImpl(node);
      }
      else if (type == HINTS) {
        return new HintsImpl(node);
      }
      else if (type == HINT_COMMENT) {
        return new HintCommentImpl(node);
      }
      else if (type == IDENTIFIER_REF) {
        return new IdentifierRefImpl(node);
      }
      else if (type == INCLUDE_MISSING) {
        return new IncludeMissingImpl(node);
      }
      else if (type == INDEXES_CLAUSE) {
        return new IndexesClauseImpl(node);
      }
      else if (type == INDEXES_PROPERTY) {
        return new IndexesPropertyImpl(node);
      }
      else if (type == INDEX_ARRAY) {
        return new IndexArrayImpl(node);
      }
      else if (type == INDEX_CLAUSE) {
        return new IndexClauseImpl(node);
      }
      else if (type == INDEX_JOIN_CLAUSE) {
        return new IndexJoinClauseImpl(node);
      }
      else if (type == INDEX_JOIN_PREDICATE) {
        return new IndexJoinPredicateImpl(node);
      }
      else if (type == INDEX_JOIN_RHS) {
        return new IndexJoinRhsImpl(node);
      }
      else if (type == INDEX_JOIN_TYPE) {
        return new IndexJoinTypeImpl(node);
      }
      else if (type == INDEX_KEY) {
        return new IndexKeyImpl(node);
      }
      else if (type == INDEX_KEY_OBJECT) {
        return new IndexKeyObjectImpl(node);
      }
      else if (type == INDEX_NAME) {
        return new IndexNameImpl(node);
      }
      else if (type == INDEX_NEST_CLAUSE) {
        return new IndexNestClauseImpl(node);
      }
      else if (type == INDEX_NEST_PREDICATE) {
        return new IndexNestPredicateImpl(node);
      }
      else if (type == INDEX_NEST_RHS) {
        return new IndexNestRhsImpl(node);
      }
      else if (type == INDEX_NEST_TYPE) {
        return new IndexNestTypeImpl(node);
      }
      else if (type == INDEX_OBJECT) {
        return new IndexObjectImpl(node);
      }
      else if (type == INDEX_ORDER) {
        return new IndexOrderImpl(node);
      }
      else if (type == INDEX_PARTITION) {
        return new IndexPartitionImpl(node);
      }
      else if (type == INDEX_PATH) {
        return new IndexPathImpl(node);
      }
      else if (type == INDEX_REF) {
        return new IndexRefImpl(node);
      }
      else if (type == INDEX_TERM) {
        return new IndexTermImpl(node);
      }
      else if (type == INDEX_TYPE) {
        return new IndexTypeImpl(node);
      }
      else if (type == INDEX_USING) {
        return new IndexUsingImpl(node);
      }
      else if (type == INDEX_WITH) {
        return new IndexWithImpl(node);
      }
      else if (type == INFER_STATEMENT) {
        return new InferStatementImpl(node);
      }
      else if (type == INSERT_SELECT) {
        return new InsertSelectImpl(node);
      }
      else if (type == INSERT_STATEMENT) {
        return new InsertStatementImpl(node);
      }
      else if (type == INSERT_VALUES) {
        return new InsertValuesImpl(node);
      }
      else if (type == IN_EXPR) {
        return new InExprImpl(node);
      }
      else if (type == IS_EXPR) {
        return new IsExprImpl(node);
      }
      else if (type == JOIN_CLAUSE) {
        return new JoinClauseImpl(node);
      }
      else if (type == JSON_HINT) {
        return new JsonHintImpl(node);
      }
      else if (type == JSON_HINT_OBJECT) {
        return new JsonHintObjectImpl(node);
      }
      else if (type == KEYSPACE_ARRAY) {
        return new KeyspaceArrayImpl(node);
      }
      else if (type == KEYSPACE_FULL) {
        return new KeyspaceFullImpl(node);
      }
      else if (type == KEYSPACE_OBJECT) {
        return new KeyspaceObjectImpl(node);
      }
      else if (type == KEYSPACE_PARTIAL) {
        return new KeyspacePartialImpl(node);
      }
      else if (type == KEYSPACE_PATH) {
        return new KeyspacePathImpl(node);
      }
      else if (type == KEYSPACE_PREFIX) {
        return new KeyspacePrefixImpl(node);
      }
      else if (type == KEYSPACE_PROPERTY) {
        return new KeyspacePropertyImpl(node);
      }
      else if (type == KEYSPACE_REF) {
        return new KeyspaceRefImpl(node);
      }
      else if (type == KEYSPACE_STATEMENT) {
        return new KeyspaceStatementImpl(node);
      }
      else if (type == KEY_ATTRIBS) {
        return new KeyAttribsImpl(node);
      }
      else if (type == KEY_EXPR) {
        return new KeyExprImpl(node);
      }
      else if (type == LEAD_KEY_ATTRIBS) {
        return new LeadKeyAttribsImpl(node);
      }
      else if (type == LETTING_CLAUSE) {
        return new LettingClauseImpl(node);
      }
      else if (type == LET_CLAUSE) {
        return new LetClauseImpl(node);
      }
      else if (type == LIKE_EXPR) {
        return new LikeExprImpl(node);
      }
      else if (type == LIMIT_CLAUSE) {
        return new LimitClauseImpl(node);
      }
      else if (type == LINE_HINT_COMMENT) {
        return new LineHintCommentImpl(node);
      }
      else if (type == LITERAL) {
        return new LiteralImpl(node);
      }
      else if (type == LOGICAL_TERM) {
        return new LogicalTermImpl(node);
      }
      else if (type == LOOKUP_JOIN_CLAUSE) {
        return new LookupJoinClauseImpl(node);
      }
      else if (type == LOOKUP_JOIN_PREDICATE) {
        return new LookupJoinPredicateImpl(node);
      }
      else if (type == LOOKUP_JOIN_RHS) {
        return new LookupJoinRhsImpl(node);
      }
      else if (type == LOOKUP_JOIN_TYPE) {
        return new LookupJoinTypeImpl(node);
      }
      else if (type == LOOKUP_MERGE) {
        return new LookupMergeImpl(node);
      }
      else if (type == LOOKUP_MERGE_ACTIONS) {
        return new LookupMergeActionsImpl(node);
      }
      else if (type == LOOKUP_MERGE_INSERT) {
        return new LookupMergeInsertImpl(node);
      }
      else if (type == LOOKUP_MERGE_PREDICATE) {
        return new LookupMergePredicateImpl(node);
      }
      else if (type == LOOKUP_MERGE_SOURCE) {
        return new LookupMergeSourceImpl(node);
      }
      else if (type == LOOKUP_NEST_CLAUSE) {
        return new LookupNestClauseImpl(node);
      }
      else if (type == LOOKUP_NEST_PREDICATE) {
        return new LookupNestPredicateImpl(node);
      }
      else if (type == LOOKUP_NEST_RHS) {
        return new LookupNestRhsImpl(node);
      }
      else if (type == LOOKUP_NEST_TYPE) {
        return new LookupNestTypeImpl(node);
      }
      else if (type == MERGE_DELETE) {
        return new MergeDeleteImpl(node);
      }
      else if (type == MERGE_SOURCE_EXPR) {
        return new MergeSourceExprImpl(node);
      }
      else if (type == MERGE_SOURCE_KEYSPACE) {
        return new MergeSourceKeyspaceImpl(node);
      }
      else if (type == MERGE_SOURCE_SUBQUERY) {
        return new MergeSourceSubqueryImpl(node);
      }
      else if (type == MERGE_STATEMENT) {
        return new MergeStatementImpl(node);
      }
      else if (type == MERGE_UPDATE) {
        return new MergeUpdateImpl(node);
      }
      else if (type == MULTIPLE_HINTS) {
        return new MultipleHintsImpl(node);
      }
      else if (type == NAME) {
        return new NameImpl(node);
      }
      else if (type == NAMESPACE_REF) {
        return new NamespaceRefImpl(node);
      }
      else if (type == NAME_VAR) {
        return new NameVarImpl(node);
      }
      else if (type == NESTED_EXPR) {
        return new NestedExprImpl(node);
      }
      else if (type == NEST_CLAUSE) {
        return new NestClauseImpl(node);
      }
      else if (type == NL_HINT_JSON) {
        return new NlHintJsonImpl(node);
      }
      else if (type == NL_HINT_SIMPLE) {
        return new NlHintSimpleImpl(node);
      }
      else if (type == NOT_EXPR) {
        return new NotExprImpl(node);
      }
      else if (type == NTHVAL_FROM) {
        return new NthvalFromImpl(node);
      }
      else if (type == NULLS_TREATMENT) {
        return new NullsTreatmentImpl(node);
      }
      else if (type == OBJ) {
        return new ObjImpl(node);
      }
      else if (type == OFFSET_CLAUSE) {
        return new OffsetClauseImpl(node);
      }
      else if (type == OPTION_PROPERTY) {
        return new OptionPropertyImpl(node);
      }
      else if (type == ORDERED_HINT_JSON) {
        return new OrderedHintJsonImpl(node);
      }
      else if (type == ORDERED_HINT_SIMPLE) {
        return new OrderedHintSimpleImpl(node);
      }
      else if (type == ORDERING_TERM) {
        return new OrderingTermImpl(node);
      }
      else if (type == ORDER_BY_CLAUSE) {
        return new OrderByClauseImpl(node);
      }
      else if (type == ORDINARY_FUNCTION) {
        return new OrdinaryFunctionImpl(node);
      }
      else if (type == OR_EXPR) {
        return new OrExprImpl(node);
      }
      else if (type == OTHER_HINT_TERMS) {
        return new OtherHintTermsImpl(node);
      }
      else if (type == OTHER_STATEMENT) {
        return new OtherStatementImpl(node);
      }
      else if (type == OVER_CLAUSE) {
        return new OverClauseImpl(node);
      }
      else if (type == PAIRS_FUNCTION) {
        return new PairsFunctionImpl(node);
      }
      else if (type == PARAMETERS) {
        return new ParametersImpl(node);
      }
      else if (type == PARAMS) {
        return new ParamsImpl(node);
      }
      else if (type == PARTITION_KEY_EXPR) {
        return new PartitionKeyExprImpl(node);
      }
      else if (type == PATH) {
        return new PathImpl(node);
      }
      else if (type == PROJECTION) {
        return new ProjectionImpl(node);
      }
      else if (type == RANGE_COND) {
        return new RangeCondImpl(node);
      }
      else if (type == RANGE_EXPR) {
        return new RangeExprImpl(node);
      }
      else if (type == RELATIONAL_EXPR) {
        return new RelationalExprImpl(node);
      }
      else if (type == RESULT_EXPR) {
        return new ResultExprImpl(node);
      }
      else if (type == RETURNING_CLAUSE) {
        return new ReturningClauseImpl(node);
      }
      else if (type == REVOKE_STATEMENT) {
        return new RevokeStatementImpl(node);
      }
      else if (type == RHS_GENERIC) {
        return new RhsGenericImpl(node);
      }
      else if (type == RHS_KEYSPACE) {
        return new RhsKeyspaceImpl(node);
      }
      else if (type == RHS_SUBQUERY) {
        return new RhsSubqueryImpl(node);
      }
      else if (type == ROLE) {
        return new RoleImpl(node);
      }
      else if (type == ROLLBACK_TRANSACTION) {
        return new RollbackTransactionImpl(node);
      }
      else if (type == SAVEPOINTNAME) {
        return new SavepointnameImpl(node);
      }
      else if (type == SAVEPOINT_STATEMENT) {
        return new SavepointStatementImpl(node);
      }
      else if (type == SCOPE_REF) {
        return new ScopeRefImpl(node);
      }
      else if (type == SEARCHED_CASE_EXPR) {
        return new SearchedCaseExprImpl(node);
      }
      else if (type == SELECT_CLAUSE) {
        return new SelectClauseImpl(node);
      }
      else if (type == SELECT_FROM) {
        return new SelectFromImpl(node);
      }
      else if (type == SELECT_STATEMENT) {
        return new SelectStatementImpl(node);
      }
      else if (type == SELECT_TERM) {
        return new SelectTermImpl(node);
      }
      else if (type == SET_CLAUSE) {
        return new SetClauseImpl(node);
      }
      else if (type == SET_OP) {
        return new SetOpImpl(node);
      }
      else if (type == SET_TRANSACTION) {
        return new SetTransactionImpl(node);
      }
      else if (type == SIMPLE_ARRAY_EXPR) {
        return new SimpleArrayExprImpl(node);
      }
      else if (type == SIMPLE_CASE_EXPR) {
        return new SimpleCaseExprImpl(node);
      }
      else if (type == SIMPLE_HINT) {
        return new SimpleHintImpl(node);
      }
      else if (type == SIMPLE_HINT_SEQUENCE) {
        return new SimpleHintSequenceImpl(node);
      }
      else if (type == SINGLE_QUOTED_STRING) {
        return new SingleQuotedStringImpl(node);
      }
      else if (type == SINGLE_QUOTED_STRING_CHARACTER) {
        return new SingleQuotedStringCharacterImpl(node);
      }
      else if (type == SLICE_EXPR) {
        return new SliceExprImpl(node);
      }
      else if (type == STATEMENT) {
        return new StatementImpl(node);
      }
      else if (type == STR) {
        return new StrImpl(node);
      }
      else if (type == SUBQUERY_EXPR) {
        return new SubqueryExprImpl(node);
      }
      else if (type == SUBSELECT) {
        return new SubselectImpl(node);
      }
      else if (type == TARGET_KEYSPACE) {
        return new TargetKeyspaceImpl(node);
      }
      else if (type == TCL_STATEMENT) {
        return new TclStatementImpl(node);
      }
      else if (type == UNNEST_CLAUSE) {
        return new UnnestClauseImpl(node);
      }
      else if (type == UNNEST_TYPE) {
        return new UnnestTypeImpl(node);
      }
      else if (type == UNSET_CLAUSE) {
        return new UnsetClauseImpl(node);
      }
      else if (type == UPDATE_FOR) {
        return new UpdateForImpl(node);
      }
      else if (type == UPDATE_STATEMENT) {
        return new UpdateStatementImpl(node);
      }
      else if (type == UPDATE_STATISTICS) {
        return new UpdateStatisticsImpl(node);
      }
      else if (type == UPDATE_STATISTICS_DELETE) {
        return new UpdateStatisticsDeleteImpl(node);
      }
      else if (type == UPDATE_STATISTICS_EXPR) {
        return new UpdateStatisticsExprImpl(node);
      }
      else if (type == UPDATE_STATISTICS_INDEX) {
        return new UpdateStatisticsIndexImpl(node);
      }
      else if (type == UPDATE_STATISTICS_INDEXES) {
        return new UpdateStatisticsIndexesImpl(node);
      }
      else if (type == UPSERT_STATEMENT) {
        return new UpsertStatementImpl(node);
      }
      else if (type == USER) {
        return new UserImpl(node);
      }
      else if (type == USE_CLAUSE) {
        return new UseClauseImpl(node);
      }
      else if (type == USE_HASH_HINT) {
        return new UseHashHintImpl(node);
      }
      else if (type == USE_HASH_TERM) {
        return new UseHashTermImpl(node);
      }
      else if (type == USE_INDEX_CLAUSE) {
        return new UseIndexClauseImpl(node);
      }
      else if (type == USE_INDEX_TERM) {
        return new UseIndexTermImpl(node);
      }
      else if (type == USE_KEYS) {
        return new UseKeysImpl(node);
      }
      else if (type == USE_KEYS_CLAUSE) {
        return new UseKeysClauseImpl(node);
      }
      else if (type == USE_KEYS_TERM) {
        return new UseKeysTermImpl(node);
      }
      else if (type == USE_NL_HINT) {
        return new UseNlHintImpl(node);
      }
      else if (type == USE_NL_TERM) {
        return new UseNlTermImpl(node);
      }
      else if (type == UTILITY_STATEMENT) {
        return new UtilityStatementImpl(node);
      }
      else if (type == VALUES_CLAUSE) {
        return new ValuesClauseImpl(node);
      }
      else if (type == VALUE_EXPR) {
        return new ValueExprImpl(node);
      }
      else if (type == VAR) {
        return new VarImpl(node);
      }
      else if (type == WHERE_CLAUSE) {
        return new WhereClauseImpl(node);
      }
      else if (type == WINDOW_CLAUSE) {
        return new WindowClauseImpl(node);
      }
      else if (type == WINDOW_DECLARATION) {
        return new WindowDeclarationImpl(node);
      }
      else if (type == WINDOW_DEFINITION) {
        return new WindowDefinitionImpl(node);
      }
      else if (type == WINDOW_FRAME_CLAUSE) {
        return new WindowFrameClauseImpl(node);
      }
      else if (type == WINDOW_FRAME_EXCLUSION) {
        return new WindowFrameExclusionImpl(node);
      }
      else if (type == WINDOW_FRAME_EXTENT) {
        return new WindowFrameExtentImpl(node);
      }
      else if (type == WINDOW_FUNCTION) {
        return new WindowFunctionImpl(node);
      }
      else if (type == WINDOW_FUNCTION_ARGUMENTS) {
        return new WindowFunctionArgumentsImpl(node);
      }
      else if (type == WINDOW_FUNCTION_NAME) {
        return new WindowFunctionNameImpl(node);
      }
      else if (type == WINDOW_FUNCTION_OPTIONS) {
        return new WindowFunctionOptionsImpl(node);
      }
      else if (type == WINDOW_NAME) {
        return new WindowNameImpl(node);
      }
      else if (type == WINDOW_ORDER_CLAUSE) {
        return new WindowOrderClauseImpl(node);
      }
      else if (type == WINDOW_PARTITION_CLAUSE) {
        return new WindowPartitionClauseImpl(node);
      }
      else if (type == WINDOW_REF) {
        return new WindowRefImpl(node);
      }
      else if (type == WITHIN_EXPR) {
        return new WithinExprImpl(node);
      }
      else if (type == WITH_CLAUSE) {
        return new WithClauseImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}

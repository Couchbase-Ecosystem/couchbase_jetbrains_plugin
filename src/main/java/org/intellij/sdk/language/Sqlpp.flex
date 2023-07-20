package org.intellij.sdk.language;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import generated.GeneratedTypes;
import com.intellij.psi.TokenType;

%%

%class SqlppLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%ignorecase
%eof{  return;
%eof}

WHITE_SPACE=[\ \n\t\f]+
PLUS = \+
INTGR = ([0-9]|[1-9][0-9]*)
FRACTION = (\.[0-9]+)
EXPONENT = ([eE][-+]?[0-9]+)
NBR = (-?{INTGR}{FRACTION}?{EXPONENT}?)

IDT = [a-zA-Z_][0-9a-zA-Z_$]*
HEX = [0-9a-fA-F]
CHR = (\\[\\\/bfnrt]|\\u{HEX}{HEX}{HEX}{HEX})
ESCAPED_IDT = `{IDT}`
ASTERISK = \*
OPEN_BRACKET = \[
CLOSE_BRACKET = \]
COMMA = \,
DOT = \.
OPEN_PAREN = \(
CLOSE_PAREN = \)
OPEN_CURLY = \{
CLOSE_CURLY = \}
SEMICOLON = ";"
COLON = ":"
SLASH = "/"
BACKSLASH = \\
QUOTE = \'
DQUOTE = \"


Backtick = "`"

%state SingleQuotedString
%state DoubleQuotedString
%%

<YYINITIAL> {

      "ADVISE" { return GeneratedTypes.ADVISE; }
      "ALL" { return GeneratedTypes.ALL; }
      "ALTER" { return GeneratedTypes.ALTER; }
      "ANALYZE" { return GeneratedTypes.ANALYZE; }
      "AND" { return GeneratedTypes.AND; }
      "ANY" { return GeneratedTypes.ANY; }
      "ARRAY" { return GeneratedTypes.ARRAY; }
      "AS" { return GeneratedTypes.AS; }
      "ASC" { return GeneratedTypes.ASC; }
      "AT" { return GeneratedTypes.AT; }

      "BEGIN" { return GeneratedTypes.BEGIN; }
      "BETWEEN" { return GeneratedTypes.BETWEEN; }
//      "BINARY" { return GeneratedTypes.BINARY; }
//      "BOOLEAN" { return GeneratedTypes.BOOLEAN; }
//      "BREAK" { return GeneratedTypes.BREAK; }
//      "BUCKET" { return GeneratedTypes.BUCKET; }
      "BUILD" { return GeneratedTypes.BUILD; }
      "BY" { return GeneratedTypes.BY; }

//      "CASE" { RETURN GENERATEDTYPES.CASE; }
      "CASE" { return GeneratedTypes.CASE; }
//      "CLUSTER" { return GeneratedTypes.CLUSTER; }
//      "COLLATE" { return GeneratedTypes.COLLATE; }
      "COLLECTION" { return GeneratedTypes.COLLECTION; }
      "COMMIT" { return GeneratedTypes.COMMIT; }
      "COMMITTED" { return GeneratedTypes.COMMITTED; }
//      "CONNECT" { return GeneratedTypes.CONNECT; }
//      "CONTINUE" { return GeneratedTypes.CONTINUE; }
//      "CORRELATED" { return GeneratedTypes.CORRELATED; }
//      "COVER" { return GeneratedTypes.COVER; }
      "CREATE" { return GeneratedTypes.CREATE; }
      "CURRENT" { return GeneratedTypes.CURRENT; }

//      "DATABASE" { return GeneratedTypes.DATABASE; }
//      "DATASET" { return GeneratedTypes.DATASET; }
//      "DATASTORE" { return GeneratedTypes.DATASTORE; }
//      "DECLARE" { return GeneratedTypes.DECLARE; }
//      "DECREMENT" { return GeneratedTypes.DECREMENT; }
      "DELETE" { return GeneratedTypes.DELETE; }
//      "DERIVED" { return GeneratedTypes.DERIVED; }
      "DESC" { return GeneratedTypes.DESC; }
//      "DESCRIBE" { return GeneratedTypes.DESCRIBE; }
      "DISTINCT" { return GeneratedTypes.DISTINCT; }
//      "DO" { return GeneratedTypes.DO; }
      "DROP" { return GeneratedTypes.DROP; }

//      "EACH" { return GeneratedTypes.EACH; }
      "ELEMENT" { return GeneratedTypes.ELEMENT; }
      "ELSE" { return GeneratedTypes.ELSE; }
      "END" { return GeneratedTypes.END; }
//      "EVERY" { return GeneratedTypes.EVERY; }
      "EXCEPT" { return GeneratedTypes.EXCEPT; }
      "EXCLUDE" { return GeneratedTypes.EXCLUDE; }
      "EXECUTE" { return GeneratedTypes.EXECUTE; }
      "EXISTS" { return GeneratedTypes.EXISTS; }
      "EXPLAIN" { return GeneratedTypes.EXPLAIN; }

      "FALSE" { return GeneratedTypes.FALSE; }
//      "FETCH" { return GeneratedTypes.FETCH; }
      "FILTER" { return GeneratedTypes.FILTER; }
      "FIRST" { return GeneratedTypes.FIRST; }
      "FLATTEN" { return GeneratedTypes.FLATTEN; }
//      "FLUSH" { return GeneratedTypes.FLUSH; }
      "FOLLOWING" { return GeneratedTypes.FOLLOWING; }
      "FOR" { return GeneratedTypes.FOR; }
//      "FORCE" { return GeneratedTypes.FORCE; }
      "FROM" { return GeneratedTypes.FROM; }
      "FTS" { return GeneratedTypes.FTS; }
      "FUNCTION" { return GeneratedTypes.FUNCTION; }

//      "GOLANG" { return GeneratedTypes.GOLANG; }
      "GRANT" { return GeneratedTypes.GRANT; }
      "GROUP" { return GeneratedTypes.GROUP; }
      "GROUPS" { return GeneratedTypes.GROUPS; }
      "GSI" { return GeneratedTypes.GSI; }

      "HASH" { return GeneratedTypes.HASH; }
      "HAVING" { return GeneratedTypes.HAVING; }

      "IF" { return GeneratedTypes.IF; }
      "ISOLATION" { return GeneratedTypes.ISOLATION; }
      "IGNORE" { return GeneratedTypes.IGNORE; }
//      "ILIKE" { return GeneratedTypes.ILIKE; }
      "IN" { return GeneratedTypes.IN; }
      "INCLUDE" { return GeneratedTypes.INCLUDE; }
//      "INCREMENT" { return GeneratedTypes.INCREMENT; }
      "INDEX" { return GeneratedTypes.INDEX; }
      "INFER" { return GeneratedTypes.INFER; }
      "INLINE" { return GeneratedTypes.INLINE; }
      "INNER" { return GeneratedTypes.INNER; }
      "INSERT" { return GeneratedTypes.INSERT; }
      "INTERSECT" { return GeneratedTypes.INTERSECT; }
      "INTO" { return GeneratedTypes.INTO; }
      "IS" { return GeneratedTypes.IS; }

      "JAVASCRIPT" { return GeneratedTypes.JAVASCRIPT; }
      "JOIN" { return GeneratedTypes.JOIN; }

      "KEY" { return GeneratedTypes.KEY; }
      "KEYS" { return GeneratedTypes.KEYS; }
      "KEYSPACE" { return GeneratedTypes.KEYSPACE; }
//      "KNOWN" { return GeneratedTypes.KNOWN; }

      "LANGUAGE" { return GeneratedTypes.LANGUAGE; }
      "LAST" { return GeneratedTypes.LAST; }
      "LEFT" { return GeneratedTypes.LEFT; }
      "LET" { return GeneratedTypes.LET; }
      "LETTING" { return GeneratedTypes.LETTING; }
      "LEVEL" { return GeneratedTypes.LEVEL; }
      "LIKE" { return GeneratedTypes.LIKE; }
      "LIMIT" { return GeneratedTypes.LIMIT; }
//      "LSM" { return GeneratedTypes.LSM; }

//      "MAP" { return GeneratedTypes.MAP; }
//      "MAPPING" { return GeneratedTypes.MAPPING; }
      "MATCHED" { return GeneratedTypes.MATCHED; }
//      "MATERIALIZED" { return GeneratedTypes.MATERIALIZED; }
      "MERGE" { return GeneratedTypes.MERGE; }
//      "MINUS" { return GeneratedTypes.MINUS; }
      "MISSING" { return GeneratedTypes.MISSING; }

//      "NAMESPACE" { return GeneratedTypes.NAMESPACE; }
      "NEST" { return GeneratedTypes.NEST; }
      "NL" { return GeneratedTypes.NL; }
      "NO" { return GeneratedTypes.NO; }
      "NOT" { return GeneratedTypes.NOT; }
//      "NTH_VALUE" { return GeneratedTypes.NTH_VALUE; }
      "NULL" { return GeneratedTypes.NULL; }
      "NULLS" { return GeneratedTypes.NULLS; }
//      "NUMBER" { return GeneratedTypes.NUMBER; }

//      "OBJECT" { return GeneratedTypes.OBJECT; }
      "OFFSET" { return GeneratedTypes.OFFSET; }
      "ON" { return GeneratedTypes.ON; }
//      "OPTION" { return GeneratedTypes.OPTION; }
      "OPTIONS" { return GeneratedTypes.OPTIONS; }
      "OR" { return GeneratedTypes.OR; }
      "ORDER" { return GeneratedTypes.ORDER; }
      "OTHERS" { return GeneratedTypes.OTHERS; }
      "OUTER" { return GeneratedTypes.OUTER; }
      "OVER" { return GeneratedTypes.OVER; }
//      "PARSE" { return GeneratedTypes.PARSE; }
      "PARTITION" { return GeneratedTypes.PARTITION; }
//      "PASSWORD" { return GeneratedTypes.PASSWORD; }
      "PATH" { return GeneratedTypes.PATH; }
//      "POOL" { return GeneratedTypes.POOL; }
      "PRECEDING" { return GeneratedTypes.PRECEDING; }
//      "PREPARE" { return GeneratedTypes.PREPARE; }
      "PRIMARY" { return GeneratedTypes.PRIMARY; }
//      "PRIVATE" { return GeneratedTypes.PRIVATE; }
//      "PRIVILEGE" { return GeneratedTypes.PRIVILEGE; }
      "PROBE" { return GeneratedTypes.PROBE; }
//      "PROCEDURE" { return GeneratedTypes.PROCEDURE; }
//      "PUBLIC" { return GeneratedTypes.PUBLIC; }

      "RANGE" { return GeneratedTypes.RANGE; }
      "RAW" { return GeneratedTypes.RAW; }
//      "REALM" { return GeneratedTypes.REALM; }
//      "REDUCE" { return GeneratedTypes.REDUCE; }
//      "RENAME" { return GeneratedTypes.RENAME; }
      "RESPECT" { return GeneratedTypes.RESPECT; }
//      "RETURN" { return GeneratedTypes.RETURN; }
      "RETURNING" { return GeneratedTypes.RETURNING; }
      "REVOKE" { return GeneratedTypes.REVOKE; }
      "RIGHT" { return GeneratedTypes.RIGHT; }
      "ROLE" { return GeneratedTypes.ROLE; }
      "ROLLBACK" { return GeneratedTypes.ROLLBACK; }
      "ROW" { return GeneratedTypes.ROW; }
      "ROWS" { return GeneratedTypes.ROWS; }

      "SATISFIES" { return GeneratedTypes.SATISFIES; }
      "SAVEPOINT" { return GeneratedTypes.SAVEPOINT; }
//      "SCHEMA" { return GeneratedTypes.SCHEMA; }
      "SCOPE" { return GeneratedTypes.SCOPE; }
      "SELECT" { return GeneratedTypes.SELECT; }
      "SELF" { return GeneratedTypes.SELF; }
//      "SEMI" { return GeneratedTypes.SEMI; }
      "SET" { return GeneratedTypes.SET; }
//      "SHOW" { return GeneratedTypes.SHOW; }
      "SOME" { return GeneratedTypes.SOME; }
      "START" { return GeneratedTypes.START; }
      "STATISTICS" { return GeneratedTypes.STATISTICS; }
//      "STRING" { return GeneratedTypes.TYPE_STRING; }
//      "SYSTEM" { return GeneratedTypes.SYSTEM; }

      "THEN" { return GeneratedTypes.THEN; }
      "TIES" { return GeneratedTypes.TIES; }
      "TO" { return GeneratedTypes.TO; }
      "TRAN" { return GeneratedTypes.TRAN; }
      "TRANSACTION" { return GeneratedTypes.TRANSACTION; }
//      "TRIGGER" { return GeneratedTypes.TRIGGER; }
      "TRUE" { return GeneratedTypes.TRUE; }
//      "TRUNCATE" { return GeneratedTypes.TRUNCATE; }

      "UNBOUNDED" { return GeneratedTypes.UNBOUNDED; }
//      "UNDER" { return GeneratedTypes.UNDER; }
      "UNION" { return GeneratedTypes.UNION; }
//      "UNIQUE" { return GeneratedTypes.UNIQUE; }
//      "UNKNOWN" { return GeneratedTypes.UNKNOWN; }
      "UNNEST" { return GeneratedTypes.UNNEST; }
      "UNSET" { return GeneratedTypes.UNSET; }
      "UPDATE" { return GeneratedTypes.UPDATE; }
      "UPSERT" { return GeneratedTypes.UPSERT; }
      "USE" { return GeneratedTypes.USE; }
      "USER" { return GeneratedTypes.USER; }
      "USING" { return GeneratedTypes.USING; }

//      "VALIDATE" { return GeneratedTypes.VALIDATE; }
      "VALUE" { return GeneratedTypes.VALUE; }
      "VALUED" { return GeneratedTypes.VALUED; }
      "VALUES" { return GeneratedTypes.VALUES; }
//      "VIA" { return GeneratedTypes.VIA; }
//      "VIEW" { return GeneratedTypes.VIEW; }

      "WHEN" { return GeneratedTypes.WHEN; }
      "WHERE" { return GeneratedTypes.WHERE; }
//      "WHILE" { return GeneratedTypes.WHILE; }
      "WINDOW" { return GeneratedTypes.WINDOW; }
      "WITH" { return GeneratedTypes.WITH; }
      "WITHIN" { return GeneratedTypes.WITHIN; }
      "WORK" { return GeneratedTypes.WORK; }

//      "XOR" { return GeneratedTypes.XOR; }


      {PLUS} { return GeneratedTypes.PLUS; }
      {SEMICOLON} { return GeneratedTypes.SEMICOLON; }
      {COLON} { return GeneratedTypes.COLON; }
      "-" { return GeneratedTypes.MINUS_SIGN; }
      "/" { return GeneratedTypes.SLASH; }
      "%" { return GeneratedTypes.PERCENT; }
//      "^" { return GeneratedTypes.CARET; }
      "<" { return GeneratedTypes.LESSTHAN; }
      ">" { return GeneratedTypes.MORETHAN; }
      "<=" { return GeneratedTypes.LESSTHAN_OR_EQUAL; }
      ">=" { return GeneratedTypes.MORETHAN_OR_EQUAL; }
      "!=" { return GeneratedTypes.NOT_EQUAL; }
      "=" { return GeneratedTypes.EQUAL; }
      {NBR} {return GeneratedTypes.NBR;}

      "ABS" { return GeneratedTypes.FUNCS; }
      "ACOS" { return GeneratedTypes.FUNCS; }
      "ARRAY_APPEND" { return GeneratedTypes.FUNCS; }
      "ARRAY_AVG" { return GeneratedTypes.FUNCS; }
      "ARRAY_CONCAT" { return GeneratedTypes.FUNCS; }
    "ARRAY_CONTAINS" { return GeneratedTypes.FUNCS; }
    "ARRAY_COUNT" { return GeneratedTypes.FUNCS; }
    "ARRAY_DISTINCT" { return GeneratedTypes.FUNCS; }
    "ARRAY_FLATTEN" { return GeneratedTypes.FUNCS; }
    "ARRAY_IFNULL" { return GeneratedTypes.FUNCS; }
    "ARRAY_INSERT" { return GeneratedTypes.FUNCS; }
    "ARRAY_INTERSECT" { return GeneratedTypes.FUNCS; }
    "ARRAY_LENGTH" { return GeneratedTypes.FUNCS; }
    "ARRAY_MAX" { return GeneratedTypes.FUNCS; }
    "ARRAY_MIN" { return GeneratedTypes.FUNCS; }
    "ARRAY_POSITION" { return GeneratedTypes.FUNCS; }
    "ARRAY_PREPEND" { return GeneratedTypes.FUNCS; }
    "ARRAY_PUT" { return GeneratedTypes.FUNCS; }
    "ARRAY_RANGE" { return GeneratedTypes.FUNCS; }
    "ARRAY_REMOVE" { return GeneratedTypes.FUNCS; }
    "ARRAY_REPEAT" { return GeneratedTypes.FUNCS; }
    "ARRAY_REPLACE" { return GeneratedTypes.FUNCS; }
    "ARRAY_REVERSE" { return GeneratedTypes.FUNCS; }
    "ARRAY_SORT" { return GeneratedTypes.FUNCS; }
    "ARRAY_STAR" { return GeneratedTypes.FUNCS; }
    "ARRAY_SUM" { return GeneratedTypes.FUNCS; }
    "ARRAY_SYMDIFF" { return GeneratedTypes.FUNCS; }
    "ARRAY_SYMDIFF1" { return GeneratedTypes.FUNCS; }
    "ARRAY_SYMDIFFN" { return GeneratedTypes.FUNCS; }
    "ARRAY_UNION" { return GeneratedTypes.FUNCS; }
    "ASIN" { return GeneratedTypes.FUNCS; }
    "ATAN" { return GeneratedTypes.FUNCS; }
    "ATAN2" { return GeneratedTypes.FUNCS; }
    "AVG" { return GeneratedTypes.FUNCS; }
    "CEIL" { return GeneratedTypes.FUNCS; }
    "CLOCK_LOCAL" { return GeneratedTypes.FUNCS; }
    "CLOCK_MILLIS" { return GeneratedTypes.FUNCS; }
    "CLOCK_STR" { return GeneratedTypes.FUNCS; }
    "CLOCK_TZ" { return GeneratedTypes.FUNCS; }
    "CLOCK_UTC" { return GeneratedTypes.FUNCS; }
    "CONCAT" { return GeneratedTypes.FUNCS; }
    "CONTAINS" { return GeneratedTypes.FUNCS; }
    "COS" { return GeneratedTypes.FUNCS; }
    "COUNT" { return GeneratedTypes.FUNCS; }
    "DATE_ADD_MILLIS" { return GeneratedTypes.FUNCS; }
    "DATE_ADD_STR" { return GeneratedTypes.FUNCS; }
    "DATE_DIFF_MILLIS" { return GeneratedTypes.FUNCS; }
    "DATE_DIFF_STR" { return GeneratedTypes.FUNCS; }
    "DATE_FORMAT_STR" { return GeneratedTypes.FUNCS; }
    "DATE_PART_MILLIS" { return GeneratedTypes.FUNCS; }
    "DATE_PART_STR" { return GeneratedTypes.FUNCS; }
    "DATE_RANGE_MILLIS" { return GeneratedTypes.FUNCS; }
    "DATE_RANGE_STR" { return GeneratedTypes.FUNCS; }
    "DATE_TRUNC_MILLIS" { return GeneratedTypes.FUNCS; }
    "DATE_TRUNC_STR" { return GeneratedTypes.FUNCS; }
    "DECODE_JSON" { return GeneratedTypes.FUNCS; }
    "DEGREES" { return GeneratedTypes.FUNCS; }
    "DURATION_TO_STR" { return GeneratedTypes.FUNCS; }
    "ENCODE_JSON" { return GeneratedTypes.FUNCS; }
    "ENCODED_SIZE" { return GeneratedTypes.FUNCS; }
    "EXP" { return GeneratedTypes.FUNCS; }
    "FLOOR" { return GeneratedTypes.FUNCS; }
    "GREATEST" { return GeneratedTypes.FUNCS; }
    "IF_INF" { return GeneratedTypes.FUNCS; }
    "IF_MISSING" { return GeneratedTypes.FUNCS; }
    "IF_MISSING_OR_NULL" { return GeneratedTypes.FUNCS; }
    "IF_NAN" { return GeneratedTypes.FUNCS; }
    "IF_NAN_OR_INF" { return GeneratedTypes.FUNCS; }
    "IF_NULL" { return GeneratedTypes.FUNCS; }
    "IFINF" { return GeneratedTypes.FUNCS; }
    "IFMISSING" { return GeneratedTypes.FUNCS; }
    "IFMISSINGORNULL" { return GeneratedTypes.FUNCS; }
    "IFNAN" { return GeneratedTypes.FUNCS; }
    "IFNANORINF" { return GeneratedTypes.FUNCS; }
    "IFNULL" { return GeneratedTypes.FUNCS; }
    "INITCAP" { return GeneratedTypes.FUNCS; }
    "IS_ARRAY" { return GeneratedTypes.FUNCS; }
    "IS_ATOM" { return GeneratedTypes.FUNCS; }
    "IS_BOOL" { return GeneratedTypes.FUNCS; }
    "IS_BOOLEAN" { return GeneratedTypes.FUNCS; }
    "IS_NUM" { return GeneratedTypes.FUNCS; }
    "IS_NUMBER" { return GeneratedTypes.FUNCS; }
    "IS_OBJ" { return GeneratedTypes.FUNCS; }
    "IS_OBJECT" { return GeneratedTypes.FUNCS; }
    "IS_STR" { return GeneratedTypes.FUNCS; }
    "IS_STRING" { return GeneratedTypes.FUNCS; }
    "ISARRAY" { return GeneratedTypes.FUNCS; }
    "LEAST" { return GeneratedTypes.FUNCS; }
    "LENGTH" { return GeneratedTypes.FUNCS; }
    "LN" { return GeneratedTypes.FUNCS; }
    "LOG" { return GeneratedTypes.FUNCS; }
    "LOWER" { return GeneratedTypes.FUNCS; }
    "LTRIM" { return GeneratedTypes.FUNCS; }
    "MAX" { return GeneratedTypes.FUNCS; }
    "META" { return GeneratedTypes.FUNCS; }
    "MILLIS" { return GeneratedTypes.FUNCS; }
    "MILLIS_TO_LOCAL" { return GeneratedTypes.FUNCS; }
    "MILLIS_TO_STR" { return GeneratedTypes.FUNCS; }
    "MILLIS_TO_TZ" { return GeneratedTypes.FUNCS; }
    "MILLIS_TO_UTC" { return GeneratedTypes.FUNCS; }
    "MILLIS_TO_ZONE_NAME" { return GeneratedTypes.FUNCS; }
    "MIN" { return GeneratedTypes.FUNCS; }
    "MISSING_IF" { return GeneratedTypes.FUNCS; }
    "MISSINGIF" { return GeneratedTypes.FUNCS; }
    "NAN_IF" { return GeneratedTypes.FUNCS; }
    "NANIF" { return GeneratedTypes.FUNCS; }
    "NEGINF_IF" { return GeneratedTypes.FUNCS; }
    "NEGINFIF" { return GeneratedTypes.FUNCS; }
    "NOW_LOCAL" { return GeneratedTypes.FUNCS; }
    "NOW_MILLIS" { return GeneratedTypes.FUNCS; }
    "NOW_STR" { return GeneratedTypes.FUNCS; }
    "NOW_TZ" { return GeneratedTypes.FUNCS; }
    "NOW_UTC" { return GeneratedTypes.FUNCS; }
    "NULL_IF" { return GeneratedTypes.FUNCS; }
    "NULLIF" { return GeneratedTypes.FUNCS; }
    "OBJECT_ADD" { return GeneratedTypes.FUNCS; }
    "OBJECT_CONCAT" { return GeneratedTypes.FUNCS; }
    "OBJECT_INNER_VALUES" { return GeneratedTypes.FUNCS; }
    "OBJECT_LENGTH" { return GeneratedTypes.FUNCS; }
    "OBJECT_NAMES" { return GeneratedTypes.FUNCS; }
    "OBJECT_PAIRS" { return GeneratedTypes.FUNCS; }
    "OBJECT_PUT" { return GeneratedTypes.FUNCS; }
    "OBJECT_REMOVE" { return GeneratedTypes.FUNCS; }
    "OBJECT_RENAME" { return GeneratedTypes.FUNCS; }
    "OBJECT_REPLACE" { return GeneratedTypes.FUNCS; }
    "OBJECT_UNWRAP" { return GeneratedTypes.FUNCS; }
    "OBJECT_VALUES" { return GeneratedTypes.FUNCS; }
    "PAIRS" { return GeneratedTypes.FUNCS; }
    "PI" { return GeneratedTypes.FUNCS; }
    "POSINFIF" { return GeneratedTypes.FUNCS; }
    "POSITION" { return GeneratedTypes.FUNCS; }
    "POWER" { return GeneratedTypes.FUNCS; }
    "RADIANS" { return GeneratedTypes.FUNCS; }
    "RANDOM" { return GeneratedTypes.FUNCS; }
    "REGEXP_CONTAINS" { return GeneratedTypes.FUNCS; }
    "REGEXP_LIKE" { return GeneratedTypes.FUNCS; }
    "REGEXP_POSITION" { return GeneratedTypes.FUNCS; }
    "REGEXP_REPLACE" { return GeneratedTypes.FUNCS; }
    "REPEAT" { return GeneratedTypes.FUNCS; }
    "REPLACE" { return GeneratedTypes.FUNCS; }
    "REVERSE" { return GeneratedTypes.FUNCS; }
    "ROUND" { return GeneratedTypes.FUNCS; }
    "RTRIM" { return GeneratedTypes.FUNCS; }
    "SIGN" { return GeneratedTypes.FUNCS; }
    "SIN" { return GeneratedTypes.FUNCS; }
    "SPLIT" { return GeneratedTypes.FUNCS; }
    "SQRT" { return GeneratedTypes.FUNCS; }
    "STR_TO_DURATION" { return GeneratedTypes.FUNCS; }
    "STR_TO_MILLIS" { return GeneratedTypes.FUNCS; }
    "STR_TO_TZ" { return GeneratedTypes.FUNCS; }
    "STR_TO_UTC" { return GeneratedTypes.FUNCS; }
    "STR_TO_ZONE_NAME" { return GeneratedTypes.FUNCS; }
    "SUBSTR" { return GeneratedTypes.FUNCS; }
    "SUM" { return GeneratedTypes.FUNCS; }
    "TAN" { return GeneratedTypes.FUNCS; }
    "TITLE" { return GeneratedTypes.FUNCS; }
    "TO_ARRAY" { return GeneratedTypes.FUNCS; }
    "TO_ATOM" { return GeneratedTypes.FUNCS; }
    "TO_BOOL" { return GeneratedTypes.FUNCS; }
    "TO_BOOLEAN" { return GeneratedTypes.FUNCS; }
    "TO_NUM" { return GeneratedTypes.FUNCS; }
    "TO_NUMBER" { return GeneratedTypes.FUNCS; }
    "TO_OBJ" { return GeneratedTypes.FUNCS; }
    "TO_OBJECT" { return GeneratedTypes.FUNCS; }
    "TO_STR" { return GeneratedTypes.FUNCS; }
    "TO_STRING" { return GeneratedTypes.FUNCS; }
    "TOARRAY" { return GeneratedTypes.FUNCS; }
    "TOATOM" { return GeneratedTypes.FUNCS; }
    "TOBOOL" { return GeneratedTypes.FUNCS; }
    "TOBOOLEAN" { return GeneratedTypes.FUNCS; }
    "TONUM" { return GeneratedTypes.FUNCS; }
    "TONUMBER" { return GeneratedTypes.FUNCS; }
    "TOOBJ" { return GeneratedTypes.FUNCS; }
    "TOOBJECT" { return GeneratedTypes.FUNCS; }
    "TOSTR" { return GeneratedTypes.FUNCS; }
    "TOSTRING" { return GeneratedTypes.FUNCS; }
    "TRIM" { return GeneratedTypes.FUNCS; }
    "TRUNC" { return GeneratedTypes.FUNCS; }
    "TYPE" { return GeneratedTypes.FUNCS; }
    "TYPENAME" { return GeneratedTypes.FUNCS; }
    "UPPER" { return GeneratedTypes.FUNCS; }
    "UUID" { return GeneratedTypes.FUNCS; }
    "WEEKDAY_MILLIS" { return GeneratedTypes.FUNCS; }
    "WEEKDAY_STR" { return GeneratedTypes.FUNCS; }
      {IDT} { return GeneratedTypes.IDENTIFIER; }
      {ESCAPED_IDT} { return GeneratedTypes.ESCAPED_IDENTIFIER; }
      {ASTERISK} { return GeneratedTypes.ASTERISK; }
      {OPEN_BRACKET} { return GeneratedTypes.LBRACKET; }
      {CLOSE_BRACKET} { return GeneratedTypes.RBRACKET; }
      {COMMA} { return GeneratedTypes.COMMA; }
      {DOT} { return GeneratedTypes.DOT; }
      {OPEN_PAREN} { return GeneratedTypes.LPAREN; }
      {CLOSE_PAREN} { return GeneratedTypes.RPAREN; }
      {OPEN_CURLY} { return GeneratedTypes.LBRACE; }
      {CLOSE_CURLY} { return GeneratedTypes.RBRACE; }
      {SLASH} { return GeneratedTypes.SLASH; }
      {QUOTE} { yybegin(SingleQuotedString); return GeneratedTypes.QUOTE; }
      {DQUOTE} { yybegin(DoubleQuotedString); return GeneratedTypes.DQUOTE; }

  {WHITE_SPACE} { return TokenType.WHITE_SPACE; }

  . { return TokenType.BAD_CHARACTER; }

}

<SingleQuotedString> {
    \'                             { yybegin(YYINITIAL); return GeneratedTypes.QUOTE; }
    \\[nrt\\]                      { return GeneratedTypes.ESCAPE_SEQUENCE; }
    \\\'                           { return GeneratedTypes.ESCAPED_QUOTE; }
    \\u{HEX}{HEX}{HEX}{HEX}        { return GeneratedTypes.ESCAPE_SEQUENCE; }
    .                              { return GeneratedTypes.STRING_CHAR; }
}

<DoubleQuotedString> {
    \"                             { yybegin(YYINITIAL); return GeneratedTypes.DQUOTE; }
    \\[nrt\\]                      { return GeneratedTypes.ESCAPE_SEQUENCE; }
    \\\"                           { return GeneratedTypes.ESCAPED_DQUOTE; }
    \\u{HEX}{HEX}{HEX}{HEX}        { return GeneratedTypes.ESCAPE_SEQUENCE; }
    .                              { return GeneratedTypes.STRING_CHAR; }
}

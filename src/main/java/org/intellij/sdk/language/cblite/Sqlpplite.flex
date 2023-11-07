package org.intellij.sdk.language.cblite;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import generated.cblite.GeneratedTypes;
import com.intellij.psi.TokenType;

%%

%class SqlppLiteLexer
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

IDT = [a-zA-Z_][0-9a-zA-Z_\-$]*
HEX = [0-9a-fA-F]
CHR = (\\[\\\/bfnrt]|\\u{HEX}{HEX}{HEX}{HEX})
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
DOLLAR = "$"
VARIABLE = \$[a-zA-Z_] [a-zA-Z0-9_$]*


Backtick = "`"

%state SingleQuotedString
%state DoubleQuotedString
%%

<YYINITIAL> {

      "ALL" { return GeneratedTypes.ALL; }
      "AND" { return GeneratedTypes.AND; }
      "ANY" { return GeneratedTypes.ANY; }
      "AS" { return GeneratedTypes.AS; }
      "ASC" { return GeneratedTypes.ASC; }

      "BETWEEN" { return GeneratedTypes.BETWEEN; }
      "BY" { return GeneratedTypes.BY; }

      "CASE" { return GeneratedTypes.CASE; }

      "DESC" { return GeneratedTypes.DESC; }
      "DIACRITICS" { return GeneratedTypes.DIACRITICS; }
      "DISTINCT" { return GeneratedTypes.DISTINCT; }

      "ELSE" { return GeneratedTypes.ELSE; }
      "END" { return GeneratedTypes.END; }
      "EXISTS" { return GeneratedTypes.EXISTS; }

      "FALSE" { return GeneratedTypes.FALSE; }
      "FROM" { return GeneratedTypes.FROM; }

      "GROUP" { return GeneratedTypes.GROUP; }

      "HAVING" { return GeneratedTypes.HAVING; }

      "IN" { return GeneratedTypes.IN; }
      "INNER" { return GeneratedTypes.INNER; }
      "IS" { return GeneratedTypes.IS; }

      "JOIN" { return GeneratedTypes.JOIN; }

      "LEFT" { return GeneratedTypes.LEFT; }
      "LIKE" { return GeneratedTypes.LIKE; }
      "LIMIT" { return GeneratedTypes.LIMIT; }

      "MISSING" { return GeneratedTypes.MISSING; }

      "NOUNICODE" { return GeneratedTypes.NOUNICODE; }
      "NOCASE" { return GeneratedTypes.NOCASE; }
      "NODIACRITICS" { return GeneratedTypes.NODIACRITICS; }
      "NOT" { return GeneratedTypes.NOT; }
      "NULL" { return GeneratedTypes.NULL; }

      "OFFSET" { return GeneratedTypes.OFFSET; }
      "ON" { return GeneratedTypes.ON; }
      "OR" { return GeneratedTypes.OR; }
      "ORDER" { return GeneratedTypes.ORDER; }
      "OUTER" { return GeneratedTypes.OUTER; }

      "SATISFIES" { return GeneratedTypes.SATISFIES; }
      "SELECT" { return GeneratedTypes.SELECT; }
      "SOME" { return GeneratedTypes.SOME; }

      "THEN" { return GeneratedTypes.THEN; }
      "TRUE" { return GeneratedTypes.TRUE; }

      "VALUED" { return GeneratedTypes.VALUED; }

      "WHEN" { return GeneratedTypes.WHEN; }
      "WHERE" { return GeneratedTypes.WHERE; }

      {PLUS} { return GeneratedTypes.PLUS; }
      {SEMICOLON} { return GeneratedTypes.SEMICOLON; }
      {COLON} { return GeneratedTypes.COLON; }
      "-" { return GeneratedTypes.MINUS_SIGN; }
      "/" { return GeneratedTypes.SLASH; }
      "%" { return GeneratedTypes.PERCENT; }
      "==" { return GeneratedTypes.DOUBLE_EQUAL; }
      "<" { return GeneratedTypes.LESSTHAN; }
      ">" { return GeneratedTypes.MORETHAN; }
      "<=" { return GeneratedTypes.LESSTHAN_OR_EQUAL; }
      ">=" { return GeneratedTypes.MORETHAN_OR_EQUAL; }
      "!=" { return GeneratedTypes.NOT_EQUAL; }
      "<>" {return GeneratedTypes.LESSTHAN_OR_MORETHAN;}
      "=" { return GeneratedTypes.EQUAL; }
      {NBR} {return GeneratedTypes.NBR;}
      "`" { return GeneratedTypes.BACKTICK; }
      "<<" {return GeneratedTypes.BINARY_SHIFT_LEFT;}
      "<<" {return GeneratedTypes.BINARY_SHIFT_LEFT;}
      "&" {return GeneratedTypes.AMPERSAND;}
      "|" {return GeneratedTypes.PIPE;}
      "||" {return GeneratedTypes.DOUBLE_PIPE;}

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
    "UNICODE" { return GeneratedTypes.UNICODE; }
    "WEEKDAY_MILLIS" { return GeneratedTypes.FUNCS; }
    "WEEKDAY_STR" { return GeneratedTypes.FUNCS; }
      {IDT} { return GeneratedTypes.IDENTIFIER; }
      {ASTERISK} { return GeneratedTypes.ASTERISK; }
      {OPEN_BRACKET} { return GeneratedTypes.LBRACKET; }
      {CLOSE_BRACKET} { return GeneratedTypes.RBRACKET; }
      {COMMA} { return GeneratedTypes.COMMA; }
      {DOT} { return GeneratedTypes.DOT; }
      {OPEN_PAREN} { return GeneratedTypes.LPAREN; }
      {CLOSE_PAREN} { return GeneratedTypes.RPAREN; }
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

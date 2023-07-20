// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.

package org.intellij.sdk.language.psi;

import com.intellij.psi.tree.TokenSet;
import generated.GeneratedTypes;

public interface SqlppTokenSets {

  TokenSet COMMENTS = TokenSet.create(GeneratedTypes.COMMENT);

  TokenSet IDENTIFIERS = TokenSet.create(
          GeneratedTypes.IDENTIFIER,
          GeneratedTypes.ESCAPED_IDENTIFIER
  );

  TokenSet PUNCTUATION = TokenSet.create(
          GeneratedTypes.ASTERISK,
          GeneratedTypes.LBRACKET,
          GeneratedTypes.RBRACKET,
          GeneratedTypes.COMMA,
          GeneratedTypes.DOT,
          GeneratedTypes.LPAREN,
          GeneratedTypes.RPAREN,
          GeneratedTypes.LBRACE,
          GeneratedTypes.RBRACE,
          GeneratedTypes.SLASH
  );

  TokenSet LITERALS = TokenSet.create(
          GeneratedTypes.NBR,
          GeneratedTypes.STR,
          GeneratedTypes.BOOL
  );

  TokenSet STRINGS = TokenSet.create(
          GeneratedTypes.QUOTE,
          GeneratedTypes.DQUOTE,
          GeneratedTypes.STRING_CHAR,
          GeneratedTypes.ESCAPE_SEQUENCE,
          GeneratedTypes.ESCAPED_QUOTE,
          GeneratedTypes.ESCAPED_DQUOTE
  );

  TokenSet KEYWORDS = TokenSet.create(
          GeneratedTypes.ADVISE,
          GeneratedTypes.ALL,
          GeneratedTypes.ALTER,
          GeneratedTypes.ANALYZE,
          GeneratedTypes.AND,
          GeneratedTypes.ANY,
          GeneratedTypes.ARRAY,
          GeneratedTypes.AS,
          GeneratedTypes.ASC,
          GeneratedTypes.AT,

          GeneratedTypes.BEGIN,
          GeneratedTypes.BETWEEN,
//          GeneratedTypes.BINARY,
//          GeneratedTypes.BOOLEAN,
//          GeneratedTypes.BREAK,
//          GeneratedTypes.BUCKET,
          GeneratedTypes.BUILD,
          GeneratedTypes.BY,

//          GeneratedTypes.CALL,
          GeneratedTypes.CASE,
//          GeneratedTypes.CAST,
//          GeneratedTypes.CLUSTER,
//          GeneratedTypes.COLLATE,
          GeneratedTypes.COLLECTION,
          GeneratedTypes.COMMIT,
          GeneratedTypes.COMMITTED,
//          GeneratedTypes.CONNECT,
//          GeneratedTypes.CONTINUE,
//          GeneratedTypes.CORRELATED,
//          GeneratedTypes.COVER,
          GeneratedTypes.CREATE,
          GeneratedTypes.CURRENT,

//          GeneratedTypes.DATABASE,
//          GeneratedTypes.DATASET,
//          GeneratedTypes.DATASTORE,
//          GeneratedTypes.DECLARE,
//          GeneratedTypes.DECREMENT,
          GeneratedTypes.DELETE,
//          GeneratedTypes.DERIVED,
          GeneratedTypes.DESC,
//          GeneratedTypes.DESCRIBE,
          GeneratedTypes.DISTINCT,
//          GeneratedTypes.DO,
          GeneratedTypes.DROP,

//          GeneratedTypes.EACH,
          GeneratedTypes.ELEMENT,
          GeneratedTypes.ELSE,
          GeneratedTypes.END,
          GeneratedTypes.EVERY,
          GeneratedTypes.EXCEPT,
          GeneratedTypes.EXCLUDE,
          GeneratedTypes.EXECUTE,
          GeneratedTypes.EXISTS,
          GeneratedTypes.EXPLAIN,

          GeneratedTypes.FALSE,
//          GeneratedTypes.FETCH,
          GeneratedTypes.FILTER,
          GeneratedTypes.FIRST,
          GeneratedTypes.FLATTEN,
//          GeneratedTypes.FLUSH,
          GeneratedTypes.FOLLOWING,
          GeneratedTypes.FOR,
//          GeneratedTypes.FORCE,
          GeneratedTypes.FROM,
          GeneratedTypes.FTS,
          GeneratedTypes.FUNCTION,
          
//          GeneratedTypes.GOLANG,
          GeneratedTypes.GRANT,
          GeneratedTypes.GROUP,
          GeneratedTypes.GROUPS,
          GeneratedTypes.GSI,
          
          GeneratedTypes.HASH,
          GeneratedTypes.HAVING,
          
          GeneratedTypes.IF,
          GeneratedTypes.ISOLATION,
          GeneratedTypes.IGNORE,
//          GeneratedTypes.ILIKE,
          GeneratedTypes.IN,
          GeneratedTypes.INCLUDE,
//          GeneratedTypes.INCREMENT,
          GeneratedTypes.INDEX,
          GeneratedTypes.INFER,
          GeneratedTypes.INLINE,
          GeneratedTypes.INNER,
          GeneratedTypes.INSERT,
          GeneratedTypes.INTERSECT,
          GeneratedTypes.INTO,
          GeneratedTypes.IS,

          GeneratedTypes.JAVASCRIPT,
          GeneratedTypes.JOIN,

          GeneratedTypes.KEY,
          GeneratedTypes.KEYS,
          GeneratedTypes.KEYSPACE,
//          GeneratedTypes.KNOWN,

          GeneratedTypes.LANGUAGE,
          GeneratedTypes.LAST,
          GeneratedTypes.LEFT,
          GeneratedTypes.LET,
          GeneratedTypes.LETTING,
          GeneratedTypes.LEVEL,
          GeneratedTypes.LIKE,
          GeneratedTypes.LIMIT,
//          GeneratedTypes.LSM,

//          GeneratedTypes.MAP,
//          GeneratedTypes.MAPPING,
          GeneratedTypes.MATCHED,
//          GeneratedTypes.MATERIALIZED,
          GeneratedTypes.MERGE,
//          GeneratedTypes.MINUS,
          GeneratedTypes.MISSING,

//          GeneratedTypes.NAMESPACE,
          GeneratedTypes.NEST,
          GeneratedTypes.NL,
          GeneratedTypes.NO,
          GeneratedTypes.NOT,
//          GeneratedTypes.NTH_VALUE,
          GeneratedTypes.NULL,
          GeneratedTypes.NULLS,
//          GeneratedTypes.NUMBER,

//          GeneratedTypes.OBJECT,
          GeneratedTypes.OFFSET,
          GeneratedTypes.ON,
//          GeneratedTypes.OPTION,
          GeneratedTypes.OPTIONS,
          GeneratedTypes.OR,
          GeneratedTypes.ORDER,
          GeneratedTypes.OTHERS,
          GeneratedTypes.OUTER,
          GeneratedTypes.OVER,

//          GeneratedTypes.PARSE,
          GeneratedTypes.PARTITION,
//          GeneratedTypes.PASSWORD,
          GeneratedTypes.PATH,
//          GeneratedTypes.POOL,
          GeneratedTypes.PRECEDING,
//          GeneratedTypes.PREPARE,
          GeneratedTypes.PRIMARY,
//          GeneratedTypes.PRIVATE,
//          GeneratedTypes.PRIVILEGE,
          GeneratedTypes.PROBE,
//          GeneratedTypes.PROCEDURE,
//          GeneratedTypes.PUBLIC,

          GeneratedTypes.RANGE,
          GeneratedTypes.RAW,
//          GeneratedTypes.REALM,
//          GeneratedTypes.REDUCE,
//          GeneratedTypes.RENAME,
          GeneratedTypes.RESPECT,
//          GeneratedTypes.RETURN,
          GeneratedTypes.RETURNING,
          GeneratedTypes.REVOKE,
          GeneratedTypes.RIGHT,
          GeneratedTypes.ROLE,
          GeneratedTypes.ROLLBACK,
          GeneratedTypes.ROW,
          GeneratedTypes.ROWS,

          GeneratedTypes.SATISFIES,
          GeneratedTypes.SAVEPOINT,
//          GeneratedTypes.SCHEMA,
          GeneratedTypes.SCOPE,
          GeneratedTypes.SELECT,
          GeneratedTypes.SELF,
//          GeneratedTypes.SEMI,
          GeneratedTypes.SET,
//          GeneratedTypes.SHOW,
          GeneratedTypes.SOME,
          GeneratedTypes.START,
          GeneratedTypes.STATISTICS,
//          GeneratedTypes.STRING,
//          GeneratedTypes.SYSTEM,

          GeneratedTypes.THEN,
          GeneratedTypes.TIES,
          GeneratedTypes.TO,
          GeneratedTypes.TRAN,
          GeneratedTypes.TRANSACTION,
//          GeneratedTypes.TRIGGER,
          GeneratedTypes.TRUE,
//          GeneratedTypes.TRUNCATE,

          GeneratedTypes.UNBOUNDED,
//          GeneratedTypes.UNDER,
          GeneratedTypes.UNION,
//          GeneratedTypes.UNIQUE,
//          GeneratedTypes.UNKNOWN,
          GeneratedTypes.UNNEST,
          GeneratedTypes.UNSET,
          GeneratedTypes.UPDATE,
          GeneratedTypes.UPSERT,
          GeneratedTypes.USE,
          GeneratedTypes.USER,
          GeneratedTypes.USING,

//          GeneratedTypes.VALIDATE,
          GeneratedTypes.VALUE,
          GeneratedTypes.VALUED,
          GeneratedTypes.VALUES,
//          GeneratedTypes.VIA,
//          GeneratedTypes.VIEW,

          GeneratedTypes.WHEN,
          GeneratedTypes.WHERE,
//          GeneratedTypes.WHILE,
          GeneratedTypes.WINDOW,
          GeneratedTypes.WITH,
          GeneratedTypes.WITHIN,
          GeneratedTypes.WORK

//          GeneratedTypes.XOR
  );
}










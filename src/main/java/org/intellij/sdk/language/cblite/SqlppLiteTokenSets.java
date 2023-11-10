package org.intellij.sdk.language.cblite;

import com.intellij.psi.tree.TokenSet;
import generated.cblite.GeneratedTypes;
import org.apache.commons.beanutils.BasicDynaBean;

public interface SqlppLiteTokenSets {
    TokenSet COMMENTS = TokenSet.EMPTY;
    TokenSet STRING_LITERALS = TokenSet.create(
            GeneratedTypes.QUOTE,
            GeneratedTypes.DQUOTE,
            GeneratedTypes.STRING_CHAR,
            GeneratedTypes.ESCAPE_SEQUENCE,
            GeneratedTypes.ESCAPED_DQUOTE,
            GeneratedTypes.ESCAPED_QUOTE
    );
    TokenSet KEYWORDS = TokenSet.create(
            GeneratedTypes.ALL,
            GeneratedTypes.AND,
            GeneratedTypes.ANY,
            GeneratedTypes.AS,
            GeneratedTypes.ASC,

            GeneratedTypes.BETWEEN,
            GeneratedTypes.BY,

            GeneratedTypes.CASE,

            GeneratedTypes.DESC,
            GeneratedTypes.DISTINCT,

            GeneratedTypes.ELSE,
            GeneratedTypes.END,
            GeneratedTypes.EVERY,
            GeneratedTypes.EXISTS,

            GeneratedTypes.FALSE,
            GeneratedTypes.FROM,

            GeneratedTypes.GROUP,

            GeneratedTypes.HAVING,

            GeneratedTypes.IN,
            GeneratedTypes.INNER,
            GeneratedTypes.IS,

            GeneratedTypes.JOIN,

            GeneratedTypes.LEFT,
            GeneratedTypes.LIKE,
            GeneratedTypes.LIMIT,

            GeneratedTypes.MISSING,

            GeneratedTypes.NOT,
            GeneratedTypes.NULL,

            GeneratedTypes.OFFSET,
            GeneratedTypes.ON,
            GeneratedTypes.OR,
            GeneratedTypes.ORDER,
            GeneratedTypes.OUTER,

            GeneratedTypes.SATISFIES,
            GeneratedTypes.SELECT,
            GeneratedTypes.SOME,

            GeneratedTypes.THEN,
            GeneratedTypes.TRUE,

            GeneratedTypes.VALUED,

            GeneratedTypes.WHEN,
            GeneratedTypes.WHERE
    );

    TokenSet PUNCTUATION = TokenSet.create(
            GeneratedTypes.ASTERISK,
            GeneratedTypes.LBRACKET,
            GeneratedTypes.RBRACKET,
            GeneratedTypes.COMMA,
            GeneratedTypes.DOT,
            GeneratedTypes.LPAREN,
            GeneratedTypes.RPAREN,
            GeneratedTypes.SLASH
    );

    TokenSet IDENTIFIERS = TokenSet.create(
            GeneratedTypes.IDENTIFIER,
            GeneratedTypes.ESCAPED_IDENTIFIER
    );
}


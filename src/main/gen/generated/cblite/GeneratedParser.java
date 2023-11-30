// This is a generated file. Not intended for manual editing.
package generated.cblite;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static generated.cblite.GeneratedTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class GeneratedParser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType t, PsiBuilder b) {
    parseLight(t, b);
    return b.getTreeBuilt();
  }

  public void parseLight(IElementType t, PsiBuilder b) {
    boolean r;
    b = adapt_builder_(t, b, this, null);
    Marker m = enter_section_(b, 0, _COLLAPSE_, null);
    r = parse_root_(t, b);
    exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType t, PsiBuilder b) {
    return parse_root_(t, b, 0);
  }

  static boolean parse_root_(IElementType t, PsiBuilder b, int l) {
    return script(b, l + 1);
  }

  /* ********************************************************** */
  // identifier-ref
  public static boolean alias(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "alias")) return false;
    if (!nextTokenIs(b, "<alias>", BACKTICK, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ALIAS, "<alias>");
    r = identifier_ref(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // any-every-operator identifier-ref IN expression SATISFIES expression END
  public static boolean any_every_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "any_every_expression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ANY_EVERY_EXPRESSION, "<any every expression>");
    r = any_every_operator(b, l + 1);
    r = r && identifier_ref(b, l + 1);
    r = r && consumeToken(b, IN);
    r = r && expression(b, l + 1);
    r = r && consumeToken(b, SATISFIES);
    r = r && expression(b, l + 1);
    r = r && consumeToken(b, END);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ((ANY | SOME) AND EVERY) | (ANY | SOME) | EVERY
  public static boolean any_every_operator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "any_every_operator")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ANY_EVERY_OPERATOR, "<any every operator>");
    r = any_every_operator_0(b, l + 1);
    if (!r) r = any_every_operator_1(b, l + 1);
    if (!r) r = consumeToken(b, EVERY);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (ANY | SOME) AND EVERY
  private static boolean any_every_operator_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "any_every_operator_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = any_every_operator_0_0(b, l + 1);
    r = r && consumeTokens(b, 0, AND, EVERY);
    exit_section_(b, m, null, r);
    return r;
  }

  // ANY | SOME
  private static boolean any_every_operator_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "any_every_operator_0_0")) return false;
    boolean r;
    r = consumeToken(b, ANY);
    if (!r) r = consumeToken(b, SOME);
    return r;
  }

  // ANY | SOME
  private static boolean any_every_operator_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "any_every_operator_1")) return false;
    boolean r;
    r = consumeToken(b, ANY);
    if (!r) r = consumeToken(b, SOME);
    return r;
  }

  /* ********************************************************** */
  // LBRACKET (expression (COMMA expression)*)? RBRACKET
  public static boolean array_literal(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array_literal")) return false;
    if (!nextTokenIs(b, LBRACKET)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LBRACKET);
    r = r && array_literal_1(b, l + 1);
    r = r && consumeToken(b, RBRACKET);
    exit_section_(b, m, ARRAY_LITERAL, r);
    return r;
  }

  // (expression (COMMA expression)*)?
  private static boolean array_literal_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array_literal_1")) return false;
    array_literal_1_0(b, l + 1);
    return true;
  }

  // expression (COMMA expression)*
  private static boolean array_literal_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array_literal_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = expression(b, l + 1);
    r = r && array_literal_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA expression)*
  private static boolean array_literal_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array_literal_1_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!array_literal_1_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "array_literal_1_0_1", c)) break;
    }
    return true;
  }

  // COMMA expression
  private static boolean array_literal_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array_literal_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && expression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // literal |
  //     array-literal |
  //     dict-literal |
  //     (op-prefix base-expression) |
  //     (EXISTS select-expression) |
  //     case-expression |
  //     any-every-expression |
  //     identifier-ref |
  //     function-call |
  //     property |
  //     (LPAREN expression RPAREN)
  public static boolean base_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "base_expression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BASE_EXPRESSION, "<base expression>");
    r = literal(b, l + 1);
    if (!r) r = array_literal(b, l + 1);
    if (!r) r = dict_literal(b, l + 1);
    if (!r) r = base_expression_3(b, l + 1);
    if (!r) r = base_expression_4(b, l + 1);
    if (!r) r = case_expression(b, l + 1);
    if (!r) r = any_every_expression(b, l + 1);
    if (!r) r = identifier_ref(b, l + 1);
    if (!r) r = function_call(b, l + 1);
    if (!r) r = property(b, l + 1);
    if (!r) r = base_expression_10(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // op-prefix base-expression
  private static boolean base_expression_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "base_expression_3")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = op_prefix(b, l + 1);
    r = r && base_expression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // EXISTS select-expression
  private static boolean base_expression_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "base_expression_4")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EXISTS);
    r = r && select_expression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // LPAREN expression RPAREN
  private static boolean base_expression_10(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "base_expression_10")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LPAREN);
    r = r && expression(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // expr5 NOT? BETWEEN expr5 AND expr5
  public static boolean between_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "between_expression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BETWEEN_EXPRESSION, "<between expression>");
    r = expr5(b, l + 1);
    r = r && between_expression_1(b, l + 1);
    r = r && consumeToken(b, BETWEEN);
    r = r && expr5(b, l + 1);
    r = r && consumeToken(b, AND);
    r = r && expr5(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // NOT?
  private static boolean between_expression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "between_expression_1")) return false;
    consumeToken(b, NOT);
    return true;
  }

  /* ********************************************************** */
  // TRUE | FALSE
  public static boolean boolean_literal(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "boolean_literal")) return false;
    if (!nextTokenIs(b, "<boolean literal>", FALSE, TRUE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BOOLEAN_LITERAL, "<boolean literal>");
    r = consumeToken(b, TRUE);
    if (!r) r = consumeToken(b, FALSE);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // FUNCS LPAREN ( expr ( COMMA expr )* )? RPAREN
  public static boolean builtin_function(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "builtin_function")) return false;
    if (!nextTokenIs(b, FUNCS)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, FUNCS, LPAREN);
    r = r && builtin_function_2(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, BUILTIN_FUNCTION, r);
    return r;
  }

  // ( expr ( COMMA expr )* )?
  private static boolean builtin_function_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "builtin_function_2")) return false;
    builtin_function_2_0(b, l + 1);
    return true;
  }

  // expr ( COMMA expr )*
  private static boolean builtin_function_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "builtin_function_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EXPR);
    r = r && builtin_function_2_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( COMMA expr )*
  private static boolean builtin_function_2_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "builtin_function_2_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!builtin_function_2_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "builtin_function_2_0_1", c)) break;
    }
    return true;
  }

  // COMMA expr
  private static boolean builtin_function_2_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "builtin_function_2_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, COMMA, EXPR);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // CASE expression?
  //     (WHEN expression THEN expression)+
  //     (ELSE expression)? END
  public static boolean case_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "case_expression")) return false;
    if (!nextTokenIs(b, CASE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, CASE);
    r = r && case_expression_1(b, l + 1);
    r = r && case_expression_2(b, l + 1);
    r = r && case_expression_3(b, l + 1);
    r = r && consumeToken(b, END);
    exit_section_(b, m, CASE_EXPRESSION, r);
    return r;
  }

  // expression?
  private static boolean case_expression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "case_expression_1")) return false;
    expression(b, l + 1);
    return true;
  }

  // (WHEN expression THEN expression)+
  private static boolean case_expression_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "case_expression_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = case_expression_2_0(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!case_expression_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "case_expression_2", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // WHEN expression THEN expression
  private static boolean case_expression_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "case_expression_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, WHEN);
    r = r && expression(b, l + 1);
    r = r && consumeToken(b, THEN);
    r = r && expression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (ELSE expression)?
  private static boolean case_expression_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "case_expression_3")) return false;
    case_expression_3_0(b, l + 1);
    return true;
  }

  // ELSE expression
  private static boolean case_expression_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "case_expression_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ELSE);
    r = r && expression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // COLLATE (collation | (LPAREN collation+ RPAREN))
  public static boolean collate_suffix(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "collate_suffix")) return false;
    if (!nextTokenIs(b, COLLATE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COLLATE);
    r = r && collate_suffix_1(b, l + 1);
    exit_section_(b, m, COLLATE_SUFFIX, r);
    return r;
  }

  // collation | (LPAREN collation+ RPAREN)
  private static boolean collate_suffix_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "collate_suffix_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = collation(b, l + 1);
    if (!r) r = collate_suffix_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // LPAREN collation+ RPAREN
  private static boolean collate_suffix_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "collate_suffix_1_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LPAREN);
    r = r && collate_suffix_1_1_1(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, null, r);
    return r;
  }

  // collation+
  private static boolean collate_suffix_1_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "collate_suffix_1_1_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = collation(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!collation(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "collate_suffix_1_1_1", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // NOUNICODE | NOCASE | NODIACRITICS | UNICODE | CASE | DIACRITICS
  public static boolean collation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "collation")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, COLLATION, "<collation>");
    r = consumeToken(b, NOUNICODE);
    if (!r) r = consumeToken(b, NOCASE);
    if (!r) r = consumeToken(b, NODIACRITICS);
    if (!r) r = consumeToken(b, UNICODE);
    if (!r) r = consumeToken(b, CASE);
    if (!r) r = consumeToken(b, DIACRITICS);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // OPEN_CURLY (dict-property (COMMA dict-property)*)?
  public static boolean dict_literal(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dict_literal")) return false;
    if (!nextTokenIs(b, OPEN_CURLY)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OPEN_CURLY);
    r = r && dict_literal_1(b, l + 1);
    exit_section_(b, m, DICT_LITERAL, r);
    return r;
  }

  // (dict-property (COMMA dict-property)*)?
  private static boolean dict_literal_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dict_literal_1")) return false;
    dict_literal_1_0(b, l + 1);
    return true;
  }

  // dict-property (COMMA dict-property)*
  private static boolean dict_literal_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dict_literal_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = dict_property(b, l + 1);
    r = r && dict_literal_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA dict-property)*
  private static boolean dict_literal_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dict_literal_1_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!dict_literal_1_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "dict_literal_1_0_1", c)) break;
    }
    return true;
  }

  // COMMA dict-property
  private static boolean dict_literal_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dict_literal_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && dict_property(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // string-literal SEMICOLON expression
  public static boolean dict_property(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dict_property")) return false;
    if (!nextTokenIs(b, "<dict property>", DQUOTE, QUOTE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, DICT_PROPERTY, "<dict property>");
    r = string_literal(b, l + 1);
    r = r && consumeToken(b, SEMICOLON);
    r = r && expression(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // DQUOTE double-quoted-string-character* DQUOTE
  public static boolean double_quoted_string(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "double_quoted_string")) return false;
    if (!nextTokenIs(b, DQUOTE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, DQUOTE);
    r = r && double_quoted_string_1(b, l + 1);
    r = r && consumeToken(b, DQUOTE);
    exit_section_(b, m, DOUBLE_QUOTED_STRING, r);
    return r;
  }

  // double-quoted-string-character*
  private static boolean double_quoted_string_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "double_quoted_string_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!double_quoted_string_character(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "double_quoted_string_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // ESCAPE_SEQUENCE
  //                                         | ESCAPED_DQUOTE
  //                                        | STRING_CHAR
  public static boolean double_quoted_string_character(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "double_quoted_string_character")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, DOUBLE_QUOTED_STRING_CHARACTER, "<double quoted string character>");
    r = consumeToken(b, ESCAPE_SEQUENCE);
    if (!r) r = consumeToken(b, ESCAPED_DQUOTE);
    if (!r) r = consumeToken(b, STRING_CHAR);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // BACKTICK IDENTIFIER BACKTICK
  public static boolean escaped_identifier(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "escaped_identifier")) return false;
    if (!nextTokenIs(b, BACKTICK)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, BACKTICK, IDENTIFIER, BACKTICK);
    exit_section_(b, m, ESCAPED_IDENTIFIER, r);
    return r;
  }

  /* ********************************************************** */
  // base-expression (DOT property-path)?
  public static boolean expr0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, EXPR_0, "<expr 0>");
    r = base_expression(b, l + 1);
    r = r && expr0_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (DOT property-path)?
  private static boolean expr0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr0_1")) return false;
    expr0_1_0(b, l + 1);
    return true;
  }

  // DOT property-path
  private static boolean expr0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, DOT);
    r = r && property_path(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // expr0 (op-prec_1 expr0)*
  public static boolean expr1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, EXPR_1, "<expr 1>");
    r = expr0(b, l + 1);
    r = r && expr1_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (op-prec_1 expr0)*
  private static boolean expr1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr1_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!expr1_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "expr1_1", c)) break;
    }
    return true;
  }

  // op-prec_1 expr0
  private static boolean expr1_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr1_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = op_prec_1(b, l + 1);
    r = r && expr0(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // expr1 (op-prec_2 expr1)*
  public static boolean expr2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr2")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, EXPR_2, "<expr 2>");
    r = expr1(b, l + 1);
    r = r && expr2_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (op-prec_2 expr1)*
  private static boolean expr2_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr2_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!expr2_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "expr2_1", c)) break;
    }
    return true;
  }

  // op-prec_2 expr1
  private static boolean expr2_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr2_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = op_prec_2(b, l + 1);
    r = r && expr1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // expr2 (op-prec_3 expr2)*
  public static boolean expr3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr3")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, EXPR_3, "<expr 3>");
    r = expr2(b, l + 1);
    r = r && expr3_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (op-prec_3 expr2)*
  private static boolean expr3_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr3_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!expr3_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "expr3_1", c)) break;
    }
    return true;
  }

  // op-prec_3 expr2
  private static boolean expr3_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr3_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = op_prec_3(b, l + 1);
    r = r && expr2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // expr3 (op-prec_4 expr3)*
  public static boolean expr4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr4")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, EXPR_4, "<expr 4>");
    r = expr3(b, l + 1);
    r = r && expr4_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (op-prec_4 expr3)*
  private static boolean expr4_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr4_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!expr4_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "expr4_1", c)) break;
    }
    return true;
  }

  // op-prec_4 expr3
  private static boolean expr4_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr4_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = op_prec_4(b, l + 1);
    r = r && expr3(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // expr4 (op-prec_5 expr4)*
  public static boolean expr5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr5")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, EXPR_5, "<expr 5>");
    r = expr4(b, l + 1);
    r = r && expr5_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (op-prec_5 expr4)*
  private static boolean expr5_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr5_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!expr5_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "expr5_1", c)) break;
    }
    return true;
  }

  // op-prec_5 expr4
  private static boolean expr5_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr5_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = op_prec_5(b, l + 1);
    r = r && expr4(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // (expr5 post-op-prec_6) |
  //     in-expression |
  //     like-expression |
  //     between-expression |
  //     (expr5 (op-prec_6 expr5)*)
  public static boolean expr6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr6")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, EXPR_6, "<expr 6>");
    r = expr6_0(b, l + 1);
    if (!r) r = in_expression(b, l + 1);
    if (!r) r = like_expression(b, l + 1);
    if (!r) r = between_expression(b, l + 1);
    if (!r) r = expr6_4(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // expr5 post-op-prec_6
  private static boolean expr6_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr6_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = expr5(b, l + 1);
    r = r && post_op_prec_6(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // expr5 (op-prec_6 expr5)*
  private static boolean expr6_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr6_4")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = expr5(b, l + 1);
    r = r && expr6_4_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (op-prec_6 expr5)*
  private static boolean expr6_4_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr6_4_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!expr6_4_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "expr6_4_1", c)) break;
    }
    return true;
  }

  // op-prec_6 expr5
  private static boolean expr6_4_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr6_4_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = op_prec_6(b, l + 1);
    r = r && expr5(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // expr6 (op-prec_7 expr6)*
  public static boolean expr7(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr7")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, EXPR_7, "<expr 7>");
    r = expr6(b, l + 1);
    r = r && expr7_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (op-prec_7 expr6)*
  private static boolean expr7_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr7_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!expr7_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "expr7_1", c)) break;
    }
    return true;
  }

  // op-prec_7 expr6
  private static boolean expr7_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr7_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = op_prec_7(b, l + 1);
    r = r && expr6(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // expr7 (op-prec_8 expr7)*
  public static boolean expr8(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr8")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, EXPR_8, "<expr 8>");
    r = expr7(b, l + 1);
    r = r && expr8_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (op-prec_8 expr7)*
  private static boolean expr8_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr8_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!expr8_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "expr8_1", c)) break;
    }
    return true;
  }

  // op-prec_8 expr7
  private static boolean expr8_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr8_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = op_prec_8(b, l + 1);
    r = r && expr7(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // expr8 collate-suffix?
  public static boolean expr9(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr9")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, EXPR_9, "<expr 9>");
    r = expr8(b, l + 1);
    r = r && expr9_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // collate-suffix?
  private static boolean expr9_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr9_1")) return false;
    collate_suffix(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // expr9
  public static boolean expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, EXPRESSION, "<expression>");
    r = expr9(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // FROM identifier-ref (AS? alias)
  public static boolean from_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "from_clause")) return false;
    if (!nextTokenIs(b, FROM)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, FROM);
    r = r && identifier_ref(b, l + 1);
    r = r && from_clause_2(b, l + 1);
    exit_section_(b, m, FROM_CLAUSE, r);
    return r;
  }

  // AS? alias
  private static boolean from_clause_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "from_clause_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = from_clause_2_0(b, l + 1);
    r = r && alias(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // AS?
  private static boolean from_clause_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "from_clause_2_0")) return false;
    consumeToken(b, AS);
    return true;
  }

  /* ********************************************************** */
  // builtin-function | ordinary-function
  public static boolean function_call(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "function_call")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FUNCTION_CALL, "<function call>");
    r = builtin_function(b, l + 1);
    if (!r) r = ordinary_function(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // identifier-ref
  public static boolean function_name(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "function_name")) return false;
    if (!nextTokenIs(b, "<function name>", BACKTICK, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FUNCTION_NAME, "<function name>");
    r = identifier_ref(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // GROUP BY expression (COMMA expression)*
  public static boolean group_by_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "group_by_clause")) return false;
    if (!nextTokenIs(b, GROUP)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, GROUP, BY);
    r = r && expression(b, l + 1);
    r = r && group_by_clause_3(b, l + 1);
    exit_section_(b, m, GROUP_BY_CLAUSE, r);
    return r;
  }

  // (COMMA expression)*
  private static boolean group_by_clause_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "group_by_clause_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!group_by_clause_3_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "group_by_clause_3", c)) break;
    }
    return true;
  }

  // COMMA expression
  private static boolean group_by_clause_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "group_by_clause_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && expression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // HAVING expression
  public static boolean having_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "having_clause")) return false;
    if (!nextTokenIs(b, HAVING)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, HAVING);
    r = r && expression(b, l + 1);
    exit_section_(b, m, HAVING_CLAUSE, r);
    return r;
  }

  /* ********************************************************** */
  // IDENTIFIER | escaped-identifier
  public static boolean identifier_ref(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "identifier_ref")) return false;
    if (!nextTokenIs(b, "<identifier ref>", BACKTICK, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, IDENTIFIER_REF, "<identifier ref>");
    r = consumeToken(b, IDENTIFIER);
    if (!r) r = escaped_identifier(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // expr5 NOT? IN (sub-statement | paren-expressions | array-literal)
  public static boolean in_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "in_expression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, IN_EXPRESSION, "<in expression>");
    r = expr5(b, l + 1);
    r = r && in_expression_1(b, l + 1);
    r = r && consumeToken(b, IN);
    r = r && in_expression_3(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // NOT?
  private static boolean in_expression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "in_expression_1")) return false;
    consumeToken(b, NOT);
    return true;
  }

  // sub-statement | paren-expressions | array-literal
  private static boolean in_expression_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "in_expression_3")) return false;
    boolean r;
    r = sub_statement(b, l + 1);
    if (!r) r = paren_expressions(b, l + 1);
    if (!r) r = array_literal(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // join-operator identifier-ref (ON expression)?
  public static boolean join_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "join_expression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, JOIN_EXPRESSION, "<join expression>");
    r = join_operator(b, l + 1);
    r = r && identifier_ref(b, l + 1);
    r = r && join_expression_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (ON expression)?
  private static boolean join_expression_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "join_expression_2")) return false;
    join_expression_2_0(b, l + 1);
    return true;
  }

  // ON expression
  private static boolean join_expression_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "join_expression_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ON);
    r = r && expression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ( (LEFT OUTER?) | INNER | CROSS ) JOIN
  public static boolean join_operator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "join_operator")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, JOIN_OPERATOR, "<join operator>");
    r = join_operator_0(b, l + 1);
    r = r && consumeToken(b, JOIN);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (LEFT OUTER?) | INNER | CROSS
  private static boolean join_operator_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "join_operator_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = join_operator_0_0(b, l + 1);
    if (!r) r = consumeToken(b, INNER);
    if (!r) r = consumeToken(b, CROSS);
    exit_section_(b, m, null, r);
    return r;
  }

  // LEFT OUTER?
  private static boolean join_operator_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "join_operator_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LEFT);
    r = r && join_operator_0_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // OUTER?
  private static boolean join_operator_0_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "join_operator_0_0_1")) return false;
    consumeToken(b, OUTER);
    return true;
  }

  /* ********************************************************** */
  // expr5 NOT? LIKE expr5
  public static boolean like_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "like_expression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LIKE_EXPRESSION, "<like expression>");
    r = expr5(b, l + 1);
    r = r && like_expression_1(b, l + 1);
    r = r && consumeToken(b, LIKE);
    r = r && expr5(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // NOT?
  private static boolean like_expression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "like_expression_1")) return false;
    consumeToken(b, NOT);
    return true;
  }

  /* ********************************************************** */
  // LIMIT expression
  public static boolean limit_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "limit_clause")) return false;
    if (!nextTokenIs(b, LIMIT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LIMIT);
    r = r && expression(b, l + 1);
    exit_section_(b, m, LIMIT_CLAUSE, r);
    return r;
  }

  /* ********************************************************** */
  // NBR | INTGR | boolean-literal | string-literal | NULL | MISSING
  public static boolean literal(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literal")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LITERAL, "<literal>");
    r = consumeToken(b, NBR);
    if (!r) r = consumeToken(b, INTGR);
    if (!r) r = boolean_literal(b, l + 1);
    if (!r) r = string_literal(b, l + 1);
    if (!r) r = consumeToken(b, NULL);
    if (!r) r = consumeToken(b, MISSING);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // select-statement | select-results
  public static boolean n1ql_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "n1ql_statement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, N_1_QL_STATEMENT, "<n 1 ql statement>");
    r = select_statement(b, l + 1);
    if (!r) r = select_results(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // OFFSET expression
  public static boolean offset_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "offset_clause")) return false;
    if (!nextTokenIs(b, OFFSET)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OFFSET);
    r = r && expression(b, l + 1);
    exit_section_(b, m, OFFSET_CLAUSE, r);
    return r;
  }

  /* ********************************************************** */
  // DOUBLE_PIPE
  public static boolean op_prec_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_prec_1")) return false;
    if (!nextTokenIs(b, DOUBLE_PIPE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, DOUBLE_PIPE);
    exit_section_(b, m, OP_PREC_1, r);
    return r;
  }

  /* ********************************************************** */
  // ASTERISK | PERCENT | SLASH
  public static boolean op_prec_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_prec_2")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_PREC_2, "<op prec 2>");
    r = consumeToken(b, ASTERISK);
    if (!r) r = consumeToken(b, PERCENT);
    if (!r) r = consumeToken(b, SLASH);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // MINUS_SIGN | PLUS
  public static boolean op_prec_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_prec_3")) return false;
    if (!nextTokenIs(b, "<op prec 3>", MINUS_SIGN, PLUS)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_PREC_3, "<op prec 3>");
    r = consumeToken(b, MINUS_SIGN);
    if (!r) r = consumeToken(b, PLUS);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // BINARY_SHIFT_LEFT | BINARY_SHIFT_RIGHT | AMPERSAND | PIPE
  public static boolean op_prec_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_prec_4")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_PREC_4, "<op prec 4>");
    r = consumeToken(b, BINARY_SHIFT_LEFT);
    if (!r) r = consumeToken(b, BINARY_SHIFT_RIGHT);
    if (!r) r = consumeToken(b, AMPERSAND);
    if (!r) r = consumeToken(b, PIPE);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // LESSTHAN_OR_EQUAL | LESSTHAN | MORETHAN_OR_EQUAL | MORETHAN
  public static boolean op_prec_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_prec_5")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_PREC_5, "<op prec 5>");
    r = consumeToken(b, LESSTHAN_OR_EQUAL);
    if (!r) r = consumeToken(b, LESSTHAN);
    if (!r) r = consumeToken(b, MORETHAN_OR_EQUAL);
    if (!r) r = consumeToken(b, MORETHAN);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (DOUBLE_EQUAL | EQUAL) |
  //     (LESSTHAN_OR_MORETHAN | NOT_EQUAL) |
  //     (IS NOT) |
  //     IS
  public static boolean op_prec_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_prec_6")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_PREC_6, "<op prec 6>");
    r = op_prec_6_0(b, l + 1);
    if (!r) r = op_prec_6_1(b, l + 1);
    if (!r) r = op_prec_6_2(b, l + 1);
    if (!r) r = consumeToken(b, IS);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // DOUBLE_EQUAL | EQUAL
  private static boolean op_prec_6_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_prec_6_0")) return false;
    boolean r;
    r = consumeToken(b, DOUBLE_EQUAL);
    if (!r) r = consumeToken(b, EQUAL);
    return r;
  }

  // LESSTHAN_OR_MORETHAN | NOT_EQUAL
  private static boolean op_prec_6_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_prec_6_1")) return false;
    boolean r;
    r = consumeToken(b, LESSTHAN_OR_MORETHAN);
    if (!r) r = consumeToken(b, NOT_EQUAL);
    return r;
  }

  // IS NOT
  private static boolean op_prec_6_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_prec_6_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, IS, NOT);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // AND
  public static boolean op_prec_7(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_prec_7")) return false;
    if (!nextTokenIs(b, AND)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, AND);
    exit_section_(b, m, OP_PREC_7, r);
    return r;
  }

  /* ********************************************************** */
  // OR
  public static boolean op_prec_8(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_prec_8")) return false;
    if (!nextTokenIs(b, OR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OR);
    exit_section_(b, m, OP_PREC_8, r);
    return r;
  }

  /* ********************************************************** */
  // MINUS_SIGN | PLUS | NOT
  public static boolean op_prefix(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "op_prefix")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OP_PREFIX, "<op prefix>");
    r = consumeToken(b, MINUS_SIGN);
    if (!r) r = consumeToken(b, PLUS);
    if (!r) r = consumeToken(b, NOT);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ORDER BY ordering-clause (COMMA ordering-clause)*
  public static boolean order_by_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "order_by_clause")) return false;
    if (!nextTokenIs(b, ORDER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, ORDER, BY);
    r = r && ordering_clause(b, l + 1);
    r = r && order_by_clause_3(b, l + 1);
    exit_section_(b, m, ORDER_BY_CLAUSE, r);
    return r;
  }

  // (COMMA ordering-clause)*
  private static boolean order_by_clause_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "order_by_clause_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!order_by_clause_3_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "order_by_clause_3", c)) break;
    }
    return true;
  }

  // COMMA ordering-clause
  private static boolean order_by_clause_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "order_by_clause_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && ordering_clause(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ASC | DESC
  public static boolean order_operator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "order_operator")) return false;
    if (!nextTokenIs(b, "<order operator>", ASC, DESC)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ORDER_OPERATOR, "<order operator>");
    r = consumeToken(b, ASC);
    if (!r) r = consumeToken(b, DESC);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // expression order-operator?
  public static boolean ordering_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ordering_clause")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ORDERING_CLAUSE, "<ordering clause>");
    r = expression(b, l + 1);
    r = r && ordering_clause_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // order-operator?
  private static boolean ordering_clause_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ordering_clause_1")) return false;
    order_operator(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // function-name LPAREN ( expr ( COMMA expr )* )? RPAREN
  public static boolean ordinary_function(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ordinary_function")) return false;
    if (!nextTokenIs(b, "<ordinary function>", BACKTICK, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ORDINARY_FUNCTION, "<ordinary function>");
    r = function_name(b, l + 1);
    r = r && consumeToken(b, LPAREN);
    r = r && ordinary_function_2(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( expr ( COMMA expr )* )?
  private static boolean ordinary_function_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ordinary_function_2")) return false;
    ordinary_function_2_0(b, l + 1);
    return true;
  }

  // expr ( COMMA expr )*
  private static boolean ordinary_function_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ordinary_function_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EXPR);
    r = r && ordinary_function_2_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( COMMA expr )*
  private static boolean ordinary_function_2_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ordinary_function_2_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!ordinary_function_2_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "ordinary_function_2_0_1", c)) break;
    }
    return true;
  }

  // COMMA expr
  private static boolean ordinary_function_2_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ordinary_function_2_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, COMMA, EXPR);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // LPAREN (expression (COMMA expression)*)? RPAREN
  public static boolean paren_expressions(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "paren_expressions")) return false;
    if (!nextTokenIs(b, LPAREN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LPAREN);
    r = r && paren_expressions_1(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, PAREN_EXPRESSIONS, r);
    return r;
  }

  // (expression (COMMA expression)*)?
  private static boolean paren_expressions_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "paren_expressions_1")) return false;
    paren_expressions_1_0(b, l + 1);
    return true;
  }

  // expression (COMMA expression)*
  private static boolean paren_expressions_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "paren_expressions_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = expression(b, l + 1);
    r = r && paren_expressions_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA expression)*
  private static boolean paren_expressions_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "paren_expressions_1_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!paren_expressions_1_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "paren_expressions_1_0_1", c)) break;
    }
    return true;
  }

  // COMMA expression
  private static boolean paren_expressions_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "paren_expressions_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && expression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // (NOT NULL) | (IS NOT? NULL) | (IS NOT? MISSING) | (IS NOT? VALUED)
  public static boolean post_op_prec_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "post_op_prec_6")) return false;
    if (!nextTokenIs(b, "<post op prec 6>", IS, NOT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, POST_OP_PREC_6, "<post op prec 6>");
    r = post_op_prec_6_0(b, l + 1);
    if (!r) r = post_op_prec_6_1(b, l + 1);
    if (!r) r = post_op_prec_6_2(b, l + 1);
    if (!r) r = post_op_prec_6_3(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // NOT NULL
  private static boolean post_op_prec_6_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "post_op_prec_6_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, NOT, NULL);
    exit_section_(b, m, null, r);
    return r;
  }

  // IS NOT? NULL
  private static boolean post_op_prec_6_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "post_op_prec_6_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IS);
    r = r && post_op_prec_6_1_1(b, l + 1);
    r = r && consumeToken(b, NULL);
    exit_section_(b, m, null, r);
    return r;
  }

  // NOT?
  private static boolean post_op_prec_6_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "post_op_prec_6_1_1")) return false;
    consumeToken(b, NOT);
    return true;
  }

  // IS NOT? MISSING
  private static boolean post_op_prec_6_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "post_op_prec_6_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IS);
    r = r && post_op_prec_6_2_1(b, l + 1);
    r = r && consumeToken(b, MISSING);
    exit_section_(b, m, null, r);
    return r;
  }

  // NOT?
  private static boolean post_op_prec_6_2_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "post_op_prec_6_2_1")) return false;
    consumeToken(b, NOT);
    return true;
  }

  // IS NOT? VALUED
  private static boolean post_op_prec_6_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "post_op_prec_6_3")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IS);
    r = r && post_op_prec_6_3_1(b, l + 1);
    r = r && consumeToken(b, VALUED);
    exit_section_(b, m, null, r);
    return r;
  }

  // NOT?
  private static boolean post_op_prec_6_3_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "post_op_prec_6_3_1")) return false;
    consumeToken(b, NOT);
    return true;
  }

  /* ********************************************************** */
  // ASTERISK | alias | property-path
  public static boolean property(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "property")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PROPERTY, "<property>");
    r = consumeToken(b, ASTERISK);
    if (!r) r = alias(b, l + 1);
    if (!r) r = property_path(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // identifier-ref ((DOT identifier-ref) | (LBRACKET INTGR RBRACKET))*
  public static boolean property_path(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "property_path")) return false;
    if (!nextTokenIs(b, "<property path>", BACKTICK, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PROPERTY_PATH, "<property path>");
    r = identifier_ref(b, l + 1);
    r = r && property_path_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ((DOT identifier-ref) | (LBRACKET INTGR RBRACKET))*
  private static boolean property_path_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "property_path_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!property_path_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "property_path_1", c)) break;
    }
    return true;
  }

  // (DOT identifier-ref) | (LBRACKET INTGR RBRACKET)
  private static boolean property_path_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "property_path_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = property_path_1_0_0(b, l + 1);
    if (!r) r = property_path_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // DOT identifier-ref
  private static boolean property_path_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "property_path_1_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, DOT);
    r = r && identifier_ref(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // LBRACKET INTGR RBRACKET
  private static boolean property_path_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "property_path_1_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, LBRACKET, INTGR, RBRACKET);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // n1ql-statement (COLON n1ql-statement)*
  static boolean script(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "script")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = n1ql_statement(b, l + 1);
    r = r && script_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COLON n1ql-statement)*
  private static boolean script_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "script_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!script_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "script_1", c)) break;
    }
    return true;
  }

  // COLON n1ql-statement
  private static boolean script_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "script_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COLON);
    r = r && n1ql_statement(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // expression (AS? alias) (join-expression)*
  public static boolean select_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "select_expression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SELECT_EXPRESSION, "<select expression>");
    r = expression(b, l + 1);
    r = r && select_expression_1(b, l + 1);
    r = r && select_expression_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // AS? alias
  private static boolean select_expression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "select_expression_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = select_expression_1_0(b, l + 1);
    r = r && alias(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // AS?
  private static boolean select_expression_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "select_expression_1_0")) return false;
    consumeToken(b, AS);
    return true;
  }

  // (join-expression)*
  private static boolean select_expression_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "select_expression_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!select_expression_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "select_expression_2", c)) break;
    }
    return true;
  }

  // (join-expression)
  private static boolean select_expression_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "select_expression_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = join_expression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // select-expression (COMMA select-expression)*
  public static boolean select_results(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "select_results")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SELECT_RESULTS, "<select results>");
    r = select_expression(b, l + 1);
    r = r && select_results_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (COMMA select-expression)*
  private static boolean select_results_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "select_results_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!select_results_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "select_results_1", c)) break;
    }
    return true;
  }

  // COMMA select-expression
  private static boolean select_results_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "select_results_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && select_expression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // SELECT (DISTINCT | ALL) select-results from-clause? where-clause?
  //     (group-by-clause? having-clause?)? order-by-clause?
  //     ((limit-clause offset-clause?) | (offset-clause limit-clause?))?
  public static boolean select_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "select_statement")) return false;
    if (!nextTokenIs(b, SELECT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, SELECT);
    r = r && select_statement_1(b, l + 1);
    r = r && select_results(b, l + 1);
    r = r && select_statement_3(b, l + 1);
    r = r && select_statement_4(b, l + 1);
    r = r && select_statement_5(b, l + 1);
    r = r && select_statement_6(b, l + 1);
    r = r && select_statement_7(b, l + 1);
    exit_section_(b, m, SELECT_STATEMENT, r);
    return r;
  }

  // DISTINCT | ALL
  private static boolean select_statement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "select_statement_1")) return false;
    boolean r;
    r = consumeToken(b, DISTINCT);
    if (!r) r = consumeToken(b, ALL);
    return r;
  }

  // from-clause?
  private static boolean select_statement_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "select_statement_3")) return false;
    from_clause(b, l + 1);
    return true;
  }

  // where-clause?
  private static boolean select_statement_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "select_statement_4")) return false;
    where_clause(b, l + 1);
    return true;
  }

  // (group-by-clause? having-clause?)?
  private static boolean select_statement_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "select_statement_5")) return false;
    select_statement_5_0(b, l + 1);
    return true;
  }

  // group-by-clause? having-clause?
  private static boolean select_statement_5_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "select_statement_5_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = select_statement_5_0_0(b, l + 1);
    r = r && select_statement_5_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // group-by-clause?
  private static boolean select_statement_5_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "select_statement_5_0_0")) return false;
    group_by_clause(b, l + 1);
    return true;
  }

  // having-clause?
  private static boolean select_statement_5_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "select_statement_5_0_1")) return false;
    having_clause(b, l + 1);
    return true;
  }

  // order-by-clause?
  private static boolean select_statement_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "select_statement_6")) return false;
    order_by_clause(b, l + 1);
    return true;
  }

  // ((limit-clause offset-clause?) | (offset-clause limit-clause?))?
  private static boolean select_statement_7(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "select_statement_7")) return false;
    select_statement_7_0(b, l + 1);
    return true;
  }

  // (limit-clause offset-clause?) | (offset-clause limit-clause?)
  private static boolean select_statement_7_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "select_statement_7_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = select_statement_7_0_0(b, l + 1);
    if (!r) r = select_statement_7_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // limit-clause offset-clause?
  private static boolean select_statement_7_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "select_statement_7_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = limit_clause(b, l + 1);
    r = r && select_statement_7_0_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // offset-clause?
  private static boolean select_statement_7_0_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "select_statement_7_0_0_1")) return false;
    offset_clause(b, l + 1);
    return true;
  }

  // offset-clause limit-clause?
  private static boolean select_statement_7_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "select_statement_7_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = offset_clause(b, l + 1);
    r = r && select_statement_7_0_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // limit-clause?
  private static boolean select_statement_7_0_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "select_statement_7_0_1_1")) return false;
    limit_clause(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // QUOTE single-quoted-string-character* QUOTE
  public static boolean single_quoted_string(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "single_quoted_string")) return false;
    if (!nextTokenIs(b, QUOTE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, QUOTE);
    r = r && single_quoted_string_1(b, l + 1);
    r = r && consumeToken(b, QUOTE);
    exit_section_(b, m, SINGLE_QUOTED_STRING, r);
    return r;
  }

  // single-quoted-string-character*
  private static boolean single_quoted_string_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "single_quoted_string_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!single_quoted_string_character(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "single_quoted_string_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // ESCAPE_SEQUENCE
  //                                         | ESCAPED_QUOTE
  //                                         | STRING_CHAR
  public static boolean single_quoted_string_character(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "single_quoted_string_character")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SINGLE_QUOTED_STRING_CHARACTER, "<single quoted string character>");
    r = consumeToken(b, ESCAPE_SEQUENCE);
    if (!r) r = consumeToken(b, ESCAPED_QUOTE);
    if (!r) r = consumeToken(b, STRING_CHAR);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // double-quoted-string | single-quoted-string
  public static boolean string_literal(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "string_literal")) return false;
    if (!nextTokenIs(b, "<string literal>", DQUOTE, QUOTE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, STRING_LITERAL, "<string literal>");
    r = double_quoted_string(b, l + 1);
    if (!r) r = single_quoted_string(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // LPAREN select-statement RPAREN
  public static boolean sub_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "sub_statement")) return false;
    if (!nextTokenIs(b, LPAREN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LPAREN);
    r = r && select_statement(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, SUB_STATEMENT, r);
    return r;
  }

  /* ********************************************************** */
  // WHERE expression
  public static boolean where_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "where_clause")) return false;
    if (!nextTokenIs(b, WHERE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, WHERE);
    r = r && expression(b, l + 1);
    exit_section_(b, m, WHERE_CLAUSE, r);
    return r;
  }

}

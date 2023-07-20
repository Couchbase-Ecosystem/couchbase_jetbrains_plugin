// This is a generated file. Not intended for manual editing.
package generated;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static generated.GeneratedTypes.*;
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
    return sequence(b, l + 1);
  }

  /* ********************************************************** */
  // ADVISE INDEX? ( select-statement | update-statement | delete-statement | merge-statement )
  public static boolean advise_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "advise_statement")) return false;
    if (!nextTokenIs(b, ADVISE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ADVISE);
    r = r && advise_statement_1(b, l + 1);
    r = r && advise_statement_2(b, l + 1);
    exit_section_(b, m, ADVISE_STATEMENT, r);
    return r;
  }

  // INDEX?
  private static boolean advise_statement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "advise_statement_1")) return false;
    consumeToken(b, INDEX);
    return true;
  }

  // select-statement | update-statement | delete-statement | merge-statement
  private static boolean advise_statement_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "advise_statement_2")) return false;
    boolean r;
    r = select_statement(b, l + 1);
    if (!r) r = update_statement(b, l + 1);
    if (!r) r = delete_statement(b, l + 1);
    if (!r) r = merge_statement(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // aggregate-function-name LPAREN ( aggregate-quantifier? expr |
  //                        ( path DOT )? ASTERISK ) RPAREN filter-clause? over-clause?
  public static boolean aggregate_function(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "aggregate_function")) return false;
    if (!nextTokenIs(b, "<aggregate function>", ESCAPED_IDENTIFIER, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, AGGREGATE_FUNCTION, "<aggregate function>");
    r = aggregate_function_name(b, l + 1);
    r = r && consumeToken(b, LPAREN);
    r = r && aggregate_function_2(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    r = r && aggregate_function_4(b, l + 1);
    r = r && aggregate_function_5(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // aggregate-quantifier? expr |
  //                        ( path DOT )? ASTERISK
  private static boolean aggregate_function_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "aggregate_function_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = aggregate_function_2_0(b, l + 1);
    if (!r) r = aggregate_function_2_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // aggregate-quantifier? expr
  private static boolean aggregate_function_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "aggregate_function_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = aggregate_function_2_0_0(b, l + 1);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // aggregate-quantifier?
  private static boolean aggregate_function_2_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "aggregate_function_2_0_0")) return false;
    aggregate_quantifier(b, l + 1);
    return true;
  }

  // ( path DOT )? ASTERISK
  private static boolean aggregate_function_2_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "aggregate_function_2_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = aggregate_function_2_1_0(b, l + 1);
    r = r && consumeToken(b, ASTERISK);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( path DOT )?
  private static boolean aggregate_function_2_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "aggregate_function_2_1_0")) return false;
    aggregate_function_2_1_0_0(b, l + 1);
    return true;
  }

  // path DOT
  private static boolean aggregate_function_2_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "aggregate_function_2_1_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = path(b, l + 1);
    r = r && consumeToken(b, DOT);
    exit_section_(b, m, null, r);
    return r;
  }

  // filter-clause?
  private static boolean aggregate_function_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "aggregate_function_4")) return false;
    filter_clause(b, l + 1);
    return true;
  }

  // over-clause?
  private static boolean aggregate_function_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "aggregate_function_5")) return false;
    over_clause(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // identifier-ref
  public static boolean aggregate_function_name(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "aggregate_function_name")) return false;
    if (!nextTokenIs(b, "<aggregate function name>", ESCAPED_IDENTIFIER, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, AGGREGATE_FUNCTION_NAME, "<aggregate function name>");
    r = identifier_ref(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ALL | DISTINCT
  public static boolean aggregate_quantifier(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "aggregate_quantifier")) return false;
    if (!nextTokenIs(b, "<aggregate quantifier>", ALL, DISTINCT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, AGGREGATE_QUANTIFIER, "<aggregate quantifier>");
    r = consumeToken(b, ALL);
    if (!r) r = consumeToken(b, DISTINCT);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // identifier-ref
  public static boolean alias(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "alias")) return false;
    if (!nextTokenIs(b, "<alias>", ESCAPED_IDENTIFIER, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ALIAS, "<alias>");
    r = identifier_ref(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ALTER INDEX ( index-path DOT index-name | index-name ON keyspace-ref )
  //                 index-using? index-with
  public static boolean alter_index(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "alter_index")) return false;
    if (!nextTokenIs(b, ALTER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, ALTER, INDEX);
    r = r && alter_index_2(b, l + 1);
    r = r && alter_index_3(b, l + 1);
    r = r && index_with(b, l + 1);
    exit_section_(b, m, ALTER_INDEX, r);
    return r;
  }

  // index-path DOT index-name | index-name ON keyspace-ref
  private static boolean alter_index_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "alter_index_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = alter_index_2_0(b, l + 1);
    if (!r) r = alter_index_2_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // index-path DOT index-name
  private static boolean alter_index_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "alter_index_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = index_path(b, l + 1);
    r = r && consumeToken(b, DOT);
    r = r && index_name(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // index-name ON keyspace-ref
  private static boolean alter_index_2_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "alter_index_2_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = index_name(b, l + 1);
    r = r && consumeToken(b, ON);
    r = r && keyspace_ref(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // index-using?
  private static boolean alter_index_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "alter_index_3")) return false;
    index_using(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // AND cond
  public static boolean and_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "and_expr")) return false;
    if (!nextTokenIs(b, AND)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, AND);
    r = r && cond(b, l + 1);
    exit_section_(b, m, AND_EXPR, r);
    return r;
  }

  /* ********************************************************** */
  // use-hash-term | use-nl-term
  public static boolean ansi_hint_terms(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ansi_hint_terms")) return false;
    if (!nextTokenIs(b, "<ansi hint terms>", HASH, NL)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ANSI_HINT_TERMS, "<ansi hint terms>");
    r = use_hash_term(b, l + 1);
    if (!r) r = use_nl_term(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ansi-join-type? JOIN ansi-join-rhs ansi-join-predicate
  public static boolean ansi_join_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ansi_join_clause")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ANSI_JOIN_CLAUSE, "<ansi join clause>");
    r = ansi_join_clause_0(b, l + 1);
    r = r && consumeToken(b, JOIN);
    r = r && ansi_join_rhs(b, l + 1);
    r = r && ansi_join_predicate(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ansi-join-type?
  private static boolean ansi_join_clause_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ansi_join_clause_0")) return false;
    ansi_join_type(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // use-hash-hint | use-nl-hint | multiple-hints
  public static boolean ansi_join_hints(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ansi_join_hints")) return false;
    if (!nextTokenIs(b, USE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = use_hash_hint(b, l + 1);
    if (!r) r = use_nl_hint(b, l + 1);
    if (!r) r = multiple_hints(b, l + 1);
    exit_section_(b, m, ANSI_JOIN_HINTS, r);
    return r;
  }

  /* ********************************************************** */
  // ON expr
  public static boolean ansi_join_predicate(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ansi_join_predicate")) return false;
    if (!nextTokenIs(b, ON)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ON);
    r = r && expr(b, l + 1);
    exit_section_(b, m, ANSI_JOIN_PREDICATE, r);
    return r;
  }

  /* ********************************************************** */
  // rhs-keyspace | rhs-subquery | rhs-generic
  public static boolean ansi_join_rhs(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ansi_join_rhs")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ANSI_JOIN_RHS, "<ansi join rhs>");
    r = rhs_keyspace(b, l + 1);
    if (!r) r = rhs_subquery(b, l + 1);
    if (!r) r = rhs_generic(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // INNER | ( LEFT | RIGHT ) OUTER?
  public static boolean ansi_join_type(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ansi_join_type")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ANSI_JOIN_TYPE, "<ansi join type>");
    r = consumeToken(b, INNER);
    if (!r) r = ansi_join_type_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( LEFT | RIGHT ) OUTER?
  private static boolean ansi_join_type_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ansi_join_type_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ansi_join_type_1_0(b, l + 1);
    r = r && ansi_join_type_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // LEFT | RIGHT
  private static boolean ansi_join_type_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ansi_join_type_1_0")) return false;
    boolean r;
    r = consumeToken(b, LEFT);
    if (!r) r = consumeToken(b, RIGHT);
    return r;
  }

  // OUTER?
  private static boolean ansi_join_type_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ansi_join_type_1_1")) return false;
    consumeToken(b, OUTER);
    return true;
  }

  /* ********************************************************** */
  // target-keyspace use-index-clause USING ansi-merge-source
  //                ansi-merge-predicate ansi-merge-actions
  public static boolean ansi_merge(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ansi_merge")) return false;
    if (!nextTokenIs(b, "<ansi merge>", ESCAPED_IDENTIFIER, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ANSI_MERGE, "<ansi merge>");
    r = target_keyspace(b, l + 1);
    r = r && use_index_clause(b, l + 1);
    r = r && consumeToken(b, USING);
    r = r && ansi_merge_source(b, l + 1);
    r = r && ansi_merge_predicate(b, l + 1);
    r = r && ansi_merge_actions(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // merge-update? merge-delete? ansi-merge-insert?
  public static boolean ansi_merge_actions(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ansi_merge_actions")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ANSI_MERGE_ACTIONS, "<ansi merge actions>");
    r = ansi_merge_actions_0(b, l + 1);
    r = r && ansi_merge_actions_1(b, l + 1);
    r = r && ansi_merge_actions_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // merge-update?
  private static boolean ansi_merge_actions_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ansi_merge_actions_0")) return false;
    merge_update(b, l + 1);
    return true;
  }

  // merge-delete?
  private static boolean ansi_merge_actions_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ansi_merge_actions_1")) return false;
    merge_delete(b, l + 1);
    return true;
  }

  // ansi-merge-insert?
  private static boolean ansi_merge_actions_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ansi_merge_actions_2")) return false;
    ansi_merge_insert(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // WHEN NOT MATCHED THEN INSERT LPAREN KEY? key-expr
  //                       ( COMMA VALUE? value-expr )? ( COMMA OPTIONS? options )? RPAREN where-clause?
  public static boolean ansi_merge_insert(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ansi_merge_insert")) return false;
    if (!nextTokenIs(b, WHEN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, WHEN, NOT, MATCHED, THEN, INSERT, LPAREN);
    r = r && ansi_merge_insert_6(b, l + 1);
    r = r && key_expr(b, l + 1);
    r = r && ansi_merge_insert_8(b, l + 1);
    r = r && ansi_merge_insert_9(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    r = r && ansi_merge_insert_11(b, l + 1);
    exit_section_(b, m, ANSI_MERGE_INSERT, r);
    return r;
  }

  // KEY?
  private static boolean ansi_merge_insert_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ansi_merge_insert_6")) return false;
    consumeToken(b, KEY);
    return true;
  }

  // ( COMMA VALUE? value-expr )?
  private static boolean ansi_merge_insert_8(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ansi_merge_insert_8")) return false;
    ansi_merge_insert_8_0(b, l + 1);
    return true;
  }

  // COMMA VALUE? value-expr
  private static boolean ansi_merge_insert_8_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ansi_merge_insert_8_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && ansi_merge_insert_8_0_1(b, l + 1);
    r = r && value_expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // VALUE?
  private static boolean ansi_merge_insert_8_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ansi_merge_insert_8_0_1")) return false;
    consumeToken(b, VALUE);
    return true;
  }

  // ( COMMA OPTIONS? options )?
  private static boolean ansi_merge_insert_9(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ansi_merge_insert_9")) return false;
    ansi_merge_insert_9_0(b, l + 1);
    return true;
  }

  // COMMA OPTIONS? options
  private static boolean ansi_merge_insert_9_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ansi_merge_insert_9_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && ansi_merge_insert_9_0_1(b, l + 1);
    r = r && consumeToken(b, OPTIONS);
    exit_section_(b, m, null, r);
    return r;
  }

  // OPTIONS?
  private static boolean ansi_merge_insert_9_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ansi_merge_insert_9_0_1")) return false;
    consumeToken(b, OPTIONS);
    return true;
  }

  // where-clause?
  private static boolean ansi_merge_insert_11(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ansi_merge_insert_11")) return false;
    where_clause(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // ON expr
  public static boolean ansi_merge_predicate(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ansi_merge_predicate")) return false;
    if (!nextTokenIs(b, ON)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ON);
    r = r && expr(b, l + 1);
    exit_section_(b, m, ANSI_MERGE_PREDICATE, r);
    return r;
  }

  /* ********************************************************** */
  // ( merge-source-keyspace | merge-source-subquery | merge-source-expr )
  //                       ansi-join-hints?
  public static boolean ansi_merge_source(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ansi_merge_source")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ANSI_MERGE_SOURCE, "<ansi merge source>");
    r = ansi_merge_source_0(b, l + 1);
    r = r && ansi_merge_source_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // merge-source-keyspace | merge-source-subquery | merge-source-expr
  private static boolean ansi_merge_source_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ansi_merge_source_0")) return false;
    boolean r;
    r = merge_source_keyspace(b, l + 1);
    if (!r) r = merge_source_subquery(b, l + 1);
    if (!r) r = merge_source_expr(b, l + 1);
    return r;
  }

  // ansi-join-hints?
  private static boolean ansi_merge_source_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ansi_merge_source_1")) return false;
    ansi_join_hints(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // ansi-nest-type? NEST ansi-nest-rhs ansi-nest-predicate
  public static boolean ansi_nest_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ansi_nest_clause")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ANSI_NEST_CLAUSE, "<ansi nest clause>");
    r = ansi_nest_clause_0(b, l + 1);
    r = r && consumeToken(b, NEST);
    r = r && ansi_nest_rhs(b, l + 1);
    r = r && ansi_nest_predicate(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ansi-nest-type?
  private static boolean ansi_nest_clause_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ansi_nest_clause_0")) return false;
    ansi_nest_type(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // ON expr
  public static boolean ansi_nest_predicate(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ansi_nest_predicate")) return false;
    if (!nextTokenIs(b, ON)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ON);
    r = r && expr(b, l + 1);
    exit_section_(b, m, ANSI_NEST_PREDICATE, r);
    return r;
  }

  /* ********************************************************** */
  // keyspace-ref ( AS? alias )?
  public static boolean ansi_nest_rhs(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ansi_nest_rhs")) return false;
    if (!nextTokenIs(b, "<ansi nest rhs>", ESCAPED_IDENTIFIER, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ANSI_NEST_RHS, "<ansi nest rhs>");
    r = keyspace_ref(b, l + 1);
    r = r && ansi_nest_rhs_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( AS? alias )?
  private static boolean ansi_nest_rhs_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ansi_nest_rhs_1")) return false;
    ansi_nest_rhs_1_0(b, l + 1);
    return true;
  }

  // AS? alias
  private static boolean ansi_nest_rhs_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ansi_nest_rhs_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ansi_nest_rhs_1_0_0(b, l + 1);
    r = r && alias(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // AS?
  private static boolean ansi_nest_rhs_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ansi_nest_rhs_1_0_0")) return false;
    consumeToken(b, AS);
    return true;
  }

  /* ********************************************************** */
  // INNER | ( LEFT OUTER? )
  public static boolean ansi_nest_type(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ansi_nest_type")) return false;
    if (!nextTokenIs(b, "<ansi nest type>", INNER, LEFT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ANSI_NEST_TYPE, "<ansi nest type>");
    r = consumeToken(b, INNER);
    if (!r) r = ansi_nest_type_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // LEFT OUTER?
  private static boolean ansi_nest_type_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ansi_nest_type_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LEFT);
    r = r && ansi_nest_type_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // OUTER?
  private static boolean ansi_nest_type_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ansi_nest_type_1_1")) return false;
    consumeToken(b, OUTER);
    return true;
  }

  /* ********************************************************** */
  // PLUS expr |
  //                     ASTERISK expr |
  //                     SLASH expr |
  //                     PERCENT expr |
  //                     MINUS_SIGN expr
  public static boolean arithmetic_term(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arithmetic_term")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ARITHMETIC_TERM, "<arithmetic term>");
    r = arithmetic_term_0(b, l + 1);
    if (!r) r = arithmetic_term_1(b, l + 1);
    if (!r) r = arithmetic_term_2(b, l + 1);
    if (!r) r = arithmetic_term_3(b, l + 1);
    if (!r) r = arithmetic_term_4(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // PLUS expr
  private static boolean arithmetic_term_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arithmetic_term_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, PLUS);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ASTERISK expr
  private static boolean arithmetic_term_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arithmetic_term_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ASTERISK);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // SLASH expr
  private static boolean arithmetic_term_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arithmetic_term_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, SLASH);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // PERCENT expr
  private static boolean arithmetic_term_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arithmetic_term_3")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, PERCENT);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // MINUS_SIGN expr
  private static boolean arithmetic_term_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arithmetic_term_4")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, MINUS_SIGN);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // full-array-expr | simple-array-expr
  public static boolean array_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array_expr")) return false;
    if (!nextTokenIs(b, "<array expr>", ALL, DISTINCT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ARRAY_EXPR, "<array expr>");
    r = full_array_expr(b, l + 1);
    if (!r) r = simple_array_expr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ( BEGIN | START ) ( WORK | TRAN | TRANSACTION )
  //                       ( ISOLATION LEVEL READ COMMITTED )?
  public static boolean begin_transaction(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "begin_transaction")) return false;
    if (!nextTokenIs(b, "<begin transaction>", BEGIN, START)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BEGIN_TRANSACTION, "<begin transaction>");
    r = begin_transaction_0(b, l + 1);
    r = r && begin_transaction_1(b, l + 1);
    r = r && begin_transaction_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // BEGIN | START
  private static boolean begin_transaction_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "begin_transaction_0")) return false;
    boolean r;
    r = consumeToken(b, BEGIN);
    if (!r) r = consumeToken(b, START);
    return r;
  }

  // WORK | TRAN | TRANSACTION
  private static boolean begin_transaction_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "begin_transaction_1")) return false;
    boolean r;
    r = consumeToken(b, WORK);
    if (!r) r = consumeToken(b, TRAN);
    if (!r) r = consumeToken(b, TRANSACTION);
    return r;
  }

  // ( ISOLATION LEVEL READ COMMITTED )?
  private static boolean begin_transaction_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "begin_transaction_2")) return false;
    begin_transaction_2_0(b, l + 1);
    return true;
  }

  // ISOLATION LEVEL READ COMMITTED
  private static boolean begin_transaction_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "begin_transaction_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, ISOLATION, LEVEL, READ, COMMITTED);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // NOT? BETWEEN expr AND expr
  public static boolean between_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "between_expr")) return false;
    if (!nextTokenIs(b, "<between expr>", BETWEEN, NOT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BETWEEN_EXPR, "<between expr>");
    r = between_expr_0(b, l + 1);
    r = r && consumeToken(b, BETWEEN);
    r = r && expr(b, l + 1);
    r = r && consumeToken(b, AND);
    r = r && expr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // NOT?
  private static boolean between_expr_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "between_expr_0")) return false;
    consumeToken(b, NOT);
    return true;
  }

  /* ********************************************************** */
  // BLOCK_COMMENT_OPEN (  LF )* COMMENT_CLOSE
  public static boolean block_comment(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "block_comment")) return false;
    if (!nextTokenIs(b, BLOCK_COMMENT_OPEN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, BLOCK_COMMENT_OPEN);
    r = r && block_comment_1(b, l + 1);
    r = r && consumeToken(b, COMMENT_CLOSE);
    exit_section_(b, m, BLOCK_COMMENT, r);
    return r;
  }

  // (  LF )*
  private static boolean block_comment_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "block_comment_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!consumeToken(b, LF)) break;
      if (!empty_element_parsed_guard_(b, "block_comment_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // BLOCK_HINT_OPEN hints COMMENT_CLOSE
  public static boolean block_hint_comment(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "block_hint_comment")) return false;
    if (!nextTokenIs(b, BLOCK_HINT_OPEN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, BLOCK_HINT_OPEN);
    r = r && hints(b, l + 1);
    r = r && consumeToken(b, COMMENT_CLOSE);
    exit_section_(b, m, BLOCK_HINT_COMMENT, r);
    return r;
  }

  /* ********************************************************** */
  // expr
  public static boolean body(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "body")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BODY, "<body>");
    r = expr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // TRUE | FALSE
  public static boolean bool(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "bool")) return false;
    if (!nextTokenIs(b, "<bool>", FALSE, TRUE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BOOL, "<bool>");
    r = consumeToken(b, TRUE);
    if (!r) r = consumeToken(b, FALSE);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // identifier-ref
  public static boolean bucket_ref(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "bucket_ref")) return false;
    if (!nextTokenIs(b, "<bucket ref>", ESCAPED_IDENTIFIER, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BUCKET_REF, "<bucket ref>");
    r = identifier_ref(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // BUILD INDEX ON keyspace-ref LPAREN index-term ( COMMA index-term)* RPAREN
  //                 index-using?
  public static boolean build_index(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "build_index")) return false;
    if (!nextTokenIs(b, BUILD)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, BUILD, INDEX, ON);
    r = r && keyspace_ref(b, l + 1);
    r = r && consumeToken(b, LPAREN);
    r = r && index_term(b, l + 1);
    r = r && build_index_6(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    r = r && build_index_8(b, l + 1);
    exit_section_(b, m, BUILD_INDEX, r);
    return r;
  }

  // ( COMMA index-term)*
  private static boolean build_index_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "build_index_6")) return false;
    while (true) {
      int c = current_position_(b);
      if (!build_index_6_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "build_index_6", c)) break;
    }
    return true;
  }

  // COMMA index-term
  private static boolean build_index_6_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "build_index_6_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && index_term(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // index-using?
  private static boolean build_index_8(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "build_index_8")) return false;
    index_using(b, l + 1);
    return true;
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
    r = expr(b, l + 1);
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
    r = consumeToken(b, COMMA);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // simple-case-expr | searched-case-expr
  public static boolean case_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "case_expr")) return false;
    if (!nextTokenIs(b, CASE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = simple_case_expr(b, l + 1);
    if (!r) r = searched_case_expr(b, l + 1);
    exit_section_(b, m, CASE_EXPR, r);
    return r;
  }

  /* ********************************************************** */
  // exists-expr | in-expr | within-expr | range-cond
  public static boolean collection_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "collection_expr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, COLLECTION_EXPR, "<collection expr>");
    r = exists_expr(b, l + 1);
    if (!r) r = in_expr(b, l + 1);
    if (!r) r = within_expr(b, l + 1);
    if (!r) r = range_cond(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // identifier-ref
  public static boolean collection_ref(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "collection_ref")) return false;
    if (!nextTokenIs(b, "<collection ref>", ESCAPED_IDENTIFIER, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, COLLECTION_REF, "<collection ref>");
    r = identifier_ref(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // COMMA ( rhs-keyspace | rhs-subquery | rhs-generic )
  public static boolean comma_separated_join(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "comma_separated_join")) return false;
    if (!nextTokenIs(b, COMMA)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && comma_separated_join_1(b, l + 1);
    exit_section_(b, m, COMMA_SEPARATED_JOIN, r);
    return r;
  }

  // rhs-keyspace | rhs-subquery | rhs-generic
  private static boolean comma_separated_join_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "comma_separated_join_1")) return false;
    boolean r;
    r = rhs_keyspace(b, l + 1);
    if (!r) r = rhs_subquery(b, l + 1);
    if (!r) r = rhs_generic(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // block-comment
  public static boolean comment(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "comment")) return false;
    if (!nextTokenIs(b, BLOCK_COMMENT_OPEN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = block_comment(b, l + 1);
    exit_section_(b, m, COMMENT, r);
    return r;
  }

  /* ********************************************************** */
  // COMMIT ( WORK | TRAN | TRANSACTION )?
  public static boolean commit_transaction(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "commit_transaction")) return false;
    if (!nextTokenIs(b, COMMIT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMIT);
    r = r && commit_transaction_1(b, l + 1);
    exit_section_(b, m, COMMIT_TRANSACTION, r);
    return r;
  }

  // ( WORK | TRAN | TRANSACTION )?
  private static boolean commit_transaction_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "commit_transaction_1")) return false;
    commit_transaction_1_0(b, l + 1);
    return true;
  }

  // WORK | TRAN | TRANSACTION
  private static boolean commit_transaction_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "commit_transaction_1_0")) return false;
    boolean r;
    r = consumeToken(b, WORK);
    if (!r) r = consumeToken(b, TRAN);
    if (!r) r = consumeToken(b, TRANSACTION);
    return r;
  }

  /* ********************************************************** */
  // relational-expr |
  //                     between-expr |
  //                     like-expr |
  //                     is-expr
  public static boolean comparison_term(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "comparison_term")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, COMPARISON_TERM, "<comparison term>");
    r = relational_expr(b, l + 1);
    if (!r) r = between_expr(b, l + 1);
    if (!r) r = like_expr(b, l + 1);
    if (!r) r = is_expr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // DBLPIPE expr
  public static boolean concatenation_term(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "concatenation_term")) return false;
    if (!nextTokenIs(b, DBLPIPE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, DBLPIPE);
    r = r && expr(b, l + 1);
    exit_section_(b, m, CONCATENATION_TERM, r);
    return r;
  }

  /* ********************************************************** */
  // expr
  public static boolean cond(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "cond")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, COND, "<cond>");
    r = expr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // CREATE COLLECTION ( ( namespace-ref SEMICOLON )? bucket-ref DOT scope-ref DOT )?
  //                       collection-ref ( IF NOT EXISTS )?
  public static boolean create_collection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_collection")) return false;
    if (!nextTokenIs(b, CREATE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, CREATE, COLLECTION);
    r = r && create_collection_2(b, l + 1);
    r = r && collection_ref(b, l + 1);
    r = r && create_collection_4(b, l + 1);
    exit_section_(b, m, CREATE_COLLECTION, r);
    return r;
  }

  // ( ( namespace-ref SEMICOLON )? bucket-ref DOT scope-ref DOT )?
  private static boolean create_collection_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_collection_2")) return false;
    create_collection_2_0(b, l + 1);
    return true;
  }

  // ( namespace-ref SEMICOLON )? bucket-ref DOT scope-ref DOT
  private static boolean create_collection_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_collection_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = create_collection_2_0_0(b, l + 1);
    r = r && bucket_ref(b, l + 1);
    r = r && consumeToken(b, DOT);
    r = r && scope_ref(b, l + 1);
    r = r && consumeToken(b, DOT);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( namespace-ref SEMICOLON )?
  private static boolean create_collection_2_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_collection_2_0_0")) return false;
    create_collection_2_0_0_0(b, l + 1);
    return true;
  }

  // namespace-ref SEMICOLON
  private static boolean create_collection_2_0_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_collection_2_0_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = namespace_ref(b, l + 1);
    r = r && consumeToken(b, SEMICOLON);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( IF NOT EXISTS )?
  private static boolean create_collection_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_collection_4")) return false;
    create_collection_4_0(b, l + 1);
    return true;
  }

  // IF NOT EXISTS
  private static boolean create_collection_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_collection_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, IF, NOT, EXISTS);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // create-function-inline | create-function-external
  public static boolean create_function(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_function")) return false;
    if (!nextTokenIs(b, CREATE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = create_function_inline(b, l + 1);
    if (!r) r = create_function_external(b, l + 1);
    exit_section_(b, m, CREATE_FUNCTION, r);
    return r;
  }

  /* ********************************************************** */
  // CREATE ( OR REPLACE )? FUNCTION function-ref LPAREN params? RPAREN
  //                              ( IF NOT EXISTS )?
  //                              LANGUAGE JAVASCRIPT AS obj AT
  public static boolean create_function_external(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_function_external")) return false;
    if (!nextTokenIs(b, CREATE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, CREATE);
    r = r && create_function_external_1(b, l + 1);
    r = r && consumeToken(b, FUNCTION);
    r = r && function_ref(b, l + 1);
    r = r && consumeToken(b, LPAREN);
    r = r && create_function_external_5(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    r = r && create_function_external_7(b, l + 1);
    r = r && consumeTokens(b, 0, LANGUAGE, JAVASCRIPT, AS);
    r = r && obj(b, l + 1);
    r = r && consumeToken(b, AT);
    exit_section_(b, m, CREATE_FUNCTION_EXTERNAL, r);
    return r;
  }

  // ( OR REPLACE )?
  private static boolean create_function_external_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_function_external_1")) return false;
    create_function_external_1_0(b, l + 1);
    return true;
  }

  // OR REPLACE
  private static boolean create_function_external_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_function_external_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, OR, REPLACE);
    exit_section_(b, m, null, r);
    return r;
  }

  // params?
  private static boolean create_function_external_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_function_external_5")) return false;
    params(b, l + 1);
    return true;
  }

  // ( IF NOT EXISTS )?
  private static boolean create_function_external_7(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_function_external_7")) return false;
    create_function_external_7_0(b, l + 1);
    return true;
  }

  // IF NOT EXISTS
  private static boolean create_function_external_7_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_function_external_7_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, IF, NOT, EXISTS);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // CREATE ( OR REPLACE )? FUNCTION function-ref LPAREN params? RPAREN
  //                            ( IF NOT EXISTS )?
  //                            ( LBRACE body RBRACE | LANGUAGE INLINE AS body )
  public static boolean create_function_inline(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_function_inline")) return false;
    if (!nextTokenIs(b, CREATE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, CREATE);
    r = r && create_function_inline_1(b, l + 1);
    r = r && consumeToken(b, FUNCTION);
    r = r && function_ref(b, l + 1);
    r = r && consumeToken(b, LPAREN);
    r = r && create_function_inline_5(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    r = r && create_function_inline_7(b, l + 1);
    r = r && create_function_inline_8(b, l + 1);
    exit_section_(b, m, CREATE_FUNCTION_INLINE, r);
    return r;
  }

  // ( OR REPLACE )?
  private static boolean create_function_inline_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_function_inline_1")) return false;
    create_function_inline_1_0(b, l + 1);
    return true;
  }

  // OR REPLACE
  private static boolean create_function_inline_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_function_inline_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, OR, REPLACE);
    exit_section_(b, m, null, r);
    return r;
  }

  // params?
  private static boolean create_function_inline_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_function_inline_5")) return false;
    params(b, l + 1);
    return true;
  }

  // ( IF NOT EXISTS )?
  private static boolean create_function_inline_7(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_function_inline_7")) return false;
    create_function_inline_7_0(b, l + 1);
    return true;
  }

  // IF NOT EXISTS
  private static boolean create_function_inline_7_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_function_inline_7_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, IF, NOT, EXISTS);
    exit_section_(b, m, null, r);
    return r;
  }

  // LBRACE body RBRACE | LANGUAGE INLINE AS body
  private static boolean create_function_inline_8(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_function_inline_8")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = create_function_inline_8_0(b, l + 1);
    if (!r) r = create_function_inline_8_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // LBRACE body RBRACE
  private static boolean create_function_inline_8_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_function_inline_8_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LBRACE);
    r = r && body(b, l + 1);
    r = r && consumeToken(b, RBRACE);
    exit_section_(b, m, null, r);
    return r;
  }

  // LANGUAGE INLINE AS body
  private static boolean create_function_inline_8_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_function_inline_8_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, LANGUAGE, INLINE, AS);
    r = r && body(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // CREATE INDEX index-name ( IF NOT EXISTS )? ON keyspace-ref
  //                  LPAREN index-key lead-key-attribs? ( ( COMMA index-key key-attribs? )+ )? RPAREN
  //                  index-partition? where-clause? index-using? index-with?
  public static boolean create_index(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_index")) return false;
    if (!nextTokenIs(b, CREATE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, CREATE, INDEX);
    r = r && index_name(b, l + 1);
    r = r && create_index_3(b, l + 1);
    r = r && consumeToken(b, ON);
    r = r && keyspace_ref(b, l + 1);
    r = r && consumeToken(b, LPAREN);
    r = r && index_key(b, l + 1);
    r = r && create_index_8(b, l + 1);
    r = r && create_index_9(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    r = r && create_index_11(b, l + 1);
    r = r && create_index_12(b, l + 1);
    r = r && create_index_13(b, l + 1);
    r = r && create_index_14(b, l + 1);
    exit_section_(b, m, CREATE_INDEX, r);
    return r;
  }

  // ( IF NOT EXISTS )?
  private static boolean create_index_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_index_3")) return false;
    create_index_3_0(b, l + 1);
    return true;
  }

  // IF NOT EXISTS
  private static boolean create_index_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_index_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, IF, NOT, EXISTS);
    exit_section_(b, m, null, r);
    return r;
  }

  // lead-key-attribs?
  private static boolean create_index_8(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_index_8")) return false;
    lead_key_attribs(b, l + 1);
    return true;
  }

  // ( ( COMMA index-key key-attribs? )+ )?
  private static boolean create_index_9(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_index_9")) return false;
    create_index_9_0(b, l + 1);
    return true;
  }

  // ( COMMA index-key key-attribs? )+
  private static boolean create_index_9_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_index_9_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = create_index_9_0_0(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!create_index_9_0_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "create_index_9_0", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // COMMA index-key key-attribs?
  private static boolean create_index_9_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_index_9_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && index_key(b, l + 1);
    r = r && create_index_9_0_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // key-attribs?
  private static boolean create_index_9_0_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_index_9_0_0_2")) return false;
    key_attribs(b, l + 1);
    return true;
  }

  // index-partition?
  private static boolean create_index_11(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_index_11")) return false;
    index_partition(b, l + 1);
    return true;
  }

  // where-clause?
  private static boolean create_index_12(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_index_12")) return false;
    where_clause(b, l + 1);
    return true;
  }

  // index-using?
  private static boolean create_index_13(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_index_13")) return false;
    index_using(b, l + 1);
    return true;
  }

  // index-with?
  private static boolean create_index_14(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_index_14")) return false;
    index_with(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // CREATE PRIMARY INDEX index-name? ( IF NOT EXISTS )?
  //                          ON keyspace-ref index-using? index-with?
  public static boolean create_primary_index(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_primary_index")) return false;
    if (!nextTokenIs(b, CREATE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, CREATE, PRIMARY, INDEX);
    r = r && create_primary_index_3(b, l + 1);
    r = r && create_primary_index_4(b, l + 1);
    r = r && consumeToken(b, ON);
    r = r && keyspace_ref(b, l + 1);
    r = r && create_primary_index_7(b, l + 1);
    r = r && create_primary_index_8(b, l + 1);
    exit_section_(b, m, CREATE_PRIMARY_INDEX, r);
    return r;
  }

  // index-name?
  private static boolean create_primary_index_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_primary_index_3")) return false;
    index_name(b, l + 1);
    return true;
  }

  // ( IF NOT EXISTS )?
  private static boolean create_primary_index_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_primary_index_4")) return false;
    create_primary_index_4_0(b, l + 1);
    return true;
  }

  // IF NOT EXISTS
  private static boolean create_primary_index_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_primary_index_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, IF, NOT, EXISTS);
    exit_section_(b, m, null, r);
    return r;
  }

  // index-using?
  private static boolean create_primary_index_7(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_primary_index_7")) return false;
    index_using(b, l + 1);
    return true;
  }

  // index-with?
  private static boolean create_primary_index_8(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_primary_index_8")) return false;
    index_with(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // CREATE SCOPE ( namespace-ref SEMICOLON )? bucket-ref DOT scope-ref ( IF NOT EXISTS )?
  public static boolean create_scope(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_scope")) return false;
    if (!nextTokenIs(b, CREATE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, CREATE, SCOPE);
    r = r && create_scope_2(b, l + 1);
    r = r && bucket_ref(b, l + 1);
    r = r && consumeToken(b, DOT);
    r = r && scope_ref(b, l + 1);
    r = r && create_scope_6(b, l + 1);
    exit_section_(b, m, CREATE_SCOPE, r);
    return r;
  }

  // ( namespace-ref SEMICOLON )?
  private static boolean create_scope_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_scope_2")) return false;
    create_scope_2_0(b, l + 1);
    return true;
  }

  // namespace-ref SEMICOLON
  private static boolean create_scope_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_scope_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = namespace_ref(b, l + 1);
    r = r && consumeToken(b, SEMICOLON);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( IF NOT EXISTS )?
  private static boolean create_scope_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_scope_6")) return false;
    create_scope_6_0(b, l + 1);
    return true;
  }

  // IF NOT EXISTS
  private static boolean create_scope_6_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_scope_6_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, IF, NOT, EXISTS);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // create-scope
  //                    | create-collection
  //                    | create-primary-index
  //                    | create-index
  //                    | create-function
  public static boolean create_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_statement")) return false;
    if (!nextTokenIs(b, CREATE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = create_scope(b, l + 1);
    if (!r) r = create_collection(b, l + 1);
    if (!r) r = create_primary_index(b, l + 1);
    if (!r) r = create_index(b, l + 1);
    if (!r) r = create_function(b, l + 1);
    exit_section_(b, m, CREATE_STATEMENT, r);
    return r;
  }

  /* ********************************************************** */
  // grant-statement
  //                 | revoke-statement
  public static boolean dcl_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dcl_statement")) return false;
    if (!nextTokenIs(b, "<dcl statement>", GRANT, REVOKE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, DCL_STATEMENT, "<dcl statement>");
    r = grant_statement(b, l + 1);
    if (!r) r = revoke_statement(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // create-statement
  //                 | drop-statement
  //                 | other-statement
  public static boolean ddl_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ddl_statement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, DDL_STATEMENT, "<ddl statement>");
    r = create_statement(b, l + 1);
    if (!r) r = drop_statement(b, l + 1);
    if (!r) r = other_statement(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ALL | STATISTICS
  public static boolean delete_all(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "delete_all")) return false;
    if (!nextTokenIs(b, "<delete all>", ALL, STATISTICS)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, DELETE_ALL, "<delete all>");
    r = consumeToken(b, ALL);
    if (!r) r = consumeToken(b, STATISTICS);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // DELETE ( delete-expr | delete-all )
  public static boolean delete_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "delete_clause")) return false;
    if (!nextTokenIs(b, DELETE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, DELETE);
    r = r && delete_clause_1(b, l + 1);
    exit_section_(b, m, DELETE_CLAUSE, r);
    return r;
  }

  // delete-expr | delete-all
  private static boolean delete_clause_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "delete_clause_1")) return false;
    boolean r;
    r = delete_expr(b, l + 1);
    if (!r) r = delete_all(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // STATISTICS? LPAREN index-key ( COMMA index-key )* RPAREN
  public static boolean delete_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "delete_expr")) return false;
    if (!nextTokenIs(b, "<delete expr>", LPAREN, STATISTICS)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, DELETE_EXPR, "<delete expr>");
    r = delete_expr_0(b, l + 1);
    r = r && consumeToken(b, LPAREN);
    r = r && index_key(b, l + 1);
    r = r && delete_expr_3(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // STATISTICS?
  private static boolean delete_expr_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "delete_expr_0")) return false;
    consumeToken(b, STATISTICS);
    return true;
  }

  // ( COMMA index-key )*
  private static boolean delete_expr_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "delete_expr_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!delete_expr_3_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "delete_expr_3", c)) break;
    }
    return true;
  }

  // COMMA index-key
  private static boolean delete_expr_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "delete_expr_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && index_key(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // DELETE FROM target-keyspace use-keys-clause? where-clause?
  //             limit-clause? returning-clause?
  public static boolean delete_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "delete_statement")) return false;
    if (!nextTokenIs(b, DELETE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, DELETE, FROM);
    r = r && target_keyspace(b, l + 1);
    r = r && delete_statement_3(b, l + 1);
    r = r && delete_statement_4(b, l + 1);
    r = r && delete_statement_5(b, l + 1);
    r = r && delete_statement_6(b, l + 1);
    exit_section_(b, m, DELETE_STATEMENT, r);
    return r;
  }

  // use-keys-clause?
  private static boolean delete_statement_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "delete_statement_3")) return false;
    use_keys_clause(b, l + 1);
    return true;
  }

  // where-clause?
  private static boolean delete_statement_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "delete_statement_4")) return false;
    where_clause(b, l + 1);
    return true;
  }

  // limit-clause?
  private static boolean delete_statement_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "delete_statement_5")) return false;
    limit_clause(b, l + 1);
    return true;
  }

  // returning-clause?
  private static boolean delete_statement_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "delete_statement_6")) return false;
    returning_clause(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // delete-statement
  //                 | insert-statement
  //                 | merge-statement
  //                 | update-statement
  //                 | upsert-statement
  public static boolean dml_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dml_statement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, DML_STATEMENT, "<dml statement>");
    r = delete_statement(b, l + 1);
    if (!r) r = insert_statement(b, l + 1);
    if (!r) r = merge_statement(b, l + 1);
    if (!r) r = update_statement(b, l + 1);
    if (!r) r = upsert_statement(b, l + 1);
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
  // select-statement |
  //                   infer-statement |
  //                   update-statistics
  public static boolean dql_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dql_statement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, DQL_STATEMENT, "<dql statement>");
    r = select_statement(b, l + 1);
    if (!r) r = infer_statement(b, l + 1);
    if (!r) r = update_statistics(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // DROP COLLECTION ( ( namespace-ref SEMICOLON )? bucket-ref DOT scope-ref DOT )?
  //                     collection-ref ( IF EXISTS )?
  public static boolean drop_collection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "drop_collection")) return false;
    if (!nextTokenIs(b, DROP)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, DROP, COLLECTION);
    r = r && drop_collection_2(b, l + 1);
    r = r && collection_ref(b, l + 1);
    r = r && drop_collection_4(b, l + 1);
    exit_section_(b, m, DROP_COLLECTION, r);
    return r;
  }

  // ( ( namespace-ref SEMICOLON )? bucket-ref DOT scope-ref DOT )?
  private static boolean drop_collection_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "drop_collection_2")) return false;
    drop_collection_2_0(b, l + 1);
    return true;
  }

  // ( namespace-ref SEMICOLON )? bucket-ref DOT scope-ref DOT
  private static boolean drop_collection_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "drop_collection_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = drop_collection_2_0_0(b, l + 1);
    r = r && bucket_ref(b, l + 1);
    r = r && consumeToken(b, DOT);
    r = r && scope_ref(b, l + 1);
    r = r && consumeToken(b, DOT);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( namespace-ref SEMICOLON )?
  private static boolean drop_collection_2_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "drop_collection_2_0_0")) return false;
    drop_collection_2_0_0_0(b, l + 1);
    return true;
  }

  // namespace-ref SEMICOLON
  private static boolean drop_collection_2_0_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "drop_collection_2_0_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = namespace_ref(b, l + 1);
    r = r && consumeToken(b, SEMICOLON);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( IF EXISTS )?
  private static boolean drop_collection_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "drop_collection_4")) return false;
    drop_collection_4_0(b, l + 1);
    return true;
  }

  // IF EXISTS
  private static boolean drop_collection_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "drop_collection_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, IF, EXISTS);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // DROP FUNCTION function-ref ( IF EXISTS )?
  public static boolean drop_function(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "drop_function")) return false;
    if (!nextTokenIs(b, DROP)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, DROP, FUNCTION);
    r = r && function_ref(b, l + 1);
    r = r && drop_function_3(b, l + 1);
    exit_section_(b, m, DROP_FUNCTION, r);
    return r;
  }

  // ( IF EXISTS )?
  private static boolean drop_function_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "drop_function_3")) return false;
    drop_function_3_0(b, l + 1);
    return true;
  }

  // IF EXISTS
  private static boolean drop_function_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "drop_function_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, IF, EXISTS);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // DROP INDEX ( index-path DOT index-name ( IF EXISTS )? |
  //                 index-name ( IF EXISTS )? ON keyspace-ref ) index-using?
  public static boolean drop_index(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "drop_index")) return false;
    if (!nextTokenIs(b, DROP)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, DROP, INDEX);
    r = r && drop_index_2(b, l + 1);
    r = r && drop_index_3(b, l + 1);
    exit_section_(b, m, DROP_INDEX, r);
    return r;
  }

  // index-path DOT index-name ( IF EXISTS )? |
  //                 index-name ( IF EXISTS )? ON keyspace-ref
  private static boolean drop_index_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "drop_index_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = drop_index_2_0(b, l + 1);
    if (!r) r = drop_index_2_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // index-path DOT index-name ( IF EXISTS )?
  private static boolean drop_index_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "drop_index_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = index_path(b, l + 1);
    r = r && consumeToken(b, DOT);
    r = r && index_name(b, l + 1);
    r = r && drop_index_2_0_3(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( IF EXISTS )?
  private static boolean drop_index_2_0_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "drop_index_2_0_3")) return false;
    drop_index_2_0_3_0(b, l + 1);
    return true;
  }

  // IF EXISTS
  private static boolean drop_index_2_0_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "drop_index_2_0_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, IF, EXISTS);
    exit_section_(b, m, null, r);
    return r;
  }

  // index-name ( IF EXISTS )? ON keyspace-ref
  private static boolean drop_index_2_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "drop_index_2_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = index_name(b, l + 1);
    r = r && drop_index_2_1_1(b, l + 1);
    r = r && consumeToken(b, ON);
    r = r && keyspace_ref(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( IF EXISTS )?
  private static boolean drop_index_2_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "drop_index_2_1_1")) return false;
    drop_index_2_1_1_0(b, l + 1);
    return true;
  }

  // IF EXISTS
  private static boolean drop_index_2_1_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "drop_index_2_1_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, IF, EXISTS);
    exit_section_(b, m, null, r);
    return r;
  }

  // index-using?
  private static boolean drop_index_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "drop_index_3")) return false;
    index_using(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // DROP PRIMARY INDEX ( IF EXISTS )? ON keyspace-ref
  //                        index-using?
  public static boolean drop_primary_index(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "drop_primary_index")) return false;
    if (!nextTokenIs(b, DROP)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, DROP, PRIMARY, INDEX);
    r = r && drop_primary_index_3(b, l + 1);
    r = r && consumeToken(b, ON);
    r = r && keyspace_ref(b, l + 1);
    r = r && drop_primary_index_6(b, l + 1);
    exit_section_(b, m, DROP_PRIMARY_INDEX, r);
    return r;
  }

  // ( IF EXISTS )?
  private static boolean drop_primary_index_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "drop_primary_index_3")) return false;
    drop_primary_index_3_0(b, l + 1);
    return true;
  }

  // IF EXISTS
  private static boolean drop_primary_index_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "drop_primary_index_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, IF, EXISTS);
    exit_section_(b, m, null, r);
    return r;
  }

  // index-using?
  private static boolean drop_primary_index_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "drop_primary_index_6")) return false;
    index_using(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // DROP SCOPE ( namespace-ref SEMICOLON )? bucket-ref DOT scope-ref ( IF EXISTS )?
  public static boolean drop_scope(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "drop_scope")) return false;
    if (!nextTokenIs(b, DROP)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, DROP, SCOPE);
    r = r && drop_scope_2(b, l + 1);
    r = r && bucket_ref(b, l + 1);
    r = r && consumeToken(b, DOT);
    r = r && scope_ref(b, l + 1);
    r = r && drop_scope_6(b, l + 1);
    exit_section_(b, m, DROP_SCOPE, r);
    return r;
  }

  // ( namespace-ref SEMICOLON )?
  private static boolean drop_scope_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "drop_scope_2")) return false;
    drop_scope_2_0(b, l + 1);
    return true;
  }

  // namespace-ref SEMICOLON
  private static boolean drop_scope_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "drop_scope_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = namespace_ref(b, l + 1);
    r = r && consumeToken(b, SEMICOLON);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( IF EXISTS )?
  private static boolean drop_scope_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "drop_scope_6")) return false;
    drop_scope_6_0(b, l + 1);
    return true;
  }

  // IF EXISTS
  private static boolean drop_scope_6_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "drop_scope_6_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, IF, EXISTS);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // drop-scope
  //                  | drop-collection
  //                  | drop-primary-index
  //                  | drop-index
  //                  | drop-function
  public static boolean drop_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "drop_statement")) return false;
    if (!nextTokenIs(b, DROP)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = drop_scope(b, l + 1);
    if (!r) r = drop_collection(b, l + 1);
    if (!r) r = drop_primary_index(b, l + 1);
    if (!r) r = drop_index(b, l + 1);
    if (!r) r = drop_function(b, l + 1);
    exit_section_(b, m, DROP_STATEMENT, r);
    return r;
  }

  /* ********************************************************** */
  // LBRACKET expr RBRACKET
  public static boolean element_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "element_expr")) return false;
    if (!nextTokenIs(b, LBRACKET)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LBRACKET);
    r = r && expr(b, l + 1);
    r = r && consumeToken(b, RBRACKET);
    exit_section_(b, m, ELEMENT_EXPR, r);
    return r;
  }

  /* ********************************************************** */
  // EXECUTE FUNCTION function-ref LPAREN ( expr ( COMMA expr )* )? RPAREN
  public static boolean execute_function(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "execute_function")) return false;
    if (!nextTokenIs(b, EXECUTE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, EXECUTE, FUNCTION);
    r = r && function_ref(b, l + 1);
    r = r && consumeToken(b, LPAREN);
    r = r && execute_function_4(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, EXECUTE_FUNCTION, r);
    return r;
  }

  // ( expr ( COMMA expr )* )?
  private static boolean execute_function_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "execute_function_4")) return false;
    execute_function_4_0(b, l + 1);
    return true;
  }

  // expr ( COMMA expr )*
  private static boolean execute_function_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "execute_function_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = expr(b, l + 1);
    r = r && execute_function_4_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( COMMA expr )*
  private static boolean execute_function_4_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "execute_function_4_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!execute_function_4_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "execute_function_4_0_1", c)) break;
    }
    return true;
  }

  // COMMA expr
  private static boolean execute_function_4_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "execute_function_4_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // EXISTS expr
  public static boolean exists_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exists_expr")) return false;
    if (!nextTokenIs(b, EXISTS)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EXISTS);
    r = r && expr(b, l + 1);
    exit_section_(b, m, EXISTS_EXPR, r);
    return r;
  }

  /* ********************************************************** */
  // EXPLAIN statement
  public static boolean explain_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "explain_statement")) return false;
    if (!nextTokenIs(b, EXPLAIN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EXPLAIN);
    r = r && statement(b, l + 1);
    exit_section_(b, m, EXPLAIN_STATEMENT, r);
    return r;
  }

  /* ********************************************************** */
  // ( literal | identifier-ref | LPAREN expr RPAREN )
  //         (
  //              arithmetic-term |
  //              comparison-term |
  //              concatenation-term |
  //              logical-term |
  //              case-expr |
  //              collection-expr |
  //              nested-expr |
  //              function-call |
  //              subquery-expr
  //          )*
  public static boolean expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, EXPR, "<expr>");
    r = expr_0(b, l + 1);
    r = r && expr_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // literal | identifier-ref | LPAREN expr RPAREN
  private static boolean expr_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = literal(b, l + 1);
    if (!r) r = identifier_ref(b, l + 1);
    if (!r) r = expr_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // LPAREN expr RPAREN
  private static boolean expr_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr_0_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LPAREN);
    r = r && expr(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, null, r);
    return r;
  }

  // (
  //              arithmetic-term |
  //              comparison-term |
  //              concatenation-term |
  //              logical-term |
  //              case-expr |
  //              collection-expr |
  //              nested-expr |
  //              function-call |
  //              subquery-expr
  //          )*
  private static boolean expr_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!expr_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "expr_1", c)) break;
    }
    return true;
  }

  // arithmetic-term |
  //              comparison-term |
  //              concatenation-term |
  //              logical-term |
  //              case-expr |
  //              collection-expr |
  //              nested-expr |
  //              function-call |
  //              subquery-expr
  private static boolean expr_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr_1_0")) return false;
    boolean r;
    r = arithmetic_term(b, l + 1);
    if (!r) r = comparison_term(b, l + 1);
    if (!r) r = concatenation_term(b, l + 1);
    if (!r) r = logical_term(b, l + 1);
    if (!r) r = case_expr(b, l + 1);
    if (!r) r = collection_expr(b, l + 1);
    if (!r) r = nested_expr(b, l + 1);
    if (!r) r = function_call(b, l + 1);
    if (!r) r = subquery_expr(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // DOT ( identifier-ref | ( ( escaped-identifier | LBRACKET expr RBRACKET ) 'i'? ) )
  public static boolean field_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "field_expr")) return false;
    if (!nextTokenIs(b, DOT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, DOT);
    r = r && field_expr_1(b, l + 1);
    exit_section_(b, m, FIELD_EXPR, r);
    return r;
  }

  // identifier-ref | ( ( escaped-identifier | LBRACKET expr RBRACKET ) 'i'? )
  private static boolean field_expr_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "field_expr_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = identifier_ref(b, l + 1);
    if (!r) r = field_expr_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( escaped-identifier | LBRACKET expr RBRACKET ) 'i'?
  private static boolean field_expr_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "field_expr_1_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = field_expr_1_1_0(b, l + 1);
    r = r && field_expr_1_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // escaped-identifier | LBRACKET expr RBRACKET
  private static boolean field_expr_1_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "field_expr_1_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ESCAPED_IDENTIFIER);
    if (!r) r = field_expr_1_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // LBRACKET expr RBRACKET
  private static boolean field_expr_1_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "field_expr_1_1_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LBRACKET);
    r = r && expr(b, l + 1);
    r = r && consumeToken(b, RBRACKET);
    exit_section_(b, m, null, r);
    return r;
  }

  // 'i'?
  private static boolean field_expr_1_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "field_expr_1_1_1")) return false;
    consumeToken(b, "i");
    return true;
  }

  /* ********************************************************** */
  // FILTER LPAREN WHERE cond RPAREN
  public static boolean filter_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "filter_clause")) return false;
    if (!nextTokenIs(b, FILTER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, FILTER, LPAREN, WHERE);
    r = r && cond(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, FILTER_CLAUSE, r);
    return r;
  }

  /* ********************************************************** */
  // FROM from-terms
  public static boolean from_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "from_clause")) return false;
    if (!nextTokenIs(b, FROM)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, FROM);
    r = r && from_terms(b, l + 1);
    exit_section_(b, m, FROM_CLAUSE, r);
    return r;
  }

  /* ********************************************************** */
  // expr ( AS alias )?
  public static boolean from_generic(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "from_generic")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FROM_GENERIC, "<from generic>");
    r = expr(b, l + 1);
    r = r && from_generic_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( AS alias )?
  private static boolean from_generic_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "from_generic_1")) return false;
    from_generic_1_0(b, l + 1);
    return true;
  }

  // AS alias
  private static boolean from_generic_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "from_generic_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, AS);
    r = r && alias(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // keyspace-ref ( AS? alias )? use-clause?
  public static boolean from_keyspace(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "from_keyspace")) return false;
    if (!nextTokenIs(b, "<from keyspace>", ESCAPED_IDENTIFIER, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FROM_KEYSPACE, "<from keyspace>");
    r = keyspace_ref(b, l + 1);
    r = r && from_keyspace_1(b, l + 1);
    r = r && from_keyspace_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( AS? alias )?
  private static boolean from_keyspace_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "from_keyspace_1")) return false;
    from_keyspace_1_0(b, l + 1);
    return true;
  }

  // AS? alias
  private static boolean from_keyspace_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "from_keyspace_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = from_keyspace_1_0_0(b, l + 1);
    r = r && alias(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // AS?
  private static boolean from_keyspace_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "from_keyspace_1_0_0")) return false;
    consumeToken(b, AS);
    return true;
  }

  // use-clause?
  private static boolean from_keyspace_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "from_keyspace_2")) return false;
    use_clause(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // with-clause? from-clause let-clause? where-clause? group-by-clause? window-clause? select-clause
  public static boolean from_select(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "from_select")) return false;
    if (!nextTokenIs(b, "<from select>", FROM, WITH)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FROM_SELECT, "<from select>");
    r = from_select_0(b, l + 1);
    r = r && from_clause(b, l + 1);
    r = r && from_select_2(b, l + 1);
    r = r && from_select_3(b, l + 1);
    r = r && from_select_4(b, l + 1);
    r = r && from_select_5(b, l + 1);
    r = r && select_clause(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // with-clause?
  private static boolean from_select_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "from_select_0")) return false;
    with_clause(b, l + 1);
    return true;
  }

  // let-clause?
  private static boolean from_select_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "from_select_2")) return false;
    let_clause(b, l + 1);
    return true;
  }

  // where-clause?
  private static boolean from_select_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "from_select_3")) return false;
    where_clause(b, l + 1);
    return true;
  }

  // group-by-clause?
  private static boolean from_select_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "from_select_4")) return false;
    group_by_clause(b, l + 1);
    return true;
  }

  // window-clause?
  private static boolean from_select_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "from_select_5")) return false;
    window_clause(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // subquery-expr AS? alias
  public static boolean from_subquery(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "from_subquery")) return false;
    if (!nextTokenIs(b, LPAREN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = subquery_expr(b, l + 1);
    r = r && from_subquery_1(b, l + 1);
    r = r && alias(b, l + 1);
    exit_section_(b, m, FROM_SUBQUERY, r);
    return r;
  }

  // AS?
  private static boolean from_subquery_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "from_subquery_1")) return false;
    consumeToken(b, AS);
    return true;
  }

  /* ********************************************************** */
  // ( from-keyspace | from-subquery | from-generic )
  //                ( join-clause | nest-clause | unnest-clause )* comma-separated-join*
  public static boolean from_terms(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "from_terms")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FROM_TERMS, "<from terms>");
    r = from_terms_0(b, l + 1);
    r = r && from_terms_1(b, l + 1);
    r = r && from_terms_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // from-keyspace | from-subquery | from-generic
  private static boolean from_terms_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "from_terms_0")) return false;
    boolean r;
    r = from_keyspace(b, l + 1);
    if (!r) r = from_subquery(b, l + 1);
    if (!r) r = from_generic(b, l + 1);
    return r;
  }

  // ( join-clause | nest-clause | unnest-clause )*
  private static boolean from_terms_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "from_terms_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!from_terms_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "from_terms_1", c)) break;
    }
    return true;
  }

  // join-clause | nest-clause | unnest-clause
  private static boolean from_terms_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "from_terms_1_0")) return false;
    boolean r;
    r = join_clause(b, l + 1);
    if (!r) r = nest_clause(b, l + 1);
    if (!r) r = unnest_clause(b, l + 1);
    return r;
  }

  // comma-separated-join*
  private static boolean from_terms_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "from_terms_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!comma_separated_join(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "from_terms_2", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // QUOTED_INDEX_FTS SEMICOLON ( index-array | index-object )
  public static boolean fts_hint_json(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fts_hint_json")) return false;
    if (!nextTokenIs(b, QUOTED_INDEX_FTS)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, QUOTED_INDEX_FTS, SEMICOLON);
    r = r && fts_hint_json_2(b, l + 1);
    exit_section_(b, m, FTS_HINT_JSON, r);
    return r;
  }

  // index-array | index-object
  private static boolean fts_hint_json_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fts_hint_json_2")) return false;
    boolean r;
    r = index_array(b, l + 1);
    if (!r) r = index_object(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // INDEX_FTS LPAREN keyspace-statement RPAREN
  public static boolean fts_hint_simple(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fts_hint_simple")) return false;
    if (!nextTokenIs(b, INDEX_FTS)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, INDEX_FTS, LPAREN);
    r = r && keyspace_statement(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, FTS_HINT_SIMPLE, r);
    return r;
  }

  /* ********************************************************** */
  // ( ALL | DISTINCT ) ARRAY expr
  //                     FOR var ( IN | WITHIN ) expr
  //                     ( COMMA var ( IN | WITHIN ) expr )* ( WHEN cond )? END
  public static boolean full_array_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "full_array_expr")) return false;
    if (!nextTokenIs(b, "<full array expr>", ALL, DISTINCT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FULL_ARRAY_EXPR, "<full array expr>");
    r = full_array_expr_0(b, l + 1);
    r = r && consumeToken(b, ARRAY);
    r = r && expr(b, l + 1);
    r = r && consumeToken(b, FOR);
    r = r && var(b, l + 1);
    r = r && full_array_expr_5(b, l + 1);
    r = r && expr(b, l + 1);
    r = r && full_array_expr_7(b, l + 1);
    r = r && full_array_expr_8(b, l + 1);
    r = r && consumeToken(b, END);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ALL | DISTINCT
  private static boolean full_array_expr_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "full_array_expr_0")) return false;
    boolean r;
    r = consumeToken(b, ALL);
    if (!r) r = consumeToken(b, DISTINCT);
    return r;
  }

  // IN | WITHIN
  private static boolean full_array_expr_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "full_array_expr_5")) return false;
    boolean r;
    r = consumeToken(b, IN);
    if (!r) r = consumeToken(b, WITHIN);
    return r;
  }

  // ( COMMA var ( IN | WITHIN ) expr )*
  private static boolean full_array_expr_7(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "full_array_expr_7")) return false;
    while (true) {
      int c = current_position_(b);
      if (!full_array_expr_7_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "full_array_expr_7", c)) break;
    }
    return true;
  }

  // COMMA var ( IN | WITHIN ) expr
  private static boolean full_array_expr_7_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "full_array_expr_7_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && var(b, l + 1);
    r = r && full_array_expr_7_0_2(b, l + 1);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // IN | WITHIN
  private static boolean full_array_expr_7_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "full_array_expr_7_0_2")) return false;
    boolean r;
    r = consumeToken(b, IN);
    if (!r) r = consumeToken(b, WITHIN);
    return r;
  }

  // ( WHEN cond )?
  private static boolean full_array_expr_8(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "full_array_expr_8")) return false;
    full_array_expr_8_0(b, l + 1);
    return true;
  }

  // WHEN cond
  private static boolean full_array_expr_8_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "full_array_expr_8_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, WHEN);
    r = r && cond(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // builtin-function |
  //                   ordinary-function |
  //                   aggregate-function |
  //                   window-function
  public static boolean function_call(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "function_call")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FUNCTION_CALL, "<function call>");
    r = builtin_function(b, l + 1);
    if (!r) r = ordinary_function(b, l + 1);
    if (!r) r = aggregate_function(b, l + 1);
    if (!r) r = window_function(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // identifier-ref
  public static boolean function_name(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "function_name")) return false;
    if (!nextTokenIs(b, "<function name>", ESCAPED_IDENTIFIER, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FUNCTION_NAME, "<function name>");
    r = identifier_ref(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ( namespace-ref SEMICOLON ( bucket-ref DOT scope-ref DOT )? )? identifier-ref
  public static boolean function_ref(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "function_ref")) return false;
    if (!nextTokenIs(b, "<function ref>", ESCAPED_IDENTIFIER, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FUNCTION_REF, "<function ref>");
    r = function_ref_0(b, l + 1);
    r = r && identifier_ref(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( namespace-ref SEMICOLON ( bucket-ref DOT scope-ref DOT )? )?
  private static boolean function_ref_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "function_ref_0")) return false;
    function_ref_0_0(b, l + 1);
    return true;
  }

  // namespace-ref SEMICOLON ( bucket-ref DOT scope-ref DOT )?
  private static boolean function_ref_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "function_ref_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = namespace_ref(b, l + 1);
    r = r && consumeToken(b, SEMICOLON);
    r = r && function_ref_0_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( bucket-ref DOT scope-ref DOT )?
  private static boolean function_ref_0_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "function_ref_0_0_2")) return false;
    function_ref_0_0_2_0(b, l + 1);
    return true;
  }

  // bucket-ref DOT scope-ref DOT
  private static boolean function_ref_0_0_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "function_ref_0_0_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = bucket_ref(b, l + 1);
    r = r && consumeToken(b, DOT);
    r = r && scope_ref(b, l + 1);
    r = r && consumeToken(b, DOT);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // GRANT role ( COMMA role )* ( ON keyspace-ref ( COMMA keyspace-ref )* )?
  //           TO user ( COMMA user )*
  public static boolean grant_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "grant_statement")) return false;
    if (!nextTokenIs(b, GRANT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, GRANT);
    r = r && role(b, l + 1);
    r = r && grant_statement_2(b, l + 1);
    r = r && grant_statement_3(b, l + 1);
    r = r && consumeToken(b, TO);
    r = r && user(b, l + 1);
    r = r && grant_statement_6(b, l + 1);
    exit_section_(b, m, GRANT_STATEMENT, r);
    return r;
  }

  // ( COMMA role )*
  private static boolean grant_statement_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "grant_statement_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!grant_statement_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "grant_statement_2", c)) break;
    }
    return true;
  }

  // COMMA role
  private static boolean grant_statement_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "grant_statement_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && role(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( ON keyspace-ref ( COMMA keyspace-ref )* )?
  private static boolean grant_statement_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "grant_statement_3")) return false;
    grant_statement_3_0(b, l + 1);
    return true;
  }

  // ON keyspace-ref ( COMMA keyspace-ref )*
  private static boolean grant_statement_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "grant_statement_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ON);
    r = r && keyspace_ref(b, l + 1);
    r = r && grant_statement_3_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( COMMA keyspace-ref )*
  private static boolean grant_statement_3_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "grant_statement_3_0_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!grant_statement_3_0_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "grant_statement_3_0_2", c)) break;
    }
    return true;
  }

  // COMMA keyspace-ref
  private static boolean grant_statement_3_0_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "grant_statement_3_0_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && keyspace_ref(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( COMMA user )*
  private static boolean grant_statement_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "grant_statement_6")) return false;
    while (true) {
      int c = current_position_(b);
      if (!grant_statement_6_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "grant_statement_6", c)) break;
    }
    return true;
  }

  // COMMA user
  private static boolean grant_statement_6_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "grant_statement_6_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && user(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // GROUP BY group-term ( COMMA group-term )*
  //                     letting-clause? having-clause? | letting-clause
  public static boolean group_by_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "group_by_clause")) return false;
    if (!nextTokenIs(b, "<group by clause>", GROUP, LETTING)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, GROUP_BY_CLAUSE, "<group by clause>");
    r = group_by_clause_0(b, l + 1);
    if (!r) r = letting_clause(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // GROUP BY group-term ( COMMA group-term )*
  //                     letting-clause? having-clause?
  private static boolean group_by_clause_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "group_by_clause_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, GROUP, BY);
    r = r && group_term(b, l + 1);
    r = r && group_by_clause_0_3(b, l + 1);
    r = r && group_by_clause_0_4(b, l + 1);
    r = r && group_by_clause_0_5(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( COMMA group-term )*
  private static boolean group_by_clause_0_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "group_by_clause_0_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!group_by_clause_0_3_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "group_by_clause_0_3", c)) break;
    }
    return true;
  }

  // COMMA group-term
  private static boolean group_by_clause_0_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "group_by_clause_0_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && group_term(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // letting-clause?
  private static boolean group_by_clause_0_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "group_by_clause_0_4")) return false;
    letting_clause(b, l + 1);
    return true;
  }

  // having-clause?
  private static boolean group_by_clause_0_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "group_by_clause_0_5")) return false;
    having_clause(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // expr ( (AS)? alias )?
  public static boolean group_term(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "group_term")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, GROUP_TERM, "<group term>");
    r = expr(b, l + 1);
    r = r && group_term_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( (AS)? alias )?
  private static boolean group_term_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "group_term_1")) return false;
    group_term_1_0(b, l + 1);
    return true;
  }

  // (AS)? alias
  private static boolean group_term_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "group_term_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = group_term_1_0_0(b, l + 1);
    r = r && alias(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (AS)?
  private static boolean group_term_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "group_term_1_0_0")) return false;
    consumeToken(b, AS);
    return true;
  }

  /* ********************************************************** */
  // QUOTED_INDEX SEMICOLON ( index-array | index-object )
  public static boolean gsi_hint_json(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "gsi_hint_json")) return false;
    if (!nextTokenIs(b, QUOTED_INDEX)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, QUOTED_INDEX, SEMICOLON);
    r = r && gsi_hint_json_2(b, l + 1);
    exit_section_(b, m, GSI_HINT_JSON, r);
    return r;
  }

  // index-array | index-object
  private static boolean gsi_hint_json_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "gsi_hint_json_2")) return false;
    boolean r;
    r = index_array(b, l + 1);
    if (!r) r = index_object(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // INDEX LPAREN keyspace-statement RPAREN
  public static boolean gsi_hint_simple(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "gsi_hint_simple")) return false;
    if (!nextTokenIs(b, INDEX)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, INDEX, LPAREN);
    r = r && keyspace_statement(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, GSI_HINT_SIMPLE, r);
    return r;
  }

  /* ********************************************************** */
  // LBRACKET hash-object ( COMMA hash-object )* RBRACKET
  public static boolean hash_array(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "hash_array")) return false;
    if (!nextTokenIs(b, LBRACKET)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LBRACKET);
    r = r && hash_object(b, l + 1);
    r = r && hash_array_2(b, l + 1);
    r = r && consumeToken(b, RBRACKET);
    exit_section_(b, m, HASH_ARRAY, r);
    return r;
  }

  // ( COMMA hash-object )*
  private static boolean hash_array_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "hash_array_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!hash_array_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "hash_array_2", c)) break;
    }
    return true;
  }

  // COMMA hash-object
  private static boolean hash_array_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "hash_array_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && hash_object(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // QUOTED_USE_HASH  ( hash-array | hash-object )
  public static boolean hash_hint_json(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "hash_hint_json")) return false;
    if (!nextTokenIs(b, QUOTED_USE_HASH)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, QUOTED_USE_HASH);
    r = r && hash_hint_json_1(b, l + 1);
    exit_section_(b, m, HASH_HINT_JSON, r);
    return r;
  }

  // hash-array | hash-object
  private static boolean hash_hint_json_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "hash_hint_json_1")) return false;
    boolean r;
    r = hash_array(b, l + 1);
    if (!r) r = hash_object(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // USE_HASH LPAREN ( keyspace-statement ( SLASH ( BUILD | PROBE ) )? )+ RPAREN
  public static boolean hash_hint_simple(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "hash_hint_simple")) return false;
    if (!nextTokenIs(b, USE_HASH)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, USE_HASH, LPAREN);
    r = r && hash_hint_simple_2(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, HASH_HINT_SIMPLE, r);
    return r;
  }

  // ( keyspace-statement ( SLASH ( BUILD | PROBE ) )? )+
  private static boolean hash_hint_simple_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "hash_hint_simple_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = hash_hint_simple_2_0(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!hash_hint_simple_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "hash_hint_simple_2", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // keyspace-statement ( SLASH ( BUILD | PROBE ) )?
  private static boolean hash_hint_simple_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "hash_hint_simple_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = keyspace_statement(b, l + 1);
    r = r && hash_hint_simple_2_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( SLASH ( BUILD | PROBE ) )?
  private static boolean hash_hint_simple_2_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "hash_hint_simple_2_0_1")) return false;
    hash_hint_simple_2_0_1_0(b, l + 1);
    return true;
  }

  // SLASH ( BUILD | PROBE )
  private static boolean hash_hint_simple_2_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "hash_hint_simple_2_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, SLASH);
    r = r && hash_hint_simple_2_0_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // BUILD | PROBE
  private static boolean hash_hint_simple_2_0_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "hash_hint_simple_2_0_1_0_1")) return false;
    boolean r;
    r = consumeToken(b, BUILD);
    if (!r) r = consumeToken(b, PROBE);
    return r;
  }

  /* ********************************************************** */
  // LBRACE keyspace-property ( COMMA option-property )? RBRACE
  public static boolean hash_object(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "hash_object")) return false;
    if (!nextTokenIs(b, LBRACE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LBRACE);
    r = r && keyspace_property(b, l + 1);
    r = r && hash_object_2(b, l + 1);
    r = r && consumeToken(b, RBRACE);
    exit_section_(b, m, HASH_OBJECT, r);
    return r;
  }

  // ( COMMA option-property )?
  private static boolean hash_object_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "hash_object_2")) return false;
    hash_object_2_0(b, l + 1);
    return true;
  }

  // COMMA option-property
  private static boolean hash_object_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "hash_object_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && option_property(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // HAVING cond
  public static boolean having_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "having_clause")) return false;
    if (!nextTokenIs(b, HAVING)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, HAVING);
    r = r && cond(b, l + 1);
    exit_section_(b, m, HAVING_CLAUSE, r);
    return r;
  }

  /* ********************************************************** */
  // block-hint-comment | line-hint-comment
  public static boolean hint_comment(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "hint_comment")) return false;
    if (!nextTokenIs(b, "<hint comment>", BLOCK_HINT_OPEN, LINE_HINT_OPEN)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, HINT_COMMENT, "<hint comment>");
    r = block_hint_comment(b, l + 1);
    if (!r) r = line_hint_comment(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // simple-hint-sequence | json-hint-object
  public static boolean hints(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "hints")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, HINTS, "<hints>");
    r = simple_hint_sequence(b, l + 1);
    if (!r) r = json_hint_object(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // IDENTIFIER | ESCAPED_IDENTIFIER
  public static boolean identifier_ref(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "identifier_ref")) return false;
    if (!nextTokenIs(b, "<identifier ref>", ESCAPED_IDENTIFIER, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, IDENTIFIER_REF, "<identifier ref>");
    r = consumeToken(b, IDENTIFIER);
    if (!r) r = consumeToken(b, ESCAPED_IDENTIFIER);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // NOT? IN expr
  public static boolean in_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "in_expr")) return false;
    if (!nextTokenIs(b, "<in expr>", IN, NOT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, IN_EXPR, "<in expr>");
    r = in_expr_0(b, l + 1);
    r = r && consumeToken(b, IN);
    r = r && expr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // NOT?
  private static boolean in_expr_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "in_expr_0")) return false;
    consumeToken(b, NOT);
    return true;
  }

  /* ********************************************************** */
  // INCLUDE MISSING
  public static boolean include_missing(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "include_missing")) return false;
    if (!nextTokenIs(b, INCLUDE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, INCLUDE, MISSING);
    exit_section_(b, m, INCLUDE_MISSING, r);
    return r;
  }

  /* ********************************************************** */
  // LBRACKET index-object ( COMMA index-object )* RBRACKET
  public static boolean index_array(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_array")) return false;
    if (!nextTokenIs(b, LBRACKET)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LBRACKET);
    r = r && index_object(b, l + 1);
    r = r && index_array_2(b, l + 1);
    r = r && consumeToken(b, RBRACKET);
    exit_section_(b, m, INDEX_ARRAY, r);
    return r;
  }

  // ( COMMA index-object )*
  private static boolean index_array_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_array_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!index_array_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "index_array_2", c)) break;
    }
    return true;
  }

  // COMMA index-object
  private static boolean index_array_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_array_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && index_object(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // INDEX ( index-path DOT index-name | index-name ON keyspace-ref )
  public static boolean index_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_clause")) return false;
    if (!nextTokenIs(b, INDEX)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, INDEX);
    r = r && index_clause_1(b, l + 1);
    exit_section_(b, m, INDEX_CLAUSE, r);
    return r;
  }

  // index-path DOT index-name | index-name ON keyspace-ref
  private static boolean index_clause_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_clause_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = index_clause_1_0(b, l + 1);
    if (!r) r = index_clause_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // index-path DOT index-name
  private static boolean index_clause_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_clause_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = index_path(b, l + 1);
    r = r && consumeToken(b, DOT);
    r = r && index_name(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // index-name ON keyspace-ref
  private static boolean index_clause_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_clause_1_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = index_name(b, l + 1);
    r = r && consumeToken(b, ON);
    r = r && keyspace_ref(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // index-join-type? JOIN index-join-rhs index-join-predicate
  public static boolean index_join_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_join_clause")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, INDEX_JOIN_CLAUSE, "<index join clause>");
    r = index_join_clause_0(b, l + 1);
    r = r && consumeToken(b, JOIN);
    r = r && index_join_rhs(b, l + 1);
    r = r && index_join_predicate(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // index-join-type?
  private static boolean index_join_clause_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_join_clause_0")) return false;
    index_join_type(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // ON PRIMARY? KEY expr FOR alias
  public static boolean index_join_predicate(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_join_predicate")) return false;
    if (!nextTokenIs(b, ON)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ON);
    r = r && index_join_predicate_1(b, l + 1);
    r = r && consumeToken(b, KEY);
    r = r && expr(b, l + 1);
    r = r && consumeToken(b, FOR);
    r = r && alias(b, l + 1);
    exit_section_(b, m, INDEX_JOIN_PREDICATE, r);
    return r;
  }

  // PRIMARY?
  private static boolean index_join_predicate_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_join_predicate_1")) return false;
    consumeToken(b, PRIMARY);
    return true;
  }

  /* ********************************************************** */
  // keyspace-ref ( AS? alias )?
  public static boolean index_join_rhs(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_join_rhs")) return false;
    if (!nextTokenIs(b, "<index join rhs>", ESCAPED_IDENTIFIER, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, INDEX_JOIN_RHS, "<index join rhs>");
    r = keyspace_ref(b, l + 1);
    r = r && index_join_rhs_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( AS? alias )?
  private static boolean index_join_rhs_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_join_rhs_1")) return false;
    index_join_rhs_1_0(b, l + 1);
    return true;
  }

  // AS? alias
  private static boolean index_join_rhs_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_join_rhs_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = index_join_rhs_1_0_0(b, l + 1);
    r = r && alias(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // AS?
  private static boolean index_join_rhs_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_join_rhs_1_0_0")) return false;
    consumeToken(b, AS);
    return true;
  }

  /* ********************************************************** */
  // INNER | ( LEFT OUTER? )
  public static boolean index_join_type(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_join_type")) return false;
    if (!nextTokenIs(b, "<index join type>", INNER, LEFT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, INDEX_JOIN_TYPE, "<index join type>");
    r = consumeToken(b, INNER);
    if (!r) r = index_join_type_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // LEFT OUTER?
  private static boolean index_join_type_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_join_type_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LEFT);
    r = r && index_join_type_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // OUTER?
  private static boolean index_join_type_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_join_type_1_1")) return false;
    consumeToken(b, OUTER);
    return true;
  }

  /* ********************************************************** */
  // expr | array-expr
  public static boolean index_key(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_key")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, INDEX_KEY, "<index key>");
    r = expr(b, l + 1);
    if (!r) r = array_expr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // expr
  public static boolean index_key_object(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_key_object")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, INDEX_KEY_OBJECT, "<index key object>");
    r = expr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // identifier-ref
  public static boolean index_name(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_name")) return false;
    if (!nextTokenIs(b, "<index name>", ESCAPED_IDENTIFIER, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, INDEX_NAME, "<index name>");
    r = identifier_ref(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // index-nest-type? NEST index-nest-rhs index-nest-predicate
  public static boolean index_nest_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_nest_clause")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, INDEX_NEST_CLAUSE, "<index nest clause>");
    r = index_nest_clause_0(b, l + 1);
    r = r && consumeToken(b, NEST);
    r = r && index_nest_rhs(b, l + 1);
    r = r && index_nest_predicate(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // index-nest-type?
  private static boolean index_nest_clause_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_nest_clause_0")) return false;
    index_nest_type(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // ON KEY expr FOR alias
  public static boolean index_nest_predicate(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_nest_predicate")) return false;
    if (!nextTokenIs(b, ON)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, ON, KEY);
    r = r && expr(b, l + 1);
    r = r && consumeToken(b, FOR);
    r = r && alias(b, l + 1);
    exit_section_(b, m, INDEX_NEST_PREDICATE, r);
    return r;
  }

  /* ********************************************************** */
  // keyspace-ref ( AS? alias )?
  public static boolean index_nest_rhs(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_nest_rhs")) return false;
    if (!nextTokenIs(b, "<index nest rhs>", ESCAPED_IDENTIFIER, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, INDEX_NEST_RHS, "<index nest rhs>");
    r = keyspace_ref(b, l + 1);
    r = r && index_nest_rhs_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( AS? alias )?
  private static boolean index_nest_rhs_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_nest_rhs_1")) return false;
    index_nest_rhs_1_0(b, l + 1);
    return true;
  }

  // AS? alias
  private static boolean index_nest_rhs_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_nest_rhs_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = index_nest_rhs_1_0_0(b, l + 1);
    r = r && alias(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // AS?
  private static boolean index_nest_rhs_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_nest_rhs_1_0_0")) return false;
    consumeToken(b, AS);
    return true;
  }

  /* ********************************************************** */
  // INNER | ( LEFT OUTER? )
  public static boolean index_nest_type(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_nest_type")) return false;
    if (!nextTokenIs(b, "<index nest type>", INNER, LEFT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, INDEX_NEST_TYPE, "<index nest type>");
    r = consumeToken(b, INNER);
    if (!r) r = index_nest_type_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // LEFT OUTER?
  private static boolean index_nest_type_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_nest_type_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LEFT);
    r = r && index_nest_type_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // OUTER?
  private static boolean index_nest_type_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_nest_type_1_1")) return false;
    consumeToken(b, OUTER);
    return true;
  }

  /* ********************************************************** */
  // LBRACE keyspace-property COMMA indexes-property RBRACE
  public static boolean index_object(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_object")) return false;
    if (!nextTokenIs(b, LBRACE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LBRACE);
    r = r && keyspace_property(b, l + 1);
    r = r && consumeToken(b, COMMA);
    r = r && indexes_property(b, l + 1);
    r = r && consumeToken(b, RBRACE);
    exit_section_(b, m, INDEX_OBJECT, r);
    return r;
  }

  /* ********************************************************** */
  // ASC | DESC
  public static boolean index_order(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_order")) return false;
    if (!nextTokenIs(b, "<index order>", ASC, DESC)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, INDEX_ORDER, "<index order>");
    r = consumeToken(b, ASC);
    if (!r) r = consumeToken(b, DESC);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // PARTITION BY HASH LPAREN partition-key-expr
  //                     ( COMMA partition-key-expr )* RPAREN
  public static boolean index_partition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_partition")) return false;
    if (!nextTokenIs(b, PARTITION)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, PARTITION, BY, HASH, LPAREN);
    r = r && partition_key_expr(b, l + 1);
    r = r && index_partition_5(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, INDEX_PARTITION, r);
    return r;
  }

  // ( COMMA partition-key-expr )*
  private static boolean index_partition_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_partition_5")) return false;
    while (true) {
      int c = current_position_(b);
      if (!index_partition_5_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "index_partition_5", c)) break;
    }
    return true;
  }

  // COMMA partition-key-expr
  private static boolean index_partition_5_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_partition_5_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && partition_key_expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // keyspace-full | keyspace-prefix | keyspace-partial
  public static boolean index_path(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_path")) return false;
    if (!nextTokenIs(b, "<index path>", ESCAPED_IDENTIFIER, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, INDEX_PATH, "<index path>");
    r = keyspace_full(b, l + 1);
    if (!r) r = keyspace_prefix(b, l + 1);
    if (!r) r = keyspace_partial(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // index-name? index-type?
  public static boolean index_ref(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_ref")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, INDEX_REF, "<index ref>");
    r = index_ref_0(b, l + 1);
    r = r && index_ref_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // index-name?
  private static boolean index_ref_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_ref_0")) return false;
    index_name(b, l + 1);
    return true;
  }

  // index-type?
  private static boolean index_ref_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_ref_1")) return false;
    index_type(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // index-name | subquery-expr
  public static boolean index_term(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_term")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, INDEX_TERM, "<index term>");
    r = index_name(b, l + 1);
    if (!r) r = subquery_expr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // USING ( GSI | FTS )
  public static boolean index_type(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_type")) return false;
    if (!nextTokenIs(b, USING)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, USING);
    r = r && index_type_1(b, l + 1);
    exit_section_(b, m, INDEX_TYPE, r);
    return r;
  }

  // GSI | FTS
  private static boolean index_type_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_type_1")) return false;
    boolean r;
    r = consumeToken(b, GSI);
    if (!r) r = consumeToken(b, FTS);
    return r;
  }

  /* ********************************************************** */
  // USING GSI
  public static boolean index_using(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_using")) return false;
    if (!nextTokenIs(b, USING)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, USING, GSI);
    exit_section_(b, m, INDEX_USING, r);
    return r;
  }

  /* ********************************************************** */
  // WITH expr
  public static boolean index_with(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_with")) return false;
    if (!nextTokenIs(b, WITH)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, WITH);
    r = r && expr(b, l + 1);
    exit_section_(b, m, INDEX_WITH, r);
    return r;
  }

  /* ********************************************************** */
  // INDEX ( LPAREN ( index-name ( COMMA index-name )* | subquery-expr ) RPAREN |
  //                              ALL )
  public static boolean indexes_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "indexes_clause")) return false;
    if (!nextTokenIs(b, INDEX)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, INDEX);
    r = r && indexes_clause_1(b, l + 1);
    exit_section_(b, m, INDEXES_CLAUSE, r);
    return r;
  }

  // LPAREN ( index-name ( COMMA index-name )* | subquery-expr ) RPAREN |
  //                              ALL
  private static boolean indexes_clause_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "indexes_clause_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = indexes_clause_1_0(b, l + 1);
    if (!r) r = consumeToken(b, ALL);
    exit_section_(b, m, null, r);
    return r;
  }

  // LPAREN ( index-name ( COMMA index-name )* | subquery-expr ) RPAREN
  private static boolean indexes_clause_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "indexes_clause_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LPAREN);
    r = r && indexes_clause_1_0_1(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, null, r);
    return r;
  }

  // index-name ( COMMA index-name )* | subquery-expr
  private static boolean indexes_clause_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "indexes_clause_1_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = indexes_clause_1_0_1_0(b, l + 1);
    if (!r) r = subquery_expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // index-name ( COMMA index-name )*
  private static boolean indexes_clause_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "indexes_clause_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = index_name(b, l + 1);
    r = r && indexes_clause_1_0_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( COMMA index-name )*
  private static boolean indexes_clause_1_0_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "indexes_clause_1_0_1_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!indexes_clause_1_0_1_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "indexes_clause_1_0_1_0_1", c)) break;
    }
    return true;
  }

  // COMMA index-name
  private static boolean indexes_clause_1_0_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "indexes_clause_1_0_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && index_name(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // QUOTED_INDEXES SEMICOLON ( NULL
  //                                      | DQUOTE index DQUOTE
  //                                      | LBRACKET DQUOTE index DQUOTE ( COMMA DQUOTE index DQUOTE )* RBRACKET )
  public static boolean indexes_property(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "indexes_property")) return false;
    if (!nextTokenIs(b, QUOTED_INDEXES)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, QUOTED_INDEXES, SEMICOLON);
    r = r && indexes_property_2(b, l + 1);
    exit_section_(b, m, INDEXES_PROPERTY, r);
    return r;
  }

  // NULL
  //                                      | DQUOTE index DQUOTE
  //                                      | LBRACKET DQUOTE index DQUOTE ( COMMA DQUOTE index DQUOTE )* RBRACKET
  private static boolean indexes_property_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "indexes_property_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, NULL);
    if (!r) r = parseTokens(b, 0, DQUOTE, INDEX, DQUOTE);
    if (!r) r = indexes_property_2_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // LBRACKET DQUOTE index DQUOTE ( COMMA DQUOTE index DQUOTE )* RBRACKET
  private static boolean indexes_property_2_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "indexes_property_2_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, LBRACKET, DQUOTE, INDEX, DQUOTE);
    r = r && indexes_property_2_2_4(b, l + 1);
    r = r && consumeToken(b, RBRACKET);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( COMMA DQUOTE index DQUOTE )*
  private static boolean indexes_property_2_2_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "indexes_property_2_2_4")) return false;
    while (true) {
      int c = current_position_(b);
      if (!indexes_property_2_2_4_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "indexes_property_2_2_4", c)) break;
    }
    return true;
  }

  // COMMA DQUOTE index DQUOTE
  private static boolean indexes_property_2_2_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "indexes_property_2_2_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, COMMA, DQUOTE, INDEX, DQUOTE);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // INFER ( COLLECTION | KEYSPACE )? keyspace-ref ( WITH options )?
  public static boolean infer_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "infer_statement")) return false;
    if (!nextTokenIs(b, INFER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, INFER);
    r = r && infer_statement_1(b, l + 1);
    r = r && keyspace_ref(b, l + 1);
    r = r && infer_statement_3(b, l + 1);
    exit_section_(b, m, INFER_STATEMENT, r);
    return r;
  }

  // ( COLLECTION | KEYSPACE )?
  private static boolean infer_statement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "infer_statement_1")) return false;
    infer_statement_1_0(b, l + 1);
    return true;
  }

  // COLLECTION | KEYSPACE
  private static boolean infer_statement_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "infer_statement_1_0")) return false;
    boolean r;
    r = consumeToken(b, COLLECTION);
    if (!r) r = consumeToken(b, KEYSPACE);
    return r;
  }

  // ( WITH options )?
  private static boolean infer_statement_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "infer_statement_3")) return false;
    infer_statement_3_0(b, l + 1);
    return true;
  }

  // WITH options
  private static boolean infer_statement_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "infer_statement_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, WITH, OPTIONS);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // LPAREN PRIMARY? KEY key-expr ( COMMA VALUE value-expr )?
  //                    ( COMMA OPTIONS options )? RPAREN select-statement
  public static boolean insert_select(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "insert_select")) return false;
    if (!nextTokenIs(b, LPAREN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LPAREN);
    r = r && insert_select_1(b, l + 1);
    r = r && consumeToken(b, KEY);
    r = r && key_expr(b, l + 1);
    r = r && insert_select_4(b, l + 1);
    r = r && insert_select_5(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    r = r && select_statement(b, l + 1);
    exit_section_(b, m, INSERT_SELECT, r);
    return r;
  }

  // PRIMARY?
  private static boolean insert_select_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "insert_select_1")) return false;
    consumeToken(b, PRIMARY);
    return true;
  }

  // ( COMMA VALUE value-expr )?
  private static boolean insert_select_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "insert_select_4")) return false;
    insert_select_4_0(b, l + 1);
    return true;
  }

  // COMMA VALUE value-expr
  private static boolean insert_select_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "insert_select_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, COMMA, VALUE);
    r = r && value_expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( COMMA OPTIONS options )?
  private static boolean insert_select_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "insert_select_5")) return false;
    insert_select_5_0(b, l + 1);
    return true;
  }

  // COMMA OPTIONS options
  private static boolean insert_select_5_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "insert_select_5_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, COMMA, OPTIONS, OPTIONS);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // INSERT INTO target-keyspace ( insert-values | insert-select )
  //             returning-clause?
  public static boolean insert_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "insert_statement")) return false;
    if (!nextTokenIs(b, INSERT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, INSERT, INTO);
    r = r && target_keyspace(b, l + 1);
    r = r && insert_statement_3(b, l + 1);
    r = r && insert_statement_4(b, l + 1);
    exit_section_(b, m, INSERT_STATEMENT, r);
    return r;
  }

  // insert-values | insert-select
  private static boolean insert_statement_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "insert_statement_3")) return false;
    boolean r;
    r = insert_values(b, l + 1);
    if (!r) r = insert_select(b, l + 1);
    return r;
  }

  // returning-clause?
  private static boolean insert_statement_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "insert_statement_4")) return false;
    returning_clause(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // ( LPAREN PRIMARY? KEY COMMA VALUE ( COMMA OPTIONS )? RPAREN )? values-clause
  public static boolean insert_values(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "insert_values")) return false;
    if (!nextTokenIs(b, "<insert values>", LPAREN, VALUES)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, INSERT_VALUES, "<insert values>");
    r = insert_values_0(b, l + 1);
    r = r && values_clause(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( LPAREN PRIMARY? KEY COMMA VALUE ( COMMA OPTIONS )? RPAREN )?
  private static boolean insert_values_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "insert_values_0")) return false;
    insert_values_0_0(b, l + 1);
    return true;
  }

  // LPAREN PRIMARY? KEY COMMA VALUE ( COMMA OPTIONS )? RPAREN
  private static boolean insert_values_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "insert_values_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LPAREN);
    r = r && insert_values_0_0_1(b, l + 1);
    r = r && consumeTokens(b, 0, KEY, COMMA, VALUE);
    r = r && insert_values_0_0_5(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, null, r);
    return r;
  }

  // PRIMARY?
  private static boolean insert_values_0_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "insert_values_0_0_1")) return false;
    consumeToken(b, PRIMARY);
    return true;
  }

  // ( COMMA OPTIONS )?
  private static boolean insert_values_0_0_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "insert_values_0_0_5")) return false;
    insert_values_0_0_5_0(b, l + 1);
    return true;
  }

  // COMMA OPTIONS
  private static boolean insert_values_0_0_5_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "insert_values_0_0_5_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, COMMA, OPTIONS);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // IS NOT? NULL |
  //             IS NOT? MISSING |
  //             IS NOT? VALUED
  public static boolean is_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "is_expr")) return false;
    if (!nextTokenIs(b, IS)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = is_expr_0(b, l + 1);
    if (!r) r = is_expr_1(b, l + 1);
    if (!r) r = is_expr_2(b, l + 1);
    exit_section_(b, m, IS_EXPR, r);
    return r;
  }

  // IS NOT? NULL
  private static boolean is_expr_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "is_expr_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IS);
    r = r && is_expr_0_1(b, l + 1);
    r = r && consumeToken(b, NULL);
    exit_section_(b, m, null, r);
    return r;
  }

  // NOT?
  private static boolean is_expr_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "is_expr_0_1")) return false;
    consumeToken(b, NOT);
    return true;
  }

  // IS NOT? MISSING
  private static boolean is_expr_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "is_expr_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IS);
    r = r && is_expr_1_1(b, l + 1);
    r = r && consumeToken(b, MISSING);
    exit_section_(b, m, null, r);
    return r;
  }

  // NOT?
  private static boolean is_expr_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "is_expr_1_1")) return false;
    consumeToken(b, NOT);
    return true;
  }

  // IS NOT? VALUED
  private static boolean is_expr_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "is_expr_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IS);
    r = r && is_expr_2_1(b, l + 1);
    r = r && consumeToken(b, VALUED);
    exit_section_(b, m, null, r);
    return r;
  }

  // NOT?
  private static boolean is_expr_2_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "is_expr_2_1")) return false;
    consumeToken(b, NOT);
    return true;
  }

  /* ********************************************************** */
  // ansi-join-clause | lookup-join-clause | index-join-clause
  public static boolean join_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "join_clause")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, JOIN_CLAUSE, "<join clause>");
    r = ansi_join_clause(b, l + 1);
    if (!r) r = lookup_join_clause(b, l + 1);
    if (!r) r = index_join_clause(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ordered-hint-json
  //              | gsi-hint-json
  //              | fts-hint-json
  //              | hash-hint-json
  //              | nl-hint-json
  public static boolean json_hint(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "json_hint")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, JSON_HINT, "<json hint>");
    r = ordered_hint_json(b, l + 1);
    if (!r) r = gsi_hint_json(b, l + 1);
    if (!r) r = fts_hint_json(b, l + 1);
    if (!r) r = hash_hint_json(b, l + 1);
    if (!r) r = nl_hint_json(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // LBRACE json-hint (COMMA json-hint )* RBRACE
  public static boolean json_hint_object(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "json_hint_object")) return false;
    if (!nextTokenIs(b, LBRACE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LBRACE);
    r = r && json_hint(b, l + 1);
    r = r && json_hint_object_2(b, l + 1);
    r = r && consumeToken(b, RBRACE);
    exit_section_(b, m, JSON_HINT_OBJECT, r);
    return r;
  }

  // (COMMA json-hint )*
  private static boolean json_hint_object_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "json_hint_object_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!json_hint_object_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "json_hint_object_2", c)) break;
    }
    return true;
  }

  // COMMA json-hint
  private static boolean json_hint_object_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "json_hint_object_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && json_hint(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // index-order
  public static boolean key_attribs(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "key_attribs")) return false;
    if (!nextTokenIs(b, "<key attribs>", ASC, DESC)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, KEY_ATTRIBS, "<key attribs>");
    r = index_order(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // expr
  public static boolean key_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "key_expr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, KEY_EXPR, "<key expr>");
    r = expr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // LBRACKET keyspace-object ( COMMA keyspace-object )* RBRACKET
  public static boolean keyspace_array(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keyspace_array")) return false;
    if (!nextTokenIs(b, LBRACKET)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LBRACKET);
    r = r && keyspace_object(b, l + 1);
    r = r && keyspace_array_2(b, l + 1);
    r = r && consumeToken(b, RBRACKET);
    exit_section_(b, m, KEYSPACE_ARRAY, r);
    return r;
  }

  // ( COMMA keyspace-object )*
  private static boolean keyspace_array_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keyspace_array_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!keyspace_array_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "keyspace_array_2", c)) break;
    }
    return true;
  }

  // COMMA keyspace-object
  private static boolean keyspace_array_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keyspace_array_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && keyspace_object(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // namespace-ref SEMICOLON bucket-ref DOT scope-ref DOT collection-ref
  public static boolean keyspace_full(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keyspace_full")) return false;
    if (!nextTokenIs(b, "<keyspace full>", ESCAPED_IDENTIFIER, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, KEYSPACE_FULL, "<keyspace full>");
    r = namespace_ref(b, l + 1);
    r = r && consumeToken(b, SEMICOLON);
    r = r && bucket_ref(b, l + 1);
    r = r && consumeToken(b, DOT);
    r = r && scope_ref(b, l + 1);
    r = r && consumeToken(b, DOT);
    r = r && collection_ref(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // LBRACE keyspace-property RBRACE
  public static boolean keyspace_object(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keyspace_object")) return false;
    if (!nextTokenIs(b, LBRACE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LBRACE);
    r = r && keyspace_property(b, l + 1);
    r = r && consumeToken(b, RBRACE);
    exit_section_(b, m, KEYSPACE_OBJECT, r);
    return r;
  }

  /* ********************************************************** */
  // collection-ref
  public static boolean keyspace_partial(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keyspace_partial")) return false;
    if (!nextTokenIs(b, "<keyspace partial>", ESCAPED_IDENTIFIER, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, KEYSPACE_PARTIAL, "<keyspace partial>");
    r = collection_ref(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ( namespace-ref SEMICOLON )? bucket-ref ( DOT scope-ref DOT collection-ref )?
  public static boolean keyspace_path(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keyspace_path")) return false;
    if (!nextTokenIs(b, "<keyspace path>", ESCAPED_IDENTIFIER, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, KEYSPACE_PATH, "<keyspace path>");
    r = keyspace_path_0(b, l + 1);
    r = r && bucket_ref(b, l + 1);
    r = r && keyspace_path_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( namespace-ref SEMICOLON )?
  private static boolean keyspace_path_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keyspace_path_0")) return false;
    keyspace_path_0_0(b, l + 1);
    return true;
  }

  // namespace-ref SEMICOLON
  private static boolean keyspace_path_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keyspace_path_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = namespace_ref(b, l + 1);
    r = r && consumeToken(b, SEMICOLON);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( DOT scope-ref DOT collection-ref )?
  private static boolean keyspace_path_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keyspace_path_2")) return false;
    keyspace_path_2_0(b, l + 1);
    return true;
  }

  // DOT scope-ref DOT collection-ref
  private static boolean keyspace_path_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keyspace_path_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, DOT);
    r = r && scope_ref(b, l + 1);
    r = r && consumeToken(b, DOT);
    r = r && collection_ref(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ( namespace-ref SEMICOLON )? bucket-ref
  public static boolean keyspace_prefix(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keyspace_prefix")) return false;
    if (!nextTokenIs(b, "<keyspace prefix>", ESCAPED_IDENTIFIER, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, KEYSPACE_PREFIX, "<keyspace prefix>");
    r = keyspace_prefix_0(b, l + 1);
    r = r && bucket_ref(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( namespace-ref SEMICOLON )?
  private static boolean keyspace_prefix_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keyspace_prefix_0")) return false;
    keyspace_prefix_0_0(b, l + 1);
    return true;
  }

  // namespace-ref SEMICOLON
  private static boolean keyspace_prefix_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keyspace_prefix_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = namespace_ref(b, l + 1);
    r = r && consumeToken(b, SEMICOLON);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ( QUOTED_KEYSPACE | QUOTED_ALIAS ) SEMICOLON DQUOTE keyspace-statement DQUOTE
  public static boolean keyspace_property(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keyspace_property")) return false;
    if (!nextTokenIs(b, "<keyspace property>", QUOTED_ALIAS, QUOTED_KEYSPACE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, KEYSPACE_PROPERTY, "<keyspace property>");
    r = keyspace_property_0(b, l + 1);
    r = r && consumeTokens(b, 0, SEMICOLON, DQUOTE);
    r = r && keyspace_statement(b, l + 1);
    r = r && consumeToken(b, DQUOTE);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // QUOTED_KEYSPACE | QUOTED_ALIAS
  private static boolean keyspace_property_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keyspace_property_0")) return false;
    boolean r;
    r = consumeToken(b, QUOTED_KEYSPACE);
    if (!r) r = consumeToken(b, QUOTED_ALIAS);
    return r;
  }

  /* ********************************************************** */
  // keyspace-path | keyspace-partial
  public static boolean keyspace_ref(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keyspace_ref")) return false;
    if (!nextTokenIs(b, "<keyspace ref>", ESCAPED_IDENTIFIER, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, KEYSPACE_REF, "<keyspace ref>");
    r = keyspace_path(b, l + 1);
    if (!r) r = keyspace_partial(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // KEYSPACE identifier-ref
  public static boolean keyspace_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keyspace_statement")) return false;
    if (!nextTokenIs(b, KEYSPACE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, KEYSPACE);
    r = r && identifier_ref(b, l + 1);
    exit_section_(b, m, KEYSPACE_STATEMENT, r);
    return r;
  }

  /* ********************************************************** */
  // index-order include-missing? | include-missing index-order?
  public static boolean lead_key_attribs(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lead_key_attribs")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LEAD_KEY_ATTRIBS, "<lead key attribs>");
    r = lead_key_attribs_0(b, l + 1);
    if (!r) r = lead_key_attribs_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // index-order include-missing?
  private static boolean lead_key_attribs_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lead_key_attribs_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = index_order(b, l + 1);
    r = r && lead_key_attribs_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // include-missing?
  private static boolean lead_key_attribs_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lead_key_attribs_0_1")) return false;
    include_missing(b, l + 1);
    return true;
  }

  // include-missing index-order?
  private static boolean lead_key_attribs_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lead_key_attribs_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = include_missing(b, l + 1);
    r = r && lead_key_attribs_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // index-order?
  private static boolean lead_key_attribs_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lead_key_attribs_1_1")) return false;
    index_order(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // LET alias EQUAL expr ( COMMA alias EQUAL expr )*
  public static boolean let_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "let_clause")) return false;
    if (!nextTokenIs(b, LET)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LET);
    r = r && alias(b, l + 1);
    r = r && consumeToken(b, EQUAL);
    r = r && expr(b, l + 1);
    r = r && let_clause_4(b, l + 1);
    exit_section_(b, m, LET_CLAUSE, r);
    return r;
  }

  // ( COMMA alias EQUAL expr )*
  private static boolean let_clause_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "let_clause_4")) return false;
    while (true) {
      int c = current_position_(b);
      if (!let_clause_4_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "let_clause_4", c)) break;
    }
    return true;
  }

  // COMMA alias EQUAL expr
  private static boolean let_clause_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "let_clause_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && alias(b, l + 1);
    r = r && consumeToken(b, EQUAL);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // LETTING alias EQUAL expr ( COMMA alias EQUAL expr )*
  public static boolean letting_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "letting_clause")) return false;
    if (!nextTokenIs(b, LETTING)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LETTING);
    r = r && alias(b, l + 1);
    r = r && consumeToken(b, EQUAL);
    r = r && expr(b, l + 1);
    r = r && letting_clause_4(b, l + 1);
    exit_section_(b, m, LETTING_CLAUSE, r);
    return r;
  }

  // ( COMMA alias EQUAL expr )*
  private static boolean letting_clause_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "letting_clause_4")) return false;
    while (true) {
      int c = current_position_(b);
      if (!letting_clause_4_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "letting_clause_4", c)) break;
    }
    return true;
  }

  // COMMA alias EQUAL expr
  private static boolean letting_clause_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "letting_clause_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && alias(b, l + 1);
    r = r && consumeToken(b, EQUAL);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // NOT? LIKE expr
  public static boolean like_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "like_expr")) return false;
    if (!nextTokenIs(b, "<like expr>", LIKE, NOT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LIKE_EXPR, "<like expr>");
    r = like_expr_0(b, l + 1);
    r = r && consumeToken(b, LIKE);
    r = r && expr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // NOT?
  private static boolean like_expr_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "like_expr_0")) return false;
    consumeToken(b, NOT);
    return true;
  }

  /* ********************************************************** */
  // LIMIT expr
  public static boolean limit_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "limit_clause")) return false;
    if (!nextTokenIs(b, LIMIT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LIMIT);
    r = r && expr(b, l + 1);
    exit_section_(b, m, LIMIT_CLAUSE, r);
    return r;
  }

  /* ********************************************************** */
  // LINE_HINT_OPEN hints
  public static boolean line_hint_comment(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "line_hint_comment")) return false;
    if (!nextTokenIs(b, LINE_HINT_OPEN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LINE_HINT_OPEN);
    r = r && hints(b, l + 1);
    exit_section_(b, m, LINE_HINT_COMMENT, r);
    return r;
  }

  /* ********************************************************** */
  // str | nbr | bool | NULL | MISSING
  public static boolean literal(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literal")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LITERAL, "<literal>");
    r = str(b, l + 1);
    if (!r) r = consumeToken(b, NBR);
    if (!r) r = bool(b, l + 1);
    if (!r) r = consumeToken(b, NULL);
    if (!r) r = consumeToken(b, MISSING);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // and-expr | or-expr | not-expr
  public static boolean logical_term(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "logical_term")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LOGICAL_TERM, "<logical term>");
    r = and_expr(b, l + 1);
    if (!r) r = or_expr(b, l + 1);
    if (!r) r = not_expr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // lookup-join-type? JOIN lookup-join-rhs lookup-join-predicate
  public static boolean lookup_join_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lookup_join_clause")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LOOKUP_JOIN_CLAUSE, "<lookup join clause>");
    r = lookup_join_clause_0(b, l + 1);
    r = r && consumeToken(b, JOIN);
    r = r && lookup_join_rhs(b, l + 1);
    r = r && lookup_join_predicate(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // lookup-join-type?
  private static boolean lookup_join_clause_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lookup_join_clause_0")) return false;
    lookup_join_type(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // ON PRIMARY? KEYS expr
  public static boolean lookup_join_predicate(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lookup_join_predicate")) return false;
    if (!nextTokenIs(b, ON)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ON);
    r = r && lookup_join_predicate_1(b, l + 1);
    r = r && consumeToken(b, KEYS);
    r = r && expr(b, l + 1);
    exit_section_(b, m, LOOKUP_JOIN_PREDICATE, r);
    return r;
  }

  // PRIMARY?
  private static boolean lookup_join_predicate_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lookup_join_predicate_1")) return false;
    consumeToken(b, PRIMARY);
    return true;
  }

  /* ********************************************************** */
  // keyspace-ref ( AS? alias )?
  public static boolean lookup_join_rhs(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lookup_join_rhs")) return false;
    if (!nextTokenIs(b, "<lookup join rhs>", ESCAPED_IDENTIFIER, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LOOKUP_JOIN_RHS, "<lookup join rhs>");
    r = keyspace_ref(b, l + 1);
    r = r && lookup_join_rhs_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( AS? alias )?
  private static boolean lookup_join_rhs_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lookup_join_rhs_1")) return false;
    lookup_join_rhs_1_0(b, l + 1);
    return true;
  }

  // AS? alias
  private static boolean lookup_join_rhs_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lookup_join_rhs_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = lookup_join_rhs_1_0_0(b, l + 1);
    r = r && alias(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // AS?
  private static boolean lookup_join_rhs_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lookup_join_rhs_1_0_0")) return false;
    consumeToken(b, AS);
    return true;
  }

  /* ********************************************************** */
  // INNER | ( LEFT OUTER? )
  public static boolean lookup_join_type(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lookup_join_type")) return false;
    if (!nextTokenIs(b, "<lookup join type>", INNER, LEFT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LOOKUP_JOIN_TYPE, "<lookup join type>");
    r = consumeToken(b, INNER);
    if (!r) r = lookup_join_type_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // LEFT OUTER?
  private static boolean lookup_join_type_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lookup_join_type_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LEFT);
    r = r && lookup_join_type_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // OUTER?
  private static boolean lookup_join_type_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lookup_join_type_1_1")) return false;
    consumeToken(b, OUTER);
    return true;
  }

  /* ********************************************************** */
  // target-keyspace USING lookup-merge-source lookup-merge-predicate
  //                  lookup-merge-actions
  public static boolean lookup_merge(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lookup_merge")) return false;
    if (!nextTokenIs(b, "<lookup merge>", ESCAPED_IDENTIFIER, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LOOKUP_MERGE, "<lookup merge>");
    r = target_keyspace(b, l + 1);
    r = r && consumeToken(b, USING);
    r = r && lookup_merge_source(b, l + 1);
    r = r && lookup_merge_predicate(b, l + 1);
    r = r && lookup_merge_actions(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // merge-update? merge-delete? lookup-merge-insert?
  public static boolean lookup_merge_actions(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lookup_merge_actions")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LOOKUP_MERGE_ACTIONS, "<lookup merge actions>");
    r = lookup_merge_actions_0(b, l + 1);
    r = r && lookup_merge_actions_1(b, l + 1);
    r = r && lookup_merge_actions_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // merge-update?
  private static boolean lookup_merge_actions_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lookup_merge_actions_0")) return false;
    merge_update(b, l + 1);
    return true;
  }

  // merge-delete?
  private static boolean lookup_merge_actions_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lookup_merge_actions_1")) return false;
    merge_delete(b, l + 1);
    return true;
  }

  // lookup-merge-insert?
  private static boolean lookup_merge_actions_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lookup_merge_actions_2")) return false;
    lookup_merge_insert(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // WHEN NOT MATCHED THEN INSERT expr where-clause?
  public static boolean lookup_merge_insert(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lookup_merge_insert")) return false;
    if (!nextTokenIs(b, WHEN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, WHEN, NOT, MATCHED, THEN, INSERT);
    r = r && expr(b, l + 1);
    r = r && lookup_merge_insert_6(b, l + 1);
    exit_section_(b, m, LOOKUP_MERGE_INSERT, r);
    return r;
  }

  // where-clause?
  private static boolean lookup_merge_insert_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lookup_merge_insert_6")) return false;
    where_clause(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // ON PRIMARY? KEY expr
  public static boolean lookup_merge_predicate(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lookup_merge_predicate")) return false;
    if (!nextTokenIs(b, ON)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ON);
    r = r && lookup_merge_predicate_1(b, l + 1);
    r = r && consumeToken(b, KEY);
    r = r && expr(b, l + 1);
    exit_section_(b, m, LOOKUP_MERGE_PREDICATE, r);
    return r;
  }

  // PRIMARY?
  private static boolean lookup_merge_predicate_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lookup_merge_predicate_1")) return false;
    consumeToken(b, PRIMARY);
    return true;
  }

  /* ********************************************************** */
  // merge-source-keyspace use-clause? |
  //                         merge-source-subquery |
  //                         merge-source-expr
  public static boolean lookup_merge_source(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lookup_merge_source")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LOOKUP_MERGE_SOURCE, "<lookup merge source>");
    r = lookup_merge_source_0(b, l + 1);
    if (!r) r = merge_source_subquery(b, l + 1);
    if (!r) r = merge_source_expr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // merge-source-keyspace use-clause?
  private static boolean lookup_merge_source_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lookup_merge_source_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = merge_source_keyspace(b, l + 1);
    r = r && lookup_merge_source_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // use-clause?
  private static boolean lookup_merge_source_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lookup_merge_source_0_1")) return false;
    use_clause(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // lookup-nest-type? NEST lookup-nest-rhs lookup-nest-predicate
  public static boolean lookup_nest_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lookup_nest_clause")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LOOKUP_NEST_CLAUSE, "<lookup nest clause>");
    r = lookup_nest_clause_0(b, l + 1);
    r = r && consumeToken(b, NEST);
    r = r && lookup_nest_rhs(b, l + 1);
    r = r && lookup_nest_predicate(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // lookup-nest-type?
  private static boolean lookup_nest_clause_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lookup_nest_clause_0")) return false;
    lookup_nest_type(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // ON KEYS expr
  public static boolean lookup_nest_predicate(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lookup_nest_predicate")) return false;
    if (!nextTokenIs(b, ON)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, ON, KEYS);
    r = r && expr(b, l + 1);
    exit_section_(b, m, LOOKUP_NEST_PREDICATE, r);
    return r;
  }

  /* ********************************************************** */
  // keyspace-ref ( AS? alias )?
  public static boolean lookup_nest_rhs(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lookup_nest_rhs")) return false;
    if (!nextTokenIs(b, "<lookup nest rhs>", ESCAPED_IDENTIFIER, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LOOKUP_NEST_RHS, "<lookup nest rhs>");
    r = keyspace_ref(b, l + 1);
    r = r && lookup_nest_rhs_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( AS? alias )?
  private static boolean lookup_nest_rhs_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lookup_nest_rhs_1")) return false;
    lookup_nest_rhs_1_0(b, l + 1);
    return true;
  }

  // AS? alias
  private static boolean lookup_nest_rhs_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lookup_nest_rhs_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = lookup_nest_rhs_1_0_0(b, l + 1);
    r = r && alias(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // AS?
  private static boolean lookup_nest_rhs_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lookup_nest_rhs_1_0_0")) return false;
    consumeToken(b, AS);
    return true;
  }

  /* ********************************************************** */
  // INNER | ( LEFT OUTER? )
  public static boolean lookup_nest_type(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lookup_nest_type")) return false;
    if (!nextTokenIs(b, "<lookup nest type>", INNER, LEFT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LOOKUP_NEST_TYPE, "<lookup nest type>");
    r = consumeToken(b, INNER);
    if (!r) r = lookup_nest_type_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // LEFT OUTER?
  private static boolean lookup_nest_type_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lookup_nest_type_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LEFT);
    r = r && lookup_nest_type_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // OUTER?
  private static boolean lookup_nest_type_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lookup_nest_type_1_1")) return false;
    consumeToken(b, OUTER);
    return true;
  }

  /* ********************************************************** */
  // WHEN MATCHED THEN DELETE where-clause?
  public static boolean merge_delete(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "merge_delete")) return false;
    if (!nextTokenIs(b, WHEN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, WHEN, MATCHED, THEN, DELETE);
    r = r && merge_delete_4(b, l + 1);
    exit_section_(b, m, MERGE_DELETE, r);
    return r;
  }

  // where-clause?
  private static boolean merge_delete_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "merge_delete_4")) return false;
    where_clause(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // expr ( AS? alias )?
  public static boolean merge_source_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "merge_source_expr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, MERGE_SOURCE_EXPR, "<merge source expr>");
    r = expr(b, l + 1);
    r = r && merge_source_expr_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( AS? alias )?
  private static boolean merge_source_expr_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "merge_source_expr_1")) return false;
    merge_source_expr_1_0(b, l + 1);
    return true;
  }

  // AS? alias
  private static boolean merge_source_expr_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "merge_source_expr_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = merge_source_expr_1_0_0(b, l + 1);
    r = r && alias(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // AS?
  private static boolean merge_source_expr_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "merge_source_expr_1_0_0")) return false;
    consumeToken(b, AS);
    return true;
  }

  /* ********************************************************** */
  // keyspace-ref ( AS? alias )?
  public static boolean merge_source_keyspace(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "merge_source_keyspace")) return false;
    if (!nextTokenIs(b, "<merge source keyspace>", ESCAPED_IDENTIFIER, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, MERGE_SOURCE_KEYSPACE, "<merge source keyspace>");
    r = keyspace_ref(b, l + 1);
    r = r && merge_source_keyspace_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( AS? alias )?
  private static boolean merge_source_keyspace_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "merge_source_keyspace_1")) return false;
    merge_source_keyspace_1_0(b, l + 1);
    return true;
  }

  // AS? alias
  private static boolean merge_source_keyspace_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "merge_source_keyspace_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = merge_source_keyspace_1_0_0(b, l + 1);
    r = r && alias(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // AS?
  private static boolean merge_source_keyspace_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "merge_source_keyspace_1_0_0")) return false;
    consumeToken(b, AS);
    return true;
  }

  /* ********************************************************** */
  // subquery-expr AS? alias
  public static boolean merge_source_subquery(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "merge_source_subquery")) return false;
    if (!nextTokenIs(b, LPAREN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = subquery_expr(b, l + 1);
    r = r && merge_source_subquery_1(b, l + 1);
    r = r && alias(b, l + 1);
    exit_section_(b, m, MERGE_SOURCE_SUBQUERY, r);
    return r;
  }

  // AS?
  private static boolean merge_source_subquery_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "merge_source_subquery_1")) return false;
    consumeToken(b, AS);
    return true;
  }

  /* ********************************************************** */
  // MERGE INTO ( ansi-merge | lookup-merge ) limit-clause? returning-clause?
  public static boolean merge_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "merge_statement")) return false;
    if (!nextTokenIs(b, MERGE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, MERGE, INTO);
    r = r && merge_statement_2(b, l + 1);
    r = r && merge_statement_3(b, l + 1);
    r = r && merge_statement_4(b, l + 1);
    exit_section_(b, m, MERGE_STATEMENT, r);
    return r;
  }

  // ansi-merge | lookup-merge
  private static boolean merge_statement_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "merge_statement_2")) return false;
    boolean r;
    r = ansi_merge(b, l + 1);
    if (!r) r = lookup_merge(b, l + 1);
    return r;
  }

  // limit-clause?
  private static boolean merge_statement_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "merge_statement_3")) return false;
    limit_clause(b, l + 1);
    return true;
  }

  // returning-clause?
  private static boolean merge_statement_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "merge_statement_4")) return false;
    returning_clause(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // WHEN MATCHED THEN UPDATE set-clause? unset-clause? where-clause?
  public static boolean merge_update(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "merge_update")) return false;
    if (!nextTokenIs(b, WHEN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, WHEN, MATCHED, THEN, UPDATE);
    r = r && merge_update_4(b, l + 1);
    r = r && merge_update_5(b, l + 1);
    r = r && merge_update_6(b, l + 1);
    exit_section_(b, m, MERGE_UPDATE, r);
    return r;
  }

  // set-clause?
  private static boolean merge_update_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "merge_update_4")) return false;
    set_clause(b, l + 1);
    return true;
  }

  // unset-clause?
  private static boolean merge_update_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "merge_update_5")) return false;
    unset_clause(b, l + 1);
    return true;
  }

  // where-clause?
  private static boolean merge_update_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "merge_update_6")) return false;
    where_clause(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // USE ( ansi-hint-terms other-hint-terms |
  //                            other-hint-terms ansi-hint-terms )
  public static boolean multiple_hints(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "multiple_hints")) return false;
    if (!nextTokenIs(b, USE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, USE);
    r = r && multiple_hints_1(b, l + 1);
    exit_section_(b, m, MULTIPLE_HINTS, r);
    return r;
  }

  // ansi-hint-terms other-hint-terms |
  //                            other-hint-terms ansi-hint-terms
  private static boolean multiple_hints_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "multiple_hints_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = multiple_hints_1_0(b, l + 1);
    if (!r) r = multiple_hints_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ansi-hint-terms other-hint-terms
  private static boolean multiple_hints_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "multiple_hints_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ansi_hint_terms(b, l + 1);
    r = r && other_hint_terms(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // other-hint-terms ansi-hint-terms
  private static boolean multiple_hints_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "multiple_hints_1_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = other_hint_terms(b, l + 1);
    r = r && ansi_hint_terms(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // identifier-ref
  public static boolean name(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "name")) return false;
    if (!nextTokenIs(b, "<name>", ESCAPED_IDENTIFIER, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NAME, "<name>");
    r = identifier_ref(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // identifier-ref
  public static boolean name_var(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "name_var")) return false;
    if (!nextTokenIs(b, "<name var>", ESCAPED_IDENTIFIER, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NAME_VAR, "<name var>");
    r = identifier_ref(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // identifier-ref
  public static boolean namespace_ref(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "namespace_ref")) return false;
    if (!nextTokenIs(b, "<namespace ref>", ESCAPED_IDENTIFIER, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NAMESPACE_REF, "<namespace ref>");
    r = identifier_ref(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ansi-nest-clause | lookup-nest-clause | index-nest-clause
  public static boolean nest_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nest_clause")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NEST_CLAUSE, "<nest clause>");
    r = ansi_nest_clause(b, l + 1);
    if (!r) r = lookup_nest_clause(b, l + 1);
    if (!r) r = index_nest_clause(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // field-expr | element-expr | slice-expr
  public static boolean nested_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nested_expr")) return false;
    if (!nextTokenIs(b, "<nested expr>", DOT, LBRACKET)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NESTED_EXPR, "<nested expr>");
    r = field_expr(b, l + 1);
    if (!r) r = element_expr(b, l + 1);
    if (!r) r = slice_expr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // QUOTED_USE_NS SEMICOLON ( keyspace-array | keyspace-object )
  public static boolean nl_hint_json(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nl_hint_json")) return false;
    if (!nextTokenIs(b, QUOTED_USE_NS)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, QUOTED_USE_NS, SEMICOLON);
    r = r && nl_hint_json_2(b, l + 1);
    exit_section_(b, m, NL_HINT_JSON, r);
    return r;
  }

  // keyspace-array | keyspace-object
  private static boolean nl_hint_json_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nl_hint_json_2")) return false;
    boolean r;
    r = keyspace_array(b, l + 1);
    if (!r) r = keyspace_object(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // USE_NL LPAREN ( keyspace-statement )+ RPAREN
  public static boolean nl_hint_simple(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nl_hint_simple")) return false;
    if (!nextTokenIs(b, USE_NL)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, USE_NL, LPAREN);
    r = r && nl_hint_simple_2(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, NL_HINT_SIMPLE, r);
    return r;
  }

  // ( keyspace-statement )+
  private static boolean nl_hint_simple_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nl_hint_simple_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = nl_hint_simple_2_0(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!nl_hint_simple_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "nl_hint_simple_2", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // ( keyspace-statement )
  private static boolean nl_hint_simple_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nl_hint_simple_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = keyspace_statement(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // NOT cond
  public static boolean not_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "not_expr")) return false;
    if (!nextTokenIs(b, NOT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, NOT);
    r = r && cond(b, l + 1);
    exit_section_(b, m, NOT_EXPR, r);
    return r;
  }

  /* ********************************************************** */
  // FROM ( FIRST | LAST )
  public static boolean nthval_from(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nthval_from")) return false;
    if (!nextTokenIs(b, FROM)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, FROM);
    r = r && nthval_from_1(b, l + 1);
    exit_section_(b, m, NTHVAL_FROM, r);
    return r;
  }

  // FIRST | LAST
  private static boolean nthval_from_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nthval_from_1")) return false;
    boolean r;
    r = consumeToken(b, FIRST);
    if (!r) r = consumeToken(b, LAST);
    return r;
  }

  /* ********************************************************** */
  // ( RESPECT | IGNORE ) NULLS
  public static boolean nulls_treatment(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nulls_treatment")) return false;
    if (!nextTokenIs(b, "<nulls treatment>", IGNORE, RESPECT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NULLS_TREATMENT, "<nulls treatment>");
    r = nulls_treatment_0(b, l + 1);
    r = r && consumeToken(b, NULLS);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // RESPECT | IGNORE
  private static boolean nulls_treatment_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nulls_treatment_0")) return false;
    boolean r;
    r = consumeToken(b, RESPECT);
    if (!r) r = consumeToken(b, IGNORE);
    return r;
  }

  /* ********************************************************** */
  // str
  public static boolean obj(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "obj")) return false;
    if (!nextTokenIs(b, "<obj>", DQUOTE, QUOTE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OBJ, "<obj>");
    r = str(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // OFFSET expr
  public static boolean offset_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "offset_clause")) return false;
    if (!nextTokenIs(b, OFFSET)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OFFSET);
    r = r && expr(b, l + 1);
    exit_section_(b, m, OFFSET_CLAUSE, r);
    return r;
  }

  /* ********************************************************** */
  // QUOTED_OPTION SEMICOLON ( QUOTED_BUILD | QUOTED_PROBE | NULL )
  public static boolean option_property(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "option_property")) return false;
    if (!nextTokenIs(b, QUOTED_OPTION)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, QUOTED_OPTION, SEMICOLON);
    r = r && option_property_2(b, l + 1);
    exit_section_(b, m, OPTION_PROPERTY, r);
    return r;
  }

  // QUOTED_BUILD | QUOTED_PROBE | NULL
  private static boolean option_property_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "option_property_2")) return false;
    boolean r;
    r = consumeToken(b, QUOTED_BUILD);
    if (!r) r = consumeToken(b, QUOTED_PROBE);
    if (!r) r = consumeToken(b, NULL);
    return r;
  }

  /* ********************************************************** */
  // OR cond
  public static boolean or_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "or_expr")) return false;
    if (!nextTokenIs(b, OR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OR);
    r = r && cond(b, l + 1);
    exit_section_(b, m, OR_EXPR, r);
    return r;
  }

  /* ********************************************************** */
  // ORDER BY ordering-term ( COMMA ordering-term )*
  public static boolean order_by_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "order_by_clause")) return false;
    if (!nextTokenIs(b, ORDER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, ORDER, BY);
    r = r && ordering_term(b, l + 1);
    r = r && order_by_clause_3(b, l + 1);
    exit_section_(b, m, ORDER_BY_CLAUSE, r);
    return r;
  }

  // ( COMMA ordering-term )*
  private static boolean order_by_clause_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "order_by_clause_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!order_by_clause_3_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "order_by_clause_3", c)) break;
    }
    return true;
  }

  // COMMA ordering-term
  private static boolean order_by_clause_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "order_by_clause_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && ordering_term(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // QUOTED_ORDERED SEMICOLON TRUE
  public static boolean ordered_hint_json(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ordered_hint_json")) return false;
    if (!nextTokenIs(b, QUOTED_ORDERED)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, QUOTED_ORDERED, SEMICOLON, TRUE);
    exit_section_(b, m, ORDERED_HINT_JSON, r);
    return r;
  }

  /* ********************************************************** */
  // ORDERED
  public static boolean ordered_hint_simple(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ordered_hint_simple")) return false;
    if (!nextTokenIs(b, ORDERED)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ORDERED);
    exit_section_(b, m, ORDERED_HINT_SIMPLE, r);
    return r;
  }

  /* ********************************************************** */
  // expr ( ASC | DESC )? ( NULLS ( FIRST | LAST ) )?
  public static boolean ordering_term(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ordering_term")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ORDERING_TERM, "<ordering term>");
    r = expr(b, l + 1);
    r = r && ordering_term_1(b, l + 1);
    r = r && ordering_term_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( ASC | DESC )?
  private static boolean ordering_term_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ordering_term_1")) return false;
    ordering_term_1_0(b, l + 1);
    return true;
  }

  // ASC | DESC
  private static boolean ordering_term_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ordering_term_1_0")) return false;
    boolean r;
    r = consumeToken(b, ASC);
    if (!r) r = consumeToken(b, DESC);
    return r;
  }

  // ( NULLS ( FIRST | LAST ) )?
  private static boolean ordering_term_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ordering_term_2")) return false;
    ordering_term_2_0(b, l + 1);
    return true;
  }

  // NULLS ( FIRST | LAST )
  private static boolean ordering_term_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ordering_term_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, NULLS);
    r = r && ordering_term_2_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // FIRST | LAST
  private static boolean ordering_term_2_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ordering_term_2_0_1")) return false;
    boolean r;
    r = consumeToken(b, FIRST);
    if (!r) r = consumeToken(b, LAST);
    return r;
  }

  /* ********************************************************** */
  // function-name LPAREN ( expr ( COMMA expr )* )? RPAREN
  public static boolean ordinary_function(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ordinary_function")) return false;
    if (!nextTokenIs(b, "<ordinary function>", ESCAPED_IDENTIFIER, IDENTIFIER)) return false;
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
    r = expr(b, l + 1);
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
    r = consumeToken(b, COMMA);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // use-index-term | use-keys-term
  public static boolean other_hint_terms(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "other_hint_terms")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OTHER_HINT_TERMS, "<other hint terms>");
    r = use_index_term(b, l + 1);
    if (!r) r = use_keys_term(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // alter-index
  //                   | build-index
  //                   | execute-function
  public static boolean other_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "other_statement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OTHER_STATEMENT, "<other statement>");
    r = alter_index(b, l + 1);
    if (!r) r = build_index(b, l + 1);
    if (!r) r = execute_function(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // OVER ( LPAREN window-definition RPAREN | window-ref )
  public static boolean over_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "over_clause")) return false;
    if (!nextTokenIs(b, OVER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OVER);
    r = r && over_clause_1(b, l + 1);
    exit_section_(b, m, OVER_CLAUSE, r);
    return r;
  }

  // LPAREN window-definition RPAREN | window-ref
  private static boolean over_clause_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "over_clause_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = over_clause_1_0(b, l + 1);
    if (!r) r = window_ref(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // LPAREN window-definition RPAREN
  private static boolean over_clause_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "over_clause_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LPAREN);
    r = r && window_definition(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // PAIRS LPAREN ( SELF | index-key-object ) RPAREN
  public static boolean pairs_function(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pairs_function")) return false;
    if (!nextTokenIs(b, PAIRS)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, PAIRS, LPAREN);
    r = r && pairs_function_2(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, PAIRS_FUNCTION, r);
    return r;
  }

  // SELF | index-key-object
  private static boolean pairs_function_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pairs_function_2")) return false;
    boolean r;
    r = consumeToken(b, SELF);
    if (!r) r = index_key_object(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // array-expr
  public static boolean parameters(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameters")) return false;
    if (!nextTokenIs(b, "<parameters>", ALL, DISTINCT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PARAMETERS, "<parameters>");
    r = array_expr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // identifier-ref ( COMMA identifier-ref )* | ELLIPSIS
  public static boolean params(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "params")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PARAMS, "<params>");
    r = params_0(b, l + 1);
    if (!r) r = consumeToken(b, ELLIPSIS);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // identifier-ref ( COMMA identifier-ref )*
  private static boolean params_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "params_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = identifier_ref(b, l + 1);
    r = r && params_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( COMMA identifier-ref )*
  private static boolean params_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "params_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!params_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "params_0_1", c)) break;
    }
    return true;
  }

  // COMMA identifier-ref
  private static boolean params_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "params_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && identifier_ref(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // expr
  public static boolean partition_key_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "partition_key_expr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PARTITION_KEY_EXPR, "<partition key expr>");
    r = expr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // identifier-ref ( LBRACKET expr RBRACKET )* ( DOT identifier-ref ( LBRACKET expr RBRACKET )* )*
  public static boolean path(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "path")) return false;
    if (!nextTokenIs(b, "<path>", ESCAPED_IDENTIFIER, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PATH, "<path>");
    r = identifier_ref(b, l + 1);
    r = r && path_1(b, l + 1);
    r = r && path_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( LBRACKET expr RBRACKET )*
  private static boolean path_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "path_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!path_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "path_1", c)) break;
    }
    return true;
  }

  // LBRACKET expr RBRACKET
  private static boolean path_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "path_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LBRACKET);
    r = r && expr(b, l + 1);
    r = r && consumeToken(b, RBRACKET);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( DOT identifier-ref ( LBRACKET expr RBRACKET )* )*
  private static boolean path_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "path_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!path_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "path_2", c)) break;
    }
    return true;
  }

  // DOT identifier-ref ( LBRACKET expr RBRACKET )*
  private static boolean path_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "path_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, DOT);
    r = r && identifier_ref(b, l + 1);
    r = r && path_2_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( LBRACKET expr RBRACKET )*
  private static boolean path_2_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "path_2_0_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!path_2_0_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "path_2_0_2", c)) break;
    }
    return true;
  }

  // LBRACKET expr RBRACKET
  private static boolean path_2_0_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "path_2_0_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LBRACKET);
    r = r && expr(b, l + 1);
    r = r && consumeToken(b, RBRACKET);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ( ALL | DISTINCT )? ( result-expr ( COMMA result-expr )* |
  //                ( RAW | ELEMENT | VALUE ) expr ( AS? alias )? )
  public static boolean projection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "projection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PROJECTION, "<projection>");
    r = projection_0(b, l + 1);
    r = r && projection_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( ALL | DISTINCT )?
  private static boolean projection_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "projection_0")) return false;
    projection_0_0(b, l + 1);
    return true;
  }

  // ALL | DISTINCT
  private static boolean projection_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "projection_0_0")) return false;
    boolean r;
    r = consumeToken(b, ALL);
    if (!r) r = consumeToken(b, DISTINCT);
    return r;
  }

  // result-expr ( COMMA result-expr )* |
  //                ( RAW | ELEMENT | VALUE ) expr ( AS? alias )?
  private static boolean projection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "projection_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = projection_1_0(b, l + 1);
    if (!r) r = projection_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // result-expr ( COMMA result-expr )*
  private static boolean projection_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "projection_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = result_expr(b, l + 1);
    r = r && projection_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( COMMA result-expr )*
  private static boolean projection_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "projection_1_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!projection_1_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "projection_1_0_1", c)) break;
    }
    return true;
  }

  // COMMA result-expr
  private static boolean projection_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "projection_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && result_expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( RAW | ELEMENT | VALUE ) expr ( AS? alias )?
  private static boolean projection_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "projection_1_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = projection_1_1_0(b, l + 1);
    r = r && expr(b, l + 1);
    r = r && projection_1_1_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // RAW | ELEMENT | VALUE
  private static boolean projection_1_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "projection_1_1_0")) return false;
    boolean r;
    r = consumeToken(b, RAW);
    if (!r) r = consumeToken(b, ELEMENT);
    if (!r) r = consumeToken(b, VALUE);
    return r;
  }

  // ( AS? alias )?
  private static boolean projection_1_1_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "projection_1_1_2")) return false;
    projection_1_1_2_0(b, l + 1);
    return true;
  }

  // AS? alias
  private static boolean projection_1_1_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "projection_1_1_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = projection_1_1_2_0_0(b, l + 1);
    r = r && alias(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // AS?
  private static boolean projection_1_1_2_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "projection_1_1_2_0_0")) return false;
    consumeToken(b, AS);
    return true;
  }

  /* ********************************************************** */
  // ( ( ANY | SOME ) ( AND EVERY )? | EVERY ) range-expr SATISFIES cond END
  public static boolean range_cond(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "range_cond")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, RANGE_COND, "<range cond>");
    r = range_cond_0(b, l + 1);
    r = r && range_expr(b, l + 1);
    r = r && consumeToken(b, SATISFIES);
    r = r && cond(b, l + 1);
    r = r && consumeToken(b, END);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( ANY | SOME ) ( AND EVERY )? | EVERY
  private static boolean range_cond_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "range_cond_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = range_cond_0_0(b, l + 1);
    if (!r) r = consumeToken(b, EVERY);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( ANY | SOME ) ( AND EVERY )?
  private static boolean range_cond_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "range_cond_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = range_cond_0_0_0(b, l + 1);
    r = r && range_cond_0_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ANY | SOME
  private static boolean range_cond_0_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "range_cond_0_0_0")) return false;
    boolean r;
    r = consumeToken(b, ANY);
    if (!r) r = consumeToken(b, SOME);
    return r;
  }

  // ( AND EVERY )?
  private static boolean range_cond_0_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "range_cond_0_0_1")) return false;
    range_cond_0_0_1_0(b, l + 1);
    return true;
  }

  // AND EVERY
  private static boolean range_cond_0_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "range_cond_0_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, AND, EVERY);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ( name-var SEMICOLON )? var ( IN | WITHIN ) expr
  //             ( COMMA ( name-var SEMICOLON )? var ( IN | WITHIN ) expr )*
  public static boolean range_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "range_expr")) return false;
    if (!nextTokenIs(b, "<range expr>", ESCAPED_IDENTIFIER, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, RANGE_EXPR, "<range expr>");
    r = range_expr_0(b, l + 1);
    r = r && var(b, l + 1);
    r = r && range_expr_2(b, l + 1);
    r = r && expr(b, l + 1);
    r = r && range_expr_4(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( name-var SEMICOLON )?
  private static boolean range_expr_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "range_expr_0")) return false;
    range_expr_0_0(b, l + 1);
    return true;
  }

  // name-var SEMICOLON
  private static boolean range_expr_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "range_expr_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = name_var(b, l + 1);
    r = r && consumeToken(b, SEMICOLON);
    exit_section_(b, m, null, r);
    return r;
  }

  // IN | WITHIN
  private static boolean range_expr_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "range_expr_2")) return false;
    boolean r;
    r = consumeToken(b, IN);
    if (!r) r = consumeToken(b, WITHIN);
    return r;
  }

  // ( COMMA ( name-var SEMICOLON )? var ( IN | WITHIN ) expr )*
  private static boolean range_expr_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "range_expr_4")) return false;
    while (true) {
      int c = current_position_(b);
      if (!range_expr_4_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "range_expr_4", c)) break;
    }
    return true;
  }

  // COMMA ( name-var SEMICOLON )? var ( IN | WITHIN ) expr
  private static boolean range_expr_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "range_expr_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && range_expr_4_0_1(b, l + 1);
    r = r && var(b, l + 1);
    r = r && range_expr_4_0_3(b, l + 1);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( name-var SEMICOLON )?
  private static boolean range_expr_4_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "range_expr_4_0_1")) return false;
    range_expr_4_0_1_0(b, l + 1);
    return true;
  }

  // name-var SEMICOLON
  private static boolean range_expr_4_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "range_expr_4_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = name_var(b, l + 1);
    r = r && consumeToken(b, SEMICOLON);
    exit_section_(b, m, null, r);
    return r;
  }

  // IN | WITHIN
  private static boolean range_expr_4_0_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "range_expr_4_0_3")) return false;
    boolean r;
    r = consumeToken(b, IN);
    if (!r) r = consumeToken(b, WITHIN);
    return r;
  }

  /* ********************************************************** */
  // EQUAL expr |
  //                     DOUBLE_EQUAL expr |
  //                     NOT_EQUAL expr |
  //                     LESSTHAN_OR_MORETHAN expr |
  //                     MORETHAN expr |
  //                     MORETHAN_OR_EQUAL expr |
  //                     LESSTHAN expr |
  //                     LESSTHAN_OR_EQUAL expr
  public static boolean relational_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "relational_expr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, RELATIONAL_EXPR, "<relational expr>");
    r = relational_expr_0(b, l + 1);
    if (!r) r = relational_expr_1(b, l + 1);
    if (!r) r = relational_expr_2(b, l + 1);
    if (!r) r = relational_expr_3(b, l + 1);
    if (!r) r = relational_expr_4(b, l + 1);
    if (!r) r = relational_expr_5(b, l + 1);
    if (!r) r = relational_expr_6(b, l + 1);
    if (!r) r = relational_expr_7(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // EQUAL expr
  private static boolean relational_expr_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "relational_expr_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EQUAL);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // DOUBLE_EQUAL expr
  private static boolean relational_expr_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "relational_expr_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, DOUBLE_EQUAL);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // NOT_EQUAL expr
  private static boolean relational_expr_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "relational_expr_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, NOT_EQUAL);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // LESSTHAN_OR_MORETHAN expr
  private static boolean relational_expr_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "relational_expr_3")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LESSTHAN_OR_MORETHAN);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // MORETHAN expr
  private static boolean relational_expr_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "relational_expr_4")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, MORETHAN);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // MORETHAN_OR_EQUAL expr
  private static boolean relational_expr_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "relational_expr_5")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, MORETHAN_OR_EQUAL);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // LESSTHAN expr
  private static boolean relational_expr_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "relational_expr_6")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LESSTHAN);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // LESSTHAN_OR_EQUAL expr
  private static boolean relational_expr_7(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "relational_expr_7")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LESSTHAN_OR_EQUAL);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ( path DOT )? ASTERISK | expr ( AS? alias )?
  public static boolean result_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "result_expr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, RESULT_EXPR, "<result expr>");
    r = result_expr_0(b, l + 1);
    if (!r) r = result_expr_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( path DOT )? ASTERISK
  private static boolean result_expr_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "result_expr_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = result_expr_0_0(b, l + 1);
    r = r && consumeToken(b, ASTERISK);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( path DOT )?
  private static boolean result_expr_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "result_expr_0_0")) return false;
    result_expr_0_0_0(b, l + 1);
    return true;
  }

  // path DOT
  private static boolean result_expr_0_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "result_expr_0_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = path(b, l + 1);
    r = r && consumeToken(b, DOT);
    exit_section_(b, m, null, r);
    return r;
  }

  // expr ( AS? alias )?
  private static boolean result_expr_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "result_expr_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = expr(b, l + 1);
    r = r && result_expr_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( AS? alias )?
  private static boolean result_expr_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "result_expr_1_1")) return false;
    result_expr_1_1_0(b, l + 1);
    return true;
  }

  // AS? alias
  private static boolean result_expr_1_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "result_expr_1_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = result_expr_1_1_0_0(b, l + 1);
    r = r && alias(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // AS?
  private static boolean result_expr_1_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "result_expr_1_1_0_0")) return false;
    consumeToken(b, AS);
    return true;
  }

  /* ********************************************************** */
  // RETURNING (result-expr (COMMA result-expr)* |
  //                     (RAW | ELEMENT | VALUE) expr)
  public static boolean returning_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "returning_clause")) return false;
    if (!nextTokenIs(b, RETURNING)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, RETURNING);
    r = r && returning_clause_1(b, l + 1);
    exit_section_(b, m, RETURNING_CLAUSE, r);
    return r;
  }

  // result-expr (COMMA result-expr)* |
  //                     (RAW | ELEMENT | VALUE) expr
  private static boolean returning_clause_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "returning_clause_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = returning_clause_1_0(b, l + 1);
    if (!r) r = returning_clause_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // result-expr (COMMA result-expr)*
  private static boolean returning_clause_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "returning_clause_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = result_expr(b, l + 1);
    r = r && returning_clause_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA result-expr)*
  private static boolean returning_clause_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "returning_clause_1_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!returning_clause_1_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "returning_clause_1_0_1", c)) break;
    }
    return true;
  }

  // COMMA result-expr
  private static boolean returning_clause_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "returning_clause_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && result_expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (RAW | ELEMENT | VALUE) expr
  private static boolean returning_clause_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "returning_clause_1_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = returning_clause_1_1_0(b, l + 1);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // RAW | ELEMENT | VALUE
  private static boolean returning_clause_1_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "returning_clause_1_1_0")) return false;
    boolean r;
    r = consumeToken(b, RAW);
    if (!r) r = consumeToken(b, ELEMENT);
    if (!r) r = consumeToken(b, VALUE);
    return r;
  }

  /* ********************************************************** */
  // REVOKE role ( COMMA role )* ( ON keyspace-ref ( COMMA keyspace-ref )* )?
  //            FROM user ( COMMA user )*
  public static boolean revoke_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "revoke_statement")) return false;
    if (!nextTokenIs(b, REVOKE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, REVOKE);
    r = r && role(b, l + 1);
    r = r && revoke_statement_2(b, l + 1);
    r = r && revoke_statement_3(b, l + 1);
    r = r && consumeToken(b, FROM);
    r = r && user(b, l + 1);
    r = r && revoke_statement_6(b, l + 1);
    exit_section_(b, m, REVOKE_STATEMENT, r);
    return r;
  }

  // ( COMMA role )*
  private static boolean revoke_statement_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "revoke_statement_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!revoke_statement_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "revoke_statement_2", c)) break;
    }
    return true;
  }

  // COMMA role
  private static boolean revoke_statement_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "revoke_statement_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && role(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( ON keyspace-ref ( COMMA keyspace-ref )* )?
  private static boolean revoke_statement_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "revoke_statement_3")) return false;
    revoke_statement_3_0(b, l + 1);
    return true;
  }

  // ON keyspace-ref ( COMMA keyspace-ref )*
  private static boolean revoke_statement_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "revoke_statement_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ON);
    r = r && keyspace_ref(b, l + 1);
    r = r && revoke_statement_3_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( COMMA keyspace-ref )*
  private static boolean revoke_statement_3_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "revoke_statement_3_0_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!revoke_statement_3_0_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "revoke_statement_3_0_2", c)) break;
    }
    return true;
  }

  // COMMA keyspace-ref
  private static boolean revoke_statement_3_0_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "revoke_statement_3_0_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && keyspace_ref(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( COMMA user )*
  private static boolean revoke_statement_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "revoke_statement_6")) return false;
    while (true) {
      int c = current_position_(b);
      if (!revoke_statement_6_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "revoke_statement_6", c)) break;
    }
    return true;
  }

  // COMMA user
  private static boolean revoke_statement_6_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "revoke_statement_6_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && user(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // expr ( AS? alias )?
  public static boolean rhs_generic(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rhs_generic")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, RHS_GENERIC, "<rhs generic>");
    r = expr(b, l + 1);
    r = r && rhs_generic_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( AS? alias )?
  private static boolean rhs_generic_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rhs_generic_1")) return false;
    rhs_generic_1_0(b, l + 1);
    return true;
  }

  // AS? alias
  private static boolean rhs_generic_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rhs_generic_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = rhs_generic_1_0_0(b, l + 1);
    r = r && alias(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // AS?
  private static boolean rhs_generic_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rhs_generic_1_0_0")) return false;
    consumeToken(b, AS);
    return true;
  }

  /* ********************************************************** */
  // keyspace-ref ( AS? alias )? ansi-join-hints?
  public static boolean rhs_keyspace(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rhs_keyspace")) return false;
    if (!nextTokenIs(b, "<rhs keyspace>", ESCAPED_IDENTIFIER, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, RHS_KEYSPACE, "<rhs keyspace>");
    r = keyspace_ref(b, l + 1);
    r = r && rhs_keyspace_1(b, l + 1);
    r = r && rhs_keyspace_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( AS? alias )?
  private static boolean rhs_keyspace_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rhs_keyspace_1")) return false;
    rhs_keyspace_1_0(b, l + 1);
    return true;
  }

  // AS? alias
  private static boolean rhs_keyspace_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rhs_keyspace_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = rhs_keyspace_1_0_0(b, l + 1);
    r = r && alias(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // AS?
  private static boolean rhs_keyspace_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rhs_keyspace_1_0_0")) return false;
    consumeToken(b, AS);
    return true;
  }

  // ansi-join-hints?
  private static boolean rhs_keyspace_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rhs_keyspace_2")) return false;
    ansi_join_hints(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // subquery-expr AS? alias
  public static boolean rhs_subquery(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rhs_subquery")) return false;
    if (!nextTokenIs(b, LPAREN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = subquery_expr(b, l + 1);
    r = r && rhs_subquery_1(b, l + 1);
    r = r && alias(b, l + 1);
    exit_section_(b, m, RHS_SUBQUERY, r);
    return r;
  }

  // AS?
  private static boolean rhs_subquery_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rhs_subquery_1")) return false;
    consumeToken(b, AS);
    return true;
  }

  /* ********************************************************** */
  // identifier-ref
  public static boolean role(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "role")) return false;
    if (!nextTokenIs(b, "<role>", ESCAPED_IDENTIFIER, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ROLE, "<role>");
    r = identifier_ref(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ROLLBACK ( WORK | TRAN | TRANSACTION )?
  //                        ( TO SAVEPOINT savepointname )?
  public static boolean rollback_transaction(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rollback_transaction")) return false;
    if (!nextTokenIs(b, ROLLBACK)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ROLLBACK);
    r = r && rollback_transaction_1(b, l + 1);
    r = r && rollback_transaction_2(b, l + 1);
    exit_section_(b, m, ROLLBACK_TRANSACTION, r);
    return r;
  }

  // ( WORK | TRAN | TRANSACTION )?
  private static boolean rollback_transaction_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rollback_transaction_1")) return false;
    rollback_transaction_1_0(b, l + 1);
    return true;
  }

  // WORK | TRAN | TRANSACTION
  private static boolean rollback_transaction_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rollback_transaction_1_0")) return false;
    boolean r;
    r = consumeToken(b, WORK);
    if (!r) r = consumeToken(b, TRAN);
    if (!r) r = consumeToken(b, TRANSACTION);
    return r;
  }

  // ( TO SAVEPOINT savepointname )?
  private static boolean rollback_transaction_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rollback_transaction_2")) return false;
    rollback_transaction_2_0(b, l + 1);
    return true;
  }

  // TO SAVEPOINT savepointname
  private static boolean rollback_transaction_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rollback_transaction_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, TO, SAVEPOINT);
    r = r && savepointname(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // SAVEPOINT savepointname
  public static boolean savepoint_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "savepoint_statement")) return false;
    if (!nextTokenIs(b, SAVEPOINT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, SAVEPOINT);
    r = r && savepointname(b, l + 1);
    exit_section_(b, m, SAVEPOINT_STATEMENT, r);
    return r;
  }

  /* ********************************************************** */
  // identifier-ref
  public static boolean savepointname(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "savepointname")) return false;
    if (!nextTokenIs(b, "<savepointname>", ESCAPED_IDENTIFIER, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SAVEPOINTNAME, "<savepointname>");
    r = identifier_ref(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // identifier-ref
  public static boolean scope_ref(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "scope_ref")) return false;
    if (!nextTokenIs(b, "<scope ref>", ESCAPED_IDENTIFIER, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SCOPE_REF, "<scope ref>");
    r = identifier_ref(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // CASE (WHEN cond THEN expr)+ (ELSE expr)? END
  public static boolean searched_case_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "searched_case_expr")) return false;
    if (!nextTokenIs(b, CASE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, CASE);
    r = r && searched_case_expr_1(b, l + 1);
    r = r && searched_case_expr_2(b, l + 1);
    r = r && consumeToken(b, END);
    exit_section_(b, m, SEARCHED_CASE_EXPR, r);
    return r;
  }

  // (WHEN cond THEN expr)+
  private static boolean searched_case_expr_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "searched_case_expr_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = searched_case_expr_1_0(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!searched_case_expr_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "searched_case_expr_1", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // WHEN cond THEN expr
  private static boolean searched_case_expr_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "searched_case_expr_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, WHEN);
    r = r && cond(b, l + 1);
    r = r && consumeToken(b, THEN);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (ELSE expr)?
  private static boolean searched_case_expr_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "searched_case_expr_2")) return false;
    searched_case_expr_2_0(b, l + 1);
    return true;
  }

  // ELSE expr
  private static boolean searched_case_expr_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "searched_case_expr_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ELSE);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // SELECT hint-comment? projection
  public static boolean select_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "select_clause")) return false;
    if (!nextTokenIs(b, SELECT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, SELECT);
    r = r && select_clause_1(b, l + 1);
    r = r && projection(b, l + 1);
    exit_section_(b, m, SELECT_CLAUSE, r);
    return r;
  }

  // hint-comment?
  private static boolean select_clause_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "select_clause_1")) return false;
    hint_comment(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // with-clause? select-clause from-clause? let-clause? where-clause? group-by-clause? window-clause?
  public static boolean select_from(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "select_from")) return false;
    if (!nextTokenIs(b, "<select from>", SELECT, WITH)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SELECT_FROM, "<select from>");
    r = select_from_0(b, l + 1);
    r = r && select_clause(b, l + 1);
    r = r && select_from_2(b, l + 1);
    r = r && select_from_3(b, l + 1);
    r = r && select_from_4(b, l + 1);
    r = r && select_from_5(b, l + 1);
    r = r && select_from_6(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // with-clause?
  private static boolean select_from_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "select_from_0")) return false;
    with_clause(b, l + 1);
    return true;
  }

  // from-clause?
  private static boolean select_from_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "select_from_2")) return false;
    from_clause(b, l + 1);
    return true;
  }

  // let-clause?
  private static boolean select_from_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "select_from_3")) return false;
    let_clause(b, l + 1);
    return true;
  }

  // where-clause?
  private static boolean select_from_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "select_from_4")) return false;
    where_clause(b, l + 1);
    return true;
  }

  // group-by-clause?
  private static boolean select_from_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "select_from_5")) return false;
    group_by_clause(b, l + 1);
    return true;
  }

  // window-clause?
  private static boolean select_from_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "select_from_6")) return false;
    window_clause(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // select-term ( set-op select-term )* order-by-clause? limit-clause? offset-clause?
  public static boolean select_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "select_statement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SELECT_STATEMENT, "<select statement>");
    r = select_term(b, l + 1);
    r = r && select_statement_1(b, l + 1);
    r = r && select_statement_2(b, l + 1);
    r = r && select_statement_3(b, l + 1);
    r = r && select_statement_4(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( set-op select-term )*
  private static boolean select_statement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "select_statement_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!select_statement_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "select_statement_1", c)) break;
    }
    return true;
  }

  // set-op select-term
  private static boolean select_statement_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "select_statement_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = set_op(b, l + 1);
    r = r && select_term(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // order-by-clause?
  private static boolean select_statement_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "select_statement_2")) return false;
    order_by_clause(b, l + 1);
    return true;
  }

  // limit-clause?
  private static boolean select_statement_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "select_statement_3")) return false;
    limit_clause(b, l + 1);
    return true;
  }

  // offset-clause?
  private static boolean select_statement_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "select_statement_4")) return false;
    offset_clause(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // subselect | LPAREN select-statement RPAREN
  public static boolean select_term(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "select_term")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SELECT_TERM, "<select term>");
    r = subselect(b, l + 1);
    if (!r) r = select_term_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // LPAREN select-statement RPAREN
  private static boolean select_term_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "select_term_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LPAREN);
    r = r && select_statement(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // statement ( COLON statement )* COLON?
  static boolean sequence(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "sequence")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = statement(b, l + 1);
    r = r && sequence_1(b, l + 1);
    r = r && sequence_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( COLON statement )*
  private static boolean sequence_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "sequence_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!sequence_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "sequence_1", c)) break;
    }
    return true;
  }

  // COLON statement
  private static boolean sequence_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "sequence_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COLON);
    r = r && statement(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // COLON?
  private static boolean sequence_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "sequence_2")) return false;
    consumeToken(b, COLON);
    return true;
  }

  /* ********************************************************** */
  // SET ( path EQUAL expr update-for? )
  //                ( COMMA (  path EQUAL expr update-for? ) )*
  public static boolean set_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "set_clause")) return false;
    if (!nextTokenIs(b, SET)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, SET);
    r = r && set_clause_1(b, l + 1);
    r = r && set_clause_2(b, l + 1);
    exit_section_(b, m, SET_CLAUSE, r);
    return r;
  }

  // path EQUAL expr update-for?
  private static boolean set_clause_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "set_clause_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = path(b, l + 1);
    r = r && consumeToken(b, EQUAL);
    r = r && expr(b, l + 1);
    r = r && set_clause_1_3(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // update-for?
  private static boolean set_clause_1_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "set_clause_1_3")) return false;
    update_for(b, l + 1);
    return true;
  }

  // ( COMMA (  path EQUAL expr update-for? ) )*
  private static boolean set_clause_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "set_clause_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!set_clause_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "set_clause_2", c)) break;
    }
    return true;
  }

  // COMMA (  path EQUAL expr update-for? )
  private static boolean set_clause_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "set_clause_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && set_clause_2_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // path EQUAL expr update-for?
  private static boolean set_clause_2_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "set_clause_2_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = path(b, l + 1);
    r = r && consumeToken(b, EQUAL);
    r = r && expr(b, l + 1);
    r = r && set_clause_2_0_1_3(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // update-for?
  private static boolean set_clause_2_0_1_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "set_clause_2_0_1_3")) return false;
    update_for(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // ( UNION | INTERSECT | EXCEPT ) ALL?
  public static boolean set_op(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "set_op")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SET_OP, "<set op>");
    r = set_op_0(b, l + 1);
    r = r && set_op_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // UNION | INTERSECT | EXCEPT
  private static boolean set_op_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "set_op_0")) return false;
    boolean r;
    r = consumeToken(b, UNION);
    if (!r) r = consumeToken(b, INTERSECT);
    if (!r) r = consumeToken(b, EXCEPT);
    return r;
  }

  // ALL?
  private static boolean set_op_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "set_op_1")) return false;
    consumeToken(b, ALL);
    return true;
  }

  /* ********************************************************** */
  // SET TRANSACTION ISOLATION LEVEL READ COMMITTED
  public static boolean set_transaction(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "set_transaction")) return false;
    if (!nextTokenIs(b, SET)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, SET, TRANSACTION, ISOLATION, LEVEL, READ, COMMITTED);
    exit_section_(b, m, SET_TRANSACTION, r);
    return r;
  }

  /* ********************************************************** */
  // ( ALL | DISTINCT ) expr
  public static boolean simple_array_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "simple_array_expr")) return false;
    if (!nextTokenIs(b, "<simple array expr>", ALL, DISTINCT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SIMPLE_ARRAY_EXPR, "<simple array expr>");
    r = simple_array_expr_0(b, l + 1);
    r = r && expr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ALL | DISTINCT
  private static boolean simple_array_expr_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "simple_array_expr_0")) return false;
    boolean r;
    r = consumeToken(b, ALL);
    if (!r) r = consumeToken(b, DISTINCT);
    return r;
  }

  /* ********************************************************** */
  // CASE expr (WHEN expr THEN expr)+ (ELSE expr)? END
  public static boolean simple_case_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "simple_case_expr")) return false;
    if (!nextTokenIs(b, CASE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, CASE);
    r = r && expr(b, l + 1);
    r = r && simple_case_expr_2(b, l + 1);
    r = r && simple_case_expr_3(b, l + 1);
    r = r && consumeToken(b, END);
    exit_section_(b, m, SIMPLE_CASE_EXPR, r);
    return r;
  }

  // (WHEN expr THEN expr)+
  private static boolean simple_case_expr_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "simple_case_expr_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = simple_case_expr_2_0(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!simple_case_expr_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "simple_case_expr_2", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // WHEN expr THEN expr
  private static boolean simple_case_expr_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "simple_case_expr_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, WHEN);
    r = r && expr(b, l + 1);
    r = r && consumeToken(b, THEN);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (ELSE expr)?
  private static boolean simple_case_expr_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "simple_case_expr_3")) return false;
    simple_case_expr_3_0(b, l + 1);
    return true;
  }

  // ELSE expr
  private static boolean simple_case_expr_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "simple_case_expr_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ELSE);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ordered-hint-simple
  //               | gsi-hint-simple
  //               | fts-hint-simple
  //               | hash-hint-simple
  //               | nl-hint-simple
  public static boolean simple_hint(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "simple_hint")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SIMPLE_HINT, "<simple hint>");
    r = ordered_hint_simple(b, l + 1);
    if (!r) r = gsi_hint_simple(b, l + 1);
    if (!r) r = fts_hint_simple(b, l + 1);
    if (!r) r = hash_hint_simple(b, l + 1);
    if (!r) r = nl_hint_simple(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // simple-hint+
  public static boolean simple_hint_sequence(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "simple_hint_sequence")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SIMPLE_HINT_SEQUENCE, "<simple hint sequence>");
    r = simple_hint(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!simple_hint(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "simple_hint_sequence", c)) break;
    }
    exit_section_(b, l, m, r, false, null);
    return r;
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
  // LBRACKET expr SEMICOLON expr? RBRACKET
  public static boolean slice_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "slice_expr")) return false;
    if (!nextTokenIs(b, LBRACKET)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LBRACKET);
    r = r && expr(b, l + 1);
    r = r && consumeToken(b, SEMICOLON);
    r = r && slice_expr_3(b, l + 1);
    r = r && consumeToken(b, RBRACKET);
    exit_section_(b, m, SLICE_EXPR, r);
    return r;
  }

  // expr?
  private static boolean slice_expr_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "slice_expr_3")) return false;
    expr(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // dcl-statement |
  //               ddl-statement |
  //               dml-statement |
  //               dql-statement |
  //               tcl-statement |
  //               utility-statement
  public static boolean statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "statement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, STATEMENT, "<statement>");
    r = dcl_statement(b, l + 1);
    if (!r) r = ddl_statement(b, l + 1);
    if (!r) r = dml_statement(b, l + 1);
    if (!r) r = dql_statement(b, l + 1);
    if (!r) r = tcl_statement(b, l + 1);
    if (!r) r = utility_statement(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // double-quoted-string | single-quoted-string
  public static boolean str(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "str")) return false;
    if (!nextTokenIs(b, "<str>", DQUOTE, QUOTE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, STR, "<str>");
    r = double_quoted_string(b, l + 1);
    if (!r) r = single_quoted_string(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // LPAREN select-statement RPAREN
  public static boolean subquery_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "subquery_expr")) return false;
    if (!nextTokenIs(b, LPAREN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LPAREN);
    r = r && select_statement(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, SUBQUERY_EXPR, r);
    return r;
  }

  /* ********************************************************** */
  // select-from | from-select
  public static boolean subselect(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "subselect")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SUBSELECT, "<subselect>");
    r = select_from(b, l + 1);
    if (!r) r = from_select(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // keyspace-ref ( AS? alias )?
  public static boolean target_keyspace(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "target_keyspace")) return false;
    if (!nextTokenIs(b, "<target keyspace>", ESCAPED_IDENTIFIER, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, TARGET_KEYSPACE, "<target keyspace>");
    r = keyspace_ref(b, l + 1);
    r = r && target_keyspace_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( AS? alias )?
  private static boolean target_keyspace_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "target_keyspace_1")) return false;
    target_keyspace_1_0(b, l + 1);
    return true;
  }

  // AS? alias
  private static boolean target_keyspace_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "target_keyspace_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = target_keyspace_1_0_0(b, l + 1);
    r = r && alias(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // AS?
  private static boolean target_keyspace_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "target_keyspace_1_0_0")) return false;
    consumeToken(b, AS);
    return true;
  }

  /* ********************************************************** */
  // begin-transaction
  //                 | set-transaction
  //                 | savepoint-statement
  //                 | rollback-transaction
  //                 | commit-transaction
  public static boolean tcl_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tcl_statement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, TCL_STATEMENT, "<tcl statement>");
    r = begin_transaction(b, l + 1);
    if (!r) r = set_transaction(b, l + 1);
    if (!r) r = savepoint_statement(b, l + 1);
    if (!r) r = rollback_transaction(b, l + 1);
    if (!r) r = commit_transaction(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // unnest-type? ( UNNEST | FLATTEN ) expr ( AS? alias )?
  public static boolean unnest_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unnest_clause")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, UNNEST_CLAUSE, "<unnest clause>");
    r = unnest_clause_0(b, l + 1);
    r = r && unnest_clause_1(b, l + 1);
    r = r && expr(b, l + 1);
    r = r && unnest_clause_3(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // unnest-type?
  private static boolean unnest_clause_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unnest_clause_0")) return false;
    unnest_type(b, l + 1);
    return true;
  }

  // UNNEST | FLATTEN
  private static boolean unnest_clause_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unnest_clause_1")) return false;
    boolean r;
    r = consumeToken(b, UNNEST);
    if (!r) r = consumeToken(b, FLATTEN);
    return r;
  }

  // ( AS? alias )?
  private static boolean unnest_clause_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unnest_clause_3")) return false;
    unnest_clause_3_0(b, l + 1);
    return true;
  }

  // AS? alias
  private static boolean unnest_clause_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unnest_clause_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = unnest_clause_3_0_0(b, l + 1);
    r = r && alias(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // AS?
  private static boolean unnest_clause_3_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unnest_clause_3_0_0")) return false;
    consumeToken(b, AS);
    return true;
  }

  /* ********************************************************** */
  // INNER | ( LEFT OUTER? )
  public static boolean unnest_type(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unnest_type")) return false;
    if (!nextTokenIs(b, "<unnest type>", INNER, LEFT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, UNNEST_TYPE, "<unnest type>");
    r = consumeToken(b, INNER);
    if (!r) r = unnest_type_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // LEFT OUTER?
  private static boolean unnest_type_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unnest_type_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LEFT);
    r = r && unnest_type_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // OUTER?
  private static boolean unnest_type_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unnest_type_1_1")) return false;
    consumeToken(b, OUTER);
    return true;
  }

  /* ********************************************************** */
  // UNSET path update-for? (COMMA path update-for?)*
  public static boolean unset_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unset_clause")) return false;
    if (!nextTokenIs(b, UNSET)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, UNSET);
    r = r && path(b, l + 1);
    r = r && unset_clause_2(b, l + 1);
    r = r && unset_clause_3(b, l + 1);
    exit_section_(b, m, UNSET_CLAUSE, r);
    return r;
  }

  // update-for?
  private static boolean unset_clause_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unset_clause_2")) return false;
    update_for(b, l + 1);
    return true;
  }

  // (COMMA path update-for?)*
  private static boolean unset_clause_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unset_clause_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!unset_clause_3_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "unset_clause_3", c)) break;
    }
    return true;
  }

  // COMMA path update-for?
  private static boolean unset_clause_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unset_clause_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && path(b, l + 1);
    r = r && unset_clause_3_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // update-for?
  private static boolean unset_clause_3_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unset_clause_3_0_2")) return false;
    update_for(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // (FOR (name-var SEMICOLON)? var (IN | WITHIN) path
  //                (COMMA (name-var SEMICOLON)? var (IN | WITHIN) path)* )+
  //                (WHEN cond)? END
  public static boolean update_for(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_for")) return false;
    if (!nextTokenIs(b, FOR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = update_for_0(b, l + 1);
    r = r && update_for_1(b, l + 1);
    r = r && consumeToken(b, END);
    exit_section_(b, m, UPDATE_FOR, r);
    return r;
  }

  // (FOR (name-var SEMICOLON)? var (IN | WITHIN) path
  //                (COMMA (name-var SEMICOLON)? var (IN | WITHIN) path)* )+
  private static boolean update_for_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_for_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = update_for_0_0(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!update_for_0_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "update_for_0", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // FOR (name-var SEMICOLON)? var (IN | WITHIN) path
  //                (COMMA (name-var SEMICOLON)? var (IN | WITHIN) path)*
  private static boolean update_for_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_for_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, FOR);
    r = r && update_for_0_0_1(b, l + 1);
    r = r && var(b, l + 1);
    r = r && update_for_0_0_3(b, l + 1);
    r = r && path(b, l + 1);
    r = r && update_for_0_0_5(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (name-var SEMICOLON)?
  private static boolean update_for_0_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_for_0_0_1")) return false;
    update_for_0_0_1_0(b, l + 1);
    return true;
  }

  // name-var SEMICOLON
  private static boolean update_for_0_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_for_0_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = name_var(b, l + 1);
    r = r && consumeToken(b, SEMICOLON);
    exit_section_(b, m, null, r);
    return r;
  }

  // IN | WITHIN
  private static boolean update_for_0_0_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_for_0_0_3")) return false;
    boolean r;
    r = consumeToken(b, IN);
    if (!r) r = consumeToken(b, WITHIN);
    return r;
  }

  // (COMMA (name-var SEMICOLON)? var (IN | WITHIN) path)*
  private static boolean update_for_0_0_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_for_0_0_5")) return false;
    while (true) {
      int c = current_position_(b);
      if (!update_for_0_0_5_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "update_for_0_0_5", c)) break;
    }
    return true;
  }

  // COMMA (name-var SEMICOLON)? var (IN | WITHIN) path
  private static boolean update_for_0_0_5_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_for_0_0_5_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && update_for_0_0_5_0_1(b, l + 1);
    r = r && var(b, l + 1);
    r = r && update_for_0_0_5_0_3(b, l + 1);
    r = r && path(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (name-var SEMICOLON)?
  private static boolean update_for_0_0_5_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_for_0_0_5_0_1")) return false;
    update_for_0_0_5_0_1_0(b, l + 1);
    return true;
  }

  // name-var SEMICOLON
  private static boolean update_for_0_0_5_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_for_0_0_5_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = name_var(b, l + 1);
    r = r && consumeToken(b, SEMICOLON);
    exit_section_(b, m, null, r);
    return r;
  }

  // IN | WITHIN
  private static boolean update_for_0_0_5_0_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_for_0_0_5_0_3")) return false;
    boolean r;
    r = consumeToken(b, IN);
    if (!r) r = consumeToken(b, WITHIN);
    return r;
  }

  // (WHEN cond)?
  private static boolean update_for_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_for_1")) return false;
    update_for_1_0(b, l + 1);
    return true;
  }

  // WHEN cond
  private static boolean update_for_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_for_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, WHEN);
    r = r && cond(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // UPDATE target-keyspace use-keys? set-clause? unset-clause?
  //             where-clause? limit-clause? returning-clause?
  public static boolean update_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_statement")) return false;
    if (!nextTokenIs(b, UPDATE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, UPDATE);
    r = r && target_keyspace(b, l + 1);
    r = r && update_statement_2(b, l + 1);
    r = r && update_statement_3(b, l + 1);
    r = r && update_statement_4(b, l + 1);
    r = r && update_statement_5(b, l + 1);
    r = r && update_statement_6(b, l + 1);
    r = r && update_statement_7(b, l + 1);
    exit_section_(b, m, UPDATE_STATEMENT, r);
    return r;
  }

  // use-keys?
  private static boolean update_statement_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_statement_2")) return false;
    use_keys(b, l + 1);
    return true;
  }

  // set-clause?
  private static boolean update_statement_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_statement_3")) return false;
    set_clause(b, l + 1);
    return true;
  }

  // unset-clause?
  private static boolean update_statement_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_statement_4")) return false;
    unset_clause(b, l + 1);
    return true;
  }

  // where-clause?
  private static boolean update_statement_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_statement_5")) return false;
    where_clause(b, l + 1);
    return true;
  }

  // limit-clause?
  private static boolean update_statement_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_statement_6")) return false;
    limit_clause(b, l + 1);
    return true;
  }

  // returning-clause?
  private static boolean update_statement_7(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_statement_7")) return false;
    returning_clause(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // update-statistics-expr | update-statistics-index | update-statistics-delete
  public static boolean update_statistics(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_statistics")) return false;
    if (!nextTokenIs(b, "<update statistics>", ANALYZE, UPDATE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, UPDATE_STATISTICS, "<update statistics>");
    r = update_statistics_expr(b, l + 1);
    if (!r) r = update_statistics_index(b, l + 1);
    if (!r) r = update_statistics_delete(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ( UPDATE STATISTICS FOR? |
  //                                ANALYZE ( KEYSPACE | COLLECTION)? )
  //                                keyspace-ref delete-clause
  public static boolean update_statistics_delete(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_statistics_delete")) return false;
    if (!nextTokenIs(b, "<update statistics delete>", ANALYZE, UPDATE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, UPDATE_STATISTICS_DELETE, "<update statistics delete>");
    r = update_statistics_delete_0(b, l + 1);
    r = r && keyspace_ref(b, l + 1);
    r = r && delete_clause(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // UPDATE STATISTICS FOR? |
  //                                ANALYZE ( KEYSPACE | COLLECTION)?
  private static boolean update_statistics_delete_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_statistics_delete_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = update_statistics_delete_0_0(b, l + 1);
    if (!r) r = update_statistics_delete_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // UPDATE STATISTICS FOR?
  private static boolean update_statistics_delete_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_statistics_delete_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, UPDATE, STATISTICS);
    r = r && update_statistics_delete_0_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // FOR?
  private static boolean update_statistics_delete_0_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_statistics_delete_0_0_2")) return false;
    consumeToken(b, FOR);
    return true;
  }

  // ANALYZE ( KEYSPACE | COLLECTION)?
  private static boolean update_statistics_delete_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_statistics_delete_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ANALYZE);
    r = r && update_statistics_delete_0_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( KEYSPACE | COLLECTION)?
  private static boolean update_statistics_delete_0_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_statistics_delete_0_1_1")) return false;
    update_statistics_delete_0_1_1_0(b, l + 1);
    return true;
  }

  // KEYSPACE | COLLECTION
  private static boolean update_statistics_delete_0_1_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_statistics_delete_0_1_1_0")) return false;
    boolean r;
    r = consumeToken(b, KEYSPACE);
    if (!r) r = consumeToken(b, COLLECTION);
    return r;
  }

  /* ********************************************************** */
  // ( UPDATE STATISTICS FOR? |
  //                              ANALYZE ( KEYSPACE | COLLECTION)? )
  //                              keyspace-ref LPAREN index-key ( COMMA index-key )* RPAREN index-with?
  public static boolean update_statistics_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_statistics_expr")) return false;
    if (!nextTokenIs(b, "<update statistics expr>", ANALYZE, UPDATE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, UPDATE_STATISTICS_EXPR, "<update statistics expr>");
    r = update_statistics_expr_0(b, l + 1);
    r = r && keyspace_ref(b, l + 1);
    r = r && consumeToken(b, LPAREN);
    r = r && index_key(b, l + 1);
    r = r && update_statistics_expr_4(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    r = r && update_statistics_expr_6(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // UPDATE STATISTICS FOR? |
  //                              ANALYZE ( KEYSPACE | COLLECTION)?
  private static boolean update_statistics_expr_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_statistics_expr_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = update_statistics_expr_0_0(b, l + 1);
    if (!r) r = update_statistics_expr_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // UPDATE STATISTICS FOR?
  private static boolean update_statistics_expr_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_statistics_expr_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, UPDATE, STATISTICS);
    r = r && update_statistics_expr_0_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // FOR?
  private static boolean update_statistics_expr_0_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_statistics_expr_0_0_2")) return false;
    consumeToken(b, FOR);
    return true;
  }

  // ANALYZE ( KEYSPACE | COLLECTION)?
  private static boolean update_statistics_expr_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_statistics_expr_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ANALYZE);
    r = r && update_statistics_expr_0_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( KEYSPACE | COLLECTION)?
  private static boolean update_statistics_expr_0_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_statistics_expr_0_1_1")) return false;
    update_statistics_expr_0_1_1_0(b, l + 1);
    return true;
  }

  // KEYSPACE | COLLECTION
  private static boolean update_statistics_expr_0_1_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_statistics_expr_0_1_1_0")) return false;
    boolean r;
    r = consumeToken(b, KEYSPACE);
    if (!r) r = consumeToken(b, COLLECTION);
    return r;
  }

  // ( COMMA index-key )*
  private static boolean update_statistics_expr_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_statistics_expr_4")) return false;
    while (true) {
      int c = current_position_(b);
      if (!update_statistics_expr_4_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "update_statistics_expr_4", c)) break;
    }
    return true;
  }

  // COMMA index-key
  private static boolean update_statistics_expr_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_statistics_expr_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && index_key(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // index-with?
  private static boolean update_statistics_expr_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_statistics_expr_6")) return false;
    index_with(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // ( UPDATE STATISTICS FOR | ANALYZE )
  //                               index-clause index-using?  index-with?
  public static boolean update_statistics_index(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_statistics_index")) return false;
    if (!nextTokenIs(b, "<update statistics index>", ANALYZE, UPDATE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, UPDATE_STATISTICS_INDEX, "<update statistics index>");
    r = update_statistics_index_0(b, l + 1);
    r = r && index_clause(b, l + 1);
    r = r && update_statistics_index_2(b, l + 1);
    r = r && update_statistics_index_3(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // UPDATE STATISTICS FOR | ANALYZE
  private static boolean update_statistics_index_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_statistics_index_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = parseTokens(b, 0, UPDATE, STATISTICS, FOR);
    if (!r) r = consumeToken(b, ANALYZE);
    exit_section_(b, m, null, r);
    return r;
  }

  // index-using?
  private static boolean update_statistics_index_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_statistics_index_2")) return false;
    index_using(b, l + 1);
    return true;
  }

  // index-with?
  private static boolean update_statistics_index_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_statistics_index_3")) return false;
    index_with(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // ( UPDATE STATISTICS FOR? |
  //                                 ANALYZE ( KEYSPACE | COLLECTION)? )
  //                                 keyspace-ref indexes-clause index-using? index-with?
  public static boolean update_statistics_indexes(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_statistics_indexes")) return false;
    if (!nextTokenIs(b, "<update statistics indexes>", ANALYZE, UPDATE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, UPDATE_STATISTICS_INDEXES, "<update statistics indexes>");
    r = update_statistics_indexes_0(b, l + 1);
    r = r && keyspace_ref(b, l + 1);
    r = r && indexes_clause(b, l + 1);
    r = r && update_statistics_indexes_3(b, l + 1);
    r = r && update_statistics_indexes_4(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // UPDATE STATISTICS FOR? |
  //                                 ANALYZE ( KEYSPACE | COLLECTION)?
  private static boolean update_statistics_indexes_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_statistics_indexes_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = update_statistics_indexes_0_0(b, l + 1);
    if (!r) r = update_statistics_indexes_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // UPDATE STATISTICS FOR?
  private static boolean update_statistics_indexes_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_statistics_indexes_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, UPDATE, STATISTICS);
    r = r && update_statistics_indexes_0_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // FOR?
  private static boolean update_statistics_indexes_0_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_statistics_indexes_0_0_2")) return false;
    consumeToken(b, FOR);
    return true;
  }

  // ANALYZE ( KEYSPACE | COLLECTION)?
  private static boolean update_statistics_indexes_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_statistics_indexes_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ANALYZE);
    r = r && update_statistics_indexes_0_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( KEYSPACE | COLLECTION)?
  private static boolean update_statistics_indexes_0_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_statistics_indexes_0_1_1")) return false;
    update_statistics_indexes_0_1_1_0(b, l + 1);
    return true;
  }

  // KEYSPACE | COLLECTION
  private static boolean update_statistics_indexes_0_1_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_statistics_indexes_0_1_1_0")) return false;
    boolean r;
    r = consumeToken(b, KEYSPACE);
    if (!r) r = consumeToken(b, COLLECTION);
    return r;
  }

  // index-using?
  private static boolean update_statistics_indexes_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_statistics_indexes_3")) return false;
    index_using(b, l + 1);
    return true;
  }

  // index-with?
  private static boolean update_statistics_indexes_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_statistics_indexes_4")) return false;
    index_with(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // UPSERT INTO target-keyspace ( insert-values | insert-select )
  //             returning-clause?
  public static boolean upsert_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "upsert_statement")) return false;
    if (!nextTokenIs(b, UPSERT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, UPSERT, INTO);
    r = r && target_keyspace(b, l + 1);
    r = r && upsert_statement_3(b, l + 1);
    r = r && upsert_statement_4(b, l + 1);
    exit_section_(b, m, UPSERT_STATEMENT, r);
    return r;
  }

  // insert-values | insert-select
  private static boolean upsert_statement_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "upsert_statement_3")) return false;
    boolean r;
    r = insert_values(b, l + 1);
    if (!r) r = insert_select(b, l + 1);
    return r;
  }

  // returning-clause?
  private static boolean upsert_statement_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "upsert_statement_4")) return false;
    returning_clause(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // use-keys-clause | use-index-clause
  public static boolean use_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "use_clause")) return false;
    if (!nextTokenIs(b, USE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = use_keys_clause(b, l + 1);
    if (!r) r = use_index_clause(b, l + 1);
    exit_section_(b, m, USE_CLAUSE, r);
    return r;
  }

  /* ********************************************************** */
  // USE use-hash-term
  public static boolean use_hash_hint(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "use_hash_hint")) return false;
    if (!nextTokenIs(b, USE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, USE);
    r = r && use_hash_term(b, l + 1);
    exit_section_(b, m, USE_HASH_HINT, r);
    return r;
  }

  /* ********************************************************** */
  // HASH LPAREN ( BUILD | PROBE ) RPAREN
  public static boolean use_hash_term(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "use_hash_term")) return false;
    if (!nextTokenIs(b, HASH)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, HASH, LPAREN);
    r = r && use_hash_term_2(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, USE_HASH_TERM, r);
    return r;
  }

  // BUILD | PROBE
  private static boolean use_hash_term_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "use_hash_term_2")) return false;
    boolean r;
    r = consumeToken(b, BUILD);
    if (!r) r = consumeToken(b, PROBE);
    return r;
  }

  /* ********************************************************** */
  // USE use-index-term
  public static boolean use_index_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "use_index_clause")) return false;
    if (!nextTokenIs(b, USE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, USE);
    r = r && use_index_term(b, l + 1);
    exit_section_(b, m, USE_INDEX_CLAUSE, r);
    return r;
  }

  /* ********************************************************** */
  // INDEX LPAREN index-ref ( COMMA index-ref )* RPAREN
  public static boolean use_index_term(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "use_index_term")) return false;
    if (!nextTokenIs(b, INDEX)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, INDEX, LPAREN);
    r = r && index_ref(b, l + 1);
    r = r && use_index_term_3(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, USE_INDEX_TERM, r);
    return r;
  }

  // ( COMMA index-ref )*
  private static boolean use_index_term_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "use_index_term_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!use_index_term_3_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "use_index_term_3", c)) break;
    }
    return true;
  }

  // COMMA index-ref
  private static boolean use_index_term_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "use_index_term_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && index_ref(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // str
  public static boolean use_keys(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "use_keys")) return false;
    if (!nextTokenIs(b, "<use keys>", DQUOTE, QUOTE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, USE_KEYS, "<use keys>");
    r = str(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // USE use-keys-term
  public static boolean use_keys_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "use_keys_clause")) return false;
    if (!nextTokenIs(b, USE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, USE);
    r = r && use_keys_term(b, l + 1);
    exit_section_(b, m, USE_KEYS_CLAUSE, r);
    return r;
  }

  /* ********************************************************** */
  // PRIMARY? KEYS expr
  public static boolean use_keys_term(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "use_keys_term")) return false;
    if (!nextTokenIs(b, "<use keys term>", KEYS, PRIMARY)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, USE_KEYS_TERM, "<use keys term>");
    r = use_keys_term_0(b, l + 1);
    r = r && consumeToken(b, KEYS);
    r = r && expr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // PRIMARY?
  private static boolean use_keys_term_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "use_keys_term_0")) return false;
    consumeToken(b, PRIMARY);
    return true;
  }

  /* ********************************************************** */
  // USE use-nl-term
  public static boolean use_nl_hint(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "use_nl_hint")) return false;
    if (!nextTokenIs(b, USE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, USE);
    r = r && use_nl_term(b, l + 1);
    exit_section_(b, m, USE_NL_HINT, r);
    return r;
  }

  /* ********************************************************** */
  // NL
  public static boolean use_nl_term(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "use_nl_term")) return false;
    if (!nextTokenIs(b, NL)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, NL);
    exit_section_(b, m, USE_NL_TERM, r);
    return r;
  }

  /* ********************************************************** */
  // identifier-ref
  public static boolean user(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "user")) return false;
    if (!nextTokenIs(b, "<user>", ESCAPED_IDENTIFIER, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, USER, "<user>");
    r = identifier_ref(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // advise-statement
  //                     | explain-statement
  public static boolean utility_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "utility_statement")) return false;
    if (!nextTokenIs(b, "<utility statement>", ADVISE, EXPLAIN)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, UTILITY_STATEMENT, "<utility statement>");
    r = advise_statement(b, l + 1);
    if (!r) r = explain_statement(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // expr
  public static boolean value_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "value_expr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, VALUE_EXPR, "<value expr>");
    r = expr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // VALUES  LPAREN key-expr COMMA value-expr ( COMMA options )? RPAREN
  //             ( COMMA VALUES? LPAREN key-expr COMMA value-expr ( COMMA options )? RPAREN )*
  public static boolean values_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "values_clause")) return false;
    if (!nextTokenIs(b, VALUES)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, VALUES, LPAREN);
    r = r && key_expr(b, l + 1);
    r = r && consumeToken(b, COMMA);
    r = r && value_expr(b, l + 1);
    r = r && values_clause_5(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    r = r && values_clause_7(b, l + 1);
    exit_section_(b, m, VALUES_CLAUSE, r);
    return r;
  }

  // ( COMMA options )?
  private static boolean values_clause_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "values_clause_5")) return false;
    values_clause_5_0(b, l + 1);
    return true;
  }

  // COMMA options
  private static boolean values_clause_5_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "values_clause_5_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, COMMA, OPTIONS);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( COMMA VALUES? LPAREN key-expr COMMA value-expr ( COMMA options )? RPAREN )*
  private static boolean values_clause_7(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "values_clause_7")) return false;
    while (true) {
      int c = current_position_(b);
      if (!values_clause_7_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "values_clause_7", c)) break;
    }
    return true;
  }

  // COMMA VALUES? LPAREN key-expr COMMA value-expr ( COMMA options )? RPAREN
  private static boolean values_clause_7_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "values_clause_7_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && values_clause_7_0_1(b, l + 1);
    r = r && consumeToken(b, LPAREN);
    r = r && key_expr(b, l + 1);
    r = r && consumeToken(b, COMMA);
    r = r && value_expr(b, l + 1);
    r = r && values_clause_7_0_6(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, null, r);
    return r;
  }

  // VALUES?
  private static boolean values_clause_7_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "values_clause_7_0_1")) return false;
    consumeToken(b, VALUES);
    return true;
  }

  // ( COMMA options )?
  private static boolean values_clause_7_0_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "values_clause_7_0_6")) return false;
    values_clause_7_0_6_0(b, l + 1);
    return true;
  }

  // COMMA options
  private static boolean values_clause_7_0_6_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "values_clause_7_0_6_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, COMMA, OPTIONS);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // identifier-ref
  public static boolean var(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "var")) return false;
    if (!nextTokenIs(b, "<var>", ESCAPED_IDENTIFIER, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, VAR, "<var>");
    r = identifier_ref(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // WHERE cond
  public static boolean where_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "where_clause")) return false;
    if (!nextTokenIs(b, WHERE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, WHERE);
    r = r && cond(b, l + 1);
    exit_section_(b, m, WHERE_CLAUSE, r);
    return r;
  }

  /* ********************************************************** */
  // WINDOW window-declaration ( COMMA window-declaration )*
  public static boolean window_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_clause")) return false;
    if (!nextTokenIs(b, WINDOW)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, WINDOW);
    r = r && window_declaration(b, l + 1);
    r = r && window_clause_2(b, l + 1);
    exit_section_(b, m, WINDOW_CLAUSE, r);
    return r;
  }

  // ( COMMA window-declaration )*
  private static boolean window_clause_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_clause_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!window_clause_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "window_clause_2", c)) break;
    }
    return true;
  }

  // COMMA window-declaration
  private static boolean window_clause_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_clause_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && window_declaration(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // window-name AS LPAREN window-definition RPAREN
  public static boolean window_declaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_declaration")) return false;
    if (!nextTokenIs(b, "<window declaration>", ESCAPED_IDENTIFIER, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, WINDOW_DECLARATION, "<window declaration>");
    r = window_name(b, l + 1);
    r = r && consumeTokens(b, 0, AS, LPAREN);
    r = r && window_definition(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // window-ref? window-partition-clause? window-order-clause?
  //                       window-frame-clause?
  public static boolean window_definition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_definition")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, WINDOW_DEFINITION, "<window definition>");
    r = window_definition_0(b, l + 1);
    r = r && window_definition_1(b, l + 1);
    r = r && window_definition_2(b, l + 1);
    r = r && window_definition_3(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // window-ref?
  private static boolean window_definition_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_definition_0")) return false;
    window_ref(b, l + 1);
    return true;
  }

  // window-partition-clause?
  private static boolean window_definition_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_definition_1")) return false;
    window_partition_clause(b, l + 1);
    return true;
  }

  // window-order-clause?
  private static boolean window_definition_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_definition_2")) return false;
    window_order_clause(b, l + 1);
    return true;
  }

  // window-frame-clause?
  private static boolean window_definition_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_definition_3")) return false;
    window_frame_clause(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // ( ROWS | RANGE | GROUPS ) window-frame-extent
  //                         window-frame-exclusion?
  public static boolean window_frame_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_frame_clause")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, WINDOW_FRAME_CLAUSE, "<window frame clause>");
    r = window_frame_clause_0(b, l + 1);
    r = r && window_frame_extent(b, l + 1);
    r = r && window_frame_clause_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ROWS | RANGE | GROUPS
  private static boolean window_frame_clause_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_frame_clause_0")) return false;
    boolean r;
    r = consumeToken(b, ROWS);
    if (!r) r = consumeToken(b, RANGE);
    if (!r) r = consumeToken(b, GROUPS);
    return r;
  }

  // window-frame-exclusion?
  private static boolean window_frame_clause_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_frame_clause_2")) return false;
    window_frame_exclusion(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // EXCLUDE ( CURRENT ROW | GROUP | TIES | NO OTHERS )
  public static boolean window_frame_exclusion(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_frame_exclusion")) return false;
    if (!nextTokenIs(b, EXCLUDE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EXCLUDE);
    r = r && window_frame_exclusion_1(b, l + 1);
    exit_section_(b, m, WINDOW_FRAME_EXCLUSION, r);
    return r;
  }

  // CURRENT ROW | GROUP | TIES | NO OTHERS
  private static boolean window_frame_exclusion_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_frame_exclusion_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = parseTokens(b, 0, CURRENT, ROW);
    if (!r) r = consumeToken(b, GROUP);
    if (!r) r = consumeToken(b, TIES);
    if (!r) r = parseTokens(b, 0, NO, OTHERS);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // UNBOUNDED PRECEDING | CURRENT ROW |
  //                         BETWEEN ( UNBOUNDED PRECEDING | CURRENT ROW | ( PRECEDING | FOLLOWING ) )
  //                             AND ( UNBOUNDED FOLLOWING | CURRENT ROW | ( PRECEDING | FOLLOWING ) )
  public static boolean window_frame_extent(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_frame_extent")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, WINDOW_FRAME_EXTENT, "<window frame extent>");
    r = parseTokens(b, 0, UNBOUNDED, PRECEDING);
    if (!r) r = parseTokens(b, 0, CURRENT, ROW);
    if (!r) r = window_frame_extent_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // BETWEEN ( UNBOUNDED PRECEDING | CURRENT ROW | ( PRECEDING | FOLLOWING ) )
  //                             AND ( UNBOUNDED FOLLOWING | CURRENT ROW | ( PRECEDING | FOLLOWING ) )
  private static boolean window_frame_extent_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_frame_extent_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, BETWEEN);
    r = r && window_frame_extent_2_1(b, l + 1);
    r = r && consumeToken(b, AND);
    r = r && window_frame_extent_2_3(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // UNBOUNDED PRECEDING | CURRENT ROW | ( PRECEDING | FOLLOWING )
  private static boolean window_frame_extent_2_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_frame_extent_2_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = parseTokens(b, 0, UNBOUNDED, PRECEDING);
    if (!r) r = parseTokens(b, 0, CURRENT, ROW);
    if (!r) r = window_frame_extent_2_1_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // PRECEDING | FOLLOWING
  private static boolean window_frame_extent_2_1_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_frame_extent_2_1_2")) return false;
    boolean r;
    r = consumeToken(b, PRECEDING);
    if (!r) r = consumeToken(b, FOLLOWING);
    return r;
  }

  // UNBOUNDED FOLLOWING | CURRENT ROW | ( PRECEDING | FOLLOWING )
  private static boolean window_frame_extent_2_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_frame_extent_2_3")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = parseTokens(b, 0, UNBOUNDED, FOLLOWING);
    if (!r) r = parseTokens(b, 0, CURRENT, ROW);
    if (!r) r = window_frame_extent_2_3_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // PRECEDING | FOLLOWING
  private static boolean window_frame_extent_2_3_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_frame_extent_2_3_2")) return false;
    boolean r;
    r = consumeToken(b, PRECEDING);
    if (!r) r = consumeToken(b, FOLLOWING);
    return r;
  }

  /* ********************************************************** */
  // window-function-name LPAREN window-function-arguments RPAREN
  //                     window-function-options? over-clause
  public static boolean window_function(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_function")) return false;
    if (!nextTokenIs(b, "<window function>", ESCAPED_IDENTIFIER, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, WINDOW_FUNCTION, "<window function>");
    r = window_function_name(b, l + 1);
    r = r && consumeToken(b, LPAREN);
    r = r && window_function_arguments(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    r = r && window_function_4(b, l + 1);
    r = r && over_clause(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // window-function-options?
  private static boolean window_function_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_function_4")) return false;
    window_function_options(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // ( expr ( COMMA expr ( COMMA expr )? )? )?
  public static boolean window_function_arguments(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_function_arguments")) return false;
    Marker m = enter_section_(b, l, _NONE_, WINDOW_FUNCTION_ARGUMENTS, "<window function arguments>");
    window_function_arguments_0(b, l + 1);
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  // expr ( COMMA expr ( COMMA expr )? )?
  private static boolean window_function_arguments_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_function_arguments_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = expr(b, l + 1);
    r = r && window_function_arguments_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( COMMA expr ( COMMA expr )? )?
  private static boolean window_function_arguments_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_function_arguments_0_1")) return false;
    window_function_arguments_0_1_0(b, l + 1);
    return true;
  }

  // COMMA expr ( COMMA expr )?
  private static boolean window_function_arguments_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_function_arguments_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && expr(b, l + 1);
    r = r && window_function_arguments_0_1_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( COMMA expr )?
  private static boolean window_function_arguments_0_1_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_function_arguments_0_1_0_2")) return false;
    window_function_arguments_0_1_0_2_0(b, l + 1);
    return true;
  }

  // COMMA expr
  private static boolean window_function_arguments_0_1_0_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_function_arguments_0_1_0_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // identifier-ref
  public static boolean window_function_name(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_function_name")) return false;
    if (!nextTokenIs(b, "<window function name>", ESCAPED_IDENTIFIER, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, WINDOW_FUNCTION_NAME, "<window function name>");
    r = identifier_ref(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // nthval-from? nulls-treatment?
  public static boolean window_function_options(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_function_options")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, WINDOW_FUNCTION_OPTIONS, "<window function options>");
    r = window_function_options_0(b, l + 1);
    r = r && window_function_options_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // nthval-from?
  private static boolean window_function_options_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_function_options_0")) return false;
    nthval_from(b, l + 1);
    return true;
  }

  // nulls-treatment?
  private static boolean window_function_options_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_function_options_1")) return false;
    nulls_treatment(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // identifier-ref
  public static boolean window_name(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_name")) return false;
    if (!nextTokenIs(b, "<window name>", ESCAPED_IDENTIFIER, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, WINDOW_NAME, "<window name>");
    r = identifier_ref(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ORDER BY ordering-term ( COMMA ordering-term )*
  public static boolean window_order_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_order_clause")) return false;
    if (!nextTokenIs(b, ORDER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, ORDER, BY);
    r = r && ordering_term(b, l + 1);
    r = r && window_order_clause_3(b, l + 1);
    exit_section_(b, m, WINDOW_ORDER_CLAUSE, r);
    return r;
  }

  // ( COMMA ordering-term )*
  private static boolean window_order_clause_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_order_clause_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!window_order_clause_3_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "window_order_clause_3", c)) break;
    }
    return true;
  }

  // COMMA ordering-term
  private static boolean window_order_clause_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_order_clause_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && ordering_term(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // PARTITION BY expr ( COMMA expr )*
  public static boolean window_partition_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_partition_clause")) return false;
    if (!nextTokenIs(b, PARTITION)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, PARTITION, BY);
    r = r && expr(b, l + 1);
    r = r && window_partition_clause_3(b, l + 1);
    exit_section_(b, m, WINDOW_PARTITION_CLAUSE, r);
    return r;
  }

  // ( COMMA expr )*
  private static boolean window_partition_clause_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_partition_clause_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!window_partition_clause_3_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "window_partition_clause_3", c)) break;
    }
    return true;
  }

  // COMMA expr
  private static boolean window_partition_clause_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_partition_clause_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // identifier-ref
  public static boolean window_ref(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_ref")) return false;
    if (!nextTokenIs(b, "<window ref>", ESCAPED_IDENTIFIER, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, WINDOW_REF, "<window ref>");
    r = identifier_ref(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // WITH alias AS LPAREN ( select-statement | expr ) RPAREN
  //                  ( COMMA alias AS LPAREN ( select-statement | expr ) RPAREN )*
  public static boolean with_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "with_clause")) return false;
    if (!nextTokenIs(b, WITH)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, WITH);
    r = r && alias(b, l + 1);
    r = r && consumeTokens(b, 0, AS, LPAREN);
    r = r && with_clause_4(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    r = r && with_clause_6(b, l + 1);
    exit_section_(b, m, WITH_CLAUSE, r);
    return r;
  }

  // select-statement | expr
  private static boolean with_clause_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "with_clause_4")) return false;
    boolean r;
    r = select_statement(b, l + 1);
    if (!r) r = expr(b, l + 1);
    return r;
  }

  // ( COMMA alias AS LPAREN ( select-statement | expr ) RPAREN )*
  private static boolean with_clause_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "with_clause_6")) return false;
    while (true) {
      int c = current_position_(b);
      if (!with_clause_6_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "with_clause_6", c)) break;
    }
    return true;
  }

  // COMMA alias AS LPAREN ( select-statement | expr ) RPAREN
  private static boolean with_clause_6_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "with_clause_6_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && alias(b, l + 1);
    r = r && consumeTokens(b, 0, AS, LPAREN);
    r = r && with_clause_6_0_4(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, null, r);
    return r;
  }

  // select-statement | expr
  private static boolean with_clause_6_0_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "with_clause_6_0_4")) return false;
    boolean r;
    r = select_statement(b, l + 1);
    if (!r) r = expr(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // NOT? WITHIN expr
  public static boolean within_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "within_expr")) return false;
    if (!nextTokenIs(b, "<within expr>", NOT, WITHIN)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, WITHIN_EXPR, "<within expr>");
    r = within_expr_0(b, l + 1);
    r = r && consumeToken(b, WITHIN);
    r = r && expr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // NOT?
  private static boolean within_expr_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "within_expr_0")) return false;
    consumeToken(b, NOT);
    return true;
  }

}

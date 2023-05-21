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
    return role(b, l + 1);
  }

  /* ********************************************************** */
  // 'ADVISE' 'INDEX'? ( select | update | delete | merge )
  public static boolean advise(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "advise")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ADVISE, "<advise>");
    r = consumeToken(b, "ADVISE");
    r = r && advise_1(b, l + 1);
    r = r && advise_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // 'INDEX'?
  private static boolean advise_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "advise_1")) return false;
    consumeToken(b, "INDEX");
    return true;
  }

  // select | update | delete | merge
  private static boolean advise_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "advise_2")) return false;
    boolean r;
    r = select(b, l + 1);
    if (!r) r = update(b, l + 1);
    if (!r) r = delete(b, l + 1);
    if (!r) r = merge(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // aggregate-function-name '(' ( aggregate-quantifier? expr |
  //                        ( path '.' )? '*' ) ')' filter-clause? over-clause?
  public static boolean aggregate_function(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "aggregate_function")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, AGGREGATE_FUNCTION, "<aggregate function>");
    r = aggregate_function_name(b, l + 1);
    r = r && consumeToken(b, "(");
    r = r && aggregate_function_2(b, l + 1);
    r = r && consumeToken(b, ")");
    r = r && aggregate_function_4(b, l + 1);
    r = r && aggregate_function_5(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // aggregate-quantifier? expr |
  //                        ( path '.' )? '*'
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

  // ( path '.' )? '*'
  private static boolean aggregate_function_2_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "aggregate_function_2_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = aggregate_function_2_1_0(b, l + 1);
    r = r && consumeToken(b, "*");
    exit_section_(b, m, null, r);
    return r;
  }

  // ( path '.' )?
  private static boolean aggregate_function_2_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "aggregate_function_2_1_0")) return false;
    aggregate_function_2_1_0_0(b, l + 1);
    return true;
  }

  // path '.'
  private static boolean aggregate_function_2_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "aggregate_function_2_1_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = path(b, l + 1);
    r = r && consumeToken(b, ".");
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
  // identifier
  public static boolean aggregate_function_name(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "aggregate_function_name")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, AGGREGATE_FUNCTION_NAME, "<aggregate function name>");
    r = identifier(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'ALL' | 'DISTINCT'
  public static boolean aggregate_quantifier(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "aggregate_quantifier")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, AGGREGATE_QUANTIFIER, "<aggregate quantifier>");
    r = consumeToken(b, "ALL");
    if (!r) r = consumeToken(b, "DISTINCT");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // identifier
  public static boolean alias(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "alias")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ALIAS, "<alias>");
    r = identifier(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'ALTER' 'INDEX' ( index-path '.' index-name | index-name 'ON' keyspace-ref )
  //                 index-using? index-with
  public static boolean alter_index(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "alter_index")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ALTER_INDEX, "<alter index>");
    r = consumeToken(b, "ALTER");
    r = r && consumeToken(b, "INDEX");
    r = r && alter_index_2(b, l + 1);
    r = r && alter_index_3(b, l + 1);
    r = r && index_with(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // index-path '.' index-name | index-name 'ON' keyspace-ref
  private static boolean alter_index_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "alter_index_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = alter_index_2_0(b, l + 1);
    if (!r) r = alter_index_2_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // index-path '.' index-name
  private static boolean alter_index_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "alter_index_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = index_path(b, l + 1);
    r = r && consumeToken(b, ".");
    r = r && index_name(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // index-name 'ON' keyspace-ref
  private static boolean alter_index_2_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "alter_index_2_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = index_name(b, l + 1);
    r = r && consumeToken(b, "ON");
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
  // cond 'AND' cond
  public static boolean and(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "and")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, AND, "<and>");
    r = cond(b, l + 1);
    r = r && consumeToken(b, "AND");
    r = r && cond(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // use-hash-term | use-nl-term
  public static boolean ansi_hint_terms(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ansi_hint_terms")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ANSI_HINT_TERMS, "<ansi hint terms>");
    r = use_hash_term(b, l + 1);
    if (!r) r = use_nl_term(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ansi-join-type? 'JOIN' ansi-join-rhs ansi-join-predicate
  public static boolean ansi_join_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ansi_join_clause")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ANSI_JOIN_CLAUSE, "<ansi join clause>");
    r = ansi_join_clause_0(b, l + 1);
    r = r && consumeToken(b, "JOIN");
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
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ANSI_JOIN_HINTS, "<ansi join hints>");
    r = use_hash_hint(b, l + 1);
    if (!r) r = use_nl_hint(b, l + 1);
    if (!r) r = multiple_hints(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'ON' expr
  public static boolean ansi_join_predicate(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ansi_join_predicate")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ANSI_JOIN_PREDICATE, "<ansi join predicate>");
    r = consumeToken(b, "ON");
    r = r && expr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
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
  // 'INNER' | ( 'LEFT' | 'RIGHT' ) 'OUTER'?
  public static boolean ansi_join_type(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ansi_join_type")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ANSI_JOIN_TYPE, "<ansi join type>");
    r = consumeToken(b, "INNER");
    if (!r) r = ansi_join_type_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( 'LEFT' | 'RIGHT' ) 'OUTER'?
  private static boolean ansi_join_type_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ansi_join_type_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ansi_join_type_1_0(b, l + 1);
    r = r && ansi_join_type_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // 'LEFT' | 'RIGHT'
  private static boolean ansi_join_type_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ansi_join_type_1_0")) return false;
    boolean r;
    r = consumeToken(b, "LEFT");
    if (!r) r = consumeToken(b, "RIGHT");
    return r;
  }

  // 'OUTER'?
  private static boolean ansi_join_type_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ansi_join_type_1_1")) return false;
    consumeToken(b, "OUTER");
    return true;
  }

  /* ********************************************************** */
  // target-keyspace use-index-clause 'USING' ansi-merge-source
  //                ansi-merge-predicate ansi-merge-actions
  public static boolean ansi_merge(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ansi_merge")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ANSI_MERGE, "<ansi merge>");
    r = target_keyspace(b, l + 1);
    r = r && use_index_clause(b, l + 1);
    r = r && consumeToken(b, "USING");
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
  // 'WHEN' 'NOT' 'MATCHED' 'THEN' 'INSERT' '(' 'KEY'? key
  //                       ( ',' 'VALUE'? value )? ( ',' 'OPTIONS'? options )? ')' where-clause?
  public static boolean ansi_merge_insert(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ansi_merge_insert")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ANSI_MERGE_INSERT, "<ansi merge insert>");
    r = consumeToken(b, "WHEN");
    r = r && consumeToken(b, "NOT");
    r = r && consumeToken(b, "MATCHED");
    r = r && consumeToken(b, "THEN");
    r = r && consumeToken(b, "INSERT");
    r = r && consumeToken(b, "(");
    r = r && ansi_merge_insert_6(b, l + 1);
    r = r && key(b, l + 1);
    r = r && ansi_merge_insert_8(b, l + 1);
    r = r && ansi_merge_insert_9(b, l + 1);
    r = r && consumeToken(b, ")");
    r = r && ansi_merge_insert_11(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // 'KEY'?
  private static boolean ansi_merge_insert_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ansi_merge_insert_6")) return false;
    consumeToken(b, "KEY");
    return true;
  }

  // ( ',' 'VALUE'? value )?
  private static boolean ansi_merge_insert_8(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ansi_merge_insert_8")) return false;
    ansi_merge_insert_8_0(b, l + 1);
    return true;
  }

  // ',' 'VALUE'? value
  private static boolean ansi_merge_insert_8_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ansi_merge_insert_8_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ",");
    r = r && ansi_merge_insert_8_0_1(b, l + 1);
    r = r && value(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // 'VALUE'?
  private static boolean ansi_merge_insert_8_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ansi_merge_insert_8_0_1")) return false;
    consumeToken(b, "VALUE");
    return true;
  }

  // ( ',' 'OPTIONS'? options )?
  private static boolean ansi_merge_insert_9(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ansi_merge_insert_9")) return false;
    ansi_merge_insert_9_0(b, l + 1);
    return true;
  }

  // ',' 'OPTIONS'? options
  private static boolean ansi_merge_insert_9_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ansi_merge_insert_9_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ",");
    r = r && ansi_merge_insert_9_0_1(b, l + 1);
    r = r && consumeToken(b, OPTIONS);
    exit_section_(b, m, null, r);
    return r;
  }

  // 'OPTIONS'?
  private static boolean ansi_merge_insert_9_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ansi_merge_insert_9_0_1")) return false;
    consumeToken(b, "OPTIONS");
    return true;
  }

  // where-clause?
  private static boolean ansi_merge_insert_11(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ansi_merge_insert_11")) return false;
    where_clause(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // 'ON' expr
  public static boolean ansi_merge_predicate(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ansi_merge_predicate")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ANSI_MERGE_PREDICATE, "<ansi merge predicate>");
    r = consumeToken(b, "ON");
    r = r && expr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
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
  // ansi-nest-type? 'NEST' ansi-nest-rhs ansi-nest-predicate
  public static boolean ansi_nest_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ansi_nest_clause")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ANSI_NEST_CLAUSE, "<ansi nest clause>");
    r = ansi_nest_clause_0(b, l + 1);
    r = r && consumeToken(b, "NEST");
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
  // 'ON' expr
  public static boolean ansi_nest_predicate(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ansi_nest_predicate")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ANSI_NEST_PREDICATE, "<ansi nest predicate>");
    r = consumeToken(b, "ON");
    r = r && expr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // keyspace-ref ( 'AS'? alias )?
  public static boolean ansi_nest_rhs(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ansi_nest_rhs")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ANSI_NEST_RHS, "<ansi nest rhs>");
    r = keyspace_ref(b, l + 1);
    r = r && ansi_nest_rhs_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( 'AS'? alias )?
  private static boolean ansi_nest_rhs_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ansi_nest_rhs_1")) return false;
    ansi_nest_rhs_1_0(b, l + 1);
    return true;
  }

  // 'AS'? alias
  private static boolean ansi_nest_rhs_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ansi_nest_rhs_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ansi_nest_rhs_1_0_0(b, l + 1);
    r = r && alias(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // 'AS'?
  private static boolean ansi_nest_rhs_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ansi_nest_rhs_1_0_0")) return false;
    consumeToken(b, "AS");
    return true;
  }

  /* ********************************************************** */
  // 'INNER' | ( 'LEFT' 'OUTER'? )
  public static boolean ansi_nest_type(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ansi_nest_type")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ANSI_NEST_TYPE, "<ansi nest type>");
    r = consumeToken(b, "INNER");
    if (!r) r = ansi_nest_type_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // 'LEFT' 'OUTER'?
  private static boolean ansi_nest_type_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ansi_nest_type_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "LEFT");
    r = r && ansi_nest_type_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // 'OUTER'?
  private static boolean ansi_nest_type_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ansi_nest_type_1_1")) return false;
    consumeToken(b, "OUTER");
    return true;
  }

  /* ********************************************************** */
  // expr '+' expr |
  //                     expr '-' expr |
  //                     expr '*' expr |
  //                     expr '/' expr |
  //                     expr '%' expr |
  //                     '-' expr
  public static boolean arithmetic_term(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arithmetic_term")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ARITHMETIC_TERM, "<arithmetic term>");
    r = arithmetic_term_0(b, l + 1);
    if (!r) r = arithmetic_term_1(b, l + 1);
    if (!r) r = arithmetic_term_2(b, l + 1);
    if (!r) r = arithmetic_term_3(b, l + 1);
    if (!r) r = arithmetic_term_4(b, l + 1);
    if (!r) r = arithmetic_term_5(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // expr '+' expr
  private static boolean arithmetic_term_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arithmetic_term_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = expr(b, l + 1);
    r = r && consumeToken(b, "+");
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // expr '-' expr
  private static boolean arithmetic_term_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arithmetic_term_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = expr(b, l + 1);
    r = r && consumeToken(b, "-");
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // expr '*' expr
  private static boolean arithmetic_term_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arithmetic_term_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = expr(b, l + 1);
    r = r && consumeToken(b, "*");
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // expr '/' expr
  private static boolean arithmetic_term_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arithmetic_term_3")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = expr(b, l + 1);
    r = r && consumeToken(b, "/");
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // expr '%' expr
  private static boolean arithmetic_term_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arithmetic_term_4")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = expr(b, l + 1);
    r = r && consumeToken(b, "%");
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // '-' expr
  private static boolean arithmetic_term_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arithmetic_term_5")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "-");
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '[' ( expr ( ',' expr )* )? ']'
  public static boolean array(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ARRAY, "<array>");
    r = consumeToken(b, "[");
    r = r && array_1(b, l + 1);
    r = r && consumeToken(b, "]");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( expr ( ',' expr )* )?
  private static boolean array_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array_1")) return false;
    array_1_0(b, l + 1);
    return true;
  }

  // expr ( ',' expr )*
  private static boolean array_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = expr(b, l + 1);
    r = r && array_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( ',' expr )*
  private static boolean array_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array_1_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!array_1_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "array_1_0_1", c)) break;
    }
    return true;
  }

  // ',' expr
  private static boolean array_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ",");
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // full-array-expr | simple-array-expr
  public static boolean array_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array_expr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ARRAY_EXPR, "<array expr>");
    r = full_array_expr(b, l + 1);
    if (!r) r = simple_array_expr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '`' identifier '`'
  public static boolean backticked_string(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "backticked_string")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BACKTICKED_STRING, "<backticked string>");
    r = consumeToken(b, "`");
    r = r && identifier(b, l + 1);
    r = r && consumeToken(b, "`");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ( 'BEGIN' | 'START' ) ( 'WORK' | 'TRAN' | 'TRANSACTION' )
  //                       ( 'ISOLATION' 'LEVEL' 'READ' 'COMMITTED' )?
  public static boolean begin_transaction(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "begin_transaction")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BEGIN_TRANSACTION, "<begin transaction>");
    r = begin_transaction_0(b, l + 1);
    r = r && begin_transaction_1(b, l + 1);
    r = r && begin_transaction_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // 'BEGIN' | 'START'
  private static boolean begin_transaction_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "begin_transaction_0")) return false;
    boolean r;
    r = consumeToken(b, "BEGIN");
    if (!r) r = consumeToken(b, "START");
    return r;
  }

  // 'WORK' | 'TRAN' | 'TRANSACTION'
  private static boolean begin_transaction_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "begin_transaction_1")) return false;
    boolean r;
    r = consumeToken(b, "WORK");
    if (!r) r = consumeToken(b, "TRAN");
    if (!r) r = consumeToken(b, "TRANSACTION");
    return r;
  }

  // ( 'ISOLATION' 'LEVEL' 'READ' 'COMMITTED' )?
  private static boolean begin_transaction_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "begin_transaction_2")) return false;
    begin_transaction_2_0(b, l + 1);
    return true;
  }

  // 'ISOLATION' 'LEVEL' 'READ' 'COMMITTED'
  private static boolean begin_transaction_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "begin_transaction_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "ISOLATION");
    r = r && consumeToken(b, "LEVEL");
    r = r && consumeToken(b, "READ");
    r = r && consumeToken(b, "COMMITTED");
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // expr 'NOT'? 'BETWEEN' start-expr 'AND' end-expr
  public static boolean between_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "between_expr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BETWEEN_EXPR, "<between expr>");
    r = expr(b, l + 1);
    r = r && between_expr_1(b, l + 1);
    r = r && consumeToken(b, "BETWEEN");
    r = r && start_expr(b, l + 1);
    r = r && consumeToken(b, "AND");
    r = r && end_expr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // 'NOT'?
  private static boolean between_expr_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "between_expr_1")) return false;
    consumeToken(b, "NOT");
    return true;
  }

  /* ********************************************************** */
  // '/*' (  newline )* '*/'
  public static boolean block_comment(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "block_comment")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BLOCK_COMMENT, "<block comment>");
    r = consumeToken(b, "/*");
    r = r && block_comment_1(b, l + 1);
    r = r && consumeToken(b, "*/");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (  newline )*
  private static boolean block_comment_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "block_comment_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!block_comment_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "block_comment_1", c)) break;
    }
    return true;
  }

  // (  newline )
  private static boolean block_comment_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "block_comment_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = newline(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '/*+' hints '*/'
  public static boolean block_hint_comment(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "block_hint_comment")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BLOCK_HINT_COMMENT, "<block hint comment>");
    r = consumeToken(b, "/*+");
    r = r && hints(b, l + 1);
    r = r && consumeToken(b, "*/");
    exit_section_(b, l, m, r, false, null);
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
  // 'TRUE' | 'FALSE'
  public static boolean bool(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "bool")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BOOL, "<bool>");
    r = consumeToken(b, "TRUE");
    if (!r) r = consumeToken(b, "FALSE");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // identifier
  public static boolean bucket(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "bucket")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BUCKET, "<bucket>");
    r = identifier(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'BUILD' 'INDEX' 'ON' keyspace-ref '(' index-term (',' index-term)* ')'
  //                 index-using?
  public static boolean build_index(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "build_index")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BUILD_INDEX, "<build index>");
    r = consumeToken(b, "BUILD");
    r = r && consumeToken(b, "INDEX");
    r = r && consumeToken(b, "ON");
    r = r && keyspace_ref(b, l + 1);
    r = r && consumeToken(b, "(");
    r = r && index_term(b, l + 1);
    r = r && build_index_6(b, l + 1);
    r = r && consumeToken(b, ")");
    r = r && build_index_8(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (',' index-term)*
  private static boolean build_index_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "build_index_6")) return false;
    while (true) {
      int c = current_position_(b);
      if (!build_index_6_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "build_index_6", c)) break;
    }
    return true;
  }

  // ',' index-term
  private static boolean build_index_6_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "build_index_6_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ",");
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
  // simple-case-expr | searched-case-expr
  public static boolean case_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "case_expr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CASE_EXPR, "<case expr>");
    r = simple_case_expr(b, l + 1);
    if (!r) r = searched_case_expr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '\' ('\' | '/' | 'b' | 'f' | 'n' | 'r' | 't' | 'u' hex hex hex hex )
  public static boolean chr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "chr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CHR, "<chr>");
    r = consumeToken(b, "\\");
    r = r && chr_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '\' | '/' | 'b' | 'f' | 'n' | 'r' | 't' | 'u' hex hex hex hex
  private static boolean chr_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "chr_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "\\");
    if (!r) r = consumeToken(b, "/");
    if (!r) r = consumeToken(b, "b");
    if (!r) r = consumeToken(b, "f");
    if (!r) r = consumeToken(b, "n");
    if (!r) r = consumeToken(b, "r");
    if (!r) r = consumeToken(b, "t");
    if (!r) r = chr_1_7(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // 'u' hex hex hex hex
  private static boolean chr_1_7(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "chr_1_7")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "u");
    r = r && hex(b, l + 1);
    r = r && hex(b, l + 1);
    r = r && hex(b, l + 1);
    r = r && hex(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // identifier
  public static boolean collection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "collection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, COLLECTION, "<collection>");
    r = identifier(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // exists-expr | in-expr | within-expr
  public static boolean collection_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "collection_expr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, COLLECTION_EXPR, "<collection expr>");
    r = exists_expr(b, l + 1);
    if (!r) r = in_expr(b, l + 1);
    if (!r) r = within_expr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ',' ( rhs-keyspace | rhs-subquery | rhs-generic )
  public static boolean comma_separated_join(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "comma_separated_join")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, COMMA_SEPARATED_JOIN, "<comma separated join>");
    r = consumeToken(b, ",");
    r = r && comma_separated_join_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
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
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, COMMENT, "<comment>");
    r = block_comment(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'COMMIT' ( 'WORK' | 'TRAN' | 'TRANSACTION' )?
  public static boolean commit_transaction(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "commit_transaction")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, COMMIT_TRANSACTION, "<commit transaction>");
    r = consumeToken(b, "COMMIT");
    r = r && commit_transaction_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( 'WORK' | 'TRAN' | 'TRANSACTION' )?
  private static boolean commit_transaction_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "commit_transaction_1")) return false;
    commit_transaction_1_0(b, l + 1);
    return true;
  }

  // 'WORK' | 'TRAN' | 'TRANSACTION'
  private static boolean commit_transaction_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "commit_transaction_1_0")) return false;
    boolean r;
    r = consumeToken(b, "WORK");
    if (!r) r = consumeToken(b, "TRAN");
    if (!r) r = consumeToken(b, "TRANSACTION");
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
  // expr '||' expr
  public static boolean concatenation_term(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "concatenation_term")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CONCATENATION_TERM, "<concatenation term>");
    r = expr(b, l + 1);
    r = r && consumeToken(b, "||");
    r = r && expr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
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
  // 'CREATE' 'COLLECTION' ( ( namespace ':' )? bucket '.' scope '.' )?
  //                       collection ( 'IF' 'NOT' 'EXISTS' )?
  public static boolean create_collection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_collection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CREATE_COLLECTION, "<create collection>");
    r = consumeToken(b, "CREATE");
    r = r && consumeToken(b, "COLLECTION");
    r = r && create_collection_2(b, l + 1);
    r = r && collection(b, l + 1);
    r = r && create_collection_4(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( ( namespace ':' )? bucket '.' scope '.' )?
  private static boolean create_collection_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_collection_2")) return false;
    create_collection_2_0(b, l + 1);
    return true;
  }

  // ( namespace ':' )? bucket '.' scope '.'
  private static boolean create_collection_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_collection_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = create_collection_2_0_0(b, l + 1);
    r = r && bucket(b, l + 1);
    r = r && consumeToken(b, ".");
    r = r && scope(b, l + 1);
    r = r && consumeToken(b, ".");
    exit_section_(b, m, null, r);
    return r;
  }

  // ( namespace ':' )?
  private static boolean create_collection_2_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_collection_2_0_0")) return false;
    create_collection_2_0_0_0(b, l + 1);
    return true;
  }

  // namespace ':'
  private static boolean create_collection_2_0_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_collection_2_0_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = namespace(b, l + 1);
    r = r && consumeToken(b, ":");
    exit_section_(b, m, null, r);
    return r;
  }

  // ( 'IF' 'NOT' 'EXISTS' )?
  private static boolean create_collection_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_collection_4")) return false;
    create_collection_4_0(b, l + 1);
    return true;
  }

  // 'IF' 'NOT' 'EXISTS'
  private static boolean create_collection_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_collection_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "IF");
    r = r && consumeToken(b, "NOT");
    r = r && consumeToken(b, "EXISTS");
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // create-function-inline | create-function-external
  public static boolean create_function(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_function")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CREATE_FUNCTION, "<create function>");
    r = create_function_inline(b, l + 1);
    if (!r) r = create_function_external(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'CREATE' ( 'OR' 'REPLACE' )? 'FUNCTION' function '(' params? ')'
  //                              ( 'IF' 'NOT' 'EXISTS' )?
  //                              'LANGUAGE' 'JAVASCRIPT' 'AS' obj 'AT'
  public static boolean create_function_external(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_function_external")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CREATE_FUNCTION_EXTERNAL, "<create function external>");
    r = consumeToken(b, "CREATE");
    r = r && create_function_external_1(b, l + 1);
    r = r && consumeToken(b, "FUNCTION");
    r = r && function(b, l + 1);
    r = r && consumeToken(b, "(");
    r = r && create_function_external_5(b, l + 1);
    r = r && consumeToken(b, ")");
    r = r && create_function_external_7(b, l + 1);
    r = r && consumeToken(b, "LANGUAGE");
    r = r && consumeToken(b, "JAVASCRIPT");
    r = r && consumeToken(b, "AS");
    r = r && obj(b, l + 1);
    r = r && consumeToken(b, "AT");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( 'OR' 'REPLACE' )?
  private static boolean create_function_external_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_function_external_1")) return false;
    create_function_external_1_0(b, l + 1);
    return true;
  }

  // 'OR' 'REPLACE'
  private static boolean create_function_external_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_function_external_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "OR");
    r = r && consumeToken(b, "REPLACE");
    exit_section_(b, m, null, r);
    return r;
  }

  // params?
  private static boolean create_function_external_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_function_external_5")) return false;
    params(b, l + 1);
    return true;
  }

  // ( 'IF' 'NOT' 'EXISTS' )?
  private static boolean create_function_external_7(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_function_external_7")) return false;
    create_function_external_7_0(b, l + 1);
    return true;
  }

  // 'IF' 'NOT' 'EXISTS'
  private static boolean create_function_external_7_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_function_external_7_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "IF");
    r = r && consumeToken(b, "NOT");
    r = r && consumeToken(b, "EXISTS");
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // 'CREATE' ( 'OR' 'REPLACE' )? 'FUNCTION' function '(' params? ')'
  //                            ( 'IF' 'NOT' 'EXISTS' )?
  //                            ( '{' body '}' | 'LANGUAGE' 'INLINE' 'AS' body )
  public static boolean create_function_inline(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_function_inline")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CREATE_FUNCTION_INLINE, "<create function inline>");
    r = consumeToken(b, "CREATE");
    r = r && create_function_inline_1(b, l + 1);
    r = r && consumeToken(b, "FUNCTION");
    r = r && function(b, l + 1);
    r = r && consumeToken(b, "(");
    r = r && create_function_inline_5(b, l + 1);
    r = r && consumeToken(b, ")");
    r = r && create_function_inline_7(b, l + 1);
    r = r && create_function_inline_8(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( 'OR' 'REPLACE' )?
  private static boolean create_function_inline_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_function_inline_1")) return false;
    create_function_inline_1_0(b, l + 1);
    return true;
  }

  // 'OR' 'REPLACE'
  private static boolean create_function_inline_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_function_inline_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "OR");
    r = r && consumeToken(b, "REPLACE");
    exit_section_(b, m, null, r);
    return r;
  }

  // params?
  private static boolean create_function_inline_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_function_inline_5")) return false;
    params(b, l + 1);
    return true;
  }

  // ( 'IF' 'NOT' 'EXISTS' )?
  private static boolean create_function_inline_7(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_function_inline_7")) return false;
    create_function_inline_7_0(b, l + 1);
    return true;
  }

  // 'IF' 'NOT' 'EXISTS'
  private static boolean create_function_inline_7_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_function_inline_7_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "IF");
    r = r && consumeToken(b, "NOT");
    r = r && consumeToken(b, "EXISTS");
    exit_section_(b, m, null, r);
    return r;
  }

  // '{' body '}' | 'LANGUAGE' 'INLINE' 'AS' body
  private static boolean create_function_inline_8(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_function_inline_8")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = create_function_inline_8_0(b, l + 1);
    if (!r) r = create_function_inline_8_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // '{' body '}'
  private static boolean create_function_inline_8_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_function_inline_8_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "{");
    r = r && body(b, l + 1);
    r = r && consumeToken(b, "}");
    exit_section_(b, m, null, r);
    return r;
  }

  // 'LANGUAGE' 'INLINE' 'AS' body
  private static boolean create_function_inline_8_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_function_inline_8_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "LANGUAGE");
    r = r && consumeToken(b, "INLINE");
    r = r && consumeToken(b, "AS");
    r = r && body(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // 'CREATE' 'INDEX' index-name ( 'IF' 'NOT' 'EXISTS' )? 'ON' keyspace-ref
  //                  '(' index-key lead-key-attribs? ( ( ',' index-key key-attribs? )+ )? ')'
  //                  index-partition? where-clause? index-using? index-with?
  public static boolean create_index(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_index")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CREATE_INDEX, "<create index>");
    r = consumeToken(b, "CREATE");
    r = r && consumeToken(b, "INDEX");
    r = r && index_name(b, l + 1);
    r = r && create_index_3(b, l + 1);
    r = r && consumeToken(b, "ON");
    r = r && keyspace_ref(b, l + 1);
    r = r && consumeToken(b, "(");
    r = r && index_key(b, l + 1);
    r = r && create_index_8(b, l + 1);
    r = r && create_index_9(b, l + 1);
    r = r && consumeToken(b, ")");
    r = r && create_index_11(b, l + 1);
    r = r && create_index_12(b, l + 1);
    r = r && create_index_13(b, l + 1);
    r = r && create_index_14(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( 'IF' 'NOT' 'EXISTS' )?
  private static boolean create_index_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_index_3")) return false;
    create_index_3_0(b, l + 1);
    return true;
  }

  // 'IF' 'NOT' 'EXISTS'
  private static boolean create_index_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_index_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "IF");
    r = r && consumeToken(b, "NOT");
    r = r && consumeToken(b, "EXISTS");
    exit_section_(b, m, null, r);
    return r;
  }

  // lead-key-attribs?
  private static boolean create_index_8(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_index_8")) return false;
    lead_key_attribs(b, l + 1);
    return true;
  }

  // ( ( ',' index-key key-attribs? )+ )?
  private static boolean create_index_9(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_index_9")) return false;
    create_index_9_0(b, l + 1);
    return true;
  }

  // ( ',' index-key key-attribs? )+
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

  // ',' index-key key-attribs?
  private static boolean create_index_9_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_index_9_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ",");
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
  // 'CREATE' 'PRIMARY' 'INDEX' index-name? ( 'IF' 'NOT' 'EXISTS' )?
  //                          'ON' keyspace-ref index-using? index-with?
  public static boolean create_primary_index(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_primary_index")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CREATE_PRIMARY_INDEX, "<create primary index>");
    r = consumeToken(b, "CREATE");
    r = r && consumeToken(b, "PRIMARY");
    r = r && consumeToken(b, "INDEX");
    r = r && create_primary_index_3(b, l + 1);
    r = r && create_primary_index_4(b, l + 1);
    r = r && consumeToken(b, "ON");
    r = r && keyspace_ref(b, l + 1);
    r = r && create_primary_index_7(b, l + 1);
    r = r && create_primary_index_8(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // index-name?
  private static boolean create_primary_index_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_primary_index_3")) return false;
    index_name(b, l + 1);
    return true;
  }

  // ( 'IF' 'NOT' 'EXISTS' )?
  private static boolean create_primary_index_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_primary_index_4")) return false;
    create_primary_index_4_0(b, l + 1);
    return true;
  }

  // 'IF' 'NOT' 'EXISTS'
  private static boolean create_primary_index_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_primary_index_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "IF");
    r = r && consumeToken(b, "NOT");
    r = r && consumeToken(b, "EXISTS");
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
  // 'CREATE' 'SCOPE' ( namespace ':' )? bucket '.' scope ( 'IF' 'NOT' 'EXISTS' )?
  public static boolean create_scope(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_scope")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CREATE_SCOPE, "<create scope>");
    r = consumeToken(b, "CREATE");
    r = r && consumeToken(b, "SCOPE");
    r = r && create_scope_2(b, l + 1);
    r = r && bucket(b, l + 1);
    r = r && consumeToken(b, ".");
    r = r && scope(b, l + 1);
    r = r && create_scope_6(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( namespace ':' )?
  private static boolean create_scope_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_scope_2")) return false;
    create_scope_2_0(b, l + 1);
    return true;
  }

  // namespace ':'
  private static boolean create_scope_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_scope_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = namespace(b, l + 1);
    r = r && consumeToken(b, ":");
    exit_section_(b, m, null, r);
    return r;
  }

  // ( 'IF' 'NOT' 'EXISTS' )?
  private static boolean create_scope_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_scope_6")) return false;
    create_scope_6_0(b, l + 1);
    return true;
  }

  // 'IF' 'NOT' 'EXISTS'
  private static boolean create_scope_6_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "create_scope_6_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "IF");
    r = r && consumeToken(b, "NOT");
    r = r && consumeToken(b, "EXISTS");
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
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CREATE_STATEMENT, "<create statement>");
    r = create_scope(b, l + 1);
    if (!r) r = create_collection(b, l + 1);
    if (!r) r = create_primary_index(b, l + 1);
    if (!r) r = create_index(b, l + 1);
    if (!r) r = create_function(b, l + 1);
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
  // 'DELETE' 'FROM' target-keyspace use-keys-clause? where-clause?
  //             limit-clause? returning-clause?
  public static boolean delete(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "delete")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, DELETE, "<delete>");
    r = consumeToken(b, "DELETE");
    r = r && consumeToken(b, "FROM");
    r = r && target_keyspace(b, l + 1);
    r = r && delete_3(b, l + 1);
    r = r && delete_4(b, l + 1);
    r = r && delete_5(b, l + 1);
    r = r && delete_6(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // use-keys-clause?
  private static boolean delete_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "delete_3")) return false;
    use_keys_clause(b, l + 1);
    return true;
  }

  // where-clause?
  private static boolean delete_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "delete_4")) return false;
    where_clause(b, l + 1);
    return true;
  }

  // limit-clause?
  private static boolean delete_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "delete_5")) return false;
    limit_clause(b, l + 1);
    return true;
  }

  // returning-clause?
  private static boolean delete_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "delete_6")) return false;
    returning_clause(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // 'ALL' | 'STATISTICS'
  public static boolean delete_all(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "delete_all")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, DELETE_ALL, "<delete all>");
    r = consumeToken(b, "ALL");
    if (!r) r = consumeToken(b, "STATISTICS");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'DELETE' ( delete-expr | delete-all )
  public static boolean delete_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "delete_clause")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, DELETE_CLAUSE, "<delete clause>");
    r = consumeToken(b, "DELETE");
    r = r && delete_clause_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
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
  // 'STATISTICS'? '(' index-key ( ',' index-key )* ')'
  public static boolean delete_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "delete_expr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, DELETE_EXPR, "<delete expr>");
    r = delete_expr_0(b, l + 1);
    r = r && consumeToken(b, "(");
    r = r && index_key(b, l + 1);
    r = r && delete_expr_3(b, l + 1);
    r = r && consumeToken(b, ")");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // 'STATISTICS'?
  private static boolean delete_expr_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "delete_expr_0")) return false;
    consumeToken(b, "STATISTICS");
    return true;
  }

  // ( ',' index-key )*
  private static boolean delete_expr_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "delete_expr_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!delete_expr_3_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "delete_expr_3", c)) break;
    }
    return true;
  }

  // ',' index-key
  private static boolean delete_expr_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "delete_expr_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ",");
    r = r && index_key(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // delete
  //                 | insert
  //                 | merge
  //                 | update
  //                 | upsert
  public static boolean dml_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dml_statement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, DML_STATEMENT, "<dml statement>");
    r = delete(b, l + 1);
    if (!r) r = insert(b, l + 1);
    if (!r) r = merge(b, l + 1);
    if (!r) r = update(b, l + 1);
    if (!r) r = upsert(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // select |
  //                   infer |
  //                   update-statistics
  public static boolean dql_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dql_statement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, DQL_STATEMENT, "<dql statement>");
    r = select(b, l + 1);
    if (!r) r = infer(b, l + 1);
    if (!r) r = update_statistics(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'DROP' 'COLLECTION' ( ( namespace ':' )? bucket '.' scope '.' )?
  //                     collection ( 'IF' 'EXISTS' )?
  public static boolean drop_collection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "drop_collection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, DROP_COLLECTION, "<drop collection>");
    r = consumeToken(b, "DROP");
    r = r && consumeToken(b, "COLLECTION");
    r = r && drop_collection_2(b, l + 1);
    r = r && collection(b, l + 1);
    r = r && drop_collection_4(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( ( namespace ':' )? bucket '.' scope '.' )?
  private static boolean drop_collection_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "drop_collection_2")) return false;
    drop_collection_2_0(b, l + 1);
    return true;
  }

  // ( namespace ':' )? bucket '.' scope '.'
  private static boolean drop_collection_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "drop_collection_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = drop_collection_2_0_0(b, l + 1);
    r = r && bucket(b, l + 1);
    r = r && consumeToken(b, ".");
    r = r && scope(b, l + 1);
    r = r && consumeToken(b, ".");
    exit_section_(b, m, null, r);
    return r;
  }

  // ( namespace ':' )?
  private static boolean drop_collection_2_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "drop_collection_2_0_0")) return false;
    drop_collection_2_0_0_0(b, l + 1);
    return true;
  }

  // namespace ':'
  private static boolean drop_collection_2_0_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "drop_collection_2_0_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = namespace(b, l + 1);
    r = r && consumeToken(b, ":");
    exit_section_(b, m, null, r);
    return r;
  }

  // ( 'IF' 'EXISTS' )?
  private static boolean drop_collection_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "drop_collection_4")) return false;
    drop_collection_4_0(b, l + 1);
    return true;
  }

  // 'IF' 'EXISTS'
  private static boolean drop_collection_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "drop_collection_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "IF");
    r = r && consumeToken(b, "EXISTS");
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // 'DROP' 'FUNCTION' function ( 'IF' 'EXISTS' )?
  public static boolean drop_function(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "drop_function")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, DROP_FUNCTION, "<drop function>");
    r = consumeToken(b, "DROP");
    r = r && consumeToken(b, "FUNCTION");
    r = r && function(b, l + 1);
    r = r && drop_function_3(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( 'IF' 'EXISTS' )?
  private static boolean drop_function_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "drop_function_3")) return false;
    drop_function_3_0(b, l + 1);
    return true;
  }

  // 'IF' 'EXISTS'
  private static boolean drop_function_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "drop_function_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "IF");
    r = r && consumeToken(b, "EXISTS");
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // 'DROP' 'INDEX' ( index-path '.' index-name ( 'IF' 'EXISTS' )? |
  //                 index-name ( 'IF' 'EXISTS' )? 'ON' keyspace-ref ) index-using?
  public static boolean drop_index(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "drop_index")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, DROP_INDEX, "<drop index>");
    r = consumeToken(b, "DROP");
    r = r && consumeToken(b, "INDEX");
    r = r && drop_index_2(b, l + 1);
    r = r && drop_index_3(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // index-path '.' index-name ( 'IF' 'EXISTS' )? |
  //                 index-name ( 'IF' 'EXISTS' )? 'ON' keyspace-ref
  private static boolean drop_index_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "drop_index_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = drop_index_2_0(b, l + 1);
    if (!r) r = drop_index_2_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // index-path '.' index-name ( 'IF' 'EXISTS' )?
  private static boolean drop_index_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "drop_index_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = index_path(b, l + 1);
    r = r && consumeToken(b, ".");
    r = r && index_name(b, l + 1);
    r = r && drop_index_2_0_3(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( 'IF' 'EXISTS' )?
  private static boolean drop_index_2_0_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "drop_index_2_0_3")) return false;
    drop_index_2_0_3_0(b, l + 1);
    return true;
  }

  // 'IF' 'EXISTS'
  private static boolean drop_index_2_0_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "drop_index_2_0_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "IF");
    r = r && consumeToken(b, "EXISTS");
    exit_section_(b, m, null, r);
    return r;
  }

  // index-name ( 'IF' 'EXISTS' )? 'ON' keyspace-ref
  private static boolean drop_index_2_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "drop_index_2_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = index_name(b, l + 1);
    r = r && drop_index_2_1_1(b, l + 1);
    r = r && consumeToken(b, "ON");
    r = r && keyspace_ref(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( 'IF' 'EXISTS' )?
  private static boolean drop_index_2_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "drop_index_2_1_1")) return false;
    drop_index_2_1_1_0(b, l + 1);
    return true;
  }

  // 'IF' 'EXISTS'
  private static boolean drop_index_2_1_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "drop_index_2_1_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "IF");
    r = r && consumeToken(b, "EXISTS");
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
  // 'DROP' 'PRIMARY' 'INDEX' ( 'IF' 'EXISTS' )? 'ON' keyspace-ref
  //                        index-using?
  public static boolean drop_primary_index(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "drop_primary_index")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, DROP_PRIMARY_INDEX, "<drop primary index>");
    r = consumeToken(b, "DROP");
    r = r && consumeToken(b, "PRIMARY");
    r = r && consumeToken(b, "INDEX");
    r = r && drop_primary_index_3(b, l + 1);
    r = r && consumeToken(b, "ON");
    r = r && keyspace_ref(b, l + 1);
    r = r && drop_primary_index_6(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( 'IF' 'EXISTS' )?
  private static boolean drop_primary_index_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "drop_primary_index_3")) return false;
    drop_primary_index_3_0(b, l + 1);
    return true;
  }

  // 'IF' 'EXISTS'
  private static boolean drop_primary_index_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "drop_primary_index_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "IF");
    r = r && consumeToken(b, "EXISTS");
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
  // 'DROP' 'SCOPE' ( namespace ':' )? bucket '.' scope ( 'IF' 'EXISTS' )?
  public static boolean drop_scope(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "drop_scope")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, DROP_SCOPE, "<drop scope>");
    r = consumeToken(b, "DROP");
    r = r && consumeToken(b, "SCOPE");
    r = r && drop_scope_2(b, l + 1);
    r = r && bucket(b, l + 1);
    r = r && consumeToken(b, ".");
    r = r && scope(b, l + 1);
    r = r && drop_scope_6(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( namespace ':' )?
  private static boolean drop_scope_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "drop_scope_2")) return false;
    drop_scope_2_0(b, l + 1);
    return true;
  }

  // namespace ':'
  private static boolean drop_scope_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "drop_scope_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = namespace(b, l + 1);
    r = r && consumeToken(b, ":");
    exit_section_(b, m, null, r);
    return r;
  }

  // ( 'IF' 'EXISTS' )?
  private static boolean drop_scope_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "drop_scope_6")) return false;
    drop_scope_6_0(b, l + 1);
    return true;
  }

  // 'IF' 'EXISTS'
  private static boolean drop_scope_6_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "drop_scope_6_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "IF");
    r = r && consumeToken(b, "EXISTS");
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
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, DROP_STATEMENT, "<drop statement>");
    r = drop_scope(b, l + 1);
    if (!r) r = drop_collection(b, l + 1);
    if (!r) r = drop_primary_index(b, l + 1);
    if (!r) r = drop_index(b, l + 1);
    if (!r) r = drop_function(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // expr '[' position ']'
  public static boolean element_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "element_expr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ELEMENT_EXPR, "<element expr>");
    r = expr(b, l + 1);
    r = r && consumeToken(b, "[");
    r = r && position(b, l + 1);
    r = r && consumeToken(b, "]");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // expr
  public static boolean end_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "end_expr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, END_EXPR, "<end expr>");
    r = expr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '`' chr+ '`'
  public static boolean escaped_identifier(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "escaped_identifier")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ESCAPED_IDENTIFIER, "<escaped identifier>");
    r = consumeToken(b, "`");
    r = r && escaped_identifier_1(b, l + 1);
    r = r && consumeToken(b, "`");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // chr+
  private static boolean escaped_identifier_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "escaped_identifier_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = chr(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!chr(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "escaped_identifier_1", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // 'EXECUTE' 'FUNCTION' function '(' ( expr ( ',' expr )* )? ')'
  public static boolean execute_function(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "execute_function")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, EXECUTE_FUNCTION, "<execute function>");
    r = consumeToken(b, "EXECUTE");
    r = r && consumeToken(b, "FUNCTION");
    r = r && function(b, l + 1);
    r = r && consumeToken(b, "(");
    r = r && execute_function_4(b, l + 1);
    r = r && consumeToken(b, ")");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( expr ( ',' expr )* )?
  private static boolean execute_function_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "execute_function_4")) return false;
    execute_function_4_0(b, l + 1);
    return true;
  }

  // expr ( ',' expr )*
  private static boolean execute_function_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "execute_function_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = expr(b, l + 1);
    r = r && execute_function_4_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( ',' expr )*
  private static boolean execute_function_4_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "execute_function_4_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!execute_function_4_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "execute_function_4_0_1", c)) break;
    }
    return true;
  }

  // ',' expr
  private static boolean execute_function_4_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "execute_function_4_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ",");
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // 'EXISTS' expr
  public static boolean exists_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exists_expr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, EXISTS_EXPR, "<exists expr>");
    r = consumeToken(b, "EXISTS");
    r = r && expr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'EXPLAIN' statement
  public static boolean explain(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "explain")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, EXPLAIN, "<explain>");
    r = consumeToken(b, "EXPLAIN");
    r = r && statement(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ('e' | 'E') ('-' | '+')? [0-9]+
  public static boolean exponent(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exponent")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, EXPONENT, "<exponent>");
    r = exponent_0(b, l + 1);
    r = r && exponent_1(b, l + 1);
    r = r && exponent_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // 'e' | 'E'
  private static boolean exponent_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exponent_0")) return false;
    boolean r;
    r = consumeToken(b, "e");
    if (!r) r = consumeToken(b, "E");
    return r;
  }

  // ('-' | '+')?
  private static boolean exponent_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exponent_1")) return false;
    exponent_1_0(b, l + 1);
    return true;
  }

  // '-' | '+'
  private static boolean exponent_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exponent_1_0")) return false;
    boolean r;
    r = consumeToken(b, "-");
    if (!r) r = consumeToken(b, "+");
    return r;
  }

  // [0-9]
  private static boolean exponent_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exponent_2")) return false;
    consumeToken(b, "0-9");
    return true;
  }

  /* ********************************************************** */
  // literal |
  //          identifier |
  //          arithmetic-term |
  //          comparison-term |
  //          concatenation-term |
  //          logical-term |
  //          case-expr |
  //          collection-expr |
  //          nested-expr |
  //          function-call |
  //          subquery-expr |
  //          '(' expr ')'
  public static boolean expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, EXPR, "<expr>");
    r = literal(b, l + 1);
    if (!r) r = identifier(b, l + 1);
    if (!r) r = arithmetic_term(b, l + 1);
    if (!r) r = comparison_term(b, l + 1);
    if (!r) r = concatenation_term(b, l + 1);
    if (!r) r = logical_term(b, l + 1);
    if (!r) r = case_expr(b, l + 1);
    if (!r) r = collection_expr(b, l + 1);
    if (!r) r = nested_expr(b, l + 1);
    if (!r) r = function_call(b, l + 1);
    if (!r) r = subquery_expr(b, l + 1);
    if (!r) r = expr_11(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '(' expr ')'
  private static boolean expr_11(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr_11")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "(");
    r = r && expr(b, l + 1);
    r = r && consumeToken(b, ")");
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // expr '.' ( identifier | ( ( escaped-identifier | '[' name-expr ']' ) 'i'? ) )
  public static boolean field_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "field_expr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FIELD_EXPR, "<field expr>");
    r = expr(b, l + 1);
    r = r && consumeToken(b, ".");
    r = r && field_expr_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // identifier | ( ( escaped-identifier | '[' name-expr ']' ) 'i'? )
  private static boolean field_expr_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "field_expr_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = identifier(b, l + 1);
    if (!r) r = field_expr_2_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( escaped-identifier | '[' name-expr ']' ) 'i'?
  private static boolean field_expr_2_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "field_expr_2_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = field_expr_2_1_0(b, l + 1);
    r = r && field_expr_2_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // escaped-identifier | '[' name-expr ']'
  private static boolean field_expr_2_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "field_expr_2_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = escaped_identifier(b, l + 1);
    if (!r) r = field_expr_2_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // '[' name-expr ']'
  private static boolean field_expr_2_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "field_expr_2_1_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "[");
    r = r && name_expr(b, l + 1);
    r = r && consumeToken(b, "]");
    exit_section_(b, m, null, r);
    return r;
  }

  // 'i'?
  private static boolean field_expr_2_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "field_expr_2_1_1")) return false;
    consumeToken(b, "i");
    return true;
  }

  /* ********************************************************** */
  // 'FILTER' '(' 'WHERE' cond ')'
  public static boolean filter_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "filter_clause")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FILTER_CLAUSE, "<filter clause>");
    r = consumeToken(b, "FILTER");
    r = r && consumeToken(b, "(");
    r = r && consumeToken(b, "WHERE");
    r = r && cond(b, l + 1);
    r = r && consumeToken(b, ")");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '.' [0-9]+
  public static boolean fraction(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fraction")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FRACTION, "<fraction>");
    r = consumeToken(b, ".");
    r = r && fraction_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // [0-9]
  private static boolean fraction_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fraction_1")) return false;
    consumeToken(b, "0-9");
    return true;
  }

  /* ********************************************************** */
  // 'FROM' from-terms
  public static boolean from_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "from_clause")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FROM_CLAUSE, "<from clause>");
    r = consumeToken(b, "FROM");
    r = r && from_terms(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // expr ( 'AS' alias )?
  public static boolean from_generic(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "from_generic")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FROM_GENERIC, "<from generic>");
    r = expr(b, l + 1);
    r = r && from_generic_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( 'AS' alias )?
  private static boolean from_generic_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "from_generic_1")) return false;
    from_generic_1_0(b, l + 1);
    return true;
  }

  // 'AS' alias
  private static boolean from_generic_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "from_generic_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "AS");
    r = r && alias(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // keyspace-ref ( 'AS'? alias )? use-clause?
  public static boolean from_keyspace(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "from_keyspace")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FROM_KEYSPACE, "<from keyspace>");
    r = keyspace_ref(b, l + 1);
    r = r && from_keyspace_1(b, l + 1);
    r = r && from_keyspace_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( 'AS'? alias )?
  private static boolean from_keyspace_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "from_keyspace_1")) return false;
    from_keyspace_1_0(b, l + 1);
    return true;
  }

  // 'AS'? alias
  private static boolean from_keyspace_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "from_keyspace_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = from_keyspace_1_0_0(b, l + 1);
    r = r && alias(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // 'AS'?
  private static boolean from_keyspace_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "from_keyspace_1_0_0")) return false;
    consumeToken(b, "AS");
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
  // subquery-expr 'AS'? alias
  public static boolean from_subquery(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "from_subquery")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FROM_SUBQUERY, "<from subquery>");
    r = subquery_expr(b, l + 1);
    r = r && from_subquery_1(b, l + 1);
    r = r && alias(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // 'AS'?
  private static boolean from_subquery_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "from_subquery_1")) return false;
    consumeToken(b, "AS");
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
  // '"index_fts"' ':' ( index-array | index-object )
  public static boolean fts_hint_json(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fts_hint_json")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FTS_HINT_JSON, "<fts hint json>");
    r = consumeToken(b, "\"index_fts\"");
    r = r && consumeToken(b, ":");
    r = r && fts_hint_json_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
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
  // 'INDEX_FTS' '(' keyspace  ')'
  public static boolean fts_hint_simple(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fts_hint_simple")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FTS_HINT_SIMPLE, "<fts hint simple>");
    r = consumeToken(b, "INDEX_FTS");
    r = r && consumeToken(b, "(");
    r = r && keyspace(b, l + 1);
    r = r && consumeToken(b, ")");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ( 'ALL' | 'DISTINCT' ) 'ARRAY' var-expr
  //                     'FOR' var ( 'IN' | 'WITHIN' ) expr
  //                     ( ',' var ( 'IN' | 'WITHIN' ) expr )* ( 'WHEN' cond )? 'END'
  public static boolean full_array_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "full_array_expr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FULL_ARRAY_EXPR, "<full array expr>");
    r = full_array_expr_0(b, l + 1);
    r = r && consumeToken(b, "ARRAY");
    r = r && var_expr(b, l + 1);
    r = r && consumeToken(b, "FOR");
    r = r && var(b, l + 1);
    r = r && full_array_expr_5(b, l + 1);
    r = r && expr(b, l + 1);
    r = r && full_array_expr_7(b, l + 1);
    r = r && full_array_expr_8(b, l + 1);
    r = r && consumeToken(b, "END");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // 'ALL' | 'DISTINCT'
  private static boolean full_array_expr_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "full_array_expr_0")) return false;
    boolean r;
    r = consumeToken(b, "ALL");
    if (!r) r = consumeToken(b, "DISTINCT");
    return r;
  }

  // 'IN' | 'WITHIN'
  private static boolean full_array_expr_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "full_array_expr_5")) return false;
    boolean r;
    r = consumeToken(b, "IN");
    if (!r) r = consumeToken(b, "WITHIN");
    return r;
  }

  // ( ',' var ( 'IN' | 'WITHIN' ) expr )*
  private static boolean full_array_expr_7(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "full_array_expr_7")) return false;
    while (true) {
      int c = current_position_(b);
      if (!full_array_expr_7_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "full_array_expr_7", c)) break;
    }
    return true;
  }

  // ',' var ( 'IN' | 'WITHIN' ) expr
  private static boolean full_array_expr_7_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "full_array_expr_7_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ",");
    r = r && var(b, l + 1);
    r = r && full_array_expr_7_0_2(b, l + 1);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // 'IN' | 'WITHIN'
  private static boolean full_array_expr_7_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "full_array_expr_7_0_2")) return false;
    boolean r;
    r = consumeToken(b, "IN");
    if (!r) r = consumeToken(b, "WITHIN");
    return r;
  }

  // ( 'WHEN' cond )?
  private static boolean full_array_expr_8(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "full_array_expr_8")) return false;
    full_array_expr_8_0(b, l + 1);
    return true;
  }

  // 'WHEN' cond
  private static boolean full_array_expr_8_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "full_array_expr_8_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "WHEN");
    r = r && cond(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // 'ABS' | 'ACOS' | 'ARRAY_APPEND' | 'ARRAY_AVG' | 'ARRAY_CONCAT' | 'ARRAY_CONTAINS' | 'ARRAY_COUNT' | 'ARRAY_DISTINCT' | 'ARRAY_FLATTEN' | 'ARRAY_IFNULL' | 'ARRAY_INSERT' | 'ARRAY_INTERSECT' | 'ARRAY_LENGTH' | 'ARRAY_MAX' | 'ARRAY_MIN' | 'ARRAY_POSITION' | 'ARRAY_PREPEND' | 'ARRAY_PUT' | 'ARRAY_RANGE' | 'ARRAY_REMOVE' | 'ARRAY_REPEAT' | 'ARRAY_REPLACE' | 'ARRAY_REVERSE' | 'ARRAY_SORT' | 'ARRAY_STAR' | 'ARRAY_SUM' | 'ARRAY_SYMDIFF' | 'ARRAY_SYMDIFF1' | 'ARRAY_SYMDIFFN' | 'ARRAY_UNION' | 'ASIN' | 'ATAN' | 'ATAN2' | 'AVG' | 'CEIL' | 'CLOCK_LOCAL' | 'CLOCK_MILLIS' | 'CLOCK_STR' | 'CLOCK_TZ' | 'CLOCK_UTC' | 'CONCAT' | 'CONTAINS' | 'COS' | 'COUNT' | 'DATE_ADD_MILLIS' | 'DATE_ADD_STR' | 'DATE_DIFF_MILLIS' | 'DATE_DIFF_STR' | 'DATE_FORMAT_STR' | 'DATE_PART_MILLIS' | 'DATE_PART_STR' | 'DATE_RANGE_MILLIS' | 'DATE_RANGE_STR' | 'DATE_TRUNC_MILLIS' | 'DATE_TRUNC_STR' | 'DECODE_JSON' | 'DEGREES' | 'DURATION_TO_STR' | 'E' | 'ENCODE_JSON' | 'ENCODED_SIZE' | 'EXP' | 'FLOOR' | 'GREATEST' | 'IF_INF' | 'IF_MISSING' | 'IF_MISSING_OR_NULL' | 'IF_NAN' | 'IF_NAN_OR_INF' | 'IF_NULL' | 'IFINF' | 'IFMISSING' | 'IFMISSINGORNULL' | 'IFNAN' | 'IFNANORINF' | 'IFNULL' | 'INITCAP' | 'IS_ARRAY' | 'IS_ATOM' | 'IS_BOOL' | 'IS_BOOLEAN' | 'IS_NUM' | 'IS_NUMBER' | 'IS_OBJ' | 'IS_OBJECT' | 'IS_STR' | 'IS_STRING' | 'ISARRAY' | 'ISATOM' | 'ISBOOL' | 'ISBOOLEAN' | 'ISNUM' | 'ISNUMBER' | 'ISOBJ' | 'ISOBJECT' | 'ISSTR' | 'ISSTRING' | 'LEAST' | 'LENGTH' | 'LN' | 'LOG' | 'LOWER' | 'LTRIM' | 'MAX' | 'META' | 'MILLIS' | 'MILLIS_TO_LOCAL' | 'MILLIS_TO_STR' | 'MILLIS_TO_TZ' | 'MILLIS_TO_UTC' | 'MILLIS_TO_ZONE_NAME' | 'MIN' | 'MISSING_IF' | 'MISSINGIF' | 'NAN_IF' | 'NANIF' | 'NEGINF_IF' | 'NEGINFIF' | 'NOW_LOCAL' | 'NOW_MILLIS' | 'NOW_STR' | 'NOW_TZ' | 'NOW_UTC' | 'NULL_IF' | 'NULLIF' | 'OBJECT_ADD' | 'OBJECT_CONCAT' | 'OBJECT_INNER_VALUES' | 'OBJECT_LENGTH' | 'OBJECT_NAMES' | 'OBJECT_PAIRS' | 'OBJECT_PUT' | 'OBJECT_REMOVE' | 'OBJECT_RENAME' | 'OBJECT_REPLACE' | 'OBJECT_UNWRAP' | 'OBJECT_VALUES' | 'PAIRS' | 'PI' | 'POSINF_IF' | 'POSINFIF' | 'POSITION' | 'POWER' | 'RADIANS' | 'RANDOM' | 'REGEXP_CONTAINS' | 'REGEXP_LIKE' | 'REGEXP_POSITION' | 'REGEXP_REPLACE' | 'REPEAT' | 'REPLACE' | 'REVERSE' | 'ROUND' | 'RTRIM' | 'SIGN' | 'SIN' | 'SPLIT' | 'SQRT' | 'STR_TO_DURATION' | 'STR_TO_MILLIS' | 'STR_TO_TZ' | 'STR_TO_UTC' | 'STR_TO_ZONE_NAME' | 'SUBSTR' | 'SUM' | 'TAN' | 'TITLE' | 'TO_ARRAY' | 'TO_ATOM' | 'TO_BOOL' | 'TO_BOOLEAN' | 'TO_NUM' | 'TO_NUMBER' | 'TO_OBJ' | 'TO_OBJECT' | 'TO_STR' | 'TO_STRING' | 'TOARRAY' | 'TOATOM' | 'TOBOOL' | 'TOBOOLEAN' | 'TONUM' | 'TONUMBER' | 'TOOBJ' | 'TOOBJECT' | 'TOSTR' | 'TOSTRING' | 'TRIM' | 'TRUNC' | 'TYPE' | 'TYPENAME' | 'UPPER' | 'UUID' | 'WEEKDAY_MILLIS' | 'WEEKDAY_STR'
  public static boolean funcs(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "funcs")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FUNCS, "<funcs>");
    r = consumeToken(b, "ABS");
    if (!r) r = consumeToken(b, "ACOS");
    if (!r) r = consumeToken(b, "ARRAY_APPEND");
    if (!r) r = consumeToken(b, "ARRAY_AVG");
    if (!r) r = consumeToken(b, "ARRAY_CONCAT");
    if (!r) r = consumeToken(b, "ARRAY_CONTAINS");
    if (!r) r = consumeToken(b, "ARRAY_COUNT");
    if (!r) r = consumeToken(b, "ARRAY_DISTINCT");
    if (!r) r = consumeToken(b, "ARRAY_FLATTEN");
    if (!r) r = consumeToken(b, "ARRAY_IFNULL");
    if (!r) r = consumeToken(b, "ARRAY_INSERT");
    if (!r) r = consumeToken(b, "ARRAY_INTERSECT");
    if (!r) r = consumeToken(b, "ARRAY_LENGTH");
    if (!r) r = consumeToken(b, "ARRAY_MAX");
    if (!r) r = consumeToken(b, "ARRAY_MIN");
    if (!r) r = consumeToken(b, "ARRAY_POSITION");
    if (!r) r = consumeToken(b, "ARRAY_PREPEND");
    if (!r) r = consumeToken(b, "ARRAY_PUT");
    if (!r) r = consumeToken(b, "ARRAY_RANGE");
    if (!r) r = consumeToken(b, "ARRAY_REMOVE");
    if (!r) r = consumeToken(b, "ARRAY_REPEAT");
    if (!r) r = consumeToken(b, "ARRAY_REPLACE");
    if (!r) r = consumeToken(b, "ARRAY_REVERSE");
    if (!r) r = consumeToken(b, "ARRAY_SORT");
    if (!r) r = consumeToken(b, "ARRAY_STAR");
    if (!r) r = consumeToken(b, "ARRAY_SUM");
    if (!r) r = consumeToken(b, "ARRAY_SYMDIFF");
    if (!r) r = consumeToken(b, "ARRAY_SYMDIFF1");
    if (!r) r = consumeToken(b, "ARRAY_SYMDIFFN");
    if (!r) r = consumeToken(b, "ARRAY_UNION");
    if (!r) r = consumeToken(b, "ASIN");
    if (!r) r = consumeToken(b, "ATAN");
    if (!r) r = consumeToken(b, "ATAN2");
    if (!r) r = consumeToken(b, "AVG");
    if (!r) r = consumeToken(b, "CEIL");
    if (!r) r = consumeToken(b, "CLOCK_LOCAL");
    if (!r) r = consumeToken(b, "CLOCK_MILLIS");
    if (!r) r = consumeToken(b, "CLOCK_STR");
    if (!r) r = consumeToken(b, "CLOCK_TZ");
    if (!r) r = consumeToken(b, "CLOCK_UTC");
    if (!r) r = consumeToken(b, "CONCAT");
    if (!r) r = consumeToken(b, "CONTAINS");
    if (!r) r = consumeToken(b, "COS");
    if (!r) r = consumeToken(b, "COUNT");
    if (!r) r = consumeToken(b, "DATE_ADD_MILLIS");
    if (!r) r = consumeToken(b, "DATE_ADD_STR");
    if (!r) r = consumeToken(b, "DATE_DIFF_MILLIS");
    if (!r) r = consumeToken(b, "DATE_DIFF_STR");
    if (!r) r = consumeToken(b, "DATE_FORMAT_STR");
    if (!r) r = consumeToken(b, "DATE_PART_MILLIS");
    if (!r) r = consumeToken(b, "DATE_PART_STR");
    if (!r) r = consumeToken(b, "DATE_RANGE_MILLIS");
    if (!r) r = consumeToken(b, "DATE_RANGE_STR");
    if (!r) r = consumeToken(b, "DATE_TRUNC_MILLIS");
    if (!r) r = consumeToken(b, "DATE_TRUNC_STR");
    if (!r) r = consumeToken(b, "DECODE_JSON");
    if (!r) r = consumeToken(b, "DEGREES");
    if (!r) r = consumeToken(b, "DURATION_TO_STR");
    if (!r) r = consumeToken(b, "E");
    if (!r) r = consumeToken(b, "ENCODE_JSON");
    if (!r) r = consumeToken(b, "ENCODED_SIZE");
    if (!r) r = consumeToken(b, "EXP");
    if (!r) r = consumeToken(b, "FLOOR");
    if (!r) r = consumeToken(b, "GREATEST");
    if (!r) r = consumeToken(b, "IF_INF");
    if (!r) r = consumeToken(b, "IF_MISSING");
    if (!r) r = consumeToken(b, "IF_MISSING_OR_NULL");
    if (!r) r = consumeToken(b, "IF_NAN");
    if (!r) r = consumeToken(b, "IF_NAN_OR_INF");
    if (!r) r = consumeToken(b, "IF_NULL");
    if (!r) r = consumeToken(b, "IFINF");
    if (!r) r = consumeToken(b, "IFMISSING");
    if (!r) r = consumeToken(b, "IFMISSINGORNULL");
    if (!r) r = consumeToken(b, "IFNAN");
    if (!r) r = consumeToken(b, "IFNANORINF");
    if (!r) r = consumeToken(b, "IFNULL");
    if (!r) r = consumeToken(b, "INITCAP");
    if (!r) r = consumeToken(b, "IS_ARRAY");
    if (!r) r = consumeToken(b, "IS_ATOM");
    if (!r) r = consumeToken(b, "IS_BOOL");
    if (!r) r = consumeToken(b, "IS_BOOLEAN");
    if (!r) r = consumeToken(b, "IS_NUM");
    if (!r) r = consumeToken(b, "IS_NUMBER");
    if (!r) r = consumeToken(b, "IS_OBJ");
    if (!r) r = consumeToken(b, "IS_OBJECT");
    if (!r) r = consumeToken(b, "IS_STR");
    if (!r) r = consumeToken(b, "IS_STRING");
    if (!r) r = consumeToken(b, "ISARRAY");
    if (!r) r = consumeToken(b, "ISATOM");
    if (!r) r = consumeToken(b, "ISBOOL");
    if (!r) r = consumeToken(b, "ISBOOLEAN");
    if (!r) r = consumeToken(b, "ISNUM");
    if (!r) r = consumeToken(b, "ISNUMBER");
    if (!r) r = consumeToken(b, "ISOBJ");
    if (!r) r = consumeToken(b, "ISOBJECT");
    if (!r) r = consumeToken(b, "ISSTR");
    if (!r) r = consumeToken(b, "ISSTRING");
    if (!r) r = consumeToken(b, "LEAST");
    if (!r) r = consumeToken(b, "LENGTH");
    if (!r) r = consumeToken(b, "LN");
    if (!r) r = consumeToken(b, "LOG");
    if (!r) r = consumeToken(b, "LOWER");
    if (!r) r = consumeToken(b, "LTRIM");
    if (!r) r = consumeToken(b, "MAX");
    if (!r) r = consumeToken(b, "META");
    if (!r) r = consumeToken(b, "MILLIS");
    if (!r) r = consumeToken(b, "MILLIS_TO_LOCAL");
    if (!r) r = consumeToken(b, "MILLIS_TO_STR");
    if (!r) r = consumeToken(b, "MILLIS_TO_TZ");
    if (!r) r = consumeToken(b, "MILLIS_TO_UTC");
    if (!r) r = consumeToken(b, "MILLIS_TO_ZONE_NAME");
    if (!r) r = consumeToken(b, "MIN");
    if (!r) r = consumeToken(b, "MISSING_IF");
    if (!r) r = consumeToken(b, "MISSINGIF");
    if (!r) r = consumeToken(b, "NAN_IF");
    if (!r) r = consumeToken(b, "NANIF");
    if (!r) r = consumeToken(b, "NEGINF_IF");
    if (!r) r = consumeToken(b, "NEGINFIF");
    if (!r) r = consumeToken(b, "NOW_LOCAL");
    if (!r) r = consumeToken(b, "NOW_MILLIS");
    if (!r) r = consumeToken(b, "NOW_STR");
    if (!r) r = consumeToken(b, "NOW_TZ");
    if (!r) r = consumeToken(b, "NOW_UTC");
    if (!r) r = consumeToken(b, "NULL_IF");
    if (!r) r = consumeToken(b, "NULLIF");
    if (!r) r = consumeToken(b, "OBJECT_ADD");
    if (!r) r = consumeToken(b, "OBJECT_CONCAT");
    if (!r) r = consumeToken(b, "OBJECT_INNER_VALUES");
    if (!r) r = consumeToken(b, "OBJECT_LENGTH");
    if (!r) r = consumeToken(b, "OBJECT_NAMES");
    if (!r) r = consumeToken(b, "OBJECT_PAIRS");
    if (!r) r = consumeToken(b, "OBJECT_PUT");
    if (!r) r = consumeToken(b, "OBJECT_REMOVE");
    if (!r) r = consumeToken(b, "OBJECT_RENAME");
    if (!r) r = consumeToken(b, "OBJECT_REPLACE");
    if (!r) r = consumeToken(b, "OBJECT_UNWRAP");
    if (!r) r = consumeToken(b, "OBJECT_VALUES");
    if (!r) r = consumeToken(b, "PAIRS");
    if (!r) r = consumeToken(b, "PI");
    if (!r) r = consumeToken(b, "POSINF_IF");
    if (!r) r = consumeToken(b, "POSINFIF");
    if (!r) r = consumeToken(b, "POSITION");
    if (!r) r = consumeToken(b, "POWER");
    if (!r) r = consumeToken(b, "RADIANS");
    if (!r) r = consumeToken(b, "RANDOM");
    if (!r) r = consumeToken(b, "REGEXP_CONTAINS");
    if (!r) r = consumeToken(b, "REGEXP_LIKE");
    if (!r) r = consumeToken(b, "REGEXP_POSITION");
    if (!r) r = consumeToken(b, "REGEXP_REPLACE");
    if (!r) r = consumeToken(b, "REPEAT");
    if (!r) r = consumeToken(b, "REPLACE");
    if (!r) r = consumeToken(b, "REVERSE");
    if (!r) r = consumeToken(b, "ROUND");
    if (!r) r = consumeToken(b, "RTRIM");
    if (!r) r = consumeToken(b, "SIGN");
    if (!r) r = consumeToken(b, "SIN");
    if (!r) r = consumeToken(b, "SPLIT");
    if (!r) r = consumeToken(b, "SQRT");
    if (!r) r = consumeToken(b, "STR_TO_DURATION");
    if (!r) r = consumeToken(b, "STR_TO_MILLIS");
    if (!r) r = consumeToken(b, "STR_TO_TZ");
    if (!r) r = consumeToken(b, "STR_TO_UTC");
    if (!r) r = consumeToken(b, "STR_TO_ZONE_NAME");
    if (!r) r = consumeToken(b, "SUBSTR");
    if (!r) r = consumeToken(b, "SUM");
    if (!r) r = consumeToken(b, "TAN");
    if (!r) r = consumeToken(b, "TITLE");
    if (!r) r = consumeToken(b, "TO_ARRAY");
    if (!r) r = consumeToken(b, "TO_ATOM");
    if (!r) r = consumeToken(b, "TO_BOOL");
    if (!r) r = consumeToken(b, "TO_BOOLEAN");
    if (!r) r = consumeToken(b, "TO_NUM");
    if (!r) r = consumeToken(b, "TO_NUMBER");
    if (!r) r = consumeToken(b, "TO_OBJ");
    if (!r) r = consumeToken(b, "TO_OBJECT");
    if (!r) r = consumeToken(b, "TO_STR");
    if (!r) r = consumeToken(b, "TO_STRING");
    if (!r) r = consumeToken(b, "TOARRAY");
    if (!r) r = consumeToken(b, "TOATOM");
    if (!r) r = consumeToken(b, "TOBOOL");
    if (!r) r = consumeToken(b, "TOBOOLEAN");
    if (!r) r = consumeToken(b, "TONUM");
    if (!r) r = consumeToken(b, "TONUMBER");
    if (!r) r = consumeToken(b, "TOOBJ");
    if (!r) r = consumeToken(b, "TOOBJECT");
    if (!r) r = consumeToken(b, "TOSTR");
    if (!r) r = consumeToken(b, "TOSTRING");
    if (!r) r = consumeToken(b, "TRIM");
    if (!r) r = consumeToken(b, "TRUNC");
    if (!r) r = consumeToken(b, "TYPE");
    if (!r) r = consumeToken(b, "TYPENAME");
    if (!r) r = consumeToken(b, "UPPER");
    if (!r) r = consumeToken(b, "UUID");
    if (!r) r = consumeToken(b, "WEEKDAY_MILLIS");
    if (!r) r = consumeToken(b, "WEEKDAY_STR");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ( namespace ':' ( bucket '.' scope '.' )? )? identifier
  public static boolean function(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "function")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FUNCTION, "<function>");
    r = function_0(b, l + 1);
    r = r && identifier(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( namespace ':' ( bucket '.' scope '.' )? )?
  private static boolean function_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "function_0")) return false;
    function_0_0(b, l + 1);
    return true;
  }

  // namespace ':' ( bucket '.' scope '.' )?
  private static boolean function_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "function_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = namespace(b, l + 1);
    r = r && consumeToken(b, ":");
    r = r && function_0_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( bucket '.' scope '.' )?
  private static boolean function_0_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "function_0_0_2")) return false;
    function_0_0_2_0(b, l + 1);
    return true;
  }

  // bucket '.' scope '.'
  private static boolean function_0_0_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "function_0_0_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = bucket(b, l + 1);
    r = r && consumeToken(b, ".");
    r = r && scope(b, l + 1);
    r = r && consumeToken(b, ".");
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ordinary-function |
  //                   aggregate-function |
  //                   window-function
  public static boolean function_call(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "function_call")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FUNCTION_CALL, "<function call>");
    r = ordinary_function(b, l + 1);
    if (!r) r = aggregate_function(b, l + 1);
    if (!r) r = window_function(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // identifier
  public static boolean function_name(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "function_name")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FUNCTION_NAME, "<function name>");
    r = identifier(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'GROUP' 'BY' group-term ( ',' group-term )*
  //                     letting-clause? having-clause? | letting-clause
  public static boolean group_by_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "group_by_clause")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, GROUP_BY_CLAUSE, "<group by clause>");
    r = group_by_clause_0(b, l + 1);
    if (!r) r = letting_clause(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // 'GROUP' 'BY' group-term ( ',' group-term )*
  //                     letting-clause? having-clause?
  private static boolean group_by_clause_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "group_by_clause_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "GROUP");
    r = r && consumeToken(b, "BY");
    r = r && group_term(b, l + 1);
    r = r && group_by_clause_0_3(b, l + 1);
    r = r && group_by_clause_0_4(b, l + 1);
    r = r && group_by_clause_0_5(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( ',' group-term )*
  private static boolean group_by_clause_0_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "group_by_clause_0_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!group_by_clause_0_3_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "group_by_clause_0_3", c)) break;
    }
    return true;
  }

  // ',' group-term
  private static boolean group_by_clause_0_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "group_by_clause_0_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ",");
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
  // expr ( ('AS')? alias )?
  public static boolean group_term(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "group_term")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, GROUP_TERM, "<group term>");
    r = expr(b, l + 1);
    r = r && group_term_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( ('AS')? alias )?
  private static boolean group_term_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "group_term_1")) return false;
    group_term_1_0(b, l + 1);
    return true;
  }

  // ('AS')? alias
  private static boolean group_term_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "group_term_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = group_term_1_0_0(b, l + 1);
    r = r && alias(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ('AS')?
  private static boolean group_term_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "group_term_1_0_0")) return false;
    group_term_1_0_0_0(b, l + 1);
    return true;
  }

  // ('AS')
  private static boolean group_term_1_0_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "group_term_1_0_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "AS");
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '"index"' ':' ( index-array | index-object )
  public static boolean gsi_hint_json(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "gsi_hint_json")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, GSI_HINT_JSON, "<gsi hint json>");
    r = consumeToken(b, "\"index\"");
    r = r && consumeToken(b, ":");
    r = r && gsi_hint_json_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
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
  // 'INDEX' '(' keyspace  ')'
  public static boolean gsi_hint_simple(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "gsi_hint_simple")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, GSI_HINT_SIMPLE, "<gsi hint simple>");
    r = consumeToken(b, "INDEX");
    r = r && consumeToken(b, "(");
    r = r && keyspace(b, l + 1);
    r = r && consumeToken(b, ")");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '[' hash-object ( ',' hash-object )* ']'
  public static boolean hash_array(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "hash_array")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, HASH_ARRAY, "<hash array>");
    r = consumeToken(b, "[");
    r = r && hash_object(b, l + 1);
    r = r && hash_array_2(b, l + 1);
    r = r && consumeToken(b, "]");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( ',' hash-object )*
  private static boolean hash_array_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "hash_array_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!hash_array_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "hash_array_2", c)) break;
    }
    return true;
  }

  // ',' hash-object
  private static boolean hash_array_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "hash_array_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ",");
    r = r && hash_object(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '"use_hash"' ':' ( hash-array | hash-object )
  public static boolean hash_hint_json(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "hash_hint_json")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, HASH_HINT_JSON, "<hash hint json>");
    r = consumeToken(b, "\"use_hash\"");
    r = r && consumeToken(b, ":");
    r = r && hash_hint_json_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // hash-array | hash-object
  private static boolean hash_hint_json_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "hash_hint_json_2")) return false;
    boolean r;
    r = hash_array(b, l + 1);
    if (!r) r = hash_object(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // 'USE_HASH' '(' ( keyspace ( '/' ( 'BUILD' | 'PROBE' ) )? )+ ')'
  public static boolean hash_hint_simple(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "hash_hint_simple")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, HASH_HINT_SIMPLE, "<hash hint simple>");
    r = consumeToken(b, "USE_HASH");
    r = r && consumeToken(b, "(");
    r = r && hash_hint_simple_2(b, l + 1);
    r = r && consumeToken(b, ")");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( keyspace ( '/' ( 'BUILD' | 'PROBE' ) )? )+
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

  // keyspace ( '/' ( 'BUILD' | 'PROBE' ) )?
  private static boolean hash_hint_simple_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "hash_hint_simple_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = keyspace(b, l + 1);
    r = r && hash_hint_simple_2_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( '/' ( 'BUILD' | 'PROBE' ) )?
  private static boolean hash_hint_simple_2_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "hash_hint_simple_2_0_1")) return false;
    hash_hint_simple_2_0_1_0(b, l + 1);
    return true;
  }

  // '/' ( 'BUILD' | 'PROBE' )
  private static boolean hash_hint_simple_2_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "hash_hint_simple_2_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "/");
    r = r && hash_hint_simple_2_0_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // 'BUILD' | 'PROBE'
  private static boolean hash_hint_simple_2_0_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "hash_hint_simple_2_0_1_0_1")) return false;
    boolean r;
    r = consumeToken(b, "BUILD");
    if (!r) r = consumeToken(b, "PROBE");
    return r;
  }

  /* ********************************************************** */
  // '{' keyspace-property ( "," option-property )? '}'
  public static boolean hash_object(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "hash_object")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, HASH_OBJECT, "<hash object>");
    r = consumeToken(b, "{");
    r = r && keyspace_property(b, l + 1);
    r = r && hash_object_2(b, l + 1);
    r = r && consumeToken(b, "}");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( "," option-property )?
  private static boolean hash_object_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "hash_object_2")) return false;
    hash_object_2_0(b, l + 1);
    return true;
  }

  // "," option-property
  private static boolean hash_object_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "hash_object_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ",");
    r = r && option_property(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // 'HAVING' cond
  public static boolean having_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "having_clause")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, HAVING_CLAUSE, "<having clause>");
    r = consumeToken(b, "HAVING");
    r = r && cond(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // [0-9a-fA-F]
  public static boolean hex(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "hex")) return false;
    Marker m = enter_section_(b, l, _NONE_, HEX, "<hex>");
    consumeToken(b, "0-9a-fA-F");
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // block-hint-comment | line-hint-comment
  public static boolean hint_comment(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "hint_comment")) return false;
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
  // unescaped-identifier | escaped-identifier
  public static boolean identifier(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "identifier")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, IDENTIFIER, "<identifier>");
    r = unescaped_identifier(b, l + 1);
    if (!r) r = escaped_identifier(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // search-expr 'NOT'? 'IN' target-expr
  public static boolean in_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "in_expr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, IN_EXPR, "<in expr>");
    r = search_expr(b, l + 1);
    r = r && in_expr_1(b, l + 1);
    r = r && consumeToken(b, "IN");
    r = r && target_expr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // 'NOT'?
  private static boolean in_expr_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "in_expr_1")) return false;
    consumeToken(b, "NOT");
    return true;
  }

  /* ********************************************************** */
  // 'INCLUDE' 'MISSING'
  public static boolean include_missing(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "include_missing")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, INCLUDE_MISSING, "<include missing>");
    r = consumeToken(b, "INCLUDE");
    r = r && consumeToken(b, "MISSING");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '[' index-object ( ',' index-object )* ']'
  public static boolean index_array(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_array")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, INDEX_ARRAY, "<index array>");
    r = consumeToken(b, "[");
    r = r && index_object(b, l + 1);
    r = r && index_array_2(b, l + 1);
    r = r && consumeToken(b, "]");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( ',' index-object )*
  private static boolean index_array_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_array_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!index_array_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "index_array_2", c)) break;
    }
    return true;
  }

  // ',' index-object
  private static boolean index_array_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_array_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ",");
    r = r && index_object(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // 'INDEX' ( index-path '.' index-name | index-name 'ON' keyspace-ref )
  public static boolean index_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_clause")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, INDEX_CLAUSE, "<index clause>");
    r = consumeToken(b, "INDEX");
    r = r && index_clause_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // index-path '.' index-name | index-name 'ON' keyspace-ref
  private static boolean index_clause_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_clause_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = index_clause_1_0(b, l + 1);
    if (!r) r = index_clause_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // index-path '.' index-name
  private static boolean index_clause_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_clause_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = index_path(b, l + 1);
    r = r && consumeToken(b, ".");
    r = r && index_name(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // index-name 'ON' keyspace-ref
  private static boolean index_clause_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_clause_1_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = index_name(b, l + 1);
    r = r && consumeToken(b, "ON");
    r = r && keyspace_ref(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // index-join-type? 'JOIN' index-join-rhs index-join-predicate
  public static boolean index_join_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_join_clause")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, INDEX_JOIN_CLAUSE, "<index join clause>");
    r = index_join_clause_0(b, l + 1);
    r = r && consumeToken(b, "JOIN");
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
  // 'ON' 'PRIMARY'? 'KEY' expr 'FOR' alias
  public static boolean index_join_predicate(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_join_predicate")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, INDEX_JOIN_PREDICATE, "<index join predicate>");
    r = consumeToken(b, "ON");
    r = r && index_join_predicate_1(b, l + 1);
    r = r && consumeToken(b, "KEY");
    r = r && expr(b, l + 1);
    r = r && consumeToken(b, "FOR");
    r = r && alias(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // 'PRIMARY'?
  private static boolean index_join_predicate_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_join_predicate_1")) return false;
    consumeToken(b, "PRIMARY");
    return true;
  }

  /* ********************************************************** */
  // keyspace-ref ( 'AS'? alias )?
  public static boolean index_join_rhs(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_join_rhs")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, INDEX_JOIN_RHS, "<index join rhs>");
    r = keyspace_ref(b, l + 1);
    r = r && index_join_rhs_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( 'AS'? alias )?
  private static boolean index_join_rhs_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_join_rhs_1")) return false;
    index_join_rhs_1_0(b, l + 1);
    return true;
  }

  // 'AS'? alias
  private static boolean index_join_rhs_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_join_rhs_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = index_join_rhs_1_0_0(b, l + 1);
    r = r && alias(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // 'AS'?
  private static boolean index_join_rhs_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_join_rhs_1_0_0")) return false;
    consumeToken(b, "AS");
    return true;
  }

  /* ********************************************************** */
  // 'INNER' | ( 'LEFT' 'OUTER'? )
  public static boolean index_join_type(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_join_type")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, INDEX_JOIN_TYPE, "<index join type>");
    r = consumeToken(b, "INNER");
    if (!r) r = index_join_type_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // 'LEFT' 'OUTER'?
  private static boolean index_join_type_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_join_type_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "LEFT");
    r = r && index_join_type_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // 'OUTER'?
  private static boolean index_join_type_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_join_type_1_1")) return false;
    consumeToken(b, "OUTER");
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
  // identifier
  public static boolean index_name(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_name")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, INDEX_NAME, "<index name>");
    r = identifier(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // index-nest-type? 'NEST' index-nest-rhs index-nest-predicate
  public static boolean index_nest_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_nest_clause")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, INDEX_NEST_CLAUSE, "<index nest clause>");
    r = index_nest_clause_0(b, l + 1);
    r = r && consumeToken(b, "NEST");
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
  // 'ON' 'KEY' expr 'FOR' alias
  public static boolean index_nest_predicate(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_nest_predicate")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, INDEX_NEST_PREDICATE, "<index nest predicate>");
    r = consumeToken(b, "ON");
    r = r && consumeToken(b, "KEY");
    r = r && expr(b, l + 1);
    r = r && consumeToken(b, "FOR");
    r = r && alias(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // keyspace-ref ( 'AS'? alias )?
  public static boolean index_nest_rhs(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_nest_rhs")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, INDEX_NEST_RHS, "<index nest rhs>");
    r = keyspace_ref(b, l + 1);
    r = r && index_nest_rhs_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( 'AS'? alias )?
  private static boolean index_nest_rhs_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_nest_rhs_1")) return false;
    index_nest_rhs_1_0(b, l + 1);
    return true;
  }

  // 'AS'? alias
  private static boolean index_nest_rhs_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_nest_rhs_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = index_nest_rhs_1_0_0(b, l + 1);
    r = r && alias(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // 'AS'?
  private static boolean index_nest_rhs_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_nest_rhs_1_0_0")) return false;
    consumeToken(b, "AS");
    return true;
  }

  /* ********************************************************** */
  // 'INNER' | ( 'LEFT' 'OUTER'? )
  public static boolean index_nest_type(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_nest_type")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, INDEX_NEST_TYPE, "<index nest type>");
    r = consumeToken(b, "INNER");
    if (!r) r = index_nest_type_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // 'LEFT' 'OUTER'?
  private static boolean index_nest_type_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_nest_type_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "LEFT");
    r = r && index_nest_type_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // 'OUTER'?
  private static boolean index_nest_type_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_nest_type_1_1")) return false;
    consumeToken(b, "OUTER");
    return true;
  }

  /* ********************************************************** */
  // '{' keyspace-property ',' indexes-property '}'
  public static boolean index_object(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_object")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, INDEX_OBJECT, "<index object>");
    r = consumeToken(b, "{");
    r = r && keyspace_property(b, l + 1);
    r = r && consumeToken(b, ",");
    r = r && indexes_property(b, l + 1);
    r = r && consumeToken(b, "}");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'ASC' | 'DESC'
  public static boolean index_order(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_order")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, INDEX_ORDER, "<index order>");
    r = consumeToken(b, "ASC");
    if (!r) r = consumeToken(b, "DESC");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'PARTITION' 'BY' 'HASH' '(' partition-key-expr
  //                     ( ',' partition-key-expr )* ')'
  public static boolean index_partition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_partition")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, INDEX_PARTITION, "<index partition>");
    r = consumeToken(b, "PARTITION");
    r = r && consumeToken(b, "BY");
    r = r && consumeToken(b, "HASH");
    r = r && consumeToken(b, "(");
    r = r && partition_key_expr(b, l + 1);
    r = r && index_partition_5(b, l + 1);
    r = r && consumeToken(b, ")");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( ',' partition-key-expr )*
  private static boolean index_partition_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_partition_5")) return false;
    while (true) {
      int c = current_position_(b);
      if (!index_partition_5_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "index_partition_5", c)) break;
    }
    return true;
  }

  // ',' partition-key-expr
  private static boolean index_partition_5_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_partition_5_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ",");
    r = r && partition_key_expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // keyspace-full | keyspace-prefix | keyspace-partial
  public static boolean index_path(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_path")) return false;
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
  // 'USING' ( 'GSI' | 'FTS' )
  public static boolean index_type(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_type")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, INDEX_TYPE, "<index type>");
    r = consumeToken(b, "USING");
    r = r && index_type_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // 'GSI' | 'FTS'
  private static boolean index_type_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_type_1")) return false;
    boolean r;
    r = consumeToken(b, "GSI");
    if (!r) r = consumeToken(b, "FTS");
    return r;
  }

  /* ********************************************************** */
  // 'USING' 'GSI'
  public static boolean index_using(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_using")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, INDEX_USING, "<index using>");
    r = consumeToken(b, "USING");
    r = r && consumeToken(b, "GSI");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'WITH' expr
  public static boolean index_with(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "index_with")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, INDEX_WITH, "<index with>");
    r = consumeToken(b, "WITH");
    r = r && expr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'INDEX' ( '(' ( index-name ( ',' index-name )* | subquery-expr ) ')' |
  //                              'ALL' )
  public static boolean indexes_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "indexes_clause")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, INDEXES_CLAUSE, "<indexes clause>");
    r = consumeToken(b, "INDEX");
    r = r && indexes_clause_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '(' ( index-name ( ',' index-name )* | subquery-expr ) ')' |
  //                              'ALL'
  private static boolean indexes_clause_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "indexes_clause_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = indexes_clause_1_0(b, l + 1);
    if (!r) r = consumeToken(b, "ALL");
    exit_section_(b, m, null, r);
    return r;
  }

  // '(' ( index-name ( ',' index-name )* | subquery-expr ) ')'
  private static boolean indexes_clause_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "indexes_clause_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "(");
    r = r && indexes_clause_1_0_1(b, l + 1);
    r = r && consumeToken(b, ")");
    exit_section_(b, m, null, r);
    return r;
  }

  // index-name ( ',' index-name )* | subquery-expr
  private static boolean indexes_clause_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "indexes_clause_1_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = indexes_clause_1_0_1_0(b, l + 1);
    if (!r) r = subquery_expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // index-name ( ',' index-name )*
  private static boolean indexes_clause_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "indexes_clause_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = index_name(b, l + 1);
    r = r && indexes_clause_1_0_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( ',' index-name )*
  private static boolean indexes_clause_1_0_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "indexes_clause_1_0_1_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!indexes_clause_1_0_1_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "indexes_clause_1_0_1_0_1", c)) break;
    }
    return true;
  }

  // ',' index-name
  private static boolean indexes_clause_1_0_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "indexes_clause_1_0_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ",");
    r = r && index_name(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '"indexes"' ':' ( 'null'
  //                                      | '"' index '"'
  //                                      | '[' '"' index '"' ( ',' '"' index '"' )* ']' )
  public static boolean indexes_property(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "indexes_property")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, INDEXES_PROPERTY, "<indexes property>");
    r = consumeToken(b, "\"indexes\"");
    r = r && consumeToken(b, ":");
    r = r && indexes_property_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // 'null'
  //                                      | '"' index '"'
  //                                      | '[' '"' index '"' ( ',' '"' index '"' )* ']'
  private static boolean indexes_property_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "indexes_property_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "null");
    if (!r) r = indexes_property_2_1(b, l + 1);
    if (!r) r = indexes_property_2_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // '"' index '"'
  private static boolean indexes_property_2_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "indexes_property_2_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "\"");
    r = r && consumeToken(b, INDEX);
    r = r && consumeToken(b, "\"");
    exit_section_(b, m, null, r);
    return r;
  }

  // '[' '"' index '"' ( ',' '"' index '"' )* ']'
  private static boolean indexes_property_2_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "indexes_property_2_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "[");
    r = r && consumeToken(b, "\"");
    r = r && consumeToken(b, INDEX);
    r = r && consumeToken(b, "\"");
    r = r && indexes_property_2_2_4(b, l + 1);
    r = r && consumeToken(b, "]");
    exit_section_(b, m, null, r);
    return r;
  }

  // ( ',' '"' index '"' )*
  private static boolean indexes_property_2_2_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "indexes_property_2_2_4")) return false;
    while (true) {
      int c = current_position_(b);
      if (!indexes_property_2_2_4_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "indexes_property_2_2_4", c)) break;
    }
    return true;
  }

  // ',' '"' index '"'
  private static boolean indexes_property_2_2_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "indexes_property_2_2_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ",");
    r = r && consumeToken(b, "\"");
    r = r && consumeToken(b, INDEX);
    r = r && consumeToken(b, "\"");
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // 'INFER' ( 'COLLECTION' | 'KEYSPACE' )? keyspace-ref ( 'WITH' options )?
  public static boolean infer(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "infer")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, INFER, "<infer>");
    r = consumeToken(b, "INFER");
    r = r && infer_1(b, l + 1);
    r = r && keyspace_ref(b, l + 1);
    r = r && infer_3(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( 'COLLECTION' | 'KEYSPACE' )?
  private static boolean infer_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "infer_1")) return false;
    infer_1_0(b, l + 1);
    return true;
  }

  // 'COLLECTION' | 'KEYSPACE'
  private static boolean infer_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "infer_1_0")) return false;
    boolean r;
    r = consumeToken(b, "COLLECTION");
    if (!r) r = consumeToken(b, "KEYSPACE");
    return r;
  }

  // ( 'WITH' options )?
  private static boolean infer_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "infer_3")) return false;
    infer_3_0(b, l + 1);
    return true;
  }

  // 'WITH' options
  private static boolean infer_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "infer_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "WITH");
    r = r && consumeToken(b, OPTIONS);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // 'INSERT' 'INTO' target-keyspace ( insert-values | insert-select )
  //             returning-clause?
  public static boolean insert(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "insert")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, INSERT, "<insert>");
    r = consumeToken(b, "INSERT");
    r = r && consumeToken(b, "INTO");
    r = r && target_keyspace(b, l + 1);
    r = r && insert_3(b, l + 1);
    r = r && insert_4(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // insert-values | insert-select
  private static boolean insert_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "insert_3")) return false;
    boolean r;
    r = insert_values(b, l + 1);
    if (!r) r = insert_select(b, l + 1);
    return r;
  }

  // returning-clause?
  private static boolean insert_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "insert_4")) return false;
    returning_clause(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // '(' 'PRIMARY'? 'KEY' key ( ',' 'VALUE' value )?
  //                    ( ',' 'OPTIONS' options )? ')' select
  public static boolean insert_select(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "insert_select")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, INSERT_SELECT, "<insert select>");
    r = consumeToken(b, "(");
    r = r && insert_select_1(b, l + 1);
    r = r && consumeToken(b, "KEY");
    r = r && key(b, l + 1);
    r = r && insert_select_4(b, l + 1);
    r = r && insert_select_5(b, l + 1);
    r = r && consumeToken(b, ")");
    r = r && select(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // 'PRIMARY'?
  private static boolean insert_select_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "insert_select_1")) return false;
    consumeToken(b, "PRIMARY");
    return true;
  }

  // ( ',' 'VALUE' value )?
  private static boolean insert_select_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "insert_select_4")) return false;
    insert_select_4_0(b, l + 1);
    return true;
  }

  // ',' 'VALUE' value
  private static boolean insert_select_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "insert_select_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ",");
    r = r && consumeToken(b, "VALUE");
    r = r && value(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( ',' 'OPTIONS' options )?
  private static boolean insert_select_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "insert_select_5")) return false;
    insert_select_5_0(b, l + 1);
    return true;
  }

  // ',' 'OPTIONS' options
  private static boolean insert_select_5_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "insert_select_5_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ",");
    r = r && consumeToken(b, "OPTIONS");
    r = r && consumeToken(b, OPTIONS);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ( '(' 'PRIMARY'? 'KEY' ',' 'VALUE' ( ',' 'OPTIONS' )? ')' )? values-clause
  public static boolean insert_values(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "insert_values")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, INSERT_VALUES, "<insert values>");
    r = insert_values_0(b, l + 1);
    r = r && values_clause(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( '(' 'PRIMARY'? 'KEY' ',' 'VALUE' ( ',' 'OPTIONS' )? ')' )?
  private static boolean insert_values_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "insert_values_0")) return false;
    insert_values_0_0(b, l + 1);
    return true;
  }

  // '(' 'PRIMARY'? 'KEY' ',' 'VALUE' ( ',' 'OPTIONS' )? ')'
  private static boolean insert_values_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "insert_values_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "(");
    r = r && insert_values_0_0_1(b, l + 1);
    r = r && consumeToken(b, "KEY");
    r = r && consumeToken(b, ",");
    r = r && consumeToken(b, "VALUE");
    r = r && insert_values_0_0_5(b, l + 1);
    r = r && consumeToken(b, ")");
    exit_section_(b, m, null, r);
    return r;
  }

  // 'PRIMARY'?
  private static boolean insert_values_0_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "insert_values_0_0_1")) return false;
    consumeToken(b, "PRIMARY");
    return true;
  }

  // ( ',' 'OPTIONS' )?
  private static boolean insert_values_0_0_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "insert_values_0_0_5")) return false;
    insert_values_0_0_5_0(b, l + 1);
    return true;
  }

  // ',' 'OPTIONS'
  private static boolean insert_values_0_0_5_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "insert_values_0_0_5_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ",");
    r = r && consumeToken(b, "OPTIONS");
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // [0-9] | [1-9] [0-9]+
  public static boolean intgr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "intgr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, INTGR, "<intgr>");
    r = intgr_0(b, l + 1);
    if (!r) r = intgr_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // [0-9]
  private static boolean intgr_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "intgr_0")) return false;
    consumeToken(b, "0-9");
    return true;
  }

  // [1-9] [0-9]+
  private static boolean intgr_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "intgr_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = intgr_1_0(b, l + 1);
    r = r && intgr_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // [1-9]
  private static boolean intgr_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "intgr_1_0")) return false;
    consumeToken(b, "1-9");
    return true;
  }

  // [0-9]
  private static boolean intgr_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "intgr_1_1")) return false;
    consumeToken(b, "0-9");
    return true;
  }

  /* ********************************************************** */
  // expr 'IS' 'NOT'? 'NULL' |
  //             expr 'IS' 'NOT'? 'MISSING' |
  //             expr 'IS' 'NOT'? 'VALUED'
  public static boolean is_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "is_expr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, IS_EXPR, "<is expr>");
    r = is_expr_0(b, l + 1);
    if (!r) r = is_expr_1(b, l + 1);
    if (!r) r = is_expr_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // expr 'IS' 'NOT'? 'NULL'
  private static boolean is_expr_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "is_expr_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = expr(b, l + 1);
    r = r && consumeToken(b, "IS");
    r = r && is_expr_0_2(b, l + 1);
    r = r && consumeToken(b, "NULL");
    exit_section_(b, m, null, r);
    return r;
  }

  // 'NOT'?
  private static boolean is_expr_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "is_expr_0_2")) return false;
    consumeToken(b, "NOT");
    return true;
  }

  // expr 'IS' 'NOT'? 'MISSING'
  private static boolean is_expr_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "is_expr_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = expr(b, l + 1);
    r = r && consumeToken(b, "IS");
    r = r && is_expr_1_2(b, l + 1);
    r = r && consumeToken(b, "MISSING");
    exit_section_(b, m, null, r);
    return r;
  }

  // 'NOT'?
  private static boolean is_expr_1_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "is_expr_1_2")) return false;
    consumeToken(b, "NOT");
    return true;
  }

  // expr 'IS' 'NOT'? 'VALUED'
  private static boolean is_expr_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "is_expr_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = expr(b, l + 1);
    r = r && consumeToken(b, "IS");
    r = r && is_expr_2_2(b, l + 1);
    r = r && consumeToken(b, "VALUED");
    exit_section_(b, m, null, r);
    return r;
  }

  // 'NOT'?
  private static boolean is_expr_2_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "is_expr_2_2")) return false;
    consumeToken(b, "NOT");
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
  // '{' json-hint (',' json-hint )* '}'
  public static boolean json_hint_object(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "json_hint_object")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, JSON_HINT_OBJECT, "<json hint object>");
    r = consumeToken(b, "{");
    r = r && json_hint(b, l + 1);
    r = r && json_hint_object_2(b, l + 1);
    r = r && consumeToken(b, "}");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (',' json-hint )*
  private static boolean json_hint_object_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "json_hint_object_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!json_hint_object_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "json_hint_object_2", c)) break;
    }
    return true;
  }

  // ',' json-hint
  private static boolean json_hint_object_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "json_hint_object_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ",");
    r = r && json_hint(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // expr
  public static boolean key(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "key")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, KEY, "<key>");
    r = expr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // index-order
  public static boolean key_attribs(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "key_attribs")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, KEY_ATTRIBS, "<key attribs>");
    r = index_order(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'KEYSPACE' <identifier>
  public static boolean keyspace(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keyspace")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, KEYSPACE, "<keyspace>");
    r = consumeToken(b, "KEYSPACE");
    r = r && identifier(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '[' keyspace-object ( ',' keyspace-object )* ']'
  public static boolean keyspace_array(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keyspace_array")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, KEYSPACE_ARRAY, "<keyspace array>");
    r = consumeToken(b, "[");
    r = r && keyspace_object(b, l + 1);
    r = r && keyspace_array_2(b, l + 1);
    r = r && consumeToken(b, "]");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( ',' keyspace-object )*
  private static boolean keyspace_array_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keyspace_array_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!keyspace_array_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "keyspace_array_2", c)) break;
    }
    return true;
  }

  // ',' keyspace-object
  private static boolean keyspace_array_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keyspace_array_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ",");
    r = r && keyspace_object(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // namespace ':' bucket '.' scope '.' collection
  public static boolean keyspace_full(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keyspace_full")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, KEYSPACE_FULL, "<keyspace full>");
    r = namespace(b, l + 1);
    r = r && consumeToken(b, ":");
    r = r && bucket(b, l + 1);
    r = r && consumeToken(b, ".");
    r = r && scope(b, l + 1);
    r = r && consumeToken(b, ".");
    r = r && collection(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '{' keyspace-property '}'
  public static boolean keyspace_object(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keyspace_object")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, KEYSPACE_OBJECT, "<keyspace object>");
    r = consumeToken(b, "{");
    r = r && keyspace_property(b, l + 1);
    r = r && consumeToken(b, "}");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // collection
  public static boolean keyspace_partial(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keyspace_partial")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, KEYSPACE_PARTIAL, "<keyspace partial>");
    r = collection(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ( namespace ':' )? bucket ( '.' scope '.' collection )?
  public static boolean keyspace_path(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keyspace_path")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, KEYSPACE_PATH, "<keyspace path>");
    r = keyspace_path_0(b, l + 1);
    r = r && bucket(b, l + 1);
    r = r && keyspace_path_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( namespace ':' )?
  private static boolean keyspace_path_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keyspace_path_0")) return false;
    keyspace_path_0_0(b, l + 1);
    return true;
  }

  // namespace ':'
  private static boolean keyspace_path_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keyspace_path_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = namespace(b, l + 1);
    r = r && consumeToken(b, ":");
    exit_section_(b, m, null, r);
    return r;
  }

  // ( '.' scope '.' collection )?
  private static boolean keyspace_path_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keyspace_path_2")) return false;
    keyspace_path_2_0(b, l + 1);
    return true;
  }

  // '.' scope '.' collection
  private static boolean keyspace_path_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keyspace_path_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ".");
    r = r && scope(b, l + 1);
    r = r && consumeToken(b, ".");
    r = r && collection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ( namespace ':' )? bucket
  public static boolean keyspace_prefix(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keyspace_prefix")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, KEYSPACE_PREFIX, "<keyspace prefix>");
    r = keyspace_prefix_0(b, l + 1);
    r = r && bucket(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( namespace ':' )?
  private static boolean keyspace_prefix_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keyspace_prefix_0")) return false;
    keyspace_prefix_0_0(b, l + 1);
    return true;
  }

  // namespace ':'
  private static boolean keyspace_prefix_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keyspace_prefix_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = namespace(b, l + 1);
    r = r && consumeToken(b, ":");
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ( '"keyspace"' | '"alias"' ) ':' '"' keyspace '"'
  public static boolean keyspace_property(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keyspace_property")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, KEYSPACE_PROPERTY, "<keyspace property>");
    r = keyspace_property_0(b, l + 1);
    r = r && consumeToken(b, ":");
    r = r && consumeToken(b, "\"");
    r = r && keyspace(b, l + 1);
    r = r && consumeToken(b, "\"");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '"keyspace"' | '"alias"'
  private static boolean keyspace_property_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keyspace_property_0")) return false;
    boolean r;
    r = consumeToken(b, "\"keyspace\"");
    if (!r) r = consumeToken(b, "\"alias\"");
    return r;
  }

  /* ********************************************************** */
  // keyspace-path | keyspace-partial
  public static boolean keyspace_ref(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keyspace_ref")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, KEYSPACE_REF, "<keyspace ref>");
    r = keyspace_path(b, l + 1);
    if (!r) r = keyspace_partial(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'ADAPTER'| 'ALL'| 'ALTER'| 'ANALYTICS'| 'AND'| 'ANY'| 'APPLY'| 'ARGS'| 'AS'| 'ASC'| 'AT'| 'AUTOGENERATED'| 'BETWEEN'| 'BTREE'| 'BY'| 'CASE'| 'CAST'| 'CLOSED'| 'COLLECTION'| 'COMPACT'| 'COMPACTION'| 'CONNECT'| 'CONNECTED'| 'CORRELATE'| 'CREATE'| 'CROSS'| 'CUBE'| 'CURRENT'| 'DATASET'| 'DATAVERSE'| 'DECLARE'| 'DEFINITION'| 'DELETE'| 'DESC'| 'DISABLE'| 'DISCONNECT'| 'DISTINCT'| 'DIV'| 'DROP'| 'ELEMENT'| 'ELSE'| 'ENABLE'| 'END'| 'ENFORCED'| 'EVERY'| 'EXCEPT'| 'EXCLUDE'| 'EXISTS'| 'EXPLAIN'| 'EXTERNAL'| 'FEED'| 'FILTER'| 'FIRST'| 'FLATTEN'| 'FOLLOWING'| 'FOR'| 'FOREIGN'| 'FROM'| 'FULL'| 'FULLTEXT'| 'FUNCTION'| 'GROUP'| 'GROUPING'| 'GROUPS'| 'HAVING'| 'HINTS'| 'IF'| 'IGNORE'| 'IN'| 'INCLUDE'| 'INDEX'| 'INGESTION'| 'INNER'| 'INSERT'| 'INTERNAL'| 'INTERSECT'| 'INTO'| 'IS'| 'JOIN'| 'KEY'| 'KEYWORD'| 'KNOWN'| 'LAST'| 'LEFT'| 'LET'| 'LETTING'| 'LIKE'| 'LIMIT'| 'LINK'| 'LOAD'| 'MISSING'| 'MOD'| 'NGRAM'| 'NO'| 'NODEGROUP'| 'NOT'| 'NULL'| 'NULLS'| 'OFFSET'| 'ON'| 'OPEN'| 'OR'| 'ORDER'| 'OTHERS'| 'OUTER'| 'OUTPUT'| 'OVER'| 'PARTITION'| 'PATH'| 'POLICY'| 'PRECEDING'| 'PRIMARY'| 'RANGE'| 'RAW'| 'REFERENCES'| 'REFRESH'| 'REPLACE'| 'RESPECT'| 'RETURN'| 'RETURNING'| 'RETURNS'| 'RIGHT'| 'ROLLUP'| 'ROW'| 'ROWS'| 'RTREE'| 'RUN'| 'SATISFIES'| 'SCOPE'| 'SECONDARY'| 'SELECT'| 'SET'| 'SETS'| 'SOME'| 'START'| 'STOP'| 'SYNONYM'| 'TEMPORARY'| 'THEN'| 'TIES'| 'TO'| 'TYPE'| 'UNBOUNDED'| 'UNION ALL'| 'UNION'| 'UNKNOWN'| 'UNNEST'| 'UPDATE'| 'UPSERT'| 'USE'| 'USING'| 'VALUE'| 'VALUED'| 'VIEW'| 'WHEN'| 'WHERE'| 'WITH'| 'WRITE '
  public static boolean kwd(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "kwd")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, KWD, "<kwd>");
    r = consumeToken(b, "ADAPTER");
    if (!r) r = consumeToken(b, "ALL");
    if (!r) r = consumeToken(b, "ALTER");
    if (!r) r = consumeToken(b, "ANALYTICS");
    if (!r) r = consumeToken(b, "AND");
    if (!r) r = consumeToken(b, "ANY");
    if (!r) r = consumeToken(b, "APPLY");
    if (!r) r = consumeToken(b, "ARGS");
    if (!r) r = consumeToken(b, "AS");
    if (!r) r = consumeToken(b, "ASC");
    if (!r) r = consumeToken(b, "AT");
    if (!r) r = consumeToken(b, "AUTOGENERATED");
    if (!r) r = consumeToken(b, "BETWEEN");
    if (!r) r = consumeToken(b, "BTREE");
    if (!r) r = consumeToken(b, "BY");
    if (!r) r = consumeToken(b, "CASE");
    if (!r) r = consumeToken(b, "CAST");
    if (!r) r = consumeToken(b, "CLOSED");
    if (!r) r = consumeToken(b, "COLLECTION");
    if (!r) r = consumeToken(b, "COMPACT");
    if (!r) r = consumeToken(b, "COMPACTION");
    if (!r) r = consumeToken(b, "CONNECT");
    if (!r) r = consumeToken(b, "CONNECTED");
    if (!r) r = consumeToken(b, "CORRELATE");
    if (!r) r = consumeToken(b, "CREATE");
    if (!r) r = consumeToken(b, "CROSS");
    if (!r) r = consumeToken(b, "CUBE");
    if (!r) r = consumeToken(b, "CURRENT");
    if (!r) r = consumeToken(b, "DATASET");
    if (!r) r = consumeToken(b, "DATAVERSE");
    if (!r) r = consumeToken(b, "DECLARE");
    if (!r) r = consumeToken(b, "DEFINITION");
    if (!r) r = consumeToken(b, "DELETE");
    if (!r) r = consumeToken(b, "DESC");
    if (!r) r = consumeToken(b, "DISABLE");
    if (!r) r = consumeToken(b, "DISCONNECT");
    if (!r) r = consumeToken(b, "DISTINCT");
    if (!r) r = consumeToken(b, "DIV");
    if (!r) r = consumeToken(b, "DROP");
    if (!r) r = consumeToken(b, "ELEMENT");
    if (!r) r = consumeToken(b, "ELSE");
    if (!r) r = consumeToken(b, "ENABLE");
    if (!r) r = consumeToken(b, "END");
    if (!r) r = consumeToken(b, "ENFORCED");
    if (!r) r = consumeToken(b, "EVERY");
    if (!r) r = consumeToken(b, "EXCEPT");
    if (!r) r = consumeToken(b, "EXCLUDE");
    if (!r) r = consumeToken(b, "EXISTS");
    if (!r) r = consumeToken(b, "EXPLAIN");
    if (!r) r = consumeToken(b, "EXTERNAL");
    if (!r) r = consumeToken(b, "FEED");
    if (!r) r = consumeToken(b, "FILTER");
    if (!r) r = consumeToken(b, "FIRST");
    if (!r) r = consumeToken(b, "FLATTEN");
    if (!r) r = consumeToken(b, "FOLLOWING");
    if (!r) r = consumeToken(b, "FOR");
    if (!r) r = consumeToken(b, "FOREIGN");
    if (!r) r = consumeToken(b, "FROM");
    if (!r) r = consumeToken(b, "FULL");
    if (!r) r = consumeToken(b, "FULLTEXT");
    if (!r) r = consumeToken(b, "FUNCTION");
    if (!r) r = consumeToken(b, "GROUP");
    if (!r) r = consumeToken(b, "GROUPING");
    if (!r) r = consumeToken(b, "GROUPS");
    if (!r) r = consumeToken(b, "HAVING");
    if (!r) r = consumeToken(b, "HINTS");
    if (!r) r = consumeToken(b, "IF");
    if (!r) r = consumeToken(b, "IGNORE");
    if (!r) r = consumeToken(b, "IN");
    if (!r) r = consumeToken(b, "INCLUDE");
    if (!r) r = consumeToken(b, "INDEX");
    if (!r) r = consumeToken(b, "INGESTION");
    if (!r) r = consumeToken(b, "INNER");
    if (!r) r = consumeToken(b, "INSERT");
    if (!r) r = consumeToken(b, "INTERNAL");
    if (!r) r = consumeToken(b, "INTERSECT");
    if (!r) r = consumeToken(b, "INTO");
    if (!r) r = consumeToken(b, "IS");
    if (!r) r = consumeToken(b, "JOIN");
    if (!r) r = consumeToken(b, "KEY");
    if (!r) r = consumeToken(b, "KEYWORD");
    if (!r) r = consumeToken(b, "KNOWN");
    if (!r) r = consumeToken(b, "LAST");
    if (!r) r = consumeToken(b, "LEFT");
    if (!r) r = consumeToken(b, "LET");
    if (!r) r = consumeToken(b, "LETTING");
    if (!r) r = consumeToken(b, "LIKE");
    if (!r) r = consumeToken(b, "LIMIT");
    if (!r) r = consumeToken(b, "LINK");
    if (!r) r = consumeToken(b, "LOAD");
    if (!r) r = consumeToken(b, "MISSING");
    if (!r) r = consumeToken(b, "MOD");
    if (!r) r = consumeToken(b, "NGRAM");
    if (!r) r = consumeToken(b, "NO");
    if (!r) r = consumeToken(b, "NODEGROUP");
    if (!r) r = consumeToken(b, "NOT");
    if (!r) r = consumeToken(b, "NULL");
    if (!r) r = consumeToken(b, "NULLS");
    if (!r) r = consumeToken(b, "OFFSET");
    if (!r) r = consumeToken(b, "ON");
    if (!r) r = consumeToken(b, "OPEN");
    if (!r) r = consumeToken(b, "OR");
    if (!r) r = consumeToken(b, "ORDER");
    if (!r) r = consumeToken(b, "OTHERS");
    if (!r) r = consumeToken(b, "OUTER");
    if (!r) r = consumeToken(b, "OUTPUT");
    if (!r) r = consumeToken(b, "OVER");
    if (!r) r = consumeToken(b, "PARTITION");
    if (!r) r = consumeToken(b, "PATH");
    if (!r) r = consumeToken(b, "POLICY");
    if (!r) r = consumeToken(b, "PRECEDING");
    if (!r) r = consumeToken(b, "PRIMARY");
    if (!r) r = consumeToken(b, "RANGE");
    if (!r) r = consumeToken(b, "RAW");
    if (!r) r = consumeToken(b, "REFERENCES");
    if (!r) r = consumeToken(b, "REFRESH");
    if (!r) r = consumeToken(b, "REPLACE");
    if (!r) r = consumeToken(b, "RESPECT");
    if (!r) r = consumeToken(b, "RETURN");
    if (!r) r = consumeToken(b, "RETURNING");
    if (!r) r = consumeToken(b, "RETURNS");
    if (!r) r = consumeToken(b, "RIGHT");
    if (!r) r = consumeToken(b, "ROLLUP");
    if (!r) r = consumeToken(b, "ROW");
    if (!r) r = consumeToken(b, "ROWS");
    if (!r) r = consumeToken(b, "RTREE");
    if (!r) r = consumeToken(b, "RUN");
    if (!r) r = consumeToken(b, "SATISFIES");
    if (!r) r = consumeToken(b, "SCOPE");
    if (!r) r = consumeToken(b, "SECONDARY");
    if (!r) r = consumeToken(b, "SELECT");
    if (!r) r = consumeToken(b, "SET");
    if (!r) r = consumeToken(b, "SETS");
    if (!r) r = consumeToken(b, "SOME");
    if (!r) r = consumeToken(b, "START");
    if (!r) r = consumeToken(b, "STOP");
    if (!r) r = consumeToken(b, "SYNONYM");
    if (!r) r = consumeToken(b, "TEMPORARY");
    if (!r) r = consumeToken(b, "THEN");
    if (!r) r = consumeToken(b, "TIES");
    if (!r) r = consumeToken(b, "TO");
    if (!r) r = consumeToken(b, "TYPE");
    if (!r) r = consumeToken(b, "UNBOUNDED");
    if (!r) r = consumeToken(b, "UNION ALL");
    if (!r) r = consumeToken(b, "UNION");
    if (!r) r = consumeToken(b, "UNKNOWN");
    if (!r) r = consumeToken(b, "UNNEST");
    if (!r) r = consumeToken(b, "UPDATE");
    if (!r) r = consumeToken(b, "UPSERT");
    if (!r) r = consumeToken(b, "USE");
    if (!r) r = consumeToken(b, "USING");
    if (!r) r = consumeToken(b, "VALUE");
    if (!r) r = consumeToken(b, "VALUED");
    if (!r) r = consumeToken(b, "VIEW");
    if (!r) r = consumeToken(b, "WHEN");
    if (!r) r = consumeToken(b, "WHERE");
    if (!r) r = consumeToken(b, "WITH");
    if (!r) r = consumeToken(b, "WRITE ");
    exit_section_(b, l, m, r, false, null);
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
  // 'LET' alias '=' expr ( ',' alias '=' expr )*
  public static boolean let_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "let_clause")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LET_CLAUSE, "<let clause>");
    r = consumeToken(b, "LET");
    r = r && alias(b, l + 1);
    r = r && consumeToken(b, "=");
    r = r && expr(b, l + 1);
    r = r && let_clause_4(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( ',' alias '=' expr )*
  private static boolean let_clause_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "let_clause_4")) return false;
    while (true) {
      int c = current_position_(b);
      if (!let_clause_4_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "let_clause_4", c)) break;
    }
    return true;
  }

  // ',' alias '=' expr
  private static boolean let_clause_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "let_clause_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ",");
    r = r && alias(b, l + 1);
    r = r && consumeToken(b, "=");
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // 'LETTING' alias '=' expr ( ',' alias '=' expr )*
  public static boolean letting_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "letting_clause")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LETTING_CLAUSE, "<letting clause>");
    r = consumeToken(b, "LETTING");
    r = r && alias(b, l + 1);
    r = r && consumeToken(b, "=");
    r = r && expr(b, l + 1);
    r = r && letting_clause_4(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( ',' alias '=' expr )*
  private static boolean letting_clause_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "letting_clause_4")) return false;
    while (true) {
      int c = current_position_(b);
      if (!letting_clause_4_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "letting_clause_4", c)) break;
    }
    return true;
  }

  // ',' alias '=' expr
  private static boolean letting_clause_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "letting_clause_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ",");
    r = r && alias(b, l + 1);
    r = r && consumeToken(b, "=");
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // expr 'NOT'? 'LIKE' expr
  public static boolean like_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "like_expr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LIKE_EXPR, "<like expr>");
    r = expr(b, l + 1);
    r = r && like_expr_1(b, l + 1);
    r = r && consumeToken(b, "LIKE");
    r = r && expr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // 'NOT'?
  private static boolean like_expr_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "like_expr_1")) return false;
    consumeToken(b, "NOT");
    return true;
  }

  /* ********************************************************** */
  // 'LIMIT' expr
  public static boolean limit_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "limit_clause")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LIMIT_CLAUSE, "<limit clause>");
    r = consumeToken(b, "LIMIT");
    r = r && expr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '--+' hints
  public static boolean line_hint_comment(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "line_hint_comment")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LINE_HINT_COMMENT, "<line hint comment>");
    r = consumeToken(b, "--+");
    r = r && hints(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // str | nbr | bool | null | missing
  public static boolean literal(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literal")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LITERAL, "<literal>");
    r = str(b, l + 1);
    if (!r) r = nbr(b, l + 1);
    if (!r) r = bool(b, l + 1);
    if (!r) r = null_$(b, l + 1);
    if (!r) r = missing(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // and | or | not
  public static boolean logical_term(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "logical_term")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LOGICAL_TERM, "<logical term>");
    r = and(b, l + 1);
    if (!r) r = or(b, l + 1);
    if (!r) r = not(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // lookup-join-type? 'JOIN' lookup-join-rhs lookup-join-predicate
  public static boolean lookup_join_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lookup_join_clause")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LOOKUP_JOIN_CLAUSE, "<lookup join clause>");
    r = lookup_join_clause_0(b, l + 1);
    r = r && consumeToken(b, "JOIN");
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
  // 'ON' 'PRIMARY'? 'KEYS' expr
  public static boolean lookup_join_predicate(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lookup_join_predicate")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LOOKUP_JOIN_PREDICATE, "<lookup join predicate>");
    r = consumeToken(b, "ON");
    r = r && lookup_join_predicate_1(b, l + 1);
    r = r && consumeToken(b, "KEYS");
    r = r && expr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // 'PRIMARY'?
  private static boolean lookup_join_predicate_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lookup_join_predicate_1")) return false;
    consumeToken(b, "PRIMARY");
    return true;
  }

  /* ********************************************************** */
  // keyspace-ref ( 'AS'? alias )?
  public static boolean lookup_join_rhs(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lookup_join_rhs")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LOOKUP_JOIN_RHS, "<lookup join rhs>");
    r = keyspace_ref(b, l + 1);
    r = r && lookup_join_rhs_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( 'AS'? alias )?
  private static boolean lookup_join_rhs_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lookup_join_rhs_1")) return false;
    lookup_join_rhs_1_0(b, l + 1);
    return true;
  }

  // 'AS'? alias
  private static boolean lookup_join_rhs_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lookup_join_rhs_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = lookup_join_rhs_1_0_0(b, l + 1);
    r = r && alias(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // 'AS'?
  private static boolean lookup_join_rhs_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lookup_join_rhs_1_0_0")) return false;
    consumeToken(b, "AS");
    return true;
  }

  /* ********************************************************** */
  // 'INNER' | ( 'LEFT' 'OUTER'? )
  public static boolean lookup_join_type(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lookup_join_type")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LOOKUP_JOIN_TYPE, "<lookup join type>");
    r = consumeToken(b, "INNER");
    if (!r) r = lookup_join_type_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // 'LEFT' 'OUTER'?
  private static boolean lookup_join_type_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lookup_join_type_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "LEFT");
    r = r && lookup_join_type_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // 'OUTER'?
  private static boolean lookup_join_type_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lookup_join_type_1_1")) return false;
    consumeToken(b, "OUTER");
    return true;
  }

  /* ********************************************************** */
  // target-keyspace 'USING' lookup-merge-source lookup-merge-predicate
  //                  lookup-merge-actions
  public static boolean lookup_merge(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lookup_merge")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LOOKUP_MERGE, "<lookup merge>");
    r = target_keyspace(b, l + 1);
    r = r && consumeToken(b, "USING");
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
  // 'WHEN' 'NOT' 'MATCHED' 'THEN' 'INSERT' expr where-clause?
  public static boolean lookup_merge_insert(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lookup_merge_insert")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LOOKUP_MERGE_INSERT, "<lookup merge insert>");
    r = consumeToken(b, "WHEN");
    r = r && consumeToken(b, "NOT");
    r = r && consumeToken(b, "MATCHED");
    r = r && consumeToken(b, "THEN");
    r = r && consumeToken(b, "INSERT");
    r = r && expr(b, l + 1);
    r = r && lookup_merge_insert_6(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // where-clause?
  private static boolean lookup_merge_insert_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lookup_merge_insert_6")) return false;
    where_clause(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // 'ON' 'PRIMARY'? 'KEY' expr
  public static boolean lookup_merge_predicate(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lookup_merge_predicate")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LOOKUP_MERGE_PREDICATE, "<lookup merge predicate>");
    r = consumeToken(b, "ON");
    r = r && lookup_merge_predicate_1(b, l + 1);
    r = r && consumeToken(b, "KEY");
    r = r && expr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // 'PRIMARY'?
  private static boolean lookup_merge_predicate_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lookup_merge_predicate_1")) return false;
    consumeToken(b, "PRIMARY");
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
  // lookup-nest-type? 'NEST' lookup-nest-rhs lookup-nest-predicate
  public static boolean lookup_nest_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lookup_nest_clause")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LOOKUP_NEST_CLAUSE, "<lookup nest clause>");
    r = lookup_nest_clause_0(b, l + 1);
    r = r && consumeToken(b, "NEST");
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
  // 'ON' 'KEYS' expr
  public static boolean lookup_nest_predicate(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lookup_nest_predicate")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LOOKUP_NEST_PREDICATE, "<lookup nest predicate>");
    r = consumeToken(b, "ON");
    r = r && consumeToken(b, "KEYS");
    r = r && expr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // keyspace-ref ( 'AS'? alias )?
  public static boolean lookup_nest_rhs(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lookup_nest_rhs")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LOOKUP_NEST_RHS, "<lookup nest rhs>");
    r = keyspace_ref(b, l + 1);
    r = r && lookup_nest_rhs_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( 'AS'? alias )?
  private static boolean lookup_nest_rhs_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lookup_nest_rhs_1")) return false;
    lookup_nest_rhs_1_0(b, l + 1);
    return true;
  }

  // 'AS'? alias
  private static boolean lookup_nest_rhs_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lookup_nest_rhs_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = lookup_nest_rhs_1_0_0(b, l + 1);
    r = r && alias(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // 'AS'?
  private static boolean lookup_nest_rhs_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lookup_nest_rhs_1_0_0")) return false;
    consumeToken(b, "AS");
    return true;
  }

  /* ********************************************************** */
  // 'INNER' | ( 'LEFT' 'OUTER'? )
  public static boolean lookup_nest_type(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lookup_nest_type")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LOOKUP_NEST_TYPE, "<lookup nest type>");
    r = consumeToken(b, "INNER");
    if (!r) r = lookup_nest_type_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // 'LEFT' 'OUTER'?
  private static boolean lookup_nest_type_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lookup_nest_type_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "LEFT");
    r = r && lookup_nest_type_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // 'OUTER'?
  private static boolean lookup_nest_type_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lookup_nest_type_1_1")) return false;
    consumeToken(b, "OUTER");
    return true;
  }

  /* ********************************************************** */
  // 'MERGE' 'INTO' ( ansi-merge | lookup-merge ) limit-clause? returning-clause?
  public static boolean merge(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "merge")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, MERGE, "<merge>");
    r = consumeToken(b, "MERGE");
    r = r && consumeToken(b, "INTO");
    r = r && merge_2(b, l + 1);
    r = r && merge_3(b, l + 1);
    r = r && merge_4(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ansi-merge | lookup-merge
  private static boolean merge_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "merge_2")) return false;
    boolean r;
    r = ansi_merge(b, l + 1);
    if (!r) r = lookup_merge(b, l + 1);
    return r;
  }

  // limit-clause?
  private static boolean merge_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "merge_3")) return false;
    limit_clause(b, l + 1);
    return true;
  }

  // returning-clause?
  private static boolean merge_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "merge_4")) return false;
    returning_clause(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // 'WHEN' 'MATCHED' 'THEN' 'DELETE' where-clause?
  public static boolean merge_delete(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "merge_delete")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, MERGE_DELETE, "<merge delete>");
    r = consumeToken(b, "WHEN");
    r = r && consumeToken(b, "MATCHED");
    r = r && consumeToken(b, "THEN");
    r = r && consumeToken(b, "DELETE");
    r = r && merge_delete_4(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // where-clause?
  private static boolean merge_delete_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "merge_delete_4")) return false;
    where_clause(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // expr ( 'AS'? alias )?
  public static boolean merge_source_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "merge_source_expr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, MERGE_SOURCE_EXPR, "<merge source expr>");
    r = expr(b, l + 1);
    r = r && merge_source_expr_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( 'AS'? alias )?
  private static boolean merge_source_expr_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "merge_source_expr_1")) return false;
    merge_source_expr_1_0(b, l + 1);
    return true;
  }

  // 'AS'? alias
  private static boolean merge_source_expr_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "merge_source_expr_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = merge_source_expr_1_0_0(b, l + 1);
    r = r && alias(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // 'AS'?
  private static boolean merge_source_expr_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "merge_source_expr_1_0_0")) return false;
    consumeToken(b, "AS");
    return true;
  }

  /* ********************************************************** */
  // keyspace-ref ( 'AS'? alias )?
  public static boolean merge_source_keyspace(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "merge_source_keyspace")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, MERGE_SOURCE_KEYSPACE, "<merge source keyspace>");
    r = keyspace_ref(b, l + 1);
    r = r && merge_source_keyspace_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( 'AS'? alias )?
  private static boolean merge_source_keyspace_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "merge_source_keyspace_1")) return false;
    merge_source_keyspace_1_0(b, l + 1);
    return true;
  }

  // 'AS'? alias
  private static boolean merge_source_keyspace_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "merge_source_keyspace_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = merge_source_keyspace_1_0_0(b, l + 1);
    r = r && alias(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // 'AS'?
  private static boolean merge_source_keyspace_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "merge_source_keyspace_1_0_0")) return false;
    consumeToken(b, "AS");
    return true;
  }

  /* ********************************************************** */
  // subquery-expr 'AS'? alias
  public static boolean merge_source_subquery(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "merge_source_subquery")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, MERGE_SOURCE_SUBQUERY, "<merge source subquery>");
    r = subquery_expr(b, l + 1);
    r = r && merge_source_subquery_1(b, l + 1);
    r = r && alias(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // 'AS'?
  private static boolean merge_source_subquery_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "merge_source_subquery_1")) return false;
    consumeToken(b, "AS");
    return true;
  }

  /* ********************************************************** */
  // 'WHEN' 'MATCHED' 'THEN' 'UPDATE' set-clause? unset-clause? where-clause?
  public static boolean merge_update(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "merge_update")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, MERGE_UPDATE, "<merge update>");
    r = consumeToken(b, "WHEN");
    r = r && consumeToken(b, "MATCHED");
    r = r && consumeToken(b, "THEN");
    r = r && consumeToken(b, "UPDATE");
    r = r && merge_update_4(b, l + 1);
    r = r && merge_update_5(b, l + 1);
    r = r && merge_update_6(b, l + 1);
    exit_section_(b, l, m, r, false, null);
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
  // 'MISSING'
  public static boolean missing(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "missing")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, MISSING, "<missing>");
    r = consumeToken(b, "MISSING");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'USE' ( ansi-hint-terms other-hint-terms |
  //                            other-hint-terms ansi-hint-terms )
  public static boolean multiple_hints(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "multiple_hints")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, MULTIPLE_HINTS, "<multiple hints>");
    r = consumeToken(b, "USE");
    r = r && multiple_hints_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
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
  // identifier
  public static boolean name(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "name")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NAME, "<name>");
    r = identifier(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // expr
  public static boolean name_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "name_expr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NAME_EXPR, "<name expr>");
    r = expr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // identifier
  public static boolean name_var(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "name_var")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NAME_VAR, "<name var>");
    r = identifier(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // identifier
  public static boolean namespace(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "namespace")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NAMESPACE, "<namespace>");
    r = identifier(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '-'? intgr fraction? exponent?
  public static boolean nbr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nbr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NBR, "<nbr>");
    r = nbr_0(b, l + 1);
    r = r && intgr(b, l + 1);
    r = r && nbr_2(b, l + 1);
    r = r && nbr_3(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '-'?
  private static boolean nbr_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nbr_0")) return false;
    consumeToken(b, "-");
    return true;
  }

  // fraction?
  private static boolean nbr_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nbr_2")) return false;
    fraction(b, l + 1);
    return true;
  }

  // exponent?
  private static boolean nbr_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nbr_3")) return false;
    exponent(b, l + 1);
    return true;
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
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NESTED_EXPR, "<nested expr>");
    r = field_expr(b, l + 1);
    if (!r) r = element_expr(b, l + 1);
    if (!r) r = slice_expr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '\n'
  public static boolean newline(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "newline")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NEWLINE, "<newline>");
    r = consumeToken(b, "\\n");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '"use_nl"' ':' ( keyspace-array | keyspace-object )
  public static boolean nl_hint_json(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nl_hint_json")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NL_HINT_JSON, "<nl hint json>");
    r = consumeToken(b, "\"use_nl\"");
    r = r && consumeToken(b, ":");
    r = r && nl_hint_json_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
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
  // 'USE_NL' '(' ( keyspace )+ ')'
  public static boolean nl_hint_simple(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nl_hint_simple")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NL_HINT_SIMPLE, "<nl hint simple>");
    r = consumeToken(b, "USE_NL");
    r = r && consumeToken(b, "(");
    r = r && nl_hint_simple_2(b, l + 1);
    r = r && consumeToken(b, ")");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( keyspace )+
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

  // ( keyspace )
  private static boolean nl_hint_simple_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nl_hint_simple_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = keyspace(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // 'NOT' cond
  public static boolean not(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "not")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NOT, "<not>");
    r = consumeToken(b, "NOT");
    r = r && cond(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'FROM' ( 'FIRST' | 'LAST' )
  public static boolean nthval_from(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nthval_from")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NTHVAL_FROM, "<nthval from>");
    r = consumeToken(b, "FROM");
    r = r && nthval_from_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // 'FIRST' | 'LAST'
  private static boolean nthval_from_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nthval_from_1")) return false;
    boolean r;
    r = consumeToken(b, "FIRST");
    if (!r) r = consumeToken(b, "LAST");
    return r;
  }

  /* ********************************************************** */
  // 'NULL'
  public static boolean null_$(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "null_$")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NULL, "<null $>");
    r = consumeToken(b, "NULL");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ( 'RESPECT' | 'IGNORE' ) 'NULLS'
  public static boolean nulls_treatment(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nulls_treatment")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NULLS_TREATMENT, "<nulls treatment>");
    r = nulls_treatment_0(b, l + 1);
    r = r && consumeToken(b, "NULLS");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // 'RESPECT' | 'IGNORE'
  private static boolean nulls_treatment_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nulls_treatment_0")) return false;
    boolean r;
    r = consumeToken(b, "RESPECT");
    if (!r) r = consumeToken(b, "IGNORE");
    return r;
  }

  /* ********************************************************** */
  // str
  public static boolean obj(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "obj")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OBJ, "<obj>");
    r = str(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'OFFSET' expr
  public static boolean offset_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "offset_clause")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OFFSET_CLAUSE, "<offset clause>");
    r = consumeToken(b, "OFFSET");
    r = r && expr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '"option"' ':' ( '"build"' | '"probe"' | 'null' )
  public static boolean option_property(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "option_property")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OPTION_PROPERTY, "<option property>");
    r = consumeToken(b, "\"option\"");
    r = r && consumeToken(b, ":");
    r = r && option_property_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '"build"' | '"probe"' | 'null'
  private static boolean option_property_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "option_property_2")) return false;
    boolean r;
    r = consumeToken(b, "\"build\"");
    if (!r) r = consumeToken(b, "\"probe\"");
    if (!r) r = consumeToken(b, "null");
    return r;
  }

  /* ********************************************************** */
  // cond 'OR' cond
  public static boolean or(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "or")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OR, "<or>");
    r = cond(b, l + 1);
    r = r && consumeToken(b, "OR");
    r = r && cond(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'ORDER' 'BY' ordering-term ( ',' ordering-term )*
  public static boolean order_by_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "order_by_clause")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ORDER_BY_CLAUSE, "<order by clause>");
    r = consumeToken(b, "ORDER");
    r = r && consumeToken(b, "BY");
    r = r && ordering_term(b, l + 1);
    r = r && order_by_clause_3(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( ',' ordering-term )*
  private static boolean order_by_clause_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "order_by_clause_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!order_by_clause_3_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "order_by_clause_3", c)) break;
    }
    return true;
  }

  // ',' ordering-term
  private static boolean order_by_clause_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "order_by_clause_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ",");
    r = r && ordering_term(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '"ordered"' ':' 'true'
  public static boolean ordered_hint_json(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ordered_hint_json")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ORDERED_HINT_JSON, "<ordered hint json>");
    r = consumeToken(b, "\"ordered\"");
    r = r && consumeToken(b, ":");
    r = r && consumeToken(b, "true");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'ORDERED'
  public static boolean ordered_hint_simple(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ordered_hint_simple")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ORDERED_HINT_SIMPLE, "<ordered hint simple>");
    r = consumeToken(b, "ORDERED");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // expr ( 'ASC' | 'DESC' )? ( 'NULLS' ( 'FIRST' | 'LAST' ) )?
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

  // ( 'ASC' | 'DESC' )?
  private static boolean ordering_term_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ordering_term_1")) return false;
    ordering_term_1_0(b, l + 1);
    return true;
  }

  // 'ASC' | 'DESC'
  private static boolean ordering_term_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ordering_term_1_0")) return false;
    boolean r;
    r = consumeToken(b, "ASC");
    if (!r) r = consumeToken(b, "DESC");
    return r;
  }

  // ( 'NULLS' ( 'FIRST' | 'LAST' ) )?
  private static boolean ordering_term_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ordering_term_2")) return false;
    ordering_term_2_0(b, l + 1);
    return true;
  }

  // 'NULLS' ( 'FIRST' | 'LAST' )
  private static boolean ordering_term_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ordering_term_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "NULLS");
    r = r && ordering_term_2_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // 'FIRST' | 'LAST'
  private static boolean ordering_term_2_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ordering_term_2_0_1")) return false;
    boolean r;
    r = consumeToken(b, "FIRST");
    if (!r) r = consumeToken(b, "LAST");
    return r;
  }

  /* ********************************************************** */
  // function-name '(' ( expr ( ',' expr )* )? ')'
  public static boolean ordinary_function(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ordinary_function")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ORDINARY_FUNCTION, "<ordinary function>");
    r = function_name(b, l + 1);
    r = r && consumeToken(b, "(");
    r = r && ordinary_function_2(b, l + 1);
    r = r && consumeToken(b, ")");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( expr ( ',' expr )* )?
  private static boolean ordinary_function_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ordinary_function_2")) return false;
    ordinary_function_2_0(b, l + 1);
    return true;
  }

  // expr ( ',' expr )*
  private static boolean ordinary_function_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ordinary_function_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = expr(b, l + 1);
    r = r && ordinary_function_2_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( ',' expr )*
  private static boolean ordinary_function_2_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ordinary_function_2_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!ordinary_function_2_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "ordinary_function_2_0_1", c)) break;
    }
    return true;
  }

  // ',' expr
  private static boolean ordinary_function_2_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ordinary_function_2_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ",");
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
  // 'OVER' ( '(' window-definition ')' | window-ref )
  public static boolean over_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "over_clause")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OVER_CLAUSE, "<over clause>");
    r = consumeToken(b, "OVER");
    r = r && over_clause_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '(' window-definition ')' | window-ref
  private static boolean over_clause_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "over_clause_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = over_clause_1_0(b, l + 1);
    if (!r) r = window_ref(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // '(' window-definition ')'
  private static boolean over_clause_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "over_clause_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "(");
    r = r && window_definition(b, l + 1);
    r = r && consumeToken(b, ")");
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // 'PAIRS' '(' ( 'SELF' | index-key-object ) ')'
  public static boolean pairs_function(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pairs_function")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PAIRS_FUNCTION, "<pairs function>");
    r = consumeToken(b, "PAIRS");
    r = r && consumeToken(b, "(");
    r = r && pairs_function_2(b, l + 1);
    r = r && consumeToken(b, ")");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // 'SELF' | index-key-object
  private static boolean pairs_function_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pairs_function_2")) return false;
    boolean r;
    r = consumeToken(b, "SELF");
    if (!r) r = index_key_object(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // array
  public static boolean parameters(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameters")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PARAMETERS, "<parameters>");
    r = array(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // identifier ( "," identifier )* | "..."
  public static boolean params(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "params")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PARAMS, "<params>");
    r = params_0(b, l + 1);
    if (!r) r = consumeToken(b, "...");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // identifier ( "," identifier )*
  private static boolean params_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "params_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = identifier(b, l + 1);
    r = r && params_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( "," identifier )*
  private static boolean params_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "params_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!params_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "params_0_1", c)) break;
    }
    return true;
  }

  // "," identifier
  private static boolean params_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "params_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ",");
    r = r && identifier(b, l + 1);
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
  // identifier ( '[' expr ']' )* ( '.' identifier ( '[' expr ']' )* )*
  public static boolean path(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "path")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PATH, "<path>");
    r = identifier(b, l + 1);
    r = r && path_1(b, l + 1);
    r = r && path_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( '[' expr ']' )*
  private static boolean path_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "path_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!path_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "path_1", c)) break;
    }
    return true;
  }

  // '[' expr ']'
  private static boolean path_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "path_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "[");
    r = r && expr(b, l + 1);
    r = r && consumeToken(b, "]");
    exit_section_(b, m, null, r);
    return r;
  }

  // ( '.' identifier ( '[' expr ']' )* )*
  private static boolean path_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "path_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!path_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "path_2", c)) break;
    }
    return true;
  }

  // '.' identifier ( '[' expr ']' )*
  private static boolean path_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "path_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ".");
    r = r && identifier(b, l + 1);
    r = r && path_2_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( '[' expr ']' )*
  private static boolean path_2_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "path_2_0_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!path_2_0_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "path_2_0_2", c)) break;
    }
    return true;
  }

  // '[' expr ']'
  private static boolean path_2_0_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "path_2_0_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "[");
    r = r && expr(b, l + 1);
    r = r && consumeToken(b, "]");
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // expr
  public static boolean position(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "position")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, POSITION, "<position>");
    r = expr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ( 'ALL' | 'DISTINCT' )? ( result-expr ( ',' result-expr )* |
  //                ( 'RAW' | 'ELEMENT' | 'VALUE' ) expr ( 'AS'? alias )? )
  public static boolean projection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "projection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PROJECTION, "<projection>");
    r = projection_0(b, l + 1);
    r = r && projection_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( 'ALL' | 'DISTINCT' )?
  private static boolean projection_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "projection_0")) return false;
    projection_0_0(b, l + 1);
    return true;
  }

  // 'ALL' | 'DISTINCT'
  private static boolean projection_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "projection_0_0")) return false;
    boolean r;
    r = consumeToken(b, "ALL");
    if (!r) r = consumeToken(b, "DISTINCT");
    return r;
  }

  // result-expr ( ',' result-expr )* |
  //                ( 'RAW' | 'ELEMENT' | 'VALUE' ) expr ( 'AS'? alias )?
  private static boolean projection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "projection_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = projection_1_0(b, l + 1);
    if (!r) r = projection_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // result-expr ( ',' result-expr )*
  private static boolean projection_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "projection_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = result_expr(b, l + 1);
    r = r && projection_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( ',' result-expr )*
  private static boolean projection_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "projection_1_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!projection_1_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "projection_1_0_1", c)) break;
    }
    return true;
  }

  // ',' result-expr
  private static boolean projection_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "projection_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ",");
    r = r && result_expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( 'RAW' | 'ELEMENT' | 'VALUE' ) expr ( 'AS'? alias )?
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

  // 'RAW' | 'ELEMENT' | 'VALUE'
  private static boolean projection_1_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "projection_1_1_0")) return false;
    boolean r;
    r = consumeToken(b, "RAW");
    if (!r) r = consumeToken(b, "ELEMENT");
    if (!r) r = consumeToken(b, "VALUE");
    return r;
  }

  // ( 'AS'? alias )?
  private static boolean projection_1_1_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "projection_1_1_2")) return false;
    projection_1_1_2_0(b, l + 1);
    return true;
  }

  // 'AS'? alias
  private static boolean projection_1_1_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "projection_1_1_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = projection_1_1_2_0_0(b, l + 1);
    r = r && alias(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // 'AS'?
  private static boolean projection_1_1_2_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "projection_1_1_2_0_0")) return false;
    consumeToken(b, "AS");
    return true;
  }

  /* ********************************************************** */
  // expr '=' expr |
  //                     expr '==' expr |
  //                     expr '!=' expr |
  //                     expr '<>' expr |
  //                     expr '>' expr |
  //                     expr '>=' expr |
  //                     expr '<' expr |
  //                     expr '<=' expr
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

  // expr '=' expr
  private static boolean relational_expr_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "relational_expr_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = expr(b, l + 1);
    r = r && consumeToken(b, "=");
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // expr '==' expr
  private static boolean relational_expr_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "relational_expr_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = expr(b, l + 1);
    r = r && consumeToken(b, "==");
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // expr '!=' expr
  private static boolean relational_expr_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "relational_expr_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = expr(b, l + 1);
    r = r && consumeToken(b, "!=");
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // expr '<>' expr
  private static boolean relational_expr_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "relational_expr_3")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = expr(b, l + 1);
    r = r && consumeToken(b, "<>");
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // expr '>' expr
  private static boolean relational_expr_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "relational_expr_4")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = expr(b, l + 1);
    r = r && consumeToken(b, ">");
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // expr '>=' expr
  private static boolean relational_expr_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "relational_expr_5")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = expr(b, l + 1);
    r = r && consumeToken(b, ">=");
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // expr '<' expr
  private static boolean relational_expr_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "relational_expr_6")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = expr(b, l + 1);
    r = r && consumeToken(b, "<");
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // expr '<=' expr
  private static boolean relational_expr_7(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "relational_expr_7")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = expr(b, l + 1);
    r = r && consumeToken(b, "<=");
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ( path '.' )? '*' | expr ( 'AS'? alias )?
  public static boolean result_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "result_expr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, RESULT_EXPR, "<result expr>");
    r = result_expr_0(b, l + 1);
    if (!r) r = result_expr_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( path '.' )? '*'
  private static boolean result_expr_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "result_expr_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = result_expr_0_0(b, l + 1);
    r = r && consumeToken(b, "*");
    exit_section_(b, m, null, r);
    return r;
  }

  // ( path '.' )?
  private static boolean result_expr_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "result_expr_0_0")) return false;
    result_expr_0_0_0(b, l + 1);
    return true;
  }

  // path '.'
  private static boolean result_expr_0_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "result_expr_0_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = path(b, l + 1);
    r = r && consumeToken(b, ".");
    exit_section_(b, m, null, r);
    return r;
  }

  // expr ( 'AS'? alias )?
  private static boolean result_expr_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "result_expr_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = expr(b, l + 1);
    r = r && result_expr_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( 'AS'? alias )?
  private static boolean result_expr_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "result_expr_1_1")) return false;
    result_expr_1_1_0(b, l + 1);
    return true;
  }

  // 'AS'? alias
  private static boolean result_expr_1_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "result_expr_1_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = result_expr_1_1_0_0(b, l + 1);
    r = r && alias(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // 'AS'?
  private static boolean result_expr_1_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "result_expr_1_1_0_0")) return false;
    consumeToken(b, "AS");
    return true;
  }

  /* ********************************************************** */
  // 'RETURNING' (result-expr (',' result-expr)* |
  //                     ('RAW' | 'ELEMENT' | 'VALUE') expr)
  public static boolean returning_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "returning_clause")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, RETURNING_CLAUSE, "<returning clause>");
    r = consumeToken(b, "RETURNING");
    r = r && returning_clause_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // result-expr (',' result-expr)* |
  //                     ('RAW' | 'ELEMENT' | 'VALUE') expr
  private static boolean returning_clause_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "returning_clause_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = returning_clause_1_0(b, l + 1);
    if (!r) r = returning_clause_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // result-expr (',' result-expr)*
  private static boolean returning_clause_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "returning_clause_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = result_expr(b, l + 1);
    r = r && returning_clause_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (',' result-expr)*
  private static boolean returning_clause_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "returning_clause_1_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!returning_clause_1_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "returning_clause_1_0_1", c)) break;
    }
    return true;
  }

  // ',' result-expr
  private static boolean returning_clause_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "returning_clause_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ",");
    r = r && result_expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ('RAW' | 'ELEMENT' | 'VALUE') expr
  private static boolean returning_clause_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "returning_clause_1_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = returning_clause_1_1_0(b, l + 1);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // 'RAW' | 'ELEMENT' | 'VALUE'
  private static boolean returning_clause_1_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "returning_clause_1_1_0")) return false;
    boolean r;
    r = consumeToken(b, "RAW");
    if (!r) r = consumeToken(b, "ELEMENT");
    if (!r) r = consumeToken(b, "VALUE");
    return r;
  }

  /* ********************************************************** */
  // expr ( 'AS'? alias )?
  public static boolean rhs_generic(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rhs_generic")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, RHS_GENERIC, "<rhs generic>");
    r = expr(b, l + 1);
    r = r && rhs_generic_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( 'AS'? alias )?
  private static boolean rhs_generic_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rhs_generic_1")) return false;
    rhs_generic_1_0(b, l + 1);
    return true;
  }

  // 'AS'? alias
  private static boolean rhs_generic_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rhs_generic_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = rhs_generic_1_0_0(b, l + 1);
    r = r && alias(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // 'AS'?
  private static boolean rhs_generic_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rhs_generic_1_0_0")) return false;
    consumeToken(b, "AS");
    return true;
  }

  /* ********************************************************** */
  // keyspace-ref ( 'AS'? alias )? ansi-join-hints?
  public static boolean rhs_keyspace(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rhs_keyspace")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, RHS_KEYSPACE, "<rhs keyspace>");
    r = keyspace_ref(b, l + 1);
    r = r && rhs_keyspace_1(b, l + 1);
    r = r && rhs_keyspace_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( 'AS'? alias )?
  private static boolean rhs_keyspace_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rhs_keyspace_1")) return false;
    rhs_keyspace_1_0(b, l + 1);
    return true;
  }

  // 'AS'? alias
  private static boolean rhs_keyspace_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rhs_keyspace_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = rhs_keyspace_1_0_0(b, l + 1);
    r = r && alias(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // 'AS'?
  private static boolean rhs_keyspace_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rhs_keyspace_1_0_0")) return false;
    consumeToken(b, "AS");
    return true;
  }

  // ansi-join-hints?
  private static boolean rhs_keyspace_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rhs_keyspace_2")) return false;
    ansi_join_hints(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // subquery-expr 'AS'? alias
  public static boolean rhs_subquery(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rhs_subquery")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, RHS_SUBQUERY, "<rhs subquery>");
    r = subquery_expr(b, l + 1);
    r = r && rhs_subquery_1(b, l + 1);
    r = r && alias(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // 'AS'?
  private static boolean rhs_subquery_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rhs_subquery_1")) return false;
    consumeToken(b, "AS");
    return true;
  }

  /* ********************************************************** */
  // identifier
  static boolean role(PsiBuilder b, int l) {
    return identifier(b, l + 1);
  }

  /* ********************************************************** */
  // 'ROLLBACK' ( 'WORK' | 'TRAN' | 'TRANSACTION' )?
  //                        ( 'TO' 'SAVEPOINT' savepointname )?
  public static boolean rollback_transaction(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rollback_transaction")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ROLLBACK_TRANSACTION, "<rollback transaction>");
    r = consumeToken(b, "ROLLBACK");
    r = r && rollback_transaction_1(b, l + 1);
    r = r && rollback_transaction_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( 'WORK' | 'TRAN' | 'TRANSACTION' )?
  private static boolean rollback_transaction_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rollback_transaction_1")) return false;
    rollback_transaction_1_0(b, l + 1);
    return true;
  }

  // 'WORK' | 'TRAN' | 'TRANSACTION'
  private static boolean rollback_transaction_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rollback_transaction_1_0")) return false;
    boolean r;
    r = consumeToken(b, "WORK");
    if (!r) r = consumeToken(b, "TRAN");
    if (!r) r = consumeToken(b, "TRANSACTION");
    return r;
  }

  // ( 'TO' 'SAVEPOINT' savepointname )?
  private static boolean rollback_transaction_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rollback_transaction_2")) return false;
    rollback_transaction_2_0(b, l + 1);
    return true;
  }

  // 'TO' 'SAVEPOINT' savepointname
  private static boolean rollback_transaction_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rollback_transaction_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "TO");
    r = r && consumeToken(b, "SAVEPOINT");
    r = r && savepointname(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // 'SAVEPOINT' savepointname
  public static boolean savepoint(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "savepoint")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SAVEPOINT, "<savepoint>");
    r = consumeToken(b, "SAVEPOINT");
    r = r && savepointname(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // identifier
  public static boolean savepointname(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "savepointname")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SAVEPOINTNAME, "<savepointname>");
    r = identifier(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // identifier
  public static boolean scope(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "scope")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SCOPE, "<scope>");
    r = identifier(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // expr
  public static boolean search_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "search_expr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SEARCH_EXPR, "<search expr>");
    r = expr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'CASE' ('WHEN' cond 'THEN' expr)+ ('ELSE' expr)? 'END'
  public static boolean searched_case_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "searched_case_expr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SEARCHED_CASE_EXPR, "<searched case expr>");
    r = consumeToken(b, "CASE");
    r = r && searched_case_expr_1(b, l + 1);
    r = r && searched_case_expr_2(b, l + 1);
    r = r && consumeToken(b, "END");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ('WHEN' cond 'THEN' expr)+
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

  // 'WHEN' cond 'THEN' expr
  private static boolean searched_case_expr_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "searched_case_expr_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "WHEN");
    r = r && cond(b, l + 1);
    r = r && consumeToken(b, "THEN");
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ('ELSE' expr)?
  private static boolean searched_case_expr_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "searched_case_expr_2")) return false;
    searched_case_expr_2_0(b, l + 1);
    return true;
  }

  // 'ELSE' expr
  private static boolean searched_case_expr_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "searched_case_expr_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "ELSE");
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // select-term ( set-op select-term )* order-by-clause? limit-clause? offset-clause?
  public static boolean select(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "select")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SELECT, "<select>");
    r = select_term(b, l + 1);
    r = r && select_1(b, l + 1);
    r = r && select_2(b, l + 1);
    r = r && select_3(b, l + 1);
    r = r && select_4(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( set-op select-term )*
  private static boolean select_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "select_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!select_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "select_1", c)) break;
    }
    return true;
  }

  // set-op select-term
  private static boolean select_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "select_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = set_op(b, l + 1);
    r = r && select_term(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // order-by-clause?
  private static boolean select_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "select_2")) return false;
    order_by_clause(b, l + 1);
    return true;
  }

  // limit-clause?
  private static boolean select_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "select_3")) return false;
    limit_clause(b, l + 1);
    return true;
  }

  // offset-clause?
  private static boolean select_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "select_4")) return false;
    offset_clause(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // 'SELECT' hint-comment? projection
  public static boolean select_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "select_clause")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SELECT_CLAUSE, "<select clause>");
    r = consumeToken(b, "SELECT");
    r = r && select_clause_1(b, l + 1);
    r = r && projection(b, l + 1);
    exit_section_(b, l, m, r, false, null);
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
  // subselect | '(' select ')'
  public static boolean select_term(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "select_term")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SELECT_TERM, "<select term>");
    r = subselect(b, l + 1);
    if (!r) r = select_term_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '(' select ')'
  private static boolean select_term_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "select_term_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "(");
    r = r && select(b, l + 1);
    r = r && consumeToken(b, ")");
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // statement ( ';' statement )* ';'?
  public static boolean sequence(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "sequence")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SEQUENCE, "<sequence>");
    r = statement(b, l + 1);
    r = r && sequence_1(b, l + 1);
    r = r && sequence_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( ';' statement )*
  private static boolean sequence_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "sequence_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!sequence_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "sequence_1", c)) break;
    }
    return true;
  }

  // ';' statement
  private static boolean sequence_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "sequence_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ";");
    r = r && statement(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ';'?
  private static boolean sequence_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "sequence_2")) return false;
    consumeToken(b, ";");
    return true;
  }

  /* ********************************************************** */
  // 'SET' ( path '=' expr update-for? )
  //                ( ',' (  path '=' expr update-for? ) )*
  public static boolean set_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "set_clause")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SET_CLAUSE, "<set clause>");
    r = consumeToken(b, "SET");
    r = r && set_clause_1(b, l + 1);
    r = r && set_clause_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // path '=' expr update-for?
  private static boolean set_clause_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "set_clause_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = path(b, l + 1);
    r = r && consumeToken(b, "=");
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

  // ( ',' (  path '=' expr update-for? ) )*
  private static boolean set_clause_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "set_clause_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!set_clause_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "set_clause_2", c)) break;
    }
    return true;
  }

  // ',' (  path '=' expr update-for? )
  private static boolean set_clause_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "set_clause_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ",");
    r = r && set_clause_2_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // path '=' expr update-for?
  private static boolean set_clause_2_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "set_clause_2_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = path(b, l + 1);
    r = r && consumeToken(b, "=");
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
  // ( 'UNION' | 'INTERSECT' | 'EXCEPT' ) 'ALL'?
  public static boolean set_op(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "set_op")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SET_OP, "<set op>");
    r = set_op_0(b, l + 1);
    r = r && set_op_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // 'UNION' | 'INTERSECT' | 'EXCEPT'
  private static boolean set_op_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "set_op_0")) return false;
    boolean r;
    r = consumeToken(b, "UNION");
    if (!r) r = consumeToken(b, "INTERSECT");
    if (!r) r = consumeToken(b, "EXCEPT");
    return r;
  }

  // 'ALL'?
  private static boolean set_op_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "set_op_1")) return false;
    consumeToken(b, "ALL");
    return true;
  }

  /* ********************************************************** */
  // 'SET' 'TRANSACTION' 'ISOLATION' 'LEVEL' 'READ' 'COMMITTED'
  public static boolean set_transaction(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "set_transaction")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SET_TRANSACTION, "<set transaction>");
    r = consumeToken(b, "SET");
    r = r && consumeToken(b, "TRANSACTION");
    r = r && consumeToken(b, "ISOLATION");
    r = r && consumeToken(b, "LEVEL");
    r = r && consumeToken(b, "READ");
    r = r && consumeToken(b, "COMMITTED");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '+' |'-' |'/' | '%' | '^' | '<' | '>' | '<=' | '=>' | '!=' | '=' | ';'
  public static boolean signals(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "signals")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SIGNALS, "<signals>");
    r = consumeToken(b, "+");
    if (!r) r = consumeToken(b, "-");
    if (!r) r = consumeToken(b, "/");
    if (!r) r = consumeToken(b, "%");
    if (!r) r = consumeToken(b, "^");
    if (!r) r = consumeToken(b, "<");
    if (!r) r = consumeToken(b, ">");
    if (!r) r = consumeToken(b, "<=");
    if (!r) r = consumeToken(b, "=>");
    if (!r) r = consumeToken(b, "!=");
    if (!r) r = consumeToken(b, "=");
    if (!r) r = consumeToken(b, ";");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ( 'ALL' | 'DISTINCT' ) expr
  public static boolean simple_array_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "simple_array_expr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SIMPLE_ARRAY_EXPR, "<simple array expr>");
    r = simple_array_expr_0(b, l + 1);
    r = r && expr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // 'ALL' | 'DISTINCT'
  private static boolean simple_array_expr_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "simple_array_expr_0")) return false;
    boolean r;
    r = consumeToken(b, "ALL");
    if (!r) r = consumeToken(b, "DISTINCT");
    return r;
  }

  /* ********************************************************** */
  // 'CASE' expr ('WHEN' expr 'THEN' expr)+ ('ELSE' expr)? 'END'
  public static boolean simple_case_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "simple_case_expr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SIMPLE_CASE_EXPR, "<simple case expr>");
    r = consumeToken(b, "CASE");
    r = r && expr(b, l + 1);
    r = r && simple_case_expr_2(b, l + 1);
    r = r && simple_case_expr_3(b, l + 1);
    r = r && consumeToken(b, "END");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ('WHEN' expr 'THEN' expr)+
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

  // 'WHEN' expr 'THEN' expr
  private static boolean simple_case_expr_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "simple_case_expr_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "WHEN");
    r = r && expr(b, l + 1);
    r = r && consumeToken(b, "THEN");
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ('ELSE' expr)?
  private static boolean simple_case_expr_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "simple_case_expr_3")) return false;
    simple_case_expr_3_0(b, l + 1);
    return true;
  }

  // 'ELSE' expr
  private static boolean simple_case_expr_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "simple_case_expr_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "ELSE");
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
  // expr '[' start-expr ':' end-expr? ']'
  public static boolean slice_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "slice_expr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SLICE_EXPR, "<slice expr>");
    r = expr(b, l + 1);
    r = r && consumeToken(b, "[");
    r = r && start_expr(b, l + 1);
    r = r && consumeToken(b, ":");
    r = r && slice_expr_4(b, l + 1);
    r = r && consumeToken(b, "]");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // end-expr?
  private static boolean slice_expr_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "slice_expr_4")) return false;
    end_expr(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // kwd | kwd sqlKeywords
  public static boolean sqlKeywords(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "sqlKeywords")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SQL_KEYWORDS, "<sql keywords>");
    r = kwd(b, l + 1);
    if (!r) r = sqlKeywords_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // kwd sqlKeywords
  private static boolean sqlKeywords_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "sqlKeywords_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = kwd(b, l + 1);
    r = r && sqlKeywords(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // expr
  public static boolean start_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "start_expr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, START_EXPR, "<start expr>");
    r = expr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ddl-statement |
  //               dml-statement |
  //               dql-statement |
  //               tcl-statement |
  //               utility-statement
  public static boolean statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "statement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, STATEMENT, "<statement>");
    r = ddl_statement(b, l + 1);
    if (!r) r = dml_statement(b, l + 1);
    if (!r) r = dql_statement(b, l + 1);
    if (!r) r = tcl_statement(b, l + 1);
    if (!r) r = utility_statement(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '"' chr* '"' | "'" chr* "'"
  public static boolean str(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "str")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, STR, "<str>");
    r = str_0(b, l + 1);
    if (!r) r = str_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '"' chr* '"'
  private static boolean str_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "str_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "\"");
    r = r && str_0_1(b, l + 1);
    r = r && consumeToken(b, "\"");
    exit_section_(b, m, null, r);
    return r;
  }

  // chr*
  private static boolean str_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "str_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!chr(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "str_0_1", c)) break;
    }
    return true;
  }

  // "'" chr* "'"
  private static boolean str_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "str_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "'");
    r = r && str_1_1(b, l + 1);
    r = r && consumeToken(b, "'");
    exit_section_(b, m, null, r);
    return r;
  }

  // chr*
  private static boolean str_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "str_1_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!chr(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "str_1_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // '(' select ')'
  public static boolean subquery_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "subquery_expr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SUBQUERY_EXPR, "<subquery expr>");
    r = consumeToken(b, "(");
    r = r && select(b, l + 1);
    r = r && consumeToken(b, ")");
    exit_section_(b, l, m, r, false, null);
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
  // expr
  public static boolean target_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "target_expr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, TARGET_EXPR, "<target expr>");
    r = expr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // keyspace-ref ( 'AS'? alias )?
  public static boolean target_keyspace(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "target_keyspace")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, TARGET_KEYSPACE, "<target keyspace>");
    r = keyspace_ref(b, l + 1);
    r = r && target_keyspace_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( 'AS'? alias )?
  private static boolean target_keyspace_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "target_keyspace_1")) return false;
    target_keyspace_1_0(b, l + 1);
    return true;
  }

  // 'AS'? alias
  private static boolean target_keyspace_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "target_keyspace_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = target_keyspace_1_0_0(b, l + 1);
    r = r && alias(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // 'AS'?
  private static boolean target_keyspace_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "target_keyspace_1_0_0")) return false;
    consumeToken(b, "AS");
    return true;
  }

  /* ********************************************************** */
  // begin-transaction
  //                 | set-transaction
  //                 | savepoint
  //                 | rollback-transaction
  //                 | commit-transaction
  public static boolean tcl_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tcl_statement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, TCL_STATEMENT, "<tcl statement>");
    r = begin_transaction(b, l + 1);
    if (!r) r = set_transaction(b, l + 1);
    if (!r) r = savepoint(b, l + 1);
    if (!r) r = rollback_transaction(b, l + 1);
    if (!r) r = commit_transaction(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // [a-zA-Z_] ( [0-9a-zA-Z_$] )*
  public static boolean unescaped_identifier(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unescaped_identifier")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, UNESCAPED_IDENTIFIER, "<unescaped identifier>");
    r = unescaped_identifier_0(b, l + 1);
    r = r && unescaped_identifier_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // [a-zA-Z_]
  private static boolean unescaped_identifier_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unescaped_identifier_0")) return false;
    consumeToken(b, A_ZA_Z_);
    return true;
  }

  // ( [0-9a-zA-Z_$] )*
  private static boolean unescaped_identifier_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unescaped_identifier_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!unescaped_identifier_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "unescaped_identifier_1", c)) break;
    }
    return true;
  }

  // [0-9a-zA-Z_$]
  private static boolean unescaped_identifier_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unescaped_identifier_1_0")) return false;
    consumeToken(b, "0-9a-zA-Z_$");
    return true;
  }

  /* ********************************************************** */
  // unnest-type? ( 'UNNEST' | 'FLATTEN' ) expr ( 'AS'? alias )?
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

  // 'UNNEST' | 'FLATTEN'
  private static boolean unnest_clause_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unnest_clause_1")) return false;
    boolean r;
    r = consumeToken(b, "UNNEST");
    if (!r) r = consumeToken(b, "FLATTEN");
    return r;
  }

  // ( 'AS'? alias )?
  private static boolean unnest_clause_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unnest_clause_3")) return false;
    unnest_clause_3_0(b, l + 1);
    return true;
  }

  // 'AS'? alias
  private static boolean unnest_clause_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unnest_clause_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = unnest_clause_3_0_0(b, l + 1);
    r = r && alias(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // 'AS'?
  private static boolean unnest_clause_3_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unnest_clause_3_0_0")) return false;
    consumeToken(b, "AS");
    return true;
  }

  /* ********************************************************** */
  // 'INNER' | ( 'LEFT' 'OUTER'? )
  public static boolean unnest_type(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unnest_type")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, UNNEST_TYPE, "<unnest type>");
    r = consumeToken(b, "INNER");
    if (!r) r = unnest_type_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // 'LEFT' 'OUTER'?
  private static boolean unnest_type_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unnest_type_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "LEFT");
    r = r && unnest_type_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // 'OUTER'?
  private static boolean unnest_type_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unnest_type_1_1")) return false;
    consumeToken(b, "OUTER");
    return true;
  }

  /* ********************************************************** */
  // 'UNSET' path update-for? (',' path update-for?)*
  public static boolean unset_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unset_clause")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, UNSET_CLAUSE, "<unset clause>");
    r = consumeToken(b, "UNSET");
    r = r && path(b, l + 1);
    r = r && unset_clause_2(b, l + 1);
    r = r && unset_clause_3(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // update-for?
  private static boolean unset_clause_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unset_clause_2")) return false;
    update_for(b, l + 1);
    return true;
  }

  // (',' path update-for?)*
  private static boolean unset_clause_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unset_clause_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!unset_clause_3_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "unset_clause_3", c)) break;
    }
    return true;
  }

  // ',' path update-for?
  private static boolean unset_clause_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unset_clause_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ",");
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
  // 'UPDATE' target-keyspace use-keys? set-clause? unset-clause?
  //             where-clause? limit-clause? returning-clause?
  public static boolean update(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, UPDATE, "<update>");
    r = consumeToken(b, "UPDATE");
    r = r && target_keyspace(b, l + 1);
    r = r && update_2(b, l + 1);
    r = r && update_3(b, l + 1);
    r = r && update_4(b, l + 1);
    r = r && update_5(b, l + 1);
    r = r && update_6(b, l + 1);
    r = r && update_7(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // use-keys?
  private static boolean update_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_2")) return false;
    use_keys(b, l + 1);
    return true;
  }

  // set-clause?
  private static boolean update_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_3")) return false;
    set_clause(b, l + 1);
    return true;
  }

  // unset-clause?
  private static boolean update_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_4")) return false;
    unset_clause(b, l + 1);
    return true;
  }

  // where-clause?
  private static boolean update_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_5")) return false;
    where_clause(b, l + 1);
    return true;
  }

  // limit-clause?
  private static boolean update_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_6")) return false;
    limit_clause(b, l + 1);
    return true;
  }

  // returning-clause?
  private static boolean update_7(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_7")) return false;
    returning_clause(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // ('FOR' (name-var ':')? var ('IN' | 'WITHIN') path
  //                (','   (name-var ':')? var ('IN' | 'WITHIN') path)* )+
  //                ('WHEN' cond)? 'END'
  public static boolean update_for(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_for")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, UPDATE_FOR, "<update for>");
    r = update_for_0(b, l + 1);
    r = r && update_for_1(b, l + 1);
    r = r && consumeToken(b, "END");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ('FOR' (name-var ':')? var ('IN' | 'WITHIN') path
  //                (','   (name-var ':')? var ('IN' | 'WITHIN') path)* )+
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

  // 'FOR' (name-var ':')? var ('IN' | 'WITHIN') path
  //                (','   (name-var ':')? var ('IN' | 'WITHIN') path)*
  private static boolean update_for_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_for_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "FOR");
    r = r && update_for_0_0_1(b, l + 1);
    r = r && var(b, l + 1);
    r = r && update_for_0_0_3(b, l + 1);
    r = r && path(b, l + 1);
    r = r && update_for_0_0_5(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (name-var ':')?
  private static boolean update_for_0_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_for_0_0_1")) return false;
    update_for_0_0_1_0(b, l + 1);
    return true;
  }

  // name-var ':'
  private static boolean update_for_0_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_for_0_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = name_var(b, l + 1);
    r = r && consumeToken(b, ":");
    exit_section_(b, m, null, r);
    return r;
  }

  // 'IN' | 'WITHIN'
  private static boolean update_for_0_0_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_for_0_0_3")) return false;
    boolean r;
    r = consumeToken(b, "IN");
    if (!r) r = consumeToken(b, "WITHIN");
    return r;
  }

  // (','   (name-var ':')? var ('IN' | 'WITHIN') path)*
  private static boolean update_for_0_0_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_for_0_0_5")) return false;
    while (true) {
      int c = current_position_(b);
      if (!update_for_0_0_5_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "update_for_0_0_5", c)) break;
    }
    return true;
  }

  // ','   (name-var ':')? var ('IN' | 'WITHIN') path
  private static boolean update_for_0_0_5_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_for_0_0_5_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ",");
    r = r && update_for_0_0_5_0_1(b, l + 1);
    r = r && var(b, l + 1);
    r = r && update_for_0_0_5_0_3(b, l + 1);
    r = r && path(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (name-var ':')?
  private static boolean update_for_0_0_5_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_for_0_0_5_0_1")) return false;
    update_for_0_0_5_0_1_0(b, l + 1);
    return true;
  }

  // name-var ':'
  private static boolean update_for_0_0_5_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_for_0_0_5_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = name_var(b, l + 1);
    r = r && consumeToken(b, ":");
    exit_section_(b, m, null, r);
    return r;
  }

  // 'IN' | 'WITHIN'
  private static boolean update_for_0_0_5_0_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_for_0_0_5_0_3")) return false;
    boolean r;
    r = consumeToken(b, "IN");
    if (!r) r = consumeToken(b, "WITHIN");
    return r;
  }

  // ('WHEN' cond)?
  private static boolean update_for_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_for_1")) return false;
    update_for_1_0(b, l + 1);
    return true;
  }

  // 'WHEN' cond
  private static boolean update_for_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_for_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "WHEN");
    r = r && cond(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // update-statistics-expr | update-statistics-index | update-statistics-delete
  public static boolean update_statistics(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_statistics")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, UPDATE_STATISTICS, "<update statistics>");
    r = update_statistics_expr(b, l + 1);
    if (!r) r = update_statistics_index(b, l + 1);
    if (!r) r = update_statistics_delete(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ( 'UPDATE' 'STATISTICS' 'FOR'? |
  //                                'ANALYZE' ( 'KEYSPACE' | 'COLLECTION')? )
  //                                keyspace-ref delete-clause
  public static boolean update_statistics_delete(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_statistics_delete")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, UPDATE_STATISTICS_DELETE, "<update statistics delete>");
    r = update_statistics_delete_0(b, l + 1);
    r = r && keyspace_ref(b, l + 1);
    r = r && delete_clause(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // 'UPDATE' 'STATISTICS' 'FOR'? |
  //                                'ANALYZE' ( 'KEYSPACE' | 'COLLECTION')?
  private static boolean update_statistics_delete_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_statistics_delete_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = update_statistics_delete_0_0(b, l + 1);
    if (!r) r = update_statistics_delete_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // 'UPDATE' 'STATISTICS' 'FOR'?
  private static boolean update_statistics_delete_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_statistics_delete_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "UPDATE");
    r = r && consumeToken(b, "STATISTICS");
    r = r && update_statistics_delete_0_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // 'FOR'?
  private static boolean update_statistics_delete_0_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_statistics_delete_0_0_2")) return false;
    consumeToken(b, "FOR");
    return true;
  }

  // 'ANALYZE' ( 'KEYSPACE' | 'COLLECTION')?
  private static boolean update_statistics_delete_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_statistics_delete_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "ANALYZE");
    r = r && update_statistics_delete_0_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( 'KEYSPACE' | 'COLLECTION')?
  private static boolean update_statistics_delete_0_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_statistics_delete_0_1_1")) return false;
    update_statistics_delete_0_1_1_0(b, l + 1);
    return true;
  }

  // 'KEYSPACE' | 'COLLECTION'
  private static boolean update_statistics_delete_0_1_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_statistics_delete_0_1_1_0")) return false;
    boolean r;
    r = consumeToken(b, "KEYSPACE");
    if (!r) r = consumeToken(b, "COLLECTION");
    return r;
  }

  /* ********************************************************** */
  // ( 'UPDATE' 'STATISTICS' 'FOR'? |
  //                              'ANALYZE' ( 'KEYSPACE' | 'COLLECTION')? )
  //                              keyspace-ref '(' index-key ( ',' index-key )* ')' index-with?
  public static boolean update_statistics_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_statistics_expr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, UPDATE_STATISTICS_EXPR, "<update statistics expr>");
    r = update_statistics_expr_0(b, l + 1);
    r = r && keyspace_ref(b, l + 1);
    r = r && consumeToken(b, "(");
    r = r && index_key(b, l + 1);
    r = r && update_statistics_expr_4(b, l + 1);
    r = r && consumeToken(b, ")");
    r = r && update_statistics_expr_6(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // 'UPDATE' 'STATISTICS' 'FOR'? |
  //                              'ANALYZE' ( 'KEYSPACE' | 'COLLECTION')?
  private static boolean update_statistics_expr_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_statistics_expr_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = update_statistics_expr_0_0(b, l + 1);
    if (!r) r = update_statistics_expr_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // 'UPDATE' 'STATISTICS' 'FOR'?
  private static boolean update_statistics_expr_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_statistics_expr_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "UPDATE");
    r = r && consumeToken(b, "STATISTICS");
    r = r && update_statistics_expr_0_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // 'FOR'?
  private static boolean update_statistics_expr_0_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_statistics_expr_0_0_2")) return false;
    consumeToken(b, "FOR");
    return true;
  }

  // 'ANALYZE' ( 'KEYSPACE' | 'COLLECTION')?
  private static boolean update_statistics_expr_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_statistics_expr_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "ANALYZE");
    r = r && update_statistics_expr_0_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( 'KEYSPACE' | 'COLLECTION')?
  private static boolean update_statistics_expr_0_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_statistics_expr_0_1_1")) return false;
    update_statistics_expr_0_1_1_0(b, l + 1);
    return true;
  }

  // 'KEYSPACE' | 'COLLECTION'
  private static boolean update_statistics_expr_0_1_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_statistics_expr_0_1_1_0")) return false;
    boolean r;
    r = consumeToken(b, "KEYSPACE");
    if (!r) r = consumeToken(b, "COLLECTION");
    return r;
  }

  // ( ',' index-key )*
  private static boolean update_statistics_expr_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_statistics_expr_4")) return false;
    while (true) {
      int c = current_position_(b);
      if (!update_statistics_expr_4_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "update_statistics_expr_4", c)) break;
    }
    return true;
  }

  // ',' index-key
  private static boolean update_statistics_expr_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_statistics_expr_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ",");
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
  // ( 'UPDATE' 'STATISTICS' 'FOR' | 'ANALYZE' )
  //                               index-clause index-using?  index-with?
  public static boolean update_statistics_index(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_statistics_index")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, UPDATE_STATISTICS_INDEX, "<update statistics index>");
    r = update_statistics_index_0(b, l + 1);
    r = r && index_clause(b, l + 1);
    r = r && update_statistics_index_2(b, l + 1);
    r = r && update_statistics_index_3(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // 'UPDATE' 'STATISTICS' 'FOR' | 'ANALYZE'
  private static boolean update_statistics_index_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_statistics_index_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = update_statistics_index_0_0(b, l + 1);
    if (!r) r = consumeToken(b, "ANALYZE");
    exit_section_(b, m, null, r);
    return r;
  }

  // 'UPDATE' 'STATISTICS' 'FOR'
  private static boolean update_statistics_index_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_statistics_index_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "UPDATE");
    r = r && consumeToken(b, "STATISTICS");
    r = r && consumeToken(b, "FOR");
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
  // ( 'UPDATE' 'STATISTICS' 'FOR'? |
  //                                 'ANALYZE' ( 'KEYSPACE' | 'COLLECTION')? )
  //                                 keyspace-ref indexes-clause index-using? index-with?
  public static boolean update_statistics_indexes(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_statistics_indexes")) return false;
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

  // 'UPDATE' 'STATISTICS' 'FOR'? |
  //                                 'ANALYZE' ( 'KEYSPACE' | 'COLLECTION')?
  private static boolean update_statistics_indexes_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_statistics_indexes_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = update_statistics_indexes_0_0(b, l + 1);
    if (!r) r = update_statistics_indexes_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // 'UPDATE' 'STATISTICS' 'FOR'?
  private static boolean update_statistics_indexes_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_statistics_indexes_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "UPDATE");
    r = r && consumeToken(b, "STATISTICS");
    r = r && update_statistics_indexes_0_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // 'FOR'?
  private static boolean update_statistics_indexes_0_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_statistics_indexes_0_0_2")) return false;
    consumeToken(b, "FOR");
    return true;
  }

  // 'ANALYZE' ( 'KEYSPACE' | 'COLLECTION')?
  private static boolean update_statistics_indexes_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_statistics_indexes_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "ANALYZE");
    r = r && update_statistics_indexes_0_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( 'KEYSPACE' | 'COLLECTION')?
  private static boolean update_statistics_indexes_0_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_statistics_indexes_0_1_1")) return false;
    update_statistics_indexes_0_1_1_0(b, l + 1);
    return true;
  }

  // 'KEYSPACE' | 'COLLECTION'
  private static boolean update_statistics_indexes_0_1_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "update_statistics_indexes_0_1_1_0")) return false;
    boolean r;
    r = consumeToken(b, "KEYSPACE");
    if (!r) r = consumeToken(b, "COLLECTION");
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
  // 'UPSERT' 'INTO' target-keyspace ( insert-values | insert-select )
  //             returning-clause?
  public static boolean upsert(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "upsert")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, UPSERT, "<upsert>");
    r = consumeToken(b, "UPSERT");
    r = r && consumeToken(b, "INTO");
    r = r && target_keyspace(b, l + 1);
    r = r && upsert_3(b, l + 1);
    r = r && upsert_4(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // insert-values | insert-select
  private static boolean upsert_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "upsert_3")) return false;
    boolean r;
    r = insert_values(b, l + 1);
    if (!r) r = insert_select(b, l + 1);
    return r;
  }

  // returning-clause?
  private static boolean upsert_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "upsert_4")) return false;
    returning_clause(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // use-keys-clause | use-index-clause
  public static boolean use_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "use_clause")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, USE_CLAUSE, "<use clause>");
    r = use_keys_clause(b, l + 1);
    if (!r) r = use_index_clause(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'USE' use-hash-term
  public static boolean use_hash_hint(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "use_hash_hint")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, USE_HASH_HINT, "<use hash hint>");
    r = consumeToken(b, "USE");
    r = r && use_hash_term(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'HASH' '(' ( 'BUILD' | 'PROBE' ) ')'
  public static boolean use_hash_term(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "use_hash_term")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, USE_HASH_TERM, "<use hash term>");
    r = consumeToken(b, "HASH");
    r = r && consumeToken(b, "(");
    r = r && use_hash_term_2(b, l + 1);
    r = r && consumeToken(b, ")");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // 'BUILD' | 'PROBE'
  private static boolean use_hash_term_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "use_hash_term_2")) return false;
    boolean r;
    r = consumeToken(b, "BUILD");
    if (!r) r = consumeToken(b, "PROBE");
    return r;
  }

  /* ********************************************************** */
  // 'USE' use-index-term
  public static boolean use_index_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "use_index_clause")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, USE_INDEX_CLAUSE, "<use index clause>");
    r = consumeToken(b, "USE");
    r = r && use_index_term(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'INDEX' '(' index-ref ( ',' index-ref )* ')'
  public static boolean use_index_term(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "use_index_term")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, USE_INDEX_TERM, "<use index term>");
    r = consumeToken(b, "INDEX");
    r = r && consumeToken(b, "(");
    r = r && index_ref(b, l + 1);
    r = r && use_index_term_3(b, l + 1);
    r = r && consumeToken(b, ")");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( ',' index-ref )*
  private static boolean use_index_term_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "use_index_term_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!use_index_term_3_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "use_index_term_3", c)) break;
    }
    return true;
  }

  // ',' index-ref
  private static boolean use_index_term_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "use_index_term_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ",");
    r = r && index_ref(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // str
  public static boolean use_keys(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "use_keys")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, USE_KEYS, "<use keys>");
    r = str(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'USE' use-keys-term
  public static boolean use_keys_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "use_keys_clause")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, USE_KEYS_CLAUSE, "<use keys clause>");
    r = consumeToken(b, "USE");
    r = r && use_keys_term(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'PRIMARY'? 'KEYS' expr
  public static boolean use_keys_term(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "use_keys_term")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, USE_KEYS_TERM, "<use keys term>");
    r = use_keys_term_0(b, l + 1);
    r = r && consumeToken(b, "KEYS");
    r = r && expr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // 'PRIMARY'?
  private static boolean use_keys_term_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "use_keys_term_0")) return false;
    consumeToken(b, "PRIMARY");
    return true;
  }

  /* ********************************************************** */
  // 'USE' use-nl-term
  public static boolean use_nl_hint(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "use_nl_hint")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, USE_NL_HINT, "<use nl hint>");
    r = consumeToken(b, "USE");
    r = r && use_nl_term(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'NL'
  public static boolean use_nl_term(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "use_nl_term")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, USE_NL_TERM, "<use nl term>");
    r = consumeToken(b, "NL");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // identifier
  public static boolean user(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "user")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, USER, "<user>");
    r = identifier(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // advise
  //                     | explain
  public static boolean utility_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "utility_statement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, UTILITY_STATEMENT, "<utility statement>");
    r = advise(b, l + 1);
    if (!r) r = explain(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // expr
  public static boolean value(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "value")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, VALUE, "<value>");
    r = expr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'VALUES'  '(' key ',' value ( ',' options )? ')'
  //             ( ',' 'VALUES'? '(' key ',' value ( ',' options )? ')' )*
  public static boolean values_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "values_clause")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, VALUES_CLAUSE, "<values clause>");
    r = consumeToken(b, "VALUES");
    r = r && consumeToken(b, "(");
    r = r && key(b, l + 1);
    r = r && consumeToken(b, ",");
    r = r && value(b, l + 1);
    r = r && values_clause_5(b, l + 1);
    r = r && consumeToken(b, ")");
    r = r && values_clause_7(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( ',' options )?
  private static boolean values_clause_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "values_clause_5")) return false;
    values_clause_5_0(b, l + 1);
    return true;
  }

  // ',' options
  private static boolean values_clause_5_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "values_clause_5_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ",");
    r = r && consumeToken(b, OPTIONS);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( ',' 'VALUES'? '(' key ',' value ( ',' options )? ')' )*
  private static boolean values_clause_7(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "values_clause_7")) return false;
    while (true) {
      int c = current_position_(b);
      if (!values_clause_7_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "values_clause_7", c)) break;
    }
    return true;
  }

  // ',' 'VALUES'? '(' key ',' value ( ',' options )? ')'
  private static boolean values_clause_7_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "values_clause_7_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ",");
    r = r && values_clause_7_0_1(b, l + 1);
    r = r && consumeToken(b, "(");
    r = r && key(b, l + 1);
    r = r && consumeToken(b, ",");
    r = r && value(b, l + 1);
    r = r && values_clause_7_0_6(b, l + 1);
    r = r && consumeToken(b, ")");
    exit_section_(b, m, null, r);
    return r;
  }

  // 'VALUES'?
  private static boolean values_clause_7_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "values_clause_7_0_1")) return false;
    consumeToken(b, "VALUES");
    return true;
  }

  // ( ',' options )?
  private static boolean values_clause_7_0_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "values_clause_7_0_6")) return false;
    values_clause_7_0_6_0(b, l + 1);
    return true;
  }

  // ',' options
  private static boolean values_clause_7_0_6_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "values_clause_7_0_6_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ",");
    r = r && consumeToken(b, OPTIONS);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // identifier
  public static boolean var(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "var")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, VAR, "<var>");
    r = identifier(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // expr
  public static boolean var_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "var_expr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, VAR_EXPR, "<var expr>");
    r = expr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'WHERE' cond
  public static boolean where_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "where_clause")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, WHERE_CLAUSE, "<where clause>");
    r = consumeToken(b, "WHERE");
    r = r && cond(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'WINDOW' window-declaration ( ',' window-declaration )*
  public static boolean window_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_clause")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, WINDOW_CLAUSE, "<window clause>");
    r = consumeToken(b, "WINDOW");
    r = r && window_declaration(b, l + 1);
    r = r && window_clause_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( ',' window-declaration )*
  private static boolean window_clause_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_clause_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!window_clause_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "window_clause_2", c)) break;
    }
    return true;
  }

  // ',' window-declaration
  private static boolean window_clause_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_clause_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ",");
    r = r && window_declaration(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // window-name 'AS' '(' window-definition ')'
  public static boolean window_declaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_declaration")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, WINDOW_DECLARATION, "<window declaration>");
    r = window_name(b, l + 1);
    r = r && consumeToken(b, "AS");
    r = r && consumeToken(b, "(");
    r = r && window_definition(b, l + 1);
    r = r && consumeToken(b, ")");
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
  // ( 'ROWS' | 'RANGE' | 'GROUPS' ) window-frame-extent
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

  // 'ROWS' | 'RANGE' | 'GROUPS'
  private static boolean window_frame_clause_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_frame_clause_0")) return false;
    boolean r;
    r = consumeToken(b, "ROWS");
    if (!r) r = consumeToken(b, "RANGE");
    if (!r) r = consumeToken(b, "GROUPS");
    return r;
  }

  // window-frame-exclusion?
  private static boolean window_frame_clause_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_frame_clause_2")) return false;
    window_frame_exclusion(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // 'EXCLUDE' ( 'CURRENT' 'ROW' | 'GROUP' | 'TIES' | 'NO' 'OTHERS' )
  public static boolean window_frame_exclusion(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_frame_exclusion")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, WINDOW_FRAME_EXCLUSION, "<window frame exclusion>");
    r = consumeToken(b, "EXCLUDE");
    r = r && window_frame_exclusion_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // 'CURRENT' 'ROW' | 'GROUP' | 'TIES' | 'NO' 'OTHERS'
  private static boolean window_frame_exclusion_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_frame_exclusion_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = window_frame_exclusion_1_0(b, l + 1);
    if (!r) r = consumeToken(b, "GROUP");
    if (!r) r = consumeToken(b, "TIES");
    if (!r) r = window_frame_exclusion_1_3(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // 'CURRENT' 'ROW'
  private static boolean window_frame_exclusion_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_frame_exclusion_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "CURRENT");
    r = r && consumeToken(b, "ROW");
    exit_section_(b, m, null, r);
    return r;
  }

  // 'NO' 'OTHERS'
  private static boolean window_frame_exclusion_1_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_frame_exclusion_1_3")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "NO");
    r = r && consumeToken(b, "OTHERS");
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // 'UNBOUNDED' 'PRECEDING' | 'CURRENT' 'ROW' |
  //                         'BETWEEN' ( 'UNBOUNDED' 'PRECEDING' | 'CURRENT' 'ROW' | ( 'PRECEDING' | 'FOLLOWING' ) )
  //                             'AND' ( 'UNBOUNDED' 'FOLLOWING' | 'CURRENT' 'ROW' | ( 'PRECEDING' | 'FOLLOWING' ) )
  public static boolean window_frame_extent(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_frame_extent")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, WINDOW_FRAME_EXTENT, "<window frame extent>");
    r = window_frame_extent_0(b, l + 1);
    if (!r) r = window_frame_extent_1(b, l + 1);
    if (!r) r = window_frame_extent_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // 'UNBOUNDED' 'PRECEDING'
  private static boolean window_frame_extent_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_frame_extent_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "UNBOUNDED");
    r = r && consumeToken(b, "PRECEDING");
    exit_section_(b, m, null, r);
    return r;
  }

  // 'CURRENT' 'ROW'
  private static boolean window_frame_extent_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_frame_extent_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "CURRENT");
    r = r && consumeToken(b, "ROW");
    exit_section_(b, m, null, r);
    return r;
  }

  // 'BETWEEN' ( 'UNBOUNDED' 'PRECEDING' | 'CURRENT' 'ROW' | ( 'PRECEDING' | 'FOLLOWING' ) )
  //                             'AND' ( 'UNBOUNDED' 'FOLLOWING' | 'CURRENT' 'ROW' | ( 'PRECEDING' | 'FOLLOWING' ) )
  private static boolean window_frame_extent_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_frame_extent_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "BETWEEN");
    r = r && window_frame_extent_2_1(b, l + 1);
    r = r && consumeToken(b, "AND");
    r = r && window_frame_extent_2_3(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // 'UNBOUNDED' 'PRECEDING' | 'CURRENT' 'ROW' | ( 'PRECEDING' | 'FOLLOWING' )
  private static boolean window_frame_extent_2_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_frame_extent_2_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = window_frame_extent_2_1_0(b, l + 1);
    if (!r) r = window_frame_extent_2_1_1(b, l + 1);
    if (!r) r = window_frame_extent_2_1_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // 'UNBOUNDED' 'PRECEDING'
  private static boolean window_frame_extent_2_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_frame_extent_2_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "UNBOUNDED");
    r = r && consumeToken(b, "PRECEDING");
    exit_section_(b, m, null, r);
    return r;
  }

  // 'CURRENT' 'ROW'
  private static boolean window_frame_extent_2_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_frame_extent_2_1_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "CURRENT");
    r = r && consumeToken(b, "ROW");
    exit_section_(b, m, null, r);
    return r;
  }

  // 'PRECEDING' | 'FOLLOWING'
  private static boolean window_frame_extent_2_1_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_frame_extent_2_1_2")) return false;
    boolean r;
    r = consumeToken(b, "PRECEDING");
    if (!r) r = consumeToken(b, "FOLLOWING");
    return r;
  }

  // 'UNBOUNDED' 'FOLLOWING' | 'CURRENT' 'ROW' | ( 'PRECEDING' | 'FOLLOWING' )
  private static boolean window_frame_extent_2_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_frame_extent_2_3")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = window_frame_extent_2_3_0(b, l + 1);
    if (!r) r = window_frame_extent_2_3_1(b, l + 1);
    if (!r) r = window_frame_extent_2_3_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // 'UNBOUNDED' 'FOLLOWING'
  private static boolean window_frame_extent_2_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_frame_extent_2_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "UNBOUNDED");
    r = r && consumeToken(b, "FOLLOWING");
    exit_section_(b, m, null, r);
    return r;
  }

  // 'CURRENT' 'ROW'
  private static boolean window_frame_extent_2_3_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_frame_extent_2_3_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "CURRENT");
    r = r && consumeToken(b, "ROW");
    exit_section_(b, m, null, r);
    return r;
  }

  // 'PRECEDING' | 'FOLLOWING'
  private static boolean window_frame_extent_2_3_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_frame_extent_2_3_2")) return false;
    boolean r;
    r = consumeToken(b, "PRECEDING");
    if (!r) r = consumeToken(b, "FOLLOWING");
    return r;
  }

  /* ********************************************************** */
  // window-function-name '(' window-function-arguments ')'
  //                     window-function-options? over-clause
  public static boolean window_function(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_function")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, WINDOW_FUNCTION, "<window function>");
    r = window_function_name(b, l + 1);
    r = r && consumeToken(b, "(");
    r = r && window_function_arguments(b, l + 1);
    r = r && consumeToken(b, ")");
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
  // ( expr ( ',' expr ( ',' expr )? )? )?
  public static boolean window_function_arguments(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_function_arguments")) return false;
    Marker m = enter_section_(b, l, _NONE_, WINDOW_FUNCTION_ARGUMENTS, "<window function arguments>");
    window_function_arguments_0(b, l + 1);
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  // expr ( ',' expr ( ',' expr )? )?
  private static boolean window_function_arguments_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_function_arguments_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = expr(b, l + 1);
    r = r && window_function_arguments_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( ',' expr ( ',' expr )? )?
  private static boolean window_function_arguments_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_function_arguments_0_1")) return false;
    window_function_arguments_0_1_0(b, l + 1);
    return true;
  }

  // ',' expr ( ',' expr )?
  private static boolean window_function_arguments_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_function_arguments_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ",");
    r = r && expr(b, l + 1);
    r = r && window_function_arguments_0_1_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( ',' expr )?
  private static boolean window_function_arguments_0_1_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_function_arguments_0_1_0_2")) return false;
    window_function_arguments_0_1_0_2_0(b, l + 1);
    return true;
  }

  // ',' expr
  private static boolean window_function_arguments_0_1_0_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_function_arguments_0_1_0_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ",");
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // identifier
  public static boolean window_function_name(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_function_name")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, WINDOW_FUNCTION_NAME, "<window function name>");
    r = identifier(b, l + 1);
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
  // identifier
  public static boolean window_name(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_name")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, WINDOW_NAME, "<window name>");
    r = identifier(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'ORDER' 'BY' ordering-term ( ',' ordering-term )*
  public static boolean window_order_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_order_clause")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, WINDOW_ORDER_CLAUSE, "<window order clause>");
    r = consumeToken(b, "ORDER");
    r = r && consumeToken(b, "BY");
    r = r && ordering_term(b, l + 1);
    r = r && window_order_clause_3(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( ',' ordering-term )*
  private static boolean window_order_clause_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_order_clause_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!window_order_clause_3_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "window_order_clause_3", c)) break;
    }
    return true;
  }

  // ',' ordering-term
  private static boolean window_order_clause_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_order_clause_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ",");
    r = r && ordering_term(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // 'PARTITION' 'BY' expr ( ',' expr )*
  public static boolean window_partition_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_partition_clause")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, WINDOW_PARTITION_CLAUSE, "<window partition clause>");
    r = consumeToken(b, "PARTITION");
    r = r && consumeToken(b, "BY");
    r = r && expr(b, l + 1);
    r = r && window_partition_clause_3(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( ',' expr )*
  private static boolean window_partition_clause_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_partition_clause_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!window_partition_clause_3_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "window_partition_clause_3", c)) break;
    }
    return true;
  }

  // ',' expr
  private static boolean window_partition_clause_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_partition_clause_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ",");
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // identifier
  public static boolean window_ref(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "window_ref")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, WINDOW_REF, "<window ref>");
    r = identifier(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'WITH' alias 'AS' '(' ( select | expr ) ')'
  //                  ( ',' alias 'AS' '(' ( select | expr ) ')' )*
  public static boolean with_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "with_clause")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, WITH_CLAUSE, "<with clause>");
    r = consumeToken(b, "WITH");
    r = r && alias(b, l + 1);
    r = r && consumeToken(b, "AS");
    r = r && consumeToken(b, "(");
    r = r && with_clause_4(b, l + 1);
    r = r && consumeToken(b, ")");
    r = r && with_clause_6(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // select | expr
  private static boolean with_clause_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "with_clause_4")) return false;
    boolean r;
    r = select(b, l + 1);
    if (!r) r = expr(b, l + 1);
    return r;
  }

  // ( ',' alias 'AS' '(' ( select | expr ) ')' )*
  private static boolean with_clause_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "with_clause_6")) return false;
    while (true) {
      int c = current_position_(b);
      if (!with_clause_6_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "with_clause_6", c)) break;
    }
    return true;
  }

  // ',' alias 'AS' '(' ( select | expr ) ')'
  private static boolean with_clause_6_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "with_clause_6_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ",");
    r = r && alias(b, l + 1);
    r = r && consumeToken(b, "AS");
    r = r && consumeToken(b, "(");
    r = r && with_clause_6_0_4(b, l + 1);
    r = r && consumeToken(b, ")");
    exit_section_(b, m, null, r);
    return r;
  }

  // select | expr
  private static boolean with_clause_6_0_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "with_clause_6_0_4")) return false;
    boolean r;
    r = select(b, l + 1);
    if (!r) r = expr(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // search-expr 'NOT'? 'WITHIN' target-expr
  public static boolean within_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "within_expr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, WITHIN_EXPR, "<within expr>");
    r = search_expr(b, l + 1);
    r = r && within_expr_1(b, l + 1);
    r = r && consumeToken(b, "WITHIN");
    r = r && target_expr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // 'NOT'?
  private static boolean within_expr_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "within_expr_1")) return false;
    consumeToken(b, "NOT");
    return true;
  }

}

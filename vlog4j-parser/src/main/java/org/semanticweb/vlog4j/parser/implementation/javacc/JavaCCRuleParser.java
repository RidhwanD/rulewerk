/* Generated By:JavaCC: Do not edit this line. JavaCCRuleParser.java */
package org.semanticweb.vlog4j.parser.implementation.javacc;

import java.util.List;
import java.util.ArrayList;

import org.semanticweb.vlog4j.parser.implementation.RuleParserBase;
import org.semanticweb.vlog4j.parser.implementation.PrologueException;

import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Literal;
import org.semanticweb.vlog4j.core.model.api.NegativeLiteral;
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.model.api.Constant;

import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makePositiveLiteral;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeNegativeLiteral;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makePositiveConjunction;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeConjunction;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeRule;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeVariable;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeConstant;


public class JavaCCRuleParser extends RuleParserBase implements JavaCCRuleParserConstants {

  final public void parse() throws ParseException, PrologueException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case BASE:
      base();
      break;
    default:
      jj_la1[0] = jj_gen;
      ;
    }
    label_1:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case PREFIX:
        ;
        break;
      default:
        jj_la1[1] = jj_gen;
        break label_1;
      }
      prefix();
    }
    label_2:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case IRI:
      case PNAME_NS:
      case PNAME_LN:
      case VARORPREDNAME:
        ;
        break;
      default:
        jj_la1[2] = jj_gen;
        break label_2;
      }
      statement();
    }
    jj_consume_token(0);
  }

  final public void base() throws ParseException, PrologueException {
    String iriString;
    jj_consume_token(BASE);
    iriString = IRIREF();
    jj_consume_token(DOT);
        localPrologue.setBase(iriString);
  }

  final public void prefix() throws ParseException, PrologueException {
    Token t;
    String iriString;
    jj_consume_token(PREFIX);
    t = jj_consume_token(PNAME_NS);
    iriString = IRIREF();
    jj_consume_token(DOT);
         //note that prefix includes the colon (:)
         localPrologue.setPrefix(t.image, iriString);
  }

  final public void statement() throws ParseException, PrologueException {
    Rule r;
    PositiveLiteral l;
    if (jj_2_1(2147483647)) {
      r = rule();
                                   listOfRules.add(r);
    } else {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case IRI:
      case PNAME_NS:
      case PNAME_LN:
      case VARORPREDNAME:
        l = positiveLiteral();
        jj_consume_token(DOT);
         if (l.getVariables().isEmpty())
             listOfFacts.add(l);
         else
             listOfQueries.add(l);
        break;
      default:
        jj_la1[3] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
  }

  final public Rule rule() throws ParseException, PrologueException {
    List < PositiveLiteral > head;
    List < Literal > body;
    head = listOfPositiveLiterals();
    jj_consume_token(ARROW);
    body = listOfLiterals();
    jj_consume_token(DOT);
      {if (true) return makeRule(makePositiveConjunction(head), makeConjunction(body));}
    throw new Error("Missing return statement in function");
  }

  final public List < PositiveLiteral > listOfPositiveLiterals() throws ParseException, PrologueException {
    PositiveLiteral l;
    List < PositiveLiteral > list = new ArrayList < PositiveLiteral > ();
    l = positiveLiteral();
                                        list.add(l);
    label_3:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case COMMA:
        ;
        break;
      default:
        jj_la1[4] = jj_gen;
        break label_3;
      }
      jj_consume_token(COMMA);
      l = positiveLiteral();
                                        list.add(l);
    }
      {if (true) return list;}
    throw new Error("Missing return statement in function");
  }

  final public List < Literal > listOfLiterals() throws ParseException, PrologueException {
    Literal l;
    List < Literal > list = new ArrayList < Literal > ();
    l = literal();
                                list.add(l);
    label_4:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case COMMA:
        ;
        break;
      default:
        jj_la1[5] = jj_gen;
        break label_4;
      }
      jj_consume_token(COMMA);
      l = literal();
                                list.add(l);
    }
      {if (true) return list;}
    throw new Error("Missing return statement in function");
  }

  final public Literal literal() throws ParseException, PrologueException {
    Literal l = null;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case IRI:
    case PNAME_NS:
    case PNAME_LN:
    case VARORPREDNAME:
      l = positiveLiteral();
                            {if (true) return l;}
      break;
    case TILDE:
      l = negativeLiteral();
                            {if (true) return l;}
      break;
    default:
      jj_la1[6] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  final public PositiveLiteral positiveLiteral() throws ParseException, PrologueException {
    Token t;
    List < Term > terms;
    String predicateName;
    predicateName = predicateName();
    jj_consume_token(LPAREN);
    terms = listOfTerms();
    jj_consume_token(RPAREN);
      {if (true) return makePositiveLiteral(predicateName, terms);}
    throw new Error("Missing return statement in function");
  }

  final public NegativeLiteral negativeLiteral() throws ParseException, PrologueException {
    List < Term > terms;
    String predicateName;
    jj_consume_token(TILDE);
    predicateName = predicateName();
    jj_consume_token(LPAREN);
    terms = listOfTerms();
    jj_consume_token(RPAREN);
      {if (true) return makeNegativeLiteral(predicateName, terms);}
    throw new Error("Missing return statement in function");
  }

  final public List < Term > listOfTerms() throws ParseException, PrologueException {
    Term t;
    List < Term > list = new ArrayList < Term > ();
    t = term();
                             list.add(t);
    label_5:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case COMMA:
        ;
        break;
      default:
        jj_la1[7] = jj_gen;
        break label_5;
      }
      jj_consume_token(COMMA);
      t = term();
                             list.add(t);
    }
      {if (true) return list;}
    throw new Error("Missing return statement in function");
  }

  final public String predicateName() throws ParseException, PrologueException {
    String s;
    Token t;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case IRI:
    case PNAME_NS:
    case PNAME_LN:
      s = IRI();
                {if (true) return s;}
      break;
    case VARORPREDNAME:
      t = jj_consume_token(VARORPREDNAME);
                            {if (true) return t.image;}
      break;
    default:
      jj_la1[8] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  final public Term term() throws ParseException, PrologueException {
    String s;
    Token t;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case IRI:
    case PNAME_NS:
    case PNAME_LN:
      s = IRI();
                       {if (true) return makeConstant(s);}
      break;
    case STRING_LITERAL1:
    case STRING_LITERAL2:
    case STRING_LITERAL_LONG1:
    case STRING_LITERAL_LONG2:
      s = RDFLiteral();
                       {if (true) return makeConstant(s);}
      break;
    case VAR:
      t = jj_consume_token(VAR);
                       {if (true) return makeVariable(t.image.substring(1));}
      break;
    default:
      jj_la1[9] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

/** [16] */
  final public Constant NumericLiteral() throws ParseException {
    Token t;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case INTEGER:
      t = jj_consume_token(INTEGER);
                      {if (true) return createLiteralInteger(t.image);}
      break;
    case DECIMAL:
      t = jj_consume_token(DECIMAL);
                      {if (true) return createLiteralDecimal(t.image);}
      break;
    case DOUBLE:
      t = jj_consume_token(DOUBLE);
                       {if (true) return createLiteralDouble(t.image);}
      break;
    default:
      jj_la1[10] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  final public String RDFLiteral() throws ParseException, PrologueException {
    Token t;
    String lex = null;
    String lang = null;   // Optional lang tag and datatype.
    String dt = null;
    lex = String();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case LANGTAG:
    case DATATYPE:
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case LANGTAG:
        lang = Langtag();
        break;
      case DATATYPE:
        jj_consume_token(DATATYPE);
        dt = IRI();
        break;
      default:
        jj_la1[11] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      break;
    default:
      jj_la1[12] = jj_gen;
      ;
    }
      {if (true) return strRDFLiteral(lex, lang, dt);}
    throw new Error("Missing return statement in function");
  }

  final public String Langtag() throws ParseException {
    Token t;
    t = jj_consume_token(LANGTAG);
        String lang = stripChars(t.image, 1);
        {if (true) return lang;}
    throw new Error("Missing return statement in function");
  }

  final public String BooleanLiteral() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case TRUE:
      jj_consume_token(TRUE);
                 {if (true) return "true^^http://www.w3.org/2001/XMLSchema#boolean";}
      break;
    case FALSE:
      jj_consume_token(FALSE);
                 {if (true) return "false^^http://www.w3.org/2001/XMLSchema#boolean";}
      break;
    default:
      jj_la1[13] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  final public String String() throws ParseException {
    Token t;
    String lex;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case STRING_LITERAL1:
      t = jj_consume_token(STRING_LITERAL1);
                                        lex = stripQuotes(t.image);
      break;
    case STRING_LITERAL2:
      t = jj_consume_token(STRING_LITERAL2);
                                        lex = stripQuotes(t.image);
      break;
    case STRING_LITERAL_LONG1:
      t = jj_consume_token(STRING_LITERAL_LONG1);
                                        lex = stripQuotes3(t.image);
      break;
    case STRING_LITERAL_LONG2:
      t = jj_consume_token(STRING_LITERAL_LONG2);
                                        lex = stripQuotes3(t.image);
      break;
    default:
      jj_la1[14] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
        lex = unescapeStr(lex, t.beginLine, t.beginColumn);
        {if (true) return lex;}
    throw new Error("Missing return statement in function");
  }

  final public String IRI() throws ParseException, PrologueException {
    String iri;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case IRI:
      iri = IRIREF();
      break;
    case PNAME_NS:
    case PNAME_LN:
      iri = PrefixedName();
      break;
    default:
      jj_la1[15] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
      {if (true) return "<"+iri+">";}
    throw new Error("Missing return statement in function");
  }

  final public String PrefixedName() throws ParseException, PrologueException {
    Token t;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case PNAME_LN:
      t = jj_consume_token(PNAME_LN);
      break;
    case PNAME_NS:
      t = jj_consume_token(PNAME_NS);
      break;
    default:
      jj_la1[16] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
      {if (true) return localPrologue.resolvePName(t.image);}
    throw new Error("Missing return statement in function");
  }

  final public String IRIREF() throws ParseException {
    Token t;
    t = jj_consume_token(IRI);
        // we remove '<' and '>'
        {if (true) return t.image.substring(1,t.image.length()-1);}
    throw new Error("Missing return statement in function");
  }

  private boolean jj_2_1(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_1(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(0, xla); }
  }

  private boolean jj_3R_16() {
    if (jj_3R_21()) return true;
    return false;
  }

  private boolean jj_3R_11() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_15()) {
    jj_scanpos = xsp;
    if (jj_3R_16()) return true;
    }
    return false;
  }

  private boolean jj_3R_15() {
    if (jj_3R_9()) return true;
    return false;
  }

  private boolean jj_3R_37() {
    if (jj_3R_39()) return true;
    return false;
  }

  private boolean jj_3R_32() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_37()) {
    jj_scanpos = xsp;
    if (jj_3R_38()) return true;
    }
    return false;
  }

  private boolean jj_3R_30() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_scan_token(26)) {
    jj_scanpos = xsp;
    if (jj_scan_token(25)) return true;
    }
    return false;
  }

  private boolean jj_3R_18() {
    if (jj_scan_token(VARORPREDNAME)) return true;
    return false;
  }

  private boolean jj_3R_13() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_17()) {
    jj_scanpos = xsp;
    if (jj_3R_18()) return true;
    }
    return false;
  }

  private boolean jj_3R_17() {
    if (jj_3R_22()) return true;
    return false;
  }

  private boolean jj_3R_12() {
    if (jj_scan_token(COMMA)) return true;
    if (jj_3R_11()) return true;
    return false;
  }

  private boolean jj_3R_39() {
    if (jj_scan_token(LANGTAG)) return true;
    return false;
  }

  private boolean jj_3R_27() {
    if (jj_3R_30()) return true;
    return false;
  }

  private boolean jj_3R_26() {
    if (jj_3R_29()) return true;
    return false;
  }

  private boolean jj_3R_8() {
    if (jj_3R_11()) return true;
    Token xsp;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3R_12()) { jj_scanpos = xsp; break; }
    }
    return false;
  }

  private boolean jj_3R_20() {
    if (jj_scan_token(COMMA)) return true;
    if (jj_3R_19()) return true;
    return false;
  }

  private boolean jj_3R_22() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_26()) {
    jj_scanpos = xsp;
    if (jj_3R_27()) return true;
    }
    return false;
  }

  private boolean jj_3R_14() {
    if (jj_3R_19()) return true;
    Token xsp;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3R_20()) { jj_scanpos = xsp; break; }
    }
    return false;
  }

  private boolean jj_3R_28() {
    if (jj_3R_31()) return true;
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_32()) jj_scanpos = xsp;
    return false;
  }

  private boolean jj_3R_10() {
    if (jj_scan_token(COMMA)) return true;
    if (jj_3R_9()) return true;
    return false;
  }

  private boolean jj_3R_7() {
    if (jj_3R_9()) return true;
    Token xsp;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3R_10()) { jj_scanpos = xsp; break; }
    }
    return false;
  }

  private boolean jj_3R_36() {
    if (jj_scan_token(STRING_LITERAL_LONG2)) return true;
    return false;
  }

  private boolean jj_3R_35() {
    if (jj_scan_token(STRING_LITERAL_LONG1)) return true;
    return false;
  }

  private boolean jj_3R_34() {
    if (jj_scan_token(STRING_LITERAL2)) return true;
    return false;
  }

  private boolean jj_3R_33() {
    if (jj_scan_token(STRING_LITERAL1)) return true;
    return false;
  }

  private boolean jj_3R_21() {
    if (jj_scan_token(TILDE)) return true;
    if (jj_3R_13()) return true;
    if (jj_scan_token(LPAREN)) return true;
    if (jj_3R_14()) return true;
    if (jj_scan_token(RPAREN)) return true;
    return false;
  }

  private boolean jj_3R_38() {
    if (jj_scan_token(DATATYPE)) return true;
    if (jj_3R_22()) return true;
    return false;
  }

  private boolean jj_3R_6() {
    if (jj_3R_7()) return true;
    if (jj_scan_token(ARROW)) return true;
    if (jj_3R_8()) return true;
    if (jj_scan_token(DOT)) return true;
    return false;
  }

  private boolean jj_3R_31() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_33()) {
    jj_scanpos = xsp;
    if (jj_3R_34()) {
    jj_scanpos = xsp;
    if (jj_3R_35()) {
    jj_scanpos = xsp;
    if (jj_3R_36()) return true;
    }
    }
    }
    return false;
  }

  private boolean jj_3R_9() {
    if (jj_3R_13()) return true;
    if (jj_scan_token(LPAREN)) return true;
    if (jj_3R_14()) return true;
    if (jj_scan_token(RPAREN)) return true;
    return false;
  }

  private boolean jj_3R_29() {
    if (jj_scan_token(IRI)) return true;
    return false;
  }

  private boolean jj_3_1() {
    if (jj_3R_6()) return true;
    return false;
  }

  private boolean jj_3R_25() {
    if (jj_scan_token(VAR)) return true;
    return false;
  }

  private boolean jj_3R_24() {
    if (jj_3R_28()) return true;
    return false;
  }

  private boolean jj_3R_23() {
    if (jj_3R_22()) return true;
    return false;
  }

  private boolean jj_3R_19() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_23()) {
    jj_scanpos = xsp;
    if (jj_3R_24()) {
    jj_scanpos = xsp;
    if (jj_3R_25()) return true;
    }
    }
    return false;
  }

  /** Generated Token Manager. */
  public JavaCCRuleParserTokenManager token_source;
  SimpleCharStream jj_input_stream;
  /** Current token. */
  public Token token;
  /** Next token. */
  public Token jj_nt;
  private int jj_ntk;
  private Token jj_scanpos, jj_lastpos;
  private int jj_la;
  private int jj_gen;
  final private int[] jj_la1 = new int[17];
  static private int[] jj_la1_0;
  static private int[] jj_la1_1;
  static {
      jj_la1_init_0();
      jj_la1_init_1();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0x200,0x100,0x7000000,0x7000000,0x0,0x0,0x7000000,0x0,0x7000000,0x17780000,0x7000,0x20000000,0x20000000,0xc00,0x780000,0x7000000,0x6000000,};
   }
   private static void jj_la1_init_1() {
      jj_la1_1 = new int[] {0x0,0x0,0x10000000,0x10000000,0x200,0x200,0x10008000,0x200,0x10000000,0x0,0x0,0x200000,0x200000,0x0,0x0,0x0,0x0,};
   }
  final private JJCalls[] jj_2_rtns = new JJCalls[1];
  private boolean jj_rescan = false;
  private int jj_gc = 0;

  /** Constructor with InputStream. */
  public JavaCCRuleParser(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public JavaCCRuleParser(java.io.InputStream stream, String encoding) {
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new JavaCCRuleParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 17; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 17; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor. */
  public JavaCCRuleParser(java.io.Reader stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new JavaCCRuleParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 17; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 17; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor with generated Token Manager. */
  public JavaCCRuleParser(JavaCCRuleParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 17; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(JavaCCRuleParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 17; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      if (++jj_gc > 100) {
        jj_gc = 0;
        for (int i = 0; i < jj_2_rtns.length; i++) {
          JJCalls c = jj_2_rtns[i];
          while (c != null) {
            if (c.gen < jj_gen) c.first = null;
            c = c.next;
          }
        }
      }
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }

  static private final class LookaheadSuccess extends java.lang.Error { }
  final private LookaheadSuccess jj_ls = new LookaheadSuccess();
  private boolean jj_scan_token(int kind) {
    if (jj_scanpos == jj_lastpos) {
      jj_la--;
      if (jj_scanpos.next == null) {
        jj_lastpos = jj_scanpos = jj_scanpos.next = token_source.getNextToken();
      } else {
        jj_lastpos = jj_scanpos = jj_scanpos.next;
      }
    } else {
      jj_scanpos = jj_scanpos.next;
    }
    if (jj_rescan) {
      int i = 0; Token tok = token;
      while (tok != null && tok != jj_scanpos) { i++; tok = tok.next; }
      if (tok != null) jj_add_error_token(kind, i);
    }
    if (jj_scanpos.kind != kind) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) throw jj_ls;
    return false;
  }


/** Get the next Token. */
  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

/** Get the specific Token. */
  final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
  private int[] jj_expentry;
  private int jj_kind = -1;
  private int[] jj_lasttokens = new int[100];
  private int jj_endpos;

  private void jj_add_error_token(int kind, int pos) {
    if (pos >= 100) return;
    if (pos == jj_endpos + 1) {
      jj_lasttokens[jj_endpos++] = kind;
    } else if (jj_endpos != 0) {
      jj_expentry = new int[jj_endpos];
      for (int i = 0; i < jj_endpos; i++) {
        jj_expentry[i] = jj_lasttokens[i];
      }
      jj_entries_loop: for (java.util.Iterator<?> it = jj_expentries.iterator(); it.hasNext();) {
        int[] oldentry = (int[])(it.next());
        if (oldentry.length == jj_expentry.length) {
          for (int i = 0; i < jj_expentry.length; i++) {
            if (oldentry[i] != jj_expentry[i]) {
              continue jj_entries_loop;
            }
          }
          jj_expentries.add(jj_expentry);
          break jj_entries_loop;
        }
      }
      if (pos != 0) jj_lasttokens[(jj_endpos = pos) - 1] = kind;
    }
  }

  /** Generate ParseException. */
  public ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[63];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 17; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
          if ((jj_la1_1[i] & (1<<j)) != 0) {
            la1tokens[32+j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 63; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.add(jj_expentry);
      }
    }
    jj_endpos = 0;
    jj_rescan_token();
    jj_add_error_token(0, 0);
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = jj_expentries.get(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  /** Enable tracing. */
  final public void enable_tracing() {
  }

  /** Disable tracing. */
  final public void disable_tracing() {
  }

  private void jj_rescan_token() {
    jj_rescan = true;
    for (int i = 0; i < 1; i++) {
    try {
      JJCalls p = jj_2_rtns[i];
      do {
        if (p.gen > jj_gen) {
          jj_la = p.arg; jj_lastpos = jj_scanpos = p.first;
          switch (i) {
            case 0: jj_3_1(); break;
          }
        }
        p = p.next;
      } while (p != null);
      } catch(LookaheadSuccess ls) { }
    }
    jj_rescan = false;
  }

  private void jj_save(int index, int xla) {
    JJCalls p = jj_2_rtns[index];
    while (p.gen > jj_gen) {
      if (p.next == null) { p = p.next = new JJCalls(); break; }
      p = p.next;
    }
    p.gen = jj_gen + xla - jj_la; p.first = token; p.arg = xla;
  }

  static final class JJCalls {
    int gen;
    Token first;
    int arg;
    JJCalls next;
  }

}

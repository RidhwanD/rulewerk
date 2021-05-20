/* JavaCCRPQParser.java */
/* Generated By:JavaCC: Do not edit this line. JavaCCRPQParser.java */
package org.semanticweb.rulewerk.rpq.parser.javacc;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.ArrayDeque;
import java.util.Deque;

import org.semanticweb.rulewerk.rpq.parser.ParsingException;
import org.semanticweb.rulewerk.rpq.parser.javacc.JavaCCRPQParserBase;
import org.semanticweb.rulewerk.core.exceptions.PrefixDeclarationException;

import org.semanticweb.rulewerk.rpq.model.api.RegExpression;
import org.semanticweb.rulewerk.rpq.model.api.AlternRegExpression;
import org.semanticweb.rulewerk.rpq.model.api.ConcatRegExpression;
import org.semanticweb.rulewerk.rpq.model.api.KStarRegExpression;
import org.semanticweb.rulewerk.rpq.model.api.KPlusRegExpression;
import org.semanticweb.rulewerk.rpq.model.api.EdgeLabel;
import org.semanticweb.rulewerk.rpq.model.api.ConverseEdgeLabel;
import org.semanticweb.rulewerk.rpq.model.api.RegPathQuery;
import org.semanticweb.rulewerk.rpq.model.api.RPQConjunction;
import org.semanticweb.rulewerk.core.model.api.Statement;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.NegativeLiteral;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.api.Constant;
import org.semanticweb.rulewerk.core.model.api.DataSource;
import org.semanticweb.rulewerk.core.model.api.PrefixDeclarationRegistry;
import org.semanticweb.rulewerk.core.model.api.Argument;
import org.semanticweb.rulewerk.core.model.api.Command;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;

import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.rpq.model.implementation.RPQExpressions;

public class JavaCCRPQParser extends JavaCCRPQParserBase implements JavaCCRPQParserConstants {
        private SubParserFactory getSubParserFactory() {
                return new SubParserFactory(this);
        }

        public void ensureEndOfInput() throws ParseException {
                jj_consume_token(EOF);
        }

  final public void parse() throws ParseException, PrefixDeclarationException {
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case BASE:{
      base();
      break;
      }
    default:
      jj_la1[0] = jj_gen;
      ;
    }
    label_1:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case PREFIX:{
        ;
        break;
        }
      default:
        jj_la1[1] = jj_gen;
        break label_1;
      }
      prefix();
    }
    query();
    jj_consume_token(0);
}

  final public void base() throws ParseException, PrefixDeclarationException {Token iri;
    jj_consume_token(BASE);
    iri = jj_consume_token(IRI_ABSOLUTE);
    jj_consume_token(DOT);
setBase(iri.image);
}

  final public void prefix() throws ParseException, PrefixDeclarationException {Token pn;
        String iri;
    jj_consume_token(PREFIX);
    pn = jj_consume_token(PNAME_NS);
    iri = absoluteIri();
    jj_consume_token(DOT);
setPrefix(pn.image, iri);
}

  final public String absoluteIri() throws ParseException, PrefixDeclarationException {Token iri;
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case IRI_ABSOLUTE:{
      iri = jj_consume_token(IRI_ABSOLUTE);
{if ("" != null) return absolutizeIri(iri.image);}
      break;
      }
    case PNAME_LN:{
      iri = jj_consume_token(PNAME_LN);
{if ("" != null) return resolvePrefixedName(iri.image);}
      break;
      }
    default:
      jj_la1[2] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
}

  final public RPQConjunction<RegPathQuery> query() throws ParseException, PrefixDeclarationException {List<RegPathQuery> rpqs;
        List<Term> vars;
    if (jj_2_1(2147483647)) {
      jj_consume_token(SELECT);
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case DISTINCT:{
        jj_consume_token(DISTINCT);
        break;
        }
      default:
        jj_la1[3] = jj_gen;
        ;
      }
      vars = listOfTerms();
      jj_consume_token(WHERE);
      jj_consume_token(LBRACE);
      rpqs = listOfRegPathQuery();
      jj_consume_token(RBRACE);
setRPQs(rpqs);
        setProjVar(vars);
        {if ("" != null) return RPQExpressions.makeRPQConjunction(rpqs, vars);}
    } else if (jj_2_2(2147483647)) {
      jj_consume_token(ASK);
      jj_consume_token(LBRACE);
      rpqs = listOfRegPathQuery();
      jj_consume_token(RBRACE);
vars = new ArrayList< Term >();
        setRPQs(rpqs);
        setProjVar(vars);
        {if ("" != null) return RPQExpressions.makeRPQConjunction(rpqs, vars);}
    } else {
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
}

  final public RegPathQuery regPathQuery() throws ParseException, PrefixDeclarationException {RegExpression rx;
        Term t1;
        Term t2;
    t1 = term();
    rx = regExpression();
    t2 = term();
{if ("" != null) return RPQExpressions.makeRegPathQuery(rx, t1, t2);}
    throw new Error("Missing return statement in function");
}

  final public List < RegPathQuery > listOfRegPathQuery() throws ParseException, PrefixDeclarationException {RegPathQuery rpq;
    List < RegPathQuery > list = new ArrayList < RegPathQuery > ();
    jj_consume_token(LBRACE);
    rpq = regPathQuery();
    jj_consume_token(DOT);
list.add(rpq);
    label_2:
    while (true) {
      if (jj_2_3(1)) {
        ;
      } else {
        break label_2;
      }
      rpq = regPathQuery();
      jj_consume_token(DOT);
list.add(rpq);
    }
    jj_consume_token(RBRACE);
{if ("" != null) return list;}
    throw new Error("Missing return statement in function");
}

  final public RegExpression regExpression() throws ParseException, PrefixDeclarationException {RegExpression rx;
    if (jj_2_4(2147483647)) {
      rx = edgeLabel();
{if ("" != null) return rx;}
    } else if (jj_2_5(2147483647)) {
      rx = alternRegExpression();
{if ("" != null) return rx;}
    } else if (jj_2_6(2147483647)) {
      rx = concatRegExpression();
{if ("" != null) return rx;}
    } else if (jj_2_7(2147483647)) {
      rx = kStarRegExpression();
{if ("" != null) return rx;}
    } else if (jj_2_8(2147483647)) {
      rx = kPlusRegExpression();
{if ("" != null) return rx;}
    } else if (jj_2_9(2147483647)) {
      rx = converseEdgeLabel();
{if ("" != null) return rx;}
    } else {
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
}

  final public AlternRegExpression alternRegExpression() throws ParseException, PrefixDeclarationException {RegExpression rx1;
        RegExpression rx2;
    jj_consume_token(LPAREN);
    rx1 = regExpression();
    jj_consume_token(ALTERN);
    rx2 = regExpression();
    jj_consume_token(RPAREN);
{if ("" != null) return RPQExpressions.makeAlternRegExpression(rx1, rx2);}
    throw new Error("Missing return statement in function");
}

  final public ConcatRegExpression concatRegExpression() throws ParseException, PrefixDeclarationException {RegExpression rx1;
        RegExpression rx2;
    jj_consume_token(LPAREN);
    rx1 = regExpression();
    jj_consume_token(CONCAT);
    rx2 = regExpression();
    jj_consume_token(RPAREN);
{if ("" != null) return RPQExpressions.makeConcatRegExpression(rx1, rx2);}
    throw new Error("Missing return statement in function");
}

  final public KStarRegExpression kStarRegExpression() throws ParseException, PrefixDeclarationException {RegExpression rx;
    jj_consume_token(LPAREN);
    rx = regExpression();
    jj_consume_token(KSTAR);
    jj_consume_token(RPAREN);
{if ("" != null) return RPQExpressions.makeKStarRegExpression(rx);}
    throw new Error("Missing return statement in function");
}

  final public KPlusRegExpression kPlusRegExpression() throws ParseException, PrefixDeclarationException {RegExpression rx;
    jj_consume_token(LPAREN);
    rx = regExpression();
    jj_consume_token(KPLUS);
    jj_consume_token(RPAREN);
{if ("" != null) return RPQExpressions.makeKPlusRegExpression(rx);}
    throw new Error("Missing return statement in function");
}

  final public ConverseEdgeLabel converseEdgeLabel() throws ParseException, PrefixDeclarationException {EdgeLabel el;
    jj_consume_token(LPAREN);
    jj_consume_token(CONVEL);
    el = edgeLabel();
    jj_consume_token(RPAREN);
{if ("" != null) return RPQExpressions.makeConverseEdgeLabel(el);}
    throw new Error("Missing return statement in function");
}

  final public EdgeLabel edgeLabel() throws ParseException, PrefixDeclarationException {String s;
    s = absoluteIri();
{if ("" != null) return RPQExpressions.makeEdgeLabel(s);}
    throw new Error("Missing return statement in function");
}

  final public List < Term > listOfTerms() throws ParseException, PrefixDeclarationException {Term t;
    List < Term > list = new ArrayList < Term > ();
    t = term();
list.add(t);
    label_3:
    while (true) {
      if (jj_2_10(1)) {
        ;
      } else {
        break label_3;
      }
      t = term();
list.add(t);
    }
{if ("" != null) return list;}
    throw new Error("Missing return statement in function");
}

  final public Term term() throws ParseException, PrefixDeclarationException {Token t;
    String s;
    Constant c;
        Term tt;
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case PNAME_LN:
    case IRI_ABSOLUTE:{
      s = absoluteIri();
{if ("" != null) return createConstant(s);}
      break;
      }
    case VARORPREDNAME:{
      t = jj_consume_token(VARORPREDNAME);
{if ("" != null) return createConstant(t.image);}
      break;
      }
    case INTEGER:
    case DECIMAL:
    case DOUBLE:{
      c = NumericLiteral();
{if ("" != null) return c;}
      break;
      }
    case SINGLE_QUOTED_STRING:
    case DOUBLE_QUOTED_STRING:
    case TRIPLE_QUOTED_STRING:
    case SIXFOLD_QUOTED_STRING:{
      c = RDFLiteral();
{if ("" != null) return c;}
      break;
      }
    case UNIVAR:{
      t = jj_consume_token(UNIVAR);
s = t.image.substring(1);
        {if ("" != null) return createUniversalVariable(s);}
      break;
      }
    default:
      jj_la1[4] = jj_gen;
      if (jj_2_11(1)) {
        try {
          tt = ConfigurableLiteral();
{if ("" != null) return tt;}
        } catch (ParsingException e) {
{if (true) throw makeParseExceptionWithCause("Invalid configurable literal expression", e);}
        }
      } else {
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
    throw new Error("Missing return statement in function");
}

  final public Constant NumericLiteral() throws ParseException {Token t;
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case INTEGER:{
      t = jj_consume_token(INTEGER);
{if ("" != null) return createConstant(t.image, PrefixDeclarationRegistry.XSD_INTEGER);}
      break;
      }
    case DECIMAL:{
      t = jj_consume_token(DECIMAL);
{if ("" != null) return createConstant(t.image, PrefixDeclarationRegistry.XSD_DECIMAL);}
      break;
      }
    case DOUBLE:{
      t = jj_consume_token(DOUBLE);
{if ("" != null) return createConstant(t.image, PrefixDeclarationRegistry.XSD_DOUBLE);}
      break;
      }
    default:
      jj_la1[5] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
}

  final public Constant RDFLiteral() throws ParseException, PrefixDeclarationException {String lex;
    Token lang = null;   // Optional lang tag and datatype.
    String dt = null;
    lex = String();
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case LANGTAG:
    case DATATYPE:{
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case LANGTAG:{
        lang = jj_consume_token(LANGTAG);
        break;
        }
      case DATATYPE:{
        jj_consume_token(DATATYPE);
        dt = absoluteIri();
        break;
        }
      default:
        jj_la1[6] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      break;
      }
    default:
      jj_la1[7] = jj_gen;
      ;
    }
if (lang != null) {
                        {if ("" != null) return createLanguageStringConstant(lex, lang.image);}
                }
                {if ("" != null) return createConstant(lex, dt);}
    throw new Error("Missing return statement in function");
}

  final public Term ConfigurableLiteral() throws ParseException, ParsingException {String s;
        Token t;
    if (jj_2_12(2147483647) && (isConfigurableLiteralRegistered(ConfigurableLiteralDelimiter.PIPE))) {
      t = jj_consume_token(PIPE_DELIMITED_LITERAL);
{if ("" != null) return parseConfigurableLiteral(ConfigurableLiteralDelimiter.PIPE, t.image, getSubParserFactory());}
    } else if (jj_2_13(2147483647) && (isConfigurableLiteralRegistered(ConfigurableLiteralDelimiter.HASH))) {
      t = jj_consume_token(HASH_DELIMITED_LITERAL);
{if ("" != null) return parseConfigurableLiteral(ConfigurableLiteralDelimiter.HASH, t.image, getSubParserFactory());}
    } else {
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
}

  final public String String() throws ParseException {Token t;
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case SINGLE_QUOTED_STRING:{
      t = jj_consume_token(SINGLE_QUOTED_STRING);
      break;
      }
    case DOUBLE_QUOTED_STRING:{
      t = jj_consume_token(DOUBLE_QUOTED_STRING);
      break;
      }
    case TRIPLE_QUOTED_STRING:{
      t = jj_consume_token(TRIPLE_QUOTED_STRING);
      break;
      }
    case SIXFOLD_QUOTED_STRING:{
      t = jj_consume_token(SIXFOLD_QUOTED_STRING);
      break;
      }
    default:
      jj_la1[8] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
{if ("" != null) return unescapeStr(t.image, t.beginLine, t.beginColumn);}
    throw new Error("Missing return statement in function");
}

  final public String PrefixedName() throws ParseException, PrefixDeclarationException {Token t;
    t = jj_consume_token(PNAME_LN);
{if ("" != null) return resolvePrefixedName(t.image);}
    throw new Error("Missing return statement in function");
}

  private boolean jj_2_1(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return (!jj_3_1()); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(0, xla); }
  }

  private boolean jj_2_2(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return (!jj_3_2()); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(1, xla); }
  }

  private boolean jj_2_3(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return (!jj_3_3()); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(2, xla); }
  }

  private boolean jj_2_4(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return (!jj_3_4()); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(3, xla); }
  }

  private boolean jj_2_5(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return (!jj_3_5()); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(4, xla); }
  }

  private boolean jj_2_6(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return (!jj_3_6()); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(5, xla); }
  }

  private boolean jj_2_7(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return (!jj_3_7()); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(6, xla); }
  }

  private boolean jj_2_8(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return (!jj_3_8()); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(7, xla); }
  }

  private boolean jj_2_9(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return (!jj_3_9()); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(8, xla); }
  }

  private boolean jj_2_10(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return (!jj_3_10()); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(9, xla); }
  }

  private boolean jj_2_11(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return (!jj_3_11()); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(10, xla); }
  }

  private boolean jj_2_12(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return (!jj_3_12()); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(11, xla); }
  }

  private boolean jj_2_13(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return (!jj_3_13()); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(12, xla); }
  }

  private boolean jj_3R_regExpression_182_5_12()
 {
    if (jj_3R_edgeLabel_242_9_7()) return true;
    return false;
  }

  private boolean jj_3R_regExpression_181_5_6()
 {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_regExpression_182_5_12()) {
    jj_scanpos = xsp;
    if (jj_3R_regExpression_184_11_13()) {
    jj_scanpos = xsp;
    if (jj_3R_regExpression_186_11_14()) {
    jj_scanpos = xsp;
    if (jj_3R_regExpression_188_11_15()) {
    jj_scanpos = xsp;
    if (jj_3R_regExpression_190_11_16()) {
    jj_scanpos = xsp;
    if (jj_3R_regExpression_192_11_17()) return true;
    }
    }
    }
    }
    }
    return false;
  }

  private boolean jj_3_11()
 {
    if (jj_3R_ConfigurableLiteral_302_9_9()) return true;
    return false;
  }

  private boolean jj_3R_term_266_5_22()
 {
    if (jj_scan_token(UNIVAR)) return true;
    return false;
  }

  private boolean jj_3R_term_265_5_21()
 {
    if (jj_3R_RDFLiteral_290_5_31()) return true;
    return false;
  }

  private boolean jj_3R_term_264_5_20()
 {
    if (jj_3R_NumericLiteral_280_5_30()) return true;
    return false;
  }

  private boolean jj_3R_regPathQuery_164_9_4()
 {
    if (jj_3R_term_262_5_8()) return true;
    return false;
  }

  private boolean jj_3R_term_263_5_19()
 {
    if (jj_scan_token(VARORPREDNAME)) return true;
    return false;
  }

  private boolean jj_3R_term_262_5_8()
 {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_term_262_5_18()) {
    jj_scanpos = xsp;
    if (jj_3R_term_263_5_19()) {
    jj_scanpos = xsp;
    if (jj_3R_term_264_5_20()) {
    jj_scanpos = xsp;
    if (jj_3R_term_265_5_21()) {
    jj_scanpos = xsp;
    if (jj_3R_term_266_5_22()) {
    jj_scanpos = xsp;
    if (jj_3_11()) return true;
    }
    }
    }
    }
    }
    return false;
  }

  private boolean jj_3R_term_262_5_18()
 {
    if (jj_3R_absoluteIri_136_9_5()) return true;
    return false;
  }

  private boolean jj_3_2()
 {
    if (jj_scan_token(ASK)) return true;
    return false;
  }

  private boolean jj_3_1()
 {
    if (jj_scan_token(SELECT)) return true;
    return false;
  }

  private boolean jj_3R_edgeLabel_242_9_7()
 {
    if (jj_3R_absoluteIri_136_9_5()) return true;
    return false;
  }

  private boolean jj_3R_converseEdgeLabel_234_9_29()
 {
    if (jj_scan_token(LPAREN)) return true;
    if (jj_scan_token(CONVEL)) return true;
    if (jj_3R_edgeLabel_242_9_7()) return true;
    if (jj_scan_token(RPAREN)) return true;
    return false;
  }

  private boolean jj_3R_absoluteIri_136_9_5()
 {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_absoluteIri_136_9_10()) {
    jj_scanpos = xsp;
    if (jj_3R_absoluteIri_137_5_11()) return true;
    }
    return false;
  }

  private boolean jj_3R_absoluteIri_136_9_10()
 {
    if (jj_scan_token(IRI_ABSOLUTE)) return true;
    return false;
  }

  private boolean jj_3_3()
 {
    if (jj_3R_regPathQuery_164_9_4()) return true;
    return false;
  }

  private boolean jj_3R_absoluteIri_137_5_11()
 {
    if (jj_scan_token(PNAME_LN)) return true;
    return false;
  }

  private boolean jj_3R_kPlusRegExpression_226_9_28()
 {
    if (jj_scan_token(LPAREN)) return true;
    if (jj_3R_regExpression_181_5_6()) return true;
    if (jj_scan_token(KPLUS)) return true;
    if (jj_scan_token(RPAREN)) return true;
    return false;
  }

  private boolean jj_3_13()
 {
    if (jj_scan_token(HASH_DELIMITED_LITERAL)) return true;
    return false;
  }

  private boolean jj_3_12()
 {
    if (jj_scan_token(PIPE_DELIMITED_LITERAL)) return true;
    return false;
  }

  private boolean jj_3R_String_318_5_35()
 {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_scan_token(67)) {
    jj_scanpos = xsp;
    if (jj_scan_token(68)) {
    jj_scanpos = xsp;
    if (jj_scan_token(69)) {
    jj_scanpos = xsp;
    if (jj_scan_token(70)) return true;
    }
    }
    }
    return false;
  }

  private boolean jj_3R_kStarRegExpression_218_9_27()
 {
    if (jj_scan_token(LPAREN)) return true;
    if (jj_3R_regExpression_181_5_6()) return true;
    if (jj_scan_token(KSTAR)) return true;
    if (jj_scan_token(RPAREN)) return true;
    return false;
  }

  private boolean jj_3R_ConfigurableLiteral_307_12_24()
 {
    if (jj_scan_token(HASH_DELIMITED_LITERAL)) return true;
    return false;
  }

  private boolean jj_3R_concatRegExpression_210_9_26()
 {
    if (jj_scan_token(LPAREN)) return true;
    if (jj_3R_regExpression_181_5_6()) return true;
    if (jj_scan_token(CONCAT)) return true;
    if (jj_3R_regExpression_181_5_6()) return true;
    if (jj_scan_token(RPAREN)) return true;
    return false;
  }

  private boolean jj_3R_ConfigurableLiteral_302_11_23()
 {
    if (jj_scan_token(PIPE_DELIMITED_LITERAL)) return true;
    return false;
  }

  private boolean jj_3R_ConfigurableLiteral_302_9_9()
 {
    Token xsp;
    xsp = jj_scanpos;
    jj_lookingAhead = true;
    jj_semLA = isConfigurableLiteralRegistered(ConfigurableLiteralDelimiter.PIPE);
    jj_lookingAhead = false;
    if (!jj_semLA || jj_3R_ConfigurableLiteral_302_11_23()) {
    jj_scanpos = xsp;
    jj_lookingAhead = true;
    jj_semLA = isConfigurableLiteralRegistered(ConfigurableLiteralDelimiter.HASH);
    jj_lookingAhead = false;
    if (!jj_semLA || jj_3R_ConfigurableLiteral_307_12_24()) return true;
    }
    return false;
  }

  private boolean jj_3_9()
 {
    if (jj_scan_token(LPAREN)) return true;
    if (jj_scan_token(CONVEL)) return true;
    if (jj_3R_edgeLabel_242_9_7()) return true;
    return false;
  }

  private boolean jj_3_8()
 {
    if (jj_scan_token(LPAREN)) return true;
    if (jj_3R_regExpression_181_5_6()) return true;
    if (jj_scan_token(KPLUS)) return true;
    return false;
  }

  private boolean jj_3R_alternRegExpression_201_9_25()
 {
    if (jj_scan_token(LPAREN)) return true;
    if (jj_3R_regExpression_181_5_6()) return true;
    if (jj_scan_token(ALTERN)) return true;
    if (jj_3R_regExpression_181_5_6()) return true;
    if (jj_scan_token(RPAREN)) return true;
    return false;
  }

  private boolean jj_3_7()
 {
    if (jj_scan_token(LPAREN)) return true;
    if (jj_3R_regExpression_181_5_6()) return true;
    if (jj_scan_token(KSTAR)) return true;
    return false;
  }

  private boolean jj_3_6()
 {
    if (jj_scan_token(LPAREN)) return true;
    if (jj_3R_regExpression_181_5_6()) return true;
    if (jj_scan_token(CONCAT)) return true;
    return false;
  }

  private boolean jj_3_5()
 {
    if (jj_scan_token(LPAREN)) return true;
    if (jj_3R_regExpression_181_5_6()) return true;
    if (jj_scan_token(ALTERN)) return true;
    return false;
  }

  private boolean jj_3R_regExpression_192_11_17()
 {
    if (jj_3R_converseEdgeLabel_234_9_29()) return true;
    return false;
  }

  private boolean jj_3R_regExpression_190_11_16()
 {
    if (jj_3R_kPlusRegExpression_226_9_28()) return true;
    return false;
  }

  private boolean jj_3R_RDFLiteral_290_5_31()
 {
    if (jj_3R_String_318_5_35()) return true;
    return false;
  }

  private boolean jj_3R_regExpression_188_11_15()
 {
    if (jj_3R_kStarRegExpression_218_9_27()) return true;
    return false;
  }

  private boolean jj_3_4()
 {
    if (jj_3R_absoluteIri_136_9_5()) return true;
    return false;
  }

  private boolean jj_3R_regExpression_186_11_14()
 {
    if (jj_3R_concatRegExpression_210_9_26()) return true;
    return false;
  }

  private boolean jj_3R_regExpression_184_11_13()
 {
    if (jj_3R_alternRegExpression_201_9_25()) return true;
    return false;
  }

  private boolean jj_3R_NumericLiteral_282_5_34()
 {
    if (jj_scan_token(DOUBLE)) return true;
    return false;
  }

  private boolean jj_3R_NumericLiteral_281_5_33()
 {
    if (jj_scan_token(DECIMAL)) return true;
    return false;
  }

  private boolean jj_3_10()
 {
    if (jj_3R_term_262_5_8()) return true;
    return false;
  }

  private boolean jj_3R_NumericLiteral_280_5_32()
 {
    if (jj_scan_token(INTEGER)) return true;
    return false;
  }

  private boolean jj_3R_NumericLiteral_280_5_30()
 {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_NumericLiteral_280_5_32()) {
    jj_scanpos = xsp;
    if (jj_3R_NumericLiteral_281_5_33()) {
    jj_scanpos = xsp;
    if (jj_3R_NumericLiteral_282_5_34()) return true;
    }
    }
    return false;
  }

  /** Generated Token Manager. */
  public JavaCCRPQParserTokenManager token_source;
  JavaCharStream jj_input_stream;
  /** Current token. */
  public Token token;
  /** Next token. */
  public Token jj_nt;
  private int jj_ntk;
  private Token jj_scanpos, jj_lastpos;
  private int jj_la;
  /** Whether we are looking ahead. */
  private boolean jj_lookingAhead = false;
  private boolean jj_semLA;
  private int jj_gen;
  final private int[] jj_la1 = new int[9];
  static private int[] jj_la1_0;
  static private int[] jj_la1_1;
  static private int[] jj_la1_2;
  static {
	   jj_la1_init_0();
	   jj_la1_init_1();
	   jj_la1_init_2();
	}
	private static void jj_la1_init_0() {
	   jj_la1_0 = new int[] {0x0,0x0,0x2000,0x40,0x1c002200,0x1c000000,0x0,0x0,0x0,};
	}
	private static void jj_la1_init_1() {
	   jj_la1_1 = new int[] {0x400,0x800,0x200,0x0,0x40200,0x0,0x300000,0x300000,0x0,};
	}
	private static void jj_la1_init_2() {
	   jj_la1_2 = new int[] {0x0,0x0,0x0,0x0,0x78,0x0,0x0,0x0,0x78,};
	}
  final private JJCalls[] jj_2_rtns = new JJCalls[13];
  private boolean jj_rescan = false;
  private int jj_gc = 0;

  /** Constructor with InputStream. */
  public JavaCCRPQParser(java.io.InputStream stream) {
	  this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public JavaCCRPQParser(java.io.InputStream stream, String encoding) {
	 try { jj_input_stream = new JavaCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
	 token_source = new JavaCCRPQParserTokenManager(jj_input_stream);
	 token = new Token();
	 jj_ntk = -1;
	 jj_gen = 0;
	 for (int i = 0; i < 9; i++) jj_la1[i] = -1;
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
	 for (int i = 0; i < 9; i++) jj_la1[i] = -1;
	 for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor. */
  public JavaCCRPQParser(java.io.Reader stream) {
	 jj_input_stream = new JavaCharStream(stream, 1, 1);
	 token_source = new JavaCCRPQParserTokenManager(jj_input_stream);
	 token = new Token();
	 jj_ntk = -1;
	 jj_gen = 0;
	 for (int i = 0; i < 9; i++) jj_la1[i] = -1;
	 for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(java.io.Reader stream) {
	if (jj_input_stream == null) {
	   jj_input_stream = new JavaCharStream(stream, 1, 1);
	} else {
	   jj_input_stream.ReInit(stream, 1, 1);
	}
	if (token_source == null) {
 token_source = new JavaCCRPQParserTokenManager(jj_input_stream);
	}

	 token_source.ReInit(jj_input_stream);
	 token = new Token();
	 jj_ntk = -1;
	 jj_gen = 0;
	 for (int i = 0; i < 9; i++) jj_la1[i] = -1;
	 for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor with generated Token Manager. */
  public JavaCCRPQParser(JavaCCRPQParserTokenManager tm) {
	 token_source = tm;
	 token = new Token();
	 jj_ntk = -1;
	 jj_gen = 0;
	 for (int i = 0; i < 9; i++) jj_la1[i] = -1;
	 for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(JavaCCRPQParserTokenManager tm) {
	 token_source = tm;
	 token = new Token();
	 jj_ntk = -1;
	 jj_gen = 0;
	 for (int i = 0; i < 9; i++) jj_la1[i] = -1;
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

  @SuppressWarnings("serial")
  static private final class LookaheadSuccess extends java.lang.Error {
    @Override
    public Throwable fillInStackTrace() {
      return this;
    }
  }
  static private final LookaheadSuccess jj_ls = new LookaheadSuccess();
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
	 Token t = jj_lookingAhead ? jj_scanpos : token;
	 for (int i = 0; i < index; i++) {
	   if (t.next != null) t = t.next;
	   else t = t.next = token_source.getNextToken();
	 }
	 return t;
  }

  private int jj_ntk_f() {
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
	 if (pos >= 100) {
		return;
	 }

	 if (pos == jj_endpos + 1) {
	   jj_lasttokens[jj_endpos++] = kind;
	 } else if (jj_endpos != 0) {
	   jj_expentry = new int[jj_endpos];

	   for (int i = 0; i < jj_endpos; i++) {
		 jj_expentry[i] = jj_lasttokens[i];
	   }

	   for (int[] oldentry : jj_expentries) {
		 if (oldentry.length == jj_expentry.length) {
		   boolean isMatched = true;

		   for (int i = 0; i < jj_expentry.length; i++) {
			 if (oldentry[i] != jj_expentry[i]) {
			   isMatched = false;
			   break;
			 }

		   }
		   if (isMatched) {
			 jj_expentries.add(jj_expentry);
			 break;
		   }
		 }
	   }

	   if (pos != 0) {
		 jj_lasttokens[(jj_endpos = pos) - 1] = kind;
	   }
	 }
  }

  /** Generate ParseException. */
  public ParseException generateParseException() {
	 jj_expentries.clear();
	 boolean[] la1tokens = new boolean[72];
	 if (jj_kind >= 0) {
	   la1tokens[jj_kind] = true;
	   jj_kind = -1;
	 }
	 for (int i = 0; i < 9; i++) {
	   if (jj_la1[i] == jj_gen) {
		 for (int j = 0; j < 32; j++) {
		   if ((jj_la1_0[i] & (1<<j)) != 0) {
			 la1tokens[j] = true;
		   }
		   if ((jj_la1_1[i] & (1<<j)) != 0) {
			 la1tokens[32+j] = true;
		   }
		   if ((jj_la1_2[i] & (1<<j)) != 0) {
			 la1tokens[64+j] = true;
		   }
		 }
	   }
	 }
	 for (int i = 0; i < 72; i++) {
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

  private boolean trace_enabled;

/** Trace enabled. */
  final public boolean trace_enabled() {
	 return trace_enabled;
  }

  /** Enable tracing. */
  final public void enable_tracing() {
  }

  /** Disable tracing. */
  final public void disable_tracing() {
  }

  private void jj_rescan_token() {
	 jj_rescan = true;
	 for (int i = 0; i < 13; i++) {
	   try {
		 JJCalls p = jj_2_rtns[i];

		 do {
		   if (p.gen > jj_gen) {
			 jj_la = p.arg; jj_lastpos = jj_scanpos = p.first;
			 switch (i) {
			   case 0: jj_3_1(); break;
			   case 1: jj_3_2(); break;
			   case 2: jj_3_3(); break;
			   case 3: jj_3_4(); break;
			   case 4: jj_3_5(); break;
			   case 5: jj_3_6(); break;
			   case 6: jj_3_7(); break;
			   case 7: jj_3_8(); break;
			   case 8: jj_3_9(); break;
			   case 9: jj_3_10(); break;
			   case 10: jj_3_11(); break;
			   case 11: jj_3_12(); break;
			   case 12: jj_3_13(); break;
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

	 p.gen = jj_gen + xla - jj_la; 
	 p.first = token;
	 p.arg = xla;
  }

  static final class JJCalls {
	 int gen;
	 Token first;
	 int arg;
	 JJCalls next;
  }

}

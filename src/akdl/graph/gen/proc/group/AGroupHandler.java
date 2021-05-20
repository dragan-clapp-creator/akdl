package akdl.graph.gen.proc.group;

import akdl.code.gen.java.CodeBeanForParser;
import akdl.code.gen.java.CodeBeanForRuntime;
import akdl.code.gen.java.CodeBeanHandler;
import akdl.code.gen.java.persist.PlaceHolder;
import akdl.code.gen.java.persist.Template;
import akdl.graph.gen.proc.DefinitionContext;
import akdl.graph.gen.proc.HandlerForPredefined;
import akdl.graph.nodes.CharSymbol;
import akdl.graph.nodes.DefinitionNode;
import akdl.graph.nodes.KeySymbol;
import akdl.graph.nodes.PredefinedSymbol;
import akdl.graph.nodes.RegExpSymbol;
import akdl.graph.nodes.StringSymbol;
import akdl.graph.nodes.elts.ASymbol;
import akdl.graph.nodes.elts.Operator;
import akdl.sheet.keys.LeafType;
import akdl.sheet.parse.ParseResult;

public abstract class AGroupHandler {

  public abstract void process(KeySymbol keySymbol, DefinitionContext context);
  public abstract void process(PredefinedSymbol sym, DefinitionContext context);
  public abstract void process(StringSymbol sym, DefinitionContext context);
  public abstract void process(CharSymbol sym, DefinitionContext context);

  /**
   * add Runtime Boolean Attributes And Methods
   * @param def 
   * @param bean
   * @param result
   */
  public void addRuntimeBoolAttributesAndMethods(DefinitionNode def, CodeBeanForRuntime bean, ParseResult result) {
    bean.insert(PlaceHolder.ATT, Template.RUNTIME_ATT_BOOL.toString(), result);
    bean.insert(PlaceHolder.METHOD, Template.RUNTIME_ACC_BOOL.toString(), result);
    replaceAll(bean, def, result);
  }

  //
  private void replaceAll(CodeBeanForRuntime bean, DefinitionNode def, ParseResult result) {
    if (def != null) {
      bean.replaceAll(PlaceHolder.PACK, def.getRuntimePath(), result);
      bean.replaceAll(PlaceHolder.TYPE, def.getRuntimeClassName(), result);
    }
    else {
      bean.replaceAll(PlaceHolder.PACK, bean.getPack()+".", result);
      bean.replaceAll(PlaceHolder.TYPE, bean.getClname(), result);
    }
    bean.replaceAll(PlaceHolder.TRANSIENT, "", result);
  }

  /**
   * add Runtime Simple Attributes And Methods
   * @param def 
   * @param bean
   * @param result
   */
  public void addRuntimeSimpleAttributesAndMethods(DefinitionNode def, CodeBeanForRuntime bean, ParseResult result) {
    bean.insert(PlaceHolder.ATT, Template.RUNTIME_ATT_SIMPLE.toString(), result);
    bean.insert(PlaceHolder.METHOD, Template.RUNTIME_ACC_SIMPLE.toString(), result);
    replaceAll(bean, def, result);
  }

  /**
   * add Runtime Array Attributes And Methods
   * @param def 
   * @param bean
   * @param cbh
   * @param result
   */
  public void addRuntimeArrayAttributesAndMethods(DefinitionNode def, CodeBeanForRuntime bean, ParseResult result) {
    bean.insert(PlaceHolder.ATT, Template.RUNTIME_ATT_ARRAY.toString(), result);
    bean.insert(PlaceHolder.METHOD, Template.RUNTIME_ACC_ARRAY.toString(), result);
    replaceAll(bean, def, result);
  }

  /**
   * process predefined symbol
   * 
   * @param codeLine
   * @param sym predefined Symbol
   * @param context
   * @param operator
   */
  public void process(int level, PredefinedSymbol sym, DefinitionContext context,
      Operator operator) {
    Info info = new Info(sym);
    ParseResult result = ParseResult.getInstance();
    CodeBeanForRuntime rbean = context.getRbean();
    if (!sym.isReference()) {
      CodeBeanForParser pbean = context.getPbean();
      String template = selectTemplate(info.getItype(), operator, sym.getType(), context.isFirst());
      if (info.getItype() == "Graph") {
        CodeBeanForParser mbean = CodeBeanHandler.getInstance().getMainParserBean();
        mbean.replaceAll(PlaceHolder.IS_GRAPH, Template.PARSER_GRAPH.toString(), result);
        info.correctType();
      }

      HandlerForPredefined.getInstance().addParserForAll(level, pbean, rbean, sym, template, result);

      if (info.getItype() == "RegExp") {
        pbean.replaceAll(PlaceHolder.VALUE, ((RegExpSymbol)sym).getPattern(), result);
        info.correctType();
      }
    }

    if (operator == Operator.STAR) {
      info.correctType();
      rbean.insert(PlaceHolder.ATT, Template.RUNTIME_ATT_ARRAY.toString(), result);
      rbean.insert(PlaceHolder.METHOD, Template.RUNTIME_ACC_ARRAY.toString(), result);
    }
    else {
      rbean.insert(PlaceHolder.ATT, Template.RUNTIME_ATT_SIMPLE.toString(), result);
      rbean.insert(PlaceHolder.METHOD, Template.RUNTIME_ACC_SIMPLE.toString(), result);
    }
    if (operator == Operator.HUT ) {
      rbean.insert(PlaceHolder.ATT, Template.RUNTIME_ATT_BOOL.toString(), result);
      rbean.insert(PlaceHolder.METHOD, Template.RUNTIME_ACC_BOOL.toString(), result);
    }
    rbean.replaceAll(PlaceHolder.TRANSIENT, info.isTransient() ? "transient " : "", result);
    rbean.replaceAll(PlaceHolder.PACK, info.getIpack(), result);
    rbean.replaceAll(PlaceHolder.TYPE, info.getItype(), result);
    rbean.replaceAll(PlaceHolder.NAME, sym.getName(), result);
    rbean.replaceAll(PlaceHolder.MNAME, sym.upper(sym.getName()), result);
    rbean.replaceAll(PlaceHolder.RT_TYPE, sym.upper(sym.getName()), result);
  }

  //
  private String selectTemplate(String itype, Operator operator, LeafType leafType, boolean isFirst) {
    String stemplate;
    if (operator == Operator.HUT) {
      if (this instanceof HutGroupHandler && isFirst) {
        stemplate = Template.PARSER_PREDEF_GROUP_MATCH01.toString();
      }
      else {
        stemplate = Template.PARSER_PREDEF_MATCH01.toString();
      }
    }
    else if (leafType == LeafType.LITERAL) {
      stemplate = Template.PARSER_PREDEF_CST_MATCH.toString();
    }
    else if (itype.equals("char")) {
      stemplate = Template.PARSER_PREDEF_CHAR_MATCH.toString();
    }
    else {
      stemplate = Template.PARSER_PREDEF_MATCH.toString();
    }
    String value = PlaceHolder.VALUE.getString();
    switch (itype) {
      case "int":
        stemplate = stemplate.replace(value, "(int) parser.nval");
        stemplate = stemplate.replace("TT_WORD", "TT_NUMBER");
        break;
      case "boolean":
        stemplate = stemplate.replace(value, "parser.sval.equalsIgnoreCase(\"true\")");
        break;
      case "char":
        stemplate = stemplate.replace(value, "(char) (parser.sval == null ? parser.nval : parser.sval.charAt(0))");
        break;
      case "double":
      case "Number":
        stemplate = stemplate.replace(value, "(double) parser.nval");
        stemplate = stemplate.replace("TT_WORD", "TT_NUMBER");
        break;
      case "long":
        stemplate = stemplate.replace(value, "(long) parser.nval");
        stemplate = stemplate.replace("TT_WORD", "TT_NUMBER");
        break;
      case "Graph":
        stemplate = stemplate.replace(value, "parser.collectGraphSentences()");
        break;
      case "RegExp":
        if (operator == Operator.HUT) {
          stemplate = Template.PARSER_PREDEF_REGEXP_MATCH01.toString();
        }
        else {
          stemplate = Template.PARSER_PREDEF_REGEXP_MATCH.toString();
        }
        break;
      case "String":
        if (operator == Operator.PLUS) {
          if (leafType == LeafType.IDENTIFIER) {
            stemplate = Template.PARSER_PLUS_WORD.toString();
          }
          else {
            stemplate = Template.PARSER_PLUS_CST.toString();
          }
          break;
        }

      default:
        stemplate = stemplate.replace(value, "parser.sval");
        break;
    }
    if (operator == Operator.STAR) {
      stemplate = stemplate.replace(".set", ".add");
    }
    else if (operator == Operator.HUT) {
      stemplate = stemplate.replace("%OPTION%", "%NAME%.setIs%TYPE%(true);");
    }
    stemplate = stemplate.replace("%OPTION%", "");
    return stemplate;
  }

  //
  void replacePalceholdersForString(StringSymbol sym,
      CodeBeanForParser pbean, CodeBeanForRuntime rbean, ParseResult result) {
    String name = getName(sym);
    pbean.replaceAll(PlaceHolder.NAME, sym.getName(), result);
    pbean.replaceAll(PlaceHolder.PARENT_NAME, rbean.getName(), result);
    pbean.replaceAll(PlaceHolder.MNAME, "String"+name, result);

    rbean.replaceAll(PlaceHolder.TRANSIENT, "", result);
    rbean.replaceAll(PlaceHolder.TYPE, "String", result);
    rbean.replaceAll(PlaceHolder.NAME, "s"+name, result);
    rbean.replaceAll(PlaceHolder.MNAME, "String"+name, result);
    rbean.replaceAll(PlaceHolder.PACK, "", result);
    rbean.replaceAll(PlaceHolder.RT_TYPE, "String"+name, result);
  }

  //
  String getName(ASymbol sym) {
    String name = "";
    for (int i=0; i<sym.getName().length(); i++) {
      char c = sym.getName().charAt(i);
      if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_' || c == '$') {
        name += c;
      }
      else if (i > 0 && c >= '0' && c <= '9') {
        name += c;
      }
      else {
        return "";
      }
    }
    return name;
  }

  // ****************************************************************

  public class Info {
    private String ipack = "";
    private String itype;
    private boolean isPrimaryType;
    private boolean isTransient;

    Info(PredefinedSymbol sym) {
      switch (sym.getType()) {
        case BOOLEAN:
          isPrimaryType = true;
          itype = "boolean";
          break;
        case BOOLEANS:
          ipack = "java.util.";
          itype = "ArrayList<java.lang.Boolean>";
          break;
        case CHAR:
          isPrimaryType = true;
          itype = "char";
          break;
        case DATE:
          ipack = "java.util.";
          itype = "Date";
          break;
        case DATES:
          ipack = "java.util.";
          itype = "ArrayList<java.util.Date>";
          break;
        case DOUBLE:
          isPrimaryType = true;
          itype = "double";
          break;
        case DOUBLES:
          ipack = "java.util.";
          itype = "ArrayList<java.lang.Double>";
          break;
        case GRAPH:
          isPrimaryType = true;
          itype = "Graph";
          break;
        case REGEXP:
          isPrimaryType = true;
          itype = "RegExp";
          break;
        case LITERAL:
        case IDENTIFIER:
          isPrimaryType = true;
          itype = "String";
          break;
        case INTEGER:
          isPrimaryType = true;
          itype = "int";
          break;
        case INTEGERS:
          ipack = "java.util.";
          itype = "ArrayList<java.lang.Integer>";
          break;
        case LONG:
          isPrimaryType = true;
          itype = "long";
          break;
        case LONGS:
          ipack = "java.util.";
          itype = "ArrayList<java.sql.Time>";
          break;
        case NUMERICAL:
          ipack = "java.lang.";
          itype = "Number";
          break;
        case NUMERICALS:
          ipack = "java.util.";
          itype = "ArrayList<java.lang.Number>";
          break;
        case OBJECT:
          ipack = "java.lang.";
          itype = "Object";
          isTransient = true;
          break;
        case OBJECTS:
          ipack = "java.util.";
          itype = "ArrayList<java.lang.Object>";
          isTransient = true;
          break;
        case STRINGS:
          ipack = "java.util.";
          itype = "ArrayList<String>";
          break;
        case TIME:
          ipack = "java.sql.";
          itype = "Time";
          break;
        case TIMES:
          ipack = "java.util.";
          itype = "ArrayList<java.sql.Time>";
          break;

        default:
          break;
      }
    }

    public void correctType() {
      switch (itype) {
        case "int":
          itype = "Integer";
          break;
        case "boolean":
          itype = "Boolean";
          break;
        case "char":
          itype = "Character";
          break;
        case "double":
          itype = "Double";
          break;
        case "long":
          itype = "Long";
          break;
        case "Graph":
        case "RegExp":
          itype = "String";
          break;

        default:
          break;
      }
    }

    /**
     * @return the ipack
     */
    public String getIpack() {
      return ipack;
    }

    /**
     * @return the itype
     */
    public String getItype() {
      return itype;
    }

    /**
     * @return the isPrimaryType
     */
    public boolean isPrimaryType() {
      return isPrimaryType;
    }

    /**
     * @return the isTransient
     */
    public boolean isTransient() {
      return isTransient;
    }
  }

  /**
   * @param sym
   * @return
   */
  public String getName(KeySymbol sym) {
    String name = sym.getVarName();
    if (name == null) {
      if (sym.getDefinition() != null) {
        name = sym.getDefinition().getName();
      }
      else {
        name = sym.getName();
      }
    }
    return name;
  }

  /**
   * @param sym
   * @param pbean 
   * @return
   */
  public String getPRSClassName(KeySymbol sym, CodeBeanForParser pbean) {
    if (sym.getDefinition() != null) {
      return sym.getDefinition().getParserClassName();
    }
    return pbean.getClname();
  }

  /**
   * @param sym
   * @param rbean 
   * @return
   */
  public String getRTClassName(KeySymbol sym, CodeBeanForRuntime rbean) {
    if (sym.getDefinition() != null) {
      return sym.getDefinition().getRuntimeClassName();
    }
    return rbean.getClname();
  }
}

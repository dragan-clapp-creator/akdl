package akdl.graph.gen.proc.group;

import akdl.code.gen.java.CodeBeanForParser;
import akdl.code.gen.java.CodeBeanForRuntime;
import akdl.code.gen.java.persist.PlaceHolder;
import akdl.code.gen.java.persist.Template;
import akdl.graph.gen.proc.DefinitionContext;
import akdl.graph.nodes.CharSymbol;
import akdl.graph.nodes.KeySymbol;
import akdl.graph.nodes.PredefinedSymbol;
import akdl.graph.nodes.StringSymbol;
import akdl.graph.nodes.elts.Operator;
import akdl.sheet.parse.MessageWriter;
import akdl.sheet.parse.ParseResult;

public class HutGroupHandler extends AGroupHandler {

  private ParseResult result;

  public HutGroupHandler() {
    result = ParseResult.getInstance();
  }

  @Override
  public void process(KeySymbol sym, DefinitionContext context) {
    if (sym.getOperator() != Operator.NONE) {
      MessageWriter.E202.store(result, sym.getOperator().name(), sym.getName());
      return;
    }
    CodeBeanForParser pbean = context.getPbean();
    CodeBeanForRuntime rbean = context.getRbean();
    pbean.insert(context.getLevel(), Template.PARSER_KEY_DECL.toString(), result);
    pbean.insert(context.getLevel(), Template.PARSER_HUT_GROUP_BLOCK.toString(), result);
    pbean.replaceAll(PlaceHolder.BOOL, (context.isFirst() ? "true" : "false"), result);
    pbean.replaceAll(PlaceHolder.INDEX, context.getIndexString(), result);
    pbean.replaceAll(PlaceHolder.PARENT_NAME, rbean.getName(), result);
    pbean.replaceAll(PlaceHolder.NAME, getName(sym), result);
    pbean.replaceAll(PlaceHolder.TYPE, getPRSClassName(sym, pbean), result);
    pbean.replaceAll(PlaceHolder.MNAME, sym.upper(getName(sym)), result);
    pbean.replaceAll(PlaceHolder.RT_TYPE, getRTClassName(sym, rbean), result);
    if (!pbean.addToImports(sym.getDefinition())) {
      pbean.addToImports(pbean.getPack()+".", pbean.getClname());
    }

    addRuntimeBoolAttributesAndMethods(sym.getDefinition(), rbean, result);
    addRuntimeSimpleAttributesAndMethods(sym.getDefinition(), rbean, result);
    rbean.replaceAll(PlaceHolder.NAME, getName(sym), result);
    rbean.replaceAll(PlaceHolder.MNAME, sym.upper(getName(sym)), result);
    rbean.replaceAll(PlaceHolder.RT_TYPE, getRTClassName(sym, rbean), result);
  }

  /**
   * process string symbol
   * 
   * @param sym     string Symbol
   * @param context
   */
  public void process(PredefinedSymbol sym, DefinitionContext context) {
    if (sym.getOperator() != Operator.NONE) {
      MessageWriter.E202.store(result, sym.getOperator().name(), sym.getName());
      return;
    }
    super.process(context.getLevel(), sym, context, Operator.HUT);
    context.getPbean().replaceAll(PlaceHolder.BOOL, (context.isFirst() ? "true" : "false"), result);
  }

  /**
   * process char symbol
   * 
   * @param sym     char Symbol
   * @param context
   */
  public void process(CharSymbol sym, DefinitionContext context) {
    if (sym.getOperator() != Operator.NONE) {
      MessageWriter.E202.store(result, sym.getOperator().name(), sym.getName());
      return;
    }
    CodeBeanForParser pbean = context.getPbean();
    CodeBeanForRuntime rbean = context.getRbean();
    ParseResult result = ParseResult.getInstance();

    pbean.insert(context.getLevel(), Template.PARSER_CHAR_MATCH.toString(), result);
    pbean.replaceAll(PlaceHolder.OPTION, Template.PARSER_CHAR_OPTION.toString(), result);

    rbean.insert(PlaceHolder.ATT, Template.RUNTIME_ATT_SIMPLE.toString(), result);
    rbean.insert(PlaceHolder.METHOD, Template.RUNTIME_ACC_SIMPLE.toString(), result);

    pbean.replaceAll(PlaceHolder.BOOL, (context.isFirst() ? "true" : "false"), result);
    pbean.replaceAll(PlaceHolder.CAR, sym.getCharacter(), ParseResult.getInstance());
    pbean.replaceAll(PlaceHolder.PARENT_NAME, rbean.getName(), result);
    pbean.replaceAll(PlaceHolder.MNAME, "Char", result);

    rbean.replaceAll(PlaceHolder.TRANSIENT, "", result);
    rbean.replaceAll(PlaceHolder.TYPE, "char", result);
    rbean.replaceAll(PlaceHolder.NAME, "ckey", result);
    rbean.replaceAll(PlaceHolder.MNAME, "Char", result);
    rbean.replaceAll(PlaceHolder.PACK, "", result);
  }

  /**
   * process string symbol
   * 
   * @param sym         string Symbol
   * @param context
   */
  public void process(StringSymbol sym, DefinitionContext context) {
    if (sym.getOperator() != Operator.NONE) {
      MessageWriter.E202.store(result, sym.getOperator().name(), sym.getName());
      return;
    }
    CodeBeanForParser pbean = context.getPbean();
    CodeBeanForRuntime rbean = context.getRbean();
    ParseResult result = ParseResult.getInstance();

    String type = sym.upper(sym.getName());
    pbean.insert(context.getLevel(), Template.PARSER_GROUP_MATCH01.toString(), result);
    pbean.replaceAll(PlaceHolder.BOOL, (context.isFirst() ? "true" : "false"), result);
    pbean.replaceAll(PlaceHolder.NAME, sym.getName(), result);
    pbean.replaceAll(PlaceHolder.PARENT_NAME, rbean.getName(), result);
    pbean.replaceAll(PlaceHolder.RT_TYPE, type, result);

    rbean.insert(PlaceHolder.ATT, Template.RUNTIME_ATT_BOOL.toString(), result);
    rbean.insert(PlaceHolder.METHOD, Template.RUNTIME_ACC_BOOL.toString(), result);
    rbean.replaceAll(PlaceHolder.RT_TYPE, type, result);
  }
}

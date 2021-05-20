package akdl.graph.gen.proc.group;

import akdl.code.gen.java.CodeBeanForParser;
import akdl.code.gen.java.CodeBeanForRuntime;
import akdl.code.gen.java.persist.PlaceHolder;
import akdl.code.gen.java.persist.Template;
import akdl.graph.gen.proc.DefinitionContext;
import akdl.graph.nodes.CharSymbol;
import akdl.graph.nodes.EnumSymbol;
import akdl.graph.nodes.KeySymbol;
import akdl.graph.nodes.PredefinedSymbol;
import akdl.graph.nodes.StringSymbol;
import akdl.graph.nodes.elts.Operator;
import akdl.sheet.keys.AttKeys;
import akdl.sheet.parse.MessageWriter;
import akdl.sheet.parse.ParseResult;

public class PlusGroupHandler extends AGroupHandler {

  private ParseResult result;

  public PlusGroupHandler() {
    result = ParseResult.getInstance();
  }

  @Override
  public void process(KeySymbol sym, DefinitionContext context) {
    if (sym.getOperator() != Operator.NONE) {
      MessageWriter.E202.store(result, sym.getOperator().name(), sym.getName());
      return;
    }
    context.getPbean().addToImports(sym.getDefinition());
    if (sym.getOperator() == Operator.NONE) {
      if (context.getRbean().isInterface()) {
        processInterface(sym, context, result);
      }
      else {
        processClass(sym, context, result);
      }
    }
    context.getPbean().replaceAll(PlaceHolder.BOOL, (context.getIndex() == 0 ? "true" : "false"), result);
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

    pbean.insert(context.getLevel(), Template.PARSER_PLUS_CHAR_MATCH.toString(), result);

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
   * @param sym     string Symbol
   * @param context
   */
  public void process(StringSymbol sym, DefinitionContext context) {
    if (sym.getOperator() != Operator.NONE) {
      MessageWriter.E202.store(result, sym.getOperator().name(), sym.getName());
      return;
    }
    CodeBeanForParser pbean = context.getPbean();
    CodeBeanForRuntime rbean = context.getRbean();

    pbean.insert(context.getLevel(), Template.PARSER_PLUS_KEY_MATCH.toString(), result);

    rbean.insert(PlaceHolder.ATT, Template.RUNTIME_ATT_SIMPLE.toString(), result);
    rbean.insert(PlaceHolder.METHOD, Template.RUNTIME_ACC_SIMPLE.toString(), result);
    rbean.insert(PlaceHolder.ATT, Template.RUNTIME_ATT_BOOL.toString(), result);
    rbean.insert(PlaceHolder.METHOD, Template.RUNTIME_ACC_BOOL.toString(), result);

    replacePalceholdersForString(sym, pbean, rbean, result);
  }

  /**
   * process predefined symbol
   * 
   * @param sym     predefined Symbol
   * @param context
   */
  public void process(PredefinedSymbol sym, DefinitionContext context) {
    super.process(context.getLevel(), sym, context, Operator.PLUS);
    context.getPbean().replaceAll(PlaceHolder.PARENT_NAME, context.getRbean().getName(), ParseResult.getInstance());
    context.getPbean().replaceAll(PlaceHolder.BOOL, (context.isFirst() ? "true" : "false"), result);
  }

  /**
   * process Enum
   * @param sym
   * @param context
   */
  public void processEnum(EnumSymbol sym, DefinitionContext context) {
    ParseResult result = ParseResult.getInstance();
    String name = (sym.getVarName() == null ? sym.getName() : sym.getVarName());
    CodeBeanForRuntime rbean = context.getRbean();
    if (context.isFirst()) {
      CodeBeanForParser pbean = context.getPbean();
      if (name.length() > 1) {
        pbean.insert(context.getLevel(), Template.PARSER_ENUM.toString(), result);
      }
      else {
        pbean.insert(context.getLevel(), Template.PARSER_ENUM_CAR.toString(), result);
      }
      pbean.replaceAll(PlaceHolder.BOOL, "true", result);
      pbean.replaceAll(PlaceHolder.PACK, context.getNode().getRuntimePath(), result);
      pbean.replaceAll(PlaceHolder.TYPE, context.getNode().getRuntimeClassName(), result);
      pbean.replaceAll(PlaceHolder.NAME, rbean.getName(), result);
      pbean.replaceAll(PlaceHolder.MNAME, sym.upper(getName(sym)), result);
    }
    rbean.insert(PlaceHolder.ATT, Template.RUNTIME_ATT_ENUM_LINE.toString(), result);
    rbean.replaceAll(PlaceHolder.ENUM_A, sym.getClname(), result);
    rbean.replaceAll(PlaceHolder.ENUM_B, name, result);
  }

  //
  private void processInterface(KeySymbol sym, DefinitionContext context, ParseResult result) {
    CodeBeanForParser pbean = context.getPbean();
    CodeBeanForRuntime rbean = context.getRbean();
    pbean.insert(context.getLevel(), Template.PARSER_PLUS_IBLOCK.toString(), result);
    pbean.replaceAll(PlaceHolder.PARENT_NAME, rbean.getName(), result);
    pbean.replaceAll(PlaceHolder.RT_TYPE, getRTClassName(sym, rbean), result);
    pbean.replaceAll(PlaceHolder.TYPE, sym.getDefinition().getParserClassName(), result);
    pbean.replaceAll(PlaceHolder.NAME, sym.getDefinition().getName(), result);
    pbean.replaceAll(PlaceHolder.MNAME, sym.upper(getName(sym)), result);

    rbean.insert(PlaceHolder.METHOD_DECL, Template.RUNTIME_METHOD_DEC.toString(), result);
    rbean.insertImpl(PlaceHolder.METHOD_DECL, Template.RUNTIME_VISITOR_METHOD.toString(), result);
    rbean.replaceAll(PlaceHolder.TYPE, sym.getClname(), result);
    rbean.replaceAll(PlaceHolder.RT_TYPE, getRTClassName(sym, rbean), result);
    if (!rbean.isForInterface() || context.getParentNode().getType() != AttKeys.ATT_INTERFACE) {
      rbean.addToImports(sym.getDefinition());
    }
  }

  //
  private void processClass(KeySymbol sym, DefinitionContext context, ParseResult result) {
    CodeBeanForParser pbean = context.getPbean();
    CodeBeanForRuntime rbean = context.getRbean();
    pbean.insert(context.getLevel(), Template.PARSER_PLUS_BLOCK.toString(), result);
    pbean.replaceAll(PlaceHolder.PARENT_NAME, rbean.getName(), result);
    pbean.replaceAll(PlaceHolder.RT_TYPE, getRTClassName(sym, rbean), result);
    pbean.replaceAll(PlaceHolder.TYPE, sym.getDefinition().getParserClassName(), result);
    pbean.replaceAll(PlaceHolder.NAME, sym.getDefinition().getName(), result);
    pbean.replaceAll(PlaceHolder.MNAME, sym.upper(getName(sym)), result);

    addRuntimeSimpleAttributesAndMethods(sym.getDefinition(), rbean, result);
    rbean.replaceAll(PlaceHolder.MNAME, sym.upper(getName(sym)), result);
    rbean.replaceAll(PlaceHolder.NAME, getName(sym), result);
  }

}

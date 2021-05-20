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
import akdl.graph.nodes.elts.ASymbol;
import akdl.sheet.parse.MessageWriter;
import akdl.sheet.parse.ParseResult;

public class AmpersAndGroupHandler extends AGroupHandler {

  @Override
  public void process(KeySymbol sym, DefinitionContext context) {
    CodeBeanForParser pbean = context.getPbean();
    CodeBeanForRuntime rbean = context.getRbean();
    ParseResult result = ParseResult.getInstance();

    switch (sym.getOperator()) {
      case NONE:
        handleParseBlock(sym, context, result, Template.PARSER_AMPERS_KEY);
        pbean.replaceAll(PlaceHolder.OPTION, "%NAME% = null;", result);

        addRuntimeSimpleAttributesAndMethods(sym.getDefinition(), rbean, result);
        break;
      case HUT:
        handleParseBlock(sym, context, result, Template.PARSER_AMPERS_KEY01);

        addRuntimeBoolAttributesAndMethods(sym.getDefinition(), rbean, result);
        addRuntimeSimpleAttributesAndMethods(sym.getDefinition(), rbean, result);
       break;
      case STAR:
        handleParseBlock(sym, context, result, Template.PARSER_AMPERS_KEY0N);

        addRuntimeArrayAttributesAndMethods(sym.getDefinition(), rbean, result);
        break;

      default:
        MessageWriter.E202.store(result, sym.getOperator().name(), sym.getName());
        break;
    }
    pbean.replaceAll(PlaceHolder.BOOL, (context.isFirst() ? "true" : "false"), result);
    rbean.replaceAll(PlaceHolder.NAME, getName(sym), result);
    rbean.replaceAll(PlaceHolder.MNAME, sym.upper(getName(sym)), result);
    rbean.replaceAll(PlaceHolder.RT_TYPE, getRTClassName(sym, rbean), result);
  }

  //
  private void handleParseBlock(KeySymbol sym, DefinitionContext context, ParseResult result,
      Template template) {
    String test = Template.PARSER_AMPERS_BOOL_TEST.toString();
    if (context.getIndex() == 0) {
      test = test.replaceAll("&&", "  ");
    }
    CodeBeanForParser pbean = context.getPbean();
    CodeBeanForRuntime rbean = context.getRbean();
    pbean.addToImports(sym.getDefinition());
    pbean.insert(PlaceHolder.BOOL_DECL, Template.PARSER_AMPERS_BOOL_DECL.toString(), result);
    pbean.insert(PlaceHolder.BOOL_TEST, test, result);

    pbean.insert(PlaceHolder.REF_DECL, Template.PARSER_AMPERS_DECL.toString(), result);
    pbean.insert(PlaceHolder.REF_BLOCK, template.toString(), result);
    pbean.replaceAll(PlaceHolder.INDEX, context.getIndexString(), result);
    pbean.replaceAll(PlaceHolder.PARENT_NAME, rbean.getName(), result);
    pbean.replaceAll(PlaceHolder.RT_TYPE, getRTClassName(sym, rbean), result);
    pbean.replaceAll(PlaceHolder.MNAME, sym.upper(getName(sym)), result);
    pbean.replaceAll(PlaceHolder.TYPE, sym.getDefinition().getParserClassName(), result);
    pbean.replaceAll(PlaceHolder.NAME, sym.getName(), result);
  }

  //
  private void handleParseBlock(ASymbol sym, DefinitionContext context, ParseResult result,
      Template template, boolean isIntDecl) {
    String test = Template.PARSER_AMPERS_BOOL_TEST.toString();
    if (context.getIndex() == 0) {
      test = test.replaceAll("&&", "  ");
    }
    CodeBeanForParser pbean = context.getPbean();
    CodeBeanForRuntime rbean = context.getRbean();
    if (isIntDecl) {
      pbean.insert(PlaceHolder.BOOL_DECL, Template.PARSER_AMPERS_INT_DECL.toString(), result);
    }
    pbean.insert(PlaceHolder.BOOL_DECL, Template.PARSER_AMPERS_BOOL_DECL.toString(), result);
    pbean.insert(PlaceHolder.BOOL_TEST, test, result);

    pbean.insert(PlaceHolder.REF_BLOCK, template.toString(), result);
    pbean.replaceAll(PlaceHolder.INDEX, context.getIndexString(), result);
    pbean.replaceAll(PlaceHolder.PARENT_NAME, rbean.getName(), result);
    pbean.replaceAll(PlaceHolder.RT_TYPE, rbean.getClname(), result);
    pbean.replaceAll(PlaceHolder.MNAME, sym.upper(getName(sym)), result);
    pbean.replaceAll(PlaceHolder.TYPE, pbean.getClname(), result);
    pbean.replaceAll(PlaceHolder.NAME, sym.getName(), result);
  }

  @Override
  public void process(PredefinedSymbol sym, DefinitionContext context) {
    CodeBeanForParser pbean = context.getPbean();
    CodeBeanForRuntime rbean = context.getRbean();
    ParseResult result = ParseResult.getInstance();

    switch (sym.getOperator()) {
      case NONE:
        handleParseBlock(sym, context, result, Template.PARSER_AMPERS_PREDEF, true);
        pbean.replaceAll(PlaceHolder.OPTION, "%NAME% = null;", result);

        rbean.insert(PlaceHolder.ATT, Template.RUNTIME_ATT_SIMPLE.toString(), result);
        rbean.insert(PlaceHolder.METHOD, Template.RUNTIME_ACC_SIMPLE.toString(), result);
        break;
      case HUT:
        handleParseBlock(sym, context, result, Template.PARSER_AMPERS_PREDEF01, true);

        rbean.insert(PlaceHolder.ATT, Template.RUNTIME_ATT_SIMPLE.toString(), result);
        rbean.insert(PlaceHolder.METHOD, Template.RUNTIME_ACC_SIMPLE.toString(), result);
        break;
      case STAR:
        handleParseBlock(sym, context, result, Template.PARSER_AMPERS_PREDEF0N, false);

        rbean.insert(PlaceHolder.ATT, Template.RUNTIME_ATT_ARRAY.toString(), result);
        rbean.insert(PlaceHolder.METHOD, Template.RUNTIME_ACC_ARRAY.toString(), result);
        break;

      default:
        MessageWriter.E202.store(result, sym.getOperator().name(), sym.getName());
        break;
    }
    pbean.replaceAll(PlaceHolder.BOOL, (context.isFirst() ? "true" : "false"), result);
    rbean.replaceAll(PlaceHolder.TRANSIENT, "", result);
    rbean.replaceAll(PlaceHolder.NAME, getName(sym), result);
    rbean.replaceAll(PlaceHolder.MNAME, sym.upper(getName(sym)), result);
    rbean.replaceAll(PlaceHolder.PACK, "", result);
  }

  /**
   * process char symbol
   * 
   * @param sym     char Symbol
   * @param context
   */
  public void process(CharSymbol sym, DefinitionContext context) {
    CodeBeanForParser pbean = context.getPbean();
    CodeBeanForRuntime rbean = context.getRbean();
    ParseResult result = ParseResult.getInstance();

    switch (sym.getOperator()) {
      case NONE:
        handleParseBlock(sym, context, result, Template.PARSER_AMPERS_CHAR, true);

        rbean.insert(PlaceHolder.ATT, Template.RUNTIME_ATT_SIMPLE.toString(), result);
        rbean.insert(PlaceHolder.METHOD, Template.RUNTIME_ACC_SIMPLE.toString(), result);
        break;
      case HUT:
        handleParseBlock(sym, context, result, Template.PARSER_AMPERS_CHAR01, true);

        rbean.insert(PlaceHolder.ATT, Template.RUNTIME_ATT_SIMPLE.toString(), result);
        rbean.insert(PlaceHolder.METHOD, Template.RUNTIME_ACC_SIMPLE.toString(), result);
        break;
      case STAR:
        handleParseBlock(sym, context, result, Template.PARSER_AMPERS_CHAR0N, false);

        rbean.insert(PlaceHolder.ATT, Template.RUNTIME_ATT_ARRAY.toString(), result);
        rbean.insert(PlaceHolder.METHOD, Template.RUNTIME_ACC_ARRAY.toString(), result);
        break;

      default:
        MessageWriter.E202.store(result, sym.getOperator().name(), pbean.getName());
        break;
    }

    pbean.replaceAll(PlaceHolder.CAR, sym.getCharacter(), ParseResult.getInstance());
    pbean.replaceAll(PlaceHolder.PARENT_NAME, rbean.getName(), result);
    pbean.replaceAll(PlaceHolder.MNAME, "Char", result);
    pbean.replaceAll(PlaceHolder.BOOL, (context.isFirst() ? "true" : "false"), result);

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
    CodeBeanForParser pbean = context.getPbean();
    CodeBeanForRuntime rbean = context.getRbean();
    ParseResult result = ParseResult.getInstance();

    switch (sym.getOperator()) {
      case NONE:
        handleParseBlock(sym, context, result, Template.PARSER_AMPERS_STRING, true);

        rbean.insert(PlaceHolder.ATT, Template.RUNTIME_ATT_SIMPLE.toString(), result);
        rbean.insert(PlaceHolder.METHOD, Template.RUNTIME_ACC_SIMPLE.toString(), result);
        rbean.replaceAll(PlaceHolder.PACK, "", result);
        break;
      case HUT:
        handleParseBlock(sym, context, result, Template.PARSER_AMPERS_STRING01, true);

        String type = sym.upper(sym.getName());
        rbean.insert(PlaceHolder.ATT, Template.RUNTIME_ATT_BOOL.toString(), result);
        rbean.insert(PlaceHolder.METHOD, Template.RUNTIME_ACC_BOOL.toString(), result);
        rbean.replaceAll(PlaceHolder.RT_TYPE, type, result);
        break;
      case STAR:
        handleParseBlock(sym, context, result, Template.PARSER_AMPERS_STRING0N, false);

        rbean.insert(PlaceHolder.ATT, Template.RUNTIME_ATT_ARRAY.toString(), result);
        rbean.insert(PlaceHolder.METHOD, Template.RUNTIME_ACC_ARRAY.toString(), result);
        break;

      default:
        MessageWriter.E202.store(result, sym.getOperator().name(), pbean.getName());
        break;
    }

    pbean.replaceAll(PlaceHolder.BOOL, (context.isFirst() ? "true" : "false"), result);
    replacePalceholdersForString(sym, pbean, rbean, result);
  }

}

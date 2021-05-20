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
import akdl.sheet.parse.MessageWriter;
import akdl.sheet.parse.ParseResult;

public class SimpleGroupHandler extends AGroupHandler {

  private ParseResult result;

  public SimpleGroupHandler() {
    result = ParseResult.getInstance();
  }

  @Override
  public void process(KeySymbol sym, DefinitionContext context) {
    CodeBeanForParser pbean = context.getPbean();
    if (!sym.isReference()) {
      pbean.addToImports(sym.getDefinition());
    }
    CodeBeanForRuntime rbean = context.getRbean();
    switch (sym.getOperator()) {
      case NONE:
        if (!sym.isReference()) {
          pbean.insert(context.getLevel(), Template.PARSER_KEY_DECL.toString(), result);
          if (sym.isParseRef()) {
            pbean.insert(context.getLevel(), Template.PARSER_REF_SET.toString(), result);
            pbean.insert(PlaceHolder.RT_DECL, Template.PARSER_RT_REF_DECL_GET.toString(), result);
            pbean.replaceAll(PlaceHolder.PACK, getRuntimePath(sym), result);
            pbean.replaceAll(PlaceHolder.VARNAME, sym.getVarName(), result);
          }
          pbean.insert(context.getLevel(), Template.PARSER_KEY_CHECK.toString(), result);
        }

        addRuntimeSimpleAttributesAndMethods(sym.getDefinition(), rbean, result);
        break;
      case HUT:
        pbean.insert(context.getLevel(), Template.PARSER_KEY_DECL.toString(), result);
        pbean.insert(context.getLevel(), Template.PARSER_HUT_BLOCK.toString(), result);
 
        addRuntimeBoolAttributesAndMethods(sym.getDefinition(), rbean, result);
        addRuntimeSimpleAttributesAndMethods(sym.getDefinition(), rbean, result);
        break;
      case STAR:
        pbean.insert(context.getLevel(), Template.PARSER_STAR_SUB_CALL.toString(), result);
        pbean.insert(PlaceHolder.SUBBLOCK, Template.PARSER_STAR_SUB_METHOD.toString(), result);
        pbean.replaceAll(PlaceHolder.LEVEL, ""+(context.getLevel()+1), result);
        pbean.replace(context.getLevel()+1, Template.PARSER_STAR_BLOCK.toString(), result);

        addRuntimeArrayAttributesAndMethods(sym.getDefinition(), rbean, result);
        break;

      default:
        break;
    }
    String type = getRTClassName(sym, rbean);
    pbean.replaceAll(PlaceHolder.BOOL, (context.isFirst() ? "true" : "false"), result);
    pbean.replaceAll(PlaceHolder.INDEX, context.getIndexString(), result);
    pbean.replaceAll(PlaceHolder.PARENT_NAME, rbean.getName(), result);
    pbean.replaceAll(PlaceHolder.TYPE, getPRSClassName(sym, pbean), result);
    pbean.replaceAll(PlaceHolder.NAME, getName(sym), result);
    pbean.replaceAll(PlaceHolder.MNAME, sym.upper(getName(sym)), result);
    pbean.replaceAll(PlaceHolder.RT_TYPE, type, result);
    if (!pbean.addToImports(sym.getDefinition())) {
      pbean.addToImports(pbean.getPack()+".", pbean.getClname());
    }

    rbean.replaceAll(PlaceHolder.NAME, getName(sym), result);
    rbean.replaceAll(PlaceHolder.RT_TYPE, type, result);
    rbean.replaceAll(PlaceHolder.MNAME, sym.upper(getName(sym)), result);
  }

  //
  private String getRuntimePath(KeySymbol sym) {
    String path = sym.getDefinition().getRuntimePath();
    return path.substring(0, path.length()-1);
  }

  /**
   * handle Single Enum Case
   * @param sym
   * @param context
   */
  public void handleSingleEnumCase(EnumSymbol sym, DefinitionContext context) {
    String name = (sym.getVarName() == null ? sym.getName() : sym.getVarName());
    CodeBeanForRuntime rbean = context.getRbean();
    if (context.getIndex() == 0) {
      CodeBeanForParser pbean = context.getPbean();
      if (name.length() > 1) {
        pbean.insert(context.getLevel(), Template.PARSER_ENUM.toString(), result);
      }
      else {
        pbean.insert(context.getLevel(), Template.PARSER_ENUM_CAR.toString(), result);
      }
      if (context.getNode() != null) {
        pbean.replaceAll(PlaceHolder.PACK, context.getNode().getRuntimePath(), result);
        pbean.replaceAll(PlaceHolder.TYPE, context.getNode().getRuntimeClassName(), result);
      }
      else {
        pbean.replaceAll(PlaceHolder.PACK, rbean.getPack(), result);
        pbean.replaceAll(PlaceHolder.TYPE, sym.getClname(), result);
      }
      pbean.replaceAll(PlaceHolder.NAME, rbean.getName(), result);
    }
    rbean.insert(PlaceHolder.ATT, Template.RUNTIME_ATT_ENUM_LINE.toString(), result);
    rbean.replaceAll(PlaceHolder.ENUM_A, sym.getClname(), result);
    rbean.replaceAll(PlaceHolder.ENUM_B, name, result);
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
        pbean.insert(context.getLevel(), Template.PARSER_CHAR_MATCH.toString(), result);
        pbean.addToLeft(PlaceHolder.OPTION);
        break;
      case HUT:
        pbean.insert(context.getLevel(), Template.PARSER_CHAR_MATCH01.toString(), result);

        rbean.insert(PlaceHolder.ATT, Template.RUNTIME_ATT_SIMPLE.toString(), result);
        rbean.insert(PlaceHolder.METHOD, Template.RUNTIME_ACC_SIMPLE.toString(), result);
        break;

      default:
        MessageWriter.E202.store(result, sym.getOperator().name(), pbean.getName());
        break;
    }

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
    CodeBeanForParser pbean = context.getPbean();
    CodeBeanForRuntime rbean = context.getRbean();
    ParseResult result = ParseResult.getInstance();

    switch (sym.getOperator()) {
      case NONE:
        pbean.insert(context.getLevel(), Template.PARSER_KEY_MATCH.toString(), result);
        pbean.replaceAll(PlaceHolder.OPTION, "", result);
        break;
      case HUT:
        String type = sym.upper(sym.getName());
        pbean.insert(context.getLevel(), Template.PARSER_KEY_MATCH01.toString(), result);
        pbean.replaceAll(PlaceHolder.RET_TRUE_OPTION, "", result);
        pbean.replaceAll(PlaceHolder.NAME, sym.getName(), result);
        pbean.replaceAll(PlaceHolder.PARENT_NAME, rbean.getName(), result);
        pbean.replaceAll(PlaceHolder.RT_TYPE, type, result);

        rbean.insert(PlaceHolder.ATT, Template.RUNTIME_ATT_BOOL.toString(), result);
        rbean.insert(PlaceHolder.METHOD, Template.RUNTIME_ACC_BOOL.toString(), result);
        rbean.replaceAll(PlaceHolder.RT_TYPE, type, result);
        break;

      default:
        MessageWriter.E202.store(result, sym.getOperator().name(), pbean.getName());
        break;
    }

    pbean.replaceAll(PlaceHolder.BOOL, (context.isFirst() ? "true" : "false"), result);
    replacePalceholdersForString(sym, pbean, rbean, result);
  }

  @Override
  public void process(PredefinedSymbol sym, DefinitionContext context) {
    super.process(context.getLevel(), sym, context, sym.getOperator());
    context.getPbean().replaceAll(PlaceHolder.BOOL, (context.isFirst() ? "true" : "false"), result);
  }
}

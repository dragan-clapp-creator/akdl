package akdl.graph.gen.proc;

import java.util.ArrayList;

import akdl.code.gen.java.CodeBeanForParser;
import akdl.code.gen.java.CodeBeanForRuntime;
import akdl.code.gen.java.persist.PlaceHolder;
import akdl.code.gen.java.persist.Template;
import akdl.eval.EvaluationInfo;
import akdl.eval.EvaluationInfo.EvalClass;
import akdl.graph.gen.proc.group.AGroupHandler;
import akdl.graph.nodes.DefinitionNode;
import akdl.graph.nodes.GroupOfSymbols;
import akdl.graph.nodes.KeySymbol;
import akdl.graph.nodes.PredefinedSymbol;
import akdl.graph.nodes.elts.ASymbol;
import akdl.graph.nodes.elts.Operator;
import akdl.sheet.keys.AttKeys;
import akdl.sheet.parse.ParseResult;

public class DefinitionContext {

  private DefinitionNode node;
  private DefinitionNode parentNode;

  private SubContext subContext;
  private int level;

  private CodeBeanForParser pbean;   // parser bean
  private CodeBeanForRuntime rbean;   // runtime bean

  private ArrayList<String> doneItems;

  /**
   * CONSTRUCTOR
   * 
   * @param nd definition node
   * @param pb parser bean
   * @param rb runtime bean
   * @param pn parent node
   * 
   */
  public DefinitionContext(DefinitionNode nd, CodeBeanForParser pb, CodeBeanForRuntime rb, DefinitionNode pn) {
    node = nd;
    parentNode = pn;
    pbean = pb;
    rbean = rb;
    subContext = new SubContext(Operator.NONE);
    level = 0;
    doneItems = new ArrayList<>();
  }

  /**
   * @return the pbean
   */
  public CodeBeanForParser getPbean() {
    return pbean;
  }

  /**
   * @return the rbean
   */
  public CodeBeanForRuntime getRbean() {
    return rbean;
  }

  /**
   * @return the index
   */
  public int getIndex() {
    return subContext.getIndex();
  }

  /**
   * @return the index
   */
  public String getIndexString() {
    return ""+subContext.getIndex();
  }

  /**
   * @param index the index to set
   */
  public void setIndex(int index) {
    subContext.setIndex(index);
  }

  /**
   * @return the node
   */
  public DefinitionNode getNode() {
    return node;
  }

  /**
   * @return the parentNode
   */
  public DefinitionNode getParentNode() {
    return parentNode;
  }

  public void setType(AttKeys type) {
    subContext.setType(type);
  }

  public AGroupHandler getHandler() {
    return subContext.getHandler();
  }

  /**
   * push current sub-context and create a new one
   * @param operator
   * @return 
   */
  public void setupNewContext(Operator operator) {
    ParseResult result = ParseResult.getInstance();
    switch (operator) {
      case NONE:
        if (node.getSyntax().size() > 1) {
          pbean.insert(level++, Template.PARSER_SIMPLE_SUB_CALL.toString(), result);
          pbean.insert(PlaceHolder.SUBBLOCK, Template.PARSER_SIMPLE_SUB_METHOD.toString(), result);
          pbean.replaceAll(PlaceHolder.LEVEL, ""+level, result);
          pbean.replaceAll(PlaceHolder.INDEX, getIndexString(), result);
        }
        break;
      case AMPERS:
        pbean.insert(level++, Template.PARSER_AMPERS_SUB_CALL.toString(), result);
        pbean.insert(PlaceHolder.SUBBLOCK, Template.PARSER_AMPERS_SUB_METHOD.toString(), result);
        pbean.replaceAll(PlaceHolder.INDEX, getIndexString(), result);
        break;
      case HUT:
        if (node.getSyntax().size() > 1) {
          pbean.insert(level++, Template.PARSER_HUT_SUB_CALL.toString(), result);
          pbean.insert(PlaceHolder.SUBBLOCK, Template.PARSER_HUT_SUB_METHOD.toString(), result);
          pbean.replaceAll(PlaceHolder.LEVEL, ""+level, result);
          pbean.replaceAll(PlaceHolder.INDEX, getIndexString(), result);
        }
        break;
      case STAR:
        if (node.getSyntax().size() > 1) {
          pbean.insert(level++, Template.PARSER_STAR_SUB_CALL.toString(), result);
          pbean.insert(PlaceHolder.SUBBLOCK, Template.PARSER_STAR_SUB_METHOD.toString(), result);
          pbean.replaceAll(PlaceHolder.LEVEL, ""+level, result);
          pbean.replaceAll(PlaceHolder.INDEX, getIndexString(), result);
        }
        break;
      case PLUS:
        if (node.getType() != AttKeys.ATT_ENUM) {
          pbean.insert(level++, Template.PARSER_PLUS_SUB_CALL.toString(), result);
          pbean.insert(PlaceHolder.SUBBLOCK, Template.PARSER_PLUS_SUB_METHOD.toString(), result);
          pbean.replaceAll(PlaceHolder.LEVEL, ""+level, result);
        }
        break;

      default:
        break;
    }

    SubContext.pushSubContext(subContext);

    subContext = new SubContext(operator);
  }

  /**
   * pop previous sub-context
   * @param bean
   */
  public void popPreviousContext() {
    subContext = SubContext.popSubContext();
    pbean.removeCodeline(level--);
  }

  public AttKeys getSimpleType() {
    return subContext.getType();
  }

  /**
   * @return the level
   */
  public int getLevel() {
    return level;
  }

  /**
   * check if node's current index corresponds to a syntax element
   * considered as first (omit optional)
   * 
   * @return
   */
  public boolean isFirst() {
    int i = getIndex();
    if (i > 0) {
      for (int j=0; j<i; j++) {
        ASymbol sym = node.getSyntax().get(j);
        if (sym.getOperator() != Operator.HUT && sym.getOperator() != Operator.STAR) {
          return false;
        }
      }
    }
    return true;
  }

  public void insertTraverseContent(ArrayList<ASymbol> syntax, boolean isArray, PlaceHolder pl, String indent) {
    doneItems.add(node.getName());
    EvaluationInfo defInfo = node.getEvaluationInfo();
    if (defInfo.getEval() == EvalClass.CUSTOM) {
      rbean.insert(pl, Template.RUNTIME_TRAVERSE_CONTENT_PUSH.toString(), ParseResult.getInstance());
      rbean.replaceAll(PlaceHolder.TNAME, "this", ParseResult.getInstance());
      rbean.replaceAll(PlaceHolder.INDENT, indent, ParseResult.getInstance());
    }
    for (ASymbol sym : syntax) {
      processPredefined(sym, pl, indent);
    }
    for (ASymbol sym : syntax) {
      processKeyOrGroup(defInfo.getPname(), sym, isArray, pl, indent);
    }
    for (ASymbol sym : syntax) {
      processOperator(sym, isArray, pl, indent);
    }
  }

  //
  private void processOperator(ASymbol sym, boolean isArray, PlaceHolder pl, String indent) {
    if (sym instanceof KeySymbol) {
      KeySymbol ksym = (KeySymbol) sym;
      if (ksym.getDefinition() != null) {
        if (ksym.getDefinition().getEval() != null) {
          EvaluationInfo symInfo = ksym.getDefinition().getEvaluationInfo();
          if (symInfo.isOperator()) {
            String replacement = buildupReplacementString(ksym, isArray);
            rbean.insert(pl, replacement, ParseResult.getInstance());
            String name = sym.getName();
            rbean.replaceAll(PlaceHolder.TNAME, name, ParseResult.getInstance());
            rbean.replaceAll(PlaceHolder.UTNAME, sym.upper(name), ParseResult.getInstance());
            rbean.replaceAll(PlaceHolder.INDENT, indent, ParseResult.getInstance());
            rbean.replaceAll(PlaceHolder.RT_TYPE, ksym.getDefinition().getRuntimeClassName(), ParseResult.getInstance());
          }
        }
      }
    }
  }

  //
  private void processPredefined(ASymbol sym, PlaceHolder pl, String indent) {
    if (sym instanceof PredefinedSymbol) {
      if (((PredefinedSymbol) sym).isReference()) {
        rbean.insert(pl, Template.RUNTIME_TRAVERSE_UE_CONTENT.toString(), ParseResult.getInstance());
        rbean.replaceAll(PlaceHolder.INDENT, indent, ParseResult.getInstance());
        EvaluationInfo info = node.getEvaluationInfo();
        String pname = info.getPname();
        rbean.replaceAll(PlaceHolder.UTNAME, pname, ParseResult.getInstance());
      }
      else {
        rbean.insert(pl, Template.RUNTIME_TRAVERSE_CONTENT_PUSH.toString(), ParseResult.getInstance());
      }
      String name = sym.getName();
      rbean.replaceAll(PlaceHolder.TNAME, name, ParseResult.getInstance());
      rbean.replaceAll(PlaceHolder.INDENT, indent, ParseResult.getInstance());
    }
  }

  //
  private void processKeyOrGroup(String pname, ASymbol sym, boolean isArray, PlaceHolder pl, String indent) {
    String name = sym.getName();
    if (sym instanceof KeySymbol) {
      KeySymbol ksym = (KeySymbol) sym;
      if (ksym.getDefinition().getEval() != null) {
        EvaluationInfo symInfo = ksym.getDefinition().getEvaluationInfo();
        if (!symInfo.isOperator()) {
          String replacement = buildupReplacementString(ksym, isArray);
          rbean.insert(pl, replacement, ParseResult.getInstance());
          rbean.replaceAll(PlaceHolder.TNAME, name, ParseResult.getInstance());
          rbean.replaceAll(PlaceHolder.UTNAME, sym.upper(name), ParseResult.getInstance());
          rbean.replaceAll(PlaceHolder.INDENT, indent, ParseResult.getInstance());
          rbean.replaceAll(PlaceHolder.RT_TYPE, ksym.getDefinition().getRuntimeClassName(), ParseResult.getInstance());
        }
      }
    }
    else if (sym instanceof GroupOfSymbols) {
      if (sym.getOperator() == Operator.STAR) {
        rbean.insert(pl, Template.RUNTIME_TRAVERSE_ARRAY_CONDITION.toString(), ParseResult.getInstance());
        rbean.replaceAll(PlaceHolder.INDENT, indent, ParseResult.getInstance());
        rbean.replaceAll(PlaceHolder.ITRAVERSE, Template.RUNTIME_TRAVERSE_ARRAY.toString(), ParseResult.getInstance());
        rbean.replaceAll(PlaceHolder.INDENT, indent, ParseResult.getInstance());
        indent += "  ";
        isArray = true;
      }
      else if (sym.getOperator() == Operator.HUT) {
        rbean.insert(pl, Template.RUNTIME_TRAVERSE_SIMPLE_CONDITION.toString(), ParseResult.getInstance());
        rbean.replaceAll(PlaceHolder.INDENT, indent, ParseResult.getInstance());
        replaceTraverseContent(((GroupOfSymbols)sym).getSyntax(), isArray, PlaceHolder.ITRAVERSE, indent+"  ");
      }
      insertTraverseContent(((GroupOfSymbols)sym).getSyntax(), isArray, PlaceHolder.FTRAVERSE, indent+"  ");
    }
  }

  //
  private void replaceTraverseContent(ArrayList<ASymbol> syntax, boolean isArray, PlaceHolder pl, String indent) {
    EvaluationInfo defInfo = node.getEvaluationInfo();
    for (ASymbol sym : syntax) {
      processPredefined(sym, pl, indent);
      rbean.replaceAll(PlaceHolder.UTNAME, sym.upper(sym.getName()), ParseResult.getInstance());
    }
    for (ASymbol sym : syntax) {
      if (sym instanceof KeySymbol) {
        processKeyOrGroup(defInfo.getPname(), sym, isArray, pl, indent);
      }
      else if (sym instanceof GroupOfSymbols) {
        replaceTraverseContent(((GroupOfSymbols)sym).getSyntax(), isArray, pl, indent+"  ");
      }
      rbean.replaceAll(PlaceHolder.UTNAME, sym.upper(sym.getName()), ParseResult.getInstance());
    }
    for (ASymbol sym : syntax) {
      processOperator(sym, isArray, pl, indent);
      rbean.replaceAll(PlaceHolder.UTNAME, sym.upper(sym.getName()), ParseResult.getInstance());
    }
  }

  //
  private String buildupReplacementString(KeySymbol sym, boolean isArray) {
    String replacement = "";
    if (sym.getDefinition().getType() == AttKeys.ATT_ENUM) {
      replacement = rbean.isCustom() ?
          Template.RUNTIME_TRAVERSE_CONTENT_PUSH.toString() :
          Template.RUNTIME_TRAVERSE_CONTENT_PUSH_NAME.toString();
    }
    else if (sym.getDefinition().getType() == AttKeys.ATT_INTERFACE) {
      if (isArray) {
        return Template.RUNTIME_TRAVERSE_CONTENT_ARRAY_ACCEPT.toString();
      }
      String accept = Template.RUNTIME_TRAVERSE_CONTENT_ACCEPT.toString();
      if (sym.getOperator() == Operator.HUT) {
        replacement = Template.RUNTIME_TRAVERSE_SIMPLE_CONDITION.toString();
        replacement = replacement.replace(PlaceHolder.ITRAVERSE.getString(), accept);
      }
      else if (sym.getOperator() == Operator.STAR) {
        replacement = Template.RUNTIME_TRAVERSE_CONTENT_VIS.toString();
        replacement = replacement.replace(PlaceHolder.ITRAVERSE.getString(), accept);
      }
      else {
        replacement = accept;
      }
    }
    else {
      String simple = Template.RUNTIME_TRAVERSE_CONTENT_SIMPLE.toString();
      if (sym.getOperator() == Operator.HUT) {
        replacement = Template.RUNTIME_TRAVERSE_SIMPLE_CONDITION.toString();
        replacement = replacement.replace(PlaceHolder.ITRAVERSE.getString(), simple);
      }
      else if (sym.getOperator() == Operator.STAR) {
        replacement = Template.RUNTIME_TRAVERSE_ARRAY.toString();
        replacement = replacement.replace(PlaceHolder.ITRAVERSE.getString(), simple);
      }
      else {
        replacement = Template.RUNTIME_TRAVERSE_CONTENT_SIMPLE.toString();
      }
    }
    if (isArray) {
      replacement = replacement.replaceAll("%TNAME%", "%TNAME%s.get(i)");
    }
    return replacement;
  }

  /**
   * used to traverse items only once
   * @param def
   * @return
   */
  public boolean isDone(DefinitionNode def) {
    return doneItems.contains(def.getName());
  }
}

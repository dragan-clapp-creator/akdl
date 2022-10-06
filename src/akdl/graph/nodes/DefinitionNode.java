package akdl.graph.nodes;

import java.util.ArrayList;

import akdl.code.gen.java.CodeBeanForParser;
import akdl.code.gen.java.CodeBeanForRuntime;
import akdl.code.gen.java.CodeBeanHandler;
import akdl.code.gen.java.persist.PlaceHolder;
import akdl.code.gen.java.persist.Template;
import akdl.eval.EvaluationHandler;
import akdl.eval.EvaluationInfo;
import akdl.eval.EvaluationInfo.EvalClass;
import akdl.eval.EvaluationInfo.EvalType;
import akdl.graph.Graph;
import akdl.graph.gen.proc.DefinitionContext;
import akdl.graph.nodes.elts.ASymbol;
import akdl.graph.nodes.elts.Operator;
import akdl.sheet.keys.AttKeys;
import akdl.sheet.parse.IMarker;
import akdl.sheet.parse.ParseResult;

public class DefinitionNode extends ASymbol implements IMarker {

  private static final long serialVersionUID = 2324671434977583098L;

  private ArrayList<ASymbol> syntax;
  private AttKeys type;
  private String parserPath;
  private String parserClassName;
  private String runtimePath;
  private String runtimeClassName;
  private EvalType eval;

  private boolean isMarked;

  private EvaluationInfo evaluationInfo;

  /**
   * constructor
   * 
   * @param n
   */
  public DefinitionNode(String n) {
    super(n, Operator.NONE);
    if (n != null) {
      parserClassName = upper(n);
      runtimeClassName = parserClassName;
    }
    syntax = new ArrayList<ASymbol>();
    if (!(this instanceof GroupOfSymbols)) {
      ParseResult.getInstance().addToDefinedKeys(n);
    }
  }

  /**
   * @return the syntax
   */
  public ArrayList<ASymbol> getSyntax() {
    return syntax;
  }

  /**
   * @param n the symbol to add to the syntax
   */
  public void addToSyntax(ASymbol n) {
    this.syntax.add(n);
  }

  /**
   * @return the type
   */
  public AttKeys getType() {
    return type;
  }

  /**
   * @param type the type to set
   */
  public void setType(AttKeys type) {
    this.type = type;
  }

  /**
   * @return the parserPath
   */
  public String getParserPath() {
    if (parserPath == null) {
      parserPath = Graph.DEFAULT_PARSER_PACKAGE;
    }
    return parserPath;
  }

  /**
   * @param parserPath the parserPath to set
   */
  public void setParserPath(String parserPath) {
    this.parserPath = parserPath;
  }

  /**
   * @return the runtimerPath
   */
  public String getRuntimePath() {
    if (runtimePath == null) {
      runtimePath = Graph.DEFAULT_RUNTIME_PACKAGE;
    }
    return runtimePath;
  }

  /**
   * @param runtimerPath the runtimerPath to set
   */
  public void setRuntimerPath(String runtimerPath) {
    this.runtimePath = runtimerPath;
  }

  /**
   * @return the parserClassName
   */
  public String getParserClassName() {
    return parserClassName;
  }

  /**
   * @return the runtimeClassName
   */
  public String getRuntimeClassName() {
    return runtimeClassName;
  }

  public String toString() {
    return getString("");
  }

  protected String getString(String indent) {
    return indent + "<" + getName() + getEvaluator() + getChildren(indent + "+--");
  }

  //
  private String getEvaluator() {
    return eval == null ? "" : ":"+eval.name();
  }

  //
  private String getChildren(String indent) {
    String s = "> [" + getPack() + "] -> ";
    if (!isMarked()) {
      mark();
      for (ASymbol n : syntax) {
        s += "\n" + n.getInformation(indent);
      }
      unmark();
    }
    else {
      s += "...";
    }
    return s;
  }

  public String getPack() {
    return (parserPath == null ? "" : parserPath) + ","
        + (runtimePath == null ? "" : runtimePath);
  }

  @Override
  public String getInformation(String indent) {
    return indent + getName();
  }

  public void dump() {
    dump("");
  }

  //
  private void dump(String indent) {
    System.out.println(indent + "<" + getName() + "> [" + type.getName() + ":" + getPack() + "] -> ");
    dumpChildren(indent+"+--");
  }

  @Override
  public void dumpChildren(String indent) {
    if (!isMarked()) {
      mark();
      for (ASymbol n : syntax) {
        System.out.println( n.getInformation(indent) );
        if (n instanceof KeySymbol && ((KeySymbol) n).getDefinition() != null) {
          ((KeySymbol) n).getDefinition().dumpChildren(indent+"+--");
        }
        else if (n instanceof DefinitionNode) {
          n.dumpChildren(indent+"+--");
        }
      }
      unmark();
    }
  }

  @Override
  public boolean isMarked() {
    return isMarked;
  }

  @Override
  public void mark() {
    isMarked = true;
  }

  @Override
  public void unmark() {
    isMarked = false;
  }

  /**
   * propagate parser and runtime packages through the graph if not set
   * 
   * @param parse
   * @param run
   */
  public void propagatePackagesAndSetType(String parse, String run) {
    if (!isMarked()) {
      mark();
      if (parserPath == null) {
        parserPath = parse;
      }
      else {
        int i = parserPath.lastIndexOf('.')+1;
        if (i == 0) {
          parserClassName = upper(parserPath);
          parserPath = parse;
        }
        else {
          parse = parserPath.substring(0, i);
          if (parserPath.length() > i) {
            parserClassName = upper(parserPath.substring(i));
          }
          parserPath = parse;
        }
      }
      if (runtimePath == null || runtimePath.isEmpty()) {
        runtimePath = run;
      }
      else {
        int i = runtimePath.lastIndexOf('.')+1;
        if (i == 0) {
          runtimeClassName = upper(runtimePath);
          runtimePath = run;
        }
        else {
          run = runtimePath.substring(0, i);
          if (runtimePath.length() > i) {
            runtimeClassName = upper(runtimePath.substring(i));
          }
          runtimePath = run;
        }
      }
      if (type == null) {
        if (!syntax.isEmpty() && syntax.get(0) instanceof GroupOfSymbols
            && ((GroupOfSymbols)syntax.get(0)).getOperator() == Operator.PLUS) {
          type = AttKeys.ATT_INTERFACE;
        }
        else {
          type = AttKeys.ATT_CLASS;
        }
      }
      else if (syntax.size() == 1 && syntax.get(0) instanceof GroupOfSymbols) {
        ((GroupOfSymbols)syntax.get(0)).setType(type);
      }
      registerEvaluation();
      for (ASymbol as : syntax) {
        as.propagatePackagesAndSetType(parse, run);
      }
      unmark();
    }
  }

  public void registerEvaluation() {
    if (eval != null) {
      String path = runtimePath.substring(0, runtimePath.length()-1);
      switch (eval) {
        case CUSTOM_ORG:
          evaluationInfo = EvaluationHandler.getInstance().createInitialEvaluationInfo(eval, EvalClass.CUSTOM, path, runtimeClassName, "Object");
          break;
        case LOGIC_ORG:
          evaluationInfo = EvaluationHandler.getInstance().createInitialEvaluationInfo(eval, EvalClass.LOGIC, path, runtimeClassName, "Boolean");
          break;
        case NUMERIC_ORG:
          evaluationInfo = EvaluationHandler.getInstance().createInitialEvaluationInfo(eval, EvalClass.NUMERIC, path, runtimeClassName, "Double");
          break;

        default:
          evaluationInfo = EvaluationHandler.getInstance().createEvaluationInfo(eval);
          break;
      }
    }
  }

  /**
   * code generation entry point
   * 
   * @param result  messages gatherer
   * @
   */
  public void initialTraverse() {
    CodeBeanHandler cbh = CodeBeanHandler.getInstance();
    CodeBeanForParser pbean0 = cbh.getParserBean(getParserPath(), "AParser", Template.PARSER_MAIN_ABS);

    DefinitionContext context = setupContext(null, Template.PARSER_MAIN);

    traverseDefinition(context);

    pbean0.replaceAll(PlaceHolder.PARSER, cbh.getMainParser(), ParseResult.getInstance());
    pbean0.replaceAll(PlaceHolder.PACK, pbean0.getPack(), ParseResult.getInstance());

    if (eval != null) {
      createAdditionalObjects();
    }
  }

  private void createAdditionalObjects() {
    ParseResult result = ParseResult.getInstance();

    CodeBeanForRuntime ueInterface = null;
    CodeBeanForRuntime evaluator = null;
    CodeBeanForRuntime handler = null;
    switch (evaluationInfo.getEval()) {
      case CUSTOM:
        ueInterface = new CodeBeanForRuntime(Template.RUNTIME_CUSTOM_UE_INTERFACE, evaluationInfo.getPack(), "I"+evaluationInfo.getPname()+"UserExit");
        evaluator = new CodeBeanForRuntime(Template.RUNTIME_CUSTOM_EVALUATOR, evaluationInfo.getPack(), "AEvaluator");
        handler = new CodeBeanForRuntime(Template.RUNTIME_CUSTOM_UE_HANDLER, evaluationInfo.getPack(), evaluationInfo.getPname()+"UserExitHandler");
        break;
      case LOGIC:
        ueInterface = new CodeBeanForRuntime(Template.RUNTIME_UE_INTERFACE, evaluationInfo.getPack(), "I"+evaluationInfo.getPname()+"UserExit");
        evaluator = new CodeBeanForRuntime(Template.RUNTIME_LOGIC_EVALUATOR, evaluationInfo.getPack(), "AEvaluator");
        handler = new CodeBeanForRuntime(Template.RUNTIME_UE_HANDLER, evaluationInfo.getPack(), evaluationInfo.getPname()+"UserExitHandler");
        break;
      case NUMERIC:
        ueInterface = new CodeBeanForRuntime(Template.RUNTIME_UE_INTERFACE, evaluationInfo.getPack(), "I"+evaluationInfo.getPname()+"UserExit");
        evaluator = new CodeBeanForRuntime(Template.RUNTIME_NUMERIC_EVALUATOR, evaluationInfo.getPack(), "AEvaluator");
        handler = new CodeBeanForRuntime(Template.RUNTIME_UE_HANDLER, evaluationInfo.getPack(), evaluationInfo.getPname()+"UserExitHandler");
        break;

      default:
        break;
    }
    ueInterface.replaceAll(PlaceHolder.PACK, evaluationInfo.getPack(), result);
    ueInterface.replaceAll(PlaceHolder.TYPE, evaluationInfo.getPname(), result);
    ueInterface.replaceAll(PlaceHolder.RT_TYPE, evaluationInfo.getRTtype(), result);
    CodeBeanHandler.getInstance().register(ueInterface);

    evaluator.replaceAll(PlaceHolder.PACK, evaluationInfo.getPack(), result);
    evaluator.replaceAll(PlaceHolder.TYPE, evaluationInfo.getPname(), result);
    CodeBeanHandler.getInstance().register(evaluator);

    handler.replaceAll(PlaceHolder.PACK, evaluationInfo.getPack(), result);
    handler.replaceAll(PlaceHolder.TYPE, evaluationInfo.getPname(), result);
    handler.replaceAll(PlaceHolder.RT_TYPE, evaluationInfo.getRTtype(), result);
    CodeBeanHandler.getInstance().register(handler);
  }

  /**
   * 
   * @param parentNode 
   * @param cbh
   * @param template 
   * @return
   */
  public DefinitionContext setupContext(DefinitionNode parentNode, Template template) {
    CodeBeanHandler cbh = CodeBeanHandler.getInstance();
    CodeBeanForRuntime parentBean = parentNode == null ? null :
        cbh.findRuntimeBean(parentNode.getRuntimePath(), parentNode.getRuntimeClassName());

    CodeBeanForParser pbean = cbh.getParserBean(parserPath, parserClassName, template);

    CodeBeanForRuntime rbean = cbh.getRuntimeBean(parentBean, parentNode, this, runtimePath, runtimeClassName, getRuntimeTemplate());
    rbean.setImplements(this, parentBean, parentNode, cbh);

    return new DefinitionContext(this, pbean, rbean, parentNode);
  }

  /**
   * 
   * @param pbean   parser skeleton
   * @param rbean   runtime skeleton
   * 
   */
  public void traverseDefinition(DefinitionContext context) {
    if (!isMarked()) {
      mark();

      ParseResult result = ParseResult.getInstance();
      CodeBeanForParser pbean = context.getPbean();
      setParserPackageAndName(pbean, result);
      CodeBeanForRuntime rbean = context.getRbean();
      setParserRTdeclaration(pbean, rbean , result);
      setRuntimePackageAndName(rbean, result);

      traverse(context);

      pbean.replaceAll(PlaceHolder.PARSER, CodeBeanHandler.getInstance().getMainParser(), result);
      pbean.addToLeft(PlaceHolder.SUBBLOCK);
      pbean.finalStep(result);
      rbean.addToLeft(PlaceHolder.ACCEPT);

      if (eval == EvalType.NUMERIC_ORG || eval == EvalType.LOGIC_ORG || eval == EvalType.CUSTOM_ORG) {
        createAdditionalObjects();
      }
    }
  }

  //
  private Template getRuntimeTemplate() {
    switch (type) {
      case ATT_CLASS:
        return Template.RUNTIME_CLASS;
      case ATT_ENUM:
        return Template.RUNTIME_ENUM;
      case ATT_INTERFACE:
        return Template.RUNTIME_INTERFACE;

      default:
        break;
    }
    return null;
  }

  @Override
  public void traverse(DefinitionContext context) {
    context.setType(type);
    for (int i=0; i<syntax.size(); i++) {
      ASymbol as = syntax.get(i);
      if ("DETACHED".equals(as.getName())) {
        continue;
      }
      context.setIndex(i);
      as.traverse(context);
    }
    if (getOperator() == Operator.NONE &&
        type != AttKeys.ATT_ENUM && type != AttKeys.ATT_INTERFACE &&
        getEval() != null && !context.isDone(this)) {
      context.insertTraverseContent(syntax, false, PlaceHolder.TRAVERSE, "");
    }
  }

  //
  private void setParserPackageAndName(CodeBeanForParser pbean, ParseResult result)
      {
    pbean.replaceAll(PlaceHolder.PACK, pbean.getPack(), result);
    pbean.replaceAll(PlaceHolder.TYPE, pbean.getClname(), result);
  }

  //
  private void setParserRTdeclaration(CodeBeanForParser pbean, CodeBeanForRuntime rbean, ParseResult result) {
    if (rbean.isInterface()) {
      pbean.insert(PlaceHolder.RT_DECL, Template.PARSER_RT_IDECL_GET.toString(), result);
    }
    else if (type == AttKeys.ATT_ENUM) {
      pbean.insert(PlaceHolder.RT_DECL, Template.PARSER_RT_FULL_DECL.toString(), result);
    }
    else {
      pbean.insert(PlaceHolder.RT_DECL, Template.PARSER_RT_DECL_GET.toString(), result);
    }
    pbean.replaceAll(PlaceHolder.PACK, rbean.getPack(), result);
    pbean.replaceAll(PlaceHolder.TYPE, rbean.getClname(), result);
    pbean.replaceAll(PlaceHolder.NAME, rbean.getName(), result);
  }

  //
  private void setRuntimePackageAndName( CodeBeanForRuntime rbean, ParseResult result) {
    rbean.replaceAll(PlaceHolder.PACK, rbean.getPack(), result);
    rbean.replaceAll(PlaceHolder.TYPE, rbean.getClname(), result);
    if (eval != null) {
      rbean.replaceAll(PlaceHolder.PACK0, evaluationInfo.getPack(), result);
    }
  }

  /**
   * @return the eval
   */
  public EvalType getEval() {
    return eval;
  }

  /**
   * @param eval the eval to set
   */
  public void setEval(EvalType eval) {
    this.eval = eval;
  }

  /**
   * @return the evaluationInfo
   */
  public EvaluationInfo getEvaluationInfo() {
    return evaluationInfo;
  }

  /**
   * @param evaluationInfo the evaluationInfo to set
   */
  public void setEvaluationInfo(EvaluationInfo evaluationInfo) {
    this.evaluationInfo = evaluationInfo;
  }
}

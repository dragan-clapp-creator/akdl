package akdl.graph.nodes;

import akdl.code.gen.java.persist.Template;
import akdl.graph.gen.proc.DefinitionContext;
import akdl.graph.nodes.elts.ASymbol;
import akdl.graph.nodes.elts.Operator;
import akdl.sheet.parse.IMarker;
import akdl.sheet.parse.ParseResult;

public class KeySymbol extends ASymbol implements IMarker {

  private static final long serialVersionUID = 2501498052539445285L;

  private boolean isReference;

  private String clname;
  private String varName;

  private boolean isParseRef;

  private DefinitionNode definition;

  private boolean isMarked;


  /**
   * constructor
   * 
   * @param n
   * @param op
   * @param b
   */
  public KeySymbol(String n, Operator op, boolean b) {
    super(n, op);
    clname = upper(n);
    isReference = b;
    ParseResult.getInstance().addToUndefinedKeys(n);
  }

  public boolean isReference() {
    return isReference;
  }

  /**
   * @param isReference the isReference to set
   */
  public void setReference(boolean isReference) {
    this.isReference = isReference;
  }

  /**
   * @return the definition
   */
  public DefinitionNode getDefinition() {
    return definition;
  }

  /**
   * @param definition the definition to set
   */
  public void setDefinition(DefinitionNode definition) {
    this.definition = definition;
    definition.setParent(this);
  }

  /**
   * @return the varName
   */
  public String getVarName() {
    return varName;
  }

  /**
   * @param name the varName to set
   */
  public void setVarName(String name) {
    this.varName = name;
  }

  /**
   * @return the clname
   */
  public String getClname() {
    return clname;
  }

  /**
   * @return the isParseRef
   */
  public boolean isParseRef() {
    return isParseRef;
  }

  /**
   * @param isParseRef the isParseRef to set
   */
  public void setParseRef(boolean isParseRef) {
    this.isParseRef = isParseRef;
  }

  @Override
  public String getInformation(String indent) {
    return indent + (isReference ? "REF:" : "") + getName()
        + (varName != null ? ":"+varName : "")
        + "(op:" + getOperator().name()
        + (isParseRef ? ", ParseReference" : "") + ")";
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

  @Override
  public void propagatePackagesAndSetType(String parse, String run) {
    if (!isMarked()) {
      mark();
      if (definition != null) {
        definition.propagatePackagesAndSetType(parse, run);
      }
      unmark();
    }
  }

  @Override
  public void dumpChildren(String indent) {
    System.out.println( getInformation(indent) );
    if (definition != null) {
      definition.dumpChildren(indent);
    }
  }

  @Override
  public void traverse(DefinitionContext context) {
    if (!isMarked()) {
      mark();
 
      context.getHandler().process(this, context);

      if (definition != null) {
        DefinitionContext ctx = definition.setupContext(context.getNode(), Template.PARSER_CLASS);
        definition.traverseDefinition(ctx);
      }
    }
  }
}

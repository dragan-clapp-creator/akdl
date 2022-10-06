package akdl.graph.nodes;

import akdl.graph.gen.proc.DefinitionContext;
import akdl.graph.gen.proc.group.PlusGroupHandler;
import akdl.graph.gen.proc.group.SimpleGroupHandler;
import akdl.graph.nodes.elts.ASymbol;
import akdl.graph.nodes.elts.Operator;
import akdl.sheet.parse.ParseResult;

public class EnumSymbol extends ASymbol {

  private static final long serialVersionUID = 2501498052539445285L;

  private String clname;
  private String varName;


  /**
   * constructor
   * 
   * @param n
   * @param op
   */
  public EnumSymbol(String n, Operator op) {
    super(n, op);
    clname = upper(n);
    ParseResult.getInstance().addToDefinedKeys(n);
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

  @Override
  public String getInformation(String indent) {
    return indent + getName() + "[" + (varName != null ? varName : "") + "]";
  }

  @Override
  public void propagatePackagesAndSetType(String parse, String run) {
  }

  @Override
  public void dumpChildren(String indent) {
    System.out.println( getInformation(indent) );
  }

  @Override
  public void traverse(DefinitionContext context) {
    if (context.getHandler() instanceof PlusGroupHandler) {
      ((PlusGroupHandler)context.getHandler()).processEnum(this, context);
    }
    else if (context.getHandler() instanceof SimpleGroupHandler) {
      ((SimpleGroupHandler)context.getHandler()).handleSingleEnumCase(this, context);
    }
  }
}

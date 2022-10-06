package akdl.graph.nodes;

import akdl.graph.gen.proc.DefinitionContext;
import akdl.graph.nodes.elts.ASymbol;
import akdl.graph.nodes.elts.Operator;
import akdl.sheet.keys.LeafType;

public class PredefinedSymbol extends ASymbol {

  private static final long serialVersionUID = -359166907320777281L;

  private LeafType type;
  private boolean isReference;

  /**
   * constructor
   * 
   * @param t
   * @param n
   * @param b
   * @param op
   */
  public PredefinedSymbol(LeafType t, String n, boolean b, Operator op) {
    super(n, op);
    type = t;
    isReference = b;
  }

  public LeafType getType() {
    return type;
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

  @Override
  public String getInformation(String indent) {
    return indent + (isReference ? "REF:" : "") + getName() + "(" + type.name() + ")";
  }

  @Override
  public void propagatePackagesAndSetType(String parse, String run) {
  }

  @Override
  public void dumpChildren(String indent) {
  }

  @Override
  public void traverse(DefinitionContext context) {
    context.getHandler().process(this, context);
  }
}
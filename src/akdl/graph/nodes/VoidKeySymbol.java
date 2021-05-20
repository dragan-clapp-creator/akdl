package akdl.graph.nodes;

import akdl.graph.gen.proc.DefinitionContext;
import akdl.graph.nodes.elts.ASymbol;
import akdl.graph.nodes.elts.Operator;

public class VoidKeySymbol extends ASymbol {

  private static final long serialVersionUID = -8023834168239196106L;

  /**
   * constructor
   * 
   * @param k
   * @param op
   */
  public VoidKeySymbol(String k, Operator op) {
    super(k, op);
  }

  @Override
  public String getInformation(String indent) {
    return indent + "void key " + getName();
  }

  @Override
  public void propagatePackagesAndSetType(String parse, String run) {
  }

  @Override
  public void dumpChildren(String indent) {
  }

  @Override
  public void traverse(DefinitionContext context) {
  }
}

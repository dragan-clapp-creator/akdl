package akdl.graph.nodes;

import akdl.graph.gen.proc.DefinitionContext;
import akdl.graph.nodes.elts.ASymbol;
import akdl.graph.nodes.elts.Operator;

public class StringSymbol extends ASymbol {

  private static final long serialVersionUID = 7197249623213893620L;

  /**
   * constructor
   * 
   * @param k
   * @param op 
   */
  public StringSymbol(String k, Operator op) {
    super(k, op);
  }

  @Override
  public String getInformation(String indent) {
    return indent + "key " + getName();
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

package akdl.graph.nodes;

import akdl.graph.gen.proc.DefinitionContext;
import akdl.graph.nodes.elts.Operator;

public class GroupOfSymbols extends DefinitionNode {

  private static final long serialVersionUID = -8727552181070743901L;


  public GroupOfSymbols(String n, Operator op) {
    super(n);
    setOperator(op);
  }

  @Override
  public void traverse(DefinitionContext context) {
    context.setupNewContext(getOperator());
    setType(context.getNode().getType());

    super.traverse(context);

    context.popPreviousContext();
  }
}

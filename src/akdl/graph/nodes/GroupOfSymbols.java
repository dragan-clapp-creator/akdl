package akdl.graph.nodes;

import akdl.graph.gen.proc.DefinitionContext;
import akdl.graph.nodes.elts.Operator;

public class GroupOfSymbols extends DefinitionNode {

  private static final long serialVersionUID = -8727552181070743901L;

  private Operator operator;

  public GroupOfSymbols(String n, Operator op) {
    super(n);
    operator = op;
  }

  /**
   * @return the operator
   */
  public Operator getOperator() {
    return operator;
  }

  /**
   * @param operator the operator to set
   */
  public void setOperator(Operator operator) {
    this.operator = operator;
  }

  @Override
  public void traverse(DefinitionContext context) {
    context.setupNewContext(operator);
    setType(context.getNode().getType());

    super.traverse(context);

    context.popPreviousContext();
  }
}

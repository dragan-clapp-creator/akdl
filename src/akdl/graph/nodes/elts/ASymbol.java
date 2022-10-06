package akdl.graph.nodes.elts;

import java.io.Serializable;

import akdl.graph.gen.proc.DefinitionContext;

abstract public class ASymbol implements Serializable {

  private static final long serialVersionUID = 2818557218049112410L;

  private String name;
  private Operator operator;
  private boolean isSerialized;
  private ASymbol parent;

  public ASymbol(String n, Operator op) {
    name = n;
    operator = op;
  }

  abstract public void dumpChildren(String indent);
  abstract public String getInformation(String indent);
  abstract public void propagatePackagesAndSetType(String parse, String run);
  abstract public void traverse(DefinitionContext context);


  public String upper(String n) {
    if (n.length() == 1) {
      return n.toUpperCase();
    }
    return n.substring(0, 1).toUpperCase() + n.substring(1);
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
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

  /**
   * @return the isSerialized
   */
  public boolean isSerialized() {
    return isSerialized;
  }

  /**
   * @param isSerialized the isSerialized to set
   */
  public void setSerialized(boolean isSerialized) {
    this.isSerialized = isSerialized;
  }

  /**
   * @return the parent
   */
  public ASymbol getParent() {
    return parent;
  }

  /**
   * @param parent the parent to set
   */
  public void setParent(ASymbol parent) {
    this.parent = parent;
  }
}

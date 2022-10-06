package akdl.graph.nodes;

import akdl.graph.gen.proc.DefinitionContext;
import akdl.graph.nodes.elts.ASymbol;
import akdl.graph.nodes.elts.Operator;

public class CharSymbol extends ASymbol {

  private static final long serialVersionUID = 7040964857405545054L;

  private char car;

  /**
   * constructor
   * 
   * @param c
   * @param op
   */
  public CharSymbol(char c, Operator op) {
    super(""+c, op);
    car = c;
  }

  /**
   * @return the car
   */
  public String getCharacter() {
    if (car == '\\') {
      return "\\\\";
    }
    return ""+car;
  }

  @Override
  public String getInformation(String indent) {
    return indent+car;
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

  @Override
  public void setName(String name) {
    if (name != null) {
      car = name.charAt(0);
      super.setName(""+car);
    }
  }
}
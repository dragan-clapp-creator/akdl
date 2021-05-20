package akdl.graph.nodes;

import akdl.graph.nodes.elts.Operator;
import akdl.sheet.keys.LeafType;

public class RegExpSymbol extends PredefinedSymbol {

  private static final long serialVersionUID = 2092847692028397843L;

  private String pattern;

  public RegExpSymbol(LeafType t, String n, String p, boolean b, Operator op) {
    super(t, n, b, op);
    pattern = p;
  }

  /**
   * @return the pattern
   */
  public String getPattern() {
    return pattern;
  }

}

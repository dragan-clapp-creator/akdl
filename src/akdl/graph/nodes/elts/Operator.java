package akdl.graph.nodes.elts;

public enum Operator {
  NONE,      // used if no modifier
  HUT,       // 0 or once
  STAR,      // 0 or more times
  PLUS,      // alternative (one of them)
  AMPERS,    // all of them but in any order
  TILD,      // used for undefined keywords
  DETACHED,  // used for detached enum keywords
}

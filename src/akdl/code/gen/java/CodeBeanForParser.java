package akdl.code.gen.java;

import akdl.code.gen.java.persist.Template;
import akdl.graph.nodes.DefinitionNode;

public class CodeBeanForParser extends ACodeBean {

  /**
   * CONSTRUCTOR
   * @param p   package
   * @param n   class name
   * @param t   initial template to load to the skeleton
   */
  public CodeBeanForParser(String p, String n, Template t) {
    super(p, n, t);
  }

  /**
   * @param definition
   * @return 
   */
  public boolean addToImports(DefinitionNode definition) {
    if (definition != null) {
      addToImports(definition.getParserPath(), definition.getParserClassName());
      return true;
    }
    return false;
  }
}

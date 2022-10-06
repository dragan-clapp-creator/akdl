package akdl.code.gen.java;

import java.io.IOException;

import akdl.graph.nodes.DefinitionNode;

public class JavaGenerator {

  /**
   * @param destination
   * @param root
   */
  public void perform(String destination, DefinitionNode root) {
    System.out.println("generate java code");
    try {
      new GraphTraverser(root, destination).traverse();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    
  }
}

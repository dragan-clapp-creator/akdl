package akdl.code.gen.java;

import java.io.IOException;

import akdl.code.gen.java.persist.FileGenerator;
import akdl.graph.nodes.DefinitionNode;

public class GraphTraverser {

  private DefinitionNode root;
  private String destination;

  public GraphTraverser(DefinitionNode rt, String dest) {
    root = rt;
    destination = dest;
  }

  public void traverse() throws IOException {
    FileGenerator cf = FileGenerator.getInstance(destination);
    CodeBeanHandler.getInstance().setFiler(cf);
    root.initialTraverse();

    CodeBeanHandler.getInstance().persistAll();
  }
}

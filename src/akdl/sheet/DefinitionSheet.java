package akdl.sheet;

import akdl.code.gen.java.JavaGenerator;
import akdl.graph.nodes.DefinitionNode;
import akdl.sheet.parse.ParseResult;

public class DefinitionSheet {

  private HeaderInfo info;

  /**
   * constructor
   */
  public DefinitionSheet() {
    info = new HeaderInfo();
  }

  /**
   * @return the destination
   */
  public String getDestination() {
    return info.getDestination();
  }

  /**
   * @param destination the destination to set
   */
  public void setDestination(String destination) {
    info.setDestination(destination);
  }

  /**
   * @return the pathToSaveDefinitions
   */
  public String getPathToSaveDefinitions() {
    return info.getPathToSaveDefinitions();
  }

  /**
   * @param pathToSaveDefinitions the pathToSaveDefinitions to set
   */
  public void setPathToSaveDefinitions(String pathToSaveDefinitions) {
    info.setPathToSaveDefinitions(pathToSaveDefinitions);
  }

  /**
   * @return the loadDefinitionsFrom
   */
  public String getLoadDefinitionsFrom() {
    return info.getLoadDefinitionsFrom();
  }

  /**
   * @param loadDefinitionsFrom the loadDefinitionsFrom to set
   */
  public void setLoadDefinitionsFrom(String loadDefinitionsFrom) {
    info.setLoadDefinitionsFrom(loadDefinitionsFrom);
  }

  /**
   * generates parser and runtime code
   * @param root
   */
  public void generateCodeFromEditor(DefinitionNode root) {
    ParseResult result = ParseResult.getInstance();
    result.initialize();
    generateCode(root, result, false);
  }

  /**
   * generates parser and runtime code
   * @param root
   */
  public void generateCode(DefinitionNode root, ParseResult result, boolean isEditor) {
    if (!isEditor && info.getDestination() != null) {
      if (!result.hasErrors()) {
        new JavaGenerator().perform(info.getDestination(), root);
      }
    }
  }

  /**
   * @return the info
   */
  public HeaderInfo getHeaderInfo() {
    return info;
  }
}

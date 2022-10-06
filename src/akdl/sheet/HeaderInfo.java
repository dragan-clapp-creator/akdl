package akdl.sheet;

import akdl.sheet.parse.ParseResult;

public class HeaderInfo {

  private String destination;
  private String pathToSaveDefinitions;
  private String pathToGrammar;
  private String loadDefinitionsFrom;
  private boolean isBottomUpApproach;

  /**
   * @return the destination
   */
  public String getDestination() {
    return destination;
  }

  /**
   * @param destination the destination to set
   */
  public void setDestination(String destination) {
    this.destination = destination;
  }

  /**
   * @return the pathToSaveDefinitions
   */
  public String getPathToSaveDefinitions() {
    return pathToSaveDefinitions;
  }

  /**
   * @param pathToSaveDefinitions the pathToSaveDefinitions to set
   */
  public void setPathToSaveDefinitions(String pathToSaveDefinitions) {
    this.pathToSaveDefinitions = pathToSaveDefinitions;
  }

  /**
   * @return the loadDefinitionsFrom
   */
  public String getLoadDefinitionsFrom() {
    return loadDefinitionsFrom;
  }

  /**
   * @param loadDefinitionsFrom the loadDefinitionsFrom to set
   */
  public void setLoadDefinitionsFrom(String loadDefinitionsFrom) {
    this.loadDefinitionsFrom = loadDefinitionsFrom;
  }

  /**
   * @return the isVerbose
   */
  public boolean isVerbose() {
    return ParseResult.getInstance().isVerbose();
  }

  /**
   * @param isVerbose the isVerbose to set
   */
  public void setVerbose(boolean isVerbose) {
    ParseResult.getInstance().setVerbosity(isVerbose ? 1 : 0);
  }

  /**
   * @return the isDebug
   */
  public boolean isDebug() {
    return ParseResult.getInstance().isDebug();
  }

  /**
   * @param isDebug the isDebug to set
   */
  public void setDebug(boolean isDebug) {
    ParseResult.getInstance().setIsDebug(isDebug);
  }

  /**
   * @return the isBottomUpApproach
   */
  public boolean isBottomUpApproach() {
    return isBottomUpApproach;
  }

  /**
   * @param isBottomUpApproach the isBottomUpApproach to set
   */
  public void setBottomUpApproach(boolean isBottomUpApproach) {
    this.isBottomUpApproach = isBottomUpApproach;
  }

  /**
   * @return the pathToGrammar
   */
  public String getPathToGrammar() {
    return pathToGrammar;
  }

  /**
   * @param pathToGrammar the pathToGrammar to set
   */
  public void setPathToGrammar(String pathToGrammar) {
    this.pathToGrammar = pathToGrammar;
  }
}

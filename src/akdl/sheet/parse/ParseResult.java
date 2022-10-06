package akdl.sheet.parse;

import java.util.ArrayList;
import java.util.Set;

import akdl.graph.nodes.DefinitionNode;

public class ParseResult {

  private static final ParseResult instance = new ParseResult();

  static public ParseResult getInstance() {
    return instance;
  }

  private ArrayList<String> messages;
  private boolean isDebug;
  private boolean isVerbose;
  private int level;
  private boolean hasErrors;
  private ArrayList<String> definedKeys;
  private ArrayList<String> undefinedKeys;
  private int baseline;

  private ParseResult() {
    messages = new ArrayList<>();
    definedKeys = new ArrayList<>();
    undefinedKeys = new ArrayList<>();
  }

  public void addMessageText(ISeverity s, String text, int lineno) {
    String string = "[" + s  + (lineno < 0 ? "" : ", line "+(lineno+baseline)) + "] " + text;
    messages.add(string);
    if (s == ISeverity.ERROR) {
      hasErrors = true;
    }
  }

  public void addMessageText(ISeverity s, String text) {
    addMessageText(s, text, -1);
  }

  public void setIsDebug(boolean isDebug) {
    this.isDebug = isDebug;
  }

  public void setIsVerbose() {
    isVerbose = true;
    level = 1;
  }

  public void setVerbosity(int nval) {
    isVerbose = nval > 0;
    level = nval;
  }

  /**
   * @return the errorMessages
   */
  public ArrayList<String> getMessages() {
    return messages;
  }

  /**
   * @return the isDebug
   */
  public boolean isDebug() {
    return isDebug;
  }

  /**
   * @return the isVerbose
   */
  public boolean isVerbose() {
    return isVerbose;
  }

  /**
   * @return the level
   */
  public int getLevel() {
    return level;
  }
  /**
   * @return the hasErrors
   */
  public boolean hasErrors() {
    return hasErrors;
  }

  public void printNode(DefinitionNode node) {
    if (isVerbose) {
      System.out.println(node);
    }
  }

  public void printErroneousNode(DefinitionNode node) {
    if (isDebug) {
      System.err.println(node);
    }
  }

  public void dump(DefinitionNode root) {
    if (isVerbose) {
      if (root == null) {
        System.err.println("Could not create ROOT");
      }
      else {
        root.dump();
      }
    }
  }

  public void addToDefinedKeys(String key) {
    if (!definedKeys.contains(key)) {
      definedKeys.add(key);
      undefinedKeys.remove(key);
    }
  }

  public void addDefinedKeys(Set<String> keySet) {
    definedKeys.addAll(keySet);
  }

  public void addToUndefinedKeys(String key) {
    if (!undefinedKeys.contains(key) &&
        !definedKeys.contains(key)) {
      undefinedKeys.add(key);
    }
  }

  public ArrayList<String> getUndefinedKeys() {
    return undefinedKeys;
  }

  public void initialize() {
    messages.clear();
    hasErrors = false;
    isDebug = false;
    isVerbose = false;
    level = 0;
  }

  public void setBaseLine(int lineno) {
    baseline = lineno;
  }
}

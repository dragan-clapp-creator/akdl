package akdl.sheet.keys;

import akdl.eval.EvaluationInfo.EvalType;

public class Parser {


  /**
   * returns a keyword from {@link ItemKeys} corresponding to given token
   * 
   * @param token
   * @return {@link ItemKeys} element
   */
  public ItemKeys getKey(int token) {
    if (token < 0) {
      return null;
    }
    for (ItemKeys value : ItemKeys.values()) {
      if (value.getKey() == token || value.getKey() == (char)token) {
        return value;
      }
    }
    System.err.println("ITEM key not found: " + (char)token);
    return null;
  }

  /**
   * returns {@link EvalType} element corresponding to the given argument of null if doesn't match
   * 
   * @param s
   * @return
   */
  public EvalType getEvaluationType(String s) {
    for (EvalType et : EvalType.values()) {
      if (et.name().equals(s)) {
        return et;
      }
    }
    return null;
  }

  /**
   * returns a keyword from {@link DefKeys} corresponding to given string
   * 
   * @param sval
   * @return {@link DefKeys} element
   */
  public DefKeys getDefKey(String sval) {
    for (DefKeys value : DefKeys.values()) {
      if (value.getName().equals(sval)) {
        return value;
      }
    }
    return null;
  }

  /**
   * returns a keyword from {@link AttKeys} corresponding to given string
   * 
   * @param sval
   * @return {@link AttKeys} element
   */
  public AttKeys getAttKey(String sval) {
    for (AttKeys value : AttKeys.values()) {
      if (value.getName().equals(sval)) {
        return value;
      }
    }
    return null;
  }

  /**
   * returns a keyword from {@link LeafType} corresponding to given string
   * 
   * @param sval
   * @return {@link LeafType} element
   */
  public LeafType getLeaf(String sval) {
    for (LeafType value : LeafType.values()) {
      if (value.name().equals(sval)) {
        return value;
      }
    }
    return null;
  }
}

package akdl.eval;

import java.io.Serializable;

public class EvaluationInfo implements Serializable {

  private static final long serialVersionUID = 1757576956872742820L;

  static public enum EvalType {
    NUMERIC_ORG, NUMERIC, NUMERIC_INIT, NUMERIC_OP,
    LOGIC_ORG, LOGIC, LOGIC_INIT, LOGIC_OP,
    CUSTOM_ORG, CUSTOM, CUSTOM_INIT, CUSTOM_OP;
  }
  static public enum EvalClass {
    NUMERIC, LOGIC, CUSTOM;
  }

  private EvalClass eval;
  private String pname;
  private String pack;
  private String rtType;
  private boolean isFirst;
  private boolean isInit;
  private boolean isOperator;

  public EvaluationInfo(EvalClass ec, String p, String n, String r, boolean bf, boolean bi, boolean bo) {
    eval = ec;
    pname = n;
    pack = p;
    rtType = r;
    isFirst = bf;
    isInit = bi;
    isOperator = bo;
  }

  /**
   * @return the eval
   */
  public EvalClass getEval() {
    return eval;
  }

  /**
   * @return the pname
   */
  public String getPname() {
    return pname;
  }

  /**
   * @return the pack
   */
  public String getPack() {
    return pack;
  }

  /**
   * @return the isInit
   */
  public boolean isInit() {
    return isInit;
  }

  public String getRTtype() {
    return rtType;
  }

  /**
   * @return the isOperator
   */
  public boolean isOperator() {
    return isOperator;
  }

  /**
   * @param isInit the isInit to set
   */
  public void setInit(boolean isInit) {
    this.isInit = isInit;
  }

  /**
   * @param isOperator the isOperator to set
   */
  public void setOperator(boolean isOperator) {
    this.isOperator = isOperator;
  }

  /**
   * @param eval the eval to set
   */
  public void setEval(EvalClass eval) {
    this.eval = eval;
  }

  /**
   * @return the isFirst
   */
  public boolean isFirst() {
    return isFirst;
  }

  /**
   * @param isFirst the isFirst to set
   */
  public void setFirst(boolean isFirst) {
    this.isFirst = isFirst;
  }
}

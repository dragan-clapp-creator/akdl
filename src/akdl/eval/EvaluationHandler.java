package akdl.eval;

import java.util.Hashtable;

import akdl.eval.EvaluationInfo.EvalClass;
import akdl.eval.EvaluationInfo.EvalType;

public class EvaluationHandler {

  private static EvaluationHandler instance = new EvaluationHandler();

  private Hashtable<EvalType, EvaluationInfo> hash;

  // PRIVATE CONSTRUCTOR
  private EvaluationHandler() {
    hash = new Hashtable<>();
  }

  /**
   * instance provider
   * @return
   */
  static public EvaluationHandler getInstance() {
    return instance;
  }

  public void initialize() {
    hash.clear();
  }

//  public void registerEvaluationInfo(EvalType type, EvalClass cl, String pack, String name, String rttype) {
//    hash.put(type, new EvaluationInfo(cl, pack, name, rttype, false, false));
//  }

  public EvaluationInfo createInitialEvaluationInfo(EvalType type, EvalClass cl, String pack, String name, String rttype) {
    hash.put(type, new EvaluationInfo(cl, pack, name, rttype, true, false, false));
    return hash.get(type);
  }

//  public void setEvaluationInfo(EvalType type) {
//    switch (type) {
//      case CUSTOM:
//        EvaluationInfo info = hash.get(EvalType.CUSTOM_ORG);
//        hash.put(type, new EvaluationInfo(EvalClass.CUSTOM, info.getPack(),  info.getPname(), "Object", false, false));
//        break;
//      case CUSTOM_INIT:
//        info = hash.get(EvalType.CUSTOM_ORG);
//        hash.put(type, new EvaluationInfo(EvalClass.CUSTOM, info.getPack(),  info.getPname(), "Object", true, false));
//        break;
//      case CUSTOM_OP:
//        info = hash.get(EvalType.CUSTOM_ORG);
//        hash.put(type, new EvaluationInfo(EvalClass.CUSTOM, info.getPack(),  info.getPname(), "Object", false, true));
//        break;
//      case LOGIC:
//        info = hash.get(EvalType.LOGIC_ORG);
//        hash.put(type, new EvaluationInfo(EvalClass.LOGIC, info.getPack(),  info.getPname(), "Boolean", false, false));
//        break;
//      case LOGIC_INIT:
//        info = hash.get(EvalType.LOGIC_ORG);
//        hash.put(type, new EvaluationInfo(EvalClass.LOGIC, info.getPack(),  info.getPname(), "Boolean", true, false));
//        break;
//      case LOGIC_OP:
//        info = hash.get(EvalType.LOGIC_ORG);
//        hash.put(type, new EvaluationInfo(EvalClass.LOGIC, info.getPack(),  info.getPname(), "Boolean", false, true));
//        break;
//      case NUMERIC:
//        info = hash.get(EvalType.NUMERIC_ORG);
//        hash.put(type, new EvaluationInfo(EvalClass.NUMERIC, info.getPack(),  info.getPname(), "Double", false, false));
//        break;
//      case NUMERIC_INIT:
//        info = hash.get(EvalType.NUMERIC_ORG);
//        hash.put(type, new EvaluationInfo(EvalClass.NUMERIC, info.getPack(),  info.getPname(), "Double", true, false));
//        break;
//      case NUMERIC_OP:
//        info = hash.get(EvalType.NUMERIC_ORG);
//        hash.put(type, new EvaluationInfo(EvalClass.NUMERIC, info.getPack(),  info.getPname(), "Double", false, true));
//        break;
//
//      default:
//        break;
//    }
//  }

  public EvaluationInfo createEvaluationInfo(EvalType type) {
    switch (type) {
      case CUSTOM:
        EvaluationInfo info = hash.get(EvalType.CUSTOM_ORG);
        return new EvaluationInfo(EvalClass.CUSTOM, info.getPack(),  info.getPname(), "Object", false, false, false);
      case CUSTOM_INIT:
        info = hash.get(EvalType.CUSTOM_ORG);
        return new EvaluationInfo(EvalClass.CUSTOM, info.getPack(),  info.getPname(), "Object", false, true, false);
      case CUSTOM_OP:
        info = hash.get(EvalType.CUSTOM_ORG);
        return new EvaluationInfo(EvalClass.CUSTOM, info.getPack(),  info.getPname(), "Object", false, false, true);
      case LOGIC:
        info = hash.get(EvalType.LOGIC_ORG);
        return new EvaluationInfo(EvalClass.LOGIC, info.getPack(),  info.getPname(), "Boolean", false, false, false);
      case LOGIC_INIT:
        info = hash.get(EvalType.LOGIC_ORG);
        return new EvaluationInfo(EvalClass.LOGIC, info.getPack(),  info.getPname(), "Boolean", false, true, false);
      case LOGIC_OP:
        info = hash.get(EvalType.LOGIC_ORG);
        return new EvaluationInfo(EvalClass.LOGIC, info.getPack(),  info.getPname(), "Boolean", false, false, true);
      case NUMERIC:
        info = hash.get(EvalType.NUMERIC_ORG);
        return new EvaluationInfo(EvalClass.NUMERIC, info.getPack(),  info.getPname(), "Double", false, false, false);
      case NUMERIC_INIT:
        info = hash.get(EvalType.NUMERIC_ORG);
        return new EvaluationInfo(EvalClass.NUMERIC, info.getPack(),  info.getPname(), "Double", false, true, false);
      case NUMERIC_OP:
        info = hash.get(EvalType.NUMERIC_ORG);
        return new EvaluationInfo(EvalClass.NUMERIC, info.getPack(),  info.getPname(), "Double", false, false, true);

      default:
        break;
    }
    return null;
  }

//  public EvaluationInfo getEvaluationInfo(EvalType type) {
//    return hash.get(type);
//  }
}

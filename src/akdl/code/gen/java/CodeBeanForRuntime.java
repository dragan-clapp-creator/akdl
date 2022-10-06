package akdl.code.gen.java;

import java.util.ArrayList;
import java.util.Hashtable;

import akdl.code.gen.java.persist.PlaceHolder;
import akdl.code.gen.java.persist.Template;
import akdl.eval.EvaluationInfo.EvalType;
import akdl.graph.nodes.DefinitionNode;
import akdl.sheet.keys.AttKeys;
import akdl.sheet.parse.MessageWriter;
import akdl.sheet.parse.ParseResult;

public class CodeBeanForRuntime extends ACodeBean {

  private static int serialNr;

  private Hashtable<PlaceHolder, String> buffer;
  private StringBuilder skeleton2;   // used for interface visitor
  private StringBuilder skeleton3;   // used for interface visitor implementation (case an evaluator is given)

  private String extend;
  private String implement;

  private boolean isInterface;
  private boolean isCustom;
  private boolean isEvaluator;

  private boolean isForInterface;
  private ArrayList<PlaceHolder> savedKeys;
  private boolean isReplaced;

  /**
   * CONSTRUCTOR
   * @param p   package
   * @param n   class name
   * @param t   initial template to load to the skeleton
   * @param evalType
   */
  public CodeBeanForRuntime(String p, String n, Template t, EvalType evalType) {
    super(p, n, t);
    isEvaluator = evalType != null;
    isCustom = (evalType == EvalType.CUSTOM || evalType == EvalType.CUSTOM_ORG || evalType == EvalType.CUSTOM_INIT);
    if (t == Template.RUNTIME_INTERFACE
        || t == Template.RUNTIME_INTERFACE_FOR_INTERFACE) {
      skeleton2 = new StringBuilder(Template.RUNTIME_VISITOR.getContent());
      isInterface = true;
      if (isEvaluator) {
        skeleton3 = new StringBuilder(Template.RUNTIME_VISITOR_IMPL.getContent());
      }
    }
    buffer = new Hashtable<PlaceHolder, String>();
    savedKeys = new ArrayList<PlaceHolder>();
  }

  /**
   * CONSTRUCTOR
   * @param t   initial template to load to the skeleton
   * @param p   package
   * @param n   class name
   */
  public CodeBeanForRuntime(Template t, String p, String n) {
    super(t, p, n);
    buffer = new Hashtable<PlaceHolder, String>();
  }

  /**
   * @param definition
   * @return 
   */
  public boolean addToImports(DefinitionNode definition) {
    if (definition != null) {
      addToImports(definition.getRuntimePath(), definition.getRuntimeClassName());
      return true;
    }
    return false;
  }

  /**
   * @return the skeleton2
   */
  public StringBuilder getSkeleton2() {
    return skeleton2;
  }

  /**
   * @return the skeleton3
   */
  public StringBuilder getSkeleton3() {
    return skeleton3;
  }

  /**
   * replace all occurrences of given place holder by given code piece
   * 
   * @param placeHolder
   * @param code
   * @param result 
   */
  public void replaceAll(PlaceHolder ph, String code, ParseResult result) {
    if (buffer.isEmpty()) {
      setSkeleton( replaceAll(ph, code, result, getSkeleton()) );
      boolean saveIsReplaced = isReplaced;
      skeleton2 = replaceAll(ph, code, result, skeleton2);
      if (skeleton3 != null && ph != PlaceHolder.INTERFACE) {
        skeleton3 = replaceAll(ph, code, result, skeleton3);
      }
      isReplaced = saveIsReplaced;
      return;
    }
    for (PlaceHolder key : buffer.keySet()) {
      String sb = buffer.get(key);
      sb = sb.replaceAll(ph.getString(), code);
      buffer.put(key, sb);
      if (sb.indexOf('%') < 0) {
        if (getSkeleton().indexOf(sb) < 0) {
          setSkeleton( insert(key, sb, result, getSkeleton()) );
          skeleton2 = insert(key, sb, result, skeleton2);
        }
        if (!savedKeys.contains(key)) {
          savedKeys.add(key);
        }
      }
    }
    if (savedKeys.size() == buffer.size()) {
      buffer.clear();
      savedKeys.clear();
    }
  }

  //
  private StringBuilder replaceAll(PlaceHolder ph, String code, ParseResult result, StringBuilder skelet) {
    isReplaced = false;
    if (code == null || skelet == null) {
      return skelet;
    }
    int index = skelet.indexOf(ph.getString());
    if (index < 0 && ph == PlaceHolder.INTERFACE) {
      index = skelet.indexOf("implements ");
      if (result != null && index < 0) {
        MessageWriter.W001.store(result, ph, getClname());
      }
      else {
        skelet = skelet.insert(index+11, code+", ");
        isReplaced = true;
      }
    }
    else if (result != null && index < 0) {
      MessageWriter.W001.store(result, ph, getClname());
    }
    else {
      while (index > 0) {
        skelet = skelet.replace(index, index+ph.getString().length(), code);
        index = skelet.indexOf(ph.getString());
      }
      isReplaced = true;
    }
    return skelet;
  }

  /**
   * insert given code piece before given place holder
   * 
   * @param placeHolder
   * @param code
   * @param result 
   */
  public void insert(PlaceHolder ph, String code, ParseResult result) {
    if (ph == PlaceHolder.ATT || ph == PlaceHolder.METHOD) {
      String value = buffer.get(ph);
      if (value == null) {
        buffer.put(ph, code);
      }
      else {
        buffer.put(ph, value + (ph == PlaceHolder.ATT ? "" : "\n") + code);
      }
      return;
    }
    setSkeleton( insert(ph, code, result, getSkeleton()) );
    skeleton2 = insert(ph, code, result, skeleton2);
  }

  /**
   * insert given code piece before given place holder to implemenation visitor (if exists)
   * 
   * @param placeHolder
   * @param code
   * @param result 
   */
  public void insertImpl(PlaceHolder ph, String code, ParseResult result) {
    if (skeleton3 != null) {
      skeleton3 = insert(ph, code, result, skeleton3);
    }
  }

  /**
   * removes all remained place holders left by {@link #insert(PlaceHolder, String, ParseResult)}
   */
  public void finalStep(ParseResult result) {
    buffer.clear();
    super.finalStep(result);
  }

  public void setIsForInterface() {
    isForInterface = true;
  }

  /**
   * @return the isForInterface
   */
  public boolean isForInterface() {
    return isForInterface;
  }

  /**
   * @return the extend
   */
  public String getExtend() {
    return extend;
  }

  /**
   * @param extend the extend to set
   */
  public void setExtend(String extend) {
    this.extend = extend;
  }

  /**
   * @return the implement
   */
  public String getImplement() {
    return implement;
  }

  /**
   * 
   * @param parentBean 
   * @param parent
   * @param cbh
   */
  public void setImplements(DefinitionNode node, CodeBeanForRuntime parentBean, DefinitionNode parent, CodeBeanHandler cbh) {
    if (isForInterface) {
      if ((implement == null || parentBean.getImplement() == null)
          && (parentBean.isForInterface() || parentBean.isInterface())) {
        implement = parent.getRuntimePath()+parent.getRuntimeClassName();
        replaceAll(PlaceHolder.INTERFACE, parent.getRuntimeClassName(), ParseResult.getInstance());
        if (isReplaced) {
          addToImports(parent.getRuntimePath(), parent.getRuntimeClassName());
          replaceAll(PlaceHolder.TYPE, node.getRuntimeClassName(), ParseResult.getInstance());
          cbh.handleAccept(this, node.upper(node.getName()), parent.getRuntimeClassName());
          if (parent.getType() == AttKeys.ATT_CLASS || node.getType() == AttKeys.ATT_CLASS) {
            addToImports(parent.getRuntimePath(), parent.getRuntimeClassName()+"Visitor");
            if (parentBean != null && parentBean.getExtend() != null) {
              String ext = parentBean.getExtend();
              String[] st = ext.split(",");
              addToImports(st[0], st[1]+"Visitor");
              cbh.handleAccept(this, parent.getRuntimeClassName(), st[1]);
            }
          }
          else if (parentBean.isInterface && parentBean.getExtend() != null) {
            String[] st = parentBean.getExtend().split(",");
            addToImports(st[0], st[1]+"Visitor");
            cbh.handleAccept(this, parent.getRuntimeClassName(), st[1]);
          }
          else if (parentBean.isInterface && parentBean.getSkeleton2() != null) {
            addToImports(parent.getRuntimePath(), parent.getRuntimeClassName()+"Visitor");
          }
        }
      }
    }
    serialNr++;
    replaceAll(PlaceHolder.NR, ""+serialNr, ParseResult.getInstance());
  }

  /**
   * @return the isInterface
   */
  public boolean isInterface() {
    return isInterface;
  }

  /**
   * @return the isEvaluator
   */
  public boolean isEvaluator() {
    return isEvaluator;
  }

  /**
   * @return the isCustom
   */
  public boolean isCustom() {
    return isCustom;
  }
}

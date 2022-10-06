package akdl.code.gen.java;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;

import akdl.code.gen.java.persist.FileGenerator;
import akdl.code.gen.java.persist.PlaceHolder;
import akdl.code.gen.java.persist.Template;
import akdl.eval.EvaluationInfo.EvalType;
import akdl.graph.nodes.CharSymbol;
import akdl.graph.nodes.DefinitionNode;
import akdl.graph.nodes.GroupOfSymbols;
import akdl.graph.nodes.KeySymbol;
import akdl.graph.nodes.PredefinedSymbol;
import akdl.graph.nodes.StringSymbol;
import akdl.graph.nodes.elts.ASymbol;
import akdl.graph.nodes.elts.Operator;
import akdl.sheet.keys.AttKeys;
import akdl.sheet.parse.ParseResult;

public class CodeBeanHandler {

  private static final CodeBeanHandler instance = new CodeBeanHandler();

  static public CodeBeanHandler getInstance() {
    return instance;
  }

  private TreeMap<String, CodeBeanForParser> p_hash;
  private TreeMap<String, CodeBeanForRuntime> r_hash;

  private ArrayList<String> accepts;

  private String aparser;
  private String mparser;
  private String mpack;

  private FileGenerator filer;
  private CodeBeanForParser mbean;

  private CodeBeanHandler() {
    p_hash = new TreeMap<String, CodeBeanForParser>();
    r_hash = new TreeMap<String, CodeBeanForRuntime>();
    accepts = new ArrayList<String>();
  }

  /**
   * @return the mparser
   */
  public String getMainParser() {
    return mparser;
  }

  /**
   * @return the filer
   */
  public FileGenerator getFiler() {
    return filer;
  }

  /**
   * @param cf  r/w utility
   */
  public void setFiler(FileGenerator cf) {
    filer = cf;
  }

  /**
   * parser bean provider
   * 
   * @param p   package
   * @param n   name
   * @param t   template
   * @return    {@link CodeBeanForParser} instance
   * 
   * @throws FileNotFoundException
   */
  public CodeBeanForParser getParserBean(String p, String n, Template t) {
    CodeBeanForParser bean = createCodeBeanForParser(p, n, t);
    if ("AParser".equals(n) ) {
      aparser = n;
     }
    else if (mparser == null) {
      mpack = p;
      mparser = n;
      mbean = bean;
    }
    else if (!mpack.equals(p)) {
      bean.addToImports(mpack, aparser);
      bean.addToImports(mpack, mparser);
    }
    return bean;
  }

  /**
   * runtime bean provider
   * @param parentBean 
   * 
   * @param parent  parent node
   * @param def 
   * @param p       package
   * @param n       name
   * @param t       template
   * @return    {@link CodeBeanForParser} instance
   * 
   * @throws FileNotFoundException
   */
  public CodeBeanForRuntime getRuntimeBean(CodeBeanForRuntime parentBean, DefinitionNode parent, DefinitionNode def, String p, String n, Template t) {
    CodeBeanForRuntime bean = getCodeBeanForRuntime(p, n);
    if (bean != null) {
      return bean;
    }
    boolean isEvaluator = def.getEval() != null;
    if (parent != null) {
      if (t == Template.RUNTIME_CLASS
          && parent.getType() == AttKeys.ATT_INTERFACE
          && isPlus(parent)
          ||
          t == Template.RUNTIME_INTERFACE
          && parent.getType() == AttKeys.ATT_INTERFACE
          && (isEnum(def) || parentBean.getSkeleton2() != null)) {
        bean = createCodeBeanForRuntime(p, n, Template.RUNTIME_CLASS_FOR_INTERFACE, def.getEval());
        if (parentBean.isForInterface() && !parentBean.isInterface()) {
          bean = createCodeBeanForRuntime(p, n, Template.RUNTIME_CLASS_FOR_CLASS, null);
        }
        bean.setIsForInterface();
        updateExtend(bean, isEvaluator);
        return bean;
      }
      if (t == Template.RUNTIME_INTERFACE) {
        if (isStringSet(def) || isPredefined(def)) {
          bean = createCodeBeanForRuntime(p, n, Template.RUNTIME_CLASS, null);
        }
        else if (parent.getType() == AttKeys.ATT_INTERFACE) {
          bean = createCodeBeanForRuntime(p, n, Template.RUNTIME_INTERFACE_FOR_INTERFACE, def.getEval());
          bean.setIsForInterface();
          bean.setExtend(parent.getRuntimePath() + "," + parent.getRuntimeClassName());
        }
        else {
          bean = createCodeBeanForRuntime(p, n, Template.RUNTIME_INTERFACE, def.getEval());
          bean.setIsForInterface();
        }
        return bean;
      }
    }
    bean = createCodeBeanForRuntime(p, n, t, null);
    updateExtend(bean, isEvaluator);
    return bean;
  }

  //
  private void updateExtend(CodeBeanForRuntime bean, boolean isEvaluator) {
    if (isEvaluator) {
      bean.replaceAll(PlaceHolder.EXTEND, Template.RUNTIME_EVALUATOR_EXTEND.toString(), ParseResult.getInstance());
      bean.replaceAll(PlaceHolder.IMPLEMENT, Template.RUNTIME_EVALUATOR_TRAVERSER.toString(), ParseResult.getInstance());
    }
    else {
      bean.replaceAll(PlaceHolder.EXTEND, "", ParseResult.getInstance());
      bean.replaceAll(PlaceHolder.IMPLEMENT, "", ParseResult.getInstance());
    }
  }

  //
  private boolean isPredefined(DefinitionNode def) {
    if (def.getSyntax().size() == 1) {
      ASymbol sym = def.getSyntax().get(0);
      if (sym instanceof GroupOfSymbols && ((GroupOfSymbols) sym).getOperator() == Operator.PLUS) {
        ArrayList<ASymbol> syntax = ((GroupOfSymbols) sym).getSyntax();
        for (ASymbol s : syntax) {
          if (s instanceof PredefinedSymbol) {
            return true;
          }
        }
      }
    }
    return false;
  }

  //
  private boolean isEnum(DefinitionNode parent) {
    if (parent.getSyntax().size() == 1) {
      ASymbol sym = parent.getSyntax().get(0);
      if (sym instanceof GroupOfSymbols && ((GroupOfSymbols) sym).getOperator() == Operator.PLUS) {
        ArrayList<ASymbol> syntax = ((GroupOfSymbols) sym).getSyntax();
        for (ASymbol s : syntax) {
          if (s instanceof KeySymbol && ((KeySymbol) s).getDefinition().getType() == AttKeys.ATT_ENUM) {
            return true;
          }
        }
      }
    }
    return false;
  }

  //
  private boolean isPlus(DefinitionNode parent) {
    if (parent.getSyntax().size() == 1) {
      ASymbol sym = parent.getSyntax().get(0);
      return sym instanceof GroupOfSymbols
          && ((GroupOfSymbols) sym).getOperator() == Operator.PLUS;
    }
    return false;
  }

  //
  private boolean isStringSet(DefinitionNode parent) {
    if (parent.getSyntax().size() == 1) {
      ASymbol sym = parent.getSyntax().get(0);
      if (sym instanceof GroupOfSymbols && ((GroupOfSymbols) sym).getOperator() == Operator.PLUS) {
        ArrayList<ASymbol> syntax = ((GroupOfSymbols) sym).getSyntax();
        for (ASymbol s : syntax) {
          if (s instanceof StringSymbol || s instanceof CharSymbol) {
            return true;
          }
        }
      }
    }
    return false;
  }

  /**
   * find Runtime CodeBean
   * 
   * @param p       package
   * @param n       name
   * @return    {@link CodeBeanForParser} instance
   */
  public CodeBeanForRuntime findRuntimeBean(String p, String n) {
    String key = p+n;
    return r_hash.get(key);
  }

  //
  private CodeBeanForParser 
  createCodeBeanForParser(String p, String n, Template t) {
    String key = p+n;
    if (p_hash.containsKey(key)) {
      return p_hash.get(key);
    }
    CodeBeanForParser cb = new CodeBeanForParser(p, n, t);
    p_hash.put(key, cb);
    return cb;
  }

  //
  private CodeBeanForRuntime createCodeBeanForRuntime(String p, String n, Template t, EvalType evalType) {
    String key = p+n;
    CodeBeanForRuntime cb = new CodeBeanForRuntime(p, n, t, evalType);
    r_hash.put(key, cb);
    return cb;
  }

  //
  private CodeBeanForRuntime getCodeBeanForRuntime(String p, String n) {
    String key = p+n;
    if (r_hash.containsKey(key)) {
      return r_hash.get(key);
    }
    return null;
  }

  public void persistAll() throws IOException {
    persistParserCode();
    persistRuntimeCode();
    reinitialize();
  }

  public void persistGrammarAndRuntime(DefinitionNode root) throws IOException {
    persistRuntimeCode();
    persistGrammar(root);
    reinitialize();
  }

  //
  private void reinitialize() {
    p_hash = new TreeMap<String, CodeBeanForParser>();
    r_hash = new TreeMap<String, CodeBeanForRuntime>();
    accepts = new ArrayList<String>();
    aparser = null;
    mparser = null;
    mbean.getImports().clear();
  }

  //
  private void persistParserCode() throws IOException {
    System.out.println("\nPARSER GENERATION\n");
    int counter = 0;
    for (String name : p_hash.keySet()) {
      counter++;
      CodeBeanForParser bean = p_hash.get(name);
      bean.finalStep(ParseResult.getInstance());
      String fname = bean.getClname();
      String fpack = bean.getPack();
      System.out.println("\t file "+counter+"    "+fpack+"."+fname);
      FileWriter file = filer.persist(fpack, fname);
      file.write(bean.getSkeleton().toString());
      file.close();
    }
  }

  //
  private void persistRuntimeCode() throws IOException {
    System.out.println("\nRUNTIME GENERATION\n");
    int counter = 0;
    for (String name : r_hash.keySet()) {
      counter++;
      CodeBeanForRuntime bean = r_hash.get(name);
      bean.finalStep(ParseResult.getInstance());
      String fname = bean.getClname();
      String fpack = bean.getPack();
      System.out.println("\t file "+counter+"    "+fpack+"."+fname);
      FileWriter file = filer.persist(fpack, fname);
      file.write(bean.getSkeleton().toString());
      file.close();
      StringBuilder sk2 = bean.getSkeleton2();
      if (sk2 != null) {
        fname = bean.getClname()+"Visitor";
        System.out.println("\t file "+counter+" bis "+fpack+"."+fname);
        file = filer.persist(fpack, fname);
        file.write(sk2.toString());
        file.close();
      }
      StringBuilder sk3 = bean.getSkeleton3();
      if (sk3 != null) {
        fname = bean.getClname()+"VisitorImpl";
        System.out.println("\t file "+counter+" ter "+fpack+"."+fname);
        file = filer.persist(fpack, fname);
        file.write(sk3.toString());
        file.close();
      }
    }
  }

  /**
   * 
   * @param context
   */
  public void handleAccept(CodeBeanForRuntime rbean, String clname, String parentClname) {
    String key = rbean.getClname()+clname+parentClname;
    if (!accepts.contains(key)) {
      accepts.add(key);
      ParseResult result = ParseResult.getInstance();
      rbean.insert(PlaceHolder.ACCEPT, Template.RUNTIME_ACCEPT.toString(), result);
      rbean.replaceAll(PlaceHolder.TYPE, clname, result);
      rbean.replaceAll(PlaceHolder.PARENT_TYPE, parentClname, result);
    }
  }

  public CodeBeanForParser getMainParserBean() {
    return mbean;
  }

  public void register(CodeBeanForRuntime rbean) {
    String key = rbean.getPack()+"."+rbean.getClname();
    r_hash.put(key, rbean);
  }

  //
  private void persistGrammar(DefinitionNode root) {
    // TODO Auto-generated method stub
    
  }
}
